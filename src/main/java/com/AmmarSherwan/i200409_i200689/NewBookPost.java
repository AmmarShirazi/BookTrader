package com.AmmarSherwan.i200409_i200689;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class NewBookPost extends AppCompatActivity {

    EditText name;
    EditText author;
    Spinner category;
    Spinner genre;
    EditText desc;
    ImageView bookImg;
    Button post;
    ImageButton back;
    FirebaseStorage storage;
    FirebaseDatabase database;
    FirebaseAuth mAuth;
    Uri imgUri;

    private String categoryStr;
    private String genreStr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_book_post);

        imgUri = null;
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        name = findViewById(R.id.txt_book_name);
        author = findViewById(R.id.txt_book_author);
        category = findViewById(R.id.category_spinner);
        genre = findViewById(R.id.genre_spinner);
        desc = findViewById(R.id.txt_book_desc);
        bookImg = findViewById(R.id.book_img);
        post = findViewById(R.id.post_book);
        back = findViewById(R.id.back);
        categoryStr = "Other";
        genreStr = "Other";

        Intent intentService = new Intent(NewBookPost.this, MusicServiceManager.class);
        startService(intentService);

        String[] categories = new String[] {"Novel", "Magazine", "Guidebook", "Mythology", "Other"};
        String[] genres = new String[] {"Fiction", "Horror", "Thriller", "Autobiography", "Mystery", "Fantasy", "Romance", "Comedy","Other"};

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        ArrayAdapter<String> genreAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, genres);

        category.setAdapter(categoryAdapter);
        genre.setAdapter(genreAdapter);
        category.setSelection(4);
        genre.setSelection(8);
        genre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Object item = adapterView.getItemAtPosition(i);


                categoryStr = item.toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Object item = adapterView.getItemAtPosition(i);

                genreStr = item.toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentService = new Intent(NewBookPost.this, MusicServiceManager.class);
                stopService(intentService);
                startActivity(new Intent(NewBookPost.this, BookList.class));
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postBook();

            }
        });
        bookImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100);

            }
        });
    }

    private void postBook() {

        if (TextUtils.isEmpty(name.getText().toString())) {
            name.setError("Name cannot be empty");
            name.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(author.getText().toString())) {
            author.setError("Author cannot be empty");
            author.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(desc.getText().toString())) {
            desc.setError("Description cannot be empty");
            desc.requestFocus();
            return;
        }
        String uriStr;
        if (imgUri == null) {
            uriStr = null;
        }
        else {
            uriStr = imgUri.toString();
        }
        BookRVModel p = new BookRVModel("", name.getText().toString(), categoryStr, genreStr, desc.getText().toString(), author.getText().toString(), uriStr, mAuth.getCurrentUser().getEmail());

        DatabaseReference newPostRef = database.getReference("tradeDB").child("booksList").push();
        newPostRef.setValue(p);

        String postId = newPostRef.getKey();
        database.getReference("tradeDB").child("booksList").child(postId).child("id").setValue(postId);
        Intent intentService = new Intent(NewBookPost.this, MusicServiceManager.class);
        stopService(intentService);
        sendNotification();

        startActivity(new Intent(NewBookPost.this, BookList.class));
    }
    private void sendNotification() {


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "add_book_notification")
                .setSmallIcon(R.drawable.ic_baseline_menu_book_24)
                .setContentTitle("New Book Posted")
                .setContentText("Good job you have posted a book \"" + name.getText().toString() + "\"")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        createNotificationChannel();
        notificationManager.notify(1, builder.build());
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("add_book_notification", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(resultCode==RESULT_OK)
        {

            Uri uri = (Uri)data.getData(); //save uri for further processing
            String img_uri = uri.toString();
            uploadImage(getImgName(getApplicationContext().getContentResolver(), uri), img_uri, uri);  //user defined function to upload image on Cloud storage
        }

    }

    private String getImgName(ContentResolver resolver, Uri uri) {
        Cursor returnCursor =
                resolver.query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
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

    private void uploadImage(String img_name, String img_path, Uri localUri) {

        // Create a reference to "mountains.jpg"
        StorageReference mountainsRef = storage.getReference().child("/book_images/" + img_name);

        // Create a reference to 'images/mountains.jpg'
        StorageReference mountainImagesRef = storage.getReference().child(img_path);

        // While the file names are the same, the references point to different files
        mountainsRef.getName().equals(mountainImagesRef.getName());    // true
        mountainsRef.getPath().equals(mountainImagesRef.getPath());

        mountainsRef.putFile(localUri)
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {
                    Task<Uri> resultTask = taskSnapshot.getStorage().getDownloadUrl();
                    resultTask.addOnSuccessListener(new OnSuccessListener<Uri>()
                    {
                        @Override
                        public void onSuccess(Uri uri)
                        {

                            imgUri = uri;
                            loadImage();
                            Toast.makeText(NewBookPost.this, "Uploaded", Toast.LENGTH_LONG).show();

                        }
                    }).addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Toast.makeText(NewBookPost.this, "Failed", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });

    }

    private void loadImage() {
        if (imgUri == null) {
            return;
        }

        StorageReference mImageRef =
                FirebaseStorage.getInstance().getReferenceFromUrl(imgUri.toString());

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

}