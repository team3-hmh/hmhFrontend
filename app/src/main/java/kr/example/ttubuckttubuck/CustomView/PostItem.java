package kr.example.ttubuckttubuck.CustomView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import kr.example.ttubuckttubuck.R;

public class PostItem extends LinearLayout {
    private TextView postContent;
    private LinearLayout starBtns;
    private ImageButton starBtn1, starBtn2, starBtn3, starBtn4, starBtn5, goBackBtn;
    private ArrayList<ImageButton> starBtnsArray = new ArrayList<>();

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

        postContent = findViewById(R.id.postContent);
        starBtns = findViewById(R.id.rate);

        starBtn1 = findViewById(R.id.star1);
        starBtnsArray.add(starBtn1);
        starBtn2 = findViewById(R.id.star2);
        starBtnsArray.add(starBtn2);
        starBtn3 = findViewById(R.id.star3);
        starBtnsArray.add(starBtn3);
        starBtn4 = findViewById(R.id.star4);
        starBtnsArray.add(starBtn4);
        starBtn5 = findViewById(R.id.star5);
        starBtnsArray.add(starBtn5);
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
        String text = "삶과윤리 수업이 아주 좋습니다. 수신지가 치국평천하라는 내용이 기억에 남습니다.";
        postContent.setText(text);

        for(int i = 0; i < 4; i++)
            starBtnsArray.get(i).setSelected(true);

        typedArray.recycle();
    }


    public void setPostContent(String text){
        postContent.setText(text);
    }

    public void setRate(String rate){
        for(int i = 0; i < Integer.valueOf(rate); i++)
            starBtnsArray.get(i).setSelected(true);
    }

}
