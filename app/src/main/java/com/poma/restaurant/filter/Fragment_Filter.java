package com.poma.restaurant.filter;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.poma.restaurant.R;
import com.poma.restaurant.model.Notification;
import com.poma.restaurant.notifications.Fragment_Notification;
import com.poma.restaurant.notifications.Fragment_Notification_List;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Filter#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Filter extends Fragment {

    private static final String TAG_LOG = Fragment_Filter.class.getName();

    private static final String CITY_NAME_STRING_KEY_FRAGMENT_FILTER= "com.poma.restaurant.CITY_NAME_STRING_KEY_FRAGMENT_FILTER";
    private static final String CATEGORY_NAME_STRING_KEY_FRAGMENT_FILTER= "com.poma.restaurant.CATEGORY_NAME_STRING_KEY_FRAGMENT_FILTER";

    //Filter
    private String city_filter = "";
    private Float vote_filter = new Float(0);
    private ArrayList<String> categories_filter = new ArrayList<>();
    private HashMap<String, Boolean> btn_pressed = new HashMap<>();
    private Boolean filtered = false;

    public void setFiltered(Boolean filtered) {
        this.filtered = filtered;
    }

    private Map<String, Object> cities;
    private static String retrieve_city = null;

    private FirebaseFirestore db;

    private FirebaseAuth mAuth;


    private TextView textView_loading_cities;
    private Spinner spinner;
    private AppCompatRatingBar rating;

    private Button btn_all;
    private Button btn_pizzeria;
    private Button btn_bar;
    private Button btn_sushi;
    private Button btn_fastfood;
    private Button btn_pasticceria;

    private Button btn_filter;
    private Button btn_reset;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Filter() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Filter.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Filter newInstance(String param1, String param2) {
        Fragment_Filter fragment = new Fragment_Filter();
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
        View view = inflater.inflate(R.layout.fragment_filter, container, false);

        this.db = FirebaseFirestore.getInstance();

        this.textView_loading_cities = view.findViewById(R.id.textview_loading_cities_filter);
        this.spinner = view.findViewById(R.id.spinner_cities_filter);
        this.rating = view.findViewById(R.id.ratingbar_filter);

        this.btn_all = view.findViewById(R.id.btn_all_categories);
        this.btn_bar = view.findViewById(R.id.btn_category_bar);
        this.btn_fastfood = view.findViewById(R.id.btn_category_fast_food);
        this.btn_pasticceria = view.findViewById(R.id.btn_category_pasticceria);
        this.btn_pizzeria = view.findViewById(R.id.btn_category_pizzeria);
        this.btn_sushi = view.findViewById(R.id.btn_category_sushi);

        btn_pressed.put("all", false);
        btn_pressed.put("bar", false);
        btn_pressed.put("fastfood", false);
        btn_pressed.put("pasticceria", false);
        btn_pressed.put("pizzeria", false);
        btn_pressed.put("sushi", false);

        this.btn_filter = view.findViewById(R.id.btn_filter);
        this.btn_reset = view.findViewById(R.id.btn_reset_filter);

        this.btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.reset_filter();
            }
        });

        this.btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                city_filter = getCity();
                vote_filter = getVote();
                categories_filter = getCategories();


                listener.filter(city_filter, vote_filter, categories_filter);
            }
        });



        return view;
    }

    private String getCity(){
        if (spinner.getSelectedItem().toString().equals("All"))
            return "";
        else
            return spinner.getSelectedItem().toString();
    }

    private Float getVote(){
        return rating.getRating();
    }

    private ArrayList<String> getCategories(){
        ArrayList<String> categories = new ArrayList<>();
        for(Map.Entry<String, Boolean> element : btn_pressed.entrySet()) {
            String key = element.getKey();
            Boolean value = element.getValue();

            if (key == "all" && value == true){
                return new ArrayList<String>();
            }
            else if (key == "bar" && value == true){
                categories.add("Bar");
            }
            else if (key == "fastfood" && value == true){
                categories.add("Fast Food");
            }
            else if (key == "pasticceria" && value == true){
                categories.add("Pastry Shop");
            }
            else if (key == "pizzeria" && value == true){
                categories.add("Pizzeria");
            }
            else if (key == "sushi" && value == true){
                categories.add("Sushi");
            }

        }
        return categories;
    }

    private void set_pressed(Button b){
        b.setBackgroundColor(getResources().getColor(R.color.red));
    }

    private void set_not_pressed(Button b){
        b.setBackgroundColor(getResources().getColor(R.color.grey));
    }

    private void set_button(){
        this.btn_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_pressed.get("all")){
                    btn_pressed.put("all", false);
                    Log.d(TAG_LOG,"pressed ALL: "+btn_pressed.toString());
                    set_not_pressed(btn_all);
                }
                else {
                    btn_pressed.put("all", true);
                    Log.d(TAG_LOG,"pressed ALL: "+btn_pressed.toString());
                    set_pressed(btn_all);

                    set_not_pressed(btn_bar);
                    set_not_pressed(btn_fastfood);
                    set_not_pressed(btn_pasticceria);
                    set_not_pressed(btn_pizzeria);
                    set_not_pressed(btn_sushi);

                    btn_pressed.put("bar", false);
                    btn_pressed.put("fastfood", false);
                    btn_pressed.put("pasticceria", false);
                    btn_pressed.put("pizzeria", false);
                    btn_pressed.put("sushi", false);
                }


            }
        });

        this.btn_sushi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_pressed.get("sushi")){
                    btn_pressed.put("sushi", false);
                    set_not_pressed(btn_sushi);
                }
                else {
                    btn_pressed.put("sushi", true);
                    set_pressed(btn_sushi);

                }

            }
        });

        this.btn_pizzeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_pressed.get("pizzeria")){
                    btn_pressed.put("pizzeria", false);
                    set_not_pressed(btn_pizzeria);
                }
                else {
                    btn_pressed.put("pizzeria", true);
                    set_pressed(btn_pizzeria);

                }

            }
        });
        this.btn_pasticceria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_pressed.get("pasticceria")){
                    btn_pressed.put("pasticceria", false);
                    set_not_pressed(btn_pasticceria);
                }
                else {
                    btn_pressed.put("pasticceria", true);
                    set_pressed(btn_pasticceria);

                }

            }
        });
        this.btn_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_pressed.get("bar")){
                    btn_pressed.put("bar", false);
                    set_not_pressed(btn_bar);
                }
                else {
                    btn_pressed.put("bar", true);
                    set_pressed(btn_bar);

                }

            }
        });
        this.btn_fastfood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_pressed.get("fastfood")){
                    btn_pressed.put("fastfood", false);
                    set_not_pressed(btn_fastfood);
                }
                else {
                    btn_pressed.put("fastfood", true);
                    set_pressed(btn_fastfood);

                }

            }
        });
    }


    //State
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putString(CITY_NAME_STRING_KEY_FRAGMENT_FILTER, spinner.getSelectedItem().toString());
        savedInstanceState.putSerializable(CATEGORY_NAME_STRING_KEY_FRAGMENT_FILTER, this.btn_pressed);
        this.retrieve_city = null;
        super.onSaveInstanceState(savedInstanceState);

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG_LOG,"on Activity Create");

        if (savedInstanceState != null){
            Log.d(TAG_LOG,"Inizio retrive state");

            this.retrieve_city = savedInstanceState.getString(CITY_NAME_STRING_KEY_FRAGMENT_FILTER);
            this.btn_pressed = (HashMap<String, Boolean>)savedInstanceState.getSerializable(CATEGORY_NAME_STRING_KEY_FRAGMENT_FILTER);

            for(Map.Entry<String, Boolean> element : btn_pressed.entrySet()) {
                String key = element.getKey();
                Boolean value = element.getValue();

                if (key == "all" && value == true){
                    set_pressed(this.btn_all);
                }
                else if (key == "bar" && value == true){
                    set_pressed(this.btn_bar);
                }
                else if (key == "fastfood" && value == true){
                    set_pressed(this.btn_fastfood);
                }
                else if (key == "pasticceria" && value == true){
                    set_pressed(this.btn_pasticceria);
                }
                else if (key == "pizzeria" && value == true){
                    set_pressed(this.btn_pizzeria);
                }
                else if (key == "sushi" && value == true){
                    set_pressed(this.btn_sushi);
                }

            }


        }
        else {
            Log.d(TAG_LOG,"Non faccio Retrive state");
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        this.cities = new HashMap<>();

        set_button();
        if (this.filtered)
            restore_filter();
        retrive_cities();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.retrieve_city = null;
    }

    private void restore_filter(){


        //Ripristino città
        if(!this.city_filter.equals(""))
            this.retrieve_city=city_filter;

        //Ripristino rating
        this.rating.setRating(this.vote_filter);

        //ripristino categoria
        if (this.categories_filter.size()>0){
            for (String cat : this.categories_filter){
                if (cat.equals("Bar")){
                    set_pressed(this.btn_bar);
                    this.btn_pressed.put("bar", true);
                }
                else if (cat.equals("Fast Food")){
                    set_pressed(this.btn_fastfood);
                    this.btn_pressed.put("fastfood", true);
                }
                else if (cat.equals("Pizzeria")){
                    set_pressed(this.btn_pizzeria);
                    this.btn_pressed.put("pizzeria", true);
                }
                else if (cat.equals("Pastry Shop")){
                    set_pressed(this.btn_pasticceria);
                    this.btn_pressed.put("pasticceria", true);
                }
                else if (cat.equals("Sushi")){
                    set_pressed(this.btn_sushi);
                    this.btn_pressed.put("sushi", true);
                }

            }
        }

    }


    private void retrive_cities(){
        db.collection("cities").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {

                        Map<String, Object> data = document.getData();
                        cities.put(document.getId(), (String)data.get("city"));
                    }
                    cities.put("0", "All");
                    if (retrieve_city == null){
                        Log.d(TAG_LOG, "Nessuna città preselezionata");
                        setCitiesSpinner(cities, "All");

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

    //Popolo spinner città
    public void setCitiesSpinner (Map<String, Object> cities, String compareValue){
        Log.d(TAG_LOG,"set cities spinner");
        String[] items = Arrays.copyOf(cities.values().toArray(), cities.values().toArray().length, String[].class);
        Arrays.sort(items);

        //Array adapter per lista di activity
        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);

        //metto un valore preciso
        if(compareValue!=null){
            int spinnerPosition = adapter.getPosition(compareValue);
            spinner.setSelection(spinnerPosition);
        }

        this.textView_loading_cities.setVisibility(View.INVISIBLE);
        this.spinner.setVisibility(View.VISIBLE);


    }

    //Interfaccia
    //Activity deve implementare i metodi specificati
    public interface FilterInterface {
        public void reset_filter();
        public void filter(String city, Float vote, ArrayList<String> categories);
    }

    private Fragment_Filter.FilterInterface listener;

    //check if activity implement the interface
    @Override
    public void onAttach(Activity activity){
        Log.d(TAG_LOG,"onAttach fragment");

        super.onAttach(activity);
        if(activity instanceof Fragment_Filter.FilterInterface){
            listener = (Fragment_Filter.FilterInterface) activity;
        }
        else {
            throw new ClassCastException(activity.toString() +
                    "Does not implement the interface");
        }
    }


    public void setCity_filter(String city_filter) {
        this.city_filter = city_filter;
    }

    public void setVote_filter(Float vote_filter) {
        this.vote_filter = vote_filter;
    }

    public void setCategories_filter(ArrayList<String> categories_filter) {
        this.categories_filter = categories_filter;
    }
}