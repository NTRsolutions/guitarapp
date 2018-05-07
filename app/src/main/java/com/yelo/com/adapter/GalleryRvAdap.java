package com.yelo.com.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.squareup.picasso.Picasso;
import com.yelo.com.R;
import com.yelo.com.main.activity.GalleryActivity;
import com.yelo.com.pojo_class.GalleryImagePojo;
import com.yelo.com.utility.CommonClass;
import com.yelo.com.utility.MyTransformation;

import java.util.ArrayList;

/**
 * <h>GalleryRvAdap</h>
 * <p>
 *     In class is called from GalleryActivity class. In this recyclerview adapter class we used to inflate
 *     single_row_gallery_images layout and shows the all gallery images.
 * </p>
 * @since 20-Jun-17
 */
public class GalleryRvAdap extends RecyclerView.Adapter<GalleryRvAdap.MyViewHolder> {

    private static final String TAG=GalleryRvAdap.class.getSimpleName();
    private ArrayList<GalleryImagePojo> arrayListGalleryImg;
    private Activity mActivity;
    private ArrayList<String> aL_cameraImgPath;

    /**
     * <h>GalleryRvAdap</h>
     * <p>
     *     This is simple constructor to initailize list datas and context.
     * </p>
     * @param activity The current context
     * @param arrayListGalleryImg The list datas
     */
    public GalleryRvAdap(Activity activity, ArrayList<GalleryImagePojo> arrayListGalleryImg,ArrayList<String> aL_cameraImgPath) {
        mActivity = activity;
        this.arrayListGalleryImg = arrayListGalleryImg;
        this.aL_cameraImgPath=aL_cameraImgPath;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_row_gallery_images, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position)
    {
        if (arrayListGalleryImg.get(position).isSelected())
        {
            holder.itemView.setBackgroundResource(R.color.pink_color);
            holder.iV_tick_mark.setVisibility(View.VISIBLE);
            holder.rL_tick_mark.setBackgroundResource(R.drawable.circle_with_pink_color_stroke_and_solid_shape);
        }
        else
        {
            holder.itemView.setBackgroundResource(R.color.transparant);
            holder.rL_tick_mark.setBackgroundResource(R.drawable.circle_with_white_color_stroke_and_solid_shape);
            holder.iV_tick_mark.setVisibility(View.GONE);
        }

        String imageUrl = arrayListGalleryImg.get(position).getGalleryImagePath();
        int rotationAngle=arrayListGalleryImg.get(position).getRotationAngle();
        System.out.println(TAG+" "+"rotaion angle="+rotationAngle);

        /*if (imageUrl!=null && !imageUrl.isEmpty())
            Picasso.with(mActivity)
                    .load("file://"+imageUrl)
                    .transform(new MyTransformation(mActivity, rotationAngle, String.valueOf(System.currentTimeMillis())), new FitCenter(mActivity))
                    .placeholder(R.drawable.default_image)
                    .error(R.drawable.default_image)
                    .into(holder.imageView);*/

        try {
            Glide.with(mActivity)
                    //.load("file://"+path)
                    .load("file://"+imageUrl)
                    .asBitmap()
                    .transform(new MyTransformation(mActivity, rotationAngle, String.valueOf(System.currentTimeMillis())), new FitCenter(mActivity))
                    .placeholder(R.drawable.default_image)
                    // .fitCenter()
                    .into(holder.imageView);

        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                System.out.println(TAG+" "+"is item selected="+arrayListGalleryImg.get(holder.getAdapterPosition()).isSelected());
                if (arrayListGalleryImg.get(holder.getAdapterPosition()).isSelected())
                {
                    holder.itemView.setBackgroundResource(R.color.transparant);
                    holder.rL_tick_mark.setBackgroundResource(R.drawable.circle_with_white_color_stroke_and_solid_shape);
                    holder.iV_tick_mark.setVisibility(View.GONE);
                    arrayListGalleryImg.get(holder.getAdapterPosition()).setSelected(false);

                    // horizontal images
                    aL_cameraImgPath.remove(arrayListGalleryImg.get(holder.getAdapterPosition()).getGalleryImagePath());
                    ((GalleryActivity)mActivity).imagesHorizontalRvAdap.notifyDataSetChanged();
                }
                else
                {
                    if (aL_cameraImgPath.size()<5) {
                        holder.itemView.setBackgroundResource(R.color.pink_color);
                        holder.iV_tick_mark.setVisibility(View.VISIBLE);
                        holder.rL_tick_mark.setBackgroundResource(R.drawable.circle_with_pink_color_stroke_and_solid_shape);
                        arrayListGalleryImg.get(holder.getAdapterPosition()).setSelected(true);

                        // horizontal images
                        aL_cameraImgPath.add(arrayListGalleryImg.get(holder.getAdapterPosition()).getGalleryImagePath());
                        ((GalleryActivity)mActivity).imagesHorizontalRvAdap.notifyDataSetChanged();
                    }
                    else CommonClass.showSnackbarMessage(((GalleryActivity)mActivity).rL_rootview,mActivity.getResources().getString(R.string.you_can_select_only_upto));

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayListGalleryImg.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView imageView,iV_tick_mark;
        private View itemView;
        private RelativeLayout rL_tick_mark;

        public MyViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.iV_gallery_img);
            imageView.getLayoutParams().height= CommonClass.getDeviceWidth(mActivity)/3;
            rL_tick_mark= (RelativeLayout) view.findViewById(R.id.rL_tick_mark);
            iV_tick_mark= (ImageView) view.findViewById(R.id.iV_tick_mark);
            itemView=view;
        }
    }

}
