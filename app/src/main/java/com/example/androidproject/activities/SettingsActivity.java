package com.example.androidproject.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.androidproject.R;

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

}