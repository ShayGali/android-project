package com.example.androidproject.activities;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.androidproject.R;
import com.example.androidproject.model.User;
import com.example.androidproject.utilities.UserHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;

public class SearchActivity extends AppCompatActivity {

    // Current User
    public FirebaseUser currentUserData;
    public String currentsUserUUID;

    // Selected User's profile pop-up
    TextView selectedUsersName;

    // Testing
    ListView listView;
    //        ArrayList<User> players;
    //TODO : check if works with adapter...

    public static ArrayAdapter<String> arrayAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        currentUserData = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUserData != null)
            currentsUserUUID = currentUserData.getUid();

        UserHandler.getUsers();
        UserHandler.getPlayerNames();

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, UserHandler.playerNames);

//        selectedUsersName = findViewById(R.id.selected_users_name);
        listView = findViewById(R.id.players_listView);

        arrayAdapter.notifyDataSetChanged();
        listView.setAdapter(arrayAdapter);

        // Events listener for pressing on a user's name in the listView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {



            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {

                //get the item and transform it to your JsonBean
                String item = (String) adapterView.getItemAtPosition(index); // item = name
                String id;

                Intent intent = new Intent(getApplicationContext(),PlayerProfileActivity.class);

                // Packets up all intents with data
                for (User user : UserHandler.userMap.values()) {
                    if (user.getUserName().equals(item)) {
//                        System.out.println("IF 1 - WORKS ");
                        intent.putExtra("playersObj",(Serializable) user) ;
                        UserHandler.getUserUUIDByName(item);
                        if (UserHandler.selectedUserUUID != null) {
//                            System.out.println("IF 2 - WORKS ");
                            intent.putExtra("playersUUID",UserHandler.selectedUserUUID);
                            UserHandler.getSelectedPlayersCategories(UserHandler.selectedUserUUID);
                            intent.putExtra("amountOfFriends",UserHandler.selectedPlayersCategories.size());
                            intent.putStringArrayListExtra("tagList",UserHandler.selectedPlayersCategories);
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





}