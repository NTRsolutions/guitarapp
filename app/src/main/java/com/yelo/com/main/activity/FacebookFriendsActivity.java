package com.yelo.com.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.Gson;
import com.yelo.com.R;
import com.yelo.com.adapter.FacebookFriendsRvAdapter;
import com.yelo.com.fcm_push_notification.Config;
import com.yelo.com.fcm_push_notification.NotificationMessageDialog;
import com.yelo.com.fcm_push_notification.NotificationUtils;
import com.yelo.com.pojo_class.facebook_friends_pojo.FacebookFriendsData;
import com.yelo.com.pojo_class.facebook_friends_pojo.FacebookFriendsMain;
import com.yelo.com.utility.ApiUrl;
import com.yelo.com.utility.CommonClass;
import com.yelo.com.utility.OkHttp3Connection;
import com.yelo.com.utility.SessionManager;
import com.yelo.com.utility.VariableConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;

/**
 * <h>FacebookFriendsActivity</h>
 * <p>
 *     This class is called from DiscoverPeopleActivity class. In this class firstly we used to
 *     call facebook graph api and get the facebook id from my facebook friend list who used
 *     this app. After this we used to send the ids to our server and get the complete details
 *     of the user.
 * </p>
 * @since 04-Jul-17
 */
public class FacebookFriendsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = FacebookFriendsActivity.class.getSimpleName();
    private CallbackManager callbackManager;
    private String fbId="";
    private Activity mActivity;
    private SessionManager mSessionManager;
    private RelativeLayout rL_rootElement,rL_friend_count;
    private ProgressBar mProgressBar;
    private TextView tV_friend_count;
    private ArrayList<FacebookFriendsData> arrayListFbFriends;
    private FacebookFriendsRvAdapter phoneContactRvAdapter;
    private NotificationMessageDialog mNotificationMessageDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        facebookSDKInitialize();
        setContentView(R.layout.activity_fb_friends);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);

        initVariables();
    }

    /**
     * <h>InitVariables</h>
     * <p>
     *     In this method we used to initialize all variables.
     * </p>
     */
    private void initVariables()
    {
        // receiving datas from last class
        Intent intent=getIntent();
        int followingCount = intent.getIntExtra("followingCount", 0);
        System.out.println(TAG+" "+"followingCount="+ followingCount);

        mActivity=FacebookFriendsActivity.this;
        mNotificationMessageDialog=new NotificationMessageDialog(mActivity);
        arrayListFbFriends=new ArrayList<>();
        mSessionManager=new SessionManager(mActivity);
        rL_rootElement= (RelativeLayout) findViewById(R.id.rL_rootElement);
        rL_friend_count= (RelativeLayout) findViewById(R.id.rL_friend_count);
        tV_friend_count= (TextView) findViewById(R.id.tV_friend_count);
        mProgressBar= (ProgressBar) findViewById(R.id.progress_bar);
        RecyclerView rV_facebookFriends = (RecyclerView) findViewById(R.id.rV_facebookFriends);
        RelativeLayout rL_back_btn= (RelativeLayout) findViewById(R.id.rL_back_btn);
        rL_back_btn.setOnClickListener(this);
        LoginManager.getInstance().logInWithReadPermissions(mActivity, Collections.singletonList("user_friends"));

        // set adapter
        phoneContactRvAdapter=new FacebookFriendsRvAdapter(mActivity,arrayListFbFriends,followingCount);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(mActivity);
        rV_facebookFriends.setLayoutManager(linearLayoutManager);
        rV_facebookFriends.setAdapter(phoneContactRvAdapter);

        System.out.println(TAG+" "+"is logged in from fb="+isLoggedIn());

        if (isLoggedIn()){
            GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response)
                        {
                            if(response.getError()==null)
                            {
                                try {
                                    parseResponse(response.getJSONObject());
                                } catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }else
                            {

                            }
                        }
                    });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,friends");
            request.setParameters(parameters);
            request.executeAsync();
        }
        else
        {
            // call method
            getLoginDetails();
        }
    }


    private void parseResponse(JSONObject jsonObject) throws Exception
    {
        JSONObject jsonObject1=jsonObject.getJSONObject("friends");
        System.out.println(TAG+" "+"json obj="+jsonObject);
        JSONArray friendListArr=jsonObject1.getJSONArray("data");
        System.out.println(TAG+" "+"friends list array when logged in="+friendListArr.toString());
        getFbFriendsList(friendListArr);
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        return accessToken != null && !accessToken.isExpired();
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
     * Initialize the Facebook callbackManager
     */
    private void facebookSDKInitialize()
    {
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
    }

    /**
     * <h>GetLoginDetails</h>
     * <p>
     *     In this method we used to call facebook graph api and get the Common friends facebook id
     *     who used this app.
     * </p>
     */
    private void getLoginDetails()
    {
        // Callback registration
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult login_result) {
                new GraphRequest(
                        login_result.getAccessToken(),
                        //AccessToken.getCurrentAccessToken(),
                        "/me/friends",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {

                                try {
                                    JSONArray friendListArr = response.getJSONObject().getJSONArray("data");
                                    getFbFriendsList(friendListArr);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                ).executeAsync();
            }

            @Override
            public void onCancel() {
                // code for cancellation
            }

            @Override
            public void onError(FacebookException exception) {
                //  code to handle error
            }
        });
    }

    private void getFbFriendsList(JSONArray friendListArr)
    {
        System.out.println(TAG+" "+"friends list array="+friendListArr.toString());
        int friendListSize=friendListArr.length();

        for (int friendListCount = 0; friendListCount<friendListSize; friendListCount++)
        {
            try {
                System.out.println(TAG+" "+"id="+friendListArr.getJSONObject(friendListCount).getString("id")+" "+"name="+friendListArr.getJSONObject(friendListCount).getString("name"));
                fbId=fbId+","+friendListArr.getJSONObject(friendListCount).getString("id");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        System.out.println(TAG+"id initial="+fbId);

        if (!fbId.isEmpty() && fbId.startsWith(","))
        {
            fbId=fbId.replaceFirst(",","");
            findFbFriendsApi(fbId);
        }

        System.out.println(TAG+"id final="+fbId);
    }

    /**
     * <h>FindFbFriendsApi</h>
     * <p>
     *     In this method we do api call to get the complete information for the
     *     given facebook ids.
     * </p>
     * @param fbId The saved facebook ids
     */
    private void findFbFriendsApi(String fbId)
    {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            mProgressBar.setVisibility(View.VISIBLE);
            JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("facebookId", fbId);
                request_datas.put("token",mSessionManager.getAuthToken());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.FACEBOOK_CONTACT_SYNC, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    mProgressBar.setVisibility(View.GONE);
                    System.out.println(TAG+" "+"find fb friends res="+result);

                    FacebookFriendsMain facebookFriendsMain;
                    Gson gson=new Gson();
                    facebookFriendsMain=gson.fromJson(result,FacebookFriendsMain.class);

                    switch (facebookFriendsMain.getCode())
                    {
                        // success
                        case "200" :
                            if (facebookFriendsMain.getFacebookUsers()!=null && facebookFriendsMain.getFacebookUsers().size()>0)
                            {
                                arrayListFbFriends.addAll(facebookFriendsMain.getFacebookUsers());
                                mSessionManager.setFbFriendCount(arrayListFbFriends.size());
                                // set count
                                rL_friend_count.setVisibility(View.VISIBLE);
                                String setCountText;
                                if (arrayListFbFriends.size()>1)
                                    setCountText=arrayListFbFriends.size()+" "+getResources().getString(R.string.friends_on)+" "+getResources().getString(R.string.app_name);
                                else setCountText=arrayListFbFriends.size()+" "+getResources().getString(R.string.friend_on)+" "+getResources().getString(R.string.app_name);
                                tV_friend_count.setText(setCountText);

                                // Notify recyclerview adapter
                                phoneContactRvAdapter.notifyDataSetChanged();
                            }
                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // error
                        default:
                            CommonClass.showSnackbarMessage(rL_rootElement,facebookFriendsMain.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    mProgressBar.setVisibility(View.GONE);
                    CommonClass.showSnackbarMessage(rL_rootElement,error);
                }
            });
        }
        else CommonClass.showSnackbarMessage(rL_rootElement,getResources().getString(R.string.NoInternetAccess));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.rL_back_btn :
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent();
        intent.putExtra("fb_count",arrayListFbFriends.size());
        intent.putExtra("followingCount",phoneContactRvAdapter.followingCount);
        setResult(VariableConstants.FB_FRIEND_REQ_CODE,intent);
        finish();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }
}
