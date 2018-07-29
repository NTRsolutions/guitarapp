package com.demskigroup.guitaramps.mqttchat.Adapters;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.mqttchat.Activities.ChatMessageScreen;
import com.demskigroup.guitaramps.mqttchat.Activities.CounterPricePage;
import com.demskigroup.guitaramps.mqttchat.Activities.MediaHistory_FullScreenImage;
import com.demskigroup.guitaramps.mqttchat.Activities.MediaHistory_FullScreenVideo;
import com.demskigroup.guitaramps.mqttchat.AppController;
import com.demskigroup.guitaramps.mqttchat.BlurTransformation.BlurTransformation;
import com.demskigroup.guitaramps.mqttchat.Database.CouchDbController;
import com.demskigroup.guitaramps.mqttchat.DownloadFile.FileDownloadService;
import com.demskigroup.guitaramps.mqttchat.DownloadFile.ServiceGenerator;
import com.demskigroup.guitaramps.mqttchat.Giphy.GifPlayer;
import com.demskigroup.guitaramps.mqttchat.ModelClasses.RetrieveSecretChatMessageItem;
import com.demskigroup.guitaramps.mqttchat.Utilities.ApiOnServer;
import com.demskigroup.guitaramps.mqttchat.Utilities.MqttEvents;
import com.demskigroup.guitaramps.mqttchat.Utilities.RingProgressBar;
import com.demskigroup.guitaramps.mqttchat.Utilities.Utilities;
import com.demskigroup.guitaramps.mqttchat.ViewHolders.CounterOfferReceived;
import com.demskigroup.guitaramps.mqttchat.ViewHolders.OfferAcceptSent;
import com.demskigroup.guitaramps.mqttchat.ViewHolders.OfferAcceptedView;
import com.demskigroup.guitaramps.mqttchat.ViewHolders.OfferReceived;
import com.demskigroup.guitaramps.mqttchat.ViewHolders.OfferSentView;
import com.demskigroup.guitaramps.mqttchat.ViewHolders.PaypalLinkReceived;
import com.demskigroup.guitaramps.mqttchat.ViewHolders.PaypalLinkShared;
import com.demskigroup.guitaramps.mqttchat.ViewHolders.ViewHolderAudioReceived;
import com.demskigroup.guitaramps.mqttchat.ViewHolders.ViewHolderAudioSentRetrieve;
import com.demskigroup.guitaramps.mqttchat.ViewHolders.ViewHolderContactReceived;
import com.demskigroup.guitaramps.mqttchat.ViewHolders.ViewHolderContactSent;
import com.demskigroup.guitaramps.mqttchat.ViewHolders.ViewHolderDoodleReceived;
import com.demskigroup.guitaramps.mqttchat.ViewHolders.ViewHolderDoodleSentRetrieve;
import com.demskigroup.guitaramps.mqttchat.ViewHolders.ViewHolderGifReceived;
import com.demskigroup.guitaramps.mqttchat.ViewHolders.ViewHolderGifSent;
import com.demskigroup.guitaramps.mqttchat.ViewHolders.ViewHolderImageReceived;
import com.demskigroup.guitaramps.mqttchat.ViewHolders.ViewHolderImageSentRetrieve;
import com.demskigroup.guitaramps.mqttchat.ViewHolders.ViewHolderLoading;
import com.demskigroup.guitaramps.mqttchat.ViewHolders.ViewHolderLocationReceived;
import com.demskigroup.guitaramps.mqttchat.ViewHolders.ViewHolderLocationSent;
import com.demskigroup.guitaramps.mqttchat.ViewHolders.ViewHolderMessageReceived;
import com.demskigroup.guitaramps.mqttchat.ViewHolders.ViewHolderMessageSent;
import com.demskigroup.guitaramps.mqttchat.ViewHolders.ViewHolderServerMessage;
import com.demskigroup.guitaramps.mqttchat.ViewHolders.ViewHolderStickerReceived;
import com.demskigroup.guitaramps.mqttchat.ViewHolders.ViewHolderStickerSent;
import com.demskigroup.guitaramps.mqttchat.ViewHolders.ViewHolderVideoReceived;
import com.demskigroup.guitaramps.mqttchat.ViewHolders.ViewHolderVideoSentRetrieve;
import com.demskigroup.guitaramps.utility.ApiUrl;
import com.demskigroup.guitaramps.utility.CommonClass;
import com.demskigroup.guitaramps.utility.OkHttp3Connection;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
/**
 * @since 05/08/17.
 */

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private ArrayList<RetrieveSecretChatMessageItem> mListData = new ArrayList<>();
    private static final int MESSAGERECEIVED = 0;
    private static final int MESSAGESENT = 1;
    private static final int IMAGERECEIVED = 2;
    private static final int IMAGESENT = 3;
    private static final int VIDEORECEIVED = 4;
    private static final int VIDEOSENT = 5;
    private static final int LOCATIONRECEIVED = 6;
    private static final int LOCATIONSENT = 7;
    private static final int CONTACTRECEIVED = 8;
    private static final int CONTACTSENT = 9;
    private static final int AUDIORECEIVED = 10;
    private static final int AUDIOSENT = 11;
    /**
     * For non standard sup like sharing
     */
    private static final int STICKERSRECEIVED = 12;
    private static final int STICKERSSENT = 13;
    private static final int SERVERMESSAGE = 14;
    private static final int DOODLERECEIVED = 15;
    private static final int DOODLESENT = 16;
    private static final int GIFRECEIVED = 17;
    private static final int GIFSENT = 18;
    private final int LOADING = 19;
    /**
     * Added for yelo. self*/
    private final int OFFERSENT=24;
    private final int COUNTEROFFERSENT=25;
    private final int OFFERACCEPTEDSENT=26;
    private final int OFFERRECEIVED=27;
    private final int COUNTEROFFERRECEIVED=28;
    private final int OFFERACCEPTEDRECEIVED=29;
    private final int PAYPALLINKRECEIVED=30;
    private final int PAYPALLINKSHARED=31;
    private static final int SECTOMILLSEC = 1000;
    private Activity mContext;
    private MediaPlayer mediaPlayer;
    private long fileSizeDownloaded;
    private CoordinatorLayout root;
    private int density;
    private HashMap<String, Object> map = new HashMap<>();
    private Bitmap thumbnail;
    private String secretId;


    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {


        if (holder.getItemViewType() == LOCATIONRECEIVED) {
            if (((ViewHolderLocationReceived) holder).mMap != null) {
                ((ViewHolderLocationReceived) holder).mMap.clear();
                ((ViewHolderLocationReceived) holder).mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
            }
        } else if (holder.getItemViewType() == LOCATIONSENT) {
            if (((ViewHolderLocationSent) holder).mMap != null) {
                ((ViewHolderLocationSent) holder).mMap.clear();
                ((ViewHolderLocationSent) holder).mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
            }
        }
    }
    private ChatMessageScreen chatMessageScreen;
    public ChatMessageAdapter(Activity mContext, ArrayList<RetrieveSecretChatMessageItem> mListData, CoordinatorLayout root, String secretId)
    {
        this.mListData = mListData;
        this.mContext = mContext;
        this.chatMessageScreen= (ChatMessageScreen) this.mContext;
        this.root = root;
        this.secretId = secretId;
        density = (int) mContext.getResources().getDisplayMetrics().density;
    }


    @Override
    public int getItemCount() {
        return this.mListData.size();
    }


    @Override
    public int getItemViewType(int position)
    {
        RetrieveSecretChatMessageItem data=mListData.get(position);
        if(data==null)
        {
            return 0;
        }
        String type = data.getMessageType();
        if(type==null)
            type="0";
        /*
         * For showing of the loading more item
         */
        if (type.equals("99")) {
            return LOADING;
        }

        if (mListData.get(position).isSelf()) {

            switch (type) {
                case "0":
                    return MESSAGESENT;
                case "1":
                    return IMAGESENT;
                case "2":
                    return VIDEOSENT;
                case "3":
                    return LOCATIONSENT;
                case "4":
                    return CONTACTSENT;
                case "5":
                    return AUDIOSENT;
                case "6":
                    return STICKERSSENT;
                case "7":
                    return DOODLESENT;
                case "16":
                    return PAYPALLINKSHARED;
                case "15":
                    if (data.getOfferType() != null) {
                        switch (data.getOfferType()) {
                            case "1":
                                return OFFERSENT;
                            case "3":
                                return OFFERSENT;
                            case "2":
                                return OFFERACCEPTEDSENT;
                            default:
                                return OFFERSENT;
                        }
                    } else {
                        return OFFERSENT;
                    }
                default:
                    return GIFSENT;
            }
        } else {
            switch (type) {
                case "0":
                    return MESSAGERECEIVED;
                case "1":
                    return IMAGERECEIVED;
                case "2":
                    return VIDEORECEIVED;
                case "3":
                    return LOCATIONRECEIVED;
                case "4":
                    return CONTACTRECEIVED;
                case "5":
                    return AUDIORECEIVED;
                case "6":
                    return STICKERSRECEIVED;
                case "7":
                    return DOODLERECEIVED;
                case "16":
                    return PAYPALLINKRECEIVED;
                case "15":
                    if (data.getOfferType() != null) {
                        switch (data.getOfferType()) {
                            case "1":
                                if (chatMessageScreen.isSold||chatMessageScreen.isAccepted) {
                                    return OFFERRECEIVED;
                                }
                                return COUNTEROFFERRECEIVED;
                            case "3":
                                if (chatMessageScreen.isSold||chatMessageScreen.isAccepted) {
                                    return OFFERRECEIVED;
                                }
                                return COUNTEROFFERRECEIVED;
                            case "2":
                                return OFFERACCEPTEDRECEIVED;
                            default:
                                if (chatMessageScreen.isSold||chatMessageScreen.isAccepted) {
                                    return OFFERRECEIVED;
                                }
                                return COUNTEROFFERRECEIVED;
                        }
                    } else {
                        if (chatMessageScreen.isSold||chatMessageScreen.isAccepted) {
                            return OFFERRECEIVED;
                        }
                        return COUNTEROFFERRECEIVED;
                    }
                default:
                    return GIFRECEIVED;
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        View v1;
        switch (viewType) {
            case MESSAGERECEIVED:
                v1 = inflater.inflate(R.layout.message_received, viewGroup, false);
                viewHolder = new ViewHolderMessageReceived(v1);
                break;

            case IMAGERECEIVED:
                v1 = inflater.inflate(R.layout.image_received, viewGroup, false);
                viewHolder = new ViewHolderImageReceived(v1);
                break;

            case VIDEORECEIVED:
                v1 = inflater.inflate(R.layout.video_received, viewGroup, false);
                viewHolder = new ViewHolderVideoReceived(v1);
                break;

            case LOCATIONRECEIVED:
                v1 = inflater.inflate(R.layout.location_received, viewGroup, false);
                viewHolder = new ViewHolderLocationReceived(v1);
                break;

            case CONTACTRECEIVED:
                v1 = inflater.inflate(R.layout.contact_received, viewGroup, false);
                viewHolder = new ViewHolderContactReceived(v1);
                break;

            case AUDIORECEIVED:
                v1 = inflater.inflate(R.layout.audio_received, viewGroup, false);
                viewHolder = new ViewHolderAudioReceived(v1);
                break;


            case STICKERSRECEIVED:
                v1 = inflater.inflate(R.layout.sticker_received, viewGroup, false);
                viewHolder = new ViewHolderStickerReceived(v1);
                break;


            case DOODLERECEIVED:
                v1 = inflater.inflate(R.layout.doodle_received, viewGroup, false);
                viewHolder = new ViewHolderDoodleReceived(v1);
                break;

            case GIFRECEIVED:
                v1 = inflater.inflate(R.layout.gif_received, viewGroup, false);
                viewHolder = new ViewHolderGifReceived(v1);
                break;
            case MESSAGESENT:
                v1 = inflater.inflate(R.layout.message_sent, viewGroup, false);
                viewHolder = new ViewHolderMessageSent(v1);
                break;

            case IMAGESENT:
                v1 = inflater.inflate(R.layout.image_sent, viewGroup, false);
                viewHolder = new ViewHolderImageSentRetrieve(v1);
                break;

            case VIDEOSENT:
                v1 = inflater.inflate(R.layout.video_sent, viewGroup, false);
                viewHolder = new ViewHolderVideoSentRetrieve(v1);
                break;

            case LOCATIONSENT:
                v1 = inflater.inflate(R.layout.location_sent, viewGroup, false);
                viewHolder = new ViewHolderLocationSent(v1);
                break;


            case CONTACTSENT:
                v1 = inflater.inflate(R.layout.contact_sent, viewGroup, false);
                viewHolder = new ViewHolderContactSent(v1);
                break;
            case AUDIOSENT:
                v1 = inflater.inflate(R.layout.audio_sent, viewGroup, false);
                viewHolder = new ViewHolderAudioSentRetrieve(v1);
                break;
            case STICKERSSENT:
                v1 = inflater.inflate(R.layout.sticker_sent, viewGroup, false);
                viewHolder = new ViewHolderStickerSent(v1);
                break;

            case DOODLESENT:
                v1 = inflater.inflate(R.layout.doodle_sent, viewGroup, false);
                viewHolder = new ViewHolderDoodleSentRetrieve(v1);
                break;

            case GIFSENT:
                v1 = inflater.inflate(R.layout.gif_sent, viewGroup, false);
                viewHolder = new ViewHolderGifSent(v1);
                break;

            case LOADING:
                v1 = inflater.inflate(R.layout.loading_item, viewGroup, false);
                viewHolder = new ViewHolderLoading(v1);
                break;
            case OFFERSENT:
                v1 = inflater.inflate(R.layout.chat_self_offer_sent,viewGroup, false);
                viewHolder = new OfferSentView(v1);
                break;
            case  OFFERACCEPTEDSENT:
                v1 = inflater.inflate(R.layout.offeacept_view,viewGroup, false);
                viewHolder=new OfferAcceptSent(v1);
                break;
            case PAYPALLINKSHARED:
                v1 = inflater.inflate(R.layout.pay_pal_sent_layout,viewGroup, false);
                viewHolder=new PaypalLinkShared(v1);
                break;
            case OFFERRECEIVED:
                v1 = inflater.inflate(R.layout.offer_received,viewGroup, false);
                viewHolder = new OfferReceived(v1);
                break;
            case COUNTEROFFERRECEIVED:
                v1 = inflater.inflate(R.layout.counter_offer_received,viewGroup, false);
                viewHolder = new CounterOfferReceived(v1);
                break;
            case OFFERACCEPTEDRECEIVED:
                v1 = inflater.inflate(R.layout.counter_offer_accepted,viewGroup, false);
                viewHolder = new OfferAcceptedView(v1);
                 break;
            case PAYPALLINKRECEIVED:
                v1 = inflater.inflate(R.layout.paypal_linked_received,viewGroup, false);
                viewHolder = new PaypalLinkReceived(v1);
                break;
            default:
                v1 = inflater.inflate(R.layout.tag, viewGroup, false);
                viewHolder = new ViewHolderServerMessage(v1);
                break;
        }
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {


        switch (viewHolder.getItemViewType())
        {
            case MESSAGERECEIVED:
                ViewHolderMessageReceived vh2 = (ViewHolderMessageReceived) viewHolder;
                configureViewHolderMessageReceived(vh2, position);
                break;
            case IMAGERECEIVED:
                ViewHolderImageReceived vh3 = (ViewHolderImageReceived) viewHolder;
                configureViewHolderImageReceived(vh3, position);
                break;

            case VIDEORECEIVED:

                ViewHolderVideoReceived vh4 = (ViewHolderVideoReceived) viewHolder;

                configureViewHolderVideoReceived(vh4, position);
                break;

            case LOCATIONRECEIVED:

                ViewHolderLocationReceived vh5 = (ViewHolderLocationReceived) viewHolder;

                configureViewHolderLocationReceived(vh5, position);
                break;

            case CONTACTRECEIVED:

                ViewHolderContactReceived vh6 = (ViewHolderContactReceived) viewHolder;
                configureViewHolderContactReceived(vh6, position);
                break;

            case AUDIORECEIVED:

                ViewHolderAudioReceived vh7 = (ViewHolderAudioReceived) viewHolder;
                configureViewHolderAudioReceived(vh7, position);
                break;


            case STICKERSRECEIVED:
                ViewHolderStickerReceived vh8 = (ViewHolderStickerReceived) viewHolder;
                configureViewHolderStickerReceived(vh8, position);
                break;

            case DOODLERECEIVED:
                ViewHolderDoodleReceived vh9 = (ViewHolderDoodleReceived) viewHolder;
                configureViewHolderDoodleReceived(vh9, position);
                break;

            case GIFRECEIVED:

                ViewHolderGifReceived vh10 = (ViewHolderGifReceived) viewHolder;
                configureViewHolderGifReceived(vh10, position);
                break;

            case MESSAGESENT:


                ViewHolderMessageSent vh11 = (ViewHolderMessageSent) viewHolder;

                configureViewHolderMessageSent(vh11, position);

                break;

            case IMAGESENT:


                ViewHolderImageSentRetrieve vh12 = (ViewHolderImageSentRetrieve) viewHolder;
                configureViewHolderImageSent(vh12, position);
                break;

            case VIDEOSENT:

                ViewHolderVideoSentRetrieve vh13 = (ViewHolderVideoSentRetrieve) viewHolder;
                configureViewHolderVideoSent(vh13, position);
                break;

            case LOCATIONSENT:
                ViewHolderLocationSent vh14 = (ViewHolderLocationSent) viewHolder;
                configureViewHolderLocationSent(vh14, position);
                break;


            case CONTACTSENT:
                ViewHolderContactSent vh15 = (ViewHolderContactSent) viewHolder;
                configureViewHolderContactSent(vh15, position);
                break;


            case AUDIOSENT:
                ViewHolderAudioSentRetrieve vh16 = (ViewHolderAudioSentRetrieve) viewHolder;
                configureViewHolderAudioSent(vh16, position);
                break;

            case STICKERSSENT:


                ViewHolderStickerSent vh17 = (ViewHolderStickerSent) viewHolder;
                configureViewHolderStickersSent(vh17, position);
                break;
            case DOODLESENT:


                ViewHolderDoodleSentRetrieve vh18 = (ViewHolderDoodleSentRetrieve) viewHolder;
                configureViewHolderDoodleSent(vh18, position);
                break;

            case GIFSENT:

                ViewHolderGifSent vh19 = (ViewHolderGifSent) viewHolder;
                configureViewHolderGifSent(vh19, position);
                break;
            case LOADING:
                ViewHolderLoading vh20 = (ViewHolderLoading) viewHolder;
                configureViewHolderLoading(vh20, position);
                break;
            case OFFERSENT:
                OfferSentView vh21=(OfferSentView)viewHolder;
                configureOfferSentHolderView(vh21, position);
                break;
            case  OFFERACCEPTEDSENT:
                OfferAcceptSent vh25=(OfferAcceptSent)viewHolder;
                configureOfferAcceptedView(vh25, position);
                break;
            case OFFERRECEIVED:
                OfferReceived vh22=(OfferReceived) viewHolder;;
                configureOfferReceived(vh22, position);
                break;
            case COUNTEROFFERRECEIVED:
                CounterOfferReceived vh23=(CounterOfferReceived) viewHolder;
                configureCounterOfferReceived(vh23, position);
                break;

            case OFFERACCEPTEDRECEIVED:
                OfferAcceptedView vh24=(OfferAcceptedView) viewHolder;
                counterOfferAccepted(vh24, position);
                break;
            case PAYPALLINKRECEIVED:
                PaypalLinkReceived vh26=(PaypalLinkReceived) viewHolder;
                payPalLinkedReceived(vh26,position);
                break;
            case PAYPALLINKSHARED:
                PaypalLinkShared vh27=(PaypalLinkShared) viewHolder;
                payPalShared(vh27,position);
                break;
            default:
                ViewHolderServerMessage vh1 = (ViewHolderServerMessage) viewHolder;
                configureViewHolderServerMessage(vh1, position);
        }
    }

    /*
     *Counter msg received. */
    private void configureCounterOfferReceived(CounterOfferReceived vh23, int position)
    {
        final RetrieveSecretChatMessageItem message = mListData.get(position);
        try {
            vh23.offerPrice.setText(""+chatMessageScreen.currencySysmbol+message.getTextMessage());
            vh23.acceptOffer.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    offerAccepted(message.getTextMessage());
                }
            });
            vh23.counterOffer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    makeCounterOffer(message.getTextMessage());
                }
            });

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    /*
     *Counter msg received. */
    private void payPalShared(PaypalLinkShared vh24, int position)
    {
        final RetrieveSecretChatMessageItem message = mListData.get(position);
    }
    /*
    *paypal linked received msg received. */
    private void payPalLinkedReceived(PaypalLinkReceived vh24, int position)
    {
        final RetrieveSecretChatMessageItem message = mListData.get(position);
        try {
            vh24.offerAmount.setText(""+chatMessageScreen.currencySysmbol+chatMessageScreen.productSelPrice);
            vh24.relative_layout_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                  openPayAplLink(message.getTextMessage());
                }
            });
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /*
     *Counter msg received. */
    private void counterOfferAccepted(OfferAcceptedView vh24, int position)
    {
        final RetrieveSecretChatMessageItem message = mListData.get(position);
        try {
            vh24.text_msg.setText("Thanks for your offer of"+" "+chatMessageScreen.currencySysmbol+message.getTextMessage());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /*
     *Offer sent view holder. */
    private void configureOfferAcceptedView(OfferAcceptSent vh25, int position)
    {
        final RetrieveSecretChatMessageItem message = mListData.get(position);
        try {
            vh25.offerAmount.setText(""+chatMessageScreen.currencySysmbol+message.getTextMessage());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /*
     *Offer sent view holder. */
    private void configureOfferSentHolderView(OfferSentView vh21, int position)
    {
        final RetrieveSecretChatMessageItem message = mListData.get(position);
        try {
            vh21.offerAmount.setText(""+chatMessageScreen.currencySysmbol+message.getTextMessage());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /*
     *Offer sent view holder. */
    private void configureOfferReceived(OfferReceived vh21, int position)
    {
        final RetrieveSecretChatMessageItem message = mListData.get(position);
        try {
            vh21.offerPrice.setText(""+chatMessageScreen.currencySysmbol+message.getTextMessage());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private void configureViewHolderMessageReceived(ViewHolderMessageReceived vh2, final int position) {
        final RetrieveSecretChatMessageItem message = mListData.get(position);
        if (message != null) {
            vh2.date.setText(findOverlayDate(message.getMessageDateOverlay()) + " ");
            vh2.time.setText(convert24to12hourformat(message.getTS()) + " ");
            try {
                vh2.message.setText(message.getTextMessage());
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
    }


    @SuppressWarnings("TryWithIdenticalCatches,unchecked")

    private void configureViewHolderImageReceived(final ViewHolderImageReceived vh2, final int position) {
        final RetrieveSecretChatMessageItem message = mListData.get(position);
        if (message != null) {
            vh2.fnf.setVisibility(View.GONE);
            vh2.time.setText(convert24to12hourformat(message.getTS()) + " ");
            vh2.date.setText(findOverlayDate(message.getMessageDateOverlay()) + " ");
            vh2.cancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Call<ResponseBody> call = (Call<ResponseBody>) map.get(message.getMessageId());
                    if (call != null)
                        call.cancel();

                }
            });


            try {


                if (message.getDownloadStatus() == 1) {

/*
 *
 * image already downloaded
 *
 * */
                    vh2.progressBar2.setVisibility(View.GONE);

                    vh2.progressBar.setVisibility(View.GONE);
                    vh2.download.setVisibility(View.GONE);
                    vh2.cancel.setVisibility(View.GONE);

                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {


                        try {


                            final BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inJustDecodeBounds = true;
                            BitmapFactory.decodeFile(message.getImagePath(), options);


                            int height = options.outHeight;
                            int width = options.outWidth;


                            int reqHeight;


                            if (width == 0) {
                                reqHeight = 150;
                            } else {


                                reqHeight = ((150 * height) / width);


                                if (reqHeight > 150) {
                                    reqHeight = 150;
                                }
                            }

                            try {
                                Glide
                                        .with(mContext)
                                        .load(message.getImagePath())
                                        .override(150 * density, reqHeight * density)
                                        .crossFade()
                                        .centerCrop()
                                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                        .placeholder(R.drawable.home_grid_view_image_icon)
                                        .listener(new RequestListener<String, GlideDrawable>() {
                                            @Override
                                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                                vh2.imageView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));
                                                return false;
                                            }
                                        })
                                        .into(vh2.imageView);
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }


                            vh2.imageView.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {


                                    Intent i = new Intent(mContext, MediaHistory_FullScreenImage.class);

                                    i.putExtra("imagePath", message.getImagePath());


                                    ActivityOptionsCompat options = ActivityOptionsCompat.
                                            makeSceneTransitionAnimation((Activity) mContext, vh2.imageView, "image");
                                    mContext.startActivity(i, options.toBundle());


                                }


                            });


                        } catch (Exception e) {

                            Glide.clear(vh2.imageView);
                            vh2.imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.chat_white_circle));
                            vh2.imageView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));
                            vh2.fnf.setVisibility(View.VISIBLE);
                        }

                    } else {
                        Glide.clear(vh2.imageView);

                        vh2.imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.chat_white_circle));
                        vh2.imageView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));
                        vh2.fnf.setVisibility(View.VISIBLE);


                        vh2.fnf.setText(R.string.PermissionDenied);


                        vh2.imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                                intent.setData(uri);
                                mContext.startActivity(intent);
                            }
                        });

                    }


                } else {


                    if (message.isDownloading()) {


                        vh2.cancel.setVisibility(View.VISIBLE);


                        vh2.download.setVisibility(View.GONE);


                        vh2.progressBar2.setVisibility(View.VISIBLE);

                        vh2.progressBar.setVisibility(View.GONE);


                    } else {
                        vh2.download.setVisibility(View.VISIBLE);

                        vh2.progressBar2.setVisibility(View.GONE);
                        vh2.progressBar.setVisibility(View.GONE);
                        vh2.cancel.setVisibility(View.GONE);
                    }

                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;


                    BitmapFactory.decodeFile(message.getThumbnailPath(), options);


                    int height = options.outHeight;
                    int width = options.outWidth;


                    int reqHeight;


                    if (width == 0) {
                        reqHeight = 150;
                    } else {


                        reqHeight = ((150 * height) / width);


                        if (reqHeight > 150) {
                            reqHeight = 150;
                        }
                    }

                    try {
                        Glide
                                .with(mContext)
                                .load(message.getThumbnailPath())


                                .bitmapTransform(new CenterCrop(mContext), new BlurTransformation(mContext))


                                .override((150 * density), (density * reqHeight))
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)


                                .placeholder(R.drawable.home_grid_view_image_icon)
                                .listener(new RequestListener<String, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                        vh2.imageView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));
                                        return false;
                                    }
                                })


                                .into(vh2.imageView);

                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                    vh2.imageView.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {

                            if (!message.isDownloading()) {
                                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        == PackageManager.PERMISSION_GRANTED) {


                                    AlertDialog.Builder builder =
                                            new AlertDialog.Builder(mContext, 0);
                                    builder.setTitle(R.string.DownloadConfirmation);
                                    builder.setMessage(mContext.getString(R.string.Download) + " " + message.getSize() + " " + mContext.getString(R.string.SizeImage));
                                    builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {


                                            String receiverUid = message.getReceiverUid();

                                            String messageId = message.getMessageId();


                                            message.setDownloading(true);


                                            ((ChatMessageScreen) mContext).runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    //notifyItemChanged(viewHolder.getAdapterPosition());

                                                    notifyDataSetChanged();
                                                }
                                            });

                                            download(message.getImagePath(), message.getThumbnailPath(),
                                                    Environment.getExternalStorageDirectory().getPath() + ApiOnServer.CHAT_DOWNLOADS_FOLDER + receiverUid + messageId + ".jpg",
                                                    AppController.getInstance().findDocumentIdOfReceiver(receiverUid, secretId), message, vh2);


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
                                } else {


                                    /*
                                     *
                                     * have to request permission
                                     *
                                     * */


                                    requestStorageAccessPermission("image");

                                }
                            } else {


                                Snackbar snackbar = Snackbar.make(root, R.string.AlreadyDownloading, Snackbar.LENGTH_SHORT);


                                snackbar.show();
                                View view = snackbar.getView();
                                TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                                txtv.setGravity(Gravity.CENTER_HORIZONTAL);

                            }

                        }
                    });
                }


            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }


        }
    }


    @SuppressWarnings("TryWithIdenticalCatches,unchecked")

    private void configureViewHolderVideoReceived(final ViewHolderVideoReceived vh2, final int position) {
        final RetrieveSecretChatMessageItem message = mListData.get(position);
        if (message != null) {


            vh2.fnf.setVisibility(View.GONE);


            vh2.time.setText(convert24to12hourformat(message.getTS()) + " ");

            vh2.date.setText(findOverlayDate(message.getMessageDateOverlay()) + " ");


            vh2.cancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {


                    Call<ResponseBody> call = (Call<ResponseBody>) map.get(message.getMessageId());


                    if (call != null)
                        call.cancel();

                }
            });

            try {


                if (message.getDownloadStatus() == 1) {

                    /*
                     *
                     * video already downloaded
                     *
                     * */
                    vh2.download.setVisibility(View.GONE);
                    vh2.progressBar.setVisibility(View.GONE);
                    vh2.progressBar2.setVisibility(View.GONE);
                    vh2.cancel.setVisibility(View.GONE);

                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {


                        final File f = new File(message.getVideoPath());


                        if (f.exists()) {


                            thumbnail = ThumbnailUtils.createVideoThumbnail(message.getVideoPath(),
                                    MediaStore.Images.Thumbnails.MINI_KIND);


                            vh2.thumbnail.setImageBitmap(thumbnail);
                            vh2.thumbnail.setOnClickListener(new View.OnClickListener() {
                                                                 public void onClick(View v) {


                                                                     try {
//                                        Intent intent = new Intent();
//                                        intent.setAction(Intent.ACTION_VIEW);
//
//                                        intent.setDataAndType(Uri.fromFile(f), "video/*");
//
//                                        mContext.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext).toBundle());


                                                                         Uri intentUri;
                                                                         if (Build.VERSION.SDK_INT >= 24) {
                                                                             intentUri = Uri.parse(message.getVideoPath());
                                                                         } else {
                                                                             intentUri = Uri.fromFile(f);
                                                                         }


                                                                         Intent intent = new Intent();
                                                                         intent.setAction(Intent.ACTION_VIEW);


                                                                         intent.setDataAndType(intentUri, "video/*");

                                                                         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {


                                                                             intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);


                                                                         } else {


                                                                             List<ResolveInfo> resInfoList = mContext.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                                                                             for (ResolveInfo resolveInfo : resInfoList) {
                                                                                 String packageName = resolveInfo.activityInfo.packageName;
                                                                                 mContext.grantUriPermission(packageName, intentUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                                             }


                                                                         }


                                                                         mContext.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext).toBundle());


                                                                     } catch (ActivityNotFoundException e) {
                                                                         Intent i = new Intent(mContext, MediaHistory_FullScreenVideo.class);

                                                                         i.putExtra("videoPath", message.getVideoPath());
                                                                         mContext.startActivity(i, ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext).toBundle());


                                                                     }
                                                                 }
                                                             }

                            );

                        } else {

                            Glide.clear(vh2.thumbnail);
                            vh2.thumbnail.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.chat_white_circle));
                            vh2.thumbnail.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));


                            vh2.fnf.setVisibility(View.VISIBLE);


                        }


                    } else {
                        Glide.clear(vh2.thumbnail);
                        vh2.thumbnail.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.chat_white_circle));
                        vh2.thumbnail.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));
                        vh2.fnf.setVisibility(View.VISIBLE);


                        vh2.fnf.setText(R.string.PermissionDenied);


                        vh2.thumbnail.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                                intent.setData(uri);
                                mContext.startActivity(intent);
                            }
                        });

                    }


                } else {


                    if (message.isDownloading()) {


                        vh2.download.setVisibility(View.GONE);


                        vh2.progressBar2.setVisibility(View.VISIBLE);

                        vh2.progressBar.setVisibility(View.GONE);
                        vh2.cancel.setVisibility(View.VISIBLE);


                    } else {
                        vh2.download.setVisibility(View.VISIBLE);
                        vh2.progressBar2.setVisibility(View.GONE);
                        vh2.cancel.setVisibility(View.GONE);
                        vh2.progressBar.setVisibility(View.GONE);
                    }
                    try {
                        Glide
                                .with(mContext)
                                .load(message.getThumbnailPath())
                                .bitmapTransform(new CenterCrop(mContext), new BlurTransformation(mContext))
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)


                                .placeholder(R.drawable.home_grid_view_image_icon)


                                .listener(new RequestListener<String, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                        vh2.thumbnail.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));
                                        return false;
                                    }
                                })
                                .into(vh2.thumbnail);

                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                    vh2.thumbnail.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {


                            /* ppopup to ask if wanna download
                         *
                         *
                         * */


                            if (!message.isDownloading()) {
                                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        == PackageManager.PERMISSION_GRANTED) {


                                    AlertDialog.Builder builder =
                                            new AlertDialog.Builder(mContext, 0);
                                    builder.setTitle(R.string.DownloadConfirmation);
                                    builder.setMessage(mContext.getString(R.string.Download) + " " + message.getSize() + " " + mContext.getString(R.string.SizeVideo));
                                    builder.setPositiveButton(R.string.ContinueCapital, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {


                                            String messageId = message.getMessageId();

                                            String receiverUid = message.getReceiverUid();
                                            message.setDownloading(true);


                                            ((ChatMessageScreen) mContext).runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    //notifyItemChanged(viewHolder.getAdapterPosition());

                                                    notifyDataSetChanged();
                                                }
                                            });


                                            download(message.getVideoPath(), message.getThumbnailPath(), Environment.getExternalStorageDirectory().getPath() + ApiOnServer.CHAT_DOWNLOADS_FOLDER + receiverUid + messageId + ".mp4", AppController.getInstance().findDocumentIdOfReceiver(receiverUid, secretId), message, vh2);

                                            //    dialog.dismiss();


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


                                } else {


                                    /*
                                     *
                                     * have to request permission
                                     *
                                     * */


                                    requestStorageAccessPermission("video");


                                }
                            } else {

                                Snackbar snackbar = Snackbar.make(root, R.string.AlreadyDownloading, Snackbar.LENGTH_SHORT);


                                snackbar.show();
                                View view = snackbar.getView();
                                TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                                txtv.setGravity(Gravity.CENTER_HORIZONTAL);

                            }

                        }
                    });


                }
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }

        }


    }


    private void configureViewHolderLocationReceived(ViewHolderLocationReceived vh2, final int position) {
        final RetrieveSecretChatMessageItem message = mListData.get(position);
        if (message != null) {


            vh2.time.setText(convert24to12hourformat(message.getTS()) + " ");
            vh2.date.setText(findOverlayDate(message.getMessageDateOverlay()) + " ");


            if (vh2.mMap != null)

                vh2.mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            String args[] = message.getPlaceInfo().split("@@");

            String LatLng = args[0];

            String[] parts = LatLng.split(",");

            String lat = parts[0].substring(1);
            String lng = parts[1].substring(0, parts[1].length() - 1);
            args = null;
            parts = null;

            vh2.positionSelected = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));


        }
    }

    @SuppressWarnings("TryWithIdenticalCatches")
    private void configureViewHolderContactReceived(ViewHolderContactReceived vh2, final int position) {
        final RetrieveSecretChatMessageItem message = mListData.get(position);
        if (message != null) {


            vh2.time.setText(convert24to12hourformat(message.getTS()) + " ");
            vh2.date.setText(findOverlayDate(message.getMessageDateOverlay()) + " ");

            String contactInfo = message.getContactInfo();


            String contactName = null, contactNumber = null;


            try {

                String parts[] = contactInfo.split("@@");

                contactName = parts[0];


                String arr[] = parts[1].split("/");


                contactNumber = arr[0];
                arr = null;
                parts = null;

            } catch (StringIndexOutOfBoundsException e) {
                vh2.contactNumber.setText(R.string.NoNumber);
            } catch (Exception e) {
                vh2.contactNumber.setText(R.string.NoNumber);
            }


            try {


                vh2.contactName.setText(contactName);

                vh2.contactNumber.setText(contactNumber);


            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
            if (contactName == null || contactName.isEmpty()) {
                vh2.contactName.setText(R.string.Unknown);
            } else if (contactNumber == null || contactNumber.isEmpty()) {
                vh2.contactNumber.setText(R.string.NoNumber);
            }


        }
    }


    @SuppressWarnings("TryWithIdenticalCatches,unchecked")


    private void configureViewHolderAudioReceived(final ViewHolderAudioReceived vh2, final int position) {
        final RetrieveSecretChatMessageItem message = mListData.get(position);


        if (message != null) {

            vh2.playButton.setVisibility(View.VISIBLE);
            vh2.fnf.setVisibility(View.GONE);


            vh2.date.setText(findOverlayDate(message.getMessageDateOverlay()) + " ");


            vh2.time.setText(convert24to12hourformat(message.getTS()) + " ");


            vh2.cancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {


                    Call<ResponseBody> call = (Call<ResponseBody>) map.get(message.getMessageId());


                    if (call != null)
                        call.cancel();

                }
            });


            if (message.getDownloadStatus() == 0) {


                if (message.isDownloading()) {


                    vh2.download.setVisibility(View.GONE);


                    vh2.progressBar2.setVisibility(View.VISIBLE);

                    vh2.progressBar.setVisibility(View.GONE);
                    vh2.cancel.setVisibility(View.VISIBLE);

                } else {
                    vh2.download.setVisibility(View.VISIBLE);


                    vh2.progressBar.setVisibility(View.GONE);
                    vh2.cancel.setVisibility(View.GONE);

                    vh2.progressBar2.setVisibility(View.GONE);

                }


                vh2.playButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_GRANTED) {

                            if (!message.isDownloading()) {
                                AlertDialog.Builder builder =
                                        new AlertDialog.Builder(mContext, 0);
                                builder.setTitle(R.string.DownloadConfirmation);
                                builder.setMessage(mContext.getString(R.string.Download) + " " + message.getSize() + " " + mContext.getString(R.string.SizeAudio));
                                builder.setPositiveButton(R.string.ContinueCapital, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {


                                        String receiverUid = message.getReceiverUid();

                                        String messageId = message.getMessageId();
                                        message.setDownloading(true);


                                        ((ChatMessageScreen) mContext).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //notifyItemChanged(viewHolder.getAdapterPosition());

                                                notifyDataSetChanged();
                                            }
                                        });

                                        download(message.getAudioPath(), null, Environment.getExternalStorageDirectory().getPath() + ApiOnServer.CHAT_DOWNLOADS_FOLDER + receiverUid + messageId + ".mp3", AppController.getInstance().findDocumentIdOfReceiver(receiverUid, secretId), message, vh2);

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

                            } else {


                                Snackbar snackbar = Snackbar.make(root, R.string.AlreadyDownloading, Snackbar.LENGTH_SHORT);


                                snackbar.show();
                                View view = snackbar.getView();
                                TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                                txtv.setGravity(Gravity.CENTER_HORIZONTAL);

                            }
                        } else

                        {


                    /*
                     * have to request permission
                     *
                     * */


                            requestStorageAccessPermission("audio");
                        }
                    }
                });


            } else {
                vh2.download.setVisibility(View.GONE);


                vh2.progressBar.setVisibility(View.GONE);


                vh2.cancel.setVisibility(View.GONE);

                vh2.progressBar2.setVisibility(View.GONE);


                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {


                    final File file = new File(message.getAudioPath());


                    if (file.exists()) {


                        vh2.playButton.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {


                                try {
//                                    Intent intent = new Intent();
//                                    intent.setAction(Intent.ACTION_VIEW);
//
//                                    intent.setDataAndType(Uri.fromFile(file), "audio/*");
//                                    intent.setPackage("com.google.android.music");
//                                    mContext.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext).toBundle());


                                    Uri intentUri;
                                    if (Build.VERSION.SDK_INT >= 24) {
                                        intentUri = Uri.parse(message.getAudioPath());
                                    } else {
                                        intentUri = Uri.fromFile(file);
                                    }


                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_VIEW);

                                    intent.setDataAndType(intentUri, "audio/*");


                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {


                                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);


                                    } else {


                                        List<ResolveInfo> resInfoList = mContext.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                                        for (ResolveInfo resolveInfo : resInfoList) {
                                            String packageName = resolveInfo.activityInfo.packageName;
                                            mContext.grantUriPermission(packageName, intentUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        }


                                    }


                                    intent.setPackage("com.google.android.music");
                                    mContext.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext).toBundle());
                                } catch (ActivityNotFoundException e) {


                                    AlertDialog.Builder builder =
                                            new AlertDialog.Builder(mContext, 0);
                                    builder.setTitle(R.string.PlayConfirmation);
                                    builder.setMessage(mContext.getString(R.string.NoApp));
                                    builder.setPositiveButton(R.string.PlayHere, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {


                                            mediaPlayer = new MediaPlayer();

                                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);


                                            try {
                                                mediaPlayer.setDataSource(mContext, Uri.fromFile(file));
                                                mediaPlayer.prepare();
                                            } catch (IOException er) {
                                                er.printStackTrace();
                                            }

                                            mediaPlayer.start();

                                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {


                                                @Override

                                                public void onCompletion(MediaPlayer mp) {

                                                    // TODO Auto-generated method stub

                                                    mediaPlayer.release();

                                                    mediaPlayer = null;

                                                }

                                            });


                                            // dialog.dismiss();


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


                                }

                            }
                        });
                    } else {


                        vh2.playButton.setVisibility(View.GONE);
                        vh2.fnf.setVisibility(View.VISIBLE);

                    }
                } else {


                    vh2.playButton.setVisibility(View.GONE);
                    vh2.fnf.setVisibility(View.VISIBLE);
                    vh2.fnf.setText(R.string.PermissionDenied);


                    vh2.fnf.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                            intent.setData(uri);
                            mContext.startActivity(intent);
                        }
                    });


                }

            }


        }


    }


    private void configureViewHolderMessageSent(ViewHolderMessageSent vh2, final int position) {
        final RetrieveSecretChatMessageItem message = mListData.get(position);
        if (message != null)
        {
            vh2.date.setText(findOverlayDate(message.getMessageDateOverlay()) + " ");
            vh2.time.setText(convert24to12hourformat(message.getTS()) + " ");
            try {
                vh2.message.setText(message.getTextMessage());
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
            String status = message.getDeliveryStatus();
            if(status==null)
                status="0";

            if (status.equals("3")) {

                vh2.clock.setVisibility(View.GONE);
                vh2.singleTick.setVisibility(View.GONE);
                vh2.doubleTickGreen.setVisibility(View.GONE);
                vh2.doubleTickBlue.setVisibility(View.VISIBLE);
            } else if (status.equals("2")) {
                vh2.clock.setVisibility(View.GONE);
                vh2.singleTick.setVisibility(View.GONE);

                vh2.doubleTickGreen.setVisibility(View.VISIBLE);
                vh2.doubleTickBlue.setVisibility(View.GONE);
            } else if (status.equals("1")) {
                vh2.clock.setVisibility(View.GONE);
                vh2.singleTick.setVisibility(View.VISIBLE);
                vh2.doubleTickGreen.setVisibility(View.GONE);
                vh2.doubleTickBlue.setVisibility(View.GONE);
            } else {
                vh2.clock.setVisibility(View.VISIBLE);
                vh2.singleTick.setVisibility(View.GONE);
                vh2.doubleTickGreen.setVisibility(View.GONE);
                vh2.doubleTickBlue.setVisibility(View.GONE);


            }
        }
    }


    @SuppressWarnings("TryWithIdenticalCatches,unchecked")

    private void configureViewHolderImageSent(final ViewHolderImageSentRetrieve vh2, final int position) {
        final RetrieveSecretChatMessageItem message = mListData.get(position);
        if (message != null) {


            vh2.fnf.setVisibility(View.GONE);


            vh2.date.setText(findOverlayDate(message.getMessageDateOverlay()) + " ");

            vh2.time.setText(convert24to12hourformat(message.getTS()) + " ");

            vh2.cancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {


                    Call<ResponseBody> call = (Call<ResponseBody>) map.get(message.getMessageId());


                    if (call != null)
                        call.cancel();

                }
            });
            if (message.getDownloadStatus() == 1) {



    /*
 *
 * image already downloaded
 *
 * */
                vh2.progressBar2.setVisibility(View.GONE);

                vh2.progressBar.setVisibility(View.GONE);
                vh2.download.setVisibility(View.GONE);
                vh2.cancel.setVisibility(View.GONE);

                if (message.getImagePath() != null) {


                    try {


                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_GRANTED) {


                            final BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inJustDecodeBounds = true;
                            BitmapFactory.decodeFile(message.getImagePath(), options);


                            int height = options.outHeight;
                            int width = options.outWidth;


                            int reqHeight;


                            if (width == 0) {
                                reqHeight = 150;
                            } else {


                                reqHeight = ((150 * height) / width);


                                if (reqHeight > 150) {
                                    reqHeight = 150;
                                }
                            }

                            try {
                                Glide
                                        .with(mContext)
                                        .load(message.getImagePath())
                                        .override((150 * density), (reqHeight * density))
                                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)

                                        .centerCrop()
                                        .placeholder(R.drawable.home_grid_view_image_icon)


                                        .listener(new RequestListener<String, GlideDrawable>() {
                                            @Override
                                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                                vh2.imageView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));
                                                return false;
                                            }
                                        })
                                        .into(vh2.imageView);

                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }

                            vh2.imageView.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {


                                    Intent i = new Intent(mContext, MediaHistory_FullScreenImage.class);

                                    i.putExtra("imagePath", message.getImagePath());


                                    ActivityOptionsCompat options = ActivityOptionsCompat.
                                            makeSceneTransitionAnimation((Activity) mContext, vh2.imageView, "image");
                                    mContext.startActivity(i, options.toBundle());


                                }
                            });
                        } else {

                            vh2.fnf.setVisibility(View.VISIBLE);


                            vh2.fnf.setText(R.string.PermissionDenied);
                            Glide.clear(vh2.imageView);
                            vh2.imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.chat_white_circle));
                            vh2.imageView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));

                            vh2.imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                                    intent.setData(uri);
                                    mContext.startActivity(intent);
                                }
                            });


                        }


                    } catch (OutOfMemoryError e) {
                        e.printStackTrace();
                    } catch (Exception e) {


                        vh2.fnf.setVisibility(View.VISIBLE);
                        Glide.clear(vh2.imageView);
                        vh2.imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.chat_white_circle));
                        vh2.imageView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));


                    }
                } else {


                    vh2.imageView.setImageURI(message.getImageUrl());
                }
            } else {


                if (message.isDownloading()) {


                    vh2.cancel.setVisibility(View.VISIBLE);


                    vh2.download.setVisibility(View.GONE);


                    vh2.progressBar2.setVisibility(View.VISIBLE);

                    vh2.progressBar.setVisibility(View.GONE);


                } else {
                    vh2.download.setVisibility(View.VISIBLE);

                    vh2.progressBar2.setVisibility(View.GONE);
                    vh2.progressBar.setVisibility(View.GONE);
                    vh2.cancel.setVisibility(View.GONE);
                }

                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;


                BitmapFactory.decodeFile(message.getThumbnailPath(), options);


                int height = options.outHeight;
                int width = options.outWidth;


                int reqHeight;


                if (width == 0) {
                    reqHeight = 150;
                } else {


                    reqHeight = ((150 * height) / width);


                    if (reqHeight > 150) {
                        reqHeight = 150;
                    }
                }

                try {
                    Glide
                            .with(mContext)
                            .load(message.getThumbnailPath())


                            .bitmapTransform(new CenterCrop(mContext), new BlurTransformation(mContext))


                            .override((150 * density), (density * reqHeight))
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)


                            .placeholder(R.drawable.home_grid_view_image_icon)
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    vh2.imageView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));
                                    return false;
                                }
                            })


                            .into(vh2.imageView);

                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                vh2.imageView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        if (!message.isDownloading()) {
                            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    == PackageManager.PERMISSION_GRANTED) {


                                AlertDialog.Builder builder =
                                        new AlertDialog.Builder(mContext, 0);
                                builder.setTitle(R.string.DownloadConfirmation);
                                builder.setMessage(mContext.getString(R.string.Download) + " " + message.getSize() + " " + mContext.getString(R.string.SizeImage));
                                builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {


                                        String receiverUid = message.getReceiverUid();

                                        String messageId = message.getMessageId();


                                        message.setDownloading(true);


                                        ((ChatMessageScreen) mContext).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {


                                                notifyDataSetChanged();
                                            }
                                        });

                                        download(message.getImagePath(), message.getThumbnailPath(),
                                                Environment.getExternalStorageDirectory().getPath() + ApiOnServer.CHAT_DOWNLOADS_FOLDER + receiverUid + messageId + ".jpg",
                                                AppController.getInstance().findDocumentIdOfReceiver(receiverUid, secretId), message, vh2);


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
                            } else {


                                    /*
                                     *
                                     * have to request permission
                                     *
                                     * */


                                requestStorageAccessPermission("image");

                            }
                        } else {


                            Snackbar snackbar = Snackbar.make(root, R.string.AlreadyDownloading, Snackbar.LENGTH_SHORT);


                            snackbar.show();
                            View view = snackbar.getView();
                            TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                            txtv.setGravity(Gravity.CENTER_HORIZONTAL);

                        }

                    }
                });
            }

            String status = message.getDeliveryStatus();

            if (status.equals("3")) {

                vh2.clock.setVisibility(View.GONE);
                vh2.singleTick.setVisibility(View.GONE);

                vh2.doubleTickGreen.setVisibility(View.GONE);
                vh2.doubleTickBlue.setVisibility(View.VISIBLE);


            } else if (status.equals("2")) {
                vh2.clock.setVisibility(View.GONE);
                vh2.singleTick.setVisibility(View.GONE);

                vh2.doubleTickGreen.setVisibility(View.VISIBLE);
                vh2.doubleTickBlue.setVisibility(View.GONE);
            } else if (status.equals("1")) {

                vh2.clock.setVisibility(View.GONE);
                vh2.singleTick.setVisibility(View.VISIBLE);

                vh2.doubleTickGreen.setVisibility(View.GONE);
                vh2.doubleTickBlue.setVisibility(View.GONE);
            } else {


                vh2.clock.setVisibility(View.VISIBLE);
                vh2.singleTick.setVisibility(View.GONE);

                vh2.doubleTickGreen.setVisibility(View.GONE);
                vh2.doubleTickBlue.setVisibility(View.GONE);
            }


        }
    }


    @SuppressWarnings("TryWithIdenticalCatches,unchecked")


    private void configureViewHolderVideoSent(final ViewHolderVideoSentRetrieve vh2, final int position) {
        final RetrieveSecretChatMessageItem message = mListData.get(position);
        if (message != null) {


            vh2.fnf.setVisibility(View.GONE);


            vh2.date.setText(findOverlayDate(message.getMessageDateOverlay()) + " ");

            vh2.time.setText(convert24to12hourformat(message.getTS()) + " ");


            vh2.cancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {


                    Call<ResponseBody> call = (Call<ResponseBody>) map.get(message.getMessageId());


                    if (call != null)
                        call.cancel();

                }
            });


            if (message.getDownloadStatus() == 1) {

                  /*
                     *
                     * video already downloaded
                     *
                     * */
                vh2.download.setVisibility(View.GONE);
                vh2.progressBar.setVisibility(View.GONE);
                vh2.progressBar2.setVisibility(View.GONE);
                vh2.cancel.setVisibility(View.GONE);


                try {


                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {


                        final File file = new File(message.getVideoPath());


                        if (file.exists()) {

                            thumbnail = ThumbnailUtils.createVideoThumbnail(message.getVideoPath(),
                                    MediaStore.Images.Thumbnails.MINI_KIND);


                            vh2.thumbnail.setImageBitmap(thumbnail);


                            vh2.thumbnail.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {


                                    try {
//                                    Intent intent = new Intent();
//                                    intent.setAction(Intent.ACTION_VIEW);
//
//                                    intent.setDataAndType(Uri.fromFile(file), "video/*");
//
//                                    mContext.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext).toBundle());


                                        Uri intentUri;
                                        if (Build.VERSION.SDK_INT >= 24) {
                                            intentUri = Uri.parse(message.getVideoPath());
                                        } else {
                                            intentUri = Uri.fromFile(file);
                                        }

                                        Intent intent = new Intent();
                                        intent.setAction(Intent.ACTION_VIEW);

                                        intent.setDataAndType(intentUri, "video/*");


                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {


                                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);


                                        } else {


                                            List<ResolveInfo> resInfoList = mContext.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                                            for (ResolveInfo resolveInfo : resInfoList) {
                                                String packageName = resolveInfo.activityInfo.packageName;
                                                mContext.grantUriPermission(packageName, intentUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                            }


                                        }


                                        mContext.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext).toBundle());


                                    } catch (ActivityNotFoundException e) {
                                        Intent i = new Intent(mContext, MediaHistory_FullScreenVideo.class);

                                        i.putExtra("videoPath", message.getVideoPath());
                                        mContext.startActivity(i, ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext).toBundle());


                                    }
                                }
                            });
                        } else {
                            Glide.clear(vh2.thumbnail);
                            vh2.fnf.setVisibility(View.VISIBLE);

                            vh2.thumbnail.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.chat_white_circle));
                            vh2.thumbnail.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));

                        }
                    } else {

                        vh2.fnf.setVisibility(View.VISIBLE);
                        Glide.clear(vh2.thumbnail);

                        vh2.fnf.setText(R.string.PermissionDenied);
                        vh2.thumbnail.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.chat_white_circle));
                        vh2.thumbnail.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));


                        vh2.thumbnail.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                                intent.setData(uri);
                                mContext.startActivity(intent);
                            }
                        });

                    }
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                } catch (Exception e) {


                    e.printStackTrace();

                }
            } else {


                if (message.isDownloading()) {


                    vh2.download.setVisibility(View.GONE);


                    vh2.progressBar2.setVisibility(View.VISIBLE);

                    vh2.progressBar.setVisibility(View.GONE);
                    vh2.cancel.setVisibility(View.VISIBLE);


                } else {
                    vh2.download.setVisibility(View.VISIBLE);
                    vh2.progressBar2.setVisibility(View.GONE);
                    vh2.cancel.setVisibility(View.GONE);
                    vh2.progressBar.setVisibility(View.GONE);
                }
                try {
                    Glide
                            .with(mContext)
                            .load(message.getThumbnailPath())
                            .bitmapTransform(new CenterCrop(mContext), new BlurTransformation(mContext))
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)


                            .placeholder(R.drawable.home_grid_view_image_icon)


                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    vh2.thumbnail.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));
                                    return false;
                                }
                            })
                            .into(vh2.thumbnail);

                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                vh2.thumbnail.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {


                            /* ppopup to ask if wanna download
                         *
                         *
                         * */


                        if (!message.isDownloading()) {
                            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    == PackageManager.PERMISSION_GRANTED) {


                                AlertDialog.Builder builder =
                                        new AlertDialog.Builder(mContext, 0);
                                builder.setTitle(R.string.DownloadConfirmation);
                                builder.setMessage(mContext.getString(R.string.Download) + " " + message.getSize() + " " + mContext.getString(R.string.SizeVideo));
                                builder.setPositiveButton(R.string.ContinueCapital, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {


                                        String messageId = message.getMessageId();

                                        String receiverUid = message.getReceiverUid();
                                        message.setDownloading(true);


                                        ((ChatMessageScreen) mContext).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //notifyItemChanged(viewHolder.getAdapterPosition());

                                                notifyDataSetChanged();
                                            }
                                        });


                                        download(message.getVideoPath(), message.getThumbnailPath(), Environment.getExternalStorageDirectory().getPath() + ApiOnServer.CHAT_DOWNLOADS_FOLDER + receiverUid + messageId + ".mp4", AppController.getInstance().findDocumentIdOfReceiver(receiverUid, secretId), message, vh2);

                                        //    dialog.dismiss();


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


                            } else {


                                    /*
                                     *
                                     * have to request permission
                                     *
                                     * */


                                requestStorageAccessPermission("video");


                            }
                        } else {

                            Snackbar snackbar = Snackbar.make(root, R.string.AlreadyDownloading, Snackbar.LENGTH_SHORT);


                            snackbar.show();
                            View view = snackbar.getView();
                            TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                            txtv.setGravity(Gravity.CENTER_HORIZONTAL);

                        }

                    }
                });
            }

            String status = message.getDeliveryStatus();

            if (status.equals("3")) {

                vh2.clock.setVisibility(View.GONE);
                vh2.singleTick.setVisibility(View.GONE);

                vh2.doubleTickGreen.setVisibility(View.GONE);
                vh2.doubleTickBlue.setVisibility(View.VISIBLE);


            } else if (status.equals("2")) {
                vh2.clock.setVisibility(View.GONE);
                vh2.singleTick.setVisibility(View.GONE);

                vh2.doubleTickGreen.setVisibility(View.VISIBLE);
                vh2.doubleTickBlue.setVisibility(View.GONE);
            } else if (status.equals("1")) {

                vh2.clock.setVisibility(View.GONE);
                vh2.singleTick.setVisibility(View.VISIBLE);

                vh2.doubleTickGreen.setVisibility(View.GONE);
                vh2.doubleTickBlue.setVisibility(View.GONE);
            } else {


                vh2.clock.setVisibility(View.VISIBLE);
                vh2.singleTick.setVisibility(View.GONE);

                vh2.doubleTickGreen.setVisibility(View.GONE);
                vh2.doubleTickBlue.setVisibility(View.GONE);
            }


        }

    }


    private void configureViewHolderLocationSent(ViewHolderLocationSent vh2, final int position) {
        final RetrieveSecretChatMessageItem message = mListData.get(position);
        if (message != null) {


            //      vh2.senderName.setText(message.getSenderName());

//            vh2.time.setTypeface(tf, Typeface.ITALIC);
            vh2.date.setText(findOverlayDate(message.getMessageDateOverlay()) + " ");
//            vh2.date.setTypeface(tf, Typeface.ITALIC);
            vh2.time.setText(convert24to12hourformat(message.getTS()) + " ");


            if (vh2.mMap != null)

                vh2.mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            String args[] = message.getPlaceInfo().split("@@");

            String LatLng = args[0];

            String[] parts = LatLng.split(",");

            String lat = parts[0].substring(1);
            String lng = parts[1].substring(0, parts[1].length() - 1);
            args = null;
            parts = null;

            vh2.positionSelected = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));


            String status = message.getDeliveryStatus();

            if (status.equals("3")) {

                vh2.clock.setVisibility(View.GONE);
                vh2.singleTick.setVisibility(View.GONE);

                vh2.doubleTickGreen.setVisibility(View.GONE);
                vh2.doubleTickBlue.setVisibility(View.VISIBLE);


            } else if (status.equals("2")) {
                vh2.clock.setVisibility(View.GONE);
                vh2.singleTick.setVisibility(View.GONE);

                vh2.doubleTickGreen.setVisibility(View.VISIBLE);
                vh2.doubleTickBlue.setVisibility(View.GONE);
            } else if (status.equals("1")) {

                vh2.clock.setVisibility(View.GONE);
                vh2.singleTick.setVisibility(View.VISIBLE);

                vh2.doubleTickGreen.setVisibility(View.GONE);
                vh2.doubleTickBlue.setVisibility(View.GONE);
            } else {


                vh2.clock.setVisibility(View.VISIBLE);
                vh2.singleTick.setVisibility(View.GONE);

                vh2.doubleTickGreen.setVisibility(View.GONE);
                vh2.doubleTickBlue.setVisibility(View.GONE);
            }


        }
    }

    @SuppressWarnings("TryWithIdenticalCatches")

    private void configureViewHolderContactSent(ViewHolderContactSent vh2, final int position) {
        final RetrieveSecretChatMessageItem message = mListData.get(position);
        if (message != null) {


            vh2.date.setText(findOverlayDate(message.getMessageDateOverlay()) + " ");


            vh2.time.setText(convert24to12hourformat(message.getTS()) + " ");


            String contactInfo = message.getContactInfo();


            String contactName, contactNumber;

            try {


                String parts[] = contactInfo.split("@@");


                contactName = parts[0];


                String arr[] = parts[1].split("/");


                contactNumber = arr[0];
                arr = null;
                parts = null;

                vh2.contactName.setText(contactName);

                vh2.contactNumber.setText(contactNumber);
                if (contactName == null || contactName.isEmpty()) {
                    vh2.contactName.setText(R.string.Unknown);
                } else if (contactNumber == null || contactNumber.isEmpty()) {
                    vh2.contactNumber.setText(R.string.NoNumber);
                }
            } catch (StringIndexOutOfBoundsException e) {
                vh2.contactNumber.setText(R.string.NoNumber);
            } catch (Exception e) {
                vh2.contactNumber.setText(R.string.NoNumber);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }


            String status = message.getDeliveryStatus();

            if (status.equals("3")) {

                vh2.clock.setVisibility(View.GONE);
                vh2.singleTick.setVisibility(View.GONE);

                vh2.doubleTickGreen.setVisibility(View.GONE);
                vh2.doubleTickBlue.setVisibility(View.VISIBLE);


            } else if (status.equals("2")) {
                vh2.clock.setVisibility(View.GONE);
                vh2.singleTick.setVisibility(View.GONE);

                vh2.doubleTickGreen.setVisibility(View.VISIBLE);
                vh2.doubleTickBlue.setVisibility(View.GONE);
            } else if (status.equals("1")) {

                vh2.clock.setVisibility(View.GONE);
                vh2.singleTick.setVisibility(View.VISIBLE);

                vh2.doubleTickGreen.setVisibility(View.GONE);
                vh2.doubleTickBlue.setVisibility(View.GONE);
            } else {


                vh2.clock.setVisibility(View.VISIBLE);
                vh2.singleTick.setVisibility(View.GONE);

                vh2.doubleTickGreen.setVisibility(View.GONE);
                vh2.doubleTickBlue.setVisibility(View.GONE);

            }

        }
    }


    @SuppressWarnings("TryWithIdenticalCatches,unchecked")

    private void configureViewHolderAudioSent(final ViewHolderAudioSentRetrieve vh2, final int position) {
        final RetrieveSecretChatMessageItem message = mListData.get(position);
        if (message != null) {


            vh2.fnf.setVisibility(View.GONE);


            vh2.playButton.setVisibility(View.VISIBLE);


            vh2.date.setText(findOverlayDate(message.getMessageDateOverlay()) + " ");


            vh2.time.setText(convert24to12hourformat(message.getTS()) + " ");

            vh2.cancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {


                    Call<ResponseBody> call = (Call<ResponseBody>) map.get(message.getMessageId());


                    if (call != null)
                        call.cancel();

                }
            });


            if (message.getDownloadStatus() == 1) {


                vh2.download.setVisibility(View.GONE);


                vh2.progressBar.setVisibility(View.GONE);


                vh2.cancel.setVisibility(View.GONE);

                vh2.progressBar2.setVisibility(View.GONE);
                try {


                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {


                        final File file = new File(message.getAudioPath());


                        if (file.exists()) {
                            vh2.playButton.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {


                                    try {


                                        Uri intentUri;
                                        if (Build.VERSION.SDK_INT >= 24) {
                                            intentUri = Uri.parse(message.getAudioPath());
                                        } else {
                                            intentUri = Uri.fromFile(file);
                                        }

                                        Intent intent = new Intent();
                                        intent.setAction(Intent.ACTION_VIEW);


                                        intent.setDataAndType(intentUri, "audio/*");
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {


                                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);


                                        } else {


                                            List<ResolveInfo> resInfoList = mContext.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                                            for (ResolveInfo resolveInfo : resInfoList) {
                                                String packageName = resolveInfo.activityInfo.packageName;
                                                mContext.grantUriPermission(packageName, intentUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                            }


                                        }


                                        intent.setPackage("com.google.android.music");
                                        mContext.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext).toBundle());
                                    } catch (ActivityNotFoundException e) {


                                        AlertDialog.Builder builder =
                                                new AlertDialog.Builder(mContext, 0);
                                        builder.setTitle(R.string.PlayConfirmation);
                                        builder.setMessage(mContext.getString(R.string.NoApp));
                                        builder.setPositiveButton(R.string.PlayHere, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {


                                                mediaPlayer = new MediaPlayer();

                                                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);


                                                try {
                                                    mediaPlayer.setDataSource(mContext, Uri.fromFile(file));
                                                    mediaPlayer.prepare();
                                                } catch (IOException er) {
                                                    er.printStackTrace();
                                                }

                                                mediaPlayer.start();

                                                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {


                                                    @Override

                                                    public void onCompletion(MediaPlayer mp) {

                                                        // TODO Auto-generated method stub

                                                        mediaPlayer.release();

                                                        mediaPlayer = null;

                                                    }

                                                });


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
                                        builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {


                                                dialog.cancel();

                                            }
                                        });
                                        builder.show();


                                    }


                                }
                            });
                        } else {

                            vh2.playButton.setVisibility(View.GONE);
                            vh2.fnf.setVisibility(View.VISIBLE);

                        }
                    } else {

                        vh2.playButton.setVisibility(View.GONE);
                        vh2.fnf.setVisibility(View.VISIBLE);

                        vh2.fnf.setText(R.string.PermissionDenied);

                        vh2.fnf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                                intent.setData(uri);
                                mContext.startActivity(intent);
                            }
                        });

                    }

                } catch (Exception e) {
                    e.printStackTrace();


                }
            } else {
                if (message.isDownloading()) {


                    vh2.download.setVisibility(View.GONE);


                    vh2.progressBar2.setVisibility(View.VISIBLE);

                    vh2.progressBar.setVisibility(View.GONE);
                    vh2.cancel.setVisibility(View.VISIBLE);

                } else {
                    vh2.download.setVisibility(View.VISIBLE);


                    vh2.progressBar.setVisibility(View.GONE);
                    vh2.cancel.setVisibility(View.GONE);

                    vh2.progressBar2.setVisibility(View.GONE);

                }


                vh2.playButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_GRANTED) {


                            if (!message.isDownloading()) {
                                AlertDialog.Builder builder =
                                        new AlertDialog.Builder(mContext, 0);
                                builder.setTitle(R.string.DownloadConfirmation);
                                builder.setMessage(mContext.getString(R.string.Download) + " " + message.getSize() + " " + mContext.getString(R.string.SizeAudio));
                                builder.setPositiveButton(R.string.ContinueCapital, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {


                                        String receiverUid = message.getReceiverUid();

                                        String messageId = message.getMessageId();
                                        message.setDownloading(true);


                                        ((ChatMessageScreen) mContext).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //notifyItemChanged(viewHolder.getAdapterPosition());

                                                notifyDataSetChanged();
                                            }
                                        });

                                        download(message.getAudioPath(), null, Environment.getExternalStorageDirectory().getPath() + ApiOnServer.CHAT_DOWNLOADS_FOLDER + receiverUid + messageId + ".mp3", AppController.getInstance().findDocumentIdOfReceiver(receiverUid, secretId), message, vh2);

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

                            } else {


                                Snackbar snackbar = Snackbar.make(root, R.string.AlreadyDownloading, Snackbar.LENGTH_SHORT);


                                snackbar.show();
                                View view = snackbar.getView();
                                TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                                txtv.setGravity(Gravity.CENTER_HORIZONTAL);

                            }
                        } else

                        {


                    /*
                     * have to request permission
                     *
                     * */


                            requestStorageAccessPermission("audio");
                        }
                    }
                });


            }


            String status = message.getDeliveryStatus();

            if (status.equals("3")) {

                vh2.clock.setVisibility(View.GONE);
                vh2.singleTick.setVisibility(View.GONE);

                vh2.doubleTickGreen.setVisibility(View.GONE);
                vh2.doubleTickBlue.setVisibility(View.VISIBLE);


            } else if (status.equals("2")) {
                vh2.clock.setVisibility(View.GONE);
                vh2.singleTick.setVisibility(View.GONE);

                vh2.doubleTickGreen.setVisibility(View.VISIBLE);
                vh2.doubleTickBlue.setVisibility(View.GONE);
            } else if (status.equals("1")) {

                vh2.clock.setVisibility(View.GONE);
                vh2.singleTick.setVisibility(View.VISIBLE);

                vh2.doubleTickGreen.setVisibility(View.GONE);
                vh2.doubleTickBlue.setVisibility(View.GONE);
            } else {


                vh2.clock.setVisibility(View.VISIBLE);
                vh2.singleTick.setVisibility(View.GONE);

                vh2.doubleTickGreen.setVisibility(View.GONE);
                vh2.doubleTickBlue.setVisibility(View.GONE);
            }
        }


    }


    private String convert24to12hourformat(String d) {

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

    private void download(final String url, final String thumbnailPath, final String filePath,
                          final String receiverDocid, final RetrieveSecretChatMessageItem message,
                          final RecyclerView.ViewHolder viewHolder)
    {


        Log.d("gthras",""+url);
        final FileDownloadService downloadService =
                ServiceGenerator.createService(FileDownloadService.class);
        Call<ResponseBody> call = downloadService.downloadFileWithDynamicUrlAsync(url);
        map.put(message.getMessageId(), call);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response)
            {
                if (response.isSuccessful()) {
                    new AsyncTask<Void, Long, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            boolean writtenToDisk = writeResponseBodyToDisk(response.body(), filePath, viewHolder, message.getMessageType(), message.getMessageId());
                            message.setDownloading(false);
                            ((ChatMessageScreen) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {


                                    notifyDataSetChanged();
                                }
                            });


                            if (writtenToDisk) {


                                deleteFileFromServer(url);


                                if (thumbnailPath != null) {
/*
 *
 * incase of image or video delete the thumbnail
 *
 * */


                                    File fdelete = new File(thumbnailPath);
                                    if (fdelete.exists()) fdelete.delete();


                                }


                                message.setDownloadStatus(1);

                                String type = message.getMessageType();

                                if (type.equals("1")) {
                                    message.setImagePath(filePath);
                                } else if (type.equals("2")) {
                                    message.setVideoPath(filePath);
                                } else if (type.equals("5")) {

                                    message.setAudioPath(filePath);
                                } else if (type.equals("7")) {
/*
 * For doodle
 */
                                    message.setImagePath(filePath);
                                }


                                ((ChatMessageScreen) mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        notifyDataSetChanged();
                                    }
                                });


                                AppController.getInstance().getDbController().updateDownloadStatusAndPath(receiverDocid, filePath, message.getMessageId());


                            } else {
/*
 *
 * failed to download the file from the server
 *
 *
 * */


                                ((ChatMessageScreen) mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {


                                        Snackbar snackbar = Snackbar.make(root, R.string.DownloadFailed, Snackbar.LENGTH_SHORT);


                                        snackbar.show();
                                        View view = snackbar.getView();
                                        TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                                        txtv.setGravity(Gravity.CENTER_HORIZONTAL);


                                    }
                                });


                            }


                            return null;


                        }
                    }.execute();


                } else {


                    ((ChatMessageScreen) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            message.setDownloading(false);
                            notifyDataSetChanged();


                            Snackbar snackbar = Snackbar.make(root, R.string.FileNotFound, Snackbar.LENGTH_SHORT);


                            snackbar.show();
                            View view = snackbar.getView();
                            TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                            txtv.setGravity(Gravity.CENTER_HORIZONTAL);


                        }
                    });
                }


            }

            @Override
            public void onFailure(final Call<ResponseBody> call, Throwable t) {


                t.printStackTrace();


                ((ChatMessageScreen) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        message.setDownloading(false);


                        notifyDataSetChanged();


                        if (call.isCanceled()) {


                            Snackbar snackbar = Snackbar.make(root, R.string.DownloadCanceled, Snackbar.LENGTH_SHORT);


                            snackbar.show();
                            View view = snackbar.getView();
                            TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                            txtv.setGravity(Gravity.CENTER_HORIZONTAL);

                        } else {


                            Snackbar snackbar = Snackbar.make(root, R.string.NoInternet, Snackbar.LENGTH_SHORT);


                            snackbar.show();
                            View view = snackbar.getView();
                            TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                            txtv.setGravity(Gravity.CENTER_HORIZONTAL);


                        }


                    }
                });


            }

        });


    }


    @SuppressWarnings("all")
    private boolean writeResponseBodyToDisk(ResponseBody body, String filePath,
                                            final RecyclerView.ViewHolder viewHolder, String messageType, final String messageId) {


        fileSizeDownloaded = 0;


        if (messageType.equals("1")) {


            try {
                if (mListData.get(viewHolder.getAdapterPosition()).isSelf()) {


                    ((ChatMessageScreen) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            try {
                                if (mListData.get(viewHolder.getAdapterPosition()).getMessageId().equals(messageId)) {
                                    if (((ViewHolderImageSentRetrieve) viewHolder).progressBar.getVisibility() == View.GONE) {
                                        ((ViewHolderImageSentRetrieve) viewHolder).progressBar.setVisibility(View.VISIBLE);
                                    }
                                }


                            } catch (ArrayIndexOutOfBoundsException e) {
                                e.printStackTrace();
                            }
                            ((ViewHolderImageSentRetrieve) viewHolder).progressBar2.setVisibility(View.GONE);

                        }
                    });


                    ((ViewHolderImageSentRetrieve) viewHolder).progressBar.setOnProgressListener(new RingProgressBar.OnProgressListener() {

                        @Override
                        public void progressToComplete() {


                            ((ChatMessageScreen) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {


                                    Snackbar snackbar = Snackbar.make(root, R.string.ImageDownloaded, Snackbar.LENGTH_SHORT);


                                    snackbar.show();
                                    View view = snackbar.getView();
                                    TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                                    txtv.setGravity(Gravity.CENTER_HORIZONTAL);


                                }
                            });

                        }
                    });


                } else {

                    ((ChatMessageScreen) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            try {
                                if (mListData.get(viewHolder.getAdapterPosition()).getMessageId().equals(messageId)) {
                                    if (((ViewHolderImageReceived) viewHolder).progressBar.getVisibility() == View.GONE) {
                                        ((ViewHolderImageReceived) viewHolder).progressBar.setVisibility(View.VISIBLE);
                                    }
                                }


                            } catch (ArrayIndexOutOfBoundsException e) {
                                e.printStackTrace();
                            }
                            ((ViewHolderImageReceived) viewHolder).progressBar2.setVisibility(View.GONE);

                        }
                    });


                    ((ViewHolderImageReceived) viewHolder).progressBar.setOnProgressListener(new RingProgressBar.OnProgressListener() {

                        @Override
                        public void progressToComplete() {


                            ((ChatMessageScreen) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {


                                    Snackbar snackbar = Snackbar.make(root, R.string.ImageDownloaded, Snackbar.LENGTH_SHORT);


                                    snackbar.show();
                                    View view = snackbar.getView();
                                    TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                                    txtv.setGravity(Gravity.CENTER_HORIZONTAL);


                                }
                            });

                        }
                    });
                }


            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        } else if (messageType.equals("2")) {


            try {
                if (mListData.get(viewHolder.getAdapterPosition()).isSelf()) {


                    ((ChatMessageScreen) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            try {
                                if (mListData.get(viewHolder.getAdapterPosition()).getMessageId().equals(messageId)) {

                                    if (((ViewHolderVideoSentRetrieve) viewHolder).progressBar.getVisibility() == View.GONE) {
                                        ((ViewHolderVideoSentRetrieve) viewHolder).progressBar.setVisibility(View.VISIBLE);
                                    }
                                }


                            } catch (ArrayIndexOutOfBoundsException e) {
                                e.printStackTrace();
                            }

                            ((ViewHolderVideoSentRetrieve) viewHolder).progressBar2.setVisibility(View.GONE);


                        }
                    });


                    ((ViewHolderVideoSentRetrieve) viewHolder).progressBar.setOnProgressListener(new RingProgressBar.OnProgressListener() {

                        @Override
                        public void progressToComplete() {
                            ((ChatMessageScreen) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {


                                    Snackbar snackbar = Snackbar.make(root, R.string.VideoDownloaded, Snackbar.LENGTH_SHORT);


                                    snackbar.show();
                                    View view = snackbar.getView();
                                    TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                                    txtv.setGravity(Gravity.CENTER_HORIZONTAL);


                                }
                            });

                        }
                    });


                } else {
                    ((ChatMessageScreen) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            try {
                                if (mListData.get(viewHolder.getAdapterPosition()).getMessageId().equals(messageId)) {

                                    if (((ViewHolderVideoReceived) viewHolder).progressBar.getVisibility() == View.GONE) {
                                        ((ViewHolderVideoReceived) viewHolder).progressBar.setVisibility(View.VISIBLE);
                                    }
                                }


                            } catch (ArrayIndexOutOfBoundsException e) {
                                e.printStackTrace();
                            }

                            ((ViewHolderVideoReceived) viewHolder).progressBar2.setVisibility(View.GONE);


                        }
                    });


                    ((ViewHolderVideoReceived) viewHolder).progressBar.setOnProgressListener(new RingProgressBar.OnProgressListener() {

                        @Override
                        public void progressToComplete() {
                            ((ChatMessageScreen) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {


                                    Snackbar snackbar = Snackbar.make(root, R.string.VideoDownloaded, Snackbar.LENGTH_SHORT);


                                    snackbar.show();
                                    View view = snackbar.getView();
                                    TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                                    txtv.setGravity(Gravity.CENTER_HORIZONTAL);


                                }
                            });

                        }
                    });
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        } else if (messageType.equals("5")) {
            try {
                if (mListData.get(viewHolder.getAdapterPosition()).isSelf()) {

                    ((ChatMessageScreen) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            try {


                                if (mListData.get(viewHolder.getAdapterPosition()).getMessageId().equals(messageId)) {
                                    if (((ViewHolderAudioSentRetrieve) viewHolder).progressBar.getVisibility() == View.GONE) {
                                        ((ViewHolderAudioSentRetrieve) viewHolder).progressBar.setVisibility(View.VISIBLE);
                                    }
                                }
                            } catch (ArrayIndexOutOfBoundsException e) {
                                e.printStackTrace();
                            }


                            ((ViewHolderAudioSentRetrieve) viewHolder).progressBar2.setVisibility(View.GONE);


                        }
                    });


                    ((ViewHolderAudioSentRetrieve) viewHolder).progressBar.setOnProgressListener(new RingProgressBar.OnProgressListener() {

                        @Override
                        public void progressToComplete() {


                            ((ChatMessageScreen) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {


                                    Snackbar snackbar = Snackbar.make(root, R.string.AudioDownloaded, Snackbar.LENGTH_SHORT);


                                    snackbar.show();
                                    View view = snackbar.getView();
                                    TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                                    txtv.setGravity(Gravity.CENTER_HORIZONTAL);


                                }
                            });


                        }
                    });


                } else {
                    ((ChatMessageScreen) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            try {


                                if (mListData.get(viewHolder.getAdapterPosition()).getMessageId().equals(messageId)) {
                                    if (((ViewHolderAudioReceived) viewHolder).progressBar.getVisibility() == View.GONE) {
                                        ((ViewHolderAudioReceived) viewHolder).progressBar.setVisibility(View.VISIBLE);
                                    }
                                }
                            } catch (ArrayIndexOutOfBoundsException e) {
                                e.printStackTrace();
                            }


                            ((ViewHolderAudioReceived) viewHolder).progressBar2.setVisibility(View.GONE);


                        }
                    });


                    ((ViewHolderAudioReceived) viewHolder).progressBar.setOnProgressListener(new RingProgressBar.OnProgressListener() {

                        @Override
                        public void progressToComplete() {


                            ((ChatMessageScreen) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {


                                    Snackbar snackbar = Snackbar.make(root, R.string.AudioDownloaded, Snackbar.LENGTH_SHORT);


                                    snackbar.show();
                                    View view = snackbar.getView();
                                    TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                                    txtv.setGravity(Gravity.CENTER_HORIZONTAL);


                                }
                            });


                        }
                    });
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        } else if (messageType.equals("7")) {

            try {

                if (mListData.get(viewHolder.getAdapterPosition()).isSelf()) {


                    ((ChatMessageScreen) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            try {
                                if (mListData.get(viewHolder.getAdapterPosition()).getMessageId().equals(messageId)) {

                                    if (((ViewHolderDoodleSentRetrieve) viewHolder).progressBar.getVisibility() == View.GONE) {
                                        ((ViewHolderDoodleSentRetrieve) viewHolder).progressBar.setVisibility(View.VISIBLE);
                                    }
                                }


                            } catch (ArrayIndexOutOfBoundsException e) {
                                e.printStackTrace();
                            }

                            ((ViewHolderDoodleSentRetrieve) viewHolder).progressBar2.setVisibility(View.GONE);


                        }
                    });


                    ((ViewHolderDoodleSentRetrieve) viewHolder).progressBar.setOnProgressListener(new RingProgressBar.OnProgressListener() {

                        @Override
                        public void progressToComplete() {
                            ((ChatMessageScreen) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {


                                    Snackbar snackbar = Snackbar.make(root, R.string.DoodleDownloaded, Snackbar.LENGTH_SHORT);


                                    snackbar.show();
                                    View view = snackbar.getView();
                                    TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                                    txtv.setGravity(Gravity.CENTER_HORIZONTAL);


                                }
                            });

                        }
                    });


                } else {
                    ((ChatMessageScreen) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (mListData.get(viewHolder.getAdapterPosition()).getMessageId().equals(messageId)) {

                                    if (((ViewHolderDoodleReceived) viewHolder).progressBar.getVisibility() == View.GONE) {
                                        ((ViewHolderDoodleReceived) viewHolder).progressBar.setVisibility(View.VISIBLE);
                                    }
                                }


                            } catch (ArrayIndexOutOfBoundsException e) {
                                e.printStackTrace();
                            }

                            ((ViewHolderDoodleReceived) viewHolder).progressBar2.setVisibility(View.GONE);


                        }
                    });


                    ((ViewHolderDoodleReceived) viewHolder).progressBar.setOnProgressListener(new RingProgressBar.OnProgressListener() {

                        @Override
                        public void progressToComplete() {
                            ((ChatMessageScreen) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {


                                    Snackbar snackbar = Snackbar.make(root, R.string.DoodleDownloaded, Snackbar.LENGTH_SHORT);


                                    snackbar.show();
                                    View view = snackbar.getView();
                                    TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                                    txtv.setGravity(Gravity.CENTER_HORIZONTAL);


                                }
                            });

                        }
                    });
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

        try {
            // todo change the file location/name according to your needs
            File folder = new File(Environment.getExternalStorageDirectory().getPath() + "/mqttChat");
            if (!folder.exists() && !folder.isDirectory()) {
                folder.mkdirs();
            }
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];
                final long fileSize = body.contentLength();
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    try {
                        if (messageType.equals("1")) {
                            ((ChatMessageScreen) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (mListData.get(viewHolder.getAdapterPosition()).isSelf())
                                        {
                                            ((ViewHolderImageSentRetrieve) viewHolder).progressBar.setProgress((int) ((fileSizeDownloaded * 100) / fileSize));
                                        } else {
                                            ((ViewHolderImageReceived) viewHolder).progressBar.setProgress((int) ((fileSizeDownloaded * 100) / fileSize));
                                        }
                                    } catch (ArrayIndexOutOfBoundsException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        } else if (messageType.equals("2")) {


                            ((ChatMessageScreen) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (mListData.get(viewHolder.getAdapterPosition()).isSelf()) {


                                            ((ViewHolderVideoSentRetrieve) viewHolder).progressBar.setProgress((int) ((fileSizeDownloaded * 100) / fileSize));


                                        } else {
                                            ((ViewHolderVideoReceived) viewHolder).progressBar.setProgress((int) ((fileSizeDownloaded * 100) / fileSize));

                                        }
                                    } catch (ArrayIndexOutOfBoundsException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });


                        } else if (messageType.equals("5")) {


                            ((ChatMessageScreen) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {


                                    try {
                                        if (mListData.get(viewHolder.getAdapterPosition()).isSelf()) {

                                            ((ViewHolderAudioSentRetrieve) viewHolder).progressBar.setProgress((int) ((fileSizeDownloaded * 100) / fileSize));


                                        } else {

                                            ((ViewHolderAudioReceived) viewHolder).progressBar.setProgress((int) ((fileSizeDownloaded * 100) / fileSize));

                                        }
                                    } catch (ArrayIndexOutOfBoundsException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        } else if (messageType.equals("7")) {


                            ((ChatMessageScreen) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    try {
                                        if (mListData.get(viewHolder.getAdapterPosition()).isSelf()) {

                                            ((ViewHolderDoodleSentRetrieve) viewHolder).progressBar.setProgress((int) ((fileSizeDownloaded * 100) / fileSize));


                                        } else {
                                            ((ViewHolderDoodleReceived) viewHolder).progressBar.setProgress((int) ((fileSizeDownloaded * 100) / fileSize));
                                        }
                                    } catch (ArrayIndexOutOfBoundsException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });

                        }


                    } catch (ClassCastException e) {
                        e.printStackTrace();
                    }

                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }

                try {
                    if (messageType.equals("1")) {


                        ((ChatMessageScreen) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    if (mListData.get(viewHolder.getAdapterPosition()).isSelf()) {


                                        ((ViewHolderImageSentRetrieve) viewHolder).progressBar.setVisibility(View.GONE);
                                    } else {
                                        ((ViewHolderImageReceived) viewHolder).progressBar.setVisibility(View.GONE);
                                    }


                                } catch (ArrayIndexOutOfBoundsException e) {
                                    e.printStackTrace();
                                }
                            }

                        });
                    } else if (messageType.equals("2")) {


                        ((ChatMessageScreen) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    if (mListData.get(viewHolder.getAdapterPosition()).isSelf()) {


                                        ((ViewHolderVideoSentRetrieve) viewHolder).progressBar.setVisibility(View.GONE);
                                    } else {
                                        ((ViewHolderVideoReceived) viewHolder).progressBar.setVisibility(View.GONE);
                                    }


                                } catch (ArrayIndexOutOfBoundsException e) {
                                    e.printStackTrace();
                                }
                            }

                        });
                    } else if (messageType.equals("5")) {


                        ((ChatMessageScreen) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (mListData.get(viewHolder.getAdapterPosition()).isSelf()) {


                                        ((ViewHolderAudioSentRetrieve) viewHolder).progressBar.setVisibility(View.GONE);

                                    } else {
                                        ((ViewHolderAudioReceived) viewHolder).progressBar.setVisibility(View.GONE);
                                    }

                                } catch (ArrayIndexOutOfBoundsException e) {
                                    e.printStackTrace();
                                }
                            }

                        });
                    } else {


                        ((ChatMessageScreen) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (mListData.get(viewHolder.getAdapterPosition()).isSelf()) {
                                        ((ViewHolderDoodleSentRetrieve) viewHolder).progressBar.setVisibility(View.GONE);
                                    } else {
                                        ((ViewHolderDoodleReceived) viewHolder).progressBar.setVisibility(View.GONE);
                                    }
                                } catch (ArrayIndexOutOfBoundsException e) {
                                    e.printStackTrace();
                                }
                            }

                        });


                    }
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }

            }
        } catch (IOException e)

        {
            return false;
        }

    }


    private void deleteFileFromServer(String url) {


        String[] arr = url.split("/");

        JSONObject obj = new JSONObject();


        try {

            obj.put("ImageName", arr[arr.length - 1]);


        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                ApiOnServer.DELETE_DOWNLOAD, obj, new com.android.volley.Response.Listener<JSONObject>() {


            @Override
            public void onResponse(JSONObject response) {


            }
        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();


            }
        });


/*
 *
 *
 * setting timeout to 20 sec
 *
 * */


        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
/* Add the request to the RequestQueue.*/
        AppController.getInstance().addToRequestQueue(jsonObjReq, "deleteFileApiRequest");

    }


    private String findOverlayDate(String date) {

        try {



            SimpleDateFormat sdf = new SimpleDateFormat("EEE dd/MMM/yyyy");


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
            month1 = null;
            month2 = null;


            if (sdf.format(new Date(System.currentTimeMillis() - AppController.getInstance().getTimeDelta())).equals(date)) {


                m2 = null;
                m1 = null;
                d2 = null;
                d1 = null;
                sdf = null;
                return "Today";
            } else if ((Integer.parseInt(d1.substring(11) + m1 + d1.substring(4, 6)) - Integer.parseInt(d2.substring(11) + m2 + d2.substring(4, 6))) == 1) {

                m2 = null;
                m1 = null;
                d2 = null;
                d1 = null;
                sdf = null;
                return "Yesterday";

            } else {

                m2 = null;
                m1 = null;
                d2 = null;
                d1 = null;
                sdf = null;


                SimpleDateFormat inputFormat = new SimpleDateFormat("EEE dd/MMM/yyyy");
                SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy");

                Date date1 = null;
                String str = null;

                try {
                    date1 = inputFormat.parse(date);
                    str = outputFormat.format(date1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return str;
            }

        } catch (Exception e) {
            e.printStackTrace();

            return date;
        }
    }


    private void requestStorageAccessPermission(String type) {


        if (ActivityCompat.shouldShowRequestPermissionRationale((ChatMessageScreen) mContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar snackbar = Snackbar.make(root, R.string.StorageAccess,
                    Snackbar.LENGTH_INDEFINITE).setAction(mContext.getString(R.string.Ok), new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ActivityCompat.requestPermissions((ChatMessageScreen) mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            21);
                }
            });


            snackbar.show();


            View view = snackbar.getView();
            TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            txtv.setGravity(Gravity.CENTER_HORIZONTAL);

        } else

        {

            ActivityCompat.requestPermissions((ChatMessageScreen) mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    21);
        }


    }


    /*
     * View holders for non-sup specific items
     */

    /*********************************************/
    private void configureViewHolderGifReceived(final ViewHolderGifReceived vh2, final int position) {
        final RetrieveSecretChatMessageItem message = mListData.get(position);
        if (message != null) {


//            vh2.time.setTypeface(tf, Typeface.ITALIC);
//
//            vh2.date.setTypeface(tf, Typeface.ITALIC);
            vh2.date.setText(findOverlayDate(message.getMessageDateOverlay()) + " ");


            vh2.time.setText(convert24to12hourformat(message.getTS()) + " ");
            try {
                Glide.with(mContext)
                        .load(message.getGifUrl())
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .placeholder(R.drawable.home_grid_view_image_icon)
                        .into(vh2.gifStillImage);


                Glide.with(mContext)
                        .load(message.getGifUrl())
                        .asGif()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .crossFade()
                        .into(vh2.gifImage);

            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }


            try {
                vh2.gifStillImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vh2.gifImage.setVisibility(View.VISIBLE);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

                vh2.gifImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(mContext, GifPlayer.class);
                        intent.putExtra("gifUrl", message.getGifUrl());


                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation((Activity) mContext, vh2.gifImage, "image");
                        mContext.startActivity(intent, options.toBundle());

                        vh2.gifImage.setVisibility(View.GONE);


                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    private void configureViewHolderGifSent(final ViewHolderGifSent vh2, final int position) {
        final RetrieveSecretChatMessageItem message = mListData.get(position);
        if (message != null) {


            vh2.date.setText(findOverlayDate(message.getMessageDateOverlay()) + " ");


            vh2.time.setText(convert24to12hourformat(message.getTS()) + " ");

            try {
                Glide.with(mContext)
                        .load(message.getGifUrl())
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .placeholder(R.drawable.home_grid_view_image_icon)
                        .into(vh2.stillGifImage);

                Glide.with(mContext)
                        .load(message.getGifUrl())
                        .asGif()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .crossFade()
                        .into(vh2.gifImage);

            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }


            String status = message.getDeliveryStatus();

            if (status.equals("3")) {

                vh2.clock.setVisibility(View.GONE);
                vh2.singleTick.setVisibility(View.GONE);

                vh2.doubleTickGreen.setVisibility(View.GONE);
                vh2.doubleTickBlue.setVisibility(View.VISIBLE);


            } else if (status.equals("2")) {
                vh2.clock.setVisibility(View.GONE);
                vh2.singleTick.setVisibility(View.GONE);

                vh2.doubleTickGreen.setVisibility(View.VISIBLE);
                vh2.doubleTickBlue.setVisibility(View.GONE);
            } else if (status.equals("1")) {

                vh2.clock.setVisibility(View.GONE);
                vh2.singleTick.setVisibility(View.VISIBLE);

                vh2.doubleTickGreen.setVisibility(View.GONE);
                vh2.doubleTickBlue.setVisibility(View.GONE);
            } else {


                vh2.clock.setVisibility(View.VISIBLE);
                vh2.singleTick.setVisibility(View.GONE);

                vh2.doubleTickGreen.setVisibility(View.GONE);
                vh2.doubleTickBlue.setVisibility(View.GONE);
            }


            try {
                vh2.stillGifImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vh2.gifImage.setVisibility(View.VISIBLE);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

                vh2.gifImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Intent intent = new Intent(mContext, GifPlayer.class);
                        intent.putExtra("gifUrl", message.getGifUrl());
                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation((Activity) mContext, vh2.gifImage, "image");
                        mContext.startActivity(intent, options.toBundle());
                        vh2.gifImage.setVisibility(View.GONE);

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }


    private void configureViewHolderStickersSent(final ViewHolderStickerSent vh15, final int position) {
        final RetrieveSecretChatMessageItem message = mListData.get(position);

        if (message != null) {

            vh15.date.setText(findOverlayDate(message.getMessageDateOverlay()) + " ");


            vh15.time.setText(convert24to12hourformat(message.getTS()) + " ");
            try {

                Glide.with(mContext)
                        .load(message.getStickerUrl())
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(vh15.imageView);


            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }

            String status = message.getDeliveryStatus();

            if (status.equals("3")) {

                vh15.clock.setVisibility(View.GONE);
                vh15.singleTick.setVisibility(View.GONE);

                vh15.doubleTickGreen.setVisibility(View.GONE);
                vh15.doubleTickBlue.setVisibility(View.VISIBLE);


            } else if (status.equals("2")) {
                vh15.clock.setVisibility(View.GONE);
                vh15.singleTick.setVisibility(View.GONE);

                vh15.doubleTickGreen.setVisibility(View.VISIBLE);
                vh15.doubleTickBlue.setVisibility(View.GONE);
            } else if (status.equals("1")) {

                vh15.clock.setVisibility(View.GONE);
                vh15.singleTick.setVisibility(View.VISIBLE);

                vh15.doubleTickGreen.setVisibility(View.GONE);
                vh15.doubleTickBlue.setVisibility(View.GONE);
            } else {


                vh15.clock.setVisibility(View.VISIBLE);
                vh15.singleTick.setVisibility(View.GONE);

                vh15.doubleTickGreen.setVisibility(View.GONE);
                vh15.doubleTickBlue.setVisibility(View.GONE);
            }


        }
    }


    private void configureViewHolderStickerReceived(final ViewHolderStickerReceived vh16, final int position) {
        final RetrieveSecretChatMessageItem message = mListData.get(position);


        if (message != null) {

            vh16.date.setText(findOverlayDate(message.getMessageDateOverlay()) + " ");


            vh16.time.setText(convert24to12hourformat(message.getTS()) + " ");

            try {

                Glide.with(mContext)
                        .load(message.getStickerUrl())

                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(vh16.imageView);


            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            } catch (NullPointerException n) {
                n.printStackTrace();

                vh16.relative_layout_message.setVisibility(View.GONE);

            }


        }
    }

    private void configureViewHolderServerMessage(ViewHolderServerMessage vh14, int position) {
        final RetrieveSecretChatMessageItem message = mListData.get(position);
        if (message != null) {
            try {
                if (message.getTextMessage().contains("created") || vh14.getAdapterPosition() == 0) {
                    vh14.gap.setVisibility(View.VISIBLE);
                } else {
                    vh14.gap.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            vh14.serverupdate.setText(message.getTextMessage());
        }
    }


    /*
     * Since image size for the doodle is hardcoded as 150dp X 150dp
     */
    @SuppressWarnings("TryWithIdenticalCatches,unchecked")


    private void configureViewHolderDoodleReceived(final ViewHolderDoodleReceived vh2, final int position) {
        final RetrieveSecretChatMessageItem message = mListData.get(position);
        if (message != null) {


            vh2.fnf.setVisibility(View.GONE);


            vh2.time.setText(convert24to12hourformat(message.getTS()) + " ");
            vh2.date.setText(findOverlayDate(message.getMessageDateOverlay()) + " ");


            vh2.cancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {


                    Call<ResponseBody> call = (Call<ResponseBody>) map.get(message.getMessageId());


                    if (call != null)
                        call.cancel();

                }
            });


            try {


                if (message.getDownloadStatus() == 1) {

/*
 *
 * doodle already downloaded
 *
 * */
                    vh2.progressBar2.setVisibility(View.GONE);

                    vh2.progressBar.setVisibility(View.GONE);
                    vh2.download.setVisibility(View.GONE);
                    vh2.cancel.setVisibility(View.GONE);

                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {


                        try {


                            try {
                                Glide
                                        .with(mContext)
                                        .load(message.getImagePath())

                                        .crossFade()


                                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                        .placeholder(R.drawable.home_grid_view_image_icon)
                                        .listener(new RequestListener<String, GlideDrawable>() {
                                            @Override
                                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                                vh2.imageView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));
                                                return false;
                                            }
                                        })
                                        .into(vh2.imageView);
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }


                            vh2.imageView.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {


                                    Intent i = new Intent(mContext, MediaHistory_FullScreenImage.class);

                                    i.putExtra("imagePath", message.getImagePath());


                                    ActivityOptionsCompat options = ActivityOptionsCompat.
                                            makeSceneTransitionAnimation((Activity) mContext, vh2.imageView, "image");
                                    mContext.startActivity(i, options.toBundle());


                                }


                            });


                        } catch (Exception e) {

                            Glide.clear(vh2.imageView);
                            vh2.imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.chat_white_circle));
                            vh2.imageView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));
                            vh2.fnf.setVisibility(View.VISIBLE);
                        }

                    } else {
                        Glide.clear(vh2.imageView);

                        vh2.imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.chat_white_circle));
                        vh2.imageView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));
                        vh2.fnf.setVisibility(View.VISIBLE);


                        vh2.fnf.setText(R.string.PermissionDenied);


                        vh2.imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                                intent.setData(uri);
                                mContext.startActivity(intent);
                            }
                        });

                    }


                } else {


                    if (message.isDownloading()) {


                        vh2.cancel.setVisibility(View.VISIBLE);


                        vh2.download.setVisibility(View.GONE);


                        vh2.progressBar2.setVisibility(View.VISIBLE);

                        vh2.progressBar.setVisibility(View.GONE);


                    } else {
                        vh2.download.setVisibility(View.VISIBLE);

                        vh2.progressBar2.setVisibility(View.GONE);
                        vh2.progressBar.setVisibility(View.GONE);
                        vh2.cancel.setVisibility(View.GONE);
                    }


                    try {
                        Glide
                                .with(mContext)
                                .load(message.getThumbnailPath())


                                .bitmapTransform(new CenterCrop(mContext), new BlurTransformation(mContext))


                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)


                                .placeholder(R.drawable.home_grid_view_image_icon)
                                .listener(new RequestListener<String, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                        vh2.imageView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));
                                        return false;
                                    }
                                })


                                .into(vh2.imageView);

                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                    vh2.imageView.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {

                            if (!message.isDownloading()) {
                                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        == PackageManager.PERMISSION_GRANTED) {


                                    AlertDialog.Builder builder =
                                            new AlertDialog.Builder(mContext, 0);
                                    builder.setTitle(R.string.DownloadConfirmation);
                                    builder.setMessage(mContext.getString(R.string.Download) + " " + message.getSize() + " " + mContext.getString(R.string.SizeImage));
                                    builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {


                                            String receiverUid = message.getReceiverUid();

                                            String messageId = message.getMessageId();


                                            message.setDownloading(true);


                                            ((ChatMessageScreen) mContext).runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    //notifyItemChanged(viewHolder.getAdapterPosition());

                                                    notifyDataSetChanged();
                                                }
                                            });

                                            download(message.getImagePath(), message.getThumbnailPath(), Environment.getExternalStorageDirectory().getPath()
                                                    + ApiOnServer.CHAT_DOWNLOADS_FOLDER + receiverUid + messageId + ".jpg", AppController.getInstance().findDocumentIdOfReceiver(receiverUid, secretId), message, vh2);


                                            // dialog.dismiss();

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
                                } else {


                                    /*
                                     *
                                     * have to request permission
                                     *
                                     * */


                                    requestStorageAccessPermission("image");

                                }
                            } else {


                                Snackbar snackbar = Snackbar.make(root, R.string.AlreadyDownloading, Snackbar.LENGTH_SHORT);


                                snackbar.show();
                                View view = snackbar.getView();
                                TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                                txtv.setGravity(Gravity.CENTER_HORIZONTAL);

                            }

                        }
                    });
                }


            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }


        }
    }


    @SuppressWarnings("TryWithIdenticalCatches,unchecked")

    private void configureViewHolderDoodleSent(final ViewHolderDoodleSentRetrieve vh2, final int position) {
        final RetrieveSecretChatMessageItem message = mListData.get(position);
        if (message != null) {


            vh2.fnf.setVisibility(View.GONE);

            vh2.date.setText(findOverlayDate(message.getMessageDateOverlay()) + " ");

            vh2.time.setText(convert24to12hourformat(message.getTS()) + " ");
            vh2.cancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {


                    Call<ResponseBody> call = (Call<ResponseBody>) map.get(message.getMessageId());


                    if (call != null)
                        call.cancel();

                }
            });


            if (message.getDownloadStatus() == 1) {


                /*
 *
 * doodle already downloaded
 *
 * */
                vh2.progressBar2.setVisibility(View.GONE);

                vh2.progressBar.setVisibility(View.GONE);
                vh2.download.setVisibility(View.GONE);
                vh2.cancel.setVisibility(View.GONE);

                if (message.getImagePath() != null) {


                    try {


                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_GRANTED) {


                            try {
                                Glide
                                        .with(mContext)
                                        .load(message.getImagePath())

                                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                                .crossFade()
                                        .centerCrop()
                                        .placeholder(R.drawable.home_grid_view_image_icon)


                                        .listener(new RequestListener<String, GlideDrawable>() {
                                            @Override
                                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                                vh2.imageView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));
                                                return false;
                                            }
                                        })
                                        .into(vh2.imageView);

                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }

                            vh2.imageView.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {


                                    Intent i = new Intent(mContext, MediaHistory_FullScreenImage.class);

                                    i.putExtra("imagePath", message.getImagePath());


                                    ActivityOptionsCompat options = ActivityOptionsCompat.
                                            makeSceneTransitionAnimation((Activity) mContext, vh2.imageView, "image");
                                    mContext.startActivity(i, options.toBundle());


                                }
                            });
                        } else {

                            vh2.fnf.setVisibility(View.VISIBLE);


                            vh2.fnf.setText(R.string.PermissionDenied);
                            Glide.clear(vh2.imageView);
                            vh2.imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.chat_white_circle));
                            vh2.imageView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));


                            vh2.imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                                    intent.setData(uri);
                                    mContext.startActivity(intent);
                                }
                            });

                        }


                    } catch (OutOfMemoryError e) {
                        e.printStackTrace();
                    } catch (Exception e) {


                        vh2.fnf.setVisibility(View.VISIBLE);
                        Glide.clear(vh2.imageView);
                        vh2.imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.chat_white_circle));
                        vh2.imageView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));


                    }
                } else {


                    vh2.imageView.setImageURI(message.getImageUrl());
                }
            } else {


                if (message.isDownloading()) {


                    vh2.cancel.setVisibility(View.VISIBLE);


                    vh2.download.setVisibility(View.GONE);


                    vh2.progressBar2.setVisibility(View.VISIBLE);

                    vh2.progressBar.setVisibility(View.GONE);


                } else {
                    vh2.download.setVisibility(View.VISIBLE);

                    vh2.progressBar2.setVisibility(View.GONE);
                    vh2.progressBar.setVisibility(View.GONE);
                    vh2.cancel.setVisibility(View.GONE);
                }


                try {
                    Glide
                            .with(mContext)
                            .load(message.getThumbnailPath())


                            .bitmapTransform(new CenterCrop(mContext), new BlurTransformation(mContext))


                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)


                            .placeholder(R.drawable.home_grid_view_image_icon)
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    vh2.imageView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));
                                    return false;
                                }
                            })


                            .into(vh2.imageView);

                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                vh2.imageView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        if (!message.isDownloading()) {
                            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    == PackageManager.PERMISSION_GRANTED) {


                                AlertDialog.Builder builder =
                                        new AlertDialog.Builder(mContext, 0);
                                builder.setTitle(R.string.DownloadConfirmation);
                                builder.setMessage(mContext.getString(R.string.Download) + " " + message.getSize() + " " + mContext.getString(R.string.SizeImage));
                                builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {


                                        String receiverUid = message.getReceiverUid();

                                        String messageId = message.getMessageId();


                                        message.setDownloading(true);


                                        ((ChatMessageScreen) mContext).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //notifyItemChanged(viewHolder.getAdapterPosition());

                                                notifyDataSetChanged();
                                            }
                                        });

                                        download(message.getImagePath(), message.getThumbnailPath(), Environment.getExternalStorageDirectory().getPath()
                                                + ApiOnServer.CHAT_DOWNLOADS_FOLDER + receiverUid + messageId + ".jpg", AppController.getInstance().findDocumentIdOfReceiver(receiverUid, secretId), message, vh2);


                                        // dialog.dismiss();

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
                            } else {


                                    /*
                                     *
                                     * have to request permission
                                     *
                                     * */


                                requestStorageAccessPermission("image");

                            }
                        } else {


                            Snackbar snackbar = Snackbar.make(root, R.string.AlreadyDownloading, Snackbar.LENGTH_SHORT);


                            snackbar.show();
                            View view = snackbar.getView();
                            TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                            txtv.setGravity(Gravity.CENTER_HORIZONTAL);

                        }

                    }
                });


            }

            String status = message.getDeliveryStatus();

            if (status.equals("3")) {

                vh2.clock.setVisibility(View.GONE);
                vh2.singleTick.setVisibility(View.GONE);

                vh2.doubleTickGreen.setVisibility(View.GONE);
                vh2.doubleTickBlue.setVisibility(View.VISIBLE);


            } else if (status.equals("2")) {
                vh2.clock.setVisibility(View.GONE);
                vh2.singleTick.setVisibility(View.GONE);

                vh2.doubleTickGreen.setVisibility(View.VISIBLE);
                vh2.doubleTickBlue.setVisibility(View.GONE);
            } else if (status.equals("1")) {

                vh2.clock.setVisibility(View.GONE);
                vh2.singleTick.setVisibility(View.VISIBLE);

                vh2.doubleTickGreen.setVisibility(View.GONE);
                vh2.doubleTickBlue.setVisibility(View.GONE);
            } else {


                vh2.clock.setVisibility(View.VISIBLE);
                vh2.singleTick.setVisibility(View.GONE);

                vh2.doubleTickGreen.setVisibility(View.GONE);
                vh2.doubleTickBlue.setVisibility(View.GONE);
            }


        }
    }

    /**
     * although position of item is not used as of now,but we might use it in the future
     */
    private void configureViewHolderLoading(final ViewHolderLoading vh, int position)
    {
        vh.slack.start();
    }


    /*
     *Making offer. */
    private void makeCounterOffer(String counterPrice)
    {
        if(chatMessageScreen.receiverUid!=null&&!chatMessageScreen.receiverUid.isEmpty()&&!chatMessageScreen.receiverUid.equals("null"))
        {
            Intent intent = new Intent(mContext,CounterPricePage.class);
            Bundle bundle=new Bundle();
            bundle.putString("profilePicUrl",chatMessageScreen.producImage);
            bundle.putString("productName",chatMessageScreen.productName);
            bundle.putString("membername",chatMessageScreen.receiverName);
            bundle.putString("negotiable",chatMessageScreen.isNegotiable);
            bundle.putString("place","");
            bundle.putString("postId",chatMessageScreen.secretId);
            bundle.putString("latitude","13");
            bundle.putString("longitude","77");
            bundle.putString("currency",chatMessageScreen.currencySysmbol);
            bundle.putString("receiverMqttId",chatMessageScreen.receiverUid);
            bundle.putString("price",chatMessageScreen.productPrice);
            bundle.putString("counterPrice",counterPrice);
            bundle.putBoolean("isSeller",chatMessageScreen.isSeller);
            intent.putExtras(bundle);
            mContext.startActivity(intent);
        }else
        {
            Toast.makeText(mContext,"Member id can,t be null",Toast.LENGTH_SHORT).show();
        }
    }

    /*
     *Make offer api */
    private  JSONObject requestDats;
    private void offerAccepted(String priceValue)
    {
        String digivalue;
        if(!priceValue.isEmpty())
        {
            priceValue = priceValue.replaceAll("[^a-zA-Z0-9 .,]|(?<!\\d)[.,]|[.,](?!\\d)", "");
           // priceValue = priceValue.replaceAll("\\D+","");
            digivalue=priceValue.trim();
            if(digivalue.length()<1)
            {
                return;
            }
        }else
        {
            return;
        }
        if (CommonClass.isNetworkAvailable(mContext))
        {
             requestDats = new JSONObject();
            try {
                requestDats.put("token",chatMessageScreen.sessionManager.getAuthToken());
                requestDats.put("offerStatus","2");
                requestDats.put("postId",chatMessageScreen.secretId);
                requestDats.put("price",digivalue);
                requestDats.put("type","0");
                if(chatMessageScreen.isSeller)
                {
                    requestDats.put("membername",chatMessageScreen.sessionManager.getUserName());
                    requestDats.put("buyer",chatMessageScreen.receiverName);
                }else
                {
                    requestDats.put("membername",chatMessageScreen.receiverName);
                }
                requestDats.put("sendchat",createMessageObject(digivalue));
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            OkHttp3Connection.doOkHttp3Connection("", ApiUrl.MAKE_OFFER,OkHttp3Connection.Request_type.POST, requestDats, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag)
                {
                    try {
                        JSONObject response=new JSONObject(result);
                       String code=response.getString("code");
                        if(code.equals("200"))
                        {
                          AppController.getInstance().sendMessageToFcm(MqttEvents. OfferMessage+"/"+requestDats.getJSONObject("sendchat").getString("to"),requestDats.getJSONObject("sendchat"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onError(String error, String user_tag)
                {}
            });
        }
    }

    /*
     * Creatign the offer accepted details*/
    private JSONObject createMessageObject(String amount) throws Exception
    {
        byte[] byteArray = amount.getBytes("UTF-8");
        String messageInbase64 = Base64.encodeToString(byteArray, Base64.DEFAULT).trim();
        String tsForServer = Utilities.tsInGmt();
        String  tsForServerEpoch = new Utilities().gmtToEpoch(tsForServer);
        JSONObject messageData=new JSONObject();
        messageData.put("name",chatMessageScreen.sessionManager.getUserName());
        messageData.put("from",chatMessageScreen.sessionManager.getmqttId());
        messageData.put("to",chatMessageScreen.receiverUid);
        messageData.put("payload",messageInbase64 );
        messageData.put("type","15");
        messageData.put("offerType","2");
        messageData.put( "id",tsForServerEpoch);
        messageData.put("isSold","1");
        messageData.put("secretId",chatMessageScreen.secretId);
        messageData.put("thumbnail","");
        messageData.put("userImage",chatMessageScreen.sessionManager.getUserImage());
        messageData.put("toDocId",chatMessageScreen.documentId);
        messageData.put("dataSize",1);
        messageData.put("productImage",chatMessageScreen.producImage);
        messageData.put("productId",chatMessageScreen.secretId);
        messageData.put("productName",chatMessageScreen.productName);
        messageData.put("productPrice",""+chatMessageScreen.productPrice);
        return messageData;
    }

    /*
     *Opening the paypal LINk */
    private void openPayAplLink(String url)
    {
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://" + url;
        try {
            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            mContext.startActivity(myIntent);
        } catch (ActivityNotFoundException e)
        {
            e.printStackTrace();
        }
    }
}
