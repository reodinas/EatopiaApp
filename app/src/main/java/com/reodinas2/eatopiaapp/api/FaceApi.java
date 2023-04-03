package com.reodinas2.eatopiaapp.api;

import com.reodinas2.eatopiaapp.model.Res;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface FaceApi {

    // 얼굴등록 api
    @Multipart
    @POST("/faces")
    Call<Res> addFace(@Header("Authorization") String token,
                      @Part MultipartBody.Part photo);

    @DELETE("/faces")
    Call<Res> deleteFace(@Header("Authorization") String token);
}
