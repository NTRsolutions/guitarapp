package com.demskigroup.guitaramps.Face_book_manger;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.Set;
/**
 * <h1>Facebook_session_manager</h1>
 * <P>
 *     Managing the app facebook login.
 * </P>
 * @since  18/11/16.
 */

public class Facebook_session_manager
{
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME="Facebook_data";
    private static final String ACESS_TOKEN="accessToken";
    private static final String APPLICATION_ID="applicationId";
    private static final String USER_ID="userId";
    private static final String PERMISSION="permissions";
    private static final String DECLINE_PERMISSION="declinedPermissions";
    private static final String ACESS_TOKEN_SOURCE="accessTokenSource";
    private static final String EXP_TIME="expirationTime";
    private static final String LAST_REF_TIME="lastRefreshTime";
    private static final String FACEBOOK_TOKEN="facebook_token";


    public Facebook_session_manager(Context context)
    {
        sharedPreferences=context.getSharedPreferences(PREF_NAME,0);
    }

    public void clear_session()
    {
        sharedPreferences.edit().clear().apply();
    }

    public String getAcessToken()
    {
        return sharedPreferences.getString(ACESS_TOKEN,"");
    }

    public void setFacebookToken(String token)
    {
        sharedPreferences.edit().putString(FACEBOOK_TOKEN,token).apply();
    }

    public String getFacebookToken()
    {
        return sharedPreferences.getString(FACEBOOK_TOKEN,"");
    }

    public void setAcessToken(String acess_token)
    {
        sharedPreferences.edit().putString(ACESS_TOKEN,acess_token).apply();
    }

    public  String getApplicationId()
    {
        return sharedPreferences.getString(APPLICATION_ID,"");
    }

    public void setApplicationId(String applicationId)
    {
        sharedPreferences.edit().putString(APPLICATION_ID,applicationId).apply();
    }


    public String getUserId()
    {
        return sharedPreferences.getString(USER_ID,"");
    }

    public void setUserId(String user_id)
    {
        sharedPreferences.edit().putString(USER_ID,user_id).apply();
    }

    public Set<String> getPermission()
    {
        return sharedPreferences.getStringSet(PERMISSION,null);
    }

    public void setPermission(Set<String> permission_list)
    {
        sharedPreferences.edit().putStringSet(PERMISSION,permission_list).apply();
    }


    public  Set<String> getDeclinePermission()
    {
        return sharedPreferences.getStringSet(DECLINE_PERMISSION,null);
    }

    public void setDeclinePermission(Set<String> dec_permission_list)
    {
        sharedPreferences.edit().putStringSet(DECLINE_PERMISSION,dec_permission_list).apply();
    }



    public String getAcessTokenSource()
    {
        return sharedPreferences.getString(ACESS_TOKEN_SOURCE,"");
    }

    public void setAcessTokenSource(String acess_token_tag)
    {
        sharedPreferences.edit().putString(ACESS_TOKEN_SOURCE,acess_token_tag).apply();
    }

    public  String getExpTime()
    {
        return sharedPreferences.getString(EXP_TIME,"");
    }

    public void setExpTime(String time_data)
    {
        sharedPreferences.edit().putString(EXP_TIME,time_data).apply();
    }


    public String getLastRefTime()
    {
        return sharedPreferences.getString(LAST_REF_TIME,"");
    }

    public void setLastRefTime(String last_time)
    {
        sharedPreferences.edit().putString(LAST_REF_TIME,last_time).apply();
    }


}
