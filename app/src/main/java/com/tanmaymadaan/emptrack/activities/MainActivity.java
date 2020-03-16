package com.tanmaymadaan.emptrack.activities;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ebanx.swipebtn.OnStateChangeListener;
import com.ebanx.swipebtn.SwipeButton;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.karumi.dexter.BuildConfig;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.tanmaymadaan.emptrack.R;
import com.tanmaymadaan.emptrack.interfaces.JsonHolderApi;
import com.tanmaymadaan.emptrack.models.CheckInPOJO;
import com.tanmaymadaan.emptrack.services.LocationServiceGps;

public class MainActivity extends AppCompatActivity {

    //Button start, stop, checkin;
    TextView textView;
    EditText checkInLocEt;

    public LocationServiceGps locService;
    public boolean mTracking = false;
    private FusedLocationProviderClient fusedLocationClient;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String kms = intent.getStringExtra("KMS");
            textView.setText(kms + " kms");
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.tanmaymadaan.emptrack");
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SwipeButton swipeButton = findViewById(R.id.swipeButton);
        swipeButton.setOnStateChangeListener(new OnStateChangeListener() {
            @Override
            public void onStateChange(boolean active) {
                if(active) {
                    startService();
                }
                else {
                    mTracking = false;
                    locService.stopTracking();
                    toggleButtons();
                }
                Toast.makeText(getApplicationContext(), "Active: " + active, Toast.LENGTH_LONG).show();
            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("LAT_OLD", String.valueOf(location.getLatitude())).apply();
                        editor.putString("LNG_OLD", String.valueOf(location.getLongitude())).apply();
                    }
                });

//        start = findViewById(R.id.startTracking);
//        stop = findViewById(R.id.stopTracking);
          textView = findViewById(R.id.textView2);
//        checkInLocEt = findViewById(R.id.checkInLoc);
//        checkin = findViewById(R.id.checkInButton);

        final Intent intent = new Intent(this.getApplication(), LocationServiceGps.class);
        this.getApplication().startService(intent);
        this.getApplication().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

//        start.setOnClickListener(v -> startService());
//
//        stop.setOnClickListener(v -> {
//            mTracking = false;
//            locService.stopTracking();
//            toggleButtons();
//        });
//
//        checkin.setOnClickListener(v -> {
//            String loc = checkInLocEt.getText().toString().trim();
//            checkIn(loc);
//        });

    }

    private void startService() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        Double d = 0.00;
        editor.putString("DIST", String.valueOf(d)).apply();
        editor.commit();

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        locService.startTracking();
                        mTracking = true;
                        toggleButtons();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            openSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void toggleButtons() {
        //start.setEnabled(!mTracking);
        //stop.setEnabled(mTracking);
        //textView.setText( (mTracking) ? "Tracking" : "GPS Ready");
    }

    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            String name = className.getClassName();
            if (name.endsWith("LocationServiceGps")) {
                locService = ((LocationServiceGps.LocationServiceBinder) service).getService();
                //start.setEnabled(true);
                //textView.setText("GPS Ready");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (name.getClassName().equals("LocationServiceGps")) {
                locService = null;
                Notification notification = getNotification("Tracking stopped :)");
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(12345678, notification);
            }
        }
    };

    private Notification getNotification(String text) {

//        NotificationChannel channel = new NotificationChannel("channel_01", "My Channel", NotificationManager.IMPORTANCE_DEFAULT);
//        NotificationManager notificationManager = getSystemService(NotificationManager.class);
//        notificationManager.createNotificationChannel(channel);

        Notification.Builder builder = new Notification.Builder(getApplicationContext(), "channel_01").setAutoCancel(true);
        builder.setContentText(text);
        builder.setSmallIcon(R.drawable.ic_launcher_background);
        return builder.build();
    }

    private void checkIn(String company){

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            String myUrl = getString(R.string.server_url);
                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl(myUrl)
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();

                            JsonHolderApi jsonPlaceHolderApi = retrofit.create(JsonHolderApi.class);
                            Call<CheckInPOJO> call = jsonPlaceHolderApi.postCheckIn("Tanmay", "2020-03-13", location.getLatitude(), location.getLongitude(), 234562, company);
                            call.enqueue(new Callback<CheckInPOJO>() {
                                @Override
                                public void onResponse(Call<CheckInPOJO> call, Response<CheckInPOJO> response) {
                                    if(!response.isSuccessful()){
                                        Toast.makeText(getApplicationContext(), response.code(), Toast.LENGTH_LONG).show();
                                    }
                                    Log.i("Success", "Saved Successfully");
                                }

                                @Override
                                public void onFailure(Call<CheckInPOJO> call, Throwable t) {
                                    Log.e("Error Location", t.getMessage());
                                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "Error fetching location", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}
