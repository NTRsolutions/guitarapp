package com.yelo.com.main.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.yelo.com.BuildConfig;
import com.yelo.com.R;
import com.yelo.com.get_current_location.FusedLocationReceiver;
import com.yelo.com.get_current_location.FusedLocationService;
import com.yelo.com.main.LoginWithFacebook;
import com.yelo.com.mqttchat.AppController;
import com.yelo.com.mqttchat.Database.CouchDbController;
import com.yelo.com.pojo_class.LogDevicePojo;
import com.yelo.com.pojo_class.LoginResponsePojo;
import com.yelo.com.utility.ApiUrl;
import com.yelo.com.utility.CommonClass;
import com.yelo.com.utility.OkHttp3Connection;
import com.yelo.com.utility.RunTimePermission;
import com.yelo.com.utility.SessionManager;
import com.yelo.com.utility.VariableConstants;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * <h>LandingActivity</h>
 * <p>
 *     This class is called when user is not logged in. In this screen
 *     we can do login with facebook or google. And we have option for
 *     login or signup.
 * </p>
 * @since 13-May-17
 */
public class LandingActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener
{
    private static final String TAG = LandingActivity.class.getSimpleName();
    private Activity mActivity;
    private LoginWithFacebook loginWithFacebook;
    private GoogleApiClient mGoogleApiClient;
    private RelativeLayout rL_rootview;
    private SessionManager mSessionManager;
    private ImageView iV_google_icon;
    private TextView tV_googleLogin;
    private ProgressBar pBar_googleLogin;
    private FusedLocationService locationService;
    private String currentLat ="",currentLng ="",address="",city="",countryCode="",fullName="",email="",id="",serverAuthCode="",personPhotoUrl="";
    private RunTimePermission runTimePermission;
    private String[] permissionsArray;
    private boolean isFromGplusLocation,isToStartActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        overridePendingTransition(R.anim.slide_up, R.anim.stay );
        initVariables();
    }

    /**
     * In this method we used to initialize the data member and xml variables.
     */
    private void initVariables()
    {
        mActivity=LandingActivity.this;
        mSessionManager=new SessionManager(mActivity);
        isToStartActivity = true;

        currentLat=mSessionManager.getCurrentLat();
        currentLng=mSessionManager.getCurrentLng();

        if (isLocationFound(currentLat, currentLng)) {
            address = CommonClass.getCompleteAddressString(mActivity, Double.parseDouble(currentLat),Double.parseDouble(currentLng));
            city = CommonClass.getCityName(mActivity, Double.parseDouble(currentLat),Double.parseDouble(currentLng));
            countryCode=CommonClass.getCountryCode(mActivity,Double.parseDouble(currentLat),Double.parseDouble(currentLng));
        }

        //Getting registration token
        if (mSessionManager.getPushToken()==null || mSessionManager.getPushToken().isEmpty()) {
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            //Displaying token on logcat
            System.out.println(TAG + " " + "My Refreshed token: " + refreshedToken);
            if (refreshedToken != null && !refreshedToken.isEmpty())
                mSessionManager.setPushToken(refreshedToken);
        }

        permissionsArray =new String[]{ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION};
        runTimePermission=new RunTimePermission(mActivity, permissionsArray,false);
        CommonClass.statusBarColor(mActivity);
        CommonClass.generateHashKey(mActivity);
        rL_rootview = (RelativeLayout) findViewById(R.id.rL_rootview);
        iV_google_icon= (ImageView) findViewById(R.id.iV_google_icon);
        ImageView iV_fbicon = (ImageView) findViewById(R.id.iV_fbicon);
        tV_googleLogin= (TextView) findViewById(R.id.tV_googleLogin);
        TextView tV_facebook = (TextView) findViewById(R.id.tV_facebook);
        pBar_googleLogin= (ProgressBar) findViewById(R.id.pBar_googleLogin);
        ProgressBar pBar_fbLogin = (ProgressBar) findViewById(R.id.pBar_fbLogin);
        // initialize google signin variables

        /*GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                .requestServerAuthCode(getResources().getString(R.string.servers_client_id),false)
                .requestEmail()
                .build();*/

       GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        loginWithFacebook=new LoginWithFacebook(mActivity, rL_rootview, pBar_fbLogin, iV_fbicon, tV_facebook);
        TextView tV_login,tV_signup;
        // Login
        tV_login= (TextView) findViewById(R.id.tV_login);
        tV_login.setOnClickListener(this);

        // Sign up
        tV_signup= (TextView) findViewById(R.id.tV_signup);
        tV_signup.setOnClickListener(this);

        // fb login
        RelativeLayout rL_fb_login = (RelativeLayout) findViewById(R.id.rL_fb_login);
        rL_fb_login.setOnClickListener(this);

        // Google login
        RelativeLayout rL_google_login= (RelativeLayout) findViewById(R.id.rL_google_login);
        rL_google_login.setOnClickListener(this);

        // close
        RelativeLayout rL_skip= (RelativeLayout) findViewById(R.id.rL_skip);
        rL_skip.setOnClickListener(this);

        // Terms and conditions
        TextView tV_termsNcondition= (TextView) findViewById(R.id.tV_termsNcondition);
        tV_termsNcondition.setOnClickListener(this);

        // privacy policy
        TextView tV_privacy= (TextView) findViewById(R.id.tV_privacy);
        tV_privacy.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isToStartActivity = true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            // Login
            case R.id.tV_login :
                openLoginSignupScreen("login");
                break;

            // Sign up
            case R.id.tV_signup :
                openLoginSignupScreen("normalSignup");
                break;

            // login with facebook
            case R.id.rL_fb_login :
                LoginManager.getInstance().logOut();
                isFromGplusLocation=false;
                loginWithFacebook.loginWithFbWithSdk();
                break;

            // login with google
            case R.id.rL_google_login :
                isFromGplusLocation=true;
                signInWithGoogle();
                break;

            // close
            case R.id.rL_skip :
                onBackPressed();
                break;

            // Terms and condition
            case R.id.tV_termsNcondition :
                if (isToStartActivity) {
                    isToStartActivity = false;
                    Intent termsNconIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.termsNconditionsUrl)));
                    startActivity(termsNconIntent);
                }
                break;

            // privacy policy
            case R.id.tV_privacy :
                if (isToStartActivity) {
                    isToStartActivity = false;

                    try {
                        Intent privacyIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.privacyPolicyUrl)));
                        startActivity(privacyIntent);
                    }catch (Exception e){

                    }

                }
                break;
        }
    }

    private void signInWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, VariableConstants.GOOGLE_LOGIN_REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data!=null)
        {
            loginWithFacebook.fbOnActivityResult(requestCode, resultCode,data);
            switch (requestCode)
            {
                case VariableConstants.GOOGLE_LOGIN_REQ_CODE :
                    GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                    handleSignInResult(result);
                    break;

                // From LoginSignUp Screen
                case VariableConstants.LOGIN_SIGNUP_REQ_CODE :
                    boolean isToFinishLandingScreen = data.getBooleanExtra("isToFinishLandingScreen",false);
                    boolean isFromSignup = data.getBooleanExtra("isFromSignup",false);
                    System.out.println(TAG+"isToFinishLandingScreen="+isToFinishLandingScreen+" "+"isFromSignup="+isFromSignup);

                    if (isToFinishLandingScreen)
                    {
                        Intent intent = new Intent();
                        intent.putExtra("isToRefreshHomePage",true);
                        intent.putExtra("isFromSignup",isFromSignup);
                        setResult(VariableConstants.LANDING_REQ_CODE,intent);
                        onBackPressed();
                    }
                    break;
            }
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            assert acct != null;
            Log.e(TAG, "display name: " + acct.getDisplayName());

            fullName = acct.getDisplayName();
            Uri imageUri=acct.getPhotoUrl();
            if (imageUri!=null)
                personPhotoUrl = imageUri.toString();

            email = acct.getEmail();
            id=acct.getId();
            String familyName = acct.getFamilyName();
            String givenName = acct.getGivenName();
            String idToken = acct.getIdToken();
            serverAuthCode=acct.getServerAuthCode();

            Log.e(TAG, "Name: " + fullName + ", email: " + email
                    + ", Image: " + personPhotoUrl+", id: "+id+", familyName: "+ familyName +", givenName: "+ givenName +", idToken: "+ idToken +", serverAuthCode: "+serverAuthCode);

            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean isLocationEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            System.out.println(TAG+" "+"is location enabled="+ isLocationEnabled +" "+"is permission allowed="+runTimePermission.checkPermissions(permissionsArray));

            googleLoginProgress(true);
            if (isLocationEnabled && runTimePermission.checkPermissions(permissionsArray))
                getCurrentLocation();
            else
                googleLoginApi();
        }
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
                    if (currentLocation != null) {
                        currentLat = String.valueOf(currentLocation.getLatitude());
                        currentLng = String.valueOf(currentLocation.getLongitude());

                        if (isLocationFound(currentLat, currentLng)) {
                            System.out.println(TAG+" "+"currentLat="+ currentLat +" "+"currentLng="+ currentLng);
                            mSessionManager.setCurrentLat(currentLat);
                            mSessionManager.setCurrentLng(currentLng);
                            address = CommonClass.getCompleteAddressString(mActivity, currentLocation.getLatitude(), currentLocation.getLongitude());
                            city = CommonClass.getCityName(mActivity, currentLocation.getLatitude(), currentLocation.getLongitude());
                            countryCode = CommonClass.getCountryCode(mActivity, currentLocation.getLatitude(), currentLocation.getLongitude());
                            googleLoginApi();
                        }
                    }
                }
            }
            );
        }
        else CommonClass.showSnackbarMessage(rL_rootview,getResources().getString(R.string.NoInternetAccess));
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

    private void googleLoginApi()
    {
        if (CommonClass.isNetworkAvailable(mActivity))
        {
            JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("loginType",VariableConstants.TYPE_GOOGLE);
                request_datas.put("pushToken",mSessionManager.getPushToken());
                request_datas.put("place",address);
                request_datas.put("city",city);
                request_datas.put("countrySname",countryCode);
                request_datas.put("latitude", currentLat);
                request_datas.put("longitude", currentLng);
                request_datas.put("googleId", id);
                request_datas.put("email",email);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.LOGIN, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag)
                {
                    System.out.println(TAG+" "+"google login res="+result);
                    //progress_bar_login.setVisibility(View.GONE);
                    LoginResponsePojo loginResponse;
                    Gson gson=new Gson();
                    loginResponse=gson.fromJson(result,LoginResponsePojo.class);

                    switch (loginResponse.getCode())
                    {
                        // Success
                        case "200":
                            mSessionManager.setIsUserLoggedIn(true);
                            mSessionManager.setmqttId(loginResponse.getMqttId());
                            mSessionManager.setAuthToken(loginResponse.getToken());
                            mSessionManager.setUserName(loginResponse.getUsername());
                            mSessionManager.setUserImage(loginResponse.getProfilePicUrl());
                            mSessionManager.setUserId(loginResponse.getUserId());
                            mSessionManager.setLoginWith("googleLogin");
                            initUserDetails(loginResponse.getProfilePicUrl(),loginResponse.getMqttId(),email,loginResponse.getUsername(),loginResponse.getToken());
                            logDeviceInfo(loginResponse.getToken());
                            break;

                        // user not found
                        case "204" :
                            if (isToStartActivity) {
                                isToStartActivity = false;
                                googleLoginProgress(false);
                                Intent intent = new Intent(mActivity, LoginOrSignupActivity.class);
                                intent.putExtra("type", "googleSignUp");
                                intent.putExtra("userFullName", fullName);
                                intent.putExtra("userImageUrl", personPhotoUrl);
                                intent.putExtra("email", email);
                                intent.putExtra("id", id);
                                intent.putExtra("serverAuthCode", serverAuthCode);
                                startActivityForResult(intent, VariableConstants.LOGIN_SIGNUP_REQ_CODE);
                            }
                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // Error
                        default:
                            //tV_do_login.setVisibility(View.VISIBLE);
                            CommonClass.showTopSnackBar(rL_rootview,loginResponse.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    googleLoginProgress(false);
                    CommonClass.showTopSnackBar(rL_rootview,error);
                }
            });
        }
        else CommonClass.showSnackbarMessage(rL_rootview,getResources().getString(R.string.NoInternetAccess));
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
                    googleLoginProgress(false);
                    System.out.println(TAG+" "+"log device info="+result);

                    LogDevicePojo logDevicePojo;
                    Gson gson=new Gson();
                    logDevicePojo=gson.fromJson(result,LogDevicePojo.class);

                    switch (logDevicePojo.getCode())
                    {
                        // success
                        case "200" :
                            // Open Home page screen
                            //onBackPressed();
                            Intent intent = new Intent();
                            intent.putExtra("isToRefreshHomePage",true);
                            setResult(VariableConstants.LANDING_REQ_CODE,intent);
                            onBackPressed();
                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // error
                        default:
                            CommonClass.showSnackbarMessage(rL_rootview,logDevicePojo.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    googleLoginProgress(false);
                    CommonClass.showSnackbarMessage(rL_rootview,error);
                }
            });
        }
        else CommonClass.showSnackbarMessage(rL_rootview,getResources().getString(R.string.NoInternetAccess));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        System.out.println(TAG+" "+"onConnectionFailed...");
    }

    /**
     * <h>OpenLoginSignupScreen</h>
     * <p>
     *     In this method we used to launch the LoginOrSignupActivity class.
     * </p>
     * @param type The string value.
     */
    private void openLoginSignupScreen(String type)
    {
        if (isToStartActivity) {
            isToStartActivity = false;
            Intent intent = new Intent(mActivity, LoginOrSignupActivity.class);
            intent.putExtra("type", type);
            startActivityForResult(intent, VariableConstants.LOGIN_SIGNUP_REQ_CODE);
        }
    }

    private void googleLoginProgress(boolean isVisible)
    {
        if (isVisible)
        {
            pBar_googleLogin.setVisibility(View.VISIBLE);
            iV_google_icon.setVisibility(View.GONE);
            tV_googleLogin.setVisibility(View.GONE);
        }
        else {
            pBar_googleLogin.setVisibility(View.GONE);
            iV_google_icon.setVisibility(View.VISIBLE);
            tV_googleLogin.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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

                    if (runTimePermission.checkPermissions(permissionsArray))
                    {
                        if (isFromGplusLocation)
                            getCurrentLocation();
                        else loginWithFacebook.getCurrentLocation();
                    }
                }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
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
            map.put("userImageUrl", "");
        }
        map.put("userIdentifier", email);
        map.put("userId",userId);
        map.put("userName",userName);
        map.put("apiToken",token);
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
