package kr.example.ttubuckttubuck;

import static kr.example.ttubuckttubuck.utils.MenuItemID.COMMUNITY;
import static kr.example.ttubuckttubuck.utils.MenuItemID.HOME;
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

import kr.example.ttubuckttubuck.api.PostingApi;
import kr.example.ttubuckttubuck.dto.PostingDto;
import kr.example.ttubuckttubuck.utils.NetworkClient;
import retrofit2.Retrofit;

public class PostingActivity extends AppCompatActivity {
    private static final String TAG = "PostingActivity_Debug";

    Retrofit retrofit = NetworkClient.getRetrofitClient(PostingActivity.this);

    PostingApi postingApi = retrofit.create(PostingApi.class);

    // UI components ↓
    private BottomNavigationView navigationView;
    private Toolbar toolBar;
    private ActionBar actionBar;
    private ImageButton starBtn1, starBtn2, starBtn3, starBtn4, starBtn5, goBackBtn;
    private ArrayList<ImageButton> starBtns = new ArrayList<>();
    private EditText postEditTxt;
    private Button submitBtn;
    private int fromWhere = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting);

        Intent intent = getIntent();
        long member = intent.getLongExtra("member", -1);
        if (member == -1) {
            Log.d(TAG + "Intent", "Not valid User");
            Intent toLoginActivity = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(toLoginActivity);
        }

        Log.d(TAG, "onCreate() called.");
        setActionBar(member);
        starBtnInit();

        postEditTxt = findViewById(R.id.postEditTxt);
        submitBtn = findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(view -> {
            long rate = 0L;
            for (ImageButton b : starBtns) {
                if (b.isSelected()) {
                    rate++;
                }
            }
            // TODO: placeId는 어떻게 할지
            // TODO: POST posting 해결해야함
            PostingDto postingDto = new PostingDto(member, 1L, String.valueOf(postEditTxt.getText()), rate);
            postingApi.savePosting(postingDto);

            Intent toCommunityActivity = new Intent(getApplicationContext(), CommunityActivity.class);
            toCommunityActivity.putExtra("fromWhere", POSTING);
            toCommunityActivity.putExtra("member", member);
            Log.d(TAG + "Intent", "Convert to Community Activity.");
            startActivity(toCommunityActivity);
        });
    }

    private void setActionBar(Long member) {
        goBackBtn = findViewById(R.id.goBackBtn);
        goBackBtn.setOnClickListener(view -> {
            Intent toCommunityActivity = new Intent(getApplicationContext(), CommunityActivity.class);
            Log.d(TAG + "Intent", "Convert to Community Activity.");
            toCommunityActivity.putExtra("fromWhere", COMMUNITY);
            startActivity(toCommunityActivity);
        });

        navigationView = findViewById(R.id.navigationBtm);
        navigationView.getMenu().findItem(COMMUNITY).setChecked(true);
        navigationView.setOnItemSelectedListener(item -> {
            Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + ": " + item.getItemId());
            if (item.getTitle().equals("Map")) {
                Intent toMapActivity = new Intent(getApplicationContext(), MapActivity.class);
                toMapActivity.putExtra("fromWhere", COMMUNITY);
                toMapActivity.putExtra("member", member);
                Log.d(TAG + "Intent", "Convert to Map Activity.");
                startActivity(toMapActivity);
            } else if (item.getTitle().equals("Home")) {
                Intent toMainActivity = new Intent(getApplicationContext(), MainActivity.class);
                Log.d(TAG + "Intent", "Convert to Main Activity.");
                toMainActivity.putExtra("fromWhere", COMMUNITY);
                toMainActivity.putExtra("member", member);
                startActivity(toMainActivity);
            } else { // Community
                Intent toCommunityActivity = new Intent(getApplicationContext(), CommunityActivity.class);
                toCommunityActivity.putExtra("fromWhere", COMMUNITY);
                toCommunityActivity.putExtra("member", member);
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
