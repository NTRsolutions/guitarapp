package com.yelo.com.main.activity.products;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.yelo.com.Face_book_manger.Facebook_login;
import com.yelo.com.Face_book_manger.Facebook_share_mamager;
import com.yelo.com.R;
import com.yelo.com.Uploader.ProductImageDatas;
import com.yelo.com.adapter.ProductDetailImagePagerAdapter;
import com.yelo.com.custom_scroll_view.AlphaForeGroundColorSpan;
import com.yelo.com.event_bus.EventBusDatasHandler;
import com.yelo.com.fcm_push_notification.Config;
import com.yelo.com.fcm_push_notification.NotificationMessageDialog;
import com.yelo.com.fcm_push_notification.NotificationUtils;
import com.yelo.com.get_current_location.FusedLocationReceiver;
import com.yelo.com.get_current_location.FusedLocationService;
import com.yelo.com.main.activity.EditProductActivity;
import com.yelo.com.main.activity.HomePageActivity;
import com.yelo.com.main.activity.LandingActivity;
import com.yelo.com.main.activity.MakeOfferActivity;
import com.yelo.com.main.activity.SelfProfileActivity;
import com.yelo.com.main.activity.UserLikesActivity;
import com.yelo.com.main.activity.UserProfileActivity;
import com.yelo.com.mqttchat.Activities.ChatMessageScreen;
import com.yelo.com.mqttchat.AppController;
import com.yelo.com.mqttchat.Utilities.Utilities;
import com.yelo.com.pojo_class.home_explore_pojo.ExploreLikedByUsersDatas;
import com.yelo.com.pojo_class.product_details_pojo.ProductDetailsMain;
import com.yelo.com.pojo_class.product_details_pojo.ProductResponseDatas;
import com.yelo.com.pull_to_zoom.PullToZoomScrollViewEx;
import com.yelo.com.swipe_to_finish.SwipeBackActivity;
import com.yelo.com.utility.ApiCall;
import com.yelo.com.utility.ApiUrl;
import com.yelo.com.utility.CircleTransform;
import com.yelo.com.utility.CommonClass;
import com.yelo.com.utility.DialogBox;
import com.yelo.com.utility.OkHttp3Connection;
import com.yelo.com.utility.RunTimePermission;
import com.yelo.com.utility.SessionManager;
import com.yelo.com.utility.VariableConstants;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Currency;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * <h>ProductDetailsActivity</h>
 * <p>
 *     In this class we used to show the product complete description like
 *     product name, image total likes apart from that we have option like
 *     share item add reviews etc.
 * </p>
 * @since 07-Jul-17
 * @version 1.0
 * @author 3Embed
 */
public class ProductDetailsActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = ProductDetailsActivity.class.getSimpleName();
    private Activity mActivity;
    private SessionManager mSessionManager;
    private ApiCall apiCall;
    public static  ProductDetailsActivity productDetailsActivity;
    private ArrayList<String> aL_multipleImages;
    private String fromChatScreen="0";
    private String receiverMqttId ="",likeStatus="",postId="",postsType="",productImage="",thumbnailImageUrl="",productName="",membername,followRequestStatus="",likesCount="",
            currency="",price="",postedOn="",description="",condition="",place="",latitude="",longitude="",cityName="",countryCode="",currentLat="",currentLng="",
            category="", containerWidth="",containerHeight="",image1 ="",image1thumbnail="", image2 ="", image2thumbnail="",image3 ="", image3thumbnail="",image4 ="",image4thumbnail="",memberProfilePicUrl="",clickCount="",negotiable="",
            cloudinaryPublicId="",cloudinaryPublicId1="",cloudinaryPublicId2="",cloudinaryPublicId3="",cloudinaryPublicId4="",getCityName="",getCountryName="";
    private int sold;
    private ArrayList<ExploreLikedByUsersDatas> aL_likedByUsers;
    private FusedLocationService locationService;
    private static View toolbar_shadow;
    private TextView tV_negotiable;
    private static ImageView iV_back_icon,iV_option_menu;
    private RelativeLayout rL_report_item;
    private ClipboardManager myClipboard;
    private ClipData myClip;
    private boolean isToMakeOffer;
    private String[] permissionsArray;
    private RunTimePermission runTimePermission;
    // Pull To Zoom and fading action bar var
    private PullToZoomScrollViewEx scrollView_itemDetails;
    public static RelativeLayout rL_actionBar,product_rootview;
    private static TextView tV_ProductName;
    private static AlphaForeGroundColorSpan mAlphaForegroundColorSpan;
    private static SpannableString mSpannableString;
    // Pull to zoom layout var
    ImageView iV_productImage;
    // product details content view layout var
    private LinearLayout linear_like_product;
    private RelativeLayout rL_follow;
    private TextView tV_productname,tV_category,tV_postedOn,tV_posted_by,tV_follow,tV_description,tV_condition,
            tV_location,tV_currency,tV_productprice,tV_like_count,tV_view_count,tV_makeoffer;
    private ImageView iV_soldby,like_item_icon,iV_followed_list,iv_staticMap;
    private LinearLayout linear_followed_images;
    private NotificationMessageDialog mNotificationMessageDialog;
    private boolean isToSellItAgain;
    private DialogBox mDialogBox;
    private ProductResponseDatas productResponse;
    private EventBusDatasHandler mEventBusDatasHandler;
    private ProgressBar mProgress_bar;
    //..viewPager with dots component
    private ViewPager vP_productImage;
    private int dotscount;
    private ImageView[] dots;
    LinearLayout sliderDotspanel;


    //..fb background sharing
    private Facebook_share_mamager facebook_share_mamager;
    private Facebook_login facebook_login;
    private CallbackManager callbackManager;
    private AdView mAdView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        facebook_login = new Facebook_login(this);
        callbackManager= CallbackManager.Factory.create();
        setContentView(R.layout.activity_product_details);
        facebook_share_mamager = Facebook_share_mamager.getInstance();
        initVariable();
    }

    /**
     * <h>InitVariable</h>
     * <p>
     *     In this method we used to initialize the xml variables.
     * </p>
     */
    private void initVariable() {
        mActivity = ProductDetailsActivity.this;
        mEventBusDatasHandler = new EventBusDatasHandler(mActivity);
        mDialogBox = new DialogBox(mActivity);
        //mDialogBox.showProgressDialog(getResources().getString(R.string.Loading));
        productDetailsActivity=this;
        permissionsArray = new String[]{ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION};
        runTimePermission = new RunTimePermission(mActivity, permissionsArray,false);
        isToMakeOffer=false;
        mNotificationMessageDialog=new NotificationMessageDialog(mActivity);
        myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        mSessionManager = new SessionManager(mActivity);
        apiCall = new ApiCall(mActivity);
        CommonClass.statusBarColor(mActivity);
        aL_multipleImages = new ArrayList<>();
        rL_actionBar = (RelativeLayout) findViewById(R.id.rL_actionBar);
        product_rootview = (RelativeLayout) findViewById(R.id.product_rootview);
        tV_ProductName = (TextView) findViewById(R.id.tV_ProductName);

        mAlphaForegroundColorSpan = new AlphaForeGroundColorSpan(ContextCompat.getColor(mActivity, R.color.purple_color));
        final ColorDrawable cd = new ColorDrawable(ContextCompat.getColor(this, R.color.white));
        rL_actionBar.setBackground(cd);
        cd.setAlpha(0);

        // toolbar bottom shadow
        toolbar_shadow = findViewById(R.id.toolbar_shadow);

        // scroll view
        scrollView_itemDetails = (PullToZoomScrollViewEx) findViewById(R.id.scrollView_itemDetails);

        // Back button
        RelativeLayout rL_back_btn = (RelativeLayout) findViewById(R.id.rL_back_btn);
        rL_back_btn.setOnClickListener(this);

        // report item
        rL_report_item = (RelativeLayout) findViewById(R.id.rL_report_item);
        rL_report_item.setOnClickListener(this);

        // back icon
        iV_back_icon= (ImageView) findViewById(R.id.iV_back_icon);

        // option menu icon
        iV_option_menu= (ImageView) findViewById(R.id.iV_option_menu);

        // make offer
        tV_makeoffer= (TextView)findViewById(R.id.tV_makeoffer);
        tV_makeoffer.setOnClickListener(this);

        // currency and price
        tV_currency= (TextView)findViewById(R.id.tV_currency);
        tV_productprice= (TextView)findViewById(R.id.tV_productprice);
        tV_negotiable= (TextView) findViewById(R.id.tV_negotiable);

        // chat
        RelativeLayout rL_chat_icon = (RelativeLayout) findViewById(R.id.rL_chat_icon);
        rL_chat_icon.setOnClickListener(this);
        CommonClass.setMargins(rL_chat_icon,0,(int)(CommonClass.getDeviceWidth(mActivity)/1.4),0,0);
        //retrieves the thumbnail data
        Intent intent=getIntent();
        productName=intent.getStringExtra("productName");
        category = intent.getStringExtra("category");
        likesCount=intent.getStringExtra("likes");
        likeStatus=intent.getStringExtra("likeStatus");
        productImage = intent.getStringExtra("image");
        thumbnailImageUrl = intent.getStringExtra("thumbnailImageUrl");
        fromChatScreen="0";
        if(intent.hasExtra("fromChatScreen"))
        {
            fromChatScreen=intent.getStringExtra("fromChatScreen");
        }else
        {
            fromChatScreen="0";
        }
        postId=intent.getStringExtra("postId");
        postsType=intent.getStringExtra("postsType");
        currency=intent.getStringExtra("currency");
        price=intent.getStringExtra("price");
        postedOn=intent.getStringExtra("postedOn");
        description=intent.getStringExtra("description");
        condition=intent.getStringExtra("condition");
        place=intent.getStringExtra("place");
        latitude=intent.getStringExtra("latitude");
        longitude=intent.getStringExtra("longitude");
        membername=intent.getStringExtra("postedByUserName");
        clickCount=intent.getStringExtra("clickCount");
        negotiable=intent.getStringExtra("negotiable");
        memberProfilePicUrl=intent.getStringExtra("memberProfilePicUrl");
        followRequestStatus=intent.getStringExtra("followRequestStatus");
        aL_likedByUsers= (ArrayList<ExploreLikedByUsersDatas>) intent.getSerializableExtra("likedByUsersArr");
        if (productName!=null && !productName.isEmpty())
            mSpannableString=new SpannableString(productName);
        else mSpannableString=new SpannableString("");

        // Load content layout
        loadViewForCode();

        // make offer
        tV_makeoffer= (TextView)findViewById(R.id.tV_makeoffer);
        tV_makeoffer.setOnClickListener(this);

        if (membername!=null && membername.equals(mSessionManager.getUserName()))
        {
            isToSellItAgain = false;
            tV_makeoffer.setText(getResources().getString(R.string.mark_as_sold));
            rL_chat_icon.setVisibility(View.GONE);
        }

        // set animation when user click on back button
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            String imageTransitionName = intent.getStringExtra(VariableConstants.EXTRA_ANIMAL_IMAGE_TRANSITION_NAME);
            iV_productImage.setTransitionName(imageTransitionName);
        }

        try {
            Glide.with(mActivity)
                    .load(productImage)
                    .asBitmap()
                    .placeholder(R.color.add_title)
                    .error(R.color.add_title)
                    .into(iV_productImage);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }

        // call this which initialize
        initializeResposeDatas();

        //Set the background color to white
        ColorDrawable colorDrawable = new ColorDrawable(Color.WHITE);
        product_rootview.setBackground(colorDrawable);

        latitude = mSessionManager.getCurrentLat();
        longitude = mSessionManager.getCurrentLng();
        if (isLocationFound(latitude,longitude))
        {
            if (mSessionManager.getIsUserLoggedIn())
                getProductDetailsService(ApiUrl.GET_POST_BY_ID_USER);
            else getProductDetailsService(ApiUrl.GET_POST_BY_ID_GUEST);
        }
        else {
            if (runTimePermission.checkPermissions(permissionsArray))
                getCurrentLocation();
            else runTimePermission.requestPermission();
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
     * <h>LoadViewForCode</h>
     * <p>
     *     In this method we used to set header(Product image view) and
     *     body i.e content view to the activity custom scroll view.
     * </p>
     */
    private void loadViewForCode() {
        final ViewGroup nullParent = null;
        View zoomView = LayoutInflater.from(this).inflate(R.layout.product_zoom_image_view, nullParent);
        View contentView = LayoutInflater.from(this).inflate(R.layout.product_details_content_view, nullParent);
        iV_productImage= (ImageView)zoomView.findViewById(R.id.iv_zoom);

        vP_productImage = (ViewPager)zoomView.findViewById(R.id.vp_product_image);
        sliderDotspanel = (LinearLayout)zoomView.findViewById(R.id.slider_dot);

        initializeContentVariables(contentView);
        scrollView_itemDetails.setHeaderViewSize(CommonClass.getDeviceWidth(mActivity),(int)(CommonClass.getDeviceWidth(mActivity)/1.25));

        scrollView_itemDetails.setZoomView(zoomView);
        scrollView_itemDetails.setScrollContentView(contentView);
    }

    /**
     * <h>InitializeContentVariables</h>
     * <p>
     *     In this method we used to initialize the content of the product details.
     * </p>
     * @param contentView the view of product details
     */
    private void initializeContentVariables(View contentView)
    {
        // Initialize xml Variables
        linear_like_product= (LinearLayout)contentView.findViewById(R.id.linear_like_product);
        linear_like_product.setOnClickListener(this);
        rL_follow = (RelativeLayout)contentView.findViewById(R.id.relative_follow);
        rL_follow.setOnClickListener(this);
        mProgress_bar= (ProgressBar) contentView.findViewById(R.id.progress_bar);
        mProgress_bar.setVisibility(View.VISIBLE);

        // sold by profile
        RelativeLayout rL_sold_by = (RelativeLayout)contentView.findViewById(R.id.rL_sold_by);
        rL_sold_by.setOnClickListener(this);

        // share item
        RelativeLayout rL_share= (RelativeLayout)contentView.findViewById(R.id.rL_share);
        rL_share.setOnClickListener(this);

        // add review
        RelativeLayout rL_addToReview= (RelativeLayout)contentView.findViewById(R.id.rL_addToReview);
        rL_addToReview.setOnClickListener(this);
        tV_productname= (TextView)contentView.findViewById(R.id.tV_productname);
        tV_category= (TextView) contentView.findViewById(R.id.tV_category);
        tV_postedOn= (TextView) contentView.findViewById(R.id.tV_postedOn);
        tV_like_count= (TextView) contentView.findViewById(R.id.tV_like_count);
        tV_view_count= (TextView) contentView.findViewById(R.id.tV_view_count);
        linear_followed_images= (LinearLayout) contentView.findViewById(R.id.linear_followed_images);
        tV_posted_by= (TextView) contentView.findViewById(R.id.tV_posted_by);
        tV_follow= (TextView) contentView.findViewById(R.id.tV_follow);
        tV_description= (TextView) contentView.findViewById(R.id.tV_description);
        tV_condition= (TextView) contentView.findViewById(R.id.tV_condition);
        tV_location= (TextView) contentView.findViewById(R.id.tV_location);
        iV_soldby= (ImageView) contentView.findViewById(R.id.iV_soldby);
        iV_soldby.getLayoutParams().width= CommonClass.getDeviceWidth(mActivity)/9;
        iV_soldby.getLayoutParams().height=CommonClass.getDeviceWidth(mActivity)/9;
        like_item_icon= (ImageView) contentView.findViewById(R.id.like_item_icon);
        iV_followed_list= (ImageView) contentView.findViewById(R.id.iV_followed_list);
        iv_staticMap= (ImageView) contentView.findViewById(R.id.iV_static_map);
    }


    /**
     * In this method we find current location using FusedLocationApi.
     * in this we have onUpdateLocation() method in which we check if
     * its not null then We call guest user api.
     */
    private void getCurrentLocation()
    {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            locationService = new FusedLocationService(mActivity, new FusedLocationReceiver() {
                @Override
                public void onUpdateLocation() {
                    Location currentLocation = locationService.receiveLocation();
                    System.out.println(TAG+" "+"currentLocation="+currentLocation);
                    if (currentLocation != null)
                    {
                        currentLat = String.valueOf(currentLocation.getLatitude());
                        currentLng = String.valueOf(currentLocation.getLongitude());

                        if (isLocationFound(currentLat, currentLng)) {
                            System.out.println(TAG+" "+"currentLat="+currentLat+" "+"currentLng="+currentLng);
                            mSessionManager.setCurrentLat(currentLat);
                            mSessionManager.setCurrentLng(currentLng);

                            cityName=CommonClass.getCityName(mActivity,currentLocation.getLatitude(),currentLocation.getLongitude());
                            countryCode=CommonClass.getCountryCode(mActivity,currentLocation.getLatitude(),currentLocation.getLongitude());

                            System.out.println(TAG+" "+"post id="+postId);

                            // call product details api call method
                            if (mSessionManager.getIsUserLoggedIn())
                                getProductDetailsService(ApiUrl.GET_POST_BY_ID_USER);
                            else getProductDetailsService(ApiUrl.GET_POST_BY_ID_GUEST);
                        }
                    }
                }
            }
            );
        }
        else CommonClass.showSnackbarMessage(product_rootview,getResources().getString(R.string.NoInternetAccess));
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
     * <h>GetProductDetailsService</h>
     * <p>
     *     In this method we do api call to get product complete information
     *     like name, image, description etc.
     * </p>
     */
    private void getProductDetailsService(String url)
    {
        if (CommonClass.isNetworkAvailable(mActivity))
        {
            // token, postId, latitude, longitude, city, countrySname
            JSONObject requestDats = new JSONObject();
            try {
                requestDats.put("token", mSessionManager.getAuthToken());
                requestDats.put("postId", postId);
                requestDats.put("latitude", currentLat);
                requestDats.put("longitude", currentLng);
                requestDats.put("city",cityName);
                requestDats.put("countrySname",countryCode);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, url, OkHttp3Connection.Request_type.POST, requestDats, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag)
                {
                    System.out.println(TAG+" "+"product details res="+result);
                    if (mDialogBox.progressBarDialog!=null)
                        mDialogBox.progressBarDialog.dismiss();
                    mProgress_bar.setVisibility(View.GONE);

                    ProductDetailsMain productDetailsMain;
                    Gson gson=new Gson();
                    productDetailsMain=gson.fromJson(result,ProductDetailsMain.class);

                    switch (productDetailsMain.getCode())
                    {
                        // success
                        case "200" :
                            productResponse=productDetailsMain.getData().get(0);
                            isToMakeOffer=true;
                            receiverMqttId =productResponse.getMemberMqttId();



                            tV_makeoffer.setBackgroundColor(ContextCompat.getColor(mActivity,R.color.status_bar_color));
                            productName=productResponse.getProductName();
                            productImage=productResponse.getMainUrl();
                            thumbnailImageUrl=productResponse.getThumbnailImageUrl();
                            likesCount=productResponse.getLikes();
                            category=productResponse.getCategoryData().get(0).getCategory();
                            postedOn=productResponse.getPostedOn();
                            likesCount=productResponse.getLikes();
                            clickCount=productResponse.getClickCount();
                            likeStatus=productResponse.getLikeStatus();
                            followRequestStatus=productResponse.getFollowRequestStatus();
                            membername=productResponse.getMembername();
                            memberProfilePicUrl=productResponse.getMemberProfilePicUrl();
                            description=productResponse.getDescription();
                            condition=productResponse.getCondition();
                            place=productResponse.getPlace();
                            price=productResponse.getPrice();
                            negotiable=productResponse.getNegotiable();
                            currency=productResponse.getCurrency();
                            latitude=productResponse.getLatitude();
                            longitude=productResponse.getLongitude();
                            image1 =productResponse.getImageUrl1();
                            image1thumbnail=productResponse.getThumbnailUrl1();
                            image2 =productResponse.getImageUrl2();
                            image2thumbnail = productResponse.getThumbnailUrl2();
                            image3 =productResponse.getImageUrl3();
                            image3thumbnail =productResponse.getThumbnailUrl3();
                            image4 =productResponse.getImageUrl4();
                            image4thumbnail= productResponse.getThumbnailUrl4();
                            containerWidth = productResponse.getContainerWidth();
                            containerHeight = productResponse.getContainerHeight();
                            cloudinaryPublicId = productResponse.getCloudinaryPublicId();
                            cloudinaryPublicId1 = productResponse.getCloudinaryPublicId1();
                            cloudinaryPublicId2 = productResponse.getCloudinaryPublicId2();
                            cloudinaryPublicId3 = productResponse.getCloudinaryPublicId3();
                            cloudinaryPublicId4 = productResponse.getCloudinaryPublicId4();
                            sold=productResponse.getSold();
                            getCityName=productResponse.getCity();


                            // To show liked By Users
                            aL_likedByUsers=productResponse.getLikedByUsers();

                            initializeResposeDatas();
                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // error
                        default:
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag)
                {
                    mProgress_bar.setVisibility(View.GONE);
                    if (mDialogBox.progressBarDialog!=null)
                        mDialogBox.progressBarDialog.dismiss();
                    CommonClass.showSnackbarMessage(product_rootview,error);
                }
            });
        }
        else
        {
            if (mDialogBox.progressBarDialog!=null)
                mDialogBox.progressBarDialog.dismiss();
            CommonClass.showSnackbarMessage(product_rootview,getResources().getString(R.string.NoInternetAccess));
        }
    }

    private void initializeResposeDatas()
    {
        aL_multipleImages.clear();
        // set multiple image
        if (productImage!=null && !productImage.isEmpty())
            aL_multipleImages.add(productImage);

        if (image1 !=null && !image1.isEmpty())
            aL_multipleImages.add(image1);

        if (image2 !=null && !image2.isEmpty())
            aL_multipleImages.add(image2);

        if (image3 !=null && !image3.isEmpty())
            aL_multipleImages.add(image3);

        if (image4 !=null && !image4.isEmpty())
            aL_multipleImages.add(image4);

        viewPagerSetup();


        iV_productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mActivity, ProductImagesActivity.class);
                intent.putExtra("imagesArrayList",aL_multipleImages);
                startActivity(intent);
            }
        });

        // product name
        if (productName!=null && !productName.isEmpty())
        {
            productName=productName.substring(0,1).toUpperCase()+productName.substring(1).toLowerCase();
            tV_productname.setText(productName);
        }

        try {
            Glide.with(mActivity)
                    .load(productImage)
                    .asBitmap()
                    .placeholder(R.color.add_title)
                    .error(R.color.add_title)
                    .into(iV_productImage);
        } catch (OutOfMemoryError | IllegalArgumentException e) {
            e.printStackTrace();
        }

        // location is only city and country
        if(getCityName!=null && !getCityName.isEmpty()){
            getCityName=getCityName.substring(0,1).toUpperCase()+""+getCityName.substring(1);
            getCountryName=CommonClass.getCountryName(mActivity,Double.parseDouble(latitude),Double.parseDouble(longitude));
            place=getCityName+", "+getCountryName;
            tV_location.setText(place);
        }

        // category
        if (category!=null&& !category.isEmpty())
        {
            //category=category.substring(0,1).toUpperCase()+category.substring(1).toLowerCase();
            tV_category.setText(capitalizeString(category));
        }

        // posted on
        if (postedOn!=null)
            tV_postedOn.setText(CommonClass.getTimeDifference(postedOn));

        // view count
        if (clickCount!=null)
            tV_view_count.setText(clickCount);

        // like count
        if (likesCount!=null)
            tV_like_count.setText(likesCount);

        System.out.println(TAG+" "+"like status="+likeStatus);
        // set like status
        if (likeStatus!=null && likeStatus.equals("1"))
        {
            like_item_icon.setImageResource(R.drawable.like_icon_on);
            tV_like_count.setTextColor(ContextCompat.getColor(mActivity,R.color.pink_color));
            linear_like_product.setBackgroundResource(R.drawable.rect_pink_color_with_stroke_shape);
        }
        else
        {
            like_item_icon.setImageResource(R.drawable.like_icon_off);
            tV_like_count.setTextColor(ContextCompat.getColor(mActivity,R.color.hide_button_border_color));
            linear_like_product.setBackgroundResource(R.drawable.rect_gray_color_with_with_stroke_shape);
        }

        // show total user likes horizontally
        if (isToMakeOffer)
            inflateUserLikes();

        // to see followed list
        iV_followed_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSessionManager.getIsUserLoggedIn()) {
                    Intent intent = new Intent(mActivity, UserLikesActivity.class);
                    intent.putExtra("postId", postId);
                    intent.putExtra("postType", postsType);
                    mActivity.startActivity(intent);
                }
                else startActivityForResult(new Intent(mActivity,LandingActivity.class), VariableConstants.LANDING_REQ_CODE);
            }
        });

        // sold by name
        tV_posted_by.setText(membername);

        // posted by pic
        if (memberProfilePicUrl!=null && !memberProfilePicUrl.isEmpty())
            Picasso.with(mActivity)
                    .load(memberProfilePicUrl)
                    .transform(new CircleTransform())
                    .placeholder(R.drawable.app_launcher_logo)
                    .error(R.drawable.app_launcher_logo)
                    .into(iV_soldby);

        // hide the follow option for the user who posted
        if (membername!=null && membername.equals(mSessionManager.getUserName()))
            rL_follow.setVisibility(View.GONE);
        else rL_follow.setVisibility(View.VISIBLE);

        // hide report icon for own post
        if (membername!=null && membername.equals(mSessionManager.getUserName()))
            rL_report_item.setVisibility(View.GONE);

        // Check follow status
        if (followRequestStatus!=null)
        {
            if (followRequestStatus.equals("1"))
            {
                rL_follow.setBackgroundResource(R.drawable.rect_purple_color_with_solid_shape);
                tV_follow.setTextColor(ContextCompat.getColor(mActivity,R.color.white));
                tV_follow.setText(mActivity.getResources().getString(R.string.Following));
            }
            else
            {
                rL_follow.setBackgroundResource(R.drawable.rect_purple_color_with_stroke_shape);
                tV_follow.setTextColor(ContextCompat.getColor(mActivity,R.color.purple_color));
                tV_follow.setText(mActivity.getResources().getString(R.string.follow));
            }
        }

        // description
        if (description!=null)
            tV_description.setText(description);

        // condition
        if (condition!=null)
            tV_condition.setText(condition);

        // api call to get static map location of product
        apiCall.staticMapApi(iv_staticMap,latitude,longitude);

        // open map location
        iv_staticMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //latitude,longitude
                Intent intent=new Intent(mActivity,ProductsMapActivity.class);
                intent.putExtra("place",place);
                intent.putExtra("latitude",latitude);
                intent.putExtra("longitude",longitude);
                startActivity(intent);
            }
        });

        /*// location
        if (place!=null)
            tV_location.setText(place);*/

        // set currency
        if (currency!=null && !currency.isEmpty())
        {
            //..from locale..//
            /*Currency c  = Currency.getInstance(currency);
            currency=c.getSymbol();
            tV_currency.setText(currency);*/

            //..from array..//
            String[] arrayCurrency=getResources().getStringArray(R.array.currency_picker);

            if (arrayCurrency.length>0)
            {
                String[] getCurrencyArr;
                for (String setCurrency : arrayCurrency) {
                    getCurrencyArr = setCurrency.split(",");
                    String currency_code=getCurrencyArr[1];
                    String currency_symbol=getCurrencyArr[2];

                    System.out.println(TAG+" "+"given currency="+currency+" "+"my currency="+currency_code);
                    if (currency.equals(currency_code))
                    {
                        tV_currency.setText(currency_symbol);
                    }
                }
            }

        }

        // price
        if (price!=null)
            //price=CommonClass.twoDigitAfterDewcimal(price);
            tV_productprice.setText(price);

        // set whether price is negotiable
        if (negotiable!=null && !negotiable.equals("1"))
            tV_negotiable.setText(getResources().getString(R.string.not_negotiable));
        else tV_negotiable.setText(getResources().getString(R.string.negotiable));

        //...if product already sold then show sold at make offer text
        if(sold==2 && mSessionManager.getIsUserLoggedIn()){
            if(membername.equalsIgnoreCase(mSessionManager.getUserName())){
                isToSellItAgain=true;
                tV_makeoffer.setText(getString(R.string.sell_it_again));
            }else {
                isToMakeOffer = false;
                tV_makeoffer.setText(getResources().getString(R.string.sold));
            }
        }

        // Make offer
        tV_makeoffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isToMakeOffer) {
                    if (membername != null && membername.equals(mSessionManager.getUserName()))
                    {
                        if (isToSellItAgain)
                        {
                            sellItAgainDialog();
                        }
                        else
                            openEditProductScreen();
                    }
                    else
                    {
                        if (mSessionManager.getIsUserLoggedIn()) {
                            Intent intent = new Intent(mActivity, MakeOfferActivity.class);
                            intent.putExtra("productPicUrl",productImage);
                            intent.putExtra("productName", productName);
                            intent.putExtra("place", place);
                            intent.putExtra("latitude", latitude);
                            intent.putExtra("longitude", longitude);
                            intent.putExtra("currency", tV_currency.getText().toString());
                            intent.putExtra("price", tV_productprice.getText().toString());
                            intent.putExtra("membername",membername);
                            intent.putExtra("postId",postId);
                            intent.putExtra("memberPicUrl",memberProfilePicUrl);
                            intent.putExtra("receiverMqttId",receiverMqttId);
                            intent.putExtra("negotiable",negotiable);
                            intent.putExtra("fromChatScreen",fromChatScreen);
                            startActivity(intent);
                        }
                        else startActivityForResult(new Intent(mActivity,LandingActivity.class), VariableConstants.LANDING_REQ_CODE);
                    }
                }
            }
        });
    }

    private void viewPagerSetup() {

        ProductDetailImagePagerAdapter vp_image_adapter = new ProductDetailImagePagerAdapter(mActivity, aL_multipleImages);
        vP_productImage.setAdapter(vp_image_adapter);
        dotscount = vp_image_adapter.getCount();
        dots = new ImageView[dotscount];

        sliderDotspanel.removeAllViews();

        for(int i = 0; i < dotscount; i++){

            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            sliderDotspanel.addView(dots[i], params);
        }
        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));



        vP_productImage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for(int i = 0; i< dotscount; i++){
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * In this method we used to open Edit product screen & pass the all required values through bundle
     */
    private void openEditProductScreen()
    {
        Intent intent = new Intent(mActivity, EditProductActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("postId", postId);
        bundle.putString("productImage", productImage);
        bundle.putString("productName", productName);
        bundle.putString("category", category);
        bundle.putString("description", description);
        bundle.putString("condition", condition);
        bundle.putString("price", price);
        bundle.putString("negotiable", negotiable);
        bundle.putString("place", place);
        bundle.putString("latitude", latitude);
        bundle.putString("longitude", longitude);
        bundle.putString("currency", currency);
        ArrayList<ProductImageDatas> aLProductImageDatases = new ArrayList<>();

        // first image
        String mainUrl = productImage;
        if (mainUrl != null && !mainUrl.isEmpty()) {
            ProductImageDatas productImageDatas1 = new ProductImageDatas();
            productImageDatas1.setMainUrl(productImage);
            productImageDatas1.setThumbnailUrl(thumbnailImageUrl);
            productImageDatas1.setPublic_id(cloudinaryPublicId);

            // set width
            if (containerWidth != null && !containerWidth.isEmpty())
                productImageDatas1.setWidth(Integer.parseInt(containerWidth));

            // set height
            String height = containerHeight;
            if (height != null && !height.isEmpty())
                productImageDatas1.setHeight(Integer.parseInt(height));

            productImageDatas1.setImageUrl(true);
            aLProductImageDatases.add(productImageDatas1);
        }

        // second image
        if (image1 != null && !image1.isEmpty()) {
            ProductImageDatas productImageDatas2 = new ProductImageDatas();
            productImageDatas2.setMainUrl(image1);
            productImageDatas2.setThumbnailUrl(image1thumbnail);
            productImageDatas2.setImageUrl(true);
            productImageDatas2.setPublic_id(cloudinaryPublicId1);
            aLProductImageDatases.add(productImageDatas2);
        }

        // Third Image
        if (image2 != null && !image2.isEmpty()) {
            ProductImageDatas productImageDatas3 = new ProductImageDatas();
            productImageDatas3.setMainUrl(image2);
            productImageDatas3.setThumbnailUrl(image2thumbnail);
            productImageDatas3.setPublic_id(cloudinaryPublicId2);
            productImageDatas3.setImageUrl(true);
            aLProductImageDatases.add(productImageDatas3);
        }

        // Fourth Image
        if (image3 != null && !image3.isEmpty()) {
            ProductImageDatas productImageDatas4 = new ProductImageDatas();
            productImageDatas4.setMainUrl(image3);
            productImageDatas4.setThumbnailUrl(image3thumbnail);
            productImageDatas4.setImageUrl(true);
            productImageDatas4.setPublic_id(cloudinaryPublicId3);
            aLProductImageDatases.add(productImageDatas4);
        }

        // Fifth Image
        if (image4 != null && !image4.isEmpty()) {
            ProductImageDatas productImageDatas5 = new ProductImageDatas();
            productImageDatas5.setMainUrl(image4);
            productImageDatas5.setThumbnailUrl(image4thumbnail);
            productImageDatas5.setPublic_id(cloudinaryPublicId4);
            productImageDatas5.setImageUrl(true);
            aLProductImageDatases.add(productImageDatas5);
        }

        bundle.putSerializable("imageDatas", aLProductImageDatases);
        intent.putExtras(bundle);
        startActivityForResult(intent, VariableConstants.SELLING_REQ_CODE);
    }

    /**
     * <h>CapitalizeString</h>
     * <p>
     *     In this method we used to capitalize the initial character of
     *     each word in a given sentence.
     * </p>
     * @param string The given line consisting several words
     * @return The Sentence with initial words in uppercase.
     */
    private String capitalizeString(String string) {
        char[] chars = string.toLowerCase().toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i]) || chars[i]=='.' || chars[i]=='\'') { // You can add other chars here
                found = false;
            }
        }
        return String.valueOf(chars);
    }

    /**
     * <p>
     *     Here we set alpha value for action bar title(Product name)
     * </p>
     * @param alpha set value
     */
    public static void setTitleAlpha(float alpha)
    {
        if(alpha<1) {
            alpha = 1;
        }

        if (alpha==1)
        {
            toolbar_shadow.setVisibility(View.VISIBLE);
            iV_back_icon.setImageResource(R.drawable.back_arrow_icon);
            iV_option_menu.setImageResource(R.drawable.option_menu_icon);
        }
        else{
            toolbar_shadow.setVisibility(View.GONE);
            iV_back_icon.setImageResource(R.drawable.white_color_back_button_with_shadow);
            iV_option_menu.setImageResource(R.drawable.white_option_menu_icon_with_shadow);
        }

        System.out.println(TAG+" "+"alpha value="+alpha);
        mAlphaForegroundColorSpan.setAlpha(alpha);
        mSpannableString.setSpan(mAlphaForegroundColorSpan, 0, mSpannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tV_ProductName.setText(mSpannableString);
    }

    /**
     * <h>InflateUserLikes</h>
     * <p>
     *     In this method we used to inflate the user likes list into
     *     LinearLayout horizontally.
     * </p>
     */
    private void inflateUserLikes()
    {
        int mLikeCount=0;
        if (likesCount!=null && !likesCount.isEmpty())
            mLikeCount=Integer.parseInt(likesCount);

        if (mLikeCount>0)
        {
            if (aL_likedByUsers!=null && aL_likedByUsers.size()>0)
            {
                linear_followed_images.removeAllViews();
                for (int likedCount=0;likedCount<aL_likedByUsers.size();likedCount++)
                {
                    LayoutInflater layoutInflater= (LayoutInflater) mActivity.getSystemService(LAYOUT_INFLATER_SERVICE);
                    View followedView=layoutInflater.inflate(R.layout.single_row_images,null);
                    ImageView viewPagerItem_image = (ImageView)followedView.findViewById(R.id.iV_image);
                    viewPagerItem_image.setBackgroundColor(ContextCompat.getColor(mActivity,R.color.white));
                    viewPagerItem_image.getLayoutParams().width=CommonClass.getDeviceWidth(mActivity)/10;
                    viewPagerItem_image.getLayoutParams().height=CommonClass.getDeviceWidth(mActivity)/10;
                    viewPagerItem_image.setImageResource(R.drawable.app_launcher_logo);

                    String likedUserImg=aL_likedByUsers.get(likedCount).getProfilePicUrl();
                    //viewPagerItem_image.setX(getResources().getDimension(R.dimen.dim));
                    if (likedUserImg != null && !likedUserImg.isEmpty()) {
                        Picasso.with(mActivity)
                                .load(likedUserImg)
                                .placeholder(R.drawable.app_launcher_logo)
                                .error(R.drawable.app_launcher_logo)
                                .transform(new CircleTransform())
                                .into(viewPagerItem_image);
                    }

                    // view user profile
                    final int finalLikedCount = likedCount;
                    viewPagerItem_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String currentUserName=aL_likedByUsers.get(finalLikedCount).getLikedByUsers();
                            if (currentUserName!=null && !currentUserName.isEmpty())
                            {
                               /* Intent intent=new Intent(mActivity, UserProfileActivity.class);
                                intent.putExtra("membername",currentUserName);
                                startActivityForResult(intent,VariableConstants.USER_FOLLOW_REQ_CODE);*/

                                Intent intent=null;
                                if ( mSessionManager.getUserName().equals(currentUserName))
                                {
                                    if (mSessionManager.getIsUserLoggedIn())
                                    {
                                        intent = new Intent(mActivity, SelfProfileActivity.class);
                                        intent.putExtra("membername", currentUserName);
                                    }
                                    else startActivityForResult(new Intent(mActivity,LandingActivity.class), VariableConstants.LANDING_REQ_CODE);
                                }
                                else
                                {
                                    intent = new Intent(mActivity, UserProfileActivity.class);
                                    intent.putExtra("membername",currentUserName);
                                }
                                if (intent!=null)
                                    mActivity.startActivityForResult(intent,VariableConstants.USER_FOLLOW_REQ_CODE);
                            }
                        }
                    });
                    linear_followed_images.addView(followedView);
                }
            }
        }
        else linear_followed_images.removeAllViews();
    }

    @Override
    public void onClick(View v)
    {
        Intent intent;
        switch (v.getId())
        {
            // Back button
            case R.id.rL_back_btn :
                intent=new Intent();
                intent.putExtra("likesCount",likesCount);
                intent.putExtra("likeStatus",likeStatus);
                intent.putExtra("followRequestStatus",followRequestStatus);
                intent.putExtra("clickCount",clickCount);
                intent.putExtra("aL_likedByUsers",aL_likedByUsers);
                intent.putExtra("isToSellItAgain",isToSellItAgain);
                setResult(VariableConstants.PRODUCT_DETAILS_REQ_CODE,intent);
                onBackPressed();
                break;

            // like or unlike
            case R.id.linear_like_product :
                if (CommonClass.isNetworkAvailable(mActivity)) {
                    if (mSessionManager.getIsUserLoggedIn()) {
                        int mLikeCount = 0;
                        if (likesCount != null && !likesCount.isEmpty())
                            mLikeCount = Integer.parseInt(likesCount);

                        // unlike
                        if (likeStatus != null && likeStatus.equals("1")) {
                            // remove my self
                            if (aL_likedByUsers.size() > 0) {
                                for (int likeCount = 0; likeCount < aL_likedByUsers.size(); likeCount++) {
                                    if (aL_likedByUsers.get(likeCount).getLikedByUsers().equals(mSessionManager.getUserName())) {
                                        aL_likedByUsers.remove(likeCount);
                                    }
                                }
                            }

                            mLikeCount -= 1;
                            likesCount = mLikeCount + "";
                            inflateUserLikes();

                            tV_like_count.setText(String.valueOf(mLikeCount));
                            likeStatus = "0";
                            like_item_icon.setImageResource(R.drawable.like_icon_off);
                            tV_like_count.setTextColor(ContextCompat.getColor(mActivity, R.color.hide_button_border_color));
                            linear_like_product.setBackgroundResource(R.drawable.rect_gray_color_with_with_stroke_shape);
                            mEventBusDatasHandler.setFavDatasFromProductDetails(productResponse,false);
                            apiCall.likeProductApi(ApiUrl.UNLIKE_PRODUCT, postId);
                        }

                        // like
                        else
                        {
                            // add myself
                            ExploreLikedByUsersDatas likedByUsersDatas = new ExploreLikedByUsersDatas();
                            likedByUsersDatas.setLikedByUsers(mSessionManager.getUserName());
                            likedByUsersDatas.setProfilePicUrl(mSessionManager.getUserImage());
                            aL_likedByUsers.add(0, likedByUsersDatas);
                            mLikeCount += 1;
                            likesCount = mLikeCount + "";
                            inflateUserLikes();

                            tV_like_count.setText(String.valueOf(mLikeCount));
                            likeStatus = "1";
                            like_item_icon.setImageResource(R.drawable.like_icon_on);
                            tV_like_count.setTextColor(ContextCompat.getColor(mActivity, R.color.pink_color));
                            linear_like_product.setBackgroundResource(R.drawable.rect_pink_color_with_stroke_shape);
                            mEventBusDatasHandler.setFavDatasFromProductDetails(productResponse,true);
                            apiCall.likeProductApi(ApiUrl.LIKE_PRODUCT, postId);
                        }

                        System.out.println(TAG + " " + "mLike count=" + mLikeCount);
                    }
                    else startActivityForResult(new Intent(mActivity,LandingActivity.class), VariableConstants.LANDING_REQ_CODE);
                }
                else CommonClass.showSnackbarMessage(product_rootview,getResources().getString(R.string.NoInternetAccess));
                break;

            // follow or unfollow
            case R.id.relative_follow:
                if (mSessionManager.getIsUserLoggedIn()) {
                    if (CommonClass.isNetworkAvailable(mActivity)) {
                        String url;
                        if (followRequestStatus != null && followRequestStatus.equals("1")) {
                            url = ApiUrl.UNFOLLOW + membername;
                            unfollowUserAlert(url);
                        } else {
                            url = ApiUrl.FOLLOW + membername;
                            apiCall.followUserApi(url);
                            rL_follow.setBackgroundResource(R.drawable.rect_purple_color_with_solid_shape);
                            tV_follow.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
                            tV_follow.setText(mActivity.getResources().getString(R.string.Following));
                            followRequestStatus = "1";
                            mEventBusDatasHandler.setSocialDatasFromProductDetails(productResponse,true);
                        }
                    } else {
                        CommonClass.showSnackbarMessage(product_rootview, getResources().getString(R.string.NoInternetAccess));
                    }
                }
                else startActivityForResult(new Intent(mActivity,LandingActivity.class), VariableConstants.LANDING_REQ_CODE);
                break;

            // Sold by profile
            case R.id.rL_sold_by :
                intent=null;
                if ( mSessionManager.getUserName().equals(membername))
                {
                    if (mSessionManager.getIsUserLoggedIn())
                    {
                        intent = new Intent(mActivity, SelfProfileActivity.class);
                        intent.putExtra("membername", membername);
                    }
                    else startActivityForResult(new Intent(mActivity,LandingActivity.class), VariableConstants.LANDING_REQ_CODE);
                }
                else
                {
                    intent = new Intent(mActivity, UserProfileActivity.class);
                    intent.putExtra("membername",membername);
                }
                if (intent!=null)
                    mActivity.startActivity(intent);
                break;

            // share
            case R.id.rL_share :
                if(mSessionManager.getIsUserLoggedIn()) {
                    openShareOptionDialog();
                }else{
                    startActivityForResult(new Intent(mActivity,LandingActivity.class), VariableConstants.LANDING_REQ_CODE);
                }
                break;

            // add review
            case R.id.rL_addToReview :
                if (mSessionManager.getIsUserLoggedIn()) {
                    intent = new Intent(mActivity, ProductReviewActivity.class);
                    intent.putExtra("postId", postId);
                    startActivity(intent);
                }
                else startActivityForResult(new Intent(mActivity,LandingActivity.class), VariableConstants.LANDING_REQ_CODE);
                break;

            // Report an item
            case R.id.rL_report_item :
                if (mSessionManager.getIsUserLoggedIn()) {
                    intent = new Intent(mActivity, ReportProductActivity.class);
                    intent.putExtra("postId", postId);
                    intent.putExtra("product_image", productImage);
                    intent.putExtra("product_name", productName);
                    intent.putExtra("sold_by_name", membername);
                    startActivity(intent);
                }
                else startActivityForResult(new Intent(mActivity,LandingActivity.class), VariableConstants.LANDING_REQ_CODE);
                break;

            case R.id.rL_chat_icon :


                try {
                    if (isToMakeOffer ) {
                        if (mSessionManager.getIsUserLoggedIn()) {
                            initiateChat();
                        } else startActivityForResult(new Intent(mActivity,LandingActivity.class), VariableConstants.LANDING_REQ_CODE);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent();
        intent.putExtra("likesCount",likesCount);
        intent.putExtra("likeStatus",likeStatus);
        intent.putExtra("followRequestStatus",followRequestStatus);
        intent.putExtra("clickCount",clickCount);
        intent.putExtra("aL_likedByUsers",aL_likedByUsers);
        intent.putExtra("isToSellItAgain",isToSellItAgain);
        setResult(VariableConstants.PRODUCT_DETAILS_REQ_CODE,intent);
        super.onBackPressed();
    }

    /**
     * <h>SellItAgainDialog</h>
     * <p>
     *     In this method we used to open a dialog to alert the user to sell the item again.
     * </p>
     */
    public void sellItAgainDialog()
    {
        final Dialog errorMessageDialog = new Dialog(mActivity);
        errorMessageDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        errorMessageDialog.setContentView(R.layout.dialog_sell_it_again);
        errorMessageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        errorMessageDialog.getWindow().setLayout((int)(CommonClass.getDeviceWidth(mActivity)*0.8), RelativeLayout.LayoutParams.WRAP_CONTENT);

        // dismiss
        TextView tV_no= (TextView) errorMessageDialog.findViewById(R.id.tV_no);
        tV_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorMessageDialog.dismiss();
            }
        });

        // yes
        TextView tV_yes= (TextView) errorMessageDialog.findViewById(R.id.tV_yes);
        tV_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonClass.isNetworkAvailable(mActivity))
                {
                    isToSellItAgain = false;
                    tV_makeoffer.setText(getResources().getString(R.string.mark_as_sold));
                    apiCall.markSellingApi(product_rootview,postId);
                    mEventBusDatasHandler.addSellingDatasFromProductDetails(productResponse);
                    mEventBusDatasHandler.addHomePageDatasFromProductDetails(productResponse);
                    mEventBusDatasHandler.setSocialDatasFromProductDetails(productResponse);
                    errorMessageDialog.dismiss();
                }
                else
                {
                    CommonClass.showSnackbarMessage(((HomePageActivity)mActivity).rL_rootElement,getResources().getString(R.string.NoInternetAccess));
                }
            }
        });
        errorMessageDialog.show();
    }

    /**
     * <h>openShareOptionDialog</h>
     * <p>
     *     In this method we used to open a dialog to show option like sharing or copy item url.
     * </p>
     */
    private void openShareOptionDialog()
    {
        final Dialog shareDialog=new Dialog(mActivity);
        shareDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        shareDialog.setContentView(R.layout.dialog_share_option);
        shareDialog.getWindow().setGravity(Gravity.BOTTOM);
        shareDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        shareDialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
        shareDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);

        // Share on facebook
        RelativeLayout rL_share_on_fb= (RelativeLayout) shareDialog.findViewById(R.id.rL_share_on_fb);
        rL_share_on_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareDialog.dismiss();

                /*ShareDialog shareDialog;
                FacebookSdk.sdkInitialize(mActivity);
                shareDialog = new ShareDialog(mActivity);
                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentTitle(productName)
                        .setContentDescription(description)
                        .setImageUrl(Uri.parse(productImage))
                        .setContentUrl(Uri.parse(getResources().getString(R.string.share_item_base_url)+postId)).build();
                shareDialog.show(linkContent);*/
                checkForPermission();
            }
        });

        // copy product url
        RelativeLayout rL_copy_url= (RelativeLayout) shareDialog.findViewById(R.id.rL_copy_url);
        rL_copy_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareDialog.dismiss();
                myClip = ClipData.newPlainText("text", getResources().getString(R.string.share_item_base_url)+postId);
                myClipboard.setPrimaryClip(myClip);
                CommonClass.showShortSuccessMsg(product_rootview,getResources().getString(R.string.url_copied));
            }
        });

        // cancel
        TextView cancel_button= (TextView)shareDialog.findViewById(R.id.cancel_button);
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareDialog.dismiss();
            }
        });
        shareDialog.show();
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
        if (memberProfilePicUrl!=null && !memberProfilePicUrl.isEmpty())
            Picasso.with(mActivity)
                    .load(memberProfilePicUrl)
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
                apiCall.followUserApi(url);
                rL_follow.setBackgroundResource(R.drawable.rect_purple_color_with_stroke_shape);
                tV_follow.setTextColor(ContextCompat.getColor(mActivity,R.color.purple_color));
                tV_follow.setText(mActivity.getResources().getString(R.string.follow));
                followRequestStatus="0";
                mEventBusDatasHandler.setSocialDatasFromProductDetails(productResponse,false);
                unfollowUserDialog.dismiss();
            }
        });
        unfollowUserDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
        if (data!=null){
            System.out.println(TAG+" "+"onactivity result "+"res code="+resultCode+" "+"req code="+requestCode+" "+"data="+data);
            switch (requestCode)
            {
                // User Follow req code
                case VariableConstants.USER_FOLLOW_REQ_CODE :
                    followRequestStatus=data.getStringExtra("followStatus");
                    if (followRequestStatus.equals("1"))
                    {
                        rL_follow.setBackgroundResource(R.drawable.rect_purple_color_with_solid_shape);
                        tV_follow.setTextColor(ContextCompat.getColor(mActivity,R.color.white));
                        tV_follow.setText(mActivity.getResources().getString(R.string.Following));
                    }
                    else
                    {
                        rL_follow.setBackgroundResource(R.drawable.rect_purple_color_with_stroke_shape);
                        tV_follow.setTextColor(ContextCompat.getColor(mActivity,R.color.purple_color));
                        tV_follow.setText(mActivity.getResources().getString(R.string.follow));
                    }
                    break;


                // Location
                case VariableConstants.REQUEST_CHECK_SETTINGS :
                    switch (resultCode)
                    {
                        case Activity.RESULT_CANCELED :
                            //mDialogBox.showProgressDialog(getResources().getString(R.string.Loading));
                            // call product details api call method
                            if (mSessionManager.getIsUserLoggedIn())
                                getProductDetailsService(ApiUrl.GET_POST_BY_ID_USER);
                            else getProductDetailsService(ApiUrl.GET_POST_BY_ID_GUEST);
                            break;
                    }
                    break;

                // sell it again
                case VariableConstants.SELLING_REQ_CODE :
                    isToSellItAgain = data.getBooleanExtra("isToSellItAgain",false);
                    boolean isPostDeleted = data.getBooleanExtra("isPostDeleted",false);
                    System.out.println(TAG+" "+"isToSwitchItem="+isToSellItAgain);
                    if (isToSellItAgain)
                        tV_makeoffer.setText(getResources().getString(R.string.sell_it_again));

                    if (isPostDeleted)
                        finish();
                    break;

                // call get product details api for logged in user
                case VariableConstants.LANDING_REQ_CODE :
                    boolean isToRefreshHomePage = data.getBooleanExtra("isToRefreshHomePage",true);
                    System.out.println(TAG+" "+"isToRefreshHomePage="+isToRefreshHomePage);
                    boolean isFromSignup = data.getBooleanExtra("isFromSignup",false);

                    // open start browsering screen
                    if (isFromSignup)
                        new DialogBox(mActivity).startBrowsingDialog();

                    if (runTimePermission.checkPermissions(permissionsArray))
                    {
                        getCurrentLocation();
                    }
                    else runTimePermission.requestPermission();
                    break;
            }
        }}


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
                    System.out.println("isAllPermissionGranted="+runTimePermission.checkPermissions(permissionsArray));
                    if (runTimePermission.checkPermissions(permissionsArray))
                    {
                        //mDialogBox.showProgressDialog(getResources().getString(R.string.Loading));
                        getCurrentLocation();
                    }
                }
        }
    }
    /*
     *initiate chat with out offer. */
    private void initiateChat()
    {

        try {
            if(receiverMqttId==null||receiverMqttId.isEmpty())
            {
                Toast.makeText(this, R.string.not_register_task,Toast.LENGTH_SHORT).show();
                return;
            }
            String doucumentId = AppController.getInstance().findDocumentIdOfReceiver(receiverMqttId,postId);
            boolean isChatNotExist;
            if (doucumentId.isEmpty())
            {
                doucumentId=null;
                isChatNotExist=true;
            }else
            {
                isChatNotExist=false;
                AppController.getInstance().getDbController().updateChatDetails(doucumentId,membername,memberProfilePicUrl);
            }
            Intent intent;
            intent = new Intent(this,ChatMessageScreen.class);
            intent.putExtra("isChatNotExist",isChatNotExist);
            intent.putExtra("productId", postId);
            intent.putExtra("receiverUid", receiverMqttId);
            intent.putExtra("receiverName",membername);
            intent.putExtra("documentId",doucumentId);
            intent.putExtra("receiverIdentifier",AppController.getInstance().getUserIdentifier());
            intent.putExtra("receiverImage",memberProfilePicUrl);
            intent.putExtra("colorCode", AppController.getInstance().getColorCode(1% 19));
            intent.putExtra("isFromOfferPage",false);
            if(fromChatScreen.equals("0"))
            {
                startActivity(intent);
            }else
            {
                mActivity.finish();
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void checkForPermission()
    {
        String permission[]=new String[]{"publish_actions"};
        facebook_login.ask_PublishPermission(callbackManager, permission, new Facebook_login.Facebook_callback() {
            @Override
            public void success(String id)
            {
                System.out.println(TAG+" "+"facebook permission success id="+id);
                String post_url=getResources().getString(R.string.share_item_base_url)+postId;
                shareOnFacebook(post_url,productImage,productName,description);
            }

            @Override
            public void error(String error) {
                System.out.println(TAG+" "+"facebook permission error="+error);
            }

            @Override
            public void cancel(String cancel) {
                System.out.println(TAG+" "+"facebook permission error="+cancel);
            }
        });
    }

    private void shareOnFacebook(String link,String thumbnail,String name,String description)
    {
        facebook_share_mamager.shareLinkOnFacebook(link, thumbnail, name, description, new Facebook_share_mamager.Share_callback() {
            @Override
            public void onSucess_share() {
                System.out.println(TAG+" "+"facebook successfully shared");
            }

            @Override
            public void onError(String error) {
                System.out.println(TAG+" "+"facebook successfully failed");
            }
        });
    }
}
