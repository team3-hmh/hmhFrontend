package kr.example.ttubuckttubuck;

import static kr.example.ttubuckttubuck.utils.MenuItemID.COMMUNITY;
import static kr.example.ttubuckttubuck.utils.MenuItemID.HOME;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import kr.example.ttubuckttubuck.CustomView.PostItem;

public class CommunityActivity extends AppCompatActivity {
    private static final String TAG = "CommunityActivity_Debug";
    private static int postitemCnt = 0;
    private static String content = "삶과윤리 수업이 아주 좋습니다. 수신지가 치국평천하 나무아미타불 색즉시공 공즉시색 도덕경 노자 공자 맹자 고자";

    // UI components ↓
    private BottomNavigationView navigationView;
    private LinearLayout postList;
    private Toolbar toolBar;
    private ActionBar actionBar;
    private ImageButton addPostBtn;
    private int fromWhere;

    private PostItem addPostItem(int postImg, String postTitle, String postContent, String date){
        PostItem tmp = new PostItem(getApplicationContext());
        tmp.setTag("todoItem"+ (++postitemCnt));
        tmp.setPostImg(R.drawable.post_img);
        tmp.setPostTitle(postTitle);
        tmp.setPostContent(postContent);
        tmp.setDate(date);

        return tmp;
    }

    private void setActionBar() {
        toolBar = findViewById(R.id.toolBar);
        setSupportActionBar(toolBar);
        toolBar.setTitle("Community");

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setTitle("Community");

        navigationView = findViewById(R.id.navigationBtm);
        navigationView.getMenu().findItem(fromWhere).setChecked(false);
        navigationView.getMenu().findItem(COMMUNITY).setChecked(true);
        navigationView.setOnItemSelectedListener(item -> {
            Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + ": " + item.getItemId() + " : " + R.id.map);
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
                Log.d(TAG + "Intent", "Already in Community Activity.");
            }
            return false;
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        fromWhere = getIntent().getIntExtra("fromWhere", HOME);
        setActionBar();

        postList = findViewById(R.id.postList);

        addPostBtn = findViewById(R.id.addPostBtn);
        addPostBtn.setOnClickListener(view-> postList.addView(addPostItem(-1,"성북구 국민대학교 북악관 207호", content,"2023-11-11")));
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
}