package com.yelo.com.utility;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * <h>SessionManager</h>
 * <p>
 *     In this class we used to save a string value like token, user name etc
 *     into SharedPreferences. And whereever we want that value then we used to
 *     make instance of same class then fetch that.
 * </p>
 * @since 4/5/2017
 */

public class SessionManager
{
    private SharedPreferences sharedpreferences;
    private static final String MY_PREFERENCES = "MyPrefs" ;
    private  SharedPreferences.Editor editor;

    // Declare contant variables
    private static final String AUTH_TOKEN="auth_token";
    private static final String IS_LOGGED_IN="logged_in";
    private static final String USER_NAME="user_name";
    private static final String USER_FULL_NAME="user_full_name";
    private static final String USER_IMAGE="user_image";
    private static final String USER_ID="user_id";
    private static final String LOGIN_WITH="login_with";
    private static final String PUSH_TOKEN="push_token";
    private static final String DEVICE_ID="device_id";
    private static final String FB_FRIEND_COUNT="fb_friend_count";
    private static final String CONTECT_FRIEND_COUNT="contect_friend_count";
    private static final String BACKGROUNG_NOTIFICATION="background_notification";
    private static final String ISCHATLISTSYNCE="chatListSync";
    private static final String CURRENT_LAT="current_lat";
    private static final String CURRENT_LNG="current_lng";
    private static final String USER_PAYPAL="userPaypal";


    private static final String USER_MARKET_OWNER="userMarketOwner";
    private static final String COMING_FROM="coming_from";



    public SessionManager(Context mActivity) {
        sharedpreferences = mActivity.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        editor.apply();
    }

    /**
     * Save Token created by FCM
     */
    public void setPushToken(String pushToken)
    {
        editor.putString(PUSH_TOKEN,pushToken);
        editor.apply();
    }

    public String getPushToken()
    {
        return sharedpreferences.getString(PUSH_TOKEN,"");
    }

    // Save Device id
    public void setDeviceId(String deviceId)
    {
        editor.putString(DEVICE_ID,deviceId);
        editor.apply();
    }

    public String getDeviceId()
    {
        return sharedpreferences.getString(DEVICE_ID,"");
    }

    // User pay pal
    public void setUserPayPal(String pay_pal)
    {
        editor.putString(USER_PAYPAL,pay_pal);
        editor.apply();
    }

    public String getUserPayPal()
    {
        return sharedpreferences.getString(USER_PAYPAL,"");
    }

    /**
     * Save auth token after login or sign up
     */
    public void setAuthToken(String authToken)
    {
        editor.putString(AUTH_TOKEN,authToken);
        editor.apply();
    }

    public String getAuthToken()
    {
        return sharedpreferences.getString(AUTH_TOKEN,"");
    }

    /**
     * save loggged in user flag i.e true if user is logged in else false.
     */
    public void setIsUserLoggedIn(boolean flag)
    {
        editor.putBoolean(IS_LOGGED_IN,flag);
        editor.commit();
    }
    public boolean getIsUserLoggedIn()
    {
        return sharedpreferences.getBoolean(IS_LOGGED_IN,false);
    }

    /**
     * Save user name, user image & user full name after login or sign up api response
     */
    public void setUserName(String userName)
    {
        editor.putString(USER_NAME,userName);
        editor.apply();
    }

    public String getUserName()
    {
        return sharedpreferences.getString(USER_NAME,"");
    }

    // user full name
    public void setUserFullName(String userName)
    {
        editor.putString(USER_FULL_NAME,userName);
        editor.apply();
    }

    public String getUserFullName()
    {
        return sharedpreferences.getString(USER_FULL_NAME,"");
    }

    // user image
    public void setUserImage(String userImage)
    {
        editor.putString(USER_IMAGE,userImage);
        editor.apply();
    }

    public String getUserImage()
    {
        return sharedpreferences.getString(USER_IMAGE,"");
    }

    // User ID
    public void setUserId(String userId)
    {
        editor.putString(USER_ID,userId);
        editor.apply();
    }


    public String getmqttId()
    {
        return sharedpreferences.getString("mqttId",null);
    }

    // User ID
    public void setmqttId(String mqttId)
    {
        editor.putString("mqttId",mqttId);
        editor.apply();
    }

    public String getUserId()
    {
        return sharedpreferences.getString(USER_ID,"");
    }

    // set login with like normal login, facebook or google plus
    public void setLoginWith(String loginWith)
    {
        editor.putString(LOGIN_WITH,loginWith);
        editor.apply();
    }

    public String getLoginWith()
    {
        return sharedpreferences.getString(LOGIN_WITH,"");
    }

    // save phone contect friend count
    public void setContectFriendCount(int contectFriendCount)
    {
        editor.putInt(CONTECT_FRIEND_COUNT,contectFriendCount);
        editor.apply();
    }

    public int getContectFriendCount()
    {
        return sharedpreferences.getInt(CONTECT_FRIEND_COUNT,0);
    }

    // save facebook friend count
    public void setFbFriendCount(int fbFriendCount)
    {
        editor.putInt(FB_FRIEND_COUNT,fbFriendCount);
        editor.apply();
    }

    public int getFbFriendCount(){
        return sharedpreferences.getInt(FB_FRIEND_COUNT,0);
    }

    public void setIsBackgroundNotification(boolean isBackgroundNotification)
    {
        editor.putBoolean(BACKGROUNG_NOTIFICATION,isBackgroundNotification);
        editor.apply();
    }

    public boolean isBackgroundNotification()
    {
        return sharedpreferences.getBoolean(BACKGROUNG_NOTIFICATION,false);
    }

    public void setChatSync(boolean isSynce)
    {
        editor.putBoolean(ISCHATLISTSYNCE,isSynce);
        editor.apply();
    }

    public boolean getChatSync()
    {
        return sharedpreferences.getBoolean(ISCHATLISTSYNCE,false);
    }

    /**
     * save user current lat and lng
     */
    public void setCurrentLat(String lat)
    {
        editor.putString(CURRENT_LAT,lat);
        editor.apply();
    }

    public String getCurrentLat()
    {
        return sharedpreferences.getString(CURRENT_LAT,"");
    }

    public void setCurrentLng(String lng)
    {
        editor.putString(CURRENT_LNG,lng);
        editor.apply();
    }

    public String getCurrentLng()
    {
        return sharedpreferences.getString(CURRENT_LNG,"");
    }


    public  boolean getUserMarketOwner() {

        return  sharedpreferences.getBoolean(USER_MARKET_OWNER,false);
    }


    public void setUserMarketOwner(boolean isOwner)
    {
        editor.putBoolean(USER_MARKET_OWNER,isOwner);
        editor.apply();
    }

    public  String getComingFrom() {
        return sharedpreferences.getString(COMING_FROM,"");
    }

    public void setComingFrom(String comingFrom){

        editor.putString(COMING_FROM,comingFrom);
        editor.apply();
    }


}
