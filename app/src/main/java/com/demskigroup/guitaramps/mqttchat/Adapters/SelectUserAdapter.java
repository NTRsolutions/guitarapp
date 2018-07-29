package com.demskigroup.guitaramps.mqttchat.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.mqttchat.AppController;
import com.demskigroup.guitaramps.mqttchat.ModelClasses.SelectUserItem;
import com.demskigroup.guitaramps.mqttchat.Utilities.TextDrawable;
import com.demskigroup.guitaramps.mqttchat.ViewHolders.ViewHolderSelectUser;

import java.util.ArrayList;

/**
 * Created by moda on 19/06/17.
 */

public class SelectUserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<SelectUserItem> mFilteredListData = new ArrayList<>();


    private Context mContext;


    public SelectUserAdapter(Context mContext, ArrayList<SelectUserItem> mListData) {

        this.mFilteredListData = mListData;
        this.mContext = mContext;


    }


    @Override
    public int getItemCount() {
        return this.mFilteredListData.size();
    }


    @Override
    public int getItemViewType(int position) {
        return 1;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());


        View v = inflater.inflate(R.layout.select_user_item, viewGroup, false);
        viewHolder = new ViewHolderSelectUser(v);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {


        ViewHolderSelectUser vh2 = (ViewHolderSelectUser) viewHolder;
        configureViewHolderSelectUser(vh2, position);

    }


    private void configureViewHolderSelectUser(final ViewHolderSelectUser vh, int position) {


        final SelectUserItem chat = mFilteredListData.get(position);


        vh.userName.setText(chat.getUserName());


        vh.userIdentifier.setText(chat.getUserIdentifier());


        if (chat.getUserImage() != null && !chat.getUserImage().isEmpty()) {

            Glide.with(mContext).load(chat.getUserImage()).asBitmap()

                    .centerCrop().placeholder(R.drawable.chat_attachment_profile_default_image_frame).
                    into(new BitmapImageViewTarget(vh.userImage) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            vh.userImage.setImageDrawable(circularBitmapDrawable);
                        }
                    });
        } else {

            try {
                vh.userImage.setImageDrawable(TextDrawable.builder()


                        .beginConfig()
                        .textColor(Color.WHITE)
                        .useFont(Typeface.DEFAULT)
                        .fontSize(24 * (int) mContext.getResources().getDisplayMetrics().density) /* size in px */
                        .bold()
                        .toUpperCase()
                        .endConfig()


                        .buildRound((chat.getUserName().trim()).charAt(0) + "", Color.parseColor(AppController.getInstance().getColorCode(vh.getAdapterPosition() % 19))));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }


    }


}