package com.poma.restaurant.menu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.poma.restaurant.databinding.ActivityMenuBinding;
import com.poma.restaurant.login.Activity_First_Access;
import com.poma.restaurant.model.Broadcast_receiver_callBack_logout;
import com.poma.restaurant.model.Notification;
import com.poma.restaurant.model.Receiver;
import com.poma.restaurant.model.User;
import com.poma.restaurant.notifications.Activity_Notifications;
import com.poma.restaurant.restaurant.Activity_Favorites;
import com.poma.restaurant.restaurant.Activity_Restaurant_Client;
import com.poma.restaurant.restaurant.Activity_Restaurants_List_Client;
import com.poma.restaurant.utilities.AsyncIntent;
import com.poma.restaurant.utilities.MyAnimationCardListener;
import com.poma.restaurant.utilities.MyAnimationTextListener;

import java.util.HashMap;
import java.util.Map;

public class Activity_Menu extends Activity_Drawer_Menu_User {

    ActivityMenuBinding activityMenuBinding;

    private static final String TAG_LOG = Activity_Menu.class.getName();
    private FirebaseAuth mAuth;
    private Button btn_logout;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private User currentUser2;
    private NotificationManager nm;
    private int SIMPLE_NOTIFICATION_ID = 1;
    private BroadcastReceiver broadcastReceiver;

    private static ListenerRegistration listener_notification;

    private static final int EDIT_REQUEST_ID = 3;

    private static final String RESTAURANT_ID_EXTRA = "com.poma.restaurant.RESTAURANT_ID_EXTRA";


    private ImageView imageView_restaurants;
    private ImageView imageView_favourites;
    private ImageView imageView_profile;
    private ImageView imageView_notifications;

    private TextView textView_restaurant;
    private TextView textView_favourites;
    private TextView textView_profile;
    private TextView textView_notifications;

    private Animation zoom_animation;

    private Animation zoom_in_image;
    private Animation zoom_out_text;


    //TODO Da buttare
    private String id_city ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG_LOG, "on create");
        super.onCreate(savedInstanceState);

        //Menu laterale
        activityMenuBinding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(activityMenuBinding.getRoot());
        allocateActivityTitle(getResources().getString(R.string.dashboard));


        this.mAuth= FirebaseAuth.getInstance();

        //notifiche
        receiveNotifications();

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






        this.imageView_restaurants = findViewById(R.id.imageView_restaurant_home_client);
        this.imageView_favourites = findViewById(R.id.imageView_favourite_home_client);
        this.imageView_profile = findViewById(R.id.imageView_profile_home_client);
        this.imageView_notifications = findViewById(R.id.imageView_notification_home_client);

        this.textView_restaurant = findViewById(R.id.textview_restaurant_menu_client);
        this.textView_favourites = findViewById(R.id.textview_favourite_menu_client);
        this.textView_profile = findViewById(R.id.textview_profile_menu_client);
        this.textView_notifications = findViewById(R.id.textview_notification_menu_client);

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


                Intent in3 = new Intent(getApplicationContext(), Activity_Restaurants_List_Client.class);
                in3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                new AsyncIntent().execute(in3, Activity_Menu.this);



            }
        });

        this.imageView_favourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAnimationCardListener.setImage(imageView_favourites);
                myAnimationCardListener.setXY(imageView_favourites.getScaleX(), imageView_favourites.getScaleY());
                create_animation(myAnimationCardListener);

                myAnimationTextListener.setImage(textView_favourites);
                myAnimationTextListener.setXY(textView_favourites.getScaleX(), textView_favourites.getScaleY());
                create_animation_text(myAnimationTextListener);

                imageView_favourites.startAnimation(zoom_in_image);
                textView_favourites.startAnimation(zoom_out_text);

                Intent in3 = new Intent(getApplicationContext(), Activity_Favorites.class);
                in3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                new AsyncIntent().execute(in3, Activity_Menu.this);

            }
        });

        this.imageView_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAnimationCardListener.setImage(imageView_profile);
                myAnimationCardListener.setXY(imageView_profile.getScaleX(), imageView_profile.getScaleY());
                create_animation(myAnimationCardListener);

                myAnimationTextListener.setImage(textView_profile);
                myAnimationTextListener.setXY(textView_profile.getScaleX(), textView_profile.getScaleY());
                create_animation_text(myAnimationTextListener);

                imageView_profile.startAnimation(zoom_in_image);
                textView_profile.startAnimation(zoom_out_text);

                Intent in3 = new Intent(getApplicationContext(), Activity_Account.class);
                in3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                new AsyncIntent().execute(in3, Activity_Menu.this);

            }
        });

        this.imageView_notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAnimationCardListener.setImage(imageView_notifications);
                myAnimationCardListener.setXY(imageView_notifications.getScaleX(), imageView_notifications.getScaleY());
                create_animation(myAnimationCardListener);

                myAnimationTextListener.setImage(textView_notifications);
                myAnimationTextListener.setXY(textView_notifications.getScaleX(), textView_notifications.getScaleY());
                create_animation_text(myAnimationTextListener);

                imageView_notifications.startAnimation(zoom_in_image);
                textView_notifications.startAnimation(zoom_out_text);

                Intent in3 = new Intent(getApplicationContext(), Activity_Notifications.class);
                in3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                new AsyncIntent().execute(in3, Activity_Menu.this);

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        //final User user;
        final Intent mainIntent;

        //risposta ad un login
        if(requestCode == EDIT_REQUEST_ID)
        {
            switch (resultCode)
            {
                case RESULT_OK:
                    Log.d(TAG_LOG, "Return from edit: OK");
                    //Toast.makeText(Activity_Menu.this, R.string.edit_restaurant, Toast.LENGTH_SHORT).show();
                    break;
                case RESULT_CANCELED:
                    Log.d(TAG_LOG, "Return from edit: CANCELED");
                    break;
            }
        }

    }



    @Override
    protected void onStart() {
        super.onStart();
        //controllo la presenza di utenti loggati
        Log.d(TAG_LOG, "Controllo ci sia un utente loggato");

        check_user();

    }

    @Override
    protected void onDestroy() {
        Log.d(TAG_LOG,"on destroy");
        super.onDestroy();
        unregisterReceiver(this.broadcastReceiver);
        Log.d(TAG_LOG,"un register receiver");
        this.listener_notification.remove();
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
            finish();
        } else {
            Toast.makeText(getBaseContext(), R.string.press_back_again_to_exit, Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }


    //controllo la presenza di un utente loggato.
    private void check_user(){

        Log.d(TAG_LOG, "Controllo ci sia un utente loggato");

        //Login Firestore
        this.currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Log.d(TAG_LOG, "Trovato utente con Firestore, id: "+this.currentUser.getUid());
        }
        else {
            Log.d(TAG_LOG, "Non trovato utente con Firestore");
            finish();
        }

        //Login Shared Preferences
        this.currentUser2 = User.load(this);
        if (currentUser2 != null) {
            Log.d(TAG_LOG, "Trovato utente con Shared preferences, id: "+this.currentUser2.getID());
        }
        else {
            Log.d(TAG_LOG, "Non trovato utente con SharedPreference");
            finish();
        }


        if (currentUser.getUid().equals(currentUser2.getID())){
            Log.d(TAG_LOG, "Gli utenti coincidono");
            this.db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map<String, Object> data = document.getData();

                            if((boolean)data.get("admin")){
                                Log.d(TAG_LOG, "L'utente è admin");
                                finish();
                            }
                            else {
                                Log.d(TAG_LOG, "L'utente è visitatore");
                            }
                        }
                    }
                }
            });
        }
        else {
            Log.d(TAG_LOG, "Errore - gli utenti non coincidono");
            finish();
        }

    }

    private void logout(){
        Log.d(TAG_LOG, "Logout - inizio procedura");

        this.mAuth.signOut();
        this.currentUser2.logout(this);
        this.listener_notification.remove();

        Intent in = new Intent(Activity_Menu.this, Activity_First_Access.class);
        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Toast.makeText(Activity_Menu.this, R.string.login, Toast.LENGTH_SHORT).show();
        startActivity(in);
        finish();
    }



    //crea una notifica per l'utente
    private void new_notify(Notification n){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel =  new NotificationChannel("a_n","approvation_notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }


        //Creo intent da associare alla notifica
        PendingIntent pending_intent;
        if (n.getType().equals(getResources().getString(R.string.new_restaurant))){
            Log.d(TAG_LOG, "Creando una notifica di tipo NUOVO RISTORANTE");
            Intent intent = new Intent(Activity_Menu.this, Activity_Restaurant_Client.class);
            intent.putExtra(RESTAURANT_ID_EXTRA, n.getUseful_id());
            Log.d(TAG_LOG, "Inserisco extra: "+RESTAURANT_ID_EXTRA+" - "+n.getUseful_id());
            int uniqueInt = (int) (System.currentTimeMillis() & 0xfffffff);
            pending_intent = PendingIntent.getActivity(Activity_Menu.this,uniqueInt,intent,0);

        }
        else {
            /*Intent notifyIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.android.com"));
            pending_intent = PendingIntent.getActivity(Activity_Menu.this,0,notifyIntent,0);*/

            Intent intent = new Intent(Activity_Menu.this, Activity_First_Access.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pending_intent = PendingIntent.getActivity(Activity_Menu.this,0,intent,0);
        }

        //Creo e mando la notifica
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "a_n")
                .setContentTitle(n.getType())
                .setSmallIcon(R.mipmap.logo_launcher_round)
                .setAutoCancel(true)
                .setContentIntent(pending_intent)
                .setContentText(n.getContent());

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getApplicationContext());
        managerCompat.notify(SIMPLE_NOTIFICATION_ID++, builder.build());
    }

    //verifica l'esistenza di notifiche per l'utente loggato (con l'id dell'utente, non mostrate, non lette)
    public void receiveNotifications(){
        this.db = FirebaseFirestore.getInstance();
        this.mAuth= FirebaseAuth.getInstance();
        this.currentUser = mAuth.getCurrentUser();
        if (this.currentUser!=null){
            Query query = this.db.collection("notifications")
                    .whereEqualTo("user_id", currentUser.getUid())
                    .whereEqualTo("showed", false).whereEqualTo("read", false);

            this.listener_notification = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot snapshots,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG_LOG, "Listen failed.", e);
                        return;
                    }

                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                Log.d(TAG_LOG, "New notify: " + dc.getDocument().getString("type"));
                                Notification n = createNotification(dc.getDocument());
                                new_notify(n);
                                deleteNotification(dc.getDocument());
                                break;
                            case MODIFIED:
                                Log.d(TAG_LOG, "Modified notify: " + dc.getDocument().getData());
                                break;
                            case REMOVED:
                                Log.d(TAG_LOG, "Removed notify: " + dc.getDocument().getData());
                                break;
                        }
                    }


                }
            });
        }

    }

    private Notification createNotification(QueryDocumentSnapshot d){
        Log.d(TAG_LOG, "Creando una notifica");
        Notification n = new Notification(d.getString("user_id"), d.getId(), d.getString("type"));
        n.setContent(d.getString("content"));
        n.setUseful_id(d.getString("useful_id"));
        Log.d(TAG_LOG, "Notifica creata correttamente, user_id: "+n.getUser_id()+", id: "+n.getId()+", type: "+n.getType());
        return n;
    }

    //Imposta la notifica come "mostrata" in questo modo non verrà più presentata
    private void deleteNotification(QueryDocumentSnapshot d){
        Map<String, Object> updates = new HashMap<>();
        updates.put("showed", true);

        DocumentReference document = this.db.collection("notifications").document(d.getId());
        document.set(updates, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG_LOG, "aggiorno notifica -> showed: true");

                        }
                        else{
                            Log.d(TAG_LOG, "problemi aggiornamento notifica");

                        }
                    }
                });

    }



}