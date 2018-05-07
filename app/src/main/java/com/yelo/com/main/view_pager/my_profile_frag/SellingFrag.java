package com.yelo.com.main.view_pager.my_profile_frag;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.squareup.otto.Subscribe;
import com.yelo.com.R;
import com.yelo.com.Uploader.ProductImageDatas;
import com.yelo.com.adapter.ProfileSellingFragRvAdap;
import com.yelo.com.event_bus.BusProvider;
import com.yelo.com.main.activity.Camera2Activity;
import com.yelo.com.main.activity.CameraActivity;
import com.yelo.com.main.activity.EditProductActivity;
import com.yelo.com.main.activity.HomePageActivity;
import com.yelo.com.main.activity.products.ProductDetailsActivity;
import com.yelo.com.pojo_class.profile_selling_pojo.ProfileSellingData;
import com.yelo.com.pojo_class.profile_selling_pojo.ProfileSellingMainPojo;
import com.yelo.com.utility.ApiUrl;
import com.yelo.com.utility.CommonClass;
import com.yelo.com.utility.OkHttp3Connection;
import com.yelo.com.utility.ProductItemClickListener;
import com.yelo.com.utility.SessionManager;
import com.yelo.com.utility.SpacesItemDecoration;
import com.yelo.com.utility.VariableConstants;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * <h>SellingFrag</h>
 * <p>
 *     In this class we used to show all the product which is posted by user.
 * </p>
 * @since 4/7/2017
 * @author 3Embed
 */
public class SellingFrag extends Fragment implements ProductItemClickListener
{
    private Activity mActivity;
    private static final String TAG=SellingFrag.class.getSimpleName();
    private SessionManager mSessionManager;
    private ProgressBar progress_bar_profile;
    private RelativeLayout rL_noProductFound;
    private ArrayList<ProfileSellingData> arrayListSellingDatas;
    private ProfileSellingFragRvAdap sellingRvAdapter;
    private RecyclerView rV_selling;
    private StaggeredGridLayoutManager gridLayoutManager;
    private String memberName;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int pageIndex;

    // Load more var
    private boolean isLoadingRequired,isToCallFirstTime,isFromMyProfile;
    private int visibleItemCount,totalItemCount,visibleThreshold=5;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity=getActivity();
        mSessionManager=new SessionManager(mActivity);
        memberName=getArguments().getString("memberName");
        isFromMyProfile=getArguments().getBoolean("isFromMyProfileFlag",false);
        isToCallFirstTime=true;
        System.out.println(TAG+" "+"isFromMyProfile="+isFromMyProfile);
    }

    public static SellingFrag newInstance(String memberName,boolean isFromMyProfile)
    {
        Bundle bundle=new Bundle();
        bundle.putString("memberName",memberName);
        bundle.putBoolean("isFromMyProfileFlag",isFromMyProfile);

        SellingFrag sellingFrag=new SellingFrag();
        sellingFrag.setArguments(bundle);
        return sellingFrag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.frag_profile_buying,container,false);
        rV_selling = (RecyclerView) view.findViewById(R.id.rV_myprofile_selling);

        pageIndex=0;
        // set space equility between recycler view items
        int spanCount = 2; // 2 columns
        int spacing = 10; // 50px
        rV_selling.addItemDecoration(new SpacesItemDecoration(spanCount, spacing));

        mSwipeRefreshLayout= (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        progress_bar_profile= (ProgressBar) view.findViewById(R.id.progress_bar_profile);

        arrayListSellingDatas=new ArrayList<>();
        sellingRvAdapter=new ProfileSellingFragRvAdap(mActivity,arrayListSellingDatas,this);
        gridLayoutManager=new StaggeredGridLayoutManager(2,1);
        rV_selling.setLayoutManager(gridLayoutManager);
        rV_selling.setAdapter(sellingRvAdapter);

        // set empty favourite icon
        rL_noProductFound= (RelativeLayout) view.findViewById(R.id.rL_noProductFound);
        rL_noProductFound.setVisibility(View.GONE);

        ImageView iV_default_icon= (ImageView) view.findViewById(R.id.iV_default_icon);
        iV_default_icon.setImageResource(R.drawable.empty_selling_icon);

        TextView tV_no_ads= (TextView) view.findViewById(R.id.tV_no_ads);
        tV_no_ads.setText(getResources().getString(R.string.no_ads_yet));

        TextView tV_snapNpost= (TextView) view.findViewById(R.id.tV_snapNpost);
        tV_snapNpost.setText(getResources().getString(R.string.snapNpostIn));

        TextView tV_start_discovering= (TextView) view.findViewById(R.id.tV_start_discovering);
        tV_start_discovering.setText(getResources().getString(R.string.start_selling));

        RelativeLayout rL_start_selling= (RelativeLayout) view.findViewById(R.id.rL_start_selling);

        if (!isFromMyProfile)
            rL_start_selling.setVisibility(View.GONE);
        else rL_start_selling.setVisibility(View.VISIBLE);
        rL_start_selling.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(mActivity, CameraActivity.class));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    startActivity(new Intent(mActivity, Camera2Activity.class));
                else
                    startActivity(new Intent(mActivity, CameraActivity.class));
            }
        });

        // call api call method
        if (CommonClass.isNetworkAvailable(mActivity) && isToCallFirstTime)
        {
            progress_bar_profile.setVisibility(View.VISIBLE);
            if(mSessionManager.getIsUserLoggedIn()) {
                profilePosts(pageIndex);
            }else{
                guestProfilePosts(pageIndex);
            }
        }
        else  CommonClass.showSnackbarMessage(((HomePageActivity)mActivity).rL_rootElement,getResources().getString(R.string.NoInternetAccess));

        // pull to refresh
        mSwipeRefreshLayout.setColorSchemeResources(R.color.pink_color);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageIndex=0;
                arrayListSellingDatas.clear();
                sellingRvAdapter.notifyDataSetChanged();
                if(mSessionManager.getIsUserLoggedIn()){
                    profilePosts(pageIndex);
                }else{
                    guestProfilePosts(pageIndex);
                }
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

    private void profilePosts(int offset)
    {
        if (CommonClass.isNetworkAvailable(mActivity))
        {
            int limit=20;
            offset=limit*offset;
            JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("token", mSessionManager.getAuthToken());
                request_datas.put("limit",limit);
                request_datas.put("offset",offset);
                request_datas.put("sold", "0");
                request_datas.put("membername",memberName);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            String url;

            Log.d("username111",memberName);
            Log.d("username112",mSessionManager.getUserName());

            if(mSessionManager.getUserName().equals(memberName)) {
                url=ApiUrl.PROFILE_POST;
                Log.d("username113",url);
            }
            else {
                url=ApiUrl.PROFILE_POST+memberName;
                Log.d("username114",url);
            }


            OkHttp3Connection.doOkHttp3Connection(TAG, url, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    progress_bar_profile.setVisibility(View.GONE);
                    System.out.println(TAG + " " + "profile selling res=" + result);
                    mSwipeRefreshLayout.setRefreshing(false);

                    /*ExplorePojoMain explorePojoMain;
                    Gson gson = new Gson();
                    explorePojoMain = gson.fromJson(result, ExplorePojoMain.class);*/

                    ProfileSellingMainPojo profileSellingMainPojo;
                    Gson gson = new Gson();
                    profileSellingMainPojo = gson.fromJson(result,ProfileSellingMainPojo.class);

                    switch (profileSellingMainPojo.getCode()) {
                        // success
                        case "200":
                            if (profileSellingMainPojo.getData() != null && profileSellingMainPojo.getData().size() > 0)
                            {
                                isToCallFirstTime=false;
                                rL_noProductFound.setVisibility(View.GONE);
                                arrayListSellingDatas.addAll(profileSellingMainPojo.getData());
                                isLoadingRequired=arrayListSellingDatas.size()>14;
                                sellingRvAdapter.notifyDataSetChanged();

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
                            }
                            break;

                        // no more data found
                        case "204":
                            mSwipeRefreshLayout.setRefreshing(false);
                            if(arrayListSellingDatas.size()==0){
                                rL_noProductFound.setVisibility(View.VISIBLE);
                            }
                            break;

                        // auth token expired
                        case "401" :
                            mSwipeRefreshLayout.setRefreshing(false);
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // Any error
                        default:
                            mSwipeRefreshLayout.setRefreshing(false);
                            CommonClass.showTopSnackBar(((HomePageActivity)mActivity).rL_rootElement,profileSellingMainPojo.getMessage());
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

    /*
* Updating the comment list data.*/
    @Subscribe
    public void getMessage(ProfileSellingData sellingDatas)
    {
        System.out.println(TAG+" "+"get message called..."+" "+"sellingDatas="+sellingDatas);

        if (sellingDatas!=null) {
            if (sellingDatas.isToRemoveSellingItem()) {
                if (arrayListSellingDatas.size() > 0) {
                    for (int sellingItemCount = 0; sellingItemCount < arrayListSellingDatas.size(); sellingItemCount++) {
                        String postId = sellingDatas.getPostId();
                        if (postId.equals(arrayListSellingDatas.get(sellingItemCount).getPostId()))
                        {
                            arrayListSellingDatas.remove(sellingItemCount);
                            sellingRvAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            } else {
                if (!isContainsId(sellingDatas.getPostId()))
                {
                    rL_noProductFound.setVisibility(View.GONE);
                    arrayListSellingDatas.add(0, sellingDatas);
                    System.out.println(TAG + " " + "arrayListSellingDatas size=" + arrayListSellingDatas.size());
                    sellingRvAdapter.notifyDataSetChanged();
                }
            }
        }

        System.out.println(TAG+" "+"selling item size="+arrayListSellingDatas.size());
        if (arrayListSellingDatas.size()>0)
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
        for (ProfileSellingData object : arrayListSellingDatas) {
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
    public void onItemClick(int pos, ImageView imageView) {
        System.out.println(TAG+" "+"selling item clicked");
        if (isFromMyProfile) {
            Intent intent = new Intent(mActivity, EditProductActivity.class);
            Bundle bundle=new Bundle();
            bundle.putString("postId", arrayListSellingDatas.get(pos).getPostId());
            bundle.putString("productImage", arrayListSellingDatas.get(pos).getMainUrl());
            bundle.putString("productName", arrayListSellingDatas.get(pos).getProductName());
            bundle.putString("category", arrayListSellingDatas.get(pos).getCategoryData().get(0).getCategory());
            bundle.putString("description", arrayListSellingDatas.get(pos).getDescription());
            bundle.putString("condition", arrayListSellingDatas.get(pos).getCondition());
            bundle.putString("price", arrayListSellingDatas.get(pos).getPrice());
            bundle.putString("negotiable", arrayListSellingDatas.get(pos).getNegotiable());
            bundle.putString("place", arrayListSellingDatas.get(pos).getPlace());
            bundle.putString("latitude", arrayListSellingDatas.get(pos).getLatitude());
            bundle.putString("longitude", arrayListSellingDatas.get(pos).getLongitude());
            bundle.putString("currency", arrayListSellingDatas.get(pos).getCurrency());
            if (arrayListSellingDatas.get(pos).getCategoryData()!=null)
                bundle.putString("category", arrayListSellingDatas.get(pos).getCategoryData().get(0).getCategory());
            System.out.println(TAG+" "+"category="+arrayListSellingDatas.get(pos).getCategoryData().get(0).getCategory());

            ArrayList<ProductImageDatas> aLProductImageDatases=new ArrayList<>();

            // first image
            String mainUrl=arrayListSellingDatas.get(pos).getMainUrl();
            if (mainUrl!=null && !mainUrl.isEmpty()) {
                ProductImageDatas productImageDatas1=new ProductImageDatas();
                productImageDatas1.setMainUrl(arrayListSellingDatas.get(pos).getMainUrl());
                productImageDatas1.setThumbnailUrl(arrayListSellingDatas.get(pos).getThumbnailImageUrl());
                productImageDatas1.setPublic_id(arrayListSellingDatas.get(pos).getCloudinaryPublicId());

                // set width
                String width=arrayListSellingDatas.get(pos).getContainerWidth();
                if (width!=null && !width.isEmpty())
                    productImageDatas1.setWidth(Integer.parseInt(width));

                // set height
                String height= arrayListSellingDatas.get(pos).getContainerHeight();
                if (height!=null && !height.isEmpty())
                    productImageDatas1.setHeight(Integer.parseInt(height));

                productImageDatas1.setImageUrl(true);
                aLProductImageDatases.add(productImageDatas1);
            }

            // second image
            String imageUrl1=arrayListSellingDatas.get(pos).getImageUrl1();
            if (imageUrl1!=null && !imageUrl1.isEmpty())
            {
                ProductImageDatas productImageDatas2=new ProductImageDatas();
                productImageDatas2.setMainUrl(arrayListSellingDatas.get(pos).getImageUrl1());
                productImageDatas2.setThumbnailUrl(arrayListSellingDatas.get(pos).getThumbnailUrl1());
                productImageDatas2.setPublic_id(arrayListSellingDatas.get(pos).getCloudinaryPublicId1());

                // set width
                String width=arrayListSellingDatas.get(pos).getContainerWidth1();
                if (width!=null && !width.isEmpty())
                    productImageDatas2.setWidth(Integer.parseInt(width));

                // set height
                String height= arrayListSellingDatas.get(pos).getContainerHeight1();
                if (height!=null && !height.isEmpty())
                    productImageDatas2.setHeight(Integer.parseInt(height));

                productImageDatas2.setImageUrl(true);
                aLProductImageDatases.add(productImageDatas2);
            }

            // Third Image
            String imageUrl2=arrayListSellingDatas.get(pos).getImageUrl2();
            if (imageUrl2!=null && !imageUrl2.isEmpty())
            {
                ProductImageDatas productImageDatas3=new ProductImageDatas();
                productImageDatas3.setMainUrl(arrayListSellingDatas.get(pos).getImageUrl2());
                productImageDatas3.setThumbnailUrl(arrayListSellingDatas.get(pos).getThumbnailUrl2());
                productImageDatas3.setPublic_id(arrayListSellingDatas.get(pos).getCloudinaryPublicId2());
                // set width
                String width=arrayListSellingDatas.get(pos).getContainerWidth2();
                if (width!=null && !width.isEmpty())
                    productImageDatas3.setWidth(Integer.parseInt(width));

                // set height
                String height= arrayListSellingDatas.get(pos).getContainerHeight2();
                if (height!=null && !height.isEmpty())
                    productImageDatas3.setHeight(Integer.parseInt(height));

                productImageDatas3.setImageUrl(true);
                aLProductImageDatases.add(productImageDatas3);
            }

            // Fourth Image
            String imageUrl3 =arrayListSellingDatas.get(pos).getImageUrl3();
            if (imageUrl3!=null && !imageUrl3.isEmpty())
            {
                ProductImageDatas productImageDatas4=new ProductImageDatas();
                productImageDatas4.setMainUrl(arrayListSellingDatas.get(pos).getImageUrl3());
                productImageDatas4.setThumbnailUrl(arrayListSellingDatas.get(pos).getThumbnailUrl3());
                productImageDatas4.setPublic_id(arrayListSellingDatas.get(pos).getCloudinaryPublicId3());
                // set width
                String width=arrayListSellingDatas.get(pos).getContainerWidth3();
                if (width!=null && !width.isEmpty())
                    productImageDatas4.setWidth(Integer.parseInt(width));

                // set height
                String height= arrayListSellingDatas.get(pos).getContainerHeight3();
                if (height!=null && !height.isEmpty())
                    productImageDatas4.setHeight(Integer.parseInt(height));

                productImageDatas4.setImageUrl(true);
                aLProductImageDatases.add(productImageDatas4);
            }

            // Fifth Image
            String imageUrl4 =arrayListSellingDatas.get(pos).getImageUrl4();
            if (imageUrl4!=null && !imageUrl4.isEmpty())
            {
                ProductImageDatas productImageDatas5=new ProductImageDatas();
                productImageDatas5.setMainUrl(arrayListSellingDatas.get(pos).getImageUrl4());
                productImageDatas5.setThumbnailUrl(arrayListSellingDatas.get(pos).getThumbnailUrl4());
                productImageDatas5.setPublic_id(arrayListSellingDatas.get(pos).getCloudinaryPublicId4());
                // set width
                String width=arrayListSellingDatas.get(pos).getContainerWidth4();
                if (width!=null && !width.isEmpty())
                    productImageDatas5.setWidth(Integer.parseInt(width));

                // set height
                String height= arrayListSellingDatas.get(pos).getContainerHeight4();
                if (height!=null && !height.isEmpty())
                    productImageDatas5.setHeight(Integer.parseInt(height));

                productImageDatas5.setImageUrl(true);
                aLProductImageDatases.add(productImageDatas5);
            }

            bundle.putSerializable("imageDatas",aLProductImageDatases);
            intent.putExtras(bundle);
            startActivity(intent);
        }
        else
        {
            Intent intent = new Intent(mActivity, ProductDetailsActivity.class);
            intent.putExtra("productName", arrayListSellingDatas.get(pos).getProductName());
            if (arrayListSellingDatas.get(pos).getCategoryData()!=null)
                intent.putExtra("category", arrayListSellingDatas.get(pos).getCategoryData().get(0).getCategory());
            System.out.println(TAG+" "+"category="+arrayListSellingDatas.get(pos).getCategoryData().get(0).getCategory());
            intent.putExtra("likes", arrayListSellingDatas.get(pos).getLikes());
            intent.putExtra("likeStatus", arrayListSellingDatas.get(pos).getLikeStatus());
            intent.putExtra("currency", arrayListSellingDatas.get(pos).getCurrency());
            intent.putExtra("price", arrayListSellingDatas.get(pos).getPrice());
            intent.putExtra("postedOn", arrayListSellingDatas.get(pos).getPostedOn());
            intent.putExtra("image",arrayListSellingDatas.get(pos).getMainUrl());
            intent.putExtra("thumbnailImageUrl",arrayListSellingDatas.get(pos).getThumbnailImageUrl());
            intent.putExtra("description",arrayListSellingDatas.get(pos).getDescription());
            intent.putExtra("condition",arrayListSellingDatas.get(pos).getCondition());
            intent.putExtra("place",arrayListSellingDatas.get(pos).getPlace());
            intent.putExtra("latitude",arrayListSellingDatas.get(pos).getLatitude());
            intent.putExtra("longitude",arrayListSellingDatas.get(pos).getLongitude());
            intent.putExtra("postId",arrayListSellingDatas.get(pos).getPostId());
            intent.putExtra("postsType",arrayListSellingDatas.get(pos).getPostsType());
            intent.putExtra("followRequestStatus","");
            intent.putExtra("clickCount","");
            intent.putExtra("memberProfilePicUrl","");
            intent.putExtra(VariableConstants.EXTRA_ANIMAL_IMAGE_TRANSITION_NAME, ViewCompat.getTransitionName(imageView));
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, imageView, ViewCompat.getTransitionName(imageView));
            startActivity(intent, options.toBundle());
        }
    }

    private void guestProfilePosts(int offset) {
        if (CommonClass.isNetworkAvailable(mActivity))
        {
            int limit=20;
            offset=limit*offset;
            JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("token", mSessionManager.getAuthToken());
                request_datas.put("limit",limit);
                request_datas.put("offset",offset);
                request_datas.put("sold", "0");
                request_datas.put("membername",memberName);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            String url=ApiUrl.GUEST_PROFILE_POST;

            OkHttp3Connection.doOkHttp3Connection(TAG, url, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    progress_bar_profile.setVisibility(View.GONE);
                    System.out.println(TAG + " " + "profile selling res=" + result);
                    mSwipeRefreshLayout.setRefreshing(false);

                    /*ExplorePojoMain explorePojoMain;
                    Gson gson = new Gson();
                    explorePojoMain = gson.fromJson(result, ExplorePojoMain.class);*/

                    ProfileSellingMainPojo profileSellingMainPojo;
                    Gson gson = new Gson();
                    profileSellingMainPojo = gson.fromJson(result,ProfileSellingMainPojo.class);

                    switch (profileSellingMainPojo.getCode()) {
                        // success
                        case "200":
                            if (profileSellingMainPojo.getData() != null && profileSellingMainPojo.getData().size() > 0)
                            {
                                isToCallFirstTime=false;
                                rL_noProductFound.setVisibility(View.GONE);
                                arrayListSellingDatas.addAll(profileSellingMainPojo.getData());
                                isLoadingRequired=arrayListSellingDatas.size()>14;
                                sellingRvAdapter.notifyDataSetChanged();

                                // Load more
                                rV_selling.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                    @Override
                                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                        super.onScrolled(recyclerView, dx, dy);

                                        int[] firstVisibleItemPositions = new int[2];
                                        totalItemCount = gridLayoutManager.getItemCount();
                                        visibleItemCount = gridLayoutManager.findLastVisibleItemPositions(firstVisibleItemPositions)[0];

                                        if (isLoadingRequired && totalItemCount<=(visibleItemCount+visibleThreshold)) {
                                            isLoadingRequired=false;
                                            pageIndex=pageIndex+1;
                                            mSwipeRefreshLayout.setRefreshing(true);
                                            guestProfilePosts(pageIndex);
                                        }
                                    }
                                });
                            }
                            break;

                        // no more data found
                        case "204":
                            mSwipeRefreshLayout.setRefreshing(false);
                            if(arrayListSellingDatas.size()==0){
                                rL_noProductFound.setVisibility(View.VISIBLE);
                            }
                            break;

                        // auth token expired
                        case "401" :
                            mSwipeRefreshLayout.setRefreshing(false);
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // Any error
                        default:
                            mSwipeRefreshLayout.setRefreshing(false);
                            CommonClass.showTopSnackBar(((HomePageActivity)mActivity).rL_rootElement,profileSellingMainPojo.getMessage());
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
}

