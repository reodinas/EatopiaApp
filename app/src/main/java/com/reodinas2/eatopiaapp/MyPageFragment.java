package com.reodinas2.eatopiaapp;

import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.reodinas2.eatopiaapp.api.NetworkClient;
import com.reodinas2.eatopiaapp.api.OrderApi;
import com.reodinas2.eatopiaapp.api.UserApi;
import com.reodinas2.eatopiaapp.config.Config;
import com.reodinas2.eatopiaapp.model.OrderRes;
import com.reodinas2.eatopiaapp.model.UserInfoRes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyPageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyPageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyPageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyPageFragment newInstance(String param1, String param2) {
        MyPageFragment fragment = new MyPageFragment();
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

    ImageView imgFace;
    TextView txtNickname;
    TextView txtEmail;
    ImageView imgEditUser;
    Button btnMyOrder;
    Button btnMyReview;
    Button btnMyFavorite;
    Button btnAddFace;
    ProgressBar progressBar;
    ProgressDialog dialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_my_page, container, false);

        showProgress("데이터 로딩 중...");

        imgFace = rootView.findViewById(R.id.imgFace);
        txtNickname = rootView.findViewById(R.id.txtNickname);
        txtEmail = rootView.findViewById(R.id.txtEmail);
        imgEditUser = rootView.findViewById(R.id.imgEditUser);
        btnMyOrder = rootView.findViewById(R.id.btnMyOrder);
        btnMyReview = rootView.findViewById(R.id.btnMyReview);
        btnMyFavorite = rootView.findViewById(R.id.btnMyFavorite);
        btnAddFace = rootView.findViewById(R.id.btnAddFace);
        progressBar = rootView.findViewById(R.id.progressBar);
        // 회원정보 세팅
        getUserInfo();

        // 주문내역 액티비티 전환
        btnMyOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyOrderListActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void getUserInfo() {

        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());
        UserApi api = retrofit.create(UserApi.class);

        // 헤더에 들어갈 액세스 토큰을 가져온다
        SharedPreferences sp = getActivity().getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
        String accessToken = "Bearer " + sp.getString(Config.ACCESS_TOKEN, "");

        Call<UserInfoRes> call = api.getUser(accessToken);

        call.enqueue(new Callback<UserInfoRes>() {
            @Override
            public void onResponse(Call<UserInfoRes> call, Response<UserInfoRes> response) {
                if (response.isSuccessful()) {

                    txtNickname.setText(response.body().getUser().getNickname());
                    txtEmail.setText(response.body().getUser().getEmail());

                    // 페이드 인 애니메이션 적용
                    txtNickname.setAlpha(0f);
                    txtEmail.setAlpha(0f);
                    txtNickname.animate().alpha(1f).setDuration(300);
                    txtEmail.animate().alpha(1f).setDuration(300);


                    String imgUrl = response.body().getImgUrl();
                    Glide.with(getActivity())
                            .load(imgUrl)
                            .placeholder(R.drawable.baseline_person_24)
                            .transition(DrawableTransitionOptions.withCrossFade(300)) // Glide에도 애니메이션 적용
                            .centerCrop()
                            .error(R.drawable.baseline_person_24)
                            .into(imgFace);


                    dismissProgress();

                } else {
                    dismissProgress();
                    Toast.makeText(getActivity(), "회원 정보를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show();

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
            public void onFailure(Call<UserInfoRes> call, Throwable t) {
                dismissProgress();
                Toast.makeText(getActivity(), "회원 정보를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                Log.e("LOGCAT", String.valueOf(t));


            }
        });
    }

    // 네트워크 로직 처리시에 화면에 보여주는 함수
    public void showProgress(String message){
        dialog = new ProgressDialog(getActivity());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(message);
        dialog.show();
    }


    // 로직처리가 끝나면 화면에서 사라지는 함수
    public void dismissProgress(){
        dialog.dismiss();
    }
}