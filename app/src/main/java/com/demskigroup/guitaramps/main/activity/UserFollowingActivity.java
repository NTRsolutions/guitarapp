package com.demskigroup.guitaramps.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.adapter.UserFollowRvAdapter;
import com.demskigroup.guitaramps.fcm_push_notification.Config;
import com.demskigroup.guitaramps.fcm_push_notification.NotificationMessageDialog;
import com.demskigroup.guitaramps.fcm_push_notification.NotificationUtils;
import com.demskigroup.guitaramps.pojo_class.user_follow_pojo.FollowMainPojo;
import com.demskigroup.guitaramps.pojo_class.user_follow_pojo.FollowResponseDatas;
import com.demskigroup.guitaramps.utility.ApiUrl;
import com.demskigroup.guitaramps.utility.CommonClass;
import com.demskigroup.guitaramps.utility.OkHttp3Connection;
import com.demskigroup.guitaramps.utility.SessionManager;
import com.demskigroup.guitaramps.utility.VariableConstants;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * <h>UserFollowingActivity</h>
 * <p>
 *     In this class we used to show the other user follower or following list. We used
 *     to do api call to receive the datas for follower or following.
 * </p>
 * @since 01-Aug-17
 */
public class UserFollowingActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = UserFollowingActivity.class.getSimpleName();
    private Activity mActivity;
    private SessionManager mSessionManager;
    private boolean isFollower;
    private RelativeLayout rL_rootview,rL_no_following;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<FollowResponseDatas> arrayListFollow;
    private UserFollowRvAdapter followRvAdapter;
    int index;
    private NotificationMessageDialog mNotificationMessageDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_following);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        index=0;
        initVariables();
    }

    /**
     * <h>initVariables</h>
     * <p>
     *     In this method we used to initialize xml data member and other variables.
     * </p>
     */
    private void initVariables()
    {
        // receiving values from last class
        Intent intent=getIntent();
        String title=intent.getStringExtra("title");
        final String membername=intent.getStringExtra("membername");
        isFollower=intent.getBooleanExtra("isFollower",false);
        int followerCount = intent.getIntExtra("followerCount", 0);

        arrayListFollow=new ArrayList<>();
        System.out.println(TAG+" "+"membername="+membername);

        // initialize variables
        mActivity=UserFollowingActivity.this;
        mNotificationMessageDialog=new NotificationMessageDialog(mActivity);
        CommonClass.statusBarColor(mActivity);

        mSessionManager=new SessionManager(mActivity);
        TextView tV_title= (TextView) findViewById(R.id.tV_title);
        if (title!=null)
            tV_title.setText(title);
        mSwipeRefreshLayout= (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.pink_color);
        rL_rootview= (RelativeLayout) findViewById(R.id.rL_rootview);
        RecyclerView rV_follow = (RecyclerView) findViewById(R.id.rV_follow);
        mProgressBar= (ProgressBar) findViewById(R.id.progress_bar_follow);
        mProgressBar.setVisibility(View.GONE);
        RelativeLayout rL_back_btn= (RelativeLayout) findViewById(R.id.rL_back_btn);
        rL_back_btn.setOnClickListener(this);

        // set recycler view adapter
        followRvAdapter=new UserFollowRvAdapter(mActivity,arrayListFollow,followerCount);
        LinearLayoutManager layoutManager=new LinearLayoutManager(mActivity);
        rV_follow.setLayoutManager(layoutManager);
        rV_follow.setAdapter(followRvAdapter);

        // No Following or follower default var
        ImageView iV_following_icon= (ImageView) findViewById(R.id.iV_following_icon);
        TextView tV_no_following= (TextView) findViewById(R.id.tV_no_following);
        rL_no_following= (RelativeLayout) findViewById(R.id.rL_no_following);
        rL_no_following.setVisibility(View.GONE);

        // set api url according to condition like follower or following
        final String apiUrl;
        if (isFollower)
        {
            apiUrl= ApiUrl.GET_MEMBER_FOLLOWER;
            iV_following_icon.setImageResource(R.drawable.empty_follower);
            tV_no_following.setText(getResources().getString(R.string.no_follower_yet));
        }
        else
        {
            apiUrl=ApiUrl.GET_MEMBER_FOLLOWING;
            iV_following_icon.setImageResource(R.drawable.empty_following);
            tV_no_following.setText(getResources().getString(R.string.no_following_yet));
        }

        if (membername!=null && !membername.isEmpty())
        {
            mProgressBar.setVisibility(View.VISIBLE);
            getFollowingService(index,apiUrl,membername);
        }

        // pull to refresh
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                arrayListFollow.clear();
                getFollowingService(index,apiUrl,membername);

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
     * <h>GetFollowingService</h>
     * <p>
     *     In this method we used to do api call to get follower or following list
     *     for particular user.
     * </p>
     * @param offsetValue The page index
     * @param ApiUrl The url for follower or following
     * @param membername The user name
     */
    private void getFollowingService(int offsetValue,String ApiUrl,String membername)
    {
        if (CommonClass.isNetworkAvailable(mActivity))
        {
            int limit=20;
            offsetValue=offsetValue*limit;

            JSONObject request_datas=new JSONObject();
            try {
                request_datas.put("token",mSessionManager.getAuthToken());
                request_datas.put("membername",membername);
                request_datas.put("offset",offsetValue);
                request_datas.put("limit",limit);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG,ApiUrl, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    mProgressBar.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(false);

                    FollowMainPojo followMainPojo;
                    Gson gson=new Gson();
                    followMainPojo=gson.fromJson(result, FollowMainPojo.class);

                    switch (followMainPojo.getCode())
                    {
                        // success
                        case "200" :
                            if (isFollower)
                                arrayListFollow.addAll(followMainPojo.getMemberFollowers());
                            else
                                arrayListFollow.addAll(followMainPojo.getFollowing());

                            if (arrayListFollow!=null && arrayListFollow.size()>0)
                            {
                                followRvAdapter.notifyDataSetChanged();
                            }
                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // no following
                        case "204" :
                            rL_no_following.setVisibility(View.VISIBLE);
                            break;

                        // error
                        default:
                            CommonClass.showSnackbarMessage(rL_rootview,followMainPojo.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    mProgressBar.setVisibility(View.GONE);
                }
            });
        }
        else CommonClass.showSnackbarMessage(rL_rootview,getResources().getString(R.string.NoInternetAccess));
    }

    @Override
    public void onBackPressed()
    {
        Intent intent=new Intent();
        intent.putExtra("followerCount",followRvAdapter.followingCount);
        setResult(VariableConstants.FOLLOW_COUNT_REQ_CODE,intent);
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
