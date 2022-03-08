package com.poma.restaurant.account;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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
import com.google.firebase.firestore.SetOptions;
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
import android.graphics.drawable.Drawable;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.io.Files.getFileExtension;

public class Activity_Account extends AppCompatActivity {

    private static final String TAG_LOG = Activity_Account.class.getName();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private User currentUser2;
    private User user_new;

    private Button btn_edit_account;
    private Button btn_cancel;
    private TextView textView_change_password;
    private ProgressBar progressBar;
    private ImageView imageView;

    private TextView textView_name;
    private TextView textView_surname;
    private TextView textView_username;
    private TextView textView_date;
    private TextView textView_mail;
    private TextView textView_location;
    private TextView textView_add_new_image;

    private BroadcastReceiver broadcastReceiver;

    private ProgressDialog progressDialog;
    private ProgressDialog progressDialog_image;

    private Uri imageUri;
    private Uri preload_imageUri;




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
        this.textView_add_new_image = (TextView) findViewById(R.id.textview_account_add_new_image);

        this.btn_cancel = (Button)findViewById(R.id.btn_account_cancel);


        this.mAuth= FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();

        this.progressDialog = new ProgressDialog(Activity_Account.this);
        this.progressDialog_image = new ProgressDialog(Activity_Account.this);


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


        this.progressBar.setVisibility(View.INVISIBLE);

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

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG_LOG, "on click imageView");
                choosePicture();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG_LOG, "on click btn cancel");
                getBack();
                //broadcast_logout(v);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        //controllo la presenza di utenti loggati
        Log.d(TAG_LOG, "on start");

        check_user();

        retrieve_user_data();

        updateImageView();

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
                    Toast.makeText(Activity_Account.this, R.string.password_has_been_successfully_updated, Toast.LENGTH_SHORT).show();
                    Log.d(TAG_LOG, "Return from change password: OK");

                    break;
                case RESULT_CANCELED:
                    Log.d(TAG_LOG, "Return from change password: CANCELED");
                    break;
            }
        }

        else if (requestCode == LOAD_IMAGE_REQUEST_ID){
            Log.d(TAG_LOG, "retrive intent image");
            switch (resultCode)
            {
                case RESULT_OK:
                    Log.d(TAG_LOG, "Return from load image: OK");
                    if (data != null && data.getData() != null){
                        imageUri = data.getData();
                        //this.imageView.setImageURI(imageUri);
                        uploadPicture();
                    }
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

    private void getBack(){
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
            Log.d(TAG_LOG, "ERRORE - ho trovato solo un utente");
            finish();
        }
        else if (this.currentUser.getUid().equals(this.currentUser2.getID())){
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

                            SimpleDateFormat formatter=new SimpleDateFormat("dd MM yyyy");
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis((long) data.get("date"));
                            textView_date.setText(formatter.format(calendar.getTime()));

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
        Log.d(TAG_LOG, "Chose pictures");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, LOAD_IMAGE_REQUEST_ID);
        Log.d(TAG_LOG, "start intent for result");
    }


    private void delete_current_image(){
        if (preload_imageUri != null){
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl(preload_imageUri.toString());
            Log.d(TAG_LOG, "delete current image: "+storageRef.getName());
            //StorageReference picRef = storageRef.child(preload_imageUri.toString());


            storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG_LOG, "delete on success");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d(TAG_LOG, "delete failure");
                }
            });
        }


    }

    private void uploadPicture() {
        Log.d(TAG_LOG, "upload pictures");

        progressDialog_image(true, getResources().getString(R.string.uploading_image));


        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        Log.d(TAG_LOG, "upload - reference storage prefe");
        // Create a reference to "mountains.jpg"
        StorageReference picRef = storageRef.child(System.currentTimeMillis() + "." + getFileExtension(this.imageUri));


        Log.d(TAG_LOG, "upload - creato picRef");
        picRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Snackbar.make(getActivity().findViewById(android.R.id.content), "Image uploaded", Snackbar.LENGTH_LONG).show();
                picRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d(TAG_LOG, "upload - on success");
                        delete_current_image();

                        DocumentReference docRef = db.collection("users").document(mAuth.getCurrentUser().getUid());

                        Map<String, Object> updates = new HashMap<>();
                        updates.put("imageUrl", uri.toString());
                        Log.d(TAG_LOG, "upload - voglio aggiungere a utente: "+uri.toString());

                        docRef.set(updates, SetOptions.merge())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Log.d(TAG_LOG, "upload - aggiorno info utente");

                                    // Reload information
                                    updateImageView();
                                    progressDialog_image(false, "");
                                    Toast.makeText(Activity_Account.this, "Image uploaded", Toast.LENGTH_LONG).show();
                                }
                                else{
                                    Log.d(TAG_LOG, "problemi aggiornamento user");
                                    progressDialog_image(false, "");

                                }
                            }
                        });

                    }
                });
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG_LOG, "upload - on failure");
                        progressDialog_image(false, "");
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        Log.d(TAG_LOG, "upload - on progress");
                        double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        if ((int)progressPercent == 100){
                            progressDialog_image(false, "arrivato a 100");
                        }
                        else {
                            progressDialog_image(true, "Progress: " + (int) progressPercent + "%");
                        }


                    }
                });

    }

    private String getFileExtension(Uri _imageUri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(_imageUri));
    }

    public void updateImageView() {

        if(currentUser != null){
            Log.d(TAG_LOG, "inizio update imageView");

            DocumentReference docRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map<String, Object> data = document.getData();
                            Log.d(TAG_LOG, "DocumentSnapshot data: " + data);

                            if ((String)data.get("imageUrl") != null){
                                textView_add_new_image.setVisibility(View.INVISIBLE);
                                progressBar.setVisibility(View.VISIBLE);
                                Uri uri = Uri.parse((String)data.get("imageUrl"));
                                preload_imageUri = uri;
                                Log.d("firebase", "Image Url: " + preload_imageUri);
                                Glide.with(Activity_Account.this)
                                        .load(uri)
                                        .listener(new RequestListener<Drawable>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                                                return false;
                                            }
                                            @Override
                                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                Log.d(TAG_LOG, "Glide on resource ready");
                                                progressBar.setVisibility(View.INVISIBLE);
                                                return false;
                                            }
                                        })
                                        .into(imageView);
                            }
                            //progressBar.setVisibility(View.INVISIBLE);



                        } else {
                            Log.d(TAG_LOG, "No such document");
                            //progressDialog(false,"");
                            //progressBar.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        Log.d(TAG_LOG, "get failed with ", task.getException());
                        //progressDialog(false,"");
                        //progressBar.setVisibility(View.INVISIBLE);
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

        Log.d(TAG_LOG, "Progress dialog ("+b.toString()+") con testo: "+text);
        if(b){
            this.progressDialog.setMessage(text);
            this.progressDialog.show();
        }
        else{
            this.progressDialog.dismiss();
        }

    }

    private void progressDialog_image(Boolean b, String text){

        Log.d(TAG_LOG, "Progress dialog ("+b.toString()+") con testo: "+text);
        if(b){
            this.progressDialog_image.setMessage(text);
            this.progressDialog_image.show();
        }
        else{
            this.progressDialog_image.dismiss();
        }

    }




}