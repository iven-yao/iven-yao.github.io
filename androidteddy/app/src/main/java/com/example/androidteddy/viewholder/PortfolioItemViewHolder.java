package com.example.androidteddy.viewholder;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.androidteddy.R;

import java.text.DecimalFormat;

public class PortfolioItemViewHolder extends RecyclerView.ViewHolder {
    private final TextView symbol;
    private final TextView share;
    private final TextView c;
    private final TextView d;
    private final TextView dp;
    private final ImageView trending;
    private final ImageView chev;
    private final DecimalFormat df = new DecimalFormat("0.00");
    private final View view;

    public PortfolioItemViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        symbol = itemView.findViewById(R.id.portfolio_symbol);
        share = itemView.findViewById(R.id.portfolio_share);
        c = itemView.findViewById(R.id.portfolio_c);
        d = itemView.findViewById(R.id.portfolio_d);
        dp = itemView.findViewById(R.id.portfolio_dp);
        trending = itemView.findViewById(R.id.portfolio_trending);
        chev = itemView.findViewById(R.id.portfolio_chevron_right);
    }

    public ImageView getChev() {
        return chev;
    }

    public void setSymbol(String val) {
        symbol.setTextColor(Color.BLACK);
        symbol.setText(val);
    }

    public void setShare(String val) {
        share.setText(val);
    }

    public void setC(Float val) {
        c.setTextColor(Color.BLACK);
        c.setText("$"+df.format(val));
    }

    public void setD(Float val) {
        d.setText("$"+ df.format(val)+" ");
    }

    public void setDP(Float val) {
        if(val > 0.005) {
            d.setTextColor(view.getContext().getColor(R.color.green));
            dp.setTextColor(view.getContext().getColor(R.color.green));
            trending.setImageResource(R.drawable.ic_baseline_trending_up_24);
        } else if( val < -0.005) {
            d.setTextColor(Color.RED);
            dp.setTextColor(Color.RED);
            trending.setImageResource(R.drawable.ic_baseline_trending_down_24);
        }
        dp.setText("( "+df.format(val)+"% )");
    }
}
