package com.demskigroup.guitaramps.main.activity.products;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.adapter.ProductAdapter;

public class ProductPictureDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    TextView textViewProduct;
    RecyclerView recyclerViewProductDetail;
    LinearLayout linearLayoutText;

    Toolbar toolbar;

    String productName;

    Context context;

    String productNameList[] = {"Solid-state amps", "Tube amps", "Modeling amps", "Hybrid amps"
            , "Acoustic amps", "Vintage amps","Bass amps","Parts","Solid-state amps", "Tube amps", "Modeling amps", "Hybrid amps"
            , "Acoustic amps", "Vintage amps","Bass amps","Parts"};

    int images[]={R.drawable.image1,R.drawable.image2,R.drawable.image3,R.drawable.image4,R.drawable.image1,R.drawable.image2,R.drawable.image3,R.drawable.image4,
            R.drawable.image1,R.drawable.image2,R.drawable.image3,R.drawable.image4,R.drawable.image1,R.drawable.image2,R.drawable.image3,R.drawable.image4 };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_picture_details);

        context=this;

        setUpViews();

        setUpData();

        setAdapter();

        setUpListener();
    }

    private void setAdapter() {

//        ProductAdapter productAdaptor = new ProductAdapter(context,productNameList, images);
//        recyclerViewProductDetail.setAdapter(productAdaptor);
    }

    private void setUpData() {

        Intent intent=getIntent();

        productName=intent.getStringExtra("productName");

        setSupportActionBar(toolbar);
        textViewProduct.setText(productName);


    }

    private void setUpListener() {

         textViewProduct.setOnClickListener(this);
    }

    private void setUpViews() {


        textViewProduct=findViewById(R.id.textViewProduct);
        recyclerViewProductDetail=findViewById(R.id.recyclerViewProductDetail);
     //   linearLayoutText=findViewById(R.id.linearLayoutText);
        toolbar=findViewById(R.id.toolbar);




    }



    @Override
    public void onClick(View v) {


        switch (v.getId())
        {
            case R.id.textViewProduct:

                showAlertDialog();

                break;
        }
    }

    private void showAlertDialog() {


        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(productName)
                .setMessage("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })

                .show();
    }
}
