package com.reodinas2.eatopiaapp.api;

import com.reodinas2.eatopiaapp.model.OrderRes;
import com.reodinas2.eatopiaapp.model.RestaurantList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OrderApi {

    @GET("/order/{orderId}")
    Call<OrderRes> getOrder(@Header("Authorization") String token,
                            @Path("orderId") int orderId);



}
