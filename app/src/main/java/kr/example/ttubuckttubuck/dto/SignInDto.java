package kr.example.ttubuckttubuck.dto;

import android.text.Editable;

import com.google.gson.annotations.SerializedName;

public class SignInDto {
    @SerializedName("email")
    private String email;
    @SerializedName("password")
    private String password;

    public SignInDto(Editable email, Editable password) {
        this.email = email.toString();
        this.password = password.toString();
    }
}
