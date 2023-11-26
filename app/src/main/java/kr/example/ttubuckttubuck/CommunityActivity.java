package kr.example.ttubuckttubuck;

import static kr.example.ttubuckttubuck.utils.MenuItemID.COMMUNITY;
import static kr.example.ttubuckttubuck.utils.MenuItemID.HOME;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import kr.example.ttubuckttubuck.CustomView.PostItem;
import kr.example.ttubuckttubuck.api.PostingApi;
import kr.example.ttubuckttubuck.dto.PostingDto;
import kr.example.ttubuckttubuck.utils.NetworkClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CommunityActivity extends AppCompatActivity {
    private static final String TAG = "CommunityActivity_Debug";

    Retrofit retrofit = NetworkClient.getRetrofitClient(CommunityActivity.this);
    PostingApi postingApi = retrofit.create(PostingApi.class);

    private static int postitemCnt = 0;
    private static String content = "삶과윤리 수업이 아주 좋습니다. 수신지가 치국평천하라는 내용이 기억에 남습니다.";

    // UI components ↓
    private BottomNavigationView navigationView;
    private LinearLayout postList;
    private ImageButton addPostBtn;
    private int fromWhere;
    private long member;

    private PostItem addPostItem(PostingDto postingDto) {
        PostItem tmp = new PostItem(getApplicationContext());
        tmp.setTag("todoItem" + (++postitemCnt));
        tmp.setPostContent(postingDto.getContent());
        // TODO: date 대신 rating 들어가게 수정
        tmp.setRate(postingDto.getRating());

        return tmp;
    }

    private void setActionBar(Long member) {
        navigationView = findViewById(R.id.navigationBtm);
        navigationView.getMenu().findItem(fromWhere).setChecked(false);
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
                Log.d(TAG + "Intent", "Already in Community Activity.");
            }
            return false;
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        Intent intent = getIntent();
        long member = intent.getLongExtra("member", -1);
        if (member == -1) {
            Log.d(TAG + "Intent", "Not valid User");
            Intent toLoginActivity = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(toLoginActivity);
        }

        fromWhere = getIntent().getIntExtra("fromWhere", HOME);
        setActionBar(member);

        postList = findViewById(R.id.postList);

        Call<List<PostingDto>> postingDtoCall = postingApi.getAllPostings();

        postingDtoCall.enqueue(new Callback<List<PostingDto>>() {
            @Override
            public void onResponse(Call<List<PostingDto>> call, Response<List<PostingDto>> response) {
                List<PostingDto> postingDtos = response.body();
                for (PostingDto p : postingDtos) {
                    postList.addView(addPostItem(p));
                }

            }

            @Override
            public void onFailure(Call<List<PostingDto>> call, Throwable t) {

            }
        });



        addPostBtn = findViewById(R.id.addPostBtn);
        addPostBtn.setOnClickListener(view -> {
                    //postList.addView(addPostItem(-1, "성북구 국민대학교 북악관 207호", content, "2023-11-11"))
                    Intent toPostingActivity = new Intent(getApplicationContext(), PostingActivity.class);
                    Log.d(TAG + "Intent", "Convert to Posting Activity.");
                    toPostingActivity.putExtra("fromWhere", COMMUNITY);
                    toPostingActivity.putExtra("member", member);
                    startActivity(toPostingActivity);
                }
        );
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