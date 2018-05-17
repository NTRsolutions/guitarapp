package com.yelo.com.mqttchat;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.RingtoneManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.couchbase.lite.android.AndroidContext;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;
import com.yelo.com.R;
import com.yelo.com.Twiter_manager.TweetManger;
import com.yelo.com.mqttchat.Activities.ChatMessageScreen;
import com.yelo.com.mqttchat.AppStateChange.AppStateListener;
import com.yelo.com.mqttchat.AppStateChange.AppStateMonitor;
import com.yelo.com.mqttchat.AppStateChange.RxAppStateMonitor;
import com.yelo.com.mqttchat.Database.CouchDbController;
import com.yelo.com.mqttchat.DownloadFile.FileUploadService;
import com.yelo.com.mqttchat.DownloadFile.FileUtils;
import com.yelo.com.mqttchat.DownloadFile.ServiceGenerator;
import com.yelo.com.mqttchat.Service.AppKilled;
import com.yelo.com.mqttchat.Utilities.ApiOnServer;
import com.yelo.com.mqttchat.Utilities.DeviceUuidFactory;
import com.yelo.com.mqttchat.Utilities.MqttEvents;
import com.yelo.com.mqttchat.Utilities.Utilities;
import com.yelo.com.utility.SessionManager;
import com.yelo.com.utility.VariableConstants;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import io.fabric.sdk.android.Fabric;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
/**
 * <h2>AppController</h2>
 * <P>
 *
 * </P>
 *@since   29/06/17.
 */
public class AppController extends android.support.multidex.MultiDexApplication implements Application.ActivityLifecycleCallbacks {

    public static final String TAG = AppController.class.getSimpleName();
    public static Bus bus = new Bus(ThreadEnforcer.ANY);
    private static AppController mInstance;
    private SharedPreferences sharedPref;
    private boolean foreground;
    private String chatDocId, unsentMessageDocId, mqttTokenDocId, notificationDocId,
            userName, userId, userImageUrl, userIdentifier;
    private String indexDocId;
    private CouchDbController db;
    private boolean signStatusChanged = false;
    private boolean signedIn = false;
    private String activeReceiverId = "";
    private RequestQueue mRequestQueue;
    private ArrayList<String> colors;
    private MqttAndroidClient mqttAndroidClient;
    private MqttConnectOptions mqttConnectOptions;
    private boolean flag = true;
    private ArrayList<HashMap<String, Object>> tokenMapping = new ArrayList<>();
    private HashSet<IMqttDeliveryToken> set = new HashSet<>();
    private boolean applicationKilled;
    private Typeface tf_robotoCondensed, tf_robotoRegular, tf_robotoMedium;
    private long timeDelta = 0;
    private String activeSecretId = "";
    private String deviceId;
    public static Bus getBus() {
        return bus;
    }
    public static synchronized AppController getInstance() {
        return mInstance;
    }
    private boolean chatSynced=true;
    private static final int NOTIFICATION_SIZE = 5;
    private ArrayList<Map<String, Object>> notifications = new ArrayList<>();
    private String apiToken;
    SessionManager mSessionManager;

    public static Context AppContext;

    @Override
    public void onCreate()
    {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        TweetManger.initialization(this, VariableConstants.TWITTER_KEY,VariableConstants.TWITTER_SECRET);
        mInstance = this;
        AppStateMonitor appStateMonitor = RxAppStateMonitor.create(this);
        deviceId = DeviceUuidFactory.getInstance().getDeviceUuid(this);
        appStateMonitor.addListener(new AppStateListener() {
            @Override
            public void onAppDidEnterForeground()
            {
                updatePresence(1, false);
                foreground = true;
            }
            @Override
            public void onAppDidEnterBackground()
            {
                updatePresence(0, false);
                foreground = false;
                updateTokenMapping();
            }
        });
        appStateMonitor.start();
        setBackgroundColorArray();
        Intent changeStatus = new Intent(mInstance, AppKilled.class);
        startService(changeStatus);
        sharedPref = this.getSharedPreferences("yeloPreferefr", Context.MODE_PRIVATE);
        applicationKilled = sharedPref.getBoolean("applicationKilled", false);
        indexDocId = sharedPref.getString("indexDoc", null);
        db = new CouchDbController(new AndroidContext(this));
        if (indexDocId == null) {
            indexDocId = db.createIndexDocument();
            sharedPref.edit().putString("indexDoc",indexDocId).apply();
        }
        chatSynced = sharedPref.getBoolean("chatSynced",false);


        registerActivityLifecycleCallbacks(mInstance);
        tf_robotoCondensed = Typeface.createFromAsset(mInstance.getAssets(),"fonts/Roboto-Condensed.ttf");
        tf_robotoRegular = Typeface.createFromAsset(getAssets(),"fonts/RobotoRegular.ttf");
        tf_robotoMedium = Typeface.createFromAsset(getAssets(),"fonts/roboto-medium.ttf");
        if (sharedPref.getBoolean("deltaRequired", true)) {
            getCurrentTime();
        } else {
            timeDelta = sharedPref.getLong("timeDelta", 0);
        }

        if(indexDocId!=null) {
            final Map<String, Object> signInDetails = db.isSignedIn(indexDocId);

            signedIn = (boolean) signInDetails.get("isSignedIn");
            if (signedIn) {
                userId = (String) signInDetails.get("signedUserId");
                getUserDocIdsFromDb(userId);
                createMQttConnection(userId);
            }else{


                if(signInDetails.containsKey("notReadyYet")){



                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            userId = (String) signInDetails.get("signedUserId");
                            getUserDocIdsFromDb(userId);
                            createMQttConnection(userId);
                        }
                    },2000);


                }



            }
        }

        FirebaseMessaging.getInstance().subscribeToTopic("/topics/" + "sendToAll" );

    }





    /**
     *initialization of the MQtt call back.*/
    private MqttCallbackExtended initMqttListener()
    {
        MqttCallbackExtended mlistener=new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI)
            {
                handelConnection();
                updatePresence(1,false);
            }
            @Override
            public void connectionLost(Throwable cause)
            {
                handelconnectionLost(cause);
            }
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception
            {
                handdleArrivedmessage(topic,message);
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken token)
            {
                handledeliveryComplete(token);
            }
        };
        return mlistener;
    }
    /*
     *Collect all document details of the logged in user. */
    public void getUserDocIdsFromDb(String userId)
    {
        String userDocId = db.getUserInformationDocumentId(indexDocId,userId);
        ArrayList<String> docIds = db.getUserDocIds(userDocId);
        chatDocId = docIds.get(0);
        unsentMessageDocId = docIds.get(1);
        mqttTokenDocId = docIds.get(2);
        notificationDocId = docIds.get(3);
        getUserInfoFromDb(userDocId);
        tokenMapping = db.fetchMqttTokenMapping(mqttTokenDocId);
    }

    public void getUserInfoFromDb(String docId)
    {
        Map<String, Object> userInfo = db.getUserInfo(docId);
        apiToken = (String) userInfo.get("apiToken");
        userName = (String) userInfo.get("userName");
        userIdentifier = (String) userInfo.get("userIdentifier");
        userId = (String) userInfo.get("userId");
        userImageUrl = (String) userInfo.get("userImageUrl");
        Log.d("log71", apiToken+" " + userName + " " + userIdentifier + " " + userId + " " + userImageUrl);
        notifications = db.fetchAllNotifications(notificationDocId);
    }
    /*
     *Handling the connection the app */
    private void handelConnection()
    {
        Log.d("log47","Connected");
        try {
            JSONObject obj = new JSONObject();
            obj.put("eventName", MqttEvents.Connect.value);
            bus.post(obj);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        try
        {
            if (!chatSynced)
            {
                JSONObject obj = new JSONObject();
                obj.put("eventName", "RefreshChats");
                bus.post(obj);
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        if (!applicationKilled)
        {
            updatePresence(1, false);
        } else
        {
            updatePresence(0, true);
        }
                /*
                 * To stop the internet checking service
                 */
        if (flag) {
            flag = false;
            Log.d("log77","subscribe topic"+userId);
            subscribeToTopic(MqttEvents.Message.value+"/"+userId, 1);
            subscribeToTopic(MqttEvents.OfferMessage.value+"/"+userId, 1);
            subscribeToTopic(MqttEvents.Acknowledgement.value+"/"+userId, 2);
            subscribeToTopic(MqttEvents.FetchMessages.value+"/"+userId, 1);
            subscribeToTopic(MqttEvents.FetchChats.value+"/"+userId, 1);
            subscribeToTopic(MqttEvents.UpdateProduct.value+"/"+userId, 1);
            subscribeToTopic(MqttEvents.UserUpdates.value+"/"+userId,2);
            subscribeToTopic(MqttEvents.Typing.value+"/"+userId,0);
        }
        resendUnsentMessages();
    }
    /*
    * handdling the conecctuon lost*/
    private void handelconnectionLost(Throwable cause)
    {
        //Log.d("log57",""+cause.getMessage());
        try {
            JSONObject obj = new JSONObject();
            obj.put("eventName", MqttEvents.Disconnect.value);
            bus.post(obj);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    /*
     *handel the message received. */
    private void handdleArrivedmessage(String topic, MqttMessage message) throws Exception
    {
        JSONObject obj = convertMessageToJsonObject(message);
        Log.d("log56", topic + " " + obj.toString());
        if (topic.equals(MqttEvents.Acknowledgement.value + "/" + userId))
        {
                    /*
                     * For an acknowledgement message received
                     */
            String sender = obj.getString("from");
            String document_id_DoubleTick = obj.getString("doc_id");
            JSONArray arr_temp = obj.getJSONArray("msgIds");
            String id = arr_temp.getString(0);
                    /*
                     * For callback in to activity to update UI
                     */
            try {
                obj.put("msgId", id);
                obj.put("eventName", topic);
                bus.post(obj);
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
            if (obj.getString("status").equals("2"))
            {
        /*
 * message delivered
 */
                if (!(db.updateMessageStatus(document_id_DoubleTick, id, 1)))
                {
                    db.updateMessageStatus(db.getDocumentIdOfReceiverChatlistScreen(AppController.getInstance().getChatDocId(), sender, obj.getString("secretId")), id, 1);
                }
            } else {
                        /*
                         * message read
                         */
                if (!(db.drawBlueTickUptoThisMessage(document_id_DoubleTick, id)))
                {
                    db.drawBlueTickUptoThisMessage(db.getDocumentIdOfReceiverChatlistScreen(AppController.getInstance().getChatDocId(), sender, obj.getString("secretId")), id);
                }
            }
        }else if (topic.equals(MqttEvents.OfferMessage.value+ "/" + userId)||topic.equals(MqttEvents.Message.value + "/" + userId))
        {
                    /*
                     * For an actual message(Like text,image,video etc.) received
                     */
            boolean isself=false;
            String receiverUid = obj.getString("from");
            if(userId!=null&&userId.equals(receiverUid))
            {
                isself=true;
                receiverUid=obj.getString("to");
            }
            String receiverIdentifier="";
            if(obj.has("receiverIdentifier"))
            {
                receiverIdentifier = obj.getString("receiverIdentifier");
            }
            String messageType= obj.getString("type");
            String actualMessage= obj.getString("payload").trim();
            String timestamp = String.valueOf(obj.getString("timestamp"));
            String id = obj.getString("id");
            String docIdForDoubleTickAck = obj.getString("toDocId");
            String offerType="1";
            if(obj.has("offerType"))
            {
                offerType = obj.getString("offerType");
            }
            String productImage="",productName="",productPrice="";
            boolean isAccepted;
            if(obj.has("productImage"))
            {
                productImage=obj.getString("productImage");
            }
            if(obj.has("productName"))
            {
                productName=obj.getString("productName");
            }
            if(obj.has("productPrice"))
            {
                productPrice=obj.getString("productPrice");
            }
            String productAcceptedPrice="";
            if(offerType.equals("2"))
            {
                isAccepted=true;
                byte[] data = Base64.decode(actualMessage, Base64.DEFAULT);
                try {
                    productAcceptedPrice=new String(data, "UTF-8");
                } catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
            }else
            {
                isAccepted=false;
            }
            int dataSize = -1;
            if (messageType.equals("1") || messageType.equals("2") || messageType.equals("5") || messageType.equals("7")) {
                dataSize = obj.getInt("dataSize");
            }
            String secretId = obj.getString("secretId");
            String receiverName;
            String userImage;
            receiverName = obj.getString("name");
            userImage = obj.getString("userImage");
            String documentId = AppController.getInstance().findDocumentIdOfReceiver(receiverUid, secretId);
            if (documentId.isEmpty())
            {
                        /*
                         *If chat document is not exist then creating the prodcut details */
                documentId = findDocumentIdOfReceiver(receiverUid, Utilities.tsInGmt(),receiverName, userImage, secretId,true, receiverIdentifier, "",productImage,productName,productPrice,isAccepted,false);
            } else
            {
                if(isAccepted)
                {
                    db.updateProductAccepted(documentId,true,productAcceptedPrice);
                }
                if(!isself)
                {
                    db.updateChatDetails(documentId,receiverName,userImage);
                }
            }
            db.setDocumentIdOfReceiver(documentId, docIdForDoubleTickAck, receiverUid);
                    /*
                     * For callback in to activity to update UI
                     */
            if (!db.checkAlreadyExists(documentId, id))
            {
                if (messageType.equals("1") || messageType.equals("2") || messageType.equals("7")) {


                    AppController.getInstance().putMessageInDb(receiverUid, messageType,offerType,
                            actualMessage, timestamp, id, documentId, obj.getString("thumbnail").trim(),
                            dataSize, receiverName, isself, -1);
                } else {

                    AppController.getInstance().putMessageInDb(receiverUid,
                            messageType,offerType,actualMessage, timestamp, id, documentId, null, dataSize, receiverName,isself, -1);
                }
                JSONObject obj2 = new JSONObject();
                obj2.put("from", AppController.getInstance().userId);
                obj2.put("msgIds", new JSONArray(Arrays.asList(new String[]{id})));
                obj2.put("doc_id", docIdForDoubleTickAck);
                obj2.put("to", receiverUid);
                obj2.put("status", "2");
                obj2.put("secretId", secretId);
                AppController.getInstance().publish(MqttEvents.Acknowledgement.value + "/" + receiverUid, obj2, 2, false);
                    /*
                     * For callback in to activity to update UI
                     */
                try {
                    obj.put("eventName", topic);
                    bus.post(obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(mInstance, ChatMessageScreen.class);
                intent.putExtra("isNew",false);
                intent.putExtra("secretId", secretId);
                intent.putExtra("receiverUid", receiverUid);
                intent.putExtra("receiverName", receiverName);
                intent.putExtra("receiverIdentifier", receiverIdentifier);
                intent.putExtra("documentId", documentId);
                intent.putExtra("colorCode", AppController.getInstance().getColorCode(5));
                intent.putExtra("receiverImage", userImage);
                intent.putExtra("fromNotification", true);
                        /*
                         *To generate the push notification locally
                         */
                if (chatSynced&&!isself)
                {
                    generatePushNotificationLocal(documentId, messageType,offerType,receiverName, actualMessage, intent, secretId, receiverUid);
                }
                if (!actualMessage.trim().isEmpty()) {
                    db.updateSecretInviteImageVisibility(documentId, false);
                }
            }
        } else if (topic.substring(0, 3).equals("Onl"))
        {
                    /*
                     * To check for the online status
                     */
            try {
                obj.put("eventName",topic);
                bus.post(obj);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        } else if (topic.equals(MqttEvents.Typing.value+"/"+userId))
        {
            try {
                obj.put("eventName",topic);
                bus.post(obj);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        } else if (topic.equals(MqttEvents.FetchChats.value +"/"+userId))
        {
            try {
                JSONArray chats = obj.getJSONArray("chats");
                String tsInGmt, receiverName;
                JSONObject jsonObject;
                boolean wasInvited;
                int totalUnreadMessageCount;
                boolean hasNewMessage, isSelf;
                String profilePic;
                for (int j = 0; j < chats.length(); j++)
                {
                    jsonObject = chats.getJSONObject(j);
                    isSelf = jsonObject.getString("senderId").equals(userId);
                    totalUnreadMessageCount = jsonObject.getInt("totalUnread");
                    hasNewMessage = totalUnreadMessageCount > 0;
                    tsInGmt = Utilities.epochtoGmt(String.valueOf(jsonObject.getLong("timestamp")));
                    receiverName = jsonObject.getString("userName");
                    String productImage="",productName="",productPrice="",userIdentifier="";
                    boolean isSold=false;
                    if(obj.has("productImage"))
                    {
                        productImage=obj.getString("productImage");
                    }
                    if(obj.has("productName"))
                    {
                        productName=obj.getString("productName");
                    }
                    if(obj.has("productPrice"))
                    {
                        productPrice=obj.getString("productPrice");
                    }
                    if (jsonObject.has("profilePic")) {
                        profilePic = jsonObject.getString("profilePic");
                    } else {
                        profilePic = "";
                    }
                    if(jsonObject.has("productSold"))
                    {
                        isSold = jsonObject.getBoolean("productSold");
                    }
                    if(jsonObject.has("userIdentifier"))
                    {
                        userIdentifier=jsonObject.getString("userIdentifier");
                    }
                    String documentId = AppController.getInstance().
                            findDocumentIdOfReceiver(jsonObject.getString("recipientId"), jsonObject.getString("secretId"));
                    wasInvited = !jsonObject.getBoolean("initiated");
                    if (documentId.isEmpty())
                    {
                        documentId = findDocumentIdOfReceiver(jsonObject.getString("recipientId"),
                                tsInGmt, receiverName, profilePic, jsonObject.getString("secretId"),
                                wasInvited,userIdentifier, jsonObject.getString("chatId"),productImage,productName,productPrice,false,isSold);
                    } else
                    {
                        if(isSold)
                        {
                            db.updateSoldDetails(documentId,true);
                        }
                        db.updateChatDetails(documentId, jsonObject.getString("chatId"),wasInvited,profilePic);
                    }

                    switch (Integer.parseInt(jsonObject.getString("messageType")))
                    {
                        case 15:
                            String text_data = "";
                            try {
                                text_data = new String(Base64.decode(jsonObject.getString("payload"), Base64.DEFAULT), "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            db.updateChatListForNewMessageFromHistory(
                                    documentId, text_data, hasNewMessage, tsInGmt, tsInGmt, totalUnreadMessageCount, isSelf, jsonObject.getInt("status"));
                            break;
                        case 16:
                            String paypal_data = "";
                            try {
                                paypal_data = new String(Base64.decode(jsonObject.getString("payload"), Base64.DEFAULT), "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            db.updateChatListForNewMessageFromHistory(documentId, paypal_data, hasNewMessage, tsInGmt, tsInGmt, totalUnreadMessageCount, isSelf, jsonObject.getInt("status"));
                            break;
                        case 0:
                            String text = "";
                            try {
                                text = new String(Base64.decode(jsonObject.getString("payload"), Base64.DEFAULT), "UTF-8");
                            } catch (UnsupportedEncodingException e)
                            {
                                e.printStackTrace();
                            }
                            if (text.trim().isEmpty())
                            {
                                if (jsonObject.getString("senderId").equals(userId))
                                {
                                    text = getResources().getString(R.string.YouInvited) + " " + receiverName + " " +
                                            getResources().getString(R.string.JoinSecretChat);
                                } else {

                                    text = getResources().getString(R.string.youAreInvited) + " " + receiverName + " " +
                                            getResources().getString(R.string.JoinSecretChat);
                                }
                            }
                            db.updateChatListForNewMessageFromHistory(
                                    documentId, text, hasNewMessage, tsInGmt, tsInGmt, totalUnreadMessageCount, isSelf, jsonObject.getInt("status"));
                            break;
                        case 1:
/*
 * receiverImage message
 */

                            if (totalUnreadMessageCount > 0)
                            {
                                db.updateChatListForNewMessageFromHistory(documentId, getString(R.string.NewImage), hasNewMessage, tsInGmt, tsInGmt, totalUnreadMessageCount, isSelf, jsonObject.getInt("status"));
                            } else {
                                db.updateChatListForNewMessageFromHistory(documentId, getString(R.string.Image), hasNewMessage, tsInGmt, tsInGmt, totalUnreadMessageCount, isSelf, jsonObject.getInt("status"));
                            }
                            break;
                        case 2:
/*
 * Video message
 */
                            if (totalUnreadMessageCount > 0)
                            {
                                db.updateChatListForNewMessageFromHistory(documentId, getString(R.string.NewVideo),
                                        hasNewMessage, tsInGmt, tsInGmt, totalUnreadMessageCount, isSelf, jsonObject.getInt("status"));
                            } else {
                                db.updateChatListForNewMessageFromHistory(documentId,
                                        getString(R.string.Video),
                                        hasNewMessage, tsInGmt, tsInGmt, totalUnreadMessageCount, isSelf, jsonObject.getInt("status"));
                            }

                            break;
                        case 3:
/*
 * Location message
 */
                            if (totalUnreadMessageCount > 0)
                            {
                                db.updateChatListForNewMessageFromHistory(documentId, getString(R.string.NewLocation), hasNewMessage, tsInGmt, tsInGmt, totalUnreadMessageCount, isSelf, jsonObject.getInt("status"));
                            } else {

                                db.updateChatListForNewMessageFromHistory(documentId, getString(R.string.Location), hasNewMessage, tsInGmt, tsInGmt, totalUnreadMessageCount, isSelf, jsonObject.getInt("status"));
                            }
                            break;
                        case 4:
/*
 * Contact message
 */
                            if (totalUnreadMessageCount > 0) {
                                db.updateChatListForNewMessageFromHistory(documentId, getString(R.string.NewContact),hasNewMessage, tsInGmt, tsInGmt, totalUnreadMessageCount, isSelf, jsonObject.getInt("status"));

                            } else {

                                db.updateChatListForNewMessageFromHistory(documentId, getString(R.string.Contact),
                                        hasNewMessage, tsInGmt, tsInGmt, totalUnreadMessageCount, isSelf, jsonObject.getInt("status"));


                            }
                            break;
                        case 5:
                                    /*
                                     * Audio message*/
                            if (totalUnreadMessageCount > 0) {
                                db.updateChatListForNewMessageFromHistory(documentId, getString(R.string.NewAudio), hasNewMessage, tsInGmt, tsInGmt, totalUnreadMessageCount, isSelf, jsonObject.getInt("status"));

                            } else {
                                db.updateChatListForNewMessageFromHistory(documentId,
                                        getString(R.string.Audio), hasNewMessage, tsInGmt, tsInGmt, totalUnreadMessageCount, isSelf, jsonObject.getInt("status"));
                            }
                            break;
                        case 6:
                                    /*
             * Sticker*/
                            if (totalUnreadMessageCount > 0)
                            {
                                db.updateChatListForNewMessageFromHistory(documentId, getString(R.string.NewSticker), hasNewMessage, tsInGmt,
                                        tsInGmt, totalUnreadMessageCount, isSelf, jsonObject.getInt("status"));
                            } else {
                                db.updateChatListForNewMessageFromHistory(documentId, getString(R.string.Stickers), hasNewMessage, tsInGmt,
                                        tsInGmt, totalUnreadMessageCount, isSelf, jsonObject.getInt("status"));
                            }
                            break;
                        case 7:
/*
 * Doodle
 */
                            if (totalUnreadMessageCount > 0)
                            {
                                db.updateChatListForNewMessageFromHistory(documentId, getString(R.string.NewDoodle), hasNewMessage, tsInGmt, tsInGmt, totalUnreadMessageCount, isSelf, jsonObject.getInt("status"));
                            } else {
                                db.updateChatListForNewMessageFromHistory(documentId, getString(R.string.Doodle), hasNewMessage, tsInGmt, tsInGmt, totalUnreadMessageCount, isSelf, jsonObject.getInt("status"));
                            }

                            break;
                        case 8:
/*
 * Gif
 */
                            if (totalUnreadMessageCount > 0) {
                                db.updateChatListForNewMessageFromHistory(documentId, getString(R.string.NewGiphy), hasNewMessage, tsInGmt, tsInGmt, totalUnreadMessageCount, isSelf, jsonObject.getInt("status"));
                            } else {
                                db.updateChatListForNewMessageFromHistory(documentId, getString(R.string.Giphy), hasNewMessage, tsInGmt, tsInGmt, totalUnreadMessageCount, isSelf, jsonObject.getInt("status"));
                            }
                            break;
                    }
                }
                obj.put("eventName", topic);
                bus.post(obj);
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        } else if (topic.equals(MqttEvents.FetchMessages.value + "/" + userId))
        {
            try {
                String secretId = obj.getString("secretId");
                JSONArray messages = obj.getJSONArray("messages");
                if (messages.length() > 0)
                {
                    JSONObject messageObject = messages.getJSONObject(0);
                    String receiverUid = messageObject.getString("senderId");
                    if(userId!=null&&userId.equals(receiverUid))
                    {
                        receiverUid=messageObject.getString("receiverId");
                    }
                    Map<String, Object> chatDetails = AppController.getInstance().getDbController().getAllChatDetails(chatDocId);
                    String documentId="";
                    if (chatDetails != null)
                    {
                        ArrayList<String> receiverUidArray = (ArrayList<String>) chatDetails.get("receiverUidArray");
                        ArrayList<String> receiverDocIdArray = (ArrayList<String>) chatDetails.get("receiverDocIdArray");
                        ArrayList<String> secretIdArray = (ArrayList<String>) chatDetails.get("secretIdArray");
                        for (int i = 0; i < receiverUidArray.size(); i++)
                        {
                            if (receiverUidArray.get(i).equals(receiverUid) && secretIdArray.get(i).equals(secretId))
                            {
                                documentId=receiverDocIdArray.get(i);
                                break;
                            }
                        }
                    }
                    Log.d("log17",""+documentId);
                    if(documentId==null||documentId.isEmpty())
                    {
                        return;
                    }
                            /*
                             *Putting msg in DB.*/
                    boolean isSelf;
                    for (int i = 0; i < messages.length(); i++)
                    {
                        messageObject = messages.getJSONObject(i);
                        isSelf = messageObject.getString("senderId").equals(userId);
                        String messageType = messageObject.getString("messageType");
                        String actualMessage = messageObject.getString("payload").trim();
                        String timestamp = String.valueOf(messageObject.getLong("timestamp"));
                        String id = messageObject.getString("messageId");
                        String docIdForDoubleTickAck = messageObject.getString("toDocId");
                        int dataSize = -1;
                        if (messageType.equals("1") || messageType.equals("2") || messageType.equals("5") || messageType.equals("7")) {
                            dataSize = messageObject.getInt("dataSize");
                        }
                        String receiverName = messageObject.getString("name");
                        String userImage = messageObject.getString("userImage");
                        String offerType="1";
                        if(obj.has("offerType"))
                        {
                            offerType = obj.getString("offerType");
                        }
                        boolean isAccepted;
                        String productSoldPrice="";
                        if(offerType.equals("2"))
                        {
                            isAccepted = true;
                            byte[] data = Base64.decode(actualMessage, Base64.DEFAULT);
                            try {
                                productSoldPrice=new String(data, "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }else
                        {
                            isAccepted=false;
                        }
                        if(isAccepted)
                        {
                            db.updateProductAccepted(documentId,true,productSoldPrice);
                        }
                        if(!isSelf)
                        {
                            db.updateChatDetails(documentId,receiverName,userImage);
                        }
                        db.setDocumentIdOfReceiver(documentId, docIdForDoubleTickAck, receiverUid);
                        if (!db.checkAlreadyExists(documentId, id))
                        {
                            if (messageType.equals("1") || messageType.equals("2") || messageType.equals("7"))
                            {
                                AppController.getInstance().putMessageInDb(receiverUid, messageType, offerType,
                                        actualMessage, timestamp, id, documentId,
                                        messageObject.getString("thumbnail").trim(),
                                        dataSize, receiverName, isSelf, messageObject.getInt("status")
                                );
                            } else {
                                AppController.getInstance().putMessageInDb(receiverUid,
                                        messageType, offerType, actualMessage, timestamp, id, documentId,
                                        null, dataSize, receiverName, isSelf,
                                        messageObject.getInt("status"));
                            }
                            if ((messageObject.getInt("status") == 1) && isSelf && (!messageType.equals("0") || !actualMessage.isEmpty())) {
                                JSONObject obj2 = new JSONObject();
                                obj2.put("from", AppController.getInstance().userId);
                                obj2.put("msgIds", new JSONArray(Arrays.asList(new String[]{id})));
                                obj2.put("doc_id", docIdForDoubleTickAck);
                                obj2.put("to", receiverUid);
                                obj2.put("status", "2");
                                obj2.put("secretId", secretId);
                                AppController.getInstance().publish(MqttEvents.Acknowledgement.value + "/" + receiverUid, obj2, 2, false);
                            }
                        }
                        if (!actualMessage.trim().isEmpty())
                        {
                            db.updateSecretInviteImageVisibility(documentId, false);
                        }
                    }
                }
                obj.put("eventName", topic);
                bus.post(obj);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }else if(topic.equals(MqttEvents.UpdateProduct.value+"/"+userId))
        {
            try
            {
                String id=obj.getString("id");
                if(obj.has("sold"))
                {
                    db.updateAllProductSold(id,obj.getBoolean("sold"));
                }else
                {
                    String productName=obj.getString("name");
                    String productImage=obj.getString("image");
                    String negotiable=obj.getString("negotiable");
                    db.updateAllProduct(id,productImage,productName,"",negotiable,"");
                }
                obj.put("eventName",topic);
                bus.post(obj);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }/*else if(topic.equals(MqttEvents.UserUpdates+"/"+userId))
                {}*/
    }
    /*
    * Handeling the token delivery*/
    public void handledeliveryComplete(IMqttDeliveryToken token)
    {
        if (set.contains(token))
        {
            String id = null, docId = null;
            int size = tokenMapping.size();
            HashMap<String, Object> map;
            for (int i = 0; i < size; i++)
            {
                map = tokenMapping.get(i);
                if (map.get("MQttToken").equals(token))
                {
                    id = (String) map.get("messageId");
                    docId = (String) map.get("docId");
                    tokenMapping.remove(i);
                    set.remove(token);
                    break;
                }
            }
            if (docId != null && id != null)
            {
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("messageId", id);
                    obj.put("docId", docId);
                    obj.put("eventName", MqttEvents.MessageResponse.value);
                    bus.post(obj);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
                db.updateMessageStatus(docId, id, 0);
                db.removeUnsentMessage(AppController.getInstance().unsentMessageDocId, id);
            }
        }
    }
    /**
     * Prepare image or audio or video file for upload
     */
    @SuppressWarnings("all")
    public File convertByteArrayToFileToUpload(byte[] data, String name, String extension) {


        File file = null;

        try {


            File folder = new File(Environment.getExternalStorageDirectory().getPath() + ApiOnServer.CHAT_UPLOAD_THUMBNAILS_FOLDER);

            if (!folder.exists() && !folder.isDirectory()) {
                folder.mkdirs();
            }


            file = new File(Environment.getExternalStorageDirectory().getPath() + ApiOnServer.CHAT_UPLOAD_THUMBNAILS_FOLDER, name + extension);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);

            fos.write(data);
            fos.flush();
            fos.close();
        } catch (IOException e) {

        }


        return file;

    }

    /**
     * To prepare image file for upload
     */
    private int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {


        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;


            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private Bitmap decodeSampledBitmapFromResource(String pathName,
                                                   int reqWidth, int reqHeight) {


        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);


        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);


        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);

    }

    /**
     * To upload image or video or audio to server using multipart upload to avoid OOM exception
     */
    @SuppressWarnings("TryWithIdenticalCatches,all")

    private void uploadFile(final Uri fileUri, final String name, final int messageType,
                            final JSONObject obj, final String receiverUid, final String id,
                            final HashMap<String, Object> mapTemp, final String secretId) {
        FileUploadService service =
                ServiceGenerator.createService(FileUploadService.class);
        final File file = FileUtils.getFile(this, fileUri);
        String url = null;
        if (messageType == 1)
        {
            url = name + ".jpg";
        } else if (messageType == 2)
        {
            url = name + ".mp4";
        } else if (messageType == 5)
        {
            url = name + ".mp3";
        } else if (messageType == 7)
        {
            url = name + ".jpg";
        }
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("photo", url, requestFile);
        String descriptionString = "Yelo File Uploading";
        RequestBody description =
                RequestBody.create(MediaType.parse("multipart/form-data"), descriptionString);

        // finally, execute the request
        Call<ResponseBody> call = service.upload(description,body,ApiOnServer.AUTH_KEY);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,Response<ResponseBody> response)
            {
/**
 * has to get url from the server in respons.
 * */

                try {


                    if (response.code() == 200) {


                        String url = null;
                        if (messageType == 1) {

                            url = name + ".jpg";


                        } else if (messageType == 2) {

                            url = name + ".mp4";


                        } else if (messageType == 5) {

                            url = name + ".mp3";


                        } else if (messageType == 7)
                        {
                            url = name + ".jpg";
                        }
                        obj.put("payload",Base64.encodeToString((ApiOnServer.CHAT_UPLOAD_PATH + url).getBytes("UTF-8"), Base64.DEFAULT));
                        obj.put("dataSize", file.length());
                        obj.put("timestamp", new Utilities().gmtToEpoch(Utilities.tsInGmt()));
                        File fdelete = new File(fileUri.getPath());
                        if (fdelete.exists()) fdelete.delete();
                    }
                } catch (JSONException e) {
                } catch (IOException e) {}


                /**
                 *
                 *
                 * emitting to the server the values after the file has been uploaded
                 *
                 * */


                String tsInGmt = Utilities.tsInGmt();
                String docId = AppController.getInstance().findDocumentIdOfReceiver(receiverUid, secretId);
                db.updateMessageTs(docId, id, tsInGmt);
                AppController.getInstance().publishChatMessage(MqttEvents.Message.value + "/" + receiverUid, obj, 1, false, mapTemp);

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {


            }
        });
    }

    /**
     * Update change in signin status
     * If signed in then have to connect socket,start listening on various socket events and disconnect socket in case of signout, stop listening on various socket events
     */
    public void setSignedIn(final boolean signedIn, final String userId, final String userName, final String userIdentifier)
    {
        this.signedIn = signedIn;
        if (signedIn)
        {
            /*
             *Disonnecting the previous user details. */
            disconnect();
            /*
             *making it true for the to add new subscriber */
            flag = true;
            /*
             *For new sign up or login sync the chat again. */
            chatSynced=false;
            mInstance.userId = userId;
            mInstance.userIdentifier = userIdentifier;
            mInstance.userName = userName;
            getUserDocIdsFromDb(userId);
            createMQttConnection(userId);
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run() {
                    if (sharedPref.getString("chatNotificationArray", null) == null) {
                        SharedPreferences.Editor prefsEditor = sharedPref.edit();
                        prefsEditor.putString("chatNotificationArray", new Gson().toJson(new ArrayList<Map<String, String>>()));
                        prefsEditor.apply();
                    }
                }
            }, 1000);
        } else {
            flag = true;
            mInstance.userId = null;
        }
    }


    public long getTimeDelta() {
        return timeDelta;
    }

    public boolean getSignedIn()
    {
        return this.signedIn;
    }

    public String getChatDocId() {

        return chatDocId;
    }

    public String getUserIdentifier() {
        return userIdentifier;
    }


    public boolean isForeground() {
        return foreground;
    }

    public SharedPreferences getSharedPreferences() {

        return sharedPref;
    }

    public boolean isSignStatusChanged()
    {
        return signStatusChanged;
    }

    public void setSignStatusChanged(boolean signStatusChanged)
    {
        this.signStatusChanged = signStatusChanged;
    }

    public String getActiveReceiverId()
    {
        return activeReceiverId;
    }


    public void setActiveReceiverId(String receiverId) {


        this.activeReceiverId = receiverId;
    }

    public void setActiveSecretId(String secretId) {


        this.activeSecretId = secretId;
    }


    public boolean getChatSynced()
    {
        return chatSynced;
    }

    public void setChatSynced(boolean synced)
    {
        chatSynced = synced;
        sharedPref.edit().putBoolean("chatSynced", synced).apply();
    }
    public CouchDbController getDbController()
    {
        return db;
    }
    public void setApplicationKilled(boolean applicationKilled)
    {
        sharedPref.edit().putBoolean("applicationKilled", applicationKilled).apply();
        this.applicationKilled = applicationKilled;
        if (applicationKilled) {
            sharedPref.edit().putString("lastSeenTime", Utilities.tsInGmt()).apply();
        }
    }
    public Typeface getRobotoCondensedFont() {
        return tf_robotoCondensed;
    }


    public Typeface getRobotoMediumFont() {
        return tf_robotoMedium;
    }

    public Typeface getRobotoRegularFont() {
        return tf_robotoRegular;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserId() {
        return userId;
    }


    public String getUserImageUrl() {
        return userImageUrl;
    }


    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;


        db.updateUserImageUrl(db.getUserDocId(userId, indexDocId), userImageUrl);

    }

    public String getDeviceId() {


        return deviceId;
    }


    public void setUserName(String userName) {
        this.userName = userName;


        db.updateUserName(db.getUserDocId(userId, indexDocId), userName);


    }
    public String getunsentMessageDocId()
    {
        return unsentMessageDocId;
    }

    public String getIndexDocId()
    {
        return indexDocId;
    }

    public String getApiToken() {


        return apiToken;
    }

    /**
     * To search if there exists any document containing chat for a particular receiver and if founde returns its document
     * id else return empty string
     */
    @SuppressWarnings("unchecked")
    public String findDocumentIdOfReceiver(String ReceiverUid, String secretId)
    {
        Log.d("Details",""+ReceiverUid+" "+secretId);

        String docId = "";
        Map<String, Object> chatDetails = db.getAllChatDetails(AppController.getInstance().getChatDocId());
        if (chatDetails != null) {
            ArrayList<String> receiverUidArray = (ArrayList<String>) chatDetails.get("receiverUidArray");
            ArrayList<String> receiverDocIdArray = (ArrayList<String>) chatDetails.get("receiverDocIdArray");
            ArrayList<String> secretIdArray = (ArrayList<String>) chatDetails.get("secretIdArray");
            for (int i = 0; i < receiverUidArray.size(); i++)
            {
                if (receiverUidArray.get(i).equals(ReceiverUid))
                {
                    if (secretId.isEmpty())
                    {
                        if (secretIdArray.get(i).isEmpty()) {
                            return receiverDocIdArray.get(i);
                        }
                    } else {
                        if (secretIdArray.get(i).equals(secretId)) {
                            return receiverDocIdArray.get(i);
                        }
                    }

                }

            }
        }


        return docId;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag)
    {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req)
    {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag)
    {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    private void setBackgroundColorArray()
    {
        colors = new ArrayList<>();
        colors.add("#FFCDD2");
        colors.add("#D1C4E9");
        colors.add("#B3E5FC");
        colors.add("#C8E6C9");
        colors.add("#FFF9C4");
        colors.add("#FFCCBC");
        colors.add("#CFD8DC");
        colors.add("#F8BBD0");
        colors.add("#C5CAE9");
        colors.add("#B2EBF2");
        colors.add("#DCEDC8");
        colors.add("#FFECB3");
        colors.add("#D7CCC8");
        colors.add("#F5F5F5");
        colors.add("#FFE0B2");
        colors.add("#F0F4C3");
        colors.add("#B2DFDB");
        colors.add("#BBDEFB");
        colors.add("#E1BEE7");
    }


    public String getColorCode(int position)
    {
        return colors.get(position);
    }

    /**
     * @param clientId is same as the userId
     */
    @SuppressWarnings("unchecked")
    public void createMQttConnection(String clientId)
    {
        clientId="yelo_"+clientId+deviceId;
        Log.d("case21","Yes called"+" "+ clientId);
        String serverUri = "tcp://"+ApiOnServer.HOST +":"+ApiOnServer.PORT;
        mqttAndroidClient = new MqttAndroidClient(mInstance,serverUri,clientId,new MemoryPersistence());
        mqttAndroidClient.setCallback(initMqttListener());

        mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);

        mqttConnectOptions.setUserName(ApiOnServer.MQTTUSER_NAME);
        String password=ApiOnServer.MQTTPASSWORD;
        mqttConnectOptions.setPassword(password.toCharArray());
        mqttConnectOptions.setAutomaticReconnect(true);



        JSONObject obj = new JSONObject();
        try
        {
            obj.put("lastSeenEnabled",sharedPref.getBoolean("enableLastSeen",true));
            obj.put("status", 2);
            obj.put("userId", userId);
        } catch(JSONException e)
        {
            e.printStackTrace();
        }
        mqttConnectOptions.setWill(MqttEvents.OnlineStatus.value + "/" + userId, obj.toString().getBytes(),0,true);
        mqttConnectOptions.setKeepAliveInterval(30);
    /*
     * Has been removed from here to avoid the reace condition for the mqtt
      * connection with the mqtt broker.
     */
        connectMqttClient();
    }
    @SuppressWarnings("TryWithIdenticalCatches")
    public void publish(String topicName, JSONObject obj, int qos, boolean retained)
    {
        Log.d("t45",""+topicName);
        try {
            mqttAndroidClient.publish(topicName, obj.toString().getBytes(), qos, retained);
        } catch (MqttException e)
        {
            e.printStackTrace();

        } catch (NullPointerException e)
        {
            e.printStackTrace();
        }
    }

    private JSONObject convertMessageToJsonObject(MqttMessage message) {

        JSONObject obj = new JSONObject();
        try {

            obj = new JSONObject(new String(message.getPayload()));
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * To save the message received to the local couchdb
     */
    private void putMessageInDb(String receiverUid, String messageType,String offerType,String actualMessage,
                                String timestamp, String id, String receiverdocId, String thumbnailMessage,
                                int dataSize, String senderName, boolean isSelf, int status) {

        byte[] data = Base64.decode(actualMessage, Base64.DEFAULT);
        byte[] thumbnailData = null;
        if ((messageType.equals("1")) || (messageType.equals("2")) || (messageType.equals("7"))) {
            thumbnailData = Base64.decode(thumbnailMessage, Base64.DEFAULT);
        }


        /*
 *
 *
 * initially in db we only store path of the thumbnail and nce downloaded we will replace that field withthe actual path of the downloaded file
 *
 *
 * */

        String tsInGmt = Utilities.epochtoGmt(timestamp);

/*
 * Text message
 */
        switch (messageType) {
            case "0": {
                String text = "";
                try {
                    text = new String(data, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


                if (text.trim().isEmpty()) {


                    if (isSelf) {


                        text = getResources().getString(R.string.YouInvited) + " " + senderName + " " +
                                getResources().getString(R.string.JoinSecretChat);

                    } else {
                        text = getResources().getString(R.string.youAreInvited) + " " + senderName + " " +
                                getResources().getString(R.string.JoinSecretChat);
                    }


                }


                Map<String, Object> map = new HashMap<>();
                map.put("message", text);
                map.put("messageType", "0");
                map.put("isSelf", isSelf);
                map.put("from", receiverUid);
                map.put("Ts", tsInGmt);
                map.put("id", id);

                if (isSelf) {
                    map.put("deliveryStatus", String.valueOf(status));
                }
                if (status == -1) {
                    db.addNewChatMessageAndSort(receiverdocId, map, tsInGmt);
                    db.updateChatListForNewMessage(receiverdocId, text, true, tsInGmt, tsInGmt);
                } else {
                    db.addNewChatMessageAndSort(receiverdocId, map, null);
                }

                break;
            }
            case "16": {
                String text = "";
                try {
                    text = new String(data, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Map<String, Object> map = new HashMap<>();
                map.put("message", text);
                map.put("messageType", "16");
                map.put("isSelf", isSelf);
                map.put("from", receiverUid);
                map.put("Ts", tsInGmt);
                map.put("id", id);

                if (isSelf) {
                    map.put("deliveryStatus", String.valueOf(status));
                }
                if (status == -1) {
                    db.addNewChatMessageAndSort(receiverdocId, map, tsInGmt);
                    db.updateChatListForNewMessage(receiverdocId, text, true, tsInGmt, tsInGmt);
                } else {
                    db.addNewChatMessageAndSort(receiverdocId, map, null);
                }

                break;
            }
            case "15": {
                String text = "";
                try {

                    text = new String(data, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (text.trim().isEmpty()) {
                    if (isSelf) {
                        text = getResources().getString(R.string.YouInvited) + " " + senderName + " " +
                                getResources().getString(R.string.JoinSecretChat);
                    } else {
                        text = getResources().getString(R.string.youAreInvited) + " " + senderName + " " +
                                getResources().getString(R.string.JoinSecretChat);
                    }
                }
                Map<String, Object> map = new HashMap<>();
                map.put("message", text);
                map.put("messageType", "15");
                map.put("offerType", offerType);
                map.put("isSelf", isSelf);
                map.put("from", receiverUid);
                map.put("Ts", tsInGmt);
                map.put("id", id);
                if (isSelf) {
                    map.put("deliveryStatus", String.valueOf(status));

                }


                if (status == -1) {
                    db.addNewChatMessageAndSort(receiverdocId, map, tsInGmt);
                    db.updateChatListForNewMessage(receiverdocId, text, true, tsInGmt, tsInGmt);


                } else {


                    db.addNewChatMessageAndSort(receiverdocId, map, null);

                }
                break;
            }
            case "1": {
/*
 * receiverImage message
 */

                String thumbnailPath = convertByteArrayToFile(thumbnailData, timestamp, "jpg");

                Map<String, Object> map = new HashMap<>();


                try {

                    map.put("message", new String(data, "UTF-8"));


                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                map.put("messageType", "1");
                map.put("isSelf", isSelf);
                map.put("from", receiverUid);
                map.put("Ts", tsInGmt);
                map.put("id", id);

                map.put("downloadStatus", 0);

                map.put("dataSize", dataSize);


                map.put("thumbnailPath", thumbnailPath);
                if (isSelf) {

                    map.put("deliveryStatus", String.valueOf(status));

                }


                if (status == -1) {
                    db.addNewChatMessageAndSort(receiverdocId, map, tsInGmt);
                    db.updateChatListForNewMessage(receiverdocId, getString(R.string.NewImage), true, tsInGmt, tsInGmt);

                } else {


                    db.addNewChatMessageAndSort(receiverdocId, map, null);

                }

                break;
            }
            case "2": {

/*
 * Video message
 */
                String thumbnailPath = convertByteArrayToFile(thumbnailData, timestamp, "jpg");


                Map<String, Object> map = new HashMap<>();


/*
 *
 *
 * message key will contail the url on server until downloaded and once downloaded
 * it will contain the local path of the video or image
 *
 * */
                try {

                    map.put("message", new String(data, "UTF-8"));


                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


                map.put("messageType", "2");
                map.put("isSelf", isSelf);
                map.put("from", receiverUid);
                map.put("Ts", tsInGmt);
                map.put("id", id);

                map.put("downloadStatus", 0);
                map.put("dataSize", dataSize);

                map.put("thumbnailPath", thumbnailPath);

                if (isSelf) {
                    map.put("deliveryStatus", String.valueOf(status));
                }


                if (status == -1) {
                    db.addNewChatMessageAndSort(receiverdocId, map, tsInGmt);
                    db.updateChatListForNewMessage(receiverdocId, getString(R.string.NewVideo), true, tsInGmt, tsInGmt);

                } else {
                    db.addNewChatMessageAndSort(receiverdocId, map, null);
                }
                break;
            }
            case "3": {
/*
 * Location message
 */
                String placeString = "";
                try {

                    placeString = new String(data, "UTF-8");
                } catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }


                Map<String, Object> map = new HashMap<>();
                map.put("message", placeString);
                map.put("messageType", "3");
                map.put("isSelf", isSelf);
                map.put("from", receiverUid);
                map.put("Ts", tsInGmt);
                map.put("id", id);

                if (isSelf) {


                    map.put("deliveryStatus", String.valueOf(status));


                }


                if (status == -1) {
                    db.addNewChatMessageAndSort(receiverdocId, map, tsInGmt);
                    db.updateChatListForNewMessage(receiverdocId, getString(R.string.NewLocation), true, tsInGmt, tsInGmt);
                } else {
                    db.addNewChatMessageAndSort(receiverdocId, map, null);


                }
                break;
            }
            case "4": {
/*
 * Contact message
 */

                String contactString = "";
                try {

                    contactString = new String(data, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


                Map<String, Object> map = new HashMap<>();
                map.put("message", contactString);
                map.put("messageType", "4");
                map.put("isSelf", isSelf);
                map.put("from", receiverUid);
                map.put("Ts", tsInGmt);
                map.put("id", id);

                if (isSelf) {
                    map.put("deliveryStatus", String.valueOf(status));
                }

                if (status == -1) {
                    db.addNewChatMessageAndSort(receiverdocId, map, tsInGmt);
                    db.updateChatListForNewMessage(receiverdocId, getString(R.string.NewContact), true, tsInGmt, tsInGmt);

                } else {
                    db.addNewChatMessageAndSort(receiverdocId, map, null);
                }
                break;
            }
            case "5": {

/*
 * Audio message
 */
                Map<String, Object> map = new HashMap<>();


                try {

                    map.put("message", new String(data, "UTF-8"));


                } catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
                map.put("messageType", "5");
                map.put("isSelf", isSelf);
                map.put("from", receiverUid);
                map.put("Ts", tsInGmt);
                map.put("id", id);
                map.put("downloadStatus", 0);
                map.put("dataSize", dataSize);


                if (isSelf) {
                    map.put("deliveryStatus", String.valueOf(status));
                }


                if (status == -1) {
                    db.addNewChatMessageAndSort(receiverdocId, map, tsInGmt);
                    db.updateChatListForNewMessage(receiverdocId, getString(R.string.NewAudio), true, tsInGmt, tsInGmt);
                } else {
                    db.addNewChatMessageAndSort(receiverdocId, map, null);


                }
                break;
            }
            case "6": {


            /*
             * Sticker
             */

                String text = "";
                try {

                    text = new String(data, "UTF-8");
                } catch (UnsupportedEncodingException e) {

                }
                Map<String, Object> map = new HashMap<>();
                map.put("message", text);
                map.put("messageType", "6");
                map.put("isSelf", isSelf);
                map.put("from", receiverUid);
                map.put("Ts", tsInGmt);
                map.put("id", id);
                if (isSelf) {
                    map.put("deliveryStatus", String.valueOf(status));
                }

                if (status == -1) {
                    db.addNewChatMessageAndSort(receiverdocId, map, tsInGmt);
                    db.updateChatListForNewMessage(receiverdocId, getString(R.string.NewSticker), true, tsInGmt, tsInGmt);
                } else {
                    db.addNewChatMessageAndSort(receiverdocId, map, null);
                }
                break;
            }
            case "7": {
/*
 * Doodle
 */
                String thumbnailPath = convertByteArrayToFile(thumbnailData, timestamp, "jpg");


                Map<String, Object> map = new HashMap<>();

                try {

                    map.put("message", new String(data, "UTF-8"));


                } catch (UnsupportedEncodingException e) {

                }

                map.put("messageType", "7");
                map.put("isSelf", isSelf);
                map.put("from", receiverUid);
                map.put("Ts", tsInGmt);
                map.put("id", id);

                map.put("downloadStatus", 0);

                map.put("dataSize", dataSize);
                map.put("thumbnailPath", thumbnailPath);

                if (isSelf) {


                    map.put("deliveryStatus", String.valueOf(status));


                }

                thumbnailPath = null;

                if (status == -1) {
                    db.addNewChatMessageAndSort(receiverdocId, map, tsInGmt);
                    db.updateChatListForNewMessage(receiverdocId, getString(R.string.NewDoodle), true, tsInGmt, tsInGmt);
                } else {
                    db.addNewChatMessageAndSort(receiverdocId, map, null);


                }
                break;
            }
            case "8": {

            /*
             * Gif
             */


                String url = "";
                try {

                    url = new String(data, "UTF-8");
                } catch (UnsupportedEncodingException e) {

                }
                Map<String, Object> map = new HashMap<>();
                map.put("message", url);
                map.put("messageType", "8");
                map.put("isSelf", isSelf);
                map.put("from", receiverUid);
                map.put("Ts", tsInGmt);
                map.put("id", id);

                if (isSelf) {


                    map.put("deliveryStatus", String.valueOf(status));


                }

                if (status == -1) {
                    db.addNewChatMessageAndSort(receiverdocId, map, tsInGmt);
                    db.updateChatListForNewMessage(receiverdocId, getString(R.string.NewGiphy), true, tsInGmt, tsInGmt);
                } else {
                    db.addNewChatMessageAndSort(receiverdocId, map, null);


                }

                break;
            }
        }


    }

    public String convertByteArrayToFile(byte[] data, String name, String extension) {


        File file;


        String path = getFilesDir() + ApiOnServer.CHAT_RECEIVED_THUMBNAILS_FOLDER;


        try {


            File folder = new File(path);


            if (!folder.exists() && !folder.isDirectory()) {
                folder.mkdirs();
            }


            file = new File(path, name + "." + extension);


            if (!file.exists())
            {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.flush();
            fos.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return path + "/" + name + "." + extension;
    }

    /*
    * Connect mqtt client*/
    private void connectMqttClient()
    {
        Log.d("cas21","Yes called");
        try
        {
            mqttAndroidClient.connect(mqttConnectOptions, mInstance, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken)
                {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception)
                {
                    Log.d("log57", "Failed to connect to: " +exception.getMessage());
                }
            });
        } catch (MqttException e)
        {
            Log.d("cas57","Yes called"+e.getMessage());
            e.printStackTrace();
        }
    }
    /*
     *Subscribe topic. */
    public void subscribeToTopic(String topic, int qos)
    {
        try {
            if (mqttAndroidClient != null)
            {
                mqttAndroidClient.subscribe(topic, qos);
            }
        } catch (MqttException e)
        {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("TryWithIdenticalCatches")
    public void unsubscribeToTopic(String topic)
    {
        try {
            if (mqttAndroidClient != null) {
                mqttAndroidClient.unsubscribe(topic);
            }
        } catch (MqttException e)
        {
            e.printStackTrace();
        } catch (NullPointerException e)
        {
            e.printStackTrace();
        }
    }
    /*
     *Updating the user presence. */
    public void updatePresence(int status, boolean applicationKilled)
    {
        if (signedIn)
        {
            if (status == 0)
            {
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("status", 0);
                    if (applicationKilled)
                    {
                        if (sharedPref.getString("lastSeenTime", null) != null)
                        {
                            obj.put("timestamp", sharedPref.getString("lastSeenTime", null));
                        } else {
                            obj.put("timestamp", Utilities.tsInGmt());
                        }
                    } else {
                        obj.put("timestamp", Utilities.tsInGmt());
                    }
                    obj.put("userId", userId);
                    obj.put("lastSeenEnabled", sharedPref.getBoolean("enableLastSeen", true));
                    publish(MqttEvents.OnlineStatus.value+"/"+userId,obj,0,true);
                } catch (JSONException w)
                {
                    w.printStackTrace();
                }
            } else {
            /*
             *Foreground
             */
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("status", 1);
                    obj.put("userId", userId);
                    obj.put("lastSeenEnabled", sharedPref.getBoolean("enableLastSeen", true));
                    publish(MqttEvents.OnlineStatus.value+"/"+userId, obj, 0, true);
                } catch (JSONException w)
                {
                    w.printStackTrace();
                }
            }}}


    public void publishChatMessage(String topicName, JSONObject obj, int qos, boolean retained, HashMap<String, Object> map)
    {
        Log.d("log33",""+topicName+" "+obj.toString());
        IMqttDeliveryToken token = null;
        try {
            obj.put("userImage",userImageUrl);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        if (mqttAndroidClient != null && mqttAndroidClient.isConnected())
        {
            try {
                token = mqttAndroidClient.publish(topicName, obj.toString().getBytes(), qos, retained);
            } catch (MqttException e)
            {
                e.printStackTrace();
            }
            map.put("MQttToken", token);
            tokenMapping.add(map);
            set.add(token);
        }
        /*
        * Throwing msg to FCM*/
        try {
            sendMessageToFcm(topicName,obj);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public boolean canPublish() {

        return mqttAndroidClient != null && mqttAndroidClient.isConnected();


    }

    public void updateTokenMapping() {


        if (signedIn) {
            db.addMqttTokenMapping(mqttTokenDocId, tokenMapping);
        }

    }

    @SuppressWarnings("TryWithIdenticalCatches")

    private void resendUnsentMessages()
    {
        String documentId = AppController.getInstance().unsentMessageDocId;
        if (documentId != null)
        {
            ArrayList<Map<String, Object>> arr = db.getUnsentMessages(documentId);
            if (arr.size() > 0) {

                String to;
                for (int i = 0; i < arr.size(); i++) {
                    Map<String, Object> map = arr.get(i);
                    JSONObject obj = new JSONObject();
                    try {
                        to = (String) map.get("to");
                        obj.put("from", userId);
                        obj.put("to", map.get("to"));
                        obj.put("receiverIdentifier", userIdentifier);
                        String type = (String) map.get("type");
                        String message = (String) map.get("message");
                        String id = (String) map.get("id");
                        String secretId = "";
                        if (map.containsKey("secretId")) {
                            secretId = (String) map.get("secretId");
                        }
                        obj.put("name", map.get("name"));
                        obj.put("toDocId", map.get("toDocId"));
                        obj.put("id", id);
                        obj.put("type", type);
                        if (!secretId.isEmpty()) {
                            obj.put("secretId", secretId);
                            obj.put("dTime", map.get("dTime"));
                        }
                        obj.put("timestamp", map.get("timestamp"));
                        HashMap<String, Object> mapTemp = new HashMap<>();
                        mapTemp.put("messageId", id);
                        mapTemp.put("docId", map.get("toDocId"));
                        if (type.equals("0")) {


                                /*
                                 * Text message
                                 */
                            try {


                                obj.put("payload", Base64.encodeToString(message.getBytes("UTF-8"), Base64.DEFAULT).trim());

                            } catch (UnsupportedEncodingException e) {

                            }


                            AppController.getInstance().publishChatMessage(MqttEvents.Message.value + "/" + to, obj, 1, false, mapTemp);

                            String tsInGmt = Utilities.tsInGmt();
                            String docId = AppController.getInstance().findDocumentIdOfReceiver((String) map.get("to"), secretId);
                            db.updateMessageTs(docId, id, tsInGmt);
                        } else if (type.equals("1")) {
  /*
                                 * receiverImage message
                                 */

                            Uri uri = null;
                            Bitmap bm = null;


                            try {

                                final BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inJustDecodeBounds = true;
                                BitmapFactory.decodeFile(message, options);


                                int height = options.outHeight;
                                int width = options.outWidth;

                                float density = AppController.getInstance().getResources().getDisplayMetrics().density;
                                int reqHeight;


                                reqHeight = (int) ((150 * density) * (height / width));

                                bm = AppController.getInstance().decodeSampledBitmapFromResource(message, (int) (150 * density), reqHeight);


                                if (bm != null) {


                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                                    bm.compress(Bitmap.CompressFormat.JPEG, 50, baos);


                                    byte[] b = baos.toByteArray();

                                    try {
                                        baos.close();
                                    } catch (IOException e) {

                                    }
                                    baos = null;


                                    File f = AppController.getInstance().convertByteArrayToFileToUpload(b, id, ".jpg");
                                    b = null;

                                    uri = Uri.fromFile(f);
                                    f = null;


                                }


                            } catch (OutOfMemoryError e) {


                            } catch (Exception e) {

                                    /*
                                     *
                                     * to handle the file not found exception
                                     *
                                     *
                                     * */


                            }


                            if (uri != null) {


                                    /*
                                     *
                                     *
                                     * make thumbnail
                                     *
                                     * */

                                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                                bm.compress(Bitmap.CompressFormat.JPEG, 1, baos);


                                bm = null;
                                byte[] b = baos.toByteArray();

                                try {
                                    baos.close();
                                } catch (IOException e) {

                                }
                                baos = null;


                                obj.put("thumbnail", Base64.encodeToString(b, Base64.DEFAULT));


                                AppController.getInstance().uploadFile(uri,
                                        AppController.getInstance().userId + id, 1, obj, (String) map.get("to"),
                                        id, mapTemp, secretId);

                                uri = null;
                                b = null;
                                bm = null;
                            }


                        } else if (type.equals("2")) {
  /*
                                 * Video message
                                 */


                            Uri uri;

                            try {
                                File video = new File(message);


                                if (video.exists())


                                {

                                    byte[] b = convertFileToByteArray(video);
                                    video = null;


                                    File f = AppController.getInstance().convertByteArrayToFileToUpload(b, id, ".mp4");
                                    b = null;

                                    uri = Uri.fromFile(f);
                                    f = null;

                                    b = null;

                                    if (uri != null) {


                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        Bitmap bm = ThumbnailUtils.createVideoThumbnail(message,
                                                MediaStore.Images.Thumbnails.MINI_KIND);

                                        if (bm != null) {

                                            bm.compress(Bitmap.CompressFormat.JPEG, 1, baos);
                                            bm = null;
                                            b = baos.toByteArray();
                                            try {
                                                baos.close();
                                            } catch (IOException e) {

                                            }
                                            baos = null;


                                            obj.put("thumbnail", Base64.encodeToString(b, Base64.DEFAULT));


                                            AppController.getInstance().uploadFile(uri,
                                                    AppController.getInstance().userId + id, 2, obj,
                                                    (String) map.get("to"), id, mapTemp, secretId);
                                            uri = null;
                                            b = null;

                                        }
                                    }

                                }


                            } catch (OutOfMemoryError e) {

                            } catch (Exception e) {


                                    /*
                                     *
                                     * to handle the file not found exception incase file has not been found
                                     *
                                     * */


                            }


                        } else if (type.equals("3")) {


                                  /*
                                 * Location message
                                 */

                            try {
                                obj.put("payload", (Base64.encodeToString(message.getBytes("UTF-8"), Base64.DEFAULT)).trim());

                            } catch (UnsupportedEncodingException e) {

                            }

                            AppController.getInstance().publishChatMessage(MqttEvents.Message.value + "/" + to, obj, 1, false, mapTemp);

                            String tsInGmt = Utilities.tsInGmt();
                            String docId = AppController.getInstance().findDocumentIdOfReceiver((String) map.get("to"),
                                    secretId);
                            db.updateMessageTs(docId, id, tsInGmt);
                        } else if (type.equals("4")) {

                                  /*
                                 * Contact message
                                 */

                            try {


                                obj.put("payload", (Base64.encodeToString(message.getBytes("UTF-8"), Base64.DEFAULT)).trim());

                            } catch (UnsupportedEncodingException e) {

                            }


                            AppController.getInstance().publishChatMessage(MqttEvents.Message.value + "/" + to, obj, 1, false, mapTemp);


                            String tsInGmt = Utilities.tsInGmt();
                            String docId = AppController.getInstance().findDocumentIdOfReceiver((String) map.get("to"), secretId);
                            db.updateMessageTs(docId, id, tsInGmt);
                        } else if (type.equals("5")) {

  /*
                                 * Audio message
                                 */


                            Uri uri;
                            try {

                                File audio = new File(message);


                                if (audio.exists()) {


                                    byte[] b = convertFileToByteArray(audio);
                                    audio = null;


                                    File f = AppController.getInstance().convertByteArrayToFileToUpload(b, id, ".mp3");
                                    b = null;

                                    uri = Uri.fromFile(f);
                                    f = null;

                                    b = null;


                                    if (uri != null) {


                                        AppController.getInstance().uploadFile(uri, AppController.getInstance().userId + id,
                                                5, obj, (String) map.get("to"), id, mapTemp, secretId);
                                    }


                                }
                            } catch (Exception e) {


                                    /*
                                     *
                                     * to handle the file not found exception incase file has not been found
                                     *
                                     * */


                            }
                        } else if (type.equals("6")) {
                            /*
                             * Sticker
                             */
                            try {
                                obj.put("payload", (Base64.encodeToString(message.getBytes("UTF-8"), Base64.DEFAULT)).trim());

                            } catch (UnsupportedEncodingException e) {

                            }


                            AppController.getInstance().publishChatMessage(MqttEvents.Message.value + "/" + to, obj, 1, false, mapTemp);

                            String tsInGmt = Utilities.tsInGmt();
                            String docId = AppController.getInstance().findDocumentIdOfReceiver((String) map.get("to"), secretId);
                            db.updateMessageTs(docId, id, tsInGmt);

                        } else if (type.equals("7")) {
                            /*
                             *Doodle
                             */

                            Uri uri = null;
                            Bitmap bm = null;


                            try {

                                final BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inJustDecodeBounds = true;
                                BitmapFactory.decodeFile(message, options);


                                int height = options.outHeight;
                                int width = options.outWidth;

                                float density = AppController.getInstance().getResources().getDisplayMetrics().density;
                                int reqHeight;


                                reqHeight = (int) ((150 * density) * (height / width));

                                bm = AppController.getInstance().decodeSampledBitmapFromResource(message, (int) (150 * density), reqHeight);


                                if (bm != null) {


                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                                    bm.compress(Bitmap.CompressFormat.JPEG, 50, baos);


                                    byte[] b = baos.toByteArray();

                                    try {
                                        baos.close();
                                    } catch (IOException e) {

                                    }
                                    baos = null;


                                    File f = AppController.getInstance().convertByteArrayToFileToUpload(b, id, ".jpg");
                                    b = null;

                                    uri = Uri.fromFile(f);
                                    f = null;


                                }


                            } catch (OutOfMemoryError e) {


                            } catch (Exception e) {

                                    /*
                                     *
                                     * to handle the file not found exception
                                     *
                                     *
                                     * */


                            }


                            if (uri != null) {


                                    /*
                                     *
                                     *
                                     * make thumbnail
                                     *
                                     * */

                                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                                bm.compress(Bitmap.CompressFormat.JPEG, 1, baos);


                                bm = null;
                                byte[] b = baos.toByteArray();
                                try {
                                    baos.close();
                                } catch (IOException e)
                                {
                                    e.printStackTrace();
                                }
                                baos = null;


                                obj.put("thumbnail", Base64.encodeToString(b, Base64.DEFAULT));


                                AppController.getInstance().uploadFile(uri, AppController.getInstance().userId + id, 7, obj,
                                        (String) map.get("to"), id, mapTemp, secretId);

                                uri = null;
                                b = null;
                                bm = null;
                            }

                        } else if (type.equals("8")) {
                            /*
                             *Gif
                             */
                            try {
                                obj.put("payload", (Base64.encodeToString(message.getBytes("UTF-8"), Base64.DEFAULT)).trim());
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            AppController.getInstance().publishChatMessage(MqttEvents.Message.value + "/" + to, obj, 1, false, mapTemp);
                            String tsInGmt = Utilities.tsInGmt();
                            String docId = AppController.getInstance().findDocumentIdOfReceiver((String) map.get("to"), secretId);
                            db.updateMessageTs(docId, id, tsInGmt);
                        }
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    } catch (OutOfMemoryError e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    /*
     *disconnect the last mqtt client */
    public void disconnect()
    {
        if (null != mqttAndroidClient && mqttAndroidClient.isConnected()) {
            try
            {
                updatePresence(0, true);
                unSubscribeAll();
                mqttAndroidClient.disconnect();
                mqttAndroidClient.unregisterResources();
                mqttAndroidClient = null;
            } catch (MqttException e)
            {
                e.printStackTrace();
            }
        }
    }
    /**
     * To generate the push notifications locally
     */

    @SuppressWarnings("unchecked")
    private void generatePushNotificationLocal(String notificationId, String messageType,String offertype,String senderName, String actualMessage, Intent intent, String secretId, String receiverUid)
    {
        Log.d("Notification","Yes in");
        int message_type=Integer.parseInt(messageType);
        if ((!foreground) || (activeReceiverId.isEmpty()) || (!(activeReceiverId.equals(receiverUid) && activeSecretId.equals(secretId))))
        {
            try {
                String pushMessage = "";
                switch (message_type)
                {
                    case 15:
                        switch (offertype)
                        {
                            case "1":
                                pushMessage=""+senderName+" "+"has made an offer on your product";
                                break;
                            case "2":
                                pushMessage=""+"Congratulations User "+senderName+" has accepted your offer.";
                                break;
                            case "3":
                                pushMessage=""+senderName+" "+"has sent a counter offer on your product";
                                break;
                            default:
                                try {
                                    pushMessage = new String(Base64.decode(actualMessage, Base64.DEFAULT), "UTF-8");
                                    pushMessage="Got offer"+" "+pushMessage;
                                } catch (UnsupportedEncodingException e)
                                {
                                    e.printStackTrace();
                                }
                        }
                        break;
                    case 16:
                        pushMessage=""+senderName+"has shared the payment link with you.";
                        break;
                    case 0:
                        try {
                            pushMessage = new String(Base64.decode(actualMessage, Base64.DEFAULT), "UTF-8");
                            if (pushMessage.trim().isEmpty()) {
                                pushMessage = getResources().getString(R.string.youAreInvited) + " " + senderName + " " +
                                        getResources().getString(R.string.JoinSecretChat);
                            }
                        } catch (UnsupportedEncodingException e)
                        {
                            e.printStackTrace();
                        }
                        break;
                    case 1:
                        pushMessage =""+senderName+" "+"has sent you an image";
                        break;
                    case 2:
                        pushMessage = "Video";
                        break;
                    case 3:
                        pushMessage =""+senderName+" "+"has shared the location with you.";
                        break;
                    case 4:
                        pushMessage = "Contact";
                        break;
                    case 5:
                        pushMessage = "Audio";
                        break;
                    case 6:
                        pushMessage = "Sticker";
                        break;
                    case 7:
                        pushMessage = "Doodle";
                        break;
                    default:
                        pushMessage = "Gif";
                }
                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                Map<String, Object> notificationInfo = fetchNotificationInfo(notificationId);
                int unreadMessageCount;
                int systemNotificationId;
                ArrayList<String> messages;
                if (notificationInfo == null)
                {
                    notificationInfo = new HashMap<>();
                    messages = new ArrayList<>();
                    messages.add(pushMessage);
                    notificationInfo.put("notificationMessages", messages);
                    notificationInfo.put("notificationId", notificationId);
                    systemNotificationId = Integer.parseInt(String.valueOf(System.currentTimeMillis()).substring(9));
                    notificationInfo.put("systemNotificationId", systemNotificationId);
                    unreadMessageCount = 0;
                } else {
                    messages = (ArrayList<String>) notificationInfo.get("notificationMessages");
                    messages.add(0, pushMessage);
                    if (messages.size() > NOTIFICATION_SIZE) {
                        messages.remove(messages.size() - 1);
                    }
                    systemNotificationId = (int) notificationInfo.get("systemNotificationId");
                    notificationInfo.put("notificationMessages", messages);
                    unreadMessageCount = (int) notificationInfo.get("messagesCount");
                }
                notificationInfo.put("messagesCount", unreadMessageCount + 1);
                addOrUpdateNotification(notificationInfo, notificationId);
                inboxStyle.setBigContentTitle(senderName);
                for (int i = 0; i < messages.size(); i++) {
                    inboxStyle.addLine(messages.get(i));
                }

                if (unreadMessageCount > (NOTIFICATION_SIZE - 1)) {
                    inboxStyle.setSummaryText("+" + (unreadMessageCount - (NOTIFICATION_SIZE - 1)) + " " + getString(R.string.MoreMessage));
                }


                PendingIntent pendingIntent = PendingIntent.getActivity(this, systemNotificationId, intent, PendingIntent.FLAG_ONE_SHOT);

                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                String title, tickerText;
                if(message_type==15||message_type==16)
                {
                    pushMessage =""+pushMessage;
                    tickerText=pushMessage;
                    title = getString(R.string.app_name);
                }else if (messages.size() == 1)
                {

                    pushMessage = senderName + ": " + pushMessage;
                    tickerText = pushMessage;
                    title = getString(R.string.app_name);
                } else {
                    tickerText = senderName + ": " + pushMessage;
                    title = senderName;
                }

                NotificationCompat.Builder
                        notificationBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                                R.drawable.ic_launcher))
                        .setContentTitle(title)
                        .setContentText(pushMessage)
                        .setTicker(tickerText)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                        .setStyle(inboxStyle)
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setPriority(Notification.PRIORITY_HIGH);

                if (Build.VERSION.SDK_INT >= 21) notificationBuilder.setVibrate(new long[0]);

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                /*
                 *Notification details. */
                notificationManager.notify(notificationId, systemNotificationId, notificationBuilder.build());
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onActivityCreated(Activity activity, Bundle bundle)
    {
        /*
        * As app can also be started when clicked on the chat notification*/
        if (AppController.getInstance().getSignedIn() && activity.getClass().getSimpleName().equals("MainActivity")) {
            foreground = true;
            setApplicationKilled(false);
            updatePresence(1, false);
        }
    }

    @Override
    public void onActivityDestroyed(Activity activity) {}
    @Override
    public void onActivityPaused(Activity activity) {}
    @Override
    public void onActivityResumed(Activity activity) {}
    @Override
    public void onActivitySaveInstanceState(Activity activity,
                                            Bundle outState) {}
    @Override
    public void onActivityStarted(Activity activity) {}
    @Override
    public void onActivityStopped(Activity activity) {}
    public void updateLastSeenSettings(boolean lastSeenSetting)
    {
        sharedPref.edit().putBoolean("enableLastSeen", lastSeenSetting).apply();
    }
    @SuppressWarnings("unchecked")
    public void getCurrentTime()
    {
        new FetchTime().execute();
    }


    public String randomString()
    {
        char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        sb.append("PnPLabs3Embed");
        return sb.toString();
    }


    /**
     * Convert image or video or audio to byte[] so that it can be sent on socket(Unsetn messages)
     */
    @SuppressWarnings("TryWithIdenticalCatches")

    private static byte[] convertFileToByteArray(File f) {
        byte[] byteArray = null;
        byte[] b;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {

            InputStream inputStream = new FileInputStream(f);
            b = new byte[2663];

            int bytesRead;

            while ((bytesRead = inputStream.read(b)) != -1) {
                bos.write(b, 0, bytesRead);
            }

            byteArray = bos.toByteArray();
        } catch (IOException e) {

        } catch (OutOfMemoryError e) {

        } finally {
            b = null;

            try {
                bos.close();

            } catch (IOException e) {

            }

        }

        return byteArray;
    }

    /**
     * To find the document id of the receiver on receipt of new message,if exists or create a new document for chat with that receiver and return its document id
     */
    @SuppressWarnings("unchecked")
    public static String findDocumentIdOfReceiver(String receiverUid, String timestamp, String receiverName,
                                                  String receiverImage, String secretId, boolean invited, String receiverIdentifier, String chatId,String productImage,String productName,String productPrice,boolean isSlod,boolean isAccepted)
    {
        CouchDbController db = AppController.getInstance().getDbController();
        Map<String, Object> chatDetails = db.getAllChatDetails(AppController.getInstance().getChatDocId());
        if (chatDetails != null)
        {
            ArrayList<String> receiverUidArray = (ArrayList<String>) chatDetails.get("receiverUidArray");
            ArrayList<String> receiverDocIdArray = (ArrayList<String>) chatDetails.get("receiverDocIdArray");
            ArrayList<String> secretIdArray = (ArrayList<String>) chatDetails.get("secretIdArray");
            for (int i = 0; i < receiverUidArray.size(); i++)
            {
                if (receiverUidArray.get(i).equals(receiverUid) && secretIdArray.get(i).equals(secretId)) {
                    return receiverDocIdArray.get(i);
                }
            }
        }
        /*  here we also need to enter receiver name*/
        String docId = db.createDocumentForChat(timestamp, receiverUid, receiverName, receiverImage, secretId, invited, receiverIdentifier, chatId,productImage,productName,productPrice,isSlod,isAccepted);
        db.addChatDocumentDetails(receiverUid, docId, AppController.getInstance().getChatDocId(), secretId);
        return docId;
    }



    @SuppressWarnings("TryWithIdenticalCatches")
    private class FetchTime extends AsyncTask
    {
        @Override
        protected Object doInBackground(Object[] params)
        {
            sharedPref.edit().putBoolean("deltaRequired", true).apply();
            String url_ping = "https://google.com/";
            URL url = null;
            try {
                url = new URL(url_ping);
            } catch (MalformedURLException e) {}
            try {
/*
 * Maybe inaccurate due to network inaccuracy
 */
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                if (urlc.getResponseCode() == 200 || urlc.getResponseCode() == 503)
                {
                    long dateStr = urlc.getDate();
                    //Here I do something with the Date String
                    timeDelta = System.currentTimeMillis() - dateStr;
                    sharedPref.edit().putBoolean("deltaRequired", false).apply();
                    sharedPref.edit().putLong("timeDelta", timeDelta).apply();
                    urlc.disconnect();
                }
            } catch (IOException e) {


                /*
                 * Should disable user from using the app
                 */


            } catch (NullPointerException e) {

            }
            return null;
        }

    }


    /**
     * To fetch a particular notification info
     */
    private Map<String, Object> fetchNotificationInfo(String notificationId) {

        for (int i = 0; i < notifications.size(); i++) {

            if (notifications.get(i).get("notificationId").equals(notificationId)) {

                return notifications.get(i);
            }
        }
        return null;
    }

    /**
     * To add or update the content of the notifications
     */
    private void addOrUpdateNotification(Map<String, Object> notification, String notificationId)
    {
        db.addOrUpdateNotificationContent(notificationDocId, notificationId, notification);
        for (int i = 0; i < notifications.size(); i++) {

            if (notifications.get(i).get("notificationId").equals(notificationId)) {
                notifications.set(i, notification);
                return;
            }
        }
        notifications.add(0, notification);
    }


    /*
     * To remove a particular notification
     */

    public void removeNotification(String notificationId)
    {
        boolean found = false;
        for (int i = 0; i < notifications.size(); i++) {

            if (notifications.get(i).get("notificationId").equals(notificationId)) {
                notifications.remove(i);
                found = true;
                break;
            }
        }

        if (found) {
            //  db.getParticularNotificationId(notificationDocId, notificationId);

            int systemNotificationId = db.removeNotification(notificationDocId, notificationId);
            if (systemNotificationId != -1) {
                NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                nMgr.cancel(notificationId, systemNotificationId);
            }
        }
    }

    /*
     *Doing user logged out. */
    public void doLoggedOut()
    {
        disconnect();
        db.updateIndexDocumentOnSignOut(indexDocId);
        this.signedIn=false;
    }

    /*
     *Doing the logged out of the previous user topic. */
    private void unSubscribeAll()
    {
        if(userId!=null&&!userId.isEmpty())
        {
            unsubscribeToTopic(MqttEvents.Message.value + "/" + userId);
            unsubscribeToTopic(MqttEvents.OfferMessage.value + "/" + userId);
            unsubscribeToTopic(MqttEvents.Acknowledgement.value + "/" + userId);
            unsubscribeToTopic(MqttEvents.FetchMessages.value + "/" + userId);
            unsubscribeToTopic(MqttEvents.FetchChats.value + "/" +userId);
            unsubscribeToTopic(MqttEvents.UpdateProduct.value + "/" +userId);
            unsubscribeToTopic(MqttEvents.UserUpdates.value+"/"+userId);
            unsubscribeToTopic(MqttEvents.Typing.value+"/"+userId);
        }
    }


    /*
    *Sending message to the FCM for IOS device Notifcation
    * problem.*/
    public void sendMessageToFcm(String topicName,JSONObject messageObject) throws Exception
    {
        String topic=topicName.substring(topicName.indexOf("/")+1,topicName.length());
        String to ="/topics/"+topic;
        String messageType= messageObject.getString("type");
        String actualMessage= messageObject.getString("payload").trim();
        String senderName=messageObject.getString("name");
        String offerType="1";
        if(messageObject.has("offerType"))
        {
            offerType = messageObject.getString("offerType");
        }
        String productId=messageObject.getString("secretId");
        String receiverId=messageObject.getString("from");
        String pushMessage ="";
        int message_type=Integer.parseInt(messageType);
        String title="";
        switch (message_type)
        {
            case 15:
                switch (offerType)
                {
                    case "1":
                        pushMessage=""+"You got an offer on your product";
                        break;
                    case "2":
                        pushMessage=""+"Congratulations User "+senderName+" has accepted your offer.";
                        break;
                    case "3":
                        pushMessage=""+"You got a counter offer on your product";
                        break;
                    default:
                        try {
                            pushMessage = new String(Base64.decode(actualMessage, Base64.DEFAULT), "UTF-8");
                            pushMessage="Got offer"+" "+pushMessage;
                        } catch (UnsupportedEncodingException e)
                        {
                            e.printStackTrace();
                        }
                }
                break;
            case 16:
                pushMessage=""+senderName+"has shared the payment link with you.";
                break;
            case 0:
                title=senderName;
                try {
                    pushMessage = new String(Base64.decode(actualMessage, Base64.DEFAULT), "UTF-8");
                } catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                    pushMessage="";
                }
                break;
            case 1:
                pushMessage =""+senderName+" "+"has sent you an image";
                break;
            case 2:
                pushMessage = "Video";
                break;
            case 3:
                pushMessage =""+senderName+" "+"has shared the location with you.";
                break;
            case 4:
                pushMessage = "Contact";
                break;
            case 5:
                pushMessage = "Audio";
                break;
            case 6:
                pushMessage = "Sticker";
                break;
            case 7:
                pushMessage = "Doodle";
                break;
            default:
                pushMessage = "Gif";
        }
        sendPushToSingleInstance(to,title,pushMessage,productId,receiverId);

//        FirebaseMessaging.getInstance().subscribeToTopic(to);
    }
    /*
     * Sending message to the FCM*/
    private void sendPushToSingleInstance(final String topicName, final String title, final String message, final String productId, final String receiverID) {

        if(mSessionManager == null){
            mSessionManager = new SessionManager( getApplicationContext());
        }

        final String url = "https://fcm.googleapis.com/fcm/send";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            public byte[] getBody()
            {
                JSONObject notification = new JSONObject();
                JSONObject root = new JSONObject();
                try {
                    if(title.isEmpty())
                    {
                        notification.put("title",""+message);
                    }else
                    {
                        notification.put("title",""+title);
                        notification.put("body",""+message);
                    }
                    notification.put("sound","default");
                    JSONObject body=new JSONObject();
                    body.put("receiverID",receiverID);
                    body.put("secretID",productId);
                    JSONObject data = new JSONObject();
                    data.put("message",message);
                    data.put("body",body);
                    root.put("notification",notification);
                    root.put("data",data);
//                    root.put("to",topicName);  //"condition": "'\(topic)' in topics"
                    root.put("to",mSessionManager.getOtherUserPushToken( ));
                } catch (JSONException e)

                {
                    e.printStackTrace();
                }
                Log.d("ddfs",root.toString());
                return root.toString().getBytes();
            }

            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "key=" + ApiOnServer.PUSH_KEY);
                return headers;
            }
        };
        AppController.getInstance().addToRequestQueue(jsonObjReq, "PushSend");
    }
}
