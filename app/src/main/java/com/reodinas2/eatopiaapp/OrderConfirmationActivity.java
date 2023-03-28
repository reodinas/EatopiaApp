package com.reodinas2.eatopiaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;

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

    Restaurant restaurantInfo;
    Order orderInfo;
    List<Menu> menuInfo;
    CountDownLatch countDownLatch = new CountDownLatch(2);
    private ProgressDialog dialog;
    SimpleDateFormat sf; // UTC 타임존을 위한 변수
    SimpleDateFormat df; // Local 타임존을 위한 변수

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

        showProgress("데이터 로딩 중...");

        // 인텐트에서 restaurantId와 orderId를 추출
        Intent intent = getIntent();
        restaurantId = intent.getIntExtra("restaurantId", 0);
        orderId = intent.getIntExtra("orderId", 0);

        getRestaurantDetails();
        getOrderDetails();

        // countDownLatch를 사용하여 두 개의 네트워크 호출 메서드가 모두 완료될 때까지 기다린 후에 UI를 업데이트
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    countDownLatch.await();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateUI();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    private void getRestaurantDetails() {
        Retrofit retrofit = NetworkClient.getRetrofitClient(OrderConfirmationActivity.this);
        RestaurantApi api = retrofit.create(RestaurantApi.class);

        Call<RestaurantRes> call = api.getRestaurant(restaurantId);

        call.enqueue(new Callback<RestaurantRes>() {
            @Override
            public void onResponse(Call<RestaurantRes> call, Response<RestaurantRes> response) {
                if (response.isSuccessful()) {
                    restaurantInfo = response.body().getRestaurantInfo();

                    // TODO: 응답에서 받은 식당 정보를 사용하여 UI를 업데이트하거나 필요한 작업을 수행합니다.
                    // 화면 세팅
//                    String imgUrl = restaurantInfo.getImgUrl();
//                    Glide.with(OrderConfirmationActivity.this)
//                            .load(imgUrl)
//                            .placeholder(R.drawable.baseline_image_24)
//                            .centerCrop()
//                            .error(R.drawable.baseline_image_24)
//                            .into(imgRestaurant);
//
//                    txtRestaurantName.setText(restaurantInfo.getName());
//                    String address = restaurantInfo.getLocCity()+" "+restaurantInfo.getLocDistrict()+" "+restaurantInfo.getLocDetail();
//                    txtAddress.setText(address);
//                    txtTel.setText(restaurantInfo.getTel());


                    
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

                countDownLatch.countDown();
            }

            @Override
            public void onFailure(Call<RestaurantRes> call, Throwable t) {
                Toast.makeText(OrderConfirmationActivity.this, "식당 정보를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                Log.e("LOGCAT", String.valueOf(t));

                countDownLatch.countDown();
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
                    orderInfo = response.body().getOrderInfo();
                    menuInfo = response.body().getMenuInfo();

                    // TODO: 응답에서 받은 주문 정보와 메뉴 정보를 사용하여 UI를 업데이트하거나 필요한 작업을 수행합니다.
                    // 화면 세팅
//                    txtPeople.setText(""+orderInfo.getPeople());
//                    txtMenu.setText(menuInfo.get(0).getMenuName()+"외 "+menuInfo.size()+" 개");


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

                countDownLatch.countDown();
            }

            @Override
            public void onFailure(Call<OrderRes> call, Throwable t) {
                Toast.makeText(OrderConfirmationActivity.this, "주문 정보를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                Log.e("LOGCAT", String.valueOf(t));

                countDownLatch.countDown();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void updateUI() {
        if (restaurantInfo != null && orderInfo != null && menuInfo != null) {
            // restaurantInfo
            String imgUrl = restaurantInfo.getImgUrl();
            Glide.with(OrderConfirmationActivity.this)
                    .load(imgUrl)
                    .placeholder(R.drawable.baseline_image_24)
                    .centerCrop()
                    .error(R.drawable.baseline_image_24)
                    .into(imgRestaurant);

            txtRestaurantName.setText("식당명: " + restaurantInfo.getName());
            String address = restaurantInfo.getLocCity()+" "+restaurantInfo.getLocDistrict()+" "+restaurantInfo.getLocDetail();
            txtAddress.setText("식당위치: " + address);
            txtTel.setText(restaurantInfo.getTel());

            // orderInfo
            txtOrderId.setText("주문번호: "+orderInfo.getId());
            txtPeople.setText(String.valueOf(orderInfo.getPeople()));

            String priceText;
            if (orderInfo.getPriceSum() == -1) {
                priceText = "가격정보 없음";
            }else {
                priceText = String.valueOf(orderInfo.getPriceSum());
            }
            txtPrice.setText("가격: " + priceText);

            String typeText;
            if (orderInfo.getType() == 1){
                typeText = "포장";
            } else {
                typeText = "매장";
            }
            txtType.setText("주문종류: " + typeText);

            // UTC => Local Time
            String reservTime = orderInfo.getReservTime();
            String createdAt = orderInfo.getCreatedAt();
            sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            sf.setTimeZone(TimeZone.getTimeZone("UTC"));
            df.setTimeZone(TimeZone.getDefault());
            try {
                Date dateReservTime = sf.parse(reservTime);
                txtReservTime.setText(df.format(dateReservTime));

                Date dateCreatedAt = sf.parse(createdAt);
                txtCreatedAt.setText(df.format(dateCreatedAt));

            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            // menuInfo
            StringBuilder menuText = new StringBuilder();
            int menuCount = menuInfo.size();
            int maxDisplayedMenus = Math.min(3, menuCount);

            for (int i = 0; i < maxDisplayedMenus; i++) {
                Menu menu = menuInfo.get(i);
                menuText.append(menu.getMenuName()).append(" ").append(menu.getCount()).append("개");

                if (i < maxDisplayedMenus - 1) {
                    menuText.append(", ");
                }
            }

            if (menuCount > 3) {
                menuText.append(" 외 ").append(menuCount - 3).append("종");
            }

            txtMenu.setText(menuText.toString());


        } else {
            Toast.makeText(OrderConfirmationActivity.this, "식당 정보 또는 주문 정보를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
        }

        dismissProgress();

    }

    // 네트워크 로직 처리시에 화면에 보여주는 함수
    public void showProgress(String message){
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(message);
        dialog.show();
    }


    // 로직처리가 끝나면 화면에서 사라지는 함수
    public void dismissProgress(){
        dialog.dismiss();
    }

}