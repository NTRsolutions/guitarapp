package com.yelo.com.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.Gson;
import com.yelo.com.BuildConfig;
import com.yelo.com.R;
import com.yelo.com.get_current_location.FusedLocationReceiver;
import com.yelo.com.get_current_location.FusedLocationService;
import com.yelo.com.main.activity.LoginOrSignupActivity;
import com.yelo.com.mqttchat.AppController;
import com.yelo.com.mqttchat.Database.CouchDbController;
import com.yelo.com.pojo_class.LogDevicePojo;
import com.yelo.com.pojo_class.LoginResponsePojo;
import com.yelo.com.utility.ApiUrl;
import com.yelo.com.utility.CommonClass;
import com.yelo.com.utility.OkHttp3Connection;
import com.yelo.com.utility.ProgressBarHandler;
import com.yelo.com.utility.RunTimePermission;
import com.yelo.com.utility.SessionManager;
import com.yelo.com.utility.VariableConstants;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * <h>LoginWithFacebook</h>
 * <p>
 *     This class is getting called when user Click on Login with facebook from Login screen(LoginActivity).
 *     In this class we do all operation regarding facebook. First of all we retrieve user all information
 *     using facebook graph api 2.0 once we get after that we call Facebook login api and pass user facebook
 *     id. if we get code 200 that means success then move to HomePageActivity or else call signUp api.
 * </p>
 * @since 04/04/17
 * @author 3Embed
 * @version 1.0
 */
public class LoginWithFacebook
{
    private static final String TAG =LoginWithFacebook.class.getSimpleName() ;
    //facebook variables
    private CallbackManager callbackManager;
    private String Fb_emailId="",Fb_firstName="",fb_lastName="",fb_pic="",facebookId="",fullName="",fb_accessToken="", currentLat ="", currentLng ="",address="",city="",countryCode="";
    private Activity mActivity;
    private ProgressBarHandler mProgressBar;
    private SessionManager mSessionManager;
    private RelativeLayout rL_rootview;
    private ProgressBar pBar_fbLogin;
    private ImageView iV_fbicon;
    private TextView tV_facebook;
    private FusedLocationService locationService;
    private RunTimePermission runTimePermission;
    private String[] permissionsArray;

    public LoginWithFacebook(Activity mActivity, RelativeLayout rL_rootview, ProgressBar pBar_fbLogin, ImageView iV_fbicon, TextView tV_facebook) {
        this.mActivity = mActivity;
        this.rL_rootview=rL_rootview;
        this.pBar_fbLogin=pBar_fbLogin;
        this.iV_fbicon=iV_fbicon;
        this.tV_facebook=tV_facebook;
        mSessionManager=new SessionManager(mActivity);
        currentLat=mSessionManager.getCurrentLat();
        currentLng=mSessionManager.getCurrentLng();
        permissionsArray =new String[]{ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION};
        runTimePermission=new RunTimePermission(mActivity, permissionsArray,false);
        if (isLocationFound(currentLat, currentLng)) {
            address = CommonClass.getCompleteAddressString(mActivity, Double.parseDouble(currentLat),Double.parseDouble(currentLng));
            city = CommonClass.getCityName(mActivity, Double.parseDouble(currentLat),Double.parseDouble(currentLng));
            countryCode=CommonClass.getCountryCode(mActivity,Double.parseDouble(currentLat),Double.parseDouble(currentLng));
            //loginRequestApi();
        }
        mProgressBar=new ProgressBarHandler(mActivity);
        FacebookSdk.sdkInitialize(mActivity);
        callbackManager = CallbackManager.Factory.create();
        initializeFacebookSdk();
    }

    /**
     * <h>LoginFacebookSdk</h>
     * <p>
     *     This method is being called from onCreate method. this method is called
     *     when user click on LoginWithFacebook button. it contains three method onSuccess,
     *     onCancel and onError. if login will be successfull then success method will be
     *     called and in that method we obtain user all details like id, email, name, profile pic
     *     etc. onCancel method will be called if user click on facebook with login button and
     *     suddenly click back button. onError method will be called if any problem occurs like
     *     internet issue.
     * </p>
     */
    private void initializeFacebookSdk()
    {
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("LoginActivity", response.toString());

                        if (response.getError() == null) {
                            facebookId = object.optString("id");
                            Fb_emailId = object.optString("email");
                            Fb_firstName = object.optString("first_name");
                            fb_lastName = object.optString("last_name");
                            fb_pic = "https://graph.facebook.com/"+facebookId+"/picture?type=large";
                            fullName=Fb_firstName+" "+fb_lastName;
                            AccessToken token = AccessToken.getCurrentAccessToken();
                            if (token!=null)
                                fb_accessToken=token.getToken();
                            System.out.println(TAG+" "+"facebookId="+facebookId+" "+"accessToken="+token.getToken());

                            if (facebookId!=null && !facebookId.isEmpty())
                            {
                                LocationManager lm = (LocationManager)mActivity.getSystemService(Context.LOCATION_SERVICE);
                                boolean isLocationEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                                System.out.println(TAG+" "+"is location enabled="+ isLocationEnabled +" "+"is permission allowed="+runTimePermission.checkPermissions(permissionsArray));

                                facebookLoginProgress(true);
                                if (isLocationEnabled && runTimePermission.checkPermissions(permissionsArray))
                                    getCurrentLocation();
                                else
                                    loginWithFbApi();
                            }
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,email,gender, birthday,first_name,last_name");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                CommonClass.showSnackbarMessage(rL_rootview,mActivity.getResources().getString(R.string.Loginfailed));
            }

            @Override
            public void onError(FacebookException error) {
                CommonClass.showSnackbarMessage(rL_rootview, error.toString());
                if (error instanceof FacebookAuthorizationException) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut();
                    }
                }
            }
        });
    }

    /**
     * In this method we find current location using FusedLocationApi.
     * in this we have onUpdateLocation() method in which we check if
     * its not null then We call guest user api.
     */
    public void getCurrentLocation()
    {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            locationService = new FusedLocationService(mActivity, new FusedLocationReceiver() {
                @Override
                public void onUpdateLocation() {
                    Location currentLocation = locationService.receiveLocation();
                    if (currentLocation != null) {
                        currentLat = String.valueOf(currentLocation.getLatitude());
                        currentLng = String.valueOf(currentLocation.getLongitude());

                        if (isLocationFound(currentLat, currentLng)) {
                            mSessionManager.setCurrentLat(currentLat);
                            mSessionManager.setCurrentLng(currentLng);
                            address = CommonClass.getCompleteAddressString(mActivity, currentLocation.getLatitude(), currentLocation.getLongitude());
                            city = CommonClass.getCityName(mActivity, currentLocation.getLatitude(), currentLocation.getLongitude());
                            countryCode = CommonClass.getCountryCode(mActivity, currentLocation.getLatitude(), currentLocation.getLongitude());
                            loginWithFbApi();
                        }
                    }
                }
            }
            );
        }
        else CommonClass.showSnackbarMessage(rL_rootview,mActivity.getResources().getString(R.string.NoInternetAccess));
    }

    /**
     * In this method we used to check whether current currentLat and
     * long has been received or not.
     * @param lat The current latitude
     * @param lng The current longitude
     * @return boolean flag true or false
     */
    private boolean isLocationFound(String lat,String lng) {
        return !(lat == null || lat.isEmpty()) && !(lng == null || lng.isEmpty());
    }

    /**
     * <h>LoginWithFbApi</h>
     * <p>
     *     This method is called from initializeFacebookSdk() after getting user complete
     *     information like facebook_id,name,email etc from Facebook login. In this method
     *     we call login api and pass facebook id which we got from facebook api. After
     *     login response we check whether This facebook id is register or not. if not
     *     then call facebook signup api.
     * </p>
     */
    private void loginWithFbApi()
    {
        if (CommonClass.isNetworkAvailable(mActivity))
        {
            //mProgressBar.show();
            JSONObject requestDatas = new JSONObject();
            try {
                // loginType, pushToken, place, city, countrySname, latitude, longitude,facebookId, email
                requestDatas.put("loginType", VariableConstants.TYPE_FACEBOOK);
                requestDatas.put("pushToken",mSessionManager.getPushToken());
                requestDatas.put("place",address);
                requestDatas.put("city",city);
                requestDatas.put("countrySname",countryCode);
                requestDatas.put("latitude", currentLat);
                requestDatas.put("longitude", currentLng);
                requestDatas.put("facebookId", facebookId);
                requestDatas.put("email",Fb_emailId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.LOGIN, OkHttp3Connection.Request_type.POST, requestDatas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    System.out.println(TAG + " " + "facebook login res=" + result);

                    LoginResponsePojo loginResponsePojo;
                    Gson gson=new Gson();
                    loginResponsePojo=gson.fromJson(result,LoginResponsePojo.class);

                    switch (loginResponsePojo.getCode())
                    {
                        // success
                        case "200" :
                            mProgressBar.hide();
                            mSessionManager.setmqttId(loginResponsePojo.getMqttId());
                            mSessionManager.setIsUserLoggedIn(true);
                            mSessionManager.setAuthToken(loginResponsePojo.getToken());
                            mSessionManager.setUserName(loginResponsePojo.getUsername());
                            mSessionManager.setUserImage(loginResponsePojo.getProfilePicUrl());
                            mSessionManager.setUserId(loginResponsePojo.getUserId());
                            mSessionManager.setLoginWith("facebookLogin");
                            initUserDetails(loginResponsePojo.getProfilePicUrl(),loginResponsePojo.getMqttId(),Fb_emailId,Fb_firstName+" "+ fb_lastName,loginResponsePojo.getToken());
                            logDeviceInfo(loginResponsePojo.getToken());
                            break;

                        // User not found
                        case "204" :
                            //signUpWithFb();
                            System.out.println(TAG+" "+"fb_pic="+fb_pic);
                            Intent intent=new Intent(mActivity,LoginOrSignupActivity.class);
                            intent.putExtra("type","fbSignUp");
                            intent.putExtra("userFullName",fullName);
                            intent.putExtra("userImageUrl",fb_pic);
                            intent.putExtra("email",Fb_emailId);
                            intent.putExtra("id",facebookId);
                            intent.putExtra("fb_accessToken",fb_accessToken);
                            mActivity.startActivityForResult(intent,VariableConstants.LOGIN_SIGNUP_REQ_CODE);
                            facebookLoginProgress(false);
                            break;

                        // Error
                        default:
                            CommonClass.showSnackbarMessage(rL_rootview,loginResponsePojo.getMessage());
                            facebookLoginProgress(false);
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    mProgressBar.hide();
                    facebookLoginProgress(false);
                    CommonClass.showSnackbarMessage(rL_rootview,error);
                }
            });
        }
        else CommonClass.showSnackbarMessage(rL_rootview,mActivity.getResources().getString(R.string.NoInternetAccess));
    }

    /**
     * <h>LogDeviceInfo</h>
     * <p>
     *     In this method we used to do api call to send device information like device name
     *     model number, device id etc to server to log the the user with specific device.
     * </p>
     * @param token The auth token for particular user
     */
    private void logDeviceInfo(String token)
    {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            //deviceName, deviceId, deviceOs, modelNumber, appVersion
            final JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("deviceName", Build.BRAND);
                request_datas.put("deviceId", mSessionManager.getDeviceId());
                request_datas.put("deviceOs", Build.VERSION.RELEASE);
                request_datas.put("modelNumber", Build.MODEL);
                request_datas.put("appVersion", BuildConfig.VERSION_NAME);
                request_datas.put("token",token);
                request_datas.put("deviceType",VariableConstants.DEVICE_TYPE);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.LOG_DEVICE, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag)
                {
                    facebookLoginProgress(false);
                    System.out.println(TAG+" "+"log device info="+result);

                    LogDevicePojo logDevicePojo;
                    Gson gson=new Gson();
                    logDevicePojo=gson.fromJson(result,LogDevicePojo.class);

                    switch (logDevicePojo.getCode())
                    {
                        // success
                        case "200" :
                            // Open Home page screen
                            facebookLoginProgress(false);
                            //mActivity.finish();

                            Intent intent = new Intent();
                            intent.putExtra("isToRefreshHomePage",true);
                            mActivity.setResult(VariableConstants.LANDING_REQ_CODE,intent);
                            mActivity.finish();
                            break;

                        // error
                        default:

                            CommonClass.showSnackbarMessage(rL_rootview,logDevicePojo.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    facebookLoginProgress(false);
                    CommonClass.showSnackbarMessage(rL_rootview,error);
                }
            });
        }
        else CommonClass.showSnackbarMessage(rL_rootview,mActivity.getResources().getString(R.string.NoInternetAccess));
    }

    public void fbOnActivityResult(int requestCode, int resultCode, Intent data)
    {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void loginWithFbWithSdk()
    {
        if (CommonClass.isNetworkAvailable(mActivity))
            LoginManager.getInstance().logInWithReadPermissions(mActivity, Arrays.asList("public_profile", "user_friends","email"));
        else
            CommonClass.showSnackbarMessage(rL_rootview,mActivity.getResources().getString(R.string.NoInternetAccess));
    }

    private void facebookLoginProgress(boolean isVisible)
    {
        if (isVisible)
        {
            pBar_fbLogin.setVisibility(View.VISIBLE);
            iV_fbicon.setVisibility(View.GONE);
            tV_facebook.setVisibility(View.GONE);
        }
        else {
            pBar_fbLogin.setVisibility(View.GONE);
            iV_fbicon.setVisibility(View.VISIBLE);
            tV_facebook.setVisibility(View.VISIBLE);
        }
    }

    /*
    * Initialization of the user details .*/
    private void initUserDetails(String profile_Url,String userId,String email,String userName,String token)
    {
        CouchDbController db =AppController.getInstance().getDbController();
        Map<String, Object> map = new HashMap<>();
        if (profile_Url!=null&&!profile_Url.isEmpty())
        {
            map.put("userImageUrl",profile_Url);
        } else {
            map.put("userImageUrl","");
        }
        map.put("userIdentifier", email);
        map.put("userId",userId);
        map.put("userName",userName);
        map.put("apiToken",token);
        if(userId==null||userId.isEmpty())
        {
            Toast.makeText(mActivity,R.string.userIdtext,Toast.LENGTH_SHORT).show();
            return;
        }

        if (!db.checkUserDocExists(AppController.getInstance().getIndexDocId(),userId))
        {
            String userDocId = db.createUserInformationDocument(map);
            db.addToIndexDocument(AppController.getInstance().getIndexDocId(),userId, userDocId);
        } else
        {
            db.updateUserDetails(db.getUserDocId(userId, AppController.getInstance().getIndexDocId()), map);
        }
        db.updateIndexDocumentOnSignIn(AppController.getInstance().getIndexDocId(),userId);
        AppController.getInstance().setSignedIn(true,userId,userName, email);
        AppController.getInstance().setSignStatusChanged(true);
    }
}
