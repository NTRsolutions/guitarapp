package com.yelo.com.mqttchat.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yelo.com.R;

public class ViewHolderSelectUser extends RecyclerView.ViewHolder {
    public TextView userName, userIdentifier;
    public ImageView userImage;

    public ViewHolderSelectUser(View view) {
        super(view);


        userName = (TextView) view.findViewById(R.id.userName);
        userIdentifier = (TextView) view.findViewById(R.id.userIdentifier);

        userImage = (ImageView) view.findViewById(R.id.userImage);

    }
}
