package kr.example.ttubuckttubuck.api;

import java.util.List;

import kr.example.ttubuckttubuck.dto.PlaceDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PlaceApi {

    @GET("/member/{member}")
    Call<List<PlaceDto>> getPlaceList(@Path("member") Long member);

    @POST("/member/place")
    Call<PlaceDto> savePlace(@Body PlaceDto placeDto);

    @DELETE("/member/place")
    Call<PlaceDto> delPlace(@Body PlaceDto placeDto);
}
