package com.example.androidteddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class SearchResultActivity extends AppCompatActivity {

    private static final String TAG = "TEDDY::";
    // spinner
    ProgressBar spinner;
    ScrollView scrollView;
    // query
    String querySymbol;
    // toolbar
    Toolbar toolbar;
    ImageView starButton;
    TextView toolbarText;
    // basic
    TextView result_symbol, result_companyName, result_c, result_d, result_dp;
    ImageView result_logo, result_trendingIcon;
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
        querySymbol = "AAPL";
        spinner = findViewById(R.id.result_spinner);
        spinner.setVisibility(View.VISIBLE);
        scrollView = findViewById(R.id.result_scroll_view);
        scrollView.setVisibility(View.GONE);
        setToolbar();
        setStarButton();
        setBasic();

        // volley
        queue = Volley.newRequestQueue(this);

    }

    private void getDataAndShowView() {
        JsonObjectRequest quoteRequest = new JsonObjectRequest(Request.Method.GET, BackendHelper.getQuoteUrl(querySymbol), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            StringBuilder cstring = new StringBuilder();
                            cstring.append("$").append(df.format(response.getDouble("c")));
                            StringBuilder dstring = new StringBuilder();
                            dstring.append("$").append(df.format(response.getDouble("d")));
                            StringBuilder dpstring = new StringBuilder();
                            dpstring.append("(").append(df.format(response.getDouble("dp")));

                            result_c.setText(cstring.toString());
                            result_d.setText(dstring.toString());
                            result_dp.setText(dpstring.toString());

                            if(response.getDouble("d") > 0) {
                                result_d.setTextColor(Color.GREEN);
                                result_dp.setTextColor(Color.GREEN);
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
//        isFav = checkIsInFav();
        isFav = false;
        starButton = findViewById(R.id.star_button);
        setStarButtonImage(isFav);
        starButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFav = !isFav;
                // delete in share preference
                setStarButtonImage(isFav);
                String toastString = querySymbol + (isFav?"is added to favorites":" is removed from favorites");
                Toast.makeText(SearchResultActivity.this, toastString, Toast.LENGTH_SHORT).show();
            }
        });
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
}