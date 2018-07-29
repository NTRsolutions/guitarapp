package com.demskigroup.guitaramps.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.main.activity.products.ProductImagesActivity;
import com.squareup.picasso.Picasso;
import com.demskigroup.guitaramps.utility.CommonClass;

import java.util.ArrayList;

/**
 * Created by embed on 5/1/18.
 */

public class ProductDetailImagePagerAdapter extends PagerAdapter {
    private Activity context;
    private LayoutInflater layoutInflater;


    private ArrayList<String> arrayList;
    private String[] image_array;

    public ProductDetailImagePagerAdapter(Activity context, ArrayList<String> aL_multipleImages) {
        this.context = context;
        this.arrayList=aL_multipleImages;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.image_array = aL_multipleImages.toArray(new String[0]);
        System.out.println("images url..."+image_array.length);
    }

    @Override
    public int getCount() {
            return image_array.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, int position)
    {
        System.out.println("instantiate.."+container+" "+position);
        View itemView = layoutInflater.inflate(R.layout.product_detail_viewpager_image, container, false);
        ImageView imageView;
        imageView = (ImageView) itemView.findViewById(R.id.viewPagerItem_image1);

        if (image_array!=null)
            if(Build.MANUFACTURER.equalsIgnoreCase("Samsung")){
                Picasso.with(context)
                        .load(image_array[position])
                        .resize(CommonClass.getDeviceWidth(context),CommonClass.getDeviceWidth(context))
                        .centerCrop()
                        .placeholder(R.drawable.app_launcher_logo)
                        .error(R.drawable.app_launcher_logo)
                        .into(imageView);
                container.addView(itemView);
            }else {
                Picasso.with(context)
                        .load(image_array[position])
                        .resize(CommonClass.getDeviceWidth(context),CommonClass.getDeviceWidth(context))
                        .centerCrop()
                        .placeholder(R.drawable.app_launcher_logo)
                        .error(R.drawable.app_launcher_logo)
                        .into(imageView);
                container.addView(itemView);
            }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, ProductImagesActivity.class);
                intent.putExtra("imagesArrayList",arrayList);
                context.startActivity(intent);
            }
        });

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
