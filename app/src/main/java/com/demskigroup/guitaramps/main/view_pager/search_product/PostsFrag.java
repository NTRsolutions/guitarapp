package com.demskigroup.guitaramps.main.view_pager.search_product;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.google.gson.Gson;
import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.adapter.SearchPostsRvAdap;
import com.demskigroup.guitaramps.main.activity.SearchProductActivity;
import com.demskigroup.guitaramps.main.activity.products.ProductDetailsActivity;
import com.demskigroup.guitaramps.pojo_class.search_post_pojo.SearchPostDatas;
import com.demskigroup.guitaramps.pojo_class.search_post_pojo.SearchPostMainPojo;
import com.demskigroup.guitaramps.utility.ApiUrl;
import com.demskigroup.guitaramps.utility.CommonClass;
import com.demskigroup.guitaramps.utility.OkHttp3Connection;
import com.demskigroup.guitaramps.utility.ProductItemClickListener;
import com.demskigroup.guitaramps.utility.SessionManager;
import com.demskigroup.guitaramps.utility.VariableConstants;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * <h>PostsFrag</h>
 * <p>
 *     In this class we used to show the list of searched products.
 * </p>
 * @since 18-May-17
 */
public class PostsFrag extends Fragment
{
    private Activity mActivity;
    private SessionManager mSessionManager;
    private static final String TAG=PostsFrag.class.getSimpleName();
    private RelativeLayout rL_rootElement;
    private RecyclerView rV_search_post;
    private boolean isPostFrag;
    private int page_index=0;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity=getActivity();
        mSessionManager=new SessionManager(mActivity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.frag_people,container,false);
        rL_rootElement= (RelativeLayout) view.findViewById(R.id.rL_rootElement);
        rV_search_post= (RecyclerView) view.findViewById(R.id.rV_search_people);
        mSwipeRefreshLayout= (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.pink_color);

        // pull to refresh
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                page_index=0;
                searchProductApi(((SearchProductActivity) mActivity).postText,page_index);
            }
        });

        searchPost();
        return view;
    }

    /**
     * <h>SearchPost</h>
     * <p>
     *     In this method we used to do api call for each text changes.
     * </p>
     */
    private void searchPost()
    {
        ((SearchProductActivity)mActivity).eT_search_users.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (isPostFrag) {
                    System.out.println(TAG + " " + "text entered=" + ((SearchProductActivity) mActivity).eT_search_users.getText().toString()+" "+"s="+s);
                    ((SearchProductActivity) mActivity).postText = ((SearchProductActivity) mActivity).eT_search_users.getText().toString();
                    page_index=0;
                    searchProductApi(((SearchProductActivity) mActivity).eT_search_users.getText().toString(),page_index);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * <h>SearchProductApi</h>
     * <p>
     *     In this method we used to do api call(using method Http get method) and get all post.
     * </p>
     * @param searchText The character
     * @param offset The page index
     */
    private void searchProductApi(String searchText,int offset)
    {
        if (CommonClass.isNetworkAvailable(mActivity))
        {
            int limit=20;
            offset=limit*offset;
            System.out.println(TAG+" "+"offset="+offset+" "+"searched text="+searchText);

            String URL = ApiUrl.SEARCH_POST + searchText+"?offset="+offset+"?limit="+limit+"?token="+mSessionManager.getAuthToken();
            //System.out.println(TAG+" "+"url="+URL);

            OkHttp3Connection.doOkHttp3Connection(TAG, URL, OkHttp3Connection.Request_type.GET, new JSONObject(), new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag)
                {
                    mSwipeRefreshLayout.setRefreshing(false);
                    System.out.println(TAG+" "+"search product api res="+result);
                    SearchPostMainPojo searchPostMainPojo;
                    Gson gson=new Gson();
                    searchPostMainPojo=gson.fromJson(result,SearchPostMainPojo.class);

                    switch (searchPostMainPojo.getCode())
                    {
                        // success
                        case "200" :
                            if (searchPostMainPojo.getData()!=null && !searchPostMainPojo.getData().isEmpty())
                            {
                                final ArrayList<SearchPostDatas> aL_searchedPosts=searchPostMainPojo.getData();
                                // set post adapter
                                SearchPostsRvAdap searchPostsRvAdap=new SearchPostsRvAdap(mActivity,aL_searchedPosts);
                                LinearLayoutManager mLinearLayoutManager=new LinearLayoutManager(mActivity);
                                rV_search_post.setLayoutManager(mLinearLayoutManager);
                                rV_search_post.setAdapter(searchPostsRvAdap);
                                searchPostsRvAdap.notifyDataSetChanged();

                                // listen item click
                                searchPostsRvAdap.setOnItemClick(new ProductItemClickListener() {
                                    @Override
                                    public void onItemClick(int pos, ImageView imageView) {
                                        Intent intent = new Intent(mActivity, ProductDetailsActivity.class);
                                        intent.putExtra("productName", aL_searchedPosts.get(pos).getProductName());
                                        intent.putExtra("category", aL_searchedPosts.get(pos).getCategory());
                                        intent.putExtra("likes", aL_searchedPosts.get(pos).getLikes());
                                        intent.putExtra("likeStatus", "");
                                        intent.putExtra("currency", aL_searchedPosts.get(pos).getCurrency());
                                        intent.putExtra("price", aL_searchedPosts.get(pos).getPrice());
                                        intent.putExtra("postedOn", aL_searchedPosts.get(pos).getPostedOn());
                                        intent.putExtra("image",aL_searchedPosts.get(pos).getMainUrl());
                                        intent.putExtra("thumbnailImageUrl",aL_searchedPosts.get(pos).getThumbnailImageUrl());
                                        intent.putExtra("likedByUsersArr",aL_searchedPosts.get(pos).getLikedByUsers());
                                        intent.putExtra("description","");
                                        intent.putExtra("condition","");
                                        intent.putExtra("place",aL_searchedPosts.get(pos).getPlace());
                                        intent.putExtra("latitude",aL_searchedPosts.get(pos).getLatitude());
                                        intent.putExtra("longitude",aL_searchedPosts.get(pos).getLongitude());
                                        intent.putExtra("postedByUserName",aL_searchedPosts.get(pos).getUsername());
                                        intent.putExtra("postId",aL_searchedPosts.get(pos).getPostId());
                                        intent.putExtra("postsType",aL_searchedPosts.get(pos).getPostsType());
                                        intent.putExtra("followRequestStatus","");
                                        intent.putExtra("clickCount","");
                                        intent.putExtra("memberProfilePicUrl","");
                                        intent.putExtra(VariableConstants.EXTRA_ANIMAL_IMAGE_TRANSITION_NAME, ViewCompat.getTransitionName(imageView));

                                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, imageView, ViewCompat.getTransitionName(imageView));
                                        startActivity(intent, options.toBundle());
                                    }
                                });
                            }
                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // no data
                        case "204" :
                            SearchPostsRvAdap searchPostsRvAdap=new SearchPostsRvAdap(mActivity,new ArrayList<SearchPostDatas>());
                            LinearLayoutManager mLinearLayoutManager=new LinearLayoutManager(mActivity);
                            rV_search_post.setLayoutManager(mLinearLayoutManager);
                            rV_search_post.setAdapter(searchPostsRvAdap);
                            searchPostsRvAdap.notifyDataSetChanged();
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    mSwipeRefreshLayout.setRefreshing(false);
                    CommonClass.showTopSnackBar(rL_rootElement,error);
                }
            });
        }
        else CommonClass.showTopSnackBar(rL_rootElement,getResources().getString(R.string.NoInternetAccess));
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isPostFrag=isVisibleToUser;
    }
}
