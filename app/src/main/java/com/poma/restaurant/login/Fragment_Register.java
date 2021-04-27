package com.poma.restaurant.login;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.poma.restaurant.R;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Register#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Register extends Fragment {
    private static final String TAG_LOG = Fragment_Register.class.getName();
    private Button btn_cancel;
    private Button btn_register;
    private EditText e_username;
    private EditText e_password;
    private DatePicker e_date;

    private EditText e_email;
    private EditText e_location;
    private EditText e_name;
    private EditText e_surname;

    private TextView error;


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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.cancel();
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkFields()){
                    String username = e_username.getText().toString();
                    String password = e_password.getText().toString();
                    String email = e_email.getText().toString();
                    String location = e_location.getText().toString();
                    String name = e_name.getText().toString();
                    String surname = e_surname.getText().toString();

                    Calendar cal = Calendar.getInstance();
                    cal.set(e_date.getYear(),e_date.getMonth(),e_date.getDayOfMonth());
                    final long date = cal.getTimeInMillis();

                    Log.d(TAG_LOG,"fragment manda comando register a activity");
                    listener.register(username,name,name, surname, location, email, date);
                }
                else{
                    return;
                }
            }
        });




        return view;
    }

    //Interfaccia
    //Activity deve implementare i metodi specificati
    public interface RegisterInterface {
        public void register(String username, String password, String name, String surname,
                             String location, String email, long date);
        public void cancel();
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
            throw new ClassCastException(activity.toString() +
                    "Does not implement the interface");
        }
    }

    public void setError(String text){
        this.error.setText(text);
    }

    private Boolean checkFields (){
        String username = this.e_username.getText().toString();
        String password = this.e_password.getText().toString();
        String email = this.e_email.getText().toString();
        String location = this.e_location.getText().toString();
        String name = this.e_name.getText().toString();
        String surname = this.e_surname.getText().toString();

        if(TextUtils.isEmpty(username))
        {
            this.error.setText(getResources().getString(R.string.user_mandatory));
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

        if(TextUtils.isEmpty(email))
        {
            this.error.setText(getResources().getString(R.string.email_mandatory));
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

}