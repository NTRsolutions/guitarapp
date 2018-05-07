package com.yelo.com.mqttchat.Giphy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yelo.com.R;


public class GifPlayer extends AppCompatActivity {

    private ImageView giphyIv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gif_player);

        giphyIv = (ImageView) findViewById(R.id.giphyIV);
        setUpActivity(getIntent());


        ImageView close = (ImageView) findViewById(R.id.close);


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);


        setUpActivity(intent);
    }


    private void setUpActivity(Intent intent) {


        String gifUrl = intent.getStringExtra("gifUrl");

        try {

            Glide.with(this)
                    .load(gifUrl)
                    .asGif()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)

                    .crossFade()
                    .into(giphyIv);

        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onBackPressed() {


        super.onBackPressed();
        supportFinishAfterTransition();


    }

}
