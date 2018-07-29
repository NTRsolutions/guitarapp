package com.demskigroup.guitaramps.county_code_picker;

import android.app.Dialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.utility.ClickListener;
import java.util.ArrayList;

/**
 * <h>CountryPickerRvAdap</h>
 * <p>
 *     In this recyclerview adpter class. We inflate single_row_select_country_code to show
 *     country list.
 * </p>
 * @since 4/12/2017
 */
class CountryPickerRvAdap extends RecyclerView.Adapter<CountryPickerRvAdap.MyViewHolder> implements Filterable
{
    private static final String TAG = CountryPickerRvAdap.class.getSimpleName();
    private ArrayList<Country> arrayListCountry;
    private MyFilter myFilter;
    private SetCountryCodeListener listener;
    private Dialog countryPickerDialog;

    CountryPickerRvAdap(ArrayList<Country> arrayListCountry,SetCountryCodeListener listener,Dialog countryPickerDialog) {
        this.arrayListCountry = arrayListCountry;
        this.listener=listener;
        this.countryPickerDialog=countryPickerDialog;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_select_country_code,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)
    {
        holder.tV_country_name.setText(arrayListCountry.get(position).getName());
        holder.tV_country_code.setText(arrayListCountry.get(position).getCode());

        holder.setClickListener(new ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                System.out.println(TAG+" "+"set code="+arrayListCountry.get(position).getCode()+" "+"set name="+arrayListCountry.get(position).getName());
                listener.getCode(arrayListCountry.get(position).getCode(),arrayListCountry.get(position).getName());
                countryPickerDialog.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayListCountry.size();
    }

    @Override
    public Filter getFilter() {
        if (myFilter==null)
            myFilter=new MyFilter();
        return myFilter;
    }

    private class MyFilter extends Filter
    {
        private ArrayList<Country> arrayListFilter=arrayListCountry;

        @Override
        protected FilterResults performFiltering(CharSequence constraint)
        {
            FilterResults results=new FilterResults();

            //CHECK CONSTRAINT VALIDITY
            if(constraint != null && constraint.length() > 0)
            {
                //CHANGE TO UPPER
                constraint=constraint.toString().toUpperCase();
                //STORE OUR FILTERED PLAYERS
                ArrayList<Country> filteredPlayers=new ArrayList<>();

                for (int i=0;i<arrayListFilter.size();i++)
                {
                    //CHECK
                    if(arrayListFilter.get(i).getName().toUpperCase().contains(constraint))
                    {
                        //ADD PLAYER TO FILTERED PLAYERS
                        filteredPlayers.add(arrayListFilter.get(i));
                    }
                }

                results.count=filteredPlayers.size();
                results.values=filteredPlayers;
            }else
            {
                results.count=arrayListFilter.size();
                results.values=arrayListFilter;

            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results)
        {
            arrayListCountry= (ArrayList<Country>) results.values;
            notifyDataSetChanged();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView tV_country_name,tV_country_code;
        private ClickListener clickListener;

        MyViewHolder(View itemView)
        {
            super(itemView);
            itemView.setOnClickListener(this);

            tV_country_name= (TextView) itemView.findViewById(R.id.tV_country_name);
            tV_country_code= (TextView) itemView.findViewById(R.id.tV_country_code);
        }
        void setClickListener(ClickListener listener)
        {
            clickListener=listener;
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(v,getAdapterPosition());
        }
    }
}
