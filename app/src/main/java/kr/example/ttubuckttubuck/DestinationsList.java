package kr.example.ttubuckttubuck;

import static kr.example.ttubuckttubuck.MapActivity.searchPath;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.ListFragment;

import org.json.JSONException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class DestinationsList extends ListFragment {
    private static final String TAG = "DestinationsList_Debug";

    DestinationsList() {
    }

    public static class ListItem {
        String address;
        String title;
        Pair<Double, Double> location;

        ListItem(String title, String address, Pair<Double, Double> location) {
            this.title = title;
            this.address = address;
            this.location = location;
        }
    }

    public static ArrayList<ListItem> listItems = new ArrayList<>();
    public static ArrayAdapter<ListItem> listAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listAdapter = new ArrayAdapter<>(getActivity(), 0, listItems) {
            @NonNull
            @Override
            public View getView(int position, View view, @NonNull ViewGroup parent) {
                ListItem item = listItems.get(position);
                if (view == null)
                    view = getActivity().getLayoutInflater().inflate(R.layout.destinations_fragment, parent, false);
                TextView title = view.findViewById(R.id.destinationTitle);
                TextView address = view.findViewById(R.id.destinationAddress);

                if(item.title == null){
                    title.setText(item.address);
                    address.setText("");
                }else {
                    title.setText(item.title);
                    address.setText(item.address);
                }
                return view;
            }
        };
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(null);
        setEmptyText("길찾기 결과가 여기에 표시됩니다.");
        ((TextView)getListView().getEmptyView()).setTextSize(18);
        ((TextView)getListView().getEmptyView()).setTextColor(Color.GRAY);
        setListAdapter(listAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
        ListItem item = (ListItem) l.getItemAtPosition(position);
        Log.d(TAG, "ListItem clicked: " + item.location.first + ", " + item.location.second);
        try {
            searchPath(item.location.first, item.location.second);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
