package com.yelo.com.main.activity.products;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yelo.com.R;
import com.yelo.com.adapter.ProductImagePagerAdapter;
import com.yelo.com.fcm_push_notification.Config;
import com.yelo.com.fcm_push_notification.NotificationMessageDialog;
import com.yelo.com.fcm_push_notification.NotificationUtils;
import com.yelo.com.swipe_to_finish.SwipeBackActivity;
import com.yelo.com.swipe_to_finish.SwipeBackLayout;

import java.util.ArrayList;

/**
 * <h>ProductImagesActivity</h>
 * <p>
 *     This class is called from ProductDetailsActivity class. In this class
 *     we used to show the multiple images of the product using viewpager.
 * </p>
 * @since 02-Jun-17
 * @version 1.0
 * @author 3Embed
 */
public class ProductImagesActivity extends SwipeBackActivity
{
    private static final String TAG = ProductImagesActivity.class.getSimpleName();
    private TextView tV_current_page;
    private NotificationMessageDialog mNotificationMessageDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_images);
        setDragEdge(SwipeBackLayout.DragEdge.TOP);

        Intent intent=getIntent();
        ArrayList<String> aL_multipleImages = (ArrayList<String>) intent.getSerializableExtra("imagesArrayList");
        String[] images_array = aL_multipleImages.toArray(new String[0]);

        Activity mActivity = ProductImagesActivity.this;
        mNotificationMessageDialog=new NotificationMessageDialog(mActivity);
        ViewPager view_pager = (ViewPager) findViewById(R.id.view_pager);
        tV_current_page= (TextView) findViewById(R.id.tV_current_page);
        TextView tV_total_pages = (TextView) findViewById(R.id.tV_total_pages);
        tV_total_pages.setText(String.valueOf(images_array.length));

        ProductImagePagerAdapter productImagePagerAdapter = new ProductImagePagerAdapter(mActivity, images_array);
        view_pager.setAdapter(productImagePagerAdapter);
        view_pager.setCurrentItem(0);
        System.out.println(TAG+" "+"current page="+ view_pager.getCurrentItem());
        String setCurrentPage=String.valueOf(view_pager.getCurrentItem()+1);
        tV_current_page.setText(setCurrentPage);

        view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.i(TAG, "page selected " + position);
                String setCurrentPage=String.valueOf(position+1);
                tV_current_page.setText(setCurrentPage);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // close
        RelativeLayout rL_close= (RelativeLayout) findViewById(R.id.rL_close);
        rL_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
}
