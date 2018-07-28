package com.demskigroup.guitaramps.main.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.facebook.all.All;
import com.google.gson.Gson;
import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.adapter.ModelCategoryRvAdapter;
import com.demskigroup.guitaramps.adapter.ProductCategoryRvAdapter;
import com.demskigroup.guitaramps.fcm_push_notification.Config;
import com.demskigroup.guitaramps.fcm_push_notification.NotificationMessageDialog;
import com.demskigroup.guitaramps.fcm_push_notification.NotificationUtils;
import com.demskigroup.guitaramps.pojo_class.product_category.ProductCategoryMainPojo;
import com.demskigroup.guitaramps.pojo_class.product_category.ProductCategoryResDatas;
import com.demskigroup.guitaramps.pojo_class.productcategory.AllModels;
import com.demskigroup.guitaramps.pojo_class.productcategory.Homecat;
import com.demskigroup.guitaramps.pojo_class.productcategory.Searchamp;
import com.demskigroup.guitaramps.utility.ApiUrl;
import com.demskigroup.guitaramps.utility.ClickListener;
import com.demskigroup.guitaramps.utility.CommonClass;
import com.demskigroup.guitaramps.utility.OkHttp3Connection;
import com.demskigroup.guitaramps.utility.VariableConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

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
public class ProductModelsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = ProductModelsActivity.class.getSimpleName();
    private ProgressBar progress_bar;
    private Activity mActivity;
    private LinearLayout rL_rootview;
    private RecyclerView rV_category;
    private NotificationMessageDialog mNotificationMessageDialog;
    EditText mEdSearch;
    LinearLayout mLiTitleSpeach;
    ModelCategoryRvAdapter categoryRvAdapter;
    List<AllModels> aL_categoryDatas = new ArrayList<>();

    private static final int REQ_CODE_SPEECH_INPUT_TITLE = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_models );
        overridePendingTransition( R.anim.activity_open_translate, R.anim.activity_close_scale );

        mActivity = ProductModelsActivity.this;
        mNotificationMessageDialog = new NotificationMessageDialog( mActivity );
        CommonClass.statusBarColor( mActivity );
        progress_bar = (ProgressBar) findViewById( R.id.progress_bar );
        rL_rootview = (LinearLayout) findViewById( R.id.rL_rootview );
        rV_category = (RecyclerView) findViewById( R.id.rV_category );
        RelativeLayout rL_back_btn = (RelativeLayout) findViewById( R.id.rL_back_btn );
        rL_back_btn.setOnClickListener( this );
        mEdSearch = findViewById( R.id.ed_search );

        mEdSearch.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {


                filter( s.toString() );
            }
        } );

        getCategoriesService();
        mLiTitleSpeach = findViewById( R.id.layout_title );
        mLiTitleSpeach.setOnClickListener( this );

    }


    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance( this ).registerReceiver( mNotificationMessageDialog.mRegistrationBroadcastReceiver, new IntentFilter( Config.REGISTRATION_COMPLETE ) );

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance( this ).registerReceiver( mNotificationMessageDialog.mRegistrationBroadcastReceiver, new IntentFilter( Config.PUSH_NOTIFICATION ) );

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications( getApplicationContext() );
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance( this ).unregisterReceiver( mNotificationMessageDialog.mRegistrationBroadcastReceiver );
        super.onPause();
    }

    /**
     * <h>GetCategoriesService</h>
     * <p>
     * This method is called from onCreate() method of the current class.
     * In this method we used to call the getCategories api using okHttp3.
     * Once we get the data we show that list in recyclerview.
     * </p>
     */
    private void getCategoriesService() {
        if (CommonClass.isNetworkAvailable( mActivity )) {
            progress_bar.setVisibility( View.VISIBLE );
            JSONObject request_datas = new JSONObject();

            try {
                request_datas.put( "searchkey", "" );

            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection( TAG, ApiUrl.GET_MODELS, OkHttp3Connection.Request_type.GET, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    progress_bar.setVisibility( View.GONE );

                    System.out.println( TAG + " " + "get category res=" + result );

                    Homecat homecat;
                    Gson gson = new Gson();
                    homecat = gson.fromJson( result, Homecat.class );

                    switch (homecat.getCode()) {

                        // success
                        case 200:
                            aL_categoryDatas = homecat.getAllModels();
                            if (aL_categoryDatas != null && aL_categoryDatas.size() > 0)

                            {

                                categoryRvAdapter = new ModelCategoryRvAdapter( ProductModelsActivity.this, aL_categoryDatas );
                                LinearLayoutManager layoutManager = new LinearLayoutManager( mActivity );

                                rV_category.setLayoutManager( layoutManager );
                                rV_category.setAdapter( categoryRvAdapter );


                                filter( "" );


//                                categoryRvAdapter.setOnItemClick(new ClickListener() {
//                                    @Override
//                                    public void onItemClick(View view, int position) {
//                                        String categoryName=aL_categoryDatas.get(position).getNameOfManufacturer();
//                                        if (categoryName!=null && !categoryName.isEmpty())
//                                        {
//                                            categoryName=categoryName.substring(0,1).toUpperCase()+categoryName.substring(1).toLowerCase();
//                                            Intent intent=new Intent();
//                                            intent.putExtra("modelName",categoryName+"/"+aL_categoryDatas.get(position).getModelNodeId());
//                                            setResult(VariableConstants.CATEGORY_REQUEST_CODE,intent);
//                                            onBackPressed();
//                                            finish();
//                                        }
//                                    }
//                                });

                            }
                            break;

                        // Error
                        default:
                            CommonClass.showSnackbarMessage( rL_rootview, homecat.getMessage() );
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    progress_bar.setVisibility( View.GONE );
                    CommonClass.showSnackbarMessage( rL_rootview, error );
                }
            } );
        } else
            CommonClass.showSnackbarMessage( rL_rootview, getResources().getString( R.string.NoInternetAccess ) );
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition( R.anim.activity_open_scale, R.anim.activity_close_translate );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rL_back_btn:
                onBackPressed();
                break;

            // title speach recognition
            case R.id.layout_title:
                startVoiceInput( REQ_CODE_SPEECH_INPUT_TITLE );
                break;
        }
    }


    List<AllModels> temp = null;

    void filter(String text) {

        if (categoryRvAdapter != null) {


            if (text.length() == 0) {
                categoryRvAdapter.updateList( aL_categoryDatas );

                categoryRvAdapter.setOnItemClick( new ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String categoryName = aL_categoryDatas.get( position ).getNameOfManufacturer();
                        if (categoryName != null && !categoryName.isEmpty()) {
                            categoryName = categoryName.substring( 0, 1 ).toUpperCase() + categoryName.substring( 1 ).toLowerCase();
                            Intent intent = new Intent();
                            intent.putExtra( "modelName", categoryName + "/" + aL_categoryDatas.get( position ).getModelNodeId() );
                            setResult( VariableConstants.MODEL_REQUEST_CODE, intent );
                            ProductModelsActivity.this.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN );
                            onBackPressed();
                            finish();
                        }
                    }
                } );

            } else {

                temp = new ArrayList();
                for (AllModels d : aL_categoryDatas) {
                    //or use .equal(text) with you want equal match
                    //use .toLowerCase() for better matches

                    if (d.getNameOfManufacturer().toLowerCase().contains( text.toLowerCase() )) {
                        temp.add( d );
                    }
                }
                categoryRvAdapter.updateList( temp );

                categoryRvAdapter.setOnItemClick( new ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String categoryName = temp.get( position ).getNameOfManufacturer();
                        if (categoryName != null && !categoryName.isEmpty()) {
                            categoryName = categoryName.substring( 0, 1 ).toUpperCase() + categoryName.substring( 1 ).toLowerCase();
                            Intent intent = new Intent();
                            intent.putExtra( "modelName", categoryName + "/" + aL_categoryDatas.get( position ).getModelNodeId() );
                            setResult( VariableConstants.MODEL_REQUEST_CODE, intent );
                            onBackPressed();
                            finish();
                        }
                    }
                } );
            }
            //update recyclerview


        }
    }

    private void startVoiceInput(int requestCode) {
        Intent intent = new Intent( RecognizerIntent.ACTION_RECOGNIZE_SPEECH );
        intent.putExtra( RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM );
        intent.putExtra( RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault() );

        if (REQ_CODE_SPEECH_INPUT_TITLE == requestCode) {
            intent.putExtra( RecognizerIntent.EXTRA_PROMPT, "Please say a product make." );
        }
        try {
            startActivityForResult( intent, requestCode );
        } catch (ActivityNotFoundException a) {

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if (data != null) {
            switch (requestCode) {

                case REQ_CODE_SPEECH_INPUT_TITLE:
                    if (resultCode == RESULT_OK && null != data) {
                        ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        String categoryName = result.get(0);
                        categoryName = capitalize(categoryName);
                        for (int i=0;i<aL_categoryDatas.size();i++){
                            if (aL_categoryDatas.get(i).getNameOfManufacturer().contains( categoryName )){

                                categoryName = categoryName.substring( 0, 1 ).toUpperCase() + categoryName.substring( 1 ).toLowerCase();

                                if (categoryName != null && !categoryName.isEmpty()) {

                                    Intent intent = new Intent();
                                    intent.putExtra( "modelName", aL_categoryDatas.get(i).getNameOfManufacturer() + "/" + aL_categoryDatas.get( i ).getModelNodeId() );
                                    setResult( VariableConstants.MODEL_REQUEST_CODE, intent );
                                    ProductModelsActivity.this.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN );
                                    onBackPressed();
                                    finish();

                                    break;
                                }
                            }
                        }


                    }
                    break;
            }
        }
    }

    public static String capitalize(String input) {

        String[] words = input.toLowerCase().split(" ");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];

            if (i > 0 && word.length() > 0) {
                builder.append(" ");
            }

            String cap = word.substring(0, 1).toUpperCase() + word.substring(1);
            builder.append(cap);
        }
        return builder.toString();
    }
}