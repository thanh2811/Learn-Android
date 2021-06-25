package com.example.chattogether;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.viewpager.widget.ViewPager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chattogether.Adapter.ViewPagerAdapter;
import com.example.chattogether.fragment.Chats;
import com.example.chattogether.fragment.Users;
import com.example.chattogether.models.User;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    DatabaseReference mRef;
    ImageView avatar, btn_setting;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
//        }

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        // set avatar
        avatar = findViewById(R.id.avatar);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, ProfileActivity.class);
                i.putExtra("userID", firebaseUser.getUid());
                startActivity(i);
            }
        });

        mRef = FirebaseDatabase.getInstance().getReference("MyUser")
                .child(firebaseUser.getUid());
        mRef.child("online").setValue("on");

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user == null) return;
                if(user.getImageUrl().equals("default"))
                    avatar.setImageResource(R.drawable.send);
                else
                    Glide.with(getApplicationContext()).load(user.getImageUrl()).into(avatar);

                Toast.makeText(HomeActivity.this, "User Login: "+ user.getUsername(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        // TabLayout and ViewPager
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.viewPager);
        // create Adapter bellow
        // add fragment into viewpager
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(new Chats(), "Chats");
        viewPagerAdapter.addFragment(new Users(), "Users");
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
        int icon[] = {R.drawable.ic_chats, R.drawable.ic_contact};
        for (int i = 0; i<tabLayout.getTabCount(); i++){
            tabLayout.getTabAt(i).setIcon(icon[i]);
        }

        btn_setting = findViewById(R.id.ic_setting);
        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.speak);

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    NotificationChannel notificationChannel = new NotificationChannel("ChanelID","Chanel1",
                            NotificationManager.IMPORTANCE_HIGH);
                    notificationChannel.setDescription("this is description");
                    notificationChannel.setLightColor(Color.BLUE);
                    notificationManager.createNotificationChannel(notificationChannel);
                }

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),"ChanelID");
                builder.setWhen(System.currentTimeMillis())
                        .setContentTitle("Title")
                        .setContentText("Content")
//                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.ic_email);
                notificationManager.notify(getId(),builder.build());

//
//                Notification notificationCompat = new Notification.Builder(getApplicationContext())
//                        .setSmallIcon(R.drawable.ic_email)
//                        .setContentTitle("Title of notification")
////                        .setContentText("Content Content Content Content Content Content Content Content " +
////                                "Content Content Content Content Content Content Content ")
////                        .setAutoCancel(true)
//                        .setStyle(new Notification.BigTextStyle().bigText("Content Content Content Content Content Content Content Content " +
//                                "Content Content Content Content Content Content Content "))
//                        .setLargeIcon(bitmap)
//                        .setDefaults(Notification.DEFAULT_SOUND)
//                        .setPriority(Notification.PRIORITY_HIGH)
//                        .setColor(getResources().getColor(android.R.color.holo_orange_dark))
//                        .build();
//                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                if(notificationManager != null)
//                    notificationManager.notify(getId(),notificationCompat);

            }
        });



    }

    public int getId(){
        return (int) new Date().getTime();
    }

    private void setActionBar(Toolbar toolbar) {
    }

    // Adding Logout Funtionality
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout_menu:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                mRef.child("online").setValue("off");
                finish();
                break;
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRef.child("online").setValue("off");
    }
}

