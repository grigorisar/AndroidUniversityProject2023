package com.example.android2022;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.android2022.database.DbLocation;
import com.example.android2022.database.LocationContentProvider;
import com.example.android2022.utils.LocationService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.android2022.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient ;
    private ActivityMapsBinding binding;

    // Geofencing
    private int GEOFENCE_RADIUS = 100;
    private GeofencingClient geofencingClient;
    private List<LatLng> tempList = new ArrayList<LatLng>();
    private ArrayList newGeofenceList = new ArrayList();

    // Buttons
    private Button startButton;
    private Button cancelButton;
    private String sessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initializing clients
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        geofencingClient = LocationServices.getGeofencingClient(this);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Geofencing
        geofencingClient = LocationServices.getGeofencingClient(this);


        //Button Logic

        cancelButton = findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                cancelButtonLogic();
            }

        });

        startButton = findViewById(R.id.start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                startButtonLogic();
            }
        });
        // Long click listener
        //        View mapView = findViewById(R.id.map);
        //        mapView.setOnLongClickListener(new GoogleMap.OnMapLongClickListener());
        sessionId = "Session"+ (System.currentTimeMillis() / 1000L);


    }




    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //Location impl
        mMap.setMyLocationEnabled(true);
        mMap.setMinZoomPreference(14.0f);
        mMap.setMaxZoomPreference(18.0f);

        getLastLocationMethod();

        // add listener
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {
                Toast.makeText(MapsActivity.this, "Long Click!", Toast.LENGTH_SHORT).show();
                //TODO: > create a geofence with radius 100m
                tempList.add(latLng);
                drawCircle(latLng);
            }
        });

        // TODO: add comments here My code
//        LocationManager mLocationMan = (LocationManager) getSystemService(LOCATION_SERVICE);
//        Criteria mCriteria = new Criteria();
//
//        mCriteria.setAccuracy(Criteria.ACCURACY_FINE);
//        mCriteria.setAltitudeRequired(false);
//        mCriteria.setBearingRequired(false);
//        mCriteria.setCostAllowed(true);
//        mCriteria.setPowerRequirement(Criteria.POWER_LOW);

//        String provider = mLocationMan.getBestProvider(mCriteria, true);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            Toast.makeText(this, "No perms", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        Location location = mLocationMan.getLastKnownLocation(provider);

//

        // Static marker TODO: REMOVE THIS BLOCK
//         LatLng current = new LatLng( -34, 151);
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(current));


        // Add a marker in Current location and move the camera
//        LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
//        mMap.addMarker(new MarkerOptions().position(current).title("Marker in current location."));
    }

    @SuppressLint("MissingPermission")
    private void getLastLocationMethod(){
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(current).title("Starting Marker."));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
                        }
                    }
                });

    }

    //Drawing fences
    private void drawCircle(LatLng point){

        // Instantiating CircleOptions to draw a circle around the marker
        CircleOptions circleOptions = new CircleOptions();

        // Specifying the center of the circle
        circleOptions.center(point);

        // Radius of the circle
        circleOptions.radius(GEOFENCE_RADIUS);

        // Border color of the circle
        circleOptions.strokeColor(Color.BLACK);

        // Fill color of the circle
        circleOptions.fillColor(0x30ff0000);

        // Border width of the circle
        circleOptions.strokeWidth(2);

        // Adding the circle to the GoogleMap
        mMap.addCircle(circleOptions);

    }

    // Adding button logic
//    @SuppressLint("NewApi")
    private void startButtonLogic() {

        //Save fences
        if (tempList != null) {
            LocationContentProvider provider = new LocationContentProvider();
            for (LatLng v: tempList) {
                ContentValues values = new ContentValues();
                values.put(DbLocation.SESSION_ID,sessionId);
                values.put(DbLocation.LAT_COL,v.latitude);
                values.put(DbLocation.LON_COL,v.longitude);

                provider.insert(DbLocation.FENCE_URI,values);
//                tempList.remove(v); // remove it from list
            }
        }
        tempList.clear();// clear fence list
        startService(new Intent(getApplicationContext(), LocationService.class));
    }

    // Remove all newly created geofences
    private void cancelButtonLogic() {
        tempList = null;
        // TODO: check if this performs as it should
        finish();
//        newGeofenceList.removeAll();
    }

}