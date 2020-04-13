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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tanmaymadaan.emptrack.R;
import com.tanmaymadaan.emptrack.adapters.DataAdapter;
import com.tanmaymadaan.emptrack.interfaces.AdminApi;
import com.tanmaymadaan.emptrack.interfaces.LoginAPIInterface;
import com.tanmaymadaan.emptrack.models.UserPOJO;

import java.util.List;

public class AdminMainActivity extends AppCompatActivity {

//    TextView textView;
    private RecyclerView recyclerView;
    private DataAdapter dataAdapter;
    private Button logoutBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        recyclerView = findViewById(R.id.recyclerView);
//        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

//        textView = findViewById(R.id.adminTv);
        String myUrl = getString(R.string.server_url);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(myUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        AdminApi adminApi = retrofit.create(AdminApi.class);
        Call<List<UserPOJO>> call = adminApi.getUsers("paramh1");
        call.enqueue(new Callback<List<UserPOJO>>() {
            @Override
            public void onResponse(Call<List<UserPOJO>> call, Response<List<UserPOJO>> response) {
                List<UserPOJO> users = response.body();
                dataAdapter = new DataAdapter(users, getApplicationContext());
                recyclerView.setAdapter(dataAdapter);

                for(int i = 0; i < users.size(); i++){
                    Log.d("USERS_MANAGER", users.get(i).getName());
                }
            }

            @Override
            public void onFailure(Call<List<UserPOJO>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_LONG).show();
//                textView.setText(t.getMessage());
            }
        });

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        logoutBtn = findViewById(R.id.logoutBtnAdmin);
        logoutBtn.setOnClickListener(v -> {

            LoginAPIInterface loginAPIInterface = retrofit.create(LoginAPIInterface.class);
            String uid = pref.getString("USER_UID", null);
            Call<UserPOJO> call2 = loginAPIInterface.logout(uid);
            call2.enqueue(new Callback<UserPOJO>() {
                @Override
                public void onResponse(Call<UserPOJO> call, Response<UserPOJO> response) {
                    editor.putString("LOGIN_STATUS", "false").apply();
                    editor.putString("CHECKIN_STATUS", "false").apply();
                    editor.putString("CHECKIN_COMPANY", "NULL").apply();
                    editor.commit();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onFailure(Call<UserPOJO> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Logout Failed", Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
