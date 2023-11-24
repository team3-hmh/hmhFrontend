package kr.example.ttubuckttubuck.CustomView;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import kr.example.ttubuckttubuck.R;

public class HomeUserItem extends LinearLayout {
    private static final String TAG = "HomeUserItem_Debug";
    private ImageView userImg;
    private Bitmap profileBmp;
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
        int img = typedArray.getResourceId(R.styleable.UserItem_userImg, R.drawable.eclipse);
        userImg.setImageResource(img);

        String text = "홍길동";
        userName.setText(text);

        typedArray.recycle();
    }

    private Bitmap StringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            Log.d(TAG, "Is bitmap null?: " + (bitmap == null));
            Log.d(TAG, "bitmap info: " + bitmap.getWidth() + ", " + bitmap.getHeight());
            return bitmap;
        } catch (Exception e) {
            Log.e(TAG, "error occurred: " + e);
            e.printStackTrace();
            return null;
        }
    }

    public void setUserImg(String stringImg) {
        Log.d(TAG, "stringImg info: " + stringImg);
        // Bitmap decodedBmp = StringToBitmap(stringImg);
        BitmapDrawable sample = (BitmapDrawable)getDrawable(getContext(), R.drawable.eclipse);
        Bitmap bmp = sample.getBitmap();
        Bitmap resizedBmp = getResizedBitmap(bmp, bmp.getWidth() / 6, bmp.getHeight() / 6);
        Bitmap circleCroppedBmp = getCroppedBitmap(resizedBmp);
        userImg.setImageBitmap(circleCroppedBmp);
    }

    private Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    private Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public void setUserDefaultImg() {
        userImg.setImageResource(R.drawable.eclipse);
    }

    public void setUserName(String text) {
        userName.setText(text);
    }
}
