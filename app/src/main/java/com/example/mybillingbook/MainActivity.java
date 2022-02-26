package com.example.mybillingbook;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Following will be the code, to hide the action abr from the splash screen activity
        //Objects.requireNonNull(getSupportActionBar()).hide();

        //Following is the code to change the color of the status bar for the splash screen activityy
        Window window=MainActivity.this.getWindow();

            window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark));


        initialiize(); //Followingg is the method, that will initialize all the objects wit their respecctive ids


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //Here, we need to check if the user is laready loggged in or not
                if (FirebaseAuth.getInstance().getCurrentUser()==null)
                {
                    //Here, it means that the current user is not logged in
                    Intent i=new Intent(getApplicationContext(),LoginActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();


                }
                else
                {
                    //Here, it menas that the user is already logged in and we have to take  the user to the home
                    Intent i=new Intent(getApplicationContext(),HomeActivity.class);
                    startActivity(i);
                    finish();
                }


            }
        },3000);







    }



    private void initialiize() {

    }
}