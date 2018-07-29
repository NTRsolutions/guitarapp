package com.demskigroup.guitaramps.mqttchat.ViewHolders;

/**
 * Created by moda on 08/08/17.
 */

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.mqttchat.Utilities.SlackLoadingView;


/**
 * View holder for the loading more results item in recycler view
 */

public class ViewHolderLoading extends RecyclerView.ViewHolder {


    public SlackLoadingView slack;


    public ViewHolderLoading(View view) {
        super(view);


        slack = (SlackLoadingView) view.findViewById(R.id.slack);


    }
}
