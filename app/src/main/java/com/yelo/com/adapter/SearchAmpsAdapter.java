package com.yelo.com.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.yelo.com.R;
import com.yelo.com.mqttchat.AppController;
import com.yelo.com.pojo_class.productcategory.Searchamp;
import com.yelo.com.pojo_class.search_people_pojo.SearchPeopleUsers;
import com.yelo.com.utility.CircleTransform;
import com.yelo.com.utility.ClickListener;
import com.yelo.com.utility.CommonClass;

import java.util.ArrayList;
import java.util.List;

/**
 * <h>SearchPeopleRvAdap</h>
 * <p>
 *     This class is called from PeoplesFrag class. In this recyclerview adapter class we used to inflate
 *     single_row_search_product layout and shows all searched people.
 * </p>
 * @since 29-Jun-17
 */
public class SearchAmpsAdapter extends RecyclerView.Adapter<SearchAmpsAdapter.MyViewHolder>
{
    private Activity mActivity;
    private List<Searchamp> aL_users;
    private ClickListener clickListener;

    /**
     * <p>
     *     This is simple constructor to initailize list datas and context.
     * </p>
     * @param mActivity The current context
     * @param aL_users The list datas
     */
    public SearchAmpsAdapter(Activity mActivity, List<Searchamp> aL_users) {
        this.mActivity = mActivity;
        this.aL_users = aL_users;
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
        View view= LayoutInflater.from(mActivity).inflate(R.layout.item_search_amps,parent,false);
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
    public void onBindViewHolder(MyViewHolder holder, int position)
    {

        holder.tV_heading.setText( aL_users.get( position ).getManufacturer() );
        holder.tV_subHeading.setText("In "+aL_users.get( position ).getCategory() );


        Glide.with( AppController.getInstance() ).load(  aL_users.get( position ).getImage() ).into( holder.image );



    }

    /**
     * Return the size of your dataset
     * @return the total number of rows
     */
    @Override
    public int getItemCount() {
        return aL_users.size();
    }

    /**
     * Recycler View item variables
     */
    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        ImageView image;
        TextView tV_heading,tV_subHeading;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener!=null)
                        clickListener.onItemClick(v,getAdapterPosition());
                }
            });
            image= (ImageView) itemView.findViewById(R.id.image_view_search_amps);
//            image.getLayoutParams().width= CommonClass.getDeviceWidth(mActivity)/7;
//            image.getLayoutParams().height= CommonClass.getDeviceWidth(mActivity)/7;
            tV_heading= (TextView) itemView.findViewById(R.id.text_view_cat_name);
            tV_subHeading= (TextView) itemView.findViewById(R.id.text_view_cat_type);
        }
    }

    public void setOnItemClick(ClickListener listener)
    {
        clickListener=listener;
    }
}
