package com.demskigroup.guitaramps.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.demskigroup.guitaramps.R;

import com.demskigroup.guitaramps.main.ProductPictureFragment;
import com.demskigroup.guitaramps.main.activity.HomePageActivity;
import com.demskigroup.guitaramps.main.activity.products.ProductPictureDetailsActivity;
import com.demskigroup.guitaramps.main.tab_fragments.HomeFrag;
import com.demskigroup.guitaramps.mqttchat.AppController;
import com.demskigroup.guitaramps.pojo_class.product_category.ProductCategoryResDatas;
import com.demskigroup.guitaramps.pojo_class.productcategory.HomeCategory;

import java.util.List;

/**
 * Created by brst-pc89 on 4/19/18.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {

    Context context;
   List<ProductCategoryResDatas> mList;
    int images[];

    public ProductAdapter(Context context, List<ProductCategoryResDatas> productName) {

        this.context = context;
        this.mList = productName;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from( context ).inflate( R.layout.custom_product_picture, parent, false );
        return new MyViewHolder( view );
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        StringBuffer res = new StringBuffer();
        String[] strArr = mList.get( position ).getName().split(" ");
        for (String str : strArr) {
            char[] stringArray = str.trim().toCharArray();
            stringArray[0] = Character.toUpperCase(stringArray[0]);
            str = new String(stringArray);

            res.append(str).append(" ");
        }
        holder.textViewProduct.setText(res);

        Glide.with( AppController.getInstance() ).load(  mList.get( position ).getActiveimage() ).into( holder.imageViewProduct );


        if (context instanceof ProductPictureDetailsActivity) {
            holder.linearLayoutProduct.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Dialog dialog = new Dialog( context );
                    dialog.setContentView( R.layout.dialog_buy_sell );
                    dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );

                    TextView textViewProductName = dialog.findViewById( R.id.textViewProductName );
                    TextView textViewCancel = dialog.findViewById( R.id.textViewCancel );

                    textViewProductName.setText( holder.textViewProduct.getText().toString() );

                    dialog.getWindow().setLayout( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT );
                    dialog.show();

                    textViewCancel.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            dialog.dismiss();
                        }
                    } );
                }
            } );
        } else {
            holder.linearLayoutProduct.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    Intent intent = new Intent(context, ProductPictureDetailsActivity.class);
//                    intent.putExtra("productName", holder.textViewProduct.getText().toString());
//                    context.startActivity(intent);
                    FragmentManager manager = ((AppCompatActivity) context).getSupportFragmentManager();


                    int count = manager.getBackStackEntryCount();
                    for (int i = 0; i < count; i++)
                    {
                        Fragment fragment =manager.findFragmentById(R.id.frame_layout);
                        Log.e("TAG", "fragment " + fragment);
                    }


                    boolean isstatesaved = manager.isStateSaved();


//                    Fragment fragment =HomeFrag.getInstance();
//                    Fragment fragment = new HomeFrag();
                    FragmentTransaction transaction = manager.beginTransaction();


                    try {
//                            if (isstatesaved){


                        Fragment fragment = new HomeFrag();
                        HomePageActivity.productcategoryFrag = fragment;

                        Bundle bundle = new Bundle();
                        bundle.putString(HomeFrag.KEY_FRAGMENT_FROM_CATGORY, mList.get( position).getName());
                        fragment.setArguments(bundle);

                        transaction.replace( R.id.frame_layout, fragment);
                        transaction.addToBackStack( null );
                        transaction.commitAllowingStateLoss();

                            /*}else {
                                transaction.add( R.id.frame_layout, fragment );
                                transaction.addToBackStack( null );
                                transaction.commit();
                            }*/

                    } catch (Exception e) {
                        e.printStackTrace();

                    }


                }
            } );
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewProduct;
        LinearLayout linearLayoutProduct;
        ImageView imageViewProduct;

        public MyViewHolder(View itemView) {
            super( itemView );

            textViewProduct = itemView.findViewById( R.id.textViewProduct );
            linearLayoutProduct = itemView.findViewById( R.id.linearLayoutProduct );
            imageViewProduct = itemView.findViewById( R.id.imageViewProduct );


        }


    }
}
