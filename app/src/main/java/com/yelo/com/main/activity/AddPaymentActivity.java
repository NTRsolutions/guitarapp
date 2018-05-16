package com.yelo.com.main.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.ScrollingTabContainerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.yelo.com.R;

import com.yelo.com.pojo_class.CitiesPojo;
import com.yelo.com.pojo_class.CountryPojo;
import com.yelo.com.pojo_class.StatePojo;
import com.yelo.com.utility.ApiUrl;
import com.yelo.com.utility.CommonClass;
import com.yelo.com.utility.OkHttp3Connection;
import com.yelo.com.utility.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

/**
 * <h>LandingActivity</h>
 * <p>
 *     This class is called when user is not logged in. In this screen
 *     we can do login with facebook or google. And we have option for
 *     login or signup.
 * </p>
 * @since 13-May-17
 */
public class AddPaymentActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = AddPaymentActivity.class.getSimpleName();

    RelativeLayout mRlPayment;
    private SessionManager mSessionManager;


    EditText mEdFirstName, mEdLastname, mEdSocialNumber, mEdBusinessType, mEdTxId, mEdPhone, mEdAddress,
            mEdCity, mEdState, mEdPostalCode, mEdEmail, mEdAccountHolderName, mEdRoutingNumber,mEdAccountNumber, mEdAccntConfirm;

    AppCompatSpinner mCountrySpinner, mStateSpinner, mCitySpinner;
    TextView mEdDateOfBirth;
    private Calendar mcalendar;



    private int day,month,year;

    android.widget.ArrayAdapter<String> arrayAdapterCountry;
    ArrayAdapter<String> arrayAdapterState;
    ArrayAdapter<String> arrayAdapterCity;
    String[] arrayCountry = null;
    List<CountryPojo> listCountry;
    List<StatePojo> listState;
    List<CitiesPojo> listCity;

    String mCountryValue = "country";

    String[] arrayState = null;
    String[] arrayCity = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment);
        overridePendingTransition(R.anim.slide_up, R.anim.stay );

        mSessionManager=new SessionManager(this);

        mcalendar=Calendar.getInstance();
        day=mcalendar.get(Calendar.DAY_OF_MONTH);
        year=mcalendar.get(Calendar.YEAR);
        month=mcalendar.get(Calendar.MONTH);

        initVariables();

    }

    /**
     * In this method we used to initialize the data member and xml variables.
     */


    private  void DateDialog(){
        DatePickerDialog.OnDateSetListener listener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                int age= getAge( year, monthOfYear, dayOfMonth );
                if(age <=13){
                    Toast.makeText( AddPaymentActivity.this, "Please enter age greater than 13", Toast.LENGTH_SHORT ).show();
                }else {

                    mEdDateOfBirth.setText( dayOfMonth + "/" + monthOfYear + "/" + year );
                }
            }};
        DatePickerDialog dpDialog=new DatePickerDialog(this, listener, year, month, day);
        dpDialog.show();
    }


    private void initVariables(){

        try {
            mEdDateOfBirth = findViewById( R.id.eT_date_of_birth );

            mEdDateOfBirth.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DateDialog();
                }
            } );

            mEdFirstName = findViewById( R.id.eT_Name );
            mEdLastname = findViewById( R.id.eT_last_name );
            mEdSocialNumber = findViewById( R.id.eT_re_ssn_number );
            mEdTxId = findViewById( R.id.eT_tx_id );
            mEdBusinessType = findViewById( R.id.eT_business );
            mEdPhone = findViewById( R.id.eT_phone_number );
//            mEdCity = findViewById( R.id.eT_city );
//            mEdState = findViewById( R.id.eT_state );
            mEdAddress = findViewById( R.id.eT_address );

            mCountrySpinner = findViewById( R.id.et_Country );
            mStateSpinner = findViewById( R.id.et_state );
            mCitySpinner = findViewById( R.id.et_city );

            mEdPostalCode = findViewById( R.id.eT_postal_code );
            mEdEmail = findViewById( R.id.eT_email );
            mEdAccountHolderName = findViewById( R.id.eT_account_holder_name );
            mEdRoutingNumber = findViewById( R.id.eT_routingNumber );
            mEdAccountNumber = findViewById( R.id.eT_account_number );
            mEdAccntConfirm = findViewById( R.id.eT_re_account_number );



            mRlPayment =findViewById(R.id.rL_do_submit);
            mRlPayment.setOnClickListener( this );

            arrayCountry = new String[getCountries(this).size()];

            if(listCountry == null){
                listCountry = new ArrayList<>();
            }else {
                listCountry.clear();
            }

            listCountry =getCountries(this);

            for (int i = 0 ; i < listCountry.size(); i++){
                arrayCountry[i]= listCountry.get(i).getName();
            }


            arrayAdapterCountry = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_dropdown_item,arrayCountry);


            mCountrySpinner.setAdapter( arrayAdapterCountry );


            mCountrySpinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                    if(position > 0){

                        mCountryValue = arrayCountry[position];
                        mStateSpinner.setAdapter(null);
                        mCitySpinner.setAdapter(null);

                        if(listState == null){
                            listState = new ArrayList<StatePojo>();
                        }else {
                            listState.clear();
                        }

                        listState = getJSonStates(AddPaymentActivity.this,listCountry.get(position).getId());

                        arrayState = new String[listState.size()];
                        Arrays.fill( arrayState, null );
                        for(int i = 0 ; i < listState.size(); i++  ){

                            arrayState[i] = listState.get(i).getName();

                        }

                        arrayAdapterState = new ArrayAdapter<String>(AddPaymentActivity.this,
                                android.R.layout.simple_spinner_dropdown_item, arrayState);

                        mStateSpinner.setAdapter(arrayAdapterState);
//                        mSpinnerCity.setAdapter(arrayAdapterCity);
                    }


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            } );


            mStateSpinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position > 0){
                        if(listCity == null){
                            listCity = new ArrayList<CitiesPojo>();
                        }else {
                            listCity.clear();
                        }
                        listCity = getJSonCities(AddPaymentActivity.this,listState.get(position).getId());
                        Log.i("listState size ==",listState.size()+"");
                        arrayCity = new String[listCity.size()];
                        Arrays.fill( arrayCity, null );
                        for(int i = 0 ; i < listCity.size(); i++  ){

                            arrayCity[i] = listCity.get(i).getName();

                        }
                        Log.i("listState size ==",arrayState.length+"");
                        arrayAdapterCity = new ArrayAdapter<String>(AddPaymentActivity.this,
                                android.R.layout.simple_spinner_dropdown_item, arrayCity);

//
                        mCitySpinner.setAdapter(arrayAdapterCity);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            } );


        }catch (Exception e){
            e.printStackTrace();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.rL_do_submit:
                if(validation()){

                    submitData(  );

                }

                break;

        }
    }

    private boolean validation(){

        if(isvalidEditText( mEdFirstName )){

            Toast.makeText( this, "Please enter first name.", Toast.LENGTH_SHORT ).show();
            return false;
        }else  if(isvalidEditText( mEdLastname )){

            Toast.makeText( this, "Please enter last name.", Toast.LENGTH_SHORT ).show();
            return false;
        }else  if(mEdDateOfBirth.getText().toString().equalsIgnoreCase( "dd-mm-yyyy" )){

            Toast.makeText( this, "Please select date of birth", Toast.LENGTH_SHORT ).show();
            return false;
        } else  if(isvalidEditText( mEdSocialNumber )){

            Toast.makeText( this, "Please enter Social number.", Toast.LENGTH_SHORT ).show();
            return false;
        }else  if(mEdSocialNumber.getText().toString().trim().length() !=4  ){

            Toast.makeText( this, "Please enter Social number of only four digit", Toast.LENGTH_SHORT ).show();
            return false;
        }

        else  if(isvalidEditText( mEdBusinessType )){

            Toast.makeText( this, "Please enter business type.", Toast.LENGTH_SHORT ).show();
            return false;
        }else  if(isvalidEditText( mEdTxId )){

            Toast.makeText( this, "Please enter tax id.", Toast.LENGTH_SHORT ).show();
            return false;
        }else  if(isvalidEditText( mEdPhone )){

            Toast.makeText( this, "Please enter phone number.", Toast.LENGTH_SHORT ).show();
        }
//        else  if(isvalidEditText( mEdCity )){
//
//            Toast.makeText( this, "Please enter city.", Toast.LENGTH_SHORT ).show();
//            return false;
//        }else  if(isvalidEditText( mEdState )){
//
//            Toast.makeText( this, "Please enter state", Toast.LENGTH_SHORT ).show();
//            return false;
//        }


        else if(mCountryValue.equalsIgnoreCase( "--Country--" ) ){

            Toast.makeText( this, "Please select country.", Toast.LENGTH_SHORT ).show();
            return false;
        }else
          if(isvalidEditText( mEdPostalCode )){

            Toast.makeText( this, "Please enter postal code.", Toast.LENGTH_SHORT ).show();
              return false;
        }else  if(isvalidEditText( mEdEmail )){

            Toast.makeText( this, "Please enter email", Toast.LENGTH_SHORT ).show();
              return false;
        }else  if(!isValidEmailId( mEdEmail.getText().toString().trim() )){

            Toast.makeText( this, "Please enter valid email.", Toast.LENGTH_SHORT ).show();
              return false;
        }

        else  if(isvalidEditText( mEdAccountHolderName )){

            Toast.makeText( this, "Please enter account holder name.", Toast.LENGTH_SHORT ).show();
              return false;
        }else  if(isvalidEditText( mEdRoutingNumber )){

            Toast.makeText( this, "Please enter rounting number.", Toast.LENGTH_SHORT ).show();
              return false;
        }else  if(isvalidEditText( mEdAccountNumber )){

            Toast.makeText( this, "Please enter account number.", Toast.LENGTH_SHORT ).show();
              return false;
        }else  if(isvalidEditText( mEdAccntConfirm )){

            Toast.makeText( this, "Please enter confirm account number.", Toast.LENGTH_SHORT ).show();
              return false;
        }else  if(isValidPassword( mEdAccntConfirm.getText().toString().trim(), mEdAccountNumber.getText().toString().trim() )){

            Toast.makeText( this, "Account number and confirm account not matches", Toast.LENGTH_SHORT ).show();
              return false;
        }


        return true;
    }


    ProgressDialog pDialog;
    private void submitData(){

        try {
            pDialog = new ProgressDialog(this,0);


            pDialog.setCancelable(false);

//        pDialog.setTitle(R.string.string_351);

            pDialog.setMessage("Please wait...");
            pDialog.show();

            paymentApi(  );

        }catch (Exception e){
            e.printStackTrace();
            pDialog.dismiss();
        }

    }


    private void paymentApi()
    {
        if (CommonClass.isNetworkAvailable(this))
        {
            JSONObject request_datas = new JSONObject();
            try {

                request_datas.put("First_name",getText(mEdFirstName));
                request_datas.put("Last_name",getText(mEdLastname));
                request_datas.put("Date_of_birth",mEdDateOfBirth.getText().toString().trim());
                request_datas.put("SSN",getText(mEdSocialNumber));
                request_datas.put("business", "NA");
                request_datas.put("type", "individual");
                request_datas.put("Tax_ID", getText(mEdTxId));
                request_datas.put("Phone_number", getText(mEdPhone));
                request_datas.put("Country","US");
                request_datas.put("address_line_1", getText(mEdAddress));
                request_datas.put("address_line_2", "");
                request_datas.put("city", "adger");
                request_datas.put("state", "alabama");
                request_datas.put("Email", getText(mEdEmail));
                request_datas.put("Default_currency", "usd");
                request_datas.put("account_holder_name",  getText(mEdAccountHolderName));
                request_datas.put("postal_code",  "123456");
                request_datas.put("routing_number",  getText(mEdRoutingNumber));
                request_datas.put("account_number",  getText(mEdAccountNumber));
                request_datas.put("user_name", mSessionManager.getUserName());

                Log.i( "request data", request_datas.toString() );
//
            }
            catch (JSONException e)
            {
                e.printStackTrace();
                pDialog.dismiss();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.PAYMENT_SETTING, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
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
                    Toast.makeText( AddPaymentActivity.this, error.toString(), Toast.LENGTH_SHORT ).show();
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

                Toast.makeText( this, "Account added successfuly", Toast.LENGTH_SHORT ).show();

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



    private boolean isvalidEditText(EditText editText)
    {
        if(editText.getText().toString().trim().isEmpty()) {
            editText.requestFocus();
            return true;
        }
        return false;
    }


    private String getText(EditText editText)
    {
        return editText.getText().toString().trim();
    }

    private boolean isValidEmailId(String email)
    {
        return   Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    private boolean isValidPassword(String pass,String confirm_pass)
    {
        if(pass.equals(confirm_pass))
            return false;
        return true;
    }


    public static java.util.List<com.yelo.com.pojo_class.CountryPojo> getCountries(Context context){

        List<CountryPojo> list = null;
        try {
            list = new ArrayList<>();

            JSONObject obj = new JSONObject(loadJSONFromAsset(context, "countries.json"));
            JSONArray m_jArry = obj.getJSONArray("countries");

            for (int i = 0; i < m_jArry.length(); i++) {
                CountryPojo pojo = new CountryPojo();
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                pojo.setId(jo_inside.optString("id"));
                pojo.setName(jo_inside.optString("name"));



                list.add(pojo);
            }

        }catch (Exception e){
            e.getStackTrace();
        }


        return list;
    }

    public static List<StatePojo> getJSonStates(Context context, String id){

        List <StatePojo> list = null;
        try {
            list = new ArrayList<>();

            JSONObject obj = new JSONObject(loadJSONFromAsset(context, "states.json"));
            JSONArray m_jArry = obj.getJSONArray("states");

            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                if(i == 0){
                    StatePojo pojo = new StatePojo();
                    pojo.setId(jo_inside.optString("id"));
                    pojo.setName(jo_inside.optString("name"));
                    pojo.setCountry_id(jo_inside.optString("country_id"));

                    Log.i("country_id",jo_inside.optString("country_id"));


                    list.add(pojo);
                }else if(id.equalsIgnoreCase(jo_inside.optString("country_id"))) {
                    StatePojo pojo = new StatePojo();
                    pojo.setId(jo_inside.optString("id"));
                    pojo.setName(jo_inside.optString("name"));
                    pojo.setCountry_id(jo_inside.optString("country_id"));

                    Log.i("country_id",jo_inside.optString("country_id"));


                    list.add(pojo);
                }
            }

        }catch (Exception e){
            e.getStackTrace();
        }


        return list;
    }

    public static String   loadJSONFromAsset(android.content.Context context,String filename) {
        String json = null;
        try {
            java.io.InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


    public static List<CitiesPojo> getJSonCities(Context context, String id){

        List <CitiesPojo> list = null;
        try {
            list = new ArrayList<>();

            JSONObject obj = new JSONObject(loadJSONFromAsset(context, "cities.json"));
            JSONArray m_jArry = obj.getJSONArray("cities");

            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);

                if(i == 0){
                    CitiesPojo pojo = new CitiesPojo();
                    pojo.setId(jo_inside.optString("id"));
                    pojo.setName(jo_inside.optString("name"));
                    pojo.setState_id(jo_inside.optString("state_id"));

                    list.add(pojo);
                }

                if(id.equalsIgnoreCase(jo_inside.optString("state_id"))) {
                    CitiesPojo pojo = new CitiesPojo();
                    pojo.setId(jo_inside.optString("id"));
                    pojo.setName(jo_inside.optString("name"));
                    pojo.setState_id(jo_inside.optString("state_id"));


                    list.add(pojo);
                }
            }

        }catch (Exception e){
            e.getStackTrace();
        }


        return list;
    }


    private int getAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);


        return ageInt;
    }
}
