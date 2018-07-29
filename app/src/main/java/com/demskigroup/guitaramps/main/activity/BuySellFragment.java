package com.demskigroup.guitaramps.main.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.main.ProductPictureFragment;
import com.demskigroup.guitaramps.pojo_class.product_category.ProductCategoryMainPojo;
import com.demskigroup.guitaramps.utility.ApiUrl;
import com.demskigroup.guitaramps.utility.CommonClass;
import com.demskigroup.guitaramps.utility.OkHttp3Connection;
import com.demskigroup.guitaramps.utility.SessionManager;
import com.demskigroup.guitaramps.utility.VariableConstants;

import org.json.JSONException;
import org.json.JSONObject;

public class BuySellFragment extends Fragment {


    private static final String TAG = BuySellFragment.class.getSimpleName();
    RelativeLayout mTvBuy, mTvSell;
    static   BuySellFragment buySellFragment;
    static Context context;
    private SessionManager mSessionManager;
    public static BuySellFragment getInstance(Context aContext){

        context = aContext;

        if (buySellFragment == null){
            buySellFragment = new BuySellFragment();
        }

        return  buySellFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.activity_buy_sell, container, false);
        initVariable(view);
        clickListner();
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated( savedInstanceState );
    }

    private void initVariable(View v){

        try {

            mTvBuy = v.findViewById( R.id.rL_do_buy );
            mTvSell = v.findViewById( R.id.rL_do_sell );
            mSessionManager=new SessionManager(getActivity());
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private void clickListner(){

        try {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//            Fragment fragment = ProductPictureFragment.getInstance( context );
            Fragment fragment =new ProductPictureFragment();

            mTvBuy.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mSessionManager.setComingFrom("login");
                    transaction.replace( R.id.frame_layout, fragment);

                    transaction.commit();
                }
            } );

            mTvSell.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                    if (mSessionManager.getIsUserLoggedIn())
                    {

                        hitApiOnServerToCheckUserAccount();

                    }
                    else
                    {

                        startActivityForResult(new Intent(getActivity(),LandingActivity.class), VariableConstants.LANDING_REQ_CODE);

                    }


//                    transaction.replace( R.id.frame_layout, fragment);
//
//                    transaction.commit();
                }
            } );

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private void hitApiOnServerToCheckUserAccount(){
        if (CommonClass.isNetworkAvailable(getActivity()))
        {
            final ProgressDialog pDialog = new ProgressDialog(getActivity(),0);


            pDialog.setCancelable(false);

//        pDialog.setTitle(R.string.string_351);

            pDialog.setMessage("Please wait checking status");
            pDialog.show();
            JSONObject request_param=new JSONObject();
            try {
                request_param.put( "user_name", mSessionManager.getUserName() );
            } catch (JSONException e) {
                e.printStackTrace();
            }


            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.CHECK_USER_ACCOUNT_STATUS, OkHttp3Connection.Request_type.POST, request_param, new OkHttp3Connection.OkHttp3RequestCallback()
            {
                @Override
                public void onSuccess(String result, String user_tag) {


                    if(pDialog != null){
                        if (pDialog.isShowing()){
                            pDialog.dismiss();
                        }
                    }

                    ProductCategoryMainPojo categoryMainPojo;
                    Gson gson=new Gson();
                    categoryMainPojo=gson.fromJson(result,ProductCategoryMainPojo.class);

                    switch (categoryMainPojo.getCode())
                    {
                        // success i.e email is not registered
                        case "200" :
                            mSessionManager.setComingFrom("login");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                            {

                                startActivity(new Intent(getActivity(), Camera2Activity.class));

                            }
                            else
                            {

                                startActivity(new Intent(getActivity(), CameraActivity.class));

                            }

                            break;


                        case "204" :
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),
                                    R.style.CustomPopUpThemeBlue);
                            builder.setMessage("Please setup your payment details to receive payments, click ok to setup.");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    startActivity(new Intent(getActivity(), AddPaymentActivity.class));
                                }
                            });

                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.setCancelable(false);
                            builder.show();


                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(getActivity());
                            break;
                        // error like email is already registered
                        default:

                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    if(pDialog != null){
                        if (pDialog.isShowing()){
                            pDialog.dismiss();
                        }
                    }
                }
            });
        }
    }



}
