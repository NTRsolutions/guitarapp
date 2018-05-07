package com.yelo.com.adapter;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.yelo.com.R;
import com.yelo.com.main.activity.Camera2Activity;
import com.yelo.com.main.activity.CameraActivity;
import com.yelo.com.utility.CapturedImage;
import com.yelo.com.utility.CommonClass;
import com.yelo.com.utility.MyTransformation;

import java.util.ArrayList;
import java.util.List;

/**
 * <h>ImagesHorizontalRvAdap</h>
 * <p>
 *     In class is called from CameraActivity class. In this recyclerview adapter class we used to inflate
 *     single_row_camera_images layout and shows the images horizontally
 * </p>
 * @since 21-Jun-17
 */
public class StoreImagesHorizontalRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Activity mActivity;
    private List<String> arrayListImgPath;

    private static final String TAG = StoreImagesHorizontalRvAdapter.class.getSimpleName();

    /**
     * <h>CurrencyRvAdap</h>
     * <p>
     *     This is simple constructor to initailize list datas and context.
     * </p>
     * @param mActivity The current context
     * @param arrayListImgPath The list datas
     */
//    public ImagesHorizontalRvAdap(Activity mActivity, ArrayList<String> arrayListImgPath, TextView tV_upload) {
//        this.mActivity = mActivity;
//        this.arrayListImgPath = arrayListImgPath;
//        this.tV_upload=tV_upload;
//    }
    public StoreImagesHorizontalRvAdapter(Activity mActivity, List<String> arrayListImgPath) {
        this.mActivity = mActivity;
        this.arrayListImgPath = arrayListImgPath;

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

            // Item

                view= LayoutInflater.from(mActivity).inflate(R.layout.single_row_camera_images,parent,false);
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
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position)
    {
        // Horizontal Images
        if (holder instanceof MyViewHolder)
        {
            final MyViewHolder myViewHolder= (MyViewHolder) holder;



            String path=arrayListImgPath.get( position );
            if (path!=null && !path.isEmpty()) {

                try {
                    Glide.with(mActivity)
                            .load(path)
                            .asBitmap()
                            .placeholder(R.drawable.default_image)
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


                }
            });
        }

    }

    /**
     * Return the size of your dataset
     * @return the total number of rows
     */
    @Override
    public int getItemCount()
    {
      return arrayListImgPath.size();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
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
