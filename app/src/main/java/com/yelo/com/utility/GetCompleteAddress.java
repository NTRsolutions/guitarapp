package com.yelo.com.utility;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yelo.com.R;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * <h>GetCompleteAddress</h>
 * <p>
 *     In this class we used to do google api call to get the complete address for
 *     the given lat and lng.
 * </p>
 * @since 22-Nov-17
 */
public class GetCompleteAddress
{
    private static final String TAG = GetCompleteAddress.class.getSimpleName();
    private TextView tV_current_location;
    private ProgressBar progress_bar_location;
    private String city,country;


    public GetCompleteAddress(Activity mActivity,String lat, String lng, TextView tV_current_location, ProgressBar progress_bar_location) {
        this.tV_current_location = tV_current_location;
        this.progress_bar_location = progress_bar_location;
        city=CommonClass.getCityName(mActivity,Double.parseDouble(lat),Double.parseDouble(lng));
        country=CommonClass.getCountryName(mActivity,Double.parseDouble(lat),Double.parseDouble(lng));

        String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=+"+lat+"+,+"+lng+"+&location_type=ROOFTOP&result_type=street_address&key="+mActivity.getResources().getString(R.string.google_map_api_key);
        // call google map api
        new RequestTask().execute(url);
    }

    private class RequestTask extends AsyncTask<String, String, String>
    {
        @Override
        protected String doInBackground(String... uri) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                response = httpclient.execute(new HttpGet(uri[0]));
                StatusLine statusLine = response.getStatusLine();
                if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    responseString = out.toString();
                    out.close();
                } else{
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
                //TODO Handle problems..
            } catch (IOException e) {
                e.printStackTrace();
                //TODO Handle problems..
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //Do anything with response..
            if (progress_bar_location!=null)
                progress_bar_location.setVisibility(View.GONE);
            tV_current_location.setVisibility(View.VISIBLE);

            System.out.println(TAG+" "+"address res="+result);
            if (result!=null)
            {
                try {
                    JSONObject reader = new JSONObject(result);
                    JSONArray sys  = reader.getJSONArray("results");
                    String formatted_address = sys.getJSONObject(0).getString("formatted_address");
                    System.out.println(TAG+" "+"formatted_address="+formatted_address);
                    if (formatted_address!=null && !formatted_address.isEmpty())
                        tV_current_location.setText(formatted_address);
                } catch (JSONException e) {
                    e.printStackTrace();

                    //..if location not detect from google api then set manually
                    String cityCaps=city.substring(0,1).toUpperCase()+""+city.substring(1);
                    String address=cityCaps+", "+country;

                    tV_current_location.setText(address);

                }
            }
        }
    }
}