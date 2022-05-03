package com.example.androidteddy.section;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.androidteddy.SearchResultActivity;
import com.example.androidteddy.viewholder.FavoritesItemViewHolder;
import com.example.androidteddy.R;

import java.util.List;
import java.util.Map;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class FavoritesSection extends Section {

    List<Map<String, Object>> itemList;

    public FavoritesSection() {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.item_favorites)
                .build());
    }

    public FavoritesSection(List<Map<String, Object>> list) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.item_favorites)
                .build());
        itemList = list;
    }

    public List<Map<String, Object>> getData(){
        return itemList;
    }

    @Override
    public int getContentItemsTotal() {
        return itemList.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new FavoritesItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        FavoritesItemViewHolder fHolder = (FavoritesItemViewHolder) holder;
        fHolder.setSymbol((String) itemList.get(position).get("symbol"));
        fHolder.setCompanyName((String) itemList.get(position).get("companyName"));
        fHolder.setC((Float) itemList.get(position).get("c"));
        fHolder.setD((Float) itemList.get(position).get("d"));
        fHolder.setDP((Float) itemList.get(position).get("dp"));

        String query = (String)itemList.get(position).get("symbol");
        fHolder.getChev().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = fHolder.itemView.getContext();
                Intent intent = new Intent(context, SearchResultActivity.class);
                intent.putExtra("query",query.toUpperCase());
                context.startActivity(intent);
            }
        });
    }


}
