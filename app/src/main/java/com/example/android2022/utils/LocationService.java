package com.example.android2022.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.android2022.database.DbLocation;
import com.example.android2022.database.LocationContentProvider;
import com.example.android2022.models.FenceModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class LocationService extends Service {
    public static final String BROADCAST_ACTION = "Observing Location :)";
    private static final int minTime = 1000 * 5;// in millis
    private static final int minDistance = 50; // in meters
    public LocationManager locationManager;
    public MyLocationListener listener;
    private ArrayList<FenceModel> fences;
    LocationContentProvider provider;

    static String travFlag = "ENTRY";


    //    private GeofencingClient geofencingClient;
//    private ArrayList geofenceList;
//    Intent intent;
//    private FusedLocationProviderClient mFusedLocationClient;
//    public Location previousBestLocation = null;


    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance , (LocationListener) listener);
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, listener);

    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onTaskRemoved(intent);
        provider = new LocationContentProvider();
        fences = provider.getLastSessionFences();

        return START_STICKY;
    }

//    @Override
//    public void onTaskRemoved(Intent rootIntent){
//        Intent restartServiceIntent = new Intent(getApplicationContext(),this.getClass());
//        restartServiceIntent.setPackage(getPackageName());
//        startService(restartServiceIntent);
//        super.onTaskRemoved(rootIntent);
//    }
//
//    private LocationCallback locationCallback = new LocationCallback() {
//        @Override
//        public void onLocationResult(LocationResult locationResult) {
//            super.onLocationResult(locationResult);
//            Location currentLocation = locationResult.getLastLocation();
//
//
//            Log.d("Locations", currentLocation.getLatitude() +"," +currentLocation.getLongitude());
//            //ToDO Publish Location
//        }
//    };

//    private void startLocationUpdates() {
//        mFusedLocationClient.requestLocationUpdates(locationRequest,
//                locationCallback,
//                Looper.getMainLooper());
//    }
    @Override
    public void onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy();
        Log.v("STOP_SERVICE", "DONE");
        locationManager.removeUpdates(listener);
    }

    public static Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {

                }
            }
        };
        t.start();
        return t;
    }

    public double calculateDistance(LatLng c1 , LatLng c2){
        double R = 6371e3; //earth's radius metres
        double lat1 = c1.latitude * Math.PI/180; // φ, λ in radians
        double lat2 = c2.latitude * Math.PI/180;
        double dLat1 = (c2.latitude-c1.latitude) * Math.PI/180;
        double dLat2 = (c2.longitude-c1.longitude) * Math.PI/180;

        double a = Math.sin(dLat1/2) * Math.sin(dLat1/2) +
                        Math.cos(lat1) * Math.cos(lat2) *
                                Math.sin(dLat2/2) * Math.sin(dLat2/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return R * c; // in metres
    }

    public class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged( Location loc) {

            Log.i("*****", "Location changed");
            //TODO: add criteria
            LatLng location = new LatLng(loc.getLatitude(),loc.getLongitude());

            for (FenceModel fence: fences) {
                double distanceFromCircle = calculateDistance(location,fence.getLatLng());
                String sessionId = fence.getSessionId();

                if (distanceFromCircle < 100){
                    Log.i("Location Update", "Fence Session ID: "+ sessionId);
                    Log.i("Location Update", "Distance from circle: "+ distanceFromCircle);
                   patchTraversal(location, sessionId);
//                    Log.i("Inserted Traversal", "onLocationChanged: " + location.longitude+ "\n"+ location.latitude);
                }
            }
//                intent.putExtra("Latitude", loc.getLatitude());
//                intent.putExtra("Longitude", loc.getLongitude());
//                sendBroadcast(intent);
        }

        public void patchTraversal(LatLng location,String sessionId){
            ContentValues values = new ContentValues();
            values.put(DbLocation.LAT_COL,location.latitude);
            values.put(DbLocation.LON_COL,location.longitude);
            values.put(DbLocation.SESSION_ID,sessionId);
            values.put(DbLocation.ACTION_COL, travFlag);
            values.put(DbLocation.TIMESTAMP_COL,String.valueOf(System.currentTimeMillis() / 1000L));

            provider.insert(DbLocation.TRAVERSAL_URI,values);
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
        }


        public void onProviderEnabled(String provider) {
            Toast.makeText(getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }
    }
}