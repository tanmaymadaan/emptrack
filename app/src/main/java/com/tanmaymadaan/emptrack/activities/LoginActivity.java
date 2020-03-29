package com.tanmaymadaan.emptrack.activities;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.content.Intent;
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
                UserPOJO userPOJO = response.body();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("user", userPOJO);
                startActivity(intent);
                //Log.i("Success", "Login Successfully");
            }

            @Override
            public void onFailure(Call<UserPOJO> call, Throwable t) {
                Log.i("Failure", "Login Failed");
            }
        });

    }
}
