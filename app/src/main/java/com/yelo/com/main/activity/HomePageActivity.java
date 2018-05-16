package com.yelo.com.main.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.otto.Subscribe;
import com.yelo.com.R;
import com.yelo.com.badgeView.Badge;
import com.yelo.com.badgeView.BadgeView;
import com.yelo.com.fcm_push_notification.Config;
import com.yelo.com.fcm_push_notification.NotificationMessageDialog;
import com.yelo.com.fcm_push_notification.NotificationUtils;
import com.yelo.com.main.ProductPictureFragment;
import com.yelo.com.main.tab_fragments.ChatFrag;
import com.yelo.com.main.tab_fragments.HomeFrag;
import com.yelo.com.main.tab_fragments.ProfileFrag;
import com.yelo.com.mqttchat.AppController;
import com.yelo.com.mqttchat.Database.CouchDbController;
import com.yelo.com.mqttchat.Utilities.MqttEvents;
import com.yelo.com.mqttchat.Utilities.TimestampSorter;
import com.yelo.com.pojo_class.product_category.ProductCategoryMainPojo;
import com.yelo.com.utility.ApiUrl;
import com.yelo.com.utility.CommonClass;
import com.yelo.com.utility.CustomBottomNavigationView;
import com.yelo.com.utility.DialogBox;
import com.yelo.com.utility.OkHttp3Connection;
import com.yelo.com.utility.SessionManager;
import com.yelo.com.utility.VariableConstants;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**Pro
 * <h>HomePageActivity</h>
 * <p>
 *     In this class we have the bottom Navigation layout for various fragment transition.
 * </p>
 * @since 3/31/2017             https://we.tl/mEV491gkWs
 */
public class HomePageActivity extends AppCompatActivity
{
    private static final String TAG = HomePageActivity.class.getSimpleName();
    public VerifyLoginWithFacebook verifyLoginWithFacebook;
    private Activity mActivity;
    private SessionManager mSessionManager;
    private Fragment homeFrag,socialFarg,chatFarg,myProfileFrag, buySellFragment;

    public  static Fragment  productcategoryFrag;

    private boolean isToHighLightTab;
    private NotificationMessageDialog mNotificationMessageDialog;
    public CustomBottomNavigationView bottomNavigationView;
    public RelativeLayout rL_rootElement;
    private boolean isToStartActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        mActivity=HomePageActivity.this;
        isToStartActivity = true;

        int fragmentcount = getSupportFragmentManager().getBackStackEntryCount();

        Log.i( "count", fragmentcount+"" );
        //FacebookSdk.sdkInitialize(mActivity);
        //share();
        //new RequestTask().execute("https://play.google.com/store/apps/details?id=com.yelo.com&hl=en");

        isToHighLightTab=false;
        mSessionManager=new SessionManager(mActivity);
        mNotificationMessageDialog=new NotificationMessageDialog(mActivity);
        rL_rootElement = (RelativeLayout) findViewById(R.id.rL_rootElement);

        // receive datas
        Intent intent = getIntent();
        if (intent!=null)
        {
            boolean isFromSignup = intent.getBooleanExtra("isFromSignup",false);
            System.out.println(TAG+" "+"isFromSignup first="+isFromSignup);

            if (isFromSignup)
                new DialogBox(mActivity).startBrowsingDialog();
        }
        initBottomNavView();
        /*
         * Updating the bagged count.*/
        setChatCount(countUnreadChat());



    }



    @Override
    protected void onResume()
    {
        super.onResume();
        isToStartActivity = true;
        AppController.getBus().register(this);
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mNotificationMessageDialog.mRegistrationBroadcastReceiver, new IntentFilter(Config.REGISTRATION_COMPLETE));
        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mNotificationMessageDialog.mRegistrationBroadcastReceiver, new IntentFilter(Config.PUSH_NOTIFICATION));
        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
        Bundle bundle=getIntent().getExtras();
        if(bundle!=null)
        {
            if(bundle.containsKey("openChat")&&bundle.getBoolean("openChat"))
            {
                if(CommonClass.isBackFromChat)
                {
                    CommonClass.isBackFromChat=false;
                    View view = bottomNavigationView.findViewById(R.id.nav_chat);
                    view.performClick();
                }
            }
        }

        /*
         * Updating the bagged count.*/
        setChatCount(countUnreadChat());
    }

    @Override
    protected void onPause()
    {
        AppController.getBus().unregister(this);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mNotificationMessageDialog.mRegistrationBroadcastReceiver);
        super.onPause();
    }

    /**
     * <h>InitBottomNavView</h>Museum_Of_London_160x120.png
     * <p>
     *     In this method we used to initialize BottomNavigationView variables.
     * </p>
     */
    private void initBottomNavView()
    {


//        va07t2669 .

        bottomNavigationView = (CustomBottomNavigationView) findViewById(R.id.navigation);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/roboto-regular.ttf");
        disableAllAnimation();
        bottomNavigationView.setTypeface(font);
        bottomNavigationView.setIconSize(26,26);
        bottomNavigationView.setTextSize(12);
        homeFrag=new HomeFrag();
        chatFarg=new ChatFrag();
        productcategoryFrag = new ProductPictureFragment();

        buySellFragment = BuySellFragment.getInstance( this );

        myProfileFrag=new ProfileFrag();
        android.support.v4.app.FragmentManager manager =this.getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        final Menu nav_menu = bottomNavigationView.getMenu();
        nav_menu.getItem(0).setIcon(R.drawable.tabbar_home_onn);
        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item)
                    {
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        switch (item.getItemId())
                        {
                            // Home frag
                            case R.id.nav_home:

                                if(!mSessionManager.getComingFrom().equals( "signup" )) {


                                    setBottomTabIcon( nav_menu, R.drawable.tabbar_home_onn, R.drawable.tabbar_social_off, R.drawable.tab_bar_chat_off, R.drawable.tabbar_profile_off );
//                                System.out.println(TAG+" "+"home frag="+productcategoryFrag.isAdded());

                                    isToHighLightTab = true;

//                                if (homeFrag.isAdded())
//                                    transaction.show(homeFrag);
//                                else
//                                    transaction.add(R.id.frame_layout,homeFrag);




//                                    if (productcategoryFrag.isAdded())
//                                        transaction.show( productcategoryFrag );
//                                    else
                                        transaction.replace( R.id.frame_layout, new ProductPictureFragment() );
//                                  transaction.replace( R.id.frame_layout, new ProductPictureFragment(), "productcategoryFrag" );


//                                    if (chatFarg.isAdded())
//                                        transaction.hide( chatFarg );
//                                    if (myProfileFrag.isAdded())
//                                        transaction.hide( myProfileFrag );


//                                    Fragment mYff = getCurrentFragment();
//
//                                    if(mYff != null) {
//                                        if (mYff instanceof HomeFrag) {
//
//                                            transaction.hide( homeFrag );
//                                        }
//                                    }

                                }else {

                                    Toast.makeText( HomePageActivity.this, "Please select any of buy or sell option", Toast.LENGTH_SHORT ).show();
                                }
                                break;


                            // Camera
                            case R.id.nav_camera:
                                //item.setIcon(R.drawable.tab_bar_camera_on);
                                isToHighLightTab=false;
                                if (mSessionManager.getIsUserLoggedIn())
                                {

                                    if(!mSessionManager.getComingFrom().equals( "signup" )) {

                                        hitApiOnServerToCheckUserAccount();

//                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                                            if (isToStartActivity) {
//                                                startActivity( new Intent( mActivity, Camera2Activity.class ) );
//                                                isToStartActivity = false;
//                                            }
//                                        } else {
//                                            if (isToStartActivity) {
//                                                isToStartActivity = false;
//                                                startActivity( new Intent( mActivity, CameraActivity.class ) );
//                                            }
//                                        }
                                    }else {
                                        Toast.makeText( HomePageActivity.this, "Please select any of buy or sell option", Toast.LENGTH_SHORT ).show();
                                    }
                                }
                                else
                                {
                                    if (isToStartActivity) {
                                        isToStartActivity = false;
                                        startActivityForResult(new Intent(mActivity,LandingActivity.class), VariableConstants.LANDING_REQ_CODE);
                                    }}
                                break;

                            // chat
                            case R.id.nav_chat:
                                if (mSessionManager.getIsUserLoggedIn())
                                {

                                    if (!mSessionManager.getComingFrom().equals( "signup" )) {
                                        setBottomTabIcon( nav_menu, R.drawable.tabbar_home_off, R.drawable.tabbar_social_off, R.drawable.tab_bar_chat_onn, R.drawable.tabbar_profile_off );
                                        isToHighLightTab = true;

//                                        if (chatFarg.isAdded())
//                                            transaction.show( chatFarg );
//                                        else transaction.add( R.id.frame_layout, chatFarg );
                                        transaction.replace( R.id.frame_layout, new ChatFrag() );

//                                        if (myProfileFrag.isAdded())
//                                            transaction.hide( myProfileFrag );
//
//
//                                        if (productcategoryFrag.isAdded()) {
//                                            transaction.hide( productcategoryFrag );
//                                        }

//                                        Fragment mYff = getCurrentFragment();
//
//                                        if(mYff != null) {
//                                            if (mYff instanceof HomeFrag) {
//
//                                                transaction.hide( homeFrag );
//                                            }
//                                        }


                                    }else {
                                        Toast.makeText( HomePageActivity.this, "Please select any of buy or sell option", Toast.LENGTH_SHORT ).show();
                                    }



                                }
                                else{
                                    if (isToStartActivity) {
                                        startActivityForResult(new Intent(mActivity, LandingActivity.class), VariableConstants.LANDING_REQ_CODE);
                                        isToStartActivity = false;
                                    }
                                }
                                break;

                            // My profile
                            case R.id.nav_profile:
                                if (mSessionManager.getIsUserLoggedIn())
                                {

                                    if (!mSessionManager.getComingFrom().equals( "signup" )) {
                                        setBottomTabIcon( nav_menu, R.drawable.tabbar_home_off, R.drawable.tabbar_social_off, R.drawable.tab_bar_chat_off, R.drawable.tabbar_profile_on );
                                        isToHighLightTab = true;

//                                        if (myProfileFrag.isAdded())
//                                            transaction.show( myProfileFrag );
//                                        else transaction.add( R.id.frame_layout, myProfileFrag );

                                        transaction.replace( R.id.frame_layout, new ProfileFrag() );
//
//                                        if (chatFarg.isAdded())
//                                            transaction.hide(chatFarg);
//
//                                        if (productcategoryFrag.isAdded())
//                                            transaction.hide(productcategoryFrag);
////
                                    }else {
                                        Toast.makeText( HomePageActivity.this, "Please select any of buy or sell option", Toast.LENGTH_SHORT ).show();
                                    }



//                                    if (productcategoryFrag.isAdded())
//                                        transaction.hide(productcategoryFrag);
                                }
                                else {
                                    if (isToStartActivity) {
                                        isToStartActivity = false;
                                        startActivityForResult(new Intent(mActivity,LandingActivity.class), VariableConstants.LANDING_REQ_CODE);
                                    }}
                                break;
                        }
                        transaction.commit();
                        return isToHighLightTab;
                    }
                });
//        transaction.add(R.id.frame_layout, homeFrag);


        if(mSessionManager.getComingFrom().equals( "login" )){
            transaction.add(R.id.frame_layout, productcategoryFrag);
//            transaction.replace(R.id.frame_layout, buySellFragment);
        }else if(mSessionManager.getComingFrom().equals( "signup" ) ) {
            transaction.add(R.id.frame_layout, buySellFragment);
        }else {
            transaction.add(R.id.frame_layout, productcategoryFrag);
        }
        transaction.commit();

    }

    /**
     * <h>DisableAllAnimation</h>
     * <p>
     *     In this method we used to disable auto-animation from bottom navigation view like
     *     shifting of icons and text.
     * </p>
     */
    private void disableAllAnimation() {
        bottomNavigationView.enableAnimation(false);
        bottomNavigationView.enableShiftingMode(false);
        bottomNavigationView.enableItemShiftingMode(false);
    }

    /**
     * <h>SetChatCount</h>
     * <p>
     *     In this method we used to set the badge count(unread chat count) on the
     *     chat icon.
     * </p>
     */
    private Badge baage;
    public void setChatCount(int count)
    {
        if(mSessionManager.getIsUserLoggedIn()) {
            Log.d("dfgt", "" + count);
            if (baage != null) {
                baage.setBadgeNumber(count);
            } else {
                baage = new BadgeView(this)
                        .setBadgeNumber(count)
                        .setGravityOffset(14, 4, true)
                        .setBadgeTextSize(8, true)
                        .setBadgeTextColor(ContextCompat.getColor(this, R.color.white))
                        .setBadgeBackgroundColor(ContextCompat.getColor(this, R.color.pink_color))
                        .bindTarget(bottomNavigationView.getBottomNavigationItemView(3));
            }
        }
    }

    public Badge getBaage()
    {
        return baage;
    }

    /**
     * <h>SetBottomTabIcon</h>
     * <p>
     *     In this method we used to set the selected and unselected icon of bottom navigation view.
     * </p>
     * @param nav_menu The Navigation Menu
     * @param homeIcon First Tab i.e HomePage icon
     * @param socialIcon The Second Tab i.e SocialPage icon
     * @param chatIcon The Fourth Tab i.e Camera Page icon
     * @param profileIcon The Fifth Tab i.e Profile page icon
     */
    private void setBottomTabIcon(Menu nav_menu,int homeIcon,int socialIcon,int chatIcon,int profileIcon)
    {
        nav_menu.getItem(0).setIcon(homeIcon);
        nav_menu.getItem(2).setIcon(chatIcon);
        nav_menu.getItem(3).setIcon(profileIcon);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(TAG+" "+"onactivity result "+"res code="+resultCode+" "+"req code="+requestCode+" "+"data="+data);
        if (data!=null)
        {
            switch (requestCode)
            {
                case VariableConstants.LANDING_REQ_CODE :
                    boolean isToRefreshHomePage = data.getBooleanExtra("isToRefreshHomePage",false);
                    boolean isFromSignup = data.getBooleanExtra("isFromSignup",false);
                    System.out.println(TAG+" "+"isToRefreshHomePage="+isToRefreshHomePage+" "+"isFromSignup="+isFromSignup);
                    if (isToRefreshHomePage)
                    {
                        isToStartActivity=false;
                        Intent intent = getIntent();
                        intent.putExtra("isFromSignup",isFromSignup);
                        startActivity(intent);
                        finish();
                    }
                    break;
            }

            if ( verifyLoginWithFacebook!=null)
                verifyLoginWithFacebook.fbOnActivityResult(requestCode,resultCode,data);

            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    /**
     * To fetch the chats from the couchdb, stored locally
     */
    @SuppressWarnings("unchecked")
    public int countUnreadChat()
    {
        int total_count=0;
        if(mSessionManager.getIsUserLoggedIn())
        {
            return total_count;
        }
        CouchDbController db = AppController.getInstance().getDbController();
        Map<String, Object> map = db.getAllChatDetails(AppController.getInstance().getChatDocId());
        if (map != null)
        {
            ArrayList<String> receiverUidArray = (ArrayList<String>) map.get("receiverUidArray");
            ArrayList<String> receiverDocIdArray = (ArrayList<String>) map.get("receiverDocIdArray");
            ArrayList<Map<String, Object>> chats = new ArrayList<>();
            for (int i = 0; i < receiverUidArray.size(); i++)
            {
                chats.add(db.getParticularChatInfo(receiverDocIdArray.get(i)));
                Collections.sort(chats, new TimestampSorter());
            }
            for (int i = 0; i < chats.size(); i++)
            {
                int temp_count= parseData(chats.get(i));
                total_count=total_count+temp_count;

            }
        }
        return total_count;
    }

    /*
     *Collect unread count*/
    private int parseData(@NonNull Map<String, Object> chat_item)
    {
        int count=0;
        boolean hasNewMessage =(Boolean)chat_item.get("hasNewMessage");
        if (hasNewMessage)
        {
            try
            {
                count=Integer.parseInt((String)chat_item.get("newMessageCount"));
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return count;
    }

    @Subscribe
    public void getMessage(JSONObject object)
    {
        try
        {
            if (object.getString("eventName").equals(MqttEvents.Message.value + "/" + AppController.getInstance().getUserId())||object.getString("eventName").equals(MqttEvents.OfferMessage.value + "/" + AppController.getInstance().getUserId()))
            {
                int count=getBaage().getBadgeNumber();
                count=count+1;
                setChatCount(count);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {


        int count = getSupportFragmentManager().getBackStackEntryCount();


        Log.d( "backstackcount", count +"");
        if(count == 0){
            this.getSupportFragmentManager().popBackStack();
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                super.finishAndRemoveTask();
            }
            else {

                super.finish();
            }
        }else {
            super.onBackPressed();
        }



    }


    private Fragment getCurrentFragment(){

        try {
            Fragment currentFragment = this.getSupportFragmentManager().findFragmentById(R.id.frame_layout);
            return currentFragment;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;

    }


    private void hitApiOnServerToCheckUserAccount(){
        if (CommonClass.isNetworkAvailable(this))
        {
            final ProgressDialog pDialog = new ProgressDialog(this,0);


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
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                if (isToStartActivity) {
                                    startActivity( new Intent( mActivity, Camera2Activity.class ) );
                                    isToStartActivity = false;
                                }
                            } else {
                                if (isToStartActivity) {
                                    isToStartActivity = false;
                                    startActivity( new Intent( mActivity, CameraActivity.class ) );
                                }
                            }
                            break;


                        case "204" :
                            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity,
                                    R.style.CustomPopUpThemeBlue);
                            builder.setMessage("You have not configured your account detail yet, Please click ok to configure");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    startActivity(new Intent(mActivity, AddPaymentActivity.class));
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
                            CommonClass.sessionExpired(mActivity);
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