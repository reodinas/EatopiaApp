package com.reodinas2.eatopiaapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.reodinas2.eatopiaapp.adapter.MenuAdapter;
import com.reodinas2.eatopiaapp.adapter.RestaurantAdapter;
import com.reodinas2.eatopiaapp.api.NetworkClient;
import com.reodinas2.eatopiaapp.api.RestaurantApi;
import com.reodinas2.eatopiaapp.model.Menu;
import com.reodinas2.eatopiaapp.model.MenuInfo;
import com.reodinas2.eatopiaapp.model.MenuList;
import com.reodinas2.eatopiaapp.model.Restaurant;
import com.reodinas2.eatopiaapp.model.RestaurantList;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RestaurantMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RestaurantMenuFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RestaurantMenuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RestaurantMenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RestaurantMenuFragment newInstance(String param1, String param2) {
        RestaurantMenuFragment fragment = new RestaurantMenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    Button btnOrder;
    ProgressBar progressBar;
    TextView txtNoResult;
    RecyclerView recyclerView;
    MenuAdapter adapter;
    ArrayList<Menu> menuArrayList = new ArrayList<>();
    int restaurantId;
    // 페이징 처리를 위한 변수
    int count = 0;
    int offset = 0;
    int limit = 20;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_restaurant_menu, container, false);

        Restaurant restaurant = (Restaurant) getArguments().getSerializable("restaurant");
        restaurantId = restaurant.getId();

        btnOrder = rootView.findViewById(R.id.btnOrder);
        progressBar = rootView.findViewById(R.id.progressBar);
        txtNoResult = rootView.findViewById(R.id.txtNoResult);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // 리사이클러뷰 페이징처리
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastPosition = ( (LinearLayoutManager)recyclerView.getLayoutManager() ).findLastCompletelyVisibleItemPosition();
                int totalCount = recyclerView.getAdapter().getItemCount();
                if(lastPosition + 1 == totalCount){
                    // 네트워크 통해서 데이터를 더 불러온다.
                    if(count == limit){
                        addNetworkData();
                    }
                }
            }
        });

        getNetworkData();

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Menu> selectedMenuList = adapter.getSelectedMenuList();
                if (selectedMenuList.size() > 0) {
                    Intent intent = new Intent(getActivity(), OrderActivity.class);
                    intent.putExtra("restaurantId", restaurantId);
                    intent.putParcelableArrayListExtra("selectedMenuList", selectedMenuList);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "주문할 메뉴를 선택해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    public void getNetworkData(){
        progressBar.setVisibility(View.VISIBLE);

        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());

        RestaurantApi api = retrofit.create(RestaurantApi.class);

        // 오프셋 초기화
        offset = 0;

//        SharedPreferences sp = getActivity().getSharedPreferences(Config.SP_NAME, Context.MODE_PRIVATE); // 또는 getActivity().MODE_PRIVATE
//        String accessToken = sp.getString(Config.ACCESS_TOKEN, "");
//        String token = "Bearer " + accessToken;


        Call<MenuList> call = api.getMenuList(restaurantId, offset, limit);
        call.enqueue(new Callback<MenuList>() {
            @Override
            public void onResponse(Call<MenuList> call, Response<MenuList> response) {
                progressBar.setVisibility(View.GONE);

                if(response.isSuccessful()){

                    menuArrayList.clear();

                    count = response.body().getCount();
                    menuArrayList.addAll( response.body().getItems() );

                    offset = offset + count;

                    adapter = new MenuAdapter(getActivity(), menuArrayList);

                    recyclerView.setAdapter(adapter);

                    // 리스트가 비어있으면 txtNoResult를 표시
                    if (menuArrayList.isEmpty()) {
                        txtNoResult.setVisibility(View.VISIBLE);
                    } else {
                        txtNoResult.setVisibility(View.GONE);
                    }

                }else {
                    Toast.makeText(getActivity(), "정상적으로 처리되지 않았습니다.", Toast.LENGTH_SHORT).show();

                    try {
                        JSONObject errorJson = new JSONObject(response.errorBody().string());
                        String errorMessage = errorJson.getString("error");
                        Toast.makeText(getActivity(), "" + errorMessage, Toast.LENGTH_SHORT).show();
                        Log.i("LOGCAT", "에러 상태코드: " + response.code() + ", 메시지: " + errorMessage);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<MenuList> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "정상적으로 처리되지 않았습니다.", Toast.LENGTH_SHORT).show();
                Log.i("LOGCAT", String.valueOf(t));
            }
        });

    }

    private void addNetworkData() {
        progressBar.setVisibility(View.VISIBLE);

        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());

        RestaurantApi api = retrofit.create(RestaurantApi.class);

        Call<MenuList> call = api.getMenuList(restaurantId, offset, limit);
        call.enqueue(new Callback<MenuList>() {
            @Override
            public void onResponse(Call<MenuList> call, Response<MenuList> response) {
                progressBar.setVisibility(View.GONE);

                if(response.isSuccessful()){

                    count = response.body().getCount();
                    menuArrayList.addAll( response.body().getItems() );

                    adapter.notifyItemRangeInserted(offset, count);

                    offset = offset + count;

                    // 리스트가 비어있으면 txtNoResult를 표시
                    if (menuArrayList.isEmpty()) {
                        txtNoResult.setVisibility(View.VISIBLE);
                    } else {
                        txtNoResult.setVisibility(View.GONE);
                    }

                }else {
                    Toast.makeText(getActivity(), "정상적으로 처리되지 않았습니다.", Toast.LENGTH_SHORT).show();

                    try {
                        JSONObject errorJson = new JSONObject(response.errorBody().string());
                        String errorMessage = errorJson.getString("error");
                        Toast.makeText(getActivity(), "" + errorMessage, Toast.LENGTH_SHORT).show();
                        Log.i("LOGCAT", "에러 상태코드: " + response.code() + ", 메시지: " + errorMessage);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<MenuList> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "정상적으로 처리되지 않았습니다.", Toast.LENGTH_SHORT).show();
                Log.i("LOGCAT", String.valueOf(t));
            }
        });
    }

}