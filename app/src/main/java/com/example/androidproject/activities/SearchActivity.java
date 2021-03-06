package com.example.androidproject.activities;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.androidproject.R;
import com.example.androidproject.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    // Keys
    private final String DATABASE_USERS_KEY = "users";

    // Database properties:
    public final FirebaseDatabase DATABASE = FirebaseDatabase.getInstance();
    public final DatabaseReference USERS_REF = DATABASE.getReference(DATABASE_USERS_KEY);

    // Current User
    public FirebaseUser currentUserData;
    public String currentsUserUUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    // DataMembers
    public Map<String, User> userMap = new HashMap<>();
    public ArrayList<String> playerNames = new ArrayList<>();
    public ArrayList<String> selectedPlayersCategories = new ArrayList<>();

    // Testing
    ListView listView;
//            ArrayList<User> players;

    public static ArrayAdapter<String> arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        currentUserData = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUserData != null)
            currentsUserUUID = currentUserData.getUid();

        getUsers();
//
        playerNames = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, playerNames);
//
//        selectedUsersName = findViewById(R.id.selected_users_name);
        listView = findViewById(R.id.players_listView);

        listView.setAdapter(arrayAdapter);

        // Events listener for pressing on a user's name in the listView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {

                //get the item and transform it to your JsonBean
                String item = (String) adapterView.getItemAtPosition(index); // item = name
                String id;

                Intent intent = new Intent(getApplicationContext(), PlayerProfileActivity.class);

                // Packets up all intents with data
                for (User user : userMap.values()) {
                    if (user.getUserName().equals(item)) {
                        intent.putExtra("playersObj", (Serializable) user);
                        getUserUUIDByName(item);
                        if (selectedUserUUID != null) {
                            intent.putExtra("playersUUID", selectedUserUUID);
                        }
                    }
                }
                startActivity(intent);
            }
        });
    }

    // configs the search icon in top menu bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.search_action);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search a player");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                arrayAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    /**************************************************************************/
//                               Database Methods

    /**************************************************************************/

    // Method for getting all users as UserObject from DB and insert into MAP
    public void getUsers() {
        USERS_REF.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userMap.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    userMap.put(postSnapshot.getKey(), postSnapshot.getValue(User.class));
                }
                getPlayerNames();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Gets from map all user's names and adds them in to an arrayList
    public void getPlayerNames() {
        playerNames.clear();
        for (Map.Entry<String, User> entry : userMap.entrySet()) {
            String key = entry.getKey();
            User user = entry.getValue();
            if (!currentsUserUUID.equals(key)) {
                playerNames.add(user.getUserName());
            }
        }
        arrayAdapter.notifyDataSetChanged();
    }

    // Gets selected players categories by UUID
    public void getSelectedPlayersCategories(String key) {
        selectedPlayersCategories.clear();
        for (Map.Entry<String, User> entry : userMap.entrySet()) {
            if (key.equals(entry.getKey())) {
                if (entry.getValue().getCategories() != null)
                    selectedPlayersCategories.addAll(entry.getValue().getCategories());
                else selectedPlayersCategories.add("");
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
    public String selectedUserUUID;

    public void getUserUUIDByName(String name) {
        selectedUserUUID = "";
        for (Map.Entry<String, User> entry : userMap.entrySet()) {
            if (entry.getValue().getUserName().equals(name)) {
                selectedUserUUID = entry.getKey();
            }
        }
    }

    public User getUserObjByUUID(String UUID) {

        for (Map.Entry<String, User> entry : userMap.entrySet()) {
            if (entry.getKey().equals(UUID))
                return entry.getValue();
        }

        return null;
    }


}