package com.yelo.com.mqttchat.LoginSignup;

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
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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
import com.yelo.com.R;
import com.yelo.com.mqttchat.AppController;
import com.yelo.com.mqttchat.Utilities.ApiOnServer;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by moda on 07/07/17.
 */

public class CheckEmail extends AppCompatActivity {

    private EditText email;
    private TextInputLayout email_et_til;

    private RelativeLayout root;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        supportRequestWindowFeature(AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);


        setContentView(R.layout.check_email);

        TextView title = (TextView) findViewById(R.id.title);


        Typeface tf = AppController.getInstance().getRobotoCondensedFont();
        title.setTypeface(tf, Typeface.BOLD);
        root = (RelativeLayout) findViewById(R.id.root);

        email = (EditText) findViewById(R.id.input_name2);

        email_et_til = (TextInputLayout) findViewById(R.id.input_layout_name2);

        email.addTextChangedListener(new MyTextWatcher(email));


        Button login = (Button) findViewById(R.id.button);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


                if (validateEmail())

                {


                    //  handleLoginWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim());


                    hitApiOnServer(email.getText().toString().trim());


                } else if (!validateEmail()) {

                    if (root != null) {
                        Snackbar snackbar = Snackbar.make(root, "Invalid email", Snackbar.LENGTH_SHORT);


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

            }
        }
    }

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


    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

@SuppressWarnings("TryWithIdenticalCatches")
    private void hitApiOnServer(final String email) {


        JSONObject obj = new JSONObject();

        try {
            obj.put("email", email);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        final ProgressDialog pDialog = new ProgressDialog(CheckEmail.this, 0);


        pDialog.setCancelable(false);

//        pDialog.setTitle(R.string.string_351);

        pDialog.setMessage("Verifying Email");
        pDialog.show();

        ProgressBar bar = (ProgressBar) pDialog.findViewById(android.R.id.progress);


        bar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(CheckEmail.this, R.color.color_black),
                android.graphics.PorterDuff.Mode.SRC_IN);


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                ApiOnServer.VERIFY_API, obj, new Response.Listener<JSONObject>() {

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


                        Intent i = new Intent(CheckEmail.this, LoginActivity.class);

                        i.putExtra("email", email);
                        startActivity(i);
                        supportFinishAfterTransition();
                    } else {


                        Intent i = new Intent(CheckEmail.this, SignupActivity.class);

                        i.putExtra("email", email);
                        startActivity(i);
                        supportFinishAfterTransition();
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
        AppController.getInstance().addToRequestQueue(jsonObjReq, "checkEmailApi");
    }

    /**
     * Error dialog incase failed to signin user(If no user has matching login credentials)
     */
    @SuppressWarnings("TryWithIdenticalCatches")
    public void showErrorDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                CheckEmail.this);
        alertDialog.setTitle("Verify Email");
        alertDialog.setMessage("Failed to verify the email");

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


}
