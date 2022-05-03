package com.example.androidteddy.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.androidteddy.R;
import com.example.androidteddy.SearchResultActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    List<Map<String, String>> newsList;

    public NewsAdapter(List<Map<String, String>> list){
        newsList = list;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return 1;
        else return 2;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_layout_first, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_layout_general, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int index = position;
        Context context = holder.itemView.getContext();
        Glide.with(context).load(newsList.get(index).get("image_url")).placeholder(R.mipmap.stock_launcher_foreground).into(holder.getNews_image());
        holder.getNews_source().setText(newsList.get(index).get("source"));
        holder.getNews_title().setText(newsList.get(index).get("title"));
        holder.getNews_timeago().setText(getTimeagoString(newsList.get(index).get("datetime")));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "onClick: ");
                Dialog dialog_news = new Dialog(view.getContext());
                dialog_news.setContentView(R.layout.dialog_news);
                dialog_news.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog_news.getWindow().setLayout((int)(view.getResources().getDisplayMetrics().widthPixels*0.95),ViewGroup.LayoutParams.WRAP_CONTENT);
                TextView dialog_source = dialog_news.findViewById(R.id.dialog_source);
                TextView dialog_timeago = dialog_news.findViewById(R.id.dialog_timeago);
                TextView dialog_title = dialog_news.findViewById(R.id.dialog_title);
                TextView dialog_summary = dialog_news.findViewById(R.id.dialog_summary);
                ImageView chrome = dialog_news.findViewById(R.id.chrome_icon);
                ImageView twitter = dialog_news.findViewById(R.id.twitter_icon);
                ImageView facebook = dialog_news.findViewById(R.id.fb_icon);

                dialog_source.setText(newsList.get(index).get("source"));
                dialog_timeago.setText(getTimeagoString(newsList.get(index).get("datetime")));
                dialog_title.setText(newsList.get(index).get("title"));
                dialog_summary.setText(newsList.get(index).get("summary"));
                chrome.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String url = newsList.get(index).get("url");
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        intent.setData(Uri.parse(url));
                        view.getContext().startActivity(intent);
                    }
                });
                twitter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        StringBuilder url = new StringBuilder();
                        try {
                            url.append("https://twitter.com/intent/tweet?text=")
                                    .append(URLEncoder.encode("Check out this Link:","UTF-8"))
                                    .append("&url=")
                                    .append(URLEncoder.encode(newsList.get(index).get("url"),"UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        intent.setData(Uri.parse(url.toString()));
                        view.getContext().startActivity(intent);
                    }
                });

                facebook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        StringBuilder url = new StringBuilder();
                        try {
                            url.append("https://www.facebook.com/sharer/sharer.php?u=")
                                    .append(URLEncoder.encode(newsList.get(index).get("url"), "UTF-8"))
                                    .append("&src=sdkpreparse");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        intent.setData(Uri.parse(url.toString()));
                        view.getContext().startActivity(intent);
                    }
                });

                dialog_news.show();
            }
        });
    }

    public String getTimeagoString(String timeago) {
        String res ="";
        Date publishedTime = new Date(Long.parseLong(timeago)*1000);
        Date now = new Date();
        long diff = now.getTime() - publishedTime.getTime();
        int secDiff = (int) (diff/1000);
        int minDiff = (int) (diff/1000/60);
        int hourDiff = (int) (diff/1000/60/60);
        int dayDiff = (int) (diff/1000/60/60/24);
        if(dayDiff < 1) {
            if (hourDiff > 1) {
                res = String.format("%d hours ago", hourDiff);
            } else if (hourDiff == 1) {
                res = "1 hour ago";
            } else {
                if (minDiff > 1) {
                    res = String.format("%d minutes ago", minDiff);
                } else if (minDiff == 1) {
                    res = "1 minute ago";
                } else {
                    res = String.format("%d seconds ago", secDiff);
                }
            }
        } else if(dayDiff == 1) {
            res = "1 day ago";
        } else {
            res = String.format("%d days ago", dayDiff);
        }


        return res;
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView news_source;
        private final TextView news_title;
        private final TextView news_timeago;
        private final ImageView news_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            news_source = itemView.findViewById(R.id.news_source);
            news_timeago = itemView.findViewById(R.id.news_timeago);
            news_title = itemView.findViewById(R.id.news_title);
            news_image = itemView.findViewById(R.id.news_image);
        }

        public TextView getNews_source() {
            return news_source;
        }

        public TextView getNews_timeago() {
            return news_timeago;
        }

        public TextView getNews_title() {
            return news_title;
        }

        public ImageView getNews_image() {
            return news_image;
        }

    }

}
