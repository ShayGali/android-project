package com.example.androidproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.androidproject.R;
import com.example.androidproject.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SearchActivity extends AppCompatActivity {

    //    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, Long.MAX_VALUE, TimeUnit.NANOSECONDS, new LinkedBlockingQueue<>());
    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);

    private static final String DATABASE_USERS_KEY = "users";

    //    FirebaseUser currentUser; //TODO - check for later
    FirebaseDatabase database;
    DatabaseReference usersRef;

    // Testing
    ListView listView;
    ArrayList<User> players;
    ArrayList<String> playerNames;

    ArrayAdapter<String> arrayAdapter;

    //Threads
    Thread threadA,threadB,threadC;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference(DATABASE_USERS_KEY);

        players = new ArrayList<>();
        playerNames = new ArrayList<>();

        getUsers();

        listView = findViewById(R.id.players_listView);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, playerNames);
        listView.setAdapter(arrayAdapter);
    }

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

    void getUsers() {
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                players.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    players.add(postSnapshot.getValue(User.class));
                }
                getPlayerNames();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void getPlayerNames() {
        playerNames.clear();
        for (User obj : players) {
            System.out.println(obj.getUserName());
            playerNames.add(obj.getUserName());
        }
        arrayAdapter.notifyDataSetChanged();
    }

    public void showList(View view) {
        System.out.println(players.toString());
    }
}