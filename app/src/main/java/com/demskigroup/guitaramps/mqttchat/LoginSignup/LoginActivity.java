package com.demskigroup.guitaramps.mqttchat.LoginSignup;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.couchbase.lite.android.AndroidContext;
import com.demskigroup.guitaramps.BuildConfig;
import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.mqttchat.Activities.LandingPage;
import com.demskigroup.guitaramps.mqttchat.AppController;
import com.demskigroup.guitaramps.mqttchat.Database.CouchDbController;
import com.demskigroup.guitaramps.mqttchat.Utilities.ApiOnServer;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {


    private RelativeLayout root;

    private EditText email, password;
    private TextInputLayout email_et_til, password_et_til;


    // [START declare_auth]
//    private FirebaseAuth mAuth;

    // [END declare_auth]
    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {


        supportRequestWindowFeature(AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);


        setContentView(R.layout.login_activity);

        // [START initialize_auth]
//        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        root = (RelativeLayout) findViewById(R.id.root);


        TextView title = (TextView) findViewById(R.id.title);

//
//        TextView tv1 = (TextView) findViewById(R.id.textView7);
        TextView tv2 = (TextView) findViewById(R.id.tv2);

        Typeface tf = AppController.getInstance().getRobotoCondensedFont();
        title.setTypeface(tf, Typeface.BOLD);
//        tv1.setTypeface(tf, Typeface.NORMAL);
        tv2.setTypeface(tf, Typeface.BOLD);


        email = (EditText) findViewById(R.id.input_name2);


        email.setText(getIntent().getExtras().getString("email"));
        password = (EditText) findViewById(R.id.input_name3);
        email_et_til = (TextInputLayout) findViewById(R.id.input_layout_name2);
        password_et_til = (TextInputLayout) findViewById(R.id.input_layout_name3);
        email.addTextChangedListener(new MyTextWatcher(email));
        password.addTextChangedListener(new MyTextWatcher(password));


        RelativeLayout login = (RelativeLayout) findViewById(R.id.createAccount);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


                if (validateEmail() && validatePassword())

                {


                    //  handleLoginWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim());


                    hitApiOnServer(email.getText().toString().trim(), password.getText().toString().trim());


                } else if (!validateEmail()) {

                    if (root != null) {
                        Snackbar snackbar = Snackbar.make(root, "Invalid email", Snackbar.LENGTH_SHORT);


                        snackbar.show();
                        View view = snackbar.getView();
                        TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                        txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                    }
                } else if (!validatePassword()) {

                    if (root != null) {
                        Snackbar snackbar = Snackbar.make(root, "Invalid password", Snackbar.LENGTH_SHORT);


                        snackbar.show();
                        View view = snackbar.getView();
                        TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                        txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                    }
                }
            }
        });


        ImageView close = (ImageView) findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                onBackPressed();

            }
        });


    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {

                case R.id.input_name2:
                    validateEmail();
                    break;
                case R.id.input_name3:
                    validatePassword();
                    break;
            }
        }
    }


    /***
     *
     * To check if email entered is a valid email
     */

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    /**
     * To validate email entered
     */
    private boolean validateEmail() {
        String email2 = email.getText().toString().trim();

        if (email2.isEmpty()) {
            email_et_til.setError(getString(R.string.EmptyEmail));
            requestFocus(email);
            return false;
        } else if (!isValidEmail(email2)) {


            email_et_til.setError(getString(R.string.UserIdentifier));
            requestFocus(email);
            return false;


        } else {
            email_et_til.setErrorEnabled(false);
        }

        return true;
    }


    /**
     * To validate password entered
     */

    private boolean validatePassword() {
        if (password.getText().toString().trim().isEmpty()) {
            password_et_til.setError(getString(R.string.EnterPW));
            requestFocus(password);
            return false;
        } else if (password.getText().toString().trim().length() < 6) {
            password_et_til.setError(getString(R.string.InvalidLength));
            requestFocus(password);
            return false;

        } else {
            password_et_til.setErrorEnabled(false);
        }

        return true;
    }


    /*
     * To validate email and password on server and if valid then fetch user details and login him into the app
     */


    /**
     * Error dialog incase failed to signin user(If no user has matching login credentials)
     */
    @SuppressWarnings("TryWithIdenticalCatches")
    public void showErrorDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                LoginActivity.this);
        alertDialog.setTitle(R.string.SigningIn);
        alertDialog.setMessage(getString(R.string.SignInFailed));

        alertDialog.setNegativeButton(R.string.Ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //dialog.dismiss();


                        Context context = ((ContextWrapper) ((Dialog) dialog).getContext()).getBaseContext();


                        if (context instanceof Activity) {


                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                if (!((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
                                    dialog.dismiss();
                                }
                            } else {


                                if (!((Activity) context).isFinishing()) {
                                    dialog.dismiss();
                                }
                            }
                        } else {


                            try {
                                dialog.dismiss();
                            } catch (final IllegalArgumentException e) {
                                e.printStackTrace();

                            } catch (final Exception e) {
                                e.printStackTrace();

                            }
                        }


                    }
                });
        alertDialog.show();
    }


    @Override
    public void onBackPressed() {


        super.onBackPressed();
        supportFinishAfterTransition();


    }


    @Override
    protected void onResume() {
        super.onResume();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @SuppressWarnings("TryWithIdenticalCatches")

    private void hitApiOnServer(final String email, String password) {


        JSONObject obj = new JSONObject();

        try {
            obj.put("email", email);
            obj.put("password", password);



            obj.put("deviceId", AppController.getInstance().getDeviceId());

            /*
             * DeviceType  1-ios,2-android
             */


            obj.put("deviceType", 2);
            obj.put("deviceModel", Build.MODEL);
            obj.put("deviceMake", Build.MANUFACTURER);
            obj.put("deviceOs", String.valueOf(Build.VERSION.SDK_INT));
            obj.put("pushToken", "");
            obj.put("appVersion", BuildConfig.VERSION_NAME);
            obj.put("versionCode", BuildConfig.VERSION_CODE);



            Log.d("log24",obj.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }


        final ProgressDialog pDialog = new ProgressDialog(LoginActivity.this, 0);


        pDialog.setCancelable(false);

//        pDialog.setTitle(R.string.string_351);

        pDialog.setMessage(getString(R.string.SigningIn));
        pDialog.show();

        ProgressBar bar = (ProgressBar) pDialog.findViewById(android.R.id.progress);


        bar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(LoginActivity.this, R.color.color_black),
                android.graphics.PorterDuff.Mode.SRC_IN);


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                ApiOnServer.LOGIN_API, obj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {


                //  hideProgressDialog();
                if (pDialog.isShowing()) {

                    // pDialog.dismiss();
                    Context context = ((ContextWrapper) (pDialog).getContext()).getBaseContext();


                    if (context instanceof Activity) {


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            if (!((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
                                pDialog.dismiss();
                            }
                        } else {


                            if (!((Activity) context).isFinishing()) {
                                pDialog.dismiss();
                            }
                        }
                    } else {


                        try {
                            pDialog.dismiss();
                        } catch (final IllegalArgumentException e) {
                            e.printStackTrace();

                        } catch (final Exception e) {
                            e.printStackTrace();

                        }
                    }


                }

                try {

Log.d("log12",response.toString());
                            /*
                             * To start the activity where jwt token returned by the server along with the signup type is shown
                             */
                    if (response.getInt("code") == 200)
                    {
                        response = response.getJSONObject("data");
                        CouchDbController db =AppController.getInstance().getDbController();
                        Map<String, Object> map = new HashMap<>();
                        if (response.has("profilePic")) {

                            map.put("userImageUrl", response.getString("profilePic"));


                        } else {
                            map.put("userImageUrl", "");


                        }


                        map.put("userIdentifier", email);
                        map.put("userId", response.getString("userId"));
                        map.put("userName", response.getString("userName"));
                        map.put("apiToken", response.getString("token"));


                        if (!db.checkUserDocExists(AppController.getInstance().getIndexDocId(), response.getString("userId"))) {


                            String userDocId = db.createUserInformationDocument(map);

                            db.addToIndexDocument(AppController.getInstance().getIndexDocId(), response.getString("userId"), userDocId);


                        } else {


                            db.updateUserDetails(db.getUserDocId(response.getString("userId"), AppController.getInstance().getIndexDocId()), map);

                        }


                        db.updateIndexDocumentOnSignIn(AppController.getInstance().getIndexDocId(), response.getString("userId"));


                        /*
                         * To update myself as available for call
                         */


                        AppController.getInstance().setSignedIn(true, response.getString("userId"), response.getString("userName"), email);
                        AppController.getInstance().setSignStatusChanged(true);


                        Intent i = new Intent(LoginActivity.this, LandingPage.class);


                        startActivity(i);
                        supportFinishAfterTransition();
                    } else {


                        if (root != null) {
                            Snackbar snackbar = Snackbar.make(root, "Invalid email password combination", Snackbar.LENGTH_SHORT);


                            snackbar.show();
                            View view = snackbar.getView();
                            TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                            txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();


                    if (pDialog.isShowing()) {

                        //   pDialog.dismiss();
                        Context context = ((ContextWrapper) (pDialog).getContext()).getBaseContext();


                        if (context instanceof Activity) {


                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                if (!((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
                                    pDialog.dismiss();
                                }
                            } else {


                                if (!((Activity) context).isFinishing()) {
                                    pDialog.dismiss();
                                }
                            }
                        } else {


                            try {
                                pDialog.dismiss();
                            } catch (final IllegalArgumentException ef) {
                                ef.printStackTrace();

                            } catch (final Exception ef) {
                                ef.printStackTrace();

                            }
                        }


                    }

                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();


                if (pDialog.isShowing()) {

                    //  pDialog.dismiss();
                    Context context = ((ContextWrapper) (pDialog).getContext()).getBaseContext();


                    if (context instanceof Activity) {


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            if (!((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
                                pDialog.dismiss();
                            }
                        } else {


                            if (!((Activity) context).isFinishing()) {
                                pDialog.dismiss();
                            }
                        }
                    } else {


                        try {
                            pDialog.dismiss();
                        } catch (final IllegalArgumentException e) {
                            e.printStackTrace();

                        } catch (final Exception e) {
                            e.printStackTrace();

                        }
                    }


                }


                showErrorDialog();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization",
                        "KMajNKHPqGt6kXwUbFN3dU46PjThSNTtrEnPZUefdasdfghsaderf1234567890ghfghsdfghjfghjkswdefrtgyhdfghj");


                return headers;
            }
        };


        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
/* Add the request to the RequestQueue.*/
        AppController.getInstance().addToRequestQueue(jsonObjReq, "loginApi");
    }


}
