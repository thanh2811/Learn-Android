package com.example.chattogether;

import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chattogether.Adapter.MessageAdapter;
import com.example.chattogether.models.Chat;
import com.example.chattogether.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    TextView textUsernameMessage;
    ImageView avatar;
    DatabaseReference mRef;
    FirebaseUser firebaseUser;
    ImageView btnSend;
    EditText messageEt;
    RecyclerView recyclerView;
    LinearLayout view_user;

    List<Chat> chatList;
    String userID, imgUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }

        // get userID selected
        Intent i = getIntent();
        userID = i.getStringExtra("userID");
        imgUrl = i.getStringExtra("imgUrl");

        // set Actionbar
        View back = findViewById(R.id.btnBack);

        //back_btn clicked
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessageActivity.this.finish();
            }
        });

        // view user information
        view_user = findViewById(R.id.user_view);
        view_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessageActivity.this, ViewProfileActivity.class);
                intent.putExtra("userId", userID);
                startActivity(intent);
            }
        });


        textUsernameMessage = findViewById(R.id.textUsernameMessage);
        avatar = findViewById(R.id.avatarMessage);

        //recycle view
        recyclerView = findViewById(R.id.messageLayout);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
//        recyclerView.getRecycledViewPool().setMaxRecycledViews(0,0);

        // Refer to data tree by id
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference("MyUser").child(userID);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                textUsernameMessage.setText(user.getUsername());

                if(user.getImageUrl().equals("default")){
                    avatar.setImageResource(R.drawable.ic_launcher_background);
                }
                else{
                    Glide.with(getApplicationContext())
                            .load(user.getImageUrl()).into(avatar);
                }

                chatList = new ArrayList<>();
                ReadChatList(firebaseUser.getUid(),userID );
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


       // send message
        btnSend = findViewById(R.id.btnSend);
        messageEt = findViewById(R.id.messageEt);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = messageEt.getText().toString().trim();
                if(msg.isEmpty()){
                    Toast.makeText(MessageActivity.this, "No message to send!", Toast.LENGTH_SHORT).show();
//                    System.out.println("Sender: ");
                }
                else {
                    sendMessage(firebaseUser.getUid(), userID, msg);
                }
                messageEt.setText("");
            }

        });

        // receive message


    }

    private void ReadChatList(String sender, String receiver) {
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chats");

        int size = (int)getResources().getDimension(R.dimen.image_size);

        MessageAdapter messageAdapter = new MessageAdapter(MessageActivity.this,chatList,imgUrl,size);

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                chatList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if(firebaseUser.getUid().equals(chat.getSender()) && chat.getReceiver().equals(userID)
                            || chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userID)) {
                        chatList.add(chat);

                        messageAdapter.notifyDataSetChanged();
                    }

                }

                recyclerView.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    //    public void setSupportActionBar(Toolbar toolbar) {
//    }
        private void sendMessage(String sender, String receiver, String msg) {
            DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("sender", sender);
            hashMap.put("receiver",receiver);
            hashMap.put("msg", msg);
            mRef.child("Chats").push().setValue(hashMap);

            // Adding the last chat to ChatFragment
            // Point sender ref
            DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("ChatList")
                                            .child(firebaseUser.getUid())
                                            .child(userID);
            chatRef.child("last_msg").setValue(msg);
            chatRef.child("id").setValue(userID);
            chatRef.child("you_are_sender").setValue(true);


            //Recevier Ref - set the last msg same to receiver
            DatabaseReference receiverRef = FirebaseDatabase.getInstance().getReference("ChatList")
                    .child(userID)
                    .child(firebaseUser.getUid());
            receiverRef.child("last_msg").setValue(msg);
            receiverRef.child("id").setValue(firebaseUser.getUid());
            receiverRef.child("you_are_sender").setValue(false);

        }

}