package kr.example.ttubuckttubuck;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import kr.example.ttubuckttubuck.api.MemberApi;
import kr.example.ttubuckttubuck.dto.MemberDto;
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
            Intent toJoinActivity = new Intent(getApplicationContext(), JoinActivity.class);
            Log.d(TAG + "Intent", "Convert to Join Activity.");
            startActivity(toJoinActivity);
        });

        loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(view -> {
            SignInDto signInDto = new SignInDto(idText.getText(), pwdText.getText());
            Call<String> loginToken = memberApi.login(signInDto); //로그인 요청
            Call<MemberDto> memberDtoCall = memberApi.findByEmail(String.valueOf(idText.getText()));
            loginToken.enqueue(new Callback<String>() {
                //로그인 성공
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    memberDtoCall.enqueue(new Callback<MemberDto>() {
                        @Override
                        public void onResponse(Call<MemberDto> call, Response<MemberDto> response) {
                            MemberDto memberDto = response.body();
                            Intent toMainActivity = new Intent(getApplicationContext(), MainActivity.class);
                            // TODO: findByEmail() 추가 하고 호출 해서 MemberDto 에서 id 추출 해서 Intent에 담아서 전송
                            assert memberDto != null;
                            toMainActivity.putExtra("id", memberDto.getId());
                            Log.d(TAG + "Intent", "Convert to Main Activity.");
                            startActivity(toMainActivity);
                        }

                        @Override
                        public void onFailure(Call<MemberDto> call, Throwable t) {
                            // TODO: 로그인 실패
                        }
                    });

                }

                //로그인 실패
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    // TODO: 로그인 실패 알람 띄우기
                }
            });
        });
    }
}
