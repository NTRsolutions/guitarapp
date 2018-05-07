package com.yelo.com.main.activity;

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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.google.gson.Gson;
import com.yelo.com.R;
import com.yelo.com.adapter.UserLikesRvAdap;
import com.yelo.com.fcm_push_notification.Config;
import com.yelo.com.fcm_push_notification.NotificationMessageDialog;
import com.yelo.com.fcm_push_notification.NotificationUtils;
import com.yelo.com.pojo_class.user_likes_pojo.UserLikesMainPojo;
import com.yelo.com.pojo_class.user_likes_pojo.UserLikesResponseDatas;
import com.yelo.com.utility.ApiUrl;
import com.yelo.com.utility.CommonClass;
import com.yelo.com.utility.OkHttp3Connection;
import com.yelo.com.utility.SessionManager;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * <h>UserLikesActivity</h>
 * <p>
 *     In this class we used to show the list of user those who liked the product. Alongwith
 *     the follow & following option to follow or unfollow.
 * </p>
 * @since 4/28/2017
 * @version 1.0
 * @author 3Embed
 */
public class UserLikesActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = UserLikesActivity.class.getSimpleName();
    private Activity mActivity;
    private SessionManager mSessionManager;
    private String postId="",postType="";
    private RelativeLayout rL_rootview,rL_noLikesFound;
    private ProgressBar mProgress_bar;
    private RecyclerView rV_userLikes;
    private SwipeRefreshLayout mRefreshLayout;
    private ArrayList<UserLikesResponseDatas> aL_likesDatas;
    private UserLikesRvAdap userLikesRvAdap;
    private LinearLayoutManager layoutManager;

    // Load more
    private boolean isLoading;
    private int pageIndex=0;
    private int visibleThreshold=5,totalVisibleItem,totalItemCount;
    private NotificationMessageDialog mNotificationMessageDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_likes);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);

        initVariables();
    }

    /**
     * <h>InitVariables</h>
     * <p>
     *     In this method we used to initialize the all xml variables.
     * </p>
     */
    private void initVariables()
    {
        mActivity=UserLikesActivity.this;
        mNotificationMessageDialog=new NotificationMessageDialog(mActivity);
        CommonClass.statusBarColor(mActivity);

        // receiving variables
        Intent intent=getIntent();
        postId=intent.getStringExtra("postId");
        postType=intent.getStringExtra("postType");

        pageIndex=0;
        aL_likesDatas=new ArrayList<>();
        mSessionManager=new SessionManager(mActivity);
        mRefreshLayout= (SwipeRefreshLayout) findViewById(R.id.mRefreshLayout);
        rL_rootview= (RelativeLayout) findViewById(R.id.rL_rootview);
        rL_noLikesFound= (RelativeLayout) findViewById(R.id.rL_noLikesFound);
        rL_noLikesFound.setVisibility(View.GONE);
        RelativeLayout rL_back_btn = (RelativeLayout) findViewById(R.id.rL_back_btn);
        rL_back_btn.setOnClickListener(this);
        mProgress_bar= (ProgressBar) findViewById(R.id.progress_bar);
        rV_userLikes= (RecyclerView) findViewById(R.id.rV_userLikes);

        // set recyclerview adapter
        userLikesRvAdap=new UserLikesRvAdap(mActivity,aL_likesDatas);
        layoutManager=new LinearLayoutManager(mActivity);
        rV_userLikes.setLayoutManager(layoutManager);
        rV_userLikes.setAdapter(userLikesRvAdap);

        // Pull to refresh
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageIndex=0;
                aL_likesDatas.clear();
                getAllLikesApi(pageIndex);
            }
        });

        // call api call method
        if (CommonClass.isNetworkAvailable(mActivity)) {
            mProgress_bar.setVisibility(View.VISIBLE);
            getAllLikesApi(pageIndex);
        }
        else CommonClass.showSnackbarMessage(rL_rootview,getResources().getString(R.string.NoInternetAccess));
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
     * <h>GetAllLikesApi</h>
     * <p>
     *     In this method we used to do the api call using OkHttp client
     *     to get all likes users.
     * </p>
     */
    private void getAllLikesApi(int offset)
    {
        if (CommonClass.isNetworkAvailable(mActivity))
        {
            int limit=20;
            offset=limit*offset;

            JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("postId", postId);
                request_datas.put("postType", postType);
                request_datas.put("offset",offset);
                request_datas.put("limit",limit);
                request_datas.put("token", mSessionManager.getAuthToken());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            System.out.println(TAG+" "+"offset="+offset);

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.GET_ALL_LIKES, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    System.out.println(TAG);
                    mProgress_bar.setVisibility(View.GONE);

                    mRefreshLayout.setRefreshing(false);
                    UserLikesMainPojo userLikesMainPojo;
                    Gson gson=new Gson();
                    userLikesMainPojo=gson.fromJson(result,UserLikesMainPojo.class);

                    switch (userLikesMainPojo.getCode())
                    {
                        // suceess
                        case "200" :
                            mRefreshLayout.setRefreshing(false);
                            aL_likesDatas.addAll(userLikesMainPojo.getData());
                            if (aL_likesDatas!=null && aL_likesDatas.size()>0)
                            {
                                userLikesRvAdap.notifyDataSetChanged();

                                // prevent loading more datas when item count is less then 15
                                if (aL_likesDatas.size()<15)
                                {
                                    isLoading=true;
                                }

                                // Load more
                                rV_userLikes.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                    @Override
                                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                        super.onScrolled(recyclerView, dx, dy);

                                        totalItemCount=layoutManager.getItemCount();
                                        totalVisibleItem=layoutManager.findLastVisibleItemPosition();

                                        if (!isLoading && totalItemCount<=(totalVisibleItem+visibleThreshold))
                                        {
                                            pageIndex=pageIndex+1;
                                            getAllLikesApi(pageIndex);
                                        }
                                    }
                                });
                            }
                            else rL_noLikesFound.setVisibility(View.VISIBLE);
                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // No Likes found
                        case "56713" :
                            rL_noLikesFound.setVisibility(View.VISIBLE);
                            break;

                        // error
                        default:
                            CommonClass.showSnackbarMessage(rL_rootview,userLikesMainPojo.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    mProgress_bar.setVisibility(View.GONE);
                    CommonClass.showSnackbarMessage(rL_rootview,error);
                }
            });
        }
        else CommonClass.showSnackbarMessage(rL_rootview,getResources().getString(R.string.NoInternetAccess));
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
