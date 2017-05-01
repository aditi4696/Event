package com.apapps.event;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Aditi on 4/27/2017.
 */

public class ShowVenue extends FragmentActivity implements OnMapReadyCallback {

    String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_show_venue);
        Intent intent = getIntent();
        address = intent.getExtras().getString("address");
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Geocoder coder = new Geocoder(this, Locale.ENGLISH);
        List<Address> addressList = null;
        LatLng p1;


            try {
                addressList = coder.getFromLocationName(address, 1);
                if (coder.isPresent()) {
                    if (addressList.size() > 0) {
                        Address location = addressList.get(0);

                        p1 = new LatLng(location.getLatitude(), location.getLongitude() );
                        //p1 = new LatLng(-33.852, 151.211);
                        googleMap.addMarker(new MarkerOptions().position(p1).title("Marker"));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(p1,12));
                    } else {
                        Toast.makeText(this, "Can't load location", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(this, "Can't load location: getFromLocationName not executed.", Toast.LENGTH_SHORT).show();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
