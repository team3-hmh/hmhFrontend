package kr.example.ttubuckttubuck.CustomView;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import kr.example.ttubuckttubuck.R;

public class AddUserItem extends LinearLayout {
    private TextView userName;
    private Button unfollowBtn;
    private boolean isFollowed = true;

    public AddUserItem(Context context) {
        super(context);
        initView();
    }

    public AddUserItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        getAttrs(attrs);
    }

    public AddUserItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        initView();
        getAttrs(attrs, defStyle);
    }

    private void initView() {
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater lI = (LayoutInflater) getContext().getSystemService(infService);
        View v = lI.inflate(R.layout.fragment_add_user, this, false);
        addView(v);

        this.userName = findViewById(R.id.userName);
        unfollowBtn = findViewById(R.id.unfollowBtn);

        if(isFollowed)
            this.setVisibility(View.VISIBLE);
        else
            this.setVisibility(View.GONE);
    }

    private void getAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.AddUserItem);
        setTypeArray(typedArray);
    }

    private void getAttrs(AttributeSet attrs, int defStyle) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.AddUserItem, defStyle, 0);
        setTypeArray(typedArray);
    }

    private void setTypeArray(TypedArray typedArray) {
        String text = "홍길동";
        userName.setText(text);

        typedArray.recycle();
    }

    public void setUserName(String name) {
        userName.setText(name);
    }
    public Button getUnfollowBtn(){ return unfollowBtn;}

    public void setIsFollowed(boolean followed){
        this.isFollowed = followed;
    }

    public boolean getIsFollowed(){
        return this.isFollowed;
    }

    public void hideView(){
        this.setVisibility(View.GONE);
    }
}
