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

public class ChatRVAdapter extends RecyclerView.Adapter <ChatRVAdapter.MyViewHolder> {
    Context context;
    ArrayList<ChatRVModel> list;
    public ChatRVAdapter(Context context, ArrayList<ChatRVModel> list ) {
        this.context=context;
        this.list=list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View row = LayoutInflater.from(context).inflate(R.layout.chat_rv_row,parent,false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView")int position) {

        holder.email.setText(list.get(position).getEmail());
        Log.d("MSG", "Chat RV IMG: " + list.get(position).getImgUri());

        loadImage(list.get(position).getImgUri(), holder);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!FirebaseAuth.getInstance().getCurrentUser().getEmail().equals("guest@guest.com")) {
                    Intent i = new Intent(context, MessagePage.class);
                    i.putExtra("bookID", "none");
                    i.putExtra("posterEmail", list.get(position).getEmail()); // poster is actual the other person's email i just named it rlly bad
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

        TextView email;
        LinearLayout layout;
        ImageView userImg;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            email = itemView.findViewById(R.id.user_email);
            layout = itemView.findViewById(R.id.chat_row_layout);
            userImg = itemView.findViewById(R.id.user_img_rv);
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
