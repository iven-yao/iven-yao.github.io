package com.example.androidteddy.viewholder;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.androidteddy.R;

import java.text.DecimalFormat;

public class FavoritesItemViewHolder extends RecyclerView.ViewHolder {
    private final TextView symbol;
    private final TextView companyName;
    private final TextView c;
    private final TextView d;
    private final TextView dp;
    private final ImageView ic;
    private final ImageView chev;
    private final DecimalFormat df = new DecimalFormat("0.00");
    private final View view;

    public FavoritesItemViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        symbol = itemView.findViewById(R.id.fav_symbol);
        companyName = itemView.findViewById(R.id.fav_companyName);
        c = itemView.findViewById(R.id.fav_c);
        d = itemView.findViewById(R.id.fav_d);
        dp = itemView.findViewById(R.id.fav_dp);
        ic = itemView.findViewById(R.id.fav_ic);
        chev = itemView.findViewById(R.id.fav_chevron_right);
    }

    public ImageView getChev() {
        return chev;
    }

    public void setSymbol(String val) {
        symbol.setTextColor(Color.BLACK);
        symbol.setText(val);
    }

    public void setC(Float val) {
        c.setTextColor(Color.BLACK);
        c.setText("$"+df.format(val));
    }

    public void setD(Float val) {
        d.setText("$"+ df.format(val)+" ");
    }

    public void setDP(Float val) {
        if(val > 0) {
            d.setTextColor(view.getContext().getColor(R.color.green));
            dp.setTextColor(view.getContext().getColor(R.color.green));
            ic.setImageResource(R.drawable.ic_baseline_trending_up_24);
        } else if( val < 0) {
            d.setTextColor(Color.RED);
            dp.setTextColor(Color.RED);
            ic.setImageResource(R.drawable.ic_baseline_trending_down_24);
        }
        dp.setText("( " + df.format(val) + "% )");
    }

    public void setCompanyName(String val) {
        companyName.setTextColor(Color.GRAY);
        companyName.setText(val);
    }
}
