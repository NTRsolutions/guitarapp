package com.yelo.com.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.yelo.com.R;
import com.yelo.com.event_bus.EventBusDatasHandler;
import com.yelo.com.main.activity.FacebookFriendsActivity;
import com.yelo.com.main.activity.PhoneContactsActivity;
import com.yelo.com.main.activity.SelfProfileActivity;
import com.yelo.com.main.activity.UserProfileActivity;
import com.yelo.com.pojo_class.UserPostDataPojo;
import com.yelo.com.pojo_class.discovery_people_pojo.DiscoverPeopleResponse;
import com.yelo.com.utility.ApiCall;
import com.yelo.com.utility.ApiUrl;
import com.yelo.com.utility.CircleTransform;
import com.yelo.com.utility.CommonClass;
import com.yelo.com.utility.RoundedCornersTransform;
import com.yelo.com.utility.SessionManager;
import com.yelo.com.utility.VariableConstants;

import java.util.ArrayList;

/**
 * <h>DiscoveryPeopleRvAdapter</h>
 * <p>
 *     This class is called from myprofile screen. In this class we show the friends
 *     list and follow button.
 * </p>
 * @since 4/27/2017
 */
public class DiscoveryPeopleRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private static final int TYPE_HEADER=0;
    private static final int TYPE_ITEM=1;
    private static final String TAG = DiscoveryPeopleRvAdapter.class.getSimpleName();
    private Activity mActivity;
    private ArrayList<DiscoverPeopleResponse> arrayListDiscoverData;
    private ApiCall apiCall;
    public  TextView tV_fb_friends_count,tV_contact_friend_count;
    public int followingCount;
    private EventBusDatasHandler mEventBusDatasHandler;
    private SessionManager mSessionManager;
    private String username;

    /**
     * <h>DiscoveryPeopleRvAdapter</h>
     * <p>
     *     This is simple constructor to initialze the variables.
     * </p>
     * @param mActivity The activity context
     * @param arrayListDiscoverData The response datas.
     */
    public DiscoveryPeopleRvAdapter(Activity mActivity, ArrayList<DiscoverPeopleResponse> arrayListDiscoverData,int followingCount) {
        this.mActivity = mActivity;
        this.arrayListDiscoverData = arrayListDiscoverData;
        this.followingCount=followingCount;
        mEventBusDatasHandler = new EventBusDatasHandler(mActivity);
        apiCall=new ApiCall(mActivity);
        mSessionManager = new SessionManager(mActivity);
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
        switch (viewType)
        {
            case TYPE_HEADER :
                view= LayoutInflater.from(mActivity).inflate(R.layout.header_discover_people,parent,false);
                return new HeaderViewHolder(view);

            case TYPE_ITEM :
                view= LayoutInflater.from(mActivity).inflate(R.layout.single_row_discover_people,parent,false);
                return new ItemViewHolder(view);
        }
        return null;
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
        // Header view
        if (holder instanceof HeaderViewHolder)
        {
            HeaderViewHolder headerViewHolder= (HeaderViewHolder) holder;

            // Connect to contact
            headerViewHolder.rL_connectToContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(mActivity, PhoneContactsActivity.class);
                    intent.putExtra("followingCount",followingCount);
                    mActivity.startActivityForResult(intent, VariableConstants.CONTACT_FRIEND_REQ_CODE);
                }
            });

            // search facebook friends
            headerViewHolder.rL_connectTofb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(mActivity, FacebookFriendsActivity.class);
                    intent.putExtra("followingCount",followingCount);
                    mActivity.startActivityForResult(intent,VariableConstants.FB_FRIEND_REQ_CODE);
                }
            });
        }

        // item view
        if (holder instanceof ItemViewHolder)
        {
            position=position-1;
            final ItemViewHolder itemViewHolder= (ItemViewHolder) holder;
            final String profilePicUrl,followsFlag,postedByUserFullName;
            String postedByUserName;

            profilePicUrl=arrayListDiscoverData.get(position).getProfilePicUrl();
            followsFlag=arrayListDiscoverData.get(position).getFollowsFlag();
            postedByUserName=arrayListDiscoverData.get(position).getPostedByUserName();
            username=arrayListDiscoverData.get(position).getPostedByUserName();
            postedByUserFullName=arrayListDiscoverData.get(position).getPostedByUserFullName();

            // Profile pic url
            if (profilePicUrl!=null && !profilePicUrl.isEmpty())
                Picasso.with(mActivity)
                        .load(profilePicUrl)
                        .transform(new CircleTransform())
                        .placeholder(R.drawable.app_launcher_logo)
                        .error(R.drawable.app_launcher_logo)
                        .into(itemViewHolder.iV_profilePicUrl);

            // follow and following icon
            if (followsFlag!=null && followsFlag.equals("1"))
            {
                itemViewHolder.rL_follow.setBackgroundResource(R.drawable.rect_purple_color_with_solid_shape);
                itemViewHolder.tV_follow.setTextColor(ContextCompat.getColor(mActivity,R.color.white));
                itemViewHolder.tV_follow.setText(mActivity.getResources().getString(R.string.Following));
            }
            else
            {
                itemViewHolder.rL_follow.setBackgroundResource(R.drawable.rect_purple_color_with_stroke_shape);
                itemViewHolder.tV_follow.setTextColor(ContextCompat.getColor(mActivity,R.color.purple_color));
                itemViewHolder.tV_follow.setText(mActivity.getResources().getString(R.string.follow));
            }

            // hide row
            itemViewHolder.tV_hide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeItem(holder.getAdapterPosition());
                }
            });

            final ArrayList<UserPostDataPojo> arrayListPostData=arrayListDiscoverData.get(position).getPostData();

            // Follow
            final String finalPostedByUserName = postedByUserName;
            itemViewHolder.rL_follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    String url;
                    String mFollowsFlag=arrayListDiscoverData.get(holder.getAdapterPosition()-1).getFollowsFlag();

                    // unfollow
                    if (mFollowsFlag!=null && mFollowsFlag.equals("1"))
                    {
                        followingCount=followingCount-1;
                        itemViewHolder.tV_hide.setVisibility(View.VISIBLE);
                        itemViewHolder.rL_follow.setBackgroundResource(R.drawable.rect_purple_color_with_stroke_shape);
                        itemViewHolder.tV_follow.setTextColor(ContextCompat.getColor(mActivity,R.color.purple_color));
                        itemViewHolder.tV_follow.setText(mActivity.getResources().getString(R.string.follow));
                        arrayListDiscoverData.get(holder.getAdapterPosition()-1).setFollowsFlag("0");
                        url=ApiUrl.UNFOLLOW+ finalPostedByUserName;
                        if (arrayListPostData!=null && arrayListPostData.size()>0)
                        mEventBusDatasHandler.setSocialDatasFromDiscovery(finalPostedByUserName,profilePicUrl,arrayListPostData.get(0),false);
                        apiCall.followUserApi(url);
                    }

                    // follow
                    else
                    {
                        followingCount=followingCount+1;
                        itemViewHolder.tV_hide.setVisibility(View.GONE);
                        itemViewHolder.rL_follow.setBackgroundResource(R.drawable.rect_purple_color_with_solid_shape);
                        itemViewHolder.tV_follow.setTextColor(ContextCompat.getColor(mActivity,R.color.white));
                        itemViewHolder.tV_follow.setText(mActivity.getResources().getString(R.string.Following));
                        arrayListDiscoverData.get(holder.getAdapterPosition()-1).setFollowsFlag("1");
                        url=ApiUrl.FOLLOW+finalPostedByUserName;
                        if (arrayListPostData!=null && arrayListPostData.size()>0)
                            mEventBusDatasHandler.setSocialDatasFromDiscovery(finalPostedByUserName,profilePicUrl,arrayListPostData.get(0),true);
                        apiCall.followUserApi(url);
                    }
                    System.out.println(TAG+" "+"followingCount="+followingCount);
                }
            });

            // user name
            if (postedByUserName!=null)
            {
                postedByUserName=postedByUserName.substring(0,1).toUpperCase()+postedByUserName.substring(1).toLowerCase();
                itemViewHolder.tV_postedByUserName.setText(postedByUserName);
            }

            // full name
            if (postedByUserFullName!=null)
                itemViewHolder.tV_postedByUserFullName.setText(postedByUserFullName);

            // Post datas
            if (arrayListPostData!=null && arrayListPostData.size()>0)
            {
                itemViewHolder.horizontal_posts.setVisibility(View.VISIBLE);
                itemViewHolder.tV_noPost.setVisibility(View.GONE);
                itemViewHolder.view_divider.setVisibility(View.VISIBLE);
                LayoutInflater layoutInflater=LayoutInflater.from(mActivity);
                itemViewHolder.linear_postData.removeAllViews();
                for (int postCount=0;postCount<arrayListPostData.size();postCount++)
                {
                    View view=layoutInflater.inflate(R.layout.single_row_images,null,false);
                    String thumbnailImageUrl=arrayListPostData.get(postCount).getThumbnailImageUrl();
                    ImageView iV_thumbnailImage= (ImageView) view.findViewById(R.id.iV_image);
                    iV_thumbnailImage.getLayoutParams().width=CommonClass.getDeviceWidth(mActivity)/5;
                    iV_thumbnailImage.getLayoutParams().height=CommonClass.getDeviceWidth(mActivity)/5;
                    CommonClass.setMargins(iV_thumbnailImage,5,0,5,0);

                    if (thumbnailImageUrl!=null && !thumbnailImageUrl.isEmpty())
                        Picasso.with(mActivity)
                                .load(thumbnailImageUrl)
                                .transform(new RoundedCornersTransform())
                                .placeholder(R.drawable.app_launcher_logo)
                                .error(R.drawable.app_launcher_logo)
                                .into(iV_thumbnailImage);
                    itemViewHolder.linear_postData.addView(view);
                }
            }
            else
            {
                itemViewHolder.horizontal_posts.setVisibility(View.GONE);
                itemViewHolder.tV_noPost.setVisibility(View.VISIBLE);
                itemViewHolder.view_divider.setVisibility(View.GONE);
            }

            final String finalPostedByUserName1 = username;
            itemViewHolder.rL_memeberName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*Intent intent=new Intent(mActivity, UserProfileActivity.class);
                    intent.putExtra("membername",arrayListDiscoverData.get(itemViewHolder.getAdapterPosition()-1).getPostedByUserName());
                    mActivity.startActivity(intent);*/

                    Intent intent;
                    if (mSessionManager.getIsUserLoggedIn() && mSessionManager.getUserName().equals(finalPostedByUserName1))
                    {
                        intent = new Intent(mActivity, SelfProfileActivity.class);
                        intent.putExtra("membername", finalPostedByUserName1);
                    }
                    else
                    {
                        intent = new Intent(mActivity, UserProfileActivity.class);
                        intent.putExtra("membername", finalPostedByUserName1);
                    }
                    mActivity.startActivity(intent);
                }
            });
        }
    }

    /**
     * Return the size of your dataset
     * @return the total number of rows
     */
    @Override
    public int getItemCount() {
        return arrayListDiscoverData.size()+1;
    }

    /**
     * <h>GetItemViewType</h>
     * <p>
     *     In this method we used to return the type whether it is Header or item.
     * </p>
     * @param position The position of the row.
     * @return it returns the type of Item
     */
    @Override
    public int getItemViewType(int position) {
        if (position==0)
            return TYPE_HEADER;
        else return TYPE_ITEM;
    }

    /**
     * In this class we used to declare and assign the HeaderView variables.
     */


    private class HeaderViewHolder extends RecyclerView.ViewHolder
    {
        RelativeLayout rL_connectToContact,rL_connectTofb;

        HeaderViewHolder(View itemView) {
            super(itemView);
            rL_connectToContact= (RelativeLayout) itemView.findViewById(R.id.rL_connectToContact);
            rL_connectTofb= (RelativeLayout) itemView.findViewById(R.id.rL_connectTofb);
            tV_fb_friends_count= (TextView) itemView.findViewById(R.id.tV_fb_friends_count);
            tV_contact_friend_count= (TextView) itemView.findViewById(R.id.tV_contect_friend_count);
        }
    }

    /**
     * In this class we used to declare and assign the Item variables.
     */
    private class ItemViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView iV_profilePicUrl;
        private TextView tV_postedByUserName,tV_postedByUserFullName,tV_hide,tV_follow,tV_noPost;
        private LinearLayout linear_postData;
        private RelativeLayout rL_follow,rL_memeberName;
        private HorizontalScrollView horizontal_posts;
        private View view_divider;

        ItemViewHolder(View itemView) {
            super(itemView);
            iV_profilePicUrl= (ImageView) itemView.findViewById(R.id.iV_profilePicUrl);
            iV_profilePicUrl.getLayoutParams().width= CommonClass.getDeviceWidth(mActivity)/7;
            iV_profilePicUrl.getLayoutParams().height= CommonClass.getDeviceWidth(mActivity)/7;
            rL_follow= (RelativeLayout)itemView.findViewById(R.id.relative_follow);
            rL_memeberName= (RelativeLayout)itemView.findViewById(R.id.rL_memeberName);
            tV_postedByUserName= (TextView) itemView.findViewById(R.id.tV_postedByUserName);
            tV_postedByUserFullName= (TextView) itemView.findViewById(R.id.tV_postedByUserFullName);
            tV_hide= (TextView) itemView.findViewById(R.id.tV_hide);
            tV_follow= (TextView) itemView.findViewById(R.id.tV_follow);
            linear_postData= (LinearLayout) itemView.findViewById(R.id.linear_postData);
            horizontal_posts= (HorizontalScrollView) itemView.findViewById(R.id.horizontal_posts);
            view_divider=itemView.findViewById(R.id.view_divider);
            tV_noPost= (TextView) itemView.findViewById(R.id.tV_noPost);
        }
    }

    private void removeItem(int position) {
        arrayListDiscoverData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, arrayListDiscoverData.size());
    }
}
