package com.demskigroup.guitaramps.mqttchat.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.otto.Subscribe;
import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.mqttchat.AppController;
import com.demskigroup.guitaramps.mqttchat.Fragments.SealingFragment;
import com.demskigroup.guitaramps.mqttchat.Fragments.BuyingFragment;
import com.demskigroup.guitaramps.mqttchat.SetupProfile.OwnProfileDetails;
import com.demskigroup.guitaramps.mqttchat.Utilities.ApiOnServer;
import com.demskigroup.guitaramps.mqttchat.Utilities.MqttEvents;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * @since  26/07/17.
 */
public class LandingPage extends AppCompatActivity implements View.OnClickListener {

    private ViewPagerAdapter adapter;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;
    private FloatingActionButton fab, fab1, fab2;


    private Boolean isFabOpen = false;
    private RelativeLayout overlay, root;
    private TextView tv1, tv2;


    @Override

    public void onCreate(Bundle savedInstanceState) {

        supportRequestWindowFeature(AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);


        setContentView(R.layout.landing_page);


        root = (RelativeLayout) findViewById(R.id.root);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.bottomNavigation);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPagerHome);
        setupViewPager(viewPager);


        tabLayout.setupWithViewPager(viewPager);
        int count = tabLayout.getTabCount();
        for (int i = 0; i < count; i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            try {
                tab.setCustomView(adapter.getTabView(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        TabLayout.Tab currentTab = tabLayout.getTabAt(0);
        if (currentTab != null) {
            View customView = currentTab.getCustomView();
            if (customView != null) {
                customView.setSelected(true);
            }
            currentTab.select();
        }


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);


        fab_open = AnimationUtils.loadAnimation(LandingPage.this, R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(LandingPage.this, R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(LandingPage.this, R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(LandingPage.this, R.anim.rotate_backward);


        tv1 = (TextView) findViewById(R.id.tv1);

        tv2 = (TextView) findViewById(R.id.tv2);


        overlay = (RelativeLayout) findViewById(R.id.overlay);


        Typeface tf = AppController.getInstance().getRobotoCondensedFont();
        tv1.setTypeface(tf, Typeface.NORMAL);
        tv2.setTypeface(tf, Typeface.NORMAL);

        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);


        overlay.setOnClickListener(new View.OnClickListener() {
                                       public void onClick(View v) {

                                           animateFAB();
                                       }
                                   }


        );


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!AppController.getInstance().getChatSynced()) {





            /*
         * If logging in for the first time,then have to sync the chats
         */


                    if (AppController.getInstance().canPublish()) {
                        AppController.getInstance().subscribeToTopic(MqttEvents.FetchChats.value + "/" + AppController.getInstance().getUserId(), 1);


                        fetchChatHistory();

                    } else {


                        if (root != null) {
                            Snackbar snackbar = Snackbar.make(root, getString(R.string.NoInternetConnectionAvailable), Snackbar.LENGTH_SHORT);


                            snackbar.show();
                            View view2 = snackbar.getView();
                            TextView txtv = (TextView) view2.findViewById(android.support.design.R.id.snackbar_text);
                            txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                        }
                    }

                }
            }
        }, 2000);


    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFrag(new SealingFragment());

        adapter.addFrag(new BuyingFragment());


        viewPager.setAdapter(adapter);
    }

    /*
    * View pager adapter.*/
    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
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

        void addFrag(Fragment fragment) {
            mFragmentList.add(fragment);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }


        private int[] imageResId = {R.drawable.call_selector, R.drawable.chat_selector, R.drawable.contact_selector};

        View getTabView(int position) {
            @SuppressLint("InflateParams") View v = LayoutInflater.from(LandingPage.this).inflate(R.layout.chatscreen_tab, null);
            ImageView img = (ImageView) v.findViewById(R.id.imgView);
            img.setImageResource(imageResId[position]);
            return v;
        }
    }


    @SuppressWarnings("unchecked")
    @Override
    public void onClick(View v) {


        int id = v.getId();
        switch (id) {
            case R.id.fab:

                animateFAB();
                break;
            case R.id.fab1:
                /*
                 * Fetch users
                 */


                Intent i = new Intent(LandingPage.this, SelectUsersActivity.class);

                i.putExtra("userId", AppController.getInstance().getUserId());

                startActivity(i);
                break;
            case R.id.fab2:

                                /*
 * Calls
 */
                Intent i2 = new Intent(LandingPage.this, OwnProfileDetails.class);


                i2.putExtra("userId", AppController.getInstance().getUserId());

                startActivity(i2);


                break;


        }
    }

    private void animateFAB() {


        if (isFabOpen) {


            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);


            tv1.startAnimation(fab_close);

            tv2.startAnimation(fab_close);


            fab1.setVisibility(View.GONE);
            fab2.setVisibility(View.GONE);

            tv1.setVisibility(View.GONE);
            tv2.setVisibility(View.GONE);

            fab1.setClickable(false);
            fab2.setClickable(false);

            isFabOpen = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                doExitReveal();
            } else {

                doExitRevealPreLollipop();
            }

        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                doCircularReveal();
            } else {

                doCircularRevealPreLollipop();
            }


            fab.startAnimation(rotate_forward);

            fab1.setVisibility(View.VISIBLE);
            fab2.setVisibility(View.VISIBLE);


            tv1.setVisibility(View.VISIBLE);
            tv2.setVisibility(View.VISIBLE);


            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);

            tv1.startAnimation(fab_open);

            tv2.startAnimation(fab_open);


            fab1.setClickable(true);
            fab2.setClickable(true);

            isFabOpen = true;


        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void doCircularReveal() {

        // get the center for the clipping circle


        int centerX = overlay.getRight();
        int centerY = overlay.getBottom();

        int startRadius = 0;
        // get the final radius for the clipping circle
        int endRadius = Math.max(overlay.getWidth(), overlay.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(overlay,
                        centerX, centerY, startRadius, endRadius);
        anim.setDuration(400);

        overlay.setVisibility(View.VISIBLE);
        anim.start();

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void doExitReveal() {

        // get the center for the clipping circle


        int centerX = overlay.getRight();
        int centerY = overlay.getBottom();

        // get the initial radius for the clipping circle
        int initialRadius = overlay.getWidth();

        // create the animation (the final radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(overlay,
                        centerX, centerY, initialRadius, 0);
        anim.setDuration(400);
        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                overlay.setVisibility(View.GONE);
            }
        });

        // start the animation
        anim.start();

    }

    private void doCircularRevealPreLollipop() {

        // get the center for the clipping circle


        int centerX = overlay.getRight();
        int centerY = overlay.getBottom();

        int startRadius = 0;
        // get the final radius for the clipping circle
        int endRadius = Math.max(overlay.getWidth(), overlay.getHeight());


        Animator animator =
                com.demskigroup.guitaramps.mqttchat.CircularReveal.ViewAnimationUtils.createCircularReveal(overlay,
                        centerX, centerY, startRadius, endRadius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(400);
        overlay.setVisibility(View.VISIBLE);
        animator.start();

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void doExitRevealPreLollipop() {

        // get the center for the clipping circle


        int centerX = overlay.getRight();
        int centerY = overlay.getBottom();

        // get the initial radius for the clipping circle
        int initialRadius = overlay.getWidth();

        // create the animation (the final radius is zero)
        Animator anim =
                com.demskigroup.guitaramps.mqttchat.CircularReveal.ViewAnimationUtils.createCircularReveal(overlay,
                        centerX, centerY, initialRadius, 0);
        anim.setDuration(400);
        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                overlay.setVisibility(View.GONE);
            }
        });

        // start the animation
        anim.start();

    }

    private void fetchChatHistory() {


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                ApiOnServer.FETCH_CHATS + "/0", null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {


                try {


                    if (response.getInt("code") != 200) {


                        if (root != null) {
                            Snackbar snackbar = Snackbar.make(root, getString(R.string.ChatSync), Snackbar.LENGTH_SHORT);


                            snackbar.show();
                            View view = snackbar.getView();
                            TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                            txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                        }

                    }

                } catch (
                        JSONException e)

                {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();

            }
        })

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "KMajNKHPqGt6kXwUbFN3dU46PjThSNTtrEnPZUefdasdfghsaderf1234567890ghfghsdfghjfghjkswdefrtgyhdfghj");

                headers.put("token", AppController.getInstance().getApiToken());

                return headers;
            }
        };


        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
/* Add the request to the RequestQueue.*/
        AppController.getInstance().addToRequestQueue(jsonObjReq, "getChatsApi");


    }

    @Subscribe
    public void getMessage(JSONObject object) {
        try {


            if (object.getString("eventName").equals("RefreshChats")) {
                fetchChatHistory();

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}