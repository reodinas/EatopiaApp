package com.reodinas2.eatopiaapp.api;

import com.reodinas2.eatopiaapp.model.RestaurantList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RestaurantApi {

    //식당리스트 조회 API
    @GET("/restaurant")
    Call<RestaurantList> getRestaurantList(@Query("lat") Double lat,
                                           @Query("lng") Double lng,
                                           @Query("offset") int offset,
                                           @Query("limit") int limit,
                                           @Query("order") String order,
                                           @Query("keyword") String keyword);
}
