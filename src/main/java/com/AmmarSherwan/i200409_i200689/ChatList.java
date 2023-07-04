package com.AmmarSherwan.i200409_i200689;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatList extends AppCompatActivity {


    ImageButton back;
    RecyclerView chatRV;
    ChatRVAdapter adapter;
    ArrayList<ChatRVModel> chatList;
    FirebaseAuth mAuth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_list);
        chatRV = findViewById(R.id.chat_list_rv);
        chatRV.setLayoutManager(new LinearLayoutManager(this));


        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        back = findViewById(R.id.back);
        chatList = new ArrayList<ChatRVModel>();

        adapter = new ChatRVAdapter(this, chatList);
        chatRV.setAdapter(adapter);




        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChatList.this, BookList.class));
            }
        });

        populateChats();

    }

    private boolean isChatAlreadyExist(MessageRVModel m) {
        for (int i = 0; i < chatList.size(); i++) {
            if (chatList.get(i).getEmail().equals(m.receiverEmail) || chatList.get(i).getEmail().equals(m.senderEmail)) {
                return true;
            }
        }
        return false;
    }

    private void populateChats() {

        database.getReference("tradeDB").child("messageList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    MessageRVModel m = snapshot.getValue(MessageRVModel.class);

                    if (!isChatAlreadyExist(m)) {
                        Log.d("MSG", "Message From DB: " + m.getMessage());
                        if (m.getReceiverEmail().equals(mAuth.getCurrentUser().getEmail())) {
                            m.setMessageType(MessageRVAdapter.MESSAGE_TYPE_IN);

                            ChatRVModel r = new ChatRVModel("", m.getSenderEmail(), null);
                            chatList.add(r);
                            chatRV.getAdapter().notifyItemInserted(chatList.size());
                            chatRV.smoothScrollToPosition(chatList.size());
                        }
                        else if (m.getSenderEmail().equals(mAuth.getCurrentUser().getEmail())) {
                            m.setMessageType(MessageRVAdapter.MESSAGE_TYPE_OUT);

                            ChatRVModel r = new ChatRVModel("", m.getReceiverEmail(), null);
                            chatList.add(r);
                            chatRV.getAdapter().notifyItemInserted(chatList.size());
                            chatRV.smoothScrollToPosition(chatList.size());
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
