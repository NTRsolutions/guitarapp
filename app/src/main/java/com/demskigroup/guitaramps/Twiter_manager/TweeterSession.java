package com.demskigroup.guitaramps.Twiter_manager;

import android.content.Context;
import android.content.SharedPreferences;
/**
 * @since  8/16/2017.
 * @author 3Embed.
 * @version 1.0.
 */
class TweeterSession
{
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public TweeterSession (Context context)
    {
        String PICOGRAM_TITLE = "Tweeter";
        /*
          Mod is private.*/
        int PREFERENCE_MODE = 0;
        sharedPreferences=context.getSharedPreferences(PICOGRAM_TITLE, PREFERENCE_MODE);
        editor=sharedPreferences.edit();
        editor.commit();
    }

    public void clear()
    {
        editor.clear();
        editor.commit();
    }

    public boolean getTweeterLoginStatus()
    {
        return sharedPreferences.getBoolean(TweeterConfig.PREFERENCE_TWITTER_IS_LOGGED_IN,false);
    }

    public void setTweeterLoginStatus(boolean login_status)
    {
        editor.putBoolean(TweeterConfig.PREFERENCE_TWITTER_IS_LOGGED_IN,login_status);
        editor.commit();
    }

    public String getTweeterLoginOAUTH_Token()
    {
        return sharedPreferences.getString(TweeterConfig.PREFERENCE_TWITTER_OAUTH_TOKEN,"");
    }

    public void setTweeterLoginOAUTH_Token(String token)
    {
        editor.putString(TweeterConfig.PREFERENCE_TWITTER_OAUTH_TOKEN,token);
        editor.commit();
    }


    public String getTweeterLoginOAUTH_Token_Secret()
    {
        return sharedPreferences.getString(TweeterConfig.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET,"");
    }


    public void setTweeterLoginOAUTH_Token_Secret(String token)
    {
        editor.putString(TweeterConfig.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET,token);
        editor.commit();
    }

    public String getAuthenticatedAppName()
    {
        return sharedPreferences.getString("APPNAME","");
    }

    public void setAuthenticatedAppName(String app_name)
    {
        editor.putString("APPNAME", app_name);
        editor.commit();
    }

}
