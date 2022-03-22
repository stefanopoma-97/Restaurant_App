package com.poma.restaurant.restaurant;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.poma.restaurant.R;
import com.poma.restaurant.account.Activity_Account;
import com.poma.restaurant.model.Favourite;
import com.poma.restaurant.model.Notification;
import com.poma.restaurant.model.Restaurant;
import com.poma.restaurant.notifications.Fragment_Notification;
import com.poma.restaurant.notifications.Fragment_Notification_List;
import com.poma.restaurant.review.Activity_Review;
import com.poma.restaurant.utilities.Action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Restaurant_Client#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Restaurant_Client extends Fragment {

    private static final String TAG_LOG = Fragment_Restaurant_Client.class.getName();
    private Boolean user_access = true;
    private Boolean anonymous_access = false;
    private Boolean favourite_access = false;
    private String restaurant_id = null;

    private static final int LOAD_IMAGE_REQUEST_ID = 5;

    private ProgressDialog progressDialog_image;


    private ProgressBar progressBar;
    private ImageView imageView;
    private TextView textView_name;
    private TextView textView_category;
    private TextView textView_description;
    private TextView textView_city;
    private TextView textView_mail;
    private TextView textView_tag1;
    private TextView textView_tag2;
    private TextView textView_tag3;
    private View view_color;
    private TextView review;

    private Button btn_edit;
    private Button btn_back;
    private FloatingActionButton btn_add_favourite;
    private FloatingActionButton btn_remove_favourite;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private Restaurant restaurant;

    private Uri preload_imageUri;
    private Uri imageUri;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Restaurant_Client() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Restaurant_Client.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Restaurant_Client newInstance(String param1, String param2) {
        Fragment_Restaurant_Client fragment = new Fragment_Restaurant_Client();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_restaurant_client, container, false);

        this.mAuth= FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();

        this.restaurant = new Restaurant();

        this.progressBar = view.findViewById(R.id.progress_bar_fragment_restaurant);

        this.imageView = view.findViewById(R.id.imagevire_fragment_restaurant);
        this.textView_name = view.findViewById(R.id.textview_name_fragment_restuarant);
        this.textView_category = view.findViewById(R.id.textview_category_fragment_restaurant);
        this.textView_description = view.findViewById(R.id.textview_description_fragment_restaurant);
        this.textView_city = view.findViewById(R.id.textview_city_fragment_restaurant);
        this.textView_mail = view.findViewById(R.id.textview_mail_fragment_restaurant);
        this.textView_tag1 = view.findViewById(R.id.textview_tag1_fragment_restaurant);
        this.textView_tag2 = view.findViewById(R.id.textview_tag2_fragment_restaurant);
        this.textView_tag3 = view.findViewById(R.id.textview_tag3_fragment_restaurant);
        this.review = view.findViewById(R.id.textview_reviews_link_fragment_restaurant);

        this.progressDialog_image = new ProgressDialog(view.getContext());

        this.btn_edit = view.findViewById(R.id.btn_edit_fragment_restaurant);
        this.btn_back = view.findViewById(R.id.btn_back_fragment_restaurant);
        this.btn_add_favourite = view.findViewById(R.id.floatingActionButton_favourite_fragment_restaurant);
        this.btn_remove_favourite = view.findViewById(R.id.floatingActionButton_removefavourite_fragment_restaurant);
        this.view_color = view.findViewById(R.id.view2_restaurant_client);

        this.btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.goBack();
            }
        });

        this.btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.edit_restaurant(restaurant_id);
            }
        });

        this.btn_add_favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_to_favourite();
            }
        });

        this.btn_remove_favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove_to_favourite();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG_LOG,"on start");
        this.progressBar.setVisibility(View.INVISIBLE);
        load_restaurant();
        load_image();
        check_page_type();
        if (this.user_access==false && this.anonymous_access==false){
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG_LOG, "on click imageView");
                    choosePicture();
                }
            });
        }

        if (anonymous_access == false){
            this.review.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getContext(), Activity_Review.class);
                    intent.putExtra(Action.RESTAURANT_ID_EXTRA, restaurant_id);
                    startActivity(intent);


                }
            });
        }
        else {
            this.review.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(getContext(), R.string.login_first, Toast.LENGTH_SHORT).show();



                }
            });
        }



    }

    private void add_to_favourite(){
        Log.d(TAG_LOG, "Creazione preferito");
        db = FirebaseFirestore.getInstance();

        Favourite n = new Favourite();
        n.setUser_id(mAuth.getCurrentUser().getUid());
        n.setRestaurant_id(this.restaurant_id);
        n.setRestaurant_name(this.restaurant.getName());
        n.setRestaurant_category(this.restaurant.getCategory());
        n.setRestaurant_address(this.restaurant.getAddress());

        Log.d(TAG_LOG, "Preparato Favourite: "+n.toString());


        db.collection("favourites").add(n).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG_LOG, "aggiunta preferito");
                        Toast.makeText(getContext(), R.string.add_favorite, Toast.LENGTH_SHORT).show();

                        load_favourite_button();
                        create_notification_favourite();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG_LOG, "Error adding preferito", e);
                    }
                });
    }

    private void remove_to_favourite(){
        Log.d(TAG_LOG, "rimozione preferito");
        db = FirebaseFirestore.getInstance();

        db.collection("favourites")
                .whereEqualTo("restaurant_id", this.restaurant_id)
                .whereEqualTo("user_id", mAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.d(TAG_LOG, "On complete");

                if (task.isSuccessful()) {
                    Log.d(TAG_LOG, "is successfull");


                    for (QueryDocumentSnapshot document : task.getResult()) {

                        document.getReference().delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Log.d(TAG_LOG, "preferito eliminata");
                                            load_favourite_button();
                                            Toast.makeText(getContext(), R.string.remove_favorite, Toast.LENGTH_SHORT).show();

                                        }
                                        else{
                                            Log.d(TAG_LOG, "problemi eliminazione notifica");

                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG_LOG, "problema eliminziome", e);
                                    }
                                });
                    }


                } else {
                    Log.d(TAG_LOG, "Error getting documents: ", task.getException());
                }
            }
        });



    }



    private void check_page_type(){
        if (user_access == false && anonymous_access==false){
            setForAdmin();
        }
        else if (user_access == false && anonymous_access==true) {
            setForAnonymous();
        }
        else if (user_access == true) {
            setForUser();
        }
    }

    private void setForAdmin(){
        this.btn_edit.setVisibility(View.VISIBLE);
        this.btn_add_favourite.setVisibility(View.INVISIBLE);
        this.btn_remove_favourite.setVisibility(View.INVISIBLE);
        this.view_color.setBackgroundColor(getResources().getColor(R.color.blue_link));
    }
    private void setForUser(){
        this.btn_edit.setVisibility(View.GONE);
        this.btn_add_favourite.setVisibility(View.INVISIBLE);
        this.btn_remove_favourite.setVisibility(View.INVISIBLE);
        load_favourite_button();
    }

    private void setForAnonymous(){
        this.btn_edit.setVisibility(View.GONE);
        this.btn_add_favourite.setVisibility(View.INVISIBLE);
        this.btn_remove_favourite.setVisibility(View.INVISIBLE);
    }

    private void load_favourite_button(){
        this.db.collection("favourites")
                .whereEqualTo("restaurant_id",this.restaurant_id)
                .whereEqualTo("user_id",this.mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean fav = false;
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Map<String, Object> data = document.getData();
                                fav = true;
                            }

                            if (fav){
                                btn_remove_favourite.setVisibility(View.VISIBLE);
                                btn_add_favourite.setVisibility(View.GONE);
                            }
                            else {
                                btn_remove_favourite.setVisibility(View.GONE);
                                btn_add_favourite.setVisibility(View.VISIBLE);
                            }



                        }
                    }
                });



    }

    private void create_notification_favourite(){
        Log.d(TAG_LOG, "Creazione notifica");

        Notification n = new Notification();
        n.setUser_id(this.restaurant.getAdmin_id());
        n.setRead(false);
        n.setShowed(false);
        n.setContent(getResources().getString(R.string.new_favourite_description)+": "+restaurant.getName());
        n.setType(getResources().getString(R.string.new_favourite));
        n.setUseful_id(this.restaurant.getId());
        Calendar calendar = Calendar.getInstance();
        long timeMilli2 = calendar.getTimeInMillis();
        n.setDate(timeMilli2);
        Log.d(TAG_LOG, "notifica istanziata: "+n.toString());



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


    private void load_image(){
        Log.d(TAG_LOG, "inizio update imageView");

        DocumentReference docRef = db.collection("restaurants").document(this.restaurant_id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> data = document.getData();
                        Log.d(TAG_LOG, "DocumentSnapshot data: " + data);

                        if ((String)data.get("imageUrl") != null && (String)data.get("imageUrl") != "" ){
                            progressBar.setVisibility(View.VISIBLE);
                            Uri uri = Uri.parse((String)data.get("imageUrl"));
                            preload_imageUri = uri;
                            Log.d("firebase", "Image Url: " + preload_imageUri);
                            Glide.with(Fragment_Restaurant_Client.this)
                                    .load(uri)
                                    .listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            imageView.setImageResource(R.drawable.ic_baseline_image_24);
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
                        else {
                            progressBar.setVisibility(View.INVISIBLE);
                            if (user_access == false && anonymous_access==false)
                                imageView.setImageResource(R.drawable.add_image);
                            else
                                imageView.setImageResource(R.drawable.ic_baseline_image_24);
                        }




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

    private void load_restaurant(){
        DocumentReference docRef = this.db.collection("restaurants").document(this.restaurant_id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> data = document.getData();
                        Log.d(TAG_LOG, "DocumentSnapshot data: " + data);

                        restaurant.setName((String) data.get("name"));
                        restaurant.setCategory((String) data.get("category"));
                        restaurant.setCity((String) data.get("city"));
                        restaurant.setAddress((String) data.get("address"));
                        restaurant.setDescription((String) data.get("description"));
                        restaurant.setEmail((String) data.get("email"));
                        restaurant.setTags((List<String>) data.get("tags"));
                        restaurant.setAdmin_id((String) data.get("admin_id"));
                        restaurant.setId((String) document.getId());


                        /*
                        Long in = (long)data.get("vote");
                        restaurant.setVote(in.intValue());
                        */
                        Long in2 = (long)data.get("n_reviews");
                        restaurant.setN_reviews(in2.intValue());




                        textView_name.setText(restaurant.getName());
                        textView_description.setText(restaurant.getDescription());
                        textView_city.setText(restaurant.getCity());
                        textView_category.setText(restaurant.getCategory());
                        textView_mail.setText(restaurant.getEmail());

                        SpannableString content = new SpannableString(String.valueOf(restaurant.getN_reviews()));
                        content.setSpan(new UnderlineSpan(), 0, (String.valueOf(restaurant.getN_reviews())).length(), 0);
                        review.setText(content);

                        String tag1 = restaurant.getTag1();
                        if (tag1 != ""){
                            textView_tag1.setText(tag1);
                        }
                        else {
                            textView_tag1.setVisibility(View.INVISIBLE);
                        }

                        String tag2 = restaurant.getTag2();
                        if (tag2 != ""){
                            textView_tag2.setText(tag2);
                        }
                        else {
                            textView_tag2.setVisibility(View.INVISIBLE);
                        }

                        String tag3 = restaurant.getTag3();
                        if (tag3 != ""){
                            textView_tag3.setText(tag3);
                        }
                        else {
                            textView_tag3.setVisibility(View.INVISIBLE);
                        }


                    } else {
                        Log.d(TAG_LOG, "No such document");
                    }
                } else {
                    Log.d(TAG_LOG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void setRestaurant_id(String id){
        this.restaurant_id=id;
    }

    public void setUser(){
        this.user_access=true;
    }

    public void setAdmin(){
        this.user_access=false;
    }

    public void setAnonymous(){
        this.anonymous_access = true;
        this.user_access=false;
    }

    public void setFavouriteAccess(){
        this.favourite_access=true;
    }

    public void setNotFavouriteAccess(){
        this.favourite_access=false;
    }

    private void choosePicture() {
        Log.d(TAG_LOG, "Chose pictures");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, LOAD_IMAGE_REQUEST_ID);
        Log.d(TAG_LOG, "start intent for result");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        //final User user;
        final Intent mainIntent;
        if (requestCode == LOAD_IMAGE_REQUEST_ID){
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

    public void setImageUri(Uri uri){
        imageUri = uri;
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

    private String getFileExtension(Uri _imageUri) {
        ContentResolver cr = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(_imageUri));
    }

    public void updateImageView() {


        Log.d(TAG_LOG, "inizio update imageView");

        DocumentReference docRef = db.collection("restaurants").document(restaurant_id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> data = document.getData();
                        Log.d(TAG_LOG, "DocumentSnapshot data: " + data);

                        if ((String)data.get("imageUrl") != null){
                            progressBar.setVisibility(View.VISIBLE);
                            Uri uri = Uri.parse((String)data.get("imageUrl"));
                            preload_imageUri = uri;
                            Log.d("firebase", "Image Url: " + preload_imageUri);
                            Glide.with(Fragment_Restaurant_Client.this)
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


    public void uploadPicture() {
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

                        DocumentReference docRef = db.collection("restaurants").document(restaurant_id);

                        Map<String, Object> updates = new HashMap<>();
                        updates.put("imageUrl", uri.toString());
                        Log.d(TAG_LOG, "upload - voglio aggiungere a utente: "+uri.toString());

                        docRef.set(updates, SetOptions.merge())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Log.d(TAG_LOG, "upload - aggiorno info ristorante");

                                            // Reload information
                                            updateImageView();
                                            progressDialog_image(false, "");
                                            Toast.makeText(getContext(), R.string.image_uploaded, Toast.LENGTH_LONG).show();
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



    //Interfaccia
    public interface RestaurantInterface {
        public void goBack();
        public void edit_restaurant(String n);
    }

    private Fragment_Restaurant_Client.RestaurantInterface listener;

    //check if activity implement the interface
    @Override
    public void onAttach(Activity activity){
        Log.d(TAG_LOG,"onAttach fragment");

        super.onAttach(activity);
        if(activity instanceof Fragment_Restaurant_Client.RestaurantInterface){
            listener = (Fragment_Restaurant_Client.RestaurantInterface) activity;
        }
        else {
            throw new ClassCastException(activity.toString() +
                    "Does not implement the interface");
        }
    }
}