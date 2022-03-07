package com.poma.restaurant.menu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.poma.restaurant.R;
import com.poma.restaurant.login.Activity_Account;
import com.poma.restaurant.login.Activity_First_Access;
import com.poma.restaurant.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Activity_Menu extends AppCompatActivity {
    private static final String TAG_LOG = Activity_Menu.class.getName();
    private FirebaseAuth mAuth;
    private Button btn_logout;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private User currentUser2;
    private NotificationManager nm;
    private int SIMPLE_NOTIFICATION_ID = 1;


    //TODO Da buttare
    private String id_city ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG_LOG, "on create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        this.mAuth= FirebaseAuth.getInstance();
        this.btn_logout= (Button)findViewById(R.id.button_menu_logout);

        //notifiche
        receiveNotifications();


        //Controllo login (capisco se anonimo, utente o admin
        this.currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Log.d(TAG_LOG, "C'è utente");
            btn_logout.setVisibility(View.VISIBLE);
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
                                set_for_admin();
                            }
                            else {
                                set_for_user();
                            }
                        }
                    }
                }
            });
        }
        else {
            set_for_anonymous();
            Log.d(TAG_LOG, "Non c'è utente, accesso anonimo");
        }

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(Activity_Menu.this, Activity_First_Access.class);
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Log.d(TAG_LOG, "Log out");
                mAuth.signOut();
                Toast.makeText(Activity_Menu.this, "Log Out", Toast.LENGTH_SHORT).show();

                startActivity(in);
                finish();
            }
        });

        Button btn = (Button)findViewById(R.id.button_menu_modificaaccount);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(Activity_Menu.this, Activity_Account.class);
                Log.d(TAG_LOG, "click account");

                startActivity(in);
            }
        });


        Button btn_map = (Button)findViewById(R.id.button_menu_map);
        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                getCityIdByName(new FirestoreCallBackCitiesId() {
                    @Override
                    public String onCallback(List<String> list) {
                        Log.d(TAG_LOG, "Bottone di test"+" -> trovo id città: "+list.get(0));
                        id_city = list.get(0);
                        return id_city.toString();

                    }
                }, "Milano");
                 */

                FirestoreCallBackCitiesId call = new FirestoreCallBackCitiesId() {
                    @Override
                    public String onCallback(List<String> list) {
                        Log.d(TAG_LOG, "istanza int -"+" trovo id città: "+list.get(0));
                        id_city = list.get(0);
                        return id_city.toString();

                    }
                };

                getCityIdByName(call, "Milano");



                Log.d(TAG_LOG, "Bottone di test"+" -> fuori da onCallBack"+id_city);





            //fine onclick
            }
        });
    }

    //Correzioni al layout sulla base del tipo di utente
    private void set_for_admin(){
        this.btn_logout.setText("LOGOUT (ADMIN)");
    }

    private void set_for_user(){
        this.btn_logout.setText("LOGOUT");
    }

    private void set_for_anonymous(){
        this.btn_logout.setVisibility(View.GONE);
    }



    @Override
    protected void onStart() {
        super.onStart();
        //controllo la presenza di utenti loggati
        Log.d(TAG_LOG, "Controllo ci sia un utente loggato");

        check_user();


    }

    //controllo la presenza di un utente loggato.
    //per farlo viene utilizzato Firestore e SharedPreferences. Viene anche confrontato l'id degli utenti ricavato nei due modi
    private void check_user(){
        Log.d(TAG_LOG, "Controllo ci sia un utente loggato");
        this.currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Log.d(TAG_LOG, "Trovato utente con Firestore, id: "+this.currentUser.getUid());
        }
        else {
            Log.d(TAG_LOG, "Non trovato utente con Firestore -> finish()");
            finish();
        }
        this.currentUser2 = User.load(this);
        if (currentUser2 != null) {
            Log.d(TAG_LOG, "Trovato utente con Shared preferences, id: "+this.currentUser2.getID());
        }
        else {
            Log.d(TAG_LOG, "Non trovato utente con SharedPreference -> finish()");
            finish();
        }

        if (currentUser.getUid() == currentUser2.getID()){
            Log.d(TAG_LOG, "Gli utenti coincidono");
        }
        else {
            Log.d(TAG_LOG, "Errore - gli utenti non coincidono");
            finish();
        }
    }



    private void new_notify(String name){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel =  new NotificationChannel("a_n","approvation_notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "a_n")
                .setContentTitle("Notifica, id utente: "+name)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setAutoCancel(true)
                .setContentText("contenuto");


        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getApplicationContext());
        Intent notifyIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.android.com"));
        PendingIntent intent = PendingIntent.getActivity(Activity_Menu.this,0,notifyIntent,0);

        builder.setContentIntent(intent);
        managerCompat.notify(SIMPLE_NOTIFICATION_ID++, builder.build());
    }

    public void receiveNotifications(){
        db = FirebaseFirestore.getInstance();
        mAuth= FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser!=null){
            db.collection("notifications")
                    .whereEqualTo("user_id", currentUser.getUid())
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                                        new_notify(currentUser.getUid());
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



    //TODO DA BUTTARE
    public interface FirestoreCallBackCitiesId {
        String onCallback(List<String> list);
    }

    private void getCityIdByName(FirestoreCallBackCitiesId callBack, String name){
        Log.d(TAG_LOG, "getcitybyname");
        List<String> cities_id = new ArrayList<>();

        db = FirebaseFirestore.getInstance();
        db.collection("cities")
                .whereEqualTo("city", name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG_LOG, "issuccessful");

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG_LOG, "trovato id della città: "+document.getId());
                                cities_id.add(document.getId());
                            }
                            callBack.onCallback(cities_id);
                        } else {
                            Log.w(TAG_LOG, "Error getting documents.", task.getException());
                            cities_id.add("");
                        }

                    }
                });

    }

}