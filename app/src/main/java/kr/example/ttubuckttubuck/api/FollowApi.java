package kr.example.ttubuckttubuck.api;

import java.util.List;

import kr.example.ttubuckttubuck.dto.FollowDto;
import kr.example.ttubuckttubuck.dto.MemberDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface FollowApi {

    @GET("/member/follow/{id}")
    Call<List<MemberDto>> getFollowingList(@Path("id") Long id);

    @POST("/member/follow")
    Call<MemberDto> follow(@Body FollowDto followDto);

    @HTTP(method = "DELETE", path="/member/follow", hasBody = true)
    Call<FollowDto> unfollow(@Body FollowDto followDto);

}
