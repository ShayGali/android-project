package com.example.androidproject.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidproject.R;
import com.example.androidproject.model.User;
import com.example.androidproject.utilities.UserHandler;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;

public class PlayerProfileActivity extends AppCompatActivity {

    //Intent From SearchActivity
    User currentPlayer;
    String playersUUID;
    Integer friendsAmountIntent;
    ArrayList<String> tags;

    // Properties:
    TextView userName;
    TextView country;
    TextView tagsTitle;
    TextView friendsAmount;
    ListView tagList;
    ExtendedFloatingActionButton addPlayerBtn;

    // Adapter
    public static ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_profile_activity);


        // Import Intents
//        currentPlayer = (User) getIntent().getExtras().getSerializable("playersObj"); // an intent Player's Object got from SearchActivity
        playersUUID = getIntent().getExtras().getString("playersUUID");
        friendsAmountIntent = getIntent().getExtras().getInt("amountOfFriends");
        tags = getIntent().getExtras().getStringArrayList("tagList");

        // Initialize adapter
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.my_text_list_view, tags);

        // Initialize Views
        userName = findViewById(R.id.selected_user_name);
        country = findViewById(R.id.country);
        tagsTitle = findViewById(R.id.tags_title);
        friendsAmount = findViewById(R.id.friends_amount);
        tagList = findViewById(R.id.tag_list);
        addPlayerBtn = findViewById(R.id.extended_fab);

        // Sets Views with data
        setViews();

        addPlayerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                UserHandler.addPlayerToFriends(playersUUID);
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
        friendsAmount.setText("Friends "+friendsAmountIntent.toString());
        System.out.println(tags.toString());
        tagList.setAdapter(arrayAdapter);
    }

}
