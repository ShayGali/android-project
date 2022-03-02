package com.example.androidproject.utilities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.R;
import com.example.androidproject.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class FriendsNameRecyclerViewAdapter extends RecyclerView.Adapter<FriendsNameRecyclerViewAdapter.CardViewHolder>{

    List<User> friendsList;

    public FriendsNameRecyclerViewAdapter(List<User> friendsList){
        this.friendsList = friendsList;
    }


    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CardViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        TextView friendNameTextView = holder.friendName;
        FloatingActionButton deleteFriend = holder.deleteFriend;

        String friendName = friendsList.get(position).getUserName();

        friendNameTextView.setText(friendName);

        deleteFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView friendName;
        FloatingActionButton deleteFriend;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            friendName = itemView.findViewById(R.id.friendName);
            deleteFriend = itemView.findViewById(R.id.deleteFriendButton);
        }
    }
}
