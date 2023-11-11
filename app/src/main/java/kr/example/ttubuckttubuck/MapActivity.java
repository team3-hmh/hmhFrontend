package kr.example.ttubuckttubuck;

import static kr.example.ttubuckttubuck.DestinationsList.listAdapter;
import static kr.example.ttubuckttubuck.DestinationsList.listItems;

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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.skt.tmap.TMapData;
import com.skt.tmap.TMapGpsManager;
import com.skt.tmap.TMapInfo;
import com.skt.tmap.TMapPoint;
import com.skt.tmap.TMapView;
import com.skt.tmap.address.TMapAddressInfo;
import com.skt.tmap.overlay.TMapMarkerItem;
import com.skt.tmap.overlay.TMapPolyLine;
import com.skt.tmap.poi.TMapPOIItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kr.example.ttubuckttubuck.utils.Locker;
import kr.example.ttubuckttubuck.utils.ReverseGeoCoding;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener, TMapView.OnMapReadyListener, TMapGpsManager.OnLocationChangedListener, TMapView.OnApiKeyListenerCallback {
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

    @Override
    public void onBackStackChanged() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(getSupportFragmentManager().getBackStackEntryCount() > 0);
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
    private static TMapView mapView;
    private static TMapPoint curLocation;
    private TMapMarkerItem curMarker;
    private ImageButton mainBtn, reloadBtn;
    private Button searchBtn;
    private EditText destination;
    private Bitmap markerBmp;
    private Fragment destinationsFragment;

    // API 변수 ↓
    private double firstLatitude, firstLongitude;
    private String currentAddressAferReverseGedoCoding = null;
    private ReverseGeoCoding mReverseGeoCoder;
    private List<TMapPoint> mapPoints;
    private static TMapPolyLine mPolyLine;

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
        final float curLatitude = (float) location.getLatitude();
        final float curLongitude = (float) location.getLongitude();
        //reverseGeoCoding(curLocation.getLatitude(), curLocation.getLongitude());
        curLocation.setLatitude(location.getLatitude());
        curLocation.setLongitude(location.getLongitude());

        Log.d(TAG + "_Callback", "Changed location: " + curLatitude + ", " + curLongitude);
        // Log.d(TAG + "_Callback", "Reverse GeoCoding address: " + currentAddressAferReverseGedoCoding);
        if (VERBOSE)
            Toast.makeText(this, "Changed location: " + curLatitude + ", " + curLongitude, Toast.LENGTH_SHORT).show();

        curMarker.setTMapPoint(new TMapPoint(curLatitude, curLongitude));
        mapView.setCenterPoint(curLatitude, curLongitude, true);
    }

    private void reverseGeoCoding(double latitude, double longitude) {
        //double editLatitude = Double.valueOf(String.format("%.6f", latitude));
        //double editLongitude = Double.valueOf(String.format("%.6f", longitude));

        TMapAddressInfo result = new TMapData().reverseGeocoding(curLocation.getLatitude(), curLocation.getLongitude(), null);

        currentAddressAferReverseGedoCoding = new TMapData().convertGpsToAddress(curLocation.getLatitude(), curLocation.getLatitude());
        Log.d(TAG, "GeoCoding result: " + result + " or " + currentAddressAferReverseGedoCoding);

        Log.d(TAG, "GeoCoder called.");
        mReverseGeoCoder = new ReverseGeoCoding(appKey, 1, latitude, longitude, "EPSG3857", "null");
    }

    // Success to work.
    private void searchAround() {
        TMapData mTMapData = new TMapData();
        TMapPoint tmp = curLocation;
        mTMapData.findAroundNamePOI(tmp, "편의점", 1, 1000, arrayList -> {
            for (int i = 0; i < arrayList.size(); i++) {
                TMapPOIItem item = arrayList.get(i);
                Log.d(TAG, "POI name: " + item.getPOIName() + ", address: " + item.getPOIAddress().replace("mull", ""));
            }
        });
    }

    private void refreshLocation() {
        mapView.setCenterPoint(curLocation.getLatitude(), curLocation.getLongitude());
        mapView.setZoomLevel(17);
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

    class FullAddrData {
        public double lat, lon, latEntr, lonEntr;
        public String addr, flag, buildingName;
    }

    private void requestFullAddress(String destination) throws IOException {
        // 지번주소 정확도 순 Flag( 인덱스 작은 수록 정확도 높음 )
        final String[] arrOldMatchFlag = {"M11", "M21", "M12", "M13", "M22", "M23", "M41", "M42", "M31", "M32", "M33"};
        // 도로명주소 정확도 순 Flag( 인덱스 작은 수록 정확도 높음 )
        final String[] arrNewMatchFlag = {"N51", "N52", "N53", "N54", "N55", "N61", "N62"};

        // https://skopenapi.readme.io/reference/full-text-geocoding
        /*
        <주소 구분 코드>
        F01
        - 지번주소
        - 구주소 타입

        F02
        - 새(도로명) 주소
        - 새(도로명) 주소 타입 F00
        - 구주소, 새(도로명)주소 타입

        <좌표 타입>
        EPSG3857
        - Google Mercator

        WGS84GEO
        - 경위도
        */

        String rawString = destination;
        // String rawString = "서울시 강남구 신사동";
        // ByteBuffer buffer = StandardCharsets.UTF_8.encode(rawString);
        // String encodedString = StandardCharsets.UTF_8.decode(buffer).toString();
        String encodedString = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU)
            encodedString = URLEncoder.encode(rawString, StandardCharsets.UTF_8);

        Log.d("encodedString", "encodedString: " + encodedString);
        // true ↓
        // Log.d(TAG, "assert: " + encodedString.equals("%EC%84%9C%EC%9A%B8%EC%8B%9C+%EA%B0%95%EB%82%A8%EA%B5%AC+%EC%8B%A0%EC%82%AC%EB%8F%99"));

        OkHttpClient client = new OkHttpClient();
        int cnt = 200;
        Request request = new Request.Builder()
                .url("https://apis.openapi.sk.com/tmap/geo/fullAddrGeo?addressFlag=F01&coordType=WGS84GEO&version=1&fullAddr=" + encodedString + "&page=1&count=" + cnt)
                .get()
                .addHeader("accept", "application/json")
                .addHeader("appKey", appKey)
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            Log.d(TAG, "Getting the response body.");
        } catch (Exception e) {
            Log.e(TAG, "Failed to get the response.");
            e.printStackTrace();
        }
        String responseString = response.body().string();
        Log.d(TAG, "[ requestFullAddress ] message: " + responseString);
        Log.d(TAG, "[ requestFullAddress ] isSuccessful(): " + response.isSuccessful());

        if (!(response.isSuccessful())) {
            runOnUiThread(() -> Toast.makeText(getApplicationContext(), "해당 주소 정보가 존재하지 않습니다.", Toast.LENGTH_SHORT).show());
        }

        FullAddrData fullAddrData = new FullAddrData();

        // JsonParsing
        try {
            ArrayList<String> alMatchFlag = new ArrayList<>(); // MatchFlag 수집
            int indexMatchFlag = -1;
            int i, j;

            JSONObject objData = new JSONObject(responseString).getJSONObject("coordinateInfo");
            int length = objData.getInt("totalCount") < cnt ? objData.getInt("totalCount") : cnt;
            JSONArray arrCoordinate = objData.getJSONArray("coordinate");
            JSONObject objCoordinate = null;

            ArrayList<JSONObject> destinations = new ArrayList<>();
            ArrayList<JSONObject> destinationsNew = new ArrayList<>();
            ArrayList<JSONObject> results = new ArrayList<>();

            // 1. matchFlag 수집

            for (i = 0; i < length; i++) {
                objCoordinate = arrCoordinate.getJSONObject(i);

                if (objCoordinate.getString("matchFlag") != null && !objCoordinate.getString("matchFlag").equals("")) {
                    // 지번주소
                    alMatchFlag.add(objCoordinate.getString("matchFlag"));
                    destinations.add(objCoordinate);
                } else if (objCoordinate.getString("newMatchFlag") != null && !objCoordinate.getString("newMatchFlag").equals("")) {
                    // 도로명주소
                    alMatchFlag.add(objCoordinate.getString("newMatchFlag"));
                    destinationsNew.add(objCoordinate);
                }
            }

            Log.d(TAG, "alMatchFlag: " + alMatchFlag.size());
            Log.d(TAG, "destinations: " + destinations.size());
            Log.d(TAG, "destinationsNew: " + destinationsNew.size());

            // 2. < matchFlag 기준으로 더 정확한 항목의 index 결정 >
            // 2_1. 수집한 matchFlag 중 "M00"(지번주소중 가장 높은정확도) 이 있으면 선택
            for (i = 0; i < destinations.size(); i++) {
                if (destinations.get(i).getString("matchFlag").equals("M00")) {
                    Log.d(TAG, "M00: " + destinations.get(i).getString("gu_gun"));
                    indexMatchFlag = i;
                    results.add(destinations.get(i));
                }
            }

            Log.d(TAG, "indexMatchFlag: " + indexMatchFlag);

            // 2_2. "M00" 없으면 arrNewMatchFlag(도로명주소) 에서 선택
            if (indexMatchFlag == -1) {
                for (i = 0; i < arrNewMatchFlag.length; i++) {
                    for (j = 0; j < destinationsNew.size(); j++) {
                        if (destinationsNew.get(j).getString("matchFlag").equals(arrNewMatchFlag[i])) {
                            indexMatchFlag = j;
                            results.add(destinations.get(j));
                            //break;
                        }
                    }
                    /*if (indexMatchFlag != -1) {
                        break;
                    }*/
                }

            }

            // 2_3. 도로명주소 없으면 arrOldMatchFlag(지번주소) 에서 선택
            if (indexMatchFlag == -1) {
                for (i = 0; i < arrOldMatchFlag.length; i++) {
                    for (j = 0; j < destinations.size(); j++) {
                        if (destinations.get(j).getString("matchFlag").equals(arrOldMatchFlag[i])) {
                            indexMatchFlag = j;
                            results.add(destinations.get(j));
                            //break;
                        }
                    }
                    /*if (indexMatchFlag != -1) {
                        break;
                    }*/
                }
            }

            Log.d(TAG, "results: " + results.size());
            listItems.clear();
            for (i = 0; i < results.size(); i++) {
                if (VERBOSE) Log.d(TAG, "i: " + i);
                // 3. 선택된 인덱스의 결과 세팅
                if (indexMatchFlag != -1) {
                    objCoordinate = arrCoordinate.getJSONObject(i);
                    if (!objCoordinate.getString("matchFlag").equals("")) {
                        // 지번 주소
                        if (!objCoordinate.getString("lat").equals(""))
                            fullAddrData.lat = Double.parseDouble(objCoordinate.getString("lat"));
                        if (!objCoordinate.getString("lon").equals(""))
                            fullAddrData.lon = Double.parseDouble(objCoordinate.getString("lon"));
                        if (!objCoordinate.getString("latEntr").equals(""))
                            fullAddrData.latEntr = Double.parseDouble(objCoordinate.getString("latEntr"));
                        if (!objCoordinate.getString("lonEntr").equals(""))
                            fullAddrData.lonEntr = Double.parseDouble(objCoordinate.getString("lonEntr"));
                        if (!objCoordinate.getString("buildingName").equals(""))
                            fullAddrData.buildingName = objCoordinate.getString("buildingName") + " ";
                        if (!objCoordinate.getString("buildingDong").equals(""))
                            fullAddrData.buildingName += objCoordinate.getString("buildingDong");
                        fullAddrData.addr = objCoordinate.getString("city_do") + " " + objCoordinate.getString("gu_gun") + " " + objCoordinate.getString("legalDong") + " " + objCoordinate.getString("adminDong") + " " + objCoordinate.getString("bunji");
                        fullAddrData.flag = objCoordinate.getString("matchFlag");
                    } else if (!objCoordinate.getString("newMatchFlag").equals("")) {
                        // 도로명 주소
                        if (!objCoordinate.getString("newLat").equals(""))
                            fullAddrData.lat = Double.parseDouble(objCoordinate.getString("newLat"));
                        if (!objCoordinate.getString("newLon").equals(""))
                            fullAddrData.lon = Double.parseDouble(objCoordinate.getString("newLon"));
                        if (!objCoordinate.getString("newLatEntr").equals(""))
                            fullAddrData.latEntr = Double.parseDouble(objCoordinate.getString("newLatEntr"));
                        if (!objCoordinate.getString("newLonEntr").equals(""))
                            fullAddrData.lonEntr = Double.parseDouble(objCoordinate.getString("newLonEntr"));
                        if (!objCoordinate.getString("buildingName").equals(""))
                            fullAddrData.buildingName = objCoordinate.getString("buildingName") + " ";
                        if (!objCoordinate.getString("buildingDong").equals(""))
                            fullAddrData.buildingName += objCoordinate.getString("buildingDong");
                        fullAddrData.addr = objCoordinate.getString("city_do") + " " + objCoordinate.getString("gu_gun") + " " + objCoordinate.getString("newRoadName") + " " + objCoordinate.getString("newBuildingIndex") + " " + objCoordinate.getString("newBuildingDong") + " (" + objCoordinate.getString("zipcode") + ")";
                        fullAddrData.flag = objCoordinate.getString("newMatchFlag");
                    }

                    Log.d(TAG, "GeoCoding: " + fullAddrData.buildingName + ": " + fullAddrData.addr + ": " + fullAddrData.lat + ", " + fullAddrData.lon);
                    listItems.add(new DestinationsList.ListItem(fullAddrData.buildingName, fullAddrData.addr, new Pair<>(fullAddrData.lat, fullAddrData.lon)));

                }
            }
            runOnUiThread(() -> listAdapter.notifyDataSetChanged());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(TAG, "Failed to get the body: " + e);
            e.printStackTrace();
        }
    }

    public static void searchPath(double destinationX, double destinationY) throws URISyntaxException, IOException, JSONException {
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
            mPolyLine.setLineColor(Color.GREEN);
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
        // reverseGeoCoding(curLocation.getLatitude(), curLocation.getLongitude());
        // searchAround();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Log.d(TAG, "onCreate() called.");

        mainHandler = new Handler(getMainLooper());
        threadPool = Executors.newCachedThreadPool();

        // UI 초기화 ↓
        mapView = new TMapView(this);
        mapView.setSKTMapApiKey(appKey); // API Key 할당.
        container = findViewById(R.id.mapView);
        container.addView(mapView);

        destination = findViewById(R.id.destinationText);

        searchBtn = findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(view -> {
            String address = destination.getText().toString();
            if (address.equals(""))
                Toast.makeText(getApplicationContext(), "목적지를 입력하세요.", Toast.LENGTH_SHORT).show();
            else {
                Runnable mSearchPath = () -> {
                    try {
                        requestFullAddress(address);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                };

                Log.d(TAG, "Start to search the query");
                threadPool.execute(mSearchPath);
            }
        });

        reloadBtn = findViewById(R.id.refreshBtn);
        reloadBtn.setOnClickListener(view -> {
            refreshLocation();
            if (VERBOSE)
                Toast.makeText(this, "Refresh", Toast.LENGTH_SHORT).show();
        });

        mainBtn = findViewById(R.id.goBackBtn);
        mainBtn.setOnClickListener(view -> {
            Intent toMainActivity = new Intent(getApplicationContext(), MainActivity.class);
            Log.d(TAG + "Intent", "Convert to Main Activity.");
            startActivity(toMainActivity);
        });

        getSupportFragmentManager().addOnBackStackChangedListener(this);
        destinationsFragment = new DestinationsList();
        getSupportFragmentManager().beginTransaction().add(R.id.candidates, destinationsFragment, "destinations").commit();

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
