package com.example.androidproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ChatsRoomsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats_rooms);
    }

    public void goToChat(View view) {
        String roomID = "239ea67e-7b58-4c80-93ea-8f381f598f4f";
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("roomID", roomID);
        startActivity(intent);
    }
}