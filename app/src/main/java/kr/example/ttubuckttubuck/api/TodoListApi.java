package kr.example.ttubuckttubuck.api;

import java.util.List;

import kr.example.ttubuckttubuck.dto.TodoListDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface TodoListApi {
    @PUT("/member/todoList/{id}")
    Call<TodoListDto> editTodoDone(@Path("id") Long id);

    @GET("/member/todoList/{member}")
    Call<List<TodoListDto>> getTodoList(@Path("member") Long member);

    @POST("/member/todoList")
    @PUT("/member/todoList")
    Call<TodoListDto> saveTodoList(@Body TodoListDto todoListDto);

    @DELETE("/member/todoList")
    Call<TodoListDto> delTodoList(@Body TodoListDto todoListDto);
}
