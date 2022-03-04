package com.poma.restaurant.login;

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
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.poma.restaurant.R;
import com.poma.restaurant.menu.Activity_Menu;
import com.poma.restaurant.model.User;
import com.poma.restaurant.notifications.Notifications;
import com.poma.restaurant.utilities.Action;

import java.util.ArrayList;
import java.util.List;

public class Activity_First_Access extends AppCompatActivity implements Fragment_Access.FirstAccessInterface {

    private static final String TAG_LOG = Activity_First_Access.class.getName();
    private static final String USER_LOGIN_EXTRA = "com.poma.restaurant.USER_LOGIN_EXTRA ";
    private FirebaseAuth mAuth;


    private static final int LOGIN_REQUEST_ID = 1;
    private static final int REGISTRATION_REQUEST_ID = 2;

    private NotificationManager nm;
    private int SIMPLE_NOTIFICATION_ID = 1;

    private Timestamp time = null;
    private static List<String> cities = new ArrayList<>();

    class AddStringTask extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... unused) {
            int u = 0;
            while (u<10) {
                u++;
                publishProgress("stringa");
                SystemClock.sleep(2000);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            Toast.makeText(Activity_First_Access.this, "Done!", Toast.LENGTH_SHORT).show();
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onProgressUpdate(String... item) {

            /*nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            CharSequence contentTitle = "Notification Details...";
            CharSequence contentText = "Browse Android Site by clicking me";
            Intent notifyIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.android.com"));
            Intent notifyIntent2 = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.it"));
            PendingIntent intent = PendingIntent.getActivity(Activity_First_Access.this,0,notifyIntent,0);
            PendingIntent deleteIntent = PendingIntent.getActivity(Activity_First_Access.this,0,notifyIntent2,0);

            Notification notifyDetails = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    notifyDetails = new Notification.Builder(getBaseContext())
                            .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                            .setContentText(contentText).setContentTitle(contentTitle)
                            .setContentIntent(intent).setDeleteIntent(deleteIntent)
                            .build();
                }
            }

            nm.notify(SIMPLE_NOTIFICATION_ID++,notifyDetails);*/

            ////////

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                NotificationChannel channel =  new NotificationChannel("a_n","approvation_notification", NotificationManager.IMPORTANCE_DEFAULT);
                NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "a_n")
                    .setContentTitle("Titolo")
                    .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                    .setAutoCancel(true)
                    .setContentText("contenuto");


            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getApplicationContext());
            Intent notifyIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.android.com"));
            Intent notifyIntent2 = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.it"));
            PendingIntent intent = PendingIntent.getActivity(Activity_First_Access.this,0,notifyIntent,0);
            PendingIntent deleteIntent = PendingIntent.getActivity(Activity_First_Access.this,0,notifyIntent2,0);

            builder.setContentIntent(intent);
            builder.setDeleteIntent(deleteIntent);
            managerCompat.notify(SIMPLE_NOTIFICATION_ID++, builder.build());


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_access);
        Log.d(TAG_LOG,"Start activity");

        //new AddStringTask().execute();
        cities.add("Bergamo");
        cities.add("Milano");
        receiveNotifications_cities();

    }

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


    @Override
    public void login(Boolean user) {
        Log.d(TAG_LOG, "Login");
        final Intent intent = new Intent(this, Activity_Login.class);
        intent.putExtra(USER_LOGIN_EXTRA, user);
        startActivityForResult(intent, LOGIN_REQUEST_ID);
        Log.d(TAG_LOG, "send Intent for result. Login with user: "+user);


    }

    @Override
    public void register(Boolean user) {
        Log.d(TAG_LOG, "Register");
        final Intent intent = new Intent(this, Activity_Register.class);
        intent.putExtra(USER_LOGIN_EXTRA, user);
        startActivityForResult(intent, REGISTRATION_REQUEST_ID);
        Log.d(TAG_LOG, "send Intent for result. Login with user: "+user);

    }

    @Override
    public void anonymous_access(Boolean user) {
        Log.d(TAG_LOG, "Anonymous access");
        Intent mainIntent = new Intent(Activity_First_Access.this, Activity_Menu.class);

        /*FirebaseDatabase db =FirebaseDatabase.getInstance("https://restaurant-app-f5ff3-default-rtdb.europe-west1.firebasedatabase.app/");
        //FirebaseDatabase db =FirebaseDatabase.getInstance();
        DatabaseReference myRef = db.getReference("message");
        myRef.setValue("Hello, World!");*/



        startActivity(mainIntent);
        Log.d(TAG_LOG, "start menù anonymous");
    }

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
        final User user;
        final Intent mainIntent;
        if(requestCode == LOGIN_REQUEST_ID)
        {
            switch (resultCode)
            {
                case RESULT_OK:
                    Log.d(TAG_LOG, "Return from login (user): OK");
                    user = data.getParcelableExtra(User.USER_DATA_EXTRA);
                    mainIntent = new Intent(Activity_First_Access.this, Activity_Menu.class);
                    startActivity(mainIntent);
                    Log.d(TAG_LOG, "start menù with user: "+user.getUsername());

                    //finish();
                    break;
                case Action.RESULT_OK_ADMIN:
                    Log.d(TAG_LOG, "Return from login (admin): OK");
                    user = data.getParcelableExtra(User.USER_DATA_EXTRA);
                    mainIntent = new Intent(Activity_First_Access.this, Activity_Menu.class);
                    startActivity(mainIntent);
                    Log.d(TAG_LOG, "start menù with user:"+user.getUsername());

                    //finish();
                    break;

                case RESULT_CANCELED:
                    Log.d(TAG_LOG, "Return from login: CANCELED");
                    break;
            }
        } else if(requestCode == REGISTRATION_REQUEST_ID)
        {
            switch (resultCode)
            {
                case RESULT_OK:
                    //TODO salvare nel db
                    Log.d(TAG_LOG, "Return from register (user): OK");
                    user = data.getParcelableExtra(User.USER_DATA_EXTRA);
                    mainIntent = new Intent(Activity_First_Access.this, Activity_Menu.class);
                    startActivity(mainIntent);
                    Log.d(TAG_LOG, "start menù with user: "+user.getUsername()+"date: "+user.getDate());
                    //finish();
                    break;
                case RESULT_CANCELED:
                    Log.d(TAG_LOG, "Return from register: CANCELED");
                    break;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG_LOG, "on start");
        mAuth= FirebaseAuth.getInstance();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //Toast.makeText(Activity_Register.this, "C'è utente: "+currentUser, Toast.LENGTH_SHORT).show();
            //mAuth.signOut();

        }
        else {
            //Toast.makeText(Activity_Register.this, "Non c'è utente: ", Toast.LENGTH_SHORT).show();

        }
    }
}