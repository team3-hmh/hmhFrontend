package kr.example.ttubuckttubuck.dto;

import com.google.gson.annotations.SerializedName;

public class TodoListDto {

    @SerializedName("id")
    private Long id;
    @SerializedName("member")
    private Long member;
    @SerializedName("place")
    private Long place;
    @SerializedName("content")
    private String content;
    @SerializedName("date")
    private String date;
    @SerializedName("done")
    private Boolean done;

    public TodoListDto(Long member, Long place, String content, String date){
        this.member = member;
        this.place = place;
        this.content = content;
        this.date = date;
        this.done = false;
    }

    public String getContent() { return this.content; }

    public String getDate() {
        return this.date;
    }

    public Long getId() {
        return this.id;
    }

    public boolean getDone() {
        return this.done;
    }
}
