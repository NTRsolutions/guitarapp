package com.demskigroup.guitaramps.main.activity;

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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
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
import com.demskigroup.guitaramps.utility.SessionManager;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * <h>ReportUserActivity</h>
 * <p>
 *     In this class we used to report for any illegal user.
 * </p>
 * @since 27-Jul-17
 */
public class ReportUserActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = ReportUserActivity.class.getSimpleName();
    private Activity mActivity;
    private SessionManager mSessionManager;
    private RelativeLayout rL_rootElement;
    private ArrayList<ReportProductDatas> arrayListReportItem;
    private ProgressBar mProgressBar,pBar_postReport;
    private String userImage="",userName="",userFullName="";
    private RecyclerView rV_reportUser;
    private RelativeLayout rL_submit;
    private TextView tV_submit;
    private NotificationMessageDialog mNotificationMessageDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_user);
        overridePendingTransition(R.anim.slide_up, R.anim.stay );

        initVariables();
    }

    /**
     * <h>InitVariables</h>
     * <p>
     *     In this method we used to initialize all variables
     * </p>
     */
    private void initVariables()
    {
        mActivity=ReportUserActivity.this;
        mNotificationMessageDialog=new NotificationMessageDialog(mActivity);
        arrayListReportItem=new ArrayList<>();
        rV_reportUser= (RecyclerView) findViewById(R.id.rV_reportUser);
        mSessionManager=new SessionManager(mActivity);
        rL_rootElement= (RelativeLayout) findViewById(R.id.rL_rootElement);
        mProgressBar= (ProgressBar) findViewById(R.id.progress_bar_reportUser);
        pBar_postReport= (ProgressBar) findViewById(R.id.pBar_postReport);
        rL_submit= (RelativeLayout) findViewById(R.id.rL_submit);
        rL_submit.setVisibility(View.GONE);
        rL_submit.setOnClickListener(this);
        tV_submit= (TextView) findViewById(R.id.tV_submit);

        // receiving data from last class
        Intent intent=getIntent();
        userImage=intent.getStringExtra("userImage");
        userName=intent.getStringExtra("userName");
        userFullName=intent.getStringExtra("userFullName");

        RelativeLayout rL_back_btn= (RelativeLayout) findViewById(R.id.rL_back_btn);
        rL_back_btn.setOnClickListener(this);

        getReportReasonApi();
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
     * <h>GetReportReasonApi</h>
     * <p>
     *     In this method we used to do api call to get the reasons to report user.
     * </p>
     */
    private void getReportReasonApi()
    {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            String url = ApiUrl.REPORT_USER_REASON + "?token=" + mSessionManager.getAuthToken();

            OkHttp3Connection.doOkHttp3Connection(TAG, url, OkHttp3Connection.Request_type.GET, new JSONObject(), new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag)
                {
                    System.out.println(TAG+" "+"get report reason res="+result);

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
                                rV_reportUser.setLayoutManager(linearLayoutManager);
                                rV_reportUser.setAdapter(reportItemRvAdapter);
                            }
                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;

                        //error
                        default:
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

    /**
     * <h>ReportUserApi</h>
     * <p>
     *     In this method we used to do api call to report user and pass the report id and description
     *     to the server.
     * </p>
     * @param reason The reason id
     * @param description The description.
     */
    private void reportUserApi(final String reason, String description)
    {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            tV_submit.setVisibility(View.GONE);
            pBar_postReport.setVisibility(View.VISIBLE);

            // token, reportedUser, reason
            JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("token", mSessionManager.getAuthToken());
                request_datas.put("reportedUser", userName);
                request_datas.put("reason", reason);
                request_datas.put("description", description);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.REPORT_USER, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                   System.out.println(TAG+"report user res="+result);

                    tV_submit.setVisibility(View.VISIBLE);
                    pBar_postReport.setVisibility(View.GONE);

                    Gson gson=new Gson();
                    PostItemReportPojo itemReportPojo=gson.fromJson(result,PostItemReportPojo.class);

                    switch (itemReportPojo.getCode())
                    {
                        // success
                        case "200" :
                            CommonClass.showSuccessSnackbarMsg(rL_rootElement,getResources().getString(R.string.the_user_report_has_been));

                            final Timer t = new Timer();
                            t.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    // when the task active then close the activity
                                    t.cancel();
                                    onBackPressed();
                                }
                            }, 3000);
                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // already reported
                        case "9477" :
                            CommonClass.showSnackbarMessage(rL_rootElement,itemReportPojo.getMessage());
                            break;

                        // error
                        default:
                            CommonClass.showSnackbarMessage(rL_rootElement,itemReportPojo.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    CommonClass.showSnackbarMessage(rL_rootElement,error);
                    tV_submit.setVisibility(View.VISIBLE);
                    pBar_postReport.setVisibility(View.GONE);
                }
            });
        }
        else CommonClass.showSnackbarMessage(rL_rootElement,getResources().getString(R.string.NoInternetAccess));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
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
                            reportUserApi(reportProductDatas.get_id(),reportProductDatas.getReportReasonByUser());
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
    }
}
