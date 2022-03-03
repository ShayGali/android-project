package com.example.androidproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.R;
import com.example.androidproject.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.EventListener;
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
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("users");

                myRef.child(currentUser.getUid()).child("friends").addListenerForSingleValueEvent(new ValueEventListener(){

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String friendId = snapshot.getKey();
                            snapshot.getRef().removeValue();

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

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
