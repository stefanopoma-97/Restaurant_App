package com.poma.restaurant.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.poma.restaurant.R;
import com.poma.restaurant.menu.Activity_Menu;
import com.poma.restaurant.model.User;
import com.poma.restaurant.utilities.Action;

import java.util.HashMap;
import java.util.Map;

public class Activity_First_Access extends AppCompatActivity implements Fragment_Access.FirstAccessInterface {

    private static final String TAG_LOG = Activity_First_Access.class.getName();
    private static final String USER_LOGIN_EXTRA = "com.poma.restaurant.USER_LOGIN_EXTRA ";
    private FirebaseAuth mAuth;


    private static final int LOGIN_REQUEST_ID = 1;
    private static final int REGISTRATION_REQUEST_ID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_access);
        Log.d(TAG_LOG,"Start activity");




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