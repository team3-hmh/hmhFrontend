package kr.example.ttubuckttubuck.dto;

import com.google.gson.annotations.SerializedName;

public class PlaceDto {
    @SerializedName("id")
    private Long id;
    @SerializedName("member")
    private Long member;
    @SerializedName("name")
    private String name;
    @SerializedName("address")
    private String address;
}
