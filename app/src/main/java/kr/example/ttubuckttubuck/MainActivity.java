package kr.example.ttubuckttubuck;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/*
    네이티브 앱 키	b2dce0727b3d31f38a6d7e9f4e36e7d9
    REST API 키	097a63061505e5e0292a89c873d6ac44
    JavaScript 키	213f8b4ddf969d0cc5167f564938867b
    Admin 키	7de1e10437899b818d5bb109857cc258
 */

public class MainActivity extends AppCompatActivity {
    private static int inx = 0;
    private HorizontalScrollView scrollViewFriendList;

    // UI components ↓
    private Button buttonMain, buttonMap, buttonCommunity;
    private LinearLayout layoutList;
    private LinearLayout layoutListItem;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 갤럭시 23 abi: arm64-v8a, armabi-v7a, armeabi
        // UI Initialization ↓
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layoutList = findViewById(R.id.layoutList);
        scrollViewFriendList = findViewById(R.id.scrollViewFriendList);
        scrollViewFriendList.setVerticalScrollBarEnabled(true);

        buttonMain = findViewById(R.id.buttonMain);
        buttonMain.setOnClickListener(view -> {
            layoutList.addView(addItem());
        });

        buttonMap = findViewById(R.id.buttonMap);
        buttonMap.setOnClickListener(view->{
            Intent toMapActivity = new Intent(MainActivity.this, MapActivity.class);
            /*
            toMapActivity.putExtra("deviceId", deviceId);
            toMapActivity.putExtra("portNum", portNum);
            toMapActivity.putExtra("baudRate", baudRate);
            */
            Log.d("MainActivity_Intent", "Convert to Map Activity.");
            startActivity(toMapActivity);
        });

        buttonCommunity = findViewById(R.id.buttonCommunity);
        buttonCommunity.setOnClickListener(view->{

        });
    }
}