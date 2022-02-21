package com.example.androidproject.utilities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.R;
import com.example.androidproject.model.Message;
import com.example.androidproject.model.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Map;

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatRecyclerViewAdapter.MessageViewHolder> {


    private ArrayList<Message> messages;
    private Map<String, User> roomParticipants;

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    public ChatRecyclerViewAdapter(ArrayList<Message> messages, Map<String, User> roomParticipants) {
        this.messages = messages;
        this.roomParticipants = roomParticipants;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT)
            return new MessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_send_layout, parent, false));

        return new MessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_received_layout, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        TextView messageContent = holder.messageContent;
        TextView textTime = holder.textTime;
        TextView userNameTextView = holder.userName;

        // get the user name
        User userNameByID = roomParticipants.get(messages.get(position).getSenderId());
        String userName = // check if i have user and if the user have userName
                userNameByID != null && userNameByID.getUserName() != null ?
                        userNameByID.getUserName() : // if we found that user and his user name
                        "UserName Not Found"; // else

        messageContent.setText(messages.get(position).getMessageContent());
        textTime.setText(messages.get(position).getTimestamp());
        userNameTextView.setText(userName);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    /**
     * @param position the position of the message in the array
     * @return if the message sent or received
     */
    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            return VIEW_TYPE_SENT;
        }
        return VIEW_TYPE_RECEIVED;
    }




    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView messageContent;
        TextView textTime;
        TextView userName;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            messageContent = itemView.findViewById(R.id.message_content);
            textTime = itemView.findViewById(R.id.text_time);
            userName = itemView.findViewById(R.id.username_text_view);
        }
    }
}

