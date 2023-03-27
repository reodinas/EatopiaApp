package com.reodinas2.eatopiaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.reodinas2.eatopiaapp.api.NetworkClient;
import com.reodinas2.eatopiaapp.api.UserApi;
import com.reodinas2.eatopiaapp.config.Config;
import com.reodinas2.eatopiaapp.model.User;
import com.reodinas2.eatopiaapp.model.UserRes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity {

    EditText editEmail;
    EditText editPassword;
    EditText editPassword2;
    EditText editNickname;
    EditText editPhone;
    Button btnRegister;
    TextView txtLogin;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        editPassword2 = findViewById(R.id.editPassword2);
        editNickname = findViewById(R.id.editNickname);
        editPhone = findViewById(R.id.editPhone);
        btnRegister = findViewById(R.id.btnRegister);
        txtLogin = findViewById(R.id.txtLogin);

        // 회원가입
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 이메일 가져와서 형식 체크
                String email = editEmail.getText().toString().trim();

                Pattern emailPattern = Patterns.EMAIL_ADDRESS;

                if(!emailPattern.matcher(email).matches()){
                    Toast.makeText(RegisterActivity.this, "이메일 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                    editEmail.requestFocus();
                    return;
                }

                // 비밀번호 체크
                String password = editPassword.getText().toString().trim();
                String password2 = editPassword2.getText().toString().trim();
                // 비밀번호 길이 4~12만 허용
                if(password.length() < 4 || password.length() > 12){
                    Toast.makeText(RegisterActivity.this, "비밀번호는 4자이상, 12자 이하여야 합니다.", Toast.LENGTH_SHORT).show();
                    editPassword.requestFocus();
                    return;
                }
                // 비밀번호 1,2가 같은지 확인
                if(!password.equals(password2)){
                    Toast.makeText(RegisterActivity.this, "비밀번호가 동일하지 않습니다.", Toast.LENGTH_SHORT).show();
                    editPassword.requestFocus();
                    return;
                }

                // 닉네임
                String nickname = editNickname.getText().toString().trim();
                // 전화번호 체크
                String phone = editPhone.getText().toString().trim();
                Pattern phonePattern = Pattern.compile("^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$");
                if(!phonePattern.matcher(phone).matches()){
                    Toast.makeText(RegisterActivity.this, "전화번호 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                    editPhone.requestFocus();
                    return;
                }

                // 회원가입 API를 호출!
                // 다이얼로그를 화면에 보여준다.
                showProgress("회원가입 중입니다");

                Retrofit retrofit = NetworkClient.getRetrofitClient(RegisterActivity.this);
                UserApi api = retrofit.create(UserApi.class);

                User user = new User(email, password, nickname, phone);

                Call<UserRes> call = api.register(user);

                call.enqueue(new Callback<UserRes>() {
                    @Override
                    public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                        // 프로그레스 다이얼로그가 있으면, 나타나지 않게 해준다.
                        dismissProgress();

                        if (response.isSuccessful()) {

                            UserRes res = response.body();

                            SharedPreferences sp =
                                    getApplication().getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString(Config.ACCESS_TOKEN, res.getAccess_token());
                            editor.apply();

                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        }else{
                            Toast.makeText(RegisterActivity.this, "정상적으로 처리되지 않았습니다.", Toast.LENGTH_SHORT).show();

                            try {
                                JSONObject errorJson = new JSONObject(response.errorBody().string());
                                String errorMessage = errorJson.getString("error");
                                Toast.makeText(RegisterActivity.this, "" + errorMessage, Toast.LENGTH_SHORT).show();
                                Log.i("LOGCAT", "에러 상태코드: " + response.code() + ", 메시지: " + errorMessage);
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<UserRes> call, Throwable t) {
                        // 프로그레스 다이얼로그가 있으면, 나타나지 않게 해준다.
                        dismissProgress();
                        Log.i("LOGCAT", ""+t);
                        Toast.makeText(RegisterActivity.this, "정상적으로 처리되지 않았습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
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