package com.example.androidteddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class SearchResultActivity extends AppCompatActivity {

    private static final String TAG = "TEDDY::";
    // spinner
    ProgressBar spinner;
    ScrollView scrollView;
    // query
    String querySymbol;
    // shared preference
    SharedPreferences favPref, portPref, networthPref;
    // toolbar
    Toolbar toolbar;
    ImageView starButton;
    TextView toolbarText;
    // basic
    double c_value;
    TextView result_symbol, result_companyName, result_c, result_d, result_dp;
    ImageView result_logo, result_trendingIcon;
    // charts
    WebView chart_hourly;
    WebView chart_historical;
    // swipeable tabs
    ViewPagerAdapter vpa;
    ViewPager2 vp;
    TabLayout tl;
    // portfolio
    TextView share, avg_cost, total_cost, change, market_value;
    Button button_trade;
    Dialog dialog_trade;
    // stats
    TextView stats_o,stats_h, stats_l, stats_p;

    // about
    TextView ipo, industry, webpage, peers;

    // about

    // volley
    RequestQueue queue;

    // df
    DecimalFormat df = new DecimalFormat("0.00");
    boolean isFav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        init();
        getDataAndShowView();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void init() {
        querySymbol = getIntent().getStringExtra("query");
        spinner = findViewById(R.id.result_spinner);
        spinner.setVisibility(View.VISIBLE);
        scrollView = findViewById(R.id.result_scroll_view);
        scrollView.setVisibility(View.GONE);
        queue = Volley.newRequestQueue(this);
        favPref = getSharedPreferences("FAVORITES", MODE_PRIVATE);
        portPref = getSharedPreferences("PORTFOLIO", MODE_PRIVATE);
        networthPref = getSharedPreferences("NETWORTH", MODE_PRIVATE);
        setToolbar();
        setStarButton();
        setBasic();
        setTabLayout();
        setPortfolio();
        setStats();
        setAbout();

    }

    private void getDataAndShowView() {
        JsonObjectRequest quoteRequest = new JsonObjectRequest(Request.Method.GET, BackendHelper.getQuoteUrl(querySymbol), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            c_value = response.getDouble("c");
                            StringBuilder cstring = new StringBuilder();
                            cstring.append("$").append(df.format(response.getDouble("c")));
                            StringBuilder dstring = new StringBuilder();
                            dstring.append("$").append(df.format(response.getDouble("d")));
                            StringBuilder dpstring = new StringBuilder();
                            dpstring.append("(").append(df.format(response.getDouble("dp"))).append(")");
                            StringBuilder ostring = new StringBuilder();
                            ostring.append("$").append(df.format(response.getDouble("o")));
                            StringBuilder lstring = new StringBuilder();
                            lstring.append("$").append(df.format(response.getDouble("l")));
                            StringBuilder hstring = new StringBuilder();
                            hstring.append("$").append(df.format(response.getDouble("h")));
                            StringBuilder pcstring = new StringBuilder();
                            pcstring.append("$").append(df.format(response.getDouble("pc")));
                            float marketVal = (float)response.getDouble("c")*portPref.getInt(querySymbol,0);
                            float diff = marketVal-portPref.getFloat(querySymbol+"::TOTAL", 0.0f);
                            StringBuilder marketString = new StringBuilder();
                            marketString.append("$").append(df.format(marketVal));
                            StringBuilder diffString = new StringBuilder();
                            diffString.append("$").append(df.format(diff));

                            stats_h.setText(hstring.toString());
                            stats_l.setText(lstring.toString());
                            stats_o.setText(ostring.toString());
                            stats_p.setText(pcstring.toString());

                            result_c.setText(cstring.toString());
                            result_d.setText(dstring.toString());
                            result_dp.setText(dpstring.toString());

                            change.setText(diffString.toString());
                            market_value.setText(marketString.toString());
                            if(diff < 0) {
                                change.setTextColor(Color.RED);
                                market_value.setTextColor(Color.RED);
                            } else if(diff > 0) {
                                change.setTextColor(getColor(R.color.green));
                                market_value.setTextColor(getColor(R.color.green));
                            }

                            if(response.getDouble("d") > 0) {
                                result_d.setTextColor(getColor(R.color.green));
                                result_dp.setTextColor(getColor(R.color.green));
                                result_trendingIcon.setImageResource(R.drawable.ic_baseline_trending_up_24);
                            } else if(response.getDouble("d") < 0) {
                                result_d.setTextColor(Color.RED);
                                result_dp.setTextColor(Color.RED);
                                result_trendingIcon.setImageResource(R.drawable.ic_baseline_trending_down_24);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        spinner.setVisibility(View.GONE);
                        scrollView.setVisibility(View.VISIBLE);
                    }
                }, null);
        JsonObjectRequest profileRequest = new JsonObjectRequest(Request.Method.GET, BackendHelper.getProfile2Url(querySymbol), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(response.length() == 0) {
                            Intent intent = new Intent(SearchResultActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            result_symbol.setText(querySymbol);
                            try {
                                result_companyName.setText(response.getString("name"));
                                Picasso.get().load(response.getString("logo")).into(result_logo);
                                ipo.setText(response.getString("ipo"));
                                industry.setText(response.getString("finnhubIndustry"));
                                webpage.setText(response.getString("weburl"));
                                webpage.setOnClickListener(new View.OnClickListener(){
                                    @Override
                                    public void onClick(View view) {
                                        String url = null;
                                        try {
                                            url = response.getString("weburl");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        Intent intent = new Intent();
                                        intent.setAction(Intent.ACTION_VIEW);
                                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                                        intent.setData(Uri.parse(url));
                                        startActivity(intent);
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            queue.add(quoteRequest);
                        }
                    }
                }, null);

        queue.add(profileRequest);
    }

    public void setToolbar(){
        toolbar = findViewById(R.id.toolbar);
        toolbarText = findViewById(R.id.toolbar_text);
        toolbarText.setText(querySymbol);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    public void setStarButton() {
        isFav = checkIsInFav();
        starButton = findViewById(R.id.star_button);
        setStarButtonImage(isFav);
        starButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFav = !isFav;
                setStarButtonImage(isFav);
                String toastString = querySymbol + (isFav?" is added to favorites":" is removed from favorites");
                Toast.makeText(SearchResultActivity.this, toastString, Toast.LENGTH_SHORT).show();

                updateFavorites(isFav);

            }
        });
    }

    private void updateFavorites(boolean isFav) {
        SharedPreferences.Editor editor = favPref.edit();
        String order = favPref.getString("ORDER","[]");
        List<String> orderList = new Gson().fromJson(order, ArrayList.class);
        if(isFav) {
            // add to shared preference
            orderList.add(querySymbol);
            editor.putString(querySymbol, result_companyName.getText().toString());
        } else {
            // remove from shared preference
            orderList.remove(querySymbol);
            editor.remove(querySymbol);
        }
        String newOrder = new Gson().toJson(orderList);
        Log.d(TAG, "updateFavorites: "+newOrder);
        editor.putString("ORDER", newOrder);
        editor.apply();
    }

    private boolean checkIsInFav() {
        SharedPreferences pref = getSharedPreferences("FAVORITES", MODE_PRIVATE);
        String s = pref.getString(querySymbol,null);
        return (s != null);
    }

    private void setStarButtonImage(boolean isFav) {
        if(isFav) {
            starButton.setImageResource(R.drawable.ic_baseline_star_24);
        } else {
            starButton.setImageResource(R.drawable.ic_baseline_star_border_24);
        }
    }

    private void setBasic() {
        result_symbol = findViewById(R.id.result_symbol);
        result_companyName = findViewById(R.id.result_company_name);
        result_c = findViewById(R.id.result_c);
        result_d = findViewById(R.id.result_d);
        result_dp = findViewById(R.id.result_dp);
        result_logo = findViewById(R.id.logo_icon);
        result_trendingIcon = findViewById(R.id.result_trending_icon);
    }

    private void setTabLayout() {
        vp = findViewById(R.id.view_pager);
        tl = findViewById(R.id.tab_layout);
        vpa = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle(), querySymbol);
        vp.setAdapter(vpa);
        int[] d_arr = new int[]{R.drawable.ic_chart_line, R.drawable.ic_clock_time_three};
        tl.addOnTabSelectedListener(
            new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    tab.getIcon().setColorFilter(new BlendModeColorFilter(getColor(R.color.purple_700), BlendMode.SRC_ATOP));
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    tab.getIcon().setColorFilter(new BlendModeColorFilter(getColor(R.color.black), BlendMode.SRC_ATOP));
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            }
        );
        new TabLayoutMediator(tl, vp, (tab, position) -> {
            tab.setIcon(d_arr[position]);
        }).attach();
    }

    private void setPortfolio() {
        share = findViewById(R.id.shares_owned_content);
        avg_cost = findViewById(R.id.avg_cost_content);
        total_cost = findViewById(R.id.total_cost_content);
        change = findViewById(R.id.change_content);
        market_value = findViewById(R.id.market_value_content);
        button_trade = findViewById(R.id.button_trade);
        dialog_trade = new Dialog(SearchResultActivity.this);
        dialog_trade.setContentView(R.layout.dialog_trade);
        dialog_trade.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        button_trade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView title = dialog_trade.findViewById(R.id.trade_title);
                title.setText("Trade "+ result_companyName.getText().toString()+" shares");
                TextView cashToBuy = dialog_trade.findViewById(R.id.cash_to_buy);
                cashToBuy.setText("$"+df.format(networthPref.getFloat("CASH",25000.0f))+" to buy "+querySymbol);
                TextView calculation = dialog_trade.findViewById(R.id.calculation);
                StringBuilder calString = new StringBuilder();
                calString.append(0).append("*").append(result_c.getText().toString());
                calString.append("/share = ").append(df.format(0*c_value));
                calculation.setText(calString.toString());
                EditText share_input = dialog_trade.findViewById(R.id.share_input);
                share_input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        Log.d(TAG, "afterTextChanged: ");
                        String input = editable.toString();
                        int inputInt = isParsable(input)?Integer.parseInt(input):0;
                        TextView calculation = dialog_trade.findViewById(R.id.calculation);
                        StringBuilder calString = new StringBuilder();
                        calString.append(inputInt).append("*").append(result_c.getText().toString());
                        calString.append("/share = ").append(df.format(inputInt*c_value));
                        calculation.setText(calString.toString());
                    }
                });
                Button button_buy = dialog_trade.findViewById(R.id.button_buy);
                button_buy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String input = share_input.getText().toString();
                        if(!isParsable(input)) {
                            Toast.makeText(SearchResultActivity.this,"Please enter a valid amount", Toast.LENGTH_LONG).show();
                        } else {
                            int toBuy = Integer.parseInt(input);
                            double cost = c_value* toBuy;
                            if(toBuy <= 0) {
                                Toast.makeText(SearchResultActivity.this, "Cannot buy non-positive shares", Toast.LENGTH_LONG).show();
                            } else if(cost > networthPref.getFloat("CASH",25000.0f)) {
                                Toast.makeText(SearchResultActivity.this,"Not enough money to buy",Toast.LENGTH_LONG).show();
                            } else {
                                //success
                                int origShares = portPref.getInt(querySymbol,0);
                                float origTotal = portPref.getFloat(querySymbol+"::TOTAL",0.0f);
                                SharedPreferences.Editor editor = portPref.edit();
                                editor.putInt(querySymbol, origShares + toBuy);
                                editor.putFloat(querySymbol+"::TOTAL", origTotal + toBuy*(float)c_value);
                                //add to order
                                if(origShares == 0) {
                                    String order = portPref.getString("ORDER","[]");
                                    List<String> orderList = new Gson().fromJson(order, ArrayList.class);
                                    orderList.add(querySymbol);
                                    String newOrder = new Gson().toJson(orderList);
                                    editor.putString("ORDER", newOrder);
                                }
                                editor.apply();

                                //show success dialog
                                Dialog dialog_success = new Dialog(SearchResultActivity.this);
                                dialog_success.setContentView(R.layout.dialog_success);

                                TextView success_msg = dialog_success.findViewById(R.id.success_msg);
                                StringBuilder successString = new StringBuilder();
                                successString.append("You have successfully bought ")
                                        .append(toBuy)
                                        .append(" shares of "+ querySymbol);
                                success_msg.setText(successString.toString());
                                Button button_done = dialog_success.findViewById(R.id.button_done);
                                button_done.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog_success.dismiss();
                                        setPortfolioContent();
                                    }
                                });
                                dialog_success.show();
                                dialog_trade.dismiss();
                            }
                        }
                    }
                });
                Button button_sell = dialog_trade.findViewById(R.id.button_sell);

                dialog_trade.show();
            }
        });

        setPortfolioContent();
    }

    private void setPortfolioContent() {
        int share_owned = portPref.getInt(querySymbol, 0);
        float total = portPref.getFloat(querySymbol+"::TOTAL",0.0f);
        float avg = share_owned == 0 ? 0: total/share_owned;
        share.setText(""+share_owned);
        avg_cost.setText("$"+df.format(avg));
        total_cost.setText("$"+df.format(total));
    }

    private boolean isParsable(String input) {
        try{
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void setStats() {
        stats_h = findViewById(R.id.high_content);
        stats_l = findViewById(R.id.low_content);
        stats_o = findViewById(R.id.open_content);
        stats_p = findViewById(R.id.prev_content);
    }

    private void setAbout() {
        ipo = findViewById(R.id.ipo_content);
        industry = findViewById(R.id.industry_content);
        webpage = findViewById(R.id.webpage_content);
        peers = findViewById(R.id.peer_content);
    }
}