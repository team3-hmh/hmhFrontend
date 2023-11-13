package kr.example.ttubuckttubuck;

import static kr.example.ttubuckttubuck.DestinationsList.listAdapter;
import static kr.example.ttubuckttubuck.DestinationsList.listItems;
import static kr.example.ttubuckttubuck.utils.MenuItemID.MAP;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.skt.tmap.TMapData;
import com.skt.tmap.TMapGpsManager;
import com.skt.tmap.TMapInfo;
import com.skt.tmap.TMapPoint;
import com.skt.tmap.TMapView;
import com.skt.tmap.address.TMapAddressInfo;
import com.skt.tmap.overlay.TMapMarkerItem;
import com.skt.tmap.overlay.TMapPolyLine;
import com.skt.tmap.poi.TMapPOIItem;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import kr.example.ttubuckttubuck.utils.ReverseGeoCoding;

public class MapActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener, TMapView.OnMapReadyListener, TMapGpsManager.OnLocationChangedListener, TMapView.OnApiKeyListenerCallback, SlidingUpPanelLayout.PanelSlideListener {
    // static(final) 변수 ↓
    private static final String TAG = "MapActivity_Debug";
    private static final String appKey = "rZWWy5hD2n87YkkTKDsV2ou4xLJHWpb5OiqBswXh";
    public static final int PERMISSION = 10000;
    private static final int ACCESS_GPS = 1;
    private static final boolean VERBOSE = false;

    // Thread 변수 ↓
    private Handler mainHandler;
    private TMapGpsManager gpsManager;
    private ExecutorService threadPool;
    private Locker locker = new Locker();
    private boolean isLocked = false;

    private static class Locker {
        private static final ReentrantLock locker = new ReentrantLock();
        private static final Condition condition = locker.newCondition();
        private static boolean key = false;

        public static void lock() {
            locker.lock();
            try {
                synchronized (condition) {
                    Log.d(TAG, "await looping.");
                    if (!key)
                        condition.await();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                Log.d(TAG, "unlock called.");
                locker.unlock();
            }
        }

        public static void unlock() {
            key = true;
            synchronized (condition) {
                Log.d(TAG, "notifyAll called.");
                condition.notifyAll();
            }
        }
    }

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
    public static SlidingUpPanelLayout slidePanel;
    private static TMapView mapView;
    private static TMapPoint curLocation;
    private TMapMarkerItem curMarker;
    private ImageButton mainBtn, reloadBtn;
    private Button searchBtn;
    private EditText destinationTxt;
    private Bitmap markerBmp;
    private Fragment destinationsFragment;
    private Toolbar toolBar;
    private ActionBar actionBar;
    private BottomNavigationView navigationView;

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        Log.d(TAG, "onPanelSlide() called.");
    }

    // Not working.
    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
        Log.d(TAG, "onPanelStateChanged() called.");
        /*
        if (previousState == SlidingUpPanelLayout.PanelState.COLLAPSED || newState == SlidingUpPanelLayout.PanelState.DRAGGING) {
            Log.d(TAG, "if");
            runOnUiThread(() -> {
                destinationTxt.setEnabled(false);
            });
        }
        else {
            Log.d(TAG, "else");
            runOnUiThread(() -> {
                destinationTxt.setEnabled(true);
            });
        }*/
    }

    // API 변수 ↓
    private double firstLatitude, firstLongitude;
    private String currentAddressAferReverseGedoCoding = null;
    private ReverseGeoCoding mReverseGeoCoder;
    private List<TMapPoint> mapPoints;
    private static TMapPolyLine mPolyLine;

    @Override
    public void onBackStackChanged() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(getSupportFragmentManager().getBackStackEntryCount() > 0);
    }

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
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: " + item.getTitle());
        if (item.getTitle() != null && item.getTitle().equals("Refresh")) {
            refreshLocation();
            if (VERBOSE)
                Toast.makeText(this, "Refresh", Toast.LENGTH_SHORT).show();
        } else {
            Intent toMainActivity = new Intent(getApplicationContext(), MainActivity.class);
            Log.d(TAG + "Intent", "Convert to Main Activity.");
            startActivity(toMainActivity);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh, menu);
        return true;
    }

    @Override
    public void onMapReady() {
        Log.d(TAG + "_Callback", "[ Callback ] : onMapReady() called.");

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
        /*final float curLatitude = (float) location.getLatitude();
        final float curLongitude = (float) location.getLongitude();*/
        if (curLocation != null) {
            curLocation.setLatitude(location.getLatitude());
            curLocation.setLongitude(location.getLongitude());

            Log.d(TAG + "_Callback", "Changed location: " + location.getLatitude() + ", " + location.getLongitude());

            if (VERBOSE)
                Toast.makeText(this, "Changed location: " + location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_SHORT).show();

            refreshLocation();
        }
    }

    private String reverseGeoCoding(double latitude, double longitude) {
        //double editLatitude = Double.valueOf(String.format("%.6f", latitude));
        //double editLongitude = Double.valueOf(String.format("%.6f", longitude));

        TMapAddressInfo result = new TMapData().reverseGeocoding(latitude, longitude, "A10");
        /*new TMapData().convertGpsToAddress(longitude, latitude, new TMapData.OnConvertGPSToAddressListener() {
                    @Override
                    public void onConverGPSToAddress(String s) {
                        // Log.d("findAllPOI_Result", "GPS to address: " + s);

                    }
                }
        );*/

        // Log.d(TAG, "GeoCoding result: " + result.strCity_do + " " + result.strGu_gun + " " + result.strLegalDong + " " + result.strRoadName + " " + result.strBuildingIndex + " " + result.strBuildingName);
        // Log.d(TAG, "GeoCoding result: " + result.strCity_do + " " + result.strGu_gun + " " + result.strLegalDong + " " + result.strBunji + " " + result.strBuildingName);
        Log.d(TAG, "GeoCoding result: " + result.strFullAddress);
        return result.strFullAddress.split(",")[1];
    }

    // Success to work.
    private void searchAround(String[] keywords) {
        String query = "";
        for (int i = 0; i < keywords.length; i++) {
            query += keywords[i];
            if (i != keywords.length - 1)
                query += ";";
        }

        TMapPoint tmp = curLocation;
        new TMapData().findAroundNamePOI(tmp, query, 1, 1000, arrayList -> {
            for (int i = 0; i < arrayList.size(); i++) {
                TMapPOIItem item = arrayList.get(i);
                Log.d(TAG, "POI name: " + item.getPOIName() + ", address: " + item.getPOIAddress().replace("mull", ""));
            }
        });
    }

    private void refreshLocation() {
        mapView.setCenterPoint(curLocation.getLatitude(), curLocation.getLongitude(), true);
        mapView.setZoomLevel(17);

        curMarker.setTMapPoint(new TMapPoint(curLocation.getLatitude(), curLocation.getLongitude()));
    }

    private static String getContentFromNode(Element item, String tagName) {
        NodeList list = item.getElementsByTagName(tagName);
        if (list.getLength() > 0) {
            if (list.item(0).getFirstChild() != null) {
                return list.item(0).getFirstChild().getNodeValue();
            }
        }
        return null;
    }

    private void geoCoding(String destination) throws InterruptedException {
        String address = new String();
        listItems.clear();
        new TMapData().findAllPOI(destination, new TMapData.OnFindAllPOIListener() {
            @Override
            public void onFindAllPOI(ArrayList<TMapPOIItem> arrayList) {
                for (int i = 0; i < arrayList.size(); i++) {
                    // Log.d("findAllPOI_Result", arrayList.get(i).getPOIAddress() + ": " + arrayList.get(i).getPOIName() + ", " + arrayList.get(i).getPOIPoint().getLatitude() + ", " + arrayList.get(i).getPOIPoint().getLongitude());
                    double lat = arrayList.get(i).getPOIPoint().getLatitude();
                    double lon = arrayList.get(i).getPOIPoint().getLongitude();

                    /*new TMapData().convertGpsToAddress(lat, lon, new TMapData.OnConvertGPSToAddressListener() {
                        @Override
                        public void onConverGPSToAddress(String s) {
                            // Log.d("findAllPOI_Result", "GPS to address: " + s);
                        }
                    });*/

                    listItems.add(new DestinationsList.ListItem(arrayList.get(i).getPOIName(), reverseGeoCoding(lat, lon), new Pair<>(lat, lon)));
                }
                runOnUiThread(() -> listAdapter.notifyDataSetChanged());
            }
        });
    }

    public static void searchPath(double destinationX, double destinationY) throws
            URISyntaxException, IOException, JSONException {
        Log.d(TAG, "searchPath() called.");

        double endX = destinationY;
        double endY = destinationX;

        TMapPoint endPoint = new TMapPoint(endY, endX);

        double startX = curLocation.getLatitude();
        double startY = curLocation.getLongitude();
        TMapPoint startPoint = new TMapPoint(startX, startY);

        mapView.removeTMapPath();

        TMapData.TMapPathType type = TMapData.TMapPathType.PEDESTRIAN_PATH;
        new TMapData().findPathDataAllType(type, startPoint, endPoint, doc -> {
            mapView.removeTMapPath();

            mPolyLine = new TMapPolyLine();
            mPolyLine.setID(type.name());
            mPolyLine.setLineWidth(10);
            mPolyLine.setLineColor(Color.parseColor("#21D524"));
            mPolyLine.setLineAlpha(255);


            if (doc != null) {
                NodeList list = doc.getElementsByTagName("Document");
                Element item2 = (Element) list.item(0);
                String totalDistance = getContentFromNode(item2, "tmap:totalDistance");
                String totalTime = getContentFromNode(item2, "tmap:totalTime");
                String totalFare;
                if (type == TMapData.TMapPathType.CAR_PATH) {
                    totalFare = getContentFromNode(item2, "tmap:totalFare");
                } else {
                    totalFare = "";
                }

                NodeList list2 = doc.getElementsByTagName("LineString");

                for (int i = 0; i < list2.getLength(); i++) {
                    Element item = (Element) list2.item(i);
                    String str = getContentFromNode(item, "coordinates");
                    if (str == null) {
                        continue;
                    }

                    String[] str2 = str.split(" ");
                    for (int k = 0; k < str2.length; k++) {
                        try {
                            String[] str3 = str2[k].split(",");
                            TMapPoint point = new TMapPoint(Double.parseDouble(str3[1]), Double.parseDouble(str3[0]));
                            mPolyLine.addLinePoint(point);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                mapView.setTMapPath(mPolyLine);

                TMapInfo info = mapView.getDisplayTMapInfo(mPolyLine.getLinePointList());
                int zoom = info.getZoom();
                if (zoom > 12) {
                    zoom = 12;
                }

                mapView.setZoomLevel(zoom);
                mapView.setCenterPoint(info.getPoint().getLatitude(), info.getPoint().getLongitude());

                mapView.setTMapPath(mPolyLine);
            }
        });
    }

    private void curLocationInit() {
        Log.d(TAG, "curLocationInit() called.");

        // 현재 위치를 나타낼 위치 class인 TMapPoint의 객체 생성.
        curLocation = new TMapPoint(firstLatitude, firstLongitude);

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
        curMarker.setPosition(0.5f, 0.5f);
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

        refreshLocation();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Log.d(TAG, "onCreate() called.");

        mainHandler = new Handler(getMainLooper());
        threadPool = Executors.newCachedThreadPool();

        // UI 초기화 ↓
        slidePanel = findViewById(R.id.slidePanel);
        navigationView = findViewById(R.id.navigationBtm);
        mapView = new TMapView(this);
        mapView.setSKTMapApiKey(appKey); // API Key 할당.
        container = findViewById(R.id.mapView);
        container.addView(mapView);
        setActionBar();

        destinationTxt = findViewById(R.id.destinationText);
        destinationTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Log.d(TAG, "setOnFocusChangeListener() worked: " + b);
                slidePanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });

        searchBtn = findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(view -> {
            String address = destinationTxt.getText().toString();
            if (address.equals(""))
                Toast.makeText(getApplicationContext(), "목적지를 입력하세요.", Toast.LENGTH_SHORT).show();
            else {
                Runnable mSearchPath = () -> {
                    try {
                        geoCoding(address);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                };

                Log.d(TAG, "Start to search the query");
                threadPool.execute(mSearchPath);

                //InputMethodManager iMM = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                //iMM.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        getSupportFragmentManager().addOnBackStackChangedListener(this);
        destinationsFragment = new DestinationsList();
        getSupportFragmentManager().beginTransaction().add(R.id.candidates, destinationsFragment, "destinations").commit();

        // 위치 접근 권한 확인 ↓:
        checkPermission(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION);
    }

    private void setActionBar() {
        toolBar = findViewById(R.id.toolBar);
        setSupportActionBar(toolBar);
        toolBar.setTitle("Map");

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setTitle("Map");

        navigationView = findViewById(R.id.navigationBtm);
        navigationView.setSelectedItemId(MAP);
        navigationView.setOnItemSelectedListener(item -> {
            Log.d(TAG, "onOptionsItemSelected: " + item.getTitle());
            if (item.getTitle().equals("Post")) {
                Log.d(TAG + "Intent", "Convert to Post Activity.");
            } else if (item.getTitle().equals("Map")) {
                Log.d(TAG + "Intent", "Already in Map Activity.");
            } else if (item.getTitle().equals("Home")) {
                Intent toMainActivity = new Intent(getApplicationContext(), MainActivity.class);
                Log.d(TAG + "Intent", "Convert to Main Activity.");
                startActivity(toMainActivity);
            } else { // Menu
                Log.d(TAG + "Intent", "Convert to Menu Activity.");
            }
            return false;
        });

    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean checkPermission(String[] permissions, int type) {
        Log.d(TAG, "checkPermission() called.");
        SharedPreferences preference = getPreferences(MODE_PRIVATE);
        //if(preference.getBoolean("isFirstCheckPermission", true))
        //    return true;

        try {
            /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                return true;*/

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

        // 원인 파악 요망.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION);

        Location curLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        firstLatitude = curLocation.getLatitude();
        firstLongitude = curLocation.getLongitude();
        // Log.d(TAG, "provider meaning?: " +curLocation.getProvider());

        //reverseGeoCoding(firstLatitude, firstLongitude);
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
