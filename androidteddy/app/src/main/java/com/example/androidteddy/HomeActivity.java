package com.example.androidteddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
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
import com.example.androidteddy.callback.DTRCallback;
import com.example.androidteddy.callback.STDCallback;
import com.example.androidteddy.section.FavoritesSection;
import com.example.androidteddy.section.PortfolioSection;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;


public class HomeActivity extends AppCompatActivity {

    // networth
    GridView networth;
    SimpleAdapter networthAdapter;
    List<Map<String, Object>> networthItems;
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
    // volley && timer
    RequestQueue queue;
    Timer timer;
    Handler handler;
    // shared preference
    SharedPreferences favPref, portPref, networthPref;
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
        // pref
        favPref = getSharedPreferences("FAVORITES", MODE_PRIVATE);
        portPref = getSharedPreferences("PORTFOLIO", MODE_PRIVATE);

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
        timer = new Timer();
        handler = new Handler(getMainLooper());
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
                searchView.setQuery(ac.getText(), true);
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // direct to searchResultActivity
                Intent intent = new Intent(HomeActivity.this, SearchResultActivity.class);
                intent.putExtra("query",query.toUpperCase());
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

    @Override
    protected void onRestart() {
        super.onRestart();
        timer = new Timer();
        fetchDataAndNotify();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        timer.cancel();
        timer.purge();
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
        }, error -> {
            error.printStackTrace();
        });
        queue.add(acRequest);

    }

    private List<String> getFavorites() {
        String order = favPref.getString("ORDER","[]");
        List<String> orderList = new Gson().fromJson(order, ArrayList.class);
        return orderList;
    }

    private List<String> getPortfolio() {
        String order = portPref.getString("ORDER","[]");
        List<String> orderList = new Gson().fromJson(order, ArrayList.class);
        return orderList;
    }


    private TimerTask fetchDataTask() {
        TimerTask asyncTask = new TimerTask() {
            String res;
            float c, d, dp;
            @Override
            public void run() {
                //background work here..
                List<String> fav_shared = getFavorites();
                List<String> port_shared = getPortfolio();
                Log.d(TAG, "run: "+fav_shared);
                favItems.clear();
                portItems.clear();
                for(String symbol: fav_shared) {
                    res = BackendHelper.getQuote(symbol);
                    try {
                        JSONObject resJSON = new JSONObject(res);
                        c = (float)resJSON.getDouble("c");
                        d = (float)resJSON.getDouble("d");
                        dp = (float)resJSON.getDouble("dp");
                        Map<String, Object> item = new HashMap<>();
                        item.put("symbol", symbol);
                        item.put("companyName", favPref.getString(symbol, ""));
                        item.put("c",c);
                        item.put("d",d);
                        item.put("dp",dp);
                        favItems.add(item);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                float cash_value = networthPref.getFloat("CASH",25000.0f);
                float networth_value = cash_value;

                for(String symbol: port_shared) {
                    res = BackendHelper.getQuote(symbol);
                    try {
                        JSONObject resJSON = new JSONObject(res);
                        c = (float)resJSON.getDouble("c");
                        Log.d(TAG, "run: "+portPref.getAll());
                        int share = portPref.getInt(symbol,0);
                        float totalcost = portPref.getFloat(symbol+"::TOTAL", 0.0f);
                        float marketVal = share * (float)c;
                        Map<String, Object> item = new HashMap<>();
                        item.put("symbol", symbol);
                        item.put("share",  share + " shares");
                        item.put("c",marketVal);
                        item.put("d",marketVal-totalcost);
                        item.put("dp",(marketVal-totalcost)*100/totalcost);
                        portItems.add(item);

                        networth_value+=marketVal;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                networthItems.clear();
                Map item = new HashMap();
                item.put("networth_value", "$"+df.format(networth_value));
                item.put("cashbalance_value", "$"+df.format(cash_value));
                networthItems.add(item);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //UI thread work here..
                        // notify
                        portfolio_adapter.notifyDataSetChanged();
                        favorites_adapter.notifyDataSetChanged();
                        networthAdapter.notifyDataSetChanged();
                        spinner.setVisibility(View.GONE);
                        scrollView.setVisibility(View.VISIBLE);
                    }
                });
            }
        };

        return asyncTask;
    }

    private void fetchDataAndNotify() {
        timer.schedule(fetchDataTask(), 0, 15000);
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
        networthPref = getSharedPreferences("NETWORTH", MODE_PRIVATE);
        networthItems = new ArrayList<Map<String, Object>>();
        Map item = new HashMap();
        item.put("networth_value", "$"+df.format(25000));
        item.put("cashbalance_value", "$"+df.format(networthPref.getFloat("CASH",25000.0f)));
        networthItems.add(item);
        String[] s = new String[]{"networth_value","cashbalance_value"};
        int[] i = new int[]{R.id.net_worth_value, R.id.cash_balance_value};
        networthAdapter = new SimpleAdapter(this, networthItems, R.layout.layout_networth, s, i);
        networth.setAdapter(networthAdapter);
    }

    private void setPortfolio() {
        portItems = new ArrayList<>();
        portfolio = findViewById(R.id.portfolio);
        portfolio_adapter = new SectionedRecyclerViewAdapter();
        portfolio_adapter.addSection(new PortfolioSection(portItems));
        portfolio.setLayoutManager(new LinearLayoutManager(portfolio.getContext()));
        portfolio.setAdapter(portfolio_adapter);
        portfolio.addItemDecoration(new DividerItemDecoration(portfolio.getContext(), 1));
        enableDragToReorder(portfolio_adapter, portfolio, portItems, portPref);
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
        enableDragToReorder(favorites_adapter, favorites, favItems, favPref);
    }

    private void enableDragToReorder(SectionedRecyclerViewAdapter sectionAdapter,
                                     RecyclerView view, List<Map<String, Object>> items, SharedPreferences pref) {
        DTRCallback dtr = new DTRCallback(1|2,0, items, sectionAdapter){
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                int from = viewHolder.getAdapterPosition();
                int to = target.getAdapterPosition();
                getItems().add(to, getItems().remove(from));
                getSectionAdapter().notifyItemMoved(from, to);
                reorderInPref(pref, from, to);
                return true;
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(dtr);
        itemTouchHelper.attachToRecyclerView(view);
    }

    private void reorderInPref(SharedPreferences pref, int from, int to) {
        SharedPreferences.Editor editor = pref.edit();
        String order = pref.getString("ORDER", "[]");
        List<String> orderList = new Gson().fromJson(order, ArrayList.class);
        String tmp = orderList.get(from);
        orderList.remove(from);
        orderList.add(to, tmp);
        String newOrder = new Gson().toJson(orderList);
        editor.putString("ORDER", newOrder);
        editor.apply();
    }


    private void enableSwipeToDeleteAndUndo(SectionedRecyclerViewAdapter sectionAdapter,
                                            RecyclerView view, List<Map<String, Object>> items) {
        STDCallback stdCallback = new STDCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                Map<String, Object> item = items.remove(position);
                sectionAdapter.notifyItemRemoved(position);

                deleteFromFavorites((String)item.get("symbol"));

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(stdCallback);
        itemTouchhelper.attachToRecyclerView(view);
    }

    private void deleteFromFavorites(String del) {
        SharedPreferences.Editor editor = favPref.edit();
        //maintain order
        String order = favPref.getString("ORDER", "[]");
        List<String> orderList = new Gson().fromJson(order, ArrayList.class);
        orderList.remove(del);
        String newOrder = new Gson().toJson(orderList);
        editor.putString("ORDER", newOrder);

        //delete detail
        editor.remove(del);
        editor.apply();
    }

}