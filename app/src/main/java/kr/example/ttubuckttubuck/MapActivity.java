package kr.example.ttubuckttubuck;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class MapActivity extends AppCompatActivity implements MapView.CurrentLocationEventListener {
    // static(final) variables ↓
    private static final String TAG = "MapActivity_Debug";
    public static final int PERMISSION = 10000;
    private static final int ACCESS_GPS = 1;

    // Thread variables ↓
    private Handler mainHandler;

    private class MarkerHeading extends AsyncTask<Void, Void, Boolean> {
        private Context mContext = null;

        public MarkerHeading(Context context) {
            mContext = context;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            while(marker != null) {
                //Log.i(TAG, "Cur rotation: " + marker.getRotation());
                publishProgress();
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "AsyncTask executed.");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            Log.i(TAG, "onProgressUpdate called.");
            Toast.makeText(getApplicationContext(), "Cur rotatioin: " + marker.getRotation(), Toast.LENGTH_SHORT).show();
        }
    }

    // UI components ↓
    private ViewGroup container;
    private MapView mapView;
    private MapPOIItem marker;
    private ImageButton mainBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mainHandler = new Handler(getMainLooper());

        // UI Initialization ↓
        mapView = new MapView(this);
        container = findViewById(R.id.mapView);
        container.addView(mapView);

        mainBtn = findViewById(R.id.goBackBtn);
        mainBtn.setOnClickListener(view -> {
            Intent toMainActivity = new Intent(getApplicationContext(), MainActivity.class);
            Log.d(TAG + "Intent", "Convert to Main Activity.");
            startActivity(toMainActivity);
        });

        // 지도 위에 표시 될 사용자의 위치를 나타내는 marker 객체 생성.
        marker = new MapPOIItem();
        marker.setItemName("Current Location");
        marker.setShowCalloutBalloonOnTouch(true); // 안 됨. 확인 요망.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
        mapView.addPOIItem(marker);

        /*LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        Location curLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        double latitude = curLocation.getLatitude();
        double longitude = curLocation.getLongitude();
        MapPoint curPosition = MapPoint.mapPointWithGeoCoord(latitude, longitude);*/

        //MarkerHeading task = new MarkerHeading(getApplicationContext());
        //task.execute();

        // 위치 접근 권한 확인 ↓:
        checkPermission(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean checkPermission(String[] permissions, int type) {
        SharedPreferences preference = getPreferences(MODE_PRIVATE);
        //if(preference.getBoolean("isFirstCheckPermission", true))
        //    return true;

        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                return true;

            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(this, permissions, type);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "The Error occurred during granting permissions: " + e);
            return false;
        }
        Log.i(TAG, "All permissions granted.");
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case ACCESS_GPS:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "Permission has been granted.");
                } else {
                    Toast.makeText(getApplicationContext(), "Permission should be granted.", Toast.LENGTH_SHORT);
                    Intent toMainActivity = new Intent(getApplicationContext(), MainActivity.class);
                    Log.d(TAG + "Intent", "Back to Main Activity: Permission denied.");
                    startActivity(toMainActivity);
                }
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(marker != null)
            marker = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(marker != null)
            marker = null;
    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
        Log.i(TAG, "onCurrentLocationUpdate called.");
    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {
        Log.w(TAG, "onCurrentLocationUpdateFailed called.");
    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }
}
