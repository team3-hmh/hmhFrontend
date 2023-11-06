package kr.example.ttubuckttubuck;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class MyPageActivity extends AppCompatActivity {
    private ImageButton goBackBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        goBackBtn = findViewById(R.id.goBackBtn);
    }

}
