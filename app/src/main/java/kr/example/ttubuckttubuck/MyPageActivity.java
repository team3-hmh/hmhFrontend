package kr.example.ttubuckttubuck;

import static kr.example.ttubuckttubuck.MapActivity.PERMISSION;
import static kr.example.ttubuckttubuck.utils.MenuItemID.HOME;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import kr.example.ttubuckttubuck.api.MemberApi;
import kr.example.ttubuckttubuck.dto.MemberDto;
import kr.example.ttubuckttubuck.utils.NetworkClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MyPageActivity extends AppCompatActivity {
    private static final String TAG = "MyPageActivity_Debug";

    Retrofit retrofit = NetworkClient.getRetrofitClient(MyPageActivity.this);
    MemberApi memberApi = retrofit.create(MemberApi.class);

    // API components ↓
    private long member;
    private Call<MemberDto> memberDtoCall;

    // UI components ↓
    private BottomNavigationView navigationView;
    private Toolbar toolBar;
    private ActionBar actionBar;
    private ImageButton cameraBtn, goBackBtn;
    private ImageView eclipseProfile;
    private Button logoutBtn;
    private TextView userName, userEmail;
    private FrameLayout profile;
    private Bitmap profileBmp = null;
    private Bitmap resizedBmp = null;
    private Bitmap circleCroppedBmp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);
        setActionBar();

        Intent intent = getIntent();
        long member = intent.getLongExtra("member", -1);
        Log.d(TAG, "member Id: " + member);
        if (member == -1) {
            Log.d(TAG + "Intent", "Not valid User");
            Intent toMainActivity = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(toMainActivity);
        }

        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        memberDtoCall = memberApi.memberInfo(member);
        memberDtoCall.enqueue(new Callback<MemberDto>() {
            @Override
            public void onResponse(Call<MemberDto> call, Response<MemberDto> response) {
                MemberDto memberDto = response.body();
                userName.setText(memberDto.getName());
                userEmail.setText(memberDto.getEmail());
                String userImg = memberDto.getImage();
                Log.d(TAG, "Is userImag null?: " + (userImg == null) + ", value: " + userImg);
                if (userImg != null) {
                    byte[] buffer = userImg.getBytes();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
                    runOnUiThread(() -> eclipseProfile.setImageBitmap(bitmap));
                }
            }

            @Override
            public void onFailure(Call<MemberDto> call, Throwable t) {
                Log.v("api fail", t.toString());
            }
        });

        eclipseProfile = findViewById(R.id.eclipse);
        profile = findViewById(R.id.profile);
        profile.setOnClickListener(view -> {
            Log.d(TAG, "Select the profile picture.");
            selectPicture();
        });
        cameraBtn = findViewById(R.id.cameraBtn);
        cameraBtn.setOnClickListener(view -> {
            Log.d(TAG, "Select the profile picture.");
            selectPicture();
        });
        logoutBtn = findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(view -> {
            Intent toLoginActivity = new Intent(getApplicationContext(), LoginActivity.class);
            // toLoginActivity.putExtra("fromWhere", HOME);
            Log.d(TAG + "Intent", "Convert to Login Activity.");
            startActivity(toLoginActivity);
        });
    }

    private final int OPEN_GALLERY = 1;

    private void selectPicture() {
        if (PermissionChecker.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
            //권한이 확인된 경우 갤러리 오픈
            Intent openGallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(openGallery, OPEN_GALLERY);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION);
        }
    }

    private String BitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, baos);
        byte[] bytes = baos.toByteArray();
        String temp = Base64.encodeToString(bytes, Base64.DEFAULT);
        Log.d(TAG, "string info: " + temp);
        return temp;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult() called.");
        if (requestCode == OPEN_GALLERY) {
            if (data != null && resultCode == RESULT_OK) {
                Uri selectedImg = data.getData();
                cameraBtn.setVisibility(View.INVISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    Log.d(TAG, "SDK version meet.");
                    try {
                        profileBmp = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), selectedImg), new ImageDecoder.OnHeaderDecodedListener() {
                            @Override
                            public void onHeaderDecoded(@NonNull ImageDecoder imageDecoder, @NonNull ImageDecoder.ImageInfo imageInfo, @NonNull ImageDecoder.Source source) {
                                imageDecoder.setMutableRequired(true);
                                imageDecoder.setAllocator(ImageDecoder.ALLOCATOR_SOFTWARE);
                            }
                        });
                        //Bitmap.createScaledBitmap(profileBmp, 75, 75, true);
                        resizedBmp = getResizedBitmap(profileBmp, profileBmp.getWidth() / 6, profileBmp.getHeight() / 6);
                        circleCroppedBmp = getCroppedBitmap(resizedBmp);
                        eclipseProfile.setImageBitmap(circleCroppedBmp);
                        Log.d(TAG, "profile has been set.");

                        memberDtoCall.clone().enqueue(new Callback<MemberDto>() {
                            @Override
                            public void onResponse(Call<MemberDto> call, Response<MemberDto> response) {
                                MemberDto memberDto = response.body();
                                Log.d(TAG, "memberDto info: " + memberDto.getId() + ", " + memberDto.getName());
                                // TODO: 이미지 불러오고 적용시키기
                                String encodedBmp = BitmapToString(resizedBmp);
                                Log.d(TAG, "string info: " + encodedBmp);

                                Call<MemberDto> insertImage = memberApi.insertImage(member, encodedBmp);
                                insertImage.enqueue(new Callback<MemberDto>() {
                                    @Override
                                    public void onResponse(Call<MemberDto> call, Response<MemberDto> nestedResponse) {
                                        MemberDto memberDto = response.body();
                                        Log.d(TAG, "memberDto info called in insertImage: " + memberDto.getId() + ", " + memberDto.getName());
                                        // 3. String으로 cast된 Bitmap을 memberDto에 set.
                                        memberDto.setImage(encodedBmp);
                                    }
                                    @Override
                                    public void onFailure(Call<MemberDto> call, Throwable t) {
                                        Log.e(TAG, "Failed to call insertImage: " + t);
                                        Log.v(TAG + "api fail", t.toString());
                                    }
                                });
                                Log.d(TAG, "insertImage called.");
                            }
                            @Override
                            public void onFailure(Call<MemberDto> call, Throwable t) {
                                Log.v(TAG + "api fail", t.toString());
                            }
                        });

                    } catch (IOException e) {
                        Log.e(TAG, "Failed to set Bitmap to profile view.");
                        e.printStackTrace();
                    }
                } else {
                    Log.d(TAG, "SDK version not meet.");
                }
            }
        }
    }

    // https://stackoverflow.com/questions/11932805/how-to-crop-circular-area-from-bitmap-in-android
    private Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    // https://stackoverflow.com/questions/4837715/how-to-resize-a-bitmap-in-android
    private Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    private void recycleBmp() {
        if (profileBmp != null)
            profileBmp.recycle();
        if (resizedBmp != null)
            resizedBmp.recycle();
        if (circleCroppedBmp != null)
            circleCroppedBmp.recycle();
    }

    private void setActionBar() {
        toolBar = findViewById(R.id.toolBar);
        setSupportActionBar(toolBar);
        toolBar.setTitle("Mypage");

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setTitle("Mypage");

        goBackBtn = findViewById(R.id.goBackBtn);
        goBackBtn.setOnClickListener(view -> {
            Intent toMainActivity = new Intent(getApplicationContext(), MainActivity.class);
            Log.d(TAG + "Intent", "Convert to Main Activity.");
            toMainActivity.putExtra("fromWhere", HOME);
            startActivity(toMainActivity);
            recycleBmp();
        });

        navigationView = findViewById(R.id.navigationBtm);
        navigationView.getMenu().findItem(HOME).setChecked(true);
        navigationView.setOnItemSelectedListener(item -> {
            Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + ": " + item.getItemId());
            if (item.getTitle().equals("Map")) {
                Intent toMapActivity = new Intent(getApplicationContext(), MapActivity.class);
                toMapActivity.putExtra("fromWhere", HOME);
                Log.d(TAG + "Intent", "Convert to Map Activity.");
                startActivity(toMapActivity);
                recycleBmp();
            } else if (item.getTitle().equals("Home")) {
                Intent toMainActivity = new Intent(getApplicationContext(), MainActivity.class);
                Log.d(TAG + "Intent", "Convert to Main Activity.");
                toMainActivity.putExtra("fromWhere", HOME);
                startActivity(toMainActivity);
                recycleBmp();
            } else { // Community
                Intent toCommunityActivity = new Intent(getApplicationContext(), CommunityActivity.class);
                toCommunityActivity.putExtra("fromWhere", HOME);
                Log.d(TAG + "Intent", "Convert to Community Activity.");
                startActivity(toCommunityActivity);
                recycleBmp();
            }
            return false;
        });
    }
}
