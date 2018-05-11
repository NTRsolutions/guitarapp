package com.yelo.com.Twiter_manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
/**
 * <h2>TweetManger</h2>
 * @author 3Embed.
 * @since  8/16/2017.
 * @version 1.0.
 */
public class TweetManger
{
    private TweeterSession tweeterSession;
    private static TweetManger manger;
    private TweetManger(Context context,String tweeterkey,String tweeterSecrest)
    {
        tweeterSession=new TweeterSession(context);
        TweeterConfig.TWITTER_CONSUMER_KEY=tweeterkey;
        TweeterConfig.TWITTER_CONSUMER_SECRET=tweeterSecrest;

    }

    public static void initialization(Context context,String key,String secret)
    {
        manger=new TweetManger(context,key,secret);
    }

    public static TweetManger getInstance()
    {
        if(manger==null)
        {

        }
        return manger;
    }

    public void loggedOut()
    {
        TwitterUtil.getInstance().logout();
        tweeterSession.clear();
    }

    public boolean isUserLoggedIn()
    {
        return tweeterSession.getTweeterLoginStatus();
    }

    public void doLogin(final int requestCode, final Activity activity, final Fragment fragment)
    {
        TwitterUtil.getInstance().reset();
        new AsyncTask<String,String,RequestToken>()
        {
            @Override
            protected void onPostExecute(RequestToken requestToken)
            {
                if(requestToken != null) {
                    Intent intent = new Intent( activity.getApplicationContext(), OAuthActivity.class );

                    intent.putExtra( TweeterConfig.STRING_EXTRA_AUTHENCATION_URL, requestToken.getAuthenticationURL() );
                    if (fragment != null) {
                        fragment.startActivityForResult( intent, requestCode );
                    } else {
                        activity.startActivityForResult( intent, requestCode );
                    }
                }else {
                    Toast.makeText( activity, "Twitter request token null", Toast.LENGTH_SHORT ).show();
                }
            }
            @Override
            protected RequestToken doInBackground(String... params)
            {
                return TwitterUtil.getInstance().getRequestToken();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    public void updateStatus(String caption,String status, final TweetSuccess callback)
    {
        new AsyncTask<String,String,Boolean>()
        {
            @Override
            protected Boolean doInBackground(String... params)
            {

                try {


                    String tiny_url=getTinnyUrl(params[0]);
                    String caption=params[1];
                    /*
                     * got the tinny url and the*/
                    String accessTokenString =tweeterSession.getTweeterLoginOAUTH_Token();
                    String accessTokenSecret =tweeterSession.getTweeterLoginOAUTH_Token_Secret();
                    if (!StringUtil.isNullOrWhitespace(accessTokenString) && !StringUtil.isNullOrWhitespace(accessTokenSecret)) {
                        AccessToken accessToken = new AccessToken(accessTokenString, accessTokenSecret);
                        twitter4j.Status status = TwitterUtil.getInstance().getTwitterFactory().getInstance(accessToken).updateStatus(""+caption+" "+tiny_url);
                        Log.d("dfs",""+status.getRetweetedStatus());

                        return true;
                    }

                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result)
            {
                if(callback!=null)
                {
                    if (result)
                        callback.onSuccess();
                    else
                        callback.onFailed();
                }

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,status,caption);
    }


    private String getTinnyUrl(String url_string) throws Exception
    {
        String result = null;
        StringBuilder sb = new StringBuilder();
        String tinny_url="http://tinyurl.com/api-create.php?url="+url_string;
        URL url = new URL(tinny_url);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try
        {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
            br.close();
            in.close();
            result = sb.toString();
        } finally {
            urlConnection.disconnect();

        }
        return result;
    }
    /**
     * Tweeter tweet success.*/
    public interface TweetSuccess
    {
        void onSuccess();

        void onFailed();
    }
}
