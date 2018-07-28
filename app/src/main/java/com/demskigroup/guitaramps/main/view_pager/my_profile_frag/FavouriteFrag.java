package com.demskigroup.guitaramps.main.view_pager.my_profile_frag;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.squareup.otto.Subscribe;
import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.adapter.LikedPostsRvAdap;
import com.demskigroup.guitaramps.event_bus.BusProvider;
import com.demskigroup.guitaramps.main.activity.HomePageActivity;
import com.demskigroup.guitaramps.main.activity.products.ProductDetailsActivity;
import com.demskigroup.guitaramps.pojo_class.home_explore_pojo.ExploreResponseDatas;
import com.demskigroup.guitaramps.pojo_class.likedPosts.LikedPostPojoMain;
import com.demskigroup.guitaramps.pojo_class.likedPosts.LikedPostResponseDatas;
import com.demskigroup.guitaramps.utility.ApiUrl;
import com.demskigroup.guitaramps.utility.CommonClass;
import com.demskigroup.guitaramps.utility.OkHttp3Connection;
import com.demskigroup.guitaramps.utility.ProductItemClickListener;
import com.demskigroup.guitaramps.utility.SessionManager;
import com.demskigroup.guitaramps.utility.SpacesItemDecoration;
import com.demskigroup.guitaramps.utility.VariableConstants;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * <h>FavouriteFrag</h>
 * <p>
 *     In this class we used to show Liked item listing in staggered grid view.
 * </p>
 * @since 4/7/2017
 */
public class FavouriteFrag extends Fragment
{
    private Activity mActivity;
    private SessionManager mSessionManager;
    private static final String TAG=FavouriteFrag.class.getSimpleName();
    private ProgressBar progress_bar_profile;
    private RelativeLayout rL_noProductFound;
    private String memberName;
    private ArrayList<LikedPostResponseDatas> arrayListLikedPosts;
    private LikedPostsRvAdap likedPostsRvAdap;
    private StaggeredGridLayoutManager gridLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView rV_myprofile_fav;
    private int pageIndex,clickedItemPosition ;
    private boolean isToCallFirstTime;

    // Load more var
    private boolean isLoadingRequired;
    private int visibleItemCount,totalItemCount,visibleThreshold=5;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity=getActivity();
        mSessionManager=new SessionManager(mActivity);
        memberName=getArguments().getString("memberName");
        arrayListLikedPosts=new ArrayList<>();
        isToCallFirstTime=true;
    }

    public static FavouriteFrag newInstance(String memberName)
    {
        Bundle bundle=new Bundle();
        bundle.putString("memberName",memberName);

        FavouriteFrag favouriteFrag=new FavouriteFrag();
        favouriteFrag.setArguments(bundle);
        return favouriteFrag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        System.out.println(TAG+" "+"fragment fav called..."+isToCallFirstTime);
        isLoadingRequired=false;
        pageIndex=0;
        View view=inflater.inflate(R.layout.frag_profile_buying,container,false);
        progress_bar_profile= (ProgressBar) view.findViewById(R.id.progress_bar_profile);
        rV_myprofile_fav = (RecyclerView) view.findViewById(R.id.rV_myprofile_selling);
        mSwipeRefreshLayout= (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.pink_color);
        rL_noProductFound= (RelativeLayout) view.findViewById(R.id.rL_noProductFound);
        rL_noProductFound.setVisibility(View.GONE);

        // start discovering
        RelativeLayout rL_start_selling = (RelativeLayout) view.findViewById(R.id.rL_start_selling);
        rL_start_selling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=mActivity.getIntent();
                i.putExtra("isToFinishLoginSignup",false);
                i.putExtra("isFromSignup",false);
                startActivity(i);
                mActivity.finish();
            }
        });

        // set empty favourite icon
        ImageView iV_default_icon= (ImageView) view.findViewById(R.id.iV_default_icon);
        iV_default_icon.setImageResource(R.drawable.empty_fav_icon);

        TextView tV_no_ads= (TextView) view.findViewById(R.id.tV_no_ads);
        tV_no_ads.setText(getResources().getString(R.string.found_your_fav_yet));

        TextView tV_snapNpost= (TextView) view.findViewById(R.id.tV_snapNpost);
        tV_snapNpost.setText(getResources().getString(R.string.ads_you_love_and_mark));

        TextView tV_start_discovering= (TextView) view.findViewById(R.id.tV_start_discovering);
        tV_start_discovering.setText(getResources().getString(R.string.start_discovery));

        // set favourite recycler view adapter
        likedPostsRvAdap=new LikedPostsRvAdap(mActivity, arrayListLikedPosts, new ProductItemClickListener() {
            @Override
            public void onItemClick(int pos, ImageView imageView) {
                clickedItemPosition = pos;
                Intent intent = new Intent(mActivity, ProductDetailsActivity.class);
                intent.putExtra("productName", arrayListLikedPosts.get(pos).getProductName());
                if (arrayListLikedPosts.get(pos).getCategoryData()!=null)
                    intent.putExtra("category", arrayListLikedPosts.get(pos).getCategoryData().get(0).getCategory());
                intent.putExtra("likes", arrayListLikedPosts.get(pos).getLikes());
                intent.putExtra("likeStatus", arrayListLikedPosts.get(pos).getLikeStatus());
                intent.putExtra("currency", arrayListLikedPosts.get(pos).getCurrency());
                intent.putExtra("price", arrayListLikedPosts.get(pos).getPrice());
                intent.putExtra("postedOn", arrayListLikedPosts.get(pos).getPostedOn());
                intent.putExtra("image",arrayListLikedPosts.get(pos).getMainUrl());
                intent.putExtra("thumbnailImageUrl",arrayListLikedPosts.get(pos).getThumbnailImageUrl());
                intent.putExtra("likedByUsersArr",arrayListLikedPosts.get(pos).getLikedByUsers());
                intent.putExtra("description",arrayListLikedPosts.get(pos).getDescription());
                intent.putExtra("condition",arrayListLikedPosts.get(pos).getCondition());
                intent.putExtra("place",arrayListLikedPosts.get(pos).getPlace());
                intent.putExtra("latitude",arrayListLikedPosts.get(pos).getLatitude());
                intent.putExtra("longitude",arrayListLikedPosts.get(pos).getLongitude());
                intent.putExtra("postedByUserName",arrayListLikedPosts.get(pos).getPostedByUserName());
                intent.putExtra("postId",arrayListLikedPosts.get(pos).getPostId());
                intent.putExtra("postsType",arrayListLikedPosts.get(pos).getPostsType());
                intent.putExtra("followRequestStatus","");
                intent.putExtra("clickCount","");
                intent.putExtra("memberProfilePicUrl","");
                intent.putExtra(VariableConstants.EXTRA_ANIMAL_IMAGE_TRANSITION_NAME, ViewCompat.getTransitionName(imageView));
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, imageView, ViewCompat.getTransitionName(imageView));
                startActivityForResult(intent,VariableConstants.PRODUCT_DETAILS_REQ_CODE, options.toBundle());

            }
        });
        gridLayoutManager=new StaggeredGridLayoutManager(2,1);
        rV_myprofile_fav.setLayoutManager(gridLayoutManager);
        rV_myprofile_fav.setAdapter(likedPostsRvAdap);

        // set space equility between recycler view items
        int spanCount = 2; // 2 columns
        int spacing = 10; // 50px
        rV_myprofile_fav.addItemDecoration(new SpacesItemDecoration(spanCount, spacing));

        if (CommonClass.isNetworkAvailable(mActivity) && isToCallFirstTime)
        {
            progress_bar_profile.setVisibility(View.VISIBLE);
            arrayListLikedPosts.clear();
            likedPostsApi(pageIndex);
        }
        else CommonClass.showSnackbarMessage(((HomePageActivity)mActivity).rL_rootElement,getResources().getString(R.string.NoInternetAccess));

        // pull to refresh
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageIndex=0;
                arrayListLikedPosts.clear();
                likedPostsRvAdap.notifyDataSetChanged();
                likedPostsApi(pageIndex);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {

        try {
            super.onViewCreated(view, savedInstanceState);
            BusProvider.getInstance().register(this);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * <h>LikedPostsApi</h>
     * <p>
     *     In this method we used to do api call to get all the favourite item which is
     *     being liked by the user.
     * </p>
     * @param offset The index
     */
    private void likedPostsApi(int offset) {
        if (CommonClass.isNetworkAvailable(mActivity))
        {
            int limit=20;
            offset=offset*limit;

            JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("token", mSessionManager.getAuthToken());
                request_datas.put("membername",memberName);
                request_datas.put("offset",offset);
                request_datas.put("limit",limit);

                System.out.println(TAG+" "+"offset="+offset);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.LIKED_POST, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag)
                {
                    mSwipeRefreshLayout.setRefreshing(false);
                    progress_bar_profile.setVisibility(View.GONE);
                    System.out.println(TAG+" "+"fav post res="+result);

                    LikedPostPojoMain likedPostPojo;
                    Gson gson=new Gson();
                    likedPostPojo=gson.fromJson(result,LikedPostPojoMain.class);

                    switch (likedPostPojo.getCode())
                    {
                        // success
                        case "200":
                            if (likedPostPojo.getData()!=null && likedPostPojo.getData().size()>0)
                            {
                                isToCallFirstTime=false;
                                rL_noProductFound.setVisibility(View.GONE);
                                arrayListLikedPosts.addAll(likedPostPojo.getData());
                                isLoadingRequired=arrayListLikedPosts.size()>14;

                                likedPostsRvAdap.notifyDataSetChanged();

                                // Load more
                                rV_myprofile_fav.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                    @Override
                                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                        super.onScrolled(recyclerView, dx, dy);

                                        int[] firstVisibleItemPositions = new int[2];
                                        totalItemCount = gridLayoutManager.getItemCount();
                                        visibleItemCount = gridLayoutManager.findLastVisibleItemPositions(firstVisibleItemPositions)[0];

                                        if (isLoadingRequired && totalItemCount<=(visibleItemCount+visibleThreshold))
                                        {
                                            isLoadingRequired=false;
                                            pageIndex=pageIndex+1;
                                            mSwipeRefreshLayout.setRefreshing(true);
                                            likedPostsApi(pageIndex);
                                        }
                                    }
                                });

                                // Product item click
                                /*likedPostsRvAdap.setItemClickListener(new ProductItemClickListener() {
                                    @Override
                                    public void onItemClick(int pos, ImageView imageView)
                                    {
                                        clickedItemPosition = pos;
                                        Intent intent = new Intent(mActivity, ProductDetailsActivity.class);
                                        intent.putExtra("productName", arrayListLikedPosts.get(pos).getProductName());
                                        if (arrayListLikedPosts.get(pos).getCategoryData()!=null)
                                            intent.putExtra("category", arrayListLikedPosts.get(pos).getCategoryData().get(0).getCategory());
                                        intent.putExtra("likes", arrayListLikedPosts.get(pos).getLikes());
                                        intent.putExtra("likeStatus", arrayListLikedPosts.get(pos).getLikeStatus());
                                        intent.putExtra("currency", arrayListLikedPosts.get(pos).getCurrency());
                                        intent.putExtra("price", arrayListLikedPosts.get(pos).getPrice());
                                        intent.putExtra("postedOn", arrayListLikedPosts.get(pos).getPostedOn());
                                        intent.putExtra("image",arrayListLikedPosts.get(pos).getMainUrl());
                                        intent.putExtra("thumbnailImageUrl",arrayListLikedPosts.get(pos).getThumbnailImageUrl());
                                        intent.putExtra("likedByUsersArr",arrayListLikedPosts.get(pos).getLikedByUsers());
                                        intent.putExtra("description",arrayListLikedPosts.get(pos).getDescription());
                                        intent.putExtra("condition",arrayListLikedPosts.get(pos).getCondition());
                                        intent.putExtra("place",arrayListLikedPosts.get(pos).getPlace());
                                        intent.putExtra("latitude",arrayListLikedPosts.get(pos).getLatitude());
                                        intent.putExtra("longitude",arrayListLikedPosts.get(pos).getLongitude());
                                        intent.putExtra("postedByUserName",arrayListLikedPosts.get(pos).getPostedByUserName());
                                        intent.putExtra("postId",arrayListLikedPosts.get(pos).getPostId());
                                        intent.putExtra("postsType",arrayListLikedPosts.get(pos).getPostsType());
                                        intent.putExtra("followRequestStatus","");
                                        intent.putExtra("clickCount","");
                                        intent.putExtra("memberProfilePicUrl","");
                                        intent.putExtra(VariableConstants.EXTRA_ANIMAL_IMAGE_TRANSITION_NAME, ViewCompat.getTransitionName(imageView));
                                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, imageView, ViewCompat.getTransitionName(imageView));
                                        startActivityForResult(intent,VariableConstants.PRODUCT_DETAILS_REQ_CODE, options.toBundle());
                                    }
                                });*/
                            }
                            break;

                        // auth token expired
                        case "401" :
                            mSwipeRefreshLayout.setRefreshing(false);
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // data not found
                        case "204" :
                            mSwipeRefreshLayout.setRefreshing(false);
                            if (arrayListLikedPosts.size()==0)
                                rL_noProductFound.setVisibility(View.VISIBLE);
                            break;

                        // Any error
                        default:
                            mSwipeRefreshLayout.setRefreshing(false);
                            CommonClass.showSnackbarMessage(((HomePageActivity)mActivity).rL_rootElement,likedPostPojo.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    mSwipeRefreshLayout.setRefreshing(false);
                    progress_bar_profile.setVisibility(View.GONE);
                    CommonClass.showSnackbarMessage(((HomePageActivity)mActivity).rL_rootElement,error);
                }
            });
        }
        else CommonClass.showSnackbarMessage(((HomePageActivity)mActivity).rL_rootElement,getResources().getString(R.string.NoInternetAccess));
    }

    @Subscribe
    public void getMessage(LikedPostResponseDatas likedPostResponseDatas)
    {
        if (likedPostResponseDatas!=null)
        {
            if (likedPostResponseDatas.isToAddLikedItem()) {
                if (!isContainsId(likedPostResponseDatas.getPostId()))
                {
                    arrayListLikedPosts.add(0, likedPostResponseDatas);
                    likedPostsRvAdap.notifyDataSetChanged();
                }
            }
            else
            {
                for (int favItemCount=0; favItemCount<arrayListLikedPosts.size(); favItemCount++)
                {
                    if (arrayListLikedPosts.get(favItemCount).getPostId().equals(likedPostResponseDatas.getPostId()))
                    {
                        arrayListLikedPosts.remove(favItemCount);
                        likedPostsRvAdap.notifyDataSetChanged();
                    }
                }
            }
        }

        // show no fav item logo if list is zero or else hide
        if (arrayListLikedPosts.size()>0)
            rL_noProductFound.setVisibility(View.GONE);
        else rL_noProductFound.setVisibility(View.VISIBLE);
    }

    /**
     * <h>IsContainsId</h>
     * <p>
     *     In this method we used to check whether the given post id is
     *     present or not in the current list.
     * </p>
     * @param postId the given post id of product
     * @return the boolean value
     */
    public boolean isContainsId(String postId) {
        boolean flag = false;
        for (LikedPostResponseDatas object : arrayListLikedPosts) {
            System.out.println(TAG+" "+"given post id="+postId+" "+"current post id="+object.getPostId());
            if (postId.equals(object.getPostId())) {
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public void onDestroy() {
        BusProvider.getInstance().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data!=null) {
            switch (requestCode) {
                case VariableConstants.PRODUCT_DETAILS_REQ_CODE:
                    String likeStatus = data.getStringExtra("likeStatus");
                    if (likeStatus!=null && !likeStatus.equals("1"))
                    {
                        /*if (arrayListLikedPosts.size()>clickedItemPosition)
                        {
                            arrayListLikedPosts.remove(clickedItemPosition);
                            likedPostsRvAdap.notifyDataSetChanged();
                        }

                        // show no product as favourite if list size is zero
                        if (arrayListLikedPosts.size()>0)
                            rL_noProductFound.setVisibility(View.GONE);
                        else rL_noProductFound.setVisibility(View.VISIBLE);*/
                    }
                    break;
            }
        }
    }
}
