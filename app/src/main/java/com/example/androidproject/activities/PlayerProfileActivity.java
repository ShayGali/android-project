package com.example.androidproject.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidproject.R;
import com.example.androidproject.model.User;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PlayerProfileActivity extends AppCompatActivity {

    // Keys
    private final String DATABASE_USERS_KEY = "users";
    private final String DATABASE_USERS_FRIENDS_REQUEST_LIST = "friends request";

    // Database properties:
    public final FirebaseDatabase DATABASE = FirebaseDatabase.getInstance();
    public final DatabaseReference USERS_REF = DATABASE.getReference(DATABASE_USERS_KEY);

    public String currentsUserUUID = FirebaseAuth.getInstance().getCurrentUser().getUid();


    //Intent From SearchActivity
    User currentPlayer;
    String playersUUID;
    Integer friendsAmountIntent;
    List<String> tags;

    // Properties:
    TextView userName;
    TextView country;
    TextView tagsTitle;
    TextView friendsAmount;
    ListView tagList;
    ExtendedFloatingActionButton addPlayerBtn;

    // Adapter
    public static ArrayAdapter<String> arrayAdapter;

    //FAB props
    ArrayList<String> playersFriendsRequestList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_profile_activity);

        playersFriendsRequestList = new ArrayList<>();

        // Import Intents
        currentPlayer = (User) getIntent().getExtras().getSerializable("playersObj"); // an intent Player's Object got from SearchActivity
        playersUUID = getIntent().getExtras().getString("playersUUID");
        friendsAmountIntent = currentPlayer.getFriends() != null ? currentPlayer.getFriends().size() : 0;
        tags = currentPlayer.getCategories() != null ? currentPlayer.getCategories() : new ArrayList<>();


        // Initialize adapter
        if (tags != null)
            arrayAdapter = new ArrayAdapter<String>(this, R.layout.my_text_list_view, tags);
        else
            arrayAdapter = new ArrayAdapter<String>(this, R.layout.my_text_list_view, new ArrayList<>());

//        // Initialize Views
        userName = findViewById(R.id.selected_user_name);
        country = findViewById(R.id.country);
        tagsTitle = findViewById(R.id.tags_title);
        friendsAmount = findViewById(R.id.friends_amount);
        tagList = findViewById(R.id.tag_list);
        addPlayerBtn = findViewById(R.id.extended_fab);
//
//        // Sets Views with data
        setViews();
//
        addPlayerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPlayerToFriends(playersUUID);
                addPlayerBtn.hide();

            }
        });

    }

    /**
     * Sets all the TextViews with their values
     *
     * @PARAM: none
     * @RETURN: void
     */
    void setViews() {
        userName.setText(currentPlayer.getUserName());
        country.setText(currentPlayer.getCountry());
        friendsAmount.setText("Friends " + friendsAmountIntent.toString());
//        System.out.println(tags.toString());
        tagList.setAdapter(arrayAdapter);
    }

    /**
     * Adds to current player's friends list
     * selected player's Profile
     *
     * @PARAM: String playersUUID
     * @RETURN: none
     */

    public void addPlayerToFriends(String playersUUID) {


        USERS_REF.child(currentsUserUUID).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
            boolean sendRequestExists = false;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Checks if you are trying to add yourself
                if (playersUUID.equals(currentsUserUUID)) {
                    MainActivity.showToastFromThread(PlayerProfileActivity.this, "Cannot add yourself");
                    return;
                }

                if (snapshot.exists()) {

                    // Checks if Player already exists in currentUser's friends
                    if (snapshot.hasChildren()) {
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            if (postSnapshot.getValue(String.class).equals(playersUUID)) {
                                MainActivity.showToastFromThread(PlayerProfileActivity.this, "Players is already your friend");
                                return;
                            }
                        }
                    }

                    // Sends the Player a Friend Request
                    USERS_REF.child(playersUUID).child(DATABASE_USERS_FRIENDS_REQUEST_LIST).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                playersFriendsRequestList.clear();
                                playersFriendsRequestList.addAll((ArrayList<String>) snapshot.getValue());
                                for (String UUID : playersFriendsRequestList) {
                                    if (UUID != null)
                                        if (UUID.equals(currentsUserUUID)) {
                                            sendRequestExists = true;
                                        }
                                }

                                if (!sendRequestExists) {
                                    System.out.println(playersFriendsRequestList);
                                    playersFriendsRequestList.add(currentsUserUUID);
                                    System.out.println(playersFriendsRequestList);
                                    USERS_REF.child(playersUUID).child(DATABASE_USERS_FRIENDS_REQUEST_LIST).setValue(playersFriendsRequestList);
                                    MainActivity.showToastFromThread(PlayerProfileActivity.this, "Players Request sent successfully");
                                }else
                                    MainActivity.showToastFromThread(PlayerProfileActivity.this, "You already sent a request");
                            }else{
                                playersFriendsRequestList.add(currentsUserUUID);
                                USERS_REF.child(playersUUID).child(DATABASE_USERS_FRIENDS_REQUEST_LIST).setValue(playersFriendsRequestList);                                    MainActivity.showToastFromThread(PlayerProfileActivity.this, "Players Request sent successfully");
                                MainActivity.showToastFromThread(PlayerProfileActivity.this, "Players Request sent successfully");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
//                    if (currentFriendsList != null)
//                        currentFriendsList.clear();d
//                    currentFriendsList = (ArrayList) snapshot.getValue();
//                    currentFriendsList.add(playersUUID);
//                    USERS_REF.child(currentsUserUUID).child("friends").setValue(currentFriendsList);
                } else {
                    MainActivity.showToastFromThread(PlayerProfileActivity.this, "Failed to send the player a friend request");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
