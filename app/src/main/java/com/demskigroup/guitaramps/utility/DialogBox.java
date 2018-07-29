package com.demskigroup.guitaramps.utility;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.main.activity.HomePageActivity;
import com.demskigroup.guitaramps.main.activity.MarketOwnerActivity;
import com.demskigroup.guitaramps.main.activity.UpdatePostActivity;
import com.demskigroup.guitaramps.mqttchat.AppController;
import com.demskigroup.guitaramps.pojo_class.LogoutPojo;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * <h>DialogBox</h>
 * <p>
 *     In this class we used to show various dialog pop-up.
 * </p>
 * @since 4/4/2017
 */
public class DialogBox
{
    private Activity mActivity;
    private ApiCall mApiCall;
    private ProgressBarHandler mProgressBar;
    private SessionManager mSessionManager;
    private static final String TAG=DialogBox.class.getSimpleName();
    public Dialog progressBarDialog;

    /**
     * <h>DialogBox</h>
     * <p>
     *    This is simple constructor to initialize the context from activity.
     * </p>
     * @param mActivity the current context.
     */
    public DialogBox(Activity mActivity) {
        this.mActivity = mActivity;
        mApiCall=new ApiCall(mActivity);
        mProgressBar=new ProgressBarHandler(mActivity);
        mSessionManager=new SessionManager(mActivity);
    }

    /**
     * <h>ShowErrorMessage</h>
     * <p>
     *     In this method we used to show dialog for error message like if
     *     there is no internet connection.
     * </p>
     * @param errorTitle the message to show
     */
    private void showErrorMessage(String errorTitle)
    {
        final Dialog errorMessageDialog = new Dialog(mActivity);
        errorMessageDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        errorMessageDialog.setContentView(R.layout.error_message_layout);
        errorMessageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        errorMessageDialog.getWindow().setLayout((int)(CommonClass.getDeviceWidth(mActivity)*0.8), RelativeLayout.LayoutParams.WRAP_CONTENT);

        // Message title
        TextView tV_message_title= (TextView) errorMessageDialog.findViewById(R.id.tV_message_title);
        if (errorTitle!=null)
            tV_message_title.setText(errorTitle);

        // Confirm button
        Button confirm_button= (Button) errorMessageDialog.findViewById(R.id.confirm_button);
        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorMessageDialog.dismiss();
            }
        });
        errorMessageDialog.show();
    }

    /**
     * <p>ShowLogoutAlert</p>
     * <p>
     *     In this method we used to show the alert for logout.
     *     in this dialog we used to show two option cancel and
     *     logout, If user click on cancel than just dismiss the
     *     dialog else call logout api.
     * </p>
     */
    public void showLogoutAlert()
    {
        final Dialog errorMessageDialog = new Dialog(mActivity);
        errorMessageDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        errorMessageDialog.setContentView(R.layout.dialog_show_alert);
        errorMessageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        errorMessageDialog.getWindow().setLayout((int)(CommonClass.getDeviceWidth(mActivity)*0.8), RelativeLayout.LayoutParams.WRAP_CONTENT);
        TextView tV_logout,tV_cancel;

        // Cancel
        tV_cancel= (TextView) errorMessageDialog.findViewById(R.id.tV_cancel);
        tV_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorMessageDialog.dismiss();
            }
        });

        // logout
        tV_logout= (TextView) errorMessageDialog.findViewById(R.id.tV_logout);
        tV_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorMessageDialog.dismiss();
                logoutService();
            }
        });

        errorMessageDialog.show();
    }

    /**
     * <h>LogoutService</h>
     * <p>
     *     In this method we used to call logout api, after getting success response
     *     from server we used to call LoginActivity and cleaer entire activity
     *     history from stack.
     * </p>
     */
    private void logoutService()
    {
        if (CommonClass.isNetworkAvailable(mActivity))
        {
            mProgressBar.show();
            JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("token", mSessionManager.getAuthToken());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.LOGOUT, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    mProgressBar.hide();
                    System.out.println(TAG+" "+"logged out res="+result);

                    LogoutPojo logoutPojo;
                    Gson gson=new Gson();
                    logoutPojo=gson.fromJson(result,LogoutPojo.class);
                    Intent intent;

                    switch (logoutPojo.getCode())
                    {
                        // success
                        case "200" :
                            mSessionManager.setIsUserLoggedIn(false);
                            mSessionManager.setChatSync(false);
                            AppController.getInstance().setChatSynced(false);
                            AppController.getInstance().setSignStatusChanged(false);
                            AppController.getInstance().doLoggedOut();
                            mSessionManager.setUserName("");
                            mSessionManager.setAuthToken("");
                            mSessionManager.setUserImage("");
                            intent = new Intent(mActivity, HomePageActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            mActivity.startActivity(intent);
                            break;

                        // User not found
                        case "204" :
                            intent = new Intent(mActivity, HomePageActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            mActivity.startActivity(intent);
                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // Error
                        default:
                            showErrorMessage(logoutPojo.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    mProgressBar.hide();
                    showErrorMessage(error);
                }
            });
        }
        else showErrorMessage(mActivity.getResources().getString(R.string.NoInternetAccess));
    }

    /**
     * <h>SelectGender</h>
     * <p>
     *     In this method we used to open a simple dialog pop-up to select
     *     gender.
     * </p>
     * @param setGender the textview on which we set the selected text
     */
    public void selectGender(final TextView setGender)
    {
        final Dialog selectGenderDialog = new Dialog(mActivity);
        selectGenderDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        selectGenderDialog.setContentView(R.layout.dialog_select_gender);
        selectGenderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //Male text
        TextView tV_male = (TextView) selectGenderDialog.findViewById(R.id.tV_male);
        tV_male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGender.setText(mActivity.getResources().getString(R.string.male));
                selectGenderDialog.dismiss();
            }
        });

        // Female text
        TextView tV_female = (TextView) selectGenderDialog.findViewById(R.id.tV_female);
        tV_female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGender.setText(mActivity.getResources().getString(R.string.female));
                selectGenderDialog.dismiss();
            }
        });
        selectGenderDialog.show();
    }

    /**
     * <h>PostedSuccessDialog</h>
     * <p>
     *     In this method we used to show simple dialog popup to show
     *     the success message just after the successfull posting of
     *     product. Once we click on ok then we used to clear the stack
     *     and move to HomePageActivity.
     * </p>
     */
    public void postedSuccessDialog()
    {
        final Dialog dialog = new Dialog(mActivity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_post_product_success);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        // click on ok to go to Home screen
        RelativeLayout relative_done= (RelativeLayout) dialog.findViewById(R.id.relative_done);
        relative_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, HomePageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
               // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(intent);

            }
        });
        dialog.show();
    }

    /**
     * <h>localCampaignDialog</h>
     * <p>
     *     In this method we used to open a dialog pop-up when local camapaign push
     *     notification come.
     * </p>
     * @param username The logged-in user name
     * @param userId The logged in user id
     * @param campaignId campaign id
     * @param imageUrl The image url for the campaign
     * @param title The title of the campaign
     * @param message The message
     * @param url The browser usl
     */
    public void localCampaignDialog(final String username, final String userId, final String campaignId, String imageUrl, String title, String message, final String url)
    {
        final Dialog campaignDialog = new Dialog(mActivity);
        campaignDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        campaignDialog.setContentView(R.layout.dialog_local_campaign);
        campaignDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        campaignDialog.getWindow().setLayout((int)(CommonClass.getDeviceWidth(mActivity)*0.9), RelativeLayout.LayoutParams.WRAP_CONTENT);

        // cancel dialog
        ImageView iV_cancel= (ImageView) campaignDialog.findViewById(R.id.iV_cancel);
        iV_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApiCall.userCampaignApi(username,userId,campaignId,"1");
                campaignDialog.dismiss();
            }
        });

        // set Image
        ImageView iV_image= (ImageView) campaignDialog.findViewById(R.id.iV_image);
        iV_image.getLayoutParams().height=(int) (CommonClass.getDeviceWidth(mActivity)/1.5);

        // set Image
        if (imageUrl!=null && imageUrl.contains("defaultImg"))
        {
            iV_image.setImageResource(R.drawable.start_broswing_icon);
        }
        else {
            if (imageUrl!=null &&!imageUrl.isEmpty())
                Picasso.with(mActivity)
                        .load(imageUrl)
                        .placeholder(R.drawable.start_broswing_icon)
                        .error(R.drawable.start_broswing_icon)
                        .transform(new RoundedCornersTransform())
                        .into(iV_image);
        }

        // first line
        TextView tV_first_text= (TextView) campaignDialog.findViewById(R.id.tV_first_text);
        tV_first_text.setVisibility(View.GONE);

        // set title
        TextView tV_title= (TextView) campaignDialog.findViewById(R.id.tV_title);
        if (title!=null && !title.isEmpty())
            tV_title.setText(title);

        // set message
        TextView tV_message= (TextView) campaignDialog.findViewById(R.id.tV_message);
        if (message!=null && !message.isEmpty())
            tV_message.setText(message);

        // know more
        TextView tV_start_browsering= (TextView) campaignDialog.findViewById(R.id.tV_start_browsering);
        tV_start_browsering.setText(mActivity.getResources().getString(R.string.know_more));

        // click on this to go to browser page
        System.out.println(TAG+" "+"url="+url+" "+"img url="+imageUrl);
        RelativeLayout rL_start_browsing= (RelativeLayout) campaignDialog.findViewById(R.id.rL_start_browsing);

        // hide know more button if url is empty
        if (url==null || url.equals("http://"))
            rL_start_browsing.setVisibility(View.GONE);

        rL_start_browsing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (url!=null && !url.isEmpty())
                {
                    mApiCall.userCampaignApi(username, userId, campaignId, "2");
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    mActivity.startActivity(browserIntent);
                    campaignDialog.dismiss();
                }
            }
        });
        if (!campaignDialog.isShowing())
        campaignDialog.show();
    }

    /**
     * <h>openShareOptionDialog</h>
     * <p>
     *     In this method we used to open a dialog to show option like sharing or copy item url.
     * </p>
     */
    public void openUpdateProductDialog(final String postId, final View rootView, final Bundle bundlePostDatas)
    {
        final Dialog shareDialog=new Dialog(mActivity);
        shareDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        shareDialog.setContentView(R.layout.dialog_update_product);
        shareDialog.getWindow().setGravity(Gravity.BOTTOM);
        shareDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        shareDialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
        shareDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);

        // delete post
        RelativeLayout rL_delete_post= (RelativeLayout) shareDialog.findViewById(R.id.rL_delete_post);
        rL_delete_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareDialog.dismiss();
            showProgressDialog(mActivity.getResources().getString(R.string.deleting));
                mApiCall.deletePostApi(postId,rootView,progressBarDialog);
        }
        });

        // Edit post
        RelativeLayout rL_edit_post= (RelativeLayout) shareDialog.findViewById(R.id.rL_edit_post);
        rL_edit_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareDialog.dismiss();
                if (bundlePostDatas!=null) {
                    Intent intent = new Intent(mActivity, UpdatePostActivity.class);
                    intent.putExtras(bundlePostDatas);
                    mActivity.startActivity(intent);
                }
            }
        });

        // cancel
        TextView cancel_button= (TextView)shareDialog.findViewById(R.id.cancel_button);
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareDialog.dismiss();
            }
        });
        shareDialog.show();
    }

    /**
     * <h>SellSomeWhereDialog</h>
     * <p>
     *     In this method we used to call api to switch product from selling tab into selling tab.
     * </p>
     */
    public void sellSomeWhereDialog(final String postId, final View rootView)
    {
        final Dialog errorMessageDialog = new Dialog(mActivity);
        errorMessageDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        errorMessageDialog.setContentView(R.layout.dialog_sell_it_again);
        errorMessageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        errorMessageDialog.getWindow().setLayout((int)(CommonClass.getDeviceWidth(mActivity)*0.8), RelativeLayout.LayoutParams.WRAP_CONTENT);

        // message title
        TextView tV_title= (TextView) errorMessageDialog.findViewById(R.id.tV_title);
        tV_title.setText(mActivity.getResources().getString(R.string.want_to_sell_somewhere_else));

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
                    showProgressDialog(mActivity.getResources().getString(R.string.Loading));
                    mApiCall.sellSomeWhereElseApi(rootView,postId,progressBarDialog);
                    errorMessageDialog.dismiss();
                }
                else CommonClass.showSnackbarMessage(rootView,mActivity.getResources().getString(R.string.NoInternetAccess));
            }
        });
        errorMessageDialog.show();
    }

    /**
     * <h>ShowProgressDialog</h>
     * <p>
     *     In this method we used to show the custom progress dialog.
     * </p>
     */
    public void showProgressDialog(String progressBarText)
    {
        progressBarDialog = new Dialog(mActivity);
        progressBarDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        progressBarDialog.setContentView(R.layout.dialog_custom_progress);
        progressBarDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressBarDialog.setCancelable(false);

        TextView tV_loading = (TextView) progressBarDialog.findViewById(R.id.tV_loading);
        if (progressBarText!=null && !progressBarText.isEmpty())
            tV_loading.setText(progressBarText);

        //The new dim amount, from 0 for no dim to 1 for full dim.
        progressBarDialog.getWindow().setDimAmount(0.5f);
        if (!mActivity.isFinishing())
        progressBarDialog.show();
    }

    /**
     * <h>StartBrowsingDialog</h>
     * <p>
     *     In this method we used to open a simple dialog box to show the start browsing pop-up
     *     once just after the registration.
     * </p>
     */
    public void startBrowsingDialog()
    {
        final Dialog dialog = new Dialog(mActivity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_start_browsing);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout((int)(CommonClass.getDeviceWidth(mActivity)*0.9), RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout rL_start_browsing= (RelativeLayout) dialog.findViewById(R.id.rL_start_browsing);

        // start browsing
        rL_start_browsing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();

                Intent intent = new Intent(mActivity, MarketOwnerActivity.class);

                mActivity.startActivity(intent);
            }
        });

        // close
        RelativeLayout rL_close= (RelativeLayout) dialog.findViewById(R.id.rL_close);
        rL_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                Intent intent = new Intent(mActivity, MarketOwnerActivity.class);

                mActivity.startActivity(intent);
            }
        });
        dialog.show();
    }
}
