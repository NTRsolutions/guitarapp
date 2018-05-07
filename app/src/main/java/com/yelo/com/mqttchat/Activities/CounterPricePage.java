package com.yelo.com.mqttchat.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.yelo.com.R;
import com.yelo.com.fcm_push_notification.Config;
import com.yelo.com.fcm_push_notification.NotificationMessageDialog;
import com.yelo.com.fcm_push_notification.NotificationUtils;
import com.yelo.com.get_current_location.FusedLocationReceiver;
import com.yelo.com.get_current_location.FusedLocationService;
import com.yelo.com.mqttchat.AppController;
import com.yelo.com.mqttchat.Utilities.MqttEvents;
import com.yelo.com.mqttchat.Utilities.Utilities;
import com.yelo.com.utility.ApiUrl;
import com.yelo.com.utility.CommonClass;
import com.yelo.com.utility.OkHttp3Connection;
import com.yelo.com.utility.RunTimePermission;
import com.yelo.com.utility.SessionManager;
import com.yelo.com.utility.VariableConstants;
import org.json.JSONObject;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class CounterPricePage extends AppCompatActivity implements View.OnClickListener
{
    private ProgressBar progress_bar_save;
    private Activity mActivity;
    private FusedLocationService locationService;
    private String[] permissionsArray;
    private RunTimePermission runTimePermission;
    private String latitude="",longitude="";
    private TextView tV_distance;
    private NotificationMessageDialog mNotificationMessageDialog;
    private RelativeLayout parentView;
    private SessionManager sessionManager;
    private String postId="";
    private String member_name="";
    private String receiverMqttId ="";
    private EditText tV_price;
    private RelativeLayout makeoffer;
    private String productName;
    private String profilePicUrl;
    private String currency;
    private String price;
    private String memberPicUrl;
    private Boolean isSeller;
    private TextView notNegotiableTitle;
    private TextView currency_tv;
    private String counterPrice;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter_price_page);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        sessionManager=new SessionManager(this);
        initVariable();
    }

    /**
     * <h>InitVariable</h>
     * <p>
     *     In this method we used to initialize all variables.
     * </p>
     */
    private void initVariable()
    {
        mActivity=CounterPricePage.this;
        Intent intent=getIntent();
        Bundle  data=intent.getExtras();
        profilePicUrl= data.getString("profilePicUrl");
        productName= data.getString("productName");
        String place= data.getString("place");
        postId= data.getString("postId");
        latitude= data.getString("latitude");
        longitude= data.getString("longitude");
        String currency_text= data.getString("currency");
       /* if(currency_text!=null)
        {
            currency=  CommonClass.getCurrencySymbol(currency_text);
        }*/
        if(currency==null)
        {
            currency=currency_text;
        }
        price= data.getString("price");
        memberPicUrl=data.getString("memberPicUrl");
        member_name=data.getString("membername");
        receiverMqttId=data.getString("receiverMqttId");
        String negotiable=data.getString("negotiable");
        String counter_text= data.getString("counterPrice");
        if(counter_text!=null)
        {
            counterPrice=counter_text.replaceAll("\\D+","");
        }
        if(counterPrice==null)
        {
            counterPrice=counter_text;
        }
        isSeller=data.getBoolean("isSeller");
        mNotificationMessageDialog=new NotificationMessageDialog(mActivity);
        CommonClass.statusBarColor(mActivity);
        tV_distance= (TextView) findViewById(R.id.tV_distance);
        permissionsArray = new String[]{ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION};
        runTimePermission = new RunTimePermission(mActivity, permissionsArray,false);
        if (runTimePermission.checkPermissions(permissionsArray))
        {
            getCurrentLocation();
        } else
        {
            runTimePermission.requestPermission();
        }

        RelativeLayout rL_back_btn = (RelativeLayout) findViewById(R.id.rL_back_btn);
        rL_back_btn.setOnClickListener(this);
        // xml Variables
        notNegotiableTitle=(TextView)findViewById(R.id.notNegotiableTitle);
        ImageView iV_productImage= (ImageView) findViewById(R.id.iV_productImage);
        TextView tV_productname,tV_location,tV_asking_price;
        tV_productname= (TextView) findViewById(R.id.tV_productname);
        tV_location= (TextView) findViewById(R.id.tV_location);
        tV_asking_price= (TextView) findViewById(R.id.tV_asking_price);
        tV_price= (EditText) findViewById(R.id.tV_price);
        currency_tv=(TextView)findViewById(R.id.currency);
        progress_bar_save=(ProgressBar)findViewById(R.id.progress_bar_save);
        makeoffer=(RelativeLayout)findViewById(R.id.make_offer);
        makeoffer.setOnClickListener(this);
        parentView =(RelativeLayout)findViewById(R.id.poarentView);
        // set product image
        iV_productImage.getLayoutParams().width= CommonClass.getDeviceWidth(mActivity)/5;
        iV_productImage.getLayoutParams().height= CommonClass.getDeviceWidth(mActivity)/5;
        if (profilePicUrl!=null && !profilePicUrl.isEmpty())
            Picasso.with(mActivity)
                    .load(profilePicUrl)
                    .placeholder(R.drawable.default_image)
                    .error(R.drawable.default_image)
                    .into(iV_productImage);
        if (productName!=null)
            tV_productname.setText(productName);
        if (place!=null)
            tV_location.setText(place);
        if (price!=null)
        {
            String askingPrice;
            if(currency!=null&&!currency.isEmpty())
            {
                askingPrice=getResources().getString(R.string.asking_price)+" "+currency+" "+counterPrice;
            }else
            {
                askingPrice=getResources().getString(R.string.asking_price)+" "+counterPrice;
            }
            tV_asking_price.setText(askingPrice);
            if(negotiable !=null&& negotiable.equals("1"))
            {
                notNegotiableTitle.setVisibility(View.GONE);
                tV_price.setEnabled(true);
            }else
            {
                notNegotiableTitle.setVisibility(View.VISIBLE);
                tV_price.setEnabled(false);
            }
            String askPrice= price.replaceAll("\\D+","");
            tV_price.setText(askPrice);
        }
        if(currency!=null&&!currency.isEmpty())
        {
            currency_tv.setText(currency);
        }
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
     * <h>GetDistance</h>
     * <p>
     *     In this method we used to find the distance of the product from my current location.
     * </p>
     * @param lat1 The cureent lat
     * @param lon1 The current lng
     * @param lat2 The product lat
     * @param lon2 The product lng
     * @return The double value of distance
     */
    private double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    /**
     * In this method we find current location using FusedLocationApi.
     * in this we have onUpdateLocation() method in which we check if
     * its not null then We call guest user api.
     */
    private void getCurrentLocation()
    {
        locationService=new FusedLocationService(mActivity, new FusedLocationReceiver() {
            @Override
            public void onUpdateLocation() {
                Location currentLocation=locationService.receiveLocation();
                if (currentLocation!=null)
                {
                    String lat,lng;
                    lat=String.valueOf(currentLocation.getLatitude());
                    lng=String.valueOf(currentLocation.getLongitude());

                    if (isLocationFound(lat,lng) && isLocationFound(latitude,longitude))
                    {
                        double givenLat=Double.parseDouble(latitude);
                        double givenLong=Double.parseDouble(longitude);
                        double distance= getDistance(currentLocation.getLatitude(),currentLocation.getLongitude(),givenLat,givenLong);
                        String setDistance=CommonClass.twoDigitAfterDewcimal(String.valueOf(distance))+" "+getResources().getString(R.string.km)+" "+getResources().getString(R.string.away);
                        tV_distance.setText(setDistance);
                    }
                }
            }
        }
        );
    }

    /**
     * In this method we used to check whether current lat and
     * long has been received or not.
     * @param lat The current latitude
     * @param lng The current longitude
     * @return boolean flag true or false
     */
    private boolean isLocationFound(String lat,String lng) {
        return !(lat == null || lat.isEmpty()) && !(lng == null || lng.isEmpty());
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
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
            case R.id.rL_back_btn :
                onBackPressed();
                break;
            case R.id.make_offer:
                makeOffer();
                break;
        }
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
                        getCurrentLocation();
                    }
                }
        }
    }

    /*
     *Make offer api */
    private  JSONObject requestDats;
    private void makeOffer()
    {
        String digivalue;
        String priceValue=tV_price.getText().toString();
        if(!priceValue.isEmpty())
        {
            priceValue = priceValue.replaceAll("[^a-zA-Z0-9 .,]|(?<!\\d)[.,]|[.,](?!\\d)", "");
            Log.d("amountvalue",""+priceValue);
            digivalue=priceValue.trim();
            if(digivalue.length()<1)
            {
                return;
            }
        }else
        {
            return;
        }
        if (CommonClass.isNetworkAvailable(mActivity))
        {
            handelButton(true);
             requestDats = new JSONObject();
            try {
                requestDats.put("token",sessionManager.getAuthToken());
                requestDats.put("offerStatus","3");
                requestDats.put("postId",postId);
                requestDats.put("price",digivalue);
                requestDats.put("type","0");
                if(isSeller)
                {
                    requestDats.put("membername",sessionManager.getUserName());
                    requestDats.put("buyer",member_name);
                }else
                {
                    requestDats.put("membername",member_name);
                }
                JSONObject jsonObject=createMessageObject(digivalue);
                if(jsonObject==null)
                {
                    return;
                }
                requestDats.put("sendchat",jsonObject);
            } catch (Exception e)
            {
                e.printStackTrace();
                handelButton(false);
            }

            OkHttp3Connection.doOkHttp3Connection("", ApiUrl.MAKE_OFFER,OkHttp3Connection.Request_type.POST, requestDats, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag)
                {
                    handelButton(false);
                    try
                    {
                        handelResponse(result);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onError(String error, String user_tag)
                {
                    handelButton(false);
                    CommonClass.showSnackbarMessage(parentView,error);
                }
            });

        }else
            CommonClass.showSnackbarMessage(parentView,getResources().getString(R.string.NoInternetAccess));
    }


    /*
    * Handling the response data.*/
    private void handelResponse(String resaponse) throws Exception
    {
        JSONObject jsonObject = new JSONObject(resaponse);
        String code_Data = jsonObject.getString("code");
        switch (code_Data)
        {
            // success
            case "200":
                AppController.getInstance().sendMessageToFcm(MqttEvents. OfferMessage+"/"+requestDats.getJSONObject("sendchat").getString("to"),requestDats.getJSONObject("sendchat"));
                mActivity.finish();
                break;
            // auth token expired
            case "401":
                CommonClass.sessionExpired(mActivity);
                break;
            case "409":
                CommonClass.showTopSnackBar(parentView,getString(R.string.already_sold_Text));
                break;
            default:
                break;
        }
    }

    /*
    * Handel make offer button data.*/
    private void handelButton(boolean isEnable)
    {
        if(isEnable)
        {
            progress_bar_save.setVisibility(View.VISIBLE);
            makeoffer.setEnabled(false);
        }else
        {
            makeoffer.setEnabled(true);
            progress_bar_save.setVisibility(View.GONE);
        }
    }

    private JSONObject createMessageObject(String amount) throws Exception
    {
        String doucumentId = AppController.getInstance().findDocumentIdOfReceiver(receiverMqttId, postId);
        Log.d("log88",""+amount+" "+receiverMqttId+" "+postId);
        if(doucumentId.isEmpty())
        {
            Log.d("log88","Error on Document id");
            return null;
        }
        byte[] byteArray = amount.getBytes("UTF-8");
        String messageInbase64 = Base64.encodeToString(byteArray, Base64.DEFAULT).trim();
        String tsForServer = Utilities.tsInGmt();
        String  tsForServerEpoch = new Utilities().gmtToEpoch(tsForServer);
        JSONObject messageData=new JSONObject();
        messageData.put("name", sessionManager.getUserName());
        messageData.put("from",sessionManager.getmqttId());
        messageData.put("to",receiverMqttId);
        messageData.put("payload",messageInbase64 );
        messageData.put("type","15");
        messageData.put("offerType","3");
        messageData.put( "id",tsForServerEpoch);
        messageData.put("secretId",postId);
        messageData.put("isSold","0");
        messageData.put("thumbnail","");
        messageData.put("userImage",sessionManager.getUserImage());
        messageData.put("toDocId", doucumentId);
        messageData.put("dataSize",1);
        messageData.put("productImage",profilePicUrl);
        messageData.put("productId",postId);
        messageData.put("productName",productName);
        messageData.put("productPrice",""+price);
        return messageData;
    }
}