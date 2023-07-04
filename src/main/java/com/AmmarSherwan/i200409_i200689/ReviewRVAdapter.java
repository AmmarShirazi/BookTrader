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
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ReviewRVAdapter extends RecyclerView.Adapter <ReviewRVAdapter.MyViewHolder> {
    Context context;
    ArrayList<ReviewRVModel> list;
    public ReviewRVAdapter(Context context, ArrayList<ReviewRVModel> list) {
        this.context=context;
        this.list=list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View row = LayoutInflater.from(context).inflate(R.layout.review_rv_row,parent,false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView")int position) {

        holder.name.setText(list.get(position).getPoster());
        holder.comment.setText(list.get(position).getComment());
        holder.rb.setRating(list.get(position).getRating());

        loadImage(list.get(position).getPosterImgUri(), holder);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name, comment;
        RatingBar rb;
        LinearLayout layout;
        ImageView userImg;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.reviewer);
            comment = itemView.findViewById(R.id.comment_txt);
            layout = itemView.findViewById(R.id.review_row_layout);
            rb = itemView.findViewById(R.id.rating_str);
            userImg = itemView.findViewById(R.id.reviewer_img);
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

                    holder.userImg.setMinimumHeight(dm.heightPixels);
                    holder.userImg.setMinimumWidth(dm.widthPixels);
                    holder.userImg.setImageBitmap(bm);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });

    }

}
