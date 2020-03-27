package com.tanmaymadaan.emptrack.interfaces;

import com.tanmaymadaan.emptrack.models.UserPOJO;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface LoginAPIInterface {

    @FormUrlEncoded
    @POST("/login")
    Call<UserPOJO> login(@Field("companyCode") String companyCode,
                         @Field("userCode") String userCode,
                         @Field("passCode") String passCode);


}
