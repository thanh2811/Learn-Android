package com.example.chattogether.Adapter;

import android.content.Context;
import android.content.Intent;
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
import com.example.chattogether.models.User;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private List<User> userList;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }


    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.username.setText(user.getUsername());
        holder.email.setText(user.getEmail());
        if(user.getImageUrl().equals("default")){
            holder.avatar.setImageResource(R.drawable.ic_launcher_background);
        } else {
            Glide.with(context)
                    .load(user.getImageUrl())
                    .into(holder.avatar);
        }

        if(user.getOnline() == null || !user.getOnline().equals("on"))
            holder.online.setVisibility(View.INVISIBLE);
        else
            holder.online.setVisibility(View.VISIBLE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, MessageActivity.class);
                i.putExtra("userID",user.getId());
                i.putExtra("imgUrl",user.getImageUrl());
                context.startActivity(i);
            }
        });
    }

//    @Override
//    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
//        User user = userList.get(position);
//        holder.
//    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        public ImageView avatar, online;
        public TextView username;
        public TextView email;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            username = itemView.findViewById(R.id.textUser);
            email = itemView.findViewById(R.id.textEmail);
            online = itemView.findViewById(R.id.online);
        }
    }
}



