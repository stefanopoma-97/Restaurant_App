package com.poma.restaurant.menu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.poma.restaurant.R;
import com.poma.restaurant.account.Activity_Account;
import com.poma.restaurant.account.Activity_Edit_Account;
import com.poma.restaurant.databinding.ActivityMenuAnonymousBinding;
import com.poma.restaurant.databinding.ActivityMenuBinding;
import com.poma.restaurant.login.Activity_First_Access;
import com.poma.restaurant.login.Activity_Register;
import com.poma.restaurant.model.Broadcast_receiver_callBack_logout;
import com.poma.restaurant.model.Notification;
import com.poma.restaurant.model.Receiver;
import com.poma.restaurant.model.Restaurant;
import com.poma.restaurant.model.User;
import com.poma.restaurant.notifications.Activity_Notifications;
import com.poma.restaurant.notifications.Activity_Notifications_Admin;
import com.poma.restaurant.restaurant.Activity_Restaurants_List_Admin;
import com.poma.restaurant.restaurant.Activity_Restaurants_List_Anonymous;
import com.poma.restaurant.utilities.Action;
import com.poma.restaurant.utilities.AsyncIntent;
import com.poma.restaurant.utilities.AsyncIntentFinish;
import com.poma.restaurant.utilities.MyAnimationCardListener;
import com.poma.restaurant.utilities.MyAnimationTextListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Activity_Menu_Anonymous extends Activity_Drawer_Menu_Anonymous {

    ActivityMenuAnonymousBinding activityMenuAnonymousBinding;

    private static final String TAG_LOG = Activity_Menu_Anonymous.class.getName();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private User currentUser2;
    private BroadcastReceiver broadcastReceiver;

    private static ListenerRegistration listener_notification;

    private ImageView imageView_restaurants;
    private ImageView imageView_login;

    private TextView textView_restaurant;
    private TextView textView_login;


    private Animation zoom_animation;

    private Animation zoom_in_image;
    private Animation zoom_out_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_anonymous);

        //Menu laterale
        activityMenuAnonymousBinding = ActivityMenuAnonymousBinding.inflate(getLayoutInflater());
        setContentView(activityMenuAnonymousBinding.getRoot());
        allocateActivityTitle(getResources().getString(R.string.dashboard_anonymous));

        this.mAuth= FirebaseAuth.getInstance();


        //Riceve broadcast
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.poma.restaurant.broadcastreceiversandintents.BROADCAST_LOGOUT");
        this.broadcastReceiver = new Receiver(new Broadcast_receiver_callBack_logout() {
            @Override
            public void onCallBack() {
                Log.d(TAG_LOG, "Receiver onCallBack");
                logout();
            }
        });
        registerReceiver(this.broadcastReceiver, intentFilter);


        this.imageView_restaurants = findViewById(R.id.imageView_restaurant_anonymous);
        this.imageView_login= findViewById(R.id.imageView_login_home_anonymous);


        this.textView_restaurant = findViewById(R.id.textview_restaurant_menu_anonymous);
        this.textView_login = findViewById(R.id.textview_login_home_anonymous);


        MyAnimationCardListener myAnimationCardListener = new MyAnimationCardListener();
        MyAnimationTextListener myAnimationTextListener = new MyAnimationTextListener();

        this.imageView_restaurants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //load animation
                myAnimationCardListener.setImage(imageView_restaurants);
                myAnimationCardListener.setXY(imageView_restaurants.getScaleX(), imageView_restaurants.getScaleY());

                create_animation(myAnimationCardListener);

                myAnimationTextListener.setImage(textView_restaurant);
                myAnimationTextListener.setXY(textView_restaurant.getScaleX(), textView_restaurant.getScaleY());
                create_animation_text(myAnimationTextListener);


                //start animation
                imageView_restaurants.startAnimation(zoom_in_image);
                textView_restaurant.startAnimation(zoom_out_text);


                Intent in3 = new Intent(getApplicationContext(), Activity_Restaurants_List_Anonymous.class);
                in3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                new AsyncIntent().execute(in3, Activity_Menu_Anonymous.this);

            }
        });

        this.imageView_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //load animation
                myAnimationCardListener.setImage(imageView_login);
                myAnimationCardListener.setXY(imageView_login.getScaleX(), imageView_login.getScaleY());

                create_animation(myAnimationCardListener);

                myAnimationTextListener.setImage(textView_login);
                myAnimationTextListener.setXY(textView_login.getScaleX(), textView_login.getScaleY());
                create_animation_text(myAnimationTextListener);


                //start animation
                imageView_login.startAnimation(zoom_in_image);
                textView_login.startAnimation(zoom_out_text);


                Intent in3 = new Intent(getApplicationContext(), Activity_First_Access.class);
                in3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                new AsyncIntent().execute(in3, Activity_Menu_Anonymous.this);

            }
        });

    }

    private void create_animation(MyAnimationCardListener listener){
        Float scalex = listener.getScalex();
        Float scaley = listener.getScalex();



        this.zoom_in_image = new ScaleAnimation(
                scalex, new Float(1.5), // Start and end values for the X axis scaling
                scaley, new Float(1.5), // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        zoom_in_image.setFillAfter(true); // Needed to keep the result of the animation
        zoom_in_image.setDuration(500);
        zoom_in_image.setAnimationListener(listener);


    }

    private void create_animation_text(MyAnimationTextListener listener){
        Float scalex = listener.getScalex();
        Float scaley = listener.getScalex();



        this.zoom_out_text = new ScaleAnimation(
                scalex, new Float(0.9), // Start and end values for the X axis scaling
                scaley, new Float(0.9), // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        zoom_out_text.setFillAfter(true); // Needed to keep the result of the animation
        zoom_out_text.setDuration(500);
        zoom_out_text.setAnimationListener(listener);


    }

    @Override
    protected void onStart() {
        super.onStart();
        //controllo la presenza di utenti loggati
        Log.d(TAG_LOG, "Controllo ci sia un utente loggato");

        check_user_anonymous();

    }

    @Override
    protected void onDestroy() {
        Log.d(TAG_LOG,"on destroy");
        super.onDestroy();
        unregisterReceiver(this.broadcastReceiver);
        Log.d(TAG_LOG,"un register receiver");
    }


    //doppio click su back per uscire dall'applicazione
    private long pressedTime;
    @Override
    public void onBackPressed() {
        Log.d(TAG_LOG,"Back button");
        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            Log.d(TAG_LOG,"2 Back button");
            logout();
            //finish();
        } else {
            Toast.makeText(getBaseContext(), R.string.press_back_again_to_exit, Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }


    private void check_user_anonymous(){

        Log.d(TAG_LOG, "Controllo ci sia un utente loggato");

        //Login Firestore
        this.currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Log.d(TAG_LOG, "Trovato utente con Firestore");
            this.mAuth.signOut();
            finish();
        }

        //Login Shared Preferences
        this.currentUser2 = User.load(this);
        if (currentUser2 != null) {
            Log.d(TAG_LOG, "Trovato utente con Firestore");
            this.currentUser2.logout(this);
            finish();
        }


    }


    private void logout(){
        Log.d(TAG_LOG, "Logout - inizio procedura");


        Intent in = new Intent(Activity_Menu_Anonymous.this, Activity_First_Access.class);
        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //Toast.makeText(Activity_Menu_Anonymous.this, "Log Out", Toast.LENGTH_SHORT).show();
        startActivity(in);
        finish();
    }

}