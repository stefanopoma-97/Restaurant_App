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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.poma.restaurant.R;
import com.poma.restaurant.databinding.ActivityMenuAdminBinding;
import com.poma.restaurant.login.Activity_First_Access;
import com.poma.restaurant.model.Broadcast_receiver_callBack_logout;
import com.poma.restaurant.model.Notification;
import com.poma.restaurant.model.Receiver;
import com.poma.restaurant.model.User;

import java.util.HashMap;
import java.util.Map;

public class Activity_Menu_Admin extends Activity_Drawer_Menu_Admin {

    ActivityMenuAdminBinding activityMenuAdminBinding;

    private static final String TAG_LOG = Activity_Menu_Admin.class.getName();
    private FirebaseAuth mAuth;
    private Button btn_logout;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private User currentUser2;
    private NotificationManager nm;
    private int SIMPLE_NOTIFICATION_ID = 1;
    private BroadcastReceiver broadcastReceiver;

    private static ListenerRegistration listener_notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_admin);


        //Menu laterale
        activityMenuAdminBinding = ActivityMenuAdminBinding.inflate(getLayoutInflater());
        setContentView(activityMenuAdminBinding.getRoot());
        allocateActivityTitle("Dashboard admin");


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
    }

    @Override
    protected void onStart() {
        super.onStart();
        //controllo la presenza di utenti loggati
        Log.d(TAG_LOG, "Controllo ci sia un utente loggato");

        check_user_admin();

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
            //finish();
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }

    //controllo la presenza di un utente loggato.
    //per farlo viene utilizzato Firestore e SharedPreferences. Viene anche confrontato l'id degli utenti ricavato nei due modi
    private void check_user_admin(){

        Log.d(TAG_LOG, "Controllo ci sia un utente loggato");

        //Login Firestore
        this.currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            Log.d(TAG_LOG, "Non trovato utente con Firestore");
            finish();
        }

        //Login Shared Preferences
        this.currentUser2 = User.load(this);
        if (currentUser2 == null) {
            Log.d(TAG_LOG, "Non trovato utente con Firestore");
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
                                Log.d(TAG_LOG, "Utente admin");
                            }
                            else {
                                Log.d(TAG_LOG, "Errore - si tratta di un utente non admin");
                                finish();
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

        Intent in = new Intent(Activity_Menu_Admin.this, Activity_First_Access.class);
        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Toast.makeText(Activity_Menu_Admin.this, "Log Out", Toast.LENGTH_SHORT).show();
        startActivity(in);
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
        PendingIntent intent = PendingIntent.getActivity(Activity_Menu_Admin.this,0,notifyIntent,0);

        builder.setContentIntent(intent);
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
                                //TODO farle cancellare
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
}