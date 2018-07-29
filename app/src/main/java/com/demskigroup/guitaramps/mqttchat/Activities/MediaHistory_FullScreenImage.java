package com.demskigroup.guitaramps.mqttchat.Activities;



import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.mqttchat.Utilities.TouchImageView;


/*
*
* Activity containing the full screen imageview with functionality to pinch and zoom
*
* */
public class MediaHistory_FullScreenImage extends AppCompatActivity {

    private TouchImageView imgDisplay;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_fullscreen_image);
        imgDisplay = (TouchImageView) findViewById(R.id.imgDisplay);
        setupActivity(getIntent());
        ImageView close = (ImageView) findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }


    @Override
    public void onBackPressed() {


        super.onBackPressed();
        supportFinishAfterTransition();


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setupActivity(intent);


    }

    @SuppressWarnings("TryWithIdenticalCatches")
    private void setupActivity(Intent intent) {


        Bundle extras = intent.getExtras();
        if (extras != null) {

            String path = extras.getString("imagePath");

            try {

                Glide
                        .with(MediaHistory_FullScreenImage.this)
                        .load(path)


                        .crossFade()
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)

                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                imgDisplay.setBackgroundColor(ContextCompat.getColor(MediaHistory_FullScreenImage.this, R.color.color_black));
                                return false;
                            }
                        })
                        .into(imgDisplay);


            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }


    }


}
