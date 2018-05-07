package com.yelo.com.main.activity.products;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.yelo.com.R;
import com.yelo.com.adapter.ProductAdapter;

public class ProductPictureActivity extends AppCompatActivity {

    RecyclerView recyclerViewProductPicture;

    Context context;

    String productName[] = {"Solid-state amps", "Tube amps", "Modeling amps", "Hybrid amps"
            , "Acoustic amps", "Vintage amps","Bass amps","Parts"};

    int images[]={R.drawable.image1,R.drawable.image2,R.drawable.image3,R.drawable.image4,R.drawable.image1,R.drawable.image2,R.drawable.image3,R.drawable.image4};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_picture);

        context = this;

        setUpViews();
    }

    private void setUpViews() {

//        recyclerViewProductPicture = findViewById(R.id.recyclerViewProductPicture);
//
//        ProductAdapter productAdaptor = new ProductAdapter(getApplicationContext(),productName,images);
//        recyclerViewProductPicture.setAdapter(productAdaptor);
    }
}
