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
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.androidproject.utilities.ChatRecyclerViewAdapter;
import com.example.androidproject.utilities.LoadingAlert;
import com.example.androidproject.R;
import com.example.androidproject.model.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {
    private static final String DATABASE_FIRST_CHILD_PATH = "message_rooms";

    LoadingAlert loadingAlert = new LoadingAlert(this);


    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef; // reference to the path of the msg room

    ArrayList<String> roomParticipants;
    ArrayList<Message> messages;

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    ChatRecyclerViewAdapter adapter;


    EditText msgInput;
    TextView roomTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        String roomId = intent.getExtras().getString("roomID");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(DATABASE_FIRST_CHILD_PATH).child(roomId);

        messages = new ArrayList<>();

        recyclerView = findViewById(R.id.messagesRecyclerView);

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new ChatRecyclerViewAdapter(messages);

        adapter = new ChatRecyclerViewAdapter(messages);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        msgInput = findViewById(R.id.enter_new_message_input);

        roomTitle = findViewById(R.id.room_title);
        roomTitle.setText("room name");

        loadingAlert.startLoadingDialog();
        getMessages();
    }


    void getMessages() {
        Thread thread = new Thread(() -> {
            myRef.addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(!messages.isEmpty()){
                        messages.clear();
                    }

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        // אם הגעתי למשתתפים
                        if (Objects.equals(postSnapshot.getKey(), "participants")) {
                            roomParticipants = (ArrayList<String>) postSnapshot.getValue();

                        } else {
                            Message msg = postSnapshot.getValue(Message.class);
                            assert msg != null;
                            msg.setTimestamp(postSnapshot.getKey());
                            messages.add(msg);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(messages.size() - 1);

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


    public void send_msg(View view) {
        Thread thread = new Thread(() -> {
            String msgContent = msgInput.getText().toString();
            Message msg = new Message(msgContent, currentUser.getUid(), LocalDateTime.now());
            String timeKeyForDataBase = msg.getTimestamp();
            msg.setTimestamp(null);
            myRef.child(timeKeyForDataBase).setValue(msg);
            // למחוק את מה שנשאר בתיבת טקסט
            // צריך למחוק אותו מ thread של ה UI
            runOnUiThread(()-> msgInput.setText(""));
        });
        thread.start();

    }
}