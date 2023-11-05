package kr.example.ttubuckttubuck;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
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

import com.skt.tmap.TMapData;
import com.skt.tmap.TMapGpsManager;
import com.skt.tmap.TMapPoint;
import com.skt.tmap.TMapView;
import com.skt.tmap.overlay.TMapMarkerItem;
import com.skt.tmap.poi.TMapPOIItem;

import java.util.ArrayList;

import kr.example.ttubuckttubuck.utils.Locker;

public class MapActivity extends AppCompatActivity implements TMapView.OnMapReadyListener, TMapGpsManager.OnLocationChangedListener, TMapView.OnApiKeyListenerCallback {
    // static(final) 변수 ↓
    private static final String TAG = "MapActivity_Debug";
    public static final int PERMISSION = 10000;
    private static final int ACCESS_GPS = 1;
    private static final boolean VERBOSE = true;

    // Thread 변수 ↓
    private Handler mainHandler;
    private TMapGpsManager gpsManager;
    private Locker locker = new Locker();
    private boolean isLocked = false;

    private class MarkerHeading extends AsyncTask<Void, Void, Boolean> {
        private Context mContext = null;

        public MarkerHeading(Context context) {
            mContext = context;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            while (curLocation != null) {
                //Log.i(TAG, "Cur rotation: " + curMarker.getRotation());
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
        }
    }

    // UI 구성 요소 ↓
    private ViewGroup container;
    private TMapView mapView;
    private TMapPoint curLocation;
    private TMapMarkerItem curMarker;
    private ImageButton mainBtn, reloadBtn;
    private Bitmap markerBmp;

    // API 변수 ↓
    private double firstLatitude, firstLongitude;
    private String currentAddressAferReverseGedoCoding = null;
    private TMapData currentLocation = null;

    @Override
    public void onSKTMapApikeySucceed() {
        if (VERBOSE)
            Toast.makeText(this, "API Key is valid.", Toast.LENGTH_SHORT);
        Log.d(TAG, "API Key is valid.");
        //locker.unlock();
    }

    @Override
    public void onSKTMapApikeyFailed(String s) {
        if (VERBOSE)
            Toast.makeText(this, "API Key is invalid.", Toast.LENGTH_SHORT);
        Log.e(TAG, "API Key is invalid.");
        //locker.unlock();
    }

    @Override
    public void onMapReady() {
        Log.d(TAG + "_Callback", "[ Callback] : onMapReady() called.");

        // Default zoomLevel value: 13.
        //Log.d(TAG + "_Callback", "Zoom level: " + mapView.getZoomLevel());

        mapView.setRotateEnable(true);

        //mapView.setTrackingMode(true);

        // 화면 회전 유무 설정.
        mapView.setCompassMode(false);

        // proper zoomLevel value set.
        mapView.setZoomLevel(17);

        curLocationInit();
    }

    @Override
    public void onLocationChange(Location location) {
        Log.d(TAG + "_Callback", "[ Callback] : onLocationChange() called.");
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        //reverseGeoCoding(curLocation.getLatitude(), curLocation.getLongitude());
        curLocation.setLatitude(latitude);
        curLocation.setLongitude(longitude);

        Log.d(TAG + "_Callback", "Changed location: " + latitude + ", " + longitude);
        Log.d(TAG + "_Callback", "Reverse GeoCoding address: " + currentAddressAferReverseGedoCoding);
        if (VERBOSE)
            Toast.makeText(this, "Changed location: " + latitude + ", " + longitude, Toast.LENGTH_SHORT).show();

        curMarker.setTMapPoint(curLocation);
        mapView.setCenterPoint(curLocation.getLatitude(), curLocation.getLongitude(), true);
        //searchAround();
    }

    private void reverseGeoCoding(double latitude, double longitude) {
        //double editLatitude = Double.valueOf(String.format("%.6f", latitude));
        //double editLongitude = Double.valueOf(String.format("%.6f", longitude));
        //Log.d(TAG, editLatitude + ", " + editLongitude);
        currentAddressAferReverseGedoCoding = new TMapData().convertGpsToAddress(latitude, longitude);
    }

    private void searchAround() {
        TMapData mTMapData = new TMapData();
        TMapPoint tmp = curLocation;
        mTMapData.findAroundNamePOI(tmp, "편의점;은행", 1, 99, new TMapData.OnFindAroundNamePOIListener() {
            @Override
            public void onFindAroundNamePOI(ArrayList<TMapPOIItem> arrayList) {
                for(int i = 0; i < arrayList.size(); i++){
                    TMapPOIItem item = arrayList.get(i);
                    Log.d(TAG, "POI name: " + item.getPOIName() + ", address: "+ item.getPOIAddress().replace("mull", ""));
                }
            }
        });
    }

    private void refreshLocation(){
        mapView.setCenterPoint(curLocation.getLatitude(), curLocation.getLongitude());
        mapView.setZoomLevel(17);
    }

    private void curLocationInit() {
        Log.d(TAG, "curLocationInit() called.");

        // 현재 위치를 나타낼 위치 class인 TMapPoint의 객체 생성.
        curLocation = new TMapPoint(firstLatitude, firstLongitude);
        refreshLocation();

        // GPS 추적 기능 class인 TMapGpsManager 객체 할당 후 Callback 등록.
        gpsManager = new TMapGpsManager(this);

        // GPS 설정
        gpsManager.openGps();
        gpsManager.setMinDistance(3);
        gpsManager.setMinTime(100);
        gpsManager.setProvider(TMapGpsManager.PROVIDER_NETWORK);
        //gpsManager.setProvider(TMapGpsManager.PROVIDER_GPS);
        Log.d(TAG, "gps getMinDistance: " + gpsManager.getMinDistance());
        /*gpsManager.setMinTime(500);
        gpsManager.setMinDistance(2);*/


        // 지도 위에 표시 될 사용자의 위치를 나타내는 TMapMarkerItem의 객체 생성.
        curMarker = new TMapMarkerItem();
        curMarker.setTMapPoint(curLocation);
        curMarker.setId("0");
        curMarker.setName("현재 위치");
        curMarker.setVisible(true);
        //Log.d(TAG, "curMarker info: " + curMarker.toString());

        // Bitmap 생성 및 marker에 등록.
        markerBmp = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.marker), 100, 100, true);
        curMarker.setIcon(markerBmp);

        try {
            // 지도에 표시할 marker 등록.
            mapView.addTMapMarkerItem(curMarker);
            Log.d(TAG, "curMarker added at mapView.");
        } catch (NullPointerException e) {
            Log.e(TAG, "NullPointerException occurred by Bitmap: " + e);
            e.printStackTrace();
        } catch (Exception e) {
            Log.e(TAG, "Failed to add the marker to mapView: " + e);
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Log.d(TAG, "onCreate() called.");

        mainHandler = new Handler(getMainLooper());
        // UI 초기화 ↓
        mapView = new TMapView(this);
        mapView.setSKTMapApiKey("rZWWy5hD2n87YkkTKDsV2ou4xLJHWpb5OiqBswXh"); // API Key 할당.

        /*try {
            locker.lock();
        } catch (InterruptedException e) {
            Log.w(TAG, "lock() failed.");
            e.printStackTrace();
        }*/

        container = findViewById(R.id.mapView);
        container.addView(mapView);

        reloadBtn = findViewById(R.id.refreshBtn);
        reloadBtn.setOnClickListener(view ->{
            refreshLocation();
            if(VERBOSE)
                Toast.makeText(this, "Refresh", Toast.LENGTH_SHORT).show();
        });

        mainBtn = findViewById(R.id.goBackBtn);
        mainBtn.setOnClickListener(view -> {
            Intent toMainActivity = new Intent(getApplicationContext(), MainActivity.class);
            Log.d(TAG + "Intent", "Convert to Main Activity.");
            startActivity(toMainActivity);
        });

        //MarkerHeading task = new MarkerHeading(getApplicationContext());
        //task.execute();

        // 위치 접근 권한 확인 ↓:
        checkPermission(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean checkPermission(String[] permissions, int type) {
        Log.d(TAG, "checkPermission() called.");
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
        Log.d(TAG, "All permissions granted.");
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "Intent to onRequestPermissionsResult.");

        switch (requestCode) {
            case ACCESS_GPS:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission has been granted.");

                    //curLocationInit();
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
        Log.d(TAG, "onPause() called.");
        if (curLocation != null)
            curLocation = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called.");

        LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        Location curLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        firstLatitude = curLocation.getLatitude();
        firstLongitude = curLocation.getLongitude();
        Log.d(TAG, "Detected current location as first: " + firstLatitude + ", " + firstLongitude);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called.");
        if (curLocation != null)
            curLocation = null;
    }
}
