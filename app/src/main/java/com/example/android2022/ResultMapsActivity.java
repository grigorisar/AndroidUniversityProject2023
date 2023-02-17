package com.example.android2022;

import static java.lang.Math.sqrt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.android2022.database.LocationContentProvider;
import com.example.android2022.models.FenceModel;
import com.example.android2022.models.TraversalModel;
import com.example.android2022.utils.LocationService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.example.android2022.databinding.ActivityResultMapsBinding;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class ResultMapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private int GEOFENCE_RADIUS = 100;
    private GoogleMap mMap;
    private ActivityResultMapsBinding binding;
    private Button returnButton, pauseButton;

    private LocationContentProvider provider;
    private ArrayList<FenceModel> fences;
    private ArrayList<TraversalModel> traversals;
    private FusedLocationProviderClient fusedLocationClient ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        binding = ActivityResultMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.results_map_fragment);
        mapFragment.getMapAsync(this);


        provider = new LocationContentProvider();
        fences = provider.getLastSessionFences();
        traversals = provider.getLastSessionTraversals();
//        Log.i("Traversals", "Traversal matches fence SessionID "+ traversals.get(0).getSessionId().equals(fences.get(0).getSessionId()));

        // Button Logic
        returnButton = findViewById(R.id.toMenu);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {returnButtonLogic();}
        });

        pauseButton = findViewById(R.id.pause);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){pauseButtonLogic();}
        });

    }

    private void returnButtonLogic() {
        finish(); //return to main activity
    }

    private void pauseButtonLogic() {
        // restart service
        try {
            stopService(new Intent(getBaseContext(), LocationService.class));
        } catch (Error e){
            Toast.makeText(this, "Error while stopping service", Toast.LENGTH_SHORT).show();
            Log.e("LocationService", "pauseButtonLogic: Error", e);
        }
        startService(new Intent(getBaseContext(), LocationService.class));

        // Quality of life change, also reloads the activity
        finish();
        startActivity(getIntent());
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        mMap.setMyLocationEnabled(true);
        mMap.setMinZoomPreference(14.0f);
        mMap.setMaxZoomPreference(18.0f);

        // get data from location provider
        drawFences(fences); // draw GeoFences on map

        // get data from location provider
        drawTraversals(traversals); // draw Entry & Exit points on map
        getLastLocationMethod();
    }

    // GeoFences
    private void drawFences(ArrayList<FenceModel> fences){
        if (fences == null) {
            return;
        }
        for (FenceModel f:fences) {
            drawFence(new LatLng(f.getLatitude(),f.getLongitude()));
        }
    }
    private void drawFence(LatLng point){
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
//                            mMap.addMarker(new MarkerOptions().position(current).title("Starting Marker."));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
                        }
                    }
                });

    }
    // Traversals
    private void drawTraversals(ArrayList<TraversalModel> traversals){
        if (traversals == null) {
            return;
        }
        for (TraversalModel t:traversals){
            drawTraversal(new LatLng(t.getLatitude(),t.getLongitude()),t.getAction(),t.getFenceId());
        }
    }
    private void drawTraversal(LatLng point, String mode, int fenceId){
        // Instantiating CircleOptions to draw a circle around the marker
        CircleOptions circleOptions = new CircleOptions();

        // Specifying the center of the circle
        circleOptions.center(point);

        // Radius of the circle
        circleOptions.radius(8);

//        FenceModel rootFence = fences.stream().filter(fence -> fenceId == fence.getId()).findFirst().orElse(null);
        switch (mode){
            case "ENTER":
                // Border color of the circle
                circleOptions.strokeColor(Color.BLACK);
                // Fill color of the circle
                circleOptions.fillColor(Color.GREEN);
                break;
            case "EXIT":
                // Border color of the circle
                circleOptions.strokeColor(Color.BLACK);
                // Fill color of the circle
                circleOptions.fillColor(Color.RED);
                break;
            default:
                // Border color of the circle
                circleOptions.strokeColor(Color.BLACK);
                // Fill color of the circle
                circleOptions.fillColor(Color.BLACK);
                break;
        }
//        drawClosestLine(rootFence,point);

        // Border width of the circle
        circleOptions.strokeWidth(1);

        // Adding the circle to the GoogleMap
        mMap.addCircle(circleOptions);
    }
//    private void drawClosestLine(FenceModel fence, LatLng trav){
//        if (fence == null) {
//            Log.i("___________", "drawClosestLine: Did not find fence with id"+fence.getId());
//            return;
//        }
//        LatLng c = new LatLng(fence.getLatitude(),fence.getLongitude());
//        int R = GEOFENCE_RADIUS;
//        double vX = trav.latitude - c.latitude;
//        double vY = trav.longitude - c.longitude;
//        double magV = sqrt(vX*vX + vY*vY);
//        double aX = c.latitude + vX / magV * R;
//        double aY = c.longitude + vY / magV * R;
//        mMap.addPolyline(new PolylineOptions()
//                .clickable(true)
//                .add(trav,new LatLng(aX,aY)));
//
//    }
}