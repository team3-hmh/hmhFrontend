package kr.example.ttubuckttubuck;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
public class TmpActivity extends AppCompatActivity {
    private Button btn;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        btn = findViewById(R.id.button);
    }


}
