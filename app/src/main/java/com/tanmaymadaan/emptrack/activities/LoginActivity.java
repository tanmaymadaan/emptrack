package com.tanmaymadaan.emptrack.activities;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tanmaymadaan.emptrack.R;
import com.tanmaymadaan.emptrack.interfaces.LoginAPIInterface;
import com.tanmaymadaan.emptrack.models.UserPOJO;

public class LoginActivity extends AppCompatActivity {

    EditText companyCodeEt, userCodeEt, passCodeEt;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        String loginStatus = pref.getString("LOGIN_STATUS", "false");
        String userRole = pref.getString("USER_ROLE", "user");
        if(loginStatus.equals("true") && userRole.equals("user")){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (loginStatus.equals("true") && userRole.equals("admin")) {
            Intent intent = new Intent(this, AdminMainActivity.class);
            startActivity(intent);
        }

         companyCodeEt = findViewById(R.id.companyCode);
         userCodeEt = findViewById(R.id.userCode);
         passCodeEt = findViewById(R.id.passCode);
         login = findViewById(R.id.loginButton);

         login.setOnClickListener(v -> {
             String companyCode, userCode, passCode;
             companyCode = companyCodeEt.getText().toString().trim();
             userCode = userCodeEt.getText().toString().trim();
             passCode = passCodeEt.getText().toString().trim();

             callLoginApi(companyCode, userCode, passCode);
         });
    }

    private void callLoginApi(String compCode, String uCode, String pCode) {
        String myUrl = getString(R.string.server_url);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(myUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        LoginAPIInterface loginAPIInterface = retrofit.create(LoginAPIInterface.class);
        Call<UserPOJO> call = loginAPIInterface.login(compCode, uCode, pCode);
        call.enqueue(new Callback<UserPOJO>() {
            @Override
            public void onResponse(Call<UserPOJO> call, Response<UserPOJO> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(getApplicationContext(), response.body().toString(), Toast.LENGTH_LONG).show();
                    //Log.i("Success", userPOJO.getCompanyCode() + "\\\\\\" + userPOJO.getUserCode() + "\\\\\\" + userPOJO.getPassCode() + "\\\\" + userPOJO.getName());
                }
                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("LOGIN_STATUS", "true").apply();
                UserPOJO userPOJO = response.body();
                editor.putString("USER_UID", userPOJO.getUid()).apply();
                editor.putString("USER_NAME", userPOJO.getName()).apply();
                editor.putString("USER_ROLE", userPOJO.getRole()).apply();
                editor.commit();

                if(userPOJO.getRole().equals("admin")){
                    Intent intent = new Intent(getApplicationContext(), AdminMainActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }

                //Log.i("Success", "Login Successfully");
            }

            @Override
            public void onFailure(Call<UserPOJO> call, Throwable t) {
                Log.i("Failure", "Login Failed");
            }
        });

    }
}
