package com.example.chattogether.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chattogether.MessageActivity;
import com.example.chattogether.R;
import com.example.chattogether.models.ChatList;
import com.example.chattogether.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    Context context;
    List<User> userChatted; // list user chat recently by ID

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    public ChatAdapter(Context context, List<User> userChatted) {
        this.context = context;
        this.userChatted = userChatted;
    }

    @NonNull
    @NotNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.chat_item, parent, false);
        ViewHolder viewHolder = new ChatAdapter.ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ChatAdapter.ViewHolder holder, int position) {
        User user = userChatted.get(position);
        if(user.getImageUrl().equals("default"))
            holder.avatar.setImageResource(R.drawable.ic_launcher_background);
        else
            Glide.with(context).load(userChatted.get(position).getImageUrl()).into(holder.avatar);
        holder.username.setText(user.getUsername());

        // check online/offline
        if(user.getOnline() == null || !user.getOnline().equals("on"))
            holder.online.setVisibility(View.INVISIBLE);
        else
            holder.online.setVisibility(View.VISIBLE);


//        final String[] last_msg = new String[1];
//        last_msg[0] = "temp";
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("ChatList/" + firebaseUser.getUid()
                                                    + "/" + user.getId());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String last_msg = snapshot.child("last_msg").getValue(String.class);

                if(last_msg.length() > 30)
                    last_msg = last_msg.substring(0,30).concat("...");

                // check who send the msg
                boolean you_are_sender = snapshot.child("you_are_sender").getValue(Boolean.class);

                if(you_are_sender)
                    last_msg = "You: " + last_msg;

                holder.lastMsg.setText(last_msg);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        // listen on chatItem is clicked
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go to msgActivity
                Intent i = new Intent(context, MessageActivity.class);
                i.putExtra("userID", userChatted.get(position).getId());
                i.putExtra("imgUrl", userChatted.get(position).getImageUrl());
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return userChatted.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView avatar, online;
        public TextView username;
        public TextView lastMsg;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.avatar = itemView.findViewById(R.id.avatar_chat_list);
            this.username = itemView.findViewById(R.id.textUser);
            this.lastMsg = itemView.findViewById(R.id.text_last_msg);
            this.online = itemView.findViewById(R.id.online);
        }
    }
}
