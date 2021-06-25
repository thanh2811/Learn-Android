package com.example.chattogether;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ViewImage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        Intent i = getIntent();
        String imgUrl = i.getStringExtra("src");

        ImageView img = findViewById(R.id.img);
        if(imgUrl.equals("default"))
            img.setImageResource(R.drawable.send);
        else
            Glide.with(this).load(imgUrl).into(img);

    }
}