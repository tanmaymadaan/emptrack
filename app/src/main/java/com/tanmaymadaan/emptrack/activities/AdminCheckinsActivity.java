package com.tanmaymadaan.emptrack.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tanmaymadaan.emptrack.R;
import com.tanmaymadaan.emptrack.adapters.CheckInsAdapter;
import com.tanmaymadaan.emptrack.interfaces.AdminApi;
import com.tanmaymadaan.emptrack.models.CheckInPOJO;

import java.util.List;

public class AdminCheckinsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CheckInsAdapter checkInsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_checkins);

        recyclerView = findViewById(R.id.adminCheckinsRecView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        getIncomingIntent();
    }

    private void getIncomingIntent(){
        if(getIntent().hasExtra("user_uid")){
            String uid = getIntent().getStringExtra("user_uid");
            Log.d("AdminCheckinsActivity", uid);
            displayCheckIns(uid);
        }
    }

    private void displayCheckIns(String uid){
        Log.d("AdminCheckinsActivity", "displayCheckIns");
        String myUrl = getString(R.string.server_url);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(myUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AdminApi adminApi = retrofit.create(AdminApi.class);
        Call<List<CheckInPOJO>> call = adminApi.getCheckIns(uid);
        call.enqueue(new Callback<List<CheckInPOJO>>() {
            @Override
            public void onResponse(Call<List<CheckInPOJO>> call, Response<List<CheckInPOJO>> response) {
                List<CheckInPOJO> checkIns = response.body();
                checkInsAdapter = new CheckInsAdapter(checkIns, getApplicationContext());
                recyclerView.setAdapter(checkInsAdapter);

                for(int i = 0; i < checkIns.size(); i++){
                    Log.d("USERS_MANAGER", checkIns.get(i).getCompany());
                }
            }

            @Override
            public void onFailure(Call<List<CheckInPOJO>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_LONG).show();
            }
        });
    }
}
