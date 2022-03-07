package com.example.androidproject.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.androidproject.R;
import com.firebase.ui.auth.AuthUI;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }


    public void navToUserInfo(View view) {
        startActivity(new Intent(this, UploadUserInfoActivity.class));
    }

    public void navToUserFriendsList(View view) {
        startActivity(new Intent(this, UploadUserFriendsActivity.class));
    }

    public void navToMainActivity(View view) {
        logoutSettings();
        startActivity(new Intent(this, MainActivity.class));
    }

    /**
     * Handle logout procedure asynchronously
     * Because this is done asynchronously, we do NOT know when this operation will be completed
     * Google Task Services API can signal upon completion
     * Then and only then can we continue with the rest of the method's code
     * Tasks use CompletionListener to get updated when the task finishes.
     */
    private void logoutSettings() {
        // TODO: צריך להתבצע בצורה אסינכרונית
        // TODO: כשהוא מתנתק להעביר אותו לדף חיבור
        AuthUI.getInstance().signOut(this).addOnCompleteListener(task -> {
            Toast.makeText(this, "You have logged-out", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
    }


}