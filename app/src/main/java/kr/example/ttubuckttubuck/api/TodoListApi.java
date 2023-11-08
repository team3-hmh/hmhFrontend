package kr.example.ttubuckttubuck.api;

import java.util.List;

import kr.example.ttubuckttubuck.dto.TodoListDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TodoListApi {
    @GET("/member/todoList/{member}")
    Call<List<TodoListDto>> getTodoList(@Path("member") Long member);
}
