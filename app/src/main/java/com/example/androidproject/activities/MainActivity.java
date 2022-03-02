package com.example.androidproject.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.example.androidproject.R;
import com.example.androidproject.model.User;
import com.example.androidproject.utilities.FriendsReqDialog;
import com.example.androidproject.utilities.LoadingAlert;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public static final int SIGN_FROM_CREATE = 1;
    public static final String FRIEND_REQUEST_PATH = "friends request";
    public static final String USER_FRIENDS_PATH = "friends";

    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    public FirebaseUser currentUser;

    LoadingAlert loadingAlert = new LoadingAlert(this);
    List<User> friendsReqUserData = new LinkedList<>();
    FriendsReqDialog friendsReqDialog;


    public boolean isClickedFriendsRequestBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        friendsReqDialog = new FriendsReqDialog(this, friendsReqUserData);

        loadingAlert.startLoadingDialog();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Intent singToFirebase = AuthUI.getInstance().createSignInIntentBuilder().build();
            startActivityForResult(singToFirebase, SIGN_FROM_CREATE);
        } else {
            checkIfTheUserInfoSaveInTheDataBase();
            loadingAlert.dismissDialog();
//            popupDetails(true);
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
        if (item.getItemId() == R.id.open_friend_req_dialog) {
            isClickedFriendsRequestBtn = true;
            getFriendsReq();
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
            loadingAlert.dismissDialog();
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
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
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

    private void checkIfTheUserInfoSaveInTheDataBase() {
        DatabaseReference myRef = database.getReference("users").child(currentUser.getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (!dataSnapshot.exists()) { // אם אין עליו מידע בדאטה בייס
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


    public static void showToastFromThread(final Activity activity, final String displayMsg) {
        activity.runOnUiThread(() -> Toast.makeText(activity, displayMsg, Toast.LENGTH_SHORT).show());
    }


    public void getFriendsReq() {
        DatabaseReference ref = database.getReference("users");

        ref.child(currentUser.getUid()).child(FRIEND_REQUEST_PATH).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                friendsReqDialog.dismissDialog();

                if (dataSnapshot.exists()) { // אם יש רשימת חברים
                    friendsReqUserData.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String currentIDOfFriendReq = postSnapshot.getValue(String.class);
                        assert currentIDOfFriendReq != null;
                        ref.child(currentIDOfFriendReq).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    User currentFriendReq = snapshot.getValue(User.class);
                                    assert currentFriendReq != null;
                                    currentFriendReq.setID(currentIDOfFriendReq);
                                    friendsReqUserData.add(currentFriendReq);
                                    friendsReqDialog.notifyAdapter();
                                } else {
                                    System.out.println("dont");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    if (isClickedFriendsRequestBtn) {
                        friendsReqDialog.startDialog();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

    }

    public void acceptFriendReq(String friendID) {

        friendsReqDialog.dismissDialog();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");

        myRef.child(currentUser.getUid()).child(FRIEND_REQUEST_PATH).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String friendReqID = postSnapshot.getValue(String.class);
                    assert friendReqID != null;
                    if (friendReqID.equals(friendID))
                        postSnapshot.getRef().removeValue();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        myRef.child(currentUser.getUid()).child(USER_FRIENDS_PATH).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                LinkedList<String> friendsList = new LinkedList<>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    friendsList.add(postSnapshot.getValue(String.class));
                }
                friendsList.add(friendID);
                snapshot.getRef().setValue(friendsList);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRef.child(friendID).child(USER_FRIENDS_PATH).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                LinkedList<String> friendsList = new LinkedList<>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    friendsList.add(postSnapshot.getValue(String.class));
                }
                friendsList.add(currentUser.getUid());
                snapshot.getRef().setValue(friendsList);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void rejectFriendReq(String friendID) {
        System.out.println("remove");


    }
}

