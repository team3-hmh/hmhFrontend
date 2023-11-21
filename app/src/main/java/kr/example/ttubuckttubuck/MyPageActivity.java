package kr.example.ttubuckttubuck;

import static kr.example.ttubuckttubuck.MapActivity.PERMISSION;
import static kr.example.ttubuckttubuck.utils.MenuItemID.HOME;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

    // UI components ↓
    private BottomNavigationView navigationView;
    private Toolbar toolBar;
    private ActionBar actionBar;
    private ImageButton cameraBtn, goBackBtn;
    private ImageView eclipseProfile;
    private Button logoutBtn;
    private TextView userName, userEmail;
    private FrameLayout profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);
        setActionBar();

        Intent intent = getIntent();
        long member = intent.getLongExtra("member", -1);
        if (member == -1) {
            Log.d(TAG + "Intent", "Not valid User");
            Intent toMainActivity = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(toMainActivity);
        }

        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        Call<MemberDto> memberDtoCall = memberApi.memberInfo(member);
        memberDtoCall.enqueue(new Callback<MemberDto>() {
            @Override
            public void onResponse(Call<MemberDto> call, Response<MemberDto> response) {
                MemberDto memberDto = response.body();
                // TODO: 이미지 불러오고 적용시키기
                userName.setText(memberDto.getName());
                userEmail.setText(memberDto.getEmail());
            }

            @Override
            public void onFailure(Call<MemberDto> call, Throwable t) {
                Log.v("api fail", t.toString());
            }
        });


        eclipseProfile = findViewById(R.id.eclipse);
        profile = findViewById(R.id.profile);
        profile.setOnClickListener(view->{
            Log.d(TAG, "Select the profile picture.");
            selectPicture();
        });
        cameraBtn = findViewById(R.id.cameraBtn);
        cameraBtn.setOnClickListener(view->{
            Log.d(TAG, "Select the profile picture.");
            selectPicture();
        });
        logoutBtn = findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(view->{
            Intent toLoginActivity = new Intent(getApplicationContext(), LoginActivity.class);
            // toLoginActivity.putExtra("fromWhere", HOME);
            Log.d(TAG + "Intent", "Convert to Login Activity.");
            startActivity(toLoginActivity);
        });
    }

    private final int OPEN_GALLERY = 1;
    private void selectPicture(){
        if(PermissionChecker.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED){
            //권한이 확인된 경우 갤러리 오픈
            Intent openGallery = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(openGallery, OPEN_GALLERY);
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult() called.");
        if(requestCode == OPEN_GALLERY){
            if(data != null && resultCode == RESULT_OK){
                Uri selectedImg = data.getData();
                cameraBtn.setVisibility(View.INVISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    Log.d(TAG, "SDK version meet.");
                    Bitmap profileBmp = null;
                    try {
                        profileBmp = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), selectedImg), new ImageDecoder.OnHeaderDecodedListener() {
                            @Override
                            public void onHeaderDecoded(@NonNull ImageDecoder imageDecoder, @NonNull ImageDecoder.ImageInfo imageInfo, @NonNull ImageDecoder.Source source) {
                                imageDecoder.setMutableRequired(true);
                                imageDecoder.setAllocator(ImageDecoder.ALLOCATOR_SOFTWARE);
                            }
                        });
                        eclipseProfile.setImageBitmap(profileBmp);
                        Log.d(TAG, "profile has been set.");
                    } catch (IOException e) {
                        Log.e(TAG, "Failed to set Bitmap to profile view.");
                        e.printStackTrace();
                    }
                }else{
                    Log.d(TAG, "SDK version not meet.");
                }
            }
        }
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
        goBackBtn.setOnClickListener(view->{
            Intent toMainActivity = new Intent(getApplicationContext(), MainActivity.class);
            Log.d(TAG + "Intent", "Convert to Main Activity.");
            toMainActivity.putExtra("fromWhere", HOME);
            startActivity(toMainActivity);
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
            } else if (item.getTitle().equals("Home")) {
                Intent toMainActivity = new Intent(getApplicationContext(), MainActivity.class);
                Log.d(TAG + "Intent", "Convert to Main Activity.");
                toMainActivity.putExtra("fromWhere", HOME);
                startActivity(toMainActivity);
            } else { // Community
                Intent toCommunityActivity = new Intent(getApplicationContext(), CommunityActivity.class);
                toCommunityActivity.putExtra("fromWhere", HOME);
                Log.d(TAG + "Intent", "Convert to Community Activity.");
                startActivity(toCommunityActivity);
            }
            return false;
        });
    }
}
