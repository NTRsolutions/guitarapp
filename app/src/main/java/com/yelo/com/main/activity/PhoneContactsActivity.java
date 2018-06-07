package com.yelo.com.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yelo.com.R;
import com.yelo.com.adapter.PhoneContactRvAdapter;
import com.yelo.com.fcm_push_notification.Config;
import com.yelo.com.fcm_push_notification.NotificationMessageDialog;
import com.yelo.com.fcm_push_notification.NotificationUtils;
import com.yelo.com.pojo_class.phone_contact_pojo.PhoneContactData;
import com.yelo.com.pojo_class.phone_contact_pojo.PhoneContactMainPojo;
import com.yelo.com.utility.ApiUrl;
import com.yelo.com.utility.CommonClass;
import com.yelo.com.utility.OkHttp3Connection;
import com.yelo.com.utility.RunTimePermission;
import com.yelo.com.utility.SessionManager;
import com.yelo.com.utility.VariableConstants;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import static android.Manifest.permission.READ_CONTACTS;

/**
 * <h>PhoneContactsActivity</h>
 * <p>
 *     In this class we used to find friends from our phone contects.
 * </p>
 * @since 04-Jul-17
 * @version 1.0
 * @author 3Embed
 */
public class PhoneContactsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG =PhoneContactsActivity.class.getSimpleName() ;
    private String[] permissionsArray;
    private RunTimePermission runTimePermission;
    private ProgressBar mProgress_bar;
    private SessionManager mSessionManager;
    private Activity mActivity;
    private RelativeLayout rL_rootElement,rL_friend_count;
    private TextView tV_friend_count;
    private ArrayList<PhoneContactData> arrayListContacts;
    private PhoneContactRvAdapter phoneContactRvAdapter;
    private NotificationMessageDialog mNotificationMessageDialog;
    RelativeLayout mRlNoPostFound;
    RecyclerView rV_phoneContacts;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_contact);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);

        initVariables();
    }

    /**
     * <h>initVariables</h>
     * <p>
     *     In this method we used to initialize all variables.
     * </p>
     */
    private void initVariables()
    {
        mActivity = PhoneContactsActivity.this;
        mNotificationMessageDialog=new NotificationMessageDialog(mActivity);
        mSessionManager=new SessionManager(mActivity);
        arrayListContacts=new ArrayList<>();
        CommonClass.statusBarColor(mActivity);

        // receiving datas from last class
        Intent intent=getIntent();
        int followingCount = intent.getIntExtra("followingCount", 0);
        System.out.println(TAG+" "+"followingCount="+ followingCount);

        // Initialize xml values
        rL_rootElement= (RelativeLayout) findViewById(R.id.rL_rootElement);
        rL_friend_count= (RelativeLayout) findViewById(R.id.rL_friend_count);
         rV_phoneContacts = (RecyclerView) findViewById(R.id.rV_phoneContacts);
        tV_friend_count= (TextView) findViewById(R.id.tV_friend_count);
        mProgress_bar= (ProgressBar) findViewById(R.id.progress_bar);
        mRlNoPostFound = findViewById( R.id.rL_noProductFound );

        // set status bar color
        CommonClass.statusBarColor(mActivity);
        permissionsArray=new String[]{READ_CONTACTS};
        runTimePermission=new RunTimePermission(mActivity,permissionsArray,true);

        // back button
        RelativeLayout rL_back_btn= (RelativeLayout) findViewById(R.id.rL_back_btn);
        rL_back_btn.setOnClickListener(this);

        // set recyclerview adapter
        phoneContactRvAdapter=new PhoneContactRvAdapter(mActivity,arrayListContacts, followingCount);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(mActivity);
        rV_phoneContacts.setLayoutManager(linearLayoutManager);
        rV_phoneContacts.setAdapter(phoneContactRvAdapter);

        // retrieve all contacts from phone
        if (runTimePermission.checkPermissions(permissionsArray))
        {
            getContactsIntoArrayList();
        }
        else runTimePermission.requestPermission();
    }


    @Override
    protected void onResume()
    {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mNotificationMessageDialog.mRegistrationBroadcastReceiver, new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mNotificationMessageDialog.mRegistrationBroadcastReceiver, new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mNotificationMessageDialog.mRegistrationBroadcastReceiver);
        super.onPause();
    }

    /**
     * <h>getContactsIntoArrayList</h>
     * <p>
     *     In this method we used to retrieve the all contacts from our phone memory.
     *     Once we get then save all contects into a string seperated by comma.
     * </p>
     */
    public void getContactsIntoArrayList()
    {
        String saveContact="";
        mProgress_bar.setVisibility(View.VISIBLE);
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext())
            {
                String phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                if (phonenumber!=null && !phonenumber.isEmpty())
                    saveContact=saveContact+","+phonenumber;
            }
            cursor.close();
        }

        if (saveContact.startsWith(","))
        {
            saveContact=saveContact.replaceFirst(",","");
        }

        if (!saveContact.isEmpty())
        {
            phoneContactsApi(saveContact);
        }
        System.out.println(TAG+" "+"save contact="+saveContact);
    }

    /**
     * <h>PhoneContactsApi</h>
     * <p>
     *     In this method we used to do api call to get complete infomation
     *     about a user who registered from this app.
     * </p>
     * @param contacts The String consisting the all contects.
     */
    private void phoneContactsApi(String contacts)
    {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("token", mSessionManager.getAuthToken());
                request_datas.put("contactNumbers", contacts);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.PHONE_CONTACTS, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    mProgress_bar.setVisibility(View.GONE);
                    System.out.println(TAG+" "+"phone contacts res="+result);

                    PhoneContactMainPojo phoneContactMainPojo;
                    Gson gson=new Gson();
                    phoneContactMainPojo =gson.fromJson(result,PhoneContactMainPojo.class);

                    switch (phoneContactMainPojo.getCode())
                    {
                        case "200" :
                            if (phoneContactMainPojo.getData()!=null && phoneContactMainPojo.getData().size()>0)
                            {
                                arrayListContacts.addAll(phoneContactMainPojo.getData());
                                mSessionManager.setContectFriendCount(arrayListContacts.size());

                                mRlNoPostFound.setVisibility( View.GONE );
                                rV_phoneContacts.setVisibility( View.VISIBLE );
                                // set count
                                rL_friend_count.setVisibility(View.VISIBLE);
                                String setCountText;
                                if (arrayListContacts.size()>1)
                                setCountText=arrayListContacts.size()+" "+getResources().getString(R.string.friends_on)+" "+getResources().getString(R.string.app_name);
                                else setCountText=arrayListContacts.size()+" "+getResources().getString(R.string.friend_on)+" "+getResources().getString(R.string.app_name);
                                tV_friend_count.setText(setCountText);

                                // notify adapter
                                phoneContactRvAdapter.notifyDataSetChanged();
                            }
                            break;

                        case "204":
                            mRlNoPostFound.setVisibility( View.VISIBLE );
                            rV_phoneContacts.setVisibility( View.GONE );
                            // set count
                            rL_friend_count.setVisibility(View.GONE);
                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    mProgress_bar.setVisibility(View.GONE);
                    CommonClass.showSnackbarMessage(rL_rootElement,error);
                }
            });
        }
        else CommonClass.showSnackbarMessage(rL_rootElement,getResources().getString(R.string.NoInternetAccess));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case VariableConstants.PERMISSION_REQUEST_CODE :
                System.out.println("grant result="+grantResults.length);
                if (grantResults.length>0)
                {
                    for (int count=0;count<grantResults.length;count++)
                    {
                        if (grantResults[count]!= PackageManager.PERMISSION_GRANTED)
                            runTimePermission.allowPermissionAlert(permissions[count]);

                    }
                    System.out.println("isAllPermissionGranted="+runTimePermission.checkPermissions(permissionsArray));
                    if (runTimePermission.checkPermissions(permissionsArray))
                    {
                        getContactsIntoArrayList();
                    }
                }
        }
    }

    @Override
    public void onBackPressed()
    {
        Intent intent=new Intent();
        intent.putExtra("contact_count",arrayListContacts.size());
        intent.putExtra("followingCount",phoneContactRvAdapter.followingCount);
        setResult(VariableConstants.CONTACT_FRIEND_REQ_CODE,intent);
        finish();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.rL_back_btn :
                onBackPressed();
                break;
        }
    }
}
