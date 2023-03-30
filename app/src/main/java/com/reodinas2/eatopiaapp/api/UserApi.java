package com.reodinas2.eatopiaapp.api;

import com.reodinas2.eatopiaapp.model.User;
import com.reodinas2.eatopiaapp.model.UserInfoRes;
import com.reodinas2.eatopiaapp.model.UserRes;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface UserApi {

    // 회원가입 API
    @POST("/user/register")
    Call<UserRes> register(@Body User user);

    // 로그인 API
    @POST("/user/login")
    Call<UserRes> login(@Body User user);

    // 로그아웃



    // 회원정보조회
    @GET("/user")
    Call<UserInfoRes> getUser(@Header("Authorization") String token);

}


