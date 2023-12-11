package kr.example.ttubuckttubuck.CustomView;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import kr.example.ttubuckttubuck.R;

public class HomeTodoItem extends LinearLayout {
//    private ImageView userImg;
    private ImageView todoChk;
    private TextView title;
    private TextView date;

    public HomeTodoItem(Context context) {
        super(context);
        initView();
    }

    public HomeTodoItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        getAttrs(attrs);
    }

    public HomeTodoItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        initView();
        getAttrs(attrs, defStyle);
    }

    private void initView() {
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater lI = (LayoutInflater) getContext().getSystemService(infService);
        View v = lI.inflate(R.layout.fragment_todo, this, false);
        addView(v);

//        userImg = findViewById(R.id.userImg);
        // userName = findViewById(R.id.userName);
        // place = findViewById(R.id.place);
        title = findViewById(R.id.title);
        date = findViewById(R.id.date);
        todoChk = findViewById(R.id.todoChk);
    }

    private void getAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TodoItem);
        setTypeArray(typedArray);
    }

    private void getAttrs(AttributeSet attrs, int defStyle) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TodoItem, defStyle, 0);
        setTypeArray(typedArray);
    }

    private void setTypeArray(TypedArray typedArray) {
//        int img = typedArray.getResourceId(R.styleable.TodoItem_userImg, R.drawable.profile);
//        userImg.setImageResource(img);

        int img2 = typedArray.getResourceId(R.styleable.TodoItem_todoChk, R.drawable.todo_chk_btn);
        todoChk.setImageResource(img2);
        todoChk.setOnClickListener(view-> todoChk.setSelected(!(todoChk.isSelected())));

        //String text = typedArray.getString(R.styleable.AddUserBtn_userName);
        String text = "홍길동과 혜화 약속";
        title.setText(text);

        String text3 = "2023-11-11";
        date.setText(text3);

        typedArray.recycle();
    }

//    public void setUserImg(int img) {
//        userImg.setImageResource(img);
//    }

    public void setTitle(String text) {
        title.setText(text);
    }

    public void setDate(String text) {
        date.setText(text);
    }

    public ImageView getTodoChk(){
        return this.todoChk;
    }
}
