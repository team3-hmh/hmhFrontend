package kr.example.ttubuckttubuck;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import kr.example.ttubuckttubuck.api.MemberApi;
import kr.example.ttubuckttubuck.dto.SignInDto;
import kr.example.ttubuckttubuck.utils.NetworkClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity_Debug";

    Retrofit retrofit = NetworkClient.getRetrofitClient(LoginActivity.this);

    MemberApi memberApi = retrofit.create(MemberApi.class);

    private EditText idText, pwdText;
    private Button loginBtn, joinBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        idText = findViewById(R.id.idText);
        pwdText = findViewById(R.id.pwdText);

        joinBtn = findViewById(R.id.joinBtn);
        joinBtn.setOnClickListener(view -> {
            SignInDto signInDto = new SignInDto(idText.getText(), pwdText.getText());
            Call<String> loginToken = memberApi.login(signInDto); //로그인 요청
            loginToken.enqueue(new Callback<String>() {
                //로그인 성공
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Intent toJoinActivity = new Intent(getApplicationContext(), JoinActivity.class);
                    Log.d(TAG + "Intent", "Convert to Join Activity.");
                    startActivity(toJoinActivity);
                }

                //로그인 실패
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    // TODO: 로그인 실패 알람 띄우기
                }
            });
        });

        loginBtn = findViewById(R.id.loginBtn);
    }
}
