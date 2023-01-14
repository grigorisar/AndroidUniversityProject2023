package com.example.android2022;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.example.android2022.database.DbLocation;
import com.example.android2022.database.LocationContentProvider;
import com.example.android2022.models.FenceModel;
import com.example.android2022.models.TraversalModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.android2022.databinding.ActivityResultMapsBinding;

import java.util.ArrayList;

public class ResultMapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private int GEOFENCE_RADIUS = 100;
    private GoogleMap mMap;
    private ActivityResultMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityResultMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        LocationContentProvider locationProvider = new LocationContentProvider();
        // get data from location provider
        ArrayList<FenceModel> fences = locationProvider.getLastSessionFences();
        drawFences(fences); // draw GeoFences on map

        // get data from location provider
        ArrayList<TraversalModel> traversals = locationProvider.getTraversals(fences.get(0).getSessionId());
        drawTraversals(traversals); // draw Entry & Exit points on map
    }

    // GeoFences
    private void drawFences(ArrayList<FenceModel> fences){
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