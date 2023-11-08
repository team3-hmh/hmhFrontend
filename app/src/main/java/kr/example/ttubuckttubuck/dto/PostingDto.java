package kr.example.ttubuckttubuck.dto;

import com.google.gson.annotations.SerializedName;

public class PostingDto {
    @SerializedName("id")
    private Long id;
    @SerializedName("member")
    private Long member;
    @SerializedName("place")
    private Long place;
    @SerializedName("content")
    private String content;
    @SerializedName("rating")
    private Long rating;
}
