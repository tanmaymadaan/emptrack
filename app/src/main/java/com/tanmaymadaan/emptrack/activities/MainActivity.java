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
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ebanx.swipebtn.SwipeButton;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity  {

    //TODO: Add History button
    //TODO: Add login info to SharedPref when users login (in onCreate (if not already logged in))
    //TODO: Add swipeStatus to SharePref
    //TODO: Fetch checkInStatus from SharedPref and display checkInBtn or checkOutBtn accordingly
    //TODO: If Swiped in then only enable the option to check_in

    //Button start, stop, checkin;
    ImageButton imageButton;
    TextView distTv, currPosTv, checkInTv;
    EditText checkInLocEt;
    String date;
    ImageView imageView;
    Boolean checkInStatus;
    SwipeButton swipeButton;

    public LocationServiceGps locService;
    public boolean mTracking = false;
    private FusedLocationProviderClient fusedLocationClient;
    private int locationRequestCode = 1000;
    private double wayLatitude = 0.0, wayLongitude = 0.0;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String kms = intent.getStringExtra("KMS");
            distTv.setText("Distance Travelled: "+ kms + " kms");
        }
    };

    @Override
    protected void onRestart() {
        super.onRestart();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode

        String checkInStatus = pref.getString("CHECKIN_STATUS", " ");
        if(!(checkInStatus.equals("Active"))){
            imageView.setImageResource(R.drawable.ic_check_in);
            checkInTv.setText("Check In");
        } else {
            imageView.setImageResource(R.drawable.ic_check_out);
            checkInTv.setText("Check Out");
        }

        String checkInCompany = pref.getString("CHECKIN_COMPANY", " ");
        currPosTv.setText("Currently at: " + checkInCompany);

        String swipeStatus = pref.getString("SWIPE_STATUS", "Inactive");
        if(!swipeStatus.equals("inactive")){
            swipeButton.setVisibility(View.GONE);
            imageButton.setVisibility(View.VISIBLE);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.tanmaymadaan.emptrack");
        registerReceiver(broadcastReceiver, intentFilter);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode

        String checkInStatus = pref.getString("CHECKIN_STATUS", " ");
        if(!(checkInStatus.equals("Active"))){
            imageView.setImageResource(R.drawable.ic_check_in);
            checkInTv.setText("Check In");
        } else {
            imageView.setImageResource(R.drawable.ic_check_out);
            checkInTv.setText("Check Out");
        }

        String checkInCompany = pref.getString("CHECKIN_COMPANY", " ");
        currPosTv.setText("Currently at: " + checkInCompany);

        String swipeStatus = pref.getString("SWIPE_STATUS", "active");
        if(!swipeStatus.equals("inactive")){
            swipeButton.setVisibility(View.GONE);
            imageButton.setVisibility(View.VISIBLE);
        }
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

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        String loginStatus = pref.getString("LOGIN_STATUS", "false");
        if(loginStatus.equals("false")){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        imageButton = findViewById(R.id.button);
        distTv = findViewById(R.id.distTv);
        currPosTv = findViewById(R.id.currPosTv);
        imageView = findViewById(R.id.checkInIv);
        checkInTv = findViewById(R.id.checkInTv);


        date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        imageButton.setOnClickListener(v -> {
            mTracking = false;
            locService.stopTracking();
            imageButton.setVisibility(View.GONE);
            swipeButton.setVisibility(View.VISIBLE);
            editor.putString("SWIPE_STATUS", "inactive").apply();
            editor.commit();
        });
        swipeButton = findViewById(R.id.swipeButton);
        swipeButton.setOnStateChangeListener(active -> {
            if(active) {
                startService();
                imageButton.setVisibility(View.VISIBLE);
                swipeButton.setVisibility(View.GONE);
                editor.putString("SWIPE_STATUS", "active").apply();
                editor.commit();
                swipeButton.toggleState();
            }
            Toast.makeText(getApplicationContext(), "Active: " + active, Toast.LENGTH_LONG).show();
        });


        imageView.setOnClickListener(v -> {
            String checkInStatus = pref.getString("CHECKIN_STATUS", null);
            if(checkInStatus == null){
                checkInStatus = "Inactive";
            }
            if(!(checkInStatus.equals("Active"))){
                Intent intent = new Intent(this, CheckInActivity.class);
                startActivity(intent);
            } else {

                Intent intent = new Intent(this, CheckOutActivity.class);
                startActivity(intent);
            }

        });


        final Intent intent = new Intent(this.getApplication(), LocationServiceGps.class);
        this.getApplication().startService(intent);
        this.getApplication().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

    }


    private void startService() {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("LAT_OLD_" + date, String.valueOf(location.getLatitude())).apply();
                    editor.putString("LNG_OLD_" + date, String.valueOf(location.getLongitude())).apply();
                    Double d = 0.00;
                    if(pref.getString("DIST_" + date, null) == null) {
                        editor.putString("DIST_" + date, String.valueOf(d)).apply();
                        editor.commit();
                    }

                    Dexter.withActivity(this)
                            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                            .withListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted(PermissionGrantedResponse response) {
                                    locService.startTracking();
                                    Toast.makeText(getApplicationContext(), "Tracking started", Toast.LENGTH_LONG).show();
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
                });

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

}
