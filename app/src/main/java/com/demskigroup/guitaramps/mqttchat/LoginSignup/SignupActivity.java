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

public class SignupActivity extends AppCompatActivity {


    private RelativeLayout root;

    private EditText email, password, name;
    private TextInputLayout email_et_til, password_et_til, name_et_til;


    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {


        supportRequestWindowFeature(AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);


        setContentView(R.layout.signup_activity);


        root = (RelativeLayout) findViewById(R.id.root);


        TextView title = (TextView) findViewById(R.id.title);

//

        TextView tv2 = (TextView) findViewById(R.id.tv2);

        Typeface tf = AppController.getInstance().getRobotoCondensedFont();
        title.setTypeface(tf, Typeface.BOLD);

        tv2.setTypeface(tf, Typeface.BOLD);


        email = (EditText) findViewById(R.id.input_name2);

        name = (EditText) findViewById(R.id.input_name1);


        email.setText(getIntent().getExtras().getString("email"));
        password = (EditText) findViewById(R.id.input_name3);


        name_et_til = (TextInputLayout) findViewById(R.id.input_layout_name1);
        email_et_til = (TextInputLayout) findViewById(R.id.input_layout_name2);
        password_et_til = (TextInputLayout) findViewById(R.id.input_layout_name3);
        email.addTextChangedListener(new MyTextWatcher(email));
        password.addTextChangedListener(new MyTextWatcher(password));


        RelativeLayout login = (RelativeLayout) findViewById(R.id.createAccount);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


                if (validateEmail() && validatePassword() && validateName())

                {


                    hitApiOnServer(email.getText().toString().trim(), password.getText().toString().trim(), name.getText().toString());


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
                case R.id.input_name1:
                    validateName();
                    break;
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


    /**
     * Error dialog incase failed to signin user(If no user has matching login credentials)
     */
    @SuppressWarnings("TryWithIdenticalCatches")
    public void showErrorDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                SignupActivity.this);
        alertDialog.setTitle("Create Account");
        alertDialog.setMessage("Failed to create the account");

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
    private void hitApiOnServer(final String email, String password, final String name) {


        JSONObject obj = new JSONObject();

        try {
            obj.put("email", email);
            obj.put("password", password);

            obj.put("userName", name);



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
Log.d("log26",obj.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }


        final ProgressDialog pDialog = new ProgressDialog(SignupActivity.this, 0);


        pDialog.setCancelable(false);


        pDialog.setMessage("Creating account");
        pDialog.show();

        ProgressBar bar = (ProgressBar) pDialog.findViewById(android.R.id.progress);


        bar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(SignupActivity.this, R.color.color_black),
                android.graphics.PorterDuff.Mode.SRC_IN);


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                ApiOnServer.SIGNUP_API, obj, new Response.Listener<JSONObject>() {

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


                            /*
                             * To start the activity where jwt token returned by the server along with the signup type is shown
                             */
                    if (response.getInt("code") == 200) {


                        response = response.getJSONObject("data");

                        CouchDbController db =AppController.getInstance().getDbController();

                        Map<String, Object> map = new HashMap<>();


                        map.put("userIdentifier", email);
                        map.put("userId", response.getString("userId"));
                        map.put("userName", name);
                        map.put("apiToken", response.getString("token"));


                        if (response.has("profilePic")) {

                            map.put("userImageUrl", response.getString("profilePic"));


                        } else {
                            map.put("userImageUrl", "");


                        }


                        if (!db.checkUserDocExists(AppController.getInstance().getIndexDocId(), response.getString("userId"))) {


                            String userDocId = db.createUserInformationDocument(map);

                            db.addToIndexDocument(AppController.getInstance().getIndexDocId(), response.getString("userId"), userDocId);


                        } else {


                            db.updateUserDetails(db.getUserDocId(response.getString("userId"), AppController.getInstance().getIndexDocId()), map);

                        }


                        db.updateIndexDocumentOnSignIn(AppController.getInstance().getIndexDocId(), response.getString("userId"));


                        AppController.getInstance().setSignedIn(true, response.getString("userId"), name, email);
                        AppController.getInstance().setSignStatusChanged(true);


                        Intent i = new Intent(SignupActivity.this, LandingPage.class);


                        startActivity(i);
                        supportFinishAfterTransition();
                    } else if (response.getInt("code") == 400) {


                        if (root != null) {
                            Snackbar snackbar = Snackbar.make(root, "Email already exists", Snackbar.LENGTH_SHORT);


                            snackbar.show();
                            View view = snackbar.getView();
                            TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                            txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                        }
                    } else {


                        if (root != null) {
                            Snackbar snackbar = Snackbar.make(root, "Failed to create the user", Snackbar.LENGTH_SHORT);


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

                //  requesting = false;
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // requesting = false;
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
                headers.put("Authorization", "KMajNKHPqGt6kXwUbFN3dU46PjThSNTtrEnPZUefdasdfghsaderf1234567890ghfghsdfghjfghjkswdefrtgyhdfghj");


                return headers;
            }
        };


        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
/* Add the request to the RequestQueue.*/
        AppController.getInstance().addToRequestQueue(jsonObjReq, "signupApi");
    }

    private boolean validateName() {
        if (name.getText().toString().trim().isEmpty()) {
            name_et_til.setError(getString(R.string.name_empty));
            requestFocus(name);
            return false;
        } else {
            name_et_til.setErrorEnabled(false);
        }

        return true;
    }


}
