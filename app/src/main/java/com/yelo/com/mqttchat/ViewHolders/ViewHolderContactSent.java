package com.yelo.com.mqttchat.ViewHolders;


import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.yelo.com.R;
import com.yelo.com.mqttchat.AppController;
/**
 * View holder for contact sent recycler view item
 */
public class ViewHolderContactSent extends RecyclerView.ViewHolder {

//    public  TextView senderName;
    public TextView time, contactName, contactNumber, date;
    public ImageView singleTick, doubleTickGreen, doubleTickBlue, clock;//,blocked;


    public ViewHolderContactSent(View view) {
        super(view);

        // senderName = (TextView) view.findViewById(R.id.lblMsgFrom);
        contactName = (TextView) view.findViewById(R.id.contactName);

        contactNumber = (TextView) view.findViewById(R.id.contactNumber);
        date = (TextView) view.findViewById(R.id.date);
//        blocked = (ImageView) view.findViewById(R.id.blocked);
        time = (TextView) view.findViewById(R.id.ts);

        singleTick = (ImageView) view.findViewById(R.id.single_tick_green);

        doubleTickGreen = (ImageView) view.findViewById(R.id.double_tick_green);

        doubleTickBlue = (ImageView) view.findViewById(R.id.double_tick_blue);

        clock = (ImageView) view.findViewById(R.id.clock);
        Typeface tf = AppController.getInstance().getRobotoCondensedFont();
        time.setTypeface(tf, Typeface.ITALIC);

        date.setTypeface(tf, Typeface.ITALIC);
        contactName.setTypeface(tf, Typeface.NORMAL);
        contactNumber.setTypeface(tf, Typeface.NORMAL);
    }
}
