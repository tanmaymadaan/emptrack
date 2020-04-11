package com.tanmaymadaan.emptrack.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.tanmaymadaan.emptrack.R;
import com.tanmaymadaan.emptrack.classes.ProgressRequestBody;
import com.tanmaymadaan.emptrack.interfaces.UserApi;
import com.tanmaymadaan.emptrack.models.CheckInPOJO;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.view.View.GONE;

public class CheckInActivity extends AppCompatActivity implements ProgressRequestBody.UploadCallbacks{

    //TODO: Change checkInStatus in SharedPref

    EditText companyEt, purposeEt;
//    ImageView image;
    Button clickPicBtn, checkInBtn;

    UserApi userApi;
    Uri picUri;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 107;
    private final static int IMAGE_RESULT = 200;
    Bitmap mBitmap;
    TextView textView;
    byte[] byteArray;
    private FusedLocationProviderClient fusedLocationClient;
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        companyEt = findViewById(R.id.companyCheckIn);
        purposeEt = findViewById(R.id.purposeCheckIn);
//        image = findViewById(R.id.imageCheckIn);
        clickPicBtn = findViewById(R.id.clickPicBtn);
        checkInBtn = findViewById(R.id.checkInBtn);

        clickPicBtn.setOnClickListener(v -> {
            startActivityForResult(getPickImageChooserIntent(), IMAGE_RESULT);
        });

        checkInBtn.setOnClickListener(v -> {
            if (mBitmap != null)
                multipartImageUpload();
            else {
                Toast.makeText(getApplicationContext(), "Bitmap is null. Try again", Toast.LENGTH_SHORT).show();
            }
        });

        askPermissions();

        date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    private void checkIn(String company, String purpose, String filename){
        String myUrl = getString(R.string.server_url);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(myUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UserApi jsonPlaceHolderApi = retrofit.create(UserApi.class);
       //TODO; location lat and lng
        //TODO: timestamp automatic
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);// 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        String uid = pref.getString("USER_UID", null);

        Call<CheckInPOJO> call = jsonPlaceHolderApi.postCheckIn(uid, date, 78.66, 89.99, 234562, company, purpose, filename);
        call.enqueue(new Callback<CheckInPOJO>() {
            @Override
            public void onResponse(Call<CheckInPOJO> call, Response<CheckInPOJO> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(getApplicationContext(), response.message(), Toast.LENGTH_LONG).show();
                }
                editor.putString("CHECKIN_STATUS", "Active").apply();
                editor.putString("CHECKIN_COMPANY", company).apply();
                editor.putString("CHECKIN_PUPROSE", purpose).apply();

                Log.i("Success", "Saved Successfully");
            }

            @Override
            public void onFailure(Call<CheckInPOJO> call, Throwable t) {
                Log.e("Error Location", t.getMessage());
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


        editor.putString("CURRENT_LOC", company).apply();
        editor.commit();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        fusedLocationClient.getLastLocation()
//                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
//                    @Override
//                    public void onSuccess(Location location) {
//                        // Got last known location. In some rare situations this can be null.
//                        if (location != null) {
//
//                        } else {
//                            Toast.makeText(getApplicationContext(), "Error fetching location", Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            ImageView imageView = findViewById(R.id.imageCheckIn);

            if (requestCode == IMAGE_RESULT) {


                String filePath = getImageFilePath(data);
                if (filePath != null) {
                    checkInBtn.setVisibility(GONE);
                    mBitmap = BitmapFactory.decodeFile(filePath);
                    getByteArrayInBackground();
                    imageView.setImageBitmap(mBitmap);

                }
            }

        }

    }

    private void getByteArrayInBackground() {

        Thread thread = new Thread() {
            @Override
            public void run() {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                mBitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
                byteArray = bos.toByteArray();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        checkInBtn.setVisibility(View.VISIBLE);
                    }
                });


            }
        };
        thread.start();
    }

    public String getImageFilePath(Intent data) {
        return getImageFromFilePath(data);
    }

    private String getImageFromFilePath(Intent data) {
        boolean isCamera = data == null || data.getData() == null;

        if (isCamera) return getCaptureImageOutputUri().getPath();
        else return getPathFromURI(data.getData());

    }

    private String getPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("pic_uri", picUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        picUri = savedInstanceState.getParcelable("pic_uri");
    }

    private void askPermissions() {
        permissions.add(CAMERA);
        permissions.add(WRITE_EXTERNAL_STORAGE);
        permissions.add(READ_EXTERNAL_STORAGE);
        permissionsToRequest = findUnAskedPermissions(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }
    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    private void initRetrofitClient() {
        OkHttpClient client = new OkHttpClient.Builder().build();

        //change the ip to yours.
//        jsonHolderApi = new Retrofit.Builder().baseUrl(getString(R.string.server_url)).client(client).build().create(JsonHolderApi.class);
        userApi = new Retrofit.Builder().baseUrl(getString(R.string.server_url)).client(client).build().create(UserApi.class);
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }
    private void multipartImageUpload() {

        initRetrofitClient();


        try {

            if (byteArray != null) {
                File filesDir = getApplicationContext().getFilesDir();
                File file = new File(filesDir, "image" + ".png");

                FileOutputStream fos = new FileOutputStream(file);
                fos.write(byteArray);
                fos.flush();
                fos.close();

                //textView.setTextColor(Color.BLUE);

                ProgressRequestBody fileBody = new ProgressRequestBody(file, this);
                MultipartBody.Part body = MultipartBody.Part.createFormData("upload", file.getName(), fileBody);
                RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload");

                Call<ResponseBody> req = userApi.postImage(body, name);
                req.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String company = companyEt.getText().toString().trim();
                            String purpose = purposeEt.getText().toString().trim();
                            checkIn(company, purpose, response.body().string());
//                            Toast.makeText(getApplicationContext(), response.body().string() + " ", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
//                        textView.setText("Uploaded Failed!");
//                        textView.setTextColor(Color.RED);
                        Toast.makeText(getApplicationContext(), "Request failed", Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }
                });
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Intent getPickImageChooserIntent() {

        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }


    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalFilesDir("");
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;
    }



    @Override
    public void onProgressUpdate(int percentage) {
//        textView.setText(percentage + "%");
    }

    @Override
    public void onError() {
//        textView.setText("Uploaded Failed!");
//        textView.setTextColor(Color.RED);
    }

    @Override
    public void onFinish() {
//        textView.setText("Uploaded Successfully");
    }

    @Override
    public void uploadStart() {
//        textView.setText("0%");
        Toast.makeText(getApplicationContext(), "Upload started", Toast.LENGTH_SHORT).show();
    }
}
