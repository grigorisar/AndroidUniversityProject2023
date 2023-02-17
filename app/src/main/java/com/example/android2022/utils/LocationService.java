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
import com.example.android2022.models.TraversalModel;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class LocationService extends Service {
    private static final int minTime = 1000 * 5;// in millis
    private static final int minDistance = 50; // in meters
    public LocationManager locationManager;
    public MyLocationListener listener;
    private ArrayList<FenceModel> fences;
    LocationContentProvider provider;
    private int lastFenceId;


    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance , (LocationListener) listener);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    @Override
    public void onDestroy() {
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
                String sessionId = fence.getSessionId();
                double distanceFromCircle = calculateDistance(location,fence.getLatLng());
                //if the last traversal fence equals to this one
                if(fence.getId()==lastFenceId){
                    // continue if user is still in circle
                    if(distanceFromCircle<100){
                        continue;
                    }else{ // otherwise user exited the circle
                        Log.i("Exiting Fence", "Fence ID: "+ fence.getId());
                        patchTraversal(location,sessionId,TraversalModel.EXIT,fence.getId());
                        lastFenceId=-999;
                    }
                }
                if (distanceFromCircle < 100){
                    Log.i("Entered Fence", "Fence ID: "+ fence.getId());
                    patchTraversal(location, sessionId, TraversalModel.ENTER,fence.getId());
                    lastFenceId = fence.getId();
//                    Log.i("Inserted Traversal", "onLocationChanged: " + location.longitude+ "\n"+ location.latitude);
                }
            }
//                intent.putExtra("Latitude", loc.getLatitude());
//                intent.putExtra("Longitude", loc.getLongitude());
//                sendBroadcast(intent);
        }

        public void patchTraversal(LatLng location,String sessionId,String action,int fenceId){
            ContentValues values = new ContentValues();
            values.put(DbLocation.LAT_COL,location.latitude);
            values.put(DbLocation.LON_COL,location.longitude);
            values.put(DbLocation.SESSION_ID,sessionId);
            values.put(DbLocation.ACTION_COL, action);
            values.put(DbLocation.TIMESTAMP_COL,String.valueOf(System.currentTimeMillis() / 1000L));
            values.put(DbLocation.FENCE_ID,fenceId);

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