package com.yelo.com.mqttchat.ViewHolders;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yelo.com.R;
import com.yelo.com.mqttchat.AppController;


/**
 * View holder for text message sent recycler view item
 */
public class ViewHolderMessageSent extends RecyclerView.ViewHolder {


//    public TextView senderName;

    public TextView message, time, date;

    public ImageView singleTick, doubleTickGreen, doubleTickBlue, clock;//, blocked;

    public ViewHolderMessageSent(View view) {
        super(view);
//        blocked = (ImageView) view.findViewById(R.id.blocked);
        date = (TextView) view.findViewById(R.id.date);
        // senderName = (TextView) view.findViewById(R.id.lblMsgFrom);

        message = (TextView) view.findViewById(R.id.txtMsg);

        time = (TextView) view.findViewById(R.id.ts);

        singleTick = (ImageView) view.findViewById(R.id.single_tick_green);

        doubleTickGreen = (ImageView) view.findViewById(R.id.double_tick_green);

        doubleTickBlue = (ImageView) view.findViewById(R.id.double_tick_blue);
        clock = (ImageView) view.findViewById(R.id.clock);

        Typeface tf = AppController.getInstance().getRobotoCondensedFont();
        time.setTypeface(tf, Typeface.ITALIC);

        date.setTypeface(tf, Typeface.ITALIC);
        message.setTypeface(tf, Typeface.NORMAL);
    }
}
