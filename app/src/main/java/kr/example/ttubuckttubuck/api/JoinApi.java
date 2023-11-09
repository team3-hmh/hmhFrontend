package kr.example.ttubuckttubuck.api;

import kr.example.ttubuckttubuck.dto.SignInDto;
import kr.example.ttubuckttubuck.dto.SignUpDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface JoinApi {

    @POST("/member/join")
    Call<Long> join(@Body SignInDto signInDto);

    @POST("/member/login")
    Call<String> login(@Body SignUpDto signUpDto);
}
