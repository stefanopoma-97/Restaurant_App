package com.poma.restaurant.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.poma.restaurant.R;
import com.poma.restaurant.login.Activity_First_Access;
import com.poma.restaurant.menu.Activity_Menu;
import com.poma.restaurant.model.Broadcast_receiver_callBack_logout;
import com.poma.restaurant.model.Receiver;
import com.poma.restaurant.model.User;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.io.Files.getFileExtension;

public class Activity_Account extends AppCompatActivity {

    private static final String TAG_LOG = Activity_Menu.class.getName();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private User currentUser2;
    private User user_new;

    private Button btn_edit_account;
    private TextView textView_change_password;
    private ProgressBar progressBar;
    private ImageView imageView;

    private TextView textView_name;
    private TextView textView_surname;
    private TextView textView_username;
    private TextView textView_date;
    private TextView textView_mail;
    private TextView textView_location;

    private BroadcastReceiver broadcastReceiver;

    private ProgressDialog progressDialog;

    private Uri imageUri;




    private static final int EDIT_REQUEST_ID = 3;
    private static final int CHANGE_PASSWORD_REQUEST_ID = 4;
    private static final int LOAD_IMAGE_REQUEST_ID = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        this.btn_edit_account = (Button)findViewById(R.id.btn_account_editdata);
        this.textView_change_password = (TextView) findViewById(R.id.textview_account_change_password);
        this.progressBar = (ProgressBar)findViewById(R.id.progress_bar_account);
        this.imageView = (ImageView)findViewById(R.id.account_imageview);

        this.textView_name = (TextView) findViewById(R.id.textview_account_name);
        this.textView_surname = (TextView) findViewById(R.id.textview_account_surname);
        this.textView_username = (TextView) findViewById(R.id.textview_account_username);
        this.textView_date = (TextView) findViewById(R.id.textview_account_date);
        this.textView_mail = (TextView) findViewById(R.id.textview_account_mail);
        this.textView_location = (TextView) findViewById(R.id.textview_account_location);


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



        btn_edit_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG_LOG, "edit account");
                final Intent intent = new Intent(Activity_Account.this, Activity_Edit_Account.class);
                startActivityForResult(intent, EDIT_REQUEST_ID);
                Log.d(TAG_LOG, "send Intent for result. edit");
            }
        });

        textView_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG_LOG, "change password");
                final Intent intent = new Intent(Activity_Account.this, Activity_Password.class);
                startActivityForResult(intent, CHANGE_PASSWORD_REQUEST_ID);
                Log.d(TAG_LOG, "send Intent for result. change password");
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        //controllo la presenza di utenti loggati
        Log.d(TAG_LOG, "Controllo ci sia un utente loggato");

        check_user();

        retrieve_user_data();

    }


    @Override
    protected void onDestroy() {
        Log.d(TAG_LOG,"on destroy");
        super.onDestroy();
        unregisterReceiver(this.broadcastReceiver);
        Log.d(TAG_LOG,"un register receiver");
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
                    Toast.makeText(Activity_Account.this, R.string.data_has_been_successfully_updated, Toast.LENGTH_SHORT).show();
                    break;
                case RESULT_CANCELED:
                    Log.d(TAG_LOG, "Return from edit: CANCELED");
                    break;
            }
        }
        else if (requestCode == CHANGE_PASSWORD_REQUEST_ID){
            switch (resultCode)
            {
                case RESULT_OK:
                    if (data != null && data.getData() != null){
                        imageUri = data.getData();
                        this.imageView.setImageURI(imageUri);
                        uploadPicture();
                    }
                    break;
                case RESULT_CANCELED:
                    Log.d(TAG_LOG, "Return from change password: CANCELED");
                    break;
            }
        }

        else if (requestCode == LOAD_IMAGE_REQUEST_ID){
            switch (resultCode)
            {
                case RESULT_OK:
                    Log.d(TAG_LOG, "Return from load image: OK");
                    Toast.makeText(Activity_Account.this, R.string.password_has_been_successfully_updated, Toast.LENGTH_SHORT).show();
                    break;
                case RESULT_CANCELED:
                    Log.d(TAG_LOG, "Return from load image: CANCELED");
                    break;
            }
        }

    }



    //METODI

    private void logout(){
        Log.d(TAG_LOG, "Logout - inizio procedura");
        finish();
    }

    //broadcast per logout
    private void broadcast_logout(View view){
        Log.d(TAG_LOG, "Logout - inizio procedura");
        Intent intent = new Intent();
        intent.setAction("com.poma.restaurant.broadcastreceiversandintents.BROADCAST_LOGOUT");
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(intent);
        Log.d(TAG_LOG, "Broadcast mandato");
    }

    //controllo la presenza di un utente loggato.
    //per farlo viene utilizzato Firestore e SharedPreferences. Viene anche confrontato l'id degli utenti ricavato nei due modi
    private void check_user(){
        Boolean anonymous_f = false;
        Boolean anonymous_s = false;
        Boolean anonymous = false;
        Log.d(TAG_LOG, "Controllo ci sia un utente loggato");

        //Login Firestore
        this.currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Log.d(TAG_LOG, "Trovato utente con Firestore, id: "+this.currentUser.getUid());
        }
        else {
            Log.d(TAG_LOG, "Non trovato utente con Firestore");
            anonymous_f = true;
        }

        //Login Shared Preferences
        this.currentUser2 = User.load(this);
        if (currentUser2 != null) {
            Log.d(TAG_LOG, "Trovato utente con Shared preferences, id: "+this.currentUser2.getID());
        }
        else {
            Log.d(TAG_LOG, "Non trovato utente con SharedPreference");
            anonymous_s = true;
        }

        //anonimo, user, admin o errore
        if (anonymous_f | anonymous_s){
            Log.d(TAG_LOG, "ERRORE - Non c'è utente, accesso anonimo");
            finish();
        }
        else if (anonymous_f!=anonymous_s){
            Log.d(TAG_LOG, "ERRORE - ho trovato sulo un utente");
            finish();
        }
        else if (currentUser.getUid() == currentUser2.getID()){
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
                                Log.d(TAG_LOG, "Utente Admin");
                            }
                            else {
                                Log.d(TAG_LOG, "Utente Visitatore");
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

    private void retrieve_user_data(){
        progressDialog(true, getResources().getString(R.string.retrieving_account_wait));
        this.currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Log.d(TAG_LOG, "inizio metodo retrive user");

            DocumentReference docRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map<String, Object> data = document.getData();
                            Log.d(TAG_LOG, "DocumentSnapshot data: " + data);


                            user_new = User.create((String) data.get("username"),(String) data.get("password"))
                                    .withSurname((String) data.get("surname"))
                                    .withName((String) data.get("name"))
                                    .withLocation((String) data.get("location"))
                                    .withEmail((String) data.get("email"))
                                    .withDate((long) data.get("date"))
                                    .withAdmin((Boolean) data.get("admin"))
                                    .withCity_id((String) data.get("city_id"))
                                    .withId(mAuth.getCurrentUser().getUid());

                            textView_name.setText((String) data.get("name"));
                            textView_surname.setText((String) data.get("surname"));
                            textView_username.setText((String) data.get("username"));
                            textView_location.setText((String) data.get("location"));
                            textView_mail.setText((String) data.get("email"));
                            textView_date.setText((String) data.get("email"));
                            progressDialog(false, "");

                            //progressBar.setVisibility(View.INVISIBLE);


                        } else {
                            Log.d(TAG_LOG, "No such document");
                            progressDialog(false,"");
                        }
                    } else {
                        Log.d(TAG_LOG, "get failed with ", task.getException());
                        progressDialog(false,"");
                    }
                }
            });
        }
        else {
            Log.d(TAG_LOG, "No such document");
            finish();
        }

    }

    private void retrieve_image(){
        /*
        if (uriS != "") {
            Uri uri = Uri.parse(String.valueOf(task.getResult().child("imageUrl").getValue()));
            Log.d("firebase", "Image Url: " + uri);
            Glide.with(this).load(uri).into(imageView);
        }*/

    }

    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, LOAD_IMAGE_REQUEST_ID);
    }

    private void uploadPicture() {
        Log.d(TAG_LOG, "upload pictures");

        progressDialog(true, getResources().getString(R.string.uploading_image));


        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        // Create a reference to "mountains.jpg"
        StorageReference picRef = storageRef.child(System.currentTimeMillis() + "." + getFileExtension(this.imageUri));



        picRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Snackbar.make(getActivity().findViewById(android.R.id.content), "Image uploaded", Snackbar.LENGTH_LONG).show();
                picRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d(TAG_LOG, "upload - on success");

                        DocumentReference docRef = db.collection("Users").document(mAuth.getCurrentUser().getUid());

                        Map<String, Object> updates = new HashMap<>();
                        updates.put("imageUrl", uri.toString());

                        docRef.update(updates);

                        // Reload information
                        updateImageView(mAuth.getUid());
                        progressDialog(false, "");
                        Toast.makeText(Activity_Account.this, "Image uploaded", Toast.LENGTH_LONG).show();
                    }
                });
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog(false, "");
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        progressDialog(true, "Progress: " + (int) progressPercent + "%");
                    }
                });

    }

    private String getFileExtension(Uri _imageUri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(_imageUri));
    }

    public void updateImageView(String userId) {

        if(currentUser != null){
            Log.d(TAG_LOG, "inizio update image");

            DocumentReference docRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map<String, Object> data = document.getData();
                            Log.d(TAG_LOG, "DocumentSnapshot data: " + data);


                            Uri uri = Uri.parse((String)data.get("imageUrl"));
                            Log.d("firebase", "Image Url: " + uri);
                            Glide.with(Activity_Account.this).load(uri).into(imageView);


                        } else {
                            Log.d(TAG_LOG, "No such document");
                            progressDialog(false,"");
                        }
                    } else {
                        Log.d(TAG_LOG, "get failed with ", task.getException());
                        progressDialog(false,"");
                    }
                }
            });
        }
        else {
            Log.d(TAG_LOG, "No such document");
            finish();
        }

    }


    private void progressDialog(Boolean b, String text){

        if(b){
            this.progressDialog = new ProgressDialog(Activity_Account.this);
            progressDialog.setMessage(text);
            progressDialog.show();
        }
        else{
            this.progressDialog.dismiss();
        }

    }




}