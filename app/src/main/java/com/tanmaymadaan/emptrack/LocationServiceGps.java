package com.tanmaymadaan.emptrack;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.util.Log;
import android.widget.Toast;

import com.tanmaymadaan.emptrack.interfaces.JsonHolderApi;
import com.tanmaymadaan.emptrack.models.LocationPOJO;

public class LocationServiceGps extends Service {
    private final LocationServiceBinder binder = new LocationServiceBinder();
    private final String TAG = "BackgroundService";
    private LocationListener mLocationListener;
    private LocationManager mLocationManager;
    private NotificationManager notificationManager;

    private final int LOCATION_INTERVAL = 60000;
    private final int LOCATION_DISTANCE = 10;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private class LocationListener implements android.location.LocationListener
    {
        private Location lastLocation = null;
        private final String TAG = "LocationListener";
        private Location mLastLocation;

        public LocationListener(String provider)
        {
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location)
        {
            mLastLocation = location;
            Log.i(TAG, "LocationChanged: "+location);
            Notification notification = getNotification("Tracking in progress :)");
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(12345678, notification);

            String myUrl = getString(R.string.server_url);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(myUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            JsonHolderApi jsonPlaceHolderApi = retrofit.create(JsonHolderApi.class);

//            LocationPOJO locationPOJO = new LocationPOJO();
            Call<LocationPOJO> call = jsonPlaceHolderApi.postLocation("Tanmay", "2020-03-13", location.getLatitude(), location.getLongitude(), 234562);
            call.enqueue(new Callback<LocationPOJO>() {
                @Override
                public void onResponse(Call<LocationPOJO> call, Response<LocationPOJO> response) {
                    if(!response.isSuccessful()){
                        Toast.makeText(getApplicationContext(), response.code(), Toast.LENGTH_LONG).show();
                    }
                    Log.i("Success", "Saved Successfully");
                    //Toast.makeText(getApplicationContext(), "Saved Successfully!", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Call<LocationPOJO> call, Throwable t) {
                    Log.e("Error Location", t.getMessage());
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + status);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate()
    {
        Log.i(TAG, "onCreate");
        startForeground(12345678, getNotification("Yoooo"));
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (mLocationManager != null) {
            try {
                mLocationManager.removeUpdates(mLocationListener);
            } catch (Exception ex) {
                Log.i(TAG, "fail to remove location listners, ignore", ex);
            }
        }
    }

    private void initializeLocationManager() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    public void startTracking() {
        initializeLocationManager();
        mLocationListener = new LocationListener(LocationManager.GPS_PROVIDER);

        try {
            mLocationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, mLocationListener );

        } catch (java.lang.SecurityException ex) {
            // Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            // Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }

    }

    public void stopTracking() {
        this.onDestroy();
    }

    private Notification getNotification(String text) {

//        NotificationChannel channel = new NotificationChannel("channel_01", "My Channel", NotificationManager.IMPORTANCE_DEFAULT);
//
//        NotificationManager notificationManager = getSystemService(NotificationManager.class);
//        notificationManager.createNotificationChannel(channel);

        Notification.Builder builder = new Notification.Builder(getApplicationContext(), "channel_01").setAutoCancel(true);
        builder.setContentText(text);
        builder.setSmallIcon(R.drawable.ic_launcher_background);
        return builder.build();
    }


    public class LocationServiceBinder extends Binder {
        public LocationServiceGps getService() {
            return LocationServiceGps.this;
        }
    }

}

