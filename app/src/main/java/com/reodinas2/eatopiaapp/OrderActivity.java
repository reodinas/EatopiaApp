package com.reodinas2.eatopiaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.reodinas2.eatopiaapp.api.NetworkClient;
import com.reodinas2.eatopiaapp.api.RestaurantApi;
import com.reodinas2.eatopiaapp.config.Config;
import com.reodinas2.eatopiaapp.model.Menu;
import com.reodinas2.eatopiaapp.model.RestaurantOrder;
import com.reodinas2.eatopiaapp.model.RestaurantOrderRes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OrderActivity extends AppCompatActivity {

    int restaurantId;
    DatePicker datePicker;
    TimePicker timePicker;
    RadioGroup radioGroup;
    RadioButton radioVisit, radioTakeout;
    TextView txt;
    LinearLayout linearLayout;
    TextView txtPeople;
    ImageView imgMinus;
    ImageView imgPlus;
    Button btnOrder;
    ProgressDialog dialog;
    ArrayList<Menu> selectedMenuList;
    int people = 1;
    int type = 0;
    SimpleDateFormat sf;
    SimpleDateFormat df;
    String reservTime;
    String reservTimeUTC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        radioGroup = findViewById(R.id.radioGroup);
        radioVisit = findViewById(R.id.radioVisit);
        radioTakeout = findViewById(R.id.radioTakeout);
        txt = findViewById(R.id.txt);
        linearLayout = findViewById(R.id.linearLayout);
        txtPeople = findViewById(R.id.txtPeople);
        imgMinus = findViewById(R.id.imgMinus);
        imgPlus = findViewById(R.id.imgPlus);
        btnOrder = findViewById(R.id.btnOrder);

        // 인텐트로부터 데이터를 받아온다.
        Intent intent = getIntent();
        restaurantId = intent.getIntExtra("restaurantId", 0);
        selectedMenuList  = intent.getParcelableArrayListExtra("selectedMenuList");

//        Log.i("SelectedMenuList", "menuName: " + selectedMenuList.get(selectedMenuList.size()-1).getMenuName());
//        Log.i("SelectedMenuList", "count: " + selectedMenuList.get(selectedMenuList.size()-1).getCount());

        // 매장인 경우 0, 포장인 경우 1
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioVisit) {
                txt.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.VISIBLE);
                type = 0;
                people = Integer.parseInt(txtPeople.getText().toString());
            } else {
                txt.setVisibility(View.GONE);
                linearLayout.setVisibility(View.GONE);
                type = 1;
                people = 0;
            }
        });

        // 인원수
        imgMinus.setOnClickListener(view -> {
            if (people > 1) {
                people--;
                txtPeople.setText(String.valueOf(people));
            }
        });

        imgPlus.setOnClickListener(view -> {
            people++;
            txtPeople.setText(String.valueOf(people));
        });

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View v) {

                // DatePicker와 TimePicker에서 날짜와 시간을 가져온다
                int year = datePicker.getYear();
                int month = datePicker.getMonth() + 1; // month는 0부터 시작하므로 +1
                int day = datePicker.getDayOfMonth();
                int hour = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();
                // 날짜와 시간을 문자열 형식으로 변환
                reservTime = String.format("%04d-%02d-%02d %02d:%02d", year, month, day, hour, minute);

                // 대화상자를 생성
                AlertDialog.Builder builder = new AlertDialog.Builder(OrderActivity.this);
                builder.setTitle("주문 하시겠습니까?");
                builder.setMessage("예약시간: " + reservTime + "\n" +
                        (type == 0 ? "인원: " + people + "\n" : "") +  // 인원수를 표시할지 결정
                        "주문종류: " + (type == 0 ? "매장" : "포장") +
                        "\n메뉴: " + getMenuInfoString());
                builder.setPositiveButton("예", (dialog, which) -> {
                    // '예'를 클릭했을 때 API 호출을 진행.
                    makeOrder();
                });
                builder.setNegativeButton("아니오", (dialog, which) -> dialog.dismiss());

                // 대화상자를 표시
                AlertDialog alertDialog = builder.create();
                alertDialog.show();


            }
        });


    }

    // 메뉴 이름과 수량을 더한 문자열 생성
    private String getMenuInfoString() {
        StringBuilder menuInfoString = new StringBuilder();
        int menuSize = selectedMenuList.size();

        for (int i = 0; i < menuSize; i++) {
            Menu menu = selectedMenuList.get(i);
            menuInfoString.append(menu.getMenuName()).append(" ").append(menu.getCount()).append("개");

            if (i != menuSize - 1) {
                menuInfoString.append(", ");
            }
        }

        return menuInfoString.toString();
    }

    @SuppressLint("SimpleDateFormat")
    private void makeOrder() {
        showProgress("주문 접수 중...");

        Retrofit retrofit = NetworkClient.getRetrofitClient(OrderActivity.this);
        RestaurantApi api = retrofit.create(RestaurantApi.class);

        // 헤더에 들어갈 억세스토큰 가져온다.
        SharedPreferences sp = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
        String accessToken = "Bearer " + sp.getString(Config.ACCESS_TOKEN, "");

        // Local Time => UTC
        sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        sf.setTimeZone(TimeZone.getTimeZone("UTC"));
        df.setTimeZone(TimeZone.getDefault());

        try {
            Date date = df.parse(reservTime);
            reservTimeUTC = sf.format(date);

        } catch (ParseException e) {
            Toast.makeText(OrderActivity.this, "예약 시간 변환에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            dismissProgress();
            Log.e("PARSE_ERROR", String.valueOf(e));
            return;
        }

        // Body
        RestaurantOrder restaurantOrder = new RestaurantOrder(people, reservTimeUTC, type, selectedMenuList);

        Call<RestaurantOrderRes> call = api.makeOrder(accessToken, restaurantId, restaurantOrder);

        call.enqueue(new Callback<RestaurantOrderRes>() {
            @Override
            public void onResponse(Call<RestaurantOrderRes> call, Response<RestaurantOrderRes> response) {
                dismissProgress();

                if(response.isSuccessful()){
                    Log.i("LOGCAT", "result: " + response.body().getResult() + ", 주문id: " + response.body().getOrderId());

                    // 성공적으로 주문이 완료되면 OrderConfirmationActivity로 이동
                    Intent intent = new Intent(OrderActivity.this, OrderConfirmationActivity.class);
                    intent.putExtra("restaurantId", restaurantId);
                    intent.putExtra("orderId", response.body().getOrderId());
                    startActivity(intent);

                    // 현재 액티비티를 종료
                    finish();


                }else {
                    Toast.makeText(OrderActivity.this, "정상적으로 처리되지 않았습니다.", Toast.LENGTH_SHORT).show();

                    try {
                        JSONObject errorJson = new JSONObject(response.errorBody().string());
                        String errorMessage = errorJson.getString("error");
                        Toast.makeText(OrderActivity.this, "" + errorMessage, Toast.LENGTH_SHORT).show();
                        Log.i("LOGCAT", "에러 상태코드: " + response.code() + ", 메시지: " + errorMessage);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<RestaurantOrderRes> call, Throwable t) {
                dismissProgress();
                Toast.makeText(OrderActivity.this, "정상적으로 처리되지 않았습니다.", Toast.LENGTH_SHORT).show();
                Log.i("LOGCAT", String.valueOf(t));
            }
        });


    }


    // 네트워크 로직 처리시에 화면에 보여주는 함수
    void showProgress(String message){
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(message);
        dialog.show();
    }


    // 로직처리가 끝나면 화면에서 사라지는 함수
    void dismissProgress(){
        dialog.dismiss();
    }
}