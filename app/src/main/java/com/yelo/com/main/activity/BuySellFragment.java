package com.yelo.com.main.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yelo.com.R;
import com.yelo.com.main.ProductPictureFragment;
import com.yelo.com.utility.SessionManager;
import com.yelo.com.utility.VariableConstants;

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

                        mSessionManager.setComingFrom("login");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        {

                            startActivity(new Intent(getActivity(), Camera2Activity.class));

                        }
                        else
                        {

                            startActivity(new Intent(getActivity(), CameraActivity.class));

                        }
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





}
