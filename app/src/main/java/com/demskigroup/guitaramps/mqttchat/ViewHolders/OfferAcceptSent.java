package com.demskigroup.guitaramps.mqttchat.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.mqttchat.AppController;

/**
 * Created by user on 9/15/2017.
 */

public class OfferAcceptSent extends RecyclerView.ViewHolder
{
    public TextView offerAmount;
    public OfferAcceptSent(View itemView) {
        super(itemView);
        offerAmount=(TextView)itemView.findViewById(R.id.offerAmount);
        offerAmount.setTypeface(AppController.getInstance().getRobotoMediumFont());
        TextView sent_offer_view=(TextView)itemView.findViewById(R.id.sent_offer_view);
        sent_offer_view.setTypeface(AppController.getInstance().getRobotoMediumFont());
    }
}
