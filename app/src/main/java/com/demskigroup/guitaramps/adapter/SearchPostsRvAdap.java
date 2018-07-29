package com.demskigroup.guitaramps.adapter;

import android.app.Activity;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.pojo_class.search_post_pojo.SearchPostDatas;
import com.demskigroup.guitaramps.utility.CircleTransform;
import com.demskigroup.guitaramps.utility.CommonClass;
import com.demskigroup.guitaramps.utility.ProductItemClickListener;

import java.util.ArrayList;

/**
 * Created by hello on 30-Jun-17.
 */
public class SearchPostsRvAdap extends RecyclerView.Adapter<SearchPostsRvAdap.MyViewHolder>
{
    private Activity mActivity;
    private ArrayList<SearchPostDatas> aL_searchedPosts;
    private ProductItemClickListener clickListener;

    public SearchPostsRvAdap(Activity mActivity, ArrayList<SearchPostDatas> aL_searchedPosts) {
        this.mActivity = mActivity;
        this.aL_searchedPosts = aL_searchedPosts;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view= LayoutInflater.from(mActivity).inflate(R.layout.single_row_search_product,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position)
    {
        String productName,productImage,category;
        productName=aL_searchedPosts.get(position).getProductName();
        productImage=aL_searchedPosts.get(position).getMainUrl();
        category=aL_searchedPosts.get(position).getCategory();

        // set Profile pic
        if (productImage!=null && !productImage.isEmpty())
            Picasso.with(mActivity)
                    .load(productImage)
                    .placeholder(R.drawable.profile_bg)
                    .error(R.drawable.profile_bg)
                    .transform(new CircleTransform())
                    .into(holder.image);

        // set product name
        if (productName!=null && !productName.isEmpty())
            holder.tV_heading.setText(productName);

        // set category
        if (category!=null && !category.isEmpty())
            holder.tV_subHeading.setText(category);

        ViewCompat.setTransitionName(holder.image, aL_searchedPosts.get(position).getProductName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener!=null)
                    clickListener.onItemClick(holder.getAdapterPosition(),holder.image);
            }
        });
    }

    @Override
    public int getItemCount() {
        return aL_searchedPosts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView tV_heading,tV_subHeading;
        View mView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            image= (ImageView) itemView.findViewById(R.id.image);
            image.getLayoutParams().width= CommonClass.getDeviceWidth(mActivity)/7;
            image.getLayoutParams().height= CommonClass.getDeviceWidth(mActivity)/7;
            tV_heading= (TextView) itemView.findViewById(R.id.tV_heading);
            tV_subHeading= (TextView) itemView.findViewById(R.id.tV_subHeading);
        }
    }
    public void setOnItemClick(ProductItemClickListener listener)
    {
        clickListener=listener;
    }
}
