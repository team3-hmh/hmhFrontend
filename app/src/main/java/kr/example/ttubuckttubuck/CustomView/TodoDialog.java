package kr.example.ttubuckttubuck.CustomView;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import kr.example.ttubuckttubuck.R;

public class TodoDialog extends Dialog {
    private Button confirmBtn;
    private ImageButton quitBtn;
    private EditText contentEditTxt, dateEditTxt;

    public TodoDialog(@NonNull Context context) {
        super(context);
        initView();
    }

    public TodoDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        initView();
    }

    protected TodoDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView();
    }

    private void initView() {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.dialog_todo);

        contentEditTxt = findViewById(R.id.contentEditTxt);
        dateEditTxt = findViewById(R.id.dateEditTxt);
        confirmBtn = findViewById(R.id.confirmBtn);

        quitBtn = findViewById(R.id.goBackBtn);
        quitBtn.setOnClickListener(view-> this.dismiss());
    }

    public Button getConfirmBtn(){
        return this.confirmBtn;
    }

    public String getDate(){
        return this.dateEditTxt.getText().toString();
    }

    public String getContent(){
        return this.contentEditTxt.getText().toString();
    }
}
