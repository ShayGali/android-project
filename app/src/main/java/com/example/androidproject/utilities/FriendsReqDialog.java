package com.example.androidproject.utilities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.R;
import com.example.androidproject.activities.MainActivity;
import com.example.androidproject.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FriendsReqDialog {
    MainActivity activity;
    AlertDialog dialog;
    List<User> friendReq;
    RecyclerView recyclerView;
    FriendsReqRecyclerViewAdapter adapter;

    public FriendsReqDialog(MainActivity activity, List<User> friendReq) {
        this.activity = activity;
        this.friendReq = friendReq;
    }

    public void startDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);


        LayoutInflater inflater = activity.getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.friend_req_dialog, null);
        builder.setView(dialogView);

        builder.setCancelable(false);

        FloatingActionButton closeBtn = dialogView.findViewById(R.id.close_floating_action_button);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
                activity.isClickedFriendsRequestBtn = false;
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView = dialogView.findViewById(R.id.friends_req_recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FriendsReqRecyclerViewAdapter(activity, friendReq);
        recyclerView.setAdapter(adapter);
        dialog = builder.create();
        dialog.show();
    }


    @SuppressLint("NotifyDataSetChanged")
    public void notifyAdapter() {
        adapter.notifyDataSetChanged();
    }

    public void dismissDialog() {
        if (dialog != null)
            dialog.dismiss();
    }

}
// com.example.androidproject.utilities.FriendsReqDialog@7ae5f22
// com.example.androidproject.utilities.FriendsReqDialog@f0a6494
// com.example.androidproject.utilities.FriendsReqDialog@7ae5f22