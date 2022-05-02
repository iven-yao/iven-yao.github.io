package com.example.androidteddy.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.androidteddy.BackendHelper;
import com.example.androidteddy.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoricalChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoricalChartFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String querySymbol;
    private WebView chart_historical;

    public HistoricalChartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment HistoricalChartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoricalChartFragment newInstance(String param1) {
        HistoricalChartFragment fragment = new HistoricalChartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            querySymbol = getArguments().getString(ARG_PARAM1);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_historical_chart, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setChartHistorical();
    }

    private void setChartHistorical() {
        chart_historical = getView().findViewById(R.id.chart_historical);
        chart_historical.getSettings().setJavaScriptEnabled(true);
        chart_historical.loadUrl("file:///android_asset/chart_historical.html");
        chart_historical.addJavascriptInterface(new BackendHelper(), "BackendHelper");
        chart_historical.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                chart_historical.evaluateJavascript("javascript:generate('"+querySymbol+"')",null);
            }
        });
    }
}