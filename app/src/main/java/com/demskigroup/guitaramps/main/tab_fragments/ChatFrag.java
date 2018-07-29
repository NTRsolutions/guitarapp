package com.demskigroup.guitaramps.main.tab_fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.adapter.ViewPagerAdapter;
import com.demskigroup.guitaramps.badgeView.Badge;
import com.demskigroup.guitaramps.main.activity.HomePageActivity;
import com.demskigroup.guitaramps.mqttchat.AppController;
import com.demskigroup.guitaramps.mqttchat.Database.CouchDbController;
import com.demskigroup.guitaramps.mqttchat.Fragments.BuyingFragment;
import com.demskigroup.guitaramps.mqttchat.Fragments.SealingFragment;
import com.demskigroup.guitaramps.mqttchat.Utilities.TimestampSorter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * <h2>ChatFrag</h2>
 * <P>
 *     Chat fragment ot show the details.
 * </P>
 * @since 3/31/2017.
 */
public class ChatFrag extends Fragment implements View.OnClickListener
{
    private BuyingFragment buyingFragment;
    private SealingFragment sealingFragment;
    private ViewPager chatViewPager;
    private EditText search_text;
    private RelativeLayout search_intiate,search_edit_view;
    private HomePageActivity homePageActivity;
    private boolean firstTime = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        buyingFragment=new BuyingFragment();
        sealingFragment=new SealingFragment();
        homePageActivity=(HomePageActivity)getActivity();
    }

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {

        if(view!=null){
            if((ViewGroup)view.getParent()!=null)
                ((ViewGroup)view.getParent()).removeView(view);
            return view;
        }


         view = inflater.inflate(R.layout.frag_chat, container, false);
        chatViewPager= (ViewPager) view.findViewById(R.id.viewpager);
        TabLayout tabs_chat= (TabLayout) view.findViewById(R.id.tabs_chat);
        setupViewPager();
        tabs_chat.setupWithViewPager(chatViewPager);
        search_intiate=(RelativeLayout)view.findViewById(R.id.search_intiate);
        search_intiate.setOnClickListener(this);
        search_edit_view=(RelativeLayout)view.findViewById(R.id.search_edit_view);
        view.findViewById(R.id.search_icon).setOnClickListener(this);
        view.findViewById(R.id.refreshButton).setOnClickListener(this);
        view.findViewById(R.id.close_icon).setOnClickListener(this);
        search_text=(EditText)view.findViewById(R.id.search_text);
        search_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                checkSearchFiltre(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        search_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                checkSearchFiltre(search_text.getText().toString().trim());
                return true;
            }
            return false;
        }
    });

        return view;
    }

    /*
     *Doing the setup for the view pager. */
    private void setupViewPager()
    {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(buyingFragment,getResources().getString(R.string.buying));
        adapter.addFragment(sealingFragment,getResources().getString(R.string.selling));
        chatViewPager.setAdapter(adapter);
        chatViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position)
            {
                search_text.setText("");
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.close_icon:
                search_text.setText("");
                animateSearchView();
                break;
            case R.id.search_intiate:
                animateSearchView();
                break;
            case R.id.search_icon:
                animateSearchView();
                break;
            case R.id.refreshButton:
                search_text.setText("");
                handelRefreshCall();
                break;
        }
    }
    /*
     *Handling the refresh call call to the  */
    private void handelRefreshCall()
    {
        switch (chatViewPager.getCurrentItem())
        {
            case 0:
                buyingFragment.performChatSync();
                break;
            case 1:
                sealingFragment.performChatSync();
                break;

        }
    }
    /*
     *searching*/
    private void checkSearchFiltre(String searchText)
    {
        switch (chatViewPager.getCurrentItem())
        {
            case 0:
                buyingFragment.performFiltre(searchText);
                break;
            case 1:
                sealingFragment.performFiltre(searchText);
                break;
        }
    }
    /*
     *Animating the chat view. */
    private void animateSearchView()
    {
        Animation animationUtils;
        if(search_edit_view.getVisibility()==View.GONE)
        {
            search_edit_view.setVisibility(View.VISIBLE);
            search_intiate.setVisibility(View.GONE);
            animationUtils=AnimationUtils.loadAnimation(getActivity(), R.anim.search_view_animaton);
            search_edit_view.setAnimation(animationUtils);
        }else
        {
            search_edit_view.setVisibility(View.GONE);
            search_intiate.setVisibility(View.VISIBLE);
        }

    }

}
