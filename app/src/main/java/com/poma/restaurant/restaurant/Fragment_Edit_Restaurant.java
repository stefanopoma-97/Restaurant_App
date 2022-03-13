package com.poma.restaurant.restaurant;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.poma.restaurant.R;
import com.poma.restaurant.login.Fragment_Register;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Edit_Restaurant#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Edit_Restaurant extends Fragment {

    private static final String TAG_LOG = Fragment_Edit_Restaurant.class.getName();

    private Map<String, Object> cities;
    private static String retrieve_city = null;

    private FirebaseFirestore db;


    private Spinner spinner_cities;
    private Spinner spinner_categories;
    private TextView textView_loading_cities;


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
        this.spinner_categories = (Spinner) view.findViewById(R.id.spinner_edit_restaurant_city);
        this.textView_loading_cities = (TextView) view.findViewById(R.id.textview_edit_restaurant_loading_cities);


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.cities = new HashMap<>();
        retrive_cities();
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


    //Interfaccia
    public interface CreateRestaurantInterface {
        public void create_restaurant(String name, String description, String email, String address,
                             String city, String phone, String admin_id, String imageUrl, String category);
        public void update_restaurant(String name, String description, String email, String address,
                                      String city, String phone, String admin_id, String imageUrl, String category);
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
}