package kr.example.ttubuckttubuck;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import net.daum.mf.map.api.MapView;

public class MapActivity extends AppCompatActivity {
    private ViewGroup container;
    private MapView mapView;
    private ImageButton goBackBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // UI Initialization ↓
        mapView = new MapView(this);
        container = findViewById(R.id.mapView);
        container.addView(mapView);

        goBackBtn = findViewById(R.id.goBackBtn);
    }
}
