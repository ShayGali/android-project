package com.example.androidproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

    ListView listView;
    ArrayList<User> friends;
    ArrayList<String> friendsNames;

    ArrayAdapter<String> arrayAdapter;

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
        friendsNames = new ArrayList<>();

//        getUserFriends();

        listView = findViewById(R.id.listView_user_friends);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, friendsNames);
        listView.setAdapter(arrayAdapter);


        loadingAlert.startLoadingDialog();
        Thread thread = new Thread(this::getUserFriends);
        thread.start();


    }

    void getUserFriends() {
        userRef.child("friends").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
//                        if (!Objects.requireNonNull(snapshot.getValue(User.class)).getFriends().isEmpty()) {
////                            System.out.println(snapshot.getValue(User.class).getFriends());
//                            friends.addAll(Objects.requireNonNull(snapshot.getValue(User.class)).getFriends());
//                            for (int i = 0; i < friends.size(); i++) {
//                                friendsNames.add(getUserObjByUUID(friends.get(i)).getUserName());
//                            }
//                            getFriendsNames();
//                            friends.clear();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        String friendID = postSnapshot.getValue(String.class);
                        usersRef.child(friendID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    User currentFriend = snapshot.getValue(User.class);
                                    friends.add(currentFriend);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    for (int i = 0; i < friends.size(); i++) {
                        friendsNames.add(friends.get(i).getUserName());
//                        System.out.println(friends.get(i).getUserName());
                    }
                    getFriendsNames();
                }
                loadingAlert.dismissDialog();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void getFriendsNames() {
        for (String item : friendsNames) {
            System.out.println(item);
        }
        arrayAdapter.notifyDataSetChanged();
        friendsNames.clear();
    }

    public void a(View view) {
        MainActivity.showToastFromThread(this, friends.toString());
    }

//    public User getUserObjByUUID(String UUID) {
//        getUsers();
//        for (Map.Entry<String, User> entry : userMap.entrySet()) {
//            if (entry.getKey().equals(UUID))
//                return entry.getValue();
//        }
//
//        return null;
//    }
//
//    public void getUsers() {
//        USERS_REF.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                userMap.clear();
//                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
//                    userMap.put(postSnapshot.getKey(), postSnapshot.getValue(User.class));
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }


}