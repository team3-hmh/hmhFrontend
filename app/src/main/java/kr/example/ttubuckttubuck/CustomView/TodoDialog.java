package kr.example.ttubuckttubuck.CustomView;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import kr.example.ttubuckttubuck.R;

public class TodoDialog extends Dialog {
    private Button confirmBtn;
    private ImageButton quitBtn;
    private EditText contentEditTxt;
    private TextView dateEditTxt;
    private CalendarView calendarView;

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

        calendarView = findViewById(R.id.calendar);
        calendarView.setVisibility(View.GONE);
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String date = year + "-" + (month + 1) + "-" + dayOfMonth;
            dateEditTxt.setText(date);
            calendarView.setVisibility(View.GONE);
        });

        contentEditTxt = findViewById(R.id.contentEditTxt);
        dateEditTxt = findViewById(R.id.dateEditTxt);
        dateEditTxt.setOnClickListener(view-> calendarView.setVisibility(View.VISIBLE));
        confirmBtn = findViewById(R.id.confirmBtn);

        quitBtn = findViewById(R.id.goBackBtn);
        quitBtn.setOnClickListener(view-> this.dismiss());
    }

    public Button getConfirmBtn(){
        return this.confirmBtn;
    }

    public void setDate(String s){
        this.dateEditTxt.setText(s);
    }

    public CalendarView getCalendarView(){
        return this.calendarView;
    }
    public String getDate(){
        return this.dateEditTxt.getText().toString();
    }

    public String getContent(){
        return this.contentEditTxt.getText().toString();
    }
}
