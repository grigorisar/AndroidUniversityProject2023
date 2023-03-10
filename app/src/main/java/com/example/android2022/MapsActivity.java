package com.example.android2022;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.android2022.database.DbLocation;
import com.example.android2022.database.LocationContentProvider;
import com.example.android2022.utils.LocationService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.example.android2022.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private ActivityMapsBinding binding;

    // Geofencing
    private int GEOFENCE_RADIUS = 100;
    private List<LatLng> tempList = new ArrayList<LatLng>();

    // Buttons
    private Button startButton;
    private Button cancelButton;
    private String sessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initializing clients
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);


        //Button Logic

        cancelButton = findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelButtonLogic();
            }

        });

        startButton = findViewById(R.id.start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startButtonLogic();
            }
        });

        sessionId = "Session" + (System.currentTimeMillis() / 1000L);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //Location impl
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        mMap.setMyLocationEnabled(true);
        mMap.setMinZoomPreference(14.0f);
        mMap.setMaxZoomPreference(18.0f);


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

        getLastLocationMethod();
    }

    private void getLastLocationMethod(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
//                            mMap.addMarker(new MarkerOptions().position(current).title("Starting Marker."));
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
        if (!tempList.isEmpty()) {
            LocationContentProvider provider = new LocationContentProvider();
            for (LatLng v: tempList) {
                ContentValues values = new ContentValues();
                values.put(DbLocation.SESSION_ID,sessionId);
                values.put(DbLocation.LAT_COL,v.latitude);
                values.put(DbLocation.LON_COL,v.longitude);

                provider.insert(DbLocation.FENCE_URI,values);
            }
            tempList.clear();// clear fence list
            startService(new Intent(getApplicationContext(), LocationService.class));
            finish();
        }else{
            Log.i("No fences created", "startButtonLogic: ");
            AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this).create();
            alertDialog.setTitle("No fences created");
            alertDialog.setMessage("Long press on the map to add a new area!");
            alertDialog.setButton("Ok", (dialog, which) -> {
                // If user click no then dialog box is canceled.
                dialog.cancel();
            });
            alertDialog.show();
            Toast.makeText(this, "Failed to start: No fences created.", Toast.LENGTH_SHORT).show();
        }
    }

    // Remove all newly created geofences
    private void cancelButtonLogic() {
        tempList = null;
        // TODO: check if this performs as it should
        finish();
    }

}