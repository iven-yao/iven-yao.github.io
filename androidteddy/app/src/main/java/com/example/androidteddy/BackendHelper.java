package com.example.androidteddy;

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
    private static final String EARNINGS = "/api/earning?";
    private static URL url;

    public static String getProfile2(String symbol) {
        Map<String, String> payload = new HashMap<>();
        payload.put("symbol",symbol);
        return connect(PROFILE2, payload);
    }

    public static String getCandle(String symbol, String res, String from, String to) {
        Map<String, String> payload = new HashMap<>();
        payload.put("symbol", symbol);
        payload.put("resolution", res);
        payload.put("from", from);
        payload.put("to", to);
        return connect(CANDLE, payload);
    }

    public static String getQuote(String symbol) {
        Map<String, String> payload = new HashMap<>();
        payload.put("symbol",symbol);
        return connect(QUOTE, payload);
    }

    public static String getSearch(String q) {
        Map<String, String> payload = new HashMap<>();
        payload.put("q",q);
        return connect(SEARCH, payload);
    }

    public static String getNews(String symbol) {
        Map<String, String> payload = new HashMap<>();
        payload.put("symbol",symbol);
        return connect(NEWS, payload);
    }

    public static String getTrend(String symbol) {
        Map<String, String> payload = new HashMap<>();
        payload.put("symbol",symbol);
        return connect(TREND, payload);
    }

    public static String getSocial(String symbol) {
        Map<String, String> payload = new HashMap<>();
        payload.put("symbol",symbol);
        return connect(SOCIAL, payload);
    }

    public static String getPeers(String symbol) {
        Map<String, String> payload = new HashMap<>();
        payload.put("symbol",symbol);
        return connect(PEERS, payload);
    }

    public static String getEarnings(String symbol) {
        Map<String, String> payload = new HashMap<>();
        payload.put("symbol",symbol);
        return connect(EARNINGS, payload);
    }

    private static String connect(String api, Map<String, String> payload) {
        BufferedReader buf;
        String line;
        StringBuilder response = new StringBuilder();
        String responseJSON = null;
        try {
            StringBuilder builder = new StringBuilder(HOSTNAME);
            builder.append(api);
            for(String key: payload.keySet()) {
                builder.append(key);
                builder.append("=");
                builder.append(payload.get(key));
            }
            url = new URL(builder.toString());
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

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

//            responseJSON = response.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return response.toString();
    }
}
