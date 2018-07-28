package com.demskigroup.guitaramps.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.event_bus.EventBusDatasHandler;
import com.demskigroup.guitaramps.main.activity.RateUserActivity;
import com.demskigroup.guitaramps.main.activity.SelfProfileActivity;
import com.demskigroup.guitaramps.main.activity.UserProfileActivity;
import com.demskigroup.guitaramps.main.activity.products.ProductDetailsActivity;
import com.demskigroup.guitaramps.pojo_class.UserPostDataPojo;
import com.demskigroup.guitaramps.pojo_class.explore_for_you_pojo.ForYouResposeDatas;
import com.demskigroup.guitaramps.utility.ApiCall;
import com.demskigroup.guitaramps.utility.ApiUrl;
import com.demskigroup.guitaramps.utility.CircleTransform;
import com.demskigroup.guitaramps.utility.CommonClass;
import com.demskigroup.guitaramps.utility.SessionManager;

import java.util.ArrayList;

/**
 * <h>YouFragRvAdapter</h>
 * <p>
 *     In class is called from YouFrag. In this recyclerview adapter class we used to inflate
 *     single_row_images layout and shows the all post posted by logged-in user.
 * </p>
 * @since 4/17/2017
 */
public class YouFragRvAdapter extends RecyclerView.Adapter<YouFragRvAdapter.MyViewHolder>
{
    private Activity mActivity;
    private ArrayList<ForYouResposeDatas> al_selfActivity_data;
    private ApiCall mApiCall;
    private SessionManager mSessionManager;
    private EventBusDatasHandler mEventBusDatasHandler;

    public YouFragRvAdapter(Activity mActivity, ArrayList<ForYouResposeDatas> al_selfActivity_data) {
        this.mActivity = mActivity;
        this.al_selfActivity_data = al_selfActivity_data;
        mApiCall=new ApiCall(mActivity);
        mSessionManager = new SessionManager(mActivity);
        mEventBusDatasHandler = new EventBusDatasHandler(mActivity);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view= LayoutInflater.from(mActivity).inflate(R.layout.single_row_following_activity,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position)
    {
        final String membername,username,notificationType,thumbnailImageUrl,memberProfilePicUrl,setNotificationMessage,postId,productName;
        String createdOn,followRequestStatus;

        username=al_selfActivity_data.get(position).getUsername();
        membername=al_selfActivity_data.get(position).getMembername();
        createdOn=al_selfActivity_data.get(position).getCreatedOn();
        notificationType=al_selfActivity_data.get(position).getNotificationType();
        thumbnailImageUrl=al_selfActivity_data.get(position).getThumbnailImageUrl();
        memberProfilePicUrl=al_selfActivity_data.get(position).getMemberProfilePicUrl();
        postId=al_selfActivity_data.get(position).getPostId();
        followRequestStatus=al_selfActivity_data.get(position).getFollowRequestStatus();
        productName = al_selfActivity_data.get(position).getProductName();

        // set member Profile Pic
        if (memberProfilePicUrl!=null && !memberProfilePicUrl.isEmpty())
            Picasso.with(mActivity)
                    .load(memberProfilePicUrl)
                    .transform(new CircleTransform())
                    .placeholder(R.drawable.profile_bg)
                    .error(R.drawable.profile_bg)
                    .into(holder.iV_user1_profilePic);

        holder.iV_user1_profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               openUserProfileScreen(membername);
            }
        });

        // set message according to notification Type
        if (notificationType!=null)
        {
            switch (notificationType)
            {
                // likedPost
                case "2":
                    holder.iV_user2_profilePic.setVisibility(View.VISIBLE);
                    holder.relative_follow.setVisibility(View.GONE);
                    holder.relative_rate_user.setVisibility(View.GONE);
                    setThumbnailImage(holder.iV_user2_profilePic,thumbnailImageUrl);
                    setNotificationMessage=mActivity.getResources().getString(R.string.liked_your_post);
                    setActivityMessage(holder.tV_activity,membername,setNotificationMessage);
                    openProduct(holder.iV_user2_profilePic,thumbnailImageUrl,postId,username);
                    break;

                // startedFollowing
                case "3":
                    holder.iV_user2_profilePic.setVisibility(View.GONE);
                    holder.relative_rate_user.setVisibility(View.GONE);
                    holder.relative_follow.setVisibility(View.VISIBLE);
                    setNotificationMessage=mActivity.getResources().getString(R.string.startedFollowingYou);
                    setActivityMessage(holder.tV_activity,membername,setNotificationMessage);
                    break;

                // commented
                case "5":
                    holder.iV_user2_profilePic.setVisibility(View.VISIBLE);
                    holder.relative_follow.setVisibility(View.GONE);
                    holder.relative_rate_user.setVisibility(View.GONE);
                    setThumbnailImage(holder.iV_user2_profilePic,thumbnailImageUrl);
                    setNotificationMessage=mActivity.getResources().getString(R.string.commented_your_post);
                    setActivityMessage(holder.tV_activity,membername,setNotificationMessage);
                    openProduct(holder.iV_user2_profilePic,thumbnailImageUrl,postId,username);
                    break;

                // offer
                case "6" :
                    holder.iV_user2_profilePic.setVisibility(View.VISIBLE);
                    holder.relative_follow.setVisibility(View.GONE);
                    holder.relative_rate_user.setVisibility(View.GONE);
                    setThumbnailImage(holder.iV_user2_profilePic,thumbnailImageUrl);
                    setNotificationMessage=mActivity.getResources().getString(R.string.send_a_offer_on_your_product);
                    setActivityMessage(holder.tV_activity,membername,setNotificationMessage);
                    openProduct(holder.iV_user2_profilePic,thumbnailImageUrl,postId,username);
                    break;

                // for rating
                case "8" :
                    holder.relative_follow.setVisibility(View.GONE);
                    holder.iV_user2_profilePic.setVisibility(View.GONE);
                    holder.relative_rate_user.setVisibility(View.VISIBLE);
                    setRateUserMessage(holder.tV_activity,productName,postId,membername,username);
                    holder.relative_rate_user.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mActivity, RateUserActivity.class);
                            intent.putExtra("userName",membername);
                            intent.putExtra("userImage",memberProfilePicUrl);
                            intent.putExtra("postId",postId);
                            intent.putExtra("isFromNotification",true);
                            mActivity.startActivity(intent);
                        }
                    });
                    break;
            }
        }

        // set follow or following status
        if (followRequestStatus!=null && !followRequestStatus.isEmpty())
        {
            holder.relative_follow.setBackgroundResource(R.drawable.rect_purple_color_with_solid_shape);
            holder.tV_follow.setTextColor(ContextCompat.getColor(mActivity,R.color.white));
            holder.tV_follow.setText(mActivity.getResources().getString(R.string.Following));
        }
        else
        {
            holder.relative_follow.setBackgroundResource(R.drawable.rect_purple_color_with_stroke_shape);
            holder.tV_follow.setTextColor(ContextCompat.getColor(mActivity,R.color.purple_color));
            holder.tV_follow.setText(mActivity.getResources().getString(R.string.follow));
        }

        final ArrayList<UserPostDataPojo> arrayListPostData = al_selfActivity_data.get(position).getPostData();

        // Follow
        holder.relative_follow.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String url;
                assert notificationType != null;
                if (notificationType.equals("3"))
                {
                    String followStatus=al_selfActivity_data.get(holder.getAdapterPosition()).getFollowRequestStatus();
                    if (followStatus==null || followStatus.isEmpty())
                    {
                        al_selfActivity_data.get(holder.getAdapterPosition()).setFollowRequestStatus("1");
                        holder.relative_follow.setBackgroundResource(R.drawable.rect_purple_color_with_solid_shape);
                        holder.tV_follow.setTextColor(ContextCompat.getColor(mActivity,R.color.white));
                        holder.tV_follow.setText(mActivity.getResources().getString(R.string.Following));
                        url=ApiUrl.FOLLOW+membername;

                        if (arrayListPostData!=null && arrayListPostData.size()>0)
                            mEventBusDatasHandler.setSocialDatasFromDiscovery(username,memberProfilePicUrl,arrayListPostData.get(0),true);
                        mApiCall.followUserApi(url);
                    }
                    else
                    {
                        unfollowUserAlert(membername,memberProfilePicUrl,holder.relative_follow,holder.tV_follow,holder.getAdapterPosition());
                    }
                }
            }
        });

        if (createdOn!=null && !createdOn.isEmpty())
        {
            createdOn= CommonClass.getTimeDifference(createdOn);
            holder.tV_time.setText(createdOn);
        }
    }

    /**
     * <h>OpenProduct</h>
     * <p>
     *     In this method we used to open a product details.
     * </p>
     * @param iV_user2_profilePic The user 2 pic
     * @param productPic The item image
     * @param postId The post id of the product
     */
    private void openProduct(ImageView iV_user2_profilePic, final String productPic, final String postId,final String username)
    {
        iV_user2_profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mActivity, ProductDetailsActivity.class);
                intent.putExtra("image",productPic);
                intent.putExtra("postId",postId);
                intent.putExtra("postedByUserName",username);
                mActivity.startActivity(intent);
            }
        });
    }

    /**
     * <h>SetThumbnailImage</h>
     * <p>
     *     In this method we used to set the right side rectangular image.
     * </p>
     * @param iV_user2_profilePic The right side image view.
     * @param thumbnailImageUrl The image url of the image
     */
    private void setThumbnailImage(ImageView iV_user2_profilePic,String thumbnailImageUrl)
    {
        if (thumbnailImageUrl!=null && !thumbnailImageUrl.isEmpty())
            Picasso.with(mActivity)
                    .load(thumbnailImageUrl)
                    .placeholder(R.drawable.default_image)
                    .error(R.drawable.default_image)
                    .into(iV_user2_profilePic);
        else iV_user2_profilePic.setImageResource(0);
    }

    /**
     * <h>SetActivityMessage</h>
     * <p>
     *      In this method we use SpannableString to make clickable part-wise on TextView.
     * </p>
     * @param tV_activity The TextView on which operation occured
     * @param membername The first user name
     * @param notificationMessage The message like started following, Liked Post etc
     */
    private void setActivityMessage(TextView tV_activity,String membername,String notificationMessage)
    {
        String setNotificationMessage=membername+" "+notificationMessage;
        SpannableString spannableString=new SpannableString(setNotificationMessage);
        spannableString.setSpan(new MyClickableSpan(membername),0,membername.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tV_activity.setText(spannableString);
        tV_activity.setMovementMethod(LinkMovementMethod.getInstance());
        tV_activity.setHighlightColor(Color.TRANSPARENT);
    }

    /**
     * <h>SetRateUserMessage</h>
     * <p>
     *     In this method we used to set notification message for notification type 8 i.e(When any user rate your product)
     * </p>
     * @param tV_activity The TextView where we set complete notification message
     * @param productName The product name of the Item.
     * @param postId The post Id the product.
     * @param membername The username
     */
    private void  setRateUserMessage(TextView tV_activity,String productName,String postId,String membername,String userName)
    {
        String s1=mActivity.getResources().getString(R.string.you_just_bought);
        String s3=mActivity.getResources().getString(R.string.from);
        String s5=mActivity.getResources().getString(R.string.pls_rate_your_exp_with);

        String message=s1+" "+ productName +" "+s3+" "+ membername +" "+s5;
        SpannableString spannableString=new SpannableString(message);
        spannableString.setSpan(new RateUserClickableSpan(productName,postId,"",userName),s1.length()+1,s1.length()+ productName.length()+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new RateUserClickableSpan("","",membername,userName),s1.length()+1+ productName.length()+1+s3.length()+1,s1.length()+1+ productName.length()+1+s3.length()+1+ membername.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tV_activity.setText(spannableString);
        tV_activity.setMovementMethod(LinkMovementMethod.getInstance());
        tV_activity.setHighlightColor(Color.TRANSPARENT);
    }

    /**
     * <h>MyClickableSpan</h>
     * <p>
     *     This class extends ClickableSpan from that we have two overrided method
     *     1> onClick() in which we do clickable oparation
     *     2> updateDrawState() in this method we do operation like change TextView Style
     * </p>
     */
    private class MyClickableSpan extends ClickableSpan
    {
        private String membername;

        MyClickableSpan(String membername) {
            this.membername = membername;
        }

        @Override
        public void onClick(View widget) {
            openUserProfileScreen(membername);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setFakeBoldText(true);
            ds.setUnderlineText(false);
            ds.setFakeBoldText(true);
        }
    }

    /**
     * <h>RateUserClickableSpan</h>
     * <p>
     *     This class extends ClickableSpan from that we have two overrided method
     *     1> onClick() in which we do clickable oparation
     *     2> updateDrawState() in this method we do operation like change TextView Style
     * </p>
     */
    private class RateUserClickableSpan extends ClickableSpan
    {
        private String productName="",postId="",membername="",username="";

        RateUserClickableSpan(String productName, String postId, String membername,String username) {
            this.productName = productName;
            this.postId = postId;
            this.membername = membername;
            this.username = username;
        }

        @Override
        public void onClick(View widget) {
            Intent intent;
            if (!productName.isEmpty())
            {
                intent=new Intent(mActivity, ProductDetailsActivity.class);
                intent.putExtra("postId",postId);
                intent.putExtra("productName",productName);
                intent.putExtra("postedByUserName",username);
                mActivity.startActivity(intent);
            }
            else
            {
                openUserProfileScreen(membername);
            }
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setFakeBoldText(true);
            ds.setUnderlineText(false);
            ds.setFakeBoldText(true);
        }
    }

    private void openUserProfileScreen(String membername)
    {
        Intent intent;
        if (mSessionManager.getIsUserLoggedIn() && mSessionManager.getUserName().equals(membername))
        {
            intent = new Intent(mActivity, SelfProfileActivity.class);
            intent.putExtra("membername",membername);
        }
        else
        {
            intent = new Intent(mActivity, UserProfileActivity.class);
            intent.putExtra("membername",membername);
        }
        mActivity.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return al_selfActivity_data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView iV_user1_profilePic,iV_user2_profilePic;
        private TextView tV_activity,tV_time,tV_follow;
        private RelativeLayout relative_follow,relative_rate_user;

        MyViewHolder(View itemView) {
            super(itemView);
            iV_user1_profilePic= (ImageView) itemView.findViewById(R.id.iV_user1_profilePic);
            iV_user2_profilePic= (ImageView) itemView.findViewById(R.id.iV_user2_profilePic);
            iV_user2_profilePic.getLayoutParams().height=CommonClass.getDeviceWidth(mActivity)/6;
            iV_user2_profilePic.getLayoutParams().width=CommonClass.getDeviceWidth(mActivity)/6;
            tV_activity= (TextView) itemView.findViewById(R.id.tV_activity);
            tV_time= (TextView) itemView.findViewById(R.id.tV_time);
            relative_follow= (RelativeLayout) itemView.findViewById(R.id.relative_follow);
            relative_rate_user= (RelativeLayout) itemView.findViewById(R.id.relative_rate_user);
            tV_follow= (TextView) itemView.findViewById(R.id.tV_follow);
        }
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
                String url= ApiUrl.UNFOLLOW+membername;
                mApiCall.followUserApi(url);
                rL_follow.setBackgroundResource(R.drawable.rect_purple_color_with_stroke_shape);
                tV_follow.setTextColor(ContextCompat.getColor(mActivity,R.color.purple_color));
                tV_follow.setText(mActivity.getResources().getString(R.string.follow));
                al_selfActivity_data.get(position).setFollowRequestStatus("0");

                if (al_selfActivity_data.get(position).getPostData()!=null && al_selfActivity_data.get(position).getPostData().size()>0)
                    mEventBusDatasHandler.setSocialDatasFromDiscovery(membername,memberProfilePicUrl,al_selfActivity_data.get(position).getPostData().get(0),false);

                unfollowUserDialog.dismiss();
            }
        });
        unfollowUserDialog.show();
    }
}
