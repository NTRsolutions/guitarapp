package com.demskigroup.guitaramps.mqttchat.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.mqttchat.Adapters.SelectUserAdapter;
import com.demskigroup.guitaramps.mqttchat.AppController;
import com.demskigroup.guitaramps.mqttchat.ModelClasses.SelectUserItem;
import com.demskigroup.guitaramps.mqttchat.Utilities.ApiOnServer;
import com.demskigroup.guitaramps.mqttchat.Utilities.CustomLinearLayoutManager;
import com.demskigroup.guitaramps.mqttchat.Utilities.RecyclerItemClickListener;
import com.demskigroup.guitaramps.mqttchat.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by moda on 19/06/17.
 */

public class SelectUsersActivity extends AppCompatActivity {


    private SelectUserAdapter mAdapter;


    private ArrayList<SelectUserItem> mUserData = new ArrayList<>();


    private RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.users_list);


        RecyclerView recyclerViewUsers = (RecyclerView) findViewById(R.id.list_of_users);
        recyclerViewUsers.setHasFixedSize(true);
        mAdapter = new SelectUserAdapter(SelectUsersActivity.this, mUserData);
        recyclerViewUsers.setItemAnimator(new DefaultItemAnimator());
        recyclerViewUsers.setLayoutManager(new CustomLinearLayoutManager(SelectUsersActivity.this, LinearLayoutManager.VERTICAL, false));
        recyclerViewUsers.setAdapter(mAdapter);


        root = (RelativeLayout) findViewById(R.id.root);

        fetchUsers();

        recyclerViewUsers.addOnItemTouchListener(new RecyclerItemClickListener(SelectUsersActivity.this, recyclerViewUsers, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {


                if (position >= 0) {


                    /*
                     * To add a conversation,we add a members nested collection
                     */


                    SelectUserItem item = mUserData.get(position);


                    Intent i = new Intent(SelectUsersActivity.this, ChatMessageScreen.class);
                    i.putExtra("isNew",false);
                    i.putExtra("receiverIdentifier", item.getUserIdentifier());
                    i.putExtra("receiverUid", item.getUserId());
                    i.putExtra("receiverImage", item.getUserImage());
                    i.putExtra("receiverName", item.getUserName());
                    i.putExtra("secretChatInitiated", true);

                    String secretId = AppController.getInstance().randomString();
                    String docId = AppController.getInstance().findDocumentIdOfReceiver(item.getUserId(), secretId);


                    if (docId.isEmpty()) {
                        docId = AppController.findDocumentIdOfReceiver(item.getUserId(), Utilities.tsInGmt(), item.getUserName(),
                                item.getUserImage(), secretId, false, item.getUserIdentifier(), "","","","",false,false);
                    }
                    i.putExtra("documentId", docId);
                    i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(i);


                }


            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));


        ImageView close = (ImageView) findViewById(R.id.close);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }

    @SuppressWarnings("TryWithIdenticalCatches")
    private void fetchUsers() {
        JSONObject obj = new JSONObject();


        final ProgressDialog pDialog = new ProgressDialog(SelectUsersActivity.this, 0);


        pDialog.setCancelable(false);

//        pDialog.setTitle(R.string.string_351);

        pDialog.setMessage(getString(R.string.FetchingUsers));
        pDialog.show();

        ProgressBar bar = (ProgressBar) pDialog.findViewById(android.R.id.progress);


        bar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(SelectUsersActivity.this, R.color.color_black),
                android.graphics.PorterDuff.Mode.SRC_IN);


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                ApiOnServer.GET_USERS_API, obj, new Response.Listener<JSONObject>() {

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


                        JSONArray arr = response.getJSONArray("data");

                        SelectUserItem item;

                        JSONObject objTemp;

Log.d("log24",response.toString());
                        for (int i = 0; i < arr.length(); i++) {
                            try {

                                objTemp = arr.getJSONObject(i);

                                item = new SelectUserItem();

                                item.setUserId(objTemp.getString("_id"));





/*
 * Have to avoid current user
 */
                                if (!(objTemp.getString("_id")).equals(AppController.getInstance().getUserId())) {
                                    item.setUserIdentifier(objTemp.getString("email"));
                                    item.setUserName(objTemp.getString("userName"));
                                    if (objTemp.has("profilePic") && objTemp.getString("profilePic") != null &&
                                            !objTemp.getString("profilePic").equals("null") &&
                                            !objTemp.getString("profilePic").isEmpty()) {


                                        item.setUserImage(objTemp.getString("profilePic"));
                                    } else {
                                        item.setUserImage("");
                                    }
                                    mUserData.add(item);


                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                mAdapter.notifyDataSetChanged();

                            }
                        });


                    } else {


                        if (root != null) {
                            Snackbar snackbar = Snackbar.make(root, getString(R.string.FetchFailed), Snackbar.LENGTH_SHORT);


                            snackbar.show();
                            View view = snackbar.getView();
                            TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                            txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                        }
                    }
                } catch (
                        JSONException e)

                {
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
        }, new Response.ErrorListener()

        {

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
        });


        jsonObjReq.setRetryPolicy(new

                DefaultRetryPolicy(
                20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
/* Add the request to the RequestQueue.*/
        AppController.getInstance().

                addToRequestQueue(jsonObjReq, "fetchUsersApi");


    }


    /**
     * Error dialog incase failed to signin user(If no user has matching login credentials)
     */
    @SuppressWarnings("TryWithIdenticalCatches")
    public void showErrorDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                SelectUsersActivity.this);
        alertDialog.setTitle(getString(R.string.FetchUsers));
        alertDialog.setMessage(getString(R.string.FetchFailed));

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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mUserData.clear();


        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mAdapter.notifyDataSetChanged();

            }
        });
        fetchUsers();
    }
}