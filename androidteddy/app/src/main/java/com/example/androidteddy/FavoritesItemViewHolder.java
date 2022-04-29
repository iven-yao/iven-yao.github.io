package com.example.androidteddy;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;

public class FavoritesItemViewHolder extends RecyclerView.ViewHolder {
    private final TextView symbol;
    private final TextView companyName;
    private final TextView c;
    private final TextView d;
    private final TextView dp;
    private final ImageView ic;
    private final DecimalFormat df = new DecimalFormat("0.00");

    public FavoritesItemViewHolder(View itemView) {
        super(itemView);
        symbol = itemView.findViewById(R.id.fav_symbol);
        companyName = itemView.findViewById(R.id.fav_companyName);
        c = itemView.findViewById(R.id.fav_c);
        d = itemView.findViewById(R.id.fav_d);
        dp = itemView.findViewById(R.id.fav_dp);
        ic = itemView.findViewById(R.id.fav_ic);
    }

    public void setSymbol(String val) {
        symbol.setTextColor(Color.BLACK);
        symbol.setText(val);
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
            ic.setImageResource(R.drawable.ic_baseline_trending_up_24);
        } else if( val < 0) {
            d.setTextColor(Color.RED);
            dp.setTextColor(Color.RED);
            ic.setImageResource(R.drawable.ic_baseline_trending_down_24);
        }
        dp.setText("(" + df.format(val) + ")");
    }

    public void setCompanyName(String val) {
        companyName.setTextColor(Color.GRAY);
        companyName.setText(val);
    }
}
