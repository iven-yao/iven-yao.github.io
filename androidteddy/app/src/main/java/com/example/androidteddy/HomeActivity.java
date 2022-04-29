package com.example.androidteddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;


public class HomeActivity extends AppCompatActivity {

    // networth
    GridView networth;
    // date, footer
    TextView date, footer;
    // portfolio, favorites
    RecyclerView portfolio, favorites;
    SectionedRecyclerViewAdapter portfolio_adapter, favorites_adapter;
    List<Map<String, Object>> favItems, portItems;
    DecimalFormat df = new DecimalFormat("0.00");
    // autocomplete
    ArrayAdapter<String> acAdapter;
    List<String> autoCompleteItems;
    SearchView.SearchAutoComplete ac;
    // volley
    RequestQueue queue;
    // spinner
    ProgressBar spinner;
    ScrollView scrollView;
    //debug
    String TAG="TEDDY::";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init();
        // get data and update view
        fetchDataAndNotify();

    }

    private void init() {
        // spinner
        spinner = findViewById(R.id.spinner);
        spinner.setVisibility(View.VISIBLE);
        scrollView = findViewById(R.id.scrollview);
        scrollView.setVisibility(View.GONE);
        // portfolio
        setPortfolio();
        // favorites
        setFavorites();
        //date
        setDate();
        // networth
        setNetworth();
        // footer
        setFooter();
        // autocomplete
        autoCompleteItems = new ArrayList<>();
        // volley
        queue = Volley.newRequestQueue(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem menuItem = menu.findItem(R.id.search_item);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search...");

        ac = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        ac.setHintTextColor(Color.GRAY);
        ac.setBackgroundColor(Color.WHITE);
        ac.setTextColor(Color.BLACK);
        ac.setDropDownBackgroundResource(android.R.color.white);

        acAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, autoCompleteItems);
        ac.setAdapter(acAdapter);
        ac.showDropDown();

        ac.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long id) {
                String queryString=(String)adapterView.getItemAtPosition(itemIndex);
                ac.setText("" + queryString.split(" | ")[0]);
                Toast.makeText(HomeActivity.this, "you clicked " + queryString, Toast.LENGTH_LONG).show();
                searchView.setQuery(ac.getText(), true);
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // direct to searchResultActivity
                Intent intent = new Intent(HomeActivity.this, SearchResultActivity.class);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length() >1) createQList(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void createQList(String q) {
        JsonObjectRequest acRequest = new JsonObjectRequest(Request.Method.GET,
                BackendHelper.getSearchUrl(q), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    autoCompleteItems.clear();
                    JSONArray resultArray = response.getJSONArray("result");
                    for (int i = 0; i < resultArray.length(); i++) {
                        JSONObject result = resultArray.getJSONObject(i);
                        if (result.getString("type").equals("Common Stock")) {
                            if(!result.getString("symbol").contains(".")) {
                                StringBuilder sb = new StringBuilder();
                                sb.append(result.getString("symbol"));
                                sb.append(" | ");
                                sb.append(result.getString("description"));
                                autoCompleteItems.add(sb.toString());
                            }
                        }
                    }
                    acAdapter = new ArrayAdapter<String>(HomeActivity.this, android.R.layout.simple_dropdown_item_1line, autoCompleteItems);
                    ac.setAdapter(acAdapter);
                    ac.showDropDown();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, null);
        queue.add(acRequest);
    }

    private void fetchDataAndNotify() {
        List<String> fav_shared = Arrays.asList("AAPL","GOOGL","SPOT");
        List<String> port_shared = Arrays.asList("AAPL","GOOGL","SPOT", "AMZN");
        Handler handler = new Handler(Looper.getMainLooper());
        Timer timer = new Timer();
        TimerTask asyncTask = new TimerTask() {
            String res, resProfile2;
            double c, d, dp;
            @Override
            public void run() {
                Log.d(TAG, "run: fetching...");
                //background work here..
                favItems.clear();
                portItems.clear();
                for(String symbol: fav_shared) {
                    res = BackendHelper.getQuote(symbol);
                    resProfile2 = BackendHelper.getProfile2(symbol);
                    try {
                        JSONObject resJSON = new JSONObject(res);
                        JSONObject jsonProfile2 = new JSONObject(resProfile2);
                        c = resJSON.getDouble("c");
                        d = resJSON.getDouble("d");
                        dp = resJSON.getDouble("dp");
                        Map<String, Object> item = new HashMap<>();
                        item.put("symbol", symbol);
                        item.put("companyName", jsonProfile2.getString("name"));
                        item.put("c",c);
                        item.put("d",d);
                        item.put("dp",dp);
                        favItems.add(item);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                for(String symbol: port_shared) {
                    res = BackendHelper.getQuote(symbol);
                    try {
                        JSONObject resJSON = new JSONObject(res);
                        c = resJSON.getDouble("c");
                        d = resJSON.getDouble("d");
                        dp = resJSON.getDouble("dp");
                        Map<String, Object> item = new HashMap<>();
                        item.put("symbol", symbol);
                        item.put("share", "3 shares");
                        item.put("c",c);
                        item.put("d",d);
                        item.put("dp",dp);
                        portItems.add(item);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //UI thread work here..
                        // notify
                        portfolio_adapter.notifyDataSetChanged();
                        favorites_adapter.notifyDataSetChanged();
                        spinner.setVisibility(View.GONE);
                        scrollView.setVisibility(View.VISIBLE);
                    }
                });
            }
        };
        timer.schedule(asyncTask, 0, 15000);
    }

    private void setFooter() {
        footer = findViewById(R.id.footer);
        footer.setText("Powered By Finnhub");
        footer.setTextColor(Color.GRAY);
        footer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String url = "https://finnhub.io";
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
    }

    private void setDate() {
        date = findViewById(R.id.date);
        String today = new SimpleDateFormat("dd MMMM yyyy", Locale.US).format(new Date());
        date.setText(today);
    }

    private void setNetworth() {
        networth = findViewById(R.id.net_worth);
        List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        Map item = new HashMap();
        item.put("networth_value", "$"+df.format(25000));
        item.put("cashbalance_value", "$"+df.format(25000));
        items.add(item);
        String[] s = new String[]{"networth_value","cashbalance_value"};
        int[] i = new int[]{R.id.net_worth_value, R.id.cash_balance_value};
        SimpleAdapter sa = new SimpleAdapter(this, items, R.layout.layout_networth, s, i);
        networth.setAdapter(sa);
    }

    private void setPortfolio() {
        portItems = new ArrayList<>();
        portfolio = findViewById(R.id.portfolio);
        portfolio_adapter = new SectionedRecyclerViewAdapter();
        portfolio_adapter.addSection(new PortfolioSection(portItems));
        portfolio.setLayoutManager(new LinearLayoutManager(portfolio.getContext()));
        portfolio.setAdapter(portfolio_adapter);
        portfolio.addItemDecoration(new DividerItemDecoration(portfolio.getContext(), 1));
        enableDragToReorder(portfolio_adapter, portfolio, portItems);
    }

    private void setFavorites() {
        favItems = new ArrayList<>();
        favorites = findViewById(R.id.favorites);
        favorites_adapter = new SectionedRecyclerViewAdapter();
        favorites_adapter.addSection(new FavoritesSection(favItems));
        favorites.setLayoutManager(new LinearLayoutManager(favorites.getContext()));
        favorites.setAdapter(favorites_adapter);
        favorites.addItemDecoration(new DividerItemDecoration(favorites.getContext(), 1));
        enableSwipeToDeleteAndUndo(favorites_adapter, favorites, favItems);
        enableDragToReorder(favorites_adapter, favorites, favItems);
    }

    private void enableDragToReorder(SectionedRecyclerViewAdapter sectionAdapter,
                                     RecyclerView view, List<Map<String, Object>> items) {
        DTRCallback dtr = new DTRCallback(1|2,0, items, sectionAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(dtr);
        itemTouchHelper.attachToRecyclerView(view);
    }


    private void enableSwipeToDeleteAndUndo(SectionedRecyclerViewAdapter sectionAdapter,
                                            RecyclerView view, List<Map<String, Object>> items) {
        STDCallback stdCallback = new STDCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                items.remove(position);
                sectionAdapter.notifyItemRemoved(position);

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(stdCallback);
        itemTouchhelper.attachToRecyclerView(view);
    }

}