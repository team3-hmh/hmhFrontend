package kr.example.ttubuckttubuck;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class PostingActivity extends AppCompatActivity {
    private static final String TAG = "PostingActivity";

    private ImageButton starBtn1, starBtn2, starBtn3, starBtn4, starBtn5;
    private ArrayList<ImageButton> starBtns = new ArrayList<>();
    private EditText postEditTxt;
    private Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting);
        Log.d(TAG, "onCreate() called.");

        postEditTxt = findViewById(R.id.postEditTxt);
        submitBtn = findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(view -> {

        });

        starBtnInit();
    }

    private void starBtnInit(){
        starBtn1 = findViewById(R.id.star1);
        starBtns.add(starBtn1);
        starBtn2 = findViewById(R.id.star2);
        starBtns.add(starBtn2);
        starBtn3 = findViewById(R.id.star3);
        starBtns.add(starBtn3);
        starBtn4 = findViewById(R.id.star4);
        starBtns.add(starBtn4);
        starBtn5 = findViewById(R.id.star5);
        starBtns.add(starBtn5);

        starBtn1.setOnClickListener(view -> {
            Log.d(TAG, "star button 0 selected.");
            setBtnSelected(0);
        });

        starBtn2.setOnClickListener(view -> {
            Log.d(TAG, "star button 1 selected.");
            setBtnSelected(1);
        });

        starBtn3.setOnClickListener(view -> {
            Log.d(TAG, "star button 2 selected.");
            setBtnSelected(2);
        });

        starBtn4.setOnClickListener(view -> {
            Log.d(TAG, "star button 3 selected.");
            setBtnSelected(3);
        });

        starBtn5.setOnClickListener(view -> {
            Log.d(TAG, "star button 4 selected.");
            setBtnSelected(4);
        });
    }

    private void setBtnSelected(int inx){
        for(int i = 0; i < 5; i++)
            starBtns.get(i).setPressed(false);

        for(int i = 0; i <= inx; i++)
            starBtns.get(i).setPressed(true);
    }
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called.");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called.");
    }
}
