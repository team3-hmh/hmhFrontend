package kr.example.ttubuckttubuck;

import static kr.example.ttubuckttubuck.utils.MenuItemID.*;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import kr.example.ttubuckttubuck.CustomView.AddUserItem;
import kr.example.ttubuckttubuck.api.FollowApi;
import kr.example.ttubuckttubuck.api.MemberApi;
import kr.example.ttubuckttubuck.dto.FollowDto;
import kr.example.ttubuckttubuck.dto.MemberDto;
import kr.example.ttubuckttubuck.dto.SignInDto;
import kr.example.ttubuckttubuck.utils.NetworkClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddFriendsActivity extends AppCompatActivity {
    private static final String TAG = "AddFriendsActivity_Debug";

    Retrofit retrofit = NetworkClient.getRetrofitClient(AddFriendsActivity.this);

    MemberApi memberApi = retrofit.create(MemberApi.class);
    FollowApi followApi = retrofit.create(FollowApi.class);

    // UI components ↓
    private BottomNavigationView navigationView;
    private Toolbar toolBar;
    private ActionBar actionBar;
    private ImageButton searchBtn, goBackBtn;
    private EditText searchEditTxt;
    private LinearLayout userList;
    private AddUserItem userItem;

    public void searchUser(String query){
        SignInDto signInDto = new SignInDto(query, "empty");
        Call<Long> idCall = memberApi.findIdByEmail(signInDto);
        idCall.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                Long searchedId = response.body();
                Call<MemberDto> serachedCall = memberApi.memberInfo(searchedId);
                serachedCall.enqueue(new Callback<MemberDto>() {
                    @Override
                    public void onResponse(Call<MemberDto> call, Response<MemberDto> response) {
                        MemberDto searchedDto = response.body();
                        // TODO: 찾은 멤버 정보를 팔로잉 창에 표시
                    }

                    @Override
                    public void onFailure(Call<MemberDto> call, Throwable t) {
                        Log.v("api fail", t.toString());
                    }
                });
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                Log.v("api fail", t.toString());
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);

        Intent intent = getIntent();
        long member = intent.getLongExtra("member", -1);
        if (member == -1) {
            Log.d(TAG + "Intent", "Not valid User");
            Intent toLoginActivity = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(toLoginActivity);
        }
        setActionBar(member);

        userList = findViewById(R.id.userList);
        searchEditTxt = findViewById(R.id.searchEditTxt);
        searchBtn = findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(view->{
            final String query = searchEditTxt.getText().toString();
            if(query.equals("") || query == null)
                Toast.makeText(getApplicationContext(), "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show();
            else
                searchUser(query);
        });

        Call<List<MemberDto>> followingCall = followApi.getFollowingList(member);
        followingCall.enqueue(new Callback<List<MemberDto>>() {
            @Override
            public void onResponse(Call<List<MemberDto>> call, Response<List<MemberDto>> response) {
                List<MemberDto> followingList = response.body();
                // TODO: 팔로우 하고 있는 유저 띄우기 (View로 출력하고 Array나 List로 묶기)
            }

            @Override
            public void onFailure(Call<List<MemberDto>> call, Throwable t) {
                Log.v("api fail", t.toString());
            }
        });

        userItem = findViewById(R.id.userItem0);
        userItem.setUserName("Kim Ho");
        userItem.getFollowBtn().setOnClickListener(view -> {

            SignInDto dummy = new SignInDto(userItem.getUserName(), "dummy");
            Call<Long> followingIdCall = memberApi.findIdByEmail(dummy);
            followingIdCall.enqueue(new Callback<Long>() {
                @Override
                public void onResponse(Call<Long> call, Response<Long> response) {
                    FollowDto followDto = new FollowDto(member, response.body());
                    followApi.follow(followDto);
                    Toast.makeText(getApplicationContext(), "팔로우 했습니다", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<Long> call, Throwable t) {
                    Log.v("api fail", t.toString());
                }
            });

        });

        userItem.getUnfollowBtn().setOnClickListener(view -> {

            SignInDto dummy = new SignInDto(userItem.getUserName(), "dummy");
            Call<Long> followingIdCall = memberApi.findIdByEmail(dummy);
            followingIdCall.enqueue(new Callback<Long>() {
                @Override
                public void onResponse(Call<Long> call, Response<Long> response) {
                    FollowDto followDto = new FollowDto(member, response.body());
                    followApi.unfollow(followDto);
                    Toast.makeText(getApplicationContext(), "팔로우를 취소했습니다", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<Long> call, Throwable t) {
                    Log.v("api fail", t.toString());
                }
            });

        });
    }

    private void setActionBar(Long member) {
        toolBar = findViewById(R.id.toolBar);
        setSupportActionBar(toolBar);
        toolBar.setTitle("Friends");

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setTitle("Friends");

        goBackBtn = findViewById(R.id.goBackBtn);
        goBackBtn.setOnClickListener(view->{
            Intent toMainActivity = new Intent(getApplicationContext(), MainActivity.class);
            Log.d(TAG + "Intent", "Convert to Main Activity.");
            toMainActivity.putExtra("fromWhere", HOME);
            startActivity(toMainActivity);
        });

        navigationView = findViewById(R.id.navigationBtm);
        navigationView.getMenu().findItem(HOME).setChecked(true);
        navigationView.setOnItemSelectedListener(item -> {
            Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + ": " + item.getItemId() + " : " + R.id.map);
            if (item.getTitle().equals("Map")) {
                Intent toMapActivity = new Intent(getApplicationContext(), MapActivity.class);
                toMapActivity.putExtra("fromWhere", HOME);
                toMapActivity.putExtra("member", member);
                Log.d(TAG + "Intent", "Convert to Map Activity.");
                startActivity(toMapActivity);
            } else if (item.getTitle().equals("Home")) {
                // todoList.addView(addItem());
//                todoList.addView(addTodoItem("김호","혜화", "2023-11-11"));
                Log.d(TAG + "Intent", "Already in Main Activity.");
            } else { // Community
                Intent toCommunityActivity = new Intent(getApplicationContext(), CommunityActivity.class);
                toCommunityActivity.putExtra("fromWhere", HOME);
                toCommunityActivity.putExtra("member", member);
                Log.d(TAG + "Intent", "Convert to Community Activity.");
                startActivity(toCommunityActivity);
            }
            return false;
        });
    }
}