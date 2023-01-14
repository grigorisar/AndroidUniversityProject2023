package com.example.android2022.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.widget.Toast;

public class GpsBroadcastReceiver extends BroadcastReceiver {

    boolean isGpsEnabled;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: debug if needed
        // an Intent broadcast.
        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            Toast.makeText(context, "in android.location.PROVIDERS_CHANGED",
                    Toast.LENGTH_SHORT).show();

            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            Intent pushIntent = new Intent(context, LocationService.class);

            if (isGpsEnabled) {
                context.startService(pushIntent);
            } else {
                context.stopService(pushIntent);
            }
       }
    }
}