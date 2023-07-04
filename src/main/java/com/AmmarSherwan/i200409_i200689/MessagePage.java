package com.AmmarSherwan.i200409_i200689;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MessagePage extends AppCompatActivity {

    ImageButton back;
    TextView email;
    RecyclerView messageRV;
    ArrayList<MessageRVModel> messageList;
    MessageRVAdapter adapter;
    EditText newMessageText;
    ImageButton send;
    String currBookID;
    FirebaseDatabase database;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_page);

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        back = findViewById(R.id.back);
        email = findViewById(R.id.chat_email);
        messageRV = findViewById(R.id.message_rv);
        newMessageText = findViewById(R.id.txt_msg);
        send = findViewById(R.id.btn_send);

        messageList = new ArrayList<MessageRVModel>();
        adapter = new MessageRVAdapter(this, messageList);
        messageRV.setLayoutManager(new LinearLayoutManager(this));
        messageRV.setAdapter(adapter);

        email.setText(getIntent().getStringExtra("posterEmail"));
        currBookID = getIntent().getStringExtra("bookID");


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currBookID.equals("none")) {
                    startActivity(new Intent(MessagePage.this, ChatList.class));
                }
                else {
                    Intent i = new Intent(MessagePage.this, BookPage.class);
                    i.putExtra("bookID", currBookID);
                    startActivity(i);
                }

            }
        });


        setMessageListener();

    }

    private void sendMessage() {

        if (TextUtils.isEmpty(newMessageText.getText().toString())) {
            newMessageText.setError("Type a message.");
            newMessageText.requestFocus();
            return;
        }
        String s = null;
        if (mAuth.getCurrentUser().getPhotoUrl() != null) {
            s = mAuth.getCurrentUser().getPhotoUrl().toString();
        }

        MessageRVModel m = new MessageRVModel("", 0, newMessageText.getText().toString(), mAuth.getCurrentUser().getEmail(), email.getText().toString(),s);

        DatabaseReference newPostRef = database.getReference("tradeDB").child("messageList").push();
        newPostRef.setValue(m);

        String postId = newPostRef.getKey();
        database.getReference("tradeDB").child("messageList").child(postId).child("id").setValue(postId);
        newMessageText.setText("");
    }

    boolean isMessageAlreadyExisting(String messageId) {

        for (int i = 0; i < messageList.size(); i++) {
            if (messageList.get(i).getId().equals(messageId)) {
                return true;
            }
        }

        return false;
    }

    private void setMessageListener() {


        database.getReference("tradeDB").child("messageList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    MessageRVModel m = snapshot.getValue(MessageRVModel.class);
                    if (!isMessageAlreadyExisting(m.getId())) {
                        Log.d("MSG", "Message From DB: " + m.getMessage());
                        if (m.getReceiverEmail().equals(mAuth.getCurrentUser().getEmail()) && m.getSenderEmail().equals(email.getText().toString())) {
                            m.setMessageType(MessageRVAdapter.MESSAGE_TYPE_IN);

                            messageList.add(m);
                            messageRV.getAdapter().notifyItemInserted(messageList.size());
                            messageRV.smoothScrollToPosition(messageList.size());
                        }
                        else if (m.getSenderEmail().equals(mAuth.getCurrentUser().getEmail()) && m.getReceiverEmail().equals(email.getText().toString())) {
                            m.setMessageType(MessageRVAdapter.MESSAGE_TYPE_OUT);
                            messageList.add(m);
                            messageRV.getAdapter().notifyItemInserted(messageList.size());
                            messageRV.smoothScrollToPosition(messageList.size());
                        }
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}
