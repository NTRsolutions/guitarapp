package com.demskigroup.guitaramps.main.view_pager.my_profile_frag;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.squareup.otto.Subscribe;
import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.adapter.ProfileSoldRvAdapter;
import com.demskigroup.guitaramps.event_bus.BusProvider;
import com.demskigroup.guitaramps.main.activity.HomePageActivity;
import com.demskigroup.guitaramps.main.activity.products.ProductDetailsActivity;
import com.demskigroup.guitaramps.pojo_class.home_explore_pojo.ExploreResponseDatas;
import com.demskigroup.guitaramps.pojo_class.profile_selling_pojo.ProfileSellingData;
import com.demskigroup.guitaramps.pojo_class.profile_sold_pojo.ProfileSoldDatas;
import com.demskigroup.guitaramps.pojo_class.profile_sold_pojo.ProfileSoldMainPojo;
import com.demskigroup.guitaramps.pojo_class.social_frag_pojo.SocialDatas;
import com.demskigroup.guitaramps.utility.ApiCall;
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
 * <h>SoldFrag</h>
 * <p>
 *     In this class we used to show to products which has been sold out.
 * </p>
 * @since 4/7/2017
 */
public class SoldFrag extends Fragment implements ProductItemClickListener
{
    private Activity mActivity;
    private static final String TAG=SoldFrag.class.getSimpleName();
    private SessionManager mSessionManager;
    private ProgressBar progress_bar_profile;
    private RelativeLayout rL_rootview,rL_noProductFound;
    private ArrayList<ProfileSoldDatas> arrayListSoldDatas;
    private ProfileSoldRvAdapter soldRvAdapter;
    private RecyclerView rV_selling;
    private StaggeredGridLayoutManager gridLayoutManager;
    private String memberName;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int pageIndex;
    private ApiCall apiCall;
    private boolean isFromMyProfile;

    // Load more var
    private boolean isLoadingRequired,isToCallFirstTime;
    private int visibleItemCount,totalItemCount,visibleThreshold=5;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity=getActivity();
        mSessionManager=new SessionManager(mActivity);
        memberName=getArguments().getString("memberName");
        isFromMyProfile=getArguments().getBoolean("isFromMyProfileFlag",false);
        apiCall=new ApiCall(mActivity);
        isToCallFirstTime=true;
        System.out.println(TAG+" "+"fragment sold called...");
    }

    public static SoldFrag newInstance(String memberName,boolean isFromMyProfile)
    {
        Bundle bundle=new Bundle();
        bundle.putString("memberName",memberName);
        bundle.putBoolean("isFromMyProfileFlag",isFromMyProfile);
        SoldFrag soldFrag=new SoldFrag();
        soldFrag.setArguments(bundle);
        return soldFrag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.frag_profile_buying,container,false);
        pageIndex=0;
        rV_selling = (RecyclerView) view.findViewById(R.id.rV_myprofile_selling);
        mSwipeRefreshLayout= (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.pink_color);

        // set space equility between recycler view items
        int spanCount = 2; // 2 columns
        int spacing = 10; // 50px
        rV_selling.addItemDecoration(new SpacesItemDecoration(spanCount, spacing));

        progress_bar_profile= (ProgressBar) view.findViewById(R.id.progress_bar_profile);
        rL_rootview= (RelativeLayout) view.findViewById(R.id.rL_rootview);

        arrayListSoldDatas =new ArrayList<>();
        soldRvAdapter =new ProfileSoldRvAdapter(mActivity, arrayListSoldDatas,this);
        gridLayoutManager=new StaggeredGridLayoutManager(2,1);
        rV_selling.setLayoutManager(gridLayoutManager);
        rV_selling.setAdapter(soldRvAdapter);

        // set empty favourite icon
        rL_noProductFound= (RelativeLayout) view.findViewById(R.id.rL_noProductFound);
        rL_noProductFound.setVisibility(View.GONE);

        ImageView iV_default_icon= (ImageView) view.findViewById(R.id.iV_default_icon);
        iV_default_icon.setImageResource(R.drawable.app_launcher_logo);



        TextView tV_no_ads= (TextView) view.findViewById(R.id.tV_no_ads);
        tV_no_ads.setText(getResources().getString(R.string.no_sold_yet));

        TextView tV_snapNpost= (TextView) view.findViewById(R.id.tV_snapNpost);
        tV_snapNpost.setVisibility(View.GONE);
        TextView tV_start_discovering= (TextView) view.findViewById(R.id.tV_start_discovering);
        tV_start_discovering.setVisibility(View.GONE);
        RelativeLayout rL_start_selling= (RelativeLayout) view.findViewById(R.id.rL_start_selling);
        rL_start_selling.setVisibility(View.GONE);

        // call api call method
        if (CommonClass.isNetworkAvailable(mActivity) && isToCallFirstTime)
        {
            progress_bar_profile.setVisibility(View.VISIBLE);
            profilePosts(pageIndex);
        }
        else  CommonClass.showSnackbarMessage(((HomePageActivity)mActivity).rL_rootElement,getResources().getString(R.string.NoInternetAccess));

        // pull to refresh
        mSwipeRefreshLayout.setColorSchemeResources(R.color.pink_color);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageIndex = 0;
                arrayListSoldDatas.clear();
                soldRvAdapter.notifyDataSetChanged();
                profilePosts(pageIndex);
            }
        });
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        try {
            BusProvider.getInstance().register(this);
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void profilePosts(int offset) {
        if (CommonClass.isNetworkAvailable(mActivity))
        {
            int limit=20;
            offset=limit*offset;

            JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("token", mSessionManager.getAuthToken());
                request_datas.put("limit",limit);
                request_datas.put("offset",offset);
                request_datas.put("sold", "1");
                request_datas.put("membername",memberName);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            String url=ApiUrl.PROFILE_POST+memberName;
            OkHttp3Connection.doOkHttp3Connection(TAG,url, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    progress_bar_profile.setVisibility(View.GONE);
                    System.out.println(TAG + " " + "profile sold res=" + result);
                    mSwipeRefreshLayout.setRefreshing(false);

                    /*ExplorePojoMain explorePojoMain;
                    Gson gson = new Gson();
                    explorePojoMain = gson.fromJson(result, ExplorePojoMain.class);*/

                    ProfileSoldMainPojo profileSoldMainPojo;
                    Gson gson = new Gson();
                    profileSoldMainPojo = gson.fromJson(result, ProfileSoldMainPojo.class);

                    switch (profileSoldMainPojo.getCode()) {
                        // success
                        case "200":
                            isToCallFirstTime=false;
                            rL_noProductFound.setVisibility(View.GONE);
                            if (profileSoldMainPojo.getData() != null && profileSoldMainPojo.getData().size() > 0)
                            {
                                rL_noProductFound.setVisibility(View.GONE);
                                arrayListSoldDatas.addAll(profileSoldMainPojo.getData());
                                isLoadingRequired= arrayListSoldDatas.size()>14;
                                soldRvAdapter.notifyDataSetChanged();

                                // Load more
                                rV_selling.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                                            profilePosts(pageIndex);
                                        }
                                    }
                                });
                                break;
                            }

                            // No post found
                        case "204" :
                            mSwipeRefreshLayout.setRefreshing(false);
                            if(arrayListSoldDatas.size()==0)
                                rL_noProductFound.setVisibility(View.VISIBLE);
                            break;

                        // auth token expired
                        case "401" :
                            mSwipeRefreshLayout.setRefreshing(false);
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // Any error
                        default:
                            mSwipeRefreshLayout.setRefreshing(false);
                            System.out.println(TAG+" "+"sold frag error="+profileSoldMainPojo.getMessage());
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
    }

    /**
     * <h>SellItAgainDialog</h>
     * <p>
     *     In this method we used to open a dialog to alert the user to sell the item again.
     * </p>
     * @param position the position of the item in the list
     */
    public void sellItAgainDialog(final int position)
    {
        final Dialog errorMessageDialog = new Dialog(mActivity);
        errorMessageDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        errorMessageDialog.setContentView(R.layout.dialog_sell_it_again);
        errorMessageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        errorMessageDialog.getWindow().setLayout((int)(CommonClass.getDeviceWidth(mActivity)*0.8), RelativeLayout.LayoutParams.WRAP_CONTENT);

        // dismiss
        TextView tV_no= (TextView) errorMessageDialog.findViewById(R.id.tV_no);
        tV_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorMessageDialog.dismiss();
            }
        });

        // yes
        TextView tV_yes= (TextView) errorMessageDialog.findViewById(R.id.tV_yes);
        tV_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonClass.isNetworkAvailable(mActivity))
                {
                    apiCall.markSellingApi(rL_rootview, arrayListSoldDatas.get(position).getPostId());
                    addSellingDatas(arrayListSoldDatas.get(position));
                    addHomePageDatas(arrayListSoldDatas.get(position));
                    setSocialDatas(arrayListSoldDatas.get(position));
                    arrayListSoldDatas.remove(position);
                    soldRvAdapter.notifyDataSetChanged();
                    if (arrayListSoldDatas.size()==0)
                        rL_noProductFound.setVisibility(View.VISIBLE);
                    errorMessageDialog.dismiss();
                }
                else
                {
                    CommonClass.showSnackbarMessage(((HomePageActivity)mActivity).rL_rootElement,getResources().getString(R.string.NoInternetAccess));
                }
            }
        });
        errorMessageDialog.show();
    }

    /**
     * <h>AddSellingDatas</h>
     * <p>
     *     In this method we used to make obj of Selling Frag datas set all values of that and
     *     send that object to the Selling via event bus.
     * </p>
     * @param mProfileSoldDatas The reference variables of ProfileSellingData.
     */
    private void addSellingDatas(ProfileSoldDatas mProfileSoldDatas)
    {
        ProfileSellingData profileSellingData = new ProfileSellingData();
        profileSellingData.setPostNodeId(mProfileSoldDatas.getPostNodeId());
        profileSellingData.setIsPromoted(mProfileSoldDatas.getIsPromoted());
        profileSellingData.setPlanId(mProfileSoldDatas.getPlanId());
        profileSellingData.setLikes(mProfileSoldDatas.getLikes());
        profileSellingData.setMainUrl(mProfileSoldDatas.getMainUrl());
        profileSellingData.setPostLikedBy(mProfileSoldDatas.getPostLikedBy());
        profileSellingData.setPlace(mProfileSoldDatas.getPlace());
        profileSellingData.setThumbnailImageUrl(mProfileSoldDatas.getThumbnailImageUrl());
        profileSellingData.setPostId(mProfileSoldDatas.getPostId());
        profileSellingData.setProductsTagged(mProfileSoldDatas.getProductsTagged());
        profileSellingData.setProductsTaggedCoordinates(mProfileSoldDatas.getProductsTaggedCoordinates());
        profileSellingData.setHasAudio(mProfileSoldDatas.getHasAudio());
        profileSellingData.setContainerHeight(mProfileSoldDatas.getContainerHeight());
        profileSellingData.setContainerWidth(mProfileSoldDatas.getContainerWidth());
        profileSellingData.setHashTags(mProfileSoldDatas.getHashTags());
        profileSellingData.setPostCaption(mProfileSoldDatas.getPostCaption());
        profileSellingData.setLatitude(mProfileSoldDatas.getLatitude());
        profileSellingData.setLongitude(mProfileSoldDatas.getLongitude());
        profileSellingData.setThumbnailUrl1(mProfileSoldDatas.getThumbnailUrl1());
        profileSellingData.setImageUrl1(mProfileSoldDatas.getImageUrl1());
        profileSellingData.setThumbnailImageUrl(mProfileSoldDatas.getThumbnailImageUrl());
        profileSellingData.setContainerWidth1(mProfileSoldDatas.getContainerWidth1());
        profileSellingData.setContainerHeight1(mProfileSoldDatas.getContainerHeight1());
        profileSellingData.setImageUrl2(mProfileSoldDatas.getImageUrl2());
        profileSellingData.setThumbnailUrl2(mProfileSoldDatas.getThumbnailUrl2());
        profileSellingData.setContainerHeight2(mProfileSoldDatas.getContainerHeight2());
        profileSellingData.setContainerWidth2(mProfileSoldDatas.getContainerWidth2());
        profileSellingData.setThumbnailUrl3(mProfileSoldDatas.getThumbnailUrl3());
        profileSellingData.setImageUrl3(mProfileSoldDatas.getImageUrl3());
        profileSellingData.setContainerHeight3(mProfileSoldDatas.getContainerHeight3());
        profileSellingData.setContainerWidth3(mProfileSoldDatas.getContainerWidth3());
        profileSellingData.setThumbnailUrl4(mProfileSoldDatas.getThumbnailUrl4());
        profileSellingData.setImageUrl4(mProfileSoldDatas.getImageUrl4());
        profileSellingData.setContainerHeight4(mProfileSoldDatas.getContainerHeight4());
        profileSellingData.setContainerWidth4(mProfileSoldDatas.getContainerWidth4());
        profileSellingData.setPostsType(mProfileSoldDatas.getPostsType());
        profileSellingData.setPostedOn(mProfileSoldDatas.getPostedOn());
        profileSellingData.setLikeStatus(mProfileSoldDatas.getLikeStatus());
        profileSellingData.setSold(mProfileSoldDatas.getSold());
        profileSellingData.setProductUrl(mProfileSoldDatas.getProductUrl());
        profileSellingData.setDescription(mProfileSoldDatas.getDescription());
        profileSellingData.setNegotiable(mProfileSoldDatas.getNegotiable());
        profileSellingData.setCondition(mProfileSoldDatas.getCondition());
        profileSellingData.setPrice(mProfileSoldDatas.getPrice());
        profileSellingData.setCurrency(mProfileSoldDatas.getCurrency());
        profileSellingData.setProductName(mProfileSoldDatas.getProductName());
        profileSellingData.setTotalComments(mProfileSoldDatas.getTotalComments());
        profileSellingData.setCategoryData(mProfileSoldDatas.getCategoryData());
        BusProvider.getInstance().post(profileSellingData);
    }

    /**
     * <h>AddHomePageDatas</h>
     * <p>
     *     In this method we used to make obj of Home Frag datas set all values of that and
     *     send that object to the Selling via event bus.
     * </p>
     * @param mProfileSoldDatas The reference variables of ProfileSellingData.
     */
    private void addHomePageDatas(ProfileSoldDatas mProfileSoldDatas)
    {
        ExploreResponseDatas mExploreResponseDatas = new ExploreResponseDatas();
        mExploreResponseDatas.setPostedByUserName(mSessionManager.getUserName());
        mExploreResponseDatas.setPostNodeId(mProfileSoldDatas.getPostNodeId());
        mExploreResponseDatas.setIsPromoted(mProfileSoldDatas.getIsPromoted());
        mExploreResponseDatas.setLikes(mProfileSoldDatas.getLikes());
        mExploreResponseDatas.setMainUrl(mProfileSoldDatas.getMainUrl());
        mExploreResponseDatas.setPlace(mProfileSoldDatas.getPlace());
        mExploreResponseDatas.setThumbnailImageUrl(mProfileSoldDatas.getThumbnailImageUrl());
        mExploreResponseDatas.setPostId(mProfileSoldDatas.getPostId());
        mExploreResponseDatas.setHasAudio(mProfileSoldDatas.getHasAudio());
        mExploreResponseDatas.setContainerHeight(mProfileSoldDatas.getContainerHeight());
        mExploreResponseDatas.setContainerWidth(mProfileSoldDatas.getContainerWidth());
        mExploreResponseDatas.setHashTags(mProfileSoldDatas.getHashTags());
        mExploreResponseDatas.setPostCaption(mProfileSoldDatas.getPostCaption());
        mExploreResponseDatas.setLatitude(mProfileSoldDatas.getLatitude());
        mExploreResponseDatas.setLongitude(mProfileSoldDatas.getLongitude());
        mExploreResponseDatas.setThumbnailUrl1(mProfileSoldDatas.getThumbnailUrl1());
        mExploreResponseDatas.setImageUrl1(mProfileSoldDatas.getImageUrl1());
        mExploreResponseDatas.setThumbnailImageUrl(mProfileSoldDatas.getThumbnailImageUrl());
        mExploreResponseDatas.setContainerWidth1(mProfileSoldDatas.getContainerWidth1());
        mExploreResponseDatas.setContainerHeight1(mProfileSoldDatas.getContainerHeight1());
        mExploreResponseDatas.setImageUrl2(mProfileSoldDatas.getImageUrl2());
        mExploreResponseDatas.setThumbnailUrl2(mProfileSoldDatas.getThumbnailUrl2());
        mExploreResponseDatas.setContainerHeight2(mProfileSoldDatas.getContainerHeight2());
        mExploreResponseDatas.setContainerWidth2(mProfileSoldDatas.getContainerWidth2());
        mExploreResponseDatas.setThumbnailUrl3(mProfileSoldDatas.getThumbnailUrl3());
        mExploreResponseDatas.setImageUrl3(mProfileSoldDatas.getImageUrl3());
        mExploreResponseDatas.setContainerHeight3(mProfileSoldDatas.getContainerHeight3());
        mExploreResponseDatas.setContainerWidth3(mProfileSoldDatas.getContainerWidth3());
        mExploreResponseDatas.setThumbnailUrl4(mProfileSoldDatas.getThumbnailUrl4());
        mExploreResponseDatas.setImageUrl4(mProfileSoldDatas.getImageUrl4());
        mExploreResponseDatas.setContainerHeight4(mProfileSoldDatas.getContainerHeight4());
        mExploreResponseDatas.setContainerWidth4(mProfileSoldDatas.getContainerWidth4());
        mExploreResponseDatas.setPostsType(mProfileSoldDatas.getPostsType());
        mExploreResponseDatas.setPostedOn(mProfileSoldDatas.getPostedOn());
        mExploreResponseDatas.setLikeStatus(mProfileSoldDatas.getLikeStatus());
        mExploreResponseDatas.setProductUrl(mProfileSoldDatas.getProductUrl());
        mExploreResponseDatas.setDescription(mProfileSoldDatas.getDescription());
        mExploreResponseDatas.setNegotiable(mProfileSoldDatas.getNegotiable());
        mExploreResponseDatas.setCondition(mProfileSoldDatas.getCondition());
        mExploreResponseDatas.setPrice(mProfileSoldDatas.getPrice());
        mExploreResponseDatas.setCurrency(mProfileSoldDatas.getCurrency());
        mExploreResponseDatas.setProductName(mProfileSoldDatas.getProductName());
        mExploreResponseDatas.setTotalComments(mProfileSoldDatas.getTotalComments());
        mExploreResponseDatas.setCategoryData(mProfileSoldDatas.getCategoryData());
        BusProvider.getInstance().post(mExploreResponseDatas);
    }

    /**
     * <h>SetSocialDatas</h>
     * <p>
     *     In this method we used to send the one complete object of a followed product
     *     to the socail screen through screen.
     * </p>
     */
    public void setSocialDatas(ProfileSoldDatas mProfileSoldDatas)
    {
        try {
            SocialDatas socialDatas = new SocialDatas();
            socialDatas.setToAddSocialData(true);
            socialDatas.setPostedOn(mProfileSoldDatas.getPostedOn());
            socialDatas.setProductName(mProfileSoldDatas.getProductName());
            socialDatas.setCategoryData(mProfileSoldDatas.getCategoryData());
            socialDatas.setCurrency(mProfileSoldDatas.getCurrency());
            socialDatas.setPrice(mProfileSoldDatas.getPrice());
            //socialDatas.setMemberProfilePicUrl(mProfileSoldDatas.getMemberProfilePicUrl());
            socialDatas.setMainUrl(mProfileSoldDatas.getMainUrl());
            socialDatas.setLikes(mProfileSoldDatas.getLikes());
            //socialDatas.setClickCount(mProfileSoldDatas.getClickCount());
            socialDatas.setLikeStatus(mProfileSoldDatas.getLikeStatus());
            socialDatas.setPostId(mProfileSoldDatas.getPostId());
            socialDatas.setMembername(mSessionManager.getUserName());
            BusProvider.getInstance().post(socialDatas);
        }
        catch (Exception e)
        {
            System.out.println(TAG+" "+"social event bus error="+e.getMessage());
        }
    }

    @Override
    public void onDestroy()
    {
        BusProvider.getInstance().unregister(this);
        super.onDestroy();
    }

    /*
        * Updating the comment list data.*/
    @Subscribe
    public void getMessage(ProfileSoldDatas soldResDetails)
    {
        if (soldResDetails!=null && !isContainsId(soldResDetails.getPostId())) {
            arrayListSoldDatas.add(0, soldResDetails);
            if (arrayListSoldDatas.size() > 0)
                rL_noProductFound.setVisibility(View.GONE);
            else rL_noProductFound.setVisibility(View.VISIBLE);
            soldRvAdapter.notifyDataSetChanged();
            System.out.println(TAG + " " + "get message called...");
        }
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
        for (ProfileSoldDatas object : arrayListSoldDatas) {
            System.out.println(TAG+" "+"given post id="+postId+" "+"current post id="+object.getPostId());
            if (postId.equals(object.getPostId())) {
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public void onItemClick(int pos, ImageView imageView) {
        System.out.println(TAG+" "+"sold item clicked");
        if (isFromMyProfile)
            sellItAgainDialog(pos);
        else
        {
            Intent intent = new Intent(mActivity, ProductDetailsActivity.class);
            intent.putExtra("productName", arrayListSoldDatas.get(pos).getProductName());
            if (arrayListSoldDatas.get(pos).getCategoryData()!=null)
                intent.putExtra("category", arrayListSoldDatas.get(pos).getCategoryData().get(0).getCategory());
            intent.putExtra("likes", arrayListSoldDatas.get(pos).getLikes());
            intent.putExtra("likeStatus", arrayListSoldDatas.get(pos).getLikeStatus());
            intent.putExtra("currency", arrayListSoldDatas.get(pos).getCurrency());
            intent.putExtra("price", arrayListSoldDatas.get(pos).getPrice());
            intent.putExtra("postedOn", arrayListSoldDatas.get(pos).getPostedOn());
            intent.putExtra("image", arrayListSoldDatas.get(pos).getMainUrl());
            intent.putExtra("thumbnailImageUrl", arrayListSoldDatas.get(pos).getThumbnailImageUrl());
            //intent.putExtra("likedByUsersArr",arrayListSoldDatas.get(pos).getLikedByUsers());
            intent.putExtra("description", arrayListSoldDatas.get(pos).getDescription());
            intent.putExtra("condition", arrayListSoldDatas.get(pos).getCondition());
            intent.putExtra("place", arrayListSoldDatas.get(pos).getPlace());
            intent.putExtra("latitude", arrayListSoldDatas.get(pos).getLatitude());
            intent.putExtra("longitude", arrayListSoldDatas.get(pos).getLongitude());
            //intent.putExtra("postedByUserName",arrayListSoldDatas.get(pos).getPostedByUserName());
            intent.putExtra("postId", arrayListSoldDatas.get(pos).getPostId());
            intent.putExtra("postsType", arrayListSoldDatas.get(pos).getPostsType());
            intent.putExtra("followRequestStatus","");
            intent.putExtra("clickCount","");
            intent.putExtra("memberProfilePicUrl","");
            intent.putExtra(VariableConstants.EXTRA_ANIMAL_IMAGE_TRANSITION_NAME, ViewCompat.getTransitionName(imageView));
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, imageView, ViewCompat.getTransitionName(imageView));
            startActivity(intent, options.toBundle());
        }
    }
}
