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
import com.example.androidproject.dialogs.FriendsReqDialog;
import com.example.androidproject.dialogs.LoadingAlert;
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
    public static final String USERS_PATH = "users";

    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    public FirebaseUser currentUser;

    public boolean isClickedFriendsRequestBtn;

    LoadingAlert loadingAlert = new LoadingAlert(this);
    List<User> friendsReqUserData = new LinkedList<>();
    FriendsReqDialog friendsReqDialog;

    /**
     * static method that help us to show a toast from thread
     * @param activity on witch activity we want to show the toast
     * @param displayMsg the message to display
     */
    public static void showToastFromThread(final Activity activity, final String displayMsg) {
        activity.runOnUiThread(() -> Toast.makeText(activity, displayMsg, Toast.LENGTH_SHORT).show());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // the friends request dialog
        friendsReqDialog = new FriendsReqDialog(this, friendsReqUserData);
        // start the loading alert
        loadingAlert.startLoadingDialog();

        // get the user from the firebase
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) { //if the is not sign in
            Intent singToFirebase = AuthUI.getInstance().createSignInIntentBuilder().build();
            startActivityForResult(singToFirebase, SIGN_FROM_CREATE);
        } else { // the user sign in
            checkIfTheUserInfoSaveInTheDataBase();
            loadingAlert.dismissDialog();
//            popupDetails(true);
        }
    }

    /**
     * gets a reference for a menu and declares the menu logic
     * onCreate will call it
     *
     * @param menu a reference in runtime for the activity's menu bar
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    /**
     * what happen when the user press on item in the list
     *
     * @param item each item on the menu
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.open_friend_req_dialog) { //the alert btn that on the menu
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
     * popup message the show if the user sign-in/ sign-up successfully
     *
     * @param success if the login success
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
     * when the user back from the
     *
     * @param requestCode what wh send on startActivityForResult
     * @param resultCode  what we get from setResult()
     * @param data intent that can return result data to the caller
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { // if we sign-in/ sign-up successfully
            currentUser = FirebaseAuth.getInstance().getCurrentUser(); // נאתחל אותו עוד פעם למה שקיבלנו עכשיו
            checkIfTheUserInfoSaveInTheDataBase(); // נבדוק אם יש לי קישור מידע שמור עליו בדאטהבייס
            loadingAlert.dismissDialog(); // נעצור את החלון של הטעינה
        } else { // אם הוא לא הצליח להתחבר
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


    /**
     * button the navigate to the chats Rooms
     *
     * @param view the button
     */
    public void navToChatsRoom(View view) {
        Intent intent = new Intent(this, ChatsRoomsActivity.class);
        startActivity(intent);
    }

    /**
     * button the navigate to the search
     *
     * @param view the button
     */
    public void navToSearch(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    /**
     * button the navigate to the settings
     *
     * @param view the button
     */
    public void navToSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * check if the user save in the realtime database
     * if not we send him to upload his settings
     */
    private void checkIfTheUserInfoSaveInTheDataBase() {
        DatabaseReference myRef = database.getReference("users").child(currentUser.getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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

    /**
     * get the friend request og the user from the database
     * if the dialog is open the function will restart the dialog
     */
    public void getFriendsReq() {
        DatabaseReference ref = database.getReference("users");

        ref.child(currentUser.getUid()).child(FRIEND_REQUEST_PATH).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                friendsReqDialog.dismissDialog(); // אנחנו סוגרים אותו כי אנחנו לא רוצים שכל פעם שיהיה שיוי הוא יפתח לי

                if (dataSnapshot.exists()) { // אם יש רשימת חברים
                    friendsReqUserData.clear(); // if we have data on the list we make the list empty
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) { // to add all the friends id
                        String currentIDOfFriendReq = postSnapshot.getValue(String.class);
                        assert currentIDOfFriendReq != null;
                        // get his data
                        ref.child(currentIDOfFriendReq).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    User currentFriendReq = snapshot.getValue(User.class);
                                    assert currentFriendReq != null;
                                    currentFriendReq.setID(currentIDOfFriendReq);  // set his id to the key
                                    friendsReqUserData.add(currentFriendReq); // add the user to the list
                                    friendsReqDialog.notifyAdapter(); // notify the adapter that change has happened
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });
                    }
                    // if the change happened will the dialog was open we open the dialog again
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

    /**
     * if the user press on the button that accept the friend request
     * @param friendID the ID of the user
     */
    public void acceptFriendReq(String friendID) {

        friendsReqDialog.dismissDialog(); // סוגרים את הדיאלוג כדי שיהיה אחד אחר במקמו עם המידע המעודכן

        // בגלל שזה מהדיאלוג אנחנו צריכים לייצר את על המשתנים של הפיירבייס
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(USERS_PATH);

        // מסיר את הבקשת החברות
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

        // מוסיף את המזהה של מי שביקש לחברים של היוזר
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

        // מוסיף את המזהה של היוזר לרשימה של החברים של מי ששלח את הבקשה
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

    /**
     * if the user press on the button that reject the friend request
     * @param friendID the ID of the user
     */
    public void rejectFriendReq(String friendID) {
        friendsReqDialog.dismissDialog();// סוגרים את הדיאלוג כדי שיהיה אחד אחר במקמו עם המידע המעודכן

        // בגלל שזה מהדיאלוג אנחנו צריכים לייצר את על המשתנים של הפיירבייס
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");

        // מסיר את הבקשת החברות
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
    }
}

