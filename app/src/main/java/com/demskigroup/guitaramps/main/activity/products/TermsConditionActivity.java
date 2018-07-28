package com.demskigroup.guitaramps.main.activity.products;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;

import android.support.v4.content.LocalBroadcastManager;

import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.demskigroup.guitaramps.R;

import com.demskigroup.guitaramps.fcm_push_notification.NotificationMessageDialog;

import com.demskigroup.guitaramps.utility.RunTimePermission;


import java.io.File;


/**
 * <h>EditProfileActivity</h>
 * <p>
 *     This class is called from MyProfileFrag class. In this class firstly we do
 *     api call to get user complete information. We have all the field editable
 *     to mobify last datas with new one.
 * </p>
 * @since 4/14/2017
 */
public class TermsConditionActivity extends Activity{
    private static final String TAG = TermsConditionActivity.class.getSimpleName();
    private Activity mActivity;

    ImageView mIvBack;
    TextView mTitle;
    Intent I = null;
    public  static  String KEY_COMING_FROM = "coming_from";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_privacy_policy );
        getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN );
        overridePendingTransition( R.anim.activity_open_translate, R.anim.activity_close_scale );


        initVariables();
    }

    /**
     * <h>InitVariables</h>
     * <p>
     * In this method we used to initialize all the xml variables.
     * </p>
     */
    private void initVariables() {

        mIvBack = findViewById( R.id.dummy_back );
        mIvBack.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        } );

        mTitle = findViewById( R.id.text_view_title );


        I = getIntent();
        if(I != null){

            if(I.getStringExtra( KEY_COMING_FROM ).equalsIgnoreCase( "Terms" )){

                mTitle.setText( "Terms & Conditions" );
            }else {
                mTitle.setText( "Privacy Policy" );
            }
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {

        super.onPause();
    }
}



