package com.yelo.com.mqttchat.Activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.yelo.com.R;

/**
 * Activity containing the full screen videoview to play video incase android video player is not found
 */
public class MediaHistory_FullScreenVideo extends AppCompatActivity {

    private VideoView video;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_fullscreen_video);

        video = (VideoView) findViewById(R.id.video);
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

    private void setupActivity(Intent intent) {

        Bundle extras = intent.getExtras();
        if (extras != null) {

            String path = extras.getString("videoPath");
            try {
                if (extras.containsKey("flag")) {


                    video.setVideoURI(Uri.parse(path));


                } else {

                    video.setVideoPath(path);
                }

            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }


            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(video);

            video.setMediaController(mediaController);


//            video.seekTo(2);
            video.start();


            video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    onBackPressed();
                }
            });
        }
    }

}
