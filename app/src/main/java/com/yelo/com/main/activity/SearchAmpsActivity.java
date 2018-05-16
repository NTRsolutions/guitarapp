package com.yelo.com.main.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.yelo.com.R;
import com.yelo.com.adapter.SearchAmpsAdapter;
import com.yelo.com.adapter.StoreImagesHorizontalRvAdapter;
import com.yelo.com.device_camera.HandleCameraEvents;
import com.yelo.com.pojo_class.productcategory.Homecat;
import com.yelo.com.pojo_class.productcategory.Searchamp;
import com.yelo.com.utility.ApiUrl;
import com.yelo.com.utility.ClickListener;
import com.yelo.com.utility.CommonClass;
import com.yelo.com.utility.OkHttp3Connection;
import com.yelo.com.utility.RunTimePermission;
import com.yelo.com.utility.SimpleDividerItemDecoration;
import com.yelo.com.utility.VariableConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import co.simplecrop.android.simplecropimage.CropImage;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.yelo.com.utility.VariableConstants.TEMP_PHOTO_FILE_NAME;

public class SearchAmpsActivity extends AppCompatActivity implements View.OnClickListener{

    RecyclerView mRvSearchAmps;
    private RecyclerView.LayoutManager layoutManager;
    SearchAmpsAdapter adapter;
    List<Searchamp> list = new ArrayList<>(  );
    public EditText eT_search_users;
    public static int numForCheckOnAdapter  = 0;
    public static  String searchCategory, make ;

    private static final String TAG = SearchAmpsActivity.class.getSimpleName();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        setContentView( R.layout.activity_search_amps);
        initVariable();

        setadpater();

    }

    private void initVariable(){

        try {
            mRvSearchAmps = findViewById( R.id.rv_search_amps );
            RelativeLayout rL_close= (RelativeLayout) findViewById(R.id.rL_close);
            rL_close.setOnClickListener(this);
            eT_search_users= (EditText) findViewById(R.id.eT_search_users);
            eT_search_users.setHint("Search Amps");


            eT_search_users.addTextChangedListener( new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                    if(s.length() >= 2){

                        // TODO call web service
                        hitApiOnServer(s.toString());

                    }

                }
            } );
        }catch (Exception e){
            e.printStackTrace();
        }

    }



    private void setadpater(){
        if(adapter == null){

            adapter=new SearchAmpsAdapter(SearchAmpsActivity.this,list);

            LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);

            mRvSearchAmps.setLayoutManager(linearLayoutManager);
            mRvSearchAmps.setAdapter(adapter);
        }else {
            adapter.notifyDataSetChanged();
        }


        adapter.setOnItemClick( new ClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                searchCategory = list.get( position ).getCategory();
                make = list.get( position ).getManufacturer();
//                Toast.makeText( SearchAmpsActivity.this, ""+list.get( position ).getCategory(), Toast.LENGTH_SHORT ).show();

                onBackPressed();
                finish();


            }
        } );

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


    private void hitApiOnServer(String searchKey){
        if (CommonClass.isNetworkAvailable(SearchAmpsActivity.this))
        {
            final ProgressDialog pDialog = new ProgressDialog(SearchAmpsActivity.this, 0);


            pDialog.setCancelable(false);

//        pDialog.setTitle(R.string.string_351);

            pDialog.setMessage("Searching amps");
            pDialog.show();
            JSONObject request_param=new JSONObject();
            try {
                request_param.put( "searchkey", searchKey );
            } catch (JSONException e) {
                e.printStackTrace();
            }


            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.SEARCH_AMPS, OkHttp3Connection.Request_type.POST, request_param ,new OkHttp3Connection.OkHttp3RequestCallback()
            {
                @Override
                public void onSuccess(String result, String user_tag) {


                    if(pDialog != null){
                        if (pDialog.isShowing()){
                            pDialog.dismiss();
                        }
                    }

                    Homecat homecat;
                    Gson gson=new Gson();
                    homecat=gson.fromJson(result,Homecat.class);

                    switch (homecat.getCode())
                    {
                        // success i.e email is not registered
                        case 200 :
                           if(homecat.getSearchamp().size() > 0){

                               list.clear();
                               list.addAll( homecat.getSearchamp() );
                               setadpater();

                           }


                            break;

                        case 204 :
                            CommonClass.showSnackbarMessage(mRvSearchAmps,homecat.getMessage());
                            list.clear();
                            setadpater();

                            break;

                        // auth token expired
                        case 401 :
                            CommonClass.sessionExpired(SearchAmpsActivity.this);
                            break;
                        // error like email is already registered
                        default:

                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    if(pDialog != null){
                        if (pDialog.isShowing()){
                            pDialog.dismiss();
                        }
                    }
                }
            });
        }
    }
}
