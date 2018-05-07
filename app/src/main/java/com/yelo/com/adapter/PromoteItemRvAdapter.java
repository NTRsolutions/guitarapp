package com.yelo.com.adapter;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.yelo.com.R;
import com.yelo.com.pojo_class.promote_item_pojo.PromoteItemData;
import java.util.ArrayList;

/**
 * <h>PromoteItemRvAdapter</h>
 * <p>
 *     In class is called from PromoteItemActivity class. In this recyclerview adapter class we used to inflate
 *     single_row_promote_item layout and shows the all promote lists.
 * </p>
 * @since 30-Aug-17
 */
public class PromoteItemRvAdapter extends RecyclerView.Adapter<PromoteItemRvAdapter.MyViewHolder>
{
    private Activity mActivity;
    private ArrayList<PromoteItemData> arrayListPromoteItem;
    private int mSelectedItem = -1;
    private static final String TAG = PromoteItemRvAdapter.class.getSimpleName();

    public PromoteItemRvAdapter(Activity mActivity, ArrayList<PromoteItemData> arrayListPromoteItem) {
        this.mActivity = mActivity;
        this.arrayListPromoteItem = arrayListPromoteItem;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.single_row_promote_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (position == mSelectedItem)
        {
            arrayListPromoteItem.get(position).setItemSelected(true);
            holder.rL_single_shell.setBackgroundResource(R.drawable.rect_pink_color_with_solid_shape);
            holder.tV_price.setTextColor(ContextCompat.getColor(mActivity,R.color.white));
            holder.tV_uniqueViews.setTextColor(ContextCompat.getColor(mActivity,R.color.white));
        } else {
            arrayListPromoteItem.get(position).setItemSelected(false);
            holder.rL_single_shell.setBackgroundResource(R.drawable.rect_gray_color_stroke_and_solid_shape);
            holder.tV_price.setTextColor(ContextCompat.getColor(mActivity,R.color.item_name_color));
            holder.tV_uniqueViews.setTextColor(ContextCompat.getColor(mActivity,R.color.item_name_color));
        }

        String price,uniqueViews;
        price = arrayListPromoteItem.get(position).getPrice();
        uniqueViews = arrayListPromoteItem.get(position).getName();

        // set price
        if (price!=null && !price.isEmpty())
            holder.tV_price.setText(setPriceValue(price));

        // set unique views
        if (uniqueViews!=null && !uniqueViews.isEmpty())
            holder.tV_uniqueViews.setText(uniqueViews);
    }

    @Override
    public int getItemCount() {
        return arrayListPromoteItem.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView tV_price,tV_uniqueViews;
        RelativeLayout rL_single_shell;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tV_uniqueViews= (TextView) itemView.findViewById(R.id.tV_uniqueViews);
            tV_price= (TextView) itemView.findViewById(R.id.tV_price);
            rL_single_shell= (RelativeLayout) itemView.findViewById(R.id.rL_single_shell);
        }

        @Override
        public void onClick(View v) {
            System.out.println(TAG+" "+"item clicked pos="+getAdapterPosition());
            mSelectedItem = getAdapterPosition();
            notifyItemRangeChanged(0, arrayListPromoteItem.size());
        }
    }

    private String setPriceValue(String price)
    {
        return price;
    }
}
