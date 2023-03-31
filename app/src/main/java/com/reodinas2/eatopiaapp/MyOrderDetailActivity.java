package com.reodinas2.eatopiaapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.reodinas2.eatopiaapp.model.Menu;
import com.reodinas2.eatopiaapp.model.MyOrder;
import com.reodinas2.eatopiaapp.model.Order;
import com.reodinas2.eatopiaapp.model.Restaurant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MyOrderDetailActivity extends AppCompatActivity {

    ImageView imgRestaurant;
    TextView txtRestaurantName;
    TextView txtOrderId;
    TextView txtIsVisited;
    TextView txtMenu;
    TextView txtPrice;
    TextView txtReservTime;
    TextView txtCreatedAt;
    TextView txtType;
    TextView txtPeople;
    TextView txtAddress;
    TextView txtTel;
    Button btnHome;
    LinearLayout restaurantLayout;

    MyOrder myOrder;
    Order orderInfo;
    Restaurant restaurantInfo;
    List<Menu> menuInfo;

    SimpleDateFormat sf; // UTC 타임존을 위한 변수
    SimpleDateFormat df; // Local 타임존을 위한 변수


    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order_detail);

        imgRestaurant = findViewById(R.id.imgRestaurant);
        txtRestaurantName = findViewById(R.id.txtRestaurantName);
        txtOrderId = findViewById(R.id.txtOrderId);
        txtIsVisited = findViewById(R.id.txtIsVisited);
        txtMenu = findViewById(R.id.txtMenu);
        txtPrice = findViewById(R.id.txtPrice);
        txtReservTime = findViewById(R.id.txtReservTime);
        txtCreatedAt = findViewById(R.id.txtCreatedAt);
        txtType = findViewById(R.id.txtType);
        txtPeople = findViewById(R.id.txtPeople);
        txtAddress = findViewById(R.id.txtAddress);
        txtTel = findViewById(R.id.txtTel);
        btnHome = findViewById(R.id.btnHome);
        restaurantLayout = findViewById(R.id.restaurantLayout);

        // 인텐트로부터 myOrder 객체 가져오기
        myOrder = getIntent().getParcelableExtra("myOrder");
        orderInfo = myOrder.getOrderInfo();
        restaurantInfo = myOrder.getRestaurantInfo();
        menuInfo = myOrder.getMenuInfo();

        sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sf.setTimeZone(TimeZone.getTimeZone("UTC"));
        df.setTimeZone(TimeZone.getDefault());

        // 화면 세팅
        updateUI();

        restaurantLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyOrderDetailActivity.this, RestaurantActivity.class);
                intent.putExtra("restaurant", restaurantInfo);
                intent.putExtra("from", "myOrder");
                startActivity(intent);
            }

        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyOrderDetailActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void updateUI() {
        if (restaurantInfo != null && orderInfo != null && menuInfo != null) {
            // restaurantInfo

            // 이미지
            String imgUrl = restaurantInfo.getImgUrl();
            Glide.with(MyOrderDetailActivity.this)
                    .load(imgUrl)
                    .placeholder(R.drawable.baseline_image_24)
                    .centerCrop()
                    .error(R.drawable.no_image)
                    .into(imgRestaurant);

            // 식당명
            txtRestaurantName.setText(restaurantInfo.getName());

            // 주소
            String address = restaurantInfo.getLocCity()+" "+restaurantInfo.getLocDistrict()+" "+restaurantInfo.getLocDetail();
            txtAddress.setText(address);
            // 전화
            txtTel.setText(restaurantInfo.getTel());

            // orderInfo

            // 주문번호
            txtOrderId.setText(""+orderInfo.getId());
            // 인원
            txtPeople.setText(String.valueOf(orderInfo.getPeople()));

            // 방문여부
            String isVisitedText;
            if (orderInfo.getIsVisited() == 0) {
                isVisitedText = "미방문";
                txtIsVisited.setText(isVisitedText);
                txtIsVisited.setTextColor(ContextCompat.getColor(MyOrderDetailActivity.this, R.color.fail_color));
            }else {
                isVisitedText = "방문";
                txtIsVisited.setText(isVisitedText);
                txtIsVisited.setTextColor(ContextCompat.getColor(MyOrderDetailActivity.this, R.color.success_color));
            }


            // 가격
            String priceText;
            if (orderInfo.getPriceSum() == -1) {
                priceText = "가격정보 없음";
            }else {
                priceText = String.valueOf(orderInfo.getPriceSum());
            }
            txtPrice.setText(priceText);

            // 주문종류
            String typeText;
            if (orderInfo.getType() == 1){
                typeText = "포장";
            } else {
                typeText = "매장";
            }
            txtType.setText(typeText);

            // 예약시간, 주문시간
            // UTC => Local Time
            try {
                Date dateReservTime = sf.parse(orderInfo.getReservTime());
                Date dateCreatedAt = sf.parse(orderInfo.getCreatedAt());

                String localReservTime = df.format(dateReservTime);
                String localCreatedAt = df.format(dateCreatedAt);

                txtReservTime.setText(localReservTime);
                txtCreatedAt.setText(localCreatedAt);

                // 현재 시간과 비교
                Date currentTime = new Date();
                Date localReservDateTime = df.parse(localReservTime);
                if (localReservDateTime.before(currentTime)) {
                    // 예약 시간이 현재 시간 이전인 경우
                    txtReservTime.setTextColor(ContextCompat.getColor(MyOrderDetailActivity.this, R.color.past_color));
                } else {
                    // 예약 시간이 현재 시간 이후인 경우
                    txtReservTime.setTextColor(ContextCompat.getColor(MyOrderDetailActivity.this, R.color.future_color));
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            // 메뉴
            txtMenu.setText(getMenuInfoString());


        } else {
            Toast.makeText(MyOrderDetailActivity.this, "식당 정보 또는 주문 정보를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
        }


    }

    // 메뉴 이름과 수량을 더한 문자열 생성
    private String getMenuInfoString() {
        StringBuilder menuInfoString = new StringBuilder();
        int menuSize = menuInfo.size();

        for (int i = 0; i < menuSize; i++) {
            Menu menu = menuInfo.get(i);
            menuInfoString.append(menu.getMenuName()).append(" ").append(menu.getCount()).append("개");

            if (i != menuSize - 1) {
                menuInfoString.append(", ");
            }
        }

        return menuInfoString.toString();
    }
}