package com.poma.restaurant.review;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.poma.restaurant.R;
import com.poma.restaurant.restaurant.Fragment_Edit_Restaurant;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Create_Review#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Create_Review extends Fragment {

    private static final String TAG_LOG = Fragment_Create_Review.class.getName();

    private static String error_state ="";

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private EditText editText_experience;
    private EditText editText_service;
    private EditText editText_location;
    private EditText editText_problem;
    private TextView textView_error;
    private AppCompatRatingBar rating;

    private Button btn_save;
    private Button btn_cancel;

    private String restaurant_id;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Create_Review() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Create_Review.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Create_Review newInstance(String param1, String param2) {
        Fragment_Create_Review fragment = new Fragment_Create_Review();
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
        View view = inflater.inflate(R.layout.fragment_create_review, container, false);

        this.editText_experience = view.findViewById(R.id.edittext_edit_review_experience);
        this.editText_service = view.findViewById(R.id.edittext_edit_review_service);
        this.editText_location = view.findViewById(R.id.edittext_edit_review_location);
        this.editText_problem = view.findViewById(R.id.edittext_edit_review_problem);
        this.rating = view.findViewById(R.id.ratingbar_create_review);
        this.textView_error = view.findViewById(R.id.textview_create_review_alert_message);
        this.btn_cancel= view.findViewById(R.id.btn_cancel_review);
        this.btn_save =view.findViewById(R.id.btn_save_review);

        this.btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.cancel();
            }
        });

        this.btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG_LOG, "on click save");
                if (checkFields()){
                    Log.d(TAG_LOG, "send to activity all data");
                    String experience = editText_experience.getText().toString();
                    String service =editText_service.getText().toString();
                    String location = editText_location.getText().toString();
                    String problem = editText_problem.getText().toString();
                    Float vote = rating.getRating();


                    listener.create_review(experience, service, location, problem, vote, restaurant_id);

                }
            }
        });



        return view;
    }


    //Interfaccia
    public interface CreateReviewInterface {
        public void create_review(String experience, String service, String location, String problems, Float vote, String restaurant_id);
        public void cancel();
    }


    private Fragment_Create_Review.CreateReviewInterface listener;

    //check if activity implement the interface
    @Override
    public void onAttach(Activity activity){
        Log.d(TAG_LOG,"onAttach fragment");

        super.onAttach(activity);
        if(activity instanceof Fragment_Create_Review.CreateReviewInterface){
            listener = (Fragment_Create_Review.CreateReviewInterface) activity;
        }
        else {
            Log.d(TAG_LOG,"Activity non ha interfaccia");
            throw new ClassCastException(activity.toString() +
                    "Does not implement the interface");
        }
    }

    private Boolean checkFields (){
        Log.d(TAG_LOG, "check fields");
        String experience = editText_experience.getText().toString();



        Log.d(TAG_LOG, "experience: "+experience);
        if(TextUtils.isEmpty(experience))
        {
            Log.d(TAG_LOG, "name vuoto");
            this.textView_error.setText(getResources().getString(R.string.name_mandatory));
            this.textView_error.setVisibility(View.VISIBLE);
            this.textView_error.requestFocus();

            return false;
        }


        return true;


    }

    public void setRestaurant_id(String id){
        this.restaurant_id=id;
    }
}