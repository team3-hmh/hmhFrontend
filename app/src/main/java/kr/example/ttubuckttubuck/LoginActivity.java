package kr.example.ttubuckttubuck;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity_Debug";
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
    }
}
