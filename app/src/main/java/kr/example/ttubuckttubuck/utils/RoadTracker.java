/*
package kr.example.ttubuckttubuck.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class RoadTracker {
    private static final String TAG = "RoadTracker";
    private GoogleMap mMap;
    private GeoApiContext mContext;
    private ArrayList<LatLng> mCapturedLocations = new ArrayList<LatLng>();//지나간 좌표 들을 저장하는 List
    private static final int PAGINATION_OVERLAP = 5;
    private static final int PAGE_SIZE_LIMIT = 100;
    private ArrayList<com.google.android.gms.maps.model.LatLng> mapPoints;

    int totalDistance;

    public RoadTracker(GoogleMap map) {
        mMap = map;
    }

    //public void drawCorrentPath(ArrayList<LatLng> checkedLocations){
//getJsonData().get();
//}

    public ArrayList<com.google.android.gms.maps.model.LatLng> getJsonData(final LatLng startPoint, final LatLng endPoint) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                HttpClient httpClient = new DefaultHttpClient();

                String urlString = "https://apis.skplanetx.com/tmap/routes/pedestrian?version=1&format=json&appKey=본인의 API 키";
                try {
                    URI uri = new URI(urlString);

                    HttpPost httpPost = new HttpPost();
                    httpPost.setURI(uri);

                    List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("startX", Double.toString(startPoint.lng)));
                    nameValuePairs.add(new BasicNameValuePair("startY", Double.toString(startPoint.lat)));

                    nameValuePairs.add(new BasicNameValuePair("endX", Double.toString(endPoint.lng)));
                    nameValuePairs.add(new BasicNameValuePair("endY", Double.toString(endPoint.lat)));

                    nameValuePairs.add(new BasicNameValuePair("startName", "출발지"));
                    nameValuePairs.add(new BasicNameValuePair("endName", "도착지"));

                    nameValuePairs.add(new BasicNameValuePair("reqCoordType", "WGS84GEO"));
                    nameValuePairs.add(new BasicNameValuePair("resCoordType", "WGS84GEO"));

                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse response = httpClient.execute(httpPost);

                    int code = response.getStatusLine().getStatusCode();
                    String message = response.getStatusLine().getReasonPhrase();
                    Log.d(TAG, "run: " + message);
                    String responseString;
                    if (response.getEntity() != null)
                        responseString = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
                    else
                        return;
                    String strData = "";

                    Log.d(TAG, "0\n");
                    JSONObject jAr = new JSONObject(responseString);

                    Log.d(TAG, "1\n");

                    JSONArray features = jAr.getJSONArray("features");
                    mapPoints = new ArrayList<>();


                    for (int i = 0; i < features.length(); i++) {
                        JSONObject test2 = features.getJSONObject(i);
                        if (i == 0) {
                            JSONObject properties = test2.getJSONObject("properties");
                            totalDistance += properties.getInt("totalDistance");
                        }
                        JSONObject geometry = test2.getJSONObject("geometry");
                        JSONArray coordinates = geometry.getJSONArray("coordinates");


                        String geoType = geometry.getString("type");
                        if (geoType.equals("Point")) {
                            double lonJson = coordinates.getDouble(0);
                            double latJson = coordinates.getDouble(1);

                            Log.d(TAG, "-");
                            Log.d(TAG, lonJson + "," + latJson + "\n");
                            com.google.android.gms.maps.model.LatLng point = new com.google.android.gms.maps.model.LatLng(latJson, lonJson);
                            mapPoints.add(point);

                        }
                        if (geoType.equals("LineString")) {
                            for (int j = 0; j < coordinates.length(); j++) {
                                JSONArray JLinePoint = coordinates.getJSONArray(j);
                                double lonJson = JLinePoint.getDouble(0);
                                double latJson = JLinePoint.getDouble(1);

                                Log.d(TAG, "-");
                                Log.d(TAG, lonJson + "," + latJson + "\n");
                                com.google.android.gms.maps.model.LatLng point = new com.google.android.gms.maps.model.LatLng(latJson, lonJson);

                                mapPoints.add(point);

                            }
                        }
                    }

                } catch (URISyntaxException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    ;
        thread.start();

        try

    {
        thread.join();
    }catch(
    InterruptedException e)

    {
        e.printStackTrace();
    }
        return mapPoints;
}
        }
                출처:https://jinseongsoft.tistory.com/34 [진성 소프트:티스토리]*/
