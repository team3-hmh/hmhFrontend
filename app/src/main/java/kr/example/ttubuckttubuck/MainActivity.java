package kr.example.ttubuckttubuck;

import static kr.example.ttubuckttubuck.utils.MenuItemID.HOME;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import kr.example.ttubuckttubuck.CustomView.HomeTodoItem;
import kr.example.ttubuckttubuck.CustomView.HomeUserItem;
import kr.example.ttubuckttubuck.api.FollowApi;
import kr.example.ttubuckttubuck.api.MemberApi;
import kr.example.ttubuckttubuck.api.TodoListApi;
import kr.example.ttubuckttubuck.dto.MemberDto;
import kr.example.ttubuckttubuck.dto.TodoListDto;
import kr.example.ttubuckttubuck.utils.NetworkClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity_Debug";
    private static int inx = 0;
    private static int userItemCnt = 0;
    private static int todotemCnt = 0;
    private HorizontalScrollView scrollViewFriendList;

    // UI components ↓
    private BottomNavigationView navigationView;
    private LinearLayout todoList, addedUserList;
    private ImageView addUserBtn;
    private Toolbar toolBar;
    private ActionBar actionBar;
    private int fromWhere;

    // 네트워크로 데이터 전송, Retrofit 객체 생성
    // NetworkClient : 위에서 Retrofit 기본 설정한 클래스 파일
    // MainActivity.this : API서버와 통신 할 액티비티 이름
    Retrofit retrofit = NetworkClient.getRetrofitClient(MainActivity.this);
    TodoListApi todoListApi = retrofit.create(TodoListApi.class);
    FollowApi followApi = retrofit.create(FollowApi.class);
    MemberApi memberApi = retrofit.create(MemberApi.class);

    private LinearLayout addItem() {
        LinearLayout tmp = new LinearLayout(getApplicationContext());
        tmp.setOrientation(LinearLayout.HORIZONTAL);
        tmp.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100));
        tmp.setTag("listItem" + String.valueOf(++inx));

        TextView tv1 = new TextView(getApplicationContext());
        tv1.setText("Test");
        tmp.addView(tv1);

        return tmp;
    }

    private LinearLayout addItem(String content) {
        LinearLayout tmp = new LinearLayout(getApplicationContext());
        tmp.setOrientation(LinearLayout.HORIZONTAL);
        tmp.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100));
        tmp.setTag("listItem" + String.valueOf(++inx));

        TextView tv1 = new TextView(getApplicationContext());
        tv1.setText(content);
        tmp.addView(tv1);

        return tmp;
    }

    private HomeUserItem addFriendItem(MemberDto memberDto){
        HomeUserItem tmp = new HomeUserItem(getApplicationContext());
        tmp.setTag("userItem"+ (userItemCnt++));
        tmp.setUserName(memberDto.getName());
        // TODO: 프로필 사진 불러오게 바꾸기
        tmp.setUserImg(R.drawable.profile);
        return tmp;
    }

    private HomeTodoItem addTodoItem(TodoListDto todoListDto){
        HomeTodoItem tmp = new HomeTodoItem(getApplicationContext());
        tmp.setTag("todoItem"+ (++todotemCnt));
        String title = todoListDto.getContent();
        tmp.setTitle(title);
        tmp.setDate(todoListDto.getDate());
        tmp.findViewById(R.id.todoChk).setOnClickListener(view-> {
            checkingTodo(view, todoListDto);
        });
        return tmp;
    }

    // TODO: 버튼 클릭하면 체크 바뀌게
    private void checkingTodo(View view, TodoListDto todoListDto) {
        Call<TodoListDto> dummy = todoListApi.editTodoDone(todoListDto.getId());
        dummy.enqueue(new Callback<TodoListDto>() {
            @Override
            public void onResponse(Call<TodoListDto> call, Response<TodoListDto> response) {
                view.setPressed(response.body().getDone());
            }

            @Override
            public void onFailure(Call<TodoListDto> call, Throwable t) {
                Log.v("api fail", t.toString());
            }
        });
    }

    private void setActionBar() {
        toolBar = findViewById(R.id.toolBar);
        setSupportActionBar(toolBar);
        toolBar.setTitle("Home");

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setTitle("Home");

        navigationView = findViewById(R.id.navigationBtm);
        navigationView.getMenu().findItem(fromWhere).setChecked(false);
        navigationView.getMenu().findItem(HOME).setChecked(true);
        navigationView.setOnItemSelectedListener(item -> {
            Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + ": " + item.getItemId() + " : " + R.id.map);
            if (item.getTitle().equals("Map")) {
                Intent toMapActivity = new Intent(getApplicationContext(), MapActivity.class);
                toMapActivity.putExtra("fromWhere", HOME);
                Log.d(TAG + "Intent", "Convert to Map Activity.");
                startActivity(toMapActivity);
            } else if (item.getTitle().equals("Home")) {
                // todoList.addView(addItem());
//                todoList.addView(addTodoItem("김호","혜화", "2023-11-11"));
                Log.d(TAG + "Intent", "Already in Main Activity.");
            } else { // Community
                Intent toCommunityActivity = new Intent(getApplicationContext(), CommunityActivity.class);
                toCommunityActivity.putExtra("fromWhere", HOME);
                Log.d(TAG + "Intent", "Convert to Community Activity.");
                startActivity(toCommunityActivity);
            }
            return false;
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 갤럭시 23 abi: arm64-v8a, armabi-v7a, armeabi
        // UI Initialization ↓
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        long member = intent.getLongExtra("id", -1);
        if (member == -1) {
            Log.d(TAG + "Intent", "Not valid User");
            Intent toLoginActivity = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(toLoginActivity);
        }

        fromWhere = getIntent().getIntExtra("fromWhere", HOME);
        setActionBar();

        addedUserList = findViewById(R.id.addedUserList);
        todoList = findViewById(R.id.todoList);
        scrollViewFriendList = findViewById(R.id.scrollViewFriendList);
        scrollViewFriendList.setVerticalScrollBarEnabled(true);

        addUserBtn = findViewById(R.id.addUserBtn);
        addUserBtn.setOnClickListener(view -> {
            // TODO: FollowActivity 만들고 거기서 팔로우 해서 친구 추가하기
            Log.d(TAG, "addUserBtn called.");
//            addedUserList.addView(addFriendItem(String.valueOf(userItemCnt)));
        });

        //todoList, follows 불러오기
        Call<List<TodoListDto>> todosCall = todoListApi.getTodoList(member);
        Call<List<MemberDto>> followsCall = followApi.getFollowingList(member);

        todosCall.enqueue(new Callback<>() {
            //로그인 성공
            @Override
            public void onResponse(Call<List<TodoListDto>> call, Response<List<TodoListDto>> response) {
                List<TodoListDto> todoLists = response.body();
                for (TodoListDto x : todoLists) {
                    if (!x.getDone()) {
                        Log.d(TAG, String.valueOf(x.getId()));
                        todoList.addView(addTodoItem(x));
                    }

                }
            }

            @Override
            public void onFailure(Call<List<TodoListDto>> call, Throwable t) {
                todoList.addView(addItem("todoListApi calling Failed, " + t.toString()));
                Log.v("api fail", t.toString());
            }
        });

        followsCall.enqueue(new Callback<List<MemberDto>>() {
            @Override
            public void onResponse(Call<List<MemberDto>> call, Response<List<MemberDto>> response) {
                List<MemberDto> follows = response.body();
                if (follows != null) {
                    for (MemberDto x : follows) {
                        addedUserList.addView(addFriendItem(x));
                    }
                }
            }

            @Override
            public void onFailure(Call<List<MemberDto>> call, Throwable t) {
                Log.v("api fail", t.toString());
            }
        });

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