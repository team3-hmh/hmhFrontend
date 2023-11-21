package kr.example.ttubuckttubuck.CustomView;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import kr.example.ttubuckttubuck.R;

public class TodoDialog extends Dialog {
    private Button confirmBtn;
    private ImageButton quitBtn;
    private EditText contentEditTxt, dateEditTxt;

    public TodoDialog(@NonNull Context context) {
        super(context);
    }

    public TodoDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected TodoDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.dialog_todo);

        contentEditTxt = findViewById(R.id.contentEditTxt);
        dateEditTxt = findViewById(R.id.dateEditTxt);
        confirmBtn = findViewById(R.id.confirmBtn);
        confirmBtn.setOnClickListener(view -> {
            Toast.makeText(getContext(), "Hello World!", Toast.LENGTH_SHORT).show();
            this.dismiss();
        });

        quitBtn = findViewById(R.id.goBackBtn);
        quitBtn.setOnClickListener(view->{
            this.dismiss();
        });
    }
}
