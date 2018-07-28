package com.demskigroup.guitaramps.utility;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import com.google.gson.Gson;
import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.event_bus.EventBusDatasHandler;
import com.demskigroup.guitaramps.pojo_class.DeletePostPojo;
import com.demskigroup.guitaramps.pojo_class.FollowUserPojo;
import com.demskigroup.guitaramps.pojo_class.LikeProductPojo;
import com.demskigroup.guitaramps.pojo_class.mark_selling.MarkSellingMainPojo;
import com.demskigroup.guitaramps.pojo_class.sold_somewhere_else_pojo.SoldSomeWhereData;
import com.demskigroup.guitaramps.pojo_class.sold_somewhere_else_pojo.SoldSomeWhereMainPojo;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

/**
 * <h>ApiCall</h>
 * <p>
 *    In this class we used to do api call like like or unlike the product etc.
 * </p>
 * @since 02-Jun-17
 * @author 3Embed
 * @version 1.0
 */
public class ApiCall
{
    private Activity mActivity;
    private SessionManager mSessionManager;
    private static final String TAG=ApiUrl.class.getSimpleName();
    private EventBusDatasHandler mEventBusDatasHandler;

    public ApiCall(Activity mActivity) {
        this.mActivity = mActivity;
        mSessionManager=new SessionManager(mActivity);
        mEventBusDatasHandler = new EventBusDatasHandler(mActivity);
    }

    /**
     * <h>StaticMapApi</h>
     * <p>
     *     In this method we used to hit google static map api using HttpClient. In the respose
     *     of this api we get the bitmap of static map then we set that map into imageview.
     * </p>
     * @param iv_staticMap The imageview
     * @param lat The given latitude
     * @param lng The given longitude.
     */
    public void staticMapApi(final ImageView iv_staticMap, String lat, String lng)
    {
        final String STATIC_MAP_API_ENDPOINT = "https://maps.googleapis.com/maps/api/staticmap?center="+lat+","+lng+"&zoom=17&size=600x250";
        AsyncTask<Void, Void, Bitmap> setImageFromUrl = new AsyncTask<Void, Void, Bitmap>()
        {
            @Override
            protected Bitmap doInBackground(Void... params) {
                Bitmap bmp = null;
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet request = new HttpGet(STATIC_MAP_API_ENDPOINT);

                InputStream in;
                try {
                    in = httpclient.execute(request).getEntity().getContent();
                    bmp = BitmapFactory.decodeStream(in);
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return bmp;
            }
            protected void onPostExecute(Bitmap bmp) {
                if (bmp!=null)
                {
                    iv_staticMap.setImageBitmap(bmp);
                }
            }
        };
        setImageFromUrl.execute();
    }

    /**
     * <h>LikeProductApi</h>
     * <p>
     *     In this method we used to do api call for like or unlike product using OkHttpClient.
     * </p>
     * @param ServiceUrl The APiUrl
     */
    public void likeProductApi(String ServiceUrl,String postId)
    {
        JSONObject request_datas = new JSONObject();
        try {
            request_datas.put("token", mSessionManager.getAuthToken());
            request_datas.put("postId",postId);
            request_datas.put("label", "Photo");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttp3Connection.doOkHttp3Connection(TAG, ServiceUrl, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
            @Override
            public void onSuccess(String result, String user_tag)
            {
                System.out.println(TAG+" "+"like product res="+result);
                Gson gson = new Gson();
                LikeProductPojo likeProductPojo = gson.fromJson(result, LikeProductPojo.class);

                switch (likeProductPojo.getCode()) {
                    // success
                    case "200":
                        System.out.println(TAG+" "+"success message="+likeProductPojo.getMessage());
                        break;

                    // auth token expired
                    case "401" :
                        CommonClass.sessionExpired(mActivity);
                        break;

                    //error
                    default:
                        System.out.println(TAG + " " + "like product error message=" + likeProductPojo.getMessage());
                        break;
                }
            }

            @Override
            public void onError(String error, String user_tag) {
                System.out.println(TAG + " " + "like product error message=" + error);
            }
        });
    }



    /**
     * <h>FollowUserApi</h>
     * <p>
     *     In this build we used to follow or unfollow the user.
     * </p>
     * @param ApiUrl The service url to follow or unfollow
     */
    public void followUserApi(String ApiUrl)
    {
        JSONObject requestDatas=new JSONObject();
        try {
            //requestDatas.put(userNameParam,membername);
            requestDatas.put("token",mSessionManager.getAuthToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println(TAG+" "+"url="+ApiUrl);

        OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl, OkHttp3Connection.Request_type.POST, requestDatas, new OkHttp3Connection.OkHttp3RequestCallback() {
            @Override
            public void onSuccess(String result, String user_tag) {

                System.out.println(TAG+" "+"follow user api res="+result);
                FollowUserPojo followUserPojo;
                Gson gson=new Gson();
                followUserPojo=gson.fromJson(result,FollowUserPojo.class);

                switch (followUserPojo.getCode())
                {
                    // success
                    case "200" :
                        System.out.println(TAG+" "+"follow user success="+followUserPojo.getMessage());
                        break;

                    // auth token expired
                    case "401" :
                        CommonClass.sessionExpired(mActivity);
                        break;

                    // error
                    default:
                        System.out.println(TAG+" "+"follow user success="+followUserPojo.getMessage());
                        break;
                }
            }

            @Override
            public void onError(String error, String user_tag)
            {
                System.out.println(TAG+" "+"follow user success="+error);

            }
        });
    }

    /**
     * <h>DeletePostComment</h>
     * <p>
     *     In this method we used to do api call to delete particular review.
     * </p>
     * @param commentId The product commment node id
     * @param postId The post id of a product
     */
    public void deletePostComment(String commentId,String postId)
    {
        if (CommonClass.isNetworkAvailable(mActivity))
        {
            JSONObject requst_body=new JSONObject();
            try {
                requst_body.put("commentId",commentId);
                requst_body.put("label","Photo");
                requst_body.put("postId",postId);
                requst_body.put("token",mSessionManager.getAuthToken());
                requst_body.put("type","0");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.DELETE_COMMENTS, OkHttp3Connection.Request_type.POST, requst_body, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    System.out.println(TAG+" "+"delete comment res="+result);
                }

                @Override
                public void onError(String error, String user_tag) {
                    System.out.println(TAG+" ");
                }
            });
        }
    }

    /**
     * <h>markSellingApi</h>
     * <p>
     *     In this method we used to do api call to move item from sold to selling within
     *     my profile fragment.
     * </p>
     * @param rL_rootview The root element
     * @param postId The post id of that product.
     */
    public void markSellingApi(final View rL_rootview, String postId)
    {
        // token, postId, type(0 : Photo, 1 : Video)
        JSONObject request_datas=new JSONObject();
        try {
            request_datas.put("token",mSessionManager.getAuthToken());
            request_datas.put("postId",postId);
            request_datas.put("type","0");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.MARK_SELLING, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
            @Override
            public void onSuccess(String result, String user_tag)
            {
                System.out.println(TAG+" "+"mark selling success="+result);

                MarkSellingMainPojo markSellingMainPojo;
                Gson gson=new Gson();
                markSellingMainPojo=gson.fromJson(result,MarkSellingMainPojo.class);

                switch (markSellingMainPojo.getCode())
                {
                    // success
                    case "200" :
                        System.out.println(TAG+" "+"sucess message="+markSellingMainPojo.getCode());
                        break;

                    // auth token expired
                    case "401" :
                        CommonClass.sessionExpired(mActivity);
                        break;

                    // error
                    default:
                        CommonClass.showSnackbarMessage(rL_rootview,markSellingMainPojo.getMessage());
                        break;
                }

            }

            @Override
            public void onError(String error, String user_tag) {
                CommonClass.showSnackbarMessage(rL_rootview,error);
            }
        });
    }

    /**
     * <h>SellSomeWhereElseApi</h>
     * <p>
     *     In this method we used to do api call to switch item from selling to sold tab.
     * </p>
     * @param rootView The root element of the EditProductActivity class
     * @param postId the postId of the product
     */
    void sellSomeWhereElseApi(final View rootView, String postId, final Dialog pDialog)
    {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("token", mSessionManager.getAuthToken());
                request_datas.put("postId", postId);
                request_datas.put("type", "0");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.ELSE_WHERE, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    System.out.println(TAG + " " + "sell somewhere else res=" + result);

                    if (pDialog!=null)
                        pDialog.dismiss();
                    SoldSomeWhereMainPojo soldSomeWhereMainPojo;
                    Gson gson = new Gson();
                    soldSomeWhereMainPojo = gson.fromJson(result, SoldSomeWhereMainPojo.class);

                    switch (soldSomeWhereMainPojo.getCode()) {
                        // success
                        case "200":
                            SoldSomeWhereData soldSomeWhereData = soldSomeWhereMainPojo.getData();
                            mEventBusDatasHandler.addSoldDatasFromEditPost(soldSomeWhereData);
                            mEventBusDatasHandler.removeHomePageDatasFromEditPost(soldSomeWhereData.getPostId());
                            mEventBusDatasHandler.removeSocialDatasFromEditPost(soldSomeWhereData.getPostId());
                            mEventBusDatasHandler.removeSellingDatasFromEditPost(soldSomeWhereData.getPostId());
                            Intent intent = new Intent();
                            intent.putExtra("isToSellItAgain",true);
                            mActivity.setResult(VariableConstants.SELLING_REQ_CODE,intent);
                            mActivity.finish();
                            break;

                        // auth token expired
                        case "401":
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // error
                        default:
                            CommonClass.showSnackbarMessage(rootView, soldSomeWhereMainPojo.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    if (pDialog!=null)
                        pDialog.dismiss();
                    CommonClass.showSnackbarMessage(rootView, error);
                }
            });
        }
        else
        {
            CommonClass.showSnackbarMessage(rootView, mActivity.getResources().getString(R.string.NoInternetAccess));
        }
    }

    /**
     * <h>userCampaignApi</h>
     * <p>
     *     In this method we used to do api call after getting local campaign push notification.
     *     To register on admin that whether that given url has been opened or not.
     * </p>
     * @param username The logged-in user name
     * @param userId The logged-in user id
     * @param campaignId The campaign id of the notification
     * @param type The type 1 means if user cancel the dialog 2 means user has clicked on see more
     */
    void userCampaignApi(String username, String userId, String campaignId, String type)
    {
        // token,username,userId,campaignId,type
        JSONObject request_datas=new JSONObject();
        try {
            request_datas.put("token",mSessionManager.getAuthToken());
            request_datas.put("username",username);
            request_datas.put("userId",userId);
            request_datas.put("campaignId",campaignId);
            request_datas.put("type",type);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.USER_CAMPAIGN, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
            @Override
            public void onSuccess(String result, String user_tag) {
                System.out.println(TAG+" "+"user campaign res="+result);
            }

            @Override
            public void onError(String error, String user_tag) {

            }
        });
    }

    /**
     * <h>DeletePostApi</h>
     * <p>
     *     In this method we used to do api call to delete my own post.
     * </p>
     * @param postId The postid of the product which is to be deleted.
     */
    void deletePostApi(final String postId, final View rootView, final Dialog pDialog)
    {
        if (CommonClass.isNetworkAvailable(mActivity))
        {
            String deletePostUrl =  ApiUrl.PRODUCT +"?postId="+postId +"&token=" + mSessionManager.getAuthToken();
            System.out.println(TAG+" "+"delete post url="+deletePostUrl);
            OkHttp3Connection.doOkHttp3Connection(TAG,deletePostUrl, OkHttp3Connection.Request_type.DELETE, new JSONObject(), new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    System.out.println(TAG + " " + "delete post res=" + result);

                    if (pDialog!=null)
                        pDialog.dismiss();
                    DeletePostPojo deletePostPojo;
                    Gson gson=new Gson();
                    deletePostPojo=gson.fromJson(result,DeletePostPojo.class);

                    switch (deletePostPojo.getCode())
                    {
                        // success
                        case "200" :
                            mEventBusDatasHandler.removeHomePageDatasFromEditPost(postId);
                            mEventBusDatasHandler.removeSocialDatasFromEditPost(postId);
                            mEventBusDatasHandler.removeSellingDatasFromEditPost(postId);

                            Intent intent=new Intent();
                            intent.putExtra("isPostDeleted",true);
                            mActivity.setResult(VariableConstants.SELLING_REQ_CODE,intent);
                            mActivity.finish();
                            break;

                        // error
                        default:
                            CommonClass.showSnackbarMessage(rootView,deletePostPojo.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    if (pDialog!=null)
                        pDialog.dismiss();
                    CommonClass.showSnackbarMessage(rootView,error);
                }
            });
        }
        else
        {
            if (pDialog!=null)
                pDialog.dismiss();
            CommonClass.showSnackbarMessage(rootView,mActivity.getResources().getString(R.string.NoInternetAccess));
        }
    }
}
