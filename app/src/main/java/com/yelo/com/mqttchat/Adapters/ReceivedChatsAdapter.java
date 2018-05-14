package com.yelo.com.mqttchat.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.yelo.com.R;
import com.yelo.com.mqttchat.AppController;
import com.yelo.com.mqttchat.Fragments.BuyingFragment;
import com.yelo.com.mqttchat.ModelClasses.ChatlistItem;
import com.yelo.com.mqttchat.Utilities.TextDrawable;
import com.yelo.com.mqttchat.Utilities.Utilities;
import com.yelo.com.mqttchat.ViewHolders.ViewHolderChatlist;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
/**
 * @since 18/08/17.
 */

public class ReceivedChatsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable
{
    private ArrayList<ChatlistItem> mOriginalListData = new ArrayList<>();
    private ArrayList<ChatlistItem> mFilteredListData;
    private Context mContext;
    private int density;
    private BuyingFragment fragment;
    /**
     * @param mContext  Context
     * @param mListData ArrayList<ChatlistItem>
     */
    public ReceivedChatsAdapter(Context mContext, ArrayList<ChatlistItem> mListData, BuyingFragment fragment)
    {
        this.fragment = fragment;
        this.mOriginalListData = mListData;
        this.mFilteredListData = mListData;
        this.mContext = mContext;
        density = (int) mContext.getResources().getDisplayMetrics().density;
    }

    /**
     * @return number of items
     */
    @Override
    public int getItemCount() {
        return this.mFilteredListData.size();
    }
    /**
     * @param position item position
     * @return item viewType
     */
    @Override
    public int getItemViewType(int position) {
        return 1;
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
        View v = inflater.inflate(R.layout.chatlist_item, viewGroup, false);
        viewHolder = new ViewHolderChatlist(v);
        return viewHolder;
    }


    /**
     * @param viewHolder RecyclerView.ViewHolder
     * @param position   item position
     */

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {


        ViewHolderChatlist vh2 = (ViewHolderChatlist) viewHolder;
        configureViewHolderChatlist(vh2, position);

    }


    /**
     * @param vh       ViewHolderChatlist
     * @param position item position
     */
    private void configureViewHolderChatlist(final ViewHolderChatlist vh, int position)
    {
        final ChatlistItem chat = mFilteredListData.get(position);
        if (chat != null)
        {
            vh.storeName.setText(chat.getProductName());
            vh.newMessage.setText(chat.getNewMessage());
            try {
                String formatedDate = Utilities.formatDate(Utilities.tsFromGmt(chat.getNewMessageTime()));
                if ((chat.getNewMessageTime().substring(0, 8)).equals((Utilities.tsInGmt().substring(0, 8)))) {
                    vh.newMessageDate.setText(R.string.Today);
                } else if ((Integer.parseInt((Utilities.tsInGmt().substring(0, 8))) - Integer.parseInt((chat.getNewMessageTime().substring(0, 8)))) == 1) {

                    vh.newMessageDate.setText(R.string.Yesterday);

                } else {
                    vh.newMessageDate.setText(formatedDate.substring(9, 24));

                }
                String last = convert24to12hourformat(formatedDate.substring(0, 9));

                vh.newMessageTime.setText(last);


                if (chat.hasNewMessage()) {
                    vh.newMessageCount.setText(chat.getNewMessageCount());
                    vh.rl.setVisibility(View.VISIBLE);
                } else {
                    vh.newMessageCount.setText("");
                    vh.rl.setVisibility(View.GONE);
                }


            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            if (chat.getProductImage() != null && !chat.getProductImage().isEmpty())
            {
                Glide.with(mContext).load(chat.getProductImage()).asBitmap()
                        .centerCrop().placeholder(R.drawable.chat_attachment_profile_default_image_frame).
                        into(new BitmapImageViewTarget(vh.storeImage) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable =
                                        RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                vh.storeImage.setImageDrawable(circularBitmapDrawable);
                            }
                        });
            } else {

                try {
                    vh.storeImage.setImageDrawable(TextDrawable.builder()
                            .beginConfig()
                            .textColor(Color.WHITE)
                            .useFont(Typeface.DEFAULT)
                            .fontSize(24 * density) /* size in px */
                            .bold()
                            .toUpperCase()
                            .endConfig()
                            .buildRound((chat.getProductName().trim()).charAt(0) + "", Color.parseColor(AppController.getInstance().getColorCode(vh.getAdapterPosition() % 19))));
                } catch (IndexOutOfBoundsException e) {
                    vh.storeImage.setImageDrawable(TextDrawable.builder()
                            .beginConfig()
                            .textColor(Color.WHITE)
                            .useFont(Typeface.DEFAULT)
                            .fontSize(24 * density) /* size in px */
                            .bold()
                            .toUpperCase()
                            .endConfig()
                            .buildRound("C", Color.parseColor(AppController.getInstance().getColorCode(vh.getAdapterPosition() % 19))));
                }
            }
            Log.d("log14", chat.isShowTick() + "" + chat.getTickStatus());
            vh.tick.clearColorFilter();
//            vh.tick.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_double_tick));
            vh.tick.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_single_tick));
            vh.tick.setVisibility(View.GONE);
            if(chat.isSold())
            {
                vh.tick.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.error_warn_icon));
                vh.tick.setColorFilter(ContextCompat.getColor(mContext, R.color.red_btn_bg_color), PorterDuff.Mode.SRC_IN);
                vh.tick.setVisibility(View.VISIBLE);
                vh.newMessage.setText(R.string.no_longer_exist);
            }else
            {
                if (chat.isShowTick()) {
                    vh.tick.setVisibility(View.VISIBLE);
                    if (chat.getTickStatus() == 0) {

                        vh.tick.clearColorFilter();
                        vh.tick.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.clock));

                    } else if (chat.getTickStatus() == 1)
                    {
                        vh.tick.clearColorFilter();
                        vh.tick.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_single_tick));
                    } else if (chat.getTickStatus() == 2) {
                        vh.tick.clearColorFilter();
//                        vh.tick.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_double_tick));
                        vh.tick.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_single_tick));
                    } else if (chat.getTickStatus() == 3) {
                        vh.tick.setColorFilter(ContextCompat.getColor(mContext, R.color.chat_blue_tick), PorterDuff.Mode.SRC_IN);
//                        vh.tick.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_double_tick));
                        vh.tick.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_single_tick));
                    }
                } else {
                    vh.tick.setVisibility(View.GONE);
                }
            }
        }
    }
    /**
     * @param d date in 24 hour format
     * @return date in 12 hour format
     */

    private String convert24to12hourformat(String d) {

        String datein12hour = null;

        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            final Date dateObj = sdf.parse(d);


            datein12hour = new SimpleDateFormat("h:mm a").format(dateObj);
        } catch (final ParseException e) {
            e.printStackTrace();
        }


        return datein12hour;

    }

    /**
     * @return list of filtered items
     */
    @Override
    public Filter getFilter()
    {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mFilteredListData = (ArrayList<ChatlistItem>) results.values;
                ReceivedChatsAdapter.this.notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<ChatlistItem> filteredResults;
                if (constraint.length() == 0) {
                    filteredResults = mOriginalListData;
                } else {
                    filteredResults = getFilteredResults(constraint.toString().toLowerCase());

                    if (filteredResults.size() == 0) {


                        (fragment).showNoSearchResults(constraint);

                    }

                }

                FilterResults results = new FilterResults();
                results.values = filteredResults;

                return results;
            }
        };
    }


    /**
     * @param constraint query to search for
     * @return ArrayList<ChatlistItem>
     */

    private ArrayList<ChatlistItem> getFilteredResults(String constraint) {
        ArrayList<ChatlistItem> results = new ArrayList<>();

        for (ChatlistItem item : mOriginalListData) {
            if (item.getProductName().toLowerCase().contains(constraint)) {
                results.add(item);
            }
        }
        return results;
    }


    /**
     * @return ArrayList<ChatlistItem>
     */

    public ArrayList<ChatlistItem> getList() {


        return mFilteredListData;
    }


}
