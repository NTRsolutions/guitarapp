package com.yelo.com.mqttchat.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.yelo.com.R;
import com.yelo.com.mqttchat.AppController;

/**
 * @since 9/20/2017.
 */
public class PaypalLinkReceived extends RecyclerView.ViewHolder
{
    public RelativeLayout relative_layout_message;
    public TextView offerAmount;
    public PaypalLinkReceived(View itemView)
    {
        super(itemView);
        offerAmount=(TextView)itemView.findViewById(R.id.offerAmount);
        offerAmount.setTypeface(AppController.getInstance().getRobotoMediumFont());
        relative_layout_message=(RelativeLayout)itemView.findViewById(R.id.relative_layout_message);
        TextView offerAmount=(TextView)itemView.findViewById(R.id.offerAmount);
        offerAmount.setTypeface(AppController.getInstance().getRobotoMediumFont());
    }
}
