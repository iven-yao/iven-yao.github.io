package com.example.androidteddy;

import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class PortfolioSection extends Section {

    private static final String TAG = "TEDDY::";

    List<Map<String, Object>> itemList;

    public PortfolioSection() {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.portfolio_item)
                .build());
        itemList = new ArrayList<>();
    }

    public PortfolioSection(List<Map<String, Object>> list) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.portfolio_item)
                .build());
        itemList = list;
    }

    @Override
    public int getContentItemsTotal() {
        return itemList.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new PortfolioItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        PortfolioItemViewHolder itemView = (PortfolioItemViewHolder)holder;
        itemView.setSymbol((String)itemList.get(position).get("symbol"));
        itemView.setShare((String) itemList.get(position).get("share"));
        itemView.setC((Float) itemList.get(position).get("c"));
        itemView.setD((Float) itemList.get(position).get("d"));
        itemView.setDP((Float) itemList.get(position).get("dp"));
    }

}
