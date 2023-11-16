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

public class PostItem extends LinearLayout {
    private ImageView postImg;
    private TextView postTitle, postContent, date;

    public PostItem(Context context) {
        super(context);
        initView();
    }

    public PostItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        getAttrs(attrs);
    }

    public PostItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        initView();
        getAttrs(attrs, defStyle);
    }

    private void initView() {
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater lI = (LayoutInflater) getContext().getSystemService(infService);
        View v = lI.inflate(R.layout.fragment_post, this, false);
        addView(v);

        postTitle = findViewById(R.id.postTitle);
        postImg = findViewById(R.id.postImg);
        postContent = findViewById(R.id.postContent);
        date = findViewById(R.id.date);
    }

    private void getAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PostItem);
        setTypeArray(typedArray);
    }

    private void getAttrs(AttributeSet attrs, int defStyle) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PostItem, defStyle, 0);
        setTypeArray(typedArray);
    }

    private void setTypeArray(TypedArray typedArray) {
        int img = typedArray.getResourceId(R.styleable.PostItem_postImg, R.drawable.kmu);
        postImg.setImageResource(img);

        //String text = typedArray.getString(R.styleable.AddUserBtn_userName);
        String text = "성북구 국민대학교 북악관 207호";
        postTitle.setText(text);


        text = "삶과윤리 수업이 아주 좋습니다. 수신지가 치국평천하라는 내용이 기억에 남습니다.";
        postContent.setText(text);


        text = "2023-11-15";
        date.setText(text);

        typedArray.recycle();
    }

    public void setPostImg(int img) {
        postImg.setImageResource(img);
    }


    public void setPostTitle(String text) {
        postTitle.setText(text);
    }

    public void setPostContent(String text){
        postContent.setText(text);
    }

    public void setDate(String text){
        date.setText(text);
    }
}
