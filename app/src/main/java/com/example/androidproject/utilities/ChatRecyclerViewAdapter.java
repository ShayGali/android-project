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
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatRecyclerViewAdapter.MessageViewHolder> {


    private ArrayList<Message> messages;

    private static int VIEW_TYPE_SENT = 1;
    private static int VIEW_TYPE_RECEIVED = 2;

    public ChatRecyclerViewAdapter(ArrayList<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_SENT)
            return new MessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_send_layout, parent, false));

        return new MessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_received_layout, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        TextView messageContent = holder.messageContent;
        TextView textTime = holder.textTime;

        messageContent.setText(messages.get(position).getMessageContent());
        textTime.setText(messages.get(position).getTimestamp());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    /**
     *
     * @param position the position of the message in the array
     * @return if the message sent or received
     */
    @Override
    public int getItemViewType(int position) {
        if(messages.get(position).getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            return VIEW_TYPE_SENT;
        }
        return VIEW_TYPE_RECEIVED;
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView messageContent;
        TextView textTime;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            messageContent = itemView.findViewById(R.id.message_content);
            textTime = itemView.findViewById(R.id.text_time);
        }
    }
}

