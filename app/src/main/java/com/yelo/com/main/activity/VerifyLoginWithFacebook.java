package com.yelo.com.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.yelo.com.R;
import com.yelo.com.pojo_class.verify_fb_login_pojo.VerifyFacebookPojo;
import com.yelo.com.utility.ApiUrl;
import com.yelo.com.utility.CommonClass;
import com.yelo.com.utility.OkHttp3Connection;
import com.yelo.com.utility.SessionManager;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Arrays;

/**
 * <h>VerifyLoginWithFacebook</h>
 * <p>
 *     This class is called from ProfileFrag class. In this class we used to
 *     login with facebook. Once we get success then retrieve access token
 *     and call verify facebook login api and verify facebook button.
 * </p>
 * @since 11-Jul-17
 */
public class VerifyLoginWithFacebook
{
    private static final String TAG=VerifyLoginWithFacebook.class.getSimpleName();
    private Activity mActivity;
    private SessionManager mSessionManager;
    private CallbackManager callbackManager;
    private View rootView;
    private ProgressBar pBar_fbVerify;
    private ImageView iV_fbicon;
    private String facebookVerified;

    public VerifyLoginWithFacebook(Activity mActivity, View rootView, ProgressBar pBar_fbVerify,ImageView iV_fbicon,String facebookVerified) {
        this.mActivity = mActivity;
        mSessionManager=new SessionManager(mActivity);
        FacebookSdk.sdkInitialize(mActivity);
        callbackManager = CallbackManager.Factory.create();
        this.rootView=rootView;
        this.pBar_fbVerify=pBar_fbVerify;
        this.iV_fbicon=iV_fbicon;
        this.facebookVerified=facebookVerified;
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
            public void onSuccess(final LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("LoginActivity", response.toString());
                        String fb_accessToken;

                        if (response.getError() == null) {
                            loginResult.getAccessToken().getToken();
                            if (loginResult.getAccessToken().getToken()!=null) {
                                fb_accessToken = loginResult.getAccessToken().getToken();
                                if (fb_accessToken!=null && !fb_accessToken.isEmpty())
                                {
                                    verifyFacebookAccApi(fb_accessToken);
                                }
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
                CommonClass.showSnackbarMessage(rootView,mActivity.getResources().getString(R.string.Loginfailed));
            }

            @Override
            public void onError(FacebookException error) {
                CommonClass.showSnackbarMessage(rootView, error.toString());
                if (error instanceof FacebookAuthorizationException) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut();
                    }
                }
            }
        });
    }

    /**
     * <h>verifyFacebookAccApi</h>
     * <p>
     *     This method is called from onSuccess of facebook login graph api.
     *     We used to pass access token and call our local server to verify
     *     facebook button from my profile.
     * </p>
     * @param accessToken The token from facebook api response
     */
    private void verifyFacebookAccApi(String accessToken)
    {
        if (CommonClass.isNetworkAvailable(mActivity))
        {
            pBar_fbVerify.setVisibility(View.VISIBLE);
            iV_fbicon.setVisibility(View.GONE);
            JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("token", mSessionManager.getAuthToken());
                request_datas.put("accessToken", accessToken);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.VERIFY_FACEBOOK_LOGIN, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    System.out.println(TAG+" "+"verifyFacebookAcc res="+result);
                    pBar_fbVerify.setVisibility(View.GONE);
                    iV_fbicon.setVisibility(View.VISIBLE);

                    VerifyFacebookPojo verifyFacebookPojo;
                    Gson gson=new Gson();
                    verifyFacebookPojo=gson.fromJson(result,VerifyFacebookPojo.class);

                    switch (verifyFacebookPojo.getCode())
                    {
                        case "200":
                            facebookVerified="1";
                            iV_fbicon.setImageResource(R.drawable.facebook_verified_icon);
                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;


                        default:
                            facebookVerified="0";
                            CommonClass.showSnackbarMessage(rootView,verifyFacebookPojo.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    facebookVerified="0";
                    pBar_fbVerify.setVisibility(View.GONE);
                    iV_fbicon.setVisibility(View.VISIBLE);
                }
            });
        }
        else CommonClass.showSnackbarMessage(rootView,mActivity.getResources().getString(R.string.NoInternetAccess));
    }

    void fbOnActivityResult(int requestCode, int resultCode, Intent data)
    {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        System.out.println(TAG+" "+"profile callbackManager called...");
    }

    public void loginWithFbWithSdk()
    {
        if (CommonClass.isNetworkAvailable(mActivity))
            LoginManager.getInstance().logInWithReadPermissions(mActivity, Arrays.asList("public_profile", "user_friends","email"));
        else
            CommonClass.showSnackbarMessage(rootView,mActivity.getResources().getString(R.string.NoInternetAccess));
    }

}
