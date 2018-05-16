package com.yelo.com.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.yelo.com.R;
import com.yelo.com.pojo_class.product_review.ProductReviewResult;
import com.yelo.com.utility.CircleTransform;
import com.yelo.com.utility.CommonClass;
import java.util.ArrayList;

/**
 * <h>HomeFragRvAdapter</h>
 * <p>
 *     In class is called from ProductReviewActivity class. In this recyclerview adapter class we used to inflate
 *     single_row_item_reviews layout and shows the all review into list.
 * </p>
 * @since 09-Jun-17
 */
public class ProductReviewRvAdap extends RecyclerView.Adapter<ProductReviewRvAdap.MyViewHolder>
{
    private Activity mActivity;
    private ArrayList<ProductReviewResult> arrayListReview;
    private static final String TAG=ProductReviewRvAdap.class.getSimpleName();

    /**
     * <h>ProductReviewRvAdap</h>
     * <p>
     *     This is simple constructor to initailize list datas and context.
     * </p>
     * @param mActivity The current context
     * @param arrayListReview The list datas
     */
    public ProductReviewRvAdap(Activity mActivity, ArrayList<ProductReviewResult> arrayListReview) {
        this.mActivity = mActivity;
        this.arrayListReview = arrayListReview;
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
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view= LayoutInflater.from(mActivity).inflate(R.layout.single_row_item_reviews,parent,false);
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
        ProductReviewResult reviewResult=arrayListReview.get(position);
        String profilePic,userName,messageBody,time;
        profilePic=reviewResult.getProfilePicUrl();
        userName=reviewResult.getUsername();
        messageBody=reviewResult.getCommentBody();
        time=reviewResult.getCommentedOn();

        System.out.println(TAG+" "+"time="+time+" "+"diff time="+CommonClass.getTimeDifference(time));

        // user image
        if (profilePic!=null && !profilePic.isEmpty())
            Picasso.with(mActivity)
            .load(profilePic)
            .transform(new CircleTransform())
            .placeholder(R.drawable.profile_bg)
            .error(R.drawable.profile_bg)
            .into(holder.iV_userPic);

        // user name
        if (userName!=null)
            holder.tV_userName.setText(userName);

        // message body
        if (messageBody!=null)
            holder.tV_description.setText(messageBody);

        // time
        if (time!=null)
            holder.tV_time.setText(CommonClass.getTimeDifference(time));
    }

    /**
     * Return the size of your dataset
     * @return the total number of rows
     */
    @Override
    public int getItemCount() {
        return arrayListReview.size();
    }

    /**
     * <h>MyViewHolder</h>
     * <p>
     *     In this class we used to declare and assign the xml variables.
     * </p>
     */
    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        ImageView iV_userPic;
        TextView tV_userName,tV_description,tV_time;

        public MyViewHolder(View itemView) {
            super(itemView);
            iV_userPic= (ImageView) itemView.findViewById(R.id.iV_userPic);
            iV_userPic.getLayoutParams().width= CommonClass.getDeviceWidth(mActivity)/8;
            iV_userPic.getLayoutParams().height= CommonClass.getDeviceWidth(mActivity)/8;
            tV_userName= (TextView) itemView.findViewById(R.id.tV_userName);
            tV_description= (TextView) itemView.findViewById(R.id.tV_description);
            tV_time= (TextView) itemView.findViewById(R.id.tV_time);
        }
    }

    public void removeItem(int position,RelativeLayout textView) {
        arrayListReview.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, arrayListReview.size());
        if (arrayListReview.size()==0)
            textView.setVisibility(View.VISIBLE);
    }
}
