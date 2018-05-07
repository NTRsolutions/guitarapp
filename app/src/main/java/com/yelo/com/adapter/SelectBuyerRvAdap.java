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
import com.yelo.com.main.activity.SelectBuyerActivity;
import com.yelo.com.pojo_class.accepted_offer.AcceptedOfferDatas;
import com.yelo.com.utility.CircleTransform;
import com.yelo.com.utility.ClickListener;
import com.yelo.com.utility.CommonClass;
import com.yelo.com.utility.DialogBox;

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
public class SelectBuyerRvAdap extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Activity mActivity;
    private ArrayList<AcceptedOfferDatas> arrayListAcceptedOffer;
    private static final int TYPE_ITEM=1;
    private static final int TYPE_FOOTER=2;
    private ClickListener clickListener;
    private static final String TAG = SelectBuyerRvAdap.class.getSimpleName();
    private DialogBox mDialogBox;

    /**
     * <h>CurrencyRvAdap</h>
     * <p>
     *     This is simple constructor to initailize list datas and context.
     * </p>
     * @param mActivity The current context
     * @param arrayListAcceptedOffer The list datas
     */
    public SelectBuyerRvAdap(Activity mActivity, ArrayList<AcceptedOfferDatas> arrayListAcceptedOffer) {
        this.mActivity = mActivity;
        this.arrayListAcceptedOffer = arrayListAcceptedOffer;
        mDialogBox= new DialogBox(mActivity);
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        switch (viewType)
        {
            // Item
            case TYPE_ITEM :
                view= LayoutInflater.from(mActivity).inflate(R.layout.single_row_select_buyer,parent,false);
                return new MyViewHolder(view);

            // Footer
            case TYPE_FOOTER :
                view= LayoutInflater.from(mActivity).inflate(R.layout.footer_select_buyer,parent,false);
                return new FooterViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        // For Item
        if (holder instanceof MyViewHolder)
        {
            MyViewHolder myViewHolder= (MyViewHolder) holder;
            String buyerName,buyerImage,price,currency;
            buyerName=arrayListAcceptedOffer.get(position).getBuyerFullName();
            buyerImage=arrayListAcceptedOffer.get(position).getBuyerProfilePicUrl();
            price=arrayListAcceptedOffer.get(position).getPrice();
            currency=arrayListAcceptedOffer.get(position).getCurrency();

            // set Profile pic
            if (buyerImage!=null && !buyerImage.isEmpty())
                Picasso.with(mActivity)
                        .load(buyerImage)
                        .placeholder(R.drawable.default_circle_img)
                        .error(R.drawable.default_circle_img)
                        .transform(new CircleTransform())
                        .into(myViewHolder.image);

            // set buyer name
            if (buyerName!=null && !buyerName.isEmpty())
                myViewHolder.tV_heading.setText(buyerName);

            // set currency
            if (currency!=null && !currency.isEmpty())
            {
                Currency c  = Currency.getInstance(currency);
                currency=c.getSymbol();
            }

            String setPrice=mActivity.getResources().getString(R.string.price_offered)+" "+currency+price;
            myViewHolder.tV_subHeading.setText(setPrice);
        }

        // For footer
        if (holder instanceof FooterViewHolder)
        {
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            footerViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String postId = ((SelectBuyerActivity)mActivity).postId;
                    if (postId!=null && !postId.isEmpty())
                    mDialogBox.sellSomeWhereDialog(postId,((SelectBuyerActivity)mActivity).rL_rootElement);
                }
            });
        }
    }

    /**
     * Return the size of your dataset
     * @return the total number of rows
     */
    @Override
    public int getItemCount() {
        return arrayListAcceptedOffer.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if (arrayListAcceptedOffer.size()==position)
            return TYPE_FOOTER;
        return TYPE_ITEM;
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
        TextView tV_heading,tV_subHeading;

        public MyViewHolder(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(v,getAdapterPosition());
                }
            });
            image= (ImageView) itemView.findViewById(R.id.image);
            image.getLayoutParams().width= CommonClass.getDeviceWidth(mActivity)/7;
            image.getLayoutParams().height= CommonClass.getDeviceWidth(mActivity)/7;
            tV_heading= (TextView) itemView.findViewById(R.id.tV_heading);
            tV_subHeading= (TextView) itemView.findViewById(R.id.tV_subHeading);
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {
        FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    public void setItemClick(ClickListener listener)
    {
        clickListener=listener;
    }
}
