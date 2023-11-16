package kr.example.ttubuckttubuck.api;

import kr.example.ttubuckttubuck.dto.MemberDto;
import kr.example.ttubuckttubuck.dto.SignInDto;
import kr.example.ttubuckttubuck.dto.SignUpDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MemberApi {

    @POST("/member/join")
    Call<Long> join(@Body SignUpDto signUpDto);

    @POST("/member/login")
    Call<String> login(@Body SignInDto signInDto);

    @POST("/member/insertImage/{id}")
    Call<MemberDto> insertImage(@Path("id") Long id, @Body MemberDto memberDto);

    @GET("/member/{id}")
    Call<MemberDto> memberInfo(@Path("id") Long id);

    @POST("/member/email")
    Call<Long> findIdByEmail(@Body SignInDto signInDto);
}
