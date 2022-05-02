package com.example.androidteddy;

import android.util.Log;
import android.webkit.JavascriptInterface;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class BackendHelper {
    private static HttpURLConnection conn;
    private static final String HOSTNAME = "https://stockteddy-backend.wl.r.appspot.com";
    private static final String PROFILE2 = "/api/profile2?";
    private static final String CANDLE = "/api/candle?";
    private static final String QUOTE = "/api/quote?";
    private static final String SEARCH = "/api/search?";
    private static final String NEWS = "/api/company-news?";
    private static final String TREND = "/api/recommendation?";
    private static final String SOCIAL = "/api/social-sentiment?";
    private static final String PEERS = "/api/peers?";
    private static final String EARNINGS = "/api/earnings?";
    private static URL url;
    private static final String TAG="TEDDY::";

    @JavascriptInterface
    public static String getProfile2(String symbol) {
        Map<String, String> payload = new HashMap<>();
        payload.put("symbol",symbol);
        return connect(PROFILE2, payload);
    }
    @JavascriptInterface
    public static String getCandle(String symbol, String res, String from, String to) {
        Map<String, String> payload = new HashMap<>();
        payload.put("symbol", symbol);
        payload.put("resolution", res);
        payload.put("from", from);
        payload.put("to", to);
        return connect(CANDLE, payload);
    }
    @JavascriptInterface
    public static String getQuote(String symbol) {
        Map<String, String> payload = new HashMap<>();
        payload.put("symbol",symbol);
        return connect(QUOTE, payload);
    }
    @JavascriptInterface
    public static String getSearch(String q) {
        Map<String, String> payload = new HashMap<>();
        payload.put("q",q);
        return connect(SEARCH, payload);
    }
    @JavascriptInterface
    public static String getNews(String symbol) {
        Map<String, String> payload = new HashMap<>();
        payload.put("symbol",symbol);
        return connect(NEWS, payload);
    }
    @JavascriptInterface
    public static String getTrend(String symbol) {
        Map<String, String> payload = new HashMap<>();
        payload.put("symbol",symbol);
        return connect(TREND, payload);
    }
    @JavascriptInterface
    public static String getSocial(String symbol) {
        Map<String, String> payload = new HashMap<>();
        payload.put("symbol",symbol);
        return connect(SOCIAL, payload);
    }
    @JavascriptInterface
    public static String getPeers(String symbol) {
        Map<String, String> payload = new HashMap<>();
        payload.put("symbol",symbol);
        return connect(PEERS, payload);
    }
    @JavascriptInterface
    public static String getEarnings(String symbol) {
        Map<String, String> payload = new HashMap<>();
        payload.put("symbol",symbol);
        return connect(EARNINGS, payload);
    }

    public static String getProfile2Url(String symbol) {
        Map<String, String> payload = new HashMap<>();
        payload.put("symbol",symbol);
        return buildUrl(PROFILE2, payload);
    }

    public static String getCandleUrl(String symbol, String res, String from, String to) {
        Map<String, String> payload = new HashMap<>();
        payload.put("symbol", symbol);
        payload.put("resolution", res);
        payload.put("from", from);
        payload.put("to", to);
        return buildUrl(CANDLE, payload);
    }

    public static String getQuoteUrl(String symbol) {
        Map<String, String> payload = new HashMap<>();
        payload.put("symbol",symbol);
        return buildUrl(QUOTE, payload);
    }

    public static String getSearchUrl(String q) {
        Map<String, String> payload = new HashMap<>();
        payload.put("q",q);
        return buildUrl(SEARCH, payload);
    }

    public static String getNewsUrl(String symbol) {
        Map<String, String> payload = new HashMap<>();
        payload.put("symbol",symbol);
        return buildUrl(NEWS, payload);
    }

    public static String getTrendUrl(String symbol) {
        Map<String, String> payload = new HashMap<>();
        payload.put("symbol",symbol);
        return buildUrl(TREND, payload);
    }

    public static String getSocialUrl(String symbol) {
        Map<String, String> payload = new HashMap<>();
        payload.put("symbol",symbol);
        return buildUrl(SOCIAL, payload);
    }

    public static String getPeersUrl(String symbol) {
        Map<String, String> payload = new HashMap<>();
        payload.put("symbol",symbol);
        return buildUrl(PEERS, payload);
    }

    public static String getEarningsUrl(String symbol) {
        Map<String, String> payload = new HashMap<>();
        payload.put("symbol",symbol);
        return buildUrl(EARNINGS, payload);
    }

    private static String buildUrl(String api, Map<String, String> params) {
        StringBuilder builder = new StringBuilder(HOSTNAME);
        builder.append(api);
        boolean first = true;
        for(String key: params.keySet()) {
            if(!first) {
                builder.append("&");
            } else {
                first = false;
            }
            builder.append(key);
            builder.append("=");
            builder.append(params.get(key));
        }
        return builder.toString();
    }

    private static String connect(String api, Map<String, String> payload) {
        BufferedReader buf;
        String line;
        StringBuilder response = new StringBuilder();
        try {
            url = new URL(buildUrl(api, payload));
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
//            conn.setConnectTimeout(10000);
//            conn.setReadTimeout(10000);
            int status = conn.getResponseCode();

            if(status >= 300) {
                buf = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            } else {
                buf = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            }
            while( (line = buf.readLine()) != null) {
                response.append(line);
            }

            buf.close();

        } catch (MalformedURLException e) {
            Log.d(TAG, "mal:" + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG, "io: "+e.getMessage());
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return response.toString();
    }
}
