package com.yelo.com.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.yelo.com.R;
import com.yelo.com.pojo_class.verify_gplus_pojo.VerifyLoginWithGooglePojo;
import com.yelo.com.utility.ApiUrl;
import com.yelo.com.utility.CommonClass;
import com.yelo.com.utility.OkHttp3Connection;
import com.yelo.com.utility.SessionManager;
import com.yelo.com.utility.VariableConstants;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * <h>VerifyLoginWithGplus</h>
 * <p>
 *     In this class we used to do login with google plus and verification with g plus.
 * </p>
 * @since 12-Jul-17.
 */
public class VerifyLoginWithGplus {

    private static final String TAG = VerifyLoginWithGplus.class.getSimpleName();
    private Activity mActivity;
    private GoogleApiClient mGoogleApiClient;
    private View rootView;
    private ProgressBar pBar_gPlusVerify;
    private ImageView iV_gPlusIcon;
    private String gPlusVerified;
    private SessionManager mSessionManager;

    public VerifyLoginWithGplus(Activity mActivity, GoogleApiClient mGoogleApiClient, View rootView, ProgressBar pBar_gPlusVerify, ImageView iV_gPlusIcon,String gPlusVerified) {
        this.mActivity = mActivity;
        this.mGoogleApiClient=mGoogleApiClient;
        this.pBar_gPlusVerify=pBar_gPlusVerify;
        this.iV_gPlusIcon=iV_gPlusIcon;
        this.gPlusVerified=gPlusVerified;
        this.rootView=rootView;
        mSessionManager=new SessionManager(mActivity);
    }

    public void signInWithGoogle(Fragment fragment,Activity mActivity)
    {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        if (fragment!=null)
        fragment.startActivityForResult(signInIntent, VariableConstants.GOOGLE_LOGIN_REQ_CODE);
        else mActivity.startActivityForResult(signInIntent, VariableConstants.GOOGLE_LOGIN_REQ_CODE);
    }

    public void onActivityResult(Intent data) {
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        handleSignInResult(result);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            assert acct != null;
            Log.e(TAG, "display name: " + acct.getDisplayName());

            String googleId = acct.getId();
            String idToken = acct.getIdToken();
            String serverAuthCode = acct.getServerAuthCode();
            emailVerification(serverAuthCode,googleId);
        }
    }

    private void emailVerification(String accessToken,String googleId)
    {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            pBar_gPlusVerify.setVisibility(View.VISIBLE);
            iV_gPlusIcon.setVisibility(View.GONE);
            // token,accessToken,googleId
            JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("token", mSessionManager.getAuthToken());
                request_datas.put("accessToken", accessToken);
                request_datas.put("googleId", googleId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.VERIFY_GOOGLE_PLUS, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    System.out.println(TAG+" "+"email verified res="+result);
                    pBar_gPlusVerify.setVisibility(View.GONE);
                    iV_gPlusIcon.setVisibility(View.VISIBLE);

                    VerifyLoginWithGooglePojo loginWithGooglePojo;
                    Gson gson=new Gson();
                    loginWithGooglePojo=gson.fromJson(result,VerifyLoginWithGooglePojo.class);

                    switch (loginWithGooglePojo.getCode())
                    {
                        // success
                        case "200" :
                            gPlusVerified="1";
                            iV_gPlusIcon.setImageResource(R.drawable.gplus_verified_icon);
                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // error
                        default:
                            gPlusVerified="0";
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    pBar_gPlusVerify.setVisibility(View.GONE);
                    iV_gPlusIcon.setVisibility(View.VISIBLE);
                }
            });
        }
        else CommonClass.showSnackbarMessage(rootView,mActivity.getResources().getString(R.string.NoInternetAccess));
    }
}
