package com.reodinas2.eatopiaapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.reodinas2.eatopiaapp.adapter.RestaurantAdapter;
import com.reodinas2.eatopiaapp.api.NetworkClient;
import com.reodinas2.eatopiaapp.api.RestaurantApi;
import com.reodinas2.eatopiaapp.config.Config;
import com.reodinas2.eatopiaapp.model.Restaurant;
import com.reodinas2.eatopiaapp.model.RestaurantList;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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

    // 멤버변수
    EditText editKeyword;
    ImageView imgSearch;
    RadioButton radioDistance;
    RadioButton radioRating;
    RadioButton radioReview;
    ProgressBar progressBar;

    RecyclerView recyclerView;
    RestaurantAdapter adapter;
    ArrayList<Restaurant> restaurantArrayList = new ArrayList<>();

    // 페이징 처리를 위한 변수
    int count = 0;
    int offset = 0;
    int limit = 20;
    Double lat;
    Double lng;
    String order = "distance";
    String keyword = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);

        editKeyword = rootView.findViewById(R.id.editKeyword);
        imgSearch = rootView.findViewById(R.id.imgSearch);
        radioDistance = rootView.findViewById(R.id.radioDistance);
        radioRating = rootView.findViewById(R.id.radioRating);
        radioReview = rootView.findViewById(R.id.radioReview);
        progressBar = rootView.findViewById(R.id.progressBar);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        getNetworkData();
    }

    public void getNetworkData(){
        progressBar.setVisibility(View.VISIBLE);

        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());

        RestaurantApi api = retrofit.create(RestaurantApi.class);

        offset = 0;

        SharedPreferences sp = getActivity().getSharedPreferences(Config.SP_NAME, Context.MODE_PRIVATE); // 또는 getActivity().MODE_PRIVATE
//        String accessToken = sp.getString(Config.ACCESS_TOKEN, "");
//        String token = "Bearer " + accessToken;

        lat = 37.543;
        lng = 126.6772;
        order = "distance";
        keyword = "";

        Call<RestaurantList> call = api.getRestaurantList(lat, lng, offset, limit, order, keyword);

        call.enqueue(new Callback<RestaurantList>() {
            @Override
            public void onResponse(Call<RestaurantList> call, Response<RestaurantList> response) {
                progressBar.setVisibility(View.GONE);

                if(response.isSuccessful()){

                    restaurantArrayList.clear();

                    count = response.body().getCount();
                    restaurantArrayList.addAll( response.body().getItems() );

                    offset = offset + count;

                    adapter = new RestaurantAdapter(getActivity(), restaurantArrayList);

                    recyclerView.setAdapter(adapter);

                }else{
                    Log.e("LOGCAT", response.body().getError());
                    Log.e("LOGCAT", response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<RestaurantList> call, Throwable t) {
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

//        String token = "Bearer " + accessToken;

        Call<RestaurantList> call = api.getRestaurantList(lat, lng, offset, limit, order, keyword);

        call.enqueue(new Callback<RestaurantList>() {
            @Override
            public void onResponse(Call<RestaurantList> call, Response<RestaurantList> response) {
                progressBar.setVisibility(View.GONE);

                if(response.isSuccessful()){

                    count = response.body().getCount();
                    restaurantArrayList.addAll( response.body().getItems() );

                    adapter.notifyItemRangeInserted(offset, count);

                    offset = offset + count;

                } else{
                    Toast.makeText(getActivity(), "정상적으로 처리되지 않았습니다.", Toast.LENGTH_SHORT).show();
                    Log.e("LOGCAT", response.body().getError());
                    Log.e("LOGCAT", response.body().getMessage());
                }

            }

            @Override
            public void onFailure(Call<RestaurantList> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "정상적으로 처리되지 않았습니다.", Toast.LENGTH_SHORT).show();
                Log.i("LOGCAT", String.valueOf(t));
            }
        });
    }

}