package com.yelo.com.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yelo.com.R;

/**
 * <h>WebViewActivity</h>
 * <p>
 *  In this class we used to open the browser using webview from our custom activity.
 * </p>
 * @since 21-Jul-17
 */
public class WebViewActivity extends AppCompatActivity implements View.OnClickListener
{
    private WebView mWebView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_paypal);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);

        Activity mActivity = WebViewActivity.this;
        mWebView= (WebView) findViewById(R.id.webView);
        mProgressBar= (ProgressBar) findViewById(R.id.progress_bar);

        // receiving datas from last activity
        Intent intent=getIntent();
        String url=intent.getStringExtra("browserLink");
        String actionBarTitle=intent.getStringExtra("actionBarTitle");

        // action bar title
        TextView tV_actionBarTitle= (TextView) findViewById(R.id.tV_actionBarTitle);
        if (actionBarTitle!=null)
        tV_actionBarTitle.setText(actionBarTitle);

        RelativeLayout rL_back_btn= (RelativeLayout) findViewById(R.id.rL_back_btn);
        rL_back_btn.setOnClickListener(this);

        if (url!=null && !url.isEmpty())
        startWebView(url);
    }

    private void startWebView(String url) {

        //Create new webview Client to show progress dialog
        //When opening a url or click on link

        mWebView.setWebViewClient(new WebViewClient() {

            //If you will not use this method url links are opeen in new brower not in webview
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            //Show loader on url load
            public void onLoadResource (WebView view, String url) {
                mProgressBar.setVisibility(View.VISIBLE);
            }
            public void onPageFinished(WebView view, String url) {
                mProgressBar.setVisibility(View.GONE);
            }

        });

        // Javascript inabled on webview
        mWebView.getSettings().setJavaScriptEnabled(true);

        //Load url in webview
        mWebView.loadUrl(url);


    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
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
}
