package com.example.androidteddy.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.androidteddy.fragment.HistoricalChartFragment;
import com.example.androidteddy.fragment.HourlyChartFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private final String querySymbol;
    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, String symbol) {
        super(fragmentManager, lifecycle);
        querySymbol = symbol;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position == 0) return HourlyChartFragment.newInstance(querySymbol);
        return HistoricalChartFragment.newInstance(querySymbol);
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
