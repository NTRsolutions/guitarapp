package com.yelo.com.mqttchat.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.yelo.com.R;
import com.yelo.com.mqttchat.AppController;

/**
 * @since  9/11/2017.
 */
public class OfferSentView extends RecyclerView.ViewHolder
{
    public TextView offerAmount;

    public OfferSentView(View itemView)
    {
        super(itemView);
        offerAmount=(TextView)itemView.findViewById(R.id.offerAmount);
        offerAmount.setTypeface(AppController.getInstance().getRobotoMediumFont());
        TextView sent_offer_view=(TextView)itemView.findViewById(R.id.sent_offer_view);
        sent_offer_view.setTypeface(AppController.getInstance().getRobotoMediumFont());
    }
}
