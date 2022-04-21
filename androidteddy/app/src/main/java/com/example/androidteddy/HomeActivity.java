package com.example.androidteddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class HomeActivity extends AppCompatActivity {

    RecyclerView portfolio;
    RecyclerView favorites;
    TextView date, test;
    CustomAdapter adapter;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        relativeLayout = findViewById(R.id.home_root);
        date = findViewById(R.id.date);
        new Thread(new Runnable() {
            String res;
            @Override
            public void run() {
                res = BackendHelper.getPeers("AAPL");
                runOnUiThread(() -> {
                    test = findViewById(R.id.test);
                    try {
                        JSONArray json = new JSONArray(res);
                        test.setText(String.valueOf(json.get(1)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                });
            }
        }).start();
        String today = new SimpleDateFormat("dd MMMM yyyy", Locale.US).format(new Date());
        date.setText(today);

        portfolio = findViewById(R.id.portfolio);
        favorites = findViewById(R.id.favorites);
        populateRecyclerView();
        enableSwipeToDeleteAndUndo();

    }
    private void populateRecyclerView() {
        ArrayList<String> stringArrayList = new ArrayList<>();
        stringArrayList.add("Item 1");
        stringArrayList.add("Item 2");
        stringArrayList.add("Item 3");
        stringArrayList.add("Item 4");
        stringArrayList.add("Item 5");

        adapter = new CustomAdapter(stringArrayList);
        portfolio.setAdapter(adapter);
    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();
                final String item = adapter.getData().get(position);

                adapter.removeItem(position);


                Snackbar snackbar = Snackbar
                        .make(relativeLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", (view) -> {
                    adapter.restoreItem(item, position);
                    portfolio.scrollToPosition(position);
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(portfolio);

    }
}