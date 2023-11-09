package kr.example.ttubuckttubuck.dto;

import com.google.gson.annotations.SerializedName;

public class SignUpDto {
    @SerializedName("email")
    private String email;
    @SerializedName("password")
    private String password;
    @SerializedName("name")
    private String name;
    @SerializedName("checkedPassword")
    private String checkedPassword;
}
