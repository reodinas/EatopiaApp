package com.reodinas2.eatopiaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.reodinas2.eatopiaapp.api.NetworkClient;
import com.reodinas2.eatopiaapp.api.OrderApi;
import com.reodinas2.eatopiaapp.api.RestaurantApi;
import com.reodinas2.eatopiaapp.config.Config;
import com.reodinas2.eatopiaapp.model.Menu;
import com.reodinas2.eatopiaapp.model.Order;
import com.reodinas2.eatopiaapp.model.OrderRes;
import com.reodinas2.eatopiaapp.model.Restaurant;
import com.reodinas2.eatopiaapp.model.RestaurantRes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OrderConfirmationActivity extends AppCompatActivity {

    int restaurantId;
    int orderId;

    ImageView imgRestaurant;
    TextView txtOrderId;
    TextView txtRestaurantName;
    TextView txtMenu;
    TextView txtPrice;
    TextView txtReservTime;
    TextView txtCreatedAt;
    TextView txtType;
    TextView txtPeople;
    TextView txtAddress;
    TextView txtTel;
    Button btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        imgRestaurant = findViewById(R.id.imgRestaurant);
        txtOrderId = findViewById(R.id.txtOrderId);
        txtRestaurantName = findViewById(R.id.txtRestaurantName);
        txtMenu = findViewById(R.id.txtMenu);
        txtPrice = findViewById(R.id.txtPrice);
        txtReservTime = findViewById(R.id.txtReservTime);
        txtCreatedAt = findViewById(R.id.txtCreatedAt);
        txtType = findViewById(R.id.txtType);
        txtPeople = findViewById(R.id.txtPeople);
        txtAddress = findViewById(R.id.txtAddress);
        txtTel = findViewById(R.id.txtTel);
        btnHome = findViewById(R.id.btnHome);


        // 인텐트에서 restaurantId와 orderId를 추출
        Intent intent = getIntent();
        restaurantId = intent.getIntExtra("restaurantId", 0);
        orderId = intent.getIntExtra("orderId", 0);

        getRestaurantDetails();
        getOrderDetails();


    }

    private void getRestaurantDetails() {
        Retrofit retrofit = NetworkClient.getRetrofitClient(OrderConfirmationActivity.this);
        RestaurantApi api = retrofit.create(RestaurantApi.class);

        Call<RestaurantRes> call = api.getRestaurant(restaurantId);

        call.enqueue(new Callback<RestaurantRes>() {
            @Override
            public void onResponse(Call<RestaurantRes> call, Response<RestaurantRes> response) {
                if (response.isSuccessful()) {
                    Restaurant restaurantInfo = response.body().getRestaurantInfo();

                    // TODO: 응답에서 받은 식당 정보를 사용하여 UI를 업데이트하거나 필요한 작업을 수행합니다.
                    // 화면 세팅
                    String imgUrl = restaurantInfo.getImgUrl();
                    Glide.with(OrderConfirmationActivity.this)
                            .load(imgUrl)
                            .placeholder(R.drawable.baseline_image_24)
                            .centerCrop()
                            .error(R.drawable.baseline_image_24)
                            .into(imgRestaurant);

                    txtRestaurantName.setText(restaurantInfo.getName());
                    String address = restaurantInfo.getLocCity()+" "+restaurantInfo.getLocDistrict()+" "+restaurantInfo.getLocDetail();
                    txtAddress.setText(address);
                    txtTel.setText(restaurantInfo.getTel());


                    
                } else {
                    Toast.makeText(OrderConfirmationActivity.this, "식당 정보를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show();

                    try {
                        JSONObject errorJson = new JSONObject(response.errorBody().string());
                        String errorMessage = errorJson.getString("error");
                        Toast.makeText(OrderConfirmationActivity.this, "" + errorMessage, Toast.LENGTH_SHORT).show();
                        Log.i("LOGCAT", "에러 상태코드: " + response.code() + ", 메시지: " + errorMessage);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<RestaurantRes> call, Throwable t) {
                Toast.makeText(OrderConfirmationActivity.this, "식당 정보를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                Log.e("LOGCAT", String.valueOf(t));
            }
        });
    }

    private void getOrderDetails() {
        Retrofit retrofit = NetworkClient.getRetrofitClient(OrderConfirmationActivity.this);
        OrderApi api = retrofit.create(OrderApi.class);

        // 헤더에 들어갈 액세스 토큰을 가져옵니다.
        SharedPreferences sp = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
        String accessToken = "Bearer " + sp.getString(Config.ACCESS_TOKEN, "");

        Call<OrderRes> call = api.getOrder(accessToken, orderId);

        call.enqueue(new Callback<OrderRes>() {
            @Override
            public void onResponse(Call<OrderRes> call, Response<OrderRes> response) {
                if (response.isSuccessful()) {
                    Order orderInfo = response.body().getOrderInfo();
                    List<Menu> menuInfo = response.body().getMenuInfo();

                    // TODO: 응답에서 받은 주문 정보와 메뉴 정보를 사용하여 UI를 업데이트하거나 필요한 작업을 수행합니다.
                    // 화면 세팅
                    txtOrderId.setText(String.valueOf(orderInfo.getId()));
                    txtPeople.setText(String.valueOf(orderInfo.getPeople()));


                    txtMenu.setText(menuInfo.get(0).getMenuName() + "외 "+ (menuInfo.size()-1) + "개");
//                    int price = 0;
//                    for (Menu menu : menuInfo){
//                        price + menu.getPrice()
//                    }


                } else {
                    Toast.makeText(OrderConfirmationActivity.this, "주문 정보를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show();

                    try {
                        JSONObject errorJson = new JSONObject(response.errorBody().string());
                        String errorMessage = errorJson.getString("error");
                        Toast.makeText(OrderConfirmationActivity.this, "" + errorMessage, Toast.LENGTH_SHORT).show();
                        Log.i("LOGCAT", "에러 상태코드: " + response.code() + ", 메시지: " + errorMessage);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<OrderRes> call, Throwable t) {
                Toast.makeText(OrderConfirmationActivity.this, "주문 정보를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                Log.e("LOGCAT", String.valueOf(t));
            }
        });
    }


}