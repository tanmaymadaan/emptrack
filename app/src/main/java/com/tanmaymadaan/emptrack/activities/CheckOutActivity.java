package com.tanmaymadaan.emptrack.activities;

import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.TextView;
import android.widget.Toast;

import com.tanmaymadaan.emptrack.R;
import com.tanmaymadaan.emptrack.interfaces.UserApi;
import com.tanmaymadaan.emptrack.models.CheckInPOJO;

public class CheckOutActivity extends AppCompatActivity {

    EditText remarksEt;
    Button checkoutBtn;
    TextView companyTv, purposeTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        remarksEt = findViewById(R.id.checkoutRemarksEt);
        checkoutBtn = findViewById(R.id.checkoutBtn);
        companyTv = findViewById(R.id.checkoutCompanyTv);
        purposeTv = findViewById(R.id.checkoutPurposeTv);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        String company = pref.getString("CHECKIN_COMPANY", "Not Checked In");
        companyTv.setText("Company: " + company);

        String purpose = pref.getString("CHECKIN_PURPOSE", "No purpose");
        purposeTv.setText("Purpose: " + purpose);

        checkoutBtn.setOnClickListener(v -> {
            String remarks = remarksEt.getText().toString().trim();
            checkout(remarks);
        });
    }

    private void checkout(String remarks){

        String myUrl = getString(R.string.server_url);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(myUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UserApi jsonPlaceHolderApi = retrofit.create(UserApi.class);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        Call<CheckInPOJO> call = jsonPlaceHolderApi.checkOut(pref.getString("USER_UID", null), remarks);
        call.enqueue(new Callback<CheckInPOJO>() {
            @Override
            public void onResponse(Call<CheckInPOJO> call, Response<CheckInPOJO> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(getApplicationContext(), response.message(), Toast.LENGTH_LONG).show();
                }
                Log.i("Success", "Saved Successfully");
                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("CHECKIN_STATUS", "Inactive").apply();
                editor.putString("CHECKIN_COMPANY", "Not Checked In").apply();
                editor.commit();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<CheckInPOJO> call, Throwable t) {
                Log.e("Error Location", t.getMessage());
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }


}
