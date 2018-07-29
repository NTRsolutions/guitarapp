package com.demskigroup.guitaramps.mqttchat.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.mqttchat.AppController;

/**
 * @since  9/15/2017.
 */
public class OfferAcceptedView extends RecyclerView.ViewHolder
{
    public TextView text_msg;
    public OfferAcceptedView(View itemView)
    {
        super(itemView);
        text_msg=(TextView)itemView.findViewById(R.id.sent_offer_view);
        text_msg.setTypeface(AppController.getInstance().getRobotoMediumFont());
        TextView accept_text=(TextView)itemView.findViewById(R.id.accept_text);
        accept_text.setTypeface(AppController.getInstance().getRobotoMediumFont());
    }
}
