package com.yelo.com.mqttchat.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.yelo.com.R;
import com.yelo.com.main.activity.HomePageActivity;
import com.yelo.com.mqttchat.Activities.ChatMessageScreen;
import com.yelo.com.mqttchat.Adapters.ReceivedChatsAdapter;
import com.yelo.com.mqttchat.AppController;
import com.yelo.com.mqttchat.Database.CouchDbController;
import com.yelo.com.mqttchat.ModelClasses.ChatlistItem;
import com.yelo.com.mqttchat.ModelClasses.Server_chat_holder;
import com.yelo.com.mqttchat.Utilities.ApiOnServer;
import com.yelo.com.mqttchat.Utilities.MqttEvents;
import com.yelo.com.mqttchat.Utilities.RecyclerItemClickListener;
import com.yelo.com.mqttchat.Utilities.SlackLoadingView;
import com.yelo.com.mqttchat.Utilities.TimestampSorter;
import com.yelo.com.mqttchat.Utilities.Utilities;
import com.yelo.com.utility.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
/**
 *<h2>BuyingFragment</h2>
 * <P>
 *   Buying fragment to show the buying list of the user.
 * </P>
 * @author 3Embed.
 * @since 13.06.2016.
 * @version 1.o.
 *  */
public class BuyingFragment extends Fragment
{
    private static Bus bus = AppController.getBus();
    private ReceivedChatsAdapter mAdapter;
    private RecyclerView recyclerView_chat;
    private ArrayList<ChatlistItem> mChatData;
    private View view;
    private boolean firstTime = true;
    private CoordinatorLayout searchRoot;
    private RelativeLayout root;
    private RelativeLayout dataFound;
    private RelativeLayout data_not_found;
    private SessionManager sessionManager;
    private HomePageActivity homePageActivity;

    public static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;
        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private SlackLoadingView slack;

    @Override
    @SuppressWarnings("unchecked")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.chatlist, container, false);
        } else {
            if (view.getParent() != null)
                ((ViewGroup) view.getParent()).removeView(view);
        }
        dataFound = (RelativeLayout) view.findViewById(R.id.dataFound);
        data_not_found = (RelativeLayout) view.findViewById(R.id.data_not_found);
        root = (RelativeLayout) view.findViewById(R.id.root);
        slack = (SlackLoadingView) view.findViewById(R.id.slack);
        searchRoot = (CoordinatorLayout) view.findViewById(R.id.root2);
        mChatData = new ArrayList<>();
        recyclerView_chat = (RecyclerView) view.findViewById(R.id.rv);
        recyclerView_chat.setHasFixedSize(true);
        mAdapter = new ReceivedChatsAdapter(getActivity(), mChatData, this);
        recyclerView_chat.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView_chat.setItemAnimator(new DefaultItemAnimator());
        recyclerView_chat.setAdapter(mAdapter);
        ItemTouchHelper.Callback callback = new ChatMessageTouchHelper();
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView_chat);

        recyclerView_chat.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView_chat, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if (position >= 0)
                {
                    final ChatlistItem item = mAdapter.getList().get(position);


                    Intent intent;
                    intent = new Intent(view.getContext(), ChatMessageScreen.class);
                    intent.putExtra("isNew", false);
                    intent.putExtra("receiverUid", item.getReceiverUid());
                    intent.putExtra("receiverName", item.getReceiverName());
                    intent.putExtra("documentId", item.getDocumentId());
                    intent.putExtra("receiverIdentifier", item.getReceiverIdentifier());
                    intent.putExtra("receiverImage", item.getReceiverImage());
                    intent.putExtra("isFromOfferPage", false);
                    intent.putExtra("colorCode", AppController.getInstance().getColorCode(position % 19));
                    if (item.hasNewMessage()) {
                        item.sethasNewMessage(false);
                    }
                    startActivity(intent);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        }));
        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!sessionManager.getChatSync()) {
            fetchChatHistory();
        }
    }

    public void showNoSearchResults(CharSequence constraint) {
        if (searchRoot != null) {
            Snackbar snackbar = Snackbar.make(searchRoot, getString(R.string.NoChatWith) + " " + constraint, Snackbar.LENGTH_SHORT);
            snackbar.show();
            View view = snackbar.getView();
            view.setBackgroundColor(Color.WHITE);
            TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            txtv.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_text_black));
            txtv.setGravity(Gravity.CENTER_HORIZONTAL);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onResume() {
        super.onResume();
        if (firstTime) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (getActivity() != null)
                        addChat();
                }
            }, 500);
            firstTime = false;
        } else {
            if (getActivity() != null)
                addChat();
        }
    }

    /*
     *performing the chat sync */
    public void performChatSync() {
        setProgressBar(true);
        fetchChatHistory();
    }

    /*
     * performing the filture*/
    public void performFiltre(String text) {
        try {
            mAdapter.getFilter().filter(text);
        }catch (Exception e){
        }
    }

    /**
     * To fetch the chats from the couchdb, stored locally
     */
    @SuppressWarnings("unchecked")
    public void addChat() {
        if (getActivity() != null) {
            mChatData.clear();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mAdapter != null)
                        mAdapter.notifyDataSetChanged();
                }
            });
            CouchDbController db = AppController.getInstance().getDbController();
            Map<String, Object> map = db.getAllChatDetails(AppController.getInstance().getChatDocId());
            if (map != null) {
                ArrayList<String> receiverUidArray = (ArrayList<String>) map.get("receiverUidArray");
                ArrayList<String> receiverDocIdArray = (ArrayList<String>) map.get("receiverDocIdArray");
                ArrayList<Map<String, Object>> chats = new ArrayList<>();
                for (int i = 0; i < receiverUidArray.size(); i++) {
                    chats.add(db.getParticularChatInfo(receiverDocIdArray.get(i)));
                    Collections.sort(chats, new TimestampSorter());
                }
                for (int i = 0; i < chats.size(); i++) {
                    ChatlistItem chat = parseData(chats.get(i));
                    if (chat != null) {
                        mChatData.add(chat);
                        showDataFound(true);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                            }
                        });

                    }
                    recyclerView_chat.scrollToPosition(0);
                }
            }
            chatNotFound();
        }
    }

    /*
     *Chat not found . Updating the UI. */
    private void chatNotFound()
    {
        if (mChatData == null || mChatData.size() == 0) {

            showDataFound(false);
        }else
        {
            showDataFound(true);
        }
    }

    /*
     * Checking chat is already exist.
     */
    public int alreadyInContact(String sender, String secretId) {
        int j = -1;
        if (mChatData == null || mChatData.size() == 0) {
            return j;
        }
        for (int i = 0; i < mChatData.size(); i++) {
            if (mChatData.get(i).getReceiverUid().equals(sender)) {
                if (secretId.isEmpty()) {
                    j = i;
                    break;
                } else {
                    if (mChatData.get(i).getSecretId().equals(secretId)) {
                        j = i;
                        break;
                    }
                }
            }
        }
        return j;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (getActivity() != null) {
                hideKeyboard(getActivity());
            }
        }
    }

    /*
     *On create of the Server.. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homePageActivity= (HomePageActivity) getActivity();
        sessionManager = new SessionManager(getActivity());
        bus.register(this);
    }

    @Override
    public void onDestroy() {
        bus.unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void getMessage(JSONObject object) {
        try {
            Log.d("dsadas12", "Sellign the caht doc " + object.getString("eventName"));
            if (object.getString("eventName").equals(MqttEvents.FetchChats.value + "/" + AppController.getInstance().getUserId())) {
                setProgressBar(false);
                try {
                    if (mChatData.size() > 0) {
                        mChatData.clear();
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                addChatsFetchedFromServer(object.getJSONArray("chats"));
                if(homePageActivity!=null)
                {
                    int count=homePageActivity.countUnreadChat();
                    homePageActivity.setChatCount(count);
                }
                JSONObject obj = new JSONObject();
                obj.put("eventName", "syncedChats");
                bus.post(obj);
                AppController.getInstance().setChatSynced(true);
            } else if (object.getString("eventName").equals(MqttEvents.Message.value + "/" + AppController.getInstance().getUserId()) || object.getString("eventName").equals(MqttEvents.OfferMessage.value + "/" + AppController.getInstance().getUserId())) {
                String sender = object.getString("from");
                String secretId = object.getString("secretId");
                boolean isSender;
                if (AppController.getInstance().getUserId() != null && AppController.getInstance().getUserId().equals(sender)) {
                    isSender = true;
                    sender = object.getString("to");
                } else {
                    isSender = false;
                }
                String docId = AppController.getInstance().findDocumentIdOfReceiver(sender, secretId);
                if (!AppController.getInstance().getDbController().checkIfInvited(docId)) {
                    Map<String, Object> chat_item = AppController.getInstance().getDbController().getParticularChatInfo(docId);
                    secretId = (String) chat_item.get("secretId");
                    String producImage = (String) chat_item.get("productImage");
                    String productName = (String) chat_item.get("productName");
                    String timestamp = object.getString("timestamp");
                    String messageType = object.getString("type");
                    String name = object.getString("name");
                    String message;
                    switch (Integer.parseInt(messageType)) {
                        case 0:
                            message = object.getString("payload").trim();
                            if (message.isEmpty()) {
                                message = getResources().getString(R.string.youAreInvited) + " " + name + " " +
                                        getResources().getString(R.string.JoinSecretChat);
                            } else {
                                try {
                                    message = new String(Base64.decode(message, Base64.DEFAULT), "UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }

                            break;
                        case 1:
                            message = getString(R.string.NewImage);
                            break;
                        case 2:
                            message = getString(R.string.NewVideo);
                            break;
                        case 3:
                            message = getString(R.string.NewLocation);
                            break;
                        case 4:
                            message = getString(R.string.NewContact);
                            break;
                        case 5:
                            message = getString(R.string.NewAudio);
                            break;
                        case 6:
                            message = getString(R.string.NewSticker);
                            break;
                        case 7:
                            message = getString(R.string.NewDoodle);
                            break;
                        case 15:
                            if (isSender) {
                                message = "Offer sent";
                            } else {
                                message = "Counter Offer received";
                            }
                            break;
                        case 16:
                            if (isSender) {

                                message = "Payment link shared.";
                            } else {
                                message = "Payment link received.";
                            }
                            break;
                        default:
                            message = getString(R.string.NewGiphy);
                            break;
                    }
                    ChatlistItem chat = new ChatlistItem();
                    chat.setReceiverUid(sender);
                    chat.setNewMessage(message);
                    chat.setSecretId(secretId);
                    chat.sethasNewMessage(true);
                    chat.setDocumentId(docId);
                    chat.setReceiverIdentifier("");
                        /*
                         * If userId doesn't exists in contact
                         */
                    chat.setReceiverName(name);
                    if (object.has("userImage")) {
                        chat.setReceiverImage(object.getString("userImage"));
                    } else {
                        chat.setReceiverImage("");
                    }
                    boolean isSold=false;
                    if (object.has("isSold")) {
                        if(object.getString("isSold").equals("1"))
                        {
                            isSold=true;
                        }
                    }
                    chat.setSold(isSold);
                    chat.setProductImage(producImage);
                    chat.setProductName(productName);
                    chat.setNewMessageTime(Utilities.epochtoGmt(timestamp));
                    chat.setNewMessageCount(AppController.getInstance().getDbController().getNewMessageCount(
                            AppController.getInstance().findDocumentIdOfReceiver(sender, secretId)));
                    int alreadyInContact = alreadyInContact(sender, secretId);
                    if (alreadyInContact == -1) {
                        mChatData.add(0, chat);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                    } else {
                        mChatData.remove(alreadyInContact);
                        mChatData.add(0, chat);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                mAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                    showDataFound(true);
                }
            } else if (object.getString("eventName").equals("ChatDeleted"))
            {
                String secretId = object.getString("secretId");
                String receiverUid = object.getString("receiverUid");
                ChatlistItem item;
                for (int i = 0; i < mChatData.size(); i++) {
                    try {
                        item = mChatData.get(i);
                        if (item.getReceiverUid().equals(receiverUid) && item.getSecretId().equals(secretId)) {
                            mChatData.remove(item);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                chatNotFound();
            }
            else  if(object.getString("eventName").equals(MqttEvents.UpdateProduct.value+"/"+AppController.getInstance().getUserId()))
            {
                String id=object.getString("id");
                if(mChatData!=null&&mChatData.size()>0)
                {
                    String image,name;
                    boolean isSold=false;
                    if(object.has("name"))
                    {
                        name=object.getString("name");
                    }else
                    {
                        name="";
                    }
                    if(object.has("image"))
                    {
                        image=object.getString("image");
                    }else
                    {
                        image="";
                    }
                    if(object.has("sold"))
                    {
                        isSold=object.getBoolean("sold");
                    }
                    for(ChatlistItem chatlistItem:mChatData)
                    {
                        if(id.equals(chatlistItem.getSecretId()))
                        {
                            chatlistItem.setSold(isSold);
                            if(!name.isEmpty())
                            {
                                chatlistItem.setProductName(name);
                            }
                            if(!image.isEmpty())
                            {
                                chatlistItem.setProductImage(image);
                            }
                        }
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
     * To fetch the list of the chats in the background
     */
    private void fetchChatHistory() {
        sessionManager.setChatSync(false);
        AppController.getInstance().setChatSynced(false);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                ApiOnServer.FETCH_CHATS + "/0", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                setProgressBar(false);
                try {
                    if (response.getInt("code") != 200) {
                        Log.d("log47", "" + response.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                setProgressBar(false);
                error.printStackTrace();
            }
        })

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", ApiOnServer.AUTH_KEY);
                headers.put("token", sessionManager.getAuthToken());
                return headers;
            }
        };


        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
/* Add the request to the RequestQueue.*/
        AppController.getInstance().addToRequestQueue(jsonObjReq, "getChatsApi");
    }

    /*
     * To add the chats fetched from the server
     */
    private void addChatsFetchedFromServer(JSONArray chats) {
        JSONObject chat;
        ChatlistItem item;
        String message, receiverName, profilePic;
        int unreadCount;
        ArrayList<Server_chat_holder> temp_server = new ArrayList<>();
        for (int j = chats.length() - 1; j >= 0; j--) {
            try {
                chat = chats.getJSONObject(j);
                if (chat.getBoolean("initiated")) {
                    boolean isSender;
                    unreadCount = chat.getInt("totalUnread");
                    item = new ChatlistItem();
                    item.setReceiverUid(chat.getString("recipientId"));
                    item.setReceiverIdentifier("");
                    receiverName = chat.getString("userName");
                    if (chat.has("profilePic")) {
                        profilePic = chat.getString("profilePic");
                    } else {
                        profilePic = "";
                    }
                    item.setReceiverImage(profilePic);
                    item.setReceiverName(receiverName);
                    item.setNewMessageTime(Utilities.epochtoGmt(String.valueOf(chat.getLong("timestamp"))));
                    item.setDocumentId(AppController.getInstance().findDocumentIdOfReceiver(chat.getString("recipientId"), chat.getString("secretId")));
                    if (chat.getString("senderId").equals(AppController.getInstance().getUserId())) {
                        isSender = true;
                        item.setShowTick(true);
                        item.setTickStatus(chat.getInt("status"));
                    } else {
                        isSender = false;
                        item.setShowTick(false);
                    }
                    switch (chat.getString("messageType")) {
                        case "0":
                            message = chat.getString("payload").trim();
                            if (message.isEmpty()) {
                                if (chat.getString("senderId").equals(AppController.getInstance().getUserId())) {

                                    message = getResources().getString(R.string.YouInvited) + " " + receiverName + " " +
                                            getResources().getString(R.string.JoinSecretChat);

                                } else {
                                    message = getResources().getString(R.string.youAreInvited) + " " + receiverName + " " +
                                            getResources().getString(R.string.JoinSecretChat);
                                }

                            } else {


                                try {
                                    message = new String(Base64.decode(message, Base64.DEFAULT), "UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }

                            break;
                        case "1":
                            if (unreadCount > 0) {
                                message = getString(R.string.NewImage);
                            } else {

                                message = getString(R.string.Image);
                            }
                            break;
                        case "2":

                            if (unreadCount > 0) {
                                message = getString(R.string.NewVideo);
                            } else {
                                message = getString(R.string.Video);

                            }
                            break;
                        case "3":
                            if (unreadCount > 0) {
                                message = getString(R.string.NewLocation);
                            } else {

                                message = getString(R.string.Location);
                            }
                            break;
                        case "4":
                            if (unreadCount > 0) {
                                message = getString(R.string.NewContact);
                            } else {

                                message = getString(R.string.Contact);
                            }
                            break;
                        case "5":
                            if (unreadCount > 0) {
                                message = getString(R.string.NewAudio);
                            } else {

                                message = getString(R.string.Audio);
                            }
                            break;
                        case "6":
                            if (unreadCount > 0) {
                                message = getString(R.string.NewSticker);
                            } else {

                                message = getString(R.string.Stickers);
                            }
                            break;
                        case "7":
                            if (unreadCount > 0) {
                                message = getString(R.string.NewDoodle);
                            } else {
                                message = getString(R.string.Doodle);
                            }
                            break;
                        case "15":
                            if (isSender) {
                                message = "Offer sent";
                            } else {
                                message = "Counter Offer received";
                            }
                            break;
                        case "16": {
                            if (isSender) {
                                message = "Payment link shared.";
                            } else {
                                message = "Payment link received.";
                            }
                        }
                        break;
                        default:
                            if (unreadCount > 0) {
                                message = getString(R.string.NewGiphy);
                            } else {
                                message = getString(R.string.Giphy);
                            }
                            break;
                    }
                    item.setNewMessage(message);
                    item.setNewMessageCount(String.valueOf(unreadCount));
                    item.sethasNewMessage(unreadCount > 0);
                    item.setSecretId(chat.getString("secretId"));
                    String tsInGmt = chat.getString("timestamp");
                    item.setNewMessageTime(Utilities.epochtoGmt(tsInGmt));
                    String productImage = "", productName = "";
                    if (chat.has("productImage")) {
                        productImage = chat.getString("productImage");
                    }
                    if (chat.has("productName")) {
                        productName = chat.getString("productName");
                    }
                    boolean isSold=false;
                    if (chat.has("productSold"))
                    {
                        isSold=chat.getBoolean("productSold");
                    }
                    item.setSold(isSold);
                    Server_chat_holder temp_Data = new Server_chat_holder();
                    temp_Data.setReceiverId(item.getReceiverUid());
                    temp_Data.setSecretId(item.getSecretId());
                    temp_server.add(temp_Data);
                    item.setProductImage(productImage);
                    item.setProductName(productName);
                    String documentId = AppController.getInstance().findDocumentIdOfReceiver(chat.getString("recipientId"), chat.getString("secretId"));
                    item.setDocumentId(documentId);
                    mChatData.add(0, item);
                    showDataFound(true);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyItemInserted(0);
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        checkFromLocal(temp_server);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
                recyclerView_chat.scrollToPosition(0);
            }
        });
        chatNotFound();
    }


    private class ChatMessageTouchHelper extends ItemTouchHelper.Callback {

        private ChatMessageTouchHelper() {
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return false;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            final int position = viewHolder.getAdapterPosition();
            android.support.v7.app.AlertDialog.Builder builder =
                    new android.support.v7.app.AlertDialog.Builder(getActivity(), 0);
            builder.setTitle(getResources().getString(R.string.DeleteConfirmation));
            builder.setMessage(getResources().getString(R.string.DeleteChat));
            builder.setPositiveButton(getResources().getString(R.string.Continue), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {


                    try {
                        ChatlistItem item = mChatData.get(position);
                        deleteChatFromServer(item);

                        mChatData.remove(position);
                        try {

                            JSONObject obj = new JSONObject();
                            obj.put("eventName", "ChatDeleted");
                            obj.put("receiverUid", item.getReceiverUid());
                            obj.put("secretId", item.getSecretId());
                            bus.post(obj);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (position == 0) {

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {


                                    mAdapter.notifyDataSetChanged();
                                }
                            });
                        } else {

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mAdapter.notifyItemRemoved(position);
                                }
                            });
                        }


                    } catch (IndexOutOfBoundsException e)

                    {
                        e.printStackTrace();
                    }

                    dialog.dismiss();
                }
            });

            builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                    dialog.cancel();

                }
            });
            android.support.v7.app.AlertDialog alertDialog = builder.create();


            alertDialog.setOnCancelListener(
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mAdapter.notifyItemChanged(position);
                                }
                            });


                        }
                    });
            alertDialog.show();

            Button b_pos;
            b_pos = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            if (b_pos != null) {
                b_pos.setTextColor(


                        ContextCompat.getColor(getActivity(), R.color.color_black)

                );
            }
            Button n_pos;
            n_pos = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            if (n_pos != null) {
                n_pos.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_black));
            }

        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder
                viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder
                viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }
    }


    private void deleteChatFromServer(final ChatlistItem chat) {
        String secretId = chat.getSecretId();
        if (secretId == null || secretId.isEmpty()) {
            secretId = null;
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.DELETE,
                ApiOnServer.FETCH_CHATS + "/" + chat.getReceiverUid() + "/" + secretId, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    if (response.getInt("code") == 200) {
                        AppController.getInstance().getDbController().deleteParticularChatDetail(AppController.getInstance().getChatDocId(),
                                AppController.getInstance().findDocumentIdOfReceiver(chat.getReceiverUid(), chat.getSecretId()));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if (root != null) {
                    Snackbar snackbar = Snackbar.make(root, R.string.DeleteFailed, Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    View view = snackbar.getView();
                    TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", ApiOnServer.AUTH_KEY);
                headers.put("token", AppController.getInstance().getApiToken());
                return headers;
            }
        };


        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
/* Add the request to the RequestQueue.*/
        AppController.getInstance().addToRequestQueue(jsonObjReq, "deleteChatApiRequest");
    }

    /*
    * Showing the progress bar.*/
    private void setProgressBar(boolean isShowing) {
        if (isShowing) {
            slack.setVisibility(View.VISIBLE);
            slack.start();
        } else {
            slack.setVisibility(View.GONE);
        }
    }


    /**
     * Showing the data found view.
     */
    private void showDataFound(boolean isdataFound) {
        if (isdataFound) {
            if (dataFound.getVisibility() == View.GONE) {
                dataFound.setVisibility(View.VISIBLE);
            }
            if (data_not_found.getVisibility() == View.VISIBLE) {
                data_not_found.setVisibility(View.GONE);
            }
        } else {
            if (dataFound.getVisibility() == View.VISIBLE) {
                dataFound.setVisibility(View.GONE);
            }
            if (data_not_found.getVisibility() == View.GONE) {
                data_not_found.setVisibility(View.VISIBLE);
            }
        }
    }

    /*
    * checking the local data.*/
    private void checkFromLocal(ArrayList<Server_chat_holder> temp)
    {
        CouchDbController db = AppController.getInstance().getDbController();
        Map<String, Object> map=db.getAllChatDetails(AppController.getInstance().getChatDocId());
        if (map != null)
        {
            ArrayList<String> receiverUidArray = (ArrayList<String>) map.get("receiverUidArray");
            ArrayList<String> receiverDocIdArray = (ArrayList<String>) map.get("receiverDocIdArray");
            ArrayList<Map<String, Object>> chats = new ArrayList<>();
            for (int i = 0; i < receiverUidArray.size(); i++)
            {
                chats.add(db.getParticularChatInfo(receiverDocIdArray.get(i)));
                Collections.sort(chats,new TimestampSorter());
            }
            for (int i = 0; i < chats.size(); i++)
            {
                ChatlistItem chat = parseData(chats.get(i));
                if(chat==null)
                    continue;

                boolean isExist=false;
                for (Server_chat_holder holder: temp)
                {
                    if(holder.getReceiverId().equals(chat.getReceiverUid())&&holder.getSecretId().equals(chat.getSecretId()))
                    {
                        isExist=true;
                        break;
                    }
                }
                if(!isExist)
                {
                    chat.setSold(true);
                    db.updateSoldDetails(chat.getDocumentId(),true);
                    mChatData.add(chat);
                    getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }
    }
    /*
    * */
    private ChatlistItem parseData(@NonNull Map<String, Object> chat_item)
    {
        if (!((Boolean) chat_item.get("wasInvited"))) {
            ChatlistItem chat = new ChatlistItem();
            boolean hasNewMessage = (Boolean) chat_item.get("hasNewMessage");
            chat.sethasNewMessage(hasNewMessage);
            try {
                chat.setSecretId((String) chat_item.get("secretId"));
            } catch (NullPointerException e) {
                chat.setSecretId("");
            }
            if (hasNewMessage) {
                chat.setNewMessageTime((String) chat_item.get("newMessageTime"));
                chat.setNewMessage((String) chat_item.get("newMessage"));
                chat.setNewMessageCount((String) chat_item.get("newMessageCount"));
                chat.setShowTick(false);
            } else {
                Map<String, Object> map2 = AppController.getInstance().getDbController().getLastMessageDetails((String) chat_item.get("selfDocId"));
                String time = (String) map2.get("lastMessageTime");
                String message = (String) map2.get("lastMessage");
                chat.setShowTick((boolean) map2.get("showTick"));
                if ((boolean) map2.get("showTick")) {
                    chat.setTickStatus((int) map2.get("tickStatus"));
                }
                chat.setNewMessageTime(time);
                chat.setNewMessage(message);
                chat.setNewMessageCount("0");
                if (message == null) {
                    chat.setNewMessage("");
                }
            }
            chat.setReceiverIdentifier((String) chat_item.get("receiverIdentifier"));
            chat.setDocumentId((String) chat_item.get("selfDocId"));
            chat.setReceiverUid((String) chat_item.get("selfUid"));
            chat.setReceiverName((String) chat_item.get("receiverName"));
            String image = (String) chat_item.get("receiverImage");
            if (image != null && !image.isEmpty()) {
                chat.setReceiverImage(image);
            } else {
                chat.setReceiverImage("");
            }
            String productImage = "", productName = "";
            if (chat_item.containsKey("productImage")) {
                productImage = (String) chat_item.get("productImage");
            }
            if (chat_item.containsKey("productName")) {
                productName = (String) chat_item.get("productName");
            }
            if(chat_item.containsKey("isSold"))
            {
                chat.setSold((boolean) chat_item.get("isSold"));
            }
            chat.setProductImage(productImage);
            chat.setProductName(productName);
            return chat;
        }
        return null;
    }
}
