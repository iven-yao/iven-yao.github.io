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
 * Use the {@link HourlyChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HourlyChartFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "querySymbol";
    private WebView chart_hourly;

    // TODO: Rename and change types of parameters
    private String querySymbol;

    public HourlyChartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment HourlyChartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HourlyChartFragment newInstance(String param1) {
        HourlyChartFragment fragment = new HourlyChartFragment();
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
        return inflater.inflate(R.layout.fragment_hourly_chart, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setChartHourly();
    }

    private void setChartHourly() {
        chart_hourly = getView().findViewById(R.id.chart_hourly);
        chart_hourly.getSettings().setJavaScriptEnabled(true);
        chart_hourly.loadUrl("file:///android_asset/chart_hourly.html");
        chart_hourly.addJavascriptInterface(new BackendHelper(), "BackendHelper");
        chart_hourly.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                chart_hourly.evaluateJavascript("javascript:generate('"+querySymbol+"')",null);
            }
        });
    }

}