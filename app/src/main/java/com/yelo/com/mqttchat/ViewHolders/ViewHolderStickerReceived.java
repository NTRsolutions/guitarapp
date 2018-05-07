package com.yelo.com.mqttchat.ViewHolders;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yelo.com.R;
import com.yelo.com.mqttchat.AppController;

public class ViewHolderStickerReceived extends RecyclerView.ViewHolder {


    public TextView senderName, time, date;

    public ImageView imageView, download;


    public RelativeLayout relative_layout_message;

    public ViewHolderStickerReceived(View view) {
        super(view);


        imageView = (ImageView) view.findViewById(R.id.imgshow);

        date = (TextView) view.findViewById(R.id.date);
        time = (TextView) view.findViewById(R.id.ts);

        relative_layout_message = (RelativeLayout) view.findViewById(R.id.relative_layout_message);
        Typeface tf = AppController.getInstance().getRobotoCondensedFont();
        time.setTypeface(tf, Typeface.ITALIC);

        date.setTypeface(tf, Typeface.ITALIC);

    }
}
