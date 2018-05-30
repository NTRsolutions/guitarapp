package com.yelo.com.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.yelo.com.R;
import com.yelo.com.event_bus.EventBusDatasHandler;
import com.yelo.com.main.activity.SelfProfileActivity;
import com.yelo.com.main.activity.UserProfileActivity;
import com.yelo.com.pojo_class.UserPostDataPojo;
import com.yelo.com.pojo_class.user_likes_pojo.UserLikesResponseDatas;
import com.yelo.com.utility.ApiCall;
import com.yelo.com.utility.ApiUrl;
import com.yelo.com.utility.CircleTransform;
import com.yelo.com.utility.CommonClass;
import com.yelo.com.utility.SessionManager;

import java.util.ArrayList;

/**
 * <h>HomeFragRvAdapter</h>
 * <p>
 *     In class is called from CurrencyListActivity class. In this recyclerview adapter class we used to inflate
 *     single_row_followers layout and shows the all list of Liked User.
 * </p>
 * @since 4/29/2017
 */
public class UserLikesRvAdap extends RecyclerView.Adapter<UserLikesRvAdap.MyViewHolder>
{
    private Activity mActivity;
    private ArrayList<UserLikesResponseDatas> aL_likesDatas;
    private SessionManager mSessionManager;
    private ApiCall apiCall;
    private EventBusDatasHandler mEventBusDatasHandler;

    /**
     * <h>UserLikesRvAdap</h>
     * <p>
     *     This is simple constructor to initailize list datas and context.
     * </p>
     * @param mActivity The current context
     * @param aL_likesDatas The list datas
     */
    public UserLikesRvAdap(Activity mActivity, ArrayList<UserLikesResponseDatas> aL_likesDatas) {
        this.mActivity = mActivity;
        this.aL_likesDatas = aL_likesDatas;
        mSessionManager=new SessionManager(mActivity);
        mEventBusDatasHandler = new EventBusDatasHandler(mActivity);
        apiCall=new ApiCall(mActivity);
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
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_followers,parent,false);
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
        final String fullName,username,profilePicUrl,followsFlag;

        fullName=aL_likesDatas.get(position).getFullname();
        username=aL_likesDatas.get(position).getUsername();
        profilePicUrl=aL_likesDatas.get(position).getProfilePicUrl();
        followsFlag=aL_likesDatas.get(position).getFollowStatus();

        // full name
        if (fullName!=null)
            holder.tV_fullName.setText(fullName);

        // user name
        if (username!=null)
            holder.tV_userName.setText(username);

        // set user pic
        if (profilePicUrl!=null && !profilePicUrl.isEmpty())
            Picasso.with(mActivity)
                    .load(profilePicUrl)
                    .transform(new CircleTransform())
                    .placeholder(R.drawable.profile_bg)
                    .error(R.drawable.profile_bg)
                    .into(holder.iV_userPic);

        // Hide the follow option the looged-in user
        if (mSessionManager.getUserName().equals(aL_likesDatas.get(position).getUsername()))
            holder.rL_follow.setVisibility(View.GONE);
        else holder.rL_follow.setVisibility(View.VISIBLE);

        // set follow and following text and background color
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

        final ArrayList<UserPostDataPojo> arrayListPostData =aL_likesDatas.get(position).getPostData();

        // Follow or unfollow the user
        holder.rL_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String url;
                if (CommonClass.isNetworkAvailable(mActivity))
                {
                    String mFollowStatus=aL_likesDatas.get(holder.getAdapterPosition()).getFollowStatus();
                    if (mFollowStatus!=null && mFollowStatus.equals("1"))
                    {
                        aL_likesDatas.get(holder.getAdapterPosition()).setFollowStatus("0");
                        holder.rL_follow.setBackgroundResource(R.drawable.rect_purple_color_with_stroke_shape);
                        holder.tV_follow.setTextColor(ContextCompat.getColor(mActivity,R.color.purple_color));
                        holder.tV_follow.setText(mActivity.getResources().getString(R.string.follow));
                        url=ApiUrl.UNFOLLOW+aL_likesDatas.get(holder.getAdapterPosition()).getUsername();
                        if (arrayListPostData!=null && arrayListPostData.size()>0)
                            mEventBusDatasHandler.setSocialDatasFromDiscovery(username,profilePicUrl,arrayListPostData.get(0),false);
                        apiCall.followUserApi(url);

                    }
                    else
                    {
                        aL_likesDatas.get(holder.getAdapterPosition()).setFollowStatus("1");
                        holder.rL_follow.setBackgroundResource(R.drawable.rect_purple_color_with_solid_shape);
                        holder.tV_follow.setTextColor(ContextCompat.getColor(mActivity,R.color.white));
                        holder.tV_follow.setText(mActivity.getResources().getString(R.string.Following));
                        url=ApiUrl.FOLLOW+aL_likesDatas.get(holder.getAdapterPosition()).getUsername();
                        if (arrayListPostData!=null && arrayListPostData.size()>0)
                            mEventBusDatasHandler.setSocialDatasFromDiscovery(username,profilePicUrl,arrayListPostData.get(0),true);
                        apiCall.followUserApi(url);
                    }
                }
            }
        });

        // open user profile
        if (username!=null && !username.isEmpty())
        {
            holder.rL_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;
                    if (mSessionManager.getIsUserLoggedIn() && mSessionManager.getUserName().equals(username))
                    {
                        intent = new Intent(mActivity, SelfProfileActivity.class);
                        intent.putExtra("membername",username);
                    }
                    else
                    {
                        intent = new Intent(mActivity, UserProfileActivity.class);
                        intent.putExtra("membername",username);
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
        return aL_likesDatas.size();
    }

    /**
     * <h>MyViewHolder</h>
     * <p>
     *     In this class we used to declare and assign the xml variables.
     * </p>
     */
    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iV_userPic;
        TextView tV_fullName,tV_userName,tV_follow;
        RelativeLayout rL_follow,rL_user;

        MyViewHolder(View itemView)
        {
            super(itemView);
            iV_userPic= (ImageView) itemView.findViewById(R.id.iV_userPic);
            iV_userPic.getLayoutParams().width= CommonClass.getDeviceWidth(mActivity)/7;
            iV_userPic.getLayoutParams().height= CommonClass.getDeviceWidth(mActivity)/7;
            tV_fullName= (TextView) itemView.findViewById(R.id.tV_fullName);
            tV_userName= (TextView) itemView.findViewById(R.id.tV_userName);
            tV_follow= (TextView) itemView.findViewById(R.id.tV_follow);
            rL_follow= (RelativeLayout) itemView.findViewById(R.id.relative_follow);
            rL_user= (RelativeLayout) itemView.findViewById(R.id.rL_user);
        }
    }
}
