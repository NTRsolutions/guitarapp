package com.yelo.com.Twiter_manager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.yelo.com.R;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
/**
 * <h2>OAuthWebViewFragment</h2>
 * <p>
 *
 * </p>
 * @since  8/16/2017.
 */
 public class OAuthWebViewFragment extends Fragment
{
    private WebView webView;
    private String authenticationUrl;
    private Activity mactivity;
    private TweeterSession tweeterSession;

    public static OAuthWebViewFragment getInstance(String authenticationUrl)
    {
        OAuthWebViewFragment temp=new OAuthWebViewFragment();
        temp.authenticationUrl= authenticationUrl;
        return temp;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        mactivity=getActivity();
        tweeterSession=new TweeterSession(mactivity);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.oauth_webview,container,false);
        webView = (WebView)view.findViewById(R.id.webViewOAuth);
        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        webView.loadUrl(authenticationUrl);
        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                if (url.contains("oauth_verifier="))
                {
                    initControl(Uri.parse(url));
                }else if(url.contains("denied="))
                {
                   if(mactivity!=null)
                   mactivity.finish();
                }else
                {
                    view.loadUrl(url);
                }
                return true;
            }
        });
        WebSettings webSettings= webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }

    /*
     * */
    private void initControl( Uri uri)
    {
        if (uri != null && uri.toString().startsWith(TweeterConfig.TWITTER_CALLBACK_URL))
        {
            String verifier = uri.getQueryParameter(TweeterConfig.URL_PARAMETER_TWITTER_OAUTH_VERIFIER);
            new TwitterGetAccessTokenTask().execute(verifier);
        } else
            new TwitterGetAccessTokenTask().execute("");
    }

    /**
     * */
    private class TwitterGetAccessTokenTask extends AsyncTask<String, String, String>
    {
        @Override
        protected void onPostExecute(String userName)
        {
            tweeterSession.setAuthenticatedAppName(userName);
            Intent intent=new Intent();
            Bundle data=new Bundle();
            data.putBoolean("ISLOGIN",true);
            intent.putExtras(data);
            if(mactivity!=null)
            {
                mactivity.setResult(Activity.RESULT_OK,intent);
                mactivity.finish();
            }
        }

        @Override
        protected String doInBackground(String... params)
        {
            Twitter twitter = TwitterUtil.getInstance().getTwitter();
            RequestToken requestToken = TwitterUtil.getInstance().getRequestToken();
            if (!StringUtil.isNullOrWhitespace(params[0]))
            {
                try
                {
                    AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, params[0]);
                    tweeterSession.setTweeterLoginOAUTH_Token(accessToken.getToken());
                    tweeterSession.setTweeterLoginOAUTH_Token_Secret(accessToken.getTokenSecret());
                    tweeterSession.setTweeterLoginStatus(true);
                    return twitter.showUser(accessToken.getUserId()).getName();
                } catch (TwitterException e)
                {
                    e.printStackTrace();
                }
            } else {

                String accessTokenString =tweeterSession.getTweeterLoginOAUTH_Token();
                String accessTokenSecret =tweeterSession.getTweeterLoginOAUTH_Token_Secret();
                AccessToken accessToken = new AccessToken(accessTokenString, accessTokenSecret);
                try
                {
                    TwitterUtil.getInstance().setTwitterFactory(accessToken);
                    return TwitterUtil.getInstance().getTwitter().showUser(accessToken.getUserId()).getName();
                } catch (TwitterException e)
                {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}

