package com.yelo.com.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.squareup.picasso.Picasso;
import com.yelo.com.R;
import com.yelo.com.utility.CommonClass;

/**
 * This is GetOrderActivity class
 * <p>
 *     This class is getting called from ItemDetailsActivity class.
 *     In this class we set paging for each image using PagerAdapter.
 * </p>
 * @author 3embed
 * @version 1.0
 * @since 18/5/16
 */
public class ProductImagePagerAdapter extends PagerAdapter
{

    private Activity context;
    private LayoutInflater layoutInflater;


    //ArrayList<String> arrayList;
    private String[] image_array;

    public ProductImagePagerAdapter(Activity context, String[] image_array) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.image_array = image_array;
        System.out.println("images url..."+image_array.length);
    }

    @Override
    public int getCount() {
        if(image_array != null){
            return image_array.length;
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        System.out.println("instantiate.."+container+" "+position);
        View itemView = layoutInflater.inflate(R.layout.product_image_viewpager_layout, container, false);
        ImageView imageView;
        imageView = (ImageView) itemView.findViewById(R.id.viewPagerItem_image1);

        if (image_array!=null)
            if(Build.MANUFACTURER.equalsIgnoreCase("Samsung")){
                Picasso.with(context)
                        .load(image_array[position])
                        .resize(CommonClass.getDeviceWidth(context),CommonClass.getDeviceWidth(context)+(CommonClass.getDeviceWidth(context)/2))
                        .centerCrop()
                        .placeholder(R.drawable.default_image)
                        .error(R.drawable.default_image)
                        .into(imageView);
                container.addView(itemView);
            }else {
                Picasso.with(context)
                        .load(image_array[position])
                        //.resize(CommonClass.getDeviceWidth(context),CommonClass.getDeviceWidth(context))
                        .placeholder(R.drawable.default_image)
                        .error(R.drawable.default_image)
                        .into(imageView);
                container.addView(itemView);
            }

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
