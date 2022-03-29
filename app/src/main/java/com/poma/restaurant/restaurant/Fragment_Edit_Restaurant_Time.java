package com.poma.restaurant.restaurant;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.poma.restaurant.R;
import com.poma.restaurant.model.Restaurant;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Edit_Restaurant_Time#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Edit_Restaurant_Time extends Fragment {

    private static final String TAG_LOG = Fragment_Edit_Restaurant_Time.class.getName();

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private Button btn_all;
    private Button btn_lunedi;
    private Button btn_martedi;
    private Button btn_mercoledi;
    private Button btn_giovedi;
    private Button btn_venerdi;
    private Button btn_sabato;
    private Button btn_domenica;

    private Button btn_morning_start;
    private Button btn_morning_end;
    private Button btn_evening_start;
    private Button btn_evening_end;

    private Switch morning;
    private Switch evening;


    private Button btn_save;

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

    public Fragment_Edit_Restaurant_Time() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Edit_Restaurant_Time.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Edit_Restaurant_Time newInstance(String param1, String param2) {
        Fragment_Edit_Restaurant_Time fragment = new Fragment_Edit_Restaurant_Time();
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
        return inflater.inflate(R.layout.fragment_edit_restaurant_time, container, false);
    }
}