package kr.example.ttubuckttubuck.dto;

import com.google.gson.annotations.SerializedName;

public class MemberDto {
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
        return this.image;
    }
}
