package com.example.androidproject.utilities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.androidproject.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserHandler {

    // Keys
    private static final String DATABASE_USERS_KEY = "users";

    //Current User props
    public static final String currentsUserUUID = FirebaseAuth.getInstance().getCurrentUser().getUid();


    // Database properties:
    // FirebaseUser currentUser; //TODO - check for later
    public static final FirebaseDatabase DATABASE = FirebaseDatabase.getInstance();
    public static final DatabaseReference USERS_REF = DATABASE.getReference(DATABASE_USERS_KEY);


    // DataMembers
    public static Map<String, User> userMap = new HashMap<>();
    public static ArrayList<String> playerNames = new ArrayList<>();
    public static ArrayList<String> selectedPlayersCategories = new ArrayList<>();

    // methods
    // Method for getting all users as UserObject from DB and insert into MAP
    public static void getUsers() {
        USERS_REF.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userMap.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    userMap.put(postSnapshot.getKey(), postSnapshot.getValue(User.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Gets from map all user's names and adds them in to an arrayList
    public static void getPlayerNames() {
        playerNames.clear();

        for (Map.Entry<String, User> entry : userMap.entrySet()) {
            String key = entry.getKey();
            User user = entry.getValue();
            playerNames.add(user.getUserName());
        }
    }

    // Gets selected players categories by UUID
    public static void getSelectedPlayersCategories(String key) {
        selectedPlayersCategories.clear();
        for (Map.Entry<String, User> entry : userMap.entrySet()) {
            if (key.equals(entry.getKey())) {
                selectedPlayersCategories.addAll(entry.getValue().getCategories());
            }
        }
    }

    /**
     * String of a name used to find it's UUID (keys in map) and
     * set selectedUserUUIDByName to its UUID.
     *
     * @PARAM: name
     * @RETURN: none
     */
    public static String selectedUserUUID;

    public static void getUserUUIDByName(String name) {
        selectedUserUUID = "";
        for (Map.Entry<String, User> entry : userMap.entrySet()) {
            if (entry.getValue().getUserName().equals(name)) {
                selectedUserUUID = entry.getKey();
            }
        }

    }

    public static User getUserObjByUUID(String UUID) {

        for (Map.Entry<String, User> entry : userMap.entrySet()) {
            if (entry.getKey().equals(UUID))
                return entry.getValue();
        }

        return null;
    }


    /**
     * Adds to current player's friends list
     * selected player's Profile
     *
     * @PARAM: String playersUUID
     * @RETURN: none
     */
    public static void addPlayerToFriends(String playersUUID) {

        USERS_REF.child(currentsUserUUID).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (getUserObjByUUID(currentsUserUUID) != null) {
//                    currentFriendsList = snapshot.getValue(ArrayList.class);
//                    assert currentFriendsList != null;
//                    currentFriendsList.add(playersUUID);
//                    USERS_REF.child(currentsUserUUID).child("friends").setValue(currentFriendsList);
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
