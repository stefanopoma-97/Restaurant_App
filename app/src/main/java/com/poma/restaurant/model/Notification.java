package com.poma.restaurant.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Notification implements Comparable<Notification>{

    private String id;
    private String user_id;
    private long date;
    private String time;
    private String type;
    private String content;
    private Boolean showed;
    private Boolean read;
    private String useful_id;



    public Notification() {
    }

    public Notification(String user_id, String id, String type) {
        this.user_id = user_id;
        this.type = type;
        this.id = id;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDateformatter() {
        SimpleDateFormat formatter=new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis((long) date);
        return formatter.format(calendar.getTime());
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public Boolean getShowed() {
        return showed;
    }

    public void setShowed(Boolean showed) {
        this.showed = showed;
    }

    public String getUseful_id() {
        return useful_id;
    }

    public void setUseful_id(String useful_id) {
        this.useful_id = useful_id;
    }

    @Override
    public int compareTo(Notification o) {
        if (this.read==true && o.getRead()==false)
            return 1;
        else if (this.read==false && o.getRead()==true)
            return -1;
        else
            return 0;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "user_id='" + user_id + '\'' +
                ", date=" + date +
                ", time='" + time + '\'' +
                ", type='" + type + '\'' +
                ", content='" + content + '\'' +
                ", showed=" + showed +
                ", read=" + read +
                ", useful_id='" + useful_id + '\'' +
                '}';
    }
}
