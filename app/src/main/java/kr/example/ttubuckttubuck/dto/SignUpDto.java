package kr.example.ttubuckttubuck.dto;

import android.text.Editable;

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

    public SignUpDto(Editable email, Editable password, Editable checkedPassword, Editable name) {
        this.email = email.toString();
        this.password = password.toString();
        this.checkedPassword = checkedPassword.toString();
        this.name = name.toString();
    }
}
