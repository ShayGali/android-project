package com.example.androidproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import com.example.androidproject.R;
import com.example.androidproject.model.User;
import com.example.androidproject.utilities.FriendsNameRecyclerViewAdapter;
import com.example.androidproject.utilities.LoadingAlert;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UploadUserFriendsActivity extends AppCompatActivity {

    private final String DATABASE_USERS_KEY = "users";
    public final FirebaseDatabase DATABASE = FirebaseDatabase.getInstance();
    public final DatabaseReference USERS_REF = DATABASE.getReference(DATABASE_USERS_KEY);

    LoadingAlert loadingAlert = new LoadingAlert(this);

    FirebaseDatabase database;
    DatabaseReference userRef;
    DatabaseReference usersRef;
    FirebaseUser currentUser;
    User userModel; // the user model

    RecyclerView recyclerView;
    ArrayList<User> friends;

    FriendsNameRecyclerViewAdapter adapter;
    LinearLayoutManager layoutManager;


    public Map<String, User> userMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_user_friends);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("users").child(currentUser.getUid());
        usersRef = database.getReference("users");


        friends = new ArrayList<>();


        recyclerView = findViewById(R.id.recyclerView_user_friends);
        adapter = new FriendsNameRecyclerViewAdapter(friends);
        recyclerView.setAdapter(adapter);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        loadingAlert.startLoadingDialog();
        Thread thread = new Thread(this::getUserFriends);
        thread.start();


    }

    void getUserFriends() {
        userRef.child("friends").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friends.clear();
                adapter.notifyDataSetChanged();
                if (snapshot.exists()) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        String friendID = postSnapshot.getValue(String.class);
                        usersRef.child(friendID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    User currentFriend = snapshot.getValue(User.class);
                                    friends.add(currentFriend);
                                    adapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
                loadingAlert.dismissDialog();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }







}