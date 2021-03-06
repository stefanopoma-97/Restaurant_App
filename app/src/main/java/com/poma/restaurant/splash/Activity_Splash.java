package com.poma.restaurant.splash;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.poma.restaurant.R;
import com.poma.restaurant.login.Activity_First_Access;

import java.util.Timer;
import java.util.TimerTask;

public class Activity_Splash extends AppCompatActivity {

    //Tag for log
    private static final String TAG_LOG = Activity_Splash.class.getName();

    //Key for save state
    private static final String START_TIME_KEY = "com.poma.restaurant.START_TIME_KEY";

    //tempo che dobbiamo aspettare (1 secondo)
    private static final long MIN_WAIT_INTERVAL = 100L;
    //starting time
    private long startTime = -1L;
    private static boolean go = true;


    private FirebaseAuth mAuth;



    //STATE
    //salva lo stato mantenendo il valore di startTime
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putLong(START_TIME_KEY, startTime);
        super.onSaveInstanceState(outState);
        Log.d(TAG_LOG,"Save state: "+START_TIME_KEY);
    }

    //Ripristina lo stato prendendo il valor di startTime
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.startTime = savedInstanceState.getLong(START_TIME_KEY);
        Log.d(TAG_LOG,"Retrive state: "+START_TIME_KEY);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG_LOG,"on Create");
        go=true;
        setContentView(R.layout.activity_splash);

        //prendo il valore salvato
        if(savedInstanceState != null) {
            this.startTime = savedInstanceState.getLong(START_TIME_KEY);
        }

        final ImageView logoImageView = (ImageView) findViewById(R.id.splash_imageview);

        //cliccando sull'immagine si pu?? andare avanti se sono passati MIN_WAIT_TIME secondi
        logoImageView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                long elapsedTime = SystemClock.uptimeMillis() - startTime; //tempo passato
                if(elapsedTime >= MIN_WAIT_INTERVAL)
                {
                    Log.d(TAG_LOG, "Click dopo un tempo sufficiente");
                    goAhead();
                } else {
                    Log.d(TAG_LOG, "Click troppo presto");
                }
                return false;
            }
        });


    }

    //se non ?? ancora impostato mette l'ora attuale
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG_LOG, "on start");
        if(startTime == -1L) {
            startTime = SystemClock.uptimeMillis();
        }

        //Dopo 5 secondi viene invocato il metodo goAhead() in automatco
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG_LOG, "Run timer ...");
                if (go){
                    Log.d(TAG_LOG, "Eseguo goAhead ...");
                    goAhead();
                }

            }
        }, 5000);


        //controlla che l'utente non sia loggato, nel caso effettua un logout automatico
        mAuth= FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //Toast.makeText(Activity_Register.this, "C'?? utente: "+currentUser, Toast.LENGTH_SHORT).show();
            mAuth.signOut();
        }


        Log.d(TAG_LOG, "started");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG_LOG, "on destroy");
    }

    //Passo a Activity_First_Access
    private void goAhead() {
        Log.d(TAG_LOG, "Metodo Go Ahead");
        go=false;
        final Intent intent = new Intent(this, Activity_First_Access.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        Log.d(TAG_LOG, "Creo intent e lo mando a Activity_First_Access. L'activity attuale viene distrutta");
        startActivity(intent);
        finish(); //destroy the activity
    }

}