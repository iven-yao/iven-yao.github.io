package com.example.androidteddy.callback;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

abstract public class DTRCallback extends ItemTouchHelper.SimpleCallback {
    List<Map<String, Object>> items;
    SectionedRecyclerViewAdapter sectionAdapter;

    public DTRCallback(int dragDirs, int swipeDirs, List<Map<String, Object>> items, SectionedRecyclerViewAdapter sectionAdapter) {
        super(dragDirs, swipeDirs);
        this.items = items;
        this.sectionAdapter = sectionAdapter;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    public List<Map<String, Object>> getItems() {
        return items;
    }

    public SectionedRecyclerViewAdapter getSectionAdapter() {
        return sectionAdapter;
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if(actionState == ItemTouchHelper.ACTION_STATE_DRAG){
            viewHolder.itemView.setAlpha(0.5f);
        }
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setAlpha(1.0f);
    }
}
