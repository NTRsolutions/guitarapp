package com.yelo.com.utility;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.yelo.com.R;

/**
 * Created by hello on 3/23/2017.
 */

public class ProgressBarHandler
{
    private ProgressBar mProgressBar;

    public ProgressBarHandler(Context context) {
        ViewGroup layout = (ViewGroup) ((Activity) context).findViewById(android.R.id.content).getRootView();

        mProgressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleLarge);
        //mProgressBar.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.custom_progress_background));
        mProgressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(context, R.color.login_page_blue_box), android.graphics.PorterDuff.Mode.MULTIPLY);
        mProgressBar.setIndeterminate(true);

        RelativeLayout.LayoutParams params = new
                RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        RelativeLayout rl = new RelativeLayout(context);

        rl.setGravity(Gravity.CENTER);
        rl.addView(mProgressBar);

        layout.addView(rl, params);

        hide();
    }

    public void show() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void hide() {
        mProgressBar.setVisibility(View.INVISIBLE);
    }
}
