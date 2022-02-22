package com.example.androidproject.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private static final String DATABASE_ROOMS_KEY = "message_rooms";
    private static final String DATABASE_USERS_KEY = "users";
    private static final String DATABASE_PARTICIPANTS_KEY = "participants";
    private static final String DATABASE_CHAT_NAME_KEY = "chat name";
    private static final String DATABASE_MESSAGES_KEY = "messages";


    LoadingAlert loadingAlert = new LoadingAlert(this);


    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference roomRef; // reference to the path of the msg room
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

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        String roomId = intent.getExtras().getString("roomID");

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        roomRef = database.getReference(DATABASE_ROOMS_KEY).child(roomId);
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
        dividerItemDecoration.setDrawable(getDrawable(R.drawable.divider_line));
        recyclerView.addItemDecoration(dividerItemDecoration);

        msgInput = findViewById(R.id.enter_new_message_input);

        roomNameTextView = findViewById(R.id.room_name);

        loadingAlert.startLoadingDialog();

        getMessages();
        getParticipantsIDs();
        getChatName();
    }


    void getParticipantsIDs(){
        roomRef.child(DATABASE_PARTICIPANTS_KEY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    roomParticipantsID.add(postSnapshot.getValue(String.class));
                }
                getUsersDataByID();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void getChatName(){
        roomRef.child(DATABASE_CHAT_NAME_KEY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                runOnUiThread(() -> roomNameTextView.setText(snapshot.getValue(String.class)));
                loadingAlert.dismissDialog();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    void getMessages() {

        roomRef.child(DATABASE_MESSAGES_KEY).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Message msg = postSnapshot.getValue(Message.class);
                    assert msg != null;
                    msg.setID(postSnapshot.getKey());
                    messages.add(msg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        roomRef.child(DATABASE_MESSAGES_KEY).addChildEventListener(new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                MainActivity.showToastFromThread(ChatActivity.this, "add");

                Message msg = snapshot.getValue(Message.class);
                assert msg != null;
                msg.setID(snapshot.getKey());
                messages.add(msg);
                try {
                    recyclerView.scrollToPosition(messages.size());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                messages.clear();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                MainActivity.showToastFromThread(ChatActivity.this, "onCancelled");

            }
        });

    }


    public void send_msg(View view) {
        Thread thread = new Thread(() -> {
            String msgContent = msgInput.getText().toString();
            if (msgContent.equals("")) return;
            Message msg = new Message(msgContent, currentUser.getUid(), LocalDateTime.now());
            roomRef.child(DATABASE_MESSAGES_KEY).push().setValue(msg);
            // למחוק את מה שנשאר בתיבת טקסט
            // צריך למחוק אותו מ thread של ה UI
            runOnUiThread(() -> msgInput.setText(""));
        });
        thread.start();

    }

    /**
     *
     */
    public void getUsersDataByID() {
        for (String userId : roomParticipantsID){
            usersRef.child(userId).addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    roomParticipants.put(userId, snapshot.getValue(User.class));
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    /**
     * remove all messages from the chat
     * its over ride all data that in the room by saving only the participant id`s
     * add then save the the chat name
     */
    public void clearMessages() {
        roomRef.child(DATABASE_MESSAGES_KEY).removeValue();
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