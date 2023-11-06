package kr.example.ttubuckttubuck;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class JoinActivity extends AppCompatActivity {
    private EditText idText, pwdText, nameText;
    private Button loginBtn, joinBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        idText = findViewById(R.id.idText);
        pwdText = findViewById(R.id.pwdText);
        nameText = findViewById(R.id.nameText);

        joinBtn = findViewById(R.id.joinBtn);
    }
}