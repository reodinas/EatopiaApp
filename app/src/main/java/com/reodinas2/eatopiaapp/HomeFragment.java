package com.reodinas2.eatopiaapp;

import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import android.widget.RadioGroup;
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
    ImageView imgDelete;
    RadioGroup radioGroup;
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
    // 현재 위도, 경도
    Double lat;
    Double lng;
    // 식당리스트를 가져올 당시의 위도,경도
    Double latAtSearch;
    Double lngAtSearch;
    String order = "dist";
    String keyword = "";

    LocationManager locationManager;
    LocationListener locationListener;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);

        editKeyword = rootView.findViewById(R.id.editKeyword);
        imgSearch = rootView.findViewById(R.id.imgSearch);
        imgDelete = rootView.findViewById(R.id.imgDelete);
        radioGroup = rootView.findViewById(R.id.radioGroup);
        radioDistance = rootView.findViewById(R.id.radioDistance);
        radioRating = rootView.findViewById(R.id.radioRating);
        radioReview = rootView.findViewById(R.id.radioReview);
        progressBar = rootView.findViewById(R.id.progressBar);

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

        // locationManager 및 locationListener 초기화
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                lat = location.getLatitude();
                lng = location.getLongitude();
                Log.i("myLocation", "위도 : " + lat);
                Log.i("myLocation", "경도 : " + lng);

            }
        };

        // 검색
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyword = editKeyword.getText().toString().trim();
                getNetworkData();
            }
        });

        // 검색어 삭제
        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editKeyword.setText("");
                keyword = editKeyword.getText().toString().trim();
                getNetworkData();
            }
        });

        // RadioGroup 체크 기본값
        radioDistance.setChecked(true);
        // RadioGroup의 OnCheckedChangeListener를 설정.
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioDistance:
                        order = "dist";
                        break;
                    case R.id.radioRating:
                        order = "avg";
                        break;
                    case R.id.radioReview:
                        order = "cnt";
                        break;
                }
                getNetworkData();
            }
        });




        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        checkLocationPermission();

    }

    @Override
    public void onPause() {
        super.onPause();

        stopLocationUpdates();
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

        latAtSearch = lat;
        lngAtSearch = lng;

        Call<RestaurantList> call = api.getRestaurantList(latAtSearch, lngAtSearch, offset, limit, order, keyword);

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

        Call<RestaurantList> call = api.getRestaurantList(latAtSearch, lngAtSearch, offset, limit, order, keyword);

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


    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            Toast.makeText(getActivity(), "위치권한을 허용하지 않으면 앱을 사용하실 수 없습니다.", Toast.LENGTH_SHORT).show();
        } else {
            setInitialLocation();
            startLocationUpdates();
            getNetworkData();
        }
    }

    private void setInitialLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                lat = lastKnownLocation.getLatitude();
                lng = lastKnownLocation.getLongitude();
                Log.i("myLocation", "초기 위도 : " + lat);
                Log.i("myLocation", "초기 경도 : " + lng);
            }
        }
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 3, locationListener);
        }
    }


    private void stopLocationUpdates() {
        locationManager.removeUpdates(locationListener);
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 허용되었을 때 위치 업데이트 시작
                setInitialLocation();
                startLocationUpdates();
                getNetworkData();

            } else {
                Toast.makeText(getActivity(), "위치권한을 허용하지 않으면 앱을 사용하실 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }


}