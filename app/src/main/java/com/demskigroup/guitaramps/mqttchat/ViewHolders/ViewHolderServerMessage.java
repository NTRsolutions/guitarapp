package com.demskigroup.guitaramps.mqttchat.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.demskigroup.guitaramps.R;


public class ViewHolderServerMessage extends RecyclerView.ViewHolder {

    public TextView serverupdate;
    public View gap;


    public ViewHolderServerMessage(View view) {
        super(view);

        serverupdate = (TextView) view.findViewById(R.id.servermessage);
        gap = view.findViewById(R.id.gap);

    }
}
