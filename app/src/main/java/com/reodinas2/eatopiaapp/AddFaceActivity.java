package com.reodinas2.eatopiaapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.reodinas2.eatopiaapp.api.FaceApi;
import com.reodinas2.eatopiaapp.api.NetworkClient;
import com.reodinas2.eatopiaapp.config.Config;
import com.reodinas2.eatopiaapp.model.Res;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddFaceActivity extends AppCompatActivity {

    ImageView imgFace;
    Button btnUpload;
    Button btnDelete;
    Button btnOk;
    ProgressDialog dialog;

    private static final int PERMISSION_CAMERA_REQUEST_CODE = 1000;
    private static final int PERMISSION_GALLERY_REQUEST_CODE = 1001;
    private static final int CAMERA_REQUEST_CODE = 1002;
    private static final int GALLERY_REQUEST_CODE = 1003;

    String accessToken;
    String currentPhotoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_face);

        imgFace = findViewById(R.id.imgFace);
        btnUpload = findViewById(R.id.btnUpload);
        btnDelete = findViewById(R.id.btnDelete);
        btnOk = findViewById(R.id.btnOk);

        // 억세스토큰
        SharedPreferences sp = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
        accessToken = "Bearer " + sp.getString(Config.ACCESS_TOKEN, "");

        // imgUrl이 존재하는 경우 화면에 세팅
        Intent intent = getIntent();
        if (intent.hasExtra("imgUrl")) {
            String imgUrl = intent.getStringExtra("imgUrl");

            Glide.with(AddFaceActivity.this)
                    .load(imgUrl)
                    .placeholder(R.drawable.baseline_person_24)
                    .transition(DrawableTransitionOptions.withCrossFade(300)) // Glide에도 애니메이션 적용
                    .centerCrop()
                    .skipMemoryCache(true) // 메모리 캐시 건너뛰기
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // 디스크 캐시 건너뛰기
                    .error(R.drawable.baseline_person_24)
                    .into(imgFace);
        }


        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUploadOptionsDialog();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getIntent().hasExtra("imgUrl")) {
                    deleteFace();
                } else {
                    Toast.makeText(AddFaceActivity.this, "등록된 얼굴 정보가 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerFace();
            }
        });

    }

    private void deleteFace() {
        showProgress("얼굴 정보 삭제 중...");

        Retrofit retrofit  = NetworkClient.getRetrofitClient(AddFaceActivity.this);
        FaceApi api = retrofit.create(FaceApi.class);

        Call<Res> call = api.deleteFace(accessToken);
        call.enqueue(new Callback<Res>() {
            @Override
            public void onResponse(Call<Res> call, Response<Res> response) {
                dismissProgress();

                if (response.isSuccessful()) {
                    // 요청이 성공한 경우 처리
                    Toast.makeText(AddFaceActivity.this, "정상적으로 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    imgFace.setImageResource(R.drawable.baseline_person_24);

                } else {
                    // 요청이 실패한 경우 처리
                    Toast.makeText(AddFaceActivity.this, "정상적으로 처리되지 않았습니다.", Toast.LENGTH_SHORT).show();

                    try {
                        JSONObject errorJson = new JSONObject(response.errorBody().string());
                        String errorMessage = errorJson.getString("error");
                        Toast.makeText(AddFaceActivity.this, "" + errorMessage, Toast.LENGTH_SHORT).show();
                        Log.i("LOGCAT", "에러 상태코드: " + response.code() + ", 메시지: " + errorMessage);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Res> call, Throwable t) {

                // 통신 실패 시 처리
                dismissProgress();
                Toast.makeText(AddFaceActivity.this, "정상적으로 처리되지 않았습니다.", Toast.LENGTH_SHORT).show();
                Log.i("LOGCAT", String.valueOf(t));
            }
        });
    }

    private void registerFace() {
        showProgress("얼굴 정보 등록 중...");

        Retrofit retrofit  = NetworkClient.getRetrofitClient(AddFaceActivity.this);
        FaceApi api = retrofit.create(FaceApi.class);

        // imgFace에서 Bitmap 가져오기
        Bitmap bitmap = ((BitmapDrawable) imgFace.getDrawable()).getBitmap();

        // Bitmap을 ByteArrayOutputStream으로 변환
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        // byteArrayOutputStream을 RequestBody로 변환
        RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpg"), byteArrayOutputStream.toByteArray());

        // MultipartBody.Part 생성
        MultipartBody.Part photo = MultipartBody.Part.createFormData("photo", "face_image.jpg", fileBody);

        // API 호출
        Call<Res> call = api.addFace(accessToken, photo);
        call.enqueue(new Callback<Res>() {
            @Override
            public void onResponse(Call<Res> call, Response<Res> response) {
                dismissProgress();

                if (response.isSuccessful()) {
                    // 요청이 성공한 경우 처리
                    Toast.makeText(AddFaceActivity.this, "사진이 등록되었습니다.", Toast.LENGTH_SHORT).show();

                    setResult(RESULT_OK);
                    finish();

                } else {
                    // 요청이 실패한 경우 처리
                    try {
                        JSONObject errorJson = new JSONObject(response.errorBody().string());
                        String errorMessage = errorJson.getString("error");
                        Toast.makeText(AddFaceActivity.this, "" + errorMessage, Toast.LENGTH_SHORT).show();
                        Log.i("LOGCAT", "에러 상태코드: " + response.code() + ", 메시지: " + errorMessage);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Res> call, Throwable t) {

                // 통신 실패 시 처리
                dismissProgress();
                Toast.makeText(AddFaceActivity.this, "정상적으로 처리되지 않았습니다.", Toast.LENGTH_SHORT).show();
                Log.i("LOGCAT", String.valueOf(t));
            }
        });

    }

    private boolean checkCameraPermission() {
        int cameraPermission = ContextCompat.checkSelfPermission(AddFaceActivity.this, Manifest.permission.CAMERA);
        return cameraPermission == PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private boolean checkGalleryPermission() {
        int storagePermission = ContextCompat.checkSelfPermission(AddFaceActivity.this, Manifest.permission.READ_MEDIA_IMAGES);
        return storagePermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.CAMERA
        }, PERMISSION_CAMERA_REQUEST_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void requestGalleryPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_MEDIA_IMAGES
        }, PERMISSION_GALLERY_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                // 카메라 권한 거절 시 처리
                Toast.makeText(this, "권한을 거절하여 업로드가 취소되었습니다.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PERMISSION_GALLERY_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                // 앨범 권한 거절 시 처리
                Toast.makeText(this, "권한을 거절하여 업로드가 취소되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showUploadOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("사진을 업로드 하세요.")
                .setItems(new String[]{"카메라로 찍기", "앨범에서 선택"}, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            if (checkCameraPermission()) {
                                openCamera();
                            } else {
                                requestCameraPermission();
                            }
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                if (checkGalleryPermission()) {
                                    openGallery();
                                } else {
                                    requestGalleryPermission();
                                }
                            }
                        }
                    }
                });
        builder.create().show();
    }

    private void openCamera() {
//        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                File file = new File(currentPhotoPath);
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                Uri photoUri = Uri.fromFile(file);
                Bitmap rotatedBitmap = getRotatedBitmap(bitmap, photoUri);
                imgFace.setImageBitmap(rotatedBitmap);

            } else if (requestCode == GALLERY_REQUEST_CODE) {
                Uri selectedImageUri = data.getData();
                String realPath = getRealPathFromURI(selectedImageUri);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    Bitmap rotatedBitmap = getRotatedBitmap(bitmap, Uri.fromFile(new File(realPath)));
                    imgFace.setImageBitmap(rotatedBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 사진이 정상적으로 나오게 회전
    private Bitmap getRotatedBitmap(Bitmap bitmap, Uri photoUri) {
        int orientation = 0;

        try {
            ExifInterface exifInterface = new ExifInterface(photoUri.getPath());
            orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;
        }

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getApplicationContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(columnIndex);
        cursor.close();
        return path;
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