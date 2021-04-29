package com.poma.restaurant.menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.poma.restaurant.R;
import com.poma.restaurant.login.Activity_Account;
import com.poma.restaurant.login.Activity_First_Access;

import java.util.Map;

public class Activity_Menu extends AppCompatActivity {
    private static final String TAG_LOG = Activity_Menu.class.getName();
    private FirebaseAuth mAuth;
    private Button btn_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG_LOG, "on create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mAuth= FirebaseAuth.getInstance();
        btn_logout= (Button)findViewById(R.id.button_menu_logout);


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
    }

    @Override
    protected void onStart() {
        super.onStart();
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
}