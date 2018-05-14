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
import com.yelo.com.main.activity.SelfProfileActivity;
import com.yelo.com.main.activity.UserProfileActivity;
import com.yelo.com.pojo_class.phone_contact_pojo.PhoneContactData;
import com.yelo.com.pojo_class.phone_contact_pojo.PhoneContactPostData;
import com.yelo.com.utility.ApiCall;
import com.yelo.com.utility.ApiUrl;
import com.yelo.com.utility.CircleTransform;
import com.yelo.com.utility.CommonClass;
import com.yelo.com.utility.RoundedCornersTransform;
import com.yelo.com.utility.SessionManager;

import java.util.ArrayList;

/**
 * <h>PhoneContactRvAdapter</h>
 * <p>
 *     In class is called from PhoneContactsActivity class. In this recyclerview adapter class we used to inflate
 *     single_row_discover_people layout and shows the friends from contact list who uses this app.
 * </p>
 * @since 04-Jul-17
 */
public class PhoneContactRvAdapter extends RecyclerView.Adapter<PhoneContactRvAdapter.MyViewHolder>
{
    private Activity mActivity;
    private ArrayList<PhoneContactData> arrayListContacts;
    private ApiCall apiCall;
    public int followingCount;
    private EventBusDatasHandler mEventBusDatasHandler;
    private SessionManager mSessionManager;

    /**
     * <h>PhoneContactRvAdapter</h>
     * <p>
     *     This is simple constructor to initailize list datas and context.
     * </p>
     * @param mActivity The current context
     * @param arrayListContacts The list datas
     */
    public PhoneContactRvAdapter(Activity mActivity, ArrayList<PhoneContactData> arrayListContacts,int followingCount) {
        this.mActivity = mActivity;
        this.arrayListContacts = arrayListContacts;
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
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mActivity).inflate(R.layout.single_row_discover_people,parent,false);
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
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final String profilePicUrl,followsFlag,postedByUserName,postedByUserFullName;

        profilePicUrl=arrayListContacts.get(position).getProfilePicUrl();
        followsFlag=arrayListContacts.get(position).getFollowing();
        postedByUserName=arrayListContacts.get(position).getMembername();
        postedByUserFullName=arrayListContacts.get(position).getFullName();

        // Profile pic url
        if (profilePicUrl!=null && !profilePicUrl.isEmpty())
            Picasso.with(mActivity)
                    .load(profilePicUrl)
                    .transform(new CircleTransform())
                    .placeholder(R.drawable.app_launcher_logo)
                    .error(R.drawable.app_launcher_logo)
                    .into(holder.iV_profilePicUrl);

        // follow and following icon
        if (followsFlag!=null && followsFlag.equals("1"))
        {
            holder.rL_follow.setBackgroundResource(R.drawable.rect_purple_color_with_solid_shape);
            holder.tV_follow.setTextColor(ContextCompat.getColor(mActivity,R.color.white));
            holder.tV_follow.setText(mActivity.getResources().getString(R.string.Following));
        }
        else
        {
            holder.rL_follow.setBackgroundResource(R.drawable.rect_purple_color_with_stroke_shape);
            holder.tV_follow.setTextColor(ContextCompat.getColor(mActivity,R.color.purple_color));
            holder.tV_follow.setText(mActivity.getResources().getString(R.string.follow));
        }

        // hide row
        holder.tV_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItem(holder.getAdapterPosition());
            }
        });
        final ArrayList<PhoneContactPostData> arrayListPostData=arrayListContacts.get(position).getPostData();

        // Follow
        holder.rL_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String url;
                String mFollowsFlag=arrayListContacts.get(holder.getAdapterPosition()).getFollowing();
                if (mFollowsFlag!=null && mFollowsFlag.equals("1"))
                {
                    followingCount=followingCount-1;
                    holder.tV_hide.setVisibility(View.VISIBLE);
                    holder.rL_follow.setBackgroundResource(R.drawable.rect_purple_color_with_stroke_shape);
                    holder.tV_follow.setTextColor(ContextCompat.getColor(mActivity,R.color.purple_color));
                    holder.tV_follow.setText(mActivity.getResources().getString(R.string.follow));
                    arrayListContacts.get(holder.getAdapterPosition()).setFollowing("0");
                    url=ApiUrl.UNFOLLOW+postedByUserName;

                    if (arrayListPostData!=null && arrayListPostData.size()>0)
                        mEventBusDatasHandler.setSocialDatasFromContactsFriends(arrayListPostData.get(0),false);
                    apiCall.followUserApi(url);
                }
                else
                {
                    followingCount=followingCount+1;
                    holder.tV_hide.setVisibility(View.GONE);
                    holder.rL_follow.setBackgroundResource(R.drawable.rect_purple_color_with_solid_shape);
                    holder.tV_follow.setTextColor(ContextCompat.getColor(mActivity,R.color.white));
                    holder.tV_follow.setText(mActivity.getResources().getString(R.string.Following));
                    arrayListContacts.get(holder.getAdapterPosition()).setFollowing("1");
                    url=ApiUrl.FOLLOW+postedByUserName;
                    if (arrayListPostData!=null && arrayListPostData.size()>0)
                        mEventBusDatasHandler.setSocialDatasFromContactsFriends(arrayListPostData.get(0),true);
                    apiCall.followUserApi(url);
                }
            }
        });

        // user name
        if (postedByUserName!=null)
            holder.tV_postedByUserName.setText(postedByUserName);

        // full name
        if (postedByUserFullName!=null)
            holder.tV_postedByUserFullName.setText(postedByUserFullName);

        // Post datas
        if (arrayListPostData!=null && arrayListPostData.size()>0)
        {
            holder.horizontal_posts.setVisibility(View.VISIBLE);
            holder.tV_noPost.setVisibility(View.GONE);
            holder.view_divider.setVisibility(View.VISIBLE);

            LayoutInflater layoutInflater=LayoutInflater.from(mActivity);
            holder.linear_postData.removeAllViews();
            for (int postCount=0;postCount<arrayListPostData.size();postCount++)
            {
                View view=layoutInflater.inflate(R.layout.single_row_images,null,false);
                String thumbnailImageUrl=arrayListPostData.get(postCount).getThumbnailImageUrl();
                ImageView iV_thumbnailImage= (ImageView) view.findViewById(R.id.iV_image);
                iV_thumbnailImage.getLayoutParams().width=CommonClass.getDeviceWidth(mActivity)/5;
                iV_thumbnailImage.getLayoutParams().height=CommonClass.getDeviceWidth(mActivity)/5;
                CommonClass.setMargins(iV_thumbnailImage,5,0,5,0);

                if (thumbnailImageUrl!=null && !thumbnailImageUrl.isEmpty()) {
                    Picasso.with(mActivity)
                            .load(thumbnailImageUrl)
                            .transform(new RoundedCornersTransform())
                            .placeholder(R.drawable.default_image)
                            .error(R.drawable.default_image)
                            .into(iV_thumbnailImage);
                    holder.linear_postData.addView(view);
                }
            }
        }
        else
        {
            holder.horizontal_posts.setVisibility(View.GONE);
            holder.tV_noPost.setVisibility(View.VISIBLE);
            holder.view_divider.setVisibility(View.GONE);
        }

        // open user profile
        holder.rL_memeberName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent=new Intent(mActivity, UserProfileActivity.class);
                intent.putExtra("membername",postedByUserName);
                mActivity.startActivity(intent);*/

                Intent intent;
                if (mSessionManager.getIsUserLoggedIn() && mSessionManager.getUserName().equals(postedByUserName))
                {
                    intent = new Intent(mActivity, SelfProfileActivity.class);
                    intent.putExtra("membername",postedByUserName);
                }
                else
                {
                    intent = new Intent(mActivity, UserProfileActivity.class);
                    intent.putExtra("membername",postedByUserName);
                }
                mActivity.startActivity(intent);
            }
        });
    }

    /**
     * Return the size of your dataset
     * @return the total number of rows
     */
    @Override
    public int getItemCount() {
        return arrayListContacts.size();
    }

    /**
     * <h>MyViewHolder</h>
     * <p>
     *     In this class we used to declare and assign the xml variables.
     * </p>
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView iV_profilePicUrl;
        private TextView tV_postedByUserName,tV_postedByUserFullName,tV_hide,tV_follow,tV_noPost;
        private LinearLayout linear_postData;
        private RelativeLayout rL_follow,rL_memeberName;
        private HorizontalScrollView horizontal_posts;
        private View view_divider;

        MyViewHolder(View itemView) {
            super(itemView);
            iV_profilePicUrl= (ImageView) itemView.findViewById(R.id.iV_profilePicUrl);
            iV_profilePicUrl.getLayoutParams().width= CommonClass.getDeviceWidth(mActivity)/7;
            iV_profilePicUrl.getLayoutParams().height= CommonClass.getDeviceWidth(mActivity)/7;
            rL_follow= (RelativeLayout)itemView.findViewById(R.id.relative_follow);
            rL_memeberName= (RelativeLayout)itemView.findViewById(R.id.rL_memeberName);
            tV_postedByUserName= (TextView) itemView.findViewById(R.id.tV_postedByUserName);
            tV_postedByUserFullName= (TextView) itemView.findViewById(R.id.tV_postedByUserFullName);
            tV_hide= (TextView) itemView.findViewById(R.id.tV_hide);
            tV_hide.setVisibility(View.GONE);
            tV_follow= (TextView) itemView.findViewById(R.id.tV_follow);
            linear_postData= (LinearLayout) itemView.findViewById(R.id.linear_postData);
            horizontal_posts= (HorizontalScrollView) itemView.findViewById(R.id.horizontal_posts);
            view_divider=itemView.findViewById(R.id.view_divider);
            tV_noPost= (TextView) itemView.findViewById(R.id.tV_noPost);
        }
    }

    private void removeItem(int position) {
        arrayListContacts.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, arrayListContacts.size());
    }
}
