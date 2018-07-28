package com.demskigroup.guitaramps.main.activity;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.adapter.ViewPagerAdapter;
import com.demskigroup.guitaramps.fcm_push_notification.Config;
import com.demskigroup.guitaramps.fcm_push_notification.NotificationMessageDialog;
import com.demskigroup.guitaramps.fcm_push_notification.NotificationUtils;
import com.demskigroup.guitaramps.main.view_pager.search_product.PeoplesFrag;
import com.demskigroup.guitaramps.main.view_pager.search_product.PostsFrag;
import com.demskigroup.guitaramps.utility.CommonClass;

/**
 * <h>SearchProductActivity</h>
 * <p>
 *     This class is called from HomePage Frag class. In this class we used to set two
 *     tab first one is Posts Tab to search a product from the given post from data base
 *     and second tab is to search the registered user.
 * </p>
 * @since 18-May-17
 */
public class SearchProductActivity extends AppCompatActivity implements View.OnClickListener
{
    public EditText eT_search_users;
    private static final String TAG=SearchProductActivity.class.getSimpleName();
    public String postText="",peopleText="";
    private NotificationMessageDialog mNotificationMessageDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_product);
        overridePendingTransition(R.anim.slide_up, R.anim.stay );

        initVariables();
    }



    /**
     * <h>initVariables</h>
     * <p>
     *     This method is being called from onCreate() of the same class. In this
     *     method we used to initliaze all variables.
     * </p>
     */
    private void initVariables()
    {
        Activity mActivity = SearchProductActivity.this;
        mNotificationMessageDialog=new NotificationMessageDialog(mActivity);
        CommonClass.statusBarColor(mActivity);

        RelativeLayout rL_close= (RelativeLayout) findViewById(R.id.rL_close);
        rL_close.setOnClickListener(this);
        eT_search_users= (EditText) findViewById(R.id.eT_search_users);
        eT_search_users.setHint(getResources().getString(R.string.search_post));

        ViewPager viewpager= (ViewPager)findViewById(R.id.viewpager);
        TabLayout tabs= (TabLayout)findViewById(R.id.tabs);
        setupViewPager(viewpager);
        tabs.setupWithViewPager(viewpager);

        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position)
            {
                System.out.println(TAG+" "+"position="+position);
                if (position==0)
                {
                    eT_search_users.setHint(getResources().getString(R.string.search_post));
                    eT_search_users.setText(postText);
                }
                else
                {
                    eT_search_users.setHint(getResources().getString(R.string.search_people));
                    eT_search_users.setText(peopleText);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

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

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PostsFrag(),getResources().getString(R.string.posts));
        adapter.addFragment(new PeoplesFrag(),getResources().getString(R.string.people));
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed()
    {
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.rL_close :
                onBackPressed();
                break;
        }
    }

}
