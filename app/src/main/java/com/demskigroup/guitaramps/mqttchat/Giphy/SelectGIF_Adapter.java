package com.demskigroup.guitaramps.mqttchat.Giphy;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.mqttchat.AppController;

import java.util.ArrayList;

/**
 * Created by embed on 4/1/17.
 */
public class SelectGIF_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context mContext;

    private ArrayList<Giphy_Item> mListData = new ArrayList<>();


    public SelectGIF_Adapter(Context mContext, ArrayList<Giphy_Item> mListData) {
        this.mListData = mListData;
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View v1 = inflater.inflate(R.layout.trending_gif, viewGroup, false);
        viewHolder = new ViewHolderTrendingGif(v1);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        ViewHolderTrendingGif vh2 = (ViewHolderTrendingGif) viewHolder;
        configureViewHolderTrendingGif(vh2, position);

    }

    private void configureViewHolderTrendingGif(final ViewHolderTrendingGif vh2, int position) {

        final Giphy_Item media = mListData.get(position);
        if (media != null) {

            try {
                vh2.image.setBackgroundColor(Color.parseColor(AppController.getInstance().getColorCode(vh2.getAdapterPosition() % 19)));
                Glide.with(mContext)
                        .load(media.getGifUrl())
                        .asGif()


                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)

                        .crossFade()

                        .listener(new RequestListener<String, GifDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GifDrawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GifDrawable resource, String model, Target<GifDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                vh2.image.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));
                                return false;
                            }
                        })
                        .into(vh2.image);

            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }


        }


    }

    @Override
    public int getItemCount() {
        return this.mListData.size();
    }
}
