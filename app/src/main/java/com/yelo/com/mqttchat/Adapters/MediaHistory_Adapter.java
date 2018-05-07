package com.yelo.com.mqttchat.Adapters;



import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.yelo.com.R;
import com.yelo.com.mqttchat.Activities.MediaHistory_FullScreenImage;
import com.yelo.com.mqttchat.AppController;
import com.yelo.com.mqttchat.ModelClasses.Media_History_Item;
import com.yelo.com.mqttchat.ViewHolders.ViewHolderImageMedia;
import com.yelo.com.mqttchat.ViewHolders.ViewHolderVideoMedia;

import java.io.File;
import java.util.ArrayList;

/*
 * Controller class to feed the media history view item based on getter-setter class item value
 */
public class MediaHistory_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Media_History_Item> mListData = new ArrayList<>();


    private Context mContext;


    private final int IMAGE = 0;

    private final int VIDEO = 1;

    private Typeface tf;

    /**
     * @param mContext  Context
     * @param mListData ArrayList<Media_History_Item>
     */
    public MediaHistory_Adapter(Context mContext, ArrayList<Media_History_Item> mListData) {
        this.mListData = mListData;
        this.mContext = mContext;


        tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Condensed.ttf");

    }


    /**
     * @return number of items
     */
    @Override
    public int getItemCount() {
        return this.mListData.size();
    }


    /**
     * @param position item position
     * @return item viewType
     */
    @Override
    public int getItemViewType(int position) {
        String type = mListData.get(position).getMessageType();


        if (type.equals("1")) {


            return 0;
        } else if (type.equals("2")) {


            return 1;
        }


        return -1;
    }

    /**
     * @param viewGroup ViewGroup
     * @param viewType  item viewType
     * @return RecyclerView.ViewHolder
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());


        View v1;
        switch (viewType) {
            case IMAGE:
                v1 = inflater.inflate(R.layout.media_shared_image, viewGroup, false);
                viewHolder = new ViewHolderImageMedia(v1);
                break;
            default:
                v1 = inflater.inflate(R.layout.media_shared_video, viewGroup, false);
                viewHolder = new ViewHolderVideoMedia(v1);


        }


        return viewHolder;
    }

    /**
     * @param viewHolder RecyclerView.ViewHolder
     * @param position   item position
     */

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        switch (viewHolder.getItemViewType()) {


            case IMAGE:
                ViewHolderImageMedia vh2 = (ViewHolderImageMedia) viewHolder;
                configureViewHolderMediaImage(vh2, position);

                break;

            case VIDEO:
                ViewHolderVideoMedia vh3 = (ViewHolderVideoMedia) viewHolder;
                configureViewHolderMediaVideo(vh3, position);
                break;

        }


    }


    /**
     * @param vh       ViewHolderImageMedia
     * @param position item position
     */
    @SuppressWarnings("TryWithIdenticalCatches")
    private void configureViewHolderMediaImage(final ViewHolderImageMedia vh, int position) {


        final Media_History_Item media = mListData.get(position);
        if (media != null) {

            try {

                vh.fnf.setVisibility(View.GONE);
                if (media.isSelf()) {
                    try {


                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_GRANTED) {


                            if (new File(media.getImagePath()).exists()) {

                                vh.image.setBackgroundColor(Color.parseColor(AppController.getInstance().getColorCode(vh.getAdapterPosition() % 19)));

                                Glide
                                        .with(mContext)
                                        .load(media.getImagePath())

                                        .crossFade()
                                        .fitCenter()
                                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)

                                        .listener(new RequestListener<String, GlideDrawable>() {
                                            @Override
                                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                                vh.image.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));
                                                return false;
                                            }
                                        })
                                        .into(vh.image);


                                vh.image.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {


                                        Intent i = new Intent(mContext, MediaHistory_FullScreenImage.class);


                                        i.putExtra("imagePath", media.getImagePath());


                                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                                makeSceneTransitionAnimation((Activity) mContext, vh.image, "image");
                                        mContext.startActivity(i, options.toBundle());


                                    }
                                });
                            } else {


                                Glide.clear(vh.image);
                                vh.fnf.setVisibility(View.VISIBLE);

                                vh.fnf.setTypeface(tf, Typeface.NORMAL);
                                vh.fnf.setText(R.string.image_nf);
                                vh.image.setImageDrawable(null);
                                vh.image.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));


                            }

                        } else {

                            /*
                             * External storage access permission denied
                             */


                            Glide.clear(vh.image);
                            vh.fnf.setVisibility(View.VISIBLE);

                            vh.fnf.setTypeface(tf, Typeface.NORMAL);
                            vh.fnf.setText(R.string.PermissionDenied);
                            vh.image.setImageDrawable(null);
                            vh.image.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));


                            vh.image.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                                    intent.setData(uri);
                                    mContext.startActivity(intent);
                                }
                            });

                        }

                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                } else {


                    if (media.getDownloadStatus() == 0) {
                        try {


                            vh.image.setBackgroundColor(Color.parseColor(AppController.getInstance().getColorCode(vh.getAdapterPosition() % 19)));

                            Glide
                                    .with(mContext)
                                    .load(media.getThumbnailPath())

                                    .crossFade()
                                    .fitCenter()


                                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)

                                    .listener(new RequestListener<String, GlideDrawable>() {
                                        @Override
                                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                            vh.image.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white)
                                            );
                                            return false;
                                        }
                                    })
                                    .into(vh.image);


                            vh.image.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {


                                    Intent i = new Intent(mContext, MediaHistory_FullScreenImage.class);


                                    i.putExtra("imagePath", media.getThumbnailPath());


                                    ActivityOptionsCompat options = ActivityOptionsCompat.
                                            makeSceneTransitionAnimation((Activity) mContext, vh.image, "image");
                                    mContext.startActivity(i, options.toBundle());


                                }
                            });

                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }

                    } else {


                        try {


                            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                                    == PackageManager.PERMISSION_GRANTED) {


                                if (new File(media.getImagePath()).exists()) {

                                    vh.image.setBackgroundColor(Color.parseColor(AppController.getInstance().getColorCode(vh.getAdapterPosition() % 19)));

                                    Glide
                                            .with(mContext)
                                            .load(media.getImagePath())

                                            .crossFade()
                                            .fitCenter()


                                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                            .listener(new RequestListener<String, GlideDrawable>() {
                                                @Override
                                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                                    return false;
                                                }

                                                @Override
                                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                                    vh.image.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));
                                                    return false;
                                                }
                                            })
                                            .into(vh.image);

                                    vh.image.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {


                                            Intent i = new Intent(mContext, MediaHistory_FullScreenImage.class);


                                            i.putExtra("imagePath", media.getImagePath());


                                            ActivityOptionsCompat options = ActivityOptionsCompat.
                                                    makeSceneTransitionAnimation((Activity) mContext, vh.image, "image");
                                            mContext.startActivity(i, options.toBundle());


                                        }
                                    });
                                } else {


                                    Glide.clear(vh.image);
                                    vh.fnf.setVisibility(View.VISIBLE);

                                    vh.fnf.setTypeface(tf, Typeface.NORMAL);
                                    vh.fnf.setText(R.string.image_nf);
                                    vh.image.setImageDrawable(null);
                                    vh.image.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));


                                }

                            } else {

                            /*
                             * External storage access permission denied
                             */


                                Glide.clear(vh.image);
                                vh.fnf.setVisibility(View.VISIBLE);

                                vh.fnf.setTypeface(tf, Typeface.NORMAL);
                                vh.fnf.setText(R.string.PermissionDenied);
                                vh.image.setImageDrawable(null);
                                vh.image.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));

                                vh.image.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent();
                                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                                        intent.setData(uri);
                                        mContext.startActivity(intent);
                                    }
                                });


                            }
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }


                    }


                }


            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }


        }
    }


    /**
     * @param vh       ViewHolderVideoMedia
     * @param position item position
     */
    @SuppressWarnings("TryWithIdenticalCatches")
    private void configureViewHolderMediaVideo(final ViewHolderVideoMedia vh, int position) {


        final Media_History_Item media = mListData.get(position);
        if (media != null) {
            try {
                vh.fnf.setVisibility(View.GONE);
                if (media.isSelf()) {


                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {


                        if (new File(media.getVideoPath()).exists()) {


                            Bitmap thumbnailBitmap = ThumbnailUtils.createVideoThumbnail(media.getVideoPath(), MediaStore.Images.Thumbnails.MINI_KIND);


                            vh.thumbnail.setImageBitmap(thumbnailBitmap);

                        } else {


                            Glide.clear(vh.thumbnail);


                            vh.fnf.setVisibility(View.VISIBLE);

                            vh.fnf.setTypeface(tf, Typeface.NORMAL);
                            vh.fnf.setText(R.string.video_nf);


                            vh.thumbnail.setImageDrawable(null);

                            vh.thumbnail.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));

                        }
                    } else {


                        Glide.clear(vh.thumbnail);
                        vh.fnf.setVisibility(View.VISIBLE);

                        vh.fnf.setTypeface(tf, Typeface.NORMAL);
                        vh.fnf.setText(R.string.PermissionDenied);
                        vh.thumbnail.setImageDrawable(null);
                        vh.thumbnail.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));

                        vh.thumbnail.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                                intent.setData(uri);
                                mContext.startActivity(intent);
                            }
                        });

                    }


                } else {


                    if (media.getDownloadStatus() == 1) {

                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_GRANTED) {


                            if (new File(media.getVideoPath()).exists()) {


                                Bitmap thumbnailBitmap = ThumbnailUtils.createVideoThumbnail(media.getVideoPath(), MediaStore.Images.Thumbnails.MINI_KIND);

                                vh.thumbnail.setImageBitmap(thumbnailBitmap);

                            } else {


                                Glide.clear(vh.thumbnail);
                                vh.fnf.setVisibility(View.VISIBLE);

                                vh.fnf.setTypeface(tf, Typeface.NORMAL);
                                vh.fnf.setText(R.string.video_nf);
                                vh.thumbnail.setImageDrawable(null);
                                vh.thumbnail.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));

                            }
                        } else {


                            Glide.clear(vh.thumbnail);
                            vh.fnf.setVisibility(View.VISIBLE);

                            vh.fnf.setTypeface(tf, Typeface.NORMAL);
                            vh.fnf.setText(R.string.PermissionDenied);
                            vh.thumbnail.setImageDrawable(null);
                            vh.thumbnail.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));

                            vh.thumbnail.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                                    intent.setData(uri);
                                    mContext.startActivity(intent);
                                }
                            });

                        }

                    } else {
                        vh.thumbnail.setBackgroundColor(Color.parseColor(AppController.getInstance().getColorCode(vh.getAdapterPosition() % 19)));

                        try {
                            Glide
                                    .with(mContext)
                                    .load(media.getThumbnailPath())

                                    .crossFade()
                                    .fitCenter()
                                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                    .listener(new RequestListener<String, GlideDrawable>() {
                                        @Override
                                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                            vh.thumbnail.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));
                                            return false;
                                        }
                                    })
                                    .into(vh.thumbnail);
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }


                    }


                }


            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }


        }


    }


}