package com.demskigroup.guitaramps.main.view_pager.notification_frag;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.google.gson.Gson;
import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.adapter.YouFragRvAdapter;
import com.demskigroup.guitaramps.main.activity.Camera2Activity;
import com.demskigroup.guitaramps.main.activity.CameraActivity;
import com.demskigroup.guitaramps.main.activity.NotificationActivity;
import com.demskigroup.guitaramps.pojo_class.explore_for_you_pojo.ForYouMainPojo;
import com.demskigroup.guitaramps.pojo_class.explore_for_you_pojo.ForYouResposeDatas;
import com.demskigroup.guitaramps.utility.ApiUrl;
import com.demskigroup.guitaramps.utility.CommonClass;
import com.demskigroup.guitaramps.utility.OkHttp3Connection;
import com.demskigroup.guitaramps.utility.SessionManager;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * <h>YouFrag</h>
 * <p>
 *     This class is called from ExploreNotificationActivity class. In this
 *     Fragment class we used we used to show self notifications like abc
 *     started following you.
 * </p>
 * @since 4/15/2017
 * @author 3Embed
 * @version 1.0
 */
public class YouFrag extends Fragment implements View.OnClickListener {
    private static final String TAG = FollowingFrag.class.getSimpleName();
    private Activity mActivity;
    private SessionManager mSessionManager;
    private YouFragRvAdapter followingFragRvAdap;
    private ArrayList<ForYouResposeDatas> al_selfActivity_data;

    // xml variables
    private ProgressBar progress_bar_notification;
    private RecyclerView rV_notification;
    private SwipeRefreshLayout swipe_refresh_layout;
    private RelativeLayout rL_rootview;
    private LinearLayout linear_no_activity;

    // Load more variables
    private int index;
    private int totalVisibleItem,totalItemCount,visibleThresold=5;
    private boolean isLoaded=false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity=getActivity();
        mSessionManager=new SessionManager(mActivity);
        index=0;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.frag_for_you_notification,container,false);
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
        al_selfActivity_data=new ArrayList<>();
        rL_rootview= (RelativeLayout) view.findViewById(R.id.rL_rootview);
        RelativeLayout rL_start_selling = (RelativeLayout) view.findViewById(R.id.rL_start_selling);
        rL_start_selling.setOnClickListener(this);
        swipe_refresh_layout= (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipe_refresh_layout.setColorSchemeResources(R.color.pink_color);
        progress_bar_notification= (ProgressBar) view.findViewById(R.id.progress_bar_notification);
        rV_notification= (RecyclerView) view.findViewById(R.id.rV_notification);
        followingFragRvAdap=new YouFragRvAdapter(mActivity,al_selfActivity_data);
        LinearLayoutManager layoutManager=new LinearLayoutManager(mActivity);
        rV_notification.setLayoutManager(layoutManager);
        rV_notification.setAdapter(followingFragRvAdap);

        linear_no_activity= (LinearLayout) view.findViewById(R.id.linear_no_activity);
        linear_no_activity.setVisibility(View.GONE);

        // call self activity api
        if (CommonClass.isNetworkAvailable(mActivity)) {
            progress_bar_notification.setVisibility(View.VISIBLE);
            selfActivityApi(index);
        }

        // pull to refresh
        swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                index=0;
                al_selfActivity_data.clear();
                selfActivityApi(index);
            }
        });
    }

    /**
     * <h>SelfActivityApi</h>
     * <p>
     *     In this method we do api selfActivityApi call to get  all
     *     self notifications.
     * </p>
     * @param offsetValue The page index
     */
    private void selfActivityApi(int offsetValue) {
        if (CommonClass.isNetworkAvailable(mActivity))
        {
            int limit=20;
            offsetValue=offsetValue*limit;
            JSONObject request_data = new JSONObject();
            try {
                request_data.put("token", mSessionManager.getAuthToken());
                request_data.put("offset",offsetValue);
                request_data.put("limit",limit);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            System.out.println(TAG+" "+"index in self activity="+offsetValue);

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.SELF_ACTIVITY, OkHttp3Connection.Request_type.POST, request_data, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    progress_bar_notification.setVisibility(View.GONE);
                    System.out.println(TAG+" "+"self Activity res="+result);

                    ForYouMainPojo forYouPojo;
                    Gson gson=new Gson();
                    forYouPojo=gson.fromJson(result, ForYouMainPojo.class);

                    switch (forYouPojo.getCode())
                    {
                        // success
                        case "200" :
                            ((NotificationActivity)mActivity).isNotificationSeen=true;
                            swipe_refresh_layout.setRefreshing(false);
                            al_selfActivity_data.addAll(forYouPojo.getData());
                            if (al_selfActivity_data!=null && al_selfActivity_data.size()>0)
                            {
                                followingFragRvAdap.notifyDataSetChanged();
                                //isLoaded=false;

                                isLoaded=al_selfActivity_data.size()<15;

                                final LinearLayoutManager layoutManager= (LinearLayoutManager) rV_notification.getLayoutManager();
                                // Load more
                                rV_notification.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                    @Override
                                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                        super.onScrolled(recyclerView, dx, dy);

                                        totalItemCount=layoutManager.getItemCount();
                                        totalVisibleItem=layoutManager.findLastVisibleItemPosition();

                                        System.out.println(TAG+" "+"you frag totalItemCount="+totalItemCount+" "+"lastVisibleItem="+totalVisibleItem+" "+"visibleThreshold="+visibleThresold+" "+"is load more="+isLoaded);
                                        if (!isLoaded && totalItemCount<=(visibleThresold+totalVisibleItem))
                                        {
                                            System.out.println(TAG+" "+"on load more called");
                                            swipe_refresh_layout.setRefreshing(true);
                                            isLoaded=true;
                                            index=index+1;
                                            selfActivityApi(index);
                                        }
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
                            //CommonClass.showSnackbarMessage(rL_rootview,forYouPojo.getMessage());
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

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.rL_start_selling :
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    startActivity(new Intent(mActivity, Camera2Activity.class));
                else
                    startActivity(new Intent(mActivity, CameraActivity.class));
                break;
        }
    }
}
