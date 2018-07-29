package com.demskigroup.guitaramps.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.event_bus.EventBusDatasHandler;
import com.demskigroup.guitaramps.fcm_push_notification.Config;
import com.demskigroup.guitaramps.fcm_push_notification.NotificationMessageDialog;
import com.demskigroup.guitaramps.fcm_push_notification.NotificationUtils;
import com.demskigroup.guitaramps.pojo_class.rate_seller.RateSellerPojo;
import com.demskigroup.guitaramps.pojo_class.rate_user_pojo.RateUserMainPojo;
import com.demskigroup.guitaramps.utility.ApiUrl;
import com.demskigroup.guitaramps.utility.CircleTransform;
import com.demskigroup.guitaramps.utility.CommonClass;
import com.demskigroup.guitaramps.utility.OkHttp3Connection;
import com.demskigroup.guitaramps.utility.SessionManager;
import com.demskigroup.guitaramps.utility.VariableConstants;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Timer;
import java.util.TimerTask;

/**
 * <h>RateUserActivity</h>
 * <p>
 *     In this class we used to do api call to set rating for user.
 * </p>
 * @since 13-Jul-17
 */
public class RateUserActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = RateUserActivity.class.getSimpleName();
    private RatingBar mRatingBar;
    private TextView tV_absolutely,tV_not,tV_yes,tV_definitely,tV_submit;
    private RelativeLayout rL_submit,rL_rootElement;
    private boolean isToSubmit=false,isFromNotification=false;
    private Activity mActivity;
    private String userName="",postId="";
    private SessionManager mSessionManager;
    private ProgressBar progress_bar_submit;
    private NotificationMessageDialog mNotificationMessageDialog;
    private EventBusDatasHandler mEventBusDatasHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_user);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);

        initVariable();
    }

    /**
     * <h>InitVariable</h>
     * <p>
     *     In this method we used to initialize all variables.
     * </p>
     */
    private void initVariable()
    {
        mActivity = RateUserActivity.this;
        mEventBusDatasHandler = new EventBusDatasHandler(mActivity);
        mNotificationMessageDialog=new NotificationMessageDialog(mActivity);
        mSessionManager=new SessionManager(mActivity);
        progress_bar_submit= (ProgressBar) findViewById(R.id.progress_bar_submit);
        tV_submit= (TextView) findViewById(R.id.tV_submit);

        // receive datas from last activity class
        Intent intent=getIntent();
        userName=intent.getStringExtra("userName");
        postId=intent.getStringExtra("postId");
        isFromNotification = intent.getBooleanExtra("isFromNotification",false);
        String userImage=intent.getStringExtra("userImage");

        // set user pic
        ImageView iV_userPic= (ImageView) findViewById(R.id.iV_userPic);
        iV_userPic.getLayoutParams().width= CommonClass.getDeviceWidth(mActivity)/4;
        iV_userPic.getLayoutParams().height= CommonClass.getDeviceWidth(mActivity)/4;

        if (userImage!=null && !userImage.isEmpty())
            Picasso.with(mActivity)
                    .load(userImage)
                    .placeholder(R.drawable.default_profile_image)
                    .placeholder(R.drawable.default_profile_image)
                    .transform(new CircleTransform())
                    .into(iV_userPic);

        String setUserName="";
        if (userName!=null)
            setUserName=getResources().getString(R.string.would_you_sell_to_megan_again)+" "+userName+" "+getResources().getString(R.string.again);

        System.out.println(TAG+" "+"set user name="+setUserName);

        // set rate user
        TextView tV_rate_user= (TextView) findViewById(R.id.tV_rate_user);
        tV_rate_user.setText(setUserName);

        // Back button
        RelativeLayout rL_back_btn= (RelativeLayout) findViewById(R.id.rL_back_btn);
        rL_back_btn.setOnClickListener(this);

        // rating
        mRatingBar = (RatingBar) findViewById(R.id.ratingBar);
        tV_absolutely= (TextView) findViewById(R.id.tV_absolutely);
        tV_not= (TextView) findViewById(R.id.tV_not);
        tV_yes= (TextView) findViewById(R.id.tV_yes);
        tV_definitely= (TextView) findViewById(R.id.tV_definitely);
        rL_submit= (RelativeLayout) findViewById(R.id.rL_submit);
        rL_rootElement= (RelativeLayout) findViewById(R.id.rL_rootElement);
        final int purple_color=ContextCompat.getColor(mActivity,R.color.purple_color);
        final int gray_color=ContextCompat.getColor(mActivity,R.color.login_tab_bg);
        CommonClass.setViewOpacity(mActivity,rL_submit,102,R.color.purple_color);

        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                System.out.println(TAG+" "+"rating progress="+rating);

                if (rating==1 || rating==2)
                {
                    tV_absolutely.setTextColor(purple_color);
                    tV_not.setTextColor(purple_color);
                    tV_yes.setTextColor(gray_color);
                    tV_definitely.setTextColor(gray_color);
                }
                else
                {
                    tV_absolutely.setTextColor(gray_color);
                    tV_not.setTextColor(gray_color);
                    tV_yes.setTextColor(purple_color);
                    tV_definitely.setTextColor(purple_color);
                }

                // show submit button
                if (rating==0)
                {
                    tV_absolutely.setTextColor(gray_color);
                    tV_not.setTextColor(gray_color);
                    tV_yes.setTextColor(gray_color);
                    tV_definitely.setTextColor(gray_color);
                    isToSubmit=false;
                    CommonClass.setViewOpacity(mActivity,rL_submit,102,R.color.purple_color);
                }
                else
                {
                    isToSubmit=true;
                    CommonClass.setViewOpacity(mActivity,rL_submit,255,R.color.purple_color);
                }
            }
        });

        // submit user rating
        rL_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isToSubmit)
                {
                    progress_bar_submit.setVisibility(View.VISIBLE);
                    tV_submit.setVisibility(View.GONE);

                    if (isFromNotification)
                        rateUserApi();
                    else
                        markSold();
                }
            }
        });
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

    /**
     * <h>MarkSold</h>
     * <p>
     *     In this method we used to rate the buyer when we used to come from
     *     EditProduct screen.
     * </p>
     */
    private void markSold() {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            // postId, type, ratings, membername, buyername
            JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("token",mSessionManager.getAuthToken());
                request_datas.put("postId", postId);
                request_datas.put("type", "0");
                request_datas.put("ratings", mRatingBar.getRating());
                request_datas.put("membername", userName);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.MARK_SOLD, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag)
                {
                    progress_bar_submit.setVisibility(View.GONE);
                    tV_submit.setVisibility(View.VISIBLE);

                    Gson gson = new Gson();
                    RateUserMainPojo rateUserMainPojo = gson.fromJson(result,RateUserMainPojo.class);

                    switch (rateUserMainPojo.getCode())
                    {
                        // success
                        case "200" :
                            mEventBusDatasHandler.addSoldDatasFromRateUser(rateUserMainPojo.getData());
                            mEventBusDatasHandler.removeHomePageDatasFromEditPost(rateUserMainPojo.getData().getPostId());
                            mEventBusDatasHandler.removeSocialDatasFromEditPost(rateUserMainPojo.getData().getPostId());
                            mEventBusDatasHandler.removeSellingDatasFromEditPost(rateUserMainPojo.getData().getPostId());

                            Intent intent =new Intent();
                            intent.putExtra("isToFinishSelectBuyer",true);
                            setResult(VariableConstants.RATE_USER_REQ_CODE,intent);
                            finish();
                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // error
                        default:
                            CommonClass.showSnackbarMessage(rL_rootElement,rateUserMainPojo.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    CommonClass.showSnackbarMessage(rL_rootElement,error);
                }
            });
        }
        else
        {
            progress_bar_submit.setVisibility(View.GONE);
            tV_submit.setVisibility(View.VISIBLE);
            CommonClass.showSnackbarMessage(rL_rootElement,getResources().getString(R.string.NoInternetAccess));
        }
    }

    /**
     * <h>RateUserApi</h>
     * <p>
     *     In this method we used to do api call to rate the seller when we usually come
     *     from Notification screen.
     * </p>
     */
    private void rateUserApi()
    {
        if (CommonClass.isNetworkAvailable(mActivity))
        {
            progress_bar_submit.setVisibility(View.VISIBLE);
            tV_submit.setVisibility(View.GONE);
            String url= ApiUrl.RATE_USER+userName;
            JSONObject request_data = new JSONObject();
            try {
                request_data.put("token",mSessionManager.getAuthToken());
                request_data.put("rating", mRatingBar.getRating());
                request_data.put("postId", postId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, url, OkHttp3Connection.Request_type.POST, request_data, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    progress_bar_submit.setVisibility(View.GONE);
                    tV_submit.setVisibility(View.VISIBLE);

                    System.out.println(TAG+" "+"rate user res="+result);
                    RateSellerPojo rateSellerPojo;
                    Gson gson = new Gson();
                    rateSellerPojo = gson.fromJson(result,RateSellerPojo.class);

                    switch (rateSellerPojo.getCode())
                    {
                        // success
                        case "200" :
                            CommonClass.showSuccessSnackbarMsg(rL_rootElement,rateSellerPojo.getMessage());
                            final Timer t = new Timer();
                            t.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    // when the task active then close the activity
                                    t.cancel();
                                    //EditProfileActivity.editProfileInstance.finish();
                                    //SelectBuyerActivity.selectBuyerActInstance.finish();
                                    onBackPressed();
                                }
                            }, 3000);
                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // error
                        default:
                            CommonClass.showSnackbarMessage(rL_rootElement,rateSellerPojo.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {

                }
            });
        }
        else CommonClass.showSnackbarMessage(rL_rootElement,getResources().getString(R.string.NoInternetAccess));
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
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
}
