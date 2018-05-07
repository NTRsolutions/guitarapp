package com.yelo.com.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.bumptech.glide.Glide;
import com.yelo.com.R;
import com.yelo.com.pojo_class.home_explore_pojo.ExploreResponseDatas;
import com.yelo.com.utility.CommonClass;
import com.yelo.com.utility.ProductItemClickListener;
import java.util.ArrayList;

/**
 * <h>ExploreRvAdapter</h>
 * <p>
 *     In class is called from ExploreFrag. In this recyclerview adapter class we used to inflate
 *     single_row_images layout and shows the all post posted by logged-in user.
 * </p>
 * @since 4/6/2017
 */
public class ExploreRvAdapter extends RecyclerView.Adapter<ExploreRvAdapter.MyViewHolder>
{
    private static final String TAG = ExploreRvAdapter.class.getSimpleName();
    private Activity mActivity;
    private ArrayList<ExploreResponseDatas> arrayListExploreDatas;
    private final ProductItemClickListener animalItemClickListener;

    public ExploreRvAdapter(Activity mActivity, ArrayList<ExploreResponseDatas> arrayListExploreDatas,ProductItemClickListener animalItemClickListener) {
        this.mActivity = mActivity;
        this.arrayListExploreDatas = arrayListExploreDatas;
        this.animalItemClickListener=animalItemClickListener;

        System.out.println(TAG+" "+"al size in adap="+arrayListExploreDatas);

        WindowManager wm = (WindowManager)mActivity.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.single_row_explore, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position)
    {

        try {
            final ExploreResponseDatas exploreResponseDatas=arrayListExploreDatas.get(position);
            String postedImageUrl=exploreResponseDatas.getMainUrl();

            String containerHeight=exploreResponseDatas.getContainerHeight();
            String containerWidth=exploreResponseDatas.getContainerWidth();
            String isPromoted=exploreResponseDatas.getIsPromoted();

            int deviceHalfWidth= CommonClass.getDeviceWidth(mActivity)/2;
            int setHeight=0;

            if (containerWidth!=null && !containerWidth.isEmpty())
                setHeight=(Integer.parseInt(containerHeight)*deviceHalfWidth)/(Integer.parseInt(containerWidth));
            holder.iV_explore_img.getLayoutParams().height=setHeight;

            System.out.println(TAG+" "+"containerHeight="+containerHeight+" "+"set height="+setHeight+" "+"device half height="+" "+CommonClass.getDeviceWidth(mActivity)+" "+CommonClass.getDeviceWidth(mActivity)/2);

            // product image
            try {
                Glide.with(mActivity)
                        .load(postedImageUrl)
                        .asBitmap()
                        .placeholder(R.color.image_bg_color)
                        .error(R.color.image_bg_color)
                        .into(holder.iV_explore_img);
            } catch (OutOfMemoryError | IllegalArgumentException | NullPointerException e) {
                e.printStackTrace();
            }

            ViewCompat.setTransitionName(holder.iV_explore_img, exploreResponseDatas.getProductName());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    animalItemClickListener.onItemClick(holder.getAdapterPosition(), holder.iV_explore_img);
                }
            });

            // show featured tag with product
            if (isPromoted!=null && !isPromoted.isEmpty())
            {
                if (!isPromoted.equals("0"))
                {
                    holder.rL_featured.setVisibility(View.VISIBLE);
                }
                else holder.rL_featured.setVisibility(View.GONE);
            }
            else holder.rL_featured.setVisibility(View.GONE);
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return arrayListExploreDatas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView iV_explore_img;
        private RelativeLayout rL_featured;

        MyViewHolder(View itemView) {
            super(itemView);

            iV_explore_img= (ImageView) itemView.findViewById(R.id.iV_image);
            rL_featured= (RelativeLayout) itemView.findViewById(R.id.rL_featured);
            rL_featured.setVisibility(View.GONE);
        }
    }
}
