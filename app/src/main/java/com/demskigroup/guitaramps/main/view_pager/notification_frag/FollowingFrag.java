package com.demskigroup.guitaramps.main.view_pager.notification_frag;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.google.gson.Gson;
import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.adapter.FollowingFragRvAdap;
import com.demskigroup.guitaramps.pojo_class.explore_following_pojo.FollowingActivityMainPojo;
import com.demskigroup.guitaramps.pojo_class.explore_following_pojo.FollowingResponseDatas;
import com.demskigroup.guitaramps.utility.ApiUrl;
import com.demskigroup.guitaramps.utility.CommonClass;
import com.demskigroup.guitaramps.utility.OkHttp3Connection;
import com.demskigroup.guitaramps.utility.OnLoadMoreListener;
import com.demskigroup.guitaramps.utility.SessionManager;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * <h>FollowingFrag</h>
 * <p>
 * This class is called from ExploreNotificationActivity class. In this class
 * we show the user following notification message.
 * </p>
 * @since 4/15/2017
 * @version 1.0
 * @author 3Embed
 */
public class FollowingFrag extends Fragment
{
    private static final String TAG = FollowingFrag.class.getSimpleName();
    private SessionManager mSessionManager;
    private ProgressBar progress_bar_notification;
    private Activity mActivity;
    private FollowingFragRvAdap followingFragRvAdap;
    private ArrayList<FollowingResponseDatas> al_following_data;
    private int index;
    private SwipeRefreshLayout swipe_refresh_layout;
    private RelativeLayout rL_rootview;
    private LinearLayout linear_no_activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity=getActivity();
        mSessionManager=new SessionManager(mActivity);
        index=0;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.frag_following_notification,container,false);
        initVariables(view);
        return view;
    }

    /**
     * <h>initVariables</h>
     * <p>
     *     In this method we initialize the xml all variables.
     * </p>
     * @param view The parent view
     */
    private void initVariables(View view)
    {
        al_following_data=new ArrayList<>();
        rL_rootview= (RelativeLayout) view.findViewById(R.id.rL_rootview);
        progress_bar_notification= (ProgressBar) view.findViewById(R.id.progress_bar_notification);
        RecyclerView rV_notification = (RecyclerView) view.findViewById(R.id.rV_notification);
        LinearLayoutManager layoutManager=new LinearLayoutManager(mActivity);
        rV_notification.setLayoutManager(layoutManager);
        followingFragRvAdap=new FollowingFragRvAdap(mActivity,al_following_data,rV_notification);
        rV_notification.setAdapter(followingFragRvAdap);

        // if no activity found
        linear_no_activity= (LinearLayout) view.findViewById(R.id.linear_no_activity);
        linear_no_activity.setVisibility(View.GONE);
        ImageView iV_icon= (ImageView) view.findViewById(R.id.iV_icon);
        iV_icon.setImageResource(R.drawable.empty_following);

        // call api method
        if (CommonClass.isNetworkAvailable(mActivity)) {
            progress_bar_notification.setVisibility(View.VISIBLE);
            followingActivityApi(index);
        }

        // pull to refresh
        swipe_refresh_layout= (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipe_refresh_layout.setColorSchemeResources(R.color.pink_color);
        swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                al_following_data.clear();
                index=0;
                followingActivityApi(index);
            }
        });
    }

    /**
     * <h>FollowingActivityApi</h>
     * <p>
     *     In this method we do api followingActivity call to get users all
     *     following notifications.
     * </p>
     * @param offsetValue The page index
     */
    private void followingActivityApi(int offsetValue) {
        if (CommonClass.isNetworkAvailable(mActivity))
        {
            int limit=20;
            offsetValue=offsetValue*limit;
            JSONObject request_data = new JSONObject();
            try {
                request_data.put("token", mSessionManager.getAuthToken());
                request_data.put("offset",offsetValue);
                request_data.put("limit", limit);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            System.out.println(TAG+" "+"index in followingActivity="+offsetValue);

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.FOLLOWING_ACTIVITY, OkHttp3Connection.Request_type.POST, request_data, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    progress_bar_notification.setVisibility(View.GONE);
                    System.out.println(TAG+" "+"following Activity res="+result);

                    FollowingActivityMainPojo followingActivityPojo;
                    Gson gson=new Gson();
                    followingActivityPojo=gson.fromJson(result, FollowingActivityMainPojo.class);

                    switch (followingActivityPojo.getCode())
                    {
                        // success
                        case "200" :
                            swipe_refresh_layout.setRefreshing(false);
                            al_following_data.addAll(followingActivityPojo.getData());

                            if (al_following_data!=null && al_following_data.size()>0)
                            {
                                followingFragRvAdap.notifyDataSetChanged();
                                followingFragRvAdap.setLoaded();

                                followingFragRvAdap.setOnLoadMore(new OnLoadMoreListener() {
                                    @Override
                                    public void onLoadMore()
                                    {
                                        System.out.println(TAG+" "+"onLoadMore called");
                                        index=index+1;
                                        swipe_refresh_layout.setRefreshing(true);
                                        followingActivityApi(index);
                                    }
                                });
                            }
                            else linear_no_activity.setVisibility(View.VISIBLE);
                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // error
                        default:
                            swipe_refresh_layout.setRefreshing(false);
                            //CommonClass.showSnackbarMessage(rL_rootview,followingActivityPojo.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    progress_bar_notification.setVisibility(View.GONE);
                    CommonClass.showSnackbarMessage(rL_rootview,error);
                }
            });
        }
        else CommonClass.showSnackbarMessage(rL_rootview,getResources().getString(R.string.NoInternetAccess));
    }
}
