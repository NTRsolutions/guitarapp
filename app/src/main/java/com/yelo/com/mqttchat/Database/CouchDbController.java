package com.yelo.com.mqttchat.Database;

import android.support.annotation.NonNull;
import android.util.Log;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;
import com.yelo.com.mqttchat.AppController;
import com.yelo.com.mqttchat.Utilities.MessageSorter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
/*
 * Database helper class for couchdb database
 */
public class CouchDbController
{
    public static final String TAG = "CouchBaseEvents";
    public Database database;
    private Manager manager;
    private final static String DB_NAME ="yelodbzza";
    private AndroidContext a_context;

    public CouchDbController(AndroidContext a_context)
    {
        this.a_context = a_context;
        try {
            manager = getManagerInstance();
            database = getDatabaseInstance();
        } catch (Exception e) {
            Log.e(TAG, "Error getting database", e);
        }
    }
    /*
    * To get existing db or create new db if db doesn't exists
    * */

    private Database getDatabaseInstance()
    {
        if ((this.database == null) & (this.manager != null))
        {
            try {
                this.database = manager.getExistingDatabase(DB_NAME);
                if (database == null) {
                    this.database = manager.getDatabase(DB_NAME);
                }
                database.open();
            } catch (CouchbaseLiteException e) {
                Log.e(TAG, "Error getting database", e);
            }
        }
        return database;
    }


    /**
     * to get instance of db manager
     */
    private Manager getManagerInstance() throws IOException {
        if (manager == null) {
            manager = new Manager(a_context, Manager.DEFAULT_OPTIONS);

        }
        return manager;
    }


    /*
    * To create the index document which contains docId for impressions and notificatioImpressions docs along with status of currently signed-in user and array of who all users have signed in on that device
    *
    * */
    public String createIndexDocument()
    {
        Map<String, Object> map = new HashMap<>();
        Document document = database.createDocument();
        map.put("userNameArray", new ArrayList<String>());
        map.put("userDocIdArray", new ArrayList<String>());
        map.put("isSignedIn", false);
        map.put("signedUserId", null);
        try {
            document.putProperties(map);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting", e);
        }
        return document.getId();
    }

    /**
     * To add the user details to index doc whenever new user signs in
     */
    @SuppressWarnings("unchecked")
    public void addToIndexDocument(String docId, String userName, String userdocId)
    {
        Document document = database.getDocument(docId);
        Map<String, Object> map_old = document.getProperties();
        if (map_old != null) {
            ArrayList<String> arrUserName = (ArrayList<String>) map_old.get("userNameArray");
            ArrayList<String> arrUserDocId = (ArrayList<String>) map_old.get("userDocIdArray");
            arrUserName.add(userName);
            arrUserDocId.add(userdocId);
            Map<String, Object> map_temp = new HashMap<>();
            map_temp.putAll(map_old);
            map_temp.put("userNameArray", arrUserName);
            map_temp.put("userDocIdArray", arrUserDocId);
            try {
                document.putProperties(map_temp);
            } catch (CouchbaseLiteException e) {
                Log.e(TAG, "Error putting", e);
            }
        }
    }
    /*
     * To get the user Info docId from the index docId with help of its
     * userId(althought it is written username here but actually it is userId)
     * */

    @SuppressWarnings("unchecked")
    public String getUserInformationDocumentId(String indexDocId, String userName)
    {
        Log.d("test123",""+indexDocId+" "+userName);
        String userDocId = "";
        Document document = database.getDocument(indexDocId);
        Map<String, Object> map = document.getProperties();
        if (map != null) {
            ArrayList<String> arr = (ArrayList<String>) map.get("userNameArray");
            for (int i = arr.size() - 1; i >= 0; i--)
            {
                if (arr.get(i)!=null&&arr.get(i).equals(userName))
                {
                    userDocId = ((ArrayList<String>) map.get("userDocIdArray")).get(i);
                    break;
                }
            }
        }
        return userDocId;
    }

    /*
    *
    * To check if index doc already contains info of current user
    * */
    @SuppressWarnings("unchecked")
    public boolean checkUserDocExists(String docId, String userName) {

        boolean exists = false;
        Document document = database.getDocument(docId);

        Map<String, Object> map = document.getProperties();


        if (map != null) {


            ArrayList<String> arr = (ArrayList<String>) map.get("userNameArray");

            for (int i = arr.size() - 1; i >= 0; i--)
            {
                if (arr.get(i).equals(userName)) {
                    exists = true;
                    break;
                }
            }
        }
        return exists;
    }


    /**
     * to be created when user signsup or logsin for the first time
     */
    public String createUserInformationDocument(Map<String, Object> map1)
    {
        Map<String, Object> map = new HashMap<>();
        Document document = database.createDocument();
        map.put("apiToken", map1.get("apiToken"));
        map.put("userIdentifier", map1.get("userIdentifier"));
        map.put("userId", map1.get("userId"));
        map.put("userName", map1.get("userName"));
        map.put("userImageUrl", map1.get("userImageUrl"));
        map.put("chatDocument", createChatDocument());
        map.put("unsentMessagesDocument", createUnsentMessagesDocument());
        map.put("ImqttTokenDocument", createMqttTokenToMessageIdMappingDocument());
        map.put("notificationDocument", createNotificationDocument());
        try
        {
            document.putProperties(map);
        } catch (CouchbaseLiteException e)
        {
            com.couchbase.lite.util.Log.e(TAG, "Error putting", e);
        }
        return document.getId();
    }


    /**
     * To get list of all docIds for a particular user
     */
    public ArrayList<String> getUserDocIds(String docId)
    {
        Log.d("test123",""+docId);
        Document document = database.getDocument(docId);
        Map<String, Object> map = document.getProperties();
        ArrayList<String> arr = new ArrayList<>();
        if (map != null) {
            arr.add((String) map.get("chatDocument"));
            arr.add((String) map.get("unsentMessagesDocument"));
            arr.add((String) map.get("ImqttTokenDocument"));
            arr.add((String) map.get("notificationDocument"));
        }
        return arr;
    }


    /**
     * To get the user info from his docId
     */
    public Map<String, Object> getUserInfo(String docId)

    {

        Document document = database.getDocument(docId);

        Map<String, Object> map = document.getProperties();


        Map<String, Object> userInfo = new HashMap<>();


        if (map != null) {

            userInfo.put("apiToken", map.get("apiToken"));
            userInfo.put("userId", map.get("userId"));
            userInfo.put("userName", map.get("userName"));
            userInfo.put("userImageUrl", map.get("userImageUrl"));
            userInfo.put("userIdentifier", map.get("userIdentifier"));

        }
        return userInfo;
    }

    /**
     * To create the doc to contain the messages until they have been delivered to the server and on getting response from the server they are deleted.Unsent messages from this doc are sent sutomatically once socket connects again when internet is back
     */
    private String createUnsentMessagesDocument()
    {
        Document document = database.createDocument();
        Map<String, Object> map = new HashMap<>();
        map.put("unsentMessageArray",new ArrayList<>());
        try {
            document.putProperties(map);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting", e);
        }
        return document.getId();
    }

    /**
     * To delete a given chat
     */
    @SuppressWarnings("unchecked")
    public void deleteParticularChatDetail(String docId, String chatDocId)
    {
        Document document = database.getDocument(docId);
        if (document != null) {
            Map<String, Object> map = document.getProperties();
            if (map != null) {
                ArrayList<String> receiverDocIdArray = (ArrayList<String>) map.get("receiverDocIdArray");
                for (int i = 0; i < receiverDocIdArray.size(); i++)
                {
                    if (receiverDocIdArray.get(i).equals(chatDocId))
                    {
                        ArrayList<String> receiverUidArray = (ArrayList<String>) map.get("receiverUidArray");
                        ArrayList<String> secretIdArray = (ArrayList<String>) map.get("secretIdArray");
                        receiverDocIdArray.remove(i);
                        receiverUidArray.remove(i);
                        secretIdArray.remove(i);
                        Map<String, Object> map_temp = new HashMap<>();
                        map_temp.putAll(map);
                        map_temp.put("receiverUidArray", receiverUidArray);
                        map_temp.put("receiverDocIdArray", receiverDocIdArray);
                        map_temp.put("secretIdArray", secretIdArray);
                        try {
                            document.putProperties(map_temp);
                        } catch (CouchbaseLiteException e) {
                            com.couchbase.lite.util.Log.e(TAG, "Error putting", e);
                        }
                        try {
                            database.deleteLocalDocument(chatDocId);
                        } catch (Exception e) {
                            com.couchbase.lite.util.Log.e(TAG, "Error putting", e);
                        }
                        break;
                    }
                }
            }
        }


    }



    /*
    *
    * To create document containing all the chats info
    * */

    private String createChatDocument()
    {
        Document document = database.createDocument();
        Map<String, Object> map = new HashMap<>();
        map.put("receiverUidArray", new ArrayList<String>());
        map.put("receiverDocIdArray", new ArrayList<String>());
        /*
         * For the secret chat
         */
        map.put("secretIdArray", new ArrayList<String>());
        try {
            document.putProperties(map);
        } catch (CouchbaseLiteException e) {
            com.couchbase.lite.util.Log.e(TAG, "Error putting", e);
        }
        return document.getId();
    }


    /**
     * To create document for a particular chat between any two of the users
     */
    @SuppressWarnings("unchecked")
    public String createDocumentForChat(String timeInGmt, String receiverUid, String receiverName, String
            receiverImage, String secretId, boolean invited, String receiverIdentifier, String chatId,String productImage,String productName,String productPrice,boolean isSold,boolean isAccepted)
    {
        Document document = database.createDocument();
        String documentId = document.getId();
        Map<String, Object> map = new HashMap<>();
        map.put("messsageArray", new ArrayList<Map<String, Object>>());
        map.put("hasNewMessage", false);
        map.put("newMessage", "");
        map.put("newMessageTime", timeInGmt);
        map.put("newMessageDate", timeInGmt);
        map.put("newMessageCount", "0");
        /*
         * last message date is required to show elements in chatlist in sorted order
         * */
        map.put("lastMessageDate", timeInGmt);
        map.put("receiver_uid_array", new ArrayList<String>());
        map.put("receiver_docid_array", new ArrayList<String>());
        map.put("receiverIdentifier", receiverIdentifier);
        map.put("receiverName", receiverName);
        map.put("receiverImage", receiverImage);
        map.put("selfDocId", documentId);
        map.put("selfUid", receiverUid);
        map.put("firstDate", "Mon 07/Mar/2016");
        map.put("lastDate", "Mon 07/Mar/2016");


        /*
         * For secret chat
         *
         *
         */


        map.put("wasInvited", invited);
        map.put("secretId", secretId);
        map.put("dTime", -1L);
        map.put("secretInviteVisibility", true);
        /*
         * For fetching of the chat history
         */
        map.put("chatId", chatId);
        if (chatId.isEmpty())
        {
            map.put("canHaveMoreMessages", false);
        } else {
            map.put("canHaveMoreMessages", true);
        }
        map.put("productImage",productImage);
        map.put("productName",productName);
        map.put("productPrice",productPrice);
        map.put("currencySymbol","");
        map.put("productSelPrice","");
        map.put("isNegotiable","0");
        map.put("isProductUpdated",false);
        map.put("isAccepted",isAccepted);
        map.put("showSafety",true);
        map.put("isSold",isSold);
        try
        {
            document.putProperties(map);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting", e);
        }
        return documentId;
    }
    /*
     *Update all product details. */
    @SuppressWarnings("unchecked")
    public void updateAllProduct(String productId,String productImage,String product_name,String price,String isNegotiable,String currency)
    {
        Map<String, Object> chatDetails=getAllChatDetails(AppController.getInstance().getChatDocId());
        if (chatDetails != null)
        {
            ArrayList<String> receiverUidArray = (ArrayList<String>) chatDetails.get("receiverUidArray");
            ArrayList<String> receiverDocIdArray = (ArrayList<String>) chatDetails.get("receiverDocIdArray");
            ArrayList<String> secretIdArray = (ArrayList<String>) chatDetails.get("secretIdArray");
            for (int i = 0; i < receiverUidArray.size(); i++)
            {
                if (secretIdArray.get(i).equals(productId))
                {
                    updateProductDetails(receiverDocIdArray.get(i),productImage,product_name,price,isNegotiable,currency);
                }
            }
        }
    }
    /*
     *Update all product details. */
    @SuppressWarnings("unchecked")
    public void updateAllProductSold(String productId,boolean isSold)
    {
        Map<String, Object> chatDetails=getAllChatDetails(AppController.getInstance().getChatDocId());
        if (chatDetails != null)
        {
            ArrayList<String> receiverUidArray = (ArrayList<String>) chatDetails.get("receiverUidArray");
            ArrayList<String> receiverDocIdArray = (ArrayList<String>) chatDetails.get("receiverDocIdArray");
            ArrayList<String> secretIdArray = (ArrayList<String>) chatDetails.get("secretIdArray");
            for (int i = 0; i < receiverUidArray.size(); i++)
            {
                if (secretIdArray.get(i).equals(productId))
                {
                    updateSoldDetails(receiverDocIdArray.get(i),isSold);
                }
            }
        }
    }


    /*
     * To fetch if the chat has messages stored locally only or the chat has been fetched from the server
     */
    public Map<String, Object> getChatInfo(String docId)
    {
        Map<String, Object> mapTemp = new HashMap<>();
        Document document = database.getDocument(docId);
        if (document != null) {
            Map<String, Object> map = document.getProperties();
            mapTemp.put("chatId", map.get("chatId"));
            mapTemp.put("canHaveMoreMessages", map.get("canHaveMoreMessages"));
        }
        return mapTemp;
    }


    public void saveCanHaveMoreMessage(String docId) {


        Document document = database.getDocument(docId);

        if (document != null) {

            Map<String, Object> map = document.getProperties();

            if (map != null) {

                Map<String, Object> map_temp = new HashMap<>();

                map_temp.putAll(map);

                map_temp.put("canHaveMoreMessages", false);

                try {
                    document.putProperties(map_temp);

                } catch (CouchbaseLiteException e) {
                    Log.e(TAG, "Error putting", e);
                }

            }


        }


    }

    /*
    *
    * Add the details of newly created chat to list of all chats
    * */
    @SuppressWarnings("unchecked")
    public void addChatDocumentDetails(String receiverUid, String receiverDocId, String chatDocId, String secretId)
    {
        Document document = database.getDocument(chatDocId);
        if (document != null) {

            Map<String, Object> map = document.getProperties();

            if (map != null) {
                ArrayList<String> receiverUidArray = (ArrayList<String>) map.get("receiverUidArray");
                ArrayList<String> receiverDocIdArray = (ArrayList<String>) map.get("receiverDocIdArray");
                ArrayList<String> secretIdArray = (ArrayList<String>) map.get("secretIdArray");
                receiverUidArray.add(receiverUid);
                receiverDocIdArray.add(receiverDocId);
                secretIdArray.add(secretId);
                Map<String, Object> map_temp = new HashMap<>();
                map_temp.putAll(map);
                map_temp.put("receiverUidArray", receiverUidArray);
                map_temp.put("receiverDocIdArray", receiverDocIdArray);
                map_temp.put("secretIdArray", secretIdArray);
                try {
                    document.putProperties(map_temp);
                } catch (CouchbaseLiteException e) {
                    Log.e(TAG, "Error putting", e);
                }
            }
        }
    }

    /*
    * To get details of the chats
    * */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getAllChatDetails(String chatDocId)
    {
        Map<String, Object> map = null;
        Document document = database.getDocument(chatDocId);
        if (document != null) {
            map = document.getProperties();
        }
        return map;
    }


    /*
    * Get details of the particular chat*/
    @SuppressWarnings("unchecked")
    public Map<String, Object> getParticularChatInfo(String chatDocId)
    {
        Document document = database.getDocument(chatDocId);
        Map<String, Object> map = document.getProperties();
        return map;
    }
    /**
     * Get list of messages in the list
     */

    @SuppressWarnings("unchecked")
    public ArrayList<Map<String, Object>> retrieveAllMessages(String docId)
    {
        Document document = database.getDocument(docId);
        ArrayList<Map<String, Object>> arr = new ArrayList<>();
        try {
            Map<String, Object> map = document.getProperties();
            if (map != null) {
                arr = (ArrayList<Map<String, Object>>) map.get("messsageArray");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return arr;
    }


    /*
     *
     * To update the status of a particular chat message sent
     *
     * */

    /**
     * status-0 not sent
     * status-1 sent
     * status-2 delivered
     * status-3 read
     */


    @SuppressWarnings("unchecked")
    public boolean updateMessageStatus(String docId, String messageId, int status)
    {
        boolean flag = false;
        Document document = database.getDocument(docId);
        try {
            Map<String, Object> map = document.getProperties();
            if (map == null) {
                return false;
            } else {
                Map<String, Object> mapMessages;
                Map<String, Object> mapTemp = new HashMap<>();
                mapTemp.putAll(map);
                ArrayList<Map<String, Object>> arr = (ArrayList<Map<String, Object>>) map.get("messsageArray");
                String Id;
                for (int i = arr.size() - 1; i >= 0; i--) {

                    if (flag) {
                        break;


                    }

                    mapMessages = (arr.get(i));
                    Id = (String) mapMessages.get("id");


                    if (Id == null) {
                        return false;
                    }


                    if (Id.equals(messageId)) {

                        switch (status) {

                            case 0:
                                mapMessages.put("deliveryStatus", "1");
                                flag = true;
                                arr.set(i, mapMessages);
                                mapTemp.put("messsageArray", arr);


                                break;
                            case 1:
                                mapMessages.put("deliveryStatus", "2");
                                flag = true;
                                arr.set(i, mapMessages);
                                mapTemp.put("messsageArray", arr);


                        }

                        try {
                            document.putProperties(mapTemp);

                        } catch (CouchbaseLiteException e) {
                            Log.e(TAG, "Error putting", e);
                        }

                    }

                }




                return true;


            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        }




        return false;
    }


    /**
     * to update the time of message sent incase it has been sent later when internet came rather than original time as typed or selected by the user
     */
    @SuppressWarnings("unchecked")


    public void updateMessageTs(String docId, String messageId, String ts) {


        Document document = database.getDocument(docId);
        if (document != null) {
            Map<String, Object> map = document.getProperties();
            Map<String, Object> mapMessages;

            Map<String, Object> mapTemp = new HashMap<>();
            mapTemp.putAll(map);


            ArrayList<Map<String, Object>> arr = (ArrayList<Map<String, Object>>) map.get("messsageArray");

            for (int i = arr.size() - 1; i >= 0; i--) {

                mapMessages = (arr.get(i));


                String Id = (String) mapMessages.get("id");

                if (Id.equals(messageId)) {
                    mapMessages.put("Ts", ts);

                    arr.set(i, mapMessages);
                    mapTemp.put("messsageArray", arr);
                    try {
                        document.putProperties(mapTemp);


                    } catch (CouchbaseLiteException e) {
                        Log.e(TAG, "Error putting", e);
                    }


                    break;
                }

            }

        }




    }


    /*To delete chat locally
    * */
    public void deleteChat(String docId) {



        Document doc = database.getDocument(docId);

        Map<String, Object> mapTemp = new HashMap<>();


        Map<String, Object> mapOld = doc.getProperties();


        if (mapOld != null) {


            mapTemp.putAll(mapOld);


            mapTemp.put("messsageArray", new ArrayList<Map<String, Object>>());


            mapTemp.put("firstDate", "Mon 07/Mar/2016");
            mapTemp.put("lastDate", "Mon 07/Mar/2016");

        }
        try {
            doc.putProperties(mapTemp);

        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting", e);
        }



    }

    /**
     * To update in db locally that no new unread messages for the chat once that chat has been seen
     */
    public void updateChatListOnViewingMessage(String docId) {


        Document document = database.getDocument(docId);

        Map<String, Object> map = document.getProperties();


        if (map == null)

        {
            return;
        }

        Map<String, Object> mapTemp = new HashMap<>();


        mapTemp.putAll(map);

        mapTemp.put("newMessageCount", "0");
        mapTemp.put("hasNewMessage", false);


        try {

            document.putProperties(mapTemp);

        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }



    }


    /**
     * in each of the document we are storing the last message date and last message time so that if flag hasNewMessage is set
     * then we can show that and also it will help us to sort chatlist based on the most recent
     */

    public void updateChatListForNewMessage(String docId, String lastmessage,
                                            boolean hasNewMessage, String lastMessageDate, String lastmessagetime)//, boolean countToUpdate, int unreadCount)


    {

        Document document = database.getDocument(docId);

        Map<String, Object> map = document.getProperties();


        if (map == null)

        {
            return;
        }
        String newMessageCount = (String) map.get("newMessageCount");

        int count = Integer.parseInt(newMessageCount);

        count += 1;


        Map<String, Object> mapTemp = new HashMap<>();
        mapTemp.putAll(map);
        mapTemp.put("hasNewMessage", hasNewMessage);
        if (hasNewMessage) {
            mapTemp.put("newMessage", lastmessage);
            mapTemp.put("newMessageTime", lastmessagetime);

            mapTemp.put("newMessageDate", lastMessageDate);


            mapTemp.put("newMessageCount", String.valueOf(count));
        }

        /*
         * Have put just for safety although not required
         */
        mapTemp.put("isSelf", false);
        mapTemp.put("deliveryStatus", 0);

        mapTemp.put("lastMessageDate", lastMessageDate);
        try {
            document.putProperties(mapTemp);

        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting", e);
        }



    }


    public void updateChatListForNewMessageFromHistory(String docId, String lastmessage,
                                                       boolean hasNewMessage, String lastMessageDate, String lastmessagetime, int count,
                                                       boolean isSelf, int deliveryStatus)//, boolean countToUpdate, int unreadCount)


    {
        Log.d("log91",lastmessage);
        Document document = database.getDocument(docId);
        Map<String, Object> map = document.getProperties();
        if (map == null)
        {
            return;
        }
        Map<String, Object> mapTemp = new HashMap<>();
        mapTemp.putAll(map);
        mapTemp.put("hasNewMessage", hasNewMessage);
        mapTemp.put("newMessage", lastmessage);
        mapTemp.put("newMessageTime", lastmessagetime);
        mapTemp.put("newMessageDate", lastMessageDate);
        mapTemp.put("isSelf", isSelf);
        mapTemp.put("deliveryStatus", deliveryStatus);
        mapTemp.put("newMessageCount", String.valueOf(count));
        mapTemp.put("lastMessageDate", lastMessageDate);
        /*
         * Required for the last message details for chats fetched from server,
         * but for whom messages has not been fetched yet
         */
        mapTemp.put("lastMessage", lastmessage);

        try {
            document.putProperties(mapTemp);

        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting", e);
        }


    }


    /**
     * Get number of new messages
     */
    public String getNewMessageCount(String docId) {


        Document document = database.getDocument(docId);
        Map<String, Object> map = document.getProperties();


        return (String) map.get("newMessageCount");
    }


    /**
     * Get last message details to show in list of all the chats
     */
    @SuppressWarnings("unchecked")

    public Map<String, Object> getLastMessageDetails(String docId)
    {
        Document document = database.getDocument(docId);
        Map<String, Object> map = document.getProperties();
        Map<String, Object> result = new HashMap<>();
        if (map != null) {
            ArrayList<Map<String, Object>> arr = (ArrayList<Map<String, Object>>) map.get("messsageArray");
            if (arr.size() == 0) {
                String time = (String) map.get("lastMessageDate");
                result.put("lastMessageDate", time);
                result.put("lastMessageTime", time);
                if (((String) map.get("chatId")).isEmpty()) {
                    result.put("lastMessage","No Messages To Show!!");
                    result.put("showTick",false);
                } else {
                    result.put("lastMessage",map.get("lastMessage"));
                    if(map.containsKey("isSelf")&&(boolean) map.get("isSelf"))
                    {
                        result.put("showTick",true);
                        result.put("tickStatus", map.get("deliveryStatus"));
                    } else {
                        result.put("showTick",false);
                    }
                }
                return result;
            }


            Map<String, Object> lastMessage = arr.get(arr.size() - 1);
            String ts = (String) lastMessage.get("Ts");
            String type = (String) lastMessage.get("messageType");
            result.put("lastMessageDate", ts);
            result.put("lastMessageTime", ts);
            boolean isSelf=(boolean) lastMessage.get("isSelf");
            if (isSelf) {
                result.put("showTick", true);
                try {
                    result.put("tickStatus", Integer.parseInt((String) lastMessage.get("deliveryStatus")));
                } catch (ClassCastException e) {
                    result.put("tickStatus", lastMessage.get("deliveryStatus"));
                }
            } else {
                result.put("showTick", false);
            }

            try {
                switch (Integer.parseInt(type)) {

                    case 0:
                        result.put("lastMessage", lastMessage.get("message"));
                        break;
                    case 1:
                        result.put("lastMessage", "Image");
                        break;
                    case 2:
                        result.put("lastMessage", "Video");
                        break;
                    case 3:
                        result.put("lastMessage", "Location");
                        break;
                    case 4:
                        result.put("lastMessage", "Contact");
                        break;
                    case 5:
                        result.put("lastMessage", "Audio");
                        break;
                    case 6:
                        result.put("lastMessage", "Sticker");
                        break;
                    case 7:
                        result.put("lastMessage", "Doodle");
                        break;
                    case 8:
                        result.put("lastMessage", "Gif");
                        break;
                    case 15:
                        if(isSelf)
                        {
                            result.put("lastMessage","Offer sent");
                        }else
                        {
                            result.put("lastMessage","Counter Offer received");
                        }
                        break;
                    case 16:
                        if(isSelf)
                        {
                            result.put("lastMessage","Payment link shared.");
                        }else
                        {
                            result.put("lastMessage","Payment link received.");
                        }
                        break;
                }

            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    /**
     * To update status of all message above a message as read if that message has been reported as read by the reciver
     */
    @SuppressWarnings("unchecked")
    public boolean drawBlueTickUptoThisMessage(String document_id, String id)

    {

        Document document = database.getDocument(document_id);

        try {
            Map<String, Object> map = document.getProperties();


            if (map == null) {

                return false;
            } else {

                Map<String, Object> mapMessages;

                Map<String, Object> mapTemp = new HashMap<>();


                mapTemp.putAll(map);


                ArrayList<Map<String, Object>> arr = (ArrayList<Map<String, Object>>) map.get("messsageArray");

                boolean flag = false;

                for (int i = arr.size() - 1; i >= 0; i--) {
                    mapMessages = (arr.get(i));
                    String Id = (String) mapMessages.get("id");


                    if (Id.equals(id)) {
                        flag = true;

                        for (int j = i; j >= 0; j--) {


                            mapMessages = arr.get(j);


                            if (mapMessages != null) {


                                if (((boolean) mapMessages.get("isSelf")) && !(mapMessages.get("deliveryStatus").equals("0"))) {

                                    if (mapMessages.get("deliveryStatus").equals("3"))
                                        break;
                                    else {
                                        mapMessages.put("deliveryStatus", "3");

                                        arr.set(j, mapMessages);
                                    }
                                }

                            }
                        }
                        mapTemp.put("messsageArray", arr);


                    }


                    if (flag) {
                        try {
                            document.putProperties(mapTemp);

                        } catch (CouchbaseLiteException e) {
                            Log.e(TAG, "Error putting", e);
                        }
                        break;
                    }

                }

                return true;

            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return false;


    }


    /**
     * To get the docId of the receiver on the chatlist screen
     */
    @SuppressWarnings("unchecked")
    public String getDocumentIdOfReceiverChatlistScreen(String documentId, String receiverUid, String secretId)
    {
        String docId = "";
        Document document = database.getDocument(documentId);
        if (document != null) {
            Map<String, Object> map = document.getProperties();
            ArrayList<String> arrReceiverUid = (ArrayList<String>) map.get("receiverUidArray");
            ArrayList<String> arrReceiverDocid = (ArrayList<String>) map.get("receiverDocIdArray");
            ArrayList<String> secretIdArray = (ArrayList<String>) map.get("secretIdArray");
            for (int i = 0; i < arrReceiverUid.size(); i++)
            {

                if (arrReceiverUid.get(i).equals(receiverUid) && secretIdArray.get(i).equals(secretId)) {
                    return arrReceiverDocid.get(i);


                }


            }


        }



        return docId;
    }


    /**
     * To get the docId of the receiver,this is to be used when acknowledging received message
     * and as sender might have changed his device so we store his latest docId corresponding
     * \to last device he sent message from and use that docId at time of acknowledging
     */
    @SuppressWarnings("unchecked")
    public String getDocumentIdOfReceiver(String document_id, String receiver_uid)

    {
        String doc_id = null;

        Document document = database.getDocument(document_id);

        Map<String, Object> map = document.getProperties();


        ArrayList<String> arr_receiver_uid = (ArrayList<String>) map.get("receiver_uid_array");
        ArrayList<String> arr_receiver_docid = (ArrayList<String>) map.get("receiver_docid_array");


        for (int i = 0; i < arr_receiver_uid.size(); i++)

        {
            if (arr_receiver_uid.get(i).equals(receiver_uid)) {

                doc_id = arr_receiver_docid.get(i);
            }


        }



        return doc_id;
    }

    /*
    *
    *
    * To set the latest docId for the sender*/
    @SuppressWarnings("unchecked")
    public void setDocumentIdOfReceiver(String document_id, String receiver_doc_id, String
            receiver_uid)

    {


        Document document = database.getDocument(document_id);

        Map<String, Object> map = document.getProperties();


        ArrayList<String> arr_receiver_uid = (ArrayList<String>) map.get("receiver_uid_array");
        ArrayList<String> arr_receiver_docid = (ArrayList<String>) map.get("receiver_docid_array");

        boolean flag = false;

        for (int i = 0; i < arr_receiver_uid.size(); i++)

        {
            if (arr_receiver_uid.get(i).equals(receiver_uid)) {
                arr_receiver_docid.remove(i);
                arr_receiver_docid.add(i, receiver_doc_id);
                flag = true;
                break;

            }
        }

        if (!flag) {

            arr_receiver_uid.add(receiver_uid);
            arr_receiver_docid.add(receiver_doc_id);
        }
        Map<String, Object> map_temp = new HashMap<>();
        map_temp.putAll(map);


        map_temp.put("receiver_uid_array", arr_receiver_uid);

        map_temp.put("receiver_docid_array", arr_receiver_docid);


        try {
            document.putProperties(map_temp);

        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting", e);
        }


    }


    /**
     * To add the message to be sent to the doc,from where it can be resent automatically again when internet comes and delete from doc when server acknowledges of receipt of the message
     */

    @SuppressWarnings("unchecked")


    public void addUnsentMessage(String docId, Map<String, Object> map) {

        Document document = database.getDocument(docId);

        if (document != null) {
            Map<String, Object> mapOld = document.getProperties();


            Map<String, Object> map_temp = new HashMap<>();
            map_temp.putAll(mapOld);


            ArrayList<Map<String, Object>> arr = (ArrayList<Map<String, Object>>) mapOld.get("unsentMessageArray");

            arr.add(map);
            map_temp.put("unsentMessageArray", arr);

            try {
                document.putProperties(map_temp);

            } catch (CouchbaseLiteException e) {
                Log.e(TAG, "Error putting", e);
            }


        }




    }


    /**
     * To remove message from the list of the unsent messages,when server acknowledges that it has received a message
     */
    @SuppressWarnings("unchecked")


    public void removeUnsentMessage(String docId, String messageId) {

        boolean removed = false;
        Document document = database.getDocument(docId);

        if (document != null) {
            Map<String, Object> mapOld = document.getProperties();


            Map<String, Object> mapTemp = new HashMap<>();


            mapTemp.putAll(mapOld);


            ArrayList<Map<String, Object>> arr = (ArrayList<Map<String, Object>>) mapOld.get("unsentMessageArray");


            if (arr.size() > 0) {
                Map<String, Object> map;
                for (int i = 0; i < arr.size(); i++) {


                    map = arr.get(i);


                    String id = (String) map.get("id");


                    if (id.equals(messageId)) {


                        arr.remove(i);
                        removed = true;


                        break;
                    }


                }

                if (removed) {


                    mapTemp.put("unsentMessageArray", arr);


                    try {
                        document.putProperties(mapTemp);

                    } catch (CouchbaseLiteException e) {
                        Log.e(TAG, "Error putting", e);
                    }
                }


            }

        }


    }


    /**
     * To get list of all unsent messages to be emitted to server when socket connects
     */

    @SuppressWarnings("unchecked")

    public ArrayList<Map<String, Object>> getUnsentMessages(String docId) {

        Document document = database.getDocument(docId);
        Map<String, Object> mapOld = document.getProperties();
        return (ArrayList<Map<String, Object>>) mapOld.get("unsentMessageArray");


    }


    /**
     * To delete the message locally when user swipes and delete that message
     * from the list of the messages
     */

    @SuppressWarnings("unchecked")


    public void deleteParticularChatMessage(String docId, String messageId) {
        Document document = database.getDocument(docId);

        boolean deleted = false;
        if (document != null) {
            Map<String, Object> mapOld = document.getProperties();


            Map<String, Object> mapTemp = new HashMap<>();


            mapTemp.putAll(mapOld);

            Map<String, Object> mapMessages;


            ArrayList<Map<String, Object>> arr = (ArrayList<Map<String, Object>>) mapOld.get("messsageArray");

            for (int i = arr.size() - 1; i >= 0; i--) {


                mapMessages = (arr.get(i));


                String Id = (String) mapMessages.get("id");


                if (Id.equals(messageId)) {


                    arr.remove(i);
                    deleted = true;
                    break;
                }
            }


            if (deleted) {


                mapTemp.put("messsageArray", arr);


                try {
                    document.putProperties(mapTemp);

                } catch (CouchbaseLiteException e) {
                    Log.e(TAG, "Error putting", e);
                }

            }


        }



    }


    /**
     * To check if message already saved locally to prevent
     * duplication of the messages locally
     */

    @SuppressWarnings("unchecked")
    public boolean checkAlreadyExists(String documentId, String id)
    {

        Document document = database.getDocument(documentId);
        Map<String, Object> map_old = document.getProperties();
        if (map_old != null) {
            ArrayList<Map<String, Object>> arr = (ArrayList<Map<String, Object>>) map_old.get("messsageArray");
            Map<String, Object> map;
            for (int i = 0; i < arr.size(); i++)
            {
                map = arr.get(i);
                if (map.get("id") != null)
                {
                    if ((!(boolean) map.get("isSelf")) && map.get("id").equals(id))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * To sort the messages array after adding the new message
     */

    @SuppressWarnings("unchecked")

    public void addNewChatMessageAndSort(String documentId, Map<String, Object> map, String dateInGmt) {

        Document document = database.getDocument(documentId);


        Map<String, Object> mapOld = document.getProperties();


        if (mapOld != null) {
            ArrayList<Map<String, Object>> arr = (ArrayList<Map<String, Object>>) mapOld.get("messsageArray");


            arr.add(map);


            Collections.sort(arr, new MessageSorter());


            Map<String, Object> mapTemp = new HashMap<>();
            mapTemp.putAll(mapOld);

            if (dateInGmt != null) {
                mapTemp.put("lastMessageDate", dateInGmt);
            }

            mapTemp.put("messsageArray", arr);
            try {
                document.putProperties(mapTemp);


            } catch (CouchbaseLiteException e) {
                Log.e(TAG, "Error putting", e);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }




    }


    /**
     * Update the status as downloaded and change path once download of file is complete
     */
    @SuppressWarnings("unchecked")

    public void updateDownloadStatusAndPath(String docId, String path, String messageId) {

        Document document = database.getDocument(docId);

        boolean updated = false;
        if (document != null) {
            Map<String, Object> mapOld = document.getProperties();


            Map<String, Object> mapTemp = new HashMap<>();


            mapTemp.putAll(mapOld);

            Map<String, Object> mapMessages;


            ArrayList<Map<String, Object>> arr = (ArrayList<Map<String, Object>>) mapOld.get("messsageArray");

            for (int i = arr.size() - 1; i >= 0; i--) {


                mapMessages = (arr.get(i));


                String Id = (String) mapMessages.get("id");


                if (Id.equals(messageId)) {


                    arr.remove(i);
                    updated = true;


                    mapMessages.put("downloadStatus", 1);

                    mapMessages.put("message", path);

                    arr.add(i, mapMessages);
                    break;
                }
            }
            if (updated)
            {
                mapTemp.put("messsageArray", arr);
                try {
                    document.putProperties(mapTemp);
                } catch (CouchbaseLiteException e) {
                    Log.e(TAG, "Error putting", e);
                }}}
    }

    /**
     * Update index document when sign in status
     * of user changes(user signs in)
     */
    public void updateIndexDocumentOnSignIn(String docId, String userId)
    {
        Document document = database.getDocument(docId);
        Map<String, Object> map = document.getProperties();
        Map<String, Object> mapTemp = new HashMap<>();
        mapTemp.putAll(map);
        mapTemp.put("isSignedIn",true);
        mapTemp.put("signedUserId",userId);
        try {
            document.putProperties(mapTemp);
        } catch (CouchbaseLiteException e)
        {
            com.couchbase.lite.util.Log.e(TAG, "Error putting", e);
        }
    }
    /**
     * Update index document when sign in status of user changes(user signs out)
     */
    public void updateIndexDocumentOnSignOut(String docId)
    {
        Document document = database.getDocument(docId);
        Map<String, Object> map = document.getProperties();
        Map<String, Object> mapTemp = new HashMap<>();
        mapTemp.putAll(map);
        mapTemp.put("isSignedIn", false);
        mapTemp.put("signedUserId", null);
        try {
            document.putProperties(mapTemp);
        } catch (CouchbaseLiteException e) {
            com.couchbase.lite.util.Log.e(TAG, "Error putting", e);
        }
    }
    /**
     * To check if user is signed in
     */
    public Map<String, Object> isSignedIn(String docId)
    {
        Map<String, Object> signInDetails = new HashMap<>();
        Document document = database.getDocument(docId);
        Map<String, Object> map = document.getProperties();

        if(map!=null){


        signInDetails.put("isSignedIn", map.get("isSignedIn"));
        signInDetails.put("signedUserId", map.get("signedUserId"));}

 else{

            signInDetails.put("notReadyYet",true);
            signInDetails.put("isSignedIn",false);

        }

        return signInDetails;
    }

    /*
     * To update the user details on login/signup
     */

    /**
     * @param docId   user doc id
     * @param mapProp HashMap<String, Object> containing user details to update in couchdb locally
     */
    public void updateUserDetails(String docId, Map<String, Object> mapProp) {


        Document document = database.getDocument(docId);

        if (document != null) {

            Map<String, Object> map = document.getProperties();

            if (map != null) {


                Map<String, Object> map_temp = new HashMap<>();

                map_temp.putAll(map);


                map_temp.put("userId", mapProp.get("userId"));
                map_temp.put("userName", mapProp.get("userName"));
                map_temp.put("apiToken", mapProp.get("apiToken"));
                map_temp.put("userIdentifier", mapProp.get("userIdentifier"));


                try {
                    document.putProperties(map_temp);


                } catch (CouchbaseLiteException e) {
                    Log.e(TAG, "Error putting", e);
                }


            }


        }
    }


    /**
     * To get the user document id from the index document on login/signup in case the user doc already exists
     */

    @SuppressWarnings("unchecked")
    public String getUserDocId(String userId, String indexDocId) {

        String docId = "";
        Document document = database.getDocument(indexDocId);
        Map<String, Object> map = document.getProperties();
        if (map != null)
        {
            ArrayList<String> arr = (ArrayList<String>) map.get("userNameArray");
            ArrayList<String> arr2 = (ArrayList<String>) map.get("userDocIdArray");
            for (int i = arr.size() - 1; i >= 0; i--)
            {
                if (arr.get(i).equals(userId))
                {
                    docId = arr2.get(i);
                    break;
                }
            }
        }
        return docId;
    }


    private String createMqttTokenToMessageIdMappingDocument()
    {
        Document document = database.createDocument();
        Map<String, Object> map = new HashMap<>();
        map.put("idMappingArray", new ArrayList<Map<String, Object>>());
        try {
            document.putProperties(map);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting", e);
        }
        return document.getId();
    }




    @SuppressWarnings("unchecked")
    public void addMqttTokenMapping(String docId, ArrayList<HashMap<String, Object>> arrToken) {

        Document document = database.getDocument(docId);

        Map<String, Object> map = document.getProperties();


        Map<String, Object> mapTemp = new HashMap<>();

        mapTemp.putAll(map);


        mapTemp.put("idMappingArray", arrToken);
        try {
            document.putProperties(mapTemp);

        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting", e);
        }
    }


    @SuppressWarnings("unchecked")
    public ArrayList<HashMap<String, Object>> fetchMqttTokenMapping(String docId) {
        Document document = database.getDocument(docId);
        Map<String, Object> map = document.getProperties();
        return ((ArrayList<HashMap<String, Object>>) map.get("idMappingArray"));

    }

    /*
* To create document containing all the notifications info
* */
    private String createNotificationDocument() {
        Document document = database.createDocument();
        String documentId = document.getId();
        Map<String, Object> map = new HashMap<>();
        map.put("notificationArray", new ArrayList<String>());
        try {
            document.putProperties(map);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting", e);
        }
        return documentId;
    }


    public void updateChatDetails(String docId, String chatId, boolean wasInvited, String receiverImage)
    {
        Document document = database.getDocument(docId);
        if (document != null) {
            Map<String, Object> map = document.getProperties();
            Map<String, Object> mapTemp = new HashMap<>();
            mapTemp.putAll(map);
            mapTemp.put("chatId", chatId);
            mapTemp.put("canHaveMoreMessages", true);
            mapTemp.put("wasInvited", wasInvited);
            mapTemp.put("receiverImage", receiverImage);
            try {
                document.putProperties(mapTemp);
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }
        }

    }


    /*
  *Updatign the product is sold. */
    public void updateProductAccepted(String docId,boolean isAcepted,@NonNull String productPrice)
    {
        Document document = database.getDocument(docId);
        if (document != null) {
            Map<String, Object> map = document.getProperties();
            Map<String, Object> mapTemp = new HashMap<>();
            mapTemp.putAll(map);
            if(!productPrice.isEmpty())
            {
                mapTemp.put("productSelPrice",productPrice);
            }
            mapTemp.put("isAccepted",isAcepted);
            try {
                document.putProperties(mapTemp);
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }
        }
    }
    /*
     *Updating the product is sold. */
    public void updateSoldDetails(String docId,boolean isSold)
    {
        Document document = database.getDocument(docId);
        if (document != null) {
            Map<String, Object> map = document.getProperties();
            Map<String, Object> mapTemp = new HashMap<>();
            mapTemp.putAll(map);
            mapTemp.put("isSold",isSold);
            try
            {
                document.putProperties(mapTemp);
            } catch (CouchbaseLiteException e)
            {
                e.printStackTrace();
            }
        }
    }
    /*
    *Updating the safety shown. */
    public void updateSafetyShown(String docId,boolean isShown)
    {
        Document document = database.getDocument(docId);
        if (document != null) {
            Map<String, Object> map = document.getProperties();
            Map<String, Object> mapTemp = new HashMap<>();
            mapTemp.putAll(map);
            mapTemp.put("showSafety",isShown);
            try
            {
                document.putProperties(mapTemp);
            } catch (CouchbaseLiteException e)
            {
                e.printStackTrace();
            }
        }
    }
    /*
     *Updating the product details */
    public void updateChatDetails(String docId, String receiverName,String receiverImage)
    {
        Document document = database.getDocument(docId);
        if (document != null) {
            Map<String, Object> map = document.getProperties();
            Map<String, Object> mapTemp = new HashMap<>();
            mapTemp.putAll(map);
            mapTemp.put("receiverName", receiverName);
            mapTemp.put("receiverImage", receiverImage);
            try {
                document.putProperties(mapTemp);
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * @return receiver identifier for the given chat for which the messages are fetched
     */
    @SuppressWarnings("unchecked")
    public String fetchReceiverIdentifierFromChatId(String docId, String chatId)
    {
        String receiverIdentifier = "";
        Document document = database.getDocument(docId);
        if (document != null) {
            Map<String, Object> map = document.getProperties();
            if (map.get("chatId").equals(chatId))
            {
                return (String) map.get("receiverIdentifier");
            }
        }
        return receiverIdentifier;
    }





      /*
     * For clubbing of the notifications
     */


    /*
     * For updating the content of a notification
     */
    @SuppressWarnings("unchecked")
    public void addOrUpdateNotificationContent(String docId, String notificationId, Map<String, Object> notification) {


        Document document = database.getDocument(docId);

        if (document != null) {

            Map<String, Object> map = document.getProperties();


            ArrayList<Map<String, Object>> arr = (ArrayList<Map<String, Object>>) map.get("notificationArray");


            for (int i = arr.size() - 1; i >= 0; i--) {


                if (arr.get(i).get("notificationId").equals(notificationId)) {


                    arr.set(i, notification);

                    Map<String, Object> mapTemp = new HashMap<>();
                    mapTemp.putAll(map);
                    mapTemp.put("notificationArray", arr);

                    try {

                        document.putProperties(mapTemp);

                    } catch (CouchbaseLiteException e) {
                        e.printStackTrace();
                    }
                    return;
                }

            }


            arr.add(notification);
            Map<String, Object> mapTemp = new HashMap<>();
            mapTemp.putAll(map);
            mapTemp.put("notificationArray", arr);

            try {

                document.putProperties(mapTemp);

            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }

        }

    }

    @SuppressWarnings("unchecked")
    public int removeNotification(String docId, String notificationId) {

        Document document = database.getDocument(docId);
        int systemNotificationId = -1;
        if (document != null) {

            Map<String, Object> map = document.getProperties();


            ArrayList<Map<String, Object>> arr = (ArrayList<Map<String, Object>>) map.get("notificationArray");


            for (int i = arr.size() - 1; i >= 0; i--) {


                if (arr.get(i).get("notificationId").equals(notificationId)) {

                    systemNotificationId = (int) (arr.get(i).get("systemNotificationId"));
                    arr.remove(i);

                    Map<String, Object> mapTemp = new HashMap<>();
                    mapTemp.putAll(map);
                    mapTemp.put("notificationArray", arr);

                    try {

                        document.putProperties(mapTemp);

                    } catch (CouchbaseLiteException e) {
                        e.printStackTrace();
                    }
                    break;
                }

            }

        }
        return systemNotificationId;
    }


    /**
     * For fetching the info for all of the notifications
     */
    @SuppressWarnings("unchecked")
    public ArrayList<Map<String, Object>> fetchAllNotifications(String docId) {

        Document document = database.getDocument(docId);
        ArrayList<Map<String, Object>> arr = new ArrayList<>();
        if (document != null) {

            Map<String, Object> map = document.getProperties();

            arr = (ArrayList<Map<String, Object>>) map.get("notificationArray");

        }
        return arr;
    }

    /*
       * To update the image for the secret invite image
       */
    public void updateSecretInviteImageVisibility(String docId, boolean imgVisibility) {

        Document document = database.getDocument(docId);

        if (document != null) {

            Map<String, Object> map = document.getProperties();

            if (map == null)

            {
                return;
            }

            Map<String, Object> mapTemp = new HashMap<>();
            mapTemp.putAll(map);

            mapTemp.put("secretInviteVisibility", imgVisibility);

            try {

                document.putProperties(mapTemp);

            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }

        }


    }


    /**
     * To update the image of the user in ocuch db
     */

    public void updateUserImageUrl(String docId, String imageUrl) {


        Document document = database.getDocument(docId);

        if (document != null) {

            Map<String, Object> map = document.getProperties();

            if (map != null) {


                Map<String, Object> map_temp = new HashMap<>();

                map_temp.putAll(map);


                map_temp.put("userImageUrl", imageUrl);


                try {
                    document.putProperties(map_temp);


                } catch (CouchbaseLiteException e) {
                    Log.e(TAG, "Error putting", e);
                }


            }


        }
    }

    /**
     * @param docId indexDocId
     * @param name  new user name to be saved
     */
    public void updateUserName(String docId, String name) {


        Document document = database.getDocument(docId);

        if (document != null) {

            Map<String, Object> map = document.getProperties();

            if (map != null) {


                Map<String, Object> map_temp = new HashMap<>();

                map_temp.putAll(map);


                map_temp.put("userName", name);


                try {
                    document.putProperties(map_temp);


                } catch (CouchbaseLiteException e) {
                    Log.e(TAG, "Error putting", e);
                }


            }


        }
    }


    /**
     * @param docId         chatDocId
     * @param receiverImage new receiver image to be saved
     */
    public void updateReceiverImage(String docId, String receiverImage) {


        Document document = database.getDocument(docId);

        if (document != null) {

            Map<String, Object> map = document.getProperties();

            if (map != null) {


                Map<String, Object> map_temp = new HashMap<>();

                map_temp.putAll(map);


                map_temp.put("receiverImage", receiverImage);


                try {
                    document.putProperties(map_temp);


                } catch (CouchbaseLiteException e) {
                    Log.e(TAG, "Error putting", e);
                }


            }


        }
    }

    /**
     * @param docId of the current chat
     * @return if chat was initiated or was invited
     */
    public boolean checkIfInvited(String docId)
    {
        Document document = database.getDocument(docId);
        if (document != null) {
            Map<String, Object> map = document.getProperties();
            if (map != null) {
                return (boolean) map.get("wasInvited");
            }
        }
        return false;
    }


    /*
    *Update product details */
    public void updateProductDetails(String documentId,String productImage,String product_name,String price,String isNegotiable,String currency)
    {
        Document document = database.getDocument(documentId);
        if (document != null) {
            Map<String, Object> map = document.getProperties();
            Map<String, Object> mapTemp = new HashMap<>();
            mapTemp.putAll(map);
            if(productImage!=null&&!productImage.isEmpty())
            {
                mapTemp.put("productImage",productImage);
            }
            if(product_name!=null&&!product_name.isEmpty())
            {
                mapTemp.put("productName",product_name);
            }
            if(price!=null&&!price.isEmpty())
            {
                mapTemp.put("productPrice",price);
            }
            mapTemp.put("isNegotiable",isNegotiable);
            mapTemp.put("isProductUpdated",true);
            if(currency!=null&&!currency.isEmpty())
            {
                mapTemp.put("currencySymbol",currency);
            }
            try {
                document.putProperties(mapTemp);
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }
        }
    }
}