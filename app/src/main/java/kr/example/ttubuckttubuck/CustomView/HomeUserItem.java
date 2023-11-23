package kr.example.ttubuckttubuck.CustomView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import kr.example.ttubuckttubuck.R;

public class HomeUserItem extends LinearLayout {
    private ImageView userImg;
    private TextView userName;

    public HomeUserItem(Context context) {
        super(context);
        initView();
    }

    public HomeUserItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        getAttrs(attrs);
    }

    public HomeUserItem(Context context, AttributeSet attrs, int defStyle) {
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
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.UserItem);
        setTypeArray(typedArray);
    }

    private void getAttrs(AttributeSet attrs, int defStyle) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.UserItem, defStyle, 0);
        setTypeArray(typedArray);
    }

    private void setTypeArray(TypedArray typedArray) {
        int img = typedArray.getResourceId(R.styleable.UserItem_userImg, R.drawable.profile);
        userImg.setImageResource(img);

        String text = "홍길동";
        userName.setText(text);

        typedArray.recycle();
    }

    public void setUserImg(String stringImg) {
        byte[] buffer = stringImg.getBytes();
        Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
        userImg.setImageBitmap(bitmap);
    }

    public void setUserDefaultImg() {
        userImg.setImageResource(R.drawable.profile);
    }

    public void setUserName(String text) {
        userName.setText(text);
    }
}
