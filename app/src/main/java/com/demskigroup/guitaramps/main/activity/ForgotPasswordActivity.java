package com.demskigroup.guitaramps.main.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.pojo_class.ForgotPasswordPojo;
import com.demskigroup.guitaramps.utility.ApiUrl;
import com.demskigroup.guitaramps.utility.CommonClass;
import com.demskigroup.guitaramps.utility.OkHttp3Connection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * <h>ForgotPasswordActivity</h>
 * <p>
 *     This class is called from Login screen. In this class we used to find the
 *     forgotten password using user email-id.
 * </p>
 * @since 4/5/2017
 * @author 3Embed
 * @version 1.0
 */
public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener
{
    private Activity mActivity;
    private EditText eT_emailId;
    private static final String TAG=ForgotPasswordActivity.class.getSimpleName();
    private TextView tV_send;
    private boolean isSendButtonEnabled;
    private ProgressBar mProgress_bar;
    private LinearLayout linear_rootElement;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);

        // request keyboard
        getWindow().setSoftInputMode (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        initVariables();
    }

    /**
     * <h>InitVariables</h>
     * <p>
     *     In this method we used to assign all the xml variables and data member like mActivity.
     * </p>
     */
    private void initVariables()
    {
        mActivity=ForgotPasswordActivity.this;
        CommonClass.statusBarColor(mActivity);

        mProgress_bar= (ProgressBar) findViewById(R.id.progress_bar);
        linear_rootElement= (LinearLayout) findViewById(R.id.linear_rootElement);
        final RelativeLayout rL_send,rL_back_btn;
        rL_send= (RelativeLayout) findViewById(R.id.rL_send);
        rL_send.setOnClickListener(this);
        rL_back_btn= (RelativeLayout) findViewById(R.id.rL_back_btn);
        rL_back_btn.setOnClickListener(this);
        tV_send= (TextView) findViewById(R.id.tV_send);

        CommonClass.setViewOpacity(mActivity,rL_send,102,R.drawable.rect_purple_color_with_solid_shape);

        // EditText Email Address
        eT_emailId= (EditText) findViewById(R.id.eT_emailId);
        eT_emailId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                String email=eT_emailId.getText().toString();
                if (!email.isEmpty() && CommonClass.isValidEmail(email))
                {
                    isSendButtonEnabled=true;
                    CommonClass.setViewOpacity(mActivity,rL_send,204,R.drawable.rect_purple_color_with_solid_shape);
                }
                else
                {
                    isSendButtonEnabled=false;
                    CommonClass.setViewOpacity(mActivity,rL_send,102,R.drawable.rect_purple_color_with_solid_shape);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        eT_emailId.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (isSendButtonEnabled)
                        resetPasswordApi();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            // back button click
            case R.id.rL_back_btn :
                onBackPressed();
                break;

            // call reset password api
            case R.id.rL_send :
                if (isSendButtonEnabled)
                    resetPasswordApi();
                break;
        }
    }

    /**
     * <h>ResetPasswordApi</h>
     * <p>
     *     In this method we used to do api call for forgot password.
     * </p>
     */
    private void resetPasswordApi() {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            mProgress_bar.setVisibility(View.VISIBLE);
            tV_send.setVisibility(View.GONE);
            JSONObject requestDatas = new JSONObject();

            try {
                requestDatas.put("type", "0");
                requestDatas.put("email", eT_emailId.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.RESET_PASSWORD, OkHttp3Connection.Request_type.POST, requestDatas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag)
                {
                    System.out.println(TAG+" "+"reset password res="+result);
                    mProgress_bar.setVisibility(View.GONE);
                    tV_send.setVisibility(View.VISIBLE);
                    ForgotPasswordPojo forgotPasswordPojo;
                    Gson gson=new Gson();
                    forgotPasswordPojo=gson.fromJson(result,ForgotPasswordPojo.class);

                    switch (forgotPasswordPojo.getCode())
                    {
                        // success
                        case "200" :
                            mProgress_bar.setVisibility(View.GONE);
                            CommonClass.showSuccessSnackbarMsg(linear_rootElement,forgotPasswordPojo.getMessage());

                            final Timer t = new Timer();
                            t.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    // when the task active then close the activity
                                    t.cancel();
                                    onBackPressed();
                                }
                            }, 3000);
                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // error
                        default:
                            CommonClass.showSnackbarMessage(linear_rootElement,forgotPasswordPojo.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    mProgress_bar.setVisibility(View.GONE);
                    CommonClass.showSnackbarMessage(linear_rootElement,error);
                }
            });
        }
        else CommonClass.showSnackbarMessage(linear_rootElement,getResources().getString(R.string.NoInternetAccess));
    }

    @Override
    public void onBackPressed()
    {
        showKeyboard(InputMethodManager.HIDE_IMPLICIT_ONLY);
        finish();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    private void showKeyboard(int flag)
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(flag,0);
    }

}
