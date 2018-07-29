package com.demskigroup.guitaramps.Face_book_manger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
/**
 *<h>Facebook_login</h>
 * <P>
 *     Class contain a async task to get the data from facebook .
 *     Contains a method to do facebook login .
 *     Here doing facebook login and taking data as user_friends .
 * </P>
 * @author 3Embed
 * @since  4/02/2016
 */
public class Facebook_login
{
    private Activity mactivity;
    private boolean isReady=false;
    private Facebook_session_manager facebook_session_manager;
    private Facebook_callback callback=null;

    public Facebook_login(Activity activity)
    {
        FacebookSdk.sdkInitialize(activity.getApplicationContext(),new FacebookSdk.InitializeCallback()
        {
            @Override
            public void onInitialized()
            {
                isReady = true;
            }
        });
         mactivity = activity;
        facebook_session_manager=new Facebook_session_manager(mactivity);
    }
    /**
     * <h2>faceBook_Login</h2>
     * <P>
     *     Facebook login data from user.
     * </P>*/
    public void faceBook_Login(CallbackManager callbackmanager, final Facebook_callback facebook_callback)
    {
        callback=facebook_callback;
        Refresh_Token();
        LoginManager.getInstance().logInWithReadPermissions(mactivity, Collections.singletonList("email"));
        LoginManager.getInstance().registerCallback(callbackmanager,new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                /*
                 * Storing all the data in session manger .*/
                AccessToken accessToken=loginResult.getAccessToken();
                store_Acess_token_details(accessToken);
                /*
                 * Giving the call back for success logged in.*/
                if(callback!=null)
                    callback.success(facebook_session_manager.getUserId());
            }
            @Override
            public void onCancel()
            {
                if(callback!=null)
                    callback.cancel("User canceled !");
            }

            @Override
            public void onError(FacebookException error)
            {
                if(callback!=null)
                    callback.error("Got some error!");
            }
        });
    }

    /**
     * <h2>get_FB_Friends</h2>
     * <P>
     *     Fb login status.
     * </P>
     * @param callbackmanager call back managers.
     * @param facebook_callback user interface manager.
     */
    public void get_FB_Friends(CallbackManager callbackmanager,Facebook_callback facebook_callback)
    {
        callback=facebook_callback;
        if(isLoggedIn())
        {
            fb_ReadPermission(callbackmanager,new String[]{"user_friends"});
        }else
        {
            first_d_login(callbackmanager,new String[]{"user_friends"},true);
        }
    }
    /**
     * <h2>fbLogin_status_for_permission</h2>
     * <P>
     *     Fb login status.
     * </P>
     * @param callbackmanager call back managers.
     * @param facebook_callback user interface manager.
     * @param permission_array checking for the  permission result.*/
    public void  ask_PublishPermission(CallbackManager callbackmanager,String permission_array[],final Facebook_callback facebook_callback)
    {
        callback=facebook_callback;
        if(isLoggedIn())
        {
            fb_PublishPermission(callbackmanager,permission_array);
        }else
        {
            first_d_login(callbackmanager,permission_array,false);
        }
    }



    /**
     * <h2>first_d_login</h2>
     * <P>
     *     Fb login status.
     * </P>
     * @param callbackmanager call back managers.
     * @param permission_array checking for the  permission result.*/
    private void first_d_login(final CallbackManager callbackmanager, final String permission_array[], final boolean isRedPermission)
    {
        Refresh_Token();
        LoginManager.getInstance().logInWithReadPermissions(mactivity,Collections.singletonList("email"));
        LoginManager.getInstance().registerCallback(callbackmanager,new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                AccessToken accessToken=loginResult.getAccessToken();
                store_Acess_token_details(accessToken);
                if(isRedPermission)
                {
                    fb_ReadPermission(callbackmanager,permission_array);
                }else
                {
                    fb_PublishPermission(callbackmanager,permission_array);
                }
            }
            @Override
            public void onCancel()
            {
                if(callback!=null)
                callback.error("User canceled!");
            }

            @Override
            public void onError(FacebookException error)
            {
                Log.d("user2","Canceled data details");
                if(callback!=null)
                callback.error("Got some error!");
            }
        });
    }
    /**
     * <h2>fb_PublishPermission</h2>
     * <P>
     *
     * </P>*/
    private void fb_ReadPermission(CallbackManager callbackmanager,String permission_array[])
    {
        if(verify_Permission(Arrays.asList(permission_array)).size()==0)
        {
            if(callback!=null)
                callback.success(facebook_session_manager.getUserId());
            return;
        }
        LoginManager.getInstance().logInWithReadPermissions(mactivity, Arrays.asList(permission_array));
        LoginManager.getInstance().registerCallback(callbackmanager,new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                /*
                 * Giving the call back for sucess logged in.*/
                if(callback!=null)
                    callback.success(facebook_session_manager.getUserId());
            }
            @Override
            public void onCancel()
            {
                if(callback!=null)
                    callback.cancel("User canceled !");
            }
            @Override
            public void onError(FacebookException error)
            {
                if(callback!=null)
                    callback.error("Got some error!");
            }
        });
    }

    /**
     * <h2>fb_PublishPermission</h2>
     * <P>
     *
     * </P>*/
    private void fb_PublishPermission(CallbackManager callbackmanager,String permission_array[])
    {
        if(verify_Permission(Arrays.asList(permission_array)).size()==0)
        {
            if(callback!=null)
            callback.success(facebook_session_manager.getUserId());
            return;
        }
        LoginManager.getInstance().logInWithPublishPermissions(mactivity, Arrays.asList(permission_array));
        LoginManager.getInstance().registerCallback(callbackmanager,new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                /*
                 * Giving the call back for sucess logged in.*/
                if(callback!=null)
               callback.success(facebook_session_manager.getUserId());
            }
            @Override
            public void onCancel()
            {
                if(callback!=null)
                callback.cancel("User canceled !");
            }
            @Override
            public void onError(FacebookException error)
            {
                if(callback!=null)
                callback.error("Got some error!");
            }
        });
    }
    /**
     * <h2>verify_Permission</h2>
     * <P>
     *   Checking the permission of the data.
     * </P>
     * @param permissions contains the token error.*/
    private ArrayList<String> verify_Permission(List<String> permissions)
    {
        ArrayList<String> temp=new ArrayList<>();
        Set<String> granted_list=AccessToken.getCurrentAccessToken().getPermissions();
        for(String check_test:permissions)
        {
            boolean isFound=false;
            for(String permissioon : granted_list)
            {
                if(check_test.equals(permissioon))
                {
                    isFound=true;
                    break;
                }
            }
            if(!isFound)
            {
                temp.add(check_test);
            }
        }
        return temp;
    }


    /*
    * Storing the facebook token and other details.*/
    private void store_Acess_token_details(AccessToken accessToken)
    {
        facebook_session_manager.setFacebookToken(accessToken.getToken());
        facebook_session_manager.setAcessToken(accessToken.getToken());
        facebook_session_manager.setApplicationId(accessToken.getApplicationId());
        facebook_session_manager.setUserId(accessToken.getUserId());
        facebook_session_manager.setPermission(accessToken.getPermissions());
        facebook_session_manager.setDeclinePermission(accessToken.getDeclinedPermissions());

        switch (accessToken.getSource())
        {
            case NONE:
            {
                facebook_session_manager.setAcessTokenSource(Facebook_common_constance.None);
                break;
            }
            case FACEBOOK_APPLICATION_WEB:
            {
                facebook_session_manager.setAcessTokenSource(Facebook_common_constance.Facebook_app_web);
                break;
            }
            case FACEBOOK_APPLICATION_NATIVE:
            {
                facebook_session_manager.setAcessTokenSource(Facebook_common_constance.Facebook_app_native);
                break;
            }
            case FACEBOOK_APPLICATION_SERVICE:
            {
                facebook_session_manager.setAcessTokenSource(Facebook_common_constance.Facebook_app_server);
                break;
            }
            case WEB_VIEW:
            {
                facebook_session_manager.setAcessTokenSource(Facebook_common_constance.web_view);
                break;
            }
            case TEST_USER:
            {
                facebook_session_manager.setAcessTokenSource(Facebook_common_constance.test_user);
                break;
            }
            case CLIENT_TOKEN:
            {
                facebook_session_manager.setAcessTokenSource(Facebook_common_constance.client_token);
                break;
            }
        }
        @SuppressLint("SimpleDateFormat") DateFormat formatter=new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        facebook_session_manager.setExpTime(formatter.format(accessToken.getExpires()));
        facebook_session_manager.setLastRefTime(formatter.format(accessToken.getLastRefresh()));
    }


    /**
     * <h>Facebook_callback</h>
     * <P>
     *     Calback interface of facebook.
     * </P>
     */
    public interface Facebook_callback
    {
        void success(String id);

        void error(String error);

        void cancel(String cancel);
    }

    /**
     * <h>Refresh_Token</h>
     * <P>
     *     Calback interface of facebook.
     * </P>
     */
    private void Refresh_Token()
    {
        if(isReady)
        {
            LoginManager.getInstance().logOut();
            /*
             * Refreshing the acess token of the Facebook*/
            AccessToken.refreshCurrentAccessTokenAsync();
        }
    }

    public boolean isLoggedIn()
    {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }
}
