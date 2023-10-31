package kr.example.ttubuckttubuck;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class JoinActivity extends AppCompatActivity {
    private static final String TAG = "JoinActivity_Debug";
    private EditText idText, pwdText, nameText;
    private Button joinBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        idText = findViewById(R.id.idText);
        pwdText = findViewById(R.id.pwdText);
        nameText = findViewById(R.id.nameText);

        joinBtn = findViewById(R.id.joinBtn);
        joinBtn.setOnClickListener(view->{
            Intent toMainActivity = new Intent(getApplicationContext(), MainActivity.class);
            Log.d(TAG+"Intent", "Convert to Main Activity.");
            startActivity(toMainActivity);
        });
    }
}