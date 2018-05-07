package com.yelo.com.mqttchat.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.Gson;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.yelo.com.R;
import com.yelo.com.aleret.SeftyAleretDialog;
import com.yelo.com.main.activity.HomePageActivity;
import com.yelo.com.main.activity.UserProfileActivity;
import com.yelo.com.main.activity.products.ProductDetailsActivity;
import com.yelo.com.mqttchat.Adapters.ChatMessageAdapter;
import com.yelo.com.mqttchat.AppController;
import com.yelo.com.mqttchat.Database.CouchDbController;
import com.yelo.com.mqttchat.DownloadFile.FileUploadService;
import com.yelo.com.mqttchat.DownloadFile.FileUtils;
import com.yelo.com.mqttchat.DownloadFile.ServiceGenerator;
import com.yelo.com.mqttchat.ModelClasses.RetrieveSecretChatMessageItem;
import com.yelo.com.mqttchat.Utilities.ApiOnServer;
import com.yelo.com.mqttchat.Utilities.CustomLinearLayoutManager;
import com.yelo.com.mqttchat.Utilities.FloatingView;
import com.yelo.com.mqttchat.Utilities.GPSTracker;
import com.yelo.com.mqttchat.Utilities.MqttEvents;
import com.yelo.com.mqttchat.Utilities.RecyclerItemClickListener;
import com.yelo.com.mqttchat.Utilities.TextDrawable;
import com.yelo.com.mqttchat.Utilities.Utilities;
import com.yelo.com.pojo_class.product_details_pojo.ProductDetailsMain;
import com.yelo.com.pojo_class.product_details_pojo.ProductResponseDatas;
import com.yelo.com.utility.ApiUrl;
import com.yelo.com.utility.CommonClass;
import com.yelo.com.utility.OkHttp3Connection;
import com.yelo.com.utility.SessionManager;
import com.yelo.com.utility.VariableConstants;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import github.ankushsachdeva.emojicon.EmojiconEditText;
import github.ankushsachdeva.emojicon.EmojiconGridView;
import github.ankushsachdeva.emojicon.EmojiconsPopup;
import github.ankushsachdeva.emojicon.emoji.Emojicon;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
/**
 * @since 09/08/17.
 *  @author 3Embed
 *  @version 1.0.
 */
public class ChatMessageScreen extends AppCompatActivity
{
    private static final int MESSAGE_PAGE_SIZE = 10;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int RESULT_LOAD_VIDEO = 2;
    private static final int REQUEST_CODE_CONTACTS = 4;
    private static final int RESULT_SHARE_LOCATION = 5;
    private static final int REQUEST_SELECT_AUDIO = 7;
    private static final int RESULT_LOAD_GIF = 8;
    private static final int RESULT_LOAD_STICKER = 9;
    private static final int IMAGE_QUALITY = 50;//change it to higher level if want,but then slower image sending
    private static final int IMAGE_CAPTURED_QUALITY = 50;//change it to higher level if want,but then slower image sending
    private static final double MAX_VIDEO_SIZE = 26 * 1024 * 1024;
    private static Uri imageUrl;
    private static int RESULT_CAPTURE_IMAGE = 0;
    private ImageView sendButton, selEmoji, selKeybord;
    private EmojiconEditText sendMessage;
    private Drawable drawable2, drawable1;
    private boolean limit = false;
    private boolean isFromOfferPage=true;
    public String userId, userName, receiverUid, documentId, picturePath, tsForServerEpoch, tsForServer, videoPath, audioPath, gifUrl, stickerUrl;
    private ChatMessageAdapter mAdapter;
    private RecyclerView recyclerView_chat;
    public String receiverName;
    private String top;
    private Uri imageUri;
    private String placeString;
    private String contactInfo;
    public SessionManager sessionManager;
    private ArrayList<RetrieveSecretChatMessageItem> mChatData;
    private int button01pos, MessageType, size, status;
    private boolean firstTenLoaded = false;
    private CouchDbController db;
    private CustomLinearLayoutManager llm;
    private FrameLayout profilePic;
    private TextView tv, receiverNameHeader, header_receiverName;
    private RelativeLayout backButton;
    private CoordinatorLayout root;
    private String contactInfoForSaving;
    private boolean opponentOnline = false;
    private boolean allowLastSeen;
    public String receiverImage;
    public String secretId = "";
    public String productName="";
    public String producImage="";
    public String  productPrice="";
    private boolean inviteVisible = true;
    public String receiverIdentifier;
    private ImageView pic;
    private boolean hasPendingAcknowledgement = false,isNewChat=false;
    private RelativeLayout header_rl, header_center_rl;
    private Bus bus = AppController.getBus();
    private boolean hasChatId = false, showingLoadingItem = false, canHaveMoreMessages = true;
    private String chatId = "";
    private int pendingApiCalls = 0;
    public boolean isSeller;
    public boolean isSold,isAccepted=true;
    private String paypal_link;
    public String productSelPrice;
    public String isNegotiable;
    public String currencySysmbol;
    private ImageView round_image_view;
    private TextView price_Text;
    private TextView product_name,already_sold_tag;
    private RelativeLayout loading_prodcut_details,product_view,productNotexist;
    private SeftyAleretDialog aleretDialog;

    @SuppressWarnings("TryWithIdenticalCatches")
    private static byte[] convertFileToByteArray(File f)
    {
        byte[] byteArray = null;
        byte[] b;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            InputStream inputStream = new FileInputStream(f);
            b = new byte[2663];
            int bytesRead;
            while ((bytesRead = inputStream.read(b)) != -1)
            {
                bos.write(b, 0, bytesRead);
            }
            byteArray = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return byteArray;
    }

    /*
     *getting the file uri path. */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri)
    {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri))
        {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri))
            {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme()))
        {
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme()))
        {
            return uri.getPath();
        }
        return null;
    }

    /*
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,String[] selectionArgs)
    {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /*
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /*
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /*
     * To get details of the type of the attachment selected
     */

    /*
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
    /*
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


    /*
     * To mark the status of given message as delivered,
     * for which acknowledgement of delivery has been receiveds
     */
    @SuppressWarnings("TryWithIdenticalCatches,unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {

        supportRequestWindowFeature(AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message_screen);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        sessionManager=new SessionManager(this);
        button01pos = 0;
        MessageType = 0;
        status = 0;
        top = "Connecting...";
        aleretDialog=SeftyAleretDialog.getInstance();
        attachKeyboardListeners();
        recyclerView_chat = (RecyclerView) findViewById(R.id.list_view_messages);
        mChatData = new ArrayList<>();
        root = (CoordinatorLayout) findViewById(R.id.typing);
        llm = new CustomLinearLayoutManager(ChatMessageScreen.this, LinearLayoutManager.VERTICAL, false);
        recyclerView_chat.setLayoutManager(llm);
        recyclerView_chat.setItemAnimator(new DefaultItemAnimator());
        recyclerView_chat.setHasFixedSize(true);
        selKeybord = (ImageView) findViewById(R.id.chat_keyboard_icon);
        ItemTouchHelper.Callback callback = new ChatMessageTouchHelper();
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView_chat);
        sendButton = (ImageView) findViewById(R.id.enter_chat1);
        View include = findViewById(R.id.chatHeader);
        header_rl = (RelativeLayout) include.findViewById(R.id.header_rl);
        header_center_rl = (RelativeLayout) include.findViewById(R.id.rl2);
        header_receiverName = (TextView) include.findViewById(R.id.headerReceiverName);
        header_receiverName.setTypeface(AppController.getInstance().getRobotoMediumFont());
        profilePic = (FrameLayout) include.findViewById(R.id.profileImageChatScreen);
        tv = (TextView) include.findViewById(R.id.onlineStatus);
        tv.setTypeface(AppController.getInstance().getRobotoRegularFont());
        receiverNameHeader = (TextView) include.findViewById(R.id.receiverName);
        receiverNameHeader.setTypeface(AppController.getInstance().getRobotoMediumFont());
        backButton = (RelativeLayout) include.findViewById(R.id.backButton);
        ImageView more = (ImageView) include.findViewById(R.id.more);
        pic = (ImageView) include.findViewById(R.id.imv);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View inflatedView;
        final TextView media_tv, delete_tv, lastseen_tv;
        inflatedView = layoutInflater.inflate(R.layout.chatscreen_menu, null, false);
        media_tv = (TextView) inflatedView.findViewById(R.id.tv1);
        delete_tv = (TextView) inflatedView.findViewById(R.id.tv2);
        lastseen_tv = (TextView) inflatedView.findViewById(R.id.tv3);
        Typeface tf = AppController.getInstance().getRobotoCondensedFont();
        media_tv.setTypeface(tf, Typeface.NORMAL);
        delete_tv.setTypeface(tf, Typeface.NORMAL);
        lastseen_tv.setTypeface(tf, Typeface.NORMAL);
        RelativeLayout history_rl = (RelativeLayout) inflatedView.findViewById(R.id.rl_media);
        RelativeLayout delete_rl = (RelativeLayout) inflatedView.findViewById(R.id.rl_delete);
        RelativeLayout last_rl = (RelativeLayout) inflatedView.findViewById(R.id.rl_last);
        final ImageView lastSeenImage = (ImageView) inflatedView.findViewById(R.id.button_done);
        sendMessage = (EmojiconEditText) findViewById(R.id.chat_edit_text1);
        selEmoji = (ImageView) findViewById(R.id.emojiButton);
        View rootView = findViewById(R.id.mainRelativeLayout);
        if (rootView != null) {
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FloatingView.dismissWindow();
                }
            });}
        include.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatingView.dismissWindow();
            }
        });
        final EmojiconsPopup popup = new EmojiconsPopup(rootView, this);
        drawable1 = ContextCompat.getDrawable(ChatMessageScreen.this, R.drawable.ic_chat_send);
        drawable2 = ContextCompat.getDrawable(ChatMessageScreen.this, R.drawable.ic_chat_send_active);
        sendButton.setImageDrawable(drawable1);
        userId = AppController.getInstance().getUserId();
        userName = AppController.getInstance().getUserName();
        initProductView();
        setUpActivity(getIntent());
        Typeface face = AppController.getInstance().getRobotoCondensedFont();
        tv.setTypeface(face, Typeface.NORMAL);
        receiverNameHeader.setTypeface(face, Typeface.NORMAL);
         /* Registering click  Listener*/
        popup.setSizeForSoftKeyboard();
        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss()
            {
                selKeybord.setVisibility(View.GONE);
                selEmoji.setVisibility(View.VISIBLE);
            }
        });


        popup.setOnSoftKeyboardOpenCloseListener(new EmojiconsPopup.OnSoftKeyboardOpenCloseListener()

                                                 {

                                                     @Override
                                                     public void onKeyboardOpen(int keyBoardHeight) {

                                                     }

                                                     @Override
                                                     public void onKeyboardClose() {


                                                         if (popup.isShowing())
                                                             popup.dismiss();

                                                     }
                                                 }

        );


        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener()

                                           {

                                               @Override
                                               public void onEmojiconClicked(Emojicon emojicon) {
                                                   if (sendMessage == null || emojicon == null) {
                                                       return;
                                                   }

                                                   int start = sendMessage.getSelectionStart();
                                                   int end = sendMessage.getSelectionEnd();
                                                   if (start < 0) {
                                                       sendMessage.append(emojicon.getEmoji());
                                                   } else {
                                                       sendMessage.getText().replace(Math.min(start, end),
                                                               Math.max(start, end), emojicon.getEmoji(), 0,
                                                               emojicon.getEmoji().length());
                                                   }
                                               }
                                           }

        );


        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener()

                                                    {

                                                        @Override
                                                        public void onEmojiconBackspaceClicked(View v) {
                                                            KeyEvent event = new KeyEvent(
                                                                    0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                                                            sendMessage.dispatchKeyEvent(event);
                                                        }
                                                    }

        );
        selEmoji.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v)
                                        {
                                            selKeybord.setVisibility(View.VISIBLE);
                                            selEmoji.setVisibility(View.GONE);
                                            if (!popup.isShowing())
                                            {
                                                if (popup.isKeyBoardOpen()) {
                                                    popup.showAtBottom();

                                                } else {
                                                    sendMessage.setFocusableInTouchMode(true);
                                                    sendMessage.requestFocus();
                                                    popup.showAtBottomPending();
                                                    final InputMethodManager inputMethodManager = (InputMethodManager)
                                                            getSystemService(Context.INPUT_METHOD_SERVICE);
                                                    inputMethodManager.showSoftInput(sendMessage, InputMethodManager.SHOW_IMPLICIT);

                                                }
                                            } else {
                                                popup.dismiss();
                                            }
                                        }
                                    }

        );


        selKeybord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                selKeybord.setVisibility(View.GONE);
                selEmoji.setVisibility(View.VISIBLE);


                if (!popup.isShowing()) {


                    sendMessage.setFocusableInTouchMode(true);
                    sendMessage.requestFocus();
                    popup.showAtBottomPending();
                    final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(sendMessage, InputMethodManager.SHOW_IMPLICIT);

                } else {
                    popup.dismiss();

                }}});

        sendMessage.addTextChangedListener(new TextWatcher()
        {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (s.length() > 0 && !(s.toString().equals(" ")))
                {
                    sendButton.setImageDrawable(drawable2);
                    button01pos = 1;
                    if (opponentOnline)
                    {
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("from",userId);
                            obj.put("to",receiverUid);
                            obj.put("secretId",secretId);
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        AppController.getInstance().publish(MqttEvents.Typing.value+"/"+receiverUid,obj,0,false);
                    }
                }
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener()
        {
            @SuppressWarnings("unchecked")
            @Override
            public void onClick(View v)
            {
                if (sendMessage.getText().toString().trim().length() > 0)
                {
                    if (MessageType == 0) {
                        addMessageToSendInUi(setMessageToSend(false, 0, null, null), false, 0, null, false);
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (root != null) {
                                Snackbar snackbar = Snackbar.make(root, R.string.TypeSomething, Snackbar.LENGTH_SHORT);
                                snackbar.show();
                                View view = snackbar.getView();
                                TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                                txtv.setGravity(Gravity.CENTER_HORIZONTAL);

                            }
                        }
                    });

                }
            }
        });



        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        profilePic.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                receiverNameHeader.callOnClick();
            }
        });

        receiverNameHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatMessageScreen.this, UserProfileActivity.class);
                intent.putExtra("membername",""+receiverName);
                startActivity(intent);
            }
        });

        recyclerView_chat.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (llm.findFirstVisibleItemPosition() == 0)
                {
                    if (firstTenLoaded) {
                        if (size != 0) {
                            loadTenMore();
                        } else {
                            if (hasChatId && canHaveMoreMessages)
                            {
                                if (pendingApiCalls == 0 && dy != 0)
                                {
                                    retrieveChatMessage(MESSAGE_PAGE_SIZE);
                                }
                            }}
                    } else {
                        if (!limit)
                        {
                            limit = true;
                        }
                    }
                }
            }
        });

        history_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatingView.dismissWindow();
                Intent j = new Intent(ChatMessageScreen.this, MediaHistory.class);
                j.putExtra("docId", documentId);
                startActivity(j, ActivityOptionsCompat.makeSceneTransitionAnimation(ChatMessageScreen.this).toBundle());
            }
        });


        delete_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatingView.dismissWindow();
                android.support.v7.app.AlertDialog.Builder builder =
                        new android.support.v7.app.AlertDialog.Builder(ChatMessageScreen.this, 0);
                builder.setTitle(R.string.DeleteCon);
                builder.setMessage(getString(R.string.DeleteChatWith) + " " + receiverName + "?");
                builder.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        AppController.getInstance().getDbController().deleteChat(documentId);
                        mChatData.clear();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                            }
                        });
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


                builder.setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        last_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatingView.dismissWindow();
                allowLastSeen = !allowLastSeen;
                AppController.getInstance().updateLastSeenSettings(allowLastSeen);
                AppController.getInstance().updatePresence(1, false);
            }
        });
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
                if (allowLastSeen) {

                    lastseen_tv.setText(getString(R.string.DisableLast));
                    lastSeenImage.setImageResource(R.drawable.cancel_normal);
                } else {
                    lastseen_tv.setText(getString(R.string.EnableLast));
                    lastSeenImage.setImageResource(R.drawable.ic_done_all_black_24px);
                }

                FloatingView.onShowPopup(ChatMessageScreen.this, inflatedView);

            }
        });

        sendMessage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                popup.dismiss();
                final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(sendMessage, InputMethodManager.SHOW_FORCED);
            }
        });


        sendMessage.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // recyclerView_chat.scrollToPosition(mChatData.size() - 1);


                            //   llm.scrollToPositionWithOffset(mChatData.size() - 1, 0);
                            if (mChatData != null) {
                                llm.scrollToPositionWithOffset(mChatData.size() - 1, 0);
                            }
                        }
                    }, 500);

                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();


                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
        recyclerView_chat.addOnItemTouchListener(new RecyclerItemClickListener(ChatMessageScreen.this, recyclerView_chat, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if (position >= 0) {
                    final RetrieveSecretChatMessageItem item = mChatData.get(position);
                    if (item.getMessageType().equals("3"))
                    {
                        final String args[] = item.getPlaceInfo().split("@@");
                        android.support.v7.app.AlertDialog.Builder builder =
                                new android.support.v7.app.AlertDialog.Builder(ChatMessageScreen.this, 0);
                        LayoutInflater inflater = LayoutInflater.from(ChatMessageScreen.this);
                        final View dialogView = inflater.inflate(R.layout.location_popup, null);
                        builder.setView(dialogView);
                        TextView name = (TextView) dialogView.findViewById(R.id.Name);
                        TextView address = (TextView) dialogView.findViewById(R.id.Address);
                        TextView latlng = (TextView) dialogView.findViewById(R.id.LatLng);
                        name.setText(getString(R.string.Name) + " " + args[1]);
                        address.setText(getString(R.string.Address) + " " + args[2]);
                        latlng.setText(getString(R.string.LatLng) + " " + args[0]);
                        builder.setTitle(R.string.LocationShared);
                        builder.setPositiveButton(R.string.OpenMap, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            {
                                try {
                                    String LatLng = args[0];
                                    String[] parts = LatLng.split(",");
                                    String lat = parts[0].substring(1);
                                    String lng = parts[1].substring(0, parts[1].length() - 1);
                                    String uri = "geo:" + lat + ","
                                            + lng + "?q=" + lat
                                            + "," + lng;
                                    startActivity(new Intent(Intent.ACTION_VIEW,
                                                    Uri.parse(uri)),
                                            ActivityOptionsCompat.makeSceneTransitionAnimation(ChatMessageScreen.this).toBundle());
                                } catch (ActivityNotFoundException e) {
                                    if (root != null) {
                                        Snackbar snackbar = Snackbar.make(root, R.string.NoLocationApp, Snackbar.LENGTH_SHORT);
                                        snackbar.show();
                                        View view2 = snackbar.getView();
                                        TextView txtv = (TextView) view2.findViewById(android.support.design.R.id.snackbar_text);
                                        txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                                    }
                                }


                                //  dialog.dismiss();


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
                        builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    } else if (item.getMessageType().equals("4") && !(item.isSelf())) {
                        checkWriteContactPermission(item.getContactInfo());
                    }
                }
            }
            @Override
            public void onItemLongClick(View view, int position) {}
        }));
       /*
         * For handling open from the notification
         */
        try {
            db.updateChatListOnViewingMessage(documentId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        bus.register(this);
        /*
         *Updating for the first time */
        AppController.getInstance().updatePresence(1, false);
    }

    /*
     *Product details view. */
    private void initProductView()
    {
        loading_prodcut_details=(RelativeLayout)findViewById(R.id.loading_prodcut_details);
        productNotexist=(RelativeLayout)findViewById(R.id.productNotexist);
        product_view=(RelativeLayout)findViewById(R.id.product_view);
        already_sold_tag=(TextView)findViewById(R.id.already_sold_tag);
        round_image_view= (ImageView) findViewById(R.id.round_image_view);
        price_Text=(TextView)findViewById(R.id.price_Text);
        price_Text.setTypeface(AppController.getInstance().getRobotoMediumFont());
        price_Text.setText(productPrice);
        product_name=(TextView)findViewById(R.id.product_name);
        product_name.setTypeface(AppController.getInstance().getRobotoRegularFont());
        TextView details_button = (TextView) findViewById(R.id.details_button);
        details_button.setTypeface(AppController.getInstance().getRobotoRegularFont());
        findViewById(R.id.product_details).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(!isSold)
                {
                    productDetailsPage();
                }
            }
        });
        TextView loading_product_details=(TextView)findViewById(R.id.loading_product_details);
        loading_product_details.setTypeface(AppController.getInstance().getRobotoRegularFont());
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (sendMessage.getText().length() == 1)
            sendMessage.setText("");
        if (hasPendingAcknowledgement) {
            for (int i = mChatData.size() - 1; i >= 0; i--) {
                if (!mChatData.get(i).isSelf()) {
                    RetrieveSecretChatMessageItem item = mChatData.get(i);
                    createObjectToSend(item.getMessageId(), item.getReceiverUid());
                    break;
                }
            }
            hasPendingAcknowledgement = false;
            CouchDbController db = AppController.getInstance().getDbController();
            db.updateChatListOnViewingMessage(documentId);
        }
    }

    /*
     * @param isImageOrVideoOrAudio to check if message is image/video or audio in which case it is to be uploaded to the server
     * @param messageType           messageType
     * @param id                    messageId
     * @param thumbnail             thumbnail of message in case of image or video message
     * @return JSONObject containing details of the message to be emitted on socket
     */
    @SuppressWarnings("TryWithIdenticalCatches")
    private JSONObject setMessageToSend(boolean isImageOrVideoOrAudio, int messageType, String
            id, String thumbnail) {
        if(isNewChat)
        {
            documentId=AppController.findDocumentIdOfReceiver(receiverUid, Utilities.tsInGmt(),receiverName,receiverImage,secretId,false,"","",producImage,productName,productPrice,false,false);
        }
        JSONObject obj = new JSONObject();
        if (id == null) {
            tsForServer = Utilities.tsInGmt();
            tsForServerEpoch = new Utilities().gmtToEpoch(tsForServer);
        } else {
            tsForServerEpoch = id;
            tsForServer = Utilities.epochtoGmt(id);
        }
        if (!isImageOrVideoOrAudio)
        {
/*
 *
 * normal text message so payload field is set as well
 *
 * */

            if (messageType == 0)
            {
                /*
                 * Text message
                 */
                try {
                    byte[] byteArray = sendMessage.getText().toString().trim().getBytes("UTF-8");
                    String messageInbase64 = Base64.encodeToString(byteArray, Base64.DEFAULT).trim();
                    /*
                     * Have been commented out intentionally
                     */
                    obj.put("receiverIdentifier", AppController.getInstance().getUserIdentifier());
                    obj.put("from", AppController.getInstance().getUserId());
                    obj.put("to", receiverUid);
                    obj.put("payload", messageInbase64);
                    obj.put("toDocId", documentId);
                    obj.put("timestamp", tsForServerEpoch);
                    obj.put("id", tsForServerEpoch);
                    obj.put("type", "0");
                    obj.put("name", AppController.getInstance().getUserName());
                    /*
                     * For secret chat exclusively
                     */
                    obj.put("secretId", secretId);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {

                    e.printStackTrace();
                }

            }else if (messageType == 16)
            {
                /*
                 * pay pal message
                 */
                try {
                    byte[] byteArray =paypal_link.trim().getBytes("UTF-8");
                    String messageInbase64 = Base64.encodeToString(byteArray, Base64.DEFAULT).trim();
                    obj.put("receiverIdentifier", AppController.getInstance().getUserIdentifier());
                    obj.put("from", AppController.getInstance().getUserId());
                    obj.put("to", receiverUid);
                    obj.put("payload", messageInbase64);
                    obj.put("toDocId", documentId);
                    obj.put("timestamp", tsForServerEpoch);
                    obj.put("id", tsForServerEpoch);
                    obj.put("type", "16");
                    obj.put("name", AppController.getInstance().getUserName());
                    obj.put("secretId", secretId);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
            }
            /*
             * location message so payload field is set as well
             * */
            else if (messageType == 3)
            {
                /*
                 * Location
                 */
                try {
                    Log.d("ddasf",placeString);
                    String MessageInbase64 = Base64.encodeToString(placeString.getBytes("UTF-8"), Base64.DEFAULT);
                    obj.put("receiverIdentifier", AppController.getInstance().getUserIdentifier());
                    obj.put("from", AppController.getInstance().getUserId());
                    obj.put("to", receiverUid);
                    obj.put("payload", MessageInbase64.trim());
                    obj.put("toDocId", documentId);
                    obj.put("timestamp", tsForServerEpoch);
                    obj.put("id", tsForServerEpoch);
                    obj.put("type", "3");
                    obj.put("name", AppController.getInstance().getUserName());
                      /*
                     * For secret chat exclusively
                     */
                    obj.put("secretId", secretId);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {

                    e.printStackTrace();
                }

            }


            /*
             *
             * contact message so payload field is set as well
             *
             * */


            else if (messageType == 4) {

/*
 * Contacts
 */
                try {


                    String MessageInbase64 = Base64.encodeToString(contactInfo.getBytes("UTF-8"), Base64.DEFAULT);
                    obj.put("receiverIdentifier", AppController.getInstance().getUserIdentifier());

                    obj.put("from", AppController.getInstance().getUserId());
                    obj.put("to", receiverUid);
                    obj.put("payload", MessageInbase64.trim());
                    obj.put("toDocId", documentId);

                    obj.put("timestamp", tsForServerEpoch);
                    obj.put("id", tsForServerEpoch);
                    obj.put("type", "4");
                    obj.put("name", AppController.getInstance().getUserName());
                      /*
                     * For secret chat exclusively
                     */
                    obj.put("secretId", secretId);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {

                    e.printStackTrace();
                }


            } else if (messageType == 8) {
                /*
                 * Gifs
                 */

                try {


                    String messageInbase64 = Base64.encodeToString(gifUrl.trim().getBytes("UTF-8"), Base64.DEFAULT).trim();


                    if (messageInbase64.isEmpty()) {
                        messageInbase64 = " ";
                    }


                    /*
                     * Has removed the thumbnail key for now,which was added for the ios
                     */
                    obj.put("receiverIdentifier", AppController.getInstance().getUserIdentifier());
                    obj.put("from", AppController.getInstance().getUserId());
                    obj.put("to", receiverUid);
                    obj.put("payload", messageInbase64);
                    obj.put("toDocId", documentId);
                    obj.put("timestamp", tsForServerEpoch);
                    obj.put("id", tsForServerEpoch);
                    obj.put("type", "8");
                    obj.put("name", AppController.getInstance().getUserName());
                    /*
                     * For secret chat exclusively
                     */
                    obj.put("secretId", secretId);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {

                    e.printStackTrace();
                }

            } else if (messageType == 6) {


                /*
                 * Stickers
                 */

                try {


                    String messageInbase64 = Base64.encodeToString(stickerUrl.trim().getBytes("UTF-8"), Base64.DEFAULT).trim();


                    if (messageInbase64.isEmpty()) {
                        messageInbase64 = " ";
                    }

                    obj.put("receiverIdentifier", AppController.getInstance().getUserIdentifier());
                    obj.put("from", AppController.getInstance().getUserId());
                    obj.put("to", receiverUid);
                    obj.put("payload", messageInbase64);
                    obj.put("toDocId", documentId);
                    obj.put("timestamp", tsForServerEpoch);
                    obj.put("id", tsForServerEpoch);
                    obj.put("type", "6");

                    obj.put("name", AppController.getInstance().getUserName());
                    /*
                     * For secret chat exclusively
                     */
                    obj.put("secretId", secretId);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {

                    e.printStackTrace();
                }


            }


        } else {

/*
 *
 *
 * image message so payload field is not set
 *
 * */

            if (messageType == 1) {
                try {

                    obj.put("receiverIdentifier", AppController.getInstance().getUserIdentifier());
                    obj.put("from", AppController.getInstance().getUserId());
                    obj.put("to", receiverUid);

                    obj.put("toDocId", documentId);
                    obj.put("timestamp", tsForServerEpoch);

                    obj.put("id", tsForServerEpoch);
                    obj.put("type", "1");
                    obj.put("name", AppController.getInstance().getUserName());
                    obj.put("thumbnail", thumbnail);

                    /*
                     * For secret chat exclusively
                     */

                    obj.put("secretId", secretId);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }


            /*
             *
             *
             * video message so payload field is not set
             *
             * */


            else if (messageType == 2) {
                try {
                    obj.put("receiverIdentifier", AppController.getInstance().getUserIdentifier());
                    obj.put("from", AppController.getInstance().getUserId());
                    obj.put("to", receiverUid);

                    obj.put("toDocId", documentId);
                    obj.put("timestamp", tsForServerEpoch);

                    obj.put("id", tsForServerEpoch);
                    obj.put("type", "2");
                    obj.put("name", AppController.getInstance().getUserName());
                    obj.put("thumbnail", thumbnail);

                    /*
                     * For secret chat exclusively
                     */

                    obj.put("secretId", secretId);

                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }


            /*
             *
             *
             * audio message so payload field is not set
             *
             * */


            else if (messageType == 5) {
                try {
                    obj.put("receiverIdentifier", AppController.getInstance().getUserIdentifier());
                    obj.put("from", AppController.getInstance().getUserId());
                    obj.put("to", receiverUid);

                    obj.put("toDocId", documentId);
                    obj.put("timestamp", tsForServerEpoch);

                    obj.put("id", tsForServerEpoch);
                    obj.put("type", "5");
                    obj.put("name", AppController.getInstance().getUserName());

                    /*
                     * For secret chat exclusively
                     */

                    obj.put("secretId", secretId);

                } catch (JSONException e) {
                    e.printStackTrace();


                }

            } else if (messageType == 7) {

/*
 * Doodle
 */
                try {
                    obj.put("receiverIdentifier", AppController.getInstance().getUserIdentifier());

                    obj.put("from", AppController.getInstance().getUserId());
                    obj.put("to", receiverUid);

                    obj.put("toDocId", documentId);
                    obj.put("timestamp", tsForServerEpoch);


                    obj.put("id", tsForServerEpoch);
                    obj.put("type", "7");

                    obj.put("thumbnail", thumbnail);
                    obj.put("name", AppController.getInstance().getUserName());


                    /*
                     * For secret chat exclusively
                     */

                    obj.put("secretId", secretId);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }


        return obj;

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {


        outState.putParcelable("file_uri", imageUri);

        outState.putString("file_path", picturePath);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            imageUri = savedInstanceState.getParcelable("file_uri");

            picturePath = savedInstanceState.getString("file_path");
        }
        super.onRestoreInstanceState(savedInstanceState);
    }


    /*
     * To update the last seen status in action bar when notified by server of some change in status of receiver(with whom user is chatting)
     */

    @SuppressWarnings("TryWithIdenticalCatches")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== VariableConstants.PAYPAL_REQ_CODE&&null!=data)
        {
            if(data.hasExtra("payPalLink"))
            {
                paypal_link=data.getStringExtra("payPalLink");
                if(paypal_link!=null)
                {
                    addMessageToSendInUi(setMessageToSend(false,16, null, null), false,16, null, false);
                }else
                {
                    Snackbar snackbar = Snackbar.make(root, R.string.pay_pal_failed_text,
                            Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    View view = snackbar.getView();
                    TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                }
            }else
            {
                Snackbar snackbar = Snackbar.make(root, R.string.pay_pal_failed_text,
                        Snackbar.LENGTH_SHORT);
                snackbar.show();
                View view = snackbar.getView();
                TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                txtv.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        }else if (requestCode == RESULT_SHARE_LOCATION && resultCode == RESULT_OK && null != data)
        {
            Place place = PlacePicker.getPlace(ChatMessageScreen.this, data);
            try {
                String latlng = place.getLatLng().toString();
                String address = place.getAddress().toString().trim();
                String name = place.getName().toString();
                if (name.charAt(0) == '(') {
                    String[] parts = name.split(",");
                    name = parts[0].trim() + "," + parts[1].trim();
                }
                if (latlng != null)
                {
                    placeString = latlng.substring(9) + "@@";
                    sendMessage.setText(getString(R.string.LatLngSelected) + " " + latlng);
                }
                if (latlng != null && name.equals(latlng.substring(9)))
                {
                    placeString = placeString + "Not Applicable" + "@@";
                } else {
                    placeString = placeString + name + "@@";
                    sendMessage.setText(getString(R.string.LocationSelected) + " " + name);
                }
                if (address.isEmpty())
                {
                    placeString = placeString + "Not Applicable";
                } else {
                    placeString = placeString + address;
                }
                addMessageToSendInUi(setMessageToSend(false, 3, null, null), false, 3, null, false);
            } catch (NullPointerException e)
            {
                Snackbar snackbar = Snackbar.make(root, R.string.LocationFailed,
                        Snackbar.LENGTH_SHORT);
                snackbar.show();
                View view = snackbar.getView();
                TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                txtv.setGravity(Gravity.CENTER_HORIZONTAL);

            } catch (StringIndexOutOfBoundsException e)
            {
                Snackbar snackbar = Snackbar.make(root, R.string.LocationFailed,
                        Snackbar.LENGTH_SHORT);
                snackbar.show();
                View view = snackbar.getView();
                TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                txtv.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        } else if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data)
        {
            picturePath = getPath(this, data.getData());
            imageUrl = data.getData();
            Uri uri = null;
            Bitmap bm = null;
            String id = null;
            try {
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(picturePath, options);
                int height = options.outHeight;
                int width = options.outWidth;
                float density = getResources().getDisplayMetrics().density;
                int reqHeight;
                if (width != 0) {
                    reqHeight = (int) ((150 * density) * (height / width));

                    bm = decodeSampledBitmapFromResource(picturePath, (int) (150 * density), reqHeight);

                    if (bm != null) {


                        ByteArrayOutputStream baos = new ByteArrayOutputStream();

                        bm.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, baos);


                        // bm = null;
                        byte[] b = baos.toByteArray();

                        try {
                            baos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        id = new Utilities().gmtToEpoch(Utilities.tsInGmt());
                        File f = convertByteArrayToFile(b, id, ".jpg");
                        uri = Uri.fromFile(f);
                    } else {
                        if (root != null) {
                            Snackbar snackbar = Snackbar.make(root, R.string.AnotherImage, Snackbar.LENGTH_SHORT);
                            snackbar.show();
                            View view = snackbar.getView();
                            TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                            txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                        }
                    }


                } else {
                    if (root != null)
                    {
                        Snackbar snackbar = Snackbar.make(root, R.string.AnotherImage, Snackbar.LENGTH_SHORT);
                        snackbar.show();
                        View view = snackbar.getView();
                        TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                        txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                    }
                }


            } catch (OutOfMemoryError e) {
                e.printStackTrace();

                if (root != null) {

                    Snackbar snackbar = Snackbar.make(root, R.string.ImageError, Snackbar.LENGTH_SHORT);


                    snackbar.show();
                    View view = snackbar.getView();
                    TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                }


            }


            if (uri != null) {


                /*
                 *
                 *
                 * make thumnail
                 * */
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 1, baos);
                byte[] b = baos.toByteArray();

                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                addMessageToSendInUi(setMessageToSend(true, 1, id, Base64.encodeToString(b, Base64.DEFAULT).trim()), true, 1, uri, false);

            }


        } else if (requestCode == RESULT_CAPTURE_IMAGE && resultCode == RESULT_OK) {


            Uri uri = null;
            String id = null;
            Bitmap bm = null;

            try {

//  picturePath = getPath(this, imageUri);


                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(picturePath, options);


                int height = options.outHeight;
                int width = options.outWidth;

                float density = getResources().getDisplayMetrics().density;
                int reqHeight;


                if (width != 0) {


                    reqHeight = (int) ((150 * density) * (height / width));


                    bm = decodeSampledBitmapFromResource(picturePath, (int) (150 * density), reqHeight);


                    ByteArrayOutputStream baos = new ByteArrayOutputStream();


                    if (bm != null) {

                        bm.compress(Bitmap.CompressFormat.JPEG, IMAGE_CAPTURED_QUALITY, baos);
                        byte[] b = baos.toByteArray();
                        try {
                            baos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        id = new Utilities().gmtToEpoch(Utilities.tsInGmt());
                        File f = convertByteArrayToFile(b, id, ".jpg");
                        uri = Uri.fromFile(f);
                    } else {
                        if (root != null) {
                            Snackbar snackbar = Snackbar.make(root, R.string.CaptureAgain, Snackbar.LENGTH_SHORT);
                            snackbar.show();
                            View view = snackbar.getView();
                            TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                            txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                        }


                    }


                } else {


                    if (root != null) {

                        Snackbar snackbar = Snackbar.make(root, R.string.CaptureAgain, Snackbar.LENGTH_SHORT);


                        snackbar.show();
                        View view = snackbar.getView();
                        TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                        txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                    }
                }


            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                if (root != null)
                {
                    Snackbar snackbar = Snackbar.make(root, R.string.ImageError, Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    View view = snackbar.getView();
                    TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                }


            }


            if (uri != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 1, baos);
                byte[] b = baos.toByteArray();
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                addMessageToSendInUi(setMessageToSend(true, 1, id, Base64.encodeToString(b, Base64.DEFAULT).trim()), true, 1, uri, false);
            }


        } else if (requestCode == RESULT_LOAD_VIDEO && resultCode == RESULT_OK && null != data) {

            Uri uri = null;
            String id = null;
            try {
                videoPath = getPath(ChatMessageScreen.this, data.getData());
                assert videoPath != null;
                File video = new File(videoPath);
                if (video.length() <= (MAX_VIDEO_SIZE)) {
                    try {
                        byte[] b = convertFileToByteArray(video);
                        id = new Utilities().gmtToEpoch(Utilities.tsInGmt());
                        File f = convertByteArrayToFile(b, id, ".mp4");
                        uri = Uri.fromFile(f);
                    } catch (OutOfMemoryError e) {
                        if (root != null) {
                            Snackbar snackbar = Snackbar.make(root, R.string.VideoError, Snackbar.LENGTH_SHORT);
                            snackbar.show();
                            View view = snackbar.getView();
                            TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                            txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                        }
                    }
                    if (uri != null)
                    {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        Bitmap bm = ThumbnailUtils.createVideoThumbnail(videoPath,
                                MediaStore.Images.Thumbnails.MINI_KIND);
                        if (bm != null) {
                            bm.compress(Bitmap.CompressFormat.JPEG, 1, baos);
                            byte[] b = baos.toByteArray();
                            try {
                                baos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            addMessageToSendInUi(setMessageToSend(true, 2, id, Base64.encodeToString(b, Base64.DEFAULT).trim()), true, 2, uri, false);
                        }
                    } else {
                        if (root != null) {

                            Snackbar snackbar = Snackbar.make(root, getString(R.string.GreaterThan) + " " + MAX_VIDEO_SIZE / (1024 * 1024) + " " + getString(R.string.Mb), Snackbar.LENGTH_SHORT);
                            snackbar.show();
                            View view = snackbar.getView();
                            TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                            txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                        }
                    }
                }
            } catch (NullPointerException e)
            {
                if (root != null) {
                    Snackbar snackbar = Snackbar.make(root, R.string.AnotherVideo, Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    View view = snackbar.getView();
                    TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                }
            }
        } else if (requestCode == REQUEST_SELECT_AUDIO && resultCode == RESULT_OK && null != data)
        {
            String id = null;
            Uri uri = null;
            try {
                audioPath = getPath(this, data.getData());
                assert audioPath != null;
                File audio = new File(audioPath);
                if (audio.length() <= (MAX_VIDEO_SIZE)) {
                    try {
                        byte[] b = convertFileToByteArray(audio);
                        id = new Utilities().gmtToEpoch(Utilities.tsInGmt());
                        File f = convertByteArrayToFile(b, id, ".mp3");
                        uri = Uri.fromFile(f);
                    } catch (OutOfMemoryError e) {
                        e.printStackTrace();
                        if (root != null) {

                            Snackbar snackbar = Snackbar.make(root, R.string.AudioAgain, Snackbar.LENGTH_SHORT);
                            snackbar.show();
                            View view = snackbar.getView();
                            TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                            txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                        }
                    }
                    if (uri != null)
                    {
                        addMessageToSendInUi(setMessageToSend(true, 5, id, null), true, 5, uri, false);
                    }
                } else {
                    if (root != null) {
                        Snackbar snackbar = Snackbar.make(root, getString(R.string.AudioGreater) + " " + MAX_VIDEO_SIZE / (1024 * 1024) + " " + getString(R.string.Mb), Snackbar.LENGTH_SHORT);
                        snackbar.show();
                        View view = snackbar.getView();
                        TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                        txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                    }
                }
            } catch (NullPointerException e)
            {
                if (root != null) {
                    Snackbar snackbar = Snackbar.make(root, R.string.AnotherAudio, Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    View view = snackbar.getView();
                    TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                }
            }
        } else if (requestCode == REQUEST_CODE_CONTACTS && resultCode == RESULT_OK && null != data)
        {
            try {
                Uri contactData = data.getData();
                Cursor c = getContentResolver().query(contactData, null, null, null, null);
                assert c != null;
                if (c.moveToFirst()) {
                    String name = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String contactID = "";
                    String contactNumber = null;
                    Cursor cursorID = getContentResolver().query(contactData,
                            new String[]{ContactsContract.Contacts._ID},
                            null, null, null);
                    assert cursorID != null;
                    if (cursorID.moveToFirst()) {
                        contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
                    }
                    cursorID.close();
                    Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                                    ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                                    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
                            new String[]{contactID},
                            null);
                    assert cursorPhone != null;
                    if (cursorPhone.moveToFirst())
                    {
                        try {
                            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            contactNumber=contactNumber.replaceAll("\\s+", "");
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                    cursorPhone.close();
                    c.close();
                    if (contactNumber == null) {
                        contactInfo = name + "@@" + getString(R.string.NoNumber);
                    } else {
                        contactInfo = name + "@@" + contactNumber;
                    }
                    addMessageToSendInUi(setMessageToSend(false, 4, null, null), false, 4, null, false);
                }
            } catch (NullPointerException e) {


                if (root != null) {

                    Snackbar snackbar = Snackbar.make(root, R.string.ContactDenied, Snackbar.LENGTH_SHORT);


                    snackbar.show();
                    View view = snackbar.getView();
                    TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                }

            }

        } else if (requestCode == RESULT_LOAD_GIF && resultCode == RESULT_OK && null != data) {


            gifUrl = data.getStringExtra("gifUrl");
            addMessageToSendInUi(setMessageToSend(false, 8, null, null), false, 8, null, false);
        } else if (requestCode == RESULT_LOAD_STICKER && resultCode == RESULT_OK && null != data) {


            /*
             * In the thumbnail field only i will send the sticker url
             */

            stickerUrl = data.getStringExtra("gifUrl");

            if (stickerUrl != null && !stickerUrl.isEmpty()) {


                addMessageToSendInUi(setMessageToSend(false, 6, null, stickerUrl), false, 6, null, false);

                recyclerView_chat.scrollToPosition(mChatData.size());
            }

        }
    }

    @SuppressWarnings("TryWithIdenticalCatches")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {


        if (requestCode == 21) {


            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {


                    Snackbar snackbar = Snackbar.make(root, R.string.TryDownload,
                            Snackbar.LENGTH_SHORT);


                    snackbar.show();
                    View view = snackbar.getView();
                    TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    txtv.setGravity(Gravity.CENTER_HORIZONTAL);

                } else {


                    Snackbar snackbar = Snackbar.make(root, R.string.StorageAccessDenied,
                            Snackbar.LENGTH_SHORT);


                    snackbar.show();
                    View view = snackbar.getView();
                    TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    txtv.setGravity(Gravity.CENTER_HORIZONTAL);

                }
            } else {


                Snackbar snackbar = Snackbar.make(root, R.string.StorageAccessDenied,
                        Snackbar.LENGTH_SHORT);


                snackbar.show();
                View view = snackbar.getView();
                TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                txtv.setGravity(Gravity.CENTER_HORIZONTAL);

            }


        } else if (requestCode == 22) {

            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED)
                {
                    if (new GPSTracker(ChatMessageScreen.this).canGetLocation())
                    {
                        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                        try {
                            startActivityForResult(builder.build(ChatMessageScreen.this), RESULT_SHARE_LOCATION);
                        } catch (GooglePlayServicesRepairableException e) {
                            e.printStackTrace();
                        } catch (GooglePlayServicesNotAvailableException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Snackbar snackbar = Snackbar.make(root, R.string.EnableLocation,
                                Snackbar.LENGTH_SHORT);
                        snackbar.show();
                        View view = snackbar.getView();
                        TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                        txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                    }


                } else {

                    Snackbar snackbar = Snackbar.make(root, R.string.LocationDenied,
                            Snackbar.LENGTH_SHORT);


                    snackbar.show();
                    View view = snackbar.getView();
                    TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                }
            } else {

                Snackbar snackbar = Snackbar.make(root, R.string.LocationDenied,
                        Snackbar.LENGTH_SHORT);


                snackbar.show();
                View view = snackbar.getView();
                TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                txtv.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        } else if (requestCode == 23) {

            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                        == PackageManager.PERMISSION_GRANTED) {
                    Intent intentContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intentContact, REQUEST_CODE_CONTACTS);


                } else {

                    Snackbar snackbar = Snackbar.make(root, R.string.ContactsDenied,
                            Snackbar.LENGTH_SHORT);


                    snackbar.show();
                    View view = snackbar.getView();
                    TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                }
            } else {

                Snackbar snackbar = Snackbar.make(root, R.string.ContactsDenied,
                        Snackbar.LENGTH_SHORT);


                snackbar.show();
                View view = snackbar.getView();
                TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                txtv.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        } else if (requestCode == 24) {

            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {


                    if (ActivityCompat.checkSelfPermission(ChatMessageScreen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED)


                    {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        if (intent.resolveActivity(getPackageManager()) != null) {

                            intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {


                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);


                            } else {


                                List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                                for (ResolveInfo resolveInfo : resInfoList) {
                                    String packageName = resolveInfo.activityInfo.packageName;
                                    grantUriPermission(packageName, imageUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                }


                            }

                            startActivityForResult(intent, RESULT_CAPTURE_IMAGE);


                        } else {
                            Snackbar snackbar = Snackbar.make(root, R.string.CameraAbsent,
                                    Snackbar.LENGTH_SHORT);
                            snackbar.show();


                            View view = snackbar.getView();
                            TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                            txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                        }
                    } else {


                        requestReadImagePermission(0);
                    }
                } else {

                    Snackbar snackbar = Snackbar.make(root, R.string.AccessCameraDenied,
                            Snackbar.LENGTH_SHORT);


                    snackbar.show();
                    View view = snackbar.getView();
                    TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                }
            } else {

                Snackbar snackbar = Snackbar.make(root, R.string.AccessCameraDenied,
                        Snackbar.LENGTH_SHORT);


                snackbar.show();
                View view = snackbar.getView();
                TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                txtv.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        }
        else if (requestCode == 26)
        {
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent, getString(R.string.SelectPicture)), RESULT_LOAD_IMAGE);
                    } else {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, getString(R.string.SelectPicture)), RESULT_LOAD_IMAGE);
                    }


                } else {

                    Snackbar snackbar = Snackbar.make(root, R.string.StorageAccessDenied,
                            Snackbar.LENGTH_SHORT);


                    snackbar.show();
                    View view = snackbar.getView();
                    TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                }
            } else {

                Snackbar snackbar = Snackbar.make(root, R.string.StorageAccessDenied,
                        Snackbar.LENGTH_SHORT);


                snackbar.show();
                View view = snackbar.getView();
                TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                txtv.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        } else if (requestCode == 27) {

            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent();
                    intent.setType("video/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(intent, RESULT_LOAD_VIDEO);

                } else {

                    Snackbar snackbar = Snackbar.make(root, R.string.StorageAccessDenied,
                            Snackbar.LENGTH_SHORT);


                    snackbar.show();
                    View view = snackbar.getView();
                    TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                }
            } else {

                Snackbar snackbar = Snackbar.make(root, R.string.StorageAccessDenied,
                        Snackbar.LENGTH_SHORT);


                snackbar.show();
                View view = snackbar.getView();
                TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                txtv.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        } else if (requestCode == 28) {

            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    Intent intent_upload = new Intent();
                    intent_upload.setType("audio/*");
                    intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent_upload, REQUEST_SELECT_AUDIO);
                } else {

                    Snackbar snackbar = Snackbar.make(root, R.string.StorageAccessDenied,
                            Snackbar.LENGTH_SHORT);


                    snackbar.show();
                    View view = snackbar.getView();
                    TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                }
            } else {

                Snackbar snackbar = Snackbar.make(root, R.string.StorageAccessDenied,
                        Snackbar.LENGTH_SHORT);


                snackbar.show();
                View view = snackbar.getView();
                TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                txtv.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        } else if (requestCode == 29) {

            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)
                        == PackageManager.PERMISSION_GRANTED) {


                    saveContact(contactInfoForSaving);


                } else {

                    Snackbar snackbar = Snackbar.make(root, R.string.ContactsDenied,
                            Snackbar.LENGTH_SHORT);


                    snackbar.show();
                    View view = snackbar.getView();
                    TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                }
            } else {

                Snackbar snackbar = Snackbar.make(root, R.string.ContactsDenied,
                        Snackbar.LENGTH_SHORT);


                snackbar.show();
                View view = snackbar.getView();
                TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                txtv.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        } else if (requestCode == 37) {
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(ChatMessageScreen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity(getPackageManager()) != null) {

                        intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        } else {
                            List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                            for (ResolveInfo resolveInfo : resInfoList) {
                                String packageName = resolveInfo.activityInfo.packageName;
                                grantUriPermission(packageName, imageUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            }
                        }
                        startActivityForResult(intent, RESULT_CAPTURE_IMAGE);
                    } else {
                        Snackbar snackbar = Snackbar.make(root, R.string.CameraAbsent,
                                Snackbar.LENGTH_SHORT);
                        snackbar.show();


                        View view = snackbar.getView();
                        TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                        txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                    }
                } else {

                    Snackbar snackbar = Snackbar.make(root, R.string.StorageAccessDenied,
                            Snackbar.LENGTH_SHORT);


                    snackbar.show();
                    View view = snackbar.getView();
                    TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                }

            } else {

                Snackbar snackbar = Snackbar.make(root, R.string.StorageAccessDenied,
                        Snackbar.LENGTH_SHORT);
                snackbar.show();
                View view = snackbar.getView();
                TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                txtv.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        }
        else if (requestCode == 47)
        {
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
            {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                {} else {
                    Snackbar snackbar = Snackbar.make(root, R.string.StorageAccessDenied,
                            Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    View view = snackbar.getView();
                    TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                }
            } else {

                Snackbar snackbar = Snackbar.make(root, R.string.StorageAccessDenied,
                        Snackbar.LENGTH_SHORT);
                snackbar.show();
                View view = snackbar.getView();
                TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                txtv.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        }
    }


    /*
     * Helper class to facilitate swipe to delete messages functionality
     */

    @SuppressWarnings("all")
    private Uri setImageUri() {
        String name = Utilities.tsInGmt();
        name = new Utilities().gmtToEpoch(name);


        File folder = new File(Environment.getExternalStorageDirectory().getPath() + ApiOnServer.IMAGE_CAPTURE_URI);

        if (!folder.exists() && !folder.isDirectory()) {
            folder.mkdirs();
        }


        File file = new File(Environment.getExternalStorageDirectory().getPath() + ApiOnServer.IMAGE_CAPTURE_URI, name + ".jpg");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Uri imgUri = FileProvider.getUriForFile(ChatMessageScreen.this, getApplicationContext().getPackageName() + ".provider", file);
        this.imageUri = imgUri;
        this.picturePath = file.getAbsolutePath();
        name = null;
        folder = null;
        file = null;
        return imgUri;
    }

    /*
     *Opening the attachment dialog box */
    public void openDialog()
    {
        closingAct=false;
        closeKeypad();
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View inflatedView;
        inflatedView = layoutInflater.inflate(R.layout.custom_dialog_options_menu, null, false);
        LinearLayout layoutGallery, layoutPhoto, layoutLocation, payPalView;
        TextView tvGallery, tvPhoto,tvLocation,paypaltext;
        tvGallery = (TextView) inflatedView.findViewById(R.id.tvGallery);
        tvPhoto = (TextView) inflatedView.findViewById(R.id.tvPhoto);
        tvLocation = (TextView) inflatedView.findViewById(R.id.tvLocation);
        paypaltext=(TextView)inflatedView.findViewById(R.id.paypaltext);
        Typeface face = AppController.getInstance().getRobotoCondensedFont();
        tvGallery.setTypeface(face, Typeface.NORMAL);
        tvPhoto.setTypeface(face, Typeface.NORMAL);
        paypaltext.setTypeface(face, Typeface.NORMAL);
        tvLocation.setTypeface(face, Typeface.NORMAL);
        layoutGallery = (LinearLayout) inflatedView.findViewById(R.id.layoutGallery);
        layoutGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FloatingView.dismissWindow();
                checkReadImage();
            }
        });

        layoutPhoto = (LinearLayout) inflatedView.findViewById(R.id.layoutPhoto);
        layoutPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                FloatingView.dismissWindow();
                checkCameraPermissionImage();
            }
        });


        layoutLocation = (LinearLayout) inflatedView.findViewById(R.id. layoutLocation);
        layoutLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FloatingView.dismissWindow();
                checkLocationAccessPermission();
            }
        });




        payPalView = (LinearLayout) inflatedView.findViewById(R.id.paypal_view);
        payPalView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                FloatingView.dismissWindow();
                if(!isAccepted)
                {
                    String message;
                    if(isSeller)
                    {
                        message=getString(R.string.sell_first);
                    }else
                    {
                        message=getString(R.string.purchased_first);
                    }
                    Snackbar snackbar = Snackbar.make(root,message, Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    View currentView = snackbar.getView();
                    TextView txtv = (TextView) currentView.findViewById(android.support.design.R.id.snackbar_text);
                    txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                    return;
                }

            }
        });
        LinearLayout item_raw=(LinearLayout)inflatedView.findViewById(R.id.item_raw);
        if(isSeller)
        {
            payPalView.setVisibility(View.VISIBLE);
            item_raw.setWeightSum(4f);
        }else
        {
            payPalView.setVisibility(View.GONE);
            item_raw.setWeightSum(3f);
        }
        FloatingView.onShowPopup(this, inflatedView);
    }

    /*
     * To load first 10 messages from  the local couchDb database
     */
    public void loadFromDbFirstTen()
    {
        ArrayList<Map<String, Object>> arrMessage = db.retrieveAllMessages(documentId);
        int s = arrMessage.size();
        size = (s > 10) ? (s - 10) : (0);
        boolean lastMessage = false;
        Map<String, Object> mapMessage;
        String ts, messageType, id, message, deliveryStatus, senderName;
        boolean isSelf;
        for (int i = s - 1; i >= size; i--) {

            try {
                mapMessage = (arrMessage.get(i));
                ts = (String) mapMessage.get("Ts");
                isSelf = (boolean) mapMessage.get("isSelf");
                messageType = (String) mapMessage.get("messageType");
                id = (String) mapMessage.get("id");
                message = (String) mapMessage.get("message");
                deliveryStatus = (String) mapMessage.get("deliveryStatus");
                senderName = (String) mapMessage.get("from");
                Log.d("msg_datype",""+messageType);
                if (!lastMessage && !isSelf) {
                    lastMessage = true;
                    createObjectToSend(id, receiverUid);
                }
                int downloadStatus = -1;
                String thumbnailPath = null;
                String offerType=null;
                int size = -1;
                switch (messageType) {
                    case "0":
                /*
                 * Text message
                 */
                        MessageType = 0;
                        break;
                    case "16":
                        MessageType =16;
                        break;
                    case "15":
                        //Offer msg
                        MessageType = 15;
                        offerType = (String) mapMessage.get("offerType");
                        break;
                    case "1":
                        MessageType = 1;
                        downloadStatus = (int) mapMessage.get("downloadStatus");
                        if (downloadStatus == 0) {
                            thumbnailPath = (String) mapMessage.get("thumbnailPath");
                            size = (int) mapMessage.get("dataSize");
                        }

                        break;
                    case "2":
/*
 * Video
 */

                        MessageType = 2;


                        downloadStatus = (int) mapMessage.get("downloadStatus");


                        if (downloadStatus == 0) {


                            thumbnailPath = (String) mapMessage.get("thumbnailPath");


                            size = (int) mapMessage.get("dataSize");


                        }


                        break;
                    case "3":
                /*
                 * Location
                 */


                        MessageType = 3;


                        break;
                    case "4":


                /*
                 * Contact
                 */

                        MessageType = 4;

                        break;
                    case "5":


                   /*
                 * Audio
                 */
                        MessageType = 5;


                        downloadStatus = (int) mapMessage.get("downloadStatus");
                        if (downloadStatus == 0) {
                            size = (int) mapMessage.get("dataSize");
                        }
                        break;
                    case "6":
                        MessageType = 6;

                        break;
                    case "7":
                        MessageType = 7;
                        downloadStatus = (int) mapMessage.get("downloadStatus");
                        if (downloadStatus == 0) {
                            thumbnailPath = (String) mapMessage.get("thumbnailPath");
                            size = (int) mapMessage.get("dataSize");
                        }
                        break;
                    case "8":
                        MessageType = 8;
                        break;
                }
                if (message != null)
                {
                    loadFromDb(MessageType, offerType ,isSelf, message, senderName, ts, deliveryStatus, id,status,downloadStatus, thumbnailPath, size);
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
        MessageType = 0;
    }


    /*
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (keyboardListenersAttached)
        {
            rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(keyboardLayoutListener);
        }
        AppController.getInstance().setActiveReceiverId("");
        AppController.getInstance().setActiveSecretId("");
        AppController.getInstance().unsubscribeToTopic(MqttEvents.OnlineStatus.value+"/"+receiverUid);
        sendButton = null;
        selEmoji = null;
        selKeybord = null;
        sendMessage = null;
        llm = null;
        profilePic = null;
        tv = null;
        receiverNameHeader = null;
        backButton = null;
        drawable2 = null;
        drawable1 = null;
        userId = null;
        userName = null;
        receiverUid = null;
        documentId = null;
        tsForServerEpoch = null;
        tsForServer = null;
        videoPath = null;
        audioPath = null;
        mAdapter = null;
        recyclerView_chat = null;
        receiverName = null;
        imageUrl = null;
        placeString = null;
        contactInfo = null;
        mChatData = null;
        Glide.get(this).clearMemory();
        Glide.get(this).getBitmapPool().clearMemory();
        bus.unregister(this);
    }

    /*
     * To acknowledge all messages above last message has been read
     */
    private void createObjectToSend(String id, String recieverUid) {
        JSONObject obj = new JSONObject();

        try {
            obj.put("from", userId);
            obj.put("msgIds", new JSONArray(Arrays.asList(new String[]{id})));
            obj.put("doc_id", db.getDocumentIdOfReceiver(documentId, recieverUid));
            obj.put("to", recieverUid);
            obj.put("status", "3");
            obj.put("secretId", secretId);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        AppController.getInstance().publish(MqttEvents.Acknowledgement.value + "/" + receiverUid, obj, 2, false);
    }

    /*
     * to convert string from the 24 hour format to 12 hour format
     */
    private void updateLastSeenInActionBar(JSONObject obj)
    {
        try {
            if (obj.getBoolean("lastSeenEnabled"))
            {
                switch (obj.getInt("status"))
                {
                    case 1:
                        opponentOnline = true;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                top = getString(R.string.Online);
                                if (tv != null)
                                    tv.setText(top);
                            }
                        });
                        break;
                    case 0:
                        opponentOnline = false;
                        String lastSeen = obj.getString("timestamp");
                        lastSeen = Utilities.changeStatusDateFromGMTToLocal(lastSeen);
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS z");
                        Date date2 = new Date(System.currentTimeMillis() - AppController.getInstance().getTimeDelta());
                        String current_date = sdf.format(date2);
                        current_date = current_date.substring(0, 8);
                        if (lastSeen != null)
                        {
                            final String onlineStatus;
                            if (current_date.equals(lastSeen.substring(0, 8))) {
                                lastSeen = convert24to12hourformat(lastSeen.substring(8, 10) + ":" + lastSeen.substring(10, 12));
                                onlineStatus = "Last Seen:Today " + lastSeen;

                            } else {
                                String last = convert24to12hourformat(lastSeen.substring(8, 10) + ":" + lastSeen.substring(10, 12));
                                String date = lastSeen.substring(6, 8) + "-" + lastSeen.substring(4, 6) + "-" + lastSeen.substring(0, 4);
                                onlineStatus = "Last Seen:" + date + " " + last;
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    top = onlineStatus;
                                    if (tv != null)
                                    {
                                        tv.setText(top);
                                    }
                                }
                            });
                        }
                        break;
                    case 2:

                        opponentOnline = false;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                top = getString(R.string.Offline);
                                if (tv != null)
                                    tv.setText(top);
                            }
                        });
                }
                try {
                    header_rl.setVisibility(View.VISIBLE);
                    header_center_rl.setVisibility(View.GONE);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            } else {

            /*
             * Have to update the visibility
             */
                switch (obj.getInt("status")) {

                    case 1:
                        opponentOnline = true;
                        break;
                    case 0:

                        opponentOnline = false;

                        break;
                    case 2:
                        opponentOnline = false;
                }
                try {
                    header_rl.setVisibility(View.GONE);
                    header_center_rl.setVisibility(View.VISIBLE);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
     * status--
     * 1-reached server
     * 2-delivered
     * 3-read
     */
    private void drawDoubleTick(String docId, String id, String status)
    {
        if (documentId.equals(docId))
        {
            boolean flag = false;
            for (int i = mChatData.size() - 1; i >= 0; i--) {
                if (mChatData.get(i).isSelf() && (mChatData.get(i).getMessageId().equals(id))) {
                    flag = true;
                    switch (status) {
                        case "1":
                            if (mChatData.get(i).getDeliveryStatus().equals("0"))
                            {
                                mChatData.get(i).setDeliveryStatus("1");
                                final int k = i;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mAdapter.notifyItemChanged(k);
                                    }
                                });
                            }
                            break;
                        case "2":
                        {
                            mChatData.get(i).setDeliveryStatus("2");
                            final int k = i;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run()
                                {
                                    mAdapter.notifyItemChanged(k);
                                }
                            });
                        }
                        break;
                        case "3":
                            if (!(mChatData.get(i).getDeliveryStatus().equals("3"))) {
                                for (int j = i; j >= 0; j--) {
                                    if (mChatData.get(j).isSelf() && !mChatData.get(j).getDeliveryStatus().equals("0")) {
                                        if (mChatData.get(j).getDeliveryStatus().equals("3")) {
                                            break;
                                        } else {
                                            mChatData.get(j).setDeliveryStatus("3");
                                            final int k = j;
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mAdapter.notifyItemChanged(k);
                                                }
                                            });
                                        }
                                    }
                                }
                            }
                            break;
                    }
                }
                if (flag) {
                    break;
                }
            }
        }
    }

    /*
     * To draw single tick when message has been received and acknowledged by the server
     */
    private void drawSingleTick(String id) {

        for (int i = mChatData.size() - 1; i >= 0; i--) {
            if (mChatData.get(i).isSelf() && (mChatData.get(i).getMessageId()).equals(id))
            {
                if (!(mChatData.get(i).getDeliveryStatus().equals("1"))) {
                    mChatData.get(i).setDeliveryStatus("1");
                    final int k = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyItemChanged(k);
                        }
                    });
                    break;
                }
            }}
    }


    /*
     * file uri will not be null only in case of image,audio or video
     */

    /*
     * To load messgae received on the socket at appropriate position in list of the messages
     */
    private void loadMessageInChatUI(String sender, String id, String messageType,String offerType,String message, String tsFromServer,
                                     int dataSize,boolean isFromself) {


        byte[] data = Base64.decode(message, Base64.DEFAULT);
        String ts = Utilities.formatDate(Utilities.tsFromGmt(Utilities.epochtoGmt(tsFromServer)));
        RetrieveSecretChatMessageItem messageItem = new RetrieveSecretChatMessageItem();
        messageItem.setSenderName(receiverName);
        messageItem.setIsSelf(isFromself);

        if ((messageType.equals("1")) || (messageType.equals("2")) || (messageType.equals("5")) || (messageType.equals("7"))) {
            String size;


            if (dataSize < 1024) {


                size = dataSize + " bytes";

            } else if (dataSize >= 1024 && dataSize <= 1048576) {


                size = (dataSize / 1024) + " KB";

            } else {


                size = (dataSize / 1048576) + " MB";
            }


            messageItem.setSize(size);
        }


        messageItem.setReceiverUid(sender);
        messageItem.setTS(ts.substring(0, 9));
        messageItem.setMessageDateOverlay(ts.substring(9, 24));
        messageItem.setMessageDateGMTEpoch(Long.parseLong(tsFromServer));
        messageItem.setMessageId(id);

        switch (messageType) {
            case "0":
                messageItem.setMessageType("0");
                try {
                    messageItem.setTextMessage(new String(data, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case "16":
                messageItem.setMessageType("16");
                try {
                    messageItem.setTextMessage(new String(data, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case "15":
                messageItem.setMessageType("15");
                try {
                    messageItem.setTextMessage(new String(data, "UTF-8"));
                    messageItem.setOfferType(offerType);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case "1":

                messageItem.setDownloading(false);
                messageItem.setMessageType("1");
                messageItem.setDownloadStatus(0);
                messageItem.setThumbnailPath(getFilesDir() + ApiOnServer.CHAT_RECEIVED_THUMBNAILS_FOLDER + "/" + tsFromServer + ".jpg");
                try {
                    messageItem.setImagePath(new String(data, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case "2":
                messageItem.setMessageType("2");
                messageItem.setDownloadStatus(0);
                messageItem.setDownloading(false);
                messageItem.setThumbnailPath(getFilesDir() + ApiOnServer.CHAT_RECEIVED_THUMBNAILS_FOLDER + "/" + tsFromServer + ".jpg");
                try {
                    messageItem.setVideoPath(new String(data, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case "3":
                String placeString = "";
                try {
                    placeString = new String(data, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                messageItem.setMessageType("3");
                messageItem.setPlaceInfo(placeString);

                break;
            case "4":
                String contactString = "";
                try {
                    contactString = new String(data, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                messageItem.setMessageType("4");
                messageItem.setContactInfo(contactString);
                break;
            case "5":
                messageItem.setMessageType("5");
                messageItem.setDownloadStatus(0);
                messageItem.setDownloading(false);
                try {
                    messageItem.setAudioPath(new String(data, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case "6":
                messageItem.setMessageType("6");
                try {
                    messageItem.setStickerUrl(new String(data, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case "7":
                messageItem.setMessageType("7");
                messageItem.setDownloadStatus(0);
                messageItem.setThumbnailPath(getFilesDir() + ApiOnServer.CHAT_RECEIVED_THUMBNAILS_FOLDER + "/" + tsFromServer + ".jpg");
                try {
                    messageItem.setImagePath(new String(data, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case "8":
                messageItem.setMessageType("8");
                try {
                    messageItem.setGifUrl(new String(data, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
        }

        final int position = getPositionOfMessage(Long.parseLong(tsFromServer));
        mChatData.add(position, messageItem);
        /*
         *Adding this for offer accepted.*/
        if(messageItem.getMessageType().equals("15")&&messageItem.getOfferType()!=null&&messageItem.getOfferType().equals("2"))
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    isAccepted=true;
                    UpdateSellPrice();
                    mAdapter.notifyDataSetChanged();
                    llm.scrollToPositionWithOffset(mAdapter.getItemCount() - 1, 0);
                }
            });
        }else
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyItemInserted(position);
                    llm.scrollToPositionWithOffset(mAdapter.getItemCount() - 1, 0);
                }
            });
        }
    }

    /*
     * To load previous 10 messages exchanged (if any) before the message on top
     */
    @SuppressWarnings("TryWithIdenticalCatches")
    private void loadTenMore()
    {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage(getString(R.string.Loading));
        pDialog.setCancelable(false);
        runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                pDialog.show();
                ProgressBar bar = (ProgressBar) pDialog.findViewById(android.R.id.progress);
                bar.getIndeterminateDrawable().setColorFilter(
                        ContextCompat.getColor(ChatMessageScreen.this, R.color.progress_drawable_color),
                        android.graphics.PorterDuff.Mode.SRC_IN);
            }
        });
        status = 1;
        ArrayList<Map<String, Object>> arrMessage = db.retrieveAllMessages(documentId);
        Map<String, Object> mapMessage;
        int s1 = (size > 10) ? (size - 10) : (0);
        String ts, messageType, id, message, deliveryStatus, senderName,offerType=null;
        boolean isSelf;
        for (int i = size - 1; i >= s1; i--)
        {
            try {
                mapMessage = (arrMessage.get(i));
                ts = (String) mapMessage.get("Ts");
                isSelf = (boolean) mapMessage.get("isSelf");
                messageType = (String) mapMessage.get("messageType");
                id = (String) mapMessage.get("id");
                message = (String) mapMessage.get("message");
                deliveryStatus = (String) mapMessage.get("deliveryStatus");
                senderName = (String) mapMessage.get("from");
                int downloadStatus = -1;
                String thumbnailPath = null;
                int size = -1;
                switch (messageType) {
                    case "0":
                        MessageType = 0;
                        break;
                    case "16":
                        MessageType = 16;
                        break;
                    case "1":
                        MessageType = 1;
                        downloadStatus = (int) mapMessage.get("downloadStatus");
                        if (downloadStatus == 0) {
                            thumbnailPath = (String) mapMessage.get("thumbnailPath");
                            size = (int) mapMessage.get("dataSize");
                        }
                        break;
                    case "2":

                /*
                 * Video
                 */


                        MessageType = 2;


                        downloadStatus = (int) mapMessage.get("downloadStatus");


                        if (downloadStatus == 0) {


                            thumbnailPath = (String) mapMessage.get("thumbnailPath");
                            size = (int) mapMessage.get("dataSize");
                        }


                        break;
                    case "3":


                /*
                 * Location
                 */
                        MessageType = 3;


                        break;
                    case "4":

                /*
                 * Contact
                 */

                        MessageType = 4;

                        break;
                    case "5":


                /*
                 * Audio
                 */
                        MessageType = 5;


                        downloadStatus = (int) mapMessage.get("downloadStatus");


                        if (downloadStatus == 0) {


                            size = (int) mapMessage.get("dataSize");
                        }

                        break;
                    case "6":

                /*
                 *Sticker
                 */


                        MessageType = 6;
                        break;
                    case "7":

                /*
                 * Doodle
                 */

                        MessageType = 7;
                        downloadStatus = (int) mapMessage.get("downloadStatus");
                        if (downloadStatus == 0) {
                            thumbnailPath = (String) mapMessage.get("thumbnailPath");
                            size = (int) mapMessage.get("dataSize");
                        }
                        break;
                    case "8":
                        MessageType = 8;
                        break;
                    case "15":
                        offerType = (String) mapMessage.get("messageType");
                        MessageType = 15;
                        break;
                }
                if (message != null)
                {
                    loadFromDb(MessageType, offerType, isSelf, message, senderName, ts, deliveryStatus, id, status,
                            downloadStatus, thumbnailPath, size);
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }


        size = (size > 10) ? (size - 10) : (0);

        MessageType = 0;


        if (sendMessage.getText().length() == 1) {
            sendMessage.setText("");
        }

        if (pDialog.isShowing())
        {
            Context context = ((ContextWrapper) (pDialog).getContext()).getBaseContext();
            if (context instanceof Activity)
            {
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
                }}}
    }

    /*
     * To add the messages loaded from the db to the list of messages in the UI
     */
    @SuppressWarnings("unchecked")

    public void loadFromDb(int messageType,String offerType,boolean isSelf, String message,
                           String senderName, String timestamp, String deliveryStatus, String id,
                           int status, int downloadStatus, String thumbnailPath, int datasize) {
        String date = Utilities.formatDate(Utilities.tsFromGmt(timestamp));
        RetrieveSecretChatMessageItem message_item = new RetrieveSecretChatMessageItem();
        if (isSelf)
        {
            message_item.setSenderName(senderName);
        } else {
            message_item.setSenderName(receiverName);
        }
        message_item.setReceiverUid(receiverUid);
        message_item.setDownloading(false);
        message_item.setDownloadStatus(downloadStatus);
        if (downloadStatus == 0) {

            if (messageType == 1 || messageType == 2 || messageType == 7) {
                message_item.setThumbnailPath(thumbnailPath);
            }
            String size;
            if (datasize < 1024) {
                size = datasize + " bytes";
            } else if (datasize >= 1024 && datasize <= 1048576) {
                size = (datasize / 1024) + " KB";
            } else {
                size = (datasize / 1048576) + " MB";
            }
            message_item.setSize(size);
        }
        message_item.setIsSelf(isSelf);
        message_item.setTS(date.substring(0, 9));
        message_item.setMessageId(id);
        message_item.setMessageDateOverlay(date.substring(9, 24));
        message_item.setMessageDateGMTEpoch(Long.parseLong(new Utilities().gmtToEpoch(timestamp)));
        message_item.setDeliveryStatus(deliveryStatus);
        message_item.setMessageType(Integer.toString(messageType));
        if (messageType == 0)
        {
            if ((message.equals(getResources().getString(R.string.youAreInvited) + " " +
                    receiverName + " " + getResources().getString(R.string.JoinSecretChat))) ||
                    (message.equals(getResources().getString(R.string.YouInvited) + " " +
                            receiverName + " " + getResources().getString(R.string.JoinSecretChat))))
            {
                return;

            } else {
                message_item.setTextMessage(message);
            }
        }else if (messageType == 16)
        {
            message_item.setTextMessage(message);
        }  else if (messageType == 1)
        {
            message_item.setImagePath(message);
        } else if (messageType == 2)
        {
            try {
                message_item.setVideoPath(message);
            } catch (NullPointerException e) {

                e.printStackTrace();
            }
        } else if (messageType== 3)
        {
            message_item.setPlaceInfo(message);
        } else if (messageType == 4)
        {
            message_item.setContactInfo(message);
        } else if (messageType == 5)
        {
            try {
                message_item.setAudioPath(message);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } else if (messageType == 6) {
            message_item.setStickerUrl(message);
        } else if (messageType == 7) {
            message_item.setImagePath(message);
        } else if (messageType == 8)
        {
            try {
                message_item.setGifUrl(message);
            } catch (NullPointerException e)
            {
                e.printStackTrace();
            }
        }else  if(messageType==15)
        {
            message_item.setOfferType(offerType);
            message_item.setTextMessage(message);
        }
        mChatData.add(0, message_item);
        if(message_item.getMessageType().equals("15")&&message_item.getOfferType().equals("2"))
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    isAccepted=true;
                    UpdateSellPrice();
                    mAdapter.notifyDataSetChanged();
                    llm.scrollToPositionWithOffset(mAdapter.getItemCount() - 1, 0);
                }
            });
        }else
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    mAdapter.notifyItemInserted(0);
                }
            });
            if (status == 0)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        llm.scrollToPositionWithOffset(mAdapter.getItemCount() - 1, 0);
                    }
                });}}
    }


    /*
     * To find the position at which to insert the newly received message
     */
    private boolean closingAct=false;
    @Override
    public void onBackPressed()
    {
        if(isKeypadOpen)
        {
            closingAct=true;
            closeKeypad();
        }else
        {
            closingAct=false;
            closeActivity();
        }
    }

    /*
     *Closing the key pad. */
    private void closeKeypad()
    {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(sendMessage.getWindowToken(), 0);
    }

    /*
     *Close the activity */
    private void closeActivity()
    {
        if(isFromOfferPage)
        {
            CommonClass.isBackFromChat=true;
            Intent   intent = new Intent(this, HomePageActivity.class);
            Bundle bundle=new Bundle();
            bundle.putBoolean("openChat",true);
            intent.putExtras(bundle);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        super.onBackPressed();
        this.supportFinishAfterTransition();
    }


///////////////////////////////////////////////////////////////////////////////////////////////////////////

    @SuppressLint("SimpleDateFormat")
    public String convert24to12hourformat(String d)
    {
        String datein12hour = null;
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            final Date dateObj = sdf.parse(d);
            datein12hour = new SimpleDateFormat("h:mm a").format(dateObj);
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return datein12hour;
    }

    /*
     * To find the date to be shown for the date overlay on top,which shows
     * the date of message currently on top.
     */
    private String findOverlayDate(String date)
    {
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("EEE dd/MMM/yyyy");
            String m1 = "", m2 = "";
            String month1, month2;
            String d1, d2;
            d1 = sdf.format(new Date(System.currentTimeMillis() - AppController.getInstance().getTimeDelta()));
            d2 = date;
            month1 = d1.substring(7, 10);
            month2 = d2.substring(7, 10);

            if (month1.equals("Jan")) {
                m1 = "01";
            } else if (month1.equals("Feb")) {
                m1 = "02";
            } else if (month2.equals("Mar")) {
                m2 = "03";
            } else if (month1.equals("Apr")) {
                m1 = "04";
            } else if (month1.equals("May")) {
                m1 = "05";
            } else if (month1.equals("Jun")) {
                m1 = "06";
            } else if (month1.equals("Jul")) {
                m1 = "07";
            } else if (month1.equals("Aug")) {
                m1 = "08";
            } else if (month1.equals("Sep")) {
                m1 = "09";
            } else if (month1.equals("Oct")) {
                m1 = "10";
            } else if (month1.equals("Nov")) {
                m1 = "11";
            } else if (month1.equals("Dec")) {
                m1 = "12";
            }

            if (month2.equals("Jan")) {
                m2 = "01";
            } else if (month2.equals("Feb")) {
                m2 = "02";
            } else if (month1.equals("Mar")) {
                m1 = "03";
            } else if (month2.equals("Apr")) {
                m2 = "04";
            } else if (month2.equals("May")) {
                m2 = "05";
            } else if (month2.equals("Jun")) {
                m2 = "06";
            } else if (month2.equals("Jul")) {
                m2 = "07";
            } else if (month2.equals("Aug")) {
                m2 = "08";
            } else if (month2.equals("Sep")) {
                m2 = "09";
            } else if (month2.equals("Oct")) {
                m2 = "10";
            } else if (month2.equals("Nov")) {
                m2 = "11";
            } else if (month2.equals("Dec")) {
                m2 = "12";
            }
            if (sdf.format(new Date(System.currentTimeMillis() - AppController.getInstance().getTimeDelta())).equals(date)) {
                return "Today";
            } else if ((Integer.parseInt(d1.substring(11) + m1 + d1.substring(4, 6)) - Integer.parseInt(d2.substring(11) + m2 + d2.substring(4, 6))) == 1) {
                return "Yesterday";
            } else {
                return date;
            }
        } catch (Exception e)
        {
            return date;
        }

    }

    /*
     * To add message to send in the list of messages
     */
    private void addMessageToSendInUi(JSONObject obj, boolean isImageOrVideoOrAudio, int messageType, Uri uri, boolean creatingSecretChat)
    {
        Log.d("log47",""+messageType);
        if (!creatingSecretChat) {
            String tempDate = Utilities.formatDate(Utilities.tsFromGmt(tsForServer));
            RetrieveSecretChatMessageItem message = new RetrieveSecretChatMessageItem();
            message.setSenderName(userName);
            message.setIsSelf(true);
            message.setTS(tempDate.substring(0, 9));
            message.setMessageDateOverlay(tempDate.substring(9, 24));
            message.setMessageDateGMTEpoch(Long.parseLong(tsForServerEpoch));
            message.setDeliveryStatus("0");
            message.setMessageId(tsForServerEpoch);
            message.setDownloadStatus(1);
        /*
         * db will contain upload status field only for the image ,
         * video or audio item.
         */
            String mess_str;
            if (messageType == 0)
            {
                /*
             * Text message
             */
                mess_str = sendMessage.getText().toString().trim();
                message.setTextMessage(mess_str);
                message.setMessageType("0");
                Map<String, Object> map = new HashMap<>();
                map.put("message", mess_str);
                map.put("messageType", "0");
                map.put("isSelf", true);
                map.put("from", userName);
                map.put("Ts", tsForServer);
                map.put("deliveryStatus", "0");
                map.put("id", tsForServerEpoch);
                AppController.getInstance().getDbController().addNewChatMessageAndSort(documentId, map, tsForServer);
            }else    if (messageType ==16)
            {
                mess_str =paypal_link.trim();
                message.setTextMessage(mess_str);
                message.setMessageType("16");
                Map<String, Object> map = new HashMap<>();
                map.put("message", mess_str);
                map.put("messageType", "16");
                map.put("isSelf", true);
                map.put("from", userName);
                map.put("Ts", tsForServer);
                map.put("deliveryStatus", "0");
                map.put("id", tsForServerEpoch);
                AppController.getInstance().getDbController().addNewChatMessageAndSort(documentId, map, tsForServer);
            }else if(messageType==15)
            {
                mess_str = sendMessage.getText().toString().trim();
                message.setTextMessage(mess_str);
                message.setMessageType("15");
                Map<String, Object> map = new HashMap<>();
                map.put("message", mess_str);
                map.put("messageType", "15");
                map.put("isSelf", true);
                map.put("from", userName);
                map.put("Ts", tsForServer);
                map.put("deliveryStatus", "0");
                map.put("id", tsForServerEpoch);
                AppController.getInstance().getDbController().addNewChatMessageAndSort(documentId, map, tsForServer);
            }else if (messageType == 1)
            {
            /*
             * receiverImage
             */
                message.setImagePath(picturePath);
                message.setMessageType("1");
                message.setImageUrl(imageUrl);
                Map<String, Object> map = new HashMap<>();
                map.put("message", picturePath);
                map.put("messageType", "1");
                map.put("isSelf", true);
                map.put("downloadStatus", 1);
                map.put("from", userName);
                map.put("Ts", tsForServer);
                map.put("deliveryStatus", "0");
                map.put("id", tsForServerEpoch);
                AppController.getInstance().getDbController().addNewChatMessageAndSort(documentId, map, tsForServer);
            } else if (messageType == 2) {

            /*
             * Video
             */
                message.setVideoPath(videoPath);
                message.setMessageType("2");
                Map<String, Object> map = new HashMap<>();
                map.put("message", videoPath);
                map.put("messageType", "2");
                map.put("isSelf", true);
                map.put("downloadStatus", 1);
                map.put("from", userName);
                map.put("Ts", tsForServer);
                map.put("deliveryStatus", "0");
                map.put("id", tsForServerEpoch);
                AppController.getInstance().getDbController().addNewChatMessageAndSort(documentId, map, tsForServer);
            } else if (messageType == 3) {
            /*
             * Location
             */
                message.setPlaceInfo(placeString);
                message.setMessageType("3");
                Map<String, Object> map = new HashMap<>();
                map.put("message", placeString);
                map.put("messageType", "3");
                map.put("isSelf", true);
                map.put("from", userName);
                map.put("Ts", tsForServer);
                map.put("deliveryStatus", "0");
                map.put("id", tsForServerEpoch);
                AppController.getInstance().getDbController().addNewChatMessageAndSort(documentId, map, tsForServer);
            } else if (messageType == 4) {
            /*
             * Contact
             */


                message.setContactInfo(contactInfo);
                message.setMessageType("4");
                Map<String, Object> map = new HashMap<>();
                map.put("message", contactInfo);
                map.put("messageType", "4");
                map.put("isSelf", true);
                map.put("from", userName);
                map.put("Ts", tsForServer);
                map.put("deliveryStatus", "0");
                map.put("id", tsForServerEpoch);
                AppController.getInstance().getDbController().addNewChatMessageAndSort(documentId, map, tsForServer);
            } else if (messageType == 5) {

/*
 * Audio
 */
                message.setAudioPath(audioPath);
                message.setMessageType("5");


                Map<String, Object> map = new HashMap<>();
                map.put("message", audioPath);
                map.put("messageType", "5");
                map.put("isSelf", true);
                map.put("from", userName);
                map.put("Ts", tsForServer);
                map.put("downloadStatus", 1);
                map.put("deliveryStatus", "0");
                map.put("id", tsForServerEpoch);
                AppController.getInstance().getDbController().addNewChatMessageAndSort(documentId, map, tsForServer);
            } else if (messageType == 6) {


            /*
             *Stickers
             */


                message.setStickerUrl(stickerUrl);
                message.setMessageType("6");

                Map<String, Object> map = new HashMap<>();
                map.put("message", stickerUrl);
                map.put("messageType", "6");
                map.put("isSelf", true);
                map.put("from", userName);
                map.put("Ts", tsForServer);
                map.put("deliveryStatus", "0");
                map.put("id", tsForServerEpoch);
                AppController.getInstance().getDbController().addNewChatMessageAndSort(documentId, map, tsForServer);
            } else if (messageType == 7) {

            /*
             * Doodle
             */
                message.setImagePath(picturePath);

                message.setMessageType("7");

                message.setImageUrl(imageUrl);


                Map<String, Object> map = new HashMap<>();
                map.put("message", picturePath);
                map.put("messageType", "7");
                map.put("downloadStatus", 1);
                map.put("isSelf", true);
                map.put("from", userName);
                map.put("Ts", tsForServer);
                map.put("deliveryStatus", "0");
                map.put("id", tsForServerEpoch);
                AppController.getInstance().getDbController().addNewChatMessageAndSort(documentId, map, tsForServer);
            } else if (messageType == 8) {

            /*
             * Gif
             */


                message.setGifUrl(gifUrl.trim());
                message.setMessageType("8");
                Map<String, Object> map = new HashMap<>();
                map.put("message", gifUrl.trim());
                map.put("messageType", "8");
                map.put("isSelf", true);
                map.put("from", userName);
                map.put("Ts", tsForServer);
                map.put("deliveryStatus", "0");
                map.put("id", tsForServerEpoch);
                AppController.getInstance().getDbController().addNewChatMessageAndSort(documentId, map, tsForServer);
            }

            Log.d("log47",""+message.getMessageType());
            mChatData.add(message);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyItemInserted(mChatData.size() - 1);
                    try {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                llm.scrollToPositionWithOffset(mAdapter.getItemCount() - 1, 0);
                            }
                        }, 500);

                    } catch (NullPointerException | IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
            });
            if (inviteVisible) {
                inviteVisible = false;
                db.updateSecretInviteImageVisibility(documentId, false);

            }
        }

        if (button01pos == 1) {

            sendButton.setImageDrawable(drawable1);

            button01pos = 0;
        }


        /*
         *
         *
         *
         * Need to store all the messages in db so that incase internet
         * not present then has to resend all messages whenever internet comes back
         *
         *
         * */


        Map<String, Object> mapTemp = new HashMap<>();
        mapTemp.put("from", userId);
        mapTemp.put("to", receiverUid);
        mapTemp.put("toDocId", documentId);
        mapTemp.put("id", tsForServerEpoch);
        mapTemp.put("timestamp", tsForServerEpoch);
        /*
         * Exclusively for secret chat
         */
        mapTemp.put("secretId", secretId);
        String type = Integer.toString(messageType);
        mapTemp.put("type", type);
        mapTemp.put("name", AppController.getInstance().getUserName());
        switch ((type)) {
            case "0":
                mapTemp.put("message", sendMessage.getText().toString().trim());
                break;
            case "16":
                mapTemp.put("message",paypal_link.trim());
                break;
            case "1":
                mapTemp.put("message", picturePath);
                picturePath = null;
                break;
            case "2":
                mapTemp.put("message", videoPath);
                videoPath = null;
                break;
            case "3":
                mapTemp.put("message", placeString);
                placeString = null;
                break;
            case "4":
                mapTemp.put("message", contactInfo);
                contactInfo = null;
                break;
            case "5":
                mapTemp.put("message", audioPath);
                audioPath = null;
                break;
            case "6":
                mapTemp.put("message", stickerUrl);
                stickerUrl = null;
                break;
            case "7":
                mapTemp.put("message", picturePath);
                picturePath = null;
                break;
            case "8":
                mapTemp.put("message", gifUrl);
                gifUrl = null;
                break;
        }
        AppController.getInstance().getDbController().addUnsentMessage(AppController.getInstance().getunsentMessageDocId(), mapTemp);


/*s
 *
 *
 * emit directly if not image or video or audio
 *
 *
 * */

        if (!isImageOrVideoOrAudio)
        {
            try {
                obj.put("name", AppController.getInstance().getUserName());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            HashMap<String, Object> map = new HashMap<>();
            map.put("messageId", tsForServerEpoch);
            map.put("docId", documentId);
            AppController.getInstance().publishChatMessage(MqttEvents.Message.value + "/" + receiverUid, obj, 1, false, map);
        } else {

/*
 *
 * if is an image or a video than have to upload and for that a dummy file in memory is
 * created which contains the compressed version of file to be send
 * */
            uploadFile(uri, userId + tsForServerEpoch, messageType, obj);
        }
        sendMessage.setText("");
        MessageType = 0;
    }
    /*
    *
    * To check for access gallery permission to select image
    * */

    /*
     * To calculate the required dimensions of image withoutb actually loading the bitmap in to the memory
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
    /*
    *
    * To check for access gallery permission to select video
    * */

    private Bitmap decodeSampledBitmapFromResource(String pathName,
                                                   int reqWidth, int reqHeight) {


        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);


        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);


        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);

    }

//    private void checkCameraPermissionVideo() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
//                == PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.checkSelfPermission( SecretChatMessageScreen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                    == PackageManager.PERMISSION_GRANTED)
//
//
//            {
//
//                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);//mms quality video not hd
//                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 120);//max 120s video
//                intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 26214400L);//max 25 mb size recording
//
//
//                startActivityForResult(intent, RESULT_CAPTURE_VIDEO);
//            } else {
//
//
//                requestReadVideoPermission(0);
//            }
//        } else {
//
//            requestCameraPermissionVideo();
//        }
//
//    }

    /*
     * Check camera permission to capture video
     */

    private int getPositionOfMessage(long ts) {


        for (int i = mChatData.size() - 1; i >= 0; i--) {


            if (mChatData.get(i).getMessageDateGMTEpoch() < ts) {


                return (i + 1);


            }
        }

        return 0;

    }

    /*
     * Uploading images and video and audio to  the server
     */
    @SuppressWarnings("TryWithIdenticalCatches,all")

    private void uploadFile(final Uri fileUri, final String name, final int messageType, final JSONObject obj) {
        FileUploadService service =
                ServiceGenerator.createService(FileUploadService.class);
        final File file = FileUtils.getFile(this, fileUri);
        String url = null;
        if (messageType == 1) {
            url = name + ".jpg";
        } else if (messageType == 2)
        {
            url = name + ".mp4";
        } else if (messageType == 5)
        {
            url = name + ".mp3";
        } else if (messageType == 7) {

            url = name + ".jpg";
        }
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("photo", url, requestFile);
        String descriptionString = getString(R.string.string_803);
        RequestBody description =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), descriptionString);
        Call<ResponseBody> call = service.upload(description, body,ApiOnServer.AUTH_KEY);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response)
            {
                Log.d("dfsd3",""+response.message());
                Log.d("dfsd3",""+response.code());
                try {
                    if (response.code() == 200)
                    {
                        String url = null;
                        if (messageType == 1) {
                            url = name + ".jpg";
                        } else if (messageType == 2) {
                            url = name + ".mp4";
                        } else if (messageType == 5) {
                            url = name + ".mp3";
                        } else if (messageType == 7) {
                            url = name + ".jpg";
                        }
                        obj.put("payload", Base64.encodeToString((ApiOnServer.CHAT_UPLOAD_PATH + url).getBytes("UTF-8"), Base64.DEFAULT));
                        obj.put("dataSize", file.length());
                        obj.put("timestamp", new Utilities().gmtToEpoch(Utilities.tsInGmt()));
                        File fdelete = new File(fileUri.getPath());
                        if (fdelete.exists()) fdelete.delete();
                    } else {
                        if (root != null) {
                            Snackbar snackbar = Snackbar.make(root, R.string.UploadFailed, Snackbar.LENGTH_SHORT);
                            snackbar.show();
                            View view = snackbar.getView();
                            TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                            txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                /*
                 *
                 *
                 * emitting to the server the values after the file has been uploaded
                 *
                 * */
                try {
                    obj.put("name", AppController.getInstance().getUserName());
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("messageId", obj.getString("id"));
                    map.put("docId", documentId);
                    AppController.getInstance().publishChatMessage(MqttEvents.Message.value + "/" + receiverUid, obj, 1, false, map);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t)
            {
                t.printStackTrace();
            }
        });
    }

    /*
     * To save the byte array received in to file
     */
    @SuppressWarnings("all")
    public File convertByteArrayToFile(byte[] data, String name, String extension) {


        File file = null;

        try {


            File folder = new File(Environment.getExternalStorageDirectory().getPath() + ApiOnServer.CHAT_UPLOAD_THUMBNAILS_FOLDER);

            if (!folder.exists() && !folder.isDirectory()) {
                folder.mkdirs();
            }


            file = new File(Environment.getExternalStorageDirectory().getPath() + ApiOnServer.CHAT_UPLOAD_THUMBNAILS_FOLDER, name + extension);
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);

            fos.write(data);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return file;

    }

    /*
    *
    * To check for camera permission
    * */
    private void checkCameraPermissionImage() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.checkSelfPermission(ChatMessageScreen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (intent.resolveActivity(getPackageManager()) != null) {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {


                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);


                    } else {


                        List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                        for (ResolveInfo resolveInfo : resInfoList) {
                            String packageName = resolveInfo.activityInfo.packageName;
                            grantUriPermission(packageName, imageUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        }


                    }

                    startActivityForResult(intent, RESULT_CAPTURE_IMAGE);
                } else {
                    Snackbar snackbar = Snackbar.make(root, R.string.CameraAbsent,
                            Snackbar.LENGTH_SHORT);
                    snackbar.show();


                    View view = snackbar.getView();
                    TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                }
            } else {


                /*
                 *permission required to save the image captured
                 */
                requestReadImagePermission(0);


            }


        } else {

            requestCameraPermissionImage();
        }

    }


       /*
    *
    * To request access location permission
    * */

    private void checkReadImage() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, getString(R.string.SelectPicture)), RESULT_LOAD_IMAGE);
            } else {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.SelectPicture)), RESULT_LOAD_IMAGE);
            }
        } else {
            requestReadImagePermission(1);
        }
    }
    /*
    * To check for the access location permission
    * */
    @SuppressWarnings("TryWithIdenticalCatches")
    private void checkLocationAccessPermission()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (new GPSTracker(ChatMessageScreen.this).canGetLocation()) {

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(ChatMessageScreen.this), RESULT_SHARE_LOCATION);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            } else
            {
                Snackbar snackbar = Snackbar.make(root, R.string.EnableLocation,
                        Snackbar.LENGTH_SHORT);
                snackbar.show();
                View view = snackbar.getView();
                TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                txtv.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        } else {
            requestLocationPermission();
        }

    }

    /*
     *
     * To check for the update contacts permission
     * */
    private void checkWriteContactPermission(String contactInfo) {


        this.contactInfoForSaving = contactInfo;


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {


            saveContact(contactInfo);

        } else {

            requestWriteContactsPermission();
        }

    }

       /*
    *
    * To request access gallery permission to select audio
    * */

    private void requestLocationPermission() {


        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            Snackbar snackbar = Snackbar.make(root, R.string.AllowLocation,
                    Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.Ok), new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ActivityCompat.requestPermissions(ChatMessageScreen.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            22);
                }
            });


            snackbar.show();


            View view = snackbar.getView();
            TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            txtv.setGravity(Gravity.CENTER_HORIZONTAL);

        } else

        {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    22);
        }
    }



       /*
    *
    * To save the contact details
    * */

    private void requestCameraPermissionImage() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {

            Snackbar snackbar = Snackbar.make(root, R.string.AccessCamera,
                    Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.Ok), new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ActivityCompat.requestPermissions(ChatMessageScreen.this, new String[]{Manifest.permission.CAMERA},
                            24);
                }
            });


            snackbar.show();


            View view = snackbar.getView();
            TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            txtv.setGravity(Gravity.CENTER_HORIZONTAL);


        } else {


            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    24);
        }
    }
     /*
     * Utility methods
     */

    private void requestWriteContactsPermission() {


        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_CONTACTS)) {


            Snackbar snackbar = Snackbar.make(root, R.string.ContactAccess,
                    Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.Ok), new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ActivityCompat.requestPermissions(ChatMessageScreen.this, new String[]{Manifest.permission.WRITE_CONTACTS},
                            29);
                }
            });


            snackbar.show();


            View view = snackbar.getView();
            TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);

            txtv.setMaxLines(3);


            txtv.setGravity(Gravity.CENTER_HORIZONTAL);


        } else {


            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CONTACTS},
                    29);
        }

    }

    private void requestReadImagePermission(int k) {
        if (k == 1) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                Snackbar snackbar = Snackbar.make(root, R.string.GalleryAccess,
                        Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.Ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ActivityCompat.requestPermissions(ChatMessageScreen.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                26);
                    }
                });
                snackbar.show();
                View view = snackbar.getView();
                TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                txtv.setGravity(Gravity.CENTER_HORIZONTAL);

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        26);
            }
        } else if (k == 0)
        {
            /*
             * For capturing the image permission
             */
            if (ActivityCompat.shouldShowRequestPermissionRationale(ChatMessageScreen.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(ChatMessageScreen.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                Snackbar snackbar = Snackbar.make(root, R.string.CaptureImagePermission,
                        Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.Ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ActivityCompat.requestPermissions(ChatMessageScreen.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                37);
                    }
                });
                snackbar.show();
                View view = snackbar.getView();
                TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                txtv.setGravity(Gravity.CENTER_HORIZONTAL);
            } else {
                ActivityCompat.requestPermissions(ChatMessageScreen.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        37);
            }
        } else if (k == 2)
        {
            /*
             * For capturing the image permission
             */
            if (ActivityCompat.shouldShowRequestPermissionRationale(ChatMessageScreen.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(ChatMessageScreen.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                Snackbar snackbar = Snackbar.make(root, R.string.DoodleAccess,
                        Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.Ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ActivityCompat.requestPermissions(ChatMessageScreen.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                47);
                    }
                });

                snackbar.show();
                View view = snackbar.getView();
                TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                txtv.setGravity(Gravity.CENTER_HORIZONTAL);
            } else {
                ActivityCompat.requestPermissions(ChatMessageScreen.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        47);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void saveContact(String contactInfo)
    {
        String contactName = "", contactNumber;
        try {
            String parts[] = contactInfo.split("@@");
            contactName = parts[0];
            String arr[] = parts[1].split("/");
            contactNumber = arr[0];
            if (contactName == null || contactName.isEmpty()) {
                contactName = getString(R.string.Unknown);
            } else if (contactNumber == null || contactNumber.isEmpty()) {
                contactNumber = getString(R.string.NoNumber);
            }

        } catch (StringIndexOutOfBoundsException e) {
            contactNumber = getString(R.string.NoNumber);
        }
        Intent intentInsertEdit = new Intent(Intent.ACTION_INSERT_OR_EDIT);
        intentInsertEdit.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
        intentInsertEdit.putExtra(ContactsContract.Intents.Insert.PHONE, contactNumber);
        intentInsertEdit.putExtra(ContactsContract.Intents.Insert.NAME, contactName);
        intentInsertEdit.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        intentInsertEdit.putExtra("finishActivityOnSaveCompleted", true);
        startActivity(intentInsertEdit, ActivityOptionsCompat.makeSceneTransitionAnimation(ChatMessageScreen.this).toBundle());
    }

    @SuppressWarnings("all")
    private void createDoodleUri(byte[] data) {
        String name = Utilities.tsInGmt();
        name = new Utilities().gmtToEpoch(name);
        File folder = new File(Environment.getExternalStorageDirectory().getPath() + ApiOnServer.CHAT_DOODLES_FOLDER);
        if (!folder.exists() && !folder.isDirectory()) {
            folder.mkdirs();
        }
        File file = new File(Environment.getExternalStorageDirectory().getPath() + ApiOnServer.CHAT_DOODLES_FOLDER, name + ".jpg");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.picturePath = file.getAbsolutePath();
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        name = null;
        folder = null;
    }

    /*
     * To show the popup for the secret chat
     */

    public void deleteParticularMessage(String docId, final int position, String messageId)
    {
        if (mChatData != null)
        {
            try {
                int mPosition = position;
                for (int i = 0; i < mChatData.size(); i++) {
                    if (mChatData.get(i).getMessageId().equals(messageId)) {
                        mPosition = i;
                        break;
                    }
                }
                mChatData.remove(mPosition);
                final int finalMPosition = mPosition;
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run() {
                        if (finalMPosition == 0) {
                            mAdapter.notifyDataSetChanged();
                        } else {
                            mAdapter.notifyItemRemoved(finalMPosition);
                        }
                    }
                });
                mAdapter.notifyItemChanged(mPosition);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        new removeMessage().execute(docId, messageId);
    }

    private class ChatMessageTouchHelper extends ItemTouchHelper.Callback
    {
        ChatMessageTouchHelper() {}
        @Override
        public boolean isLongPressDragEnabled() {
            return false;
        }
        @Override
        public boolean isItemViewSwipeEnabled() {
            return true;
        }
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            return makeMovementFlags(dragFlags, swipeFlags);
        }


        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                              RecyclerView.ViewHolder target) {

            return false;
        }

        @SuppressWarnings("TryWithIdenticalCatches")
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction)
        {
            final int position = viewHolder.getAdapterPosition();
            try {
                android.support.v7.app.AlertDialog.Builder builder =
                        new android.support.v7.app.AlertDialog.Builder(ChatMessageScreen.this, 0);
                builder.setTitle(R.string.DeleteCon);
                builder.setMessage(getString(R.string.DeleteMessage));
                builder.setPositiveButton(R.string.ContinueCapital, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String type = getString(R.string.Message);
                        if (mChatData.get(position).getMessageType().equals("1"))
                        {
                            type = getString(R.string.ImageCamelCase);
                        } else if (mChatData.get(position).getMessageType().equals("2")) {

                            type = getString(R.string.VideoCamelCase);

                        } else if (mChatData.get(position).getMessageType().equals("3")) {
                            type = getString(R.string.LocationCamelCase);
                        } else if (mChatData.get(position).getMessageType().equals("4")) {

                            type = getString(R.string.ContactCamelCase);

                        } else if (mChatData.get(position).getMessageType().equals("5")) {
                            type = getString(R.string.AudioCamelCase);

                        } else if (mChatData.get(position).getMessageType().equals("6")) {
                            type = getString(R.string.Sticker);

                        } else if (mChatData.get(position).getMessageType().equals("7")) {
                            type = getString(R.string.Doodle);

                        } else if (mChatData.get(position).getMessageType().equals("8")) {
                            type = getString(R.string.Gif);

                        }else if (mChatData.get(position).getMessageType().equals("16")) {
                            type =getString(R.string.pay_pal_shared);

                        }
                        AppController.getInstance().getDbController().deleteParticularChatMessage(documentId, mChatData.get(position).getMessageId());
                        mChatData.remove(position);
                        final String str = type;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (position == 0) {
                                    mAdapter.notifyDataSetChanged();
                                } else {
                                    mAdapter.notifyItemRemoved(position);
                                }
                                Snackbar snackbar = Snackbar.make(root, str + " " + getString(R.string.DeleteSuccess), Snackbar.LENGTH_SHORT);
                                snackbar.show();
                                View view = snackbar.getView();
                                TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                                txtv.setGravity(Gravity.CENTER_HORIZONTAL);

                            }
                        });

                        Context context = ((ContextWrapper) ((Dialog) dialog).getContext()).getBaseContext();
                        if (context instanceof Activity)
                        {
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
                builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                                mAdapter.notifyItemChanged(position);


                            }
                        });
                        dialog.cancel();

                    }
                });
                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


//                                mAdapter.notifyItemChanged(position);
                                mAdapter.notifyDataSetChanged();


                            }
                        });
                        dialog.cancel();

                    }
                });
                builder.show();


            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }


        }


    }


    private class removeMessage extends AsyncTask<String, Void, Void>
    {
        @Override
        protected Void doInBackground(String... params)
        {
            try {
                AppController.getInstance().getDbController().deleteParticularChatMessage(params[0], params[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        setUpActivity(intent);
    }
    /*
     *Extracting the required data */
    private void setUpActivity(Intent intent)
    {
        final Bundle bundle = intent.getExtras();
        if (bundle != null) {
            receiverImage = bundle.getString("receiverImage");
            receiverIdentifier = bundle.getString("receiverIdentifier");
            receiverUid = bundle.getString("receiverUid");
            receiverName = bundle.getString("receiverName");
            documentId = bundle.getString("documentId");
            if(bundle.containsKey("productId"))
            {
                secretId=bundle.getString("productId");
            }
            isFromOfferPage = bundle.containsKey("isFromOfferPage") && bundle.getBoolean("isFromOfferPage");
            isNewChat = bundle.containsKey("isChatNotExist") && bundle.getBoolean("isChatNotExist");
            AppController.getInstance().setActiveReceiverId(receiverUid);
        }
        TextView inviteTv = (TextView) findViewById(R.id.TV_invite);
        db = AppController.getInstance().getDbController();
        Map<String, Object> chat_item = null;
        boolean isAleretRequired=false;
        if(!isNewChat)
        {
            chat_item = db.getParticularChatInfo(documentId);
            secretId = (String) chat_item.get("secretId");
            producImage= (String) chat_item.get("productImage");
            productName= (String) chat_item.get("productName");
            productPrice= (String) chat_item.get("productPrice");
            isSeller=(boolean) chat_item.get("wasInvited");
            isSold=(boolean)chat_item.get("isSold");
            isAccepted=(boolean)chat_item.get("isAccepted");
            productSelPrice=(String)chat_item.get("productSelPrice");
            isNegotiable=(String)chat_item.get("isNegotiable");
            currencySysmbol=(String)chat_item.get("currencySymbol");
            if(chat_item.containsKey("showSafety"))
            {
                isAleretRequired=(boolean)chat_item.get("showSafety");
            }
        /*
        * Updating the safty shown for this chat.*/
            if(isAleretRequired)
            {
                db.updateSafetyShown(documentId,false);
            }
        }
        if(isSold)
        {
            productNotExist();
        }else
        {
            updateProductUI();
        }
        handelDetails(true,isSold);
        updateProductDetails();
        AppController.getInstance().setActiveSecretId(secretId);
        mAdapter = new ChatMessageAdapter(ChatMessageScreen.this,mChatData, root,secretId);
        recyclerView_chat.setAdapter(mAdapter);
        /*
         * Intentionally checked for this condition more than once
         */
        if (bundle != null && bundle.containsKey("secretChatInitiated"))
        {
            addMessageToSendInUi(setMessageToSend(false, 0, null, null), false, 0, null, true);
        }

        if (chat_item!=null&&(boolean) chat_item.get("secretInviteVisibility"))
        {
            if ((boolean) chat_item.get("wasInvited")) {
            /*
             * I was invited to join the secret chat by the receiver
             */
                inviteTv.setText(getResources().getString(R.string.youAreInvited) + " " + receiverName + " " + getResources().getString(R.string.JoinSecretChat));
            } else {
                /*
             * I invited receiver to join the secret chat
             */
                inviteTv.setText(getResources().getString(R.string.YouInvited) + " " + receiverName + " " + getResources().getString(R.string.JoinSecretChat));
            }
        } else {
            inviteVisible = false;
        }

         /*
         * For the last seen time/online status on the top
         */
        if (receiverImage != null && !receiverImage.isEmpty())
        {
            Glide.with(ChatMessageScreen.this).load(receiverImage).asBitmap()
                    .centerCrop().placeholder(R.drawable.chat_attachment_profile_default_image_frame).
                    into(new BitmapImageViewTarget(pic) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            pic.setImageDrawable(circularBitmapDrawable);
                        }
                    });

        } else {

            if(receiverName.length()<1)
            {
                return;
            }
            float density = getResources().getDisplayMetrics().density;
            try {

                assert bundle != null;
                pic.setImageDrawable(TextDrawable.builder()
                        .beginConfig()
                        .textColor(Color.WHITE)
                        .useFont(Typeface.DEFAULT)
                        .fontSize((int) (20 * density)) /* size in px */
                        .bold()
                        .toUpperCase()
                        .endConfig()
                        .buildRound((receiverName.trim()).charAt(0) + "", Color.parseColor(bundle.getString("colorCode"))));
            } catch (NullPointerException e) {
                pic.setImageDrawable(TextDrawable.builder()
                        .beginConfig()
                        .textColor(Color.WHITE)
                        .useFont(Typeface.DEFAULT)
                        .fontSize((int) (14 * density)) /* size in px */
                        .bold()
                        .toUpperCase()
                        .endConfig()
                        .buildRound((receiverName.trim()).charAt(0) + "", ContextCompat.getColor(ChatMessageScreen.this, R.color.color_profile)));
            }


        }
        header_rl.setVisibility(View.GONE);
        header_center_rl.setVisibility(View.VISIBLE);
        header_receiverName.setText(receiverName);
        receiverNameHeader.setText(receiverName);
        allowLastSeen = AppController.getInstance().

                getSharedPreferences().

                getBoolean("enableLastSeen", true);


           /*
         * For the last seen time/online status on the top
         */
        top = "";
        opponentOnline = false;
        AppController.getInstance().subscribeToTopic(MqttEvents.OnlineStatus.value+"/"+receiverUid, 0);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mChatData.clear();
                mAdapter.notifyDataSetChanged();
            }
        });
        db = AppController.getInstance().getDbController();
        if(!isNewChat)
        {
            if (bundle != null && !bundle.containsKey("secretChatInitiated"))
            {
                loadFromDbFirstTen();
                Map<String, Object> chatInfo = db.getChatInfo(documentId);
                chatId = (String) chatInfo.get("chatId");
                canHaveMoreMessages = (boolean) chatInfo.get("canHaveMoreMessages");
                if (chatId.isEmpty())
                {
                    hasChatId = false;
                } else {
                    hasChatId = true;
                    if (canHaveMoreMessages)
                    {
                        if (mChatData.size() == 0)
                        {
                            retrieveChatMessage(MESSAGE_PAGE_SIZE);
                        }
                    }
                }
            }
            try
            {
                db.updateChatListOnViewingMessage(documentId);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        limit = false;
        firstTenLoaded = true;
        if(isAleretRequired)
        {
            aleretDialog.showSefty(this);
        }
    }
    @SuppressWarnings("TryWithIdenticalCatches")
    @Subscribe
    public void getMessage(JSONObject object)
    {
        Log.d("log13",""+object.toString());
        try {
            float density = getResources().getDisplayMetrics().density;
            if (object.getString("eventName").equals(MqttEvents.UserUpdates.value + "/" + AppController.getInstance().getUserId()))
            {
                receiverImage = object.getString("profilePic");
                switch (object.getInt("type"))
                {
                    case 2:
                        /*
                        * Profile update*/
                        if (receiverImage != null && !receiverImage.isEmpty())
                        {
                            Glide.with(ChatMessageScreen.this).load(receiverImage).asBitmap()
                                    .centerCrop().placeholder(R.drawable.chat_attachment_profile_default_image_frame).
                                    into(new BitmapImageViewTarget(pic) {
                                        @Override
                                        protected void setResource(Bitmap resource) {
                                            RoundedBitmapDrawable circularBitmapDrawable =
                                                    RoundedBitmapDrawableFactory.create(getResources(), resource);
                                            circularBitmapDrawable.setCircular(true);
                                            pic.setImageDrawable(circularBitmapDrawable);
                                        }
                                    });

                        } else {
                            pic.setImageDrawable(TextDrawable.builder()
                                    .beginConfig()
                                    .textColor(Color.WHITE)
                                    .useFont(Typeface.DEFAULT)
                                    .fontSize((int) ((20) * density)) /* size in px */
                                    .bold()
                                    .toUpperCase()
                                    .endConfig()
                                    .buildRound((receiverName.trim()).charAt(0) + "", R.color.color_profile));
                        }
                        break;


                }
            } else if (object.getString("eventName").equals(MqttEvents.Message.value + "/" + userId)||object.getString("eventName").equals(MqttEvents.OfferMessage.value + "/" + userId))
            {
                /*
                 *Message details update */
                Log.d("log47",""+object.toString());
                try {
                    String docIdForDoubleTickAck = object.getString("toDocId");
                    String sender = object.getString("from");
                    String secretId;
                    if (object.has("secretId"))
                    {
                        secretId = object.getString("secretId");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                inviteVisible = false;
                            }
                        });
                        String messageType = object.getString("type");
                        String offerType ="1";
                        if(object.has("offerType"))
                        {
                            offerType=object.getString("offerType");
                        }
                        if(offerType.equals("2"))
                        {
                            isAccepted=true;
                        }
                        boolean isFromself=false;
                        if(sender.equals(AppController.getInstance().getUserId())&&messageType.equals("15"))
                        {
                            sender=object.getString("to");
                            isFromself=true;
                        }
                        String docId = AppController.getInstance().findDocumentIdOfReceiver(sender, secretId);
                        if (docId.equals(documentId)) {

                            String id = object.getString("id");

                            String message = object.getString("payload");


                            if (AppController.getInstance().isForeground())
                            {
                                if (!messageType.equals("0")||!messageType.equals("16") || !message.trim().isEmpty()) {

                           /*
                             * Need to publish to the topic of the receiver uid
                             */
                                    JSONObject obj = new JSONObject();
                                    obj.put("from", userId);
                                    obj.put("msgIds", new JSONArray(Arrays.asList(new String[]{id})));
                                    obj.put("doc_id", docIdForDoubleTickAck);
                                    obj.put("to", sender);
                                    obj.put("secretId", secretId);
                                    obj.put("status", "3");
                                    AppController.getInstance().publish(MqttEvents.Acknowledgement.value + "/" + receiverUid, obj, 2, false);
                                }
                                CouchDbController db = AppController.getInstance().getDbController();
                                db.updateChatListOnViewingMessage(documentId);
                            } else {
                                hasPendingAcknowledgement = true;
                            }
                            String tsFromServer = object.getString("timestamp");
                            int dataSize = -1;
                            if (messageType.equals("1") || messageType.equals("2") || messageType.equals("5") || messageType.equals("7")) {
                                dataSize = object.getInt("dataSize");
                            }
                            loadMessageInChatUI(sender, id, messageType,offerType, message, tsFromServer, dataSize,isFromself);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (object.getString("eventName").equals(MqttEvents.Connect.value))
            {
                AppController.getInstance().subscribeToTopic(MqttEvents.OnlineStatus.value+"/"+receiverUid,0);
                AppController.getInstance().updatePresence(1, false);
            } else if (object.getString("eventName").equals(MqttEvents.Disconnect.value))
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run() {

                        top = getString(R.string.Connecting);
                        if (tv != null)
                            tv.setText(top);
                    }
                });

                    /*
                     *Incase mine internet goes off,then also i don't emit on typing event
                     */

                opponentOnline = false;
            } else if (object.getString("eventName").substring(0, 3).equals("Onl"))
            {
                try
                {
                    if (object.getString("userId").equals(receiverUid))
                    {
                        updateLastSeenInActionBar(object);
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
                /*
                 *updating to the sender that i am online*/
                if(AppController.getInstance().isForeground())
                {
                    AppController.getInstance().updatePresence(1, false);
                }
            } else if (object.getString("eventName").equals(MqttEvents.MessageResponse.value))
            {
                try {
                    String docId = object.getString("docId");
                    if (docId.equals(documentId)) {
                        drawSingleTick(object.getString("messageId"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (object.getString("eventName").equals(MqttEvents.Typing.value+"/"+userId))
            {
                try {
                    String from=object.getString("from");
                    String to=object.getString("to");
                    String secretTemp=object.getString("secretId");
                    if(from.equals(receiverUid)&&to.equals(userId)&&secretId.equals(secretTemp))
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (tv != null) {
                                    tv.setText(R.string.Typing);
                                    new Handler().postDelayed(new Runnable() {
                                        public void run() {
                                            if (tv != null) {
                                                tv.setText(top);
                                            }
                                        }
                                    }, 1000);
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (object.getString("eventName").equals(MqttEvents.Acknowledgement.value + "/" + userId))
            {


                try {
                    if (object.has("secretId") && object.getString("secretId").equals(secretId)) {


                        /*
                         * Although this check is redundant, but have put for safety
                         */
                        if (object.getString("from").equals(receiverUid))
                        {
                            drawDoubleTick(object.getString("doc_id"), object.getString("msgId"), object.getString("status"));
                        }
                    }

                    String documentIdDoubleTick = object.getString("doc_id");
                    if (documentId.equals(documentIdDoubleTick)) {


                        JSONArray arr_temp = object.getJSONArray("msgIds");
                        String id = arr_temp.getString(0);

                        String status = object.getString("status");


                        boolean flag = false;

                        for (int i = mChatData.size() - 1; i >= 0; i--) {


                            if (mChatData.get(i).isSelf() && (mChatData.get(i).getMessageId()).equals(id)) {
                                flag = true;


                                if (status.equals("2")) {


                                    mChatData.get(i).setDeliveryStatus("2");


                                    final int k = i;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {


                                            mAdapter.notifyItemChanged(k);


                                        }
                                    });


                                } else if (status.equals("3")) {


                                    if (!mChatData.get(i).getDeliveryStatus().equals("3")) {


                                        for (int j = i; j >= 0; j--) {
                                            if (mChatData.get(j).isSelf() && !mChatData.get(j).getDeliveryStatus().equals("0")) {

                                                if (mChatData.get(j).getDeliveryStatus().equals("3"))
                                                {
                                                    break;
                                                } else {
                                                    mChatData.get(j).setDeliveryStatus("3");
                                                    final int k = j;
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            mAdapter.notifyItemChanged(k);
                                                        }
                                                    });
                                                }}}}}}
                            if (flag) {
                                break;
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (object.getString("eventName").equals(MqttEvents.FetchMessages.value + "/" + userId))
            {

                if (showingLoadingItem)
                {
                    mChatData.remove(0);
                    showingLoadingItem = false;
                }
                addMessagesFetchedFromServer(object.getJSONArray("messages"));
                final int length = object.getJSONArray("messages").length();
                if ((length > 0 && length % MESSAGE_PAGE_SIZE != 0) || (length == 0)) {
                    canHaveMoreMessages = false;
                    db.saveCanHaveMoreMessage(documentId);
                }
                pendingApiCalls--;
            }else if(object.getString("eventName").equals(MqttEvents.UpdateProduct.value+"/"+userId))
            {
                String id=object.getString("id");
                if(secretId.equals(id))
                {
                    String image,name,negotiable,price_value;
                    if(object.has("name"))
                    {
                        name=object.getString("name");
                    }else
                    {
                        name=productName;
                    }
                    if(object.has("image"))
                    {
                        image=object.getString("image");
                    }else
                    {
                        image=producImage;
                    }
                    if(object.has("negotiable"))
                    {
                        negotiable=object.getString("negotiable");
                    }else
                    {
                        negotiable=isNegotiable;
                    }
                    if(object.has("price"))
                    {
                        price_value=object.getString("price");
                    }else
                    {
                        price_value=productPrice;
                    }
                    if(object.has("sold"))
                    {
                        isSold=object.getBoolean("sold");
                        handelDetails(true,isSold);
                        if(isSold)
                        {
                            productNotExist();
                        }else
                        {
                            producImage=image;
                            productName=name;
                            isNegotiable=negotiable;
                            productPrice=price_value;
                            productExist();
                            updateProductUI();
                        }
                    }else
                    {
                        producImage=image;
                        productName=name;
                        isNegotiable=negotiable;
                        productPrice=price_value;
                        productExist();
                        updateProductUI();
                    }
                }
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
    /**
     * @param pageSize to identify the number foR messages to be retrieved at a time
     */

    @SuppressWarnings("TryWithIdenticalCatches")
    private void retrieveChatMessage(int pageSize)
    {
        /*
         * As of now we just fetch the message of the 0th page b4 the timestamp of the first message
         */
        pendingApiCalls++;
        long timestamp;
        try {
            timestamp = (mChatData.get(0).getMessageDateGMTEpoch());
        } catch (Exception e) {
            e.printStackTrace();
            timestamp = Utilities.getGmtEpoch();
        }


        final ProgressDialog pDialog = new ProgressDialog(ChatMessageScreen.this, 0);
        if (mChatData.size() == 0) {
            /*
             *
             *For first time coming in
             */
            showingLoadingItem = false;
            pDialog.setCancelable(false);
            pDialog.setMessage(getString(R.string.RetrieveMessage));
            pDialog.show();
            ProgressBar bar = (ProgressBar) pDialog.findViewById(android.R.id.progress);


            bar.getIndeterminateDrawable().setColorFilter(
                    ContextCompat.getColor(ChatMessageScreen.this, R.color.color_black),
                    android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            RetrieveSecretChatMessageItem loadingItem = new RetrieveSecretChatMessageItem();
            loadingItem.setMessageType("99");
            mChatData.add(0, loadingItem);
            mAdapter.notifyDataSetChanged();
            showingLoadingItem = true;
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                ApiOnServer.FETCH_MESSAGES + "/" + chatId + "/" + timestamp + "/" + pageSize, null,
                new com.android.volley.Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d("log12", response.toString());
                        try {
                            if (response.getInt("code") != 200)
                            {
                                if (root != null) {
                                    Snackbar snackbar = Snackbar.make(root, response.getString("message"), Snackbar.LENGTH_SHORT);
                                    snackbar.show();
                                    View view = snackbar.getView();
                                    TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                                    txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                                }
                            }
                            if (pDialog.isShowing())
                            {
                                Context context = ((ContextWrapper) (pDialog).getContext()).getBaseContext();
                                if (context instanceof Activity)
                                {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                        if (!((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
                                            pDialog.dismiss();
                                        }
                                    } else
                                    {
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();


                if (pDialog.isShowing()) {


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

                    if (root != null) {


                        Snackbar snackbar = Snackbar.make(root, R.string.NoInternetConnectionAvailable, Snackbar.LENGTH_SHORT);


                        snackbar.show();
                        View view = snackbar.getView();
                        TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                        txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                    }

                } else {


                    mChatData.remove(0);


                    mAdapter.notifyDataSetChanged();

                    if (root != null) {
                        Snackbar snackbar = Snackbar.make(root, R.string.FailedMessageRetrieve, Snackbar.LENGTH_SHORT);


                        snackbar.show();
                        View view = snackbar.getView();
                        TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                        txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                    }


                }

            }
        }


        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization",ApiOnServer.AUTH_KEY);
                headers.put("token", AppController.getInstance().getApiToken());
                return headers;
            }
        };


        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
/* Add the request to the RequestQueue.*/
        AppController.getInstance().addToRequestQueue(jsonObjReq, "fetchMessagesApiRequest");


    }


    /**
     * @param messages JSONArray containing the lsit of the messages fetched from the server
     */
    @SuppressWarnings("unchecked")
    private void addMessagesFetchedFromServer(final JSONArray messages)
    {
        recyclerView_chat.setLayoutFrozen(true);
        JSONObject obj;
        RetrieveSecretChatMessageItem message;
        String messageType, ts, tsFromServer;
        byte[] data;
        long dataSize;
        for (int i = 0; i < messages.length(); i++)
        {
            try {
                obj = messages.getJSONObject(i);
                message = new RetrieveSecretChatMessageItem();


                data = Base64.decode(obj.getString("payload"), Base64.DEFAULT);

                tsFromServer = String.valueOf(obj.getLong("timestamp"));
                ts = Utilities.formatDate(Utilities.tsFromGmt(Utilities.epochtoGmt(tsFromServer)));


                message.setReceiverUid(receiverUid);


                if (obj.getString("receiverId").equals(receiverUid)) {

                    message.setIsSelf(true);
/*
 * To identify if the message has been delivered/read by the opponent
 */

                    message.setDeliveryStatus(obj.getString("status"));


                } else {


                    message.setIsSelf(false);
                }

                messageType = obj.getString("messageType");

                if ((messageType.equals("1")) || (messageType.equals("2")) || (messageType.equals("5")) || (messageType.equals("7"))) {
                    String size;
                    dataSize = obj.getLong("dataSize");

                    if (dataSize < 1024) {


                        size = dataSize + " bytes";

                    } else if (dataSize >= 1024 && dataSize <= 1048576) {


                        size = (dataSize / 1024) + " KB";

                    } else {


                        size = (dataSize / 1048576) + " MB";
                    }


                    message.setSize(size);
                }


                message.setTS(ts.substring(0, 9));


                message.setMessageDateOverlay(ts.substring(9, 24));

                message.setMessageDateGMTEpoch(Long.parseLong(tsFromServer));
                message.setMessageId(obj.getString("messageId"));


                /*
                 * By default assuming all the values being not downloaded
                 */


                switch (Integer.parseInt(messageType)) {


                    case 0:
                        message.setMessageType("0");
                        try {
                            message.setTextMessage(new String(data, "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        if (message.getTextMessage().trim().isEmpty()) {
                            continue;
                        }
                        break;
                    case 16:
                        message.setMessageType("16");
                        try {
                            message.setTextMessage(new String(data, "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        if (message.getTextMessage().trim().isEmpty()) {
                            continue;
                        }
                        break;
                    case 15:
                        message.setMessageType("15");
                        try {
                            message.setTextMessage(new String(data, "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        if (message.getTextMessage().trim().isEmpty()) {
                            continue;
                        }
                        break;
                    case 1:
                        message.setMessageType("1");
                        message.setDownloading(false);
                        message.setDownloadStatus(0);
                        message.setThumbnailPath(getFilesDir() + ApiOnServer.CHAT_RECEIVED_THUMBNAILS_FOLDER + "/" + tsFromServer + ".jpg");
                        try {
                            message.setImagePath(new String(data, "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        message.setMessageType("2");
                        message.setDownloadStatus(0);
                        message.setDownloading(false);
                        message.setThumbnailPath(getFilesDir() + ApiOnServer.CHAT_RECEIVED_THUMBNAILS_FOLDER + "/" + tsFromServer + ".jpg");
                        try {
                            message.setVideoPath(new String(data, "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 3:
                        message.setMessageType("3");
                        String placeString = "";
                        try {
                            placeString = new String(data, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        message.setPlaceInfo(placeString);
                        break;
                    case 4:
                        message.setMessageType("4");
                        String contactString = "";
                        try {
                            contactString = new String(data, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        message.setContactInfo(contactString);
                        break;
                    case 5:
                        message.setMessageType("5");
                        message.setDownloadStatus(0);
                        message.setDownloading(false);
                        try {
                            message.setAudioPath(new String(data, "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 6:
                        message.setMessageType("6");
                        try {
                            message.setStickerUrl(new String(data, "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 7:
                        message.setMessageType("7");
                        message.setDownloadStatus(0);
                        message.setThumbnailPath(getFilesDir() + ApiOnServer.CHAT_RECEIVED_THUMBNAILS_FOLDER + "/" + tsFromServer + ".jpg");
                        try {
                            message.setImagePath(new String(data, "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 8:
                        message.setMessageType("8");
                        try {
                            message.setGifUrl(new String(data, "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        break;
                }
                mChatData.add(0, message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    mAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (mChatData.size() <= MESSAGE_PAGE_SIZE)
                    {

/*
 * To scroll the item to last position incase of adding the last message
 */

                        llm.scrollToPositionWithOffset(mAdapter.getItemCount() - 1, 0);


                    } else {
   /*
         *
         * To handle the problem of the unsmooth scroll
         */

                        llm.scrollToPositionWithOffset(messages.length(), 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        recyclerView_chat.setLayoutFrozen(false);
    }


    /*
     *Opening of the product details page. */
    private void productDetailsPage()
    {
        Intent intent=new Intent(this,ProductDetailsActivity.class);
        intent.putExtra("image",producImage);
        intent.putExtra("postId",secretId);
        intent.putExtra("price",productPrice);
        intent.putExtra("productName",productName);
        intent.putExtra("fromChatScreen","1");
        if (isSeller)
            intent.putExtra("postedByUserName",sessionManager.getUserName());
        startActivity(intent);
    }
    /*
     * Updatign the product details*/
    public void UpdateSellPrice()
    {
        Map<String, Object> chat_item = db.getParticularChatInfo(documentId);
        if(isAccepted)
        {
            productSelPrice=(String)chat_item.get("productSelPrice");
        }
    }
    /*
     *Updating the product details. */
    private void updateProductDetails()
    {
        handelDetails(false,false);
        if (CommonClass.isNetworkAvailable(this))
        {
            JSONObject requestDats = new JSONObject();
            try {
                requestDats.put("token",sessionManager.getAuthToken());
                requestDats.put("postId",secretId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            OkHttp3Connection.doOkHttp3Connection("", ApiUrl.GET_POST_BY_ID, OkHttp3Connection.Request_type.POST, requestDats, new OkHttp3Connection.OkHttp3RequestCallback()
            {
                @Override
                public void onSuccess(String result, String user_tag)
                {
                    ProductDetailsMain productDetailsMain;
                    Gson gson=new Gson();
                    productDetailsMain=gson.fromJson(result,ProductDetailsMain.class);
                    switch (productDetailsMain.getCode())
                    {
                        case "200" :
                            final ProductResponseDatas productResponse=productDetailsMain.getData().get(0);
                            producImage=productResponse.getMainUrl();
                            productName=productResponse.getProductName();
                            productPrice=productResponse.getPrice();
                            isNegotiable=productResponse.getNegotiable();
                            currencySysmbol =CommonClass.getCurrencySymbol(productResponse.getCurrency());
                            if(!isSeller)
                            {
                                db.updateChatDetails(documentId,productResponse.getMembername(),productResponse.getMemberProfilePicUrl());
                            }
                            db.updateProductDetails(documentId,producImage,productName,productPrice,isNegotiable,currencySysmbol);
                            if(productResponse.getOfferAccepted()>0)
                            {
                                isAccepted=true;
                                String offerPrice=productPrice;
                                if(productResponse.getOfferPrice()>0)
                                {
                                    offerPrice=""+productResponse.getOfferPrice();
                                }
                                db.updateProductAccepted(documentId,true,offerPrice);
                                UpdateSellPrice();
                            }else
                            {
                                isAccepted=false;
                            }
                            if(productResponse.getIsSold()==0)
                            {
                                isSold=false;
                            }else
                            {
                                isSold=true;
                                db.updateSoldDetails(documentId,true);
                                productNotExist();
                            }
                            handelDetails(true,isSold);
                            updateProductUI();
                            if(mAdapter!=null)
                            {
                                mAdapter.notifyDataSetChanged();
                            }
                            break;
                        case "401" :
                            CommonClass.sessionExpired(ChatMessageScreen.this);
                            break;
                        case "204":
                            db.updateSoldDetails(documentId,true);
                            productNotExist();
                            break;
                        default:
                            showErr(getString(R.string.error_on_product_update));
                            break;
                    }
                }
                @Override
                public void onError(String error, String user_tag)
                {
                    showErr(error);
                }
            });

        }else
        {
            showErr(getString(R.string.error_on_product_update));
        }
    }
    /*
     *Showing error */
    private void showErr(String error)
    {
        if (root != null&&error!=null)
        {
            Snackbar snackbar = Snackbar.make(root,error, Snackbar.LENGTH_SHORT);
            snackbar.show();
            View view = snackbar.getView();
            TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            txtv.setGravity(Gravity.CENTER_HORIZONTAL);
        }
    }
    /*
     * Product details setup*/
    private void updateProductUI()
    {
        Glide.with(this)
                .load(producImage)
                .into(round_image_view);
        price_Text.setText(currencySysmbol+" "+productPrice);
        product_name.setText(productName);
    }

    /*
     *Updating the UI */
    private void handelDetails(boolean found,boolean isSold)
    {
        if(found)
        {
            loading_prodcut_details.setVisibility(View.GONE);
            if(isSold)
            {
                product_view.setVisibility(View.GONE);
                already_sold_tag.setVisibility(View.VISIBLE);
            }else
            {
                product_view.setVisibility(View.VISIBLE);
                already_sold_tag.setVisibility(View.GONE);
            }
        }else
        {
            already_sold_tag.setVisibility(View.GONE);
            product_view.setVisibility(View.GONE);
            loading_prodcut_details.setVisibility(View.VISIBLE);
        }
    }
    /*
     * keyboard change listener*/
    private boolean keyboardListenersAttached = false;
    private ViewGroup rootLayout;
    private void attachKeyboardListeners()
    {
        if (keyboardListenersAttached) {
            return;
        }
        rootLayout = (ViewGroup)findViewById(R.id.mainRelativeLayout);
        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);
        keyboardListenersAttached = true;
    }
    private boolean isSet=false;
    private boolean isKeypadOpen=false;
    private int normal_height;
    private ViewTreeObserver.OnGlobalLayoutListener keyboardLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        public void onGlobalLayout()
        {
            int heightDiff= rootLayout.getRootView().getHeight() - rootLayout.getHeight();
            if(!isSet)
            {
                isSet=true;
                normal_height=heightDiff;
            }
            if(heightDiff>normal_height)
            {
                isKeypadOpen=true;
            }else
            {
                isKeypadOpen=false;
                if(closingAct&&normal_height<=heightDiff)
                {
                    closeActivity();
                }
            }
        }
    };
    /*
     *product not exist */
    private void productNotExist()
    {
        isSold=true;
        isAccepted=true;
        if(mAdapter!=null)
        {
            mAdapter.notifyDataSetChanged();
        }
        product_view.setVisibility(View.GONE);
        loading_prodcut_details.setVisibility(View.GONE);
        productNotexist.setVisibility(View.VISIBLE);
        already_sold_tag.setVisibility(View.VISIBLE);
        productNotexist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatMessageScreen.this, R.string.no_product_aleret_msg,Toast.LENGTH_SHORT).show();
            }
        });
    }
    /*
    *product not exist */
    private void productExist()
    {
        isSold=false;
        loading_prodcut_details.setVisibility(View.GONE);
        already_sold_tag.setVisibility(View.GONE);
        productNotexist.setVisibility(View.GONE);
        product_view.setVisibility(View.VISIBLE);
        if(mAdapter!=null)
        {
            mAdapter.notifyDataSetChanged();
        }
    }
}
