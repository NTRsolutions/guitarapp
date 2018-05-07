package com.yelo.com.mqttchat.ViewHolders;
/*
 * Created by moda on 02/04/16.
 */

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.yelo.com.R;
import com.yelo.com.mqttchat.AppController;


/**
 * View holder for text message received recycler view item
 */
public class ViewHolderMessageReceived extends RecyclerView.ViewHolder {


//    public TextView senderName;

    public TextView message, time, date;


    public ViewHolderMessageReceived(View view) {
        super(view);

        date = (TextView) view.findViewById(R.id.date);
        // senderName = (TextView) view.findViewById(R.id.lblMsgFrom);

        message = (TextView) view.findViewById(R.id.txtMsg);

        time = (TextView) view.findViewById(R.id.ts);

     Typeface tf = AppController.getInstance().getRobotoCondensedFont();

        time.setTypeface(tf, Typeface.ITALIC);

      date.setTypeface(tf, Typeface.ITALIC);
    message.setTypeface(tf, Typeface.NORMAL);
    }
}
