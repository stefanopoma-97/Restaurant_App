package com.poma.restaurant.login;

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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
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
import com.poma.restaurant.menu.Activity_Menu;
import com.poma.restaurant.menu.Activity_Menu_Admin;
import com.poma.restaurant.menu.Activity_Menu_Anonymous;
import com.poma.restaurant.model.User;
import com.poma.restaurant.notifications.Notifications;
import com.poma.restaurant.utilities.Action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Activity_First_Access extends AppCompatActivity implements Fragment_Access.FirstAccessInterface {
    //Log
    private static final String TAG_LOG = Activity_First_Access.class.getName();

    //extra
    private static final String USER_LOGIN_EXTRA = "com.poma.restaurant.USER_LOGIN_EXTRA ";
    private static final int LOGIN_REQUEST_ID = 1;
    private static final int REGISTRATION_REQUEST_ID = 2;

    private FirebaseAuth mAuth;

    private User user;


    //TODO tutta la parte di gestione delle notifiche è da spostare
    private NotificationManager nm;
    private int SIMPLE_NOTIFICATION_ID = 1;
    private Timestamp time = null;
    private static List<String> cities = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG_LOG,"on create");
        setContentView(R.layout.activity_first_access);



        ImageView login_rapido = (ImageView) findViewById(R.id.login_rapido);
        login_rapido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_rapido();
            }
        });

        FloatingActionButton btn_login_rapido = (FloatingActionButton)findViewById(R.id.floatingActionButton_login_user);
        FloatingActionButton btn_login_rapido_admin = (FloatingActionButton)findViewById(R.id.floatingActionButton_login_admin);

        btn_login_rapido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_rapido();
            }
        });

        btn_login_rapido_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG_LOG, "Login admin rapido");
                login_rapido_admin();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG_LOG, "on start");
        mAuth= FirebaseAuth.getInstance();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Log.d(TAG_LOG, "Trovato utente con Firestore -> Logout");
            mAuth.signOut();

        }
        else {
            //Toast.makeText(Activity_Register.this, "Non c'è utente: ", Toast.LENGTH_SHORT).show();
        }

        User u = User.load(this);
        if (u!=null){
            Log.d(TAG_LOG, "Trovato utente in SharedPreferences -> Logout");
            u.logout(this);
        }
    }

    //doppio click su back per uscire dall'applicazione
    private long pressedTime;
    @Override
    public void onBackPressed() {
        Log.d(TAG_LOG,"Back button");
        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            Log.d(TAG_LOG,"2 Back button");
            finish();
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }


    //Richiama Activity_Login e attende i risultati
    @Override
    public void login(Boolean user) {
        Log.d(TAG_LOG, "Login");
        final Intent intent = new Intent(this, Activity_Login.class);
        intent.putExtra(USER_LOGIN_EXTRA, user);
        startActivityForResult(intent, LOGIN_REQUEST_ID);
        Log.d(TAG_LOG, "send Intent for result. Login with user: "+user+" (True -> User)");

    }

    public void login_rapido() {

        String email = "stefano1997poma97@gmail.com";
        String password = "maziamazia";

        Log.d(TAG_LOG, "inizio metodo login, con tipo utente: "+this.user+" (True=user, False=Admin)");


        //LOGIN UTENTE

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG_LOG, "signInWithEmail:success");

                                //PRENDO UTENTE
                                Log.d(TAG_LOG, "inizio metodo retrive user");

                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                DocumentReference docRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                Map<String, Object> data = document.getData();
                                                Log.d(TAG_LOG, "DocumentSnapshot data: " + data);
                                                if(!(boolean)data.get("admin")){
                                                    User user = User.create((String) data.get("username"),(String) data.get("password"))
                                                            .withSurname((String) data.get("surname"))
                                                            .withName((String) data.get("name"))
                                                            .withLocation((String) data.get("location"))
                                                            .withEmail((String) data.get("email"))
                                                            .withDate((long) data.get("date"))
                                                            .withId(mAuth.getCurrentUser().getUid());

                                                    Log.d(TAG_LOG, "oggetto User creato con successo");

                                                    //Shared preferences
                                                    user.save(getApplicationContext());
                                                    final Intent mainIntent;
                                                    mainIntent = new Intent(Activity_First_Access.this, Activity_Menu.class);
                                                    startActivity(mainIntent);

                                                    Toast.makeText(Activity_First_Access.this, "Login", Toast.LENGTH_SHORT).show();
                                                    Log.d(TAG_LOG, "start menù with user:"+user.getUsername());
                                                    finish();
                                                }
                                                else {
                                                    Log.d(TAG_LOG, "questo utente è admin (sto facendo login per utente normale)");


                                                }


                                            } else {
                                                Log.d(TAG_LOG, "No such document");
                                            }
                                        } else {
                                            Log.d(TAG_LOG, "get failed with ", task.getException());
                                        }
                                    }
                                });





                            } else {

                                // If sign in fails, display a message to the user.
                                Log.w(TAG_LOG, "signInWithEmail:failure", task.getException());
                                //Toast.makeText(Activity_Login.this, getResources().getString(R.string.authentication_failed), Toast.LENGTH_SHORT).show();

                                try {
                                    throw task.getException();
                                }
                                catch(FirebaseAuthInvalidUserException e) {

                                    Log.d(TAG_LOG, "no user found: "+password);


                                }
                                catch(FirebaseAuthInvalidCredentialsException e) {
                                    Log.d(TAG_LOG, "credential exception, error code: "+e.getErrorCode());
                                    if (e.getErrorCode() == "ERROR_INVALID_EMAIL"){

                                    }
                                    else if(e.getErrorCode() == "ERROR_WRONG_PASSWORD"){

                                    }


                                }


                                catch(Exception e) {
                                    Log.e(TAG_LOG, e.getMessage());

                                }


                            }
                            //progressBarr(false);
                        }

                    });


    }

    public void login_rapido_admin() {

        String email = "admin@admin.it";
        String password = "adminadmin";
        Log.d(TAG_LOG, "setto mail e password");



        //LOGIN UTENTE

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG_LOG, "signInWithEmail:success");

                            //PRENDO UTENTE
                            Log.d(TAG_LOG, "inizio metodo retrive user");

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference docRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Map<String, Object> data = document.getData();
                                            Log.d(TAG_LOG, "DocumentSnapshot data: " + data);
                                            if((boolean)data.get("admin")){
                                                User user = User.create((String) data.get("username"),(String) data.get("password"))
                                                        .withSurname((String) data.get("surname"))
                                                        .withName((String) data.get("name"))
                                                        .withLocation((String) data.get("location"))
                                                        .withEmail((String) data.get("email"))
                                                        .withDate((long) data.get("date"))
                                                        .withId(mAuth.getCurrentUser().getUid());

                                                Log.d(TAG_LOG, "oggetto User creato con successo");

                                                //Shared preferences
                                                user.save(getApplicationContext());
                                                final Intent mainIntent;
                                                mainIntent = new Intent(Activity_First_Access.this, Activity_Menu_Admin.class);
                                                startActivity(mainIntent);

                                                Toast.makeText(Activity_First_Access.this, "Login", Toast.LENGTH_SHORT).show();
                                                Log.d(TAG_LOG, "start menù with user:"+user.getUsername());
                                                finish();
                                            }
                                            else {
                                                Log.d(TAG_LOG, "questo utente è admin (sto facendo login per utente normale)");


                                            }


                                        } else {
                                            Log.d(TAG_LOG, "No such document");
                                        }
                                    } else {
                                        Log.d(TAG_LOG, "get failed with ", task.getException());
                                    }
                                }
                            });





                        } else {

                            // If sign in fails, display a message to the user.
                            Log.w(TAG_LOG, "signInWithEmail:failure", task.getException());
                            //Toast.makeText(Activity_Login.this, getResources().getString(R.string.authentication_failed), Toast.LENGTH_SHORT).show();

                            try {
                                throw task.getException();
                            }
                            catch(FirebaseAuthInvalidUserException e) {

                                Log.d(TAG_LOG, "no user found: "+password);


                            }
                            catch(FirebaseAuthInvalidCredentialsException e) {
                                Log.d(TAG_LOG, "credential exception, error code: "+e.getErrorCode());
                                if (e.getErrorCode() == "ERROR_INVALID_EMAIL"){

                                }
                                else if(e.getErrorCode() == "ERROR_WRONG_PASSWORD"){

                                }


                            }


                            catch(Exception e) {
                                Log.e(TAG_LOG, e.getMessage());

                            }


                        }
                        //progressBarr(false);
                    }

                });


    }

    //Richiama Activity_Register e attende i risultati
    @Override
    public void register(Boolean user) {
        Log.d(TAG_LOG, "Register");
        final Intent intent = new Intent(this, Activity_Register.class);
        intent.putExtra(USER_LOGIN_EXTRA, user);
        startActivityForResult(intent, REGISTRATION_REQUEST_ID);
        Log.d(TAG_LOG, "send Intent for result. Login with user: "+user+" False -> Admin");

    }

    //Richiama Activity_Menu
    @Override
    public void anonymous_access(Boolean user) {
        Log.d(TAG_LOG, "Anonymous access");
        Intent mainIntent = new Intent(Activity_First_Access.this, Activity_Menu_Anonymous.class);
        startActivity(mainIntent);
        Toast.makeText(Activity_First_Access.this, "Anonymous access", Toast.LENGTH_SHORT).show();
        finish();
        Log.d(TAG_LOG, "start menù anonymous");
    }

    //Richiama Activity_Login e attende i risultati
    @Override
    public void login_admin(Boolean user) {
        Log.d(TAG_LOG, "Login Admin");
        final Intent intent = new Intent(this, Activity_Login.class);
        intent.putExtra(USER_LOGIN_EXTRA, user);
        startActivityForResult(intent, LOGIN_REQUEST_ID);
        Log.d(TAG_LOG, "send Intent for result. Login with user: "+user);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        //final User user;
        final Intent mainIntent;

        //risposta ad un login
        if(requestCode == LOGIN_REQUEST_ID)
        {
            switch (resultCode)
            {
                case RESULT_OK:
                    Log.d(TAG_LOG, "Return from login (user): OK");
                    //user = data.getParcelableExtra(User.USER_DATA_EXTRA);
                    //Log.d(TAG_LOG, "start menù with user: "+user.getUsername());

                    Log.d(TAG_LOG, "Id utente Firebase: "+mAuth.getCurrentUser().getUid());
                    this.user = User.load(this);
                    Log.d(TAG_LOG, "Shared preference, utente loggato con ID: "+this.user.getID());
                    mainIntent = new Intent(Activity_First_Access.this, Activity_Menu.class);
                    startActivity(mainIntent);

                    Toast.makeText(Activity_First_Access.this, "Login", Toast.LENGTH_SHORT).show();
                    Log.d(TAG_LOG, "start menù with user:"+user.getUsername());
                    finish();
                    break;
                case Action.RESULT_OK_ADMIN:
                    Log.d(TAG_LOG, "Return from login (admin): OK");
                    Log.d(TAG_LOG, "Id utente Firebase: "+mAuth.getCurrentUser().getUid());
                    this.user = User.load(this);
                    Log.d(TAG_LOG, "Shared preference, utente loggato con ID: "+this.user.getID());
                    mainIntent = new Intent(Activity_First_Access.this, Activity_Menu_Admin.class);
                    startActivity(mainIntent);
                    Toast.makeText(Activity_First_Access.this, "Login Admin", Toast.LENGTH_SHORT).show();
                    Log.d(TAG_LOG, "start menù with user:"+user.getUsername());
                    finish();
                    break;

                case RESULT_CANCELED:
                    Log.d(TAG_LOG, "Return from login: CANCELED");
                    break;
            }
        }
        //risposta alla registrazione
        else if(requestCode == REGISTRATION_REQUEST_ID)
        {
            switch (resultCode)
            {
                case RESULT_OK:
                    Log.d(TAG_LOG, "Return from register (user): OK");
                    //user = data.getParcelableExtra(User.USER_DATA_EXTRA);
                    Log.d(TAG_LOG, "Id utente Firebase: "+mAuth.getCurrentUser().getUid());
                    this.user = User.load(this);
                    Log.d(TAG_LOG, "Id utente SharedPreferences: "+this.user.getID());
                    mainIntent = new Intent(Activity_First_Access.this, Activity_Menu.class);
                    startActivity(mainIntent);
                    Toast.makeText(Activity_First_Access.this, "Registered", Toast.LENGTH_SHORT).show();
                    Log.d(TAG_LOG, "start menù with user: "+user.getUsername());
                    finish();
                    break;
                case RESULT_CANCELED:
                    Log.d(TAG_LOG, "Return from register: CANCELED");
                    break;
            }
        }
    }



    //Gestione delle notifiche (non da usare qua)
    //TODO queta gestione è da spostare nelle activity dopo il login
    private void notify_new_city(String name){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel =  new NotificationChannel("a_n","approvation_notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "a_n")
                .setContentTitle("New City: "+name)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setAutoCancel(true)
                .setContentText("contenuto");


        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getApplicationContext());
        Intent notifyIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.android.com"));
        PendingIntent intent = PendingIntent.getActivity(Activity_First_Access.this,0,notifyIntent,0);

        builder.setContentIntent(intent);
        managerCompat.notify(SIMPLE_NOTIFICATION_ID++, builder.build());
    }

    public void receiveNotifications_cities(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("n")
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
                                    if (!cities.contains(dc.getDocument().getString("city"))){
                                        Log.d(TAG_LOG, "New city: " + dc.getDocument().getString("city"));
                                        cities.add(dc.getDocument().getString("city"));
                                        notify_new_city(dc.getDocument().getString("city"));
                                    }
                                    break;
                                case MODIFIED:
                                    Log.d(TAG_LOG, "Modified city: " + dc.getDocument().getData());
                                    break;
                                case REMOVED:
                                    Log.d(TAG_LOG, "Removed city: " + dc.getDocument().getData());
                                    break;
                            }
                        }


                    }
                });
    }

}