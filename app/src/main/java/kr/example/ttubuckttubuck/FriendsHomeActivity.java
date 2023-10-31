package kr.example.ttubuckttubuck;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class FriendsHomeActivity extends AppCompatActivity {
    private static final String TAG = "FriendsHomeActivity_Debug";
    private ImageButton mainBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_home);

        mainBtn = findViewById(R.id.goBackBtn);
        mainBtn.setOnClickListener(view->{
            Intent toMainActivity = new Intent(getApplicationContext(), MainActivity.class);
            Log.d(TAG+"Intent", "Convert to Main Activity.");
            startActivity(toMainActivity);
        });
    }



}
