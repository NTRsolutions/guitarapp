package com.demskigroup.guitaramps.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.main.activity.SelfProfileActivity;
import com.demskigroup.guitaramps.main.activity.UserProfileActivity;
import com.demskigroup.guitaramps.main.activity.products.ProductDetailsActivity;
import com.demskigroup.guitaramps.pojo_class.explore_following_pojo.FollowingResponseDatas;
import com.demskigroup.guitaramps.utility.CircleTransform;
import com.demskigroup.guitaramps.utility.CommonClass;
import com.demskigroup.guitaramps.utility.OnLoadMoreListener;
import com.demskigroup.guitaramps.utility.SessionManager;

import java.util.ArrayList;

/**
 * <h>FollowingFragRvAdap</h>
 * <p>
 *     This class is getting called from FollowingFrag. In this recyclerview adapter class we used to inflate
 *     single_row_following_activity layout and shows the all following activity done by others users.
 * </p>
 * @since 4/17/2017
 */
public class FollowingFragRvAdap extends RecyclerView.Adapter<FollowingFragRvAdap.ViewHolder>
{
    private static final String TAG = FollowingFragRvAdap.class.getSimpleName();
    private Activity mActivity;
    private ArrayList<FollowingResponseDatas> al_following_data;

    // load more parameters
    private OnLoadMoreListener onLoadMoreListener;
    private boolean isLoading;
    private int lastVisibleItem,totalItemCount;
    private int visibleThresold=5;
    private SessionManager mSessionManager;

    /**
     * <h>FollowingFragRvAdap</h>
     * <p>
     *     This is simple constructor to initailize list datas and context.
     * </p>
     * @param mActivity The current context
     * @param al_following_data The response datas
     */
    public FollowingFragRvAdap(Activity mActivity, final ArrayList<FollowingResponseDatas> al_following_data, RecyclerView mRecyclerView) {
        this.mActivity = mActivity;
        this.al_following_data = al_following_data;
        mSessionManager = new SessionManager(mActivity);

        final LinearLayoutManager layoutManager= (LinearLayoutManager) mRecyclerView.getLayoutManager();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount=layoutManager.getItemCount();
                lastVisibleItem=layoutManager.findLastVisibleItemPosition();

                System.out.println(TAG+" "+"is loading="+isLoading+" "+"totalItemCount="+totalItemCount+" "+"lastVisibleItem="+lastVisibleItem);
                isLoading=al_following_data.size()<15;

                if (!isLoading && totalItemCount<=(lastVisibleItem+visibleThresold))
                {
                    if (onLoadMoreListener!=null)
                    {
                        onLoadMoreListener.onLoadMore();
                    }
                    isLoading=true;
                }
            }
        });
    }

    public void setOnLoadMore(OnLoadMoreListener listener)
    {
        this.onLoadMoreListener=listener;
    }

    public void setLoaded()
    {
        isLoading=false;
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_following_activity,parent,false);
        return new ViewHolder(view);
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
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        String time;
        final String user1_name,user1_profilePic,user2_profilePic,notificationType,mainUrl,postId,user2_name;
        user1_name=al_following_data.get(position).getUser1_username();
        user1_profilePic=al_following_data.get(position).getUser1_profilePicUrl();
        user2_name=al_following_data.get(position).getUser2_username();
        user2_profilePic=al_following_data.get(position).getUser2_profilePicUrl();
        mainUrl=al_following_data.get(position).getMainUrl();
        time=al_following_data.get(position).getCreatedOn();
        notificationType=al_following_data.get(position).getNotificationType();
        postId=al_following_data.get(position).getPostId();

        System.out.println(TAG+" "+"user2 profilePic="+user2_profilePic);

        // set user1 profile pic
        if (user1_profilePic!=null && !user1_profilePic.isEmpty())
            Picasso.with(mActivity)
                    .load(user1_profilePic)
                    .transform(new CircleTransform())
                    .placeholder(R.drawable.app_launcher_logo)
                    .error(R.drawable.app_launcher_logo)
                    .into(holder.iV_user1_profilePic);

        holder.iV_user1_profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent=new Intent(mActivity, UserProfileActivity.class);
                intent.putExtra("membername",user1_name);
                mActivity.startActivity(intent);*/

                openUserProfileScreen(user1_name);
            }
        });

        // set activity complete message
        setActivityMessage(holder.tV_activity,user1_name,notificationType,user2_name);

        if (notificationType!=null && !notificationType.isEmpty())
        {
            switch (notificationType)
            {
                // liked post
                case "2" :
                    setRightSideImage(mainUrl,holder.iV_user2_profilePic);
                    break;

                // started following
                case "3" :
                    setRightSideImage(user2_profilePic,holder.iV_user2_profilePic);
                    break;

                // commented
                case "5" :
                    setRightSideImage(mainUrl,holder.iV_user2_profilePic);
                    break;
            }
        }

        // set on post click
        holder.iV_user2_profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                assert notificationType != null;
                switch (notificationType)
                {
                    // open product details
                    case "2" :
                        intent=new Intent(mActivity, ProductDetailsActivity.class);
                        intent.putExtra("image",mainUrl);
                        intent.putExtra("postId",postId);
                        mActivity.startActivity(intent);
                        break;

                    // open profile
                    case "3" :
                        /*intent=new Intent(mActivity, UserProfileActivity.class);
                        intent.putExtra("membername",user2_name);
                        mActivity.startActivity(intent);*/

                        openUserProfileScreen(user2_name);
                        break;

                    case "5" :
                        intent=new Intent(mActivity, ProductDetailsActivity.class);
                        intent.putExtra("image",mainUrl);
                        intent.putExtra("postId",postId);
                        mActivity.startActivity(intent);
                        break;
                }
            }
        });

        // set time
        if (time!=null)
            time= CommonClass.getTimeDifference(time);
        holder.tV_time.setText(time);
    }

    private void setRightSideImage(String imageUrl,ImageView imageView)
    {
        // set user2 profile pic
        if (imageUrl!=null && !imageUrl.isEmpty()) {
            Picasso.with(mActivity)
                    .load(imageUrl)
                    .placeholder(R.drawable.app_launcher_logo)
                    .error(R.drawable.app_launcher_logo)
                    .into(imageView);
        }
    }

    /**
     * <h>SetActivityMessage</h>
     * <p>
     *     In this method we use SpannableString to make clickable part-wise on TextView.
     * </p>
     * @param tV_activity The TextView on which operation occured
     * @param user1_username The first user name
     * @param user2_username The second user name
     */
    private void setActivityMessage(TextView tV_activity,String user1_username,String notificationType,String user2_username)
    {
        // remove extra spaces
        if (user1_username!=null && !user1_username.isEmpty())
            user1_username=user1_username.trim();
        if (user2_username!=null && !user2_username.isEmpty())
            user2_username=user2_username.trim();

        int user1Length=0;
        if (user1_username!=null)
            user1Length=user1_username.length();

        String customMessage="";
        if (notificationType!=null && !notificationType.isEmpty())
        {
            switch (notificationType)
            {
                // liked post
                case "2" :
                    if(user1_username.equals(user2_username)){
                        customMessage = user1_username + " " + mActivity.getResources().getString(R.string.liked) + " " + mActivity.getString(R.string.own)+ " " + mActivity.getResources().getString(R.string.post);
                    }else {
                        customMessage = user1_username + " " + mActivity.getResources().getString(R.string.liked) + " " + user2_username + " " + mActivity.getResources().getString(R.string.post);
                    }
                    break;

                // started following
                case "3" :
                    customMessage=user1_username+" "+mActivity.getResources().getString(R.string.startes_following)+" "+user2_username;
                    break;

                // commented
                case "5" :
                    customMessage=user1_username+" "+mActivity.getResources().getString(R.string.left_a_review_on)+" "+user2_username+" "+mActivity.getResources().getString(R.string.post);
                    break;
            }
        }

        SpannableString ss = new SpannableString(customMessage);
        ss.setSpan(new MyClickableSpan(user1_username), 0, user1Length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //ss.setSpan(new MyClickableSpan(user2_username),user1Length+noitificationMsgLen+2,user1Length+noitificationMsgLen+user2Length+2,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tV_activity.setText(ss);
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
            /*Intent intent=new Intent(mActivity, UserProfileActivity.class);
            intent.putExtra("membername",membername);
            mActivity.startActivity(intent);*/

            openUserProfileScreen(membername);
        }

        @Override
        public void updateDrawState(TextPaint ds)
        {
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


    /**
     * Return the size of your dataset
     * @return the total number of rows
     */
    @Override
    public int getItemCount() {
        return al_following_data.size();
    }

    /**
     * In this class we declare and initialize xml all variables.
     */
    class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView iV_user1_profilePic,iV_user2_profilePic;
        TextView tV_activity,tV_time;

        ViewHolder(View itemView)
        {
            super(itemView);
            iV_user1_profilePic= (ImageView) itemView.findViewById(R.id.iV_user1_profilePic);
            iV_user2_profilePic= (ImageView) itemView.findViewById(R.id.iV_user2_profilePic);
            iV_user2_profilePic.getLayoutParams().height=CommonClass.getDeviceWidth(mActivity)/6;
            iV_user2_profilePic.getLayoutParams().width=CommonClass.getDeviceWidth(mActivity)/6;
            tV_activity= (TextView) itemView.findViewById(R.id.tV_activity);
            tV_time= (TextView) itemView.findViewById(R.id.tV_time);
        }
    }
}
