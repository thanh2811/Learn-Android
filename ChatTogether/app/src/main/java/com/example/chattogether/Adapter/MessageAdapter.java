package com.example.chattogether.Adapter;

import android.content.Context;
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
import com.example.chattogether.models.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    Context context;
    List<Chat> chatList;
    FirebaseUser firebaseUser;
    String imgUrl;
    private static int MSG_RIGHT = 1;
    private static int MSG_LEFT = 0;
    int imgSize;

    public MessageAdapter(Context context, List<Chat> chatList, String imgUrl, int imgSize) {
        this.context = context;
        this.chatList = chatList;
        this.imgUrl = imgUrl;
        this.imgSize = imgSize;
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser.getUid().equals(chatList.get(position).getSender()))
            return MSG_RIGHT;

        return MSG_LEFT;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v;
        if(viewType == MSG_RIGHT){
            v = LayoutInflater.from(context).inflate(R.layout.chat_item_right,parent, false);
        } else {
            v = LayoutInflater.from(context).inflate(R.layout.chat_item_left,parent, false);
        }

        return new MessageAdapter.ViewHolder(v) ;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.msg.setText(chat.getMsg());
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        System.out.println(position);
        if(chat.getSender().equals(firebaseUser.getUid()))
            return;
        if(position >= 0 && position < chatList.size() - 1) {
            Chat nextChat = chatList.get(position + 1);
            if (!nextChat.getSender().equals(firebaseUser.getUid())){
                holder.avatar.getLayoutParams().height = 0;
                holder.avatar.setVisibility(View.INVISIBLE);
                return;
            }
        }

        // Q: Why Glide does not load image view when I scroll ?

        // A: In RecyclerView, new itemView is reused from previousView without creating one new.
        //    So if you set imageView invisible, you don't have it visible for the later use.

        holder.avatar.getLayoutParams().height = imgSize;
        holder.avatar.setVisibility(View.VISIBLE);
        if (imgUrl.equals("defalut"))
            holder.avatar.setImageResource(R.drawable.ic_launcher_background);
        else
            Glide.with(context).load(imgUrl).into(holder.avatar);

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView msg;
        public ImageView avatar;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            msg = itemView.findViewById(R.id.messageLayout);
            avatar = itemView.findViewById(R.id.avatar_message);
        }
    }
}
