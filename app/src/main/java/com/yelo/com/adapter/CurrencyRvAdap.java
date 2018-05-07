package com.yelo.com.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import com.yelo.com.R;
import com.yelo.com.pojo_class.CurrencyPojo;
import com.yelo.com.utility.ClickListener;
import com.yelo.com.utility.VariableConstants;
import java.util.ArrayList;

/**
 * <h>HomeFragRvAdapter</h>
 * <p>
 *     In class is called from CurrencyListActivity class. In this recyclerview adapter class we used to inflate
 *     single_row_select_currency layout and shows the all country and their code and symcol into list.
 * </p>
 * @since 05-May-17
 */
public class CurrencyRvAdap extends RecyclerView.Adapter<CurrencyRvAdap.MyViewHolder> implements Filterable {
    private static final String TAG = CurrencyRvAdap.class.getSimpleName();
    private Activity mActivity;
    private ArrayList<CurrencyPojo> arrayListCurrency;
    private RecordFilter filter;

    /**
     * <h>CurrencyRvAdap</h>
     * <p>
     *     This is simple constructor to initailize list datas and context.
     * </p>
     * @param mActivity The current context
     * @param arrayListCurrency The list datas
     */
    public CurrencyRvAdap(Activity mActivity, ArrayList<CurrencyPojo> arrayListCurrency) {
        this.mActivity = mActivity;
        this.arrayListCurrency = arrayListCurrency;
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
        View view = LayoutInflater.from(mActivity).inflate(R.layout.single_row_select_currency, parent, false);
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
        String countryName, code, symbol;
        countryName = arrayListCurrency.get(position).getCountry();
        code = arrayListCurrency.get(position).getCode();
        symbol = arrayListCurrency.get(position).getSymbol();

        // country name
        if (countryName != null)
            holder.tV_countryName.setText(countryName);

        // code
        if (code != null)
            holder.tV_code.setText(code);

        // symbol
        if (symbol != null)
            holder.tV_symbol.setText(symbol);

        //IMPLEMENT CLICK LISTENET
        holder.setItemClickListener(new ClickListener() {
            @Override
            public void onItemClick(View view, int position)
            {
                System.out.println(TAG+" "+"item clicked");
                String cuurency_code=arrayListCurrency.get(position).getCode();
                String currency_symbol=arrayListCurrency.get(position).getSymbol();
                if (cuurency_code!=null && currency_symbol!=null)
                {
                    Intent intent=new Intent();
                    intent.putExtra("cuurency_code",cuurency_code);
                    intent.putExtra("currency_symbol",currency_symbol);
                    mActivity.setResult(VariableConstants.CURRENCY_REQUEST_CODE,intent);
                    mActivity.onBackPressed();
                }
            }
        });
    }

    /**
     * Return the size of your dataset
     * @return the total number of rows
     */
    @Override
    public int getItemCount() {
        return arrayListCurrency.size();
    }

    /**
     * <h>GetFilter</h>
     * <p>
     *     a filter used to constrain data based on some filtering pattern
     * </p>
     * @return Returns a filter that can be used to constrain data with a filtering pattern.
     */
    @Override
    public Filter getFilter() {
        if(filter==null)
        {
            //filter=new CustomFilter(arrayListCurrency,this);
            filter=new RecordFilter();
        }
        return filter;
    }

    /**
     * <h>RecordFilter</h>
     * <p>
     *     This is custom filter class which extend Filter abstract class. The Filter class has 2 abstract
     *     methods that must be overridden.
     *    1> performFiltering() – Filter the data based on a pattern in a worker thread. It must return a
     *    FilterResults object which holds the results of the filtering operation. A FilterResults object
     *    has 2 properties, count that holds the count of results computed by the operation and values that
     *    contains the values returned by the same filtering operation.
     *    2> Once the filtering is completed, the results are passed through this method to publish them in
     *    the user interface on the UI thread.
     * </p>
     */
    private class RecordFilter extends Filter
    {
        private ArrayList<CurrencyPojo> filterList=arrayListCurrency;

        //FILTERING OCURS
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results=new FilterResults();

            //CHECK CONSTRAINT VALIDITY
            if(constraint != null && constraint.length() > 0)
            {
                //CHANGE TO UPPER
                constraint=constraint.toString().toUpperCase();
                //STORE OUR FILTERED PLAYERS
                ArrayList<CurrencyPojo> filteredPlayers=new ArrayList<>();

                for (int i=0;i<filterList.size();i++)
                {
                    //CHECK
                    if(filterList.get(i).getCountry().toUpperCase().contains(constraint))
                    {
                        //ADD PLAYER TO FILTERED PLAYERS
                        filteredPlayers.add(filterList.get(i));
                    }
                }

                results.count=filteredPlayers.size();
                results.values=filteredPlayers;
            }else
            {
                results.count=filterList.size();
                results.values=filterList;

            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            arrayListCurrency= (ArrayList<CurrencyPojo>) results.values;
            //REFRESH
            notifyDataSetChanged();
        }
    }

    /**
     * <h>MyViewHolder</h>
     * <p>
     *     In this class we used to declare and assign the xml variables.
     * </p>
     */
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //OUR VIEWS
        TextView tV_countryName,tV_code,tV_symbol;
        ClickListener itemClickListener;

        public MyViewHolder(View itemView) {
            super(itemView);
            tV_countryName= (TextView) itemView.findViewById(R.id.tV_countryName);
            tV_code= (TextView) itemView.findViewById(R.id.tV_code);
            tV_symbol= (TextView) itemView.findViewById(R.id.tV_symbol);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            this.itemClickListener.onItemClick(v,getLayoutPosition());
        }
        void setItemClickListener(ClickListener ic)
        {
            this.itemClickListener=ic;
        }
    }
}
