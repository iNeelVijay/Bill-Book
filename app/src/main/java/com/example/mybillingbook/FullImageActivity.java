package com.example.mybillingbook;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.Objects;

public class FullImageActivity extends AppCompatActivity {

    private ImageView backBtn;
    private ImageView fullImage;

    private String url="";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        //Following will be the code, to hide the action abr from the splash screen activity
        //Objects.requireNonNull(getSupportActionBar()).hide();

        //Following is the code to change the color of the status bar for the splash screen activityy
        Window window=FullImageActivity.this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(FullImageActivity.this, R.color.colorPrimaryDark));

        backBtn=findViewById(R.id.backBtn_fullImage);
        fullImage=findViewById(R.id.image_fillImage);


        Intent i=getIntent();
        url=i.getStringExtra("url");


        Picasso.get().load(url).placeholder(R.drawable.placeholder).into(fullImage);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}