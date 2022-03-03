package com.example.androidproject.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.R;
import com.example.androidproject.activities.MainActivity;
import com.example.androidproject.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class FriendsReqRecyclerViewAdapter extends RecyclerView.Adapter<FriendsReqRecyclerViewAdapter.ReqViewHolder> {

    private List<User> usersInFriendsReq;
    private Activity activity;

    public FriendsReqRecyclerViewAdapter(Activity activity, List<User> usersInFriendsReq) {
        this.usersInFriendsReq = usersInFriendsReq;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ReqViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReqViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_req, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReqViewHolder holder, int position) {
        TextView userName = holder.userName;
        FloatingActionButton acceptFriend = holder.acceptFriend;
        FloatingActionButton reject = holder.rejectFriend;

        User currentFriendReq = usersInFriendsReq.get(position);
        MainActivity mainActivity = (MainActivity) activity;

        userName.setText(currentFriendReq.getUserName());


        acceptFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.acceptFriendReq(currentFriendReq.getID());
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.rejectFriendReq(currentFriendReq.getID());
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersInFriendsReq.size();
    }

    public static class ReqViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        FloatingActionButton acceptFriend;
        FloatingActionButton rejectFriend;

        public ReqViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);
            acceptFriend = itemView.findViewById(R.id.accept_friend_floating_button);
            rejectFriend = itemView.findViewById(R.id.reject_friend_floating_button);
        }
    }
}
