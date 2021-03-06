package com.paulusworld.geofence;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements OnCameraChangeListener {


//     * Google Map object
    private GoogleMap mMap;

//     * Geofences Array
    ArrayList<Geofence> mGeofences;

//     * Geofence Coordinates
    ArrayList<LatLng> mGeofenceCoordinates;

//     * Geofence Radius'
    ArrayList<Integer> mGeofenceRadius;

//     * Geofence Store
    private GeofenceStore mGeofenceStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing variables
        mGeofences = new ArrayList<Geofence>();
        mGeofenceCoordinates = new ArrayList<LatLng>();
        mGeofenceRadius = new ArrayList<Integer>();

        // Adding geofence coordinates to array.
        mGeofenceCoordinates.add(new LatLng(2.9230733, 101.6613033)); //FingerTec R&D Centre
//        mGeofenceCoordinates.add(new LatLng(2.927269, 101.656859)); //TimeTec HQ Puchong

//        Marker marker = mMap.addMarker(new MarkerOptions()
//                .position(new LatLng(2.9230733, 101.6613033))
//                .title("San Francisco")
//                .snippet("Population: 776733"));

        // Adding associated geofence radius' to array.
        mGeofenceRadius.add(60);
//        mGeofenceRadius.add(50);
//        mGeofenceRadius.add(160);

        // Bulding the geofences and adding them to the geofence array.

        // FingerTec R&D Centre
        mGeofences.add(new Geofence.Builder()
                .setRequestId("FingerTec R&D Centre")
                        // The coordinates of the center of the geofence and the radius in meters.
                .setCircularRegion(mGeofenceCoordinates.get(0).latitude, mGeofenceCoordinates.get(0).longitude, mGeofenceRadius.get(0).intValue())
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                // Required when we use the transition type of GEOFENCE_TRANSITION_DWELL
                .setLoiteringDelay(30000) // (60000 = 1 minute Delay)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT).build());

//        // TimeTec HQ Puchong
//        mGeofences.add(new Geofence.Builder()
//                .setRequestId("TimeTec HQ Puchong")
//                        // The coordinates of the center of the geofence and the radius in meters.
//                .setCircularRegion(mGeofenceCoordinates.get(1).latitude, mGeofenceCoordinates.get(1).longitude, mGeofenceRadius.get(1).intValue())
//                .setExpirationDuration(Geofence.NEVER_EXPIRE)
//                        // Required when we use the transition type of GEOFENCE_TRANSITION_DWELL
//                .setLoiteringDelay(30000)
//                .setTransitionTypes(
//                        Geofence.GEOFENCE_TRANSITION_ENTER
//                                | Geofence.GEOFENCE_TRANSITION_DWELL
//                                | Geofence.GEOFENCE_TRANSITION_EXIT).build());


        // Add the geofences to the GeofenceStore.Java object.
        mGeofenceStore = new GeofenceStore(this, mGeofences);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
//        mGeofenceStore.disconnect();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            setUpMapIfNeeded();
        } else {
            GooglePlayServicesUtil.getErrorDialog(
                    GooglePlayServicesUtil.isGooglePlayServicesAvailable(this),
                    this, 0);
        }
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the
        // map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map)).getMap();

            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the
     * camera. In this case, we just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap}
     * is not null.
     */
    private void setUpMap() {
        // Centers the camera over the building and zooms int far enough to
        // show the floor picker.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(2.9230733, 101.6613033), 17));

        // Hide labels.
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setIndoorEnabled(false);

        //Add Permission :
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mMap.setMyLocationEnabled(true);

        mMap.setOnCameraChangeListener(this);

    }

    @Override
    public void onCameraChange(CameraPosition position) {
        // Makes sure the visuals remain when zoom changes.
        for(int i = 0; i < mGeofenceCoordinates.size(); i++) {
            mMap.addCircle(new CircleOptions().center(mGeofenceCoordinates.get(i))
                    .radius(mGeofenceRadius.get(i).intValue())
//                    .fillColor(0x40ff0000)
                    .strokeColor(Color.RED).strokeWidth(5));

        }
    }
}