package com.example.androidproject.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.androidproject.R;
import com.example.androidproject.model.User;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        TextView roomId = findViewById(R.id.roomId);
        roomId.setText(intent.getExtras().getString("roomID"));
    }


}