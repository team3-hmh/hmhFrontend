package kr.example.ttubuckttubuck;

import static kr.example.ttubuckttubuck.utils.MenuItemID.COMMUNITY;
import static kr.example.ttubuckttubuck.utils.MenuItemID.POSTING;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class PostingActivity extends AppCompatActivity {
    private static final String TAG = "PostingActivity_Debug";

    // UI components ↓
    private BottomNavigationView navigationView;
    private Toolbar toolBar;
    private ActionBar actionBar;
    private ImageButton starBtn1, starBtn2, starBtn3, starBtn4, starBtn5;
    private ArrayList<ImageButton> starBtns = new ArrayList<>();
    private EditText postEditTxt;
    private Button submitBtn;
    private int fromWhere = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting);
        Log.d(TAG, "onCreate() called.");
        setActionBar();
        starBtnInit();

        postEditTxt = findViewById(R.id.postEditTxt);
        submitBtn = findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(view -> {
            Intent toCommunityActivity = new Intent(getApplicationContext(), CommunityActivity.class);
            toCommunityActivity.putExtra("fromWhere", POSTING);
            Log.d(TAG + "Intent", "Convert to Community Activity.");
            startActivity(toCommunityActivity);
        });
    }

    private void setActionBar() {
        toolBar = findViewById(R.id.toolBar);
        setSupportActionBar(toolBar);
        toolBar.setTitle("Posting");

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setTitle("Posting");

        navigationView = findViewById(R.id.navigationBtm);
        navigationView.getMenu().findItem(COMMUNITY).setChecked(true);
        navigationView.setOnItemSelectedListener(item -> {
            Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + ": " + item.getItemId());
            if (item.getTitle().equals("Map")) {
                Intent toMapActivity = new Intent(getApplicationContext(), MapActivity.class);
                toMapActivity.putExtra("fromWhere", COMMUNITY);
                Log.d(TAG + "Intent", "Convert to Map Activity.");
                startActivity(toMapActivity);
            } else if (item.getTitle().equals("Home")) {
                Intent toMainActivity = new Intent(getApplicationContext(), MainActivity.class);
                Log.d(TAG + "Intent", "Convert to Main Activity.");
                toMainActivity.putExtra("fromWhere", COMMUNITY);
                startActivity(toMainActivity);
            } else { // Community
                Intent toCommunityActivity = new Intent(getApplicationContext(), CommunityActivity.class);
                toCommunityActivity.putExtra("fromWhere", COMMUNITY);
                Log.d(TAG + "Intent", "Convert to Community Activity.");
                startActivity(toCommunityActivity);
            }
            return false;
        });
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
        for(int i = inx + 1; i < 5; i++)
            starBtns.get(i).setSelected(false);
        for(int i = 0; i <= inx; i++)
            starBtns.get(i).setSelected(true);
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
