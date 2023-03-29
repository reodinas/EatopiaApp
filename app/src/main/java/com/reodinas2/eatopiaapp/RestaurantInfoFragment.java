package com.reodinas2.eatopiaapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.reodinas2.eatopiaapp.model.Restaurant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RestaurantInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RestaurantInfoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RestaurantInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RestaurantHomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RestaurantInfoFragment newInstance(String param1, String param2) {
        RestaurantInfoFragment fragment = new RestaurantInfoFragment();
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


    ImageView imgRestaurant;
    TextView txtCategory;
    TextView txtDistance;
    TextView txtAvg;
    TextView txtCnt;
    TextView txtAddress;
    TextView txtTel;
    TextView txtSummary;
    TextView txtUpdatedAt;

    SimpleDateFormat sf; // UTC 타임존을 위한 변수
    SimpleDateFormat df; // Local 타임존을 위한 변수

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_restaurant_info, container, false);

        imgRestaurant = rootView.findViewById(R.id.imgRestaurant);
        txtCategory = rootView.findViewById(R.id.txtCategory);
        txtDistance = rootView.findViewById(R.id.txtDistance);
        txtAvg = rootView.findViewById(R.id.txtAvg);
        txtCnt = rootView.findViewById(R.id.txtCnt);
        txtAddress = rootView.findViewById(R.id.txtAddress);
        txtTel = rootView.findViewById(R.id.txtTel);
        txtSummary = rootView.findViewById(R.id.txtSummary);
        txtUpdatedAt = rootView.findViewById(R.id.txtUpdatedAt);



        // 액티비티에서 전달해준 restaurant 객체를 받는다.
        Restaurant restaurant = (Restaurant) getArguments().getSerializable("restaurant");

        // 화면 세팅
        String imgUrl = restaurant.getImgUrl();
        Glide.with(getActivity())
                .load(imgUrl)
                .placeholder(R.drawable.baseline_image_24)
                .centerCrop()
                .error(R.drawable.no_image)
                .into(imgRestaurant);

        txtCategory.setText(restaurant.getCategory());
        txtDistance.setText(restaurant.getDistance()+"m");
        txtAvg.setText(""+restaurant.getAvg());
        txtCnt.setText(""+restaurant.getCnt());
        String address = restaurant.getLocCity()+" "+restaurant.getLocDistrict()+" "+restaurant.getLocDetail();
        txtAddress.setText(address);
        txtTel.setText(restaurant.getTel());
        if(restaurant.getSummary() == null) {
            txtSummary.setText("소개를 준비중입니다.");
        }else{
            txtSummary.setText(restaurant.getSummary());
        }

        // UTC => Local Time
        String updatedAt = restaurant.getUpdatedAt();
        sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        sf.setTimeZone(TimeZone.getTimeZone("UTC"));
        df.setTimeZone(TimeZone.getDefault());
        try {
            Date date = sf.parse(updatedAt);
            txtUpdatedAt.setText(df.format(date));

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }





        return rootView;
    }
}