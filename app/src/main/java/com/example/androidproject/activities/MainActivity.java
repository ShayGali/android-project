package com.example.androidproject.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.example.androidproject.R;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class MainActivity extends AppCompatActivity {

    public static final int SIGN_FROM_CREATE = 1;
    public  FirebaseDatabase database = FirebaseDatabase.getInstance();
    public FirebaseUser currentUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Intent singToFirebase = AuthUI.getInstance().createSignInIntentBuilder().build();
            startActivityForResult(singToFirebase, SIGN_FROM_CREATE);
        } else {
            //TODO: לשים בפונקציה
            checkIfTheUserInfoSaveInTheDataBase();
            popupDetails(true);
        }
    }

    /**
     * gets a reference for a menu and declares the menu logic
     * onCreate will call it
     *
     * @param menu - a reference in runtime for the activity's menu bar
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * what happen when the user press on item in the list
     *
     * @param item - each item on the menu
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.log_out_item) {
            logout();
        }
        if (item.getItemId() == R.id.settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return true;
    }

    /**
     * @param success - if the login success
     */
    private void popupDetails(boolean success) {
        if (success) {
            String userDetails = "Your display name is: " + currentUser.getDisplayName() +
                    ", your id: " + currentUser.getUid();

            // הצגת הפופאפ

            Toast.makeText(this, userDetails, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "failed to sign-in/sign-up", Toast.LENGTH_SHORT).show();

        }
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            currentUser = FirebaseAuth.getInstance().getCurrentUser();
            checkIfTheUserInfoSaveInTheDataBase();
        } else {
            popupDetails(false);
            finish();// סוגר את המסך הנוכחי - מוציא אותו מהאפליקציה
        }
    }



    /**
     * Handle logout procedure asynchronously
     * Because this is done asynchronously, we do NOT know when this operation will be completed
     * Google Task Services API can signal upon completion
     * Then and only then can we continue with the rest of the method's code
     * Tasks use CompletionListener to get updated when the task finishes.
     *
     */

    /**
     *
     */
    private void logout() {

        // TODO: צריך להתבצע בצורה אסינכרונית
        // TODO: כשהוא מתנתק להעביר אותו לדף חיבור
        AuthUI.getInstance().signOut(this).addOnCompleteListener(task -> {
            Toast.makeText(this, "You have logged-out", Toast.LENGTH_LONG).show();
            finish();
        });
    }


    public void navToChatsRoom(View view) {
        Intent intent = new Intent(this, ChatsRoomsActivity.class);
        startActivity(intent);
    }

    public void navToSearch(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    public void navToSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void checkIfTheUserInfoSaveInTheDataBase(){
        DatabaseReference myRef = database.getReference("users").child(currentUser.getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if(!dataSnapshot.exists()){ // אם אין עליו מידע בדאטה בייס
                    Intent intent = new Intent(MainActivity.this, UploadUserInfoActivity.class);
                    intent.putExtra("user", currentUser);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }
}

