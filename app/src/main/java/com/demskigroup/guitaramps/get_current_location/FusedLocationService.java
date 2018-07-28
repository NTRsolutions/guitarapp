package com.demskigroup.guitaramps.get_current_location;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.demskigroup.guitaramps.utility.ProgressBarHandler;
import com.demskigroup.guitaramps.utility.VariableConstants;
import static com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import static com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

/**
 * <h>FusedLocationService</h>
 * <p>
 *     In This class we used to do Google fused Location api call to
 *     receive the current latitude and longitude.
 * </p>
 * @since 25/7/16
 */
public class FusedLocationService implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
    private static final String TAG = FusedLocationService.class.getSimpleName();
    private Activity activity;
    private GoogleApiClient mGoogleApiClient;
    private ProgressBarHandler mProgressDialog;
    private static final float MINIMUM_ACCURACY = 50.0f;

    private FusedLocationReceiver locationReceiver;
    private Location getLocation;
    private FusedLocationProviderApi fusedLocationProviderApi=LocationServices.FusedLocationApi;
    //private TextView txtOutputLat,txtOutputLon;

    public FusedLocationService(Activity activity, FusedLocationReceiver locationReceiver) {
        this.activity = activity;
        this.locationReceiver = locationReceiver;
        mProgressDialog = new ProgressBarHandler(activity);
        buildGoogleApiClient();

        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();

        settingRequest();
    }

    ////////// Overriden methods of ConnectionCallbacks ///////////////
    @Override
    public void onConnected(Bundle bundle) {
        System.out.println("Callback=" + "onConnected");
        LocationRequest mLocationRequest;
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(100); // Update location every second

        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        Location mLastLocation= LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation!=null)
        {
            System.out.println(TAG+" "+"location onConnected="+mLastLocation.getLatitude()+" "+mLastLocation.getLongitude());
            getLocation=mLastLocation;
        }
    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    /////////////////// Overriden method of OnConnectionFailedListener /////////
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        buildGoogleApiClient();
    }

    ////////////// Overriden method of LocationListener ///////////////////////
    @Override
    public void onLocationChanged(Location location)
    {
        if (mProgressDialog!=null)
            mProgressDialog.hide();

        if (location!=null)
        {
            this.getLocation=location;
            locationReceiver.onUpdateLocation();
        }

        if (this.getLocation.getAccuracy()<MINIMUM_ACCURACY)
            fusedLocationProviderApi.removeLocationUpdates(mGoogleApiClient,this);
    }

    private synchronized void buildGoogleApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


    }

    private void settingRequest()
    {
        LocationRequest locationRequest;
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        // **************************
        builder.setAlwaysShow(true); // this is the key ingredient
        // **************************

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                .checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result)
            {
                final Status status = result.getStatus();
                switch (status.getStatusCode())
                {
                    case LocationSettingsStatusCodes.SUCCESS:

                        // Log.i(TAG,"LocationSettingsStatusCodes SUCCESS");
                        System.out.println("Pending result="+"SUCCESS");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be
                        // fixed by showing the user
                        // a dialog.
                        try
                        {
                            // Log.i(TAG,"LocationSettingsStatusCodes RESOLUTION_REQUIRED");
                            System.out.println("Pending result="+"RESOLUTION_REQUIRED");

                            // Show the dialog by calling
                            // startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(activity, VariableConstants.REQUEST_CHECK_SETTINGS);
                        }
                        catch (IntentSender.SendIntentException e)
                        {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                        System.out.println("Pending result="+"SETTINGS_CHANGE_UNAVAILABLE");

                        break;
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (data!=null)
        {
            switch (requestCode)
            {
                case VariableConstants.REQUEST_CHECK_SETTINGS :
                    switch (resultCode)
                    {
                        case Activity.RESULT_OK :
                            System.out.println("result ok..");
                            mProgressDialog.show();
                            break;
                        case Activity.RESULT_CANCELED :
                            System.out.println("result cancel..");
                            activity.finish();
                            break;
                    }
                    break;
            }
        }

    }

    ////////////// Get location ///////////////////
    public Location receiveLocation()
    {
        return getLocation;
    }
}
