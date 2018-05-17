package com.yelo.com.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.yelo.com.R;
import com.yelo.com.event_bus.EventBusDatasHandler;
import com.yelo.com.main.activity.SelfProfileActivity;
import com.yelo.com.main.activity.UserProfileActivity;
import com.yelo.com.pojo_class.user_follow_pojo.FollowResponseDatas;
import com.yelo.com.utility.ApiCall;
import com.yelo.com.utility.ApiUrl;
import com.yelo.com.utility.CircleTransform;
import com.yelo.com.utility.CommonClass;
import com.yelo.com.utility.SessionManager;

import java.util.ArrayList;

/**
 * <h>HomeFragRvAdapter</h>
 * <p>
 *     In class is called from SelfFollowingActivity class. In this recyclerview adapter class we used to inflate
 *     single_row_followers layout and shows follower or following lists.
 * </p>
 * @since 4/21/2017
 */
public class UserFollowRvAdapter extends RecyclerView.Adapter<UserFollowRvAdapter.MyViewHolder>
{
    private static final String TAG = UserFollowRvAdapter.class.getSimpleName();
    private Activity mActivity;
    private ArrayList<FollowResponseDatas> arrayListFollow;
    private ApiCall apiCall;
    public int followingCount;
    private SessionManager mSessionManager;
    private EventBusDatasHandler mEventBusDatasHandler;

    /**
     * <h>CurrencyRvAdap</h>
     * <p>
     *     This is simple constructor to initailize list datas and context.
     * </p>
     * @param mActivity The current context
     * @param arrayListFollow The list datas
     */
    public UserFollowRvAdapter(Activity mActivity, ArrayList<FollowResponseDatas> arrayListFollow,int followingCount) {
        this.mActivity = mActivity;
        this.arrayListFollow = arrayListFollow;
        this.followingCount=followingCount;
        mSessionManager = new SessionManager(mActivity);
        apiCall=new ApiCall(mActivity);
        mEventBusDatasHandler = new EventBusDatasHandler(mActivity);
        System.out.println(TAG+" "+"al size="+arrayListFollow.size());
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
        View exploreView= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_followers,parent,false);
        return new MyViewHolder(exploreView);
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
    public void onBindViewHolder(final MyViewHolder holder, int position)
    {
        String fullName,followsFlag;
        final String profilePicUrl,username;

        fullName=arrayListFollow.get(position).getFullName();
        if (fullName==null || fullName.isEmpty())
            fullName=arrayListFollow.get(position).getFullname();
        username=arrayListFollow.get(position).getUsername();
        profilePicUrl=arrayListFollow.get(position).getProfilePicUrl();
        followsFlag=arrayListFollow.get(position).getUserFollowRequestStatus();

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
                    .placeholder(R.drawable.app_launcher_logo)
                    .error(R.drawable.app_launcher_logo)
                    .into(holder.iV_userPic);

        // for own hide the follow button
        if (username!=null && username.equals(mSessionManager.getUserName()))
            holder.rL_follow.setVisibility(View.GONE);
        else holder.rL_follow.setVisibility(View.VISIBLE);

        // show following button if the flag is one
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

        // Follow or unfollow
        holder.rL_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String url;
                if (arrayListFollow.get(holder.getAdapterPosition()).getFollowsFlag().equals("1"))
                {
                    unfollowUserAlert(username,profilePicUrl,holder.rL_follow,holder.tV_follow,holder.getAdapterPosition());
                }
                else
                {
                    followingCount=followingCount+1;
                    arrayListFollow.get(holder.getAdapterPosition()).setFollowsFlag("1");
                    url= ApiUrl.FOLLOW+username;
                    apiCall.followUserApi(url);
                    holder.rL_follow.setBackgroundResource(R.drawable.rect_purple_color_with_solid_shape);
                    holder.tV_follow.setTextColor(ContextCompat.getColor(mActivity,R.color.white));
                    holder.tV_follow.setText(mActivity.getResources().getString(R.string.Following));
                    if (arrayListFollow.get(holder.getAdapterPosition()).getPostData().size()>holder.getAdapterPosition())
                    mEventBusDatasHandler.setSocialDatasFromDiscovery(username,profilePicUrl,arrayListFollow.get(holder.getAdapterPosition()).getPostData().get(0),true);
                }
            }
        });

        // User Profile
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

    /**
     * <h>unfollowUserAlert</h>
     * <p>
     *     In this method we used to open a simple dialog pop-up to show
     *     alert to unfollow
     * </p>
     */
    private void unfollowUserAlert(final String membername, final String memberProfilePicUrl, final RelativeLayout rL_follow, final TextView tV_follow, final int position)
    {
        final Dialog unfollowUserDialog = new Dialog(mActivity);
        unfollowUserDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        unfollowUserDialog.setContentView(R.layout.dialog_unfollow_user);
        unfollowUserDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        unfollowUserDialog.getWindow().setLayout((int)(CommonClass.getDeviceWidth(mActivity)*0.9), RelativeLayout.LayoutParams.WRAP_CONTENT);

        // set user pic
        ImageView imageViewPic= (ImageView)unfollowUserDialog.findViewById(R.id.iV_userPic);
        imageViewPic.getLayoutParams().width=CommonClass.getDeviceWidth(mActivity)/5;
        imageViewPic.getLayoutParams().height=CommonClass.getDeviceWidth(mActivity)/5;

        // posted by pic
        if (memberProfilePicUrl!=null && !memberProfilePicUrl.isEmpty())
            Picasso.with(mActivity)
                    .load(memberProfilePicUrl)
                    .transform(new CircleTransform())
                    .placeholder(R.drawable.default_profile_image)
                    .error(R.drawable.default_profile_image)
                    .into(imageViewPic);

        // set user name
        TextView tV_userName= (TextView)unfollowUserDialog.findViewById(R.id.tV_userName);
        if (membername!=null && !membername.isEmpty())
        {
            String setUserName=mActivity.getResources().getString(R.string.at_the_rate)+membername+mActivity.getResources().getString(R.string.question_mark);
            tV_userName.setText(setUserName);
        }

        // set cancel button
        TextView tV_cancel= (TextView) unfollowUserDialog.findViewById(R.id.tV_cancel);
        tV_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unfollowUserDialog.dismiss();
            }
        });

        // set done button
        TextView tV_unfollow= (TextView)unfollowUserDialog.findViewById(R.id.tV_unfollow);
        tV_unfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followingCount=followingCount-1;
                String url= ApiUrl.UNFOLLOW+membername;
                apiCall.followUserApi(url);
                rL_follow.setBackgroundResource(R.drawable.rect_purple_color_with_stroke_shape);
                tV_follow.setTextColor(ContextCompat.getColor(mActivity,R.color.purple_color));
                tV_follow.setText(mActivity.getResources().getString(R.string.follow));
                arrayListFollow.get(position).setFollowsFlag("0");
                if (arrayListFollow.get(position).getPostData().size()>position)
                mEventBusDatasHandler.setSocialDatasFromDiscovery(membername,memberProfilePicUrl,arrayListFollow.get(position).getPostData().get(0),false);
                unfollowUserDialog.dismiss();
            }
        });
        unfollowUserDialog.show();
    }

    /**
     * Return the size of your dataset
     * @return the total number of rows
     */
    @Override
    public int getItemCount() {
        return arrayListFollow.size();
    }

    /**
     * <h>MyViewHolder</h>
     * <p>
     *     In this class we used to declare and assign the xml variables.
     * </p>
     */
    class MyViewHolder extends RecyclerView.ViewHolder
    {
        ImageView iV_userPic;
        TextView tV_fullName,tV_userName,tV_follow;
        RelativeLayout rL_follow,rL_user;

        MyViewHolder(View itemView) {
            super(itemView);
            iV_userPic= (ImageView) itemView.findViewById(R.id.iV_userPic);
            iV_userPic.getLayoutParams().width= CommonClass.getDeviceWidth(mActivity)/7;
            iV_userPic.getLayoutParams().height= CommonClass.getDeviceWidth(mActivity)/7;
            tV_fullName= (TextView) itemView.findViewById(R.id.tV_fullName);
            tV_userName= (TextView) itemView.findViewById(R.id.tV_userName);
            rL_follow= (RelativeLayout) itemView.findViewById(R.id.relative_follow);
            rL_user= (RelativeLayout) itemView.findViewById(R.id.rL_user);
            tV_follow= (TextView) itemView.findViewById(R.id.tV_follow);
            //rL_follow= (RelativeLayout) itemView.findViewById(rL_follow);
        }
    }
}
