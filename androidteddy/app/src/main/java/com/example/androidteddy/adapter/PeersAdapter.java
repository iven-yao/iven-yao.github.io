package com.example.androidteddy.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidteddy.R;
import com.example.androidteddy.SearchResultActivity;

import java.util.List;

public class PeersAdapter extends RecyclerView.Adapter<PeersAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView peer_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            peer_name = itemView.findViewById(R.id.peer_name);
        }

        public TextView getPeer_name() {
            return peer_name;
        }
    }

    private List<String> peers;

    public PeersAdapter(List<String> list) {
        peers = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_peer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String peer = peers.get(position);
        holder.getPeer_name().setText(Html.fromHtml("<a href='#'>"+peer+(position==getItemCount()-1?"":","+"</a>"), Html.FROM_HTML_MODE_COMPACT));
        Context context = holder.itemView.getContext();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SearchResultActivity.class);
                intent.putExtra("query",peer.toUpperCase());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return peers.size();
    }
}
