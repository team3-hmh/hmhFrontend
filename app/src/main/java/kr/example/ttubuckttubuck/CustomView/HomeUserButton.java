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

public class HomeUserButton extends LinearLayout {
    private ImageView userImg;
    private TextView userName;

    public HomeUserButton(Context context) {
        super(context);
        initView();
    }

    public HomeUserButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        getAttrs(attrs);
    }

    public HomeUserButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        initView();
        getAttrs(attrs, defStyle);
    }

    private void initView() {
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater lI = (LayoutInflater) getContext().getSystemService(infService);
        View v = lI.inflate(R.layout.fragment_user, this, false);
        addView(v);

        userName = findViewById(R.id.userName);
        userImg = findViewById(R.id.userImg);
    }

    private void getAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.AddUserBtn);
        setTypeArray(typedArray);
    }

    private void getAttrs(AttributeSet attrs, int defStyle) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.AddUserBtn, defStyle, 0);
        setTypeArray(typedArray);
    }

    private void setTypeArray(TypedArray typedArray) {
        int img = typedArray.getResourceId(R.styleable.AddUserBtn_userImg, R.drawable.profile);
        userImg.setImageResource(img);

        //String text = typedArray.getString(R.styleable.AddUserBtn_userName);
        String text = "김 호";
        userName.setText(text);

        typedArray.recycle();
    }

    void setUserImg(int img) {
        userImg.setImageResource(img);
    }


    void setUserName(String text) {
        userName.setText(text);
    }
}
