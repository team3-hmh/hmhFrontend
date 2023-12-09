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
import android.widget.Toast;

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
    private Call<MemberDto> memberDtoCall;
    private String tmp = "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAAXNSR0IArs4c6QAAAARzQklUCAgICHwIZIgAAAJVSURBVDiNrZFLSFRxGMV//ztzR8d5XW18pIUuHMHFmKjgVIsQ0YIYWrWwTWUUiOs2hcsgiJbRogiiTRiEUEg+CHEnNMhoRjm28IWjZNPceevc+28xzmBStKizPHDOd8754B8hALaf0Ifk" +
            "MpIviuBN9S2W5XRHGwZ+ABSxIPpCiztPaTYNgkLQguB17U2mrADSZEAIriPAWuF+kB9vjGMKrWAP" +
            "SMiP+2OJ3VVtL6OLA64CmFIOckQBKty1aDU+YbHatKNRLVZbpVbrE3Z3TZHaAlAOemzYyl04q078" +
            "tbOr6iRquQsh2SgZKDDt0OoByO1L3s7FMUxZEhlmgdvLFzinVo+wMlUy8Da2baplTgBCkRTBkRUi" +
            "mzkyW4LMlmB5I0dwZIXQchoAtcyJt6ZzvfSFgR7tXKDVNTN43ovTrhD9vo/bYWFhTIKAtksCPWVQ" +
            "V6WSzpk8e/eN2UW999Vs/L0AkB861a9LuaTXbbV5HBYAbjxcpdxWGDyZNXl+uwkAPW2wE9vPNTdV" +
            "O0XPTL74KO5eOT7vayhrv9p3DIBExkBPmQC4HQoue8H4xfQun9ay8/dfRjtKGwCc9TsGL3R59Jlw" +
            "orC23UKDV6XBq5bEs4tJettd+mm/51pRV0oAcKbF+Wjsjm+oul75hS9id9OUF+9FHs+tJId/axDu" +
            "73fIbDLqaZVOd7OBy1wt1FEa0SMKPz5bEkYqW9cVCqWLGuWwwanJyZQ0lWDso7IencijxrdR49tE" +
            "J/LElixrUirBw+I/QoISDgS6E8OVo/pQ5Wg4EOiWR479N/wE4aXcriAWd8UAAAAASUVORK5CYII=";

    private String tmp2 = "iVBORw0KGgoAAAANSUhEUgAAAAIAAAACCAYAAABytg0kAAAAAXNSR0IArs4c6QAAAARzQklUCAgI\n" +
            "CHwIZIgAAAAaSURBVAiZY/y8Qvw/j5AMA/PBS4xiAfpCJgA7NAVfdDo6XgAAAABJRU5ErkJggg==";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);
        setActionBar();

        Intent intent = getIntent();
        member = intent.getLongExtra("member", -1);
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
                Log.d(TAG, "Is userImg null?: " + (userImg == null) + ", value: " + userImg);
                if (userImg != null) {
                    //byte[] buffer = Base64.decode(userImg, 1);
                    //Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
                    //Bitmap bitmap = stringToBitmap(tmp);
                    runOnUiThread(() -> {
                        eclipseProfile.setImageBitmap(stringToBitmap(userImg));
                        cameraBtn.setVisibility(View.INVISIBLE);
                    });
                } else {
                    runOnUiThread(() -> cameraBtn.setVisibility(View.VISIBLE));
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

    public static Bitmap stringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public static String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        String temp = Base64.encodeToString(bytes, Base64.DEFAULT);
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
                        // Bitmap.createScaledBitmap(profileBmp, 250, 250, false);
                        final int basicWidth = profileBmp.getWidth();
                        final int basicHeight = profileBmp.getHeight();
                        Toast.makeText(getApplicationContext(), basicWidth + ", " + basicHeight, Toast.LENGTH_SHORT).show();
                        if(basicWidth > circleSize || basicHeight > circleSize) {
                            resizedBmp = getDownScaledBitmap(profileBmp, 512, 512);
                            Toast.makeText(getApplicationContext(), resizedBmp.getWidth() + ", " + resizedBmp.getHeight(), Toast.LENGTH_SHORT).show();
                            circleCroppedBmp = getCroppedBitmap(resizedBmp);
                        }else {
                            resizedBmp = getUpScaledBitmap(profileBmp, 512, 512);
                            Toast.makeText(getApplicationContext(), resizedBmp.getWidth() + ", " + resizedBmp.getHeight(), Toast.LENGTH_SHORT).show();
                            circleCroppedBmp = getCroppedBitmap(profileBmp);
                        }
                        eclipseProfile.setImageBitmap(circleCroppedBmp);
                        Log.d(TAG, "profile has been set.");

                        String encodedmap = bitmapToString(profileBmp);
                        Log.d(TAG, "Encoded Bitmap: " + encodedmap);

                        /*// TODO: 아래 주석 처리한 Call 호출하면 onResponse는 되는데 response.body()가 null임.
                        Call<MemberDto> insertImageCall = memberApi.insertImage(member, encodedmap);
                        insertImageCall.enqueue(new Callback<MemberDto>() {
                            @Override
                            public void onResponse(Call<MemberDto> call, Response<MemberDto> response) {
                                try {
                                    Log.d(TAG, "onResponse()'s response info: " + response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                MemberDto memberDto = response.body();
                                Log.d(TAG, "onResponse() called.");
                                // Log.d(TAG, "memberDto info called in insertImage: " + memberDto.toString());
                                // 3. String으로 cast된 Bitmap을 memberDto에 set.
                                memberDto.setImage(encodedmap);
                            }

                            @Override
                            public void onFailure(Call<MemberDto> call, Throwable t) {
                                Log.v(TAG + "api fail", t.toString());
                            }
                        });*/

                        // TODO: 하지만 이렇게 memberDtoCall을 한 번 더 호출하고 그 안에 insertImage를 호출해서 바깥 response.body()를 가져오면 null이 아니긴 함.
                        memberDtoCall.clone().enqueue(new Callback<MemberDto>() {
                            @Override
                            public void onResponse(Call<MemberDto> call, Response<MemberDto> response) {
                                MemberDto memberDto = response.body();
                                Log.d(TAG, "memberDto info: " + memberDto.getId() + ", " + memberDto.getName());
                                // TODO: 이미지 불러오고 적용시키기

                                Call<MemberDto> insertImage = memberApi.insertImage(member, encodedmap);
                                insertImage.enqueue(new Callback<MemberDto>() {
                                    @Override
                                    public void onResponse(Call<MemberDto> call, Response<MemberDto> nestedResponse) {
                                        MemberDto memberDto = response.body();
                                        Log.d(TAG, "Is nestedResponse null?: " + (nestedResponse.body() == null)); // true.
                                        Log.d(TAG, "memberDto info called in insertImage: " + memberDto.getId() + ", " + memberDto.getName());
                                        // 3. String으로 cast된 Bitmap을 memberDto에 set.
                                        memberDto.setImage(encodedmap);
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

    final int circleSize = 512;
    // https://stackoverflow.com/questions/11932805/how-to-crop-circular-area-from-bitmap-in-android
    private Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(circleSize, circleSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        canvas.drawCircle(circleSize / 2, circleSize / 2,
                circleSize / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    // https://stackoverflow.com/questions/4837715/how-to-resize-a-bitmap-in-android
    private Bitmap getUpScaledBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = (((float) newWidth) / width);
        float scaleHeight = (((float) newHeight) / height);

        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(2f, 2f);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, true);

        return resizedBitmap;
    }

    private Bitmap getDownScaledBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();

        float scaleWidth;
        float scaleHeight;

        if(width > height) {
            scaleWidth = (float) (((float) newWidth) / width);
            scaleHeight = (float) (((float) newHeight ) / width);
        }
        else {
            scaleWidth = (float) (((float) newWidth ) / height);
            scaleHeight = (float) (((float) newHeight) / height);
        }

        Log.d("downscaletest", scaleWidth + ", " + scaleHeight);

        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale((float) (scaleWidth * 1.5), (float) (scaleHeight * 1.5));

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, true);

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
            //recycleBmp();
        });

        navigationView = findViewById(R.id.navigationBtm);
        navigationView.getMenu().findItem(HOME).setChecked(true);
        navigationView.setOnItemSelectedListener(item -> {
            Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + ": " + item.getItemId());
            if (item.getTitle().equals("Map")) {
                Intent toMapActivity = new Intent(getApplicationContext(), MapActivity.class);
                toMapActivity.putExtra("fromWhere", HOME);
                toMapActivity.putExtra("member", member);
                Log.d(TAG + "Intent", "Convert to Map Activity.");
                startActivity(toMapActivity);
                recycleBmp();
            } else if (item.getTitle().equals("Home")) {
                Intent toMainActivity = new Intent(getApplicationContext(), MainActivity.class);
                Log.d(TAG + "Intent", "Convert to Main Activity.");
                toMainActivity.putExtra("fromWhere", HOME);
                toMainActivity.putExtra("member", member);
                startActivity(toMainActivity);
                recycleBmp();
            } else { // Community
                Intent toCommunityActivity = new Intent(getApplicationContext(), CommunityActivity.class);
                toCommunityActivity.putExtra("fromWhere", HOME);
                toCommunityActivity.putExtra("member", member);
                Log.d(TAG + "Intent", "Convert to Community Activity.");
                startActivity(toCommunityActivity);
                recycleBmp();
            }
            return false;
        });
    }
}
