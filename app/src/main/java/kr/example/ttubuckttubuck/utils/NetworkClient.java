package kr.example.ttubuckttubuck.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkClient {

    public static Retrofit retrofit;

    public static Retrofit getRetrofitClient(Context context) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        if(retrofit == null) {
            // TODO : 데이터 통신의 로그를 Logcat에서 확인할 수 있다.
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

            OkHttpClient httpClient = new OkHttpClient.Builder().connectTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(1, TimeUnit.MINUTES)
                    .writeTimeout(1, TimeUnit.MINUTES)
                    .addInterceptor(loggingInterceptor)
                    .build();

            retrofit = new Retrofit.Builder().baseUrl("http://3.35.16.248:8080/")
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson)) // 데이터를 주고 받을 때 모델에 만든 클래스로 사용
                    .build();
        }
        return retrofit;
    }
}