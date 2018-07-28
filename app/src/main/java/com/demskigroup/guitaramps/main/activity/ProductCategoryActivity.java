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
import com.google.gson.Gson;
import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.adapter.ProductCategoryRvAdapter;
import com.demskigroup.guitaramps.fcm_push_notification.Config;
import com.demskigroup.guitaramps.fcm_push_notification.NotificationMessageDialog;
import com.demskigroup.guitaramps.fcm_push_notification.NotificationUtils;
import com.demskigroup.guitaramps.pojo_class.product_category.ProductCategoryMainPojo;
import com.demskigroup.guitaramps.pojo_class.product_category.ProductCategoryResDatas;
import com.demskigroup.guitaramps.utility.ApiUrl;
import com.demskigroup.guitaramps.utility.ClickListener;
import com.demskigroup.guitaramps.utility.CommonClass;
import com.demskigroup.guitaramps.utility.OkHttp3Connection;
import com.demskigroup.guitaramps.utility.VariableConstants;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * <h>ProductCategoryActivity</h>
 * <p>
 *     This class is getting called from PostProductActivity class. In this
 *     class we used to show the list of product categories in RecyclerView.
 *     Once user clicks on any category from that list we send back that
 *     product category to previous class.
 * </p>
 * @since 2017-05-04
 * @version 1.0
 * @author 3Embed
 */
public class ProductCategoryActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = ProductCategoryActivity.class.getSimpleName();
    private ProgressBar progress_bar;
    private Activity mActivity;
    private RelativeLayout rL_rootview;
    private RecyclerView rV_category;
    private NotificationMessageDialog mNotificationMessageDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);

        mActivity=ProductCategoryActivity.this;
        mNotificationMessageDialog=new NotificationMessageDialog(mActivity);
        CommonClass.statusBarColor(mActivity);
        progress_bar= (ProgressBar) findViewById(R.id.progress_bar);
        rL_rootview= (RelativeLayout) findViewById(R.id.rL_rootview);
        rV_category= (RecyclerView) findViewById(R.id.rV_category);
        RelativeLayout rL_back_btn = (RelativeLayout) findViewById(R.id.rL_back_btn);
        rL_back_btn.setOnClickListener(this);

        getCategoriesService();
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
     * <h>GetCategoriesService</h>
     * <p>
     *     This method is called from onCreate() method of the current class.
     *     In this method we used to call the getCategories api using okHttp3.
     *     Once we get the data we show that list in recyclerview.
     * </p>
     */
    private void getCategoriesService()
    {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            progress_bar.setVisibility(View.VISIBLE);
            JSONObject request_datas = new JSONObject();

            try {
                request_datas.put("token", "20");
                request_datas.put("offset", "0");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.GET_CATEGORIES, OkHttp3Connection.Request_type.GET, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    progress_bar.setVisibility(View.GONE);

                    System.out.println(TAG+" "+"get category res="+result);

                    ProductCategoryMainPojo categoryMainPojo;
                    Gson gson=new Gson();
                    categoryMainPojo=gson.fromJson(result,ProductCategoryMainPojo.class);

                    switch (categoryMainPojo.getCode())
                    {

                        // success
                        case "200" :
                            final ArrayList<ProductCategoryResDatas> aL_categoryDatas=categoryMainPojo.getData();
                            if (aL_categoryDatas!=null && aL_categoryDatas.size()>0)
                            {
                                ProductCategoryRvAdapter categoryRvAdapter=new ProductCategoryRvAdapter(mActivity,aL_categoryDatas);
                                LinearLayoutManager layoutManager=new LinearLayoutManager(mActivity);

                                rV_category.setLayoutManager(layoutManager);
                                rV_category.setAdapter(categoryRvAdapter);

                                categoryRvAdapter.setOnItemClick(new ClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {
                                        String categoryName=aL_categoryDatas.get(position).getName();
                                        if (categoryName!=null && !categoryName.isEmpty())
                                        {
                                            categoryName=categoryName.substring(0,1).toUpperCase()+categoryName.substring(1).toLowerCase();
                                            Intent intent=new Intent();
                                            intent.putExtra("categoryName",categoryName);
                                            setResult(VariableConstants.CATEGORY_REQUEST_CODE,intent);
                                            onBackPressed();
                                            finish();
                                        }
                                    }
                                });

                            }
                            break;

                        // Error
                        default:
                            CommonClass.showSnackbarMessage(rL_rootview,categoryMainPojo.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    progress_bar.setVisibility(View.GONE);
                    CommonClass.showSnackbarMessage(rL_rootview,error);
                }
            });
        }
        else CommonClass.showSnackbarMessage(rL_rootview,getResources().getString(R.string.NoInternetAccess));
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
