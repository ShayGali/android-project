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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidproject.model.User;
import com.example.androidproject.utilities.ChatRecyclerViewAdapter;
import com.example.androidproject.utilities.LoadingAlert;
import com.example.androidproject.R;
import com.example.androidproject.model.Message;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {
    private static final String DATABASE_MESSAGE_KEY = "message_rooms";
    private static final String DATABASE_USERS_KEY = "users";
    private static final String DATABASE_PARTICIPANTS_KEY = "participants";
    private static final String DATABASE_CHAT_NAME_KEY = "chat name";



    LoadingAlert loadingAlert = new LoadingAlert(this);


    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference messagesPerRoomRef; // reference to the path of the msg room
    DatabaseReference usersRef; // reference to the path of the users

    // all the messages of the chat
    ArrayList<Message> messages;

    // ID of the participants
    ArrayList<String> roomParticipantsID;

    // the data of the users
    Map<String, User> roomParticipants;


    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    ChatRecyclerViewAdapter adapter;

    EditText msgInput;
    TextView roomNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        String roomId = intent.getExtras().getString("roomID");

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        messagesPerRoomRef = database.getReference(DATABASE_MESSAGE_KEY).child(roomId);
        usersRef = database.getReference(DATABASE_USERS_KEY);

        messages = new ArrayList<>();
        roomParticipantsID = new ArrayList<>();
        roomParticipants = new LinkedHashMap<>();

        recyclerView = findViewById(R.id.messagesRecyclerView);

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new ChatRecyclerViewAdapter(messages, roomParticipants);

        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        msgInput = findViewById(R.id.enter_new_message_input);

        roomNameTextView = findViewById(R.id.room_name);

        loadingAlert.startLoadingDialog();
        getMessages();
    }


    void getMessages() {

        Thread thread = new Thread(() -> {
            messagesPerRoomRef.addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!messages.isEmpty()) {
                        messages.clear();
                    }

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        // אם הגעתי למשתתפים
                        if (Objects.equals(postSnapshot.getKey(), DATABASE_PARTICIPANTS_KEY)) {
                            roomParticipantsID = (ArrayList<String>) postSnapshot.getValue(); // מביאים את הid שלהם
                            getUsersByID(); // מביאים את המידע עליהם
                        } else if (Objects.equals(postSnapshot.getKey(), DATABASE_CHAT_NAME_KEY)) {
                            runOnUiThread(() -> roomNameTextView.setText(postSnapshot.getValue(String.class)));
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
            if (msgContent.equals("")) return;
            Message msg = new Message(msgContent, currentUser.getUid(), LocalDateTime.now());
            String timeKeyForDataBase = msg.getTimestamp();
            msg.setTimestamp(null);
            messagesPerRoomRef.child(timeKeyForDataBase).setValue(msg);
            // למחוק את מה שנשאר בתיבת טקסט
            // צריך למחוק אותו מ thread של ה UI
            runOnUiThread(() -> msgInput.setText(""));
        });
        thread.start();

    }

    /**
     *
     */
    public void getUsersByID() {
        Thread thread = new Thread(() -> {
            usersRef.addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (String userID : roomParticipantsID) {
                        if (snapshot.child(userID).exists())
                            roomParticipants.put(userID, snapshot.child(userID).getValue(User.class));
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
        thread.start();
    }

    /**
     * remove all messages from the chat
     * its over ride all data that in the room by saving only the participant id`s
     * add then save the the chat name
     */
    public void clearMessages() {

        Map<String, List<String>> roomParticipantsKeyValue = new LinkedHashMap<>();
        roomParticipantsKeyValue.put(DATABASE_PARTICIPANTS_KEY, roomParticipantsID);

        messagesPerRoomRef.setValue(roomParticipantsKeyValue);
        messagesPerRoomRef.child(DATABASE_CHAT_NAME_KEY).setValue(roomNameTextView.getText().toString());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.clear_chet) {
            clearMessages();
        }
        if (item.getItemId() == R.id.settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.log_out_item) {
            logout();
        }
        return true;
    }


    private void logout() {

        // TODO: צריך להתבצע בצורה אסינכרונית
        // TODO: כשהוא מתנתק להעביר אותו לדף חיבור
        AuthUI.getInstance().signOut(this).addOnCompleteListener(task -> {
            Toast.makeText(this, "You have logged-out", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
    }
}