package kr.example.ttubuckttubuck;

import static kr.example.ttubuckttubuck.utils.MenuItemID.*;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import kr.example.ttubuckttubuck.CustomView.AddUserItem;

public class AddFriendsActivity extends AppCompatActivity {
    private static final String TAG = "AddFriendsActivity_Debug";

    // UI components ↓
    private BottomNavigationView navigationView;
    private Toolbar toolBar;
    private ActionBar actionBar;
    private ImageButton searchBtn, goBackBtn;
    private EditText searchEditTxt;
    private LinearLayout userList;
    private AddUserItem userItem;

    public void searchUser(String query){
        Log.d(TAG, "searchUser() called.");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        setActionBar();

        userList = findViewById(R.id.userList);
        searchEditTxt = findViewById(R.id.searchEditTxt);
        searchBtn = findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(view->{
            final String query = searchEditTxt.getText().toString();
            if(query.equals("") || query == null)
                Toast.makeText(getApplicationContext(), "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show();
            else
                searchUser(query);
        });

        userItem = findViewById(R.id.userItem0);
        userItem.setUserName("Kim Ho");
        userItem.getFollowBtn().setOnClickListener(view -> {
            Toast.makeText(getApplicationContext(), "Hello World", Toast.LENGTH_SHORT).show();
        });
    }

    private void setActionBar() {
        toolBar = findViewById(R.id.toolBar);
        setSupportActionBar(toolBar);
        toolBar.setTitle("Friends");

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setTitle("Friends");

        goBackBtn = findViewById(R.id.goBackBtn);
        goBackBtn.setOnClickListener(view->{
            Intent toMainActivity = new Intent(getApplicationContext(), MainActivity.class);
            Log.d(TAG + "Intent", "Convert to Main Activity.");
            toMainActivity.putExtra("fromWhere", HOME);
            startActivity(toMainActivity);
        });

        navigationView = findViewById(R.id.navigationBtm);
        navigationView.getMenu().findItem(HOME).setChecked(true);
        navigationView.setOnItemSelectedListener(item -> {
            Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + ": " + item.getItemId());
            if (item.getTitle().equals("Map")) {
                Intent toMapActivity = new Intent(getApplicationContext(), MapActivity.class);
                toMapActivity.putExtra("fromWhere", HOME);
                Log.d(TAG + "Intent", "Convert to Map Activity.");
                startActivity(toMapActivity);
            } else if (item.getTitle().equals("Home")) {
                Intent toMainActivity = new Intent(getApplicationContext(), MainActivity.class);
                Log.d(TAG + "Intent", "Convert to Main Activity.");
                toMainActivity.putExtra("fromWhere", HOME);
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