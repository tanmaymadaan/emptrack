package com.tanmaymadaan.emptrack.interfaces;

import com.tanmaymadaan.emptrack.models.CheckInPOJO;
import com.tanmaymadaan.emptrack.models.LocationPOJO;
import com.tanmaymadaan.emptrack.models.UserPOJO;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface UserApi {


    @FormUrlEncoded
    @POST("swipe-in")
    Call<UserPOJO> swipeIn(@Field("uid") String uid);

    @FormUrlEncoded
    @POST("swipe-out")
    Call<UserPOJO> swipeOut(@Field("uid") String uid);

    @POST("add-position")
    Call<LocationPOJO> postLocationss(@Body LocationPOJO location);

    @FormUrlEncoded
    @POST("add-position")
    Call<LocationPOJO> postLocation(@Field("uid") String uid,
                                    @Field("date") String date,
                                    @Field("lat") Double lat,
                                    @Field("lng") Double lng,
                                    @Field("timestamp") Integer timestamp);

    @FormUrlEncoded
    @POST("checkIn")
    Call<CheckInPOJO> postCheckIn(@Field("uid") String uid,
                                  @Field("date") String date,
                                  @Field("lat") Double lat,
                                  @Field("lng") Double lng,
                                  @Field("timestamp") Integer timestamp,
                                  @Field("company") String company,
                                  @Field("purpose") String purpose,
                                  @Field("image") String path);

    @FormUrlEncoded
    @POST("checkOut")
    Call<CheckInPOJO> checkOut(@Field("uid") String uid,
                               @Field("remarks") String remarks);

    @Multipart
    @POST("/upload")
    Call<ResponseBody> postImage(@Part MultipartBody.Part image, @Part("upload")RequestBody name);
}
