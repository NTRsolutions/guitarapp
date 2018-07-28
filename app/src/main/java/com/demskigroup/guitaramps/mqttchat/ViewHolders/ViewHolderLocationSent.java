package com.demskigroup.guitaramps.mqttchat.ViewHolders;
/*
 * Created by moda on 02/04/16.
 */

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.mqttchat.AppController;


/**
 * View holder for location sent recycler view item
 */
public class ViewHolderLocationSent extends RecyclerView.ViewHolder implements OnMapReadyCallback {

//    public TextView senderName;

    public TextView time, date;

    public ImageView singleTick, doubleTickGreen, doubleTickBlue, clock;//,blocked;


    public MapView mapView;


    public GoogleMap mMap;


    public String nameSelected = "";


    public LatLng positionSelected = new LatLng(0, 0);


    public ViewHolderLocationSent(View view) {
        super(view);
        date = (TextView) view.findViewById(R.id.date);

        //  senderName = (TextView) view.findViewById(R.id.lblMsgFrom);

//        blocked = (ImageView) view.findViewById(R.id.blocked);
        time = (TextView) view.findViewById(R.id.ts);

        singleTick = (ImageView) view.findViewById(R.id.single_tick_green);

        doubleTickGreen = (ImageView) view.findViewById(R.id.double_tick_green);

        doubleTickBlue = (ImageView) view.findViewById(R.id.double_tick_blue);

        clock = (ImageView) view.findViewById(R.id.clock);


        mapView = (MapView) view.findViewById(R.id.map);

        mapView.onCreate(null);
        mapView.onResume();
        mapView.getMapAsync(this);

        Typeface tf = AppController.getInstance().getRobotoCondensedFont();
        time.setTypeface(tf, Typeface.ITALIC);

        date.setTypeface(tf, Typeface.ITALIC);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        mMap.getUiSettings().setMapToolbarEnabled(true);

        mMap.addMarker(new MarkerOptions().position(positionSelected).title(nameSelected));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(positionSelected, 16.0f));


    }


}
