package com.demskigroup.guitaramps.main.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.fcm_push_notification.Config;
import com.demskigroup.guitaramps.fcm_push_notification.NotificationMessageDialog;
import com.demskigroup.guitaramps.fcm_push_notification.NotificationUtils;
import com.demskigroup.guitaramps.pojo_class.insight_pojo.BasicInsight;
import com.demskigroup.guitaramps.pojo_class.insight_pojo.BasicInsightData;
import com.demskigroup.guitaramps.pojo_class.insight_pojo.InsightMainPojo;
import com.demskigroup.guitaramps.pojo_class.insight_pojo.LocationInsight;
import com.demskigroup.guitaramps.pojo_class.insight_pojo.LocationInsightData;
import com.demskigroup.guitaramps.pojo_class.insight_pojo.TimeInsight;
import com.demskigroup.guitaramps.utility.ApiUrl;
import com.demskigroup.guitaramps.utility.CommonClass;
import com.demskigroup.guitaramps.utility.OkHttp3Connection;
import com.demskigroup.guitaramps.utility.SessionManager;
import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import static com.demskigroup.guitaramps.utility.VariableConstants.MONTH;
import static com.demskigroup.guitaramps.utility.VariableConstants.WEEK;
import static com.demskigroup.guitaramps.utility.VariableConstants.YEAR;

/**
 * <h>InsightActivity</h>
 * <p>
 *     In this class we used to show the graphical represation for the user
 *     post total clicks week, month or year wise.
 * </p>
 * @since 21-Aug-17
 */
public class InsightActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = InsightActivity.class.getSimpleName();
    private TextView tV_postedOn,tV_unique_click,tV_reviews,tV_total_click,tV_saved;
    private Activity mActivity;
    private String postId="";
    private LinearLayout linear_rootElement;
    private SessionManager mSessionManager;
    private LinearLayout linear_location;
    private RelativeLayout rL_duration;
    private TextView tV_duration;
    private NotificationMessageDialog mNotificationMessageDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insight);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);

        // receive data from last activity
        Intent intent=getIntent();
        postId=intent.getStringExtra("postId");

        mActivity = InsightActivity.this;
        mNotificationMessageDialog=new NotificationMessageDialog(mActivity);
        mSessionManager=new SessionManager(mActivity);
        linear_rootElement= (LinearLayout) findViewById(R.id.linear_rootElement);
        initVariables();

        // Call get insight api
        getInsight(WEEK);
    }

    private void initVariables()
    {
        tV_postedOn= (TextView) findViewById(R.id.tV_postedOn);
        tV_unique_click= (TextView) findViewById(R.id.tV_unique_click);
        tV_reviews= (TextView) findViewById(R.id.tV_reviews);
        tV_total_click= (TextView) findViewById(R.id.tV_total_click);
        tV_saved= (TextView) findViewById(R.id.tV_saved);
        linear_location=(LinearLayout)findViewById(R.id.linear_location);
        rL_duration= (RelativeLayout) findViewById(R.id.rL_duration);
        rL_duration.setOnClickListener(this);
        tV_duration= (TextView) findViewById(R.id.tV_duration);

        //back button
        RelativeLayout rL_back_btn= (RelativeLayout) findViewById(R.id.rL_back_btn);
        rL_back_btn.setOnClickListener(this);
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
     * <h>GetInsight</h>
     * <p>
     *     In this method we used to do api call and pass values like week, month and year.
     * </p>
     * @param durationType The string value like week, month and year
     */
    private void getInsight(String durationType)
    {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            // token, postId, durationType
            JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("token", mSessionManager.getAuthToken());
                request_datas.put("postId", postId);
                request_datas.put("durationType", durationType);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.INSIGHT, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    System.out.println(TAG + " " + "insight response=" + result);

                    InsightMainPojo insightMainPojo;
                    Gson gson = new Gson();
                    insightMainPojo = gson.fromJson(result,InsightMainPojo.class);

                    switch (insightMainPojo.getCode())
                    {
                        // success
                        case "200" :
                            if (insightMainPojo.getData()!=null && insightMainPojo.getData().size()>0)
                            {
                                // type 1 i.e basic insight
                                BasicInsight basicInsight = insightMainPojo.getData().get(0).getBasicInsight();
                                if (basicInsight != null)
                                {
                                    ArrayList<BasicInsightData> aL_basicInsightDatas=basicInsight.getData();
                                    if (aL_basicInsightDatas!=null && aL_basicInsightDatas.size()>0)
                                    {
                                        String totalViews, distinctViews, commented, likes, postedOn;
                                        BasicInsightData basicInsightData=aL_basicInsightDatas.get(0);
                                        totalViews=basicInsightData.getTotalViews();
                                        distinctViews=basicInsightData.getDistinctViews();
                                        commented=basicInsightData.getCommented();
                                        likes=basicInsightData.getLikes();
                                        postedOn=basicInsightData.getPostedOn();

                                        // posted on
                                        if (postedOn!=null && !postedOn.isEmpty())
                                        {
                                            System.out.println(TAG+" "+"posted on="+CommonClass.getDate(postedOn));
                                            postedOn=getResources().getString(R.string.posted_on)+" "+CommonClass.getDate(postedOn);
                                            tV_postedOn.setText(postedOn);
                                        }

                                        // Unique click
                                        if (distinctViews!=null && !distinctViews.isEmpty())
                                            tV_unique_click.setText(distinctViews);

                                        // Comments
                                        if (commented!=null && !commented.isEmpty())
                                            tV_reviews.setText(commented);

                                        // total reviews
                                        if (totalViews!=null && !totalViews.isEmpty())
                                            tV_total_click.setText(totalViews);

                                        // likes
                                        if (likes!=null && !likes.isEmpty())
                                            tV_saved.setText(likes);
                                    }
                                }

                                // type 2 i.e Time Insight
                                TimeInsight timeInsight= insightMainPojo.getData().get(1).getTimeInsight();
                                if (timeInsight!=null)
                                {
                                    ArrayList<Integer> aL_count=timeInsight.getData().getCount();
                                    ArrayList<String> al_day=timeInsight.getData().getDay();

                                    openChart(al_day,aL_count);
                                }

                                // set location insight
                                LocationInsight locationInsight=insightMainPojo.getData().get(2).getLocationInsight();
                                if (locationInsight!=null)
                                {
                                    ArrayList<LocationInsightData> arrayListLocation=locationInsight.getData();
                                    if (arrayListLocation!=null && arrayListLocation.size()>0)
                                    {
                                        inflateLocationInsight(arrayListLocation);
                                    }
                                }
                            }
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {

                }
            });
        }
        else CommonClass.showSnackbarMessage(linear_rootElement,getResources().getString(R.string.NoInternetAccess));
    }

    public void popup() {
        PopupMenu popup = new PopupMenu(mActivity, rL_duration); //the v is the view that you click replace it with your menuitem like : menu.getItem(1)
        popup.getMenuInflater().inflate(R.menu.insight_duration_menu, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item2)
            {
                switch (item2.getItemId())
                {
                    // This week
                    case R.id.item_week:
                        tV_duration.setText(getResources().getString(R.string.this_week));
                        getInsight(WEEK);
                        break;

                    // This month
                    case R.id.item_month:
                        tV_duration.setText(getResources().getString(R.string.this_month));
                        getInsight(MONTH);
                        break;

                    // This year
                    case R.id.item_year :
                        tV_duration.setText(getResources().getString(R.string.this_year));
                        getInsight(YEAR);
                        break;
                }
                return true;
            }
        });
    }

    private void inflateLocationInsight(ArrayList<LocationInsightData> arrayListLocation)
    {
        final LayoutInflater inflater= (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        linear_location.removeAllViews();
        for (int locCount=0;locCount<arrayListLocation.size();locCount++)
        {
            View view = inflater.inflate(R.layout.single_row_insight_location, null);
            TextView tV_country_name= (TextView) view.findViewById(R.id.tV_country_name);
            TextView tV_totalViews= (TextView) view.findViewById(R.id.tV_totalViews);
            final String countrySname=arrayListLocation.get(locCount).getCountrySname();
            String totalViews=arrayListLocation.get(locCount).getTotalViews();

            // set country name
            if (countrySname!=null && !countrySname.isEmpty())
            {
                Locale loc = new Locale("",countrySname);
                //locale.getCountry();
                tV_country_name.setText(loc.getDisplayCountry());
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

    private void openChart(ArrayList<String> arrayList_day, ArrayList<Integer> aL_count)
    {
        // Creating an XYSeries for Expense
        XYSeries expenseSeries = new XYSeries(getResources().getString(R.string.click));

        // set count
        for(int i=0;i<aL_count.size();i++){
            expenseSeries.add(i,aL_count.get(i));
        }

        System.out.println(TAG+" "+"max n0="+ Collections.max(aL_count));

        // Creating a dataset to hold  series
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

        // Adding Income Series to the dataset
        dataset.addSeries(expenseSeries);

        // Creating XYSeriesRenderer to customize expenseSeries
        XYSeriesRenderer expenseRenderer = new XYSeriesRenderer();
        expenseRenderer.setColor(ContextCompat.getColor(mActivity,R.color.purple_color)); //color of the graph set to cyan
        expenseRenderer.setFillPoints(true);
        expenseRenderer.setLineWidth(100);
        expenseRenderer.setDisplayChartValues(true);
        expenseRenderer.setDisplayChartValues(true);

        // Creating a XYMultipleSeriesRenderer to customize the whole chart
        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
        multiRenderer.setOrientation(XYMultipleSeriesRenderer.Orientation.HORIZONTAL);
        multiRenderer.setXLabels(0);
        multiRenderer.setXLabelsColor(ContextCompat.getColor(mActivity,R.color.text_color));
        multiRenderer.setYLabelsColor(0,ContextCompat.getColor(mActivity,R.color.text_color));
        multiRenderer.setLabelsColor(ContextCompat.getColor(mActivity,R.color.text_color));
        //multiRenderer.setYTitle(getResources().getString(R.string.click));

        /***
         * Customizing graphs
         */
        //setting text size of the axis title
        multiRenderer.setAxisTitleTextSize(30);

        //setting text size of the graph lable
        multiRenderer.setLabelsTextSize(30);
        multiRenderer.setLabelsColor(ContextCompat.getColor(mActivity,R.color.purple_color));

        //setting zoom buttons visiblity
        multiRenderer.setZoomButtonsVisible(true);

        //setting pan enablity which uses graph to move on both axis
        multiRenderer.setPanEnabled(false, false);

        //setting click false on graph
        multiRenderer.setClickEnabled(false);

        //setting zoom to false on both axis
        multiRenderer.setZoomEnabled(false, false);

        //setting lines to display on y axis
        multiRenderer.setShowGridY(false);

        //setting lines to display on x axis
        multiRenderer.setShowGridX(false);

        //setting legend to fit the screen size
        multiRenderer.setFitLegend(true);

        //setting displaying line on grid
        multiRenderer.setShowGrid(false);

        //setting zoom to false
        multiRenderer.setZoomEnabled(false);

        //setting external zoom functions to false
        multiRenderer.setExternalZoomEnabled(false);

        //setting displaying lines on graph to be formatted(like using graphics)
        multiRenderer.setAntialiasing(true);

        //setting to in scroll to false
        multiRenderer.setInScroll(false);

        //setting to set legend height of the graph
        multiRenderer.setLegendHeight(50);

        //setting x axis label align
        multiRenderer.setXLabelsAlign(Paint.Align.LEFT);

        //setting y axis label to align
        multiRenderer.setYLabelsAlign(Paint.Align.LEFT);

        //setting text style
        multiRenderer.setTextTypeface("sans_serif", Typeface.NORMAL);

        //setting no of values to display in y axis
        multiRenderer.setYLabels(10);

        // setting y axis max value, Since i'm using static values inside the graph so i'm setting y max value to 4000.
        // if you use dynamic values then get the max y value and set here
        multiRenderer.setYAxisMax(Collections.max(aL_count));

        //setting used to move the graph on xaxiz to .5 to the right
        multiRenderer.setXAxisMin(-0.5);

        //setting max values to be display in x axis
        multiRenderer.setXAxisMax(arrayList_day.size());

        //setting bar size or space between two bars
        multiRenderer.setBarSpacing(2);

        //Setting background color of the graph to transparent
        multiRenderer.setBackgroundColor(Color.TRANSPARENT);

        //Setting margin color of the graph to transparent
        multiRenderer.setMarginsColor(ContextCompat.getColor(mActivity,R.color.transparent_background));
        multiRenderer.setApplyBackgroundColor(true);

        //setting the margin size for the graph in the order top, left, bottom, right
        multiRenderer.setMargins(new int[]{30, 30, 30, 30});

        // set day, week or month
        for(int i=0; i< arrayList_day.size();i++){
            multiRenderer.addXTextLabel(i, arrayList_day.get(i));
        }

        // Adding expenseRenderer to multipleRenderer
        multiRenderer.addSeriesRenderer(expenseRenderer);


        //this part is used to display graph on the xml
        LinearLayout chartContainer = (LinearLayout) findViewById(R.id.chart_container);

        //remove any views before u paint the chart
        chartContainer.removeAllViews();

        //drawing bar chart
        View chart = ChartFactory.getBarChartView(mActivity, dataset, multiRenderer, BarChart.Type.DEFAULT);

        //adding the view to the linearlayout
        chartContainer.addView(chart);
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
            // back press
            case R.id.rL_back_btn :
                onBackPressed();
                break;

            // menu
            case R.id.rL_duration :
                popup();
                break;
        }
    }
}
