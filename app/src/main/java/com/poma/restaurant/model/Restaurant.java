package com.poma.restaurant.model;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.util.Log;

import com.poma.restaurant.model.RecyclerViewAdapter.RecyclerViewAdapter_Restaurant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Restaurant implements Comparable<Restaurant>{
    private static final String TAG_LOG = "Restaurant class";
    private String id;
    private String name, description, email, address, city, phone, admin_id,
            imageUrl, category, category_id;
    private int n_reviews;
    private Float vote;
    private List<String> tags = new ArrayList<String>();
    private List<Boolean> days = new ArrayList<Boolean>(Arrays.asList(false,false,false,false,false,false,false));
    private List<Integer> times = new ArrayList<Integer>(Arrays.asList(6,0,15,0,16,0,4,0));
    private boolean morning = false;
    private boolean evening = false;

    public Boolean isOpen(){
        Log.d(TAG_LOG, "IS open:"+name);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("EEEE - HH:mm");

        int day_now = calendar.get(Calendar.DAY_OF_WEEK); //1-7
        int day = day_now-2;
        if (day==-1)
            day=6;


        int hour_now = calendar.get(Calendar.HOUR_OF_DAY); //0-23
        int minute_now = calendar.get(Calendar.MINUTE);//0-59

        //TEST
        //hour_now=23;
        //minute_now=0;

        boolean morning_now = false;
        boolean evening_now = false;
        if (hour_now>=6 && hour_now<=15){
            morning_now=true;
            evening_now=false;
        }
        else if (hour_now>=16 && hour_now<=23){
            morning_now=false;
            evening_now=true;
        }
        else if (hour_now>=0 && hour_now<=4){
            morning_now=false;
            evening_now=true;
        }

        //se sono tra le 00 e le 5 setto il giorno precedente
        if (hour_now<=5){
            day--;
            day_now--;
        }



        //Log.d(TAG_LOG, "ORARI NOW: settimana: "+day_now+", "+hour_now+" : "+minute_now+" morning:"+morning_now+" evening:"+evening_now);
        //Log.d(TAG_LOG, "ORARI APERTURA: settimana: "+days.get(day_now)+" Mattina("+morning+"):"+times.get(0)+":"+times.get(1)+" - "+times.get(2)+":"+times.get(3)+
        //        "   Sera("+evening+"):"+times.get(4)+":"+times.get(5)+" - "+times.get(6)+":"+times.get(7));
        boolean open = false;

        //TEST
        //calendar.set(Calendar.HOUR_OF_DAY, 23);
        //calendar.set(Calendar.MINUTE, 0);

        //calendar.set(Calendar.DAY_OF_WEEK, day_now);
        Date time_now = calendar.getTime();
        Log.d(TAG_LOG, "Orario formattato ora: "+mdformat.format(time_now));


        Calendar cal = Calendar.getInstance();




        if (days.get(day)==true){
            Log.d(TAG_LOG, "è aperto questo giorno");
            if (morning_now == true && morning==true){
                Log.d(TAG_LOG, "è mattina ed è aperto la mattina");
                cal.set(Calendar.HOUR_OF_DAY, times.get(0));
                cal.set(Calendar.MINUTE, times.get(1));
                Date time_morning_start = cal.getTime();

                cal.set(Calendar.HOUR_OF_DAY, times.get(2));
                cal.set(Calendar.MINUTE, times.get(3));
                Date time_morning_end = cal.getTime();

                if ((time_now.after(time_morning_start) || time_now.equals(time_morning_start))
                &&(time_now.before(time_morning_end) || time_now.equals(time_morning_end))){
                    open=true;
                    Log.d(TAG_LOG, "Segno come aperto");
                }

            }
            else if (evening_now == true && evening==true){
                Log.d(TAG_LOG, "è sera ed è aperto la sera");
                cal.set(Calendar.HOUR_OF_DAY, times.get(4));
                cal.set(Calendar.MINUTE, times.get(5));
                cal.set(Calendar.DAY_OF_WEEK, day_now);
                if (times.get(4)<=5)
                    cal.set(Calendar.DAY_OF_WEEK, day_now+1);

                Date time_evening_start = cal.getTime();

                cal.set(Calendar.HOUR_OF_DAY, times.get(6));
                cal.set(Calendar.MINUTE, times.get(7));
                cal.set(Calendar.DAY_OF_WEEK, day_now);
                if (times.get(6)<=5){
                    Log.d(TAG_LOG, "aumento giorno di 1");
                    cal.set(Calendar.DAY_OF_WEEK, day_now+1);
                }

                Date time_evening_end = cal.getTime();

                Log.d(TAG_LOG, "Orario sera start: "+mdformat.format(time_evening_start));
                Log.d(TAG_LOG, "Orario sera end: "+mdformat.format(time_evening_end));

                if ((time_now.after(time_evening_start) || time_now.equals(time_evening_start))
                        &&(time_now.before(time_evening_end) || time_now.equals(time_evening_end))){
                    open=true;
                    Log.d(TAG_LOG, "Segno come aperto");
                }


            }
        }
        return open;


    }

    public List<Boolean> getDays() {
        return days;
    }

    public void setDays(List<Boolean> days) {
        this.days = days;
    }

    public List<Integer> getTimes() {
        return times;
    }

    public void setTimes(List<Integer> times) {
        this.times = times;
    }

    public boolean isMorning() {
        return morning;
    }

    public void setMorning(boolean morning) {
        this.morning = morning;
    }

    public boolean isEvening() {
        return evening;
    }

    public void setEvening(boolean evening) {
        this.evening = evening;
    }

    public String getMorningTime(){
        String h="";
        String m="";
        String h2="";
        String m2="";
        if (times.get(0)<10)
            h="0"+times.get(0);
        else
            h=times.get(0)+"";

        if (times.get(1)<10)
            m="0"+times.get(1);
        else
            m=times.get(1)+"";

        if (times.get(2)<10)
            h2="0"+times.get(2);
        else
            h2=times.get(2)+"";

        if (times.get(3)<10)
            m2="0"+times.get(3);
        else
            m2=times.get(3)+"";

        return h+":"+m+" - "+h2+":"+m2;

    }

    public String getEveningTime(){
        String h="";
        String m="";
        String h2="";
        String m2="";
        if (times.get(4)<10)
            h="0"+times.get(4);
        else
            h=times.get(4)+"";

        if (times.get(5)<10)
            m="0"+times.get(5);
        else
            m=times.get(5)+"";

        if (times.get(6)<10)
            h2="0"+times.get(6);
        else
            h2=times.get(6)+"";

        if (times.get(7)<10)
            m2="0"+times.get(7);
        else
            m2=times.get(7)+"";

        return h+":"+m+" - "+h2+":"+m2;

    }


    public int getN_reviews() {
        return n_reviews;
    }

    public void setN_reviews(int n_reviews) {
        this.n_reviews = n_reviews;
    }

    public Float getVote() {
        return vote;
    }


    public void setVote(Float vote) {
        this.vote = vote;
    }

    //TODO capire se gestire i Tags


    public Restaurant() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(String admin_id) {
        this.admin_id = admin_id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;

    }

    public String getTag1(){
        if (this.tags.size() >= 1)
            return tags.get(0);
        else
            return "";
    }

    public String getTag2(){
        if (this.tags.size() >= 2)
            return tags.get(1);
        else
            return "";
    }

    public String getTag3(){
        if (this.tags.size() >= 3)
            return tags.get(2);
        else
            return "";
    }


    @Override
    public int compareTo(Restaurant o) {
        return this.name.compareTo(o.name);
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", phone='" + phone + '\'' +
                ", admin_id='" + admin_id + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", category='" + category + '\'' +
                ", category_id='" + category_id + '\'' +
                ", n_reviews=" + n_reviews +
                ", vote=" + vote +
                ", tags=" + tags +
                '}';
    }
}
