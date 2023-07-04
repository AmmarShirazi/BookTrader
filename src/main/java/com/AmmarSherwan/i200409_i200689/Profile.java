package com.AmmarSherwan.i200409_i200689;

import android.content.ContentResolver;
import android.content.Context;
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
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Profile extends AppCompatActivity {

    FirebaseAuth mAuth;
    Button logout;
    TextView emailField;
    Context mContext;
    ImageView userImage;
    TextView userNameField;
    FirebaseStorage storage;
    ImageButton back;
    Button home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        mContext = this;
        logout = findViewById(R.id.logout_btn);
        emailField = findViewById(R.id.txt_profile_email);

        back = findViewById(R.id.back);
        userImage = findViewById(R.id.profile_pic);
        userNameField = findViewById(R.id.txt_profile_username);
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        emailField.setText(mAuth.getCurrentUser().getEmail());
        userNameField.setText(mAuth.getCurrentUser().getDisplayName());
        home = findViewById(R.id.home_btn);

        loadImage();

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Profile.this, BookList.class));
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Profile.this, BookList.class));
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                startActivity(new Intent(mContext, SignIn.class));
            }
        });

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mAuth.getCurrentUser().getEmail().equals("guest@guest.com")) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100);
                }


            }
        });
    }

    private void loadImage() {
        if (mAuth.getCurrentUser().getPhotoUrl() == null) {
            return;
        }

        StorageReference mImageRef =
                FirebaseStorage.getInstance().getReferenceFromUrl(mAuth.getCurrentUser().getPhotoUrl().toString());

        final long TEN_MEGABYTE = 1024 * 1024 * 10;

        mImageRef.getBytes(TEN_MEGABYTE)
            .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    bm = getRoundedCroppedBitmap(bm);
                    DisplayMetrics dm = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(dm);

                    userImage.setMinimumHeight(dm.heightPixels);
                    userImage.setMinimumWidth(dm.widthPixels);
                    userImage.setImageBitmap(bm);
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
    private void uploadImage(String img_name, String img_path, Uri localUri) {

        // Create a reference to "mountains.jpg"
        StorageReference mountainsRef = storage.getReference().child("/profile_images/" + img_name);

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

                            FirebaseUser user = mAuth.getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(uri)
                                    .build();
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("MSG", "User profile updated.");
                                                loadImage();
                                            }
                                        }
                                    });
                            Toast.makeText(Profile.this, "Uploaded", Toast.LENGTH_LONG).show();

                        }
                    }).addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Toast.makeText(Profile.this, "Failed", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
    }

}
