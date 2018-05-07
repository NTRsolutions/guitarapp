package com.yelo.com.main.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yelo.com.R;
import com.yelo.com.fcm_push_notification.Config;
import com.yelo.com.fcm_push_notification.NotificationMessageDialog;
import com.yelo.com.fcm_push_notification.NotificationUtils;
import com.yelo.com.pojo_class.get_total_clicks_pojo.TotalClicksDatas;
import com.yelo.com.pojo_class.get_total_clicks_pojo.TotalClicksMainPojo;
import com.yelo.com.utility.ApiUrl;
import com.yelo.com.utility.CommonClass;
import com.yelo.com.utility.OkHttp3Connection;
import com.yelo.com.utility.SessionManager;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;

/**
 * <h>GetTotalClicksActivity</h>
 * <p>
 *     This class is called from InsightActivity. In this class we used to show the list of city and its clicks from
 *     the particular county.
 * </p>
 * @since 22-Aug-17
 * @version 1.0
 * @author 3Embed
 */
public class GetTotalClicksActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = GetTotalClicksActivity.class.getSimpleName();
    private Activity mActivity;
    private SessionManager mSessionManager;
    private String postId;
    private String countrySname;
    private LinearLayout linear_rootElement,linear_location;
    private NotificationMessageDialog mNotificationMessageDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_clicks);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);

        // Receiving datas from last class
        Intent intent=getIntent();
        postId = intent.getStringExtra("postId");        countrySname=intent.getStringExtra("countrySname");


        mActivity=GetTotalClicksActivity.this;
        mNotificationMessageDialog=new NotificationMessageDialog(mActivity);
        mSessionManager=new SessionManager(mActivity);

        initVariables();
        getCountriesTotalClick();
    }

    /**
     * <h>initVariables</h>
     * <p>
     *     In this method we used to assign the xml variables
     * </p>
     */
    private void initVariables()
    {
        // set country name
        TextView tV_country_name= (TextView) findViewById(R.id.tV_country_name);
        if (countrySname!=null && !countrySname.isEmpty())
        {
            Locale loc = new Locale("",countrySname);
            String setCountryName=getResources().getString(R.string.clicks_from)+" "+loc.getDisplayCountry();
            tV_country_name.setText(setCountryName);
        }
        RelativeLayout rL_back_btn= (RelativeLayout) findViewById(R.id.rL_back_btn);
        rL_back_btn.setOnClickListener(this);
        linear_rootElement= (LinearLayout) findViewById(R.id.linear_rootElement);
        linear_location= (LinearLayout) findViewById(R.id.linear_location);
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
     * <h>getCountriesTotalClick</h>
     * <p>
     *     In this method we used to do api call to get list of cities and its clicks
     *     for any particular county.
     * </p>
     */
    private void getCountriesTotalClick()
    {
        if (mSessionManager.getIsUserLoggedIn()) {
            JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("token", mSessionManager.getAuthToken());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String URL= ApiUrl.INSIGHT+"/"+postId+"/"+countrySname;

            OkHttp3Connection.doOkHttp3Connection(TAG, URL, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    System.out.println(TAG+" "+"total click res="+result);

                   Gson gson = new Gson();
                    Type listType = new TypeToken<ArrayList<TotalClicksMainPojo>>(){}.getType();
                    ArrayList<TotalClicksMainPojo> arrayListTotalClicks = gson.fromJson(result, listType);

                    System.out.println(TAG+" "+"posts code="+arrayListTotalClicks.get(0).getCode());

                    if (arrayListTotalClicks.size()>0)
                    {
                        ArrayList<TotalClicksDatas> arrayListClicksData=arrayListTotalClicks.get(0).getData();
                        if (arrayListClicksData!=null && arrayListClicksData.size()>0)
                        {
                            inflateLocationInsight(arrayListClicksData);
                        }
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    CommonClass.showSnackbarMessage(linear_rootElement,error);
                }
            });
        }
        else CommonClass.showSnackbarMessage(linear_rootElement,getResources().getString(R.string.NoInternetAccess));
    }

    /**
     * <h>InflateLocationInsight</h>
     * <p>
     *     In this method we used to inflate the list of city and its total clicks.
     * </p>
     * @param arrayListLocation The list containing the cities and its click
     */
    private void inflateLocationInsight(ArrayList<TotalClicksDatas> arrayListLocation)
    {
        final LayoutInflater inflater= (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        linear_location.removeAllViews();
        for (int locCount=0;locCount<arrayListLocation.size();locCount++)
        {
            View view = inflater.inflate(R.layout.single_row_insight_location, null);
            TextView tV_country_name= (TextView) view.findViewById(R.id.tV_country_name);
            TextView tV_totalViews= (TextView) view.findViewById(R.id.tV_totalViews);
            final String cityName=arrayListLocation.get(locCount).getCity();
            String totalViews=arrayListLocation.get(locCount).getCount();
            ImageView iV_array= (ImageView) view.findViewById(R.id.iV_array);
            iV_array.setVisibility(View.GONE);

            // set country name
            if (cityName!=null && !cityName.isEmpty())
            {
                tV_country_name.setText(cityName);
            }

            // set total reviews
            if (totalViews!=null && !totalViews.isEmpty())
                tV_totalViews.setText(totalViews);

            // click on view
            final int finalLocCount = locCount;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    System.out.println(TAG+" "+"pos="+ finalLocCount);
                    Intent intent=new Intent(mActivity,GetTotalClicksActivity.class);
                    intent.putExtra("postId",postId);
                    intent.putExtra("countrySname",countrySname);
                    startActivity(intent);
                }
            });
            linear_location.addView(view);
        }
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
