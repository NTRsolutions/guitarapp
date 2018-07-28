package com.demskigroup.guitaramps.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.google.gson.Gson;
import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.adapter.DiscoveryPeopleRvAdapter;
import com.demskigroup.guitaramps.fcm_push_notification.Config;
import com.demskigroup.guitaramps.fcm_push_notification.NotificationMessageDialog;
import com.demskigroup.guitaramps.fcm_push_notification.NotificationUtils;
import com.demskigroup.guitaramps.pojo_class.discovery_people_pojo.DiscoverPeopleMainPojo;
import com.demskigroup.guitaramps.pojo_class.discovery_people_pojo.DiscoverPeopleResponse;
import com.demskigroup.guitaramps.utility.ApiUrl;
import com.demskigroup.guitaramps.utility.CommonClass;
import com.demskigroup.guitaramps.utility.OkHttp3Connection;
import com.demskigroup.guitaramps.utility.SessionManager;
import com.demskigroup.guitaramps.utility.VariableConstants;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * <h>DiscoverPeopleActivity</h>
 * <p>
 *     In this class we used to show the users and its posts.
 * </p>
 * @since 4/26/2017
 */
public class DiscoverPeopleActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG =DiscoverPeopleActivity.class.getSimpleName();
    private Activity mActivity;
    private SessionManager mSessionManager;
    private ProgressBar mProgressBar;
    private RelativeLayout rL_rootview;
    private RecyclerView rV_discover_people;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<DiscoverPeopleResponse> arrayListDiscoverData;
    private DiscoveryPeopleRvAdapter peopleRvAdapter;
    private LinearLayoutManager mLayoutManager;
    private int pageIndex;

    // Load more
    private int totalItemCount,totalVisibleItem,visibleTheshold=5;
    private boolean isLoadMoreNeeded;
    private NotificationMessageDialog mNotificationMessageDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_people);
        overridePendingTransition(R.anim.slide_up, R.anim.stay );
        pageIndex=0;
        mActivity=DiscoverPeopleActivity.this;
        mNotificationMessageDialog=new NotificationMessageDialog(mActivity);

        // receiving datas from last activty
        Intent intent=getIntent();
        int followingCount = intent.getIntExtra("followingCount", 0);
        System.out.println(TAG+" "+"followingCount="+ followingCount);

        // Initialize data member
        arrayListDiscoverData=new ArrayList<>();
        CommonClass.statusBarColor(mActivity);
        mSessionManager=new SessionManager(mActivity);
        mProgressBar= (ProgressBar) findViewById(R.id.progress_bar);
        rL_rootview= (RelativeLayout) findViewById(R.id.rL_rootview);

        RelativeLayout rL_back_btn= (RelativeLayout) findViewById(R.id.rL_back_btn);
        rL_back_btn.setOnClickListener(this);
        rV_discover_people= (RecyclerView) findViewById(R.id.rV_discover_people);
        mSwipeRefreshLayout= (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.pink_color);

        // adapter
        peopleRvAdapter=new DiscoveryPeopleRvAdapter(mActivity,arrayListDiscoverData, followingCount);
        mLayoutManager=new LinearLayoutManager(mActivity);
        rV_discover_people.setLayoutManager(mLayoutManager);
        rV_discover_people.setAdapter(peopleRvAdapter);

        // pull to refresh
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                arrayListDiscoverData.clear();
                pageIndex=0;
                discoverPeopleApi(pageIndex);
            }
        });

        // call api when open the screen
        if (CommonClass.isNetworkAvailable(mActivity))
        {
            mProgressBar.setVisibility(View.VISIBLE);
            discoverPeopleApi(pageIndex);
        }
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
     * <h>DiscoverPeopleApi</h>
     * <p>
     *     In this method we used to do api call to get list of registered users and there posts.
     * </p>
     * @param offset The page index
     */
    private void discoverPeopleApi(int offset)
    {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            int limit=20;
            offset=limit*offset;

            JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("token", mSessionManager.getAuthToken());
                request_datas.put("offset",offset);
                request_datas.put("limit",limit);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            System.out.println(TAG+" "+"offset="+offset);

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.DISCOVER_PEOPLE, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    mProgressBar.setVisibility(View.GONE);
                    System.out.println(TAG+" "+"discovery people res="+result);
                    mSwipeRefreshLayout.setRefreshing(false);

                    Gson gson=new Gson();
                    DiscoverPeopleMainPojo discoverPeoplePojo=gson.fromJson(result,DiscoverPeopleMainPojo.class);

                    switch (discoverPeoplePojo.getCode())
                    {
                        case "200" :
                            if (discoverPeoplePojo.getDiscoverData()!=null && discoverPeoplePojo.getDiscoverData().size()>0)
                            {
                                arrayListDiscoverData.addAll(discoverPeoplePojo.getDiscoverData());
                                isLoadMoreNeeded=arrayListDiscoverData.size()>15;
                                peopleRvAdapter.notifyDataSetChanged();

                                // set facebook and contact count
                                int contactCount=mSessionManager.getContectFriendCount();
                                if (contactCount>0)
                                {
                                    peopleRvAdapter.tV_contact_friend_count.setTextColor(ContextCompat.getColor(mActivity, R.color.pink_color));
                                    String setContactFrdCount;
                                    if (contactCount>1)
                                        setContactFrdCount=contactCount+" "+getResources().getString(R.string.friends);
                                    else setContactFrdCount=contactCount+" "+getResources().getString(R.string.friend);
                                    peopleRvAdapter.tV_contact_friend_count.setText(setContactFrdCount);
                                }

                                int fb_friend_count = mSessionManager.getFbFriendCount();
                                System.out.println(TAG+" "+"fb_friend_count="+fb_friend_count);
                                if (fb_friend_count>0)
                                {
                                    peopleRvAdapter.tV_fb_friends_count.setTextColor(ContextCompat.getColor(mActivity, R.color.pink_color));
                                    String setContactFrdCount;
                                    if (fb_friend_count>1)
                                        setContactFrdCount=fb_friend_count+" "+getResources().getString(R.string.friends);
                                    else setContactFrdCount=fb_friend_count+" "+getResources().getString(R.string.friend);
                                    peopleRvAdapter.tV_fb_friends_count.setText(setContactFrdCount);
                                }

                                // Load more
                                rV_discover_people.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                    @Override
                                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                        super.onScrolled(recyclerView, dx, dy);
                                        totalItemCount=mLayoutManager.getItemCount();
                                        totalVisibleItem=mLayoutManager.findLastVisibleItemPosition();

                                        System.out.println(TAG+" "+"isLoadMoreNeeded="+isLoadMoreNeeded+" "+"total item count="+totalItemCount+" "+"total visible="+totalVisibleItem+" "+"visible threshold="+visibleTheshold);

                                        if (isLoadMoreNeeded && totalItemCount<=(visibleTheshold+totalVisibleItem))
                                        {
                                            System.out.println(TAG+" "+"loading more...");
                                            isLoadMoreNeeded=false;
                                            pageIndex=pageIndex+1;
                                            mSwipeRefreshLayout.setRefreshing(true);
                                            discoverPeopleApi(pageIndex);
                                        }
                                    }
                                });
                            }
                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // error
                        default:
                            CommonClass.showSnackbarMessage(rL_rootview,discoverPeoplePojo.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    mSwipeRefreshLayout.setRefreshing(false);
                    mProgressBar.setVisibility(View.GONE);
                    CommonClass.showSnackbarMessage(rL_rootview,error);
                }
            });
        }
        else CommonClass.showSnackbarMessage(rL_rootview,getResources().getString(R.string.NoInternetAccess));
    }

    @Override
    public void onBackPressed()
    {
        System.out.println(TAG+" "+"followingCount back press="+peopleRvAdapter.followingCount);
        Intent intent=new Intent();
        intent.putExtra("followingCount",peopleRvAdapter.followingCount);
        setResult(VariableConstants.FOLLOW_COUNT_REQ_CODE,intent);
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data!=null) {
            int intFollowingCount;
            switch (requestCode) {
                // contact friends count
                case VariableConstants.CONTACT_FRIEND_REQ_CODE:
                    int contact_friend_count = data.getIntExtra("contact_count",0);
                    intFollowingCount=data.getIntExtra("followingCount",0);
                    peopleRvAdapter.followingCount=intFollowingCount;

                    System.out.println(TAG+" "+"contact_friend_count="+contact_friend_count+" "+"following count="+intFollowingCount);
                    if (contact_friend_count>0)
                    {
                        peopleRvAdapter.tV_contact_friend_count.setTextColor(ContextCompat.getColor(mActivity, R.color.pink_color));
                        String setContactFrdCount;
                        if (contact_friend_count>1)
                            setContactFrdCount=contact_friend_count+" "+getResources().getString(R.string.friends);
                        else setContactFrdCount=contact_friend_count+" "+getResources().getString(R.string.friend);
                        peopleRvAdapter.tV_contact_friend_count.setText(setContactFrdCount);
                    }
                    break;

                // facebook friends count
                case VariableConstants.FB_FRIEND_REQ_CODE:
                    int fb_friend_count = data.getIntExtra("fb_count",0);
                    intFollowingCount=data.getIntExtra("followingCount",0);
                    peopleRvAdapter.followingCount=intFollowingCount;
                    System.out.println(TAG+" "+"fb_friend_count="+fb_friend_count+" "+"following count="+intFollowingCount);
                    if (fb_friend_count>0)
                    {
                        peopleRvAdapter.tV_fb_friends_count.setTextColor(ContextCompat.getColor(mActivity, R.color.pink_color));
                        String setContactFrdCount;
                        if (fb_friend_count>1)
                            setContactFrdCount=fb_friend_count+" "+getResources().getString(R.string.friends);
                        else setContactFrdCount=fb_friend_count+" "+getResources().getString(R.string.friend);
                        peopleRvAdapter.tV_fb_friends_count.setText(setContactFrdCount);
                    }
                    break;
            }
        }
    }
}
