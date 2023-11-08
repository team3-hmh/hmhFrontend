package kr.example.ttubuckttubuck.api;

import java.util.List;

import kr.example.ttubuckttubuck.dto.PlaceDto;
import kr.example.ttubuckttubuck.dto.PostingDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PostingApi {

    @GET("/member/posting")
    Call<List<PostingDto>> getAllPostings();

    @GET("/member/posting/{member}")
    Call<List<PostingDto>> getMemberPostings(@Path("member") Long member);

    @POST("/member/posting")
    @PUT("/member/posting")
    Call<PostingDto> savePosting(@Body PlaceDto placeDto);

    @DELETE("/member/posting")
    Call<PostingDto> delPosting(@Body PlaceDto placeDto);
}
