package com.yelo.com.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.squareup.picasso.Picasso;
import com.yelo.com.R;
import com.yelo.com.pojo_class.GalleryImagePojo;
import com.yelo.com.utility.CommonClass;
import com.yelo.com.utility.MyTransformation;

import java.util.ArrayList;

/**
 * <h>ImagesHorizontalRvAdap</h>
 * <p>
 *     In class is called from CameraActivity class. In this recyclerview adapter class we used to inflate
 *     single_row_camera_images layout and shows the images horizontally
 * </p>
 * @since 14-Aug-17
 */
public class GalleryImgHorizontalRvAdap extends RecyclerView.Adapter<GalleryImgHorizontalRvAdap.MyViewHolder>
{
    private Activity mActivity;
    private ArrayList<String> arrayListImgPath;
    private GalleryRvAdap galleryRvAdap;
    private ArrayList<GalleryImagePojo> arrayListGalleryImg;
    private ArrayList<Integer> rotationAngles;

    /**
     * <h>CurrencyRvAdap</h>
     * <p>
     *     This is simple constructor to initailize list datas and context.
     * </p>
     * @param mActivity The current context
     * @param arrayListImgPath The list datas
     */
    public GalleryImgHorizontalRvAdap(Activity mActivity, ArrayList<String> arrayListImgPath,GalleryRvAdap galleryRvAdap,ArrayList<GalleryImagePojo> arrayListGalleryImg,ArrayList<Integer> rotationAngles) {
        this.mActivity = mActivity;
        this.arrayListImgPath = arrayListImgPath;
        this.galleryRvAdap=galleryRvAdap;
        this.arrayListGalleryImg=arrayListGalleryImg;
        this.rotationAngles=rotationAngles;
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
    public GalleryImgHorizontalRvAdap.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mActivity).inflate(R.layout.single_row_camera_images,parent,false);
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
    public void onBindViewHolder(final GalleryImgHorizontalRvAdap.MyViewHolder holder, int position)
    {
        String path=arrayListImgPath.get(position);
        int rotationAngle=0;

        if (position<rotationAngles.size())
        rotationAngle=rotationAngles.get(position);
       /* if (path!=null && !path.isEmpty())
            Picasso.with(mActivity)
                    .load("file://"+path)
                    .placeholder(R.drawable.default_image)
                    .error(R.drawable.default_image)
                    .into(holder.iV_captured_img);*/

        try {
            Glide.with(mActivity)
                    //.load("file://"+path)
                    .load("file://"+path)
                    .asBitmap()
                    .transform(new MyTransformation(mActivity, rotationAngle, String.valueOf(System.currentTimeMillis())), new FitCenter(mActivity))
                    .placeholder(R.drawable.app_launcher_logo)
                    // .fitCenter()
                    .into(holder.iV_captured_img);

        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        // remove image
        holder.iV_deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                for (int galleryImgCount=0;galleryImgCount<arrayListGalleryImg.size();galleryImgCount++)
                {
                    if (arrayListGalleryImg.get(galleryImgCount).getGalleryImagePath().equals(arrayListImgPath.get(holder.getAdapterPosition())))
                    {
                        arrayListGalleryImg.get(galleryImgCount).setSelected(false);
                        galleryRvAdap.notifyItemChanged(galleryImgCount);
                    }
                }

                arrayListImgPath.remove(holder.getAdapterPosition());
                //rotationAngles.remove(holder.getAdapterPosition());
                if (arrayListImgPath.size()==0)
                {
                    arrayListImgPath.clear();
                    rotationAngles.clear();
                }
                notifyDataSetChanged();
            }
        });
    }

    /**
     * Return the size of your dataset
     * @return the total number of rows
     */
    @Override
    public int getItemCount() {
        return arrayListImgPath.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iV_captured_img,iV_deleteImg;
        public MyViewHolder(View itemView) {
            super(itemView);
            iV_captured_img= (ImageView) itemView.findViewById(R.id.iV_image);
            iV_captured_img.getLayoutParams().width= CommonClass.getDeviceWidth(mActivity)/4;
            iV_captured_img.getLayoutParams().height= CommonClass.getDeviceWidth(mActivity)/4;
            iV_deleteImg= (ImageView) itemView.findViewById(R.id.iV_deleteImg);
        }
    }
}
