package kr.example.ttubuckttubuck;

import static kr.example.ttubuckttubuck.utils.MenuItemID.*;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ErrorActivity extends AppCompatActivity {
    private static final String TAG = "ErrorActivity_Debug";

    // UI components ↓
    private BottomNavigationView navigationView;
    private Toolbar toolBar;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
        setActionBar();
    }


    private void setActionBar() {
        toolBar = findViewById(R.id.toolBar);
        setSupportActionBar(toolBar);
        toolBar.setTitle("Error");

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setTitle("Error");

        navigationView = findViewById(R.id.navigationBtm);
        navigationView.getMenu().findItem(HOME).setChecked(false);
        navigationView.getMenu().findItem(COMMUNITY).setChecked(false);
        navigationView.getMenu().findItem(MAP).setChecked(false);
        navigationView.setOnItemSelectedListener(item -> {
            Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + ": " + item.getItemId() + " : " + R.id.map);
            if (item.getTitle().equals("Map")) {
                Intent toMapActivity = new Intent(getApplicationContext(), MapActivity.class);
                toMapActivity.putExtra("fromWhere", HOME);
                Log.d(TAG + "Intent", "Convert to Map Activity.");
                startActivity(toMapActivity);
            } else if (item.getTitle().equals("Home")) {
                Intent toMainActivity = new Intent(getApplicationContext(), MapActivity.class);
                toMainActivity.putExtra("fromWhere", HOME);
                Log.d(TAG + "Intent", "Convert to Main Activity.");
                startActivity(toMainActivity);
            } else { // Community
                Intent toCommunityActivity = new Intent(getApplicationContext(), CommunityActivity.class);
                toCommunityActivity.putExtra("fromWhere", HOME);
                Log.d(TAG + "Intent", "Convert to Community Activity.");
                startActivity(toCommunityActivity);
            }
            return false;
        });
    }
}
