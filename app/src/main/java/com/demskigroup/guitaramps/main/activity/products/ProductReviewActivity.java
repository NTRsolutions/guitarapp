package com.demskigroup.guitaramps.main.activity.products;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.adapter.ProductReviewRvAdap;
import com.demskigroup.guitaramps.fcm_push_notification.Config;
import com.demskigroup.guitaramps.fcm_push_notification.NotificationMessageDialog;
import com.demskigroup.guitaramps.fcm_push_notification.NotificationUtils;
import com.demskigroup.guitaramps.pojo_class.add_review_pojo.AddReviewCommentData;
import com.demskigroup.guitaramps.pojo_class.add_review_pojo.AddReviewData;
import com.demskigroup.guitaramps.pojo_class.add_review_pojo.AddReviewMainPojo;
import com.demskigroup.guitaramps.pojo_class.product_review.ProductReviewMainPojo;
import com.demskigroup.guitaramps.pojo_class.product_review.ProductReviewResult;
import com.demskigroup.guitaramps.utility.ApiCall;
import com.demskigroup.guitaramps.utility.ApiUrl;
import com.demskigroup.guitaramps.utility.CommonClass;
import com.demskigroup.guitaramps.utility.OkHttp3Connection;
import com.demskigroup.guitaramps.utility.SessionManager;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * <h>ProductReviewActivity</h>
 * <p>
 *     This class is called from Product details class. In this class we used to retrive the
 *     all reviews of the given product from server. we show that in a list. Apart from that
 *     we have option to add our new comment and delete my review by sliding the row from right
 *     to left.
 * </p>
 * @since 08-Jun-17
 * @version 1.0
 * @author 3Embed
 */
public class ProductReviewActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = ProductReviewActivity.class.getSimpleName();
    private Activity mActivity;
    private SessionManager mSessionManager;
    private String postId;
    private RelativeLayout rL_rootview,rL_noReviews;
    private ProgressBar progress_bar,progress_bar_send;
    private ArrayList<ProductReviewResult> arrayListReview;
    private ProductReviewRvAdap reviewRvAdap;
    private EditText eT_add_review;
    private RecyclerView rV_reviews;
    private Paint p;
    private ApiCall mApiCall;
    private int pageIndex;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayoutManager linearLayoutManager;
    private TextView tV_send;

    // load more variables
    private int totalItemCount,totalVisibleItem,visibleThreshold=5;
    private boolean isLoading;
    private NotificationMessageDialog mNotificationMessageDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_review);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);

        initVariables();
    }

    /**
     * <h>InitVariables</h>
     * <p>
     *     In this method we used to set the xml variable and data member.
     * </p>
     */
    private void initVariables()
    {
        // receiving datas from last activity
        Intent intent=getIntent();
        postId=intent.getStringExtra("postId");

        Activity activity=ProductReviewActivity.this;
        mNotificationMessageDialog=new NotificationMessageDialog(activity);

        pageIndex=0;
        mActivity=ProductReviewActivity.this;
        mApiCall=new ApiCall(mActivity);
        mSessionManager=new SessionManager(mActivity);
        p = new Paint();

        // initialize xml variables
        mSwipeRefreshLayout= (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.pink_color);
        rL_rootview= (RelativeLayout) findViewById(R.id.rL_rootview);
        progress_bar= (ProgressBar) findViewById(R.id.progress_bar);
        progress_bar_send= (ProgressBar) findViewById(R.id.progress_bar_send);
        tV_send= (TextView) findViewById(R.id.tV_send);
        rL_noReviews= (RelativeLayout) findViewById(R.id.rL_noReviews);
        eT_add_review= (EditText) findViewById(R.id.eT_add_review);
        final RelativeLayout rL_send_message= (RelativeLayout) findViewById(R.id.rL_send_message);
        rL_send_message.setOnClickListener(this);

        // change send button background color to purple when text is entered
        eT_add_review.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(eT_add_review.getText()))
                    rL_send_message.setBackgroundColor(ContextCompat.getColor(mActivity,R.color.sub_heading_color));
                else rL_send_message.setBackgroundColor(ContextCompat.getColor(mActivity,R.color.purple_color));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        RelativeLayout rL_back_btn= (RelativeLayout) findViewById(R.id.rL_back_btn);
        rL_back_btn.setOnClickListener(this);

        // reviews result
        arrayListReview=new ArrayList<>();
        rV_reviews = (RecyclerView) findViewById(R.id.rV_reviews);
        linearLayoutManager=new LinearLayoutManager(mActivity);
        rV_reviews.setLayoutManager(linearLayoutManager);
        reviewRvAdap=new ProductReviewRvAdap(mActivity,arrayListReview);
        rV_reviews.setAdapter(reviewRvAdap);

        // pull to refresh
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageIndex=0;
                arrayListReview.clear();
                getPostCommentsApi(pageIndex);
            }
        });

        // initialize swipe method for recycler view
        initItemSwipe();

        // call post comment method to get all comments
        if (CommonClass.isNetworkAvailable(mActivity))
        {
            progress_bar.setVisibility(View.VISIBLE);
            getPostCommentsApi(pageIndex);
        }
        else CommonClass.showSnackbarMessage(rL_rootview,getResources().getString(R.string.NoInternetAccess));
    }


    @Override
    protected void onResume()
    {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mNotificationMessageDialog.mRegistrationBroadcastReceiver, new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mNotificationMessageDialog.mRegistrationBroadcastReceiver, new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mNotificationMessageDialog.mRegistrationBroadcastReceiver);
        super.onPause();
    }

    /**
     * <h>initItemSwipe</h>
     * <p>
     *     In this method we used to do swiping of particular item of recyclerview
     *     from right to left direction for the purpose of deleting that rows.
     * </p>
     */
    private void initItemSwipe()
    {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT)
        {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                String itemUserId=arrayListReview.get(position).getUserId();
                String userId=mSessionManager.getUserId();

                if (direction == ItemTouchHelper.LEFT && userId.equals(itemUserId)){
                    //adapter.removeItem(position);
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity); //alert for confirm to delete
                    builder.setCancelable(false);
                    builder.setMessage("Are you sure to delete?");    //set message

                    builder.setPositiveButton("REMOVE", new DialogInterface.OnClickListener() { //when click on DELETE
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mApiCall.deletePostComment(arrayListReview.get(position).getCommentId(),postId);
                            reviewRvAdap.removeItem(position,rL_noReviews);
                        }
                    }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {  //not removing items if cancel is done
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            reviewRvAdap.notifyItemRemoved(position + 1);    //notifies the RecyclerView Adapter that data in adapter has been removed at a particular position.
                            reviewRvAdap.notifyItemRangeChanged(position, reviewRvAdap.getItemCount());   //notifies the RecyclerView Adapter that positions of element in adapter has been changed from position(removed element index to end of list), please update it.
                            //return;
                        }
                    }).show();  //show alert dialog
                }
                else
                {
                    reviewRvAdap.notifyItemRemoved(position + 1);    //notifies the RecyclerView Adapter that data in adapter has been removed at a particular position.
                    reviewRvAdap.notifyItemRangeChanged(position, reviewRvAdap.getItemCount());   //notifies the RecyclerView Adapter that positions of element in adapter has been changed from position(removed element index to end of list), please update it.
                }
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder)
            {
                final int position = viewHolder.getAdapterPosition();
                String itemUserId=arrayListReview.get(position).getUserId();
                String userId=mSessionManager.getUserId();

                if (!userId.equals(itemUserId)) return 0;
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if(dX > 0){
                        p.setColor(ContextCompat.getColor(mActivity,R.color.red_color));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.white_color_cross_icon);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    }
                    else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.white_color_cross_icon);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rV_reviews);
    }

    /**
     * <h>GetPostCommentsApi</h>
     * <p>
     *     In this method we used to do getPostComments api to get all comments of the product.
     * </p>
     */
    private void getPostCommentsApi(int offset)
    {
        if (CommonClass.isNetworkAvailable(mActivity))
        {
            int limit=20;
            offset=offset*limit;

            JSONObject requestDatas=new JSONObject();
            try {
                requestDatas.put("token",mSessionManager.getAuthToken());
                requestDatas.put("postId",postId);
                requestDatas.put("limit",limit);
                requestDatas.put("offset",offset);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.GET_POST_COMMENTS, OkHttp3Connection.Request_type.POST, requestDatas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag)
                {
                    mSwipeRefreshLayout.setRefreshing(false);
                    progress_bar.setVisibility(View.GONE);
                    System.out.println(TAG+" "+"get post comment res="+result);

                    ProductReviewMainPojo productReviewMainPojo;
                    Gson gson=new Gson();
                    productReviewMainPojo=gson.fromJson(result,ProductReviewMainPojo.class);

                    switch (productReviewMainPojo.getCode())
                    {
                        // success
                        case "200" :
                            mSwipeRefreshLayout.setRefreshing(false);
                            arrayListReview.addAll(productReviewMainPojo.getResult());
                            Collections.reverse(arrayListReview);
                            if (arrayListReview!=null && arrayListReview.size()>0)
                            {
                                rL_noReviews.setVisibility(View.GONE);
                                if (arrayListReview.size()<15)
                                {
                                    isLoading=true;
                                }

                                reviewRvAdap.notifyDataSetChanged();

                                // Load more
                                rV_reviews.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                    @Override
                                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                        super.onScrolled(recyclerView, dx, dy);
                                        totalItemCount=linearLayoutManager.getItemCount();
                                        totalVisibleItem=linearLayoutManager.findLastVisibleItemPosition();
                                        if (!isLoading && totalItemCount<=(visibleThreshold+totalVisibleItem))
                                        {
                                            System.out.println(TAG+" "+"on load more called");
                                            mSwipeRefreshLayout.setRefreshing(true);
                                            isLoading=true;
                                            pageIndex=pageIndex+1;
                                            getPostCommentsApi(pageIndex);
                                        }

                                    }
                                });
                            }
                            else rL_noReviews.setVisibility(View.VISIBLE);
                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // error
                        default:
                            progress_bar.setVisibility(View.GONE);
                            if (arrayListReview.size()==0)
                                rL_noReviews.setVisibility(View.VISIBLE);
                            System.out.println(TAG+" "+"get post comment error="+productReviewMainPojo.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    mSwipeRefreshLayout.setRefreshing(false);
                    progress_bar.setVisibility(View.GONE);
                }
            });
        }
        else CommonClass.showSnackbarMessage(rL_rootview,getResources().getString(R.string.NoInternetAccess));
    }


    // to get current time in long value
    private String getCurrentDateTime()
    {
        Date date = new Date();
        return date.getTime()+""; //2014/08/06 15:59:48
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            // back button
            case R.id.rL_back_btn :
                onBackPressed();
                break;

            // send
            case R.id.rL_send_message :

                if(eT_add_review.getText().toString().trim().length() > 0) {
                    addReviewComment( eT_add_review.getText().toString(), postId );
                }
                break;
        }
    }

    /**
     * <h>AddReviewComment</h>
     * <p>
     *     In this method we used to do api call add review for particular product.
     * </p>
     * @param message The message body to add as review
     * @param postId The post id of a product
     */
    public void addReviewComment(String message,String postId)
    {
        if (CommonClass.isNetworkAvailable(mActivity))
        {
            progress_bar_send.setVisibility(View.VISIBLE);
            tV_send.setVisibility(View.GONE);
            JSONObject request_datas=new JSONObject();
            try {
                request_datas.put("token",mSessionManager.getAuthToken());
                request_datas.put("comment",message);
                request_datas.put("postId",postId);
                request_datas.put("type","0");
                request_datas.put("hashTags","");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.ADD_COMMENTS, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    System.out.println(TAG+" "+"add review res="+result);
                    progress_bar_send.setVisibility(View.GONE);
                    tV_send.setVisibility(View.VISIBLE);

                    AddReviewMainPojo addReviewMainPojo;
                    Gson gson=new Gson();
                    addReviewMainPojo=gson.fromJson(result,AddReviewMainPojo.class);

                    switch (addReviewMainPojo.getCode())
                    {
                        // success
                        case "200" :
                            ArrayList<AddReviewData> arrayListReviewData=addReviewMainPojo.getData();
                            if (arrayListReviewData!=null && arrayListReviewData.size()>0)
                            {
                                ArrayList<AddReviewCommentData> arrayListCommentData=arrayListReviewData.get(0).getCommentData();
                                if (arrayListCommentData!=null && arrayListCommentData.size()>0)
                                {
                                    rL_noReviews.setVisibility(View.GONE);
                                    String commentId=arrayListCommentData.get(0).getCommentId();
                                    System.out.println(TAG+" "+"commentId="+commentId);

                                    ProductReviewResult productReviewResult=new ProductReviewResult();
                                    productReviewResult.setCommentBody(arrayListCommentData.get(0).getCommentBody());
                                    productReviewResult.setUsername(mSessionManager.getUserName());
                                    productReviewResult.setUserId(mSessionManager.getUserId());
                                    productReviewResult.setCommentId(commentId);
                                    productReviewResult.setProfilePicUrl(mSessionManager.getUserImage());
                                    productReviewResult.setCommentedOn(getCurrentDateTime());
                                    eT_add_review.setText("");
                                    arrayListReview.add(productReviewResult);
                                    rV_reviews.smoothScrollToPosition(arrayListReview.size());
                                    reviewRvAdap.notifyDataSetChanged();
                                }
                            }
                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    progress_bar_send.setVisibility(View.GONE);
                    tV_send.setVisibility(View.VISIBLE);
                    System.out.println(TAG+" "+"add review error="+error);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

/*    @Override
    public boolean dispatchTouchEvent(final MotionEvent ev) {
        // all touch events close the keyboard before they are processed except EditText instances.
        // if focus is an EditText we need to check, if the touchevent was inside the focus editTexts
        final View currentFocus = getCurrentFocus();
        if (!(currentFocus instanceof EditText) || !isTouchInsideView(ev, currentFocus)) {
            ((InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        return super.dispatchTouchEvent(ev);
    }

    *//**
     * determine if the given motionevent is inside the given view.
     *
     * @param ev
     *            the given view
     * @param currentFocus
     *            the motion event.
     * @return if the given motionevent is inside the given view
     *//*
    private boolean isTouchInsideView(final MotionEvent ev, final View currentFocus) {
        final int[] loc = new int[2];
        currentFocus.getLocationOnScreen(loc);
        return ev.getRawX() > loc[0] && ev.getRawY() > loc[1] && ev.getRawX() < (loc[0] + currentFocus.getWidth())
                && ev.getRawY() < (loc[1] + currentFocus.getHeight());
    }*/
}
