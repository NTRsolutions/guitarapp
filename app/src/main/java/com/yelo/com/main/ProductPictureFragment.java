package com.yelo.com.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.yelo.com.BuildConfig;
import com.yelo.com.R;
import com.yelo.com.adapter.ProductAdapter;
import com.yelo.com.main.activity.AddPaymentActivity;
import com.yelo.com.main.activity.Camera2Activity;
import com.yelo.com.main.activity.CameraActivity;
import com.yelo.com.main.activity.LandingActivity;
import com.yelo.com.main.tab_fragments.HomeFrag;
import com.yelo.com.mqttchat.Activities.LandingPage;
import com.yelo.com.mqttchat.AppController;
import com.yelo.com.mqttchat.Database.CouchDbController;
import com.yelo.com.mqttchat.LoginSignup.LoginActivity;
import com.yelo.com.mqttchat.Utilities.ApiOnServer;
import com.yelo.com.pojo_class.EmailCheckPojo;
import com.yelo.com.pojo_class.product_category.ProductCategoryMainPojo;
import com.yelo.com.pojo_class.product_category.ProductCategoryResDatas;
import com.yelo.com.pojo_class.productcategory.HomeCategory;
import com.yelo.com.pojo_class.productcategory.Homecat;
import com.yelo.com.utility.ApiUrl;
import com.yelo.com.utility.CommonClass;
import com.yelo.com.utility.OkHttp3Connection;
import com.yelo.com.utility.SessionManager;
import com.yelo.com.utility.VariableConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductPictureFragment extends Fragment {

    RecyclerView recyclerViewProductPicture;

    //     Context context;
    ProductPictureFragment productPictureFragment = null;

//    String productName[] = {"Solid-state amps", "Tube amps", "Modeling amps", "Hybrid amps"
//            , "Acoustic amps", "Vintage amps","Bass amps","Parts"};
//
//    int images[]={R.drawable.image1,R.drawable.image2,R.drawable.image3,R.drawable.image4,R.drawable.image1,R.drawable.image2,R.drawable.image3,R.drawable.image4};

    public  static   String TAG ="productcategoryFrag";

    RelativeLayout mRlSellProduct;
    private SessionManager mSessionManager;
    ProductAdapter productAdaptor = null;
    List<ProductCategoryResDatas> mList = new ArrayList<>(  );


    /*    public static ProductPictureFragment getInstance(Context acontext){

            try {

                if(productPictureFragment == null){


                    productPictureFragment = new ProductPictureFragment();
                    context = acontext;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return productPictureFragment;
        }*/
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(view!=null){
            if((ViewGroup)view.getParent()!=null)
                ((ViewGroup)view.getParent()).removeView(view);
            return view;
        }

        view = inflater.inflate(R.layout.activity_product_picture, container, false);
        setUpViews(view);
        mSessionManager=new SessionManager(getActivity());


        String com = mSessionManager.getComingFrom();

        if(mSessionManager.getComingFrom().equals( "login" )){
            mRlSellProduct.setVisibility( View.VISIBLE );
        }else {
            mRlSellProduct.setVisibility( View.VISIBLE );
        }

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated( savedInstanceState );

    }

    private void setUpViews(View view) {


        recyclerViewProductPicture = view.findViewById(R.id.recyclerViewProductPicture);
        mRlSellProduct =  view.findViewById( R.id.rL_sell_stuff );

        setadapter();

        mRlSellProduct.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSessionManager.getIsUserLoggedIn())
                {
                    // startActivity(new Intent(mActivity, CameraActivity.class));

                    hitApiOnServerToCheckUserAccount();
                }
                else ProductPictureFragment.this.startActivityForResult(new Intent(getActivity(),LandingActivity.class), VariableConstants.LANDING_REQ_CODE);

            }
        } );

        hitApiOnServer();
    }



    private void hitApiOnServer(){
        if (CommonClass.isNetworkAvailable(getActivity()))
        {
            final ProgressDialog pDialog = new ProgressDialog(getActivity(),0);


            pDialog.setCancelable(false);

//        pDialog.setTitle(R.string.string_351);

            pDialog.setMessage("Get categories");
            pDialog.show();
            JSONObject request_param=new JSONObject();


            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.GET_CATEGORIES, OkHttp3Connection.Request_type.GET, request_param, new OkHttp3Connection.OkHttp3RequestCallback()
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
                           mList.clear();
                           mList.addAll( categoryMainPojo.getData());
                           setadapter();

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


    private void setadapter(){

        try {

            if (productAdaptor == null){
                productAdaptor = new ProductAdapter(getActivity(),mList);
                recyclerViewProductPicture.setAdapter(productAdaptor);
            }else {
                productAdaptor.notifyDataSetChanged();
            }

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

            pDialog.setMessage("Get categories");
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
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                                startActivity(new Intent(getActivity(), Camera2Activity.class));
                            else
                                startActivity(new Intent(getActivity(), CameraActivity.class));

                            break;


                        case "204" :
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),
                                    R.style.CustomPopUpThemeBlue);
                            builder.setMessage("You have not configured your account detail yet, Please click ok to configure");
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
