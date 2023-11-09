package kr.example.ttubuckttubuck.utils;

import android.util.Log;

import com.google.gson.annotations.Expose;
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
                    Log.d(TAG, "addressType: " + result.getAddressType());
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
                @Query("addressType") String addressType); //WGS84GEO

        @FormUrlEncoded
        @POST("/posts")
        Call<AddressInfo> postData(@FieldMap HashMap<String, Object> params);
    }

    private class AddressInfo {
        @SerializedName("fullAddress")
        @Expose private String fullAddress;
        @SerializedName("addressType")
        @Expose private String addressType;
        @SerializedName("city_do")
        @Expose private String city_do;
        @SerializedName("gu_gun")
        @Expose private String gu_gun;
        @SerializedName("eup_myun")
        @Expose private String eup_myun;
        @SerializedName("adminDong")
        @Expose private String adminDong;
        @SerializedName("adminDongCode")
        @Expose private String adminDongCode;
        @SerializedName("legalDong")
        @Expose private String legalDong;
        @SerializedName("legalDongCode")
        @Expose private String legalDongCode;
        @SerializedName("ri")
        @Expose private String ri;
        @SerializedName("bunji")
        @Expose private String bunji;
        @SerializedName("roadName")
        @Expose private String roadName;
        @SerializedName("buildingIndex")
        @Expose private String buildingIndex;
        @SerializedName("buildingName")
        @Expose private String buildingName;
        @SerializedName("mappingDistance")
        @Expose private String mappingDistance;
        @SerializedName("roadCode")
        @Expose private String roadCode;

        // addressType section
        public void setAddressType(String addressType) {
            this.addressType = addressType;
        }
        public String getAddressType() {
            return addressType;
        }
    }
}
