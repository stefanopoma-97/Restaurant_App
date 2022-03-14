package com.poma.restaurant.restaurant;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.poma.restaurant.R;
import com.poma.restaurant.account.Activity_Edit_Account;
import com.poma.restaurant.login.Fragment_Register;
import com.poma.restaurant.model.Restaurant;
import com.poma.restaurant.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Edit_Restaurant#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Edit_Restaurant extends Fragment {

    private static final String TAG_LOG = Fragment_Edit_Restaurant.class.getName();
    private static final String ERROR_STRING_KEY_FRAGMENT_EDIT_RESTAURANT = "com.poma.restaurant.ERROR_STRING_KEY_FRAGMENT_EDIT_RESTAURANT";
    private static final String CITY_NAME_STRING_KEY_FRAGMENT_EDIT_RESTAURANT= "com.poma.restaurant.CITY_NAME_STRING_KEY_FRAGMENT_EDIT_RESTAURANT";
    private static final String CATEGORY_NAME_STRING_KEY_FRAGMENT_EDIT_RESTAURANT= "com.poma.restaurant.CATEGORY_NAME_STRING_KEY_FRAGMENT_EDIT_RESTAURANT";
    private static final String TAG1_STRING_KEY_FRAGMENT_EDIT_RESTAURANT= "com.poma.restaurant.TAG1_STRING_KEY_FRAGMENT_EDIT_RESTAURANT";
    private static final String TAG2_STRING_KEY_FRAGMENT_EDIT_RESTAURANT= "com.poma.restaurant.TAG2_STRING_KEY_FRAGMENT_EDIT_RESTAURANT";
    private static final String TAG3_STRING_KEY_FRAGMENT_EDIT_RESTAURANT= "com.poma.restaurant.TAG3_STRING_KEY_FRAGMENT_EDIT_RESTAURANT";

    private static final String ID_STRING_KEY_FRAGMENT_EDIT_RESTAURANT= "com.poma.restaurant.ID_STRING_KEY_FRAGMENT_EDIT_RESTAURANT";
    private static final String SAVED_STRING_KEY_FRAGMENT_EDIT_RESTAURANT= "com.poma.restaurant.SAVED_STRING_KEY_FRAGMENT_EDIT_RESTAURANT";



    private Map<String, Object> cities;
    private Map<String, Object> categories;
    private static String retrieve_city = null;
    private static String retrieve_category = null;
    private static String error_state ="";

    private FirebaseFirestore db;

    private FirebaseAuth mAuth;

    private ProgressDialog progressDialog;

    private Spinner spinner_cities;
    private Spinner spinner_categories;
    private TextView textView_loading_cities;
    private TextView textView_loading_categories;
    private TextView textView_error;
    private EditText editText_tags;
    private EditText editText_name;
    private EditText editText_description;
    private EditText editText_mail;
    private EditText editText_address;
    private EditText editText_phone;

    private ScrollView scrollView;

    private Chip chip1;
    private Chip chip2;
    private Chip chip3;
    private Boolean[] tags_insert;

    private Button btn_create;
    private Button btn_cancel;


    //Per gestire UPDATE
    private Boolean update = false;
    private String restaurant_id = null;
    private Restaurant restaurant;

    private Boolean saved_state = null;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Edit_Restaurant() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Edit_Restaurant.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Edit_Restaurant newInstance(String param1, String param2) {
        Fragment_Edit_Restaurant fragment = new Fragment_Edit_Restaurant();
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
        View view = inflater.inflate(R.layout.fragment_edit_restaurant, container, false);
        this.db = FirebaseFirestore.getInstance();

        this.spinner_cities = (Spinner) view.findViewById(R.id.spinner_edit_restaurant_city);
        this.spinner_categories = (Spinner) view.findViewById(R.id.spinner_edit_restaurant_category);
        this.textView_loading_cities = (TextView) view.findViewById(R.id.textview_edit_restaurant_loading_cities);
        this.textView_loading_categories = (TextView) view.findViewById(R.id.textview_edit_restaurant_loading_categories);
        this.editText_tags = view.findViewById(R.id.edittext_edit_restaurant_tags);

        this.textView_error = (TextView) view.findViewById(R.id.textview_edit_restaurant_alert_message);
        this.scrollView = view.findViewById(R.id.scrollview_edit_restaurant);

        this.editText_name = view.findViewById(R.id.edittext_edit_restaurant_name);
        this.editText_description = view.findViewById(R.id.edittext_edit_restaurant_description);
        this.editText_mail= view.findViewById(R.id.edittext_edit_restaurant_email);
        this.editText_address = view.findViewById(R.id.edittext_edit_restaurant_address);
        this.editText_phone = view.findViewById(R.id.edittext_edit_restaurant_phone);

        this.btn_create = view.findViewById(R.id.btn_edit_restaurant_create);
        this.btn_cancel = view.findViewById(R.id.btn_edit_restaurant_cancel);

        this.chip1 = view.findViewById(R.id.chipCpp1);
        this.chip2 = view.findViewById(R.id.chipCpp2);
        this.chip3 = view.findViewById(R.id.chipCpp3);
        this.chip1.setVisibility(View.GONE);
        this.chip2.setVisibility(View.GONE);
        this.chip3.setVisibility(View.GONE);


        this.mAuth= FirebaseAuth.getInstance();

        this.tags_insert = new Boolean[]{false, false, false};


        this.chip1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chip1.isChecked()){
                    chip1.setText("");
                    chip1.setChecked(false);
                    chip1.setVisibility(View.INVISIBLE);
                    tags_insert[0]=false;
                    Toast.makeText(getContext(), "Remove tag 1", Toast.LENGTH_SHORT).show();
                }
            }
        });

        this.chip2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chip2.isChecked()){
                    chip2.setText("");
                    chip2.setChecked(false);
                    chip2.setVisibility(View.INVISIBLE);
                    tags_insert[1]=false;
                    Toast.makeText(getContext(), "Remove tag 2", Toast.LENGTH_SHORT).show();
                }
            }
        });

        this.chip3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chip3.isChecked()){
                    chip3.setText("");
                    chip3.setChecked(false);
                    chip3.setVisibility(View.INVISIBLE);
                    tags_insert[2]=false;
                    Toast.makeText(getContext(), "Remove tag 3", Toast.LENGTH_SHORT).show();
                }
            }
        });

        this.editText_tags.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.d(TAG_LOG, "onKey");
                // If the event is a key-down event on the "enter" button
                if ((keyCode == KeyEvent.KEYCODE_ENTER) && (!editText_tags.getText().toString().equals(""))) {
                    Log.d(TAG_LOG, "Enter");
                    enter_tag(view);
                    return true;
                }
                return false;
            }
        });

        this.btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.cancel();
            }
        });

        this.btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG_LOG, "on click edit");
                if (checkFields()){
                    Log.d(TAG_LOG, "send to activity all data");
                    String name = editText_name.getText().toString();
                    String description =editText_description.getText().toString();
                    String mail = editText_mail.getText().toString();
                    String address = editText_address.getText().toString();
                    String phone = editText_phone.getText().toString();

                    String city = spinner_cities.getSelectedItem().toString();
                    String category = spinner_categories.getSelectedItem().toString();
                    List<String> tags = new ArrayList<>();
                    if (tags_insert[0])
                        tags.add(chip1.getText().toString());
                    if (tags_insert[1])
                        tags.add(chip2.getText().toString());
                    if (tags_insert[2])
                        tags.add(chip3.getText().toString());

                    if (update)
                        listener.update_restaurant(name, description, mail, address, city, phone, mAuth.getCurrentUser().getUid(), "", category,tags, restaurant_id);
                    else
                        listener.create_restaurant(name, description, mail, address, city, phone, mAuth.getCurrentUser().getUid(), "", category,tags);
                }


            }
        });



        return view;
    }

    //State
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putString(ERROR_STRING_KEY_FRAGMENT_EDIT_RESTAURANT, this.textView_error.getText().toString());
        savedInstanceState.putString(CITY_NAME_STRING_KEY_FRAGMENT_EDIT_RESTAURANT, getCity());
        savedInstanceState.putString(CATEGORY_NAME_STRING_KEY_FRAGMENT_EDIT_RESTAURANT, getCategory());
        savedInstanceState.putString(ID_STRING_KEY_FRAGMENT_EDIT_RESTAURANT, this.restaurant_id);
        if (this.tags_insert[0])
            savedInstanceState.putString(TAG1_STRING_KEY_FRAGMENT_EDIT_RESTAURANT, this.chip1.getText().toString());
        else
            savedInstanceState.putString(TAG1_STRING_KEY_FRAGMENT_EDIT_RESTAURANT, "");
        if (this.tags_insert[1])
            savedInstanceState.putString(TAG2_STRING_KEY_FRAGMENT_EDIT_RESTAURANT, this.chip2.getText().toString());
        else
            savedInstanceState.putString(TAG2_STRING_KEY_FRAGMENT_EDIT_RESTAURANT, "");
        if (this.tags_insert[2])
            savedInstanceState.putString(TAG3_STRING_KEY_FRAGMENT_EDIT_RESTAURANT, this.chip3.getText().toString());
        else
            savedInstanceState.putString(TAG3_STRING_KEY_FRAGMENT_EDIT_RESTAURANT, "");

        this.retrieve_city = null;
        this.retrieve_category = null;
        this.saved_state=null;
        super.onSaveInstanceState(savedInstanceState);
        Log.d(TAG_LOG,"Save state: "+ERROR_STRING_KEY_FRAGMENT_EDIT_RESTAURANT+" valore: "+this.textView_error.getText().toString());
        Log.d(TAG_LOG,"Save state: "+CITY_NAME_STRING_KEY_FRAGMENT_EDIT_RESTAURANT+" valore: "+getCity());
        Log.d(TAG_LOG,"Save state: "+CATEGORY_NAME_STRING_KEY_FRAGMENT_EDIT_RESTAURANT+" valore: "+getCategory());
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG_LOG,"on Activity Create");

        if (savedInstanceState != null){
            this.saved_state = true;
            String errore = savedInstanceState.getString(ERROR_STRING_KEY_FRAGMENT_EDIT_RESTAURANT);
            if (errore==""){
                this.error_state="";
            }
            else {
                this.error_state=errore;
            }
            setError(this.error_state);

            this.retrieve_city = savedInstanceState.getString(CITY_NAME_STRING_KEY_FRAGMENT_EDIT_RESTAURANT);
            this.retrieve_category = savedInstanceState.getString(CATEGORY_NAME_STRING_KEY_FRAGMENT_EDIT_RESTAURANT);


            String tag1 = savedInstanceState.getString(TAG1_STRING_KEY_FRAGMENT_EDIT_RESTAURANT);
            String tag2 = savedInstanceState.getString(TAG2_STRING_KEY_FRAGMENT_EDIT_RESTAURANT);
            String tag3 = savedInstanceState.getString(TAG3_STRING_KEY_FRAGMENT_EDIT_RESTAURANT);

            this.restaurant_id = savedInstanceState.getString(ID_STRING_KEY_FRAGMENT_EDIT_RESTAURANT);

            if (!tag1.equals("")){
                this.chip1.setText(tag1);
                this.chip1.setVisibility(View.VISIBLE);
                this.tags_insert[0]=true;
            }
            if (!tag2.equals("")){
                this.chip2.setText(tag2);
                this.chip2.setVisibility(View.VISIBLE);
                this.tags_insert[1]=true;
            }
            if (!tag3.equals("")){
                this.chip3.setText(tag3);
                this.chip3.setVisibility(View.VISIBLE);
                this.tags_insert[2]=true;
            }


            Log.d(TAG_LOG,"Retrive state: "+ERROR_STRING_KEY_FRAGMENT_EDIT_RESTAURANT+" valore: "+errore);
            Log.d(TAG_LOG,"Retrive state: "+CITY_NAME_STRING_KEY_FRAGMENT_EDIT_RESTAURANT+" valore: "+this.retrieve_city);
            Log.d(TAG_LOG,"Retrive state: "+CATEGORY_NAME_STRING_KEY_FRAGMENT_EDIT_RESTAURANT+" valore: "+this.retrieve_category);
        }
        else {
            Log.d(TAG_LOG,"Retrive state: "+ERROR_STRING_KEY_FRAGMENT_EDIT_RESTAURANT+" valore: null");
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        this.cities = new HashMap<>();
        this.categories = new HashMap<>();

        //se sono in modalità update e non c'è uno stato da recuperare
        if (this.update && this.saved_state==null)
            retrive_restaurant_info();
        else {
            retrive_cities();
            retrive_categories();
        }
    }

    private void retrive_restaurant_info(){
        progressDialog(true, getResources().getString(R.string.retrieving_restaurant_wait));


        Log.d(TAG_LOG, "inizio metodo retrive restuarant");

        DocumentReference docRef = db.collection("restaurants").document(this.restaurant_id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> data = document.getData();
                        Log.d(TAG_LOG, "DocumentSnapshot data: " + data);

                        restaurant = new Restaurant();
                        restaurant.setName((String) data.get("name"));
                        restaurant.setDescription((String) data.get("description"));
                        restaurant.setEmail((String) data.get("email"));
                        restaurant.setAddress((String) data.get("address"));
                        restaurant.setPhone((String) data.get("phone"));
                        restaurant.setCity((String) data.get("city"));
                        restaurant.setCategory((String) data.get("category"));
                        restaurant.setTags((List<String>) data.get("tags"));
                        restaurant.setAdmin_id((String) data.get("admin_id"));
                        restaurant.setId((String) document.getId());

                        retrieve_city=restaurant.getCity();
                        retrieve_category=restaurant.getCategory();


                        editText_name.setText(restaurant.getName());
                        editText_description.setText(restaurant.getDescription());
                        editText_mail.setText(restaurant.getEmail());
                        editText_address.setText(restaurant.getAddress());
                        editText_phone.setText(restaurant.getPhone());

                        if (!restaurant.getTag1().equals("")){
                            chip1.setText(restaurant.getTag1());
                            chip1.setVisibility(View.VISIBLE);
                            tags_insert[0]=true;
                        }

                        if (!restaurant.getTag2().equals("")){
                            chip2.setText(restaurant.getTag2());
                            chip2.setVisibility(View.VISIBLE);
                            tags_insert[1]=true;
                        }

                        if (!restaurant.getTag3().equals("")){
                            chip3.setText(restaurant.getTag3());
                            chip3.setVisibility(View.VISIBLE);
                            tags_insert[2]=true;
                        }


                        retrive_cities();
                        retrive_categories();

                        progressDialog(false, getResources().getString(R.string.retrieving_restaurant_wait));

                    } else {
                        Log.d(TAG_LOG, "No such document");
                        progressDialog(false, "");
                    }
                } else {
                    Log.d(TAG_LOG, "get failed with ", task.getException());
                    progressDialog(false, "");
                }
            }
        });

    }

    private void enter_tag(View view){


        String tag = editText_tags.getText().toString();
        if (this.tags_insert[0]==false){
            this.chip1.setText(tag);
            this.chip1.setVisibility(View.VISIBLE);
            this.tags_insert[0]=true;
            Toast.makeText(getContext(), "Add tag 1", Toast.LENGTH_SHORT).show();
        }
        else if (this.tags_insert[1]==false){
            this.chip2.setText(tag);
            //this.chip2.setChecked(true);
            this.chip2.setVisibility(View.VISIBLE);
            this.tags_insert[1]=true;
            Toast.makeText(getContext(), "Add tag 2", Toast.LENGTH_SHORT).show();
        }
        else if (this.tags_insert[2]==false){
            this.chip3.setText(tag);
            //this.chip3.setChecked(true);
            this.chip3.setVisibility(View.VISIBLE);
            this.tags_insert[2]=true;
            Toast.makeText(getContext(), "Add tag 1", Toast.LENGTH_SHORT).show();
        }



        editText_tags.clearFocus();
        editText_tags.setText("");
        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    private void retrive_cities(){
        db.collection("cities").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.d(TAG_LOG, "Città 2 - On complete");

                if (task.isSuccessful()) {
                    Log.d(TAG_LOG, "Città 3 -  is successfull");

                    for (QueryDocumentSnapshot document : task.getResult()) {

                        Map<String, Object> data = document.getData();
                        //Map<String, Object>
                        cities.put(document.getId(), (String)data.get("city"));
                    }
                    if (retrieve_city == null){
                        Log.d(TAG_LOG, "Nessuna città preselezionata");
                        setCitiesSpinner(cities, null);

                    }
                    else {
                        Log.d(TAG_LOG, "città preselezionata: "+retrieve_city);
                        setCitiesSpinner(cities, retrieve_city);
                    }



                    Log.d(TAG_LOG, "Città 4 - lista id città :"+cities.toString());

                } else {
                    Log.d(TAG_LOG, "Error getting documents: ", task.getException());
                }
            }
        });

    }

    private void retrive_categories(){
        db.collection("categories").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    Log.d(TAG_LOG, "is successfull");

                    for (QueryDocumentSnapshot document : task.getResult()) {

                        Map<String, Object> data = document.getData();
                        //Map<String, Object>
                        categories.put(document.getId(), (String)data.get("category"));
                    }
                    if (retrieve_category == null){
                        Log.d(TAG_LOG, "Nessuna categoria preselezionata");
                        setCategoriesSpinner(categories, null);

                    }
                    else {
                        Log.d(TAG_LOG, "città preselezionata: "+retrieve_category);
                        setCategoriesSpinner(categories, retrieve_category);
                    }



                    Log.d(TAG_LOG, "ista id categorie :"+categories.toString());

                } else {
                    Log.d(TAG_LOG, "Error getting documents: ", task.getException());
                }
            }
        });

    }

    //Popolo spinner città
    public void setCitiesSpinner (Map<String, Object> cities, String compareValue){
        Log.d(TAG_LOG,"set cities spinner");
        String[] items = Arrays.copyOf(cities.values().toArray(), cities.values().toArray().length, String[].class);

        //Array adapter per lista di activity
        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, items);
        spinner_cities.setAdapter(adapter);

        //metto un valore preciso
        if(compareValue!=null){
            int spinnerPosition = adapter.getPosition(compareValue);
            spinner_cities.setSelection(spinnerPosition);
        }

        this.textView_loading_cities.setVisibility(View.INVISIBLE);
        this.spinner_cities.setVisibility(View.VISIBLE);


    }

    //Popolo spinner categorie
    public void setCategoriesSpinner (Map<String, Object> categories, String compareValue){
        Log.d(TAG_LOG,"set categories spinner");
        String[] items = Arrays.copyOf(categories.values().toArray(), categories.values().toArray().length, String[].class);

        //Array adapter per lista di activity
        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, items);
        spinner_categories.setAdapter(adapter);

        //metto un valore preciso
        if(compareValue!=null){
            int spinnerPosition = adapter.getPosition(compareValue);
            spinner_categories.setSelection(spinnerPosition);
        }
        else {
            spinner_categories.setSelection(1);
        }

        this.textView_loading_categories.setVisibility(View.INVISIBLE);
        this.spinner_categories.setVisibility(View.VISIBLE);


    }

    //Controllo inserimento campi
    private Boolean checkFields (){
        Log.d(TAG_LOG, "check fields");
        String name = this.editText_name.getText().toString();
        String description = this.editText_description.getText().toString();
        String mail = this.editText_mail.getText().toString();
        String address = this.editText_address.getText().toString();
        String phone = this.editText_phone.getText().toString();

        String city = this.spinner_cities.getSelectedItem().toString();
        String category = this.spinner_categories.getSelectedItem().toString();

        Log.d(TAG_LOG, "name: "+name);
        if(TextUtils.isEmpty(name))
        {
            Log.d(TAG_LOG, "name vuoto");
            this.textView_error.setText(getResources().getString(R.string.name_mandatory));
            this.textView_error.setVisibility(View.VISIBLE);
            this.textView_error.requestFocus();

            return false;
        }

        if(TextUtils.isEmpty(description))
        {
            this.textView_error.setText(getResources().getString(R.string.description_mandatory));
            this.textView_error.setVisibility(View.VISIBLE);
            this.textView_error.requestFocus();
            return false;
        }

        if(TextUtils.isEmpty(mail))
        {
            this.textView_error.setText(getResources().getString(R.string.mail_mandatory));
            this.textView_error.setVisibility(View.VISIBLE);
            this.textView_error.requestFocus();

            return false;
        }

        if(TextUtils.isEmpty(address))
        {
            this.textView_error.setText(getResources().getString(R.string.address_mandatory));
            this.textView_error.setVisibility(View.VISIBLE);
            this.textView_error.requestFocus();

            return false;
        }


        if(TextUtils.isEmpty(phone))
        {
            this.textView_error.setText(getResources().getString(R.string.phone_mandatory));
            this.textView_error.setVisibility(View.VISIBLE);
            this.textView_error.requestFocus();
            return false;
        }

        if(!TextUtils.isDigitsOnly(phone))
        {
            this.textView_error.setText(getResources().getString(R.string.phone_not_digits_only));
            this.textView_error.setVisibility(View.VISIBLE);
            this.textView_error.requestFocus();
            return false;
        }




        return true;


    }

    //Interfaccia
    public interface CreateRestaurantInterface {
        public void create_restaurant(String name, String description, String email, String address,
                                      String city, String phone, String admin_id, String imageUrl, String category, List<String> tags);
        public void update_restaurant(String name, String description, String email, String address,
                                      String city, String phone, String admin_id, String imageUrl, String category, List<String> tags, String id);
        public void cancel();
    }


    private Fragment_Edit_Restaurant.CreateRestaurantInterface listener;

    //check if activity implement the interface
    @Override
    public void onAttach(Activity activity){
        Log.d(TAG_LOG,"onAttach fragment");

        super.onAttach(activity);
        if(activity instanceof Fragment_Edit_Restaurant.CreateRestaurantInterface){
            listener = (Fragment_Edit_Restaurant.CreateRestaurantInterface) activity;
        }
        else {
            Log.d(TAG_LOG,"Activity non ha interfaccia");
            throw new ClassCastException(activity.toString() +
                    "Does not implement the interface");
        }
    }

    public String getCity() {
        return this.spinner_cities.getSelectedItem().toString();
    }

    public String getCategory() {
        return this.spinner_categories.getSelectedItem().toString();
    }

    public void setError(String text){
        this.textView_error.setText(text);
        this.textView_error.setVisibility(View.VISIBLE);
        this.scrollView.smoothScrollTo(0,0);
    }

    public void setUpdate(){
        this.update = true;
        this.btn_create.setText(R.string.update);
    }

    public void setRestaurantID(String id){
        this.restaurant_id = id;
    }

    private void progressDialog(Boolean b, String text){

        if(b){
            this.progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage(text);
            progressDialog.show();

        }
        else{
            this.progressDialog.dismiss();
        }

    }
}