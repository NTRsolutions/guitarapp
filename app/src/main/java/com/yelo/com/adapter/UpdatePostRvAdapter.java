package com.yelo.com.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.yelo.com.R;
import com.yelo.com.Uploader.ProductImageDatas;
import com.yelo.com.main.activity.Camera2Activity;
import com.yelo.com.main.activity.CameraActivity;
import com.yelo.com.utility.CommonClass;
import com.yelo.com.utility.MyTransformation;
import com.yelo.com.utility.VariableConstants;

import java.util.ArrayList;

/**
 * <h>HomeFragRvAdapter</h>
 * <p>
 *     In class is called from UpdatePostActivity class. In this recyclerview adapter class we used to inflate
 *     single_row_post_images layout and shows the all Images.
 * </p>
 * @since 29-Aug-17
 */
public class UpdatePostRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Activity mActivity;
    private ArrayList<ProductImageDatas> aLProductImageDatases;
    private TextView tV_add_more_image;
    private static final String TAG=UpdatePostRvAdapter.class.getSimpleName();
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private ArrayList<ProductImageDatas> aLUpdateProductImage;

    /**
     * <h>CurrencyRvAdap</h>
     * <p>
     *     This is simple constructor to initailize list datas and context.
     * </p>
     * @param mActivity The current context
     * @param aLProductImageDatases The list datas
     */
    public UpdatePostRvAdapter(Activity mActivity, ArrayList<ProductImageDatas> aLProductImageDatases,ArrayList<ProductImageDatas> aLUpdateProductImage,TextView tV_add_more_image) {
        this.mActivity = mActivity;
        this.aLProductImageDatases = aLProductImageDatases;
        this.tV_add_more_image=tV_add_more_image;
        this.aLUpdateProductImage=aLUpdateProductImage;
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view;
        switch (viewType)
        {
            // posted images
            case TYPE_ITEM :
                view= LayoutInflater.from(mActivity).inflate(R.layout.single_row_post_images,parent,false);
                return new MyViewHolder(view);

            // footer
            case TYPE_FOOTER :
                view= LayoutInflater.from(mActivity).inflate(R.layout.footer_post_product_images,parent,false);
                return new FooterViewHolder(view);
        }
        return null;
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
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position)
    {
        // Image
        if (holder instanceof MyViewHolder)
        {
            final MyViewHolder myViewHolder= (MyViewHolder) holder;

            String path=aLProductImageDatases.get(position).getMainUrl();
            int rotationAngle = aLProductImageDatases.get(position).getRotationAngle();
            System.out.println(TAG+" "+"image url="+aLProductImageDatases.get(position).isImageUrl()+" "+"isImageUrl="+aLProductImageDatases.get(position).isImageUrl());
            if (path!=null && !path.isEmpty())
            {
                /*if (aLProductImageDatases.get(position).isImageUrl())
                    Picasso.with(mActivity)
                            .load(path)
                            .placeholder(R.drawable.default_image)
                            .error(R.drawable.default_image)
                            .into(myViewHolder.iV_captured_img);
                else
                    Picasso.with(mActivity)
                            .load("file://"+path)
                            .placeholder(R.drawable.default_image)
                            .error(R.drawable.default_image)
                            .into(myViewHolder.iV_captured_img);*/

                    Glide.with(mActivity)
                            .load(path)
                            .asBitmap()
                            .transform(new MyTransformation(mActivity,rotationAngle, String.valueOf(System.currentTimeMillis())), new FitCenter(mActivity))
                            .placeholder(R.drawable.app_launcher_logo)
                            .into(myViewHolder.iV_captured_img);
            }

            // remove image
            myViewHolder.iV_deleteImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    aLProductImageDatases.remove(holder.getAdapterPosition());
                    if (aLUpdateProductImage.size()>position)
                       aLUpdateProductImage.remove(holder.getAdapterPosition());
                    notifyDataSetChanged();

                    tV_add_more_image.setVisibility(View.VISIBLE);
                    switch (aLProductImageDatases.size())
                    {
                        case 0 :
                            tV_add_more_image.setText(mActivity.getResources().getString(R.string.at_least_one_image_is));
                            break;
                        // text to show add 4 more image
                        case 1 :
                            tV_add_more_image.setText(mActivity.getResources().getString(R.string.add_upto_4_more_img));
                            break;

                        // text to show add 3 more image
                        case 2 :
                            tV_add_more_image.setText(mActivity.getResources().getString(R.string.add_upto_3_more_img));
                            break;

                        // text to show add 3 more image
                        case 3 :
                            tV_add_more_image.setText(mActivity.getResources().getString(R.string.add_upto_2_more_img));
                            break;

                        // text to show add 3 more image
                        case 4 :
                            tV_add_more_image.setText(mActivity.getResources().getString(R.string.add_upto_1_more_img));
                            break;

                        // hide text since it reached to the max limit
                        case  5 :
                            tV_add_more_image.setVisibility(View.GONE);
                            break;
                    }
                }
            });
        }

        // footer
        if (holder instanceof FooterViewHolder)
        {
            FooterViewHolder footerViewHolder= (FooterViewHolder) holder;
            footerViewHolder.iV_add_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent cameraIntent;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    {
                        cameraIntent=new Intent(mActivity, Camera2Activity.class);
                        cameraIntent.putExtra("isUpdatePost",true);
                        mActivity.startActivityForResult(cameraIntent, VariableConstants.UPDATE_IMAGE_REQ_CODE);
                    }
                    else
                    {
                        cameraIntent=new Intent(mActivity, CameraActivity.class);
                        cameraIntent.putExtra("isUpdatePost",true);
                        mActivity.startActivityForResult(cameraIntent, VariableConstants.UPDATE_IMAGE_REQ_CODE);
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        if (aLProductImageDatases.size()<5) {
            if (isPositionFooter(position)) {
                return TYPE_FOOTER;
            }
            return TYPE_ITEM;
        }
        return TYPE_ITEM;
    }

    private boolean isPositionFooter (int position) {
        return position == aLProductImageDatases.size ();
    }

    /**
     * Return the size of your dataset
     * @return the total number of rows
     */
    @Override
    public int getItemCount() {
        if (aLProductImageDatases.size()<5)
            return aLProductImageDatases.size()+1;
        else return aLProductImageDatases.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iV_captured_img,iV_deleteImg;
        public MyViewHolder(View itemView) {
            super(itemView);
            iV_captured_img= (ImageView) itemView.findViewById(R.id.iV_image);
            iV_captured_img.getLayoutParams().width= CommonClass.getDeviceWidth(mActivity)/4;
            iV_captured_img.getLayoutParams().height= CommonClass.getDeviceWidth(mActivity)/4;
            iV_deleteImg= (ImageView) itemView.findViewById(R.id.iV_delete_icon);
            iV_deleteImg.setVisibility(View.VISIBLE);
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder
    {
        ImageView iV_add_image;
        FooterViewHolder(View itemView)
        {
            super(itemView);
            iV_add_image= (ImageView) itemView.findViewById(R.id.iV_add_image);
            iV_add_image.getLayoutParams().width= CommonClass.getDeviceWidth(mActivity)/4;
            iV_add_image.getLayoutParams().height= CommonClass.getDeviceWidth(mActivity)/4;
        }
    }
}
