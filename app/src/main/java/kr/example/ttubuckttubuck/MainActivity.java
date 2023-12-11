package kr.example.ttubuckttubuck;

import static kr.example.ttubuckttubuck.utils.MenuItemID.HOME;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import kr.example.ttubuckttubuck.CustomView.HomeTodoItem;
import kr.example.ttubuckttubuck.CustomView.HomeUserItem;
import kr.example.ttubuckttubuck.CustomView.TodoDialog;
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
    private ImageView addUserBtn, addTodoBtn;
    private Toolbar toolBar;
    private ActionBar actionBar;
    private int fromWhere;
    private HomeUserItem myself;
    private TodoDialog todoDialog;

    private long member;

    // 네트워크로 데이터 전송, Retrofit 객체 생성
    // NetworkClient : 위에서 Retrofit 기본 설정한 클래스 파일
    // MainActivity.this : API서버와 통신 할 액티비티 이름
    Retrofit retrofit = NetworkClient.getRetrofitClient(MainActivity.this);
    TodoListApi todoListApi = retrofit.create(TodoListApi.class);
    FollowApi followApi = retrofit.create(FollowApi.class);
    MemberApi memberApi = retrofit.create(MemberApi.class);

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

    private HomeUserItem addUserItem(MemberDto memberDto) {
        HomeUserItem tmp = new HomeUserItem(getApplicationContext());
        tmp.setTag("userItem_" + memberDto.getId());
        tmp.setUserName(memberDto.getName());
        String userImg = memberDto.getImage();
        if (userImg == null || userImg.equals("011101001001")) {
            Log.d(TAG, "default img set.");
            tmp.setUserDefaultImg();
        }
        else {
            Log.d(TAG + "userImg", "userImg: " + userImg);
            Log.d(TAG, "custom img set.");
            tmp.setUserImg(userImg);
        }
        return tmp;
    }

    private HomeTodoItem addTodoItem(TodoListDto todoListDto) {
        HomeTodoItem tmp = new HomeTodoItem(getApplicationContext());
        tmp.setTag("todoItem" + (++todotemCnt));
        String title = todoListDto.getContent();
        tmp.setTitle(title);
        tmp.setDate(todoListDto.getDate());
        tmp.getTodoChk().setOnClickListener(v-> tmp.getTodoChk().setSelected(!(tmp.getTodoChk().isSelected())));

//        tmp.findViewById(R.id.todoChk).setOnClickListener(view -> {
//            Call<TodoListDto> dummy = todoListApi.editTodoDone(todoListDto.getId());
//            dummy.enqueue(new Callback<TodoListDto>() {
//                @Override
//                public void onResponse(Call<TodoListDto> call, Response<TodoListDto> response) {
//                    Log.d(TAG, "todo checked");
//                    view.findViewById(R.id.todoChk).setSelected(!(view.findViewById(R.id.todoChk).isSelected()));
//                }
//
//                @Override
//                public void onFailure(Call<TodoListDto> call, Throwable t) {
//                    Log.v("api fail", t.toString());
//                }
//            });
//
//        });

        return tmp;
    }

    private void setAddTodo() {
        todoDialog = new TodoDialog(MainActivity.this);
    }

    private void showAddTodoDialog() {
        todoDialog.getConfirmBtn().setOnClickListener(view -> {
            HomeTodoItem result = new HomeTodoItem(getApplicationContext());
            result.getTodoChk().setOnClickListener(v-> result.getTodoChk().setSelected(!(result.getTodoChk().isSelected())));
            if(todoDialog.getContent().equals("") || todoDialog.getContent().equals(null)){
                Toast.makeText(this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            result.setTitle(todoDialog.getContent());
            result.setDate(todoDialog.getDate());
            todoList.addView(result);
            todoDialog.dismiss();
        });
        todoDialog.show();
    }

    private void setActionBar(Long member) {
        toolBar = findViewById(R.id.toolBar);
        setSupportActionBar(toolBar);
        toolBar.setTitle("Home");

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        //actionBar.setTitle("뚜벅");

        navigationView = findViewById(R.id.navigationBtm);
        navigationView.getMenu().findItem(fromWhere).setChecked(false);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 갤럭시 23 abi: arm64-v8a, armabi-v7a, armeabi
        // UI Initialization ↓
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        member = intent.getLongExtra("id", -1);
        if (member == -1) {
            Log.d(TAG + "Intent", "Not valid User");
            Intent toLoginActivity = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(toLoginActivity);
        }

        fromWhere = getIntent().getIntExtra("fromWhere", HOME);
        setActionBar(member);

        addedUserList = findViewById(R.id.addedUserList);
        todoList = findViewById(R.id.todoList);
        scrollViewFriendList = findViewById(R.id.scrollViewFriendList);
        scrollViewFriendList.setVerticalScrollBarEnabled(true);

        myself = findViewById(R.id.userItem0);
        Call<MemberDto> memberDtoCall = memberApi.memberInfo(member);
        memberDtoCall.enqueue(new Callback<MemberDto>() {
            @Override
            public void onResponse(Call<MemberDto> call, Response<MemberDto> response) {
                myself.setUserImg(response.body().getImage());
                myself.setUserName(response.body().getName());
            }

            @Override
            public void onFailure(Call<MemberDto> call, Throwable t) {
                Log.v("api fail", t.toString());
            }
        });

        myself.setOnClickListener(view -> {
            Log.d(TAG + "Intent", "Convert to MyPage Activity");
            Intent toMyPageActivity = new Intent(getApplicationContext(), MyPageActivity.class);
            toMyPageActivity.putExtra("member", member);
            startActivity(toMyPageActivity);
        });

        setAddTodo();
        addTodoBtn = findViewById(R.id.addTodoBtn);
        addTodoBtn.setOnClickListener(view -> {
            Log.d(TAG, "addTodoList() called.");
            showAddTodoDialog();
        });

        addUserBtn = findViewById(R.id.addUserBtn);
        addUserBtn.setOnClickListener(view -> {
            // TODO: FollowActivity 만들고 거기서 팔로우 해서 친구 추가하기
            Log.d(TAG, "addUserBtn called.");
            Intent toAddFriendsActivity = new Intent(getApplicationContext(), AddFriendsActivity.class);
            toAddFriendsActivity.putExtra("fromWhere", HOME);
            toAddFriendsActivity.putExtra("member", member);
            Log.d(TAG + "Intent", "Convert to Community Activity.");
            startActivity(toAddFriendsActivity);
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
                        HomeTodoItem newTodo = addTodoItem(x);
                        newTodo.findViewById(R.id.todoChk).setOnClickListener(view -> {
                            view.setSelected(!(newTodo.findViewById(R.id.todoChk).isSelected()));
                            Call<TodoListDto> dummy = todoListApi.editTodoDone(x.getId());
                            dummy.enqueue(new Callback<TodoListDto>() {
                                @Override
                                public void onResponse(Call<TodoListDto> call, Response<TodoListDto> response) {
                                    Log.d(TAG, "todo checked");
                                }

                                @Override
                                public void onFailure(Call<TodoListDto> call, Throwable t) {
                                    Log.v("api fail", t.toString());
                                }
                            });
                        });
                        todoList.addView(newTodo);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<TodoListDto>> call, Throwable t) {
                todoList.addView(addItem("todoListApi calling Failed, " + t.toString()));
                Log.v("api fail", t.toString());
            }
        });

        /* followsCall.enqueue(new Callback<List<MemberDto>>() {
            @Override
            public void onResponse(Call<List<MemberDto>> call, Response<List<MemberDto>> response) {
                List<MemberDto> follows = response.body();
                if (follows != null) {
                    for (MemberDto x : follows) {
                        addedUserList.addView(addUserItem(x));
                    }
                }
            }

            @Override
            public void onFailure(Call<List<MemberDto>> call, Throwable t) {
                Log.v("api fail", t.toString());
            }
        }); */
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called.");

        Call<MemberDto> memberDtoCall = memberApi.memberInfo(member);
        memberDtoCall.enqueue(new Callback<MemberDto>() {
            @Override
            public void onResponse(Call<MemberDto> call, Response<MemberDto> response) {
                String userImg = response.body().getImage();
                if (userImg == null || userImg.equals("011101001001")) {
                    Log.d(TAG, "default img set.");
                    myself.setUserDefaultImg();
                }
                else {
                    // Log.d(TAG + "userImg", "userImg: " + userImg);
                    Log.d(TAG, "custom img set.");
                    myself.setUserImg(response.body().getImage());
                }
                // Log.d(TAG, "onResume: getImage() value: " + response.body().getImage());
                myself.setUserName(response.body().getName());
            }

            @Override
            public void onFailure(Call<MemberDto> call, Throwable t) {
                Log.v("api fail", t.toString());
            }
        });

        addedUserList.removeAllViews();


        //todoList, follows 불러오기
        Call<List<TodoListDto>> todosCall = todoListApi.getTodoList(member);
        Call<List<MemberDto>> followsCall = followApi.getFollowingList(member);
        followsCall.enqueue(new Callback<List<MemberDto>>() {
            @Override
            public void onResponse(Call<List<MemberDto>> call, Response<List<MemberDto>> response) {
                List<MemberDto> follows = response.body();
                if (follows != null) {
                    for (MemberDto x : follows) {
                        addedUserList.addView(addUserItem(x));
                    }
                }
            }

            @Override
            public void onFailure(Call<List<MemberDto>> call, Throwable t) {
                Log.v("api fail", t.toString());
            }
        });
    }
}