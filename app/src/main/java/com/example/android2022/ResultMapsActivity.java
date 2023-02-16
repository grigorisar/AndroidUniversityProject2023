package com.example.android2022;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.android2022.database.LocationContentProvider;
import com.example.android2022.models.FenceModel;
import com.example.android2022.models.TraversalModel;
import com.example.android2022.utils.LocationService;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.example.android2022.databinding.ActivityResultMapsBinding;

import java.util.ArrayList;

public class ResultMapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private int GEOFENCE_RADIUS = 100;
    private GoogleMap mMap;
    private ActivityResultMapsBinding binding;
    private Button returnButton, pauseButton;

    private LocationContentProvider provider;
    private ArrayList<FenceModel> fences;
    private ArrayList<TraversalModel> traversals;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityResultMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.results_map);
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

        // Add a marker in Sydney and move the camera
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
        mMap.setMinZoomPreference(14.0f);
        mMap.setMaxZoomPreference(18.0f);

        // get data from location provider
        drawFences(fences); // draw GeoFences on map

        // get data from location provider
        drawTraversals(traversals); // draw Entry & Exit points on map
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

    // Traversals
    private void drawTraversals(ArrayList<TraversalModel> traversals){
        if (traversals == null) {
            return;
        }
        for (TraversalModel t:traversals){
            drawTraversal(new LatLng(t.getLatitude(),t.getLongitude()),t.getAction());
        }
    }
    private void drawTraversal(LatLng point, String mode){
        // Instantiating CircleOptions to draw a circle around the marker
        CircleOptions circleOptions = new CircleOptions();

        // Specifying the center of the circle
        circleOptions.center(point);

        // Radius of the circle
        circleOptions.radius(5);
        switch (mode){
            case "ENTRY":
                // Border color of the circle
                circleOptions.strokeColor(Color.GREEN);
                // Fill color of the circle
                circleOptions.fillColor(Color.GREEN);
                break;
            case "EXIT":
                // Border color of the circle
                circleOptions.strokeColor(Color.RED);
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
        // Border width of the circle
        circleOptions.strokeWidth(1);

        // Adding the circle to the GoogleMap
        mMap.addCircle(circleOptions);
    }
}