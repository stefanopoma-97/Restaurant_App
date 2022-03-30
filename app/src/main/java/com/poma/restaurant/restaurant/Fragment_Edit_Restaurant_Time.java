package com.poma.restaurant.restaurant;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.poma.restaurant.R;
import com.poma.restaurant.model.Restaurant;
import com.poma.restaurant.utilities.CustomTimePickerMorning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Edit_Restaurant_Time#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Edit_Restaurant_Time extends Fragment {

    private static final String TAG_LOG = Fragment_Edit_Restaurant_Time.class.getName();

    private static final String DAYS_NAME_STRING_KEY_FRAGMENT_EDIT_TIME= "com.poma.restaurant.DAYS_NAME_STRING_KEY_FRAGMENT_EDIT_TIME";
    private static final String TIMES_ARRAY_KEY_FRAGMENT_EDIT_TIME= "com.poma.restaurant.TIMES_ARRAY__KEY_FRAGMENT_EDIT_TIME";
    private static final String RESTAURANT_ID_KEY_FRAGMENT_EDIT_TIME= "com.poma.restaurant.RESTAURANT_ID_KEY_FRAGMENT_EDIT_TIME";


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
    private ProgressDialog progressDialog;


    //Info
    private HashMap<String, Boolean> days = new HashMap<>();
    private ArrayList<Integer> times = new ArrayList<Integer>();
    private int morning_start_hour, morning_end_hour, morning_start_minute, morning_end_minute,
            evening_start_hour, evening_end_hour, evening_start_minute, evening_end_minute;




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
        View view = inflater.inflate(R.layout.fragment_edit_restaurant_time, container, false);
        this.db = FirebaseFirestore.getInstance();
        this.mAuth= FirebaseAuth.getInstance();

        btn_all = view.findViewById(R.id.btn_all_days);
        btn_lunedi = view.findViewById(R.id.btn_lunedi);
        btn_martedi = view.findViewById(R.id.btn_martedi);
        btn_mercoledi = view.findViewById(R.id.btn_mercoledi);
        btn_giovedi = view.findViewById(R.id.btn_giovedi);
        btn_venerdi = view.findViewById(R.id.btn_venerdi);
        btn_sabato = view.findViewById(R.id.btn_sabato);
        btn_domenica = view.findViewById(R.id.btn_domenica);

        btn_morning_start = view.findViewById(R.id.btn_morning_start);
        btn_morning_end = view.findViewById(R.id.btn_morning_end);

        btn_evening_start = view.findViewById(R.id.btn_evening_start);
        btn_evening_end = view.findViewById(R.id.btn_evening_end);

        btn_save = view.findViewById(R.id.btn_save_time);

        morning = view.findViewById(R.id.switch_morning);
        evening = view.findViewById(R.id.switch_evening);


        days.put("all", false);
        days.put("lunedi", false);
        days.put("martedi", false);
        days.put("mercoledi", false);
        days.put("giovedi", false);
        days.put("venerdi", false);
        days.put("sabato", false);
        days.put("domenica", false);

        set_button();

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean monrning_active = false;
                boolean evening_active = false;
                if(morning.isChecked())
                    monrning_active=true;
                if(evening.isChecked())
                    evening_active=true;
                listener.edit_time(getDays(), monrning_active, evening_active, getTimes(), restaurant_id);
            }
        });


        morning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!morning.isChecked()){
                    btn_morning_start.setEnabled(false);
                    btn_morning_end.setEnabled(false);

                }
                else{
                    btn_morning_start.setEnabled(true);
                    btn_morning_end.setEnabled(true);
                }

            }
        });

        evening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!evening.isChecked()){
                    btn_evening_start.setEnabled(false);
                    btn_evening_end.setEnabled(false);

                }
                else{
                    btn_evening_start.setEnabled(true);
                    btn_evening_end.setEnabled(true);
                }

            }
        });

        btn_morning_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CustomTimePickerMorning.OnTimeSetListener onTimeSetListener = new CustomTimePickerMorning.OnTimeSetListener()
                {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute)
                    {
                        morning_start_hour = selectedHour;
                        morning_start_minute = selectedMinute;
                        btn_morning_start.setText(String.format(Locale.getDefault(), "%02d:%02d",selectedHour, selectedMinute));
                    }
                };

                CustomTimePickerMorning timePickerDialog = new CustomTimePickerMorning(getContext(),android.R.style.Theme_Holo_Light_Dialog_NoActionBar,onTimeSetListener, morning_start_hour, morning_start_minute, true);
                timePickerDialog.setTitle("Select Time");

                int max_h = 15;
                int max_m = 0;
                if (morning_end_hour<max_h){
                    max_h=morning_end_hour;
                    max_m=morning_end_minute;
                }

                Log.d(TAG_LOG,"Ora massima: "+max_h);

                timePickerDialog.setMax(max_h, max_m);
                timePickerDialog.setMin(6, 0);

                String max = null;
                String max2=null;
                if(max_m<10)
                    max2="0"+String.valueOf(max_m);
                else
                    max2=String.valueOf(max_m);
                if(max_h<10)
                    max="0"+String.valueOf(max_h);
                else
                    max=String.valueOf(max_h);
                timePickerDialog.setTitle(getResources().getString(R.string.select_time)+" (6:00 - "+max+":"+max2+")");
                timePickerDialog.show();
            }
        });


        btn_morning_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CustomTimePickerMorning.OnTimeSetListener onTimeSetListener = new CustomTimePickerMorning.OnTimeSetListener()
                {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute)
                    {
                        morning_end_hour = selectedHour;
                        morning_end_minute = selectedMinute;
                        btn_morning_end.setText(String.format(Locale.getDefault(), "%02d:%02d",selectedHour, selectedMinute));
                    }
                };

                CustomTimePickerMorning timePickerDialog2 = new CustomTimePickerMorning(getContext(),android.R.style.Theme_Holo_Light_Dialog_NoActionBar,onTimeSetListener, morning_end_hour, morning_end_minute, true);


                int min_h = 6;
                int min_m = 0;
                if (morning_start_hour>min_h){
                    min_h=morning_start_hour;
                    min_m=morning_start_minute;
                }
                Log.d(TAG_LOG,"Ora minima: "+min_h);


                timePickerDialog2.setMax(15, 0);
                timePickerDialog2.setMin(min_h, min_m);

                String min = null;
                String min2=null;
                if(min_m<10)
                    min2="0"+String.valueOf(min_m);
                else
                    min2=String.valueOf(min_m);
                if(min_h<10)
                    min="0"+String.valueOf(min_h);
                else
                    min=String.valueOf(min_h);
                timePickerDialog2.setTitle(getResources().getString(R.string.select_time)+" ("+min+":"+min2+" - 15:00)");
                timePickerDialog2.show();
            }
        });


        btn_evening_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomTimePickerMorning.OnTimeSetListener onTimeSetListener = new CustomTimePickerMorning.OnTimeSetListener()
                {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute)
                    {
                        evening_start_hour = selectedHour;
                        evening_start_minute = selectedMinute;
                        btn_evening_start.setText(String.format(Locale.getDefault(), "%02d:%02d",selectedHour, selectedMinute));
                    }
                };

                CustomTimePickerMorning timePickerDialog3 = new CustomTimePickerMorning(getContext(),android.R.style.Theme_Holo_Light_Dialog_NoActionBar,onTimeSetListener, evening_start_hour, evening_start_minute, true);

                int max_h = 4;
                int max_m = 0;
                if (evening_end_hour<max_h){
                    max_h=evening_end_hour;
                    max_m=evening_end_minute;
                }

                Log.d(TAG_LOG,"Ora massima: "+max_h);

                timePickerDialog3.setMax(max_h, max_m);
                timePickerDialog3.setMin(16, 0);

                String max = null;
                String max2=null;
                if(max_m<10)
                    max2="0"+String.valueOf(max_m);
                else
                    max2=String.valueOf(max_m);
                if(max_h<10)
                    max="0"+String.valueOf(max_h);
                else
                    max=String.valueOf(max_h);
                timePickerDialog3.setTitle(getResources().getString(R.string.select_time)+" (16:00 - "+max+":"+max2+")");
                timePickerDialog3.show();
            }
        });

        btn_evening_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CustomTimePickerMorning.OnTimeSetListener onTimeSetListener = new CustomTimePickerMorning.OnTimeSetListener()
                {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute)
                    {
                        evening_end_hour = selectedHour;
                        evening_end_minute = selectedMinute;
                        btn_evening_end.setText(String.format(Locale.getDefault(), "%02d:%02d",selectedHour, selectedMinute));
                    }
                };

                CustomTimePickerMorning timePickerDialog4 = new CustomTimePickerMorning(getContext(),android.R.style.Theme_Holo_Light_Dialog_NoActionBar,onTimeSetListener, evening_end_hour, evening_end_minute, true);


                int min_h = 16;
                int min_m = 0;
                if (evening_start_hour>min_h){
                    min_h=evening_start_hour;
                    min_m=evening_start_minute;
                }
                Log.d(TAG_LOG,"Ora minima: "+min_h);


                timePickerDialog4.setMax(4, 0);
                timePickerDialog4.setMin(min_h, min_m);

                String min = null;
                String min2=null;
                if(min_m<10)
                    min2="0"+String.valueOf(min_m);
                else
                    min2=String.valueOf(min_m);
                if(min_h<10)
                    min="0"+String.valueOf(min_h);
                else
                    min=String.valueOf(min_h);
                timePickerDialog4.setTitle(getResources().getString(R.string.select_time)+" ("+min+":"+min2+" - 04:00)");
                timePickerDialog4.show();
            }
        });


        return view;
    }



    @Override
    public void onStart() {
        super.onStart();
        if (this.saved_state==null)
            set_restaurant_time();
        else
            set_data();

    }

    private void setDays(List<Boolean> list){

        if (!list.contains(false)){
            set_pressed(btn_all);
            days.put("all", true);
            return;
        }
        days.put("lunedi", list.get(0));
        if (list.get(0)){
            set_pressed(btn_lunedi);
        }
        days.put("martedi", list.get(1));
        if (list.get(1)){
            set_pressed(btn_martedi);
        }
        days.put("mercoledi", list.get(2));
        if (list.get(2)){
            set_pressed(btn_mercoledi);
        }
        days.put("giovedi", list.get(3));
        if (list.get(3)){
            set_pressed(btn_giovedi);
        }
        days.put("venerdi", list.get(4));
        if (list.get(4)){
            set_pressed(btn_venerdi);
        }
        days.put("sabato", list.get(5));
        if (list.get(5)){
            set_pressed(btn_sabato);
        }
        days.put("domenica", list.get(6));
        if (list.get(6)){
            set_pressed(btn_domenica);
        }
    }

    private void set_restaurant_time(){
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

                        //morning
                        if (((Boolean) data.get("morning"))!=null){
                            Boolean m = (Boolean) data.get("morning");
                            if (m)
                                morning.setChecked(true);
                            else{
                                morning.setChecked(false);
                            }

                        }
                        else{
                            morning.setChecked(true);
                        }


                        //evening
                        if (((Boolean) data.get("evening"))!=null){
                            Boolean m = (Boolean) data.get("evening");
                            if (m)
                                evening.setChecked(true);
                            else{
                                evening.setChecked(false);
                            }
                        }
                        else{
                            evening.setChecked(true);
                        }

                        //days
                        if (((List<Boolean>) data.get("days"))!=null){
                            setDays((List<Boolean>) data.get("days"));
                        }

                        //times
                        ArrayList<Long> pl = (ArrayList<Long>) data.get("times");
                        Log.d(TAG_LOG, "TIMES ricavato da firestore: "+pl);
                        if (pl!=null){
                            if(pl.size()==8){
                                Log.d(TAG_LOG, "TIMES ricavato da firestore: "+pl);
                                morning_start_hour = pl.get(0).intValue();
                                morning_start_minute=pl.get(1).intValue();
                                morning_end_hour=pl.get(2).intValue();
                                morning_end_minute=pl.get(3).intValue();
                                evening_start_hour=pl.get(4).intValue();
                                evening_start_minute=pl.get(5).intValue();
                                evening_end_hour=pl.get(6).intValue();
                                evening_end_minute=pl.get(7).intValue();
                            }
                            else{
                                morning_start_hour=6;
                                morning_start_minute=0;
                                morning_end_hour=15;
                                morning_end_minute=0;
                                evening_start_hour=16;
                                evening_start_minute=0;
                                evening_end_hour=4;
                                evening_end_minute=0;
                            }

                        }
                        else{
                            morning_start_hour=6;
                            morning_start_minute=0;
                            morning_end_hour=15;
                            morning_end_minute=0;
                            evening_start_hour=16;
                            evening_start_minute=0;
                            evening_end_hour=4;
                            evening_end_minute=0;
                        }



                        set_data();
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

    private void set_data(){


        btn_morning_end.setText(String.format(Locale.getDefault(), "%02d:%02d",morning_end_hour, morning_end_minute));
        btn_morning_start.setText(String.format(Locale.getDefault(), "%02d:%02d",morning_start_hour, morning_start_minute));
        btn_evening_end.setText(String.format(Locale.getDefault(), "%02d:%02d",evening_end_hour, evening_end_minute));
        btn_evening_start.setText(String.format(Locale.getDefault(), "%02d:%02d",evening_start_hour, evening_start_minute));


        set_time();
    }

    private void set_time(){
        if (!morning.isChecked()){
            btn_morning_start.setEnabled(false);
            btn_morning_end.setEnabled(false);

        }
        else{
            btn_morning_start.setEnabled(true);
            btn_morning_end.setEnabled(true);
        }

        if (!evening.isChecked()){
            btn_evening_start.setEnabled(false);
            btn_evening_end.setEnabled(false);

        }
        else{
            btn_evening_start.setEnabled(true);
            btn_evening_end.setEnabled(true);
        }
    }

    //State
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putSerializable(DAYS_NAME_STRING_KEY_FRAGMENT_EDIT_TIME, this.days);
        this.times = new ArrayList<Integer>();
        times.add(morning_start_hour);
        times.add(morning_start_minute);
        times.add(morning_end_hour);
        times.add(morning_end_minute);
        times.add(evening_start_hour);
        times.add(evening_start_minute);
        times.add(evening_end_hour);
        times.add(evening_end_minute);
        savedInstanceState.putSerializable(TIMES_ARRAY_KEY_FRAGMENT_EDIT_TIME, this.times);
        savedInstanceState.putSerializable(RESTAURANT_ID_KEY_FRAGMENT_EDIT_TIME, this.restaurant_id);


        this.saved_state=null;
        super.onSaveInstanceState(savedInstanceState);

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG_LOG,"on Activity Create");

        if (savedInstanceState != null){
            Log.d(TAG_LOG,"Inizio retrive state");
            this.saved_state=true;
            this.days = (HashMap<String, Boolean>)savedInstanceState.getSerializable(DAYS_NAME_STRING_KEY_FRAGMENT_EDIT_TIME);
            this.times = (ArrayList<Integer>)savedInstanceState.getSerializable(TIMES_ARRAY_KEY_FRAGMENT_EDIT_TIME);
            this.restaurant_id = (String) savedInstanceState.getSerializable(RESTAURANT_ID_KEY_FRAGMENT_EDIT_TIME);
            Log.d(TAG_LOG,"days:"+this.days);
            Log.d(TAG_LOG,"times:"+this.times);

            for(Map.Entry<String, Boolean> element : days.entrySet()) {
                String key = element.getKey();
                Boolean value = element.getValue();

                if (key == "all" && value == true){
                    set_pressed(this.btn_all);
                }
                else if (key == "lunedi" && value == true){
                    set_pressed(this.btn_lunedi);
                }
                else if (key == "martedi" && value == true){
                    set_pressed(this.btn_martedi);
                }
                else if (key == "mercoledi" && value == true){
                    set_pressed(this.btn_mercoledi);
                }
                else if (key == "giovedi" && value == true){
                    set_pressed(this.btn_giovedi);
                }
                else if (key == "venerdi" && value == true){
                    set_pressed(this.btn_venerdi);
                }
                else if (key == "sabato" && value == true){
                    set_pressed(this.btn_sabato);
                }
                else if (key == "domenica" && value == true){
                    set_pressed(this.btn_domenica);
                }

            }


            this.morning_start_hour=this.times.get(0);
            this.morning_start_minute=this.times.get(1);
            this.morning_end_hour=this.times.get(2);
            this.morning_end_minute=this.times.get(3);
            this.evening_start_hour=this.times.get(4);
            this.evening_start_minute=this.times.get(5);
            this.evening_end_hour=this.times.get(6);
            this.evening_end_minute=this.times.get(7);


        }
        else {
            Log.d(TAG_LOG,"Non faccio Retrive state");
        }

    }

    private ArrayList<Boolean> getDays(){
        ArrayList<Boolean> arrayList = new ArrayList<Boolean>(Arrays.asList(false,false,false,false,false,false,false));
        for(Map.Entry<String, Boolean> element : days.entrySet()) {
            String key = element.getKey();
            Boolean value = element.getValue();
            Log.d(TAG_LOG,"Stampo HASH map: "+days);

            if (key == "all" && value == true){
                return new ArrayList<Boolean>(Arrays.asList(true, true, true, true, true, true, true));
            }
            if (key == "lunedi" && value == true){
                arrayList.set(0, true);

            }
            else if (key == "lunedi" && value == false){
                arrayList.set(0, false);
            }

            else if (key == "martedi" && value == true){
                arrayList.set(1, true);
            }
            else if (key == "martedi" && value == false){
                arrayList.set(1,false);
            }

            else if (key == "mercoledi" && value == true){
                arrayList.set(2,true);
            }
            else if (key == "mercoledi" && value == false){
                arrayList.set(2,false);
            }

            else if (key == "giovedi" && value == true){
                arrayList.set(3,true);
            }
            else if(key == "giovedi" && value == false) {
                arrayList.set(3,false);
            }

            else if (key == "venerdi" && value == true){
                arrayList.set(4,true);
            }
            else if (key == "venerdi" && value == false){
                arrayList.set(4,false);
            }

            else if (key == "sabato" && value == true){
                arrayList.set(5,true);
            }
            else if (key == "sabato" && value == false){
                arrayList.set(5,false);
            }

            else if (key == "domenica" && value == true){
                arrayList.set(6,true);
            }
            else if (key == "domenica" && value == false){
                arrayList.set(6,false);
            }

        }
        return arrayList;
    }

    private ArrayList<Integer> getTimes(){
        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.add(morning_start_hour);
        arrayList.add(morning_start_minute);
        arrayList.add(morning_end_hour);
        arrayList.add(morning_end_minute);
        arrayList.add(evening_start_hour);
        arrayList.add(evening_start_minute);
        arrayList.add(evening_end_hour);
        arrayList.add(evening_end_minute);
        return arrayList;
    }

    private void set_button(){
        this.btn_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG_LOG,"Click su ALL: "+days.toString());

                if(days.get("all")){
                    days.put("all", false);
                    Log.d(TAG_LOG,"pressed ALL: "+days.toString());
                    set_not_pressed(btn_all);
                }
                else {
                    days.put("all", true);
                    Log.d(TAG_LOG,"pressed ALL: "+days.toString());
                    set_pressed(btn_all);

                    set_not_pressed(btn_lunedi);
                    set_not_pressed(btn_martedi);
                    set_not_pressed(btn_mercoledi);
                    set_not_pressed(btn_giovedi);
                    set_not_pressed(btn_venerdi);
                    set_not_pressed(btn_sabato);
                    set_not_pressed(btn_domenica);


                    days.put("lunedi", false);
                    days.put("martedi", false);
                    days.put("mercoledi", false);
                    days.put("giovedi", false);
                    days.put("venerdi", false);
                    days.put("sabato", false);
                    days.put("domenica", false);
                }


            }
        });

        this.btn_lunedi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!days.get("all")){
                    if(days.get("lunedi")){
                        days.put("lunedi", false);
                        set_not_pressed(btn_lunedi);
                    }
                    else {
                        days.put("lunedi", true);
                        set_pressed(btn_lunedi);

                    }
                }


            }
        });

        this.btn_martedi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!days.get("all")){
                    if(days.get("martedi")){
                        days.put("martedi", false);
                        set_not_pressed(btn_martedi);
                    }
                    else {
                        days.put("martedi", true);
                        set_pressed(btn_martedi);

                    }
                }


            }
        });
        this.btn_mercoledi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!days.get("all")){
                    if(days.get("mercoledi")){
                        days.put("mercoledi", false);
                        set_not_pressed(btn_mercoledi);
                    }
                    else {
                        days.put("mercoledi", true);
                        set_pressed(btn_mercoledi);

                    }
                }


            }
        });
        this.btn_giovedi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!days.get("all")){
                    if(days.get("giovedi")){
                        days.put("giovedi", false);
                        set_not_pressed(btn_giovedi);
                    }
                    else {
                        days.put("giovedi", true);
                        set_pressed(btn_giovedi);

                    }
                }


            }
        });
        this.btn_venerdi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!days.get("all")){
                    if(days.get("venerdi")){
                        days.put("venerdi", false);
                        set_not_pressed(btn_venerdi);
                    }
                    else {
                        days.put("venerdi", true);
                        set_pressed(btn_venerdi);

                    }
                }


            }
        });

        this.btn_sabato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!days.get("all")){
                    if(days.get("sabato")){
                        days.put("sabato", false);
                        set_not_pressed(btn_sabato);
                    }
                    else {
                        days.put("sabato", true);
                        set_pressed(btn_sabato);

                    }
                }


            }
        });

        this.btn_domenica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!days.get("all")){
                    if(days.get("domenica")){
                        days.put("domenica", false);
                        set_not_pressed(btn_domenica);
                    }
                    else {
                        days.put("domenica", true);
                        set_pressed(btn_domenica);

                    }
                }


            }
        });
    }

    private void set_pressed(Button b){
        b.setBackgroundColor(getResources().getColor(R.color.red));
    }

    private void set_not_pressed(Button b){
        b.setBackgroundColor(getResources().getColor(R.color.grey));
    }

    //Interfaccia
    //Interfaccia
    public interface EditRestaurantTime {
        public void edit_time(List<Boolean>days, boolean morning, boolean evening, List<Integer>times, String id);
        public void cancel();
    }


    private Fragment_Edit_Restaurant_Time.EditRestaurantTime listener;

    //check if activity implement the interface
    @Override
    public void onAttach(Activity activity){
        Log.d(TAG_LOG,"onAttach fragment");

        super.onAttach(activity);
        if(activity instanceof Fragment_Edit_Restaurant_Time.EditRestaurantTime){
            listener = (Fragment_Edit_Restaurant_Time.EditRestaurantTime) activity;
        }
        else {
            Log.d(TAG_LOG,"Activity non ha interfaccia");
            throw new ClassCastException(activity.toString() +
                    "Does not implement the interface");
        }
    }

    public void set_restaurant_id(String id){
        this.restaurant_id=id;
    }
}