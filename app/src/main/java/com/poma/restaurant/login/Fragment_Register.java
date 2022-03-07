package com.poma.restaurant.login;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.poma.restaurant.R;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Register#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Register extends Fragment {
    private static final String TAG_LOG = Fragment_Register.class.getName();
    private static final String ERROR_STRING_KEY_FRAGMENT_REGISTER = "com.poma.restaurant.ERROR_STRING_KEY_FRAGMENT_REGISTER";
    private static final String CITY_NAME_STRING_KEY_FRAGMENT_REGISTER = "com.poma.restaurant.CITY_NAME_STRING_KEY_FRAGMENT_REGISTER";
    private static String error_state ="";;
    private static String retrieve_city = null;

    private Button btn_cancel;
    private Button btn_register;
    private EditText e_username;
    private EditText e_password;
    private DatePicker e_date;

    private EditText e_email;
    private EditText e_location;
    private EditText e_name;
    private EditText e_surname;
    private Button btn_changepassword;
    private Button btn_changeemail;

    private TextView t_email;
    private TextView t_loading_cities;

    private Spinner spinner;

    private TextView error;
    private ScrollView scrollView;

    private static boolean modifica = false;

    private Map<String, Object> cities;
    private FirebaseFirestore db;

    //TODO progress bar


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Register() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Login_Admin.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Register newInstance(String param1, String param2) {
        Fragment_Register fragment = new Fragment_Register();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG_LOG,"On create");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG_LOG,"On createView");

        View view= (View) inflater.inflate(R.layout.fragment_register, container, false);

        this.btn_cancel = (Button)view.findViewById(R.id.button_registerform_cancel);
        this.btn_register = (Button)view.findViewById(R.id.button_registerform_register);
        this.e_username = (EditText)view.findViewById(R.id.edittext_registerform_username);
        this.e_password = (EditText)view.findViewById(R.id.edittext_registerform_password);
        this.error = (TextView)view.findViewById(R.id.textview_registerform_alert_message);
        this.e_date = (DatePicker)view.findViewById(R.id.datepicker_registerform_date);

        this.e_email = (EditText)view.findViewById(R.id.edittext_registerform_email);
        this.e_location = (EditText)view.findViewById(R.id.edittext_registerform_location);
        this.e_name = (EditText)view.findViewById(R.id.edittext_registerform_name);
        this.e_surname = (EditText)view.findViewById(R.id.edittext_registerform_surnamename);
        this.spinner = (Spinner) view.findViewById(R.id.spinner_registerform_location);


        this.btn_changepassword = (Button) view.findViewById(R.id.button_registerform_changepassword);
        this.btn_changeemail = (Button)view.findViewById(R.id.button_registerform_changeemail);
        this.scrollView = (ScrollView) view.findViewById(R.id.registerform_scrollview);
        this.t_loading_cities = (TextView)view.findViewById(R.id.textview_registerform_retrieve_cities);

        /*
        String[] items = new String[]{"Brescia", "Milano", "Bergamo"};
        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);*/

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.cancel();
            }
        });

        btn_changepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.changePassword();
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG_LOG,"Click su bottone register, modifica: "+modifica);

                if(modifica==false){
                    Log.d(TAG_LOG,"procedure registrazione");

                    if(checkFields()){
                        String username = e_username.getText().toString();
                        String password = e_password.getText().toString();
                        String email = e_email.getText().toString();
                        //String location = e_location.getText().toString();
                        String location = spinner.getSelectedItem().toString();
                        String name = e_name.getText().toString();
                        String surname = e_surname.getText().toString();

                        Calendar cal = Calendar.getInstance();
                        cal.set(e_date.getYear(),e_date.getMonth(),e_date.getDayOfMonth());
                        final long date = cal.getTimeInMillis();

                        Log.d(TAG_LOG,"fragment manda comando register a activity");
                        error.setVisibility(View.GONE);
                        listener.register(username,password,name, surname, location, email, date);
                    }
                    else{
                        return;
                    }
                }
                else {
                    Log.d(TAG_LOG,"procedure modifica");

                    if(checkUpdateFields()){
                        String username = e_username.getText().toString();
                        String location = spinner.getSelectedItem().toString();
                        String name = e_name.getText().toString();
                        String surname = e_surname.getText().toString();

                        /*Calendar cal = Calendar.getInstance();
                        cal.set(e_date.getYear(),e_date.getMonth(),e_date.getDayOfMonth());
                        final long date = cal.getTimeInMillis();*/
                        error.setVisibility(View.GONE);
                        listener.update(username, name, surname, location);
                    }
                    else {return;}
                }


            }
        });




        return view;
    }

    //State
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putString(ERROR_STRING_KEY_FRAGMENT_REGISTER, this.error.getText().toString());
        savedInstanceState.putString(CITY_NAME_STRING_KEY_FRAGMENT_REGISTER, getCity());
        this.retrieve_city = null;
        super.onSaveInstanceState(savedInstanceState);
        Log.d(TAG_LOG,"Save state: "+ERROR_STRING_KEY_FRAGMENT_REGISTER+" valore: "+this.error.getText().toString());
        Log.d(TAG_LOG,"Save state: "+CITY_NAME_STRING_KEY_FRAGMENT_REGISTER+" valore: "+getCity());
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG_LOG,"on Activity Create");

        if (savedInstanceState != null){
            String errore = savedInstanceState.getString(ERROR_STRING_KEY_FRAGMENT_REGISTER);
            if (errore==""){
                this.error_state="";
            }
            else {
                this.error_state=errore;
            }

            setError(this.error_state);

            this.retrieve_city = savedInstanceState.getString(CITY_NAME_STRING_KEY_FRAGMENT_REGISTER);

            Log.d(TAG_LOG,"Retrive state: "+ERROR_STRING_KEY_FRAGMENT_REGISTER+" valore: "+errore);
            Log.d(TAG_LOG,"Retrive state: "+CITY_NAME_STRING_KEY_FRAGMENT_REGISTER+" valore: "+this.retrieve_city);
        }
        else {
            Log.d(TAG_LOG,"Retrive state: "+ERROR_STRING_KEY_FRAGMENT_REGISTER+" valore: null");
        }

    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG_LOG, "on start");
        Log.d(TAG_LOG, "Città 1 - inizio metodo prendere città");
        this.cities = new HashMap<>();
        this.db = FirebaseFirestore.getInstance();
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
        Log.d(TAG_LOG, "Città 5 - fine");


    }

    //Interfaccia
    //Activity deve implementare i metodi specificati
    public interface RegisterInterface {
        public void register(String username, String password, String name, String surname,
                             String location, String email, long date);
        public void update(String username, String name, String surname, String location);
        public void cancel();

        public void changePassword();
    }

    private Fragment_Register.RegisterInterface listener;

    //check if activity implement the interface
    @Override
    public void onAttach(Activity activity){
        Log.d(TAG_LOG,"onAttach fragment");

        super.onAttach(activity);
        if(activity instanceof Fragment_Register.RegisterInterface){
            listener = (Fragment_Register.RegisterInterface) activity;
        }
        else {
            Log.d(TAG_LOG,"Activity non ha interfaccia");
            throw new ClassCastException(activity.toString() +
                    "Does not implement the interface");
        }
    }

    public void setError(String text){
        this.error.setText(text);
        this.error.setVisibility(View.VISIBLE);
        //this.t_focus.requestFocus();
        this.scrollView.smoothScrollTo(0,0);
    }


    //Controllo inserimento campi
    private Boolean checkFields (){
        String username = this.e_username.getText().toString();
        String password = this.e_password.getText().toString();
        String email = this.e_email.getText().toString();
        //String location = this.e_location.getText().toString();
        String location = this.spinner.getSelectedItem().toString();
        String name = this.e_name.getText().toString();
        String surname = this.e_surname.getText().toString();

        if(TextUtils.isEmpty(email))
        {
            this.error.setText(getResources().getString(R.string.email_mandatory));
            this.error.setVisibility(View.VISIBLE);
            this.error.requestFocus();

            return false;
        }

        if(TextUtils.isEmpty(password))
        {
            this.error.setText(getResources().getString(R.string.password_mandatory));
            this.error.setVisibility(View.VISIBLE);
            this.error.requestFocus();
            return false;
        }

        if(TextUtils.isEmpty(name))
        {
            this.error.setText(getResources().getString(R.string.name_mandatory));
            this.error.setVisibility(View.VISIBLE);
            this.error.requestFocus();

            return false;
        }

        if(TextUtils.isEmpty(surname))
        {
            this.error.setText(getResources().getString(R.string.surname_mandatory));
            this.error.setVisibility(View.VISIBLE);
            this.error.requestFocus();

            return false;
        }


        if(TextUtils.isEmpty(username))
        {
            this.error.setText(getResources().getString(R.string.user_mandatory));
            this.error.setVisibility(View.VISIBLE);
            this.error.requestFocus();
            return false;
        }

        if(TextUtils.isEmpty(location))
        {
            this.error.setText(getResources().getString(R.string.location_mandatory));
            this.error.setVisibility(View.VISIBLE);
            this.error.requestFocus();

            return false;
        }


        return true;


    }

    private Boolean checkUpdateFields (){
        String username = this.e_username.getText().toString();
        String location = this.spinner.getSelectedItem().toString();
        String name = this.e_name.getText().toString();
        String surname = this.e_surname.getText().toString();

        if(TextUtils.isEmpty(username))
        {
            this.error.setText(getResources().getString(R.string.user_mandatory));
            this.error.setVisibility(View.VISIBLE);
            this.error.requestFocus();
            return false;
        }


        if(TextUtils.isEmpty(location))
        {
            this.error.setText(getResources().getString(R.string.location_mandatory));
            this.error.setVisibility(View.VISIBLE);
            this.error.requestFocus();

            return false;
        }
        if(TextUtils.isEmpty(name))
        {
            this.error.setText(getResources().getString(R.string.name_mandatory));
            this.error.setVisibility(View.VISIBLE);
            this.error.requestFocus();

            return false;
        }
        if(TextUtils.isEmpty(surname))
        {
            this.error.setText(getResources().getString(R.string.surname_mandatory));
            this.error.setVisibility(View.VISIBLE);
            this.error.requestFocus();

            return false;
        }
        return true;
    }



    //SETTERS
    public void setE_username(String e_username) {
        this.e_username.setText(e_username);
    }

    public void setE_password(String e_password) {
        this.e_password.setText(e_password);
    }

    public void setE_date(long e_date) {
        final DateFormat format = DateFormat.getDateInstance(DateFormat.MEDIUM);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(e_date);
        this.e_date.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH));
    }

    public void setE_email(String e_email) {
        this.e_email.setText(e_email);
    }

    public void setT_email(String e_email) {
        this.t_email.setText(e_email);
    }

    public void setE_location(String e_location) {
        this.e_location.setText(e_location);
    }

    public void setE_name(String e_name) {
        this.e_name.setText(e_name);
    }

    public void setE_surname(String e_surname) {
        this.e_surname.setText(e_surname);
    }

    public void setVisibilityDate(int v){
        this.e_date.setVisibility(v);
    }

    public void setVisibilityPassword(int v){
        this.e_password.setVisibility(v);
    }

    public void setVisibilityEmail(int v){
        this.e_email.setVisibility(v);
    }

    public void setVisibilityTextViewEmail(int v){
        this.t_email.setVisibility(v);
    }

    public void setVisibilityBtnPassword(int v){
        this.btn_changepassword.setVisibility(v);
    }

    public void setVisibilityBtnEmail(int v){
        this.btn_changeemail.setVisibility(v);
    }


    public void setRegisterText(String text){
        this.btn_register.setText(text);
    }

    public void setCancelText(String text){
        this.btn_cancel.setText(text);
    }

    //GETTERS

    public String getE_username() {
        return this.e_username.getText().toString();
    }

    public String getE_password() {
        return this.e_password.getText().toString();
    }

    public String getE_email() {
        return this.e_email.getText().toString();
    }

    public String getT_email() {
        return this.t_email.getText().toString();
    }

    public String getE_name() {
        return this.e_name.getText().toString();
    }

    public String getE_surname() {
        return this.e_surname.getText().toString();
    }

    public String getE_location() {
        return this.e_location.getText().toString();
    }

    public String getCity() {
        return this.spinner.getSelectedItem().toString();
    }

    public long  getE_date() {
        Calendar cal = Calendar.getInstance();
        cal.set(e_date.getYear(),e_date.getMonth(),e_date.getDayOfMonth());
        long date = cal.getTimeInMillis();
        return date;
    }

    public void setModifica (boolean b){
        modifica=b;
    }

    //Popolo spinner città
    public void setCitiesSpinner (Map<String, Object> cities, String compareValue){
        //Map into array
        String[] items = Arrays.copyOf(cities.values().toArray(), cities.values().toArray().length, String[].class);

        //Array adapter per lista di activity
        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);

        //metto un valore preciso
        if(compareValue!=null){
            int spinnerPosition = adapter.getPosition(compareValue);
            spinner.setSelection(spinnerPosition);
        }

        this.t_loading_cities.setVisibility(View.INVISIBLE);
        this.spinner.setVisibility(View.VISIBLE);


    }

    //da nome città setta la posizione dello spinner
    public void setCitySpinnerValue (String s){
        ArrayAdapter adapter = (ArrayAdapter)spinner.getAdapter();
        int spinnerPosition = adapter.getPosition(s);
        spinner.setSelection(spinnerPosition);

    }





}