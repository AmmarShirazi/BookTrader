package com.AmmarSherwan.i200409_i200689;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BookList extends AppCompatActivity {

    ArrayList<BookRVModel> bookList;
    BookRVAdapter adapter;
    RecyclerView bookRV;
    FirebaseDatabase database;
    ImageButton addBook;
    ImageButton messages;
    ImageButton ownBook;
    ImageButton profile;
    ImageButton traded;
    ImageButton feed;
    SearchView searchView;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_list_navigation);

        searchView=findViewById(R.id.search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.navbar);
        ActionBarDrawerToggle toggle= new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.OpenDrawer, R.string.CloseDrawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();

                if(id==R.id.optProfile) {
                    startActivity(new Intent(BookList.this, Profile.class));
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                else if (id==R.id.optFeed) {
                    startActivity(new Intent(BookList.this, FeedList.class));
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                else if (id==R.id.optAdd) {
                    if (!FirebaseAuth.getInstance().getCurrentUser().getEmail().equals("guest@guest.com")) {
                        startActivity(new Intent(BookList.this, NewBookPost.class));
                    }
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                else if (id==R.id.optChat) {
                    if (!FirebaseAuth.getInstance().getCurrentUser().getEmail().equals("guest@guest.com")) {
                        startActivity(new Intent(BookList.this, ChatList.class));
                    }
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                else if (id==R.id.optHistory) {
                    startActivity(new Intent(BookList.this, OwnBookList.class));
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                else if (id==R.id.optTraded) {
                    startActivity(new Intent(BookList.this, TradedBookList.class));
                    drawerLayout.closeDrawer(GravityCompat.START);
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        bookRV = findViewById(R.id.book_rv);
        bookRV.setLayoutManager(new LinearLayoutManager(this));
        bookList = new ArrayList<BookRVModel>();
        adapter = new BookRVAdapter(this, bookList);
        bookRV.setAdapter(adapter);
        messages = findViewById(R.id.msg_btn);
        ownBook = findViewById(R.id.own_books_btn);
        profile = findViewById(R.id.profile_btn);
        addBook = findViewById(R.id.add_book);
        database = FirebaseDatabase.getInstance();
        traded = findViewById(R.id.traded_btn);
        feed = findViewById(R.id.feed_show_btn);

        feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BookList.this, FeedList.class));
            }
        });

        traded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BookList.this, TradedBookList.class));
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BookList.this, Profile.class));
            }
        });

        ownBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BookList.this, OwnBookList.class));
            }
        });

        messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!FirebaseAuth.getInstance().getCurrentUser().getEmail().equals("guest@guest.com")) {
                    startActivity(new Intent(BookList.this, ChatList.class));
                }
            }
        });

        addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!FirebaseAuth.getInstance().getCurrentUser().getEmail().equals("guest@guest.com")) {
                    startActivity(new Intent(BookList.this, NewBookPost.class));
                }

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
                            bookList.add(book);
                            bookRV.getAdapter().notifyItemInserted(bookList.size());
                            bookRV.smoothScrollToPosition(bookList.size());
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
    private void filterList(String newText) {
        ArrayList<BookRVModel> filteredList=new ArrayList<>();
        for(BookRVModel model:bookList){
            if(model.getName().toLowerCase().contains(newText.toLowerCase())){
                filteredList.add(model);
            }
            else if (model.getCategory().toLowerCase().contains(newText.toLowerCase())) {
                filteredList.add(model);
            }
            else if (model.getGenre().toLowerCase().contains(newText.toLowerCase())) {
                filteredList.add(model);
            }
            else if (model.getAuthor().toLowerCase().contains(newText.toLowerCase())) {
                filteredList.add(model);
            }

            if(!filteredList.isEmpty()) {
                adapter.setFilteredList(filteredList);
            }
        }

    }
}
