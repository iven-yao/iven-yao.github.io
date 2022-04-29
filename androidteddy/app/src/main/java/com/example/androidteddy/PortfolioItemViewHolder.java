package com.example.androidteddy;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;

public class PortfolioItemViewHolder extends RecyclerView.ViewHolder {
    private final TextView symbol;
    private final TextView share;
    private final TextView c;
    private final TextView d;
    private final TextView dp;
    private final ImageView trending;
    private final DecimalFormat df = new DecimalFormat("0.00");

    public PortfolioItemViewHolder(View itemView) {
        super(itemView);
        symbol = itemView.findViewById(R.id.portfolio_symbol);
        share = itemView.findViewById(R.id.portfolio_share);
        c = itemView.findViewById(R.id.portfolio_c);
        d = itemView.findViewById(R.id.portfolio_d);
        dp = itemView.findViewById(R.id.portfolio_dp);
        trending = itemView.findViewById(R.id.portfolio_trending);
    }

    public void setSymbol(String val) {
        symbol.setTextColor(Color.BLACK);
        symbol.setText(val);
    }

    public void setShare(String val) {
        share.setText(val);
    }

    public void setC(Double val) {
        c.setTextColor(Color.BLACK);
        c.setText("$"+df.format(val));
    }

    public void setD(Double val) {
        d.setText("$"+ df.format(val)+" ");
    }

    public void setDP(Double val) {
        if(val > 0) {
            d.setTextColor(Color.GREEN);
            dp.setTextColor(Color.GREEN);
            trending.setImageResource(R.drawable.ic_baseline_trending_up_24);
        } else if( val < 0) {
            d.setTextColor(Color.RED);
            dp.setTextColor(Color.RED);
            trending.setImageResource(R.drawable.ic_baseline_trending_down_24);
        }
        dp.setText("("+df.format(val)+")");
    }
}
