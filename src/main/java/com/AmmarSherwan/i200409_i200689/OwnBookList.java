package com.AmmarSherwan.i200409_i200689;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OwnBookList extends AppCompatActivity {

    ArrayList<BookRVModel> bookList;
    BookRVAdapter adapter;
    RecyclerView bookRV;
    FirebaseDatabase database;
    ImageButton back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.own_book_list);

        back = findViewById(R.id.back);
        bookRV = findViewById(R.id.own_book_rv);
        bookRV.setLayoutManager(new LinearLayoutManager(this));
        bookList = new ArrayList<BookRVModel>();
        adapter = new BookRVAdapter(this, bookList);
        bookRV.setAdapter(adapter);
        database = FirebaseDatabase.getInstance();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OwnBookList.this, BookList.class));
            }
        });
        loadBooks();

    }

    private void loadBooks() {
        database.getReference("tradeDB").child("booksList")
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        BookRVModel book = snapshot.getValue(BookRVModel.class);
                        Log.d("MSG", "Book Found In DB: " + book.getName());
                        if (book.getPoster().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                            bookList.add(book);
                            bookRV.getAdapter().notifyItemInserted(bookList.size());
                            bookRV.smoothScrollToPosition(bookList.size());
                        }
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
        bookList.clear();
        bookRV.getAdapter().notifyItemInserted(bookList.size());
        bookRV.smoothScrollToPosition(bookList.size());
    }

}
