package com.yelo.com.mqttchat.Fragments;


import android.app.Activity;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.yelo.com.mqttchat.Activities.ChatProductDetails;
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
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatMakeOfferfrg#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatMakeOfferfrg extends Fragment implements View.OnClickListener
{
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM1 = "param1";
    private Bundle data;
    private String mParam2;
    private ProgressBar progress_bar_save;
    private Activity mActivity;
    private FusedLocationService locationService;
    private String[] permissionsArray;
    private RunTimePermission runTimePermission;
    private String latitude = "", longitude = "";
    private TextView tV_distance;
    private NotificationMessageDialog mNotificationMessageDialog;
    private RelativeLayout parentView;
    private SessionManager sessionManager;
    private String postId = "";
    private String member_name = "";
    private String receiverMqttId = "";
    private EditText tV_price;
    private RelativeLayout makeoffer;
    private String productName;
    private String offerType = "3";
    private String productPicUrl;
    private String currency;
    private String price;
    private String memberPicUrl;
    private ChatProductDetails chatProductDetails;


    public ChatMakeOfferfrg() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatMakeOfferfrg.
     */
    public static ChatMakeOfferfrg newInstance(Bundle param1, String param2)
    {
        ChatMakeOfferfrg fragment = new ChatMakeOfferfrg();
        Bundle args = new Bundle();
        args.putBundle(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(getActivity());
         chatProductDetails= (ChatProductDetails) getActivity();
        if (getArguments() != null) {
            data = getArguments().getBundle(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_make_offerfrg, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initVariable(view);
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * <h>InitVariable</h>
     * <p>
     * In this method we used to initialize all variables.
     * </p>
     */
    private void initVariable(View view) {
        mActivity = getActivity();
        // Receiving datas from last activity
        String place;
        productPicUrl = data.getString("productPicUrl");
        productName = data.getString("productName");
        place = data.getString("place");
        postId = data.getString("postId");
        latitude = data.getString("latitude");
        longitude = data.getString("longitude");
        currency = data.getString("currency");
        price = data.getString("price");
        memberPicUrl = data.getString("memberPicUrl");
        member_name = data.getString("membername");
        receiverMqttId = data.getString("receiverMqttId");
        String negotiable = data.getString("negotiable");
        mNotificationMessageDialog = new NotificationMessageDialog(mActivity);
        CommonClass.statusBarColor(mActivity);
        // get current location
        tV_distance = (TextView) view.findViewById(R.id.tV_distance);
        permissionsArray = new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION};
        runTimePermission = new RunTimePermission(mActivity, permissionsArray, false);
        if (runTimePermission.checkPermissions(permissionsArray)) {
            getCurrentLocation();
        } else {
            runTimePermission.requestPermission();
        }

        RelativeLayout rL_back_btn = (RelativeLayout) view.findViewById(R.id.rL_back_btn);
        rL_back_btn.setOnClickListener(this);
        // xml Variables
        ImageView iV_productImage = (ImageView) view.findViewById(R.id.iV_productImage);
        TextView tV_productname, tV_location, tV_asking_price;
        tV_location = (TextView) view.findViewById(R.id.tV_location);
        tV_productname = (TextView) view.findViewById(R.id.tV_productname);
        tV_asking_price = (TextView) view.findViewById(R.id.tV_asking_price);
        tV_price = (EditText) view.findViewById(R.id.tV_price);
        progress_bar_save = (ProgressBar) view.findViewById(R.id.progress_bar_save);
        makeoffer = (RelativeLayout) view.findViewById(R.id.make_offer);
        makeoffer.setOnClickListener(this);
        parentView = (RelativeLayout) view.findViewById(R.id.poarentView);
        parentView.setOnClickListener(this);
        // set product image
        iV_productImage.getLayoutParams().width = CommonClass.getDeviceWidth(mActivity) / 5;
        iV_productImage.getLayoutParams().height = CommonClass.getDeviceWidth(mActivity) / 5;

        if (productPicUrl != null && !productPicUrl.isEmpty())
            Picasso.with(mActivity)
                    .load(productPicUrl)
                    .placeholder(R.drawable.default_image)
                    .error(R.drawable.default_image)
                    .into(iV_productImage);

        // product name
        if (productName != null)
            tV_productname.setText(productName);

        // location
        if (place != null)
            tV_location.setText(place);

        // set asking price
        if (price != null) {
            String askingPrice;
            if (currency != null && !currency.isEmpty()) {
                askingPrice = getResources().getString(R.string.asking_price) + " " + currency + price;
            } else {
                askingPrice = getResources().getString(R.string.asking_price) + " " + price;
            }

            tV_asking_price.setText(askingPrice);

            if (negotiable != null && negotiable.equals("1")) {
                tV_price.setEnabled(false);
            } else {
                tV_price.setEnabled(true);
            }
            if (currency != null && !currency.isEmpty()) {
                price = currency + " " + price;
            }
            tV_price.setText(price);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mNotificationMessageDialog.mRegistrationBroadcastReceiver, new IntentFilter(Config.REGISTRATION_COMPLETE));
        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mNotificationMessageDialog.mRegistrationBroadcastReceiver, new IntentFilter(Config.PUSH_NOTIFICATION));
        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getActivity().getApplicationContext());
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mNotificationMessageDialog.mRegistrationBroadcastReceiver);
        super.onPause();
    }

    /**
     * <h>GetDistance</h>
     * <p>
     * In this method we used to find the distance of the product from my current location.
     * </p>
     *
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
    private void getCurrentLocation() {
        locationService = new FusedLocationService(mActivity, new FusedLocationReceiver() {
            @Override
            public void onUpdateLocation() {
                Location currentLocation = locationService.receiveLocation();
                if (currentLocation != null) {
                    String lat, lng;
                    lat = String.valueOf(currentLocation.getLatitude());
                    lng = String.valueOf(currentLocation.getLongitude());

                    if (isLocationFound(lat, lng) && isLocationFound(latitude, longitude)) {
                        double givenLat = Double.parseDouble(latitude);
                        double givenLong = Double.parseDouble(longitude);
                        double distance = getDistance(currentLocation.getLatitude(), currentLocation.getLongitude(), givenLat, givenLong);
                        String setDistance = CommonClass.twoDigitAfterDewcimal(String.valueOf(distance)) + " " + getResources().getString(R.string.km) + " " + getResources().getString(R.string.away);
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
     *
     * @param lat The current latitude
     * @param lng The current longitude
     * @return boolean flag true or false
     */
    private boolean isLocationFound(String lat, String lng) {
        return !(lat == null || lat.isEmpty()) && !(lng == null || lng.isEmpty());
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.rL_back_btn:
                chatProductDetails.onBackPressed();
                break;
            case R.id.make_offer:
                makeOffer();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case VariableConstants.PERMISSION_REQUEST_CODE:
                System.out.println("grant result=" + grantResults.length);
                if (grantResults.length > 0) {
                    for (int count = 0; count < grantResults.length; count++) {
                        if (grantResults[count] != PackageManager.PERMISSION_GRANTED)
                            runTimePermission.allowPermissionAlert(permissions[count]);

                    }
                    System.out.println("isAllPermissionGranted=" + runTimePermission.checkPermissions(permissionsArray));
                    if (runTimePermission.checkPermissions(permissionsArray)) {
                        getCurrentLocation();
                    }
                }
        }
    }

    /*
    *Make offer api */
    private JSONObject requestDats;
    private void makeOffer()
    {
        if(receiverMqttId==null||receiverMqttId.isEmpty())
        {
            Log.d("mqttId"," no Mqtt id is there");
            return;
        }
        String digivalue;
        String priceValue=tV_price.getText().toString();
        if(!priceValue.isEmpty())
        {
            priceValue = priceValue.replaceAll("\\D+","");
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
                requestDats.put("offerStatus","1");
                requestDats.put("postId",postId);
                requestDats.put("price",digivalue);
                requestDats.put("type","0");
                requestDats.put("membername",member_name);
                requestDats.put("sendchat",createMessageObject(digivalue));
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
        switch (code_Data) {
            // success
            case "200":
                AppController.getInstance().sendMessageToFcm(MqttEvents. OfferMessage+"/"+requestDats.getJSONObject("sendchat").getString("to"),requestDats.getJSONObject("sendchat"));
                mActivity.finish();
                break;
            // auth token expired
            case "401":
                CommonClass.sessionExpired(mActivity);
                break;
            // error
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

    /*
     *creating the msg object */
    private String doucumentId;
    private JSONObject createMessageObject(String amount) throws Exception
    {
        doucumentId = AppController.getInstance().findDocumentIdOfReceiver(receiverMqttId,postId);
        if (doucumentId.isEmpty())
        {
            offerType="1";
            doucumentId =AppController.findDocumentIdOfReceiver(receiverMqttId, Utilities.tsInGmt(),member_name,memberPicUrl,postId,false,"","", productPicUrl,productName,price,false,false);
        }else
        {
            offerType="3";
        }
        Log.d("log88",""+amount);
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
        messageData.put("offerType",offerType);
        messageData.put( "id",tsForServerEpoch);
        messageData.put("secretId",postId);
        messageData.put("thumbnail","");
        messageData.put("userImage",sessionManager.getUserImage());
        messageData.put("toDocId",doucumentId);
        messageData.put("dataSize",1);
        messageData.put("isSold","0");
        messageData.put("productImage", productPicUrl);
        messageData.put("productId",postId);
        messageData.put("productName",productName);
        messageData.put("productPrice",""+price);
        return messageData;
    }

}
