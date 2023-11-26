package kr.example.ttubuckttubuck;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import kr.example.ttubuckttubuck.api.MemberApi;
import kr.example.ttubuckttubuck.dto.SignUpDto;
import kr.example.ttubuckttubuck.utils.NetworkClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class JoinActivity extends AppCompatActivity {
    private static final String TAG = "JoinActivity_Debug";

    Retrofit retrofit = NetworkClient.getRetrofitClient(JoinActivity.this);

    MemberApi memberApi = retrofit.create(MemberApi.class);
    private EditText emailText, pwdText, nameText, pwdCheckText;
    private Button joinBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        nameText = findViewById(R.id.nameText);
        emailText = findViewById(R.id.emailText);
        pwdText = findViewById(R.id.pwdText);
        pwdCheckText = findViewById(R.id.pwdCheckText);

        joinBtn = findViewById(R.id.joinBtn);
        joinBtn.setOnClickListener(view->{
            SignUpDto signUpDto = new SignUpDto(
                    emailText.getText(),
                    pwdText.getText(),
                    pwdCheckText.getText(),
                    nameText.getText()
            );
            Call<Long> join = memberApi.join(signUpDto);
            join.enqueue(new Callback<Long>() {
                @Override
                public void onResponse(Call<Long> call, Response<Long> response) {
                    Intent toLoginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                    Log.d(TAG + "Intent", "Completed Join. Convert to Login Activity.");
                    startActivity(toLoginActivity);
                }

                @Override
                public void onFailure(Call<Long> call, Throwable t) {
                    // TODO: 회원가입 실패하면 어떻게 할지?
                }
            });
        });
    }
}