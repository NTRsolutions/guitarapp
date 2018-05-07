package com.yelo.com.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.yelo.com.R;
import com.yelo.com.pojo_class.product_category.ProductCategoryResDatas;
import com.yelo.com.utility.ClickListener;

import java.util.ArrayList;

/**
 * <h>ProductCategoryRvAdapter</h>
 * <p>
 *     This class is called from ProductCategoryActivity class. This class extends RecyclerView.Adapter
 *     from that we have three overrided method 1. onCreateViewHolder() In this method we used to inflate
 *     single_row_product_category.  2> getItemCount() This method retuns the total number of inflated rows
 *     3>onBindViewHolder() In this method we used to set the all values to that inflated xml from list datas.
 * </p>
 * @since 2017-05-04
 */
public class ProductCategoryRvAdapter extends RecyclerView.Adapter<ProductCategoryRvAdapter.MyViewHolder>
{
    private ArrayList<ProductCategoryResDatas> aL_categoryDatas;
    private Activity mActivity;
    private ClickListener clickListener;
    private static final String TAG=ProductCategoryRvAdapter.class.getSimpleName();

    /**
     * <h>ProductCategoryRvAdapter</h>
     * <p>
     *     This is simple constructor to initailize list datas and context.
     * </p>
     * @param mActivity The current context
     * @param aL_categoryDatas The response datas
     */
    public ProductCategoryRvAdapter(Activity mActivity,ArrayList<ProductCategoryResDatas> aL_categoryDatas) {
        this.aL_categoryDatas = aL_categoryDatas;
        this.mActivity = mActivity;
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
        View view= LayoutInflater.from(mActivity).inflate(R.layout.single_row_product_category,parent,false);
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
        String categoryName=aL_categoryDatas.get(position).getName();
        System.out.println(TAG+" "+"categoryName="+categoryName);
        if (categoryName!=null && !categoryName.isEmpty())
        {
            categoryName=categoryName.substring(0,1).toUpperCase()+categoryName.substring(1).toLowerCase();
            holder.tV_category.setText(categoryName);
        }
    }

    /**
     * Return the size of your dataset
     * @return the total number of rows
     */
    @Override
    public int getItemCount() {
        return aL_categoryDatas.size();
    }

    /**
     * <h>MyViewHolder</h>
     * <p>
     *     In this class we used to declare and assign the xml variables.
     * </p>
     */
    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView tV_category;

        MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(v,getAdapterPosition());
                }
            });
            tV_category= (TextView) itemView.findViewById(R.id.tV_category);
        }
    }

    public void setOnItemClick(ClickListener listener)
    {
        clickListener=listener;
    }
}
