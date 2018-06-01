package com.yelo.com.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yelo.com.R;
import com.yelo.com.adapter.FilterCategoryRvAdapter;
import com.yelo.com.fcm_push_notification.Config;
import com.yelo.com.fcm_push_notification.NotificationMessageDialog;
import com.yelo.com.fcm_push_notification.NotificationUtils;
import com.yelo.com.get_current_location.FusedLocationReceiver;
import com.yelo.com.get_current_location.FusedLocationService;
import com.yelo.com.pojo_class.product_category.ProductCategoryResDatas;
import com.yelo.com.utility.CommonClass;
import com.yelo.com.utility.GetCompleteAddress;
import com.yelo.com.utility.RunTimePermission;
import com.yelo.com.utility.SessionManager;
import com.yelo.com.utility.VariableConstants;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;
import java.util.Map;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * <h>FilterActivity</h>
 * <p>
 *     This class is called from HomePageFrag class. In this class we used to save the selected
 *     filter datas like address, category etc and send back to the HomePageFrag class.
 * </p>
 * @since 4/17/2017
 * @author 3Embed
 * @version 1.0
 */
public class FilterActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = FilterActivity.class.getSimpleName();
    private SeekBar distance_seekbar;
    private RadioButton radio_NewestFirst, radio_ClosestFirst, radio_highToLow, radio_lowToHigh, radio_last24hr, radio_last7day, radio_last30day, radio_allProduct;
    private FusedLocationService locationService;
    private String[] permissionsArray;
    private RunTimePermission runTimePermission;
    private Activity mActivity;
    private ProgressBar progress_bar_save;
    private RelativeLayout rL_rootElement;
    private TextView tV_currency,tV_setLocation,tV_starting_dis, tV_save;
    private ImageView iV_dropDown_currency;
    private FilterCategoryRvAdapter categoryRvAdapter;
    private EditText eT_minprice,eT_maxprice;
    private String currency_symbol="",currency_code="",userLat="",userLng="",sortByText="",postedWithinText="",address="",sortBy="",setDistanceValue="",postedWithin="",minPrice="",maxPrice="", filterMake="", filterYear="", filterModel="" ;
    private ArrayList<ProductCategoryResDatas> aL_categoryDatas;
    private boolean isToApplyFilter;
    private NotificationMessageDialog mNotificationMessageDialog;
    private SessionManager mSessionManager;
    private boolean isToStartActivity;

    RadioGroup rdgFirst, rdgHours;

    boolean isCheckedWork = true;
    RelativeLayout mRlModels,mRlModelsYear,mRlMake;
    static int ManufactrerId = -1;
    TextView mTvcategoryModels, mTvCategoryYears, mTvMakes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        overridePendingTransition(R.anim.slide_up, R.anim.stay);

        initializeVariables();
    }

    /**
     * <h>InitializeVariables</h>
     * <p>
     * In this method we used to initialize The data member
     * and xml all variable.
     * </p>
     */
    private void initializeVariables()
    {
        mActivity = FilterActivity.this;
        mSessionManager=new SessionManager(mActivity);
        mNotificationMessageDialog=new NotificationMessageDialog(mActivity);
        isToApplyFilter=false;
        isToStartActivity = true;

        // Receive datas from HomePage Frag
        Intent intent=getIntent();
        aL_categoryDatas= (ArrayList<ProductCategoryResDatas>) intent.getSerializableExtra("aL_categoryDatas");
        address=intent.getStringExtra("address");
        setDistanceValue = intent.getStringExtra("distance");
        sortBy=intent.getStringExtra("sortBy");
        postedWithin=intent.getStringExtra("postedWithin");
        currency_code=intent.getStringExtra("currency_code");
        currency_symbol=intent.getStringExtra("currency_symbol");
        minPrice=intent.getStringExtra("minPrice");
        maxPrice=intent.getStringExtra("maxPrice");
        userLat=intent.getStringExtra("userLat");
        userLng=intent.getStringExtra("userLng");

        filterMake=intent.getStringExtra("filterMake");
        filterYear=intent.getStringExtra("year");
        filterModel=intent.getStringExtra("model");

        if(filterMake.length() <= 0){
            ManufactrerId = -1;
        }

        permissionsArray = new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION};
        runTimePermission = new RunTimePermission(mActivity, permissionsArray, false);

        // set category recyclerview adapter
        RecyclerView rV_category = (RecyclerView) findViewById(R.id.rV_category);
        categoryRvAdapter = new FilterCategoryRvAdapter(mActivity, aL_categoryDatas);
        GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 2);
        rV_category.setLayoutManager(layoutManager);
        rV_category.setAdapter(categoryRvAdapter);

        // Initialize xml variables
        CommonClass.statusBarColor(mActivity);
        rL_rootElement = (RelativeLayout) findViewById(R.id.rL_rootElement);
        tV_save = (TextView) findViewById(R.id.tV_save);

        TextView tV_reset = (TextView) findViewById(R.id.tV_reset);
        tV_reset.setOnClickListener(this);
        progress_bar_save = (ProgressBar) findViewById(R.id.progress_bar_save);
        progress_bar_save.setVisibility(View.GONE);
        RelativeLayout rL_save, rL_back_btn;
        rL_back_btn = (RelativeLayout) findViewById(R.id.rL_back_btn);
        rL_back_btn.setOnClickListener(this);
        rL_save = (RelativeLayout) findViewById(R.id.rL_apply);
        rL_save.setOnClickListener(this);
        eT_minprice= (EditText) findViewById(R.id.eT_minprice);
        eT_maxprice= (EditText) findViewById(R.id.eT_maxprice);


        mRlModels = findViewById( R.id.rL_product_model );
        mRlModelsYear = findViewById( R.id.rL_product_model_year );
        mRlMake = findViewById( R.id.rL_product_model_make );

        mRlModels.setOnClickListener(this);
        mRlModelsYear.setOnClickListener(this);
        mRlMake.setOnClickListener(this);

        mTvcategoryModels = findViewById( R.id.tV_category_model );
        mTvCategoryYears = findViewById( R.id.tV_category_model_year );
        mTvMakes  = findViewById( R.id.tV_category_model_make );

        mTvcategoryModels.setText( filterMake );
        mTvCategoryYears.setText( filterYear );
        mTvMakes.setText( filterModel );

        // Distance seekbar
        tV_starting_dis = (TextView) findViewById(R.id.tV_starting_dis);
        distance_seekbar = (SeekBar) findViewById(R.id.distance_seekbar);
        distance_seekbar.setMax(3000);
        distance_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String setValue = progress + " " + getResources().getString(R.string.km);


                tV_starting_dis.setText(setValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        rdgFirst = findViewById(R.id.rdg_first);
        rdgHours = findViewById(R.id.rdg_hours);

        rdgFirst.setOnCheckedChangeListener( new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId){
                    case R.id.radio_NewestFirst:

                        if(isCheckedWork) {
                            radio_NewestFirst.setChecked( true );
                            radio_ClosestFirst.setChecked( false );
                            radio_highToLow.setChecked( false );
                            radio_lowToHigh.setChecked( false );
                        }

                        break;

                    case R.id.radio_ClosestFirst:
                        if(isCheckedWork) {
                            radio_NewestFirst.setChecked( false );
                            radio_ClosestFirst.setChecked( true );
                            radio_highToLow.setChecked( false );
                            radio_lowToHigh.setChecked( false );
                        }
                        break;

                    case R.id.radio_highToLow:
                        if(isCheckedWork) {
                            radio_NewestFirst.setChecked( false );
                            radio_ClosestFirst.setChecked( false );
                            radio_highToLow.setChecked( true );
                            radio_lowToHigh.setChecked( false );
                        }
                        break;
                    case R.id.radio_lowToHigh:

                        if(isCheckedWork) {
                            radio_NewestFirst.setChecked( false );
                            radio_ClosestFirst.setChecked( false );
                            radio_highToLow.setChecked( false );
                            radio_lowToHigh.setChecked( true );
                        }
                        break;

                }

            }
        } );


        rdgHours.setOnCheckedChangeListener( new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId){
                    case R.id.radio_last24hr:

                        // posted within
                        if(isCheckedWork) {
                            radio_last24hr.setChecked( true );
                            radio_last7day.setChecked( false );
                            radio_last30day.setChecked( false );
                            radio_allProduct.setChecked( false );
                        }
                        break;

                    case R.id.radio_last7day:
                        if(isCheckedWork) {
                            radio_last24hr.setChecked( false );
                            radio_last7day.setChecked( true );
                            radio_last30day.setChecked( false );
                            radio_allProduct.setChecked( false );
                        }
                        break;

                    case R.id.radio_last30day:
                        if(isCheckedWork) {
                            radio_last24hr.setChecked( false );
                            radio_last7day.setChecked( false );
                            radio_last30day.setChecked( true );
                            radio_allProduct.setChecked( false );
                        }
                        break;
                    case R.id.radio_allProduct:
                        if(isCheckedWork) {
                            radio_last24hr.setChecked( false );
                            radio_last7day.setChecked( false );
                            radio_last30day.setChecked( false );
                            radio_allProduct.setChecked( true );
                        }
                        break;

                }

            }
        } );


        // sorted by value
        radio_NewestFirst = (RadioButton) findViewById(R.id.radio_NewestFirst);
        radio_ClosestFirst = (RadioButton) findViewById(R.id.radio_ClosestFirst);
        radio_highToLow = (RadioButton) findViewById(R.id.radio_highToLow);
        radio_lowToHigh = (RadioButton) findViewById(R.id.radio_lowToHigh);


        // posted within
        radio_last24hr = (RadioButton) findViewById(R.id.radio_last24hr);
        radio_last7day = (RadioButton) findViewById(R.id.radio_last7day);
        radio_last30day = (RadioButton) findViewById(R.id.radio_last30day);
        radio_allProduct = (RadioButton) findViewById(R.id.radio_allProduct);

        // Currency
        RelativeLayout rL_cuurency = (RelativeLayout) findViewById(R.id.rL_currency);
        rL_cuurency.setOnClickListener(this);
        tV_currency= (TextView) findViewById(R.id.tV_currency);
        tV_setLocation= (TextView) findViewById(R.id.tV_setLocation);
        iV_dropDown_currency= (ImageView) findViewById(R.id.iV_dropDown_currency);
        setDefaultCurrency();

        // Change location
        RelativeLayout rL_changeLoc= (RelativeLayout) findViewById(R.id.rL_changeLoc);
        rL_changeLoc.setOnClickListener(this);

        if (runTimePermission.checkPermissions(permissionsArray)) {
            if (!isLocationFound(userLat,userLng)) {
                System.out.println(TAG+" "+"is location found="+isLocationFound(userLat,userLng));
                isToApplyFilter = false;
                getCurrentLocation();
            }
        } else {
            runTimePermission.requestPermission();
        }
        setFilterDatas();
    }

    /**
     * <h>setFilterDatas</h>
     * <p>
     *     In this method we used set the xml variables
     * </p>
     */
    private void setFilterDatas()
    {
        // set address
        if (address!=null && !address.isEmpty())
            tV_setLocation.setText(address);

        // set distance
        if (setDistanceValue!=null && !setDistanceValue.isEmpty()) {
            distance_seekbar.setProgress(Integer.parseInt(setDistanceValue));
            setDistanceValue = setDistanceValue + " " + getResources().getString(R.string.km);
            tV_starting_dis.setText(setDistanceValue);
        }

        System.out.println(TAG+" "+"sortBy value="+sortBy);
        // set sort by radio button
        switch (sortBy)
        {
            // Newest first
            case "postedOnDesc" :
                radio_NewestFirst.setChecked(true);
                break;

            // closest first
            case "distanceAsc" :
                radio_ClosestFirst.setChecked(true);
                break;

            // High to low
            case "priceDsc" :
                radio_highToLow.setChecked(true);
                break;

            // low to high
            case "priceAsc" :
                radio_lowToHigh.setChecked(true);
                break;
        }

        // set posted within
        switch (postedWithin)
        {
            // The last 24 hour
            case "1" :
                radio_last24hr.setChecked(true);
                break;

            // Last 7 days
            case "7" :
                radio_last7day.setChecked(true);
                break;

            // The last 30 days
            case "30" :
                radio_last30day.setChecked(true);
                break;
        }

        // set currency code
        if (currency_code!=null && !currency_code.isEmpty())
            tV_currency.setText(currency_code);

        // min price
        if (minPrice!=null && !minPrice.isEmpty())
            eT_minprice.setText(minPrice);

        if (maxPrice!=null && !maxPrice.isEmpty())
            eT_maxprice.setText(maxPrice);

        isCheckedWork = true;
    }

    /**
     * <h>SetDefaultCurrency</h>
     * <p>
     *     In this method we used to find the current country currency.
     * </p>
     */
    private void setDefaultCurrency()
    {
        Map<Currency, Locale> map = CommonClass.getCurrencyLocaleMap();
        String countryIsoCode = Locale.getDefault().getCountry();
        Locale locale = new Locale("EN",countryIsoCode);
        Currency currency = Currency.getInstance(locale);
        currency_symbol = currency.getSymbol(map.get(currency));
        if (currency_symbol!=null && !currency_symbol.isEmpty())
        {
            tV_currency.setText(String.valueOf(currency));
            currency_code=String.valueOf(currency);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            // Back button
            case R.id.rL_back_btn:
                onBackPressed();
                break;


            // category
            case R.id.rL_product_model :

                    intent = new Intent(mActivity, ProductModelsActivity.class);
                    startActivityForResult(intent, VariableConstants.MODEL_REQUEST_CODE);

                break;
            // year
            case R.id.rL_product_model_year :


                    if(ManufactrerId != -1) {
                        intent = new Intent( mActivity, YearModelsActivity.class );
                        intent.putExtra( "model_id", ManufactrerId );
                        startActivityForResult( intent, VariableConstants.MODEL_REQUEST_YEAR_CODE );
                    }else {
                        Toast.makeText( mActivity, "Please select make first.", Toast.LENGTH_SHORT ).show();
                    }

                break;

            // make
            case R.id.rL_product_model_make:

                    if(ManufactrerId != -1) {
                        String str = mTvcategoryModels.getText().toString();
                        intent = new Intent( mActivity, MakeModelsActivity.class );
                        intent.putExtra( "model_id", ManufactrerId );
                        startActivityForResult( intent, VariableConstants.MODEL_REQUEST_MAKE_CODE );
                    }else {
                        Toast.makeText( mActivity, "Please select make first.", Toast.LENGTH_SHORT ).show();
                    }


                break;



            // save filtered datas
            case R.id.rL_apply:
                double minValue=0;
                double maxValue=0;
                if (!eT_minprice.getText().toString().isEmpty())
                    minValue=Double.parseDouble(eT_minprice.getText().toString());

                if (!eT_maxprice.getText().toString().isEmpty())
                    maxValue=Double.parseDouble(eT_maxprice.getText().toString());
                if (maxValue>=minValue) {
                    if (!tV_setLocation.getText().toString().equals(getResources().getString(R.string.change_Location))) {
                        if (!isLocationFound(userLat, userLng)) {
                            if (runTimePermission.checkPermissions(permissionsArray)) {
                                isToApplyFilter=true;
                                tV_save.setVisibility(View.GONE);
                                progress_bar_save.setVisibility(View.VISIBLE);
                                getCurrentLocation();
                            } else {
                                runTimePermission.requestPermission();
                            }
                        } else {
                            getAllSavedValues();
                        }
                    } else
                        CommonClass.showSnackbarMessage(rL_rootElement, getResources().getString(R.string.please_select_your_loc));
                }
                else CommonClass.showSnackbarMessage(rL_rootElement, getResources().getString(R.string.min_price_cant_be_higher));
                break;

            // reset filter
            case R.id.tV_reset :
                isToApplyFilter=false;
                getCurrentLocation();
                resetFilterDatas();
                break;

            // Currency picker
            case R.id.rL_currency:
                if (isToStartActivity) {
                    isToStartActivity = false;
                    intent = new Intent(mActivity, CurrencyListActivity.class);
                    startActivityForResult(intent, VariableConstants.CURRENCY_REQUEST_CODE);
                }
                break;

            // Change location
            case R.id.rL_changeLoc :
                if (isToStartActivity) {
                    isToStartActivity = false;
                    intent = new Intent(mActivity, ChangeLocationActivity.class);
                    startActivityForResult(intent, VariableConstants.CHANGE_LOC_REQ_CODE);
                }
                break;
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        isToStartActivity = true;
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
     * In this method we find current location using FusedLocationApi.
     * in this we have onUpdateLocation() method in which we check if
     * its not null then We call guest user api.
     */
    private void getCurrentLocation() {
        locationService = new FusedLocationService(mActivity, new FusedLocationReceiver() {
            @Override
            public void onUpdateLocation() {
                Location currentLocation = locationService.receiveLocation();
                if (currentLocation != null) {
                    userLat = String.valueOf(currentLocation.getLatitude());
                    userLng= String.valueOf(currentLocation.getLongitude());
                    progress_bar_save.setVisibility(View.GONE);
                    if (isLocationFound(userLat, userLng))
                    {
                        System.out.println(TAG + " " + "lat=" + userLat + " " + "lng=" + userLng);
                        mSessionManager.setCurrentLat(userLat);
                        mSessionManager.setCurrentLng(userLng);

                        if (isToApplyFilter)
                            getAllSavedValues();

                        String address=CommonClass.getCompleteAddressString(mActivity,currentLocation.getLatitude(),currentLocation.getLongitude());
                        if (!address.isEmpty())
                            tV_setLocation.setText(address);
                        else new GetCompleteAddress(mActivity,userLat,userLng,tV_setLocation,null);
                    }
                }
            }
        }
        );
    }


    /**
     * <h>GetAllSavedValues</h>
     * <p>
     * In this method we fetch all saved values like categories, distance, price etc
     * once we receive all those values we send back all those values to previous
     * activity and do filter operation.
     * </p>
     */
    private void getAllSavedValues() {
        String distance, postedWithin, minPrice = eT_minprice.getText().toString(), maxPrice = eT_maxprice.getText().toString();

        distance = distance_seekbar.getProgress()+"";
        postedWithin = getPostedWithinValue();
        getSortBy();

        if (distance.equals("0"))
            distance="";

        System.out.println(TAG+" "+"currency code="+tV_currency.getText().toString()+" "+" "+"distance="+distance+" "+"latitude="+userLat+" "+"long="+userLng+" "+"maxPrice="+maxPrice+" "+"min price="+minPrice+" "+"postedWithin="+postedWithin+" "+"sortBy="+sortBy+" "+"location="+tV_setLocation.getText().toString()+" ");

        Intent filterIntent = new Intent();
        filterIntent.putExtra("aL_categoryDatas",aL_categoryDatas);
        filterIntent.putExtra("distance", distance);
        filterIntent.putExtra("currentLatitude",userLat);
        filterIntent.putExtra("currentLongitude",userLng);
        filterIntent.putExtra("address",tV_setLocation.getText().toString());
        filterIntent.putExtra("sortBy",sortBy);
        filterIntent.putExtra("postedWithin", postedWithin);
        filterIntent.putExtra("minPrice", minPrice);
        filterIntent.putExtra("maxPrice", maxPrice);
        filterIntent.putExtra("currency_code",currency_code);
        filterIntent.putExtra("currency",currency_symbol);
        filterIntent.putExtra("postedWithinText",postedWithinText);
        filterIntent.putExtra("sortByText",sortByText);

        filterIntent.putExtra("filterMake",mTvcategoryModels.getText().toString());
        filterIntent.putExtra("year",mTvCategoryYears.getText().toString().trim());
        filterIntent.putExtra("model",mTvMakes.getText().toString().trim());
        setResult(VariableConstants.FILTER_REQUEST_CODE, filterIntent);
        progress_bar_save.setVisibility(View.GONE);
        onBackPressed();
    }

    /**
     * <h>ResetFilterDatas</h>
     * <p>
     *     In this method we used to reset all the selected filtered values.
     * </p>
     */
    private void resetFilterDatas()
    {
        // clear selected category
        if (aL_categoryDatas!=null && aL_categoryDatas.size()>0)
        {
            for (ProductCategoryResDatas productCategoryResDatas : aL_categoryDatas)
            {
                System.out.println(TAG+" "+"name="+productCategoryResDatas.getName()+" "+"selected value="+productCategoryResDatas.isSelected());
                productCategoryResDatas.setSelected(false);
            }
        }
        categoryRvAdapter.notifyDataSetChanged();

        // location
        address=getResources().getString(R.string.change_Location);
        userLat="";
        userLng="";

        // set distance
        setDistanceValue=getResources().getString(R.string.zero);

        isCheckedWork = false;
        rdgFirst.clearCheck();
        rdgHours.clearCheck();
        // sort by
        sortBy="";
        radio_NewestFirst.setChecked(false);
        radio_ClosestFirst.setChecked(false);
        radio_highToLow.setChecked(false);
        radio_lowToHigh.setChecked(false);

        // posted within
        postedWithin="";
        radio_last24hr.setChecked(false);
        radio_last7day.setChecked(false);
        radio_last30day.setChecked(false);
        radio_allProduct.setChecked(false);

        // currency
        setDefaultCurrency();

        // price
        eT_minprice.getText().clear();
        eT_maxprice.getText().clear();
        maxPrice="";
        minPrice="";

        System.out.println(TAG+" "+"min price="+eT_minprice.getText().toString()+" "+"max price="+eT_maxprice.getText().toString());

        //eT_minprice.setHint(getResources().getString(R.string.default_price));
        //eT_maxprice.setHint(getResources().getString(R.string.default_price));

        mTvMakes.setText( "" );
        mTvCategoryYears.setText( "" );
        mTvcategoryModels.setText( "" );
        ManufactrerId = -1;

        filterMake = "";
        filterModel = "";
        filterYear = "";

        setFilterDatas();
    }

    /**
     * <h>GetSortBy</h>
     * <p>
     *     In this method we used to set sort by value according to checked sort by checked radio button.
     * </p>
     */
    private void getSortBy()
    {
        if (radio_NewestFirst.isChecked())
        {
            sortBy=getResources().getString(R.string.postedOnDesc);
            sortByText=getResources().getString(R.string.newest_first);
        }
        else if (radio_ClosestFirst.isChecked())
        {
            sortBy=getResources().getString(R.string.distanceAsc);
            sortByText=getResources().getString(R.string.closest_first);
        }
        else if (radio_highToLow.isChecked())
        {
            sortBy=getResources().getString(R.string.priceDsc);
            sortByText=getResources().getString(R.string.price_high_to_low);
        }
        else if (radio_lowToHigh.isChecked())
        {
            sortBy=getResources().getString(R.string.priceAsc);
            sortByText=getResources().getString(R.string.price_low_to_high);
        }
        else sortBy="";
    }

    /**
     * <h>GetPostedWithinValue</h>
     * <p>
     * In this method we used to find any one selected Posted Within Value since
     * it contains radiobutton.
     * </p>
     *
     * @return The selected sort value
     */
    private String getPostedWithinValue() {
        String postedWithin = "";
        if (radio_last24hr.isChecked())
        {
            postedWithin = "1";                 // i.e one day
            postedWithinText=getResources().getString(R.string.the_last_24hr);
        }
        else if (radio_last7day.isChecked())
        {
            postedWithin = "7";                 // i.e seven day
            postedWithinText=getResources().getString(R.string.the_last_7day);
        }
        else if (radio_last30day.isChecked())
        {
            postedWithin = "30";                // i.e 30 day
            postedWithinText=getResources().getString(R.string.the_last_30day);
        }
        else if (radio_allProduct.isChecked())
        {
            postedWithin = "";                  // i.e by default all product
            postedWithinText=getResources().getString(R.string.all_producs);
        }
        return postedWithin;
    }

    /**
     * In this method we used to check whether current lat and
     * long has been received or not.
     *
     * @param lat The current latitude
     * @param lng The current longitude
     * @return boolean flag true or false
     */
    private boolean isLocationFound(String lat, String lng) {
        return !(lat == null || lat.isEmpty()) && !(lng == null || lng.isEmpty());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case VariableConstants.PERMISSION_REQUEST_CODE:
                System.out.println("grant result=" + grantResults.length);
                if (grantResults.length > 0) {
                    for (int count = 0; count < grantResults.length; count++) {
                        if (grantResults[count] != PackageManager.PERMISSION_GRANTED)
                            runTimePermission.allowPermissionAlert(permissions[count]);

                    }
                    System.out.println("isAllPermissionGranted=" + runTimePermission.checkPermissions(permissionsArray));
                    if (runTimePermission.checkPermissions(permissionsArray)) {
                        getCurrentLocation();
                    }
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(TAG + " " + "on activity result called...."+" "+"req code="+requestCode+" "+"res code="+resultCode+" "+"data="+data);
        if (data != null) {
            switch (requestCode) {
                // currency symbol
                case VariableConstants.CURRENCY_REQUEST_CODE:
                    currency_code = data.getStringExtra("cuurency_code");
                    currency_symbol = data.getStringExtra("currency_symbol");

                    // set currency cde eg. Inr
                    if (currency_code != null)
                        tV_currency.setText(currency_code);

                    // change dropdown icon
                    iV_dropDown_currency.setImageResource(R.drawable.drop_down_black_color_icon);
                    break;

                case VariableConstants.CHANGE_LOC_REQ_CODE :
                    userLat=data.getStringExtra("lat");
                    userLng=data.getStringExtra("lng");
                    address=data.getStringExtra("address");
                    tV_setLocation.setText(address);
                    System.out.println(TAG+" "+"lat="+userLat+" "+"lng="+userLng+" "+"address="+address);
                    break;


                // modelName
                case VariableConstants.MODEL_REQUEST_CODE :
                    String modelName=data.getStringExtra("modelName");
                    String[] arr = modelName.split( "/" );

                    if (modelName!=null)
                    {
                        mTvcategoryModels.setText(arr[0]);
                        mTvcategoryModels.requestFocus();

                        ManufactrerId = Integer.parseInt(arr[1]);

//                        mRlModelsYear.performClick();

                    }
                    break;


                // modelName  MODEL_REQUEST_MAKE_CODE
                case VariableConstants.MODEL_REQUEST_YEAR_CODE :
                    String yearName=data.getStringExtra("yearName");
                    if (yearName!=null)
                    {
                        mTvCategoryYears.setText(yearName);
                        mTvCategoryYears.requestFocus();

//                        mRlMake.performClick();
                    }
                    break;


                case VariableConstants.MODEL_REQUEST_MAKE_CODE :
                    String makeName=data.getStringExtra("make_name");
                    if (makeName!=null)
                    {
                        mTvMakes.setText(makeName);
                        mTvMakes.requestFocus();
                    }
                    break;

            }
        }
    }
}
