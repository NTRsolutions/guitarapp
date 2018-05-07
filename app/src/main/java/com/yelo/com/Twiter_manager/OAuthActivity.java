package com.yelo.com.Twiter_manager;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
/**
 * <h2>OAuthActivity</h2>
 * <P>
 *
 * </P>
 * @since  8/16/2017.
 */
public class OAuthActivity extends AppCompatActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        String authenticationUrl = getIntent().getStringExtra(TweeterConfig.STRING_EXTRA_AUTHENCATION_URL);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        OAuthWebViewFragment oAuthWebViewFragment = OAuthWebViewFragment.getInstance(authenticationUrl);
        fragmentTransaction.add(android.R.id.content,oAuthWebViewFragment);
        fragmentTransaction.commit();
    }
}

