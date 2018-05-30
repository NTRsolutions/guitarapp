package com.yelo.com.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.yelo.com.R;
import com.yelo.com.pojo_class.accepted_offer.AcceptedOfferDatas;
import com.yelo.com.utility.CircleTransform;
import com.yelo.com.utility.CommonClass;
import java.util.ArrayList;
import java.util.Currency;

/**
 * <h>HomeFragRvAdapter</h>
 * <p>
 *     In class is called from EditProductActivity class. In this recyclerview adapter class we used to inflate
 *     single_row_accepted_offer layout and shows the all accepted offer.
 * </p>
 * @since 13-Jul-17
 */
public class AcceptedOfferRvAdap extends RecyclerView.Adapter<AcceptedOfferRvAdap.MyViewHolder>
{
    private Activity mActivity;
    private ArrayList<AcceptedOfferDatas> arrayListAcceptedOffer;

    /**
     * <h>CurrencyRvAdap</h>
     * <p>
     *     This is simple constructor to initailize list datas and context.
     * </p>
     * @param mActivity The current context
     * @param arrayListAcceptedOffer The list datas
     */
    public AcceptedOfferRvAdap(Activity mActivity, ArrayList<AcceptedOfferDatas> arrayListAcceptedOffer) {
        this.mActivity = mActivity;
        this.arrayListAcceptedOffer = arrayListAcceptedOffer;
    }

    /**
     * <h>OnCreateViewHolder</h>
     * <p>
     *     In this method The adapter prepares the layout of the items by inflating the correct
     *     layout for the individual data elements.
     * </p>
     * @param parent A ViewGroup is a special view that can contain other views (called children.)
     * @param viewType Within the getItemViewType method the recycler view determines which type should be used for data.
     * @return It returns an object of type ViewHolder per visual entry in the recycler view.
     */
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mActivity).inflate(R.layout.single_row_accepted_offer,parent,false);
        return new MyViewHolder(view);
    }

    /**
     * <h>OnBindViewHolder</h>
     * <p>
     *     In this method Every visible entry in a recycler view is filled with the
     *     correct data model item by the adapter. Once a data item becomes visible,
     *     the adapter assigns this data to the individual widgets which he inflated
     *     earlier.
     * </p>
     * @param holder The referece of MyViewHolder class of current class.
     * @param position The position of particular item
     */
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String buyerName,buyerImage,price,currency,offerCreatedOn;
        buyerName=arrayListAcceptedOffer.get(position).getBuyerFullName();
        buyerImage=arrayListAcceptedOffer.get(position).getBuyerProfilePicUrl();
        price=arrayListAcceptedOffer.get(position).getPrice();
        currency=arrayListAcceptedOffer.get(position).getCurrency();

        // set Profile pic
        if (buyerImage!=null && !buyerImage.isEmpty())
            Picasso.with(mActivity)
                    .load(buyerImage)
                    .placeholder(R.drawable.profile_bg)
                    .error(R.drawable.profile_bg)
                    .transform(new CircleTransform())
                    .into(holder.image);

        // set buyer name
        if (buyerName!=null && !buyerName.isEmpty())
            holder.tV_heading.setText(buyerName);

        // set currency
        if (currency!=null && !currency.isEmpty())
        {
            Currency c  = Currency.getInstance(currency);
            currency=c.getSymbol();
        }

        String setPrice=mActivity.getResources().getString(R.string.price_offered)+" "+currency+price;
        holder.tV_subHeading.setText(setPrice);

        // posted on
        offerCreatedOn=arrayListAcceptedOffer.get(position).getOfferCreatedOn();
        if (offerCreatedOn!=null && !offerCreatedOn.isEmpty())
            holder.tV_postedOn.setText(CommonClass.getTimeDifference(offerCreatedOn));
    }

    /**
     * Return the size of your dataset
     * @return the total number of rows
     */
    @Override
    public int getItemCount() {
        return arrayListAcceptedOffer.size();
    }

    /**
     * <h>MyViewHolder</h>
     * <p>
     *     In this class we used to declare and assign the xml variables.
     * </p>
     */
    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        ImageView image;
        TextView tV_heading,tV_subHeading,tV_postedOn;

        public MyViewHolder(View itemView) {
            super(itemView);

            image= (ImageView) itemView.findViewById(R.id.image);
            image.getLayoutParams().width= CommonClass.getDeviceWidth(mActivity)/7;
            image.getLayoutParams().height= CommonClass.getDeviceWidth(mActivity)/7;
            tV_heading= (TextView) itemView.findViewById(R.id.tV_heading);
            tV_subHeading= (TextView) itemView.findViewById(R.id.tV_subHeading);
            tV_postedOn= (TextView) itemView.findViewById(R.id.tV_postedOn);
        }
    }
}
