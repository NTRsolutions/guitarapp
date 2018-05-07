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
import com.squareup.picasso.Picasso;
import com.yelo.com.R;
import com.yelo.com.main.activity.Camera2Activity;
import com.yelo.com.main.activity.CameraActivity;
import com.yelo.com.utility.CapturedImage;
import com.yelo.com.utility.CommonClass;
import com.yelo.com.utility.MyTransformation;

import java.util.ArrayList;

/**
 * <h>PostProductImagesRvAdap</h>
 * <p>
 *     In this class we used to set the custom adapter class for recycler view. In this recyclerview adapter class we used to inflate
 *     single_row_images layout and shows the all the seleted image for posting.
 * </p>
 * @since 21-Jun-17
 */
public class PostProductImagesRvAdap extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Activity mActivity;


    private ArrayList<CapturedImage> arrayListImgPath;

    private TextView tV_add_more_image;

    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;

//    public PostProductImagesRvAdap(Activity mActivity, ArrayList<String> arrayListImgPath,TextView tV_add_more_image) {
//        this.mActivity = mActivity;
//        this.arrayListImgPath = arrayListImgPath;
//        this.tV_add_more_image=tV_add_more_image;
//    }



    public PostProductImagesRvAdap(Activity mActivity, ArrayList<CapturedImage> arrayListImgPath, TextView tV_add_more_image) {
        this.mActivity = mActivity;
        this.arrayListImgPath = arrayListImgPath;
        this.tV_add_more_image=tV_add_more_image;
    }

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

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position)
    {
        // Image
        if (holder instanceof MyViewHolder)
        {
            final MyViewHolder myViewHolder= (MyViewHolder) holder;

//            String path=arrayListImgPath.get(position);
//            if (path!=null && !path.isEmpty())
//                Picasso.with(mActivity)
//                        .load("file://"+path)
//                        .placeholder(R.drawable.default_image)
//                        .error(R.drawable.default_image)
//                        .into(myViewHolder.iV_captured_img);






            CapturedImage image=arrayListImgPath.get(position);
            String path=image.getImagePath();
            if (path!=null && !path.isEmpty()) {


                try {
                    Glide.with(mActivity)
                            .load(path)
                            .asBitmap()
                            .transform(new MyTransformation(mActivity, image.getRotateAngle(), String.valueOf(System.currentTimeMillis())), new FitCenter(mActivity))
                            .placeholder(R.drawable.app_launcher_logo)
                            .into(myViewHolder.iV_captured_img);

                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            }




            // remove image
            myViewHolder.iV_deleteImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    arrayListImgPath.remove(holder.getAdapterPosition());
                    notifyDataSetChanged();

                    tV_add_more_image.setVisibility(View.VISIBLE);
                    switch (arrayListImgPath.size())
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
            /*FooterViewHolder footerViewHolder= (FooterViewHolder) holder;
            footerViewHolder.iV_add_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.finish();
                }
            });*/

            FooterViewHolder footerViewHolder= (FooterViewHolder) holder;
            footerViewHolder.iV_add_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.finish();
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        if (arrayListImgPath.size()<5) {
            if (isPositionFooter(position)) {
                return TYPE_FOOTER;
            }
            return TYPE_ITEM;
        }
        return TYPE_ITEM;
    }

    private boolean isPositionFooter (int position) {
        return position == arrayListImgPath.size ();
    }

    @Override
    public int getItemCount() {
        if (arrayListImgPath.size()<5)
        return arrayListImgPath.size()+1;
        else return arrayListImgPath.size();
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
