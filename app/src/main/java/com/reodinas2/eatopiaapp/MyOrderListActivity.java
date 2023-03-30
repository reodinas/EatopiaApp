package com.reodinas2.eatopiaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.reodinas2.eatopiaapp.adapter.OrderAdapter;
import com.reodinas2.eatopiaapp.api.NetworkClient;
import com.reodinas2.eatopiaapp.api.OrderApi;
import com.reodinas2.eatopiaapp.api.UserApi;
import com.reodinas2.eatopiaapp.config.Config;
import com.reodinas2.eatopiaapp.model.MyOrder;
import com.reodinas2.eatopiaapp.model.MyOrderList;
import com.reodinas2.eatopiaapp.model.Order;
import com.reodinas2.eatopiaapp.model.UserInfoRes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MyOrderListActivity extends AppCompatActivity {

    ImageButton btnBack;

    RecyclerView recyclerView;
    OrderAdapter adapter;
    ArrayList<MyOrder> myOrderArrayList = new ArrayList<>();

    ProgressDialog dialog;
    String accessToken;

    // 페이징 처리를 위한 변수
    int count = 0;
    int offset = 0;
    int limit = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order_list);

        btnBack = findViewById(R.id.btnBack);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MyOrderListActivity.this));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastPosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int totalCount = recyclerView.getAdapter().getItemCount();
                if(lastPosition+1 == totalCount){
                    // 네트워크 통해서 데이터를 더 불러온다.
                    if(count == limit){
                        addNetworkData();
                    }
                }
            }
        });

        // 헤더에 들어갈 액세스 토큰을 가져온다
        SharedPreferences sp = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
        accessToken = "Bearer " + sp.getString(Config.ACCESS_TOKEN, "");

        getNetworkData();
    }

    private void getNetworkData() {
        showProgress("데이터 로딩 중...");

        Retrofit retrofit = NetworkClient.getRetrofitClient(MyOrderListActivity.this);
        OrderApi api = retrofit.create(OrderApi.class);

        offset = 0;

        Call<MyOrderList> call = api.getOrderList(accessToken, offset, limit);

        call.enqueue(new Callback<MyOrderList>() {
            @Override
            public void onResponse(Call<MyOrderList> call, Response<MyOrderList> response) {
                dismissProgress();

                if (response.isSuccessful()) {

                    myOrderArrayList.clear();

                    count = response.body().getCount();
                    myOrderArrayList.addAll( response.body().getItems() );

                    offset = offset + count;

                    adapter = new OrderAdapter(MyOrderListActivity.this, myOrderArrayList);
                    recyclerView.setAdapter(adapter);

                } else {
                    Toast.makeText(MyOrderListActivity.this, "주문 내역을 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show();

                    try {
                        JSONObject errorJson = new JSONObject(response.errorBody().string());
                        String errorMessage = errorJson.getString("error");
//                        Toast.makeText(MyOrderListActivity.this, "" + errorMessage, Toast.LENGTH_SHORT).show();
                        Log.i("LOGCAT", "에러 상태코드: " + response.code() + ", 메시지: " + errorMessage);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }


            }

            @Override
            public void onFailure(Call<MyOrderList> call, Throwable t) {
                dismissProgress();
                Toast.makeText(MyOrderListActivity.this, "주문 내역을 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                Log.e("LOGCAT", String.valueOf(t));

            }
        });
    }

    private void addNetworkData() {
        showProgress("데이터 로딩 중...");

        Retrofit retrofit = NetworkClient.getRetrofitClient(MyOrderListActivity.this);
        OrderApi api = retrofit.create(OrderApi.class);

        Call<MyOrderList> call = api.getOrderList(accessToken, offset, limit);

        call.enqueue(new Callback<MyOrderList>() {
            @Override
            public void onResponse(Call<MyOrderList> call, Response<MyOrderList> response) {
                dismissProgress();

                if (response.isSuccessful()) {

                    count = response.body().getCount();
                    myOrderArrayList.addAll( response.body().getItems() );

                    adapter.notifyItemRangeInserted(offset, count);

                    offset = offset + count;

                } else {
                    Toast.makeText(MyOrderListActivity.this, "주문 내역을 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show();

                    try {
                        JSONObject errorJson = new JSONObject(response.errorBody().string());
                        String errorMessage = errorJson.getString("error");
//                        Toast.makeText(MyOrderListActivity.this, "" + errorMessage, Toast.LENGTH_SHORT).show();
                        Log.i("LOGCAT", "에러 상태코드: " + response.code() + ", 메시지: " + errorMessage);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<MyOrderList> call, Throwable t) {
                dismissProgress();
                Toast.makeText(MyOrderListActivity.this, "주문 내역을 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                Log.e("LOGCAT", String.valueOf(t));

            }
        });
    }

    // 네트워크 로직 처리시에 화면에 보여주는 함수
    public void showProgress(String message){
        dialog = new ProgressDialog(MyOrderListActivity.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(message);
        dialog.show();
    }


    // 로직처리가 끝나면 화면에서 사라지는 함수
    public void dismissProgress(){
        dialog.dismiss();
    }
}