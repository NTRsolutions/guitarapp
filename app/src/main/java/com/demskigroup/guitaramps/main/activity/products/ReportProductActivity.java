package com.demskigroup.guitaramps.main.activity.products;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.adapter.ReportItemRvAdapter;
import com.demskigroup.guitaramps.fcm_push_notification.Config;
import com.demskigroup.guitaramps.fcm_push_notification.NotificationMessageDialog;
import com.demskigroup.guitaramps.fcm_push_notification.NotificationUtils;
import com.demskigroup.guitaramps.pojo_class.PostItemReportPojo;
import com.demskigroup.guitaramps.pojo_class.report_product_pojo.ReportProductDatas;
import com.demskigroup.guitaramps.pojo_class.report_product_pojo.ReportProductMain;
import com.demskigroup.guitaramps.utility.ApiUrl;
import com.demskigroup.guitaramps.utility.CommonClass;
import com.demskigroup.guitaramps.utility.OkHttp3Connection;
import com.demskigroup.guitaramps.utility.RoundedCornersTransform;
import com.demskigroup.guitaramps.utility.SessionManager;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * <h>ReportProductActivity</h>
 * <p>
 *     In this class we used to show the list of report reason. Once
 *     user will click on any item we show one optional box to write
 *     his own comment. then once we click on submit. then it will
 *     be submitted and current screen i.e activity will be finished.
 * </p>
 * @since 06-Jun-17
 * @version 1.0
 * @author 3Embed
 */
public class ReportProductActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = ReportProductActivity.class.getSimpleName();
    private Activity mActivity;
    private SessionManager mSessionManager;
    private RelativeLayout rL_rootElement;
    private ProgressBar mProgressBar,pBar_postReport;
    private RecyclerView rV_reportProduct;
    private String postId="";
    private RelativeLayout rL_submit;
    private ArrayList<ReportProductDatas> arrayListReportItem;
    private TextView tV_submit;
    private NotificationMessageDialog mNotificationMessageDialog;
    private Timer timer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_product);
        overridePendingTransition(R.anim.slide_up, R.anim.stay );

        initVar();
    }

    /**
     * <h>initVar</h>
     * <p>
     *     In this method we used to initialize all variables
     * </p>
     */
    private void initVar()
    {
        mActivity=ReportProductActivity.this;
        timer = new Timer();
        mNotificationMessageDialog=new NotificationMessageDialog(mActivity);
        arrayListReportItem=new ArrayList<>();
        rV_reportProduct= (RecyclerView) findViewById(R.id.rV_reportProduct);
        mSessionManager=new SessionManager(mActivity);
        rL_rootElement= (RelativeLayout) findViewById(R.id.rL_rootElement);
        mProgressBar= (ProgressBar) findViewById(R.id.progress_bar_reportItem);
        pBar_postReport= (ProgressBar) findViewById(R.id.pBar_postReport);
        rL_submit= (RelativeLayout) findViewById(R.id.rL_submit);
        rL_submit.setVisibility(View.GONE);
        rL_submit.setOnClickListener(this);
        tV_submit= (TextView) findViewById(R.id.tV_submit);

        // receiving data from last class
        Intent intent=getIntent();
        String product_image,product_name,sold_by_name;
        product_image=intent.getStringExtra("product_image");
        product_name=intent.getStringExtra("product_name");
        sold_by_name=intent.getStringExtra("sold_by_name");
        postId=intent.getStringExtra("postId");

        // Product image
        ImageView iV_productImage= (ImageView)findViewById(R.id.iV_productImage);
        iV_productImage.getLayoutParams().width= CommonClass.getDeviceWidth(mActivity)/5;
        iV_productImage.getLayoutParams().height= CommonClass.getDeviceWidth(mActivity)/5;
        if (product_image!=null && !product_image.isEmpty())
            Picasso.with(mActivity)
                    .load(product_image)
                    .transform(new RoundedCornersTransform())
                    .placeholder(R.drawable.default_image)
                    .error(R.drawable.default_image)
                    .into(iV_productImage);

        // product name
        TextView tV_productname= (TextView)findViewById(R.id.tV_productname);
        if (product_name!=null)
            tV_productname.setText(product_name);

        // sold by name
        TextView tV_posted_by= (TextView)findViewById(R.id.tV_posted_by);
        if (sold_by_name!=null)
            tV_posted_by.setText(sold_by_name);

        RelativeLayout rL_back_btn= (RelativeLayout) findViewById(R.id.rL_back_btn);
        rL_back_btn.setOnClickListener(this);

        getReportReasonAPi();
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
     * <h>GetReportReasonAPi</h>
     * <p>
     *     In this method we used to do api call to get all report reasons. Once
     *     we get than show that in recyclerview.
     * </p>
     */
    private void getReportReasonAPi()
    {
        if (CommonClass.isNetworkAvailable(mActivity))
        {
            mProgressBar.setVisibility(View.VISIBLE);

            String postReportReasonUrl=""+ApiUrl.REPORT_POST_REASON+"?token="+mSessionManager.getAuthToken();
            OkHttp3Connection.doOkHttp3Connection(TAG, postReportReasonUrl, OkHttp3Connection.Request_type.GET, new JSONObject(), new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    mProgressBar.setVisibility(View.GONE);
                    System.out.println(TAG+" "+"report post reason res="+result);

                    Gson gson=new Gson();
                    ReportProductMain reportProductMain=gson.fromJson(result,ReportProductMain.class);

                    switch (reportProductMain.getCode())
                    {
                        // success
                        case "200" :
                            arrayListReportItem.addAll(reportProductMain.getData());
                            if (arrayListReportItem!=null && arrayListReportItem.size()>0)
                            {
                                rL_submit.setVisibility(View.VISIBLE);
                                ReportItemRvAdapter reportItemRvAdapter=new ReportItemRvAdapter(mActivity,arrayListReportItem);
                                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(mActivity);
                                rV_reportProduct.setLayoutManager(linearLayoutManager);
                                rV_reportProduct.setAdapter(reportItemRvAdapter);
                            }
                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // error
                        default:
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    mProgressBar.setVisibility(View.GONE);
                }
            });
        }
        else CommonClass.showSnackbarMessage(rL_rootElement,getResources().getString(R.string.NoInternetAccess));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            // back button
            case R.id.rL_back_btn :
                onBackPressed();
                break;

            // submit
            case R.id.rL_submit :
                if (arrayListReportItem.size()>0)
                {
                    for (ReportProductDatas reportProductDatas : arrayListReportItem)
                    {
                        if (reportProductDatas.isItemSelected())
                        {
                            System.out.println(TAG+" "+"user report reason="+reportProductDatas.getReportReasonByUser());
                            reportPostApi(reportProductDatas.get_id(),reportProductDatas.getReportReasonByUser());
                        }
                    }
                }
                break;
        }
    }

    /**
     * <h>reportPostApi</h>
     * <p>
     *     In this method we used to post the report to the server.
     * </p>
     * @param reasonId The reasonId which we get in getReason api.
     * @param reasonByUser The reason written by user
     */
    private void reportPostApi(String reasonId,String reasonByUser)
    {
        //token, postId, reason
        if (CommonClass.isNetworkAvailable(mActivity)) {
            tV_submit.setVisibility(View.GONE);
            pBar_postReport.setVisibility(View.VISIBLE);
            JSONObject requestDatas = new JSONObject();
            try {
                requestDatas.put("token", mSessionManager.getAuthToken());
                requestDatas.put("postId",postId);
                requestDatas.put("reasonId",reasonId);
                requestDatas.put("membername",mSessionManager.getUserName());
                requestDatas.put("description",reasonByUser);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.REPORT_POST, OkHttp3Connection.Request_type.POST, requestDatas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    System.out.println(TAG + " " + "report reason=" + result);
                    tV_submit.setVisibility(View.VISIBLE);
                    pBar_postReport.setVisibility(View.GONE);

                    Gson gson=new Gson();
                    PostItemReportPojo itemReportPojo=gson.fromJson(result,PostItemReportPojo.class);

                    switch (itemReportPojo.getCode())
                    {
                        // success
                        case "200" :
                            CommonClass.showSuccessSnackbarMsg(rL_rootElement,getResources().getString(R.string.the_product_report_has_been));

                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    // when the task active then close the activity
                                    timer.cancel();
                                    onBackPressed();
                                }
                            }, 3000);
                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // already reported
                        case "409" :
                            CommonClass.showSnackbarMessage(rL_rootElement,getResources().getString(R.string.the_report_is_already_reported));
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    // when the task active then close the activity
                                    timer.cancel();
                                    onBackPressed();
                                }
                            }, 3000);
                            break;

                        // error
                        default:
                            CommonClass.showSnackbarMessage(rL_rootElement,itemReportPojo.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {

                }
            });
        }
        else CommonClass.showSnackbarMessage(rL_rootElement,getResources().getString(R.string.NoInternetAccess));
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
    }
}
