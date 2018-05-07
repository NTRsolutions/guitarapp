package com.yelo.com.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import com.yelo.com.R;
import com.yelo.com.pojo_class.report_product_pojo.ReportProductDatas;
import java.util.ArrayList;

/**
 * <h>ReportItemRvAdapter</h>
 * <p>
 *     This class is called from ReportProductActivity class. In this recyclerview adapter class we used to inflate
 *     single_row_report_product layout and shows the report reason of the product.
 * </p>
 * @since 13-Jun-17
 */
public class ReportItemRvAdapter extends RecyclerView.Adapter<ReportItemRvAdapter.MyViewHolder>
{
    private Activity mActivity;
    private ArrayList<ReportProductDatas> arrayListReportItem;
    private int mSelectedItem = -1;

    /**
     * <p>
     *     This is simple constructor to initailize list datas and context.
     * </p>
     * @param mActivity The current context
     * @param arrayListReportItem The list datas
     */
    public ReportItemRvAdapter(Activity mActivity, ArrayList<ReportProductDatas> arrayListReportItem) {
        this.mActivity = mActivity;
        this.arrayListReportItem = arrayListReportItem;
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
        View view= LayoutInflater.from(mActivity).inflate(R.layout.single_row_report_product,parent,false);
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
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        if (position == mSelectedItem)
        {
            arrayListReportItem.get(position).setItemSelected(true);
            holder.radio_reportReason.setChecked(true);
            holder.rL_reasonDesc.setVisibility(View.VISIBLE);
        } else {
            arrayListReportItem.get(position).setItemSelected(false);
            holder.radio_reportReason.setChecked(false);
            holder.rL_reasonDesc.setVisibility(View.GONE);
        }

        // show user report reason given by user
        holder.eT_description.setText(arrayListReportItem.get(position).getReportReasonByUser());

        // save the report reason into array
        holder.eT_description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                arrayListReportItem.get(holder.getAdapterPosition()).setReportReasonByUser(holder.eT_description.getText().toString());
            }
        });
        holder.radio_reportReason.setText(arrayListReportItem.get(position).getReportReason());
    }

    /**
     * Return the size of your dataset
     * @return the total number of rows
     */
    @Override
    public int getItemCount() {
        return arrayListReportItem.size();
    }

    /**
     * Recycler View item variables
     */
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        RadioButton radio_reportReason;
        RelativeLayout rL_reasonDesc;
        EditText eT_description;

        public MyViewHolder(View itemView) {
            super(itemView);
            radio_reportReason= (RadioButton) itemView.findViewById(R.id.radio_reportReason);
            radio_reportReason.setOnClickListener(this);
            rL_reasonDesc= (RelativeLayout) itemView.findViewById(R.id.rL_reasonDesc);
            eT_description= (EditText) itemView.findViewById(R.id.eT_description);
        }

        @Override
        public void onClick(View v) {
            mSelectedItem = getAdapterPosition();
            notifyItemRangeChanged(0, arrayListReportItem.size());
        }
    }
}
