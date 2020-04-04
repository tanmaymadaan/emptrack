package com.tanmaymadaan.emptrack.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.tanmaymadaan.emptrack.R;
import com.tanmaymadaan.emptrack.interfaces.JsonHolderApi;
import com.tanmaymadaan.emptrack.models.LocationPOJO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class LocationServiceGps extends Service {
    private final LocationServiceBinder binder = new LocationServiceBinder();
    private final String TAG = "BackgroundService";
    private LocationListener mLocationListener;
    private LocationManager mLocationManager;
    private NotificationManager notificationManager;

    private TextView textView;
    private final int LOCATION_INTERVAL = 10000;
    private final int LOCATION_DISTANCE = 10;
    private int secs = 0;
    private int distance = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private class LocationListener implements android.location.LocationListener {
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
            secs++;
            mLastLocation = location;
            Log.i(TAG, "LocationChanged: " + location);


            if(secs % 12 == 0) {

                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                String lat = pref.getString("LAT_OLD_" + date, null);
                String lng = pref.getString("LNG_OLD_" + date, null);
                Double dist = calcDist(Double.parseDouble(lat), Double.parseDouble(lng), location.getLatitude(), location.getLongitude());


                dist = dist + Double.parseDouble(Objects.requireNonNull(pref.getString("DIST_" + date, null)));
                Notification notification = getNotification("Travelled " + dist + " kms today :)");
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(12345678, notification);

                SharedPreferences.Editor editor = pref.edit();
                editor.putString("LAT_OLD_" + date, String.valueOf(location.getLatitude())).apply();
                editor.putString("LNG_OLD_" + date, String.valueOf(location.getLongitude())).apply();
                editor.putString("DIST_" + date, String.format("%.2f", dist)).apply();
                editor.commit();

                //push to server
                String myUrl = getString(R.string.server_url);
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(myUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                JsonHolderApi jsonPlaceHolderApi = retrofit.create(JsonHolderApi.class);

//              LocationPOJO locationPOJO = new LocationPOJO();
                Call<LocationPOJO> call = jsonPlaceHolderApi.postLocation("Tanmay", date, location.getLatitude(), location.getLongitude(), 234562);
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
            } else {
                //calc and add distance
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                String lat = pref.getString("LAT_OLD_" + date, null);
                String lng = pref.getString("LNG_OLD_" + date, null);


                Double dist = calcDist(Double.parseDouble(lat), Double.parseDouble(lng), location.getLatitude(), location.getLongitude());

                dist = dist + Double.parseDouble(Objects.requireNonNull(pref.getString("DIST_" + date, null)));
                Intent intent1 = new Intent();
                intent1.setAction("com.tanmaymadaan.emptrack");
                intent1.putExtra("KMS", String.format("%.2f", dist)+"");
                sendBroadcast(intent1);
                Notification notification = getNotification("Travelled " + dist + " kms today :)");
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(12345678, notification);

                SharedPreferences.Editor editor = pref.edit();
                editor.putString("LAT_OLD_" + date, String.valueOf(location.getLatitude())).apply();
                editor.putString("LNG_OLD_" + date, String.valueOf(location.getLongitude())).apply();
                editor.putString("DIST_" + date, String.format("%.2f", dist)).apply();
                editor.commit();
            }

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

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        Double d = 0.00;
        editor.putString("DIST", String.valueOf(d)).apply();
        editor.commit();
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

        NotificationChannel channel = new NotificationChannel("channel_01", "My Channel", NotificationManager.IMPORTANCE_DEFAULT);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

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


    public double calcDist(double lat1, double lon1, double lat2, double lon2)
    {
        final int R = 6371;
        // Radius of the earth in km
        double dLat = deg2rad(lat2 - lat1);
        // deg2rad below
        double dLon = deg2rad(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        // Distance in km
        return d;
    }
    private double deg2rad(double deg)
    {
        return deg * (Math.PI / 180);
    }
}

