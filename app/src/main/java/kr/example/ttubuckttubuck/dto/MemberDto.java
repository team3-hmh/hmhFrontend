package kr.example.ttubuckttubuck.dto;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

public class MemberDto {
    private static final String TAG = "MemberDto_Debug";
    @SerializedName("id")
    private Long id;
    @SerializedName("email")
    private String email;
    @SerializedName("name")
    private String name;
    @SerializedName("birth")
    private String birth;
    @SerializedName("password")
    private String password;
    @SerializedName("image")
    private String image;

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public String getImage() {
        Log.d(TAG, "getImage() called: " + this.image + ", by: " + this.id);
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
        Log.d(TAG, "setImage() called: " + this.image + ", by: " + this.id);
    }
}
