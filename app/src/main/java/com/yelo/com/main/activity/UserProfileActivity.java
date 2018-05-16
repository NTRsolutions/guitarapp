package com.yelo.com.main.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.yelo.com.R;
import com.yelo.com.adapter.ViewPagerAdapter;
import com.yelo.com.fcm_push_notification.Config;
import com.yelo.com.fcm_push_notification.NotificationMessageDialog;
import com.yelo.com.fcm_push_notification.NotificationUtils;
import com.yelo.com.main.tab_fragments.ProfileFrag;
import com.yelo.com.main.view_pager.my_profile_frag.SellingFrag;
import com.yelo.com.main.view_pager.my_profile_frag.SoldFrag;
import com.yelo.com.pojo_class.profile_pojo.ProfilePojoMain;
import com.yelo.com.pojo_class.profile_pojo.ProfileResultDatas;
import com.yelo.com.utility.ApiCall;
import com.yelo.com.utility.ApiUrl;
import com.yelo.com.utility.CircleTransform;
import com.yelo.com.utility.CommonClass;
import com.yelo.com.utility.DialogBox;
import com.yelo.com.utility.OkHttp3Connection;
import com.yelo.com.utility.PositionedCropTransformation;
import com.yelo.com.utility.RunTimePermission;
import com.yelo.com.utility.SessionManager;
import com.yelo.com.utility.VariableConstants;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import static android.Manifest.permission.CALL_PHONE;

/**
 * <h>ProfileFrag</h>
 * <p>
 *     In this class we show the user profile description like user name, user profile
 *     users total posts, following count, follower count etc
 * </p>
 * @since 11-May-17
 */
public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = ProfileFrag.class.getSimpleName();
    private Activity mActivity;
    private String profilePicUrl="",fullName="", membername="",followStatus="",phoneNumber="",email="";
    private ApiCall mApiCall;
    private RunTimePermission runTimePermission;
    private String[] permissionsArray;
    private SessionManager mSessionManager;
    private boolean isOwnUserFlag;
    private int intFollowerCount=0;

    // Declare xml variables
    private ProgressBar progress_bar_profile;
    private ImageView iV_profile_pic,iv_background,iV_fbicon,iV_google_icon,iV_email_icon,iV_follow;
    private TextView tV_posts_count,tV_follower_count,tV_following_count,tV_fullName;
    private ViewPager chatViewPager;
    private CoordinatorLayout coordinate_rootView;
    private AppBarLayout appBarLayout;
    private RelativeLayout rL_report_user;
    private NotificationMessageDialog mNotificationMessageDialog;

    private String avgRating="",ratedBy="";
    private RatingBar ratingBar;
    private TextView tv_ratedBy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);

        initVariables();
    }

    /**
     * <h>InitVariables</h>
     * <p>
     *     In this method we used to initialize all variables.
     * </p>
     */
    private void initVariables()
    {
        mActivity=UserProfileActivity.this;
        mNotificationMessageDialog=new NotificationMessageDialog(mActivity);
        mSessionManager=new SessionManager(mActivity);
        mApiCall=new ApiCall(mActivity);
        coordinate_rootView= (CoordinatorLayout) findViewById(R.id.coordinate_rootView);
        appBarLayout= (AppBarLayout) findViewById(R.id.appBarLayout);
        appBarLayout.setVisibility(View.GONE);
        ratingBar=(RatingBar)findViewById(R.id.ratingBar);
        tv_ratedBy=(TextView)findViewById(R.id.tV_ratedBy);

        permissionsArray =new String[]{CALL_PHONE};
        runTimePermission=new RunTimePermission(mActivity, permissionsArray,false);

        // Receiving datas from last class
        Intent intent=getIntent();
        membername=intent.getStringExtra("membername");

        isOwnUserFlag = membername.equals(mSessionManager.getUserName());

        // Back button
        RelativeLayout rL_back_btn= (RelativeLayout) findViewById(R.id.rL_back_btn);
        rL_back_btn.setOnClickListener(this);

        progress_bar_profile= (ProgressBar)findViewById(R.id.progress_bar_profile);

        // profile pic
        iV_profile_pic= (ImageView)findViewById(R.id.iV_profile_pic);
        iV_profile_pic.getLayoutParams().width=CommonClass.getDeviceWidth(mActivity)/4;
        iV_profile_pic.getLayoutParams().height=CommonClass.getDeviceWidth(mActivity)/4;

        // background pic
        iv_background= (ImageView) findViewById(R.id.iv_background);
        iV_fbicon= (ImageView) findViewById(R.id.iV_fbicon);
        iV_google_icon= (ImageView) findViewById(R.id.iV_google_icon);
        iV_email_icon= (ImageView) findViewById(R.id.iV_email_icon);
        iV_follow= (ImageView) findViewById(R.id.iV_follow);
        iV_follow.setOnClickListener(this);
        ImageView iV_call_mail= (ImageView) findViewById(R.id.iV_call_mail);
        iV_call_mail.setOnClickListener(this);

        // number of posts
        tV_posts_count= (TextView)findViewById(R.id.tV_posts_count);
        tV_follower_count= (TextView)findViewById(R.id.tV_follower_count);
        tV_following_count= (TextView)findViewById(R.id.tV_following_count);

        // Following
        RelativeLayout rL_following= (RelativeLayout)findViewById(R.id.rL_following);
        rL_following.setOnClickListener(this);

        // Follower
        RelativeLayout rL_follower= (RelativeLayout)findViewById(R.id.rL_follower);
        rL_follower.setOnClickListener(this);

        // User name and full name
        tV_fullName= (TextView)findViewById(R.id.tV_fullName);

        // report user
        rL_report_user= (RelativeLayout) findViewById(R.id.rL_report_user);
        rL_report_user.setOnClickListener(this);
        // view pager
        chatViewPager= (ViewPager)findViewById(R.id.viewpager);
        TabLayout tabs= (TabLayout)findViewById(R.id.tabs);
        tabs.setupWithViewPager(chatViewPager);

        if (mSessionManager.getIsUserLoggedIn())
        getUserProfileDatas(ApiUrl.USER_PROFILE);
        else getUserProfileDatas(ApiUrl.GUEST_PROFILE);
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
     * <h>GetUserProfileDatas</h>
     * <p>
     *     In this method we do api call to get users profile complete description
     *     Once we receive all datas then set all values to the respective fields.
     * </p>
     */
    private void getUserProfileDatas(String apiUrl)
    {
        // token, limit, offset, membername
        if (CommonClass.isNetworkAvailable(mActivity)) {
            progress_bar_profile.setVisibility(View.VISIBLE);
            JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("token", mSessionManager.getAuthToken());
                request_datas.put("limit", "20");
                request_datas.put("offset", "0");
                request_datas.put("membername",membername);

            }

            catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, apiUrl, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    progress_bar_profile.setVisibility(View.GONE);
                    System.out.println(TAG + " " + "my profile res=" + result);

                    ProfilePojoMain profilePojoMain;
                    Gson gson=new Gson();
                    profilePojoMain=gson.fromJson(result,ProfilePojoMain.class);

                    switch (profilePojoMain.getCode())
                    {
                        // Success
                        case "200" :

                            // Set my profile fields like image, name etc
                            ArrayList<ProfileResultDatas> profileResponseDatas=profilePojoMain.getData();
                            if (profileResponseDatas!=null && profileResponseDatas.size()>0)
                            {
                                appBarLayout.setVisibility(View.VISIBLE);
                                String  noOfPost, followersCount, followingCount,googleVerified,facebookVerified,emailVerified;
                                profilePicUrl = profileResponseDatas.get(0).getProfilePicUrl();
                                noOfPost = profileResponseDatas.get(0).getPosts();
                                followersCount = profileResponseDatas.get(0).getFollowers();
                                followingCount = profileResponseDatas.get(0).getFollowing();
                                if (followersCount!=null && !followersCount.isEmpty())
                                    intFollowerCount=Integer.parseInt(followersCount);
                                googleVerified=profileResponseDatas.get(0).getGoogleVerified();
                                facebookVerified=profileResponseDatas.get(0).getFacebookVerified();
                                emailVerified=profileResponseDatas.get(0).getEmailVerified();
                                fullName = profileResponseDatas.get(0).getFullName();
                                followStatus=profileResponseDatas.get(0).getFollowStatus();
                                phoneNumber=profileResponseDatas.get(0).getPhoneNumber();
                                email=profileResponseDatas.get(0).getEmail();
                                String paypalUrl=profileResponseDatas.get(0).getPaypalUrl();
                                avgRating=profileResponseDatas.get(0).getAvgRating();
                                ratedBy=profileResponseDatas.get(0).getRatedBy();


                                if(avgRating!=null && !avgRating.isEmpty()){
                                    ratingBar.setVisibility(View.VISIBLE); //..visible while get rating
                                    ratingBar.setRating(Float.parseFloat(avgRating));
                                }

                                if(ratedBy!=null && !ratedBy.isEmpty()){
                                    ratingBar.setVisibility(View.VISIBLE); //..visible while get rating
                                    tv_ratedBy.setVisibility(View.VISIBLE); //..visible while get rating
                                    ratedBy="("+ratedBy+")";
                                    tv_ratedBy.setText(ratedBy);
                                }

                                if (membername.equals(mSessionManager.getUserName()))
                                {
                                    rL_report_user.setVisibility(View.GONE);
                                }

                                System.out.println(TAG+" "+"followStatus="+followStatus);

                                // Set profile pic
                                if (profilePicUrl != null && !profilePicUrl.isEmpty())
                                {
                                    Picasso.with(mActivity)
                                            .load(profilePicUrl)
                                            .placeholder(R.drawable.profile_bg)
                                            .error(R.drawable.profile_bg)
                                            .transform(new CircleTransform())
                                            .into(iV_profile_pic);

                                    Glide.with(mActivity)
                                            .load(profilePicUrl)
                                            .transform(new PositionedCropTransformation((mActivity), 0, 0))
                                            .placeholder(R.drawable.default_cover_image)
                                            .error(R.drawable.default_cover_image)
                                            .into(iv_background);
                                }

                                // set facebook verified icon
                                if (facebookVerified!=null && facebookVerified.equals("1"))
                                    iV_fbicon.setImageResource(R.drawable.facebook_verified_icon);

                                // set google plus verified icon
                                if (googleVerified!=null && googleVerified.isEmpty())
                                    iV_google_icon.setImageResource(R.drawable.gplus_verified_icon);

                                // set email verified iocn
                                if (emailVerified!=null && emailVerified.isEmpty())
                                    iV_email_icon.setImageResource(R.drawable.email_verified_icon);

                                // set follow or following icon
                                if (followStatus!=null && followStatus.equals("1"))
                                    iV_follow.setImageResource(R.drawable.followed_icon);
                                else iV_follow.setImageResource(R.drawable.follow_icon);

                                // set number of posts
                                if (noOfPost != null)
                                    tV_posts_count.setText(noOfPost);

                                // Followers count
                                if (followersCount != null)
                                    tV_follower_count.setText(followersCount);

                                // Following count
                                if (followingCount != null)
                                    tV_following_count.setText(followingCount);

                                // Full name
                                if (fullName != null)
                                    tV_fullName.setText(fullName);

                            }
                            setupViewPager();
                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // Error
                        default:
                            CommonClass.showTopSnackBar(coordinate_rootView,profilePojoMain.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    progress_bar_profile.setVisibility(View.GONE);
                    CommonClass.showTopSnackBar(coordinate_rootView,error);
                }
            });
        }
        else  CommonClass.showTopSnackBar(coordinate_rootView,getResources().getString(R.string.NoInternetAccess));
    }

    /**
     * <h>SetupViewPager</h>
     * <p>
     *     In this method we used to set viewpager with respective fragments.
     * </p>
     */
    private void setupViewPager()
    {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(SellingFrag.newInstance(membername,isOwnUserFlag),getResources().getString(R.string.selling));
        adapter.addFragment(SoldFrag.newInstance(membername,isOwnUserFlag),getResources().getString(R.string.sold));
        chatViewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed()
    {
        Intent intent=new Intent();
        intent.putExtra("followStatus",followStatus);
        setResult(VariableConstants.USER_FOLLOW_REQ_CODE,intent);
        finish();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId())
        {
            // back button
            case R.id.rL_back_btn :
                onBackPressed();
                break;

            // Report user
            case R.id.rL_report_user :
                if (mSessionManager.getIsUserLoggedIn()) {
                    intent=new Intent(mActivity,ReportUserActivity.class);
                    intent.putExtra("userImage",profilePicUrl);
                    intent.putExtra("userName",membername);
                    intent.putExtra("userFullName",fullName);
                    startActivity(intent);
                }
                else startActivityForResult(new Intent(mActivity,LandingActivity.class), VariableConstants.LANDING_REQ_CODE);
                break;

            // Following
            case R.id.rL_following :
                intent=new Intent(mActivity, UserFollowingActivity.class);
                intent.putExtra("title",getResources().getString(R.string.Following));
                intent.putExtra("isFollower",false);
                intent.putExtra("membername",membername);
                startActivity(intent);
                break;

            // Follower
            case R.id.rL_follower :
                intent=new Intent(mActivity, UserFollowingActivity.class);
                intent.putExtra("title",getResources().getString(R.string.Followers));
                intent.putExtra("isFollower",true);
                intent.putExtra("membername",membername);
                intent.putExtra("followerCount",intFollowerCount);
                startActivityForResult(intent,VariableConstants.FOLLOW_COUNT_REQ_CODE);
                break;

            // open call or mail dialod
            case R.id.iV_call_mail :
                callOrEmailDialog();
                break;

            // follow or following
            case R.id.iV_follow :
                if (mSessionManager.getIsUserLoggedIn()) {
                    if (CommonClass.isNetworkAvailable(mActivity))
                    {

                        String url;
                        if (followStatus != null && followStatus.equals("1")) {
                            url = ApiUrl.UNFOLLOW + membername;
                            unfollowUserAlert(url);
                        }
                        else {
                            url = ApiUrl.FOLLOW + membername;
                            mApiCall.followUserApi(url);
                            iV_follow.setImageResource(R.drawable.followed_icon);
                            followStatus = "1";
                            intFollowerCount = intFollowerCount+1;

                            if (intFollowerCount >=0)
                                tV_follower_count.setText(String.valueOf(intFollowerCount));
                        }

                    } else {
                        CommonClass.showSnackbarMessage(coordinate_rootView, getResources().getString(R.string.NoInternetAccess));
                    }
                }
                else startActivityForResult(new Intent(mActivity,LandingActivity.class), VariableConstants.LANDING_REQ_CODE);
                break;
        }
    }

    /**
     * <h>unfollowUserAlert</h>
     * <p>
     *     In this method we used to open a simple dialog pop-up to show
     *     alert to unfollow
     * </p>
     */
    public void unfollowUserAlert(final String url)
    {
        final Dialog unfollowUserDialog = new Dialog(mActivity);
        unfollowUserDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        unfollowUserDialog.setContentView(R.layout.dialog_unfollow_user);
        unfollowUserDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        unfollowUserDialog.getWindow().setLayout((int)(CommonClass.getDeviceWidth(mActivity)*0.9), RelativeLayout.LayoutParams.WRAP_CONTENT);

        // set user pic
        ImageView imageViewPic= (ImageView)unfollowUserDialog.findViewById(R.id.iV_userPic);
        imageViewPic.getLayoutParams().width=CommonClass.getDeviceWidth(mActivity)/5;
        imageViewPic.getLayoutParams().height=CommonClass.getDeviceWidth(mActivity)/5;

        // posted by pic
        if (profilePicUrl!=null && !profilePicUrl.isEmpty())
            Picasso.with(mActivity)
                    .load(profilePicUrl)
                    .transform(new CircleTransform())
                    .placeholder(R.drawable.default_profile_image)
                    .error(R.drawable.default_profile_image)
                    .into(imageViewPic);

        // set user name
        TextView tV_userName= (TextView)unfollowUserDialog.findViewById(R.id.tV_userName);
        if (membername!=null && !membername.isEmpty())
        {
            String setUserName=getResources().getString(R.string.at_the_rate)+membername+getResources().getString(R.string.question_mark);
            tV_userName.setText(setUserName);
        }

        // set cancel button
        TextView tV_cancel= (TextView) unfollowUserDialog.findViewById(R.id.tV_cancel);
        tV_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unfollowUserDialog.dismiss();
            }
        });

        // set done button
        TextView tV_unfollow= (TextView)unfollowUserDialog.findViewById(R.id.tV_unfollow);
        tV_unfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApiCall.followUserApi(url);
                iV_follow.setImageResource(R.drawable.follow_icon);
                followStatus="0";
                intFollowerCount = intFollowerCount-1;
                tV_follower_count.setText(String.valueOf(intFollowerCount));
                unfollowUserDialog.dismiss();
            }
        });
        unfollowUserDialog.show();
    }

    /**
     * In this method we open one simple Dialog pop-up to show
     * option like call or mail to the the user
     */
    public void callOrEmailDialog()
    {
        final Dialog showOptionDialog=new Dialog(mActivity);
        showOptionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        showOptionDialog.getWindow().setGravity(Gravity.BOTTOM);
        showOptionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        showOptionDialog.setContentView(R.layout.dialog_call_mail);
        showOptionDialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
        showOptionDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        // call
        RelativeLayout rL_call= (RelativeLayout) showOptionDialog.findViewById(R.id.rL_call);
        rL_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callUser();
                showOptionDialog.dismiss();
            }
        });

        // mail
        RelativeLayout rL_mail= (RelativeLayout) showOptionDialog.findViewById(R.id.rL_mail);
        rL_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonClass.sendEmail(mActivity,email,mSessionManager.getUserName(),"");
            }
        });

        // cancel button
        TextView cancel_button= (TextView) showOptionDialog.findViewById(R.id.cancel_button);
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOptionDialog.dismiss();
            }
        });

        showOptionDialog.show();
    }

    private void callUser()
    {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        String mobileNo="tel:"+phoneNumber;
        System.out.println(TAG+" "+"mob no="+mobileNo);
        callIntent.setData(Uri.parse(mobileNo));

        if (runTimePermission.checkPermissions(permissionsArray))
            startActivity(callIntent);
        else runTimePermission.requestPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case VariableConstants.PERMISSION_REQUEST_CODE :

                System.out.println("grant result="+grantResults.length);
                if (grantResults.length>0)
                {
                    for (int count=0;count<grantResults.length;count++)
                    {
                        if (grantResults[count]!= PackageManager.PERMISSION_GRANTED)
                            runTimePermission.allowPermissionAlert(permissions[count]);

                    }


                    if (runTimePermission.checkPermissions(permissionsArray))
                    {
                        callUser();
                    }
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data!=null)
        {
            switch (requestCode) {
                case VariableConstants.LANDING_REQ_CODE:
                    boolean isToRefreshHomePage = data.getBooleanExtra("isToRefreshHomePage", true);
                    System.out.println(TAG + " " + "isToRefreshHomePage=" + isToRefreshHomePage);
                    if (isToRefreshHomePage && mSessionManager.getUserName().equals(membername))
                    {
                        Intent intent = new Intent(mActivity, SelfProfileActivity.class);
                        intent.putExtra("membername",membername);
                        startActivity(intent);
                        finish();
                    }

                    boolean isFromSignup = data.getBooleanExtra("isFromSignup",false);

                    // open start browsering screen
                    if (isFromSignup)
                        new DialogBox(mActivity).startBrowsingDialog();

                    break;

                    // follower count
                case VariableConstants.FOLLOW_COUNT_REQ_CODE :
                    System.out.println(TAG+" "+"followerCount="+data.getIntExtra("followerCount",0));
                    break;
            }
        }
    }
}
