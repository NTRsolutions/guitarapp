package com.demskigroup.guitaramps.main.activity;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.adapter.CurrencyRvAdap;
import com.demskigroup.guitaramps.fcm_push_notification.Config;
import com.demskigroup.guitaramps.fcm_push_notification.NotificationMessageDialog;
import com.demskigroup.guitaramps.fcm_push_notification.NotificationUtils;
import com.demskigroup.guitaramps.pojo_class.CurrencyPojo;
import com.demskigroup.guitaramps.utility.CommonClass;

import java.util.ArrayList;

/**
 * <h>CurrencyListActivity</h>
 * <p>
 *     This class is called from PostProductActivity class. In this class we used
 *     to get all country name and its currency symbol and to show in recyclerview
 *     using its adpter. On the click of any particular country. we used to send
 *     its cuurency code and symbol to the previous activity.
 * </p>
 * @since 05-May-17
 * @author 3embed
 * @version 1.0
 */
public class CurrencyListActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = CurrencyListActivity.class.getSimpleName();
    private String[] arrayCurrency;
    private ArrayList<CurrencyPojo> arrayListCurrency;
    private RecyclerView rV_currency;
    private Activity mActivity;
    private CurrencyRvAdap currencyRvAdap;
    private NotificationMessageDialog mNotificationMessageDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_list);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        initVariables();
    }

    /**
     * <h>initVariables</h>
     * <p>
     *     In this method we used to initialize all variables.
     * </p>
     */
    private void initVariables()
    {
        mActivity=CurrencyListActivity.this;
        mNotificationMessageDialog=new NotificationMessageDialog(mActivity);
        CommonClass.statusBarColor(mActivity);
        arrayCurrency =getResources().getStringArray(R.array.currency_picker);
        arrayListCurrency=new ArrayList<>();

        RelativeLayout rL_back_btn = (RelativeLayout) findViewById(R.id.rL_back_btn);
        rL_back_btn.setOnClickListener(this);
        rV_currency= (RecyclerView) findViewById(R.id.rV_currency);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);

        System.out.println(TAG+" "+"arrayCurrency size="+arrayCurrency.length);
        storeCurrencyIntoList();

        /**
         * Search currency
         */

        EditText eT_searchCode= (EditText) findViewById(R.id.eT_searchCode);
        eT_searchCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currencyRvAdap.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

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

    /**
     * <h>StoreCurrencyIntoList</h>
     * <p>
     *     This method is called from onCreate() method of the same class. In this
     *     method we used to save all country name, currency code and its symbol
     *     into generic type arraylist. After we used to send list to its adpter
     *     class and show in recyclerview.
     * </p>
     */
    private void storeCurrencyIntoList()
    {
        if (arrayCurrency.length>0)
        {
            String[] getCurrencyArr;
            for (String currency : arrayCurrency) {
                getCurrencyArr = currency.split(",");
                CurrencyPojo currencyPojo = new CurrencyPojo();
                currencyPojo.setCountry(getCurrencyArr[0]);
                currencyPojo.setCode(getCurrencyArr[1]);
                currencyPojo.setSymbol(getCurrencyArr[2]);
                arrayListCurrency.add(currencyPojo);
            }

            // show country and its currency
            if (arrayListCurrency.size()>0)
            {
                currencyRvAdap=new CurrencyRvAdap(mActivity,arrayListCurrency);
                LinearLayoutManager layoutManager=new LinearLayoutManager(mActivity);
                rV_currency.setLayoutManager(layoutManager);
                rV_currency.setAdapter(currencyRvAdap);
            }
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }
}
