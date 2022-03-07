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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Activity_Menu extends AppCompatActivity {
    private static final String TAG_LOG = Activity_Menu.class.getName();
    private FirebaseAuth mAuth;
    private Button btn_logout;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private NotificationManager nm;
    private int SIMPLE_NOTIFICATION_ID = 1;

    private String id_city ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG_LOG, "on create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mAuth= FirebaseAuth.getInstance();
        btn_logout= (Button)findViewById(R.id.button_menu_logout);
        receiveNotifications();


        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Log.d(TAG_LOG, "C'è utente, metto visibile bottone");
            btn_logout.setVisibility(View.VISIBLE);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map<String, Object> data = document.getData();

                            if((boolean)data.get("admin")){
                                btn_logout.setText("LOGOUT (ADMIN)");
                            }
                        }
                    }
                }
            });
        }
        else {
            this.btn_logout.setVisibility(View.GONE);
            Log.d(TAG_LOG, "Non c'è utente, metto invisibile bottone");
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

    //TEST

    //lista




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

    @Override
    protected void onStart() {
        super.onStart();
        this.mAuth=FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            this.btn_logout.setVisibility(View.VISIBLE);
            Toast.makeText(Activity_Menu.this, "C'è utente: "+currentUser, Toast.LENGTH_SHORT).show();
        }
        else {
            this.btn_logout.setVisibility(View.GONE);
            Toast.makeText(Activity_Menu.this, "Non c'è utente: ", Toast.LENGTH_SHORT).show();

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

}