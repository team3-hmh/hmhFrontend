package kr.example.ttubuckttubuck;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText idText, pwdText;
    private Button loginBtn, joinBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        idText = findViewById(R.id.idText);
        pwdText = findViewById(R.id.pwdText);

        joinBtn = findViewById(R.id.joinBtn);
        loginBtn = findViewById(R.id.loginBtn);
    }
}
