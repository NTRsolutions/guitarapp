package com.demskigroup.guitaramps.main.view_pager.search_product;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.google.gson.Gson;
import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.adapter.SearchPeopleRvAdap;
import com.demskigroup.guitaramps.main.activity.SearchProductActivity;
import com.demskigroup.guitaramps.main.activity.SelfProfileActivity;
import com.demskigroup.guitaramps.main.activity.UserProfileActivity;
import com.demskigroup.guitaramps.pojo_class.search_people_pojo.SearchPeopleMainPojo;
import com.demskigroup.guitaramps.pojo_class.search_people_pojo.SearchPeopleUsers;
import com.demskigroup.guitaramps.utility.ApiUrl;
import com.demskigroup.guitaramps.utility.ClickListener;
import com.demskigroup.guitaramps.utility.CommonClass;
import com.demskigroup.guitaramps.utility.OkHttp3Connection;
import com.demskigroup.guitaramps.utility.SessionManager;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * <h>PeoplesFrag</h>
 * <p>
 *     This class is getting called from SearchProductActivity class.
 *     In this class we used to search the user using search user api.
 * </p>
 * @since 18-May-17
 * @version 1.0
 * @author 3Embed
 */
public class PeoplesFrag extends Fragment
{
    private Activity mActivity;
    private static final String TAG=PeoplesFrag.class.getSimpleName();
    private SessionManager mSessionManager;
    private RelativeLayout rL_rootElement;
    private RecyclerView rV_search_people;
    private boolean isPeopleFrag;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int pageIndex;

    // Load more variables
    private int totalVisibleItem,totalItemCount,visibleThreshold=5;
    private boolean isToLoadData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageIndex=0;
        mActivity=getActivity();
        mSessionManager=new SessionManager(mActivity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.frag_people,container,false);
        rL_rootElement= (RelativeLayout) view.findViewById(R.id.rL_rootElement);
        rV_search_people= (RecyclerView) view.findViewById(R.id.rV_search_people);
        mSwipeRefreshLayout= (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.pink_color);

        // search user
        searchUser();

        // pull to refresh
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                pageIndex=0;
                if (mSessionManager.getIsUserLoggedIn())
                searchUsersApi(((SearchProductActivity) mActivity).peopleText,pageIndex,ApiUrl.SEARCH_USER);
                else searchUsersApi(((SearchProductActivity) mActivity).peopleText,pageIndex,ApiUrl.SEARCH_GUEST_USER);
            }
        });
        return view;
    }

    /**
     * <h>SearchUser</h>
     * <p>
     *     In this method we used to do search people api call
     *     on every text changes.
     * </p>
     */
    private void searchUser()
    {
        ((SearchProductActivity)mActivity).eT_search_users.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isPeopleFrag) {
                    System.out.println(TAG + " " + "text entered=" + ((SearchProductActivity) mActivity).eT_search_users.getText().toString());
                    ((SearchProductActivity) mActivity).peopleText = ((SearchProductActivity) mActivity).eT_search_users.getText().toString();
                    pageIndex=0;
                    //searchUsersApi(((SearchProductActivity) mActivity).eT_search_users.getText().toString(), pageIndex);

                    if (mSessionManager.getIsUserLoggedIn())
                        searchUsersApi(((SearchProductActivity) mActivity).eT_search_users.getText().toString(), pageIndex,ApiUrl.SEARCH_USER);
                    else searchUsersApi(((SearchProductActivity) mActivity).eT_search_users.getText().toString(), pageIndex,ApiUrl.SEARCH_GUEST_USER);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void searchGuestMemberApi(String value,int offset)
    {

    }

    /**
     * <h>SearchUsersApi</h>
     * <p>
     *     In this method we used to do search user api.
     * </p>
     * @param value The list we get from server
     * @param offset The index
     */
    private void searchUsersApi(String value,int offset,String apiUrl)
    {
        if (CommonClass.isNetworkAvailable(mActivity))
        {
            int limit=20;
            offset=limit*offset;

            JSONObject request_datas=new JSONObject();
            try {
                request_datas.put("userNameToSearch",value);
                request_datas.put("member",value);
                request_datas.put("offset",offset);
                request_datas.put("limit",limit);
                request_datas.put("token",mSessionManager.getAuthToken());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            System.out.println(TAG+" "+"offset="+offset+" "+"searched text="+value);

            OkHttp3Connection.doOkHttp3Connection(TAG, apiUrl, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag)
                {
                    mSwipeRefreshLayout.setRefreshing(false);
                    System.out.println(TAG+" "+"search user res="+result);

                    final SearchPeopleMainPojo searchPeopleMainPojo;
                    Gson gson=new Gson();
                    searchPeopleMainPojo=gson.fromJson(result,SearchPeopleMainPojo.class);

                    switch (searchPeopleMainPojo.getCode())
                    {
                        case "200" :
                            if (searchPeopleMainPojo.getData()!=null && searchPeopleMainPojo.getData().size()>0)
                            {
                                final ArrayList<SearchPeopleUsers> aL_users=searchPeopleMainPojo.getData();
                                SearchPeopleRvAdap searchPeopleRvAdap=new SearchPeopleRvAdap(mActivity,aL_users);
                                final LinearLayoutManager mLinearLayoutManager=new LinearLayoutManager(mActivity);
                                rV_search_people.setLayoutManager(mLinearLayoutManager);
                                rV_search_people.setAdapter(searchPeopleRvAdap);
                                searchPeopleRvAdap.notifyDataSetChanged();

                                // Item click
                                searchPeopleRvAdap.setOnItemClick(new ClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position)
                                    {
                                        Intent intent;
                                        if (mSessionManager.getIsUserLoggedIn() && mSessionManager.getUserName().equals(aL_users.get(position).getUsername()))
                                        {
                                            intent = new Intent(mActivity, SelfProfileActivity.class);
                                            intent.putExtra("membername",aL_users.get(position).getUsername());
                                        }
                                        else
                                        {
                                            intent = new Intent(mActivity, UserProfileActivity.class);
                                            intent.putExtra("membername",aL_users.get(position).getUsername());
                                        }
                                        mActivity.startActivity(intent);
                                    }
                                });
                            }
                            else
                            {
                                final ArrayList<SearchPeopleUsers> aL_users=new ArrayList<>();
                                SearchPeopleRvAdap searchPeopleRvAdap=new SearchPeopleRvAdap(mActivity,aL_users);
                                LinearLayoutManager mLinearLayoutManager=new LinearLayoutManager(mActivity);
                                rV_search_people.setLayoutManager(mLinearLayoutManager);
                                rV_search_people.setAdapter(searchPeopleRvAdap);
                                searchPeopleRvAdap.notifyDataSetChanged();
                            }
                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // mandatory field
                        case "56714" :
                            final ArrayList<SearchPeopleUsers> aL_users=new ArrayList<>();
                            SearchPeopleRvAdap searchPeopleRvAdap=new SearchPeopleRvAdap(mActivity,aL_users);
                            LinearLayoutManager mLinearLayoutManager=new LinearLayoutManager(mActivity);
                            rV_search_people.setLayoutManager(mLinearLayoutManager);
                            rV_search_people.setAdapter(searchPeopleRvAdap);
                            searchPeopleRvAdap.notifyDataSetChanged();
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

    private void setRvAdapterDatas()
    {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isPeopleFrag=isVisibleToUser;
    }
}
