package com.demskigroup.guitaramps.main.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.ClientTokenRequest;
import com.braintreegateway.Environment;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;
import com.demskigroup.guitaramps.BuildConfig;
import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.get_current_location.FusedLocationReceiver;
import com.demskigroup.guitaramps.get_current_location.FusedLocationService;
import com.demskigroup.guitaramps.main.LoginWithFacebook;
import com.demskigroup.guitaramps.main.activity.products.ProductDetailsActivity;
import com.demskigroup.guitaramps.mqttchat.Activities.ChatMessageScreen;
import com.demskigroup.guitaramps.mqttchat.AppController;
import com.demskigroup.guitaramps.mqttchat.Database.CouchDbController;
import com.demskigroup.guitaramps.mqttchat.Utilities.MqttEvents;
import com.demskigroup.guitaramps.pojo_class.LogDevicePojo;
import com.demskigroup.guitaramps.pojo_class.LoginResponsePojo;
import com.demskigroup.guitaramps.utility.ApiUrl;
import com.demskigroup.guitaramps.utility.CommonClass;
import com.demskigroup.guitaramps.utility.OkHttp3Connection;
import com.demskigroup.guitaramps.utility.RunTimePermission;
import com.demskigroup.guitaramps.utility.SessionManager;
import com.demskigroup.guitaramps.utility.VariableConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static java.security.AccessController.getContext;

/**
 * <h>LandingActivity</h>
 * <p>
 *     This class is called when user is not logged in. In this screen
 *     we can do login with facebook or google. And we have option for
 *     login or signup.
 * </p>
 * @since 13-May-17
 */
public class PaymentActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = PaymentActivity.class.getSimpleName();

    Button mRlPayment;
    private SessionManager mSessionManager;
    public static String KEY_PRICE = "key_price";
    Intent I = null;
    String mPriceValue;
    String postId;
    EditText mEdCardNumber, mEdExirationdate, mEdCvv;
    CheckBox mCheckBox;

    private static BraintreeGateway gateway = new BraintreeGateway(
            Environment.SANDBOX,
            "4rrgw4c38db356qz",
            "pyg9v323y57hc8n8",
            "c7ac4e27800f52999c8605a0a006c824"
    );
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_view);
        overridePendingTransition(R.anim.slide_up, R.anim.stay );

        mEdCardNumber = (EditText)findViewById(R.id.edit_card_number);
        mEdExirationdate = (EditText)findViewById(R.id.ed_expiry_date);
        mEdCvv =  (EditText)findViewById(R.id.edit_cvv);
        mCheckBox = findViewById( R.id.mCheckSavecard );



        mEdExirationdate.addTextChangedListener(new TextWatcher() {
            int len=0;
            @Override
            public void afterTextChanged(Editable s) {
                String str = mEdExirationdate.getText().toString();
                if(str.length()==4&& len <str.length()){//len check for backspace
                    mEdExirationdate.append("-");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                String str = mEdExirationdate.getText().toString();
                len = str.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }


        });

        mRlPayment =findViewById(R.id.rL_do_buy);
        mRlPayment.setOnClickListener( this );

        mSessionManager=new SessionManager(this);


        I = getIntent();
        if(I!= null){

            String[] arr = new String[2];
            arr = I.getStringExtra( KEY_PRICE ).split( "/" );
            mPriceValue =  arr[0];
            postId = arr[1];

            mRlPayment.setText( "Pay $"+mPriceValue );


        }
    }

    /**
     * In this method we used to initialize the data member and xml variables.
     */


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.rL_do_buy:

                if(CommonClass.isNetworkAvailable(this)) {

//                    generateClientToken();


                    if(validate()){
                        String[] array = new String[2];
                        array = mEdExirationdate.getText().toString().trim().split("-");
                        if(ValidateCard(mEdCardNumber.getText().toString().trim(),Integer.parseInt(array[1]),Integer.parseInt(array[0]),mEdCvv.getText().toString().trim(),this))
                        {

                            Card card = new Card(
                                    mEdCardNumber.getText().toString().trim(),
                                    Integer.parseInt(array[1]),
                                    Integer.parseInt(array[0]),
                                    mEdCvv.getText().toString().trim()
                            );

                            getStripeToken( card );


                        }


                    }
                }

                else {
                    Toast.makeText( this, "Please check your network", Toast.LENGTH_SHORT ).show();
                }

                break;

        }
    }


    public void onBraintreeSubmit(String token) {
        DropInRequest dropInRequest = new DropInRequest().collectDeviceData( true )
                .clientToken(token);

        startActivityForResult(dropInRequest.getIntent(this), 1001);
    }



    ProgressDialog pDialog;
    private void getStripeToken(Card card){

        try {
            pDialog = new ProgressDialog(this,0);


            pDialog.setCancelable(false);

//        pDialog.setTitle(R.string.string_351);

            pDialog.setMessage("Please wait payment is in process...");
            pDialog.show();

            Stripe stripe = new Stripe(this, "pk_test_nKU62kAjMq5ldNtw420NHgBg");
            stripe.createToken(
                    card,
                    new TokenCallback() {
                        public void onSuccess(Token token) {
                            // Send token to your server
                            paymentApi( token );
//                            Toast.makeText(PaymentActivity.this, "Token"+token.getId(), Toast.LENGTH_LONG ).show();
                        }
                        public void onError(Exception error) {
                            // Show localized error message
                            Toast.makeText(PaymentActivity.this, "Error", Toast.LENGTH_LONG ).show();
                        }
                    }
            );
        }catch (Exception e){
            e.printStackTrace();
            pDialog.dismiss();
        }

    }


    private void paymentApi(Token mToken)
    {
        if (CommonClass.isNetworkAvailable(this))
        {
            JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("amount",mPriceValue);
                request_datas.put("auth_token",mSessionManager.getAuthToken());
                request_datas.put("stripeToken",mToken.getId());
                request_datas.put("postId",postId);
                request_datas.put("user_name",mSessionManager.getUserName());
                request_datas.put("type", "0");
                Log.i( "request payment", request_datas.toString() );
//
            }
            catch (JSONException e)
            {
                e.printStackTrace();
                pDialog.dismiss();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.PAYMENT, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag)
                {
                    try
                    {

                        handelResponse(result);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    pDialog.dismiss();
                }

                @Override
                public void onError(String error, String user_tag) {
                    Toast.makeText( PaymentActivity.this, error.toString(), Toast.LENGTH_SHORT ).show();
                    pDialog.dismiss();
                }
            });
        }
//        else CommonClass.showSnackbarMessage(rL_rootview,getResources().getString(R.string.NoInternetAccess));
    }

    private void handelResponse(String resaponse) throws Exception
    {
        JSONObject jsonObject = new JSONObject(resaponse);
        String code_Data = jsonObject.getString("code");
        switch (code_Data) {
            // success
            case "200":
//                AppController.getInstance().sendMessageToFcm( MqttEvents. OfferMessage+"/"+requestDats.getJSONObject("sendchat").getString("to"),requestDats.getJSONObject("sendchat"));
//                Intent intent;

                Toast.makeText( this, jsonObject.getString( "message" ), Toast.LENGTH_SHORT ).show();

                finish();
                break;
            // auth token expired
            case "401":
                CommonClass.sessionExpired(this);
                break;
            case "409":
                Toast.makeText( this, jsonObject.optString( "message" ), Toast.LENGTH_SHORT ).show();
                break;
            // error
            default:
                Toast.makeText( this, jsonObject.optString( "message" ), Toast.LENGTH_SHORT ).show();
                break;
        }
    }


    public static boolean ValidateCard(String cardNumber, int cardExpMonth, int cardExpYear, String cardCVC, Context context) {
        Card card = new Card(
                cardNumber,
                cardExpMonth,
                cardExpYear,
                cardCVC
        );

        if( !card.validateNumber()){
            Toast.makeText(context, "Please enter valid card number", Toast.LENGTH_SHORT).show();
            return false;
        }

        if( !card.validateCVC()){
            Toast.makeText(context, "Please enter valid CVV", Toast.LENGTH_SHORT).show();
            return false;
        }

        if( !card.validateExpMonth()){
            Toast.makeText(context, "Please enter valid expiry date", Toast.LENGTH_SHORT).show();
            return false;
        }

        if( !card.validateExpiryDate()){
            Toast.makeText(context, "Card expiration date error", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    private boolean validate() {
        if(mEdCardNumber.getText().toString().trim().length() == 0){
            Toast.makeText( this, "Please enter card number", Toast.LENGTH_SHORT ).show();
            mEdCardNumber.requestFocus();
            return false;
        }
        if(mEdExirationdate.getText().toString().trim().length() == 0){
            Toast.makeText( this, "Please enter expiry date", Toast.LENGTH_SHORT ).show();

            mEdExirationdate.requestFocus();
            return false;
        }
        if(mEdCvv.getText().toString().trim().length() == 0){

            Toast.makeText( this, "Please enter cvv number", Toast.LENGTH_SHORT ).show();

            mEdCvv.requestFocus();
            return false;
        }

        return true;


    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == 1001) {
//            if (resultCode == Activity.RESULT_OK) {
//                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
//
//            String nonce =  result.getPaymentMethodNonce().getNonce();
//                // use the result to update your UI and send the payment method nonce to your server
//            } else if (resultCode == Activity.RESULT_CANCELED) {
//                // the user canceled
//            } else {
//                // handle errors here, an exception may be available in
//                Exception error = (Exception) data.getSerializableExtra( DropInActivity.EXTRA_ERROR);
//            }
//        }
//    }


    private void generateClientToken(){

        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

            StrictMode.setThreadPolicy(policy);
            ClientTokenRequest clientTokenRequest = new ClientTokenRequest();

            String clientToken = gateway.clientToken().generate(clientTokenRequest);
            Log.i( "token for braintree ", clientToken );
            onBraintreeSubmit(clientToken);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


}
