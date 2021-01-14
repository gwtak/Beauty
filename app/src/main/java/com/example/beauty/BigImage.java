package com.example.beauty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class BigImage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layouy_bigimage);
        Intent intent=getIntent();
        String url=intent.getStringExtra("Image");
        Glide.with(this).load(url).into((ImageView)findViewById(R.id.big_image));
    }
}