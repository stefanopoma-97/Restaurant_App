package com.poma.restaurant;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.poma.restaurant.login.Activity_First_Access;

public class Activity_Splash extends AppCompatActivity {
    private FirebaseAuth mAuth;

    //tempo che dobbiamo aspettare
    private static final long MIN_WAIT_INTERVAL = 1000L;

    //starting time
    private long startTime = -1L;

    //Tag for log
    private static final String TAG_LOG = Activity_Splash.class.getName();

    //Key for save state
    private static final String START_TIME_KEY = "com.poma.restaurant.START_TIME_KEY";


    //STATE
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putLong(START_TIME_KEY, startTime);
        super.onSaveInstanceState(outState);
        Log.d(TAG_LOG,"Save state: "+START_TIME_KEY);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.startTime = savedInstanceState.getLong(START_TIME_KEY);
        Log.d(TAG_LOG,"Retrive state: "+START_TIME_KEY);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final ImageView logoImageView = (ImageView) findViewById(R.id.splash_imageview);

        //reference and behaviour to the image
        logoImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                long elapsedTime = SystemClock.uptimeMillis() - startTime; //tempo passato
                if(elapsedTime >= MIN_WAIT_INTERVAL)
                {
                    Log.d(TAG_LOG, "OK! Let's go ahead...");
                    goAhead();
                } else {
                    Log.d(TAG_LOG, "Too early!");
                }
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(startTime == -1L) {
            startTime = SystemClock.uptimeMillis();
        }

        Log.d(TAG_LOG, "on start");
        mAuth= FirebaseAuth.getInstance();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //Toast.makeText(Activity_Register.this, "C'è utente: "+currentUser, Toast.LENGTH_SHORT).show();
            mAuth.signOut();

        }
        else {
            //Toast.makeText(Activity_Register.this, "Non c'è utente: ", Toast.LENGTH_SHORT).show();

        }

        Log.d(TAG_LOG, "Activity started");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG_LOG, "Activity destroyed");
    }

    private void goAhead() {
        final Intent intent = new Intent(this, Activity_First_Access.class);
        startActivity(intent);
        finish(); //destroy the activity
    }

}