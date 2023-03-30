package com.reodinas2.eatopiaapp.api;

import com.reodinas2.eatopiaapp.model.MyOrderList;
import com.reodinas2.eatopiaapp.model.OrderRes;
import com.reodinas2.eatopiaapp.model.RestaurantList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OrderApi {

    // 주문 상세정보 조회
    @GET("/order/{orderId}")
    Call<OrderRes> getOrder(@Header("Authorization") String token,
                            @Path("orderId") int orderId);

    // 내 전체 주문 내역 조회
    @GET("/order")
    Call<MyOrderList> getOrderList(@Header("Authorization") String token,
                                   @Query("offset") int offset,
                                   @Query("limit") int limit);

}
