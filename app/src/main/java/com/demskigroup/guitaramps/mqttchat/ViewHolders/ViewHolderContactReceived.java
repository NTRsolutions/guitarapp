package com.demskigroup.guitaramps.mqttchat.ViewHolders;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.mqttchat.AppController;


/**
 * View holder for contact received recycler view item
 */
public class ViewHolderContactReceived extends RecyclerView.ViewHolder {
//    public  TextView senderName;


    public TextView time, contactName, contactNumber, date;

    public ViewHolderContactReceived(View view) {
        super(view);

        date = (TextView) view.findViewById(R.id.date);
        //  senderName = (TextView) view.findViewById(R.id.lblMsgFrom);


        time = (TextView) view.findViewById(R.id.ts);

        contactName = (TextView) view.findViewById(R.id.contactName);

        contactNumber = (TextView) view.findViewById(R.id.contactNumber);
        Typeface tf = AppController.getInstance().getRobotoCondensedFont();
        time.setTypeface(tf, Typeface.ITALIC);

        date.setTypeface(tf, Typeface.ITALIC);
        contactName.setTypeface(tf, Typeface.NORMAL);
        contactNumber.setTypeface(tf, Typeface.NORMAL);

    }
}
