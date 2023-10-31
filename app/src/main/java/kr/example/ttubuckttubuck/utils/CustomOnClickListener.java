package kr.example.ttubuckttubuck.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import kr.example.ttubuckttubuck.MainActivity;

public class CustomOnClickListener {
    protected View.OnClickListener toMainOnClickListner(Context context, String TAG){
        Intent toMainActivity = new Intent(context, MainActivity.class);
        Log.d(TAG+"Intent", "Convert to Main Activity.");
        //startActivity(toMainActivity);
        return null;
    }

    private void backspaceProber(EditText e) {
        e.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                StringBuffer contents = new StringBuffer(e.getText().toString());
                int l = contents.length();
                contents.replace(l - 1, l - 1, "");
                e.setText(contents.toString());
                return true;
            }
            return false;
        });
    }
}
