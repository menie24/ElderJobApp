package com.example.elderjobapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.elderjobapp.helper.ImageHelper;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    Button findJobButton;
    Button giveJobButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.jobImageView);
        imageView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(((BitmapDrawable)imageView.getDrawable()).getBitmap(),50));
        findJobButton = (Button) findViewById(R.id.findJobButton);
        findJobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity1.class);
                intent.putExtra("user type","find job");
                startActivity(intent);
                finish();
            }
        });
        giveJobButton = (Button) findViewById(R.id.giveJobButton);
        giveJobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity1.class);
                intent.putExtra("user type","give job");
                startActivity(intent);
                finish();
            }
        });
    }

}