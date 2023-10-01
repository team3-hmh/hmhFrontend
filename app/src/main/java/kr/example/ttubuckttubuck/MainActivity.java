package kr.example.ttubuckttubuck;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import net.daum.android.map.MapView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import android.content.pm.Signature;

/*
    네이티브 앱 키	b2dce0727b3d31f38a6d7e9f4e36e7d9
    REST API 키	097a63061505e5e0292a89c873d6ac44
    JavaScript 키	213f8b4ddf969d0cc5167f564938867b
    Admin 키	7de1e10437899b818d5bb109857cc258
 */

public class MainActivity extends AppCompatActivity {
    private ViewGroup container;
    private MapView mapView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 갤럭시 23 abi: arm64-v8a, armabi-v7a, armeabi
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = new MapView(this);
        Log.i("fuckyou", "mapView allocation succeess.");
        container = (ViewGroup) findViewById(R.id.mapView);
        Log.i("fuckyou", "container allocation succeess.");
        container.addView(mapView);
        Log.i("fuckyou", "addView allocation succeess.");
    }
}