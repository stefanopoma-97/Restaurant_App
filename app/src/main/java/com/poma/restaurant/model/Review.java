package com.poma.restaurant.model;

public class Review {
    private String id;
    private String text, user_id, restaurant_id;
    private int vote;

    public Review(){

    }
    public Review(String text, String user_id, String restaurant_id, int vote){
        this.text = text;
        this.user_id = user_id;
        this.restaurant_id = restaurant_id;
        this.vote = vote;

    }

    public Review(int vote){
        this.vote = vote;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(String restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public int getVote() {
        return vote;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }
}
