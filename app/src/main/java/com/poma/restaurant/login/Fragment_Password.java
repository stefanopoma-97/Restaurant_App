package com.poma.restaurant.login;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.poma.restaurant.R;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Password#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Password extends Fragment {

    private static final String TAG_LOG = Fragment_Password.class.getName();
    private Button btn_cancel;
    private Button btn_save;
    private EditText e_old_password;
    private EditText e_new_password;
    private EditText e_repeat_password;

    private TextView error;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Password() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Password.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Password newInstance(String param1, String param2) {
        Fragment_Password fragment = new Fragment_Password();
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

        View view = (View)inflater.inflate(R.layout.fragment_password, container, false);

        this.btn_cancel = (Button)view.findViewById(R.id.button_password_cancel);
        this.btn_save = (Button)view.findViewById(R.id.button_password_save);
        this.e_new_password = (EditText)view.findViewById(R.id.edittext_password_newpassword);
        this.e_old_password = (EditText)view.findViewById(R.id.edittext_password_oldpassword);
        this.e_repeat_password = (EditText)view.findViewById(R.id.edittext_password_repeatpassword);

        this.error = (TextView)view.findViewById(R.id.textview_password_alert_message);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.cancel();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkFields())
                {
                    listener.save(e_new_password.getText().toString(), e_old_password.getText().toString());

                }
                else
                    return;
            }
        });



        return view;
    }

    //Interfaccia
    //Activity deve implementare i metodi specificati
    public interface PasswordInterface {
        public void save(String new_password, String old_password);
        public void cancel();
    }

    private Fragment_Password.PasswordInterface listener;

    //check if activity implement the interface
    @Override
    public void onAttach(Activity activity){
        Log.d(TAG_LOG,"onAttach fragment");

        super.onAttach(activity);
        if(activity instanceof Fragment_Password.PasswordInterface){
            listener = (Fragment_Password.PasswordInterface) activity;
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
        this.error.requestFocus();
    }

    private Boolean checkFields (){
        String new_password = this.e_new_password.getText().toString();
        String old_password = this.e_old_password.getText().toString();
        String repeat_password = this.e_repeat_password.getText().toString();

        if(TextUtils.isEmpty(old_password))
        {
            this.error.setText(getResources().getString(R.string.old_password));
            this.error.setVisibility(View.VISIBLE);
            this.error.requestFocus();
            return false;
        }

        if(TextUtils.isEmpty(new_password))
        {
            this.error.setText(getResources().getString(R.string.new_password));
            this.error.setVisibility(View.VISIBLE);
            this.error.requestFocus();
            return false;
        }

        if(TextUtils.isEmpty(repeat_password))
        {
            this.error.setText(getResources().getString(R.string.repeat_password));
            this.error.setVisibility(View.VISIBLE);
            this.error.requestFocus();

            return false;
        }

        if(!new_password.equals(repeat_password))
        {
            this.error.setText(getResources().getString(R.string.password_not_match));
            this.error.setVisibility(View.VISIBLE);
            this.error.requestFocus();

            return false;
        }

        if(new_password.equals(old_password))
        {
            this.error.setText(getResources().getString(R.string.password_same));
            this.error.setVisibility(View.VISIBLE);
            this.error.requestFocus();

            return false;
        }


        return true;
    }

    public void buttonSaveSetEnable(Boolean b){
        btn_save.setEnabled(b);
        if(b){
            btn_save.setBackgroundColor(getResources().getColor(R.color.red));
        }
        else
            btn_save.setBackgroundColor(getResources().getColor(R.color.red_light));

    }

    private void checkEnableButton(){
        String new_password = this.e_new_password.getText().toString();
        String old_password = this.e_old_password.getText().toString();
        String repeat_password = this.e_repeat_password.getText().toString();

        if((!TextUtils.isEmpty(new_password)) && (!TextUtils.isEmpty(old_password))
                && (!TextUtils.isEmpty(repeat_password))){
            buttonSaveSetEnable(true);
        }
        else {
            buttonSaveSetEnable(false);
        }
    }

    public String get_new(){
        return this.e_new_password.getText().toString();
    }

    public String get_old(){
        return this.e_old_password.getText().toString();
    }

    public String get_repeat(){
        return this.e_repeat_password.getText().toString();
    }

    public void set_new (String s){
        this.e_new_password.setText(s);
    }

    public void set_old (String s){
        this.e_old_password.setText(s);
    }

    public void set_repeat (String s){
        this.e_repeat_password.setText(s);
    }



}