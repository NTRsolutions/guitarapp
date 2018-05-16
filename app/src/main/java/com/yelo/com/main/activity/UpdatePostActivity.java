package com.yelo.com.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.yelo.com.R;
import com.yelo.com.Uploader.FileUploader;
import com.yelo.com.Uploader.ProductImageDatas;
import com.yelo.com.Uploader.UploadedCallback;
import com.yelo.com.adapter.UpdatePostRvAdapter;
import com.yelo.com.mqttchat.AppController;
import com.yelo.com.pojo_class.update_product_pojo.UpdateProductData;
import com.yelo.com.pojo_class.update_product_pojo.UpdateProductMainPojo;
import com.yelo.com.utility.ApiUrl;
import com.yelo.com.utility.CommonClass;
import com.yelo.com.utility.DialogBox;
import com.yelo.com.utility.OkHttp3Connection;
import com.yelo.com.utility.SessionManager;
import com.yelo.com.utility.VariableConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <h>UpdatePostActivity</h>
 * <p>
 *     In this class we used to provide options to user for modifications for
 *     the posted products like Title, description or prices etc.
 * </p>
 * @since 28-Aug-17.
 * @author 3Embed.
 */
public class UpdatePostActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = UpdatePostActivity.class.getSimpleName();
    private Activity mActivity;
    private EditText eT_title,eT_description,eT_price;
    private TextView tV_category;
    private TextView tV_condition;
    private TextView tV_currency;
    private TextView tV_currency_symbol;
    private TextView tV_current_location;
    private String postId="",productName="",description="",category="",condition="",price="",currency="",negotiable="",place="",
            latitude="",longitude="",city="",countrySname="";
    private ArrayList<ProductImageDatas> aLProductImageDatases;
    private ArrayList<ProductImageDatas> aLUpdateProductImage;
    private UpdatePostRvAdapter imagesHorizontalRvAdap;
    private ArrayList<String> arrayListImgPath;
    //private ProgressBar progress_bar_post;
    private RelativeLayout rL_rootElement;
    private SessionManager mSessionManager;
    private ArrayList<Integer> rotationAngles;
    private DialogBox mDialogBox;
    TextView mTxtCancel;
    RelativeLayout mRlModels,mRlModelsYear,mRlMake;
    TextView mTvcategoryModels, mTvCategoryYears, mTvMakes;
    int ManufactrerId = -1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_product);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        initVariables();
    }

    /**
     * <h>InitVariables</h>
     * <p>
     *     In this method we used to initialize all variables.
     * </p>
     */
    private void initVariables() {
        mActivity = UpdatePostActivity.this;
        mDialogBox = new DialogBox(mActivity);
        mSessionManager=new SessionManager(mActivity);
        arrayListImgPath=new ArrayList<>();
        rotationAngles=new ArrayList<>();
        aLUpdateProductImage=new ArrayList<>();
        rL_rootElement= (RelativeLayout) findViewById(R.id.rL_rootElement);
        TextView tV_change_loc = (TextView) findViewById(R.id.tV_change_loc);
        tV_change_loc.setOnClickListener(this);
        LinearLayout linear_share = (LinearLayout) findViewById(R.id.linear_share);
        linear_share.setVisibility(View.GONE);
        TextView tV_actionBarTitle = (TextView) findViewById(R.id.tV_actionBarTitle);
        tV_actionBarTitle.setText(getResources().getString(R.string.update_post));

        TextView mTxtCancel = (TextView) findViewById(R.id.btn_cancel);

        mRlModels = findViewById( R.id.rL_product_model );
        mRlModelsYear = findViewById( R.id.rL_product_model_year );
        mRlMake = findViewById( R.id.rL_product_model_make );

        mRlModels.setOnClickListener(this);
        mRlModelsYear.setOnClickListener(this);
        mRlMake.setOnClickListener(this);


        mTvcategoryModels = findViewById( R.id.tV_category_model );
        mTvCategoryYears = findViewById( R.id.tV_category_model_year );
        mTvMakes  = findViewById( R.id.tV_category_model_make );

        mTxtCancel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        } );


        // receive datas
        Bundle bundlePostItem = getIntent().getExtras();
        if (bundlePostItem != null) {
            postId = bundlePostItem.getString("postId");
            productName = bundlePostItem.getString("productName");
            description = bundlePostItem.getString("description");
            category = bundlePostItem.getString("category");
            condition = bundlePostItem.getString("condition");
            price = bundlePostItem.getString("price");
            currency = bundlePostItem.getString("currency");
            negotiable = bundlePostItem.getString("negotiable");
            place = bundlePostItem.getString("place");
            latitude = bundlePostItem.getString("latitude");
            longitude = bundlePostItem.getString("longitude");

            if (isLocationFound(latitude,longitude))
            {
                city=CommonClass.getCityName(mActivity,Double.parseDouble(latitude),Double.parseDouble(longitude));
                countrySname=CommonClass.getCountryCode(mActivity,Double.parseDouble(latitude),Double.parseDouble(longitude));
            }
            System.out.println(TAG + " " + "place=" + place+" "+"city="+city+" "+"countrySname="+countrySname);
        }

        // title
        eT_title = (EditText) findViewById(R.id.eT_title);
        if (productName != null && !productName.isEmpty())
            eT_title.setText(productName);

        // Description
        eT_description = (EditText) findViewById(R.id.eT_description);
        if (description != null && !description.isEmpty())
            eT_description.setText(description);

        // price
        eT_price = (EditText) findViewById(R.id.eT_price);
        if (price != null && !price.isEmpty())
            eT_price.setText(price);

        // Back button
        RelativeLayout rL_back_btn, rL_product_category, rL_conditions, rL_currency;
        rL_back_btn= (RelativeLayout) findViewById(R.id.rL_back_btn);
        rL_back_btn.setOnClickListener(this);

        // category
        rL_product_category = (RelativeLayout) findViewById(R.id.rL_product_category);
        rL_product_category.setOnClickListener(this);
        System.out.println(TAG+" "+"category="+category);
        tV_category = (TextView) findViewById(R.id.tV_category);
        if (category != null && !category.isEmpty())
            tV_category.setText(category);

        // condition
        rL_conditions = (RelativeLayout) findViewById(R.id.rL_conditions);
        rL_conditions.setOnClickListener(this);
        tV_condition = (TextView) findViewById(R.id.tV_condition);
        if (condition != null && !condition.isEmpty())
            tV_condition.setText(condition);

        // currency
        rL_currency = (RelativeLayout) findViewById(R.id.rL_currency);
        rL_currency.setOnClickListener(this);
        tV_currency = (TextView) findViewById(R.id.tV_currency);
        if (currency != null && !currency.isEmpty())
            tV_currency.setText(currency);

        // currency symbol
        tV_currency_symbol = (TextView) findViewById(R.id.tV_currency_symbol);

        // set place
        tV_current_location = (TextView) findViewById(R.id.tV_current_location);
        if (place != null && !place.isEmpty()) {
            System.out.println(TAG + " " + "place 2 =" + place);
            tV_current_location.setVisibility(View.VISIBLE);
            tV_current_location.setText(place);
        }

        // disable fetching location progress bar
        ProgressBar progress_bar_location = (ProgressBar) findViewById(R.id.progress_bar_location);
        progress_bar_location.setVisibility(View.GONE);

        // switch negotiable
        SwitchCompat switch_negotiable = (SwitchCompat) findViewById(R.id.switch_negotiable);
        if (negotiable != null && negotiable.equals("1"))
            switch_negotiable.setChecked(true);
        else switch_negotiable.setChecked(false);
        switch_negotiable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    negotiable = "1";
                else negotiable = "0";
            }
        });

        aLProductImageDatases = (ArrayList<ProductImageDatas>) bundlePostItem.getSerializable("imageDatas");
        if (aLProductImageDatases!=null && aLProductImageDatases.size()>0)
            aLUpdateProductImage.addAll(aLProductImageDatases);

        // add more images text
        TextView tV_add_more_image = (TextView) findViewById(R.id.tV_add_more_image);

        // set adpter for horizontal images
        if (aLProductImageDatases.size() > 0) {
            switch (aLProductImageDatases.size()) {
                // text to show add 4 more image
                case 1:
                    tV_add_more_image.setText(getResources().getString(R.string.add_upto_4_more_img));
                    break;

                // text to show add 3 more image
                case 2:
                    tV_add_more_image.setText(getResources().getString(R.string.add_upto_3_more_img));
                    break;

                // text to show add 3 more image
                case 3:
                    tV_add_more_image.setText(getResources().getString(R.string.add_upto_2_more_img));
                    break;

                // text to show add 3 more image
                case 4:
                    tV_add_more_image.setText(getResources().getString(R.string.add_upto_1_more_img));
                    break;

                // hide text since it reached to the max limit
                case 5:
                    tV_add_more_image.setVisibility(View.GONE);
                    break;
            }

            // set image recycler view adapter
            imagesHorizontalRvAdap = new UpdatePostRvAdapter(mActivity, aLProductImageDatases,aLUpdateProductImage,tV_add_more_image);
            RecyclerView rV_cameraImages = (RecyclerView) findViewById(R.id.rV_cameraImages);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
            rV_cameraImages.setLayoutManager(linearLayoutManager);
            rV_cameraImages.setAdapter(imagesHorizontalRvAdap);
            imagesHorizontalRvAdap.notifyDataSetChanged();
        }

        // post
        TextView tV_post = (TextView) findViewById(R.id.tV_post);
        tV_post.setText(getResources().getString(R.string.update));
        tV_post.setOnClickListener(this);
        //progress_bar_post= (ProgressBar) findViewById(R.id.progress_bar_post);

        getCurrencySymbol();
    }

    /**
     * In this method we used to check whether current lat and
     * long has been received or not.
     * @param lat The current latitude
     * @param lng The current longitude
     * @return boolean flag true or false
     */
    private boolean isLocationFound(String lat,String lng) {
        return !(lat == null || lat.isEmpty()) && !(lng == null || lng.isEmpty());
    }

    /**
     * <h>GetCurrencySymbol</h>
     * <p>
     *     In this method we used to get the country currency symbol e.g $ from given Currency code e.g USD
     * </p>
     */
    private void getCurrencySymbol()
    {
        String[] arrayCurrency=getResources().getStringArray(R.array.currency_picker);

        if (arrayCurrency.length>0)
        {
            String[] getCurrencyArr;
            for (String setCurrency : arrayCurrency) {
                getCurrencyArr = setCurrency.split(",");
                String currency_code=getCurrencyArr[1];
                String currency_symbol=getCurrencyArr[2];

                System.out.println(TAG+" "+"given currency="+currency+" "+"my currency="+currency_code);
                if (currency.equals(currency_code))
                {
                    tV_currency_symbol.setText(currency_symbol);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId())
        {
            // post
            case R.id.tV_post :
                mDialogBox.showProgressDialog(mActivity.getResources().getString(R.string.updating));
                if (arrayListImgPath.size()>0)
                {
                    uploadImages(arrayListImgPath,rotationAngles);
                }
                else
                {
                    for (ProductImageDatas productImageDatas : aLProductImageDatases)
                    {
                        System.out.println(TAG + " "+"main url="+ productImageDatas.getMainUrl()+" "+"thumb nail url="+ productImageDatas.getThumbnailUrl()+" "+"width="+ productImageDatas.getWidth()+" "+"height="+ productImageDatas.getHeight()+" "+"message="+ productImageDatas.getMessage()+" "+"public id="+productImageDatas.getPublic_id());
                    }

                    if (aLProductImageDatases.size()>0)
                    updatePostApi(aLProductImageDatases);
                }
                break;

            // back button
            case R.id.rL_back_btn :
                onBackPressed();
                break;



            // category
            case R.id.rL_product_model :

                    intent = new Intent(mActivity, ProductModelsActivity.class);
                    startActivityForResult(intent, VariableConstants.MODEL_REQUEST_CODE);

                break;

            // conditions
            case R.id.rL_product_model_year :


                    if(ManufactrerId != -1) {
                        intent = new Intent( mActivity, YearModelsActivity.class );
                        intent.putExtra( "model_id", ManufactrerId );
                        startActivityForResult( intent, VariableConstants.MODEL_REQUEST_YEAR_CODE );
                    }else {
                        Toast.makeText( mActivity, "Please select make first.", Toast.LENGTH_SHORT ).show();
                    }

                break;

            // currency
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


            // category
            case R.id.rL_product_category :
                intent=new Intent(mActivity,ProductCategoryActivity.class);
                startActivityForResult(intent, VariableConstants.CATEGORY_REQUEST_CODE);
                break;

            // conditions
            case R.id.rL_conditions :
                intent=new Intent(mActivity,PostConditionsActivity.class);
                startActivityForResult(intent, VariableConstants.CONDITION_REQUEST_CODE);
                break;

            // currency
            case R.id.rL_currency:
                intent=new Intent(mActivity,CurrencyListActivity.class);
                startActivityForResult(intent, VariableConstants.CURRENCY_REQUEST_CODE);
                break;

            // change location
            case R.id.tV_change_loc :
                intent=new Intent(mActivity,ChangeLocationActivity.class);
                startActivityForResult(intent, VariableConstants.CHANGE_LOC_REQ_CODE);
                break;
        }
    }


    /*
     *Uploading the image in cloudinary. */
    private void uploadImages(ArrayList<String> list, ArrayList<Integer> rotationAnagles)
    {
        System.out.println(TAG+" "+"post clicked"+" "+"3");
        // Reduce the Bitmap size
        try{
            Bitmap bitmap;
            ByteArrayOutputStream outputStream;
            for(int i=0;i<list.size();i++){
                bitmap= BitmapFactory.decodeFile(list.get(i));
                outputStream = new ByteArrayOutputStream();
                bitmap = rotate(bitmap, rotationAnagles.get(i));
                if (bitmap != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 30, outputStream);
                }
                try {
                    FileOutputStream file_out = new FileOutputStream(new File(list.get(i)));
                    file_out.write(outputStream.toByteArray());
                    file_out.close();
                }
                catch(IOException e){e.printStackTrace();}
            }

            System.out.println(TAG+" "+"post clicked"+" "+"4");
            // Upload Images to cloudinary
            FileUploader.getFileUploader(getApplicationContext()).UploadMultiple(list, new UploadedCallback() {
                @Override
                public void onSuccess(List<ProductImageDatas> data_list, List<ProductImageDatas> failed_list)
                {
                    for (ProductImageDatas productImageDatas : aLUpdateProductImage)
                    {
                        System.out.println(TAG + " "+"main url 1="+ productImageDatas.getMainUrl()+" "+"thumb nail url="+ productImageDatas.getThumbnailUrl()+" "+"width="+ productImageDatas.getWidth()+" "+"height="+ productImageDatas.getHeight()+" "+"message="+ productImageDatas.getMessage());
                    }

                    for (ProductImageDatas productImageDatas : data_list) {
                        System.out.println(TAG + " "+"main url 2="+ productImageDatas.getMainUrl()+" "+"thumb nail url="+ productImageDatas.getThumbnailUrl()+" "+"width="+ productImageDatas.getWidth()+" "+"height="+ productImageDatas.getHeight()+" "+"message="+ productImageDatas.getMessage());
                        ProductImageDatas productImage = new ProductImageDatas();
                        productImage.setMainUrl(productImageDatas.getMainUrl());
                        productImage.setThumbnailUrl(productImageDatas.getThumbnailUrl());
                        productImage.setWidth(productImageDatas.getWidth());
                        productImage.setHeight(productImageDatas.getHeight());
                        productImage.setPublic_id(productImageDatas.getPublic_id());
                        aLUpdateProductImage.add(productImage);
                    }

                    if(aLUpdateProductImage.size()>0) {
                        for (ProductImageDatas productImageDatas : aLUpdateProductImage) {
                            System.out.println(TAG + " " + "main url 3=" + productImageDatas.getMainUrl() + " " + "thumb nail url=" + productImageDatas.getThumbnailUrl() + " " + "width=" + productImageDatas.getWidth() + " " + "height=" + productImageDatas.getHeight() + " " + "message=" + productImageDatas.getMessage()+" "+"public id="+productImageDatas.getPublic_id());
                        }
                        updatePostApi(aLUpdateProductImage);
                    }
                }

                @Override
                public void onError(String error)
                {
                    if (mDialogBox.progressBarDialog!=null)
                        mDialogBox.progressBarDialog.dismiss();
                    Log.d("fdsf324"," "+error);
                }
            });
        }
        catch(OutOfMemoryError e){
            if (mDialogBox.progressBarDialog!=null)
                mDialogBox.progressBarDialog.dismiss();
            CommonClass.showSnackbarMessage(rL_rootElement,getString(R.string.failedToUpload));
        }
    }

    /**
     * <h>UpdatePostApi</h>
     * <p>
     *     In this method we used we used to call update post api for modification
     *     in the existing products.
     * </p>
     * @param aLProductImageDatases The list containing the image description like main image, thumbnail image, width and height
     */
    private void updatePostApi(List<ProductImageDatas> aLProductImageDatases)
    {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            category=tV_category.getText().toString();
            if (!category.isEmpty())
                category=category.trim();

            JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("token",mSessionManager.getAuthToken());
                request_datas.put("postId",postId);
                request_datas.put("type","0");
                request_datas.put("subCategory","");
                request_datas.put("category",tV_category.getText().toString());
                System.out.println(TAG+" "+"category name="+tV_category.getText().toString());

                // product main image, thumbnail image, width and height
                request_datas.put("mainUrl",aLProductImageDatases.get(0).getMainUrl());
                request_datas.put("thumbnailImageUrl", aLProductImageDatases.get(0).getThumbnailUrl());
                request_datas.put("containerHeight",aLProductImageDatases.get(0).getHeight());
                request_datas.put("containerWidth",aLProductImageDatases.get(0).getWidth());
                request_datas.put("imageCount",aLProductImageDatases.size());
                request_datas.put("cloudinaryPublicId",aLProductImageDatases.get(0).getPublic_id());

                Log.d("cloudinaryPublicId110",aLProductImageDatases.get(0).getPublic_id());

                request_datas.put("price",eT_price.getText().toString());
                request_datas.put("currency",tV_currency.getText().toString());
                request_datas.put("productName",eT_title.getText().toString());
                request_datas.put("condition", tV_condition.getText().toString());
                request_datas.put("description",eT_description.getText().toString());
                request_datas.put("negotiable",negotiable);
                request_datas.put("location",tV_current_location.getText().toString());
                request_datas.put("latitude",latitude);
                request_datas.put("longitude",longitude);
                request_datas.put("city",city);
                request_datas.put("countrySname",countrySname);
                request_datas.put("tagProduct", "");
                request_datas.put("tagProductCoordinates", "");

                // Second Image
                if(aLProductImageDatases.size()>1)
                {
                    request_datas.put("thumbnailUrl1",aLProductImageDatases.get(1).getThumbnailUrl());
                    request_datas.put("imageUrl1",aLProductImageDatases.get(1).getMainUrl());
                    request_datas.put("containerHeight1",aLProductImageDatases.get(1).getHeight());
                    request_datas.put("containerWidth1",aLProductImageDatases.get(1).getWidth());
                    request_datas.put("cloudinaryPublicId1",aLProductImageDatases.get(1).getPublic_id());

//                    Log.d("cloudinaryPublicId111",aLProductImageDatases.get(1).getPublic_id());

                }else
                {
                    request_datas.put("thumbnailUrl1", "");
                    request_datas.put("imageUrl1", "");
                    request_datas.put("containerHeight1", "");
                    request_datas.put("containerWidth1", "");
                    request_datas.put("cloudinaryPublicId1","");
 //                   Log.d("cloudinaryPublicId111",aLProductImageDatases.get(1).getPublic_id());
                }

                // Third Image
                if(aLProductImageDatases.size()>2)
                {
                    request_datas.put("imageUrl2",aLProductImageDatases.get(2).getMainUrl());
                    request_datas.put("thumbnailUrl2",aLProductImageDatases.get(2).getThumbnailUrl());
                    request_datas.put("containerHeight2",aLProductImageDatases.get(2).getHeight());
                    request_datas.put("containerWidth2",aLProductImageDatases.get(2).getWidth());
                    request_datas.put("cloudinaryPublicId2",aLProductImageDatases.get(2).getPublic_id());
//                    Log.d("cloudinaryPublicId112",aLProductImageDatases.get(2).getPublic_id());

                }else
                {
                    request_datas.put("imageUrl2", "");
                    request_datas.put("thumbnailUrl2", "");
                    request_datas.put("containerHeight2", "");
                    request_datas.put("containerWidth2", "");
                    request_datas.put("cloudinaryPublicId2","");
//                    Log.d("cloudinaryPublicId112",aLProductImageDatases.get(2).getPublic_id());
                }

                // Fourth Image
                if(aLProductImageDatases.size()>3)
                {
                    request_datas.put("imageUrl3",aLProductImageDatases.get(3).getMainUrl());
                    request_datas.put("thumbnailUrl3",aLProductImageDatases.get(3).getThumbnailUrl());
                    request_datas.put("containerHeight3",aLProductImageDatases.get(3).getHeight());
                    request_datas.put("containerWidth3",aLProductImageDatases.get(3).getWidth());
                    request_datas.put("cloudinaryPublicId3",aLProductImageDatases.get(3).getPublic_id());

//                    Log.d("cloudinaryPublicId113",aLProductImageDatases.get(3).getPublic_id());
                }else
                {
                    request_datas.put("imageUrl3","");
                    request_datas.put("thumbnailUrl3","");
                    request_datas.put("containerHeight3","");
                    request_datas.put("containerWidth3","");
                    request_datas.put("cloudinaryPublicId3","");
//                    Log.d("cloudinaryPublicId113",aLProductImageDatases.get(3).getPublic_id());
                }

                // Fifth Image
                if(aLProductImageDatases.size()>4)
                {
                    request_datas.put("imageUrl4",aLProductImageDatases.get(4).getMainUrl());
                    request_datas.put("thumbnailUrl4",aLProductImageDatases.get(4).getThumbnailUrl());
                    request_datas.put("containerHeight4",aLProductImageDatases.get(4).getHeight());
                    request_datas.put("containerWidth4",aLProductImageDatases.get(4).getWidth());
                    request_datas.put("cloudinaryPublicId4",aLProductImageDatases.get(4).getPublic_id());

//                    Log.d("cloudinaryPublicId114",aLProductImageDatases.get(4).getPublic_id());
                }else
                {
                    request_datas.put("imageUrl4", "");
                    request_datas.put("thumbnailUrl4", "");
                    request_datas.put("containerHeight4", "");
                    request_datas.put("containerWidth4", "");
                    request_datas.put("cloudinaryPublicId4","");
//                    Log.d("cloudinaryPublicId114",aLProductImageDatases.get(4).getPublic_id());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.PRODUCT, OkHttp3Connection.Request_type.PUT, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    System.out.println(TAG + " " + "update product res=" + result);
                    if (mDialogBox.progressBarDialog!=null)
                        mDialogBox.progressBarDialog.dismiss();
                    //tV_post.setVisibility(View.VISIBLE);

                    UpdateProductMainPojo updateProductMainPojo;
                    Gson gson = new Gson();
                    updateProductMainPojo = gson.fromJson(result,UpdateProductMainPojo.class);

                    switch (updateProductMainPojo.getCode())
                    {
                        // success
                        case "200" :
                            UpdateProductData data=updateProductMainPojo.getData().get(0);
                            AppController.getInstance().getDbController().updateAllProduct(data.getPostId(),data.getMainUrl(),data.getProductName(),data.getPrice(),data.getNegotiable(),data.getCurrency());
                            Intent intent = new Intent(mActivity, HomePageActivity.class);
                           // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mActivity.startActivity(intent);
                            break;

                        default:
                            CommonClass.showSnackbarMessage(rL_rootElement,result);
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    if (mDialogBox.progressBarDialog!=null)
                        mDialogBox.progressBarDialog.dismiss();
                    //tV_post.setVisibility(View.VISIBLE);
                    CommonClass.showSnackbarMessage(rL_rootElement,error);
                }
            });
        }
        else CommonClass.showSnackbarMessage(rL_rootElement,getResources().getString(R.string.NoInternetAccess));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data!=null) {
            switch (requestCode) {
                case VariableConstants.UPDATE_IMAGE_REQ_CODE:
                    arrayListImgPath = data.getStringArrayListExtra("arrayListImgPath");
                    rotationAngles = data.getIntegerArrayListExtra("rotationAngles");

                    System.out.println(TAG + " " + "receive image count=" + arrayListImgPath.size());
                    if (arrayListImgPath.size()>0)
                    {
                        //for (String imagePath : arrayListImgPath)
                        for (int imgPathCount=0; imgPathCount<arrayListImgPath.size(); imgPathCount++)
                        {
                            ProductImageDatas productImageDatas = new ProductImageDatas();
                            productImageDatas.setMainUrl(arrayListImgPath.get(imgPathCount));
                            productImageDatas.setImageUrl(false);
                            productImageDatas.setRotationAngle(rotationAngles.get(imgPathCount));
                            aLProductImageDatases.add(productImageDatas);
                        }
                        imagesHorizontalRvAdap.notifyDataSetChanged();
                    }
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
                    }
                    break;


                // modelName  MODEL_REQUEST_MAKE_CODE
                case VariableConstants.MODEL_REQUEST_YEAR_CODE :
                    String yearName=data.getStringExtra("yearName");
                    if (yearName!=null)
                    {
                        mTvCategoryYears.setText(yearName);
                        mTvCategoryYears.requestFocus();
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

                // category name
                case VariableConstants.CATEGORY_REQUEST_CODE :
                    String categoryName=data.getStringExtra("categoryName");
                    if (categoryName!=null)
                        tV_category.setText(categoryName);
                    break;

                // condition
                case VariableConstants.CONDITION_REQUEST_CODE :
                    String condition=data.getStringExtra("condition");
                    if (condition!=null)
                        tV_condition.setText(condition);
                    break;

                // currency symbol
                case VariableConstants.CURRENCY_REQUEST_CODE :
                    String cuurency_code=data.getStringExtra("cuurency_code");
                    String currency_symbol = data.getStringExtra("currency_symbol");

                    // set currency cde eg. Inr
                    if (cuurency_code!=null)
                        tV_currency.setText(cuurency_code);

                    // set currency symbol eg. $
                    if (currency_symbol !=null)
                        tV_currency_symbol.setText(currency_symbol);
                    break;

                // selected location
                case VariableConstants.CHANGE_LOC_REQ_CODE :
                    String placeName=data.getStringExtra("address");
                    latitude = data.getStringExtra("lat");
                    longitude= data.getStringExtra("lng");

                    //..find city and countrysname from chnaged lat long
                    city=CommonClass.getCityName(mActivity,Double.parseDouble(latitude),Double.parseDouble(longitude));
                    countrySname=CommonClass.getCountryCode(mActivity,Double.parseDouble(latitude),Double.parseDouble(longitude));

                    System.out.println(TAG+" "+"place name="+placeName+" "+latitude+" "+longitude);
                    if (placeName!=null)
                        tV_current_location.setText(placeName);
                    break;
            }
        }
    }

    /**
     * <h>deleteAllCapturedImages</h>
     * <p>
     *     In this method we used to delete all the captured images which is taken by the camera.
     * </p>
     * @param mList The list containing the images
     */
    public void deleteAllCapturedImages(ArrayList<String>  mList) {
        try {
            File f;
            boolean deleted = false;

            for (int i = 0; i < mList.size(); i++) {

                f = new File(mList.get(i));
                if (f.exists()) {
                    f.delete();
                    deleted = true;
                }
            }

            if (deleted) {
                MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStorageDirectory().toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                    /*
                     *   (non-Javadoc)
                     * @see android.media.MediaScannerConnection.OnScanCompletedListener#onScanCompleted(java.lang.String, android.net.Uri)
                     */
                    public void onScanCompleted(String path, Uri uri) {

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.postRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }
}
