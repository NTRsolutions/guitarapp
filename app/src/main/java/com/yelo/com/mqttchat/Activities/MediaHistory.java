package com.yelo.com.mqttchat.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.yelo.com.R;
import com.yelo.com.mqttchat.Fragments.MediaHistory_Received;
import com.yelo.com.mqttchat.Fragments.MediaHistory_Sent;
import com.yelo.com.mqttchat.Utilities.ZoomOutPageTransformer;
import java.util.ArrayList;
import java.util.List;


/*
*
* Activity containing the fragmnents for the media sent and received respectively
* */
public class MediaHistory extends AppCompatActivity

{
/*
 * Activity to show history of media shared along with option of switching between list and grid view
 */


    public static String docId;


    private ViewPager viewPager;
    private TabLayout tabLayout;

    @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        supportRequestWindowFeature(AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);


        setContentView(R.layout.media_history);


        RelativeLayout toolbar = (RelativeLayout) findViewById(R.id.toolbar);


        toolbar.setVisibility(View.VISIBLE);

        View v2 = findViewById(R.id.seperator2);
        v2.setVisibility(View.GONE);


        TextView title = (TextView) findViewById(R.id.title);


        title.setText(R.string.MediaHistory);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        setupActivity(getIntent());

        ImageView close = (ImageView) findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                onBackPressed();

            }
        });


    }


    public String getDocId() {
        return docId;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());


        adapter.addFrag(new MediaHistory_Received(), getString(R.string.Received));
        adapter.addFrag(new MediaHistory_Sent(), getString(R.string.Sent));


        viewPager.setAdapter(adapter);


        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }


        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    @Override
    public void onBackPressed() {


        super.onBackPressed();
        supportFinishAfterTransition();


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();


        Glide.get(this).clearMemory();
        Glide.get(this).getBitmapPool().clearMemory();


    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setupActivity(intent);
    }


    private void setupActivity(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {


            docId = extras.getString("docId");
        }

        setupViewPager(viewPager);


        if (tabLayout != null)
            tabLayout.setupWithViewPager(viewPager);
    }


}