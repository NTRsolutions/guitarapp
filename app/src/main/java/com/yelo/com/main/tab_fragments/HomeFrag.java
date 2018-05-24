package com.yelo.com.main.tab_fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.otto.Subscribe;
import com.yelo.com.BuildConfig;
import com.yelo.com.R;
import com.yelo.com.adapter.ExploreRvAdapter;
import com.yelo.com.event_bus.BusProvider;
import com.yelo.com.fcm_push_notification.Config;
import com.yelo.com.get_current_location.FusedLocationReceiver;
import com.yelo.com.get_current_location.FusedLocationService;
import com.yelo.com.main.activity.AddPaymentActivity;
import com.yelo.com.main.activity.BuySellFragment;
import com.yelo.com.main.activity.Camera2Activity;
import com.yelo.com.main.activity.CameraActivity;
import com.yelo.com.main.activity.FilterActivity;
import com.yelo.com.main.activity.HomePageActivity;
import com.yelo.com.main.activity.LandingActivity;
import com.yelo.com.main.activity.NotificationActivity;
import com.yelo.com.main.activity.SearchAmpsActivity;
import com.yelo.com.main.activity.SearchProductActivity;
import com.yelo.com.main.activity.products.ProductDetailsActivity;
import com.yelo.com.pojo_class.LogDevicePojo;
import com.yelo.com.pojo_class.UnseenNotifiactionCountPojo;
import com.yelo.com.pojo_class.home_explore_pojo.ExploreLikedByUsersDatas;
import com.yelo.com.pojo_class.home_explore_pojo.ExplorePojoMain;
import com.yelo.com.pojo_class.home_explore_pojo.ExploreResponseDatas;
import com.yelo.com.pojo_class.product_category.ProductCategoryMainPojo;
import com.yelo.com.pojo_class.product_category.ProductCategoryResDatas;
import com.yelo.com.utility.ApiUrl;
import com.yelo.com.utility.CommonClass;
import com.yelo.com.utility.DialogBox;
import com.yelo.com.utility.HideShowScrollListener;
import com.yelo.com.utility.OkHttp3Connection;
import com.yelo.com.utility.ProductItemClickListener;
import com.yelo.com.utility.RunTimePermission;
import com.yelo.com.utility.SessionManager;
import com.yelo.com.utility.SpacesItemDecoration;
import com.yelo.com.utility.VariableConstants;
import com.yelo.com.utility.WrapContentLinearLayoutManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * <h>HomeFrag</h>
 * <p>
 *     This class is called from first tab of MainActivity class.
 *     In this class we used to show the users all posts.
 * </p>
 * @since 3/31/2017
 * @author 3Embed
 * @version 1.0
 */
public class HomeFrag extends Fragment implements View.OnClickListener, ProductItemClickListener
{
    private String category="",categoryValue="",distance="",postedWithin="",minPrice="",maxPrice="",currency="",currency_code="",
            currentLatitude="",currentLongitude="",sortByText="",sortBy="",postedWithinText="",address="";
    private SessionManager mSessionManager;
    private Activity mActivity;
    private static final String TAG=HomeFrag.class.getSimpleName();
    private ProgressBar mProgressBar;
    private ArrayList<ExploreResponseDatas> arrayListExploreDatas;
    private int index;
    private SwipeRefreshLayout mRefreshLayout;
    private boolean isFromSearch,isHomeFragVisible;
    private LinearLayout linear_filter;
    private ArrayList<String> arrayListFilter;
    private View view_filter_divider;
    private RelativeLayout rL_noProductFound,rL_no_internet,rL_action_bar,rL_sell_stuff;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager gridLayoutManager;
    private ArrayList<ProductCategoryResDatas> aL_categoryDatas;
    private String[] postedWithinArr,sortByArr;
    private int clickedItemPosition;
    private ExploreRvAdapter exploreRvAdapter;
    private FusedLocationService locationService;
    private String lat="",lng="";
    private String[] permissionsArray;
    private RunTimePermission runTimePermission;
    private TextView tV_notification_count;
    private BroadcastReceiver mBroadcastReceiver;
    private int notificationCount;
    private int fineLocResult,coarseLocResult;
    private boolean isFineLocDenied,isCoarseLocDenied;
    private HomePageActivity homePageActivity;

    // load more variables
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private HorizontalScrollView scrollViewFilter;
    private int recycleviewPaddingTop;
    static  HomeFrag homeFrag = null;

    public  static String KEY_FRAGMENT_FROM_CATGORY = "categoryname";

    String mcategoryName , make;
    AppCompatTextView mTvTitle;

    public static HomeFrag getInstance(){

        if(homeFrag == null){
            homeFrag = new HomeFrag();
        }
        return  homeFrag;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach( context );

        Log.e("TAG","onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity=getActivity();
        homePageActivity = (HomePageActivity) mActivity;
        setNotificationBroadCast();
        index=0;
        notificationCount=0;
        isFromSearch=false;// load more variables
        mSessionManager=new SessionManager(mActivity);
        arrayListExploreDatas=new ArrayList<>();
        aL_categoryDatas=new ArrayList<>();
        postedWithinArr=new String[]{getResources().getString(R.string.the_last_24hr),getResources().getString(R.string.the_last_7day),getResources().getString(R.string.the_last_30day),getResources().getString(R.string.all_producs)};
        sortByArr=new String[]{getResources().getString(R.string.newest_first),getResources().getString(R.string.closest_first),getResources().getString(R.string.price_high_to_low),getResources().getString(R.string.price_low_to_high)};

        permissionsArray = new String[]{ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION};
        runTimePermission = new RunTimePermission(mActivity, permissionsArray,false);

        fineLocResult = ContextCompat.checkSelfPermission(mActivity, ACCESS_FINE_LOCATION);
        coarseLocResult = ContextCompat.checkSelfPermission(mActivity,ACCESS_COARSE_LOCATION);


        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mcategoryName= bundle.getString(KEY_FRAGMENT_FROM_CATGORY);
        }

//        Log.i( "catgoryName==", mcategoryName );
        getCategoriesService();
        if (!mSessionManager.getIsUserLoggedIn())
            logGuestInfo();

        // to see notification count
        unseenNotificationCountApi();
    }

    private void setNotificationBroadCast()
    {
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION))
                {
                    // new push notification is received
                    String message = intent.getStringExtra("message");
                    String jsonMessageResponse=intent.getStringExtra("jsonMessage");
                    System.out.println(TAG+" "+"message="+message+" "+"jsonMessageResponse="+jsonMessageResponse+" "+"notificationCount="+notificationCount);
                    unseenNotificationCountApi();
                }
            }
        };
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        recycleviewPaddingTop=mRecyclerView.getPaddingTop();
        BusProvider.getInstance().register(this);
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(mBroadcastReceiver, new IntentFilter(Config.PUSH_NOTIFICATION));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BusProvider.getInstance().unregister(this);
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mBroadcastReceiver);
    }

    View view;
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {

        view = inflater.inflate(R.layout.frag_home, container, false);
        mProgressBar= (ProgressBar) view.findViewById(R.id.progress_bar_home);
        mTvTitle = view.findViewById( R.id.text_title );

        view_filter_divider=view.findViewById(R.id.view_filter_divider);
        view_filter_divider.setVisibility(View.GONE);
        rL_no_internet = (RelativeLayout) view.findViewById(R.id.rL_no_internet);
        rL_no_internet.setVisibility(View.GONE);

        rL_action_bar = (RelativeLayout) view.findViewById(R.id.rL_action_bar);
        scrollViewFilter = (HorizontalScrollView) view.findViewById(R.id.scrollViewFilter);

        tV_notification_count= (TextView) view.findViewById(R.id.tV_notification_count);
        tV_notification_count.setVisibility(View.GONE);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rV_home);
        gridLayoutManager = new StaggeredGridLayoutManager(2, 1);

//        gridLayoutManager = new WrapContentLinearLayoutManager(2,1);
        gridLayoutManager.setGapStrategy(0);

        mRecyclerView.setLayoutManager(gridLayoutManager);
//        mRecyclerView.setLayoutManager(gridLayoutManager);
        exploreRvAdapter=new ExploreRvAdapter(getActivity(),arrayListExploreDatas,this);
        mRecyclerView.setAdapter(exploreRvAdapter);

        linear_filter= (LinearLayout) view.findViewById(R.id.linear_filter);
        rL_noProductFound= (RelativeLayout) view.findViewById(R.id.rL_noProductFound);
        rL_noProductFound.setVisibility(View.GONE);

        // Set spacing the recycler view items
        int spanCount = 2; // 2 columns
        int spacing = 5; // 50px
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(spanCount, spacing));

        // Pull to refresh
        mRefreshLayout= (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh_layout);
        mRefreshLayout.setColorSchemeResources(R.color.pink_color);
        mRefreshLayout.setProgressViewOffset(false,100,200);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {

                try {
                    if (CommonClass.isNetworkAvailable(mActivity))
                    {
                        rL_no_internet.setVisibility(View.GONE);
                        arrayListExploreDatas.clear();
                        exploreRvAdapter.notifyDataSetChanged();
                        index=0;
                        if (isFromSearch)
                            searchProductsApi(index);
                        else
                        {
                            // check is user is logged in or not if its login then show according to location if not then show all posts.
                            if (mSessionManager.getIsUserLoggedIn())
                            {
                                System.out.println(TAG+" "+"lat in refreshing="+lat+" "+"lng="+lng);
                                if (isLocationFound(lat,lng))
                                    getUserPosts(index);
                                else getCurrentLocation();
                            }
                            else {
                                if (isLocationFound(lat,lng)) {
                                    getGuestPosts( index );
                                }else {
                                    getCurrentLocation();
                                }
                            }
                        }
                    }
                    else
                    {
                        rL_no_internet.setVisibility(View.VISIBLE);
                        mRefreshLayout.setRefreshing(false);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        });

        // Sell your stuff
        RelativeLayout rL_notification,rL_filter,rL_search;
        rL_sell_stuff= (RelativeLayout) view.findViewById(R.id.rL_sell_stuff);
        rL_sell_stuff.setOnClickListener(this);
        rL_notification  = (RelativeLayout) view.findViewById(R.id.rL_notification);
        rL_notification.setOnClickListener(this);
        rL_filter  = (RelativeLayout) view.findViewById(R.id.rL_filter);
        rL_filter.setOnClickListener(this);
        rL_search= (RelativeLayout) view.findViewById(R.id.rL_search);
        rL_search.setOnClickListener(this);

        System.out.println(TAG+" "+"frag visible home="+isHomeFragVisible);
        // Call all posted api
        if (CommonClass.isNetworkAvailable(mActivity))
        {
            rL_no_internet.setVisibility(View.GONE);
            index=0;
            arrayListExploreDatas.clear();
            mProgressBar.setVisibility(View.VISIBLE);

            // check is user logged in or not if its logged in then show according to location if not then show all posts.
            if (mSessionManager.getIsUserLoggedIn()) {
                if (runTimePermission.checkPermissions(permissionsArray)) {
                    getCurrentLocation();
                } else {
                    requestPermissions(permissionsArray, VariableConstants.PERMISSION_REQUEST_CODE);
                }
            }
            else
            {

                if (runTimePermission.checkPermissions(permissionsArray)) {
                    getGuestPosts(index);
                } else {
                    requestPermissions(permissionsArray, VariableConstants.PERMISSION_REQUEST_CODE);
                }

            }
        }
        else rL_no_internet.setVisibility(View.VISIBLE);


//        String upperString = mcategoryName.substring(0,1).toUpperCase() + mcategoryName.substring(1);

        StringBuffer res = new StringBuffer();
        String[] strArr = mcategoryName.split(" ");
        for (String str : strArr) {
            char[] stringArray = str.trim().toCharArray();
            stringArray[0] = Character.toUpperCase(stringArray[0]);
            str = new String(stringArray);

            res.append(str).append(" ");
        }



        mTvTitle.setText( res );

        return view;
    }



    /**
     * <h>UnseenNotificationCountApi</h>
     * <p>
     *     In this method we used to do api call to get total unseen notification count.
     * </p>
     */
    private void unseenNotificationCountApi()
    {

        if (CommonClass.isNetworkAvailable(mActivity) && mSessionManager.getIsUserLoggedIn()) {
            JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("token", mSessionManager.getAuthToken());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            final String unseenNotificationCountUrl=ApiUrl.UNSEEN_NOTIFICATION_COUNT+"?token="+mSessionManager.getAuthToken();

            OkHttp3Connection.doOkHttp3Connection(TAG, unseenNotificationCountUrl, OkHttp3Connection.Request_type.GET, new JSONObject(), new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    System.out.println(TAG+" "+"unseen notification count res="+result);

                    UnseenNotifiactionCountPojo unseenNotifiactionCountPojo;
                    Gson gson=new Gson();
                    unseenNotifiactionCountPojo=gson.fromJson(result,UnseenNotifiactionCountPojo.class);

                    switch (unseenNotifiactionCountPojo.getCode())
                    {
                        case "200" :
                            System.out.println(TAG+" "+"Notification count="+unseenNotifiactionCountPojo.getData());
                            notificationCount=unseenNotifiactionCountPojo.getData();

                            try {
                                if (notificationCount>0)
                                {


                                    tV_notification_count.setVisibility(View.VISIBLE);
                                    tV_notification_count.setText(String.valueOf(notificationCount));
                                }
                                else tV_notification_count.setVisibility(View.GONE);
                            }catch (Exception e){
                                e.printStackTrace();
                            }


                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {

                }
            });
        }
    }

    /**
     * <h>GetCategoriesService</h>
     * <p>
     * This method is called from onCreate() method of the current class.
     * In this method we used to call the getCategories api using okHttp3.
     * Once we get the data we show that list in recyclerview.
     * </p>
     */
    private void getCategoriesService() {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            String auth = "basicAuth: &jno-@8az=wSo*NHYVGpF^AQ?4yn36ZvW5ToUCUN+XGOuC?sz#SE$oxXVbwQGP|3WFyjcTAj2SIRQnLE|vo^-|-ATV5FZUf2*5A3Oiu|_EOMmG==&iApzQL3R7HHQj?jtb0mc2mT$Y%Isrgrxveld#Z^g3-ul^|0xAITganIuF23J0waSa6z6aP_+%De5LqtuY&ptx?qhZySECdyE^*4R^b*hFjQ-9?cCSJNfROzztEYbRyN=SqDyhhpzSmmP|Eb";



            byte[] data = new byte[0];
            try {
                data = auth.getBytes("UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            String base64 ="Basic "+ Base64.encodeToString(data, Base64.DEFAULT);


            JSONObject request_datas = new JSONObject();

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.GET_CATEGORIES, OkHttp3Connection.Request_type.GET, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    System.out.println(TAG+" "+"get category res="+result);
                    ProductCategoryMainPojo categoryMainPojo;
                    Gson gson = new Gson();
                    categoryMainPojo = gson.fromJson(result, ProductCategoryMainPojo.class);

                    switch (categoryMainPojo.getCode()) {
                        // success
                        case "200":
                            if (categoryMainPojo.getData() != null && categoryMainPojo.getData().size() > 0)
                            {
                                aL_categoryDatas.addAll(categoryMainPojo.getData());
                            }
                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                }
            });
        }
    }

    /**
     * In this method we find current location using FusedLocationApi.
     * in this we have onUpdateLocation() method in which we check if
     * its not null then We call guest user api.
     */
    private void getCurrentLocation()
    {
        locationService=new FusedLocationService(mActivity, new FusedLocationReceiver() {
            @Override
            public void onUpdateLocation() {
                Location currentLocation=locationService.receiveLocation();
                if (currentLocation!=null)
                {
                    lat=String.valueOf(currentLocation.getLatitude());
                    lng=String.valueOf(currentLocation.getLongitude());

                    System.out.println(TAG+" "+"lat="+lat+" "+"lng="+lng);

                    if (isLocationFound(lat,lng))
                    {
                        mSessionManager.setCurrentLat(lat);
                        mSessionManager.setCurrentLng(lng);
                        index=0;

                        if (mSessionManager.getIsUserLoggedIn()) {
                            getUserPosts( index );
                        }else {
                            getGuestPosts( index );
                        }
                    }
                }
            }
        }
        );
    }

    /**
     * <h>LogDeviceInfo</h>
     * <p>
     *     In this method we used to do api call to send device information like device name
     *     model number, device id etc to server to log the the user with specific device.
     * </p>
     */
    private void logGuestInfo()
    {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            if (rL_no_internet!=null)
                rL_no_internet.setVisibility(View.GONE);
            //deviceName, deviceId, deviceOs, modelNumber, appVersion
            final JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("deviceName", Build.BRAND);
                request_datas.put("deviceId", mSessionManager.getDeviceId());
                request_datas.put("deviceOs", Build.VERSION.RELEASE);
                request_datas.put("modelNumber", Build.MODEL);
                request_datas.put("appVersion", BuildConfig.VERSION_NAME);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.LOG_GUEST, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag)
                {
                    System.out.println(TAG+" "+"log guest res="+result);

                    LogDevicePojo logDevicePojo;
                    Gson gson=new Gson();
                    logDevicePojo=gson.fromJson(result,LogDevicePojo.class);

                    switch (logDevicePojo.getCode())
                    {
                        // success
                        case "200" :
                            // Open Home page screen
                            //startActivity(new Intent(mActivity,HomePageActivity.class));
                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // error
                        default:
                            CommonClass.showSnackbarMessage(((HomePageActivity)mActivity).rL_rootElement,logDevicePojo.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    CommonClass.showSnackbarMessage(((HomePageActivity)mActivity).rL_rootElement,error);
                }
            });
        }
        else {
            if (rL_no_internet!=null)
                rL_no_internet.setVisibility(View.VISIBLE);
        }
    }

    /**
     * <h>GetGuestPosts</h>
     * <p>
     *     In this method we used to call guest user api to get all posts.
     * </p>
     * @param offset The page index
     */
    private void getGuestPosts(int offset)
    {
        if (CommonClass.isNetworkAvailable(mActivity))
        {
            rL_no_internet.setVisibility(View.GONE);
            JSONObject requestDatas = new JSONObject();
            int limit=20;
            offset=limit*offset;

            try {
                requestDatas.put("offset", offset);
                requestDatas.put("limit",limit);
                requestDatas.put("latitude", lat);
                requestDatas.put("longitude",lng);

                requestDatas.put("category_name",mcategoryName);
                requestDatas.put( "make", make );
                requestDatas.put("pushToken",mSessionManager.getPushToken());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.i( "get guest posts", requestDatas.toString() );

            System.out.println(TAG+" "+"offset in guest post api="+offset+" "+"lat="+lat+" "+"lng="+lng);

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.GET_GUEST_ALL_POSTS, OkHttp3Connection.Request_type.POST, requestDatas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {

                    SearchAmpsActivity.make = null;
                    SearchAmpsActivity.searchCategory = null;
                    mProgressBar.setVisibility(View.GONE);
                    System.out.println(TAG+" "+"get explore guest res="+result);
                    if (result!=null && !result.isEmpty())
                        responseHandler(result);
                }

                @Override
                public void onError(String error, String user_tag) {
                    mProgressBar.setVisibility(View.GONE);
                    mRefreshLayout.setRefreshing(false);
                    CommonClass.showSnackbarMessage(((HomePageActivity)mActivity).rL_rootElement,error);
                }
            });
        }
        else {
            mRefreshLayout.setRefreshing(false);
            rL_no_internet.setVisibility(View.VISIBLE);
        }
    }

    /**
     * In this method we used to check whether current lat and
     * long has been received or not.
     * @param lat The current latitude
     * @param lng The current longitude
     * @return boolean flag true or false
     */
    private boolean isLocationFound(String lat,String lng) {
        return !(lat == null || lat.isEmpty()) && !(lng == null || lng.isEmpty());
    }

    /**
     * <h>GetUserPosts</h>
     * <p>
     *     In this method we used to do call getUserPosts api. And get all posts
     *     in response. Once we get all post then show that in recyclerview.
     * </p>
     * @param offset The pagination
     */
    private void getUserPosts(int offset) {
        if (CommonClass.isNetworkAvailable(mActivity))
        {
            rL_no_internet.setVisibility(View.GONE);
            JSONObject requestDatas = new JSONObject();
            int limit=20;
            offset=limit*offset;

            try {

                requestDatas.put("offset", offset);
                requestDatas.put("limit",limit);
                requestDatas.put("token", mSessionManager.getAuthToken());
                requestDatas.put("latitude",40.758018);
                requestDatas.put("longitude",-73.974976);
                requestDatas.put( "category_name", mcategoryName );
                requestDatas.put( "make", make );
                requestDatas.put("pushToken",mSessionManager.getPushToken());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.i( "get user posts", requestDatas.toString() );
            System.out.println(TAG+" "+"offset in user post api="+offset+" "+"lat="+lat+" "+"lng="+lng);

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.GET_USER_ALL_POSTS, OkHttp3Connection.Request_type.POST, requestDatas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    mProgressBar.setVisibility(View.GONE);

                    System.out.println(TAG+" "+"get explore res="+result);
                    SearchAmpsActivity.make = null;
                    SearchAmpsActivity.searchCategory = null;
                    /*if(arrayListExploreDatas!=null)
                        arrayListExploreDatas.clear();*/
                    if (result!=null && !result.isEmpty())
                        responseHandler(result);

                    //new DialogBox(mActivity).localCampaignDialog("shobhit","","","http://dev.yelo-app.xyz/public/defaultImg.png","title","message","");
                }

                @Override
                public void onError(String error, String user_tag) {
                    mProgressBar.setVisibility(View.GONE);
                    mRefreshLayout.setRefreshing(false);
                    CommonClass.showSnackbarMessage(((HomePageActivity)mActivity).rL_rootElement,error);
                }
            });
        }
        else{
            mRefreshLayout.setRefreshing(false);
            rL_no_internet.setVisibility(View.VISIBLE);
        }
    }

    /**
     * <h>ResponseHandler</h>
     * <p>
     *     This method is called from onSuccess of getUserPosts(). In this method
     *     we used to handle the above api response like if we get the error code
     *     200 then we do futher process or else show error message.
     * </p>
     * @param result The response of the allPosts api
     */
    private void responseHandler(String result)
    {
        ExplorePojoMain explorePojoMain;
        Gson gson=new Gson();
        explorePojoMain=gson.fromJson(result,ExplorePojoMain.class);

        switch (explorePojoMain.getCode())
        {
            // success
            case "200":
                mRefreshLayout.setRefreshing(false);
                arrayListExploreDatas.addAll(explorePojoMain.getData());
                if (arrayListExploreDatas!=null && arrayListExploreDatas.size()>0)
                {
                    isLoading = arrayListExploreDatas.size() < 15;
                    System.out.println(TAG+" "+"home page set isLoading="+isLoading);

                    rL_noProductFound.setVisibility(View.GONE);
                    exploreRvAdapter.notifyDataSetChanged();

                    // Load more
                    /*mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            int[] firstVisibleItemPositions = new int[2];
                            totalItemCount = gridLayoutManager.getItemCount();
                            lastVisibleItem = gridLayoutManager.findLastVisibleItemPositions(firstVisibleItemPositions)[0];

                            System.out.println(TAG+" "+"home activity totalItemCount="+totalItemCount+" "+"lastVisibleItem="+lastVisibleItem+" "+"visibleThreshold="+visibleThreshold+" "+"is load more="+isLoading);
                            if (lastVisibleItem==-1)
                                isLoading=true;

                            if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold))
                            {
                                System.out.println(TAG+" "+"home page load more...");
                                isLoading = true;
                                mRefreshLayout.setRefreshing(true);
                                index=index+1;
                                if (isFromSearch)
                                    searchProductsApi(index);
                                else
                                {
                                    // check is user is logged in or not if its login then show according to location if not then show all posts.
                                    if (mSessionManager.getIsUserLoggedIn())
                                        getUserPosts(index);
                                    else getGuestPosts(index);
                                }
                            }
                        }
                    });*/

                    mRecyclerView.addOnScrollListener(new HideShowScrollListener() {
                        @Override
                        public void onHide() {
                            hideViews();
                        }

                        @Override
                        public void onShow() {
                            showViews();
                        }

                        @Override
                        public void onScrolled() {


                            try {
                                mRefreshLayout.setRefreshing(false);
                                int[] firstVisibleItemPositions = new int[2];
                                totalItemCount = gridLayoutManager.getItemCount();

                                lastVisibleItem = gridLayoutManager.findLastVisibleItemPositions(firstVisibleItemPositions)[0];

                                System.out.println(TAG+" "+"home activity totalItemCount="+totalItemCount+" "+"lastVisibleItem="+lastVisibleItem+" "+"visibleThreshold="+visibleThreshold+" "+"is load more="+isLoading);
                                if (lastVisibleItem==-1)
                                    isLoading=true;

                                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold))
                                {
                                    System.out.println(TAG+" "+"home page load more...");
                                    isLoading = true;
                                    mRefreshLayout.setRefreshing(true);
                                    index=index+1;
                                    if (isFromSearch)
                                        searchProductsApi(index);
                                    else {
                                        // check is user is logged in or not if its login then show according to location if not then show all posts.
                                        if (mSessionManager.getIsUserLoggedIn())
                                            getUserPosts(index);
                                        else getGuestPosts(index);
                                    }
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }


                        }
                    });
                }
                break;

            // No more content
            case "204" :
                mRefreshLayout.setRefreshing(false);
                System.out.println(TAG+" "+"no more product="+explorePojoMain.getMessage());

//               if(arrayListExploreDatas != null){
//                   arrayListExploreDatas.clear();
//               }

//                rL_noProductFound.setVisibility(View.VISIBLE);

                if (arrayListExploreDatas.size()==0)
                {
                    rL_noProductFound.setVisibility(View.VISIBLE);
                }
                break;

            // auth token expired
            case "401" :
                mRefreshLayout.setRefreshing(false);
                CommonClass.sessionExpired(mActivity);
                break;

            // Error
            default:
                mRefreshLayout.setRefreshing(false);
                CommonClass.showSnackbarMessage(((HomePageActivity)mActivity).rL_rootElement,explorePojoMain.getMessage());
                break;
        }
    }

    private void hideViews() {
        scrollViewFilter.animate().translationY(-rL_action_bar.getHeight()).setInterpolator(new AccelerateInterpolator(2)).start();
        rL_action_bar.animate().translationY(-rL_action_bar.getHeight()).setInterpolator(new AccelerateInterpolator(2)).start();
        homePageActivity.bottomNavigationView.animate().translationY(homePageActivity.bottomNavigationView.getHeight()).setInterpolator(new AccelerateInterpolator(2)).start();
        rL_sell_stuff.animate().translationY(homePageActivity.bottomNavigationView.getHeight()).setInterpolator(new AccelerateInterpolator(2)).start();
    }

    private void showViews() {
        scrollViewFilter.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
        rL_action_bar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
        homePageActivity.bottomNavigationView.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
        rL_sell_stuff.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            // Open explore notification
            case R.id.rL_notification :
                if (mSessionManager.getIsUserLoggedIn())
                {
                    Intent intent = new Intent(mActivity, NotificationActivity.class);
                    startActivityForResult(intent,VariableConstants.IS_NOTIFICATION_SEEN_REQ_CODE);
                }
                else HomeFrag.this.startActivityForResult(new Intent(mActivity,LandingActivity.class), VariableConstants.LANDING_REQ_CODE);
                break;

            // Filter
            case R.id.rL_filter :
                Intent filterIntent=new Intent(mActivity, FilterActivity.class);
                filterIntent.putExtra("aL_categoryDatas",aL_categoryDatas);
                filterIntent.putExtra("address",address);
                filterIntent.putExtra("distance",distance);
                filterIntent.putExtra("sortBy",sortBy);
                filterIntent.putExtra("postedWithin",postedWithin);
                filterIntent.putExtra("currency_code",currency_code);
                filterIntent.putExtra("currency_symbol",currency);
                filterIntent.putExtra("minPrice",minPrice);
                filterIntent.putExtra("maxPrice",maxPrice);
                filterIntent.putExtra("userLat",currentLatitude);
                filterIntent.putExtra("userLng",currentLongitude);
                HomeFrag.this.startActivityForResult(filterIntent, VariableConstants.FILTER_REQUEST_CODE);
                break;

            // sell your stuff
            case R.id.rL_sell_stuff :
                if (mSessionManager.getIsUserLoggedIn())
                {
                    // startActivity(new Intent(mActivity, CameraActivity.class));

                    hitApiOnServerToCheckUserAccount();
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//                        startActivity(new Intent(mActivity, Camera2Activity.class));
//                    else
//                        startActivity(new Intent(mActivity, CameraActivity.class));

                }
                else HomeFrag.this.startActivityForResult(new Intent(mActivity,LandingActivity.class), VariableConstants.LANDING_REQ_CODE);
                break;

            // Search screen
            case R.id.rL_search :
//                startActivity(new Intent(mActivity, SearchProductActivity.class));

                startActivity(new Intent(mActivity, SearchAmpsActivity.class));
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        if(SearchAmpsActivity.make != null && SearchAmpsActivity.searchCategory != null){

            mcategoryName = SearchAmpsActivity.searchCategory;
            make     = SearchAmpsActivity.make;

            if(   arrayListExploreDatas != null){
                arrayListExploreDatas.clear();
            }



            if (mSessionManager.getIsUserLoggedIn())
            {
                System.out.println(TAG+" "+"lat in refreshing="+lat+" "+"lng="+lng);
                if (isLocationFound(lat,lng))
                    getUserPosts(index);
                else getCurrentLocation();
            }
            else {
                if (isLocationFound(lat,lng)) {
                    getGuestPosts( index );
                }else {
                    getCurrentLocation();
                }
            }
        }else {
            make = "";
        }

//        Toast.makeText( getActivity(), "onResume", Toast.LENGTH_SHORT ).show();
//        if(SearchAmpsActivity.numForCheckOnAdapter == 1){
//
//            if (mSessionManager.getIsUserLoggedIn())
//            {
//                System.out.println(TAG+" "+"lat in refreshing="+lat+" "+"lng="+lng);
//                if (isLocationFound(lat,lng))
//                    getUserPosts(index);
//                else getCurrentLocation();
//            }
//            else {
//                if (isLocationFound(lat,lng)) {
//                    getGuestPosts( index );
//                }else {
//                    getCurrentLocation();
//                }
//            }
//
//        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println(TAG+" "+"onactivity resultt "+"res code="+resultCode+" "+"req code="+requestCode+" "+"data="+data);
        super.onActivityResult(requestCode, resultCode, data);
        if (data!=null)
        {
            switch (requestCode)
            {
                // set for filter
                case VariableConstants.FILTER_REQUEST_CODE :
                    aL_categoryDatas.clear();
                    aL_categoryDatas.addAll((ArrayList<ProductCategoryResDatas>) data.getSerializableExtra("aL_categoryDatas"));
                    category=data.getStringExtra("category");
                    distance=data.getStringExtra("distance");
                    sortBy=data.getStringExtra("sortBy");
                    postedWithin=data.getStringExtra("postedWithin");
                    minPrice=data.getStringExtra("minPrice");
                    maxPrice=data.getStringExtra("maxPrice");
                    currency=data.getStringExtra("currency");
                    currency_code=data.getStringExtra("currency_code");
                    currentLatitude=data.getStringExtra("currentLatitude");
                    currentLongitude=data.getStringExtra("currentLongitude");
                    address=data.getStringExtra("address");
                    postedWithinText=data.getStringExtra("postedWithinText");
                    sortByText=data.getStringExtra("sortByText");
                    arrayListExploreDatas.clear();
                    exploreRvAdapter.notifyDataSetChanged();
                    isFromSearch=true;

                    setFilterDatasList();
                    break;

                // set for product details
                case  VariableConstants.PRODUCT_DETAILS_REQ_CODE :
                    String followRequestStatus=data.getStringExtra("followRequestStatus");
                    System.out.println(TAG+" "+"followRequestStatus="+followRequestStatus);
                    String likesCount = data.getStringExtra("likesCount");
                    String likeStatus =data.getStringExtra("likeStatus");
                    String clickCount =data.getStringExtra("clickCount");
                    ArrayList<ExploreLikedByUsersDatas> aL_likedByUsers = (ArrayList<ExploreLikedByUsersDatas>) data.getSerializableExtra("aL_likedByUsers");
                    if (followRequestStatus!=null && !followRequestStatus.isEmpty())
                    {
                        if (arrayListExploreDatas.size()>clickedItemPosition) {
                            arrayListExploreDatas.get(clickedItemPosition).setFollowRequestStatus(followRequestStatus);
                            arrayListExploreDatas.get(clickedItemPosition).setLikes(likesCount);
                            arrayListExploreDatas.get(clickedItemPosition).setLikeStatus(likeStatus);
                            arrayListExploreDatas.get(clickedItemPosition).setClickCount(clickCount);
                            arrayListExploreDatas.get(clickedItemPosition).setLikedByUsers(aL_likedByUsers);
                        }
                    }
                    break;

                // coming from Notification
                case VariableConstants.IS_NOTIFICATION_SEEN_REQ_CODE :
                    boolean isNotificationSeen = data.getBooleanExtra("isNotificationSeen",false);
                    if (isNotificationSeen)
                    {
                        notificationCount=0;
                        tV_notification_count.setVisibility(View.GONE);
                    }
                    break;

                // Location
                case VariableConstants.REQUEST_CHECK_SETTINGS :
                    switch (resultCode)
                    {
                        case Activity.RESULT_CANCELED :
                            index = 0;
                            getUserPosts(index);
                            break;
                    }
                    break;

                // call user post api
                case VariableConstants.LANDING_REQ_CODE :
                    boolean isToRefreshHomePage = data.getBooleanExtra("isToRefreshHomePage",false);
                    boolean isFromSignup = data.getBooleanExtra("isFromSignup",false);
                    System.out.println(TAG+" "+"isToRefreshHomePage="+isToRefreshHomePage+" "+"isFromSignup="+isFromSignup);
                    if (isToRefreshHomePage)
                    {
                        index=0;
                        arrayListExploreDatas.clear();
                        mProgressBar.setVisibility(View.VISIBLE);
                        exploreRvAdapter.notifyDataSetChanged();

                        if (runTimePermission.checkPermissions(permissionsArray)) {
                            getCurrentLocation();
                        } else {
                            requestPermissions(permissionsArray, VariableConstants.PERMISSION_REQUEST_CODE);
                        }

                        // open start browsering screen

                        if(isFromSignup){
                            Intent intent = new Intent(mActivity, BuySellFragment.class);

                            mActivity.startActivity(intent);
                        }


                        if (isFromSignup) {
                            new DialogBox( mActivity ).startBrowsingDialog();

                        }
                    }
                    break;
            }
        }
    }

    @Subscribe
    public void getMessage(ExploreResponseDatas setExploreResponseDatas)
    {
        if (setExploreResponseDatas!=null)
        {
            // add item
            if (!isContainsId(setExploreResponseDatas.getPostId())) {
                arrayListExploreDatas.add(0, setExploreResponseDatas);
                exploreRvAdapter.notifyDataSetChanged();
            }
            // remove item
            else
            {
                if (arrayListExploreDatas.size()>0 && setExploreResponseDatas.isToRemoveHomeItem())
                {
                    for (int homeItemCount=0; homeItemCount<arrayListExploreDatas.size();homeItemCount++)
                    {
                        if (setExploreResponseDatas.getPostId().equals(arrayListExploreDatas.get(homeItemCount).getPostId()))
                        {
                            arrayListExploreDatas.remove(clickedItemPosition);
                            exploreRvAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }

        if (arrayListExploreDatas.size()>0)
            rL_noProductFound.setVisibility(View.GONE);
        else rL_noProductFound.setVisibility(View.VISIBLE);
    }

    /**
     * <h>IsContainsId</h>
     * <p>
     *     In this method we used to check whether the given post id is
     *     present or not in the current list.
     * </p>
     * @param postId the given post id of product
     * @return the boolean value
     */
    public boolean isContainsId(String postId) {
        boolean flag = false;
        for (ExploreResponseDatas object : arrayListExploreDatas) {
            System.out.println(TAG+" "+"given post id="+postId+" "+"current post id="+object.getPostId());
            if (postId.equals(object.getPostId())) {
                flag = true;
            }
        }
        return flag;
    }

    // private void

    /**
     * <h>SetFilterDatasList</h>
     * <p>
     *     In this method we used to set the all filter datas like categories(Baby abd child, electronics etc),
     *     posted within(The last 24hr) etc into list.
     * </p>
     */
    private void setFilterDatasList()
    {
        int unSelectedCount=0;
        /////////////////////////////
        if (aL_categoryDatas!=null && aL_categoryDatas.size()>0)
        {
            category="";
            categoryValue="";
            for (ProductCategoryResDatas productCategoryResDatas : aL_categoryDatas)
            {
                if (productCategoryResDatas.isSelected())
                {
                    String name=productCategoryResDatas.getName();
                    category+="^"+name.substring(0,1).toUpperCase()+name.substring(1).toLowerCase();
                    categoryValue+=","+name;
                }
                else
                {
                    unSelectedCount+=1;
                }
            }

            if (unSelectedCount==aL_categoryDatas.size())
            {
                category="";
                categoryValue="";
            }
            System.out.println(TAG+" "+"unselected count="+unSelectedCount);
        }

        // remove comma
        if (!categoryValue.isEmpty())
            categoryValue=categoryValue.substring(1);

        System.out.println(TAG+" "+"categoryValue="+categoryValue);

        // remove first character
        if (!category.isEmpty())
            category=category.substring(1);

        System.out.println(TAG+" "+"selected category="+category);
        ////////////////////
        String[] category_arr;
        if (category!=null && !category.isEmpty())
        {
            category_arr=category.split("\\^");
        }
        else category_arr=new String[]{};

        // create empty arraylist
        arrayListFilter=new ArrayList<>();

        //set category
        Collections.addAll(arrayListFilter, category_arr);

        System.out.println(TAG+" "+"arrayListFilter size="+arrayListFilter.size());

        // add distance
        if (distance!=null && !distance.isEmpty() && !distance.equals("0"))
            arrayListFilter.add(distance+" "+getResources().getString(R.string.km));

        // add sorted by
        if (sortByText!=null && !sortByText.isEmpty())
            arrayListFilter.add(sortByText);

        // add posted within
        if (postedWithinText!=null && !postedWithinText.isEmpty())
            arrayListFilter.add(postedWithinText);

        if (minPrice!=null && !minPrice.isEmpty())
            arrayListFilter.add(getResources().getString(R.string.min_price)+" "+currency+minPrice);

        if (maxPrice!=null && !maxPrice.isEmpty())
            arrayListFilter.add(getResources().getString(R.string.max_price)+" "+currency+maxPrice);

        inflateFilterDatas();
    }

    /**
     * <h>InflateFilterDatas</h>
     * <p>
     *     In this method we used to inflate the the filtered data list like distance, price etc on the top
     *     of the screen.
     * </p>
     */
    private void inflateFilterDatas()
    {
        System.out.println(TAG+" "+"array list filter size="+arrayListFilter.size());
        if (arrayListFilter!=null && arrayListFilter.size()>0)
        {
            // view_filter_divider.setVisibility(View.VISIBLE);
            linear_filter.removeAllViews();
            LayoutInflater layoutInflater=LayoutInflater.from(mActivity);
            linear_filter.removeAllViews();
            for (int postCount=0;postCount<arrayListFilter.size();postCount++)
            {
                final View view=layoutInflater.inflate(R.layout.single_row_selected_filter_list,null,false);
                TextView tV_filter= (TextView) view.findViewById(R.id.tV_filter);
                tV_filter.setText(arrayListFilter.get(postCount));
                ImageView tV_delete= (ImageView) view.findViewById(R.id.tV_delete);
                final int finalPostCount = postCount;
                tV_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        if (arrayListFilter.size()>0)
                        {
                            String deletedValue=arrayListFilter.get(finalPostCount);
                            System.out.println(TAG+" "+"filter value="+deletedValue);

                            // remove category value
                            if (aL_categoryDatas.size()>0) {
                                for (int i = 0; i < aL_categoryDatas.size(); i++) {
                                    System.out.println(TAG + " get cate name=" + aL_categoryDatas.get(i).getName());
                                    if (aL_categoryDatas.get(i).getName().equalsIgnoreCase(deletedValue)) {
                                        System.out.println(TAG + "removed=" + aL_categoryDatas.get(i).getName());
                                        aL_categoryDatas.get(i).setSelected(false);
                                    }
                                }
                            }

                            // remove distance value
                            if (deletedValue.contains(getResources().getString(R.string.km)))
                                distance="";

                            // remove min price
                            if (deletedValue.contains(getResources().getString(R.string.min_price)))
                                minPrice="";

                            // remove max price
                            if (deletedValue.contains(getResources().getString(R.string.max_price)))
                                maxPrice="";

                            // remove posted within value
                            if (postedWithinArr.length>0)
                            {
                                for (String post : postedWithinArr)
                                {
                                    if (post.equalsIgnoreCase(deletedValue))
                                    {
                                        postedWithinText="";
                                        postedWithin="";
                                    }
                                }
                            }

                            // remove sort by value
                            if (sortByArr.length>0)
                            {
                                for (String sort : sortByArr)
                                {
                                    if (sort.equalsIgnoreCase(deletedValue))
                                    {
                                        sortBy="";
                                        sortByText="";
                                    }
                                }
                            }

                            arrayListFilter.remove(finalPostCount);
                            linear_filter.removeView(view);
                            setFilterDatasList();
                            //inflateFilterDatas();
                        }
                    }
                });
                linear_filter.addView(view);
            }
            mRecyclerView.setPadding(0,recycleviewPaddingTop+recycleviewPaddingTop,0,0);
        }
        else {
            linear_filter.removeAllViews();
            mRecyclerView.setPadding(0, recycleviewPaddingTop, 0, 0);
        }

        //call filter api
        arrayListExploreDatas.clear();
        exploreRvAdapter.notifyDataSetChanged();
        mRefreshLayout.setRefreshing(true);
        index=0;

        if (arrayListFilter.size()>0)
            searchProductsApi(index);
        else
        {
            isFromSearch = false;

            // check is user is logged in or not if its login then show according to location if not then show all posts.
            if (mSessionManager.getIsUserLoggedIn())
            {
                System.out.println(TAG+" "+"lat in refreshing="+lat+" "+"lng="+lng);
                if (isLocationFound(lat,lng))
                    getUserPosts(index);
                else getCurrentLocation();
            }
            else getGuestPosts(index);
        }
    }

    /**
     * <h>SearchProductsApi</h>
     * <p>
     *     In this method we do api call for searching product based on filtering
     *     like on category, min price, max price etc.
     * </p>
     */
    private void searchProductsApi(int offset) {
        if (CommonClass.isNetworkAvailable(mActivity))
        {
            rL_no_internet.setVisibility(View.GONE);
            int limit=20;
            offset=limit*offset;
            JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("currency",currency_code);
                request_datas.put("distance",distance);
                request_datas.put("latitude",currentLatitude);
                request_datas.put("limit",limit);
                request_datas.put("location",address);
                request_datas.put("longitude",currentLongitude);
                request_datas.put("maxPrice",maxPrice);
                request_datas.put("minPrice",minPrice);
                request_datas.put("offset",offset);
                request_datas.put("postedWithin",postedWithin);
                request_datas.put("searchKey",categoryValue);
                request_datas.put("sortBy",sortBy);
                request_datas.put("token",mSessionManager.getAuthToken());
                request_datas.put("pushToken",mSessionManager.getPushToken());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            System.out.println(TAG+" "+"offset in search api="+offset);

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.SEARCH_FILTER, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    mProgressBar.setVisibility(View.GONE);
                    System.out.println(TAG+" "+"product search res="+result);
                    if (result!=null && !result.isEmpty())
                        responseHandler(result);
                }

                @Override
                public void onError(String error, String user_tag) {
                    mProgressBar.setVisibility(View.GONE);
                    mRefreshLayout.setRefreshing(false);
                    CommonClass.showSnackbarMessage(((HomePageActivity)mActivity).rL_rootElement,error);
                }
            });
        }
        else {
            mRefreshLayout.setRefreshing(false);
            rL_no_internet.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isHomeFragVisible=isVisibleToUser;
    }

    @Override
    public void onItemClick(int pos, ImageView imageView)
    {
        System.out.println(TAG+" "+"pos="+pos+" "+"list size="+arrayListExploreDatas.size());
        if (arrayListExploreDatas.size()>pos) {
            clickedItemPosition = pos;
            Intent intent = new Intent(mActivity, ProductDetailsActivity.class);
            intent.putExtra("productName", arrayListExploreDatas.get(pos).getProductName());
            intent.putExtra("image", arrayListExploreDatas.get(pos).getMainUrl());
            if (arrayListExploreDatas.get(pos).getCategoryData() != null && arrayListExploreDatas.get(pos).getCategoryData().size() > 0)
                intent.putExtra("category", arrayListExploreDatas.get(pos).getCategoryData().get(0).getCategory());
            intent.putExtra("likes", arrayListExploreDatas.get(pos).getLikes());
            intent.putExtra("likeStatus", arrayListExploreDatas.get(pos).getLikeStatus());
            intent.putExtra("currency", arrayListExploreDatas.get(pos).getCurrency());
            intent.putExtra("price", arrayListExploreDatas.get(pos).getPrice());
            intent.putExtra("postedOn", arrayListExploreDatas.get(pos).getPostedOn());
            intent.putExtra("thumbnailImageUrl", arrayListExploreDatas.get(pos).getThumbnailImageUrl());
            intent.putExtra("likedByUsersArr", arrayListExploreDatas.get(pos).getLikedByUsers());
            intent.putExtra("description", arrayListExploreDatas.get(pos).getDescription());
            intent.putExtra("condition", arrayListExploreDatas.get(pos).getCondition());
            intent.putExtra("place", arrayListExploreDatas.get(pos).getPlace());
            intent.putExtra("latitude", arrayListExploreDatas.get(pos).getLatitude());
            intent.putExtra("longitude", arrayListExploreDatas.get(pos).getLongitude());
            intent.putExtra("postedByUserName", arrayListExploreDatas.get(pos).getPostedByUserName());
            intent.putExtra("postId", arrayListExploreDatas.get(pos).getPostId());
            intent.putExtra("postsType", arrayListExploreDatas.get(pos).getPostsType());
            intent.putExtra("followRequestStatus", arrayListExploreDatas.get(pos).getFollowRequestStatus());
            intent.putExtra("clickCount", arrayListExploreDatas.get(pos).getClickCount());
            intent.putExtra("negotiable", arrayListExploreDatas.get(pos).getNegotiable());
            intent.putExtra("memberProfilePicUrl", arrayListExploreDatas.get(pos).getMemberProfilePicUrl());
            intent.putExtra(VariableConstants.EXTRA_ANIMAL_IMAGE_TRANSITION_NAME, ViewCompat.getTransitionName(imageView));
            System.out.println(TAG + " " + "followRequestStatus=" + arrayListExploreDatas.get(pos).getFollowRequestStatus());
            HomeFrag.this.startActivityForResult(intent, VariableConstants.PRODUCT_DETAILS_REQ_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        System.out.println(TAG+" "+"on request permission result called...");
        switch (requestCode)
        {
            case VariableConstants.PERMISSION_REQUEST_CODE :
                System.out.println("grant result="+grantResults.length);
                if (grantResults.length>0)
                {
                    for (int count=0;count<grantResults.length;count++)
                    {
                        if (grantResults[count]!= PackageManager.PERMISSION_GRANTED)
                            allowPermissionAlert(permissions[count]);

                    }
                    System.out.println("isAllPermissionGranted="+runTimePermission.checkPermissions(permissionsArray));
                    if (runTimePermission.checkPermissions(permissionsArray))
                    {
                        getCurrentLocation();
                    }
                }
        }
    }

    /**
     * <h>ShowErrorMessage</h>
     * <p>
     *     In this method we used to show dialog for error message if
     *     the user denies any permissions.
     * </p>
     * @param permissionName the permission name
     */
    public void allowPermissionAlert(final String permissionName)
    {
        final Dialog errorMessageDialog = new Dialog(mActivity);
        errorMessageDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        errorMessageDialog.setContentView(R.layout.dialog_permission_denied);
        errorMessageDialog.getWindow().setGravity(Gravity.BOTTOM);
        errorMessageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        errorMessageDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        errorMessageDialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;

        TextView tV_permission_name = (TextView) errorMessageDialog.findViewById(R.id.tV_permission_name);
        String deniedPermission;
        if (permissionName.contains("CAMERA"))
        {
            deniedPermission = mActivity.getResources().getString(R.string.camera);
        }
        else if (permissionName.contains("WRITE_EXTERNAL_STORAGE"))
        {
            deniedPermission = mActivity.getResources().getString(R.string.external_storage);
        }
        else if (permissionName.contains("ACCESS_COARSE_LOCATION"))
        {
            deniedPermission = mActivity.getResources().getString(R.string.coarse_location);
        }
        else if (permissionName.contains("ACCESS_FINE_LOCATION"))
        {
            deniedPermission = mActivity.getResources().getString(R.string.fine_location);
        }
        else if (permissionName.contains("READ_CONTACTS"))
        {
            deniedPermission = mActivity.getResources().getString(R.string.read_contact);
        }
        else deniedPermission = permissionName;
        deniedPermission=  mActivity.getResources().getString(R.string.please_allow_the_permission) + " "+deniedPermission+" "+mActivity.getResources().getString(R.string.by_clicking_on_allow_button);
        tV_permission_name.setText(deniedPermission);

        // Cancel
        TextView tV_cancel= (TextView) errorMessageDialog.findViewById(R.id.tV_cancel);
        tV_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (permissionName.contains("ACCESS_COARSE_LOCATION") && coarseLocResult == PackageManager.PERMISSION_DENIED)
                {
                    System.out.println(TAG+" "+"location fine denied...");
                    isFineLocDenied=true;
                }

                if (permissionName.contains("ACCESS_FINE_LOCATION") && fineLocResult == PackageManager.PERMISSION_DENIED)
                {
                    System.out.println(TAG+" "+"location coarse denied...");
                    isCoarseLocDenied=true;
                }

                System.out.println(TAG+" "+"fineLocResult denied="+isFineLocDenied+" "+"coarseLocResult denied="+isCoarseLocDenied);
                if (isCoarseLocDenied && isFineLocDenied)
                {
                    index = 0;
                    lat=lng="";
                    arrayListExploreDatas.clear();
                    getUserPosts(index);
                }

                errorMessageDialog.dismiss();
            }
        });

        // allow
        RelativeLayout rL_allow= (RelativeLayout) errorMessageDialog.findViewById(R.id.rL_allow);
        rL_allow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
                {
                    if (mActivity.shouldShowRequestPermissionRationale(permissionName))
                        requestPermissions(permissionsArray, VariableConstants.PERMISSION_REQUEST_CODE);
                }
                errorMessageDialog.dismiss();
            }
        });
        errorMessageDialog.show();
    }

    private void hitApiOnServerToCheckUserAccount(){
        if (CommonClass.isNetworkAvailable(getActivity()))
        {
            final ProgressDialog pDialog = new ProgressDialog(getActivity(),0);


            pDialog.setCancelable(false);

//        pDialog.setTitle(R.string.string_351);

            pDialog.setMessage("Get categories");
            pDialog.show();
            JSONObject request_param=new JSONObject();
            try {
                request_param.put( "user_name", mSessionManager.getUserName() );
            } catch (JSONException e) {
                e.printStackTrace();
            }


            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.CHECK_USER_ACCOUNT_STATUS, OkHttp3Connection.Request_type.POST, request_param, new OkHttp3Connection.OkHttp3RequestCallback()
            {
                @Override
                public void onSuccess(String result, String user_tag) {


                    if(pDialog != null){
                        if (pDialog.isShowing()){
                            pDialog.dismiss();
                        }
                    }

                    ProductCategoryMainPojo categoryMainPojo;
                    Gson gson=new Gson();
                    categoryMainPojo=gson.fromJson(result,ProductCategoryMainPojo.class);

                    switch (categoryMainPojo.getCode())
                    {
                        // success i.e email is not registered
                        case "200" :
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                                startActivity(new Intent(getActivity(), Camera2Activity.class));
                            else
                                startActivity(new Intent(getActivity(), CameraActivity.class));

                            break;


                        case "204" :
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),
                                    R.style.CustomPopUpThemeBlue);
                            builder.setMessage("Please setup your payment details to receive payments, click ok to setup.");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    startActivity(new Intent(getActivity(), AddPaymentActivity.class));
                                }
                            });

                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.setCancelable(false);
                            builder.show();


                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(getActivity());
                            break;
                        // error like email is already registered
                        default:

                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    if(pDialog != null){
                        if (pDialog.isShowing()){
                            pDialog.dismiss();
                        }
                    }
                }
            });
        }
    }
}
