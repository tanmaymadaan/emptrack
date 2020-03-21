package com.tanmaymadaan.emptrack.interfaces;

import com.tanmaymadaan.emptrack.models.CheckInPOJO;
import com.tanmaymadaan.emptrack.models.LocationPOJO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface JsonHolderApi {


    @POST("add-position")
    Call<LocationPOJO> postLocationss(@Body LocationPOJO location);

    @FormUrlEncoded
    @POST("add-position")
    Call<LocationPOJO> postLocation(@Field("userId") String userId,
                                    @Field("date") String date,
                                    @Field("lat") Double lat,
                                    @Field("lng") Double lng,
                                    @Field("timestamp") Integer timestamp);

    @FormUrlEncoded
    @POST("checkIn")
    Call<CheckInPOJO> postCheckIn(@Field("userId") String userId,
                                  @Field("date") String date,
                                  @Field("lat") Double lat,
                                  @Field("lng") Double lng,
                                  @Field("timestamp") Integer timestamp,
                                  @Field("company") String company);

    @GET("checkOut/{id}")
    Call<CheckInPOJO> checkOut(@Path("id") String id);
}
