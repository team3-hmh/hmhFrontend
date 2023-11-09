package kr.example.ttubuckttubuck;

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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.skt.tmap.TMapData;
import com.skt.tmap.TMapGpsManager;
import com.skt.tmap.TMapInfo;
import com.skt.tmap.TMapPoint;
import com.skt.tmap.TMapView;
import com.skt.tmap.address.TMapAddressInfo;
import com.skt.tmap.overlay.TMapMarkerItem;
import com.skt.tmap.overlay.TMapPolyLine;
import com.skt.tmap.poi.TMapPOIItem;
import com.skt.tmap.vsm.map.VSMNavigationView;

import org.json.JSONException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kr.example.ttubuckttubuck.utils.Locker;
import kr.example.ttubuckttubuck.utils.ReverseGeoCoding;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MapActivity extends AppCompatActivity implements TMapView.OnMapReadyListener, TMapGpsManager.OnLocationChangedListener, TMapView.OnApiKeyListenerCallback {
    // static(final) 변수 ↓
    private static final String TAG = "MapActivity_Debug";
    private static final String appKey = "rZWWy5hD2n87YkkTKDsV2ou4xLJHWpb5OiqBswXh";
    public static final int PERMISSION = 10000;
    private static final int ACCESS_GPS = 1;
    private static final boolean VERBOSE = true;

    // Thread 변수 ↓
    private Handler mainHandler;
    private TMapGpsManager gpsManager;
    private ExecutorService threadPool;
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
    private Button searchBtn;
    private EditText destination;
    private Bitmap markerBmp;
    private VSMNavigationView navigationView;

    // API 변수 ↓
    private double firstLatitude, firstLongitude;
    private String currentAddressAferReverseGedoCoding = null;
    private ReverseGeoCoding mReverseGeoCoder;
    private List<TMapPoint> mapPoints;
    private TMapPolyLine mPolyLine;

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
        final float curLatitude = (float) location.getLatitude();
        final float curLongitude = (float) location.getLongitude();
        //reverseGeoCoding(curLocation.getLatitude(), curLocation.getLongitude());
        curLocation.setLatitude(location.getLatitude());
        curLocation.setLongitude(location.getLongitude());

        Log.d(TAG + "_Callback", "Changed location: " + curLatitude + ", " + curLongitude);
        // Log.d(TAG + "_Callback", "Reverse GeoCoding address: " + currentAddressAferReverseGedoCoding);
        if (VERBOSE)
            Toast.makeText(this, "Changed location: " + curLatitude + ", " + curLongitude, Toast.LENGTH_SHORT).show();

        curMarker.setPosition(curLatitude, curLongitude);
        mapView.setCenterPoint(curLatitude, curLongitude, true);

        // reverseGeoCoding(curLatitude, curLongitude);
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

    void setNavigationView() {
        navigationView = new VSMNavigationView(this);
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

    private String getContentFromNode(Element item, String tagName) {
        NodeList list = item.getElementsByTagName(tagName);
        if (list.getLength() > 0) {
            if (list.item(0).getFirstChild() != null) {
                return list.item(0).getFirstChild().getNodeValue();
            }
        }
        return null;
    }

    /*private void requestFullAddress(String strAddr) {
        // 지번주소 정확도 순 Flag( 인덱스 작은 수록 정확도 높음 )
        final String[] arrOldMatchFlag = {"M11", "M21", "M12", "M13", "M22", "M23", "M41", "M42", "M31", "M32", "M33"};
        // 도로명주소 정확도 순 Flag( 인덱스 작은 수록 정확도 높음 )
        final String[] arrNewMatchFlag = {"N51", "N52", "N53", "N54", "N55", "N61", "N62"};
        SLHttpRequest request = new SLHttpRequest("https://api2.sktelecom.com/tmap/geo/fullAddrGeo"); // SKT
        request.addParameter("version", "1");
        request.addParameter("appKey", API_KEY);
        request.addParameter("coordType", "WGS84GEO");
        request.addParameter("addressFlag", "F00");
        request.addParameter("fullAddr", strAddr);
        request.send(new SLHttpRequest.OnResponseListener() {

            @Override
            public void OnSuccess(String data) {
                // TODO Auto-generated method stub

                FullAddrData fullAddrData = new FullAddrData();

                // JsonParsing
                try {
                    ArrayList<String> alMatchFlag = new ArrayList<String>(); // MatchFlag 수집
                    int indexMatchFlag = -1;
                    int i, j;

                    JSONObject objData = new JSONObject(data).getJSONObject("coordinateInfo");
                    int length = objData.getInt("totalCount");
                    JSONArray arrCoordinate = objData.getJSONArray("coordinate");
                    JSONObject objCoordinate = null;

                    // 1. matchFlag 수집
                    for (i = 0; i < length; i++) {
                        objCoordinate = arrCoordinate.getJSONObject(i);

                        if (objCoordinate.getString("matchFlag") != null && !objCoordinate.getString("matchFlag").equals("")) {
                            // 지번주소
                            alMatchFlag.add(objCoordinate.getString("matchFlag"));
                        } else if (objCoordinate.getString("newMatchFlag") != null && !objCoordinate.getString("newMatchFlag").equals("")) {
                            // 도로명주소
                            alMatchFlag.add(objCoordinate.getString("newMatchFlag"));
                        }
                    }

                    // 2. < matchFlag 기준으로 더 정확한 항목의 index 결정 >
                    // 2_1. 수집한 matchFlag 중 "M11"(지번주소중 가장 높은정확도) 이 있으면 선택
                    for (i = 0; i < alMatchFlag.size(); i++) {
                        if (alMatchFlag.get(i).equals("M11")) {
                            indexMatchFlag = i;
                            break;
                        }
                    }

                    // 2_2. "M11" 없으면 arrNewMatchFlag(도로명주소) 에서 선택
                    if (indexMatchFlag == -1) {
                        for (i = 0; i < arrNewMatchFlag.length; i++) {
                            for (j = 0; j < alMatchFlag.size(); j++) {
                                if (alMatchFlag.get(j).equals(arrNewMatchFlag[i])) {
                                    indexMatchFlag = j;
                                    break;
                                }
                            }
                            if (indexMatchFlag != -1) {
                                break;
                            }
                        }
                    }
                    // 2_3. 도로명주소 없으면 arrOldMatchFlag(지번주소) 에서 선택
                    if (indexMatchFlag == -1) {
                        for (i = 0; i < arrOldMatchFlag.length; i++) {
                            for (j = 0; j < alMatchFlag.size(); j++) {
                                if (alMatchFlag.get(j).equals(arrOldMatchFlag[i])) {
                                    indexMatchFlag = j;
                                    break;
                                }
                            }
                            if (indexMatchFlag != -1) {
                                break;
                            }
                        }
                    }

                    // 3. 선택된 인덱스의 결과 세팅
                    if (indexMatchFlag != -1) {
                        objCoordinate = arrCoordinate.getJSONObject(indexMatchFlag);
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
                            fullAddrData.addr = objCoordinate.getString("city_do") + " " + objCoordinate.getString("gu_gun") + " " + objCoordinate.getString("legalDong") + " " + objCoordinate.getString("bunji");
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
                            fullAddrData.addr = objCoordinate.getString("city_do") + " " + objCoordinate.getString("gu_gun") + " " + objCoordinate.getString("newRoadName") + " " + objCoordinate.getString("newBuildingIndex") + " " + objCoordinate.getString("newBuildingDong") + " (" + objCoordinate.getString("zipcode") + ")";
                            fullAddrData.flag = objCoordinate.getString("newMatchFlag");
                        }
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Log.d("debug", e.toString());
                }

                //listener.onComplete(fullAddrData);

                setFullTextGeoCoding(fullAddrData);
            }

            @Override
            public void OnFail(int errorCode, String errorMessage) {
                // TODO Auto-generated method stub
                Log.d("debug", "errorMessage :" + errorMessage);
                //listener.onComplete(null);
            }
        });
    }*/

    private void searchPath(String desitnation) throws URISyntaxException, IOException, JSONException {
        Log.d(TAG, "searchPath() called.");

        // double endX, double endY

        // Not worked. ----------------------------------------------------------------------------------------
        /*// final String urlString = "https://apis.skplanetx.com/tmap/routes/pedestrian?version=1&format=json&appKey=" + appKey;
        final String urlString = "https://apis.skplanetx.com/tmap/routes/pedestrian?version=1&startName=a&endName=b&startX=" + curLocation.getLongitude() + "&startY=" + curLocation.getLongitude() + "&endX=0.0&endY=0.0&appKey=" + appKey + "&format=json";
        URI uri = new URI(urlString);
        Log.d(TAG, "URI set.");

        HttpPost httpPost = new HttpPost(uri);
        // httpPost.setPath(urlString);
        Log.d(TAG, "HttpPost set.");
        // HttpClient httpClient = (HttpClient) new DefaultHttpClient(); // Error occurred.
        HttpClient httpClient = HttpClientBuilder.create().build();
        Log.d(TAG, "HttpClient set.");

        httpPost.setEntity(new UrlEncodedFormEntity(namePairs));
        Log.d(TAG, "HttpPost.setEntity() called.");

        OkHttpClient client = new OkHttpClient();
        ----------------------------------------------------------------------------------------*/

        mPolyLine = new TMapPolyLine();
        mPolyLine.setLineColor(Color.BLUE);
        mPolyLine.setLineWidth(10);
        mPolyLine.setLineColor(Color.GRAY);
        mPolyLine.setID("1");


        double endX= 126.92432158129688;
        double endY = 37.55279861528311;

        TMapPoint endPoint = new TMapPoint(endY, endX);

        double startY = curLocation.getLongitude();
        double startX = curLocation.getLatitude();
        TMapPoint startPoint = new TMapPoint(startX, startY);


        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"startX\":" + startX + ",\"startY\":" + startY + ",\"angle\":20,\"speed\":30,\"endPoiId\":\"10001\",\"endX\":" + endX + ",\"endY\":" + endY + ",\"passList\":\"126.92774822,37.55395475_126.92577620,37.55337145\",\"reqCoordType\":\"WGS84GEO\",\"startName\":\"%EC%B6%9C%EB%B0%9C\",\"endName\":\"%EB%8F%84%EC%B0%A9\",\"searchOption\":\"0\",\"resCoordType\":\"WGS84GEO\",\"sort\":\"index\"}");
        Request request = new Request.Builder()
                .url("https://apis.openapi.sk.com/tmap/routes/pedestrian?version=1&callback=function")
                .post(body)
                .addHeader("accept", "application/json")
                .addHeader("content-type", "application/json")
                .addHeader("appKey", appKey)
                .build();
        Response response = null;

        OkHttpClient client = new OkHttpClient();
        try {
            response = client.newCall(request).execute();
            Log.d(TAG, "Getting the response body.");
        } catch (Exception e) {
            Log.e(TAG, "Failed to get the response.");
            e.printStackTrace();

        }
        String responseString = response.body().string();
        Log.d(TAG, "[ Response ] message: " + responseString);
        Log.d(TAG, "[ Response ] isSuccessful(): " + response.isSuccessful());

        mapView.removeTMapPath();

        TMapData.TMapPathType type = TMapData.TMapPathType.PEDESTRIAN_PATH;
        new TMapData().findPathDataAllType(type, startPoint, endPoint, new TMapData.OnFindPathDataAllTypeListener() {
            @Override
            public void onFindPathDataAllType(Document doc) {
                mapView.removeTMapPath();

                TMapPolyLine polyline = new TMapPolyLine();
                polyline.setID(type.name());
                polyline.setLineWidth(10);
                polyline.setLineColor(Color.RED);
                polyline.setLineAlpha(255);


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
                                polyline.addLinePoint(point);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    mapView.setTMapPath(polyline);

                    TMapInfo info = mapView.getDisplayTMapInfo(polyline.getLinePointList());
                    int zoom = info.getZoom();
                    if (zoom > 12) {
                        zoom = 12;
                    }

                    mapView.setZoomLevel(zoom);
                    mapView.setCenterPoint(info.getPoint().getLatitude(), info.getPoint().getLongitude());
                }
            }
        });

        mapView.setTMapPath(mPolyLine);
    }

    private void curLocationInit() {
        Log.d(TAG, "curLocationInit() called.");
        mapView.setCompassModeFix(true);


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
        //reverseGeoCoding(curLocation.getLatitude(), curLocation.getLongitude());
        searchAround();
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

        destination = findViewById(R.id.destinationText);
        /*try {
            locker.lock();
        } catch (InterruptedException e) {
            Log.w(TAG, "lock() failed.");
            e.printStackTrace();
        }*/

        container = findViewById(R.id.mapView);
        container.addView(mapView);

        searchBtn = findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(view->{
            String address = destination.getText().toString();
            if(address.equals(""))
                Toast.makeText(getApplicationContext(), "목적지를 입력하세요.", Toast.LENGTH_SHORT).show();
            else {
                Runnable mSearchPath = () -> {
                    try {
                        searchPath(address);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
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
