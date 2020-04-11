package com.tanmaymadaan.emptrack.interfaces;

import com.tanmaymadaan.emptrack.models.UserPOJO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface AdminApi {

    @GET("get-users/{uid}")
    Call<List<UserPOJO>> getUsers(@Path("uid") String uid);
}
