package com.yelo.com.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.yelo.com.R;
import com.yelo.com.adapter.AcceptedOfferRvAdap;
import com.yelo.com.pojo_class.accepted_offer.AcceptedOfferDatas;
import com.yelo.com.pojo_class.accepted_offer.AcceptedOfferMainPojo;
import com.yelo.com.utility.ApiUrl;
import com.yelo.com.utility.CommonClass;
import com.yelo.com.utility.DialogBox;
import com.yelo.com.utility.OkHttp3Connection;
import com.yelo.com.utility.SessionManager;
import com.yelo.com.utility.VariableConstants;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * <h>EditProductActivity</h>
 * <p>
 *     In this class we show the all accepted offer for particular product. We also have option
 *     to edit the product structure and can make product as mark as sold.
 * </p>
 * @since 13-Jul-17
 */
public class EditProductActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG=EditProductActivity.class.getSimpleName();
    private Activity mActivity;
    private RelativeLayout rL_rootElement,rL_empty_offer;
    private ProgressBar mProgressBar;
    private ArrayList<AcceptedOfferDatas> arrayListAcceptedOffer;
    private SessionManager mSessionManager;
    private RecyclerView rV_acceptedOffer;
    private LinearLayoutManager mLinearLayoutManager;
    private AcceptedOfferRvAdap acceptedOfferRvAdap;
    private String postId;
    private int pageIndex;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View view2;
    private DialogBox mDialogBox;
    private Bundle bundlePostDatas;

    // Load more
    private boolean isLoadMoreReq=false;
    private int totalItemCount,visibleThreshold=5,visibleCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        initVariables();
    }

    /**
     * <h>InitVariables</h>
     * <p>
     *     In this method we used to assign all the xml variable and data member.
     * </p>
     */
    private void initVariables()
    {
        mActivity=EditProductActivity.this;
        mDialogBox=new DialogBox(mActivity);

        //Get the bundlePostDatas
        String productName="",productImage="",category="";
        bundlePostDatas = getIntent().getExtras();
        if (bundlePostDatas!=null)
        {
            postId=bundlePostDatas.getString("postId");
            productImage=bundlePostDatas.getString("productImage");
            productName=bundlePostDatas.getString("productName");
            category=bundlePostDatas.getString("category");
        }

        System.out.println(TAG+" "+"product image="+productImage);

        pageIndex=0;
        view2=findViewById(R.id.view2);
        mSessionManager=new SessionManager(mActivity);
        rL_rootElement= (RelativeLayout)findViewById(R.id.rL_rootElement);
        rL_empty_offer= (RelativeLayout)findViewById(R.id.rL_empty_offer);
        mProgressBar= (ProgressBar) findViewById(R.id.progress_bar);
        rV_acceptedOffer= (RecyclerView) findViewById(R.id.rV_acceptedOffer);
        mSwipeRefreshLayout= (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.pink_color);
        arrayListAcceptedOffer=new ArrayList<>();

        // Back button
        RelativeLayout rL_back_btn= (RelativeLayout) findViewById(R.id.rL_back_btn);
        rL_back_btn.setOnClickListener(this);

        // set values to product info
        ImageView iV_productImage = (ImageView) findViewById(R.id.iV_productImage);
        if (productImage!=null && !productImage.isEmpty())
            Glide.with(mActivity)
                    .load(productImage)
                    .placeholder(R.drawable.app_launcher_logo)
                    .error(R.drawable.app_launcher_logo)
                    .into(iV_productImage);

        // set product name
        TextView tV_productname = (TextView) findViewById(R.id.tV_productname);
        if (productName!=null && !productName.isEmpty())
        {
            productName=productName.substring(0,1).toUpperCase()+productName.substring(1).toLowerCase();
            tV_productname.setText(productName);
        }

        // category
        TextView tV_category = (TextView) findViewById(R.id.tV_category);
        if (category!=null && !category.isEmpty())
        {
            category=category.substring(0,1).toUpperCase()+category.substring(1).toLowerCase();
            tV_category.setText(category);
        }

        // Accepted offer recycler view adapter
        acceptedOfferRvAdap=new AcceptedOfferRvAdap(mActivity,arrayListAcceptedOffer);
        mLinearLayoutManager=new LinearLayoutManager(mActivity);
        rV_acceptedOffer.setLayoutManager(mLinearLayoutManager);
        rV_acceptedOffer.setAdapter(acceptedOfferRvAdap);

        // call api call method to get all offer price
        if (CommonClass.isNetworkAvailable(mActivity))
        {
            mProgressBar.setVisibility(View.VISIBLE);
            acceptedOffersApi(pageIndex);
        }
        else CommonClass.showSnackbarMessage(rL_rootElement,getResources().getString(R.string.NoInternetAccess));

        // pull to refresh
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                arrayListAcceptedOffer.clear();
                pageIndex=0;
                acceptedOffersApi(pageIndex);
            }
        });

        // Mark as sold
        RelativeLayout rL_markAsSold= (RelativeLayout) findViewById(R.id.rL_markAsSold);
        rL_markAsSold.setOnClickListener(this);

        // insight
        TextView tV_insight= (TextView) findViewById(R.id.tV_insight);
        tV_insight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent insightIntent=new Intent(mActivity,InsightActivity.class);
                insightIntent.putExtra("postId",postId);
                startActivity(insightIntent);
            }
        });

        // Update product
        RelativeLayout rL_update_product= (RelativeLayout) findViewById(R.id.rL_update_product);
        rL_update_product.setOnClickListener(this);
    }

    /**
     * <h>AcceptedOffersApi</h>
     * <p>
     *     In this method we used to do api call to get all accepted offer for a
     *     product.
     * </p>
     * @param offset The page index
     */
    private void acceptedOffersApi(int offset)
    {
        if (CommonClass.isNetworkAvailable(mActivity))
        {
            int limit=20;
            offset=offset*limit;

            JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("token",mSessionManager.getAuthToken());
                request_datas.put("postId",postId);
                request_datas.put("postType", "0");
                request_datas.put("limit",limit);
                request_datas.put("offset",offset);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.ACCEPTED_OFFER, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    mProgressBar.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(false);
                    System.out.println(TAG+" "+"accepted offer="+result);

                    AcceptedOfferMainPojo acceptedOfferMainPojo;
                    Gson gson=new Gson();
                    acceptedOfferMainPojo=gson.fromJson(result,AcceptedOfferMainPojo.class);

                    switch (acceptedOfferMainPojo.getCode())
                    {
                        // success
                        case "200" :
                            if (acceptedOfferMainPojo.getData()!=null && acceptedOfferMainPojo.getData().size()>0)
                            {
                                arrayListAcceptedOffer.addAll(acceptedOfferMainPojo.getData());

                                isLoadMoreReq=arrayListAcceptedOffer.size()>14;
                                acceptedOfferRvAdap.notifyDataSetChanged();

                                // Load more
                                rV_acceptedOffer.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                    @Override
                                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                        super.onScrolled(recyclerView, dx, dy);

                                        totalItemCount=mLinearLayoutManager.getItemCount();
                                        visibleCount=mLinearLayoutManager.findLastVisibleItemPosition();

                                        if (isLoadMoreReq && totalItemCount<=(visibleCount+visibleThreshold))
                                        {
                                            isLoadMoreReq=false;
                                            pageIndex=pageIndex+1;
                                            mSwipeRefreshLayout.setRefreshing(true);
                                            acceptedOffersApi(pageIndex);
                                        }
                                    }
                                });
                            }
                            break;

                        // no offer found
                        case "204" :
                            view2.setVisibility(View.GONE);
                            rL_empty_offer.setVisibility(View.VISIBLE);
                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // error
                        default:
                            CommonClass.showSnackbarMessage(rL_rootElement,acceptedOfferMainPojo.getMessgae());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    mSwipeRefreshLayout.setRefreshing(false);
                    mProgressBar.setVisibility(View.GONE);
                    CommonClass.showSnackbarMessage(rL_rootElement,error);
                }
            });
        }
        else CommonClass.showSnackbarMessage(rL_rootElement,getResources().getString(R.string.NoInternetAccess));
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            // back button
            case R.id.rL_back_btn :
                onBackPressed();
                break;

            // mark as sold
            case R.id.rL_markAsSold :
                if (arrayListAcceptedOffer.size()>0) {
                    Intent intent = new Intent(mActivity, SelectBuyerActivity.class);
                    intent.putExtra("acceptedOffer",arrayListAcceptedOffer);
                    intent.putExtra("postId",postId);
                    startActivityForResult(intent, VariableConstants.SELLING_REQ_CODE);
                }
                else
                {
                    mDialogBox.sellSomeWhereDialog(postId,rL_rootElement);
                }
                break;

            // Update product
            case R.id.rL_update_product :
                mDialogBox.openUpdateProductDialog(postId,rL_rootElement,bundlePostDatas);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(TAG+" "+"requestCode="+requestCode+" "+"resultCode="+resultCode+" "+"data="+data);

        if (data!=null)
        {
            switch (requestCode)
            {
                case VariableConstants.SELLING_REQ_CODE :

                    boolean isToSellItAgain =data.getBooleanExtra("isToSellItAgain",false);
                    if (isToSellItAgain)
                        finish();

                    boolean isToFinishEditPost = data.getBooleanExtra("isToFinishEditPost",false);
                    if (isToFinishEditPost)
                        finish();
                    break;
            }
        }
    }
}
