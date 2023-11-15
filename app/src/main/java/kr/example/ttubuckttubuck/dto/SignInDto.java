package kr.example.ttubuckttubuck.dto;

import com.google.gson.annotations.SerializedName;

public class SignInDto {
    @SerializedName("email")
    private String email;
    @SerializedName("password")
    private String password;

    public SignInDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
