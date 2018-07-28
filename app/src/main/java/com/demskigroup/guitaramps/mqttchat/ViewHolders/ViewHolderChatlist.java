package com.demskigroup.guitaramps.mqttchat.ViewHolders;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.mqttchat.AppController;


public class ViewHolderChatlist extends RecyclerView.ViewHolder {
    public TextView newMessageTime, newMessage, storeName, newMessageDate, newMessageCount;
    public ImageView storeImage, tick;
    public RelativeLayout rl;

    public ViewHolderChatlist(View view) {
        super(view);


        newMessageTime = (TextView) view.findViewById(R.id.newMessageTime);
        newMessage = (TextView) view.findViewById(R.id.newMessage);
        newMessageDate = (TextView) view.findViewById(R.id.newMessageDate);
        storeName = (TextView) view.findViewById(R.id.storeName);
        storeImage = (ImageView) view.findViewById(R.id.storeImage2);
        tick = (ImageView) view.findViewById(R.id.tick);
        rl = (RelativeLayout) view.findViewById(R.id.rl);


        newMessageCount = (TextView) view.findViewById(R.id.newMessageCount);


        Typeface tf = AppController.getInstance().getRobotoCondensedFont();


        newMessageCount.setTypeface(tf, Typeface.BOLD);
        newMessageDate.setTypeface(tf, Typeface.NORMAL);
        newMessageTime.setTypeface(tf, Typeface.NORMAL);
        newMessage.setTypeface(tf, Typeface.NORMAL);
    }
}
