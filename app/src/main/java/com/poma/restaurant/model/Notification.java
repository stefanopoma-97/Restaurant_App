package com.poma.restaurant.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Notification {

    private String id;
    private String user_id;
    private long date;
    private String time;
    private String type;
    private String content;
    private Boolean showed;
    private Boolean read;



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

    public String getDate() {
        SimpleDateFormat formatter=new SimpleDateFormat("dd MM yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis((long) date);
        return formatter.format(calendar.getTime());
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
}
