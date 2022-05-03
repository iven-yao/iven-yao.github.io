package com.example.androidteddy.section;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.androidteddy.viewholder.PortfolioItemViewHolder;
import com.example.androidteddy.R;
import com.example.androidteddy.SearchResultActivity;

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
                .itemResourceId(R.layout.item_portfolio)
                .build());
        itemList = new ArrayList<>();
    }

    public PortfolioSection(List<Map<String, Object>> list) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.item_portfolio)
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
        PortfolioItemViewHolder pHolder = (PortfolioItemViewHolder)holder;
        pHolder.setSymbol((String)itemList.get(position).get("symbol"));
        pHolder.setShare((String) itemList.get(position).get("share"));
        pHolder.setC((Float) itemList.get(position).get("c"));
        pHolder.setD((Float) itemList.get(position).get("d"));
        pHolder.setDP((Float) itemList.get(position).get("dp"));

        String query = (String)itemList.get(position).get("symbol");
        pHolder.getChev().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = pHolder.itemView.getContext();
                Intent intent = new Intent(context, SearchResultActivity.class);
                intent.putExtra("query",query.toUpperCase());
                context.startActivity(intent);
            }
        });
    }

}
