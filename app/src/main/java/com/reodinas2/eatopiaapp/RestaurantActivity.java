package com.reodinas2.eatopiaapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.reodinas2.eatopiaapp.model.Restaurant;

public class RestaurantActivity extends AppCompatActivity {

    TabLayout tabLayout;
    Fragment restaurantInfoFragment;
    Fragment restaurantMenuFragment;
    Fragment restaurantReviewFragment;
    TextView txtName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        restaurantInfoFragment = new RestaurantInfoFragment();
        restaurantMenuFragment = new RestaurantMenuFragment();
        restaurantReviewFragment = new RestaurantReviewFragment();
        tabLayout = findViewById(R.id.tabLayout);
        txtName = findViewById(R.id.txtName);

        // 인텐트로부터 Restaurant 객체 가져오기
        Restaurant restaurant = getIntent().getParcelableExtra("restaurant");
        // 이동 경로 가져오기
        String from = getIntent().getStringExtra("from");

        // 식당이름을 세팅
        txtName.setText(restaurant.getName());

        // 프래그먼트에 Restaurant 객체와 이동경로 전달하기
        Bundle bundle = new Bundle();
        bundle.putParcelable("restaurant", restaurant);
        bundle.putString("from", from);

        restaurantInfoFragment.setArguments(bundle);
        restaurantMenuFragment.setArguments(bundle);
        restaurantReviewFragment.setArguments(bundle);

        // 디폴트 화면을 restaurantInfoFragment로 설정
        getSupportFragmentManager().beginTransaction().replace(R.id.container, restaurantInfoFragment).commit();

        tabLayout.addTab(tabLayout.newTab().setText("정보"));
        tabLayout.addTab(tabLayout.newTab().setText("메뉴"));
        tabLayout.addTab(tabLayout.newTab().setText("리뷰"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // 탭이 선택되었을 때 실행할 코드
                int position = tab.getPosition();

                Fragment fragment = null;
                if(position == 0)
                    fragment = restaurantInfoFragment;
                else if(position == 1)
                    fragment = restaurantMenuFragment;
                else if(position == 2)
                    fragment = restaurantReviewFragment;

                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // 탭 선택이 해제되었을 때 실행할 코드
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // 이미 선택된 탭이 다시 선택되었을 때 실행할 코드
            }
        });




    }


}