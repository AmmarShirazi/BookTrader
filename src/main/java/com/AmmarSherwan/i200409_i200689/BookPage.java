package com.AmmarSherwan.i200409_i200689;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class BookPage extends AppCompatActivity {

    String currBookID;
    TextView name;
    TextView author;
    TextView category;
    TextView genre;
    TextView description;
    ImageButton back;
    ImageView bookImg;
    ImageButton message;
    Button review;
    Button delete;
    Button trade;
    FirebaseDatabase database;
    ReviewRVAdapter adapter;
    RecyclerView reviewRV;
    FirebaseAuth mAuth;
    BookRVModel currBook;
    ArrayList<ReviewRVModel> reviewList;
    EditText email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_page);

        Intent i = getIntent();
        currBookID = i.getStringExtra("bookID");

        email = findViewById(R.id.txt_receiver_email);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        name = findViewById(R.id.txt_book_name);
        author = findViewById(R.id.txt_book_author);
        category = findViewById(R.id.txt_book_category);
        genre = findViewById(R.id.txt_book_genre);
        description = findViewById(R.id.txt_book_desc);
        back = findViewById(R.id.back);
        review = findViewById(R.id.add_review_btn);
        delete = findViewById(R.id.delete_book_btn);
        bookImg = findViewById(R.id.book_img);
        message = findViewById(R.id.msg);
        reviewRV = findViewById(R.id.comment_rv);
        reviewRV.setLayoutManager(new LinearLayoutManager(this));
        reviewList = new ArrayList<ReviewRVModel>();
        adapter = new ReviewRVAdapter(this, reviewList);
        reviewRV.setAdapter(adapter);

        trade = findViewById(R.id.trade_book_btn);

        loadData();

        trade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToTradedBooks();

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCurrBook();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BookPage.this, BookList.class));
            }
        });

        review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ix = new Intent(BookPage.this, NewReview.class);
                ix.putExtra("bookID", currBookID);
                startActivity(ix);
            }
        });

    }

    private void addToTradedBooks() {

        if (TextUtils.isEmpty(email.getText().toString())) {
            email.setError("Receiver's email cannot be empty");
            email.requestFocus();
            return;
        }

        DatabaseReference newPostRef = database.getReference("tradeDB").child("tradedBookList").push();
        newPostRef.setValue(currBook);

        String postId = newPostRef.getKey();
        database.getReference("tradeDB").child("tradedBookList").child(postId).child("id").setValue(postId);


        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String strDate = dateFormat.format(date);


        FeedRVModel md = new FeedRVModel("", mAuth.getCurrentUser().getEmail(), currBook.getName(), email.getText().toString(), strDate);

        DatabaseReference newFeed = database.getReference("tradeDB").child("feedList").push();
        newFeed.setValue(md);

        String feedId = newFeed.getKey();
        database.getReference("tradeDB").child("feedList").child(feedId).child("id").setValue(feedId);

        sendNotification();

        deleteCurrBook();
        startActivity(new Intent(BookPage.this, BookList.class));

    }
    private void sendNotification() {


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "trade_book_notification")
                .setSmallIcon(R.drawable.ic_baseline_menu_book_24)
                .setContentTitle("Book Traded")
                .setContentText("Book Traded \"" + name.getText().toString() + "\"")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        createNotificationChannel();
        notificationManager.notify(2, builder.build());
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("trade_book_notification", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void deleteCurrBook() {

        database.getReference("tradeDB").child("booksList").child(currBookID).removeValue();

        startActivity(new Intent(BookPage.this, BookList.class));

    }

    private void loadData() {


        database.getReference("tradeDB").child("booksList").child(currBookID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                BookRVModel r = dataSnapshot.getValue(BookRVModel.class);
                currBook = r;
                name.setText(r.getName());
                author.setText(r.getAuthor());
                category.setText(r.getCategory());
                genre.setText(r.getGenre());
                description.setText(r.getDescription());
                if (r.getImgUri() != null) {
                    loadImage(r.getImgUri());
                }

                if (r.getPoster().equals(mAuth.getCurrentUser().getEmail())) {
                    delete.setVisibility(View.VISIBLE);
                    trade.setVisibility(View.VISIBLE);
                    email.setVisibility(View.VISIBLE);
                    review.setVisibility(View.GONE);
                }
                else {
                    message.setVisibility(View.VISIBLE);
                    message.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent ix = new Intent(BookPage.this, MessagePage.class);
                            ix.putExtra("posterEmail", r.getPoster());
                            ix.putExtra("bookID", currBookID);
                            startActivity(ix);
                        }
                    });
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });

        database.getReference("tradeDB").child("reviewList")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            ReviewRVModel rev = snapshot.getValue(ReviewRVModel.class);

                            if (rev.getBookID().equals(currBookID))  {
                                reviewList.add(rev);
                                reviewRV.getAdapter().notifyItemInserted(reviewList.size());
                                reviewRV.smoothScrollToPosition(reviewList.size());
                            }

                        }

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



    }


    private void loadImage(String iUri) {
        if (iUri == null) {
            return;
        }

        StorageReference mImageRef =
                FirebaseStorage.getInstance().getReferenceFromUrl(iUri);

        final long TEN_MEGABYTE = 1024 * 1024 * 10;

        mImageRef.getBytes(TEN_MEGABYTE)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        bm = getRoundedCroppedBitmap(bm);
                        DisplayMetrics dm = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(dm);

                        bookImg.setMinimumHeight(dm.heightPixels);
                        bookImg.setMinimumWidth(dm.widthPixels);
                        bookImg.setImageBitmap(bm);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });

    }



    private Bitmap getRoundedCroppedBitmap(Bitmap bitmap) {

        int widthLight = bitmap.getWidth();
        int heightLight = bitmap.getHeight();

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);
        Paint paintColor = new Paint();
        paintColor.setFlags(Paint.ANTI_ALIAS_FLAG);

        RectF rectF = new RectF(new Rect(0, 0, widthLight, heightLight));

        canvas.drawRoundRect(rectF, widthLight / 2, heightLight / 2, paintColor);

        Paint paintImage = new Paint();
        paintImage.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        canvas.drawBitmap(bitmap, 0, 0, paintImage);

        return output;
    }


}
