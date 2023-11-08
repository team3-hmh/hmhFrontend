package kr.example.ttubuckttubuck;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import kr.example.ttubuckttubuck.api.TodoListApi;
import kr.example.ttubuckttubuck.dto.TodoListDto;
import kr.example.ttubuckttubuck.utils.NetworkClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/*
    네이티브 앱 키	b2dce0727b3d31f38a6d7e9f4e36e7d9
    REST API 키	097a63061505e5e0292a89c873d6ac44
    JavaScript 키	213f8b4ddf969d0cc5167f564938867b
    Admin 키	7de1e10437899b818d5bb109857cc258
 */

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity_Debug";
    private static int inx = 0;
    private HorizontalScrollView scrollViewFriendList;

    // UI components ↓
    private Button mainBtn, mapBtn, communityBtn;
    private LinearLayout layoutList;
    private LinearLayout layoutListItem;

    // 네트워크로 데이터 전송, Retrofit 객체 생성
    // NetworkClient : 위에서 Retrofit 기본 설정한 클래스 파일
    // MainActivity.this : API서버와 통신 할 액티비티 이름
    Retrofit retrofit = NetworkClient.getRetrofitClient(MainActivity.this);
    TodoListApi todoListApi = retrofit.create(TodoListApi.class);

    /*private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }*/

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 갤럭시 23 abi: arm64-v8a, armabi-v7a, armeabi
        // UI Initialization ↓
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layoutList = findViewById(R.id.layoutList);
        scrollViewFriendList = findViewById(R.id.scrollViewFriendList);
        scrollViewFriendList.setVerticalScrollBarEnabled(true);

        //Api 요청
        Call<List<TodoListDto>> todos = todoListApi.getTodoList(1L);
        todos.enqueue(new Callback<List<TodoListDto>>() {
            @Override
            public void onResponse(Call<List<TodoListDto>> call, Response<List<TodoListDto>> response) {
                List<TodoListDto> todoLists = response.body();
                if (todoLists != null) {
                    for (TodoListDto x : todoLists) {
                        layoutList.addView(addItem(x.getContent()));
                    }
                }
            }
            @Override
            public void onFailure(Call<List<TodoListDto>> call, Throwable t) {
                layoutList.addView(addItem("Api calling Failed, " + t.toString()));
                Log.v("api fail", t.toString());
            }
        });

        mainBtn = findViewById(R.id.buttonMain);
        mainBtn.setOnClickListener(view -> {
            layoutList.addView(addItem());
        });

        mapBtn = findViewById(R.id.buttonMap);
        mapBtn.setOnClickListener(view -> {
            Intent toMapActivity = new Intent(MainActivity.this, MapActivity.class);
            /*
            toMapActivity.putExtra("deviceId", deviceId);
            toMapActivity.putExtra("portNum", portNum);
            toMapActivity.putExtra("baudRate", baudRate);
            */
            Log.d(TAG+"Intent", "Convert to Map Activity.");
            startActivity(toMapActivity);
        });

        communityBtn = findViewById(R.id.buttonCommunity);
        communityBtn.setOnClickListener(view -> {
            Intent toCommunityActivity = new Intent(getApplicationContext(), CommunityActivity.class);
            Log.d(TAG+"Intent", "Convert to Community Activity.");
            startActivity(toCommunityActivity);
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