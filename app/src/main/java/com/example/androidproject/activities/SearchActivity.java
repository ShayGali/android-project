package com.example.androidproject.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.androidproject.R;
import com.example.androidproject.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    private static final String DATABASE_USERS_KEY = "users";

//    FirebaseUser currentUser; //TODO - check for later
    FirebaseDatabase database;
    DatabaseReference usersRef;

    // Testing
    ListView listView;
    ArrayList<User> players;
    Map<String, User> playersNames;

    ArrayAdapter<String> arrayAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference(DATABASE_USERS_KEY);
        playersNames = new LinkedHashMap<>();

        getUsers();

        listView = findViewById(R.id.players_listView);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,players);
        listView.setAdapter(arrayAdapter);

    }

    private void getUsers() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);

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