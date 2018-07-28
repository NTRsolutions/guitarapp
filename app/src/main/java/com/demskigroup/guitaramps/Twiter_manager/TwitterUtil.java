package com.demskigroup.guitaramps.Twiter_manager;

import android.content.Intent;
import android.net.Uri;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
/**
 * @since  8/16/2017.
 */
class TwitterUtil
{
    private RequestToken requestToken = null;
    private TwitterFactory twitterFactory = null;
    private Twitter twitter;

    private TwitterUtil()
    {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setOAuthConsumerKey(TweeterConfig.TWITTER_CONSUMER_KEY);
        configurationBuilder.setOAuthConsumerSecret(TweeterConfig.TWITTER_CONSUMER_SECRET);
        Configuration configuration = configurationBuilder.build();
        twitterFactory = new TwitterFactory(configuration);
        twitter = twitterFactory.getInstance();


    }

    public TwitterFactory getTwitterFactory()
    {
        return twitterFactory;
    }

     void setTwitterFactory(AccessToken accessToken)
    {
        twitter = twitterFactory.getInstance();
    }

     Twitter getTwitter()
    {
        return twitter;
    }

     RequestToken getRequestToken()
    {
        if (requestToken == null)
        {
            try {
                requestToken = twitterFactory.getInstance().getOAuthRequestToken(TweeterConfig.TWITTER_CALLBACK_URL);


            } catch (TwitterException e)
            {
                e.printStackTrace();
            }
        }
        return requestToken;
    }

    static TwitterUtil instance = new TwitterUtil();
    public static TwitterUtil getInstance() {
        return instance;
    }


    public void logout()
    {
        twitter.setOAuthAccessToken(null);
        reset();
    }

    public void reset() {
        instance = new TwitterUtil();
    }
}
