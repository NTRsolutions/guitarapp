package com.yelo.com.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import com.yelo.com.R;
import com.yelo.com.adapter.SelectBuyerRvAdap;
import com.yelo.com.fcm_push_notification.Config;
import com.yelo.com.fcm_push_notification.NotificationMessageDialog;
import com.yelo.com.fcm_push_notification.NotificationUtils;
import com.yelo.com.pojo_class.accepted_offer.AcceptedOfferDatas;
import com.yelo.com.utility.ClickListener;
import com.yelo.com.utility.VariableConstants;
import java.util.ArrayList;

/**
 * <h>SelectBuyerActivity</h>
 * <p>
 *     This class is called from EditProductActivity class. In this class we get all
 *     accepted offer from last class and show in this.
 * </p>
 * @since 13-Jul-17
 */
public class SelectBuyerActivity extends AppCompatActivity implements View.OnClickListener
{
    private NotificationMessageDialog mNotificationMessageDialog;
    public String postId="";
    public RelativeLayout rL_rootElement;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_buyer);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);

        initVariables();
    }

    private void initVariables()
    {
        final Activity mActivity = SelectBuyerActivity.this;
        mNotificationMessageDialog=new NotificationMessageDialog(mActivity);

        // accept datas from last class
        Intent intent=getIntent();
        final ArrayList<AcceptedOfferDatas> arrayListAcceptedOffer= (ArrayList<AcceptedOfferDatas>) intent.getSerializableExtra("acceptedOffer");
        postId = intent.getStringExtra("postId");

        // Accepted offer recycler view adapter
        RecyclerView rV_acceptedOffer= (RecyclerView) findViewById(R.id.rV_acceptedOffer);
        rL_rootElement = (RelativeLayout) findViewById(R.id.rL_rootElement);
        SelectBuyerRvAdap selectBuyerRvAdap=new SelectBuyerRvAdap(mActivity,arrayListAcceptedOffer);
        LinearLayoutManager mLinearLayoutManager=new LinearLayoutManager(mActivity);
        rV_acceptedOffer.setLayoutManager(mLinearLayoutManager);
        rV_acceptedOffer.setAdapter(selectBuyerRvAdap);

        // item click
        selectBuyerRvAdap.setItemClick(new ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent=new Intent(mActivity,RateUserActivity.class);
                intent.putExtra("userName",arrayListAcceptedOffer.get(position).getBuyername());
                intent.putExtra("userImage",arrayListAcceptedOffer.get(position).getBuyerProfilePicUrl());
                intent.putExtra("postId",arrayListAcceptedOffer.get(position).getPostId());
                startActivityForResult(intent, VariableConstants.RATE_USER_REQ_CODE);
            }
        });

        RelativeLayout rL_back_btn= (RelativeLayout) findViewById(R.id.rL_back_btn);
        rL_back_btn.setOnClickListener(this);
    }


    @Override
    protected void onResume()
    {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mNotificationMessageDialog.mRegistrationBroadcastReceiver, new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mNotificationMessageDialog.mRegistrationBroadcastReceiver, new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mNotificationMessageDialog.mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.rL_back_btn :
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case VariableConstants.RATE_USER_REQ_CODE :
                boolean isToFinishSelectBuyer = data.getBooleanExtra("isToFinishSelectBuyer",false);
                if (isToFinishSelectBuyer)
                {
                    Intent intent = new Intent();
                    intent.putExtra("isToFinishEditPost",true);
                    setResult(VariableConstants.SELLING_REQ_CODE,intent);
                    finish();
                }
                break;
        }
    }
}
