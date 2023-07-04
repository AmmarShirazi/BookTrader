package com.AmmarSherwan.i200409_i200689;

import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class FeedRVAdapter extends RecyclerView.Adapter <FeedRVAdapter.MyViewHolder> {
    Context context;
    ArrayList<FeedRVModel> list;
    public FeedRVAdapter(Context context, ArrayList<FeedRVModel> list ) {
        this.context=context;
        this.list=list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View row = LayoutInflater.from(context).inflate(R.layout.feed_rv_row,parent,false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView")int position) {
        String giver = list.get(position).getGiver();
        String book = list.get(position).getBookName();
        String taker = list.get(position).getTaker();

        holder.text.setText(giver + " gave " + book + " to " + taker + "!!");
        holder.date.setText(list.get(position).getDate());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView text, date;
        LinearLayout layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.feed_txt);
            date = itemView.findViewById(R.id.feed_date);
            layout = itemView.findViewById(R.id.feed_row_layout);
        }
    }

}
