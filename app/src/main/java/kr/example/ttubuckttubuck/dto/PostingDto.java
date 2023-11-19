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

    public PostingDto(Long member, Long place, String content, Long rating) {
        this.member = member;
        this.place = place;
        this.content = content;
        this.rating = rating;
    }

    public String getContent() {
        return this.content;
    }


    public String getRating() {
        return String.valueOf(this.rating);
    }
}
