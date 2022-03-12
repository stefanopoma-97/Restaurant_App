package com.poma.restaurant.menu;

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
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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
import com.poma.restaurant.databinding.ActivityMenuBinding;
import com.poma.restaurant.databinding.ActivityTestBinding;
import com.poma.restaurant.login.Activity_First_Access;
import com.poma.restaurant.model.Broadcast_receiver_callBack_logout;
import com.poma.restaurant.model.Notification;
import com.poma.restaurant.model.Receiver;
import com.poma.restaurant.model.Restaurant;
import com.poma.restaurant.model.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Activity_Test extends Activity_Drawer_Menu_User {

    ActivityTestBinding activityTestBinding;

    private static final String TAG_LOG = Activity_Test.class.getName();
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG_LOG, "on create");
        super.onCreate(savedInstanceState);

        //Menu laterale
        activityTestBinding = ActivityTestBinding.inflate(getLayoutInflater());
        setContentView(activityTestBinding.getRoot());
        allocateActivityTitle("Test");


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






        Button btn_notifica = (Button)findViewById(R.id.btn_test_crea_notifica);
        btn_notifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nuova_notifica_ristorante();
            }
        });


        Button btn_ristorante = (Button)findViewById(R.id.btn_test_crea_ristorante);
        btn_ristorante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nuovo_ristorante();

            }
        });

    }



    @Override
    protected void onStart() {
        super.onStart();
        //controllo la presenza di utenti loggati
        Log.d(TAG_LOG, "Controllo ci sia un utente loggato");


    }

    @Override
    protected void onDestroy() {
        Log.d(TAG_LOG,"on destroy");
        super.onDestroy();
        unregisterReceiver(this.broadcastReceiver);
        Log.d(TAG_LOG,"un register receiver");
    }






    private void logout(){
        Log.d(TAG_LOG, "Logout - inizio procedura");
        finish();
    }



    //crea una notifica per l'utente
    //TODO da personalizzare con intent che rimanda alla pagina della notifica
    private void new_notify(Notification n){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel =  new NotificationChannel("a_n","approvation_notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "a_n")
                .setContentTitle("Notifica: "+ n.getType())
                .setSmallIcon(R.mipmap.logo_launcher_round)
                .setAutoCancel(true)
                .setContentText(n.getContent());


        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getApplicationContext());
        Intent notifyIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.android.com"));
        PendingIntent intent = PendingIntent.getActivity(Activity_Test.this,0,notifyIntent,0);

        builder.setContentIntent(intent);
        managerCompat.notify(SIMPLE_NOTIFICATION_ID++, builder.build());
    }


    //TODO da personalizzare sulla base del tipo di notifica
    private Notification createNotification(QueryDocumentSnapshot d){
        Log.d(TAG_LOG, "Creando una notifica");
        Notification n = new Notification(d.getString("user_id"), d.getId(), d.getString("type"));
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


        /*
        this.db.collection("notifications").document(d.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG_LOG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG_LOG, "Error deleting document", e);
                    }
                });;

         */

    }

    private void nuovo_ristorante(){
        Restaurant r = new Restaurant();

        r.setName("Ristorante 1");
        r.setDescription("Descrizione ristorante");
        r.setEmail("mail@mail.it");
        r.setAddress("Via ancona 13");
        r.setCity("Brescia");
        r.setPhone("345664433");
        r.setCategory("Pizzeria");
        r.setImageUrl("https://firebasestorage.googleapis.com/v0/b/restaurant-app-f5ff3.appspot.com/o/1646757849636.jpg?alt=media&token=6ec76b56-bdb2-4df4-ae08-d4c240144eee");
        r.setAdmin_id("TcD9ubVpgdfgmIBn26R5W7SHFPG3");
        List<String> array = new ArrayList<String>();
        array.add("tag1");
        array.add("tag2");
        r.setTags(array);
        Integer n = new Integer(100);
        Integer n2 = new Integer(4);
        r.setN_reviews(n);
        r.setVote(n2);

        db = FirebaseFirestore.getInstance();
        db.collection("restaurants").add(r).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG_LOG, "aggiunta ristorante");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG_LOG, "Error adding ristorante", e);
                    }
                });



    }


    private void nuova_notifica_ristorante(){
        Log.d(TAG_LOG, "Creazione notifica");

        Notification n = new Notification();
        n.setUser_id(mAuth.getCurrentUser().getUid());
        n.setRead(false);
        n.setShowed(false);
        n.setContent("è stato aggiunto un nuovo ristorante alla nostra applicazione");
        n.setType("Nuovo ristorante");
        n.setUseful_id("id ristorante");
        Calendar calendar = Calendar.getInstance();
        long timeMilli2 = calendar.getTimeInMillis();
        n.setDate(timeMilli2);
        Log.d(TAG_LOG, "notifica istanziata: "+n.getType());



        db = FirebaseFirestore.getInstance();
        db.collection("notifications").add(n).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG_LOG, "aggiunta notifica ristorante");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG_LOG, "Error adding notifica", e);
                    }
                });
    }


    private void direction(){
        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://maps.google.com/maps?daddr=via ancona 13 - palazzolo sull'oglio"));
        startActivity(intent);
    }

    private void call(){
        String number = "1234567890";
        Uri num = Uri.parse("tel:" + number);
        Intent dial = new Intent(Intent.ACTION_DIAL);
        dial.setData(num);
        startActivity(dial);

    }

    private void create_favourite(){
        
    }

}