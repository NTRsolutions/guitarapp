package com.demskigroup.guitaramps.adapter;

import android.app.Activity;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.squareup.picasso.Picasso;
import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.pojo_class.likedPosts.LikedPostResponseDatas;
import com.demskigroup.guitaramps.utility.CommonClass;
import com.demskigroup.guitaramps.utility.DynamicHeightImageView;
import com.demskigroup.guitaramps.utility.ProductItemClickListener;
import java.util.ArrayList;

/**
 * <h>FollowingFragRvAdap</h>
 * <p>
 *     This class is getting called from FollowingFrag. In this recyclerview adapter class we used to inflate
 *     single_row_images layout and shows the all following activity done by others users.
 * </p>
 * @since 4/10/2017
 */
public class LikedPostsRvAdap extends RecyclerView.Adapter<LikedPostsRvAdap.MyViewHolder>
{
    private static final String TAG = LikedPostsRvAdap.class.getSimpleName();
    private Activity mActivity;
    private ArrayList<LikedPostResponseDatas> arrayListLikedPosts;
    private ProductItemClickListener itemClickListener;

    /**
     * <h>CurrencyRvAdap</h>
     * <p>
     *     This is simple constructor to initailize list datas and context.
     * </p>
     * @param mActivity The current context
     * @param arrayListLikedPosts The list datas
     */
    public LikedPostsRvAdap(Activity mActivity, ArrayList<LikedPostResponseDatas> arrayListLikedPosts,ProductItemClickListener itemClickListener) {
        this.mActivity = mActivity;
        this.arrayListLikedPosts = arrayListLikedPosts;
        this.itemClickListener = itemClickListener;
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
        View exploreView= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_myprofile_images,parent,false);
        return new MyViewHolder(exploreView);
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
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        String postedImageUrl=arrayListLikedPosts.get(position).getMainUrl();
        System.out.println(TAG+" "+"postedImageUrl="+postedImageUrl);
        String containerWidth = arrayListLikedPosts.get(position).getContainerWidth();
        String containerHeight = arrayListLikedPosts.get(position).getContainerHeight();

        int deviceHalfWidth= CommonClass.getDeviceWidth(mActivity)/2;
        int deviceHalfHeight=CommonClass.getDeviceHeight(mActivity)/2;
        int setHeight;

        setHeight=(Integer.parseInt(containerHeight)*deviceHalfWidth)/(Integer.parseInt(containerWidth));
        holder.iV_explore_img.getLayoutParams().height=setHeight;

        System.out.println(TAG+" "+"containerHeight="+containerHeight+" "+"set height="+setHeight+" "+"device half height="+deviceHalfHeight);

        // set product image
        if (postedImageUrl!=null && !postedImageUrl.isEmpty())
            Picasso.with(mActivity)
                    .load(postedImageUrl)
                    .resize(CommonClass.getDeviceWidth(mActivity)/2,setHeight)
                    .placeholder(R.color.image_bg_color)
                    .error(R.color.image_bg_color)
                    .into(holder.iV_explore_img);

        // item click
        ViewCompat.setTransitionName(holder.iV_explore_img, arrayListLikedPosts.get(position).getProductName());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener!=null)
                    itemClickListener.onItemClick(holder.getAdapterPosition(),holder.iV_explore_img);
            }
        });
    }

    /**
     * Return the size of your dataset
     * @return the total number of rows
     */
    @Override
    public int getItemCount() {
        return arrayListLikedPosts.size();
    }

    /**
     * <h>MyViewHolder</h>
     * <p>
     *     In this class we used to declare and assign the xml variables.
     * </p>
     */
    class MyViewHolder extends RecyclerView.ViewHolder {
        private DynamicHeightImageView iV_explore_img;
        private View mView;

        MyViewHolder(View itemView) {
            super(itemView);
            iV_explore_img= (DynamicHeightImageView) itemView.findViewById(R.id.iV_image);
            mView=itemView;
        }
    }

/*    public void setItemClickListener(ProductItemClickListener listener)
    {
        itemClickListener=listener;
    }*/
}
