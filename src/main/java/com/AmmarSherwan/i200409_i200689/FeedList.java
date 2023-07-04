package com.AmmarSherwan.i200409_i200689;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FeedList extends AppCompatActivity {

    ArrayList<FeedRVModel> feedList;
    FeedRVAdapter adapter;
    RecyclerView feedRV;
    FirebaseDatabase database;
    ImageButton back;
    TextView header;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.own_book_list);

        header = findViewById(R.id.header);
        header.setText("TradeIt Feed");
        back = findViewById(R.id.back);
        feedRV = findViewById(R.id.own_book_rv);
        feedRV.setLayoutManager(new LinearLayoutManager(this));
        feedList = new ArrayList<FeedRVModel>();
        adapter = new FeedRVAdapter(this, feedList);
        feedRV.setAdapter(adapter);
        database = FirebaseDatabase.getInstance();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FeedList.this, BookList.class));
            }
        });
        loadFeed();

    }

    private void loadFeed() {
        database.getReference("tradeDB").child("feedList")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            FeedRVModel feed = snapshot.getValue(FeedRVModel.class);
                            feedList.add(feed);
                            feedRV.getAdapter().notifyItemInserted(feedList.size());
                            feedRV.smoothScrollToPosition(feedList.size());

                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    @Override
    public void onResume(){
        super.onResume();
        feedList.clear();
        feedRV.getAdapter().notifyItemInserted(feedList.size());
        feedRV.smoothScrollToPosition(feedList.size());
    }

}
