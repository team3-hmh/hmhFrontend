package kr.example.ttubuckttubuck;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import net.daum.mf.map.api.MapView;

/*
    네이티브 앱 키	b2dce0727b3d31f38a6d7e9f4e36e7d9
    REST API 키	097a63061505e5e0292a89c873d6ac44
    JavaScript 키	213f8b4ddf969d0cc5167f564938867b
    Admin 키	7de1e10437899b818d5bb109857cc258
 */

public class MainActivity extends AppCompatActivity {
    private ViewGroup container;
    private MapView mapView;

    // UI components ↓
    private Button button1;
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

    LinearLayout addItem(){

        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 갤럭시 23 abi: arm64-v8a, armabi-v7a, armeabi
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layoutList = findViewById(R.id.layoutList);

        button1 = findViewById(R.id.button1);
        button1.setOnClickListener(view->{
            layoutList.addView(addItem());
        });

        /*mapView = new MapView(this);
        container = (ViewGroup) findViewById(R.id.mapView);
        container.addView(mapView);*/

    }
}