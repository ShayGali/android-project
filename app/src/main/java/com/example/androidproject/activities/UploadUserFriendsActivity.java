package com.example.androidproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.androidproject.R;
import com.example.androidproject.model.User;
import com.example.androidproject.utilities.LoadingAlert;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class UploadUserFriendsActivity extends AppCompatActivity {

    LoadingAlert loadingAlert = new LoadingAlert(this);

    FirebaseDatabase database;
    DatabaseReference userRef;
    FirebaseUser currentUser;
    User userModel; // the user model

    ListView listView;
    ArrayList<String> friends;
    ArrayList<String> friendsNames;

    ArrayAdapter<String> arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_user_friends);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("users").child(currentUser.getUid());

        friends = new ArrayList<>();
        friendsNames = new ArrayList<>();


        listView = findViewById(R.id.listView_user_friends);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, friends);
        listView.setAdapter(arrayAdapter);


        loadingAlert.startLoadingDialog();
        Thread thread = new Thread(this::getUserFriends);
        thread.start();


    }


    void getUserFriends() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friends.clear();
                friends.addAll(Objects.requireNonNull(snapshot.getValue(User.class)).getFriends());
                getFriendsNames();
                loadingAlert.dismissDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void getFriendsNames() {
        friendsNames.clear();
        for (String item : friends) {
            //item = FirebaseDatabase.getInstance().getReference().child("users/" + item + "/userName").toString();
            System.out.println(item);
            friendsNames.add(item);
        }
        arrayAdapter.notifyDataSetChanged();
    }



}