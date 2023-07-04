package com.AmmarSherwan.i200409_i200689;

import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class BookRVAdapter extends RecyclerView.Adapter <BookRVAdapter.MyViewHolder> {
    Context context;
    ArrayList<BookRVModel> list;
    public BookRVAdapter(Context context, ArrayList<BookRVModel> list ) {
        this.context=context;
        this.list=list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View row = LayoutInflater.from(context).inflate(R.layout.book_rv_row,parent,false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView")int position) {

        holder.name.setText(list.get(position).getName());
        holder.category.setText(list.get(position).getCategory());
        holder.genre.setText(list.get(position).getGenre());
        Log.d("MSG", "Book RV IMG: " + list.get(position).getImgUri());

        loadImage(list.get(position).getImgUri(), holder);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!FirebaseAuth.getInstance().getCurrentUser().getEmail().equals("guest@guest.com")) {
                    Intent i = new Intent(context, BookPage.class);
                    i.putExtra("bookID",list.get(position).getId());
                    context.startActivity(i);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name, category,genre;
        LinearLayout layout;
        ImageView bookImg;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.book_name);
            category = itemView.findViewById(R.id.book_category);
            genre = itemView.findViewById(R.id.book_genre);
            layout = itemView.findViewById(R.id.row_layout);
            bookImg = itemView.findViewById(R.id.book_img_rv);
        }
    }

    private void loadImage(String imgUri, @NonNull MyViewHolder holder) {
        if (imgUri == null) {
            return;
        }

        StorageReference mImageRef =
                FirebaseStorage.getInstance().getReferenceFromUrl(imgUri);

        final long TEN_MEGABYTE = 1024 * 1024 * 10;

        mImageRef.getBytes(TEN_MEGABYTE)
            .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    DisplayMetrics dm = new DisplayMetrics();
                    ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);

                    holder.bookImg.setMinimumHeight(dm.heightPixels);
                    holder.bookImg.setMinimumWidth(dm.widthPixels);
                    holder.bookImg.setImageBitmap(bm);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });

    }
    public void setFilteredList(ArrayList<BookRVModel> filteredList){
        this.list=filteredList;
        notifyDataSetChanged();
    }

}
