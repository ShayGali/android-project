package com.example.androidproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.example.androidproject.ChatRecyclerViewAdapter;
import com.example.androidproject.LoadingAlert;
import com.example.androidproject.R;
import com.example.androidproject.model.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    LoadingAlert loadingAlert = new LoadingAlert(this);


    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef; // reference to the path that the user data is save on the database

    ArrayList<String> roomParticipants;
    ArrayList<Message> messages;

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    ChatRecyclerViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        String roomId = intent.getExtras().getString("roomID");

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("message_rooms").child(roomId);

        messages = new ArrayList<>();

        recyclerView = findViewById(R.id.messagesRecyclerView);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new ChatRecyclerViewAdapter(messages);

        adapter = new ChatRecyclerViewAdapter(messages);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        loadingAlert.startLoadingDialog();
        getMessages();
    }


    void getMessages() {
        Thread thread = new Thread(() -> {
            myRef.addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        // אם הגעתי למשתתפים
                        if (Objects.equals(postSnapshot.getKey(), "participants")) {
                            roomParticipants = (ArrayList<String>) postSnapshot.getValue();

                        } else
                            messages.add(postSnapshot.getValue(Message.class));
                    }
                    adapter.notifyDataSetChanged();
                    loadingAlert.dismissDialog();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Failed to read value
                }
            });

        });
        thread.start();
    }

    void displayMessages() {

    }
}