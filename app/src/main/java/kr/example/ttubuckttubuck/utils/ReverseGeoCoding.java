package kr.example.ttubuckttubuck.utils;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class ReverseGeoCoding {
    private static final String baseUrl = "https://apis.openapi.sk.com/tmap/geo/reversegeocoding/";
    private static final String TAG = "ReverseGeoCoding_Debug";
    private Retrofit mRetroFit;
    private Endpoint mEndpoint;
    private Call<AddressInfo> callAddress;

    public ReverseGeoCoding(String appKey,int ver, double lat, double lon, String coordType, String addressType) {
        mRetroFit = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build();
        mEndpoint = mRetroFit.create(Endpoint.class);

        callAddress = mEndpoint.getData(appKey, ver, lat, lon, coordType, addressType);
        callAddress.enqueue(new Callback<>() {    //비동기로 실행되어 콜백으로 앱으로 알려줌
            //API Reponse 됐을 경우 호출 단, 404, 500 error에도 호출
            @Override
            public void onResponse(Call<AddressInfo> call, Response<AddressInfo> response) {
                Log.d(TAG, "response info: " + response);
                //응답이 성공적으로 됐을 경우
                if (response.isSuccessful()) {
                    //자바 객체로 변환된 JSON데이터 저장
                    AddressInfo result = response.body();
                    Log.d(TAG, "ver: " + result.getVer() + ", " + "lat: " + result.getLatitude() + ", " + "lon: " + result.getLongitude()+ ", " + "appKey: " + result.getAppKey());
                } else
                    Log.e(TAG, "Failed to get the response");
            }

            @Override
            public void onFailure(Call<AddressInfo> call, Throwable t) {
                Log.e(TAG, "Failed to get the response: " + t);
                t.printStackTrace();
            }
        });
    }

    // https://jaejong.tistory.com/38
    public interface Endpoint {
        //@GET("ver/lat/lon/coordType/addressType/callback/appKey")
        @GET(baseUrl)
        Call<AddressInfo> getData(
                @Header("appKey") String appKey,
                @Query("version") int ver,
                @Query("lat") double latitude,
                @Query("lon") double longitude,
                @Query("coordType") String coordType,
                @Query("addressType") String addressType);

        @FormUrlEncoded
        @POST("/posts")
        Call<AddressInfo> postData(@FieldMap HashMap<String, Object> params);
    }

    private class AddressInfo {
        @SerializedName("appKey")
        private String appKey;
        @SerializedName("version")
        private int ver;
        @SerializedName("lat")
        private double latitude;
        @SerializedName("lon")
        private double longitude;
        @SerializedName("coordType")
        private String coordType;
        @SerializedName("addressType")
        private String addressType;

        // appKey section
        public String getAppKey() {
            return appKey;
        }
        public void setAppKey(String appKey) {
            this.appKey = appKey;
        }

        // ver section
        public int getVer() {
            return ver;
        }

        public void setVer(int ver) {
            this.ver = ver;
        }

        // lat section
        public double getLatitude() {
            return latitude;
        }
        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        // long section
        public double getLongitude() {
            return longitude;
        }
        public void setLongitude(double longitude){
            this.longitude = longitude;
        }

        // coordType section
        public void setCoordType(String coordType) {
            this.coordType = coordType;
        }
        public String getCoordType() {
            return coordType;
        }

        // addressType section
        public void setAddressType(String addressType) {
            this.addressType = addressType;
        }
        public String getAddressType() {
            return addressType;
        }
    }
}
