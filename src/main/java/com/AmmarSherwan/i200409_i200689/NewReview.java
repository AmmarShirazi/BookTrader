package com.AmmarSherwan.i200409_i200689;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewReview extends AppCompatActivity {


    RatingBar rating;
    EditText comment;
    Button post;
    ImageButton back;
    FirebaseDatabase database;
    String bookID;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_review);

        Intent i = getIntent();
        bookID = i.getStringExtra("bookID");
        Log.d("MSG",  "Review Book ID: " + bookID);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        back = findViewById(R.id.back);
        post = findViewById(R.id.post_review);
        rating = findViewById(R.id.rating_stars);
        comment = findViewById(R.id.txt_comment);
        rating.setRating(0);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ix = new Intent(NewReview.this, BookPage.class);
                ix.putExtra("bookID", bookID);
                startActivity(ix);
            }
        });



        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postReview();
            }
        });
    }


    private void postReview() {

        if (TextUtils.isEmpty(comment.getText().toString())) {
            comment.setError("Comment cannot be empty");
            comment.requestFocus();
            return;
        }

        float r =  rating.getRating();

        ReviewRVModel rModel = new ReviewRVModel("", r, comment.getText().toString(), bookID, mAuth.getCurrentUser().getEmail(), mAuth.getCurrentUser().getPhotoUrl().toString());

        DatabaseReference newPostRef = database.getReference("tradeDB").child("reviewList").push();
        newPostRef.setValue(rModel);

        String postId = newPostRef.getKey();
        database.getReference("tradeDB").child("reviewList").child(postId).child("id").setValue(postId);
        Intent ix = new Intent(NewReview.this, BookPage.class);
        ix.putExtra("bookID", bookID);
        startActivity(ix);

    }
}
