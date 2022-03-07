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

import com.example.androidproject.model.User;
import com.example.androidproject.adapters.ChatRecyclerViewAdapter;
import com.example.androidproject.dialogs.LoadingAlert;
import com.example.androidproject.R;
import com.example.androidproject.model.Message;
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

    // כל מה שקשור לרשימה שמוצגת
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    ChatRecyclerViewAdapter adapter;


    EditText msgInput; // איפה ששולחים את ההודעה
    TextView roomNameTextView; // השם של הצאט שמוצג

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // the intent that we gat from the ChatsRoomsActivity
        // we gat the roomID
        Intent intent = getIntent();
        String roomId = intent.getExtras().getString("roomID"); // TODO: make the "roomID" static variable on ChatsRoomsActivity

        // מאתחלים את על המשתנים שקשורים לפיירבייס
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        roomRef = database.getReference(DATABASE_ROOMS_KEY).child(roomId);
        usersRef = database.getReference(DATABASE_USERS_KEY);

        // the massage list
        messages = new ArrayList<>();
        // the ID`s of the room participants
        roomParticipantsID = new ArrayList<>();
        // the user data of the room participants in map of <String -> userID, User -> dataOnTheUser>
        roomParticipants = new LinkedHashMap<>();

        // get the recyclerView from the layout
        recyclerView = findViewById(R.id.messagesRecyclerView);

        //set the layoutManager
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // start from the bottom
        // set the layoutManager to the recyclerView
        recyclerView.setLayoutManager(layoutManager);

        // the animation when scrolling
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // set the adapter with the data
        adapter = new ChatRecyclerViewAdapter(messages, roomParticipants);

        // set the adapter to the recyclerView
        recyclerView.setAdapter(adapter);

        // add divider between the recyclerView items
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        dividerItemDecoration.setDrawable(getDrawable(R.drawable.divider_line)); // the drawable file
        recyclerView.addItemDecoration(dividerItemDecoration);

        // the message input field
        msgInput = findViewById(R.id.enter_new_message_input);

        roomNameTextView = findViewById(R.id.room_name);

        // start the loading dialog
        loadingAlert.startLoadingDialog();

        // get the data that we need from the database
        getMessages();
        getParticipantsIDs();
        getChatName();
    }


    /**
     * get the ID`s of the room paticipants
     * when we end getting the data we get all of the data of the users
     */
    void getParticipantsIDs() {
        roomRef.child(DATABASE_PARTICIPANTS_KEY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    roomParticipantsID.add(postSnapshot.getValue(String.class));
                }
                getUsersDataByID();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * get the users data by they ID`s.
     * put the data in a map <String -> userID, User -> dataOnTheUser>.
     */
    public void getUsersDataByID() {
        for (String userId : roomParticipantsID) {
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
     * getting the chat name from the database and display it to the screen
     */
    void getChatName() {
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

    /**
     * at the beginning of the function we receive all messages that have already been sent
     * after we add listener to get new messages or delete then
     */
    void getMessages() {
        // get the messages
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
                recyclerView.scrollToPosition(messages.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // add listener to the child
        roomRef.child(DATABASE_MESSAGES_KEY).addChildEventListener(new ChildEventListener() {

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message msg = snapshot.getValue(Message.class);
                assert msg != null;
                msg.setID(snapshot.getKey());
                messages.add(msg);
                recyclerView.scrollToPosition(messages.size() - 1);

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                messages.clear();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }


    /**
     * send new message to the room.
     * run asynchronously.
     * clear the message input
     *
     * @param view the send button
     */
    public void send_msg(View view) {
        Thread thread = new Thread(() -> {
            String msgContent = msgInput.getText().toString();
            if (msgContent.trim().equals("")) return;
            Message msg = new Message(msgContent, currentUser.getUid(), LocalDateTime.now());
            roomRef.child(DATABASE_MESSAGES_KEY).push().setValue(msg);
            // למחוק את מה שנשאר בתיבת טקסט
            // צריך למחוק אותו מ thread של ה UI
            runOnUiThread(() -> msgInput.setText(""));
        });
        thread.start();

    }


    /**
     * remove all messages from the chat.
     * its over ride all data that in the room by saving only the participant id`s.
     * add then save the the chat name.
     */
    public void clearMessages() {
        roomRef.child(DATABASE_MESSAGES_KEY).removeValue();
    }

    /**
     * gets a reference for a menu and declares the menu logic
     * onCreate will call it
     *
     * @param menu a reference in runtime for the activity's menu bar
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    /**
     * what happen when the user press on item in the list
     *
     * @param item each item on the menu
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.clear_chet) {
            clearMessages();
        }
        if (item.getItemId() == R.id.settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return true;
    }

}