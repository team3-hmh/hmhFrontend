package kr.example.ttubuckttubuck.CustomView;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import kr.example.ttubuckttubuck.R;

public class ErrorDialog extends Dialog {
    private TextView titleTxt, contentTxt;
    private ImageButton quitBtn;

    public ErrorDialog(@NonNull Context context) {
        super(context);
    }

    public ErrorDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected ErrorDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.dialog_error);

        titleTxt = findViewById(R.id.titleTxt);
        contentTxt = findViewById(R.id.contentTxt);

        quitBtn = findViewById(R.id.goBackBtn);
        quitBtn.setOnClickListener(view->{
            this.dismiss();
        });
    }
}
