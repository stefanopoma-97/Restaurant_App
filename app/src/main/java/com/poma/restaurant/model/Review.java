package com.poma.restaurant.model;

public class Review {
    private String id;
    private String user_id, restaurant_id, location, service, experience, problems, username;
    private float vote;



    public Review(){

    }
    public Review(String experience, String user_id, String restaurant_id, float vote){
        this.experience = experience;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getProblems() {
        return problems;
    }

    public void setProblems(String problems) {
        this.problems = problems;
    }

    public void setVote(float vote) {
        this.vote = vote;
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

    public float getVote() {
        return vote;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Review{" +
                "user_id='" + user_id + '\'' +
                ", restaurant_id='" + restaurant_id + '\'' +
                ", location='" + location + '\'' +
                ", service='" + service + '\'' +
                ", experience='" + experience + '\'' +
                ", problems='" + problems + '\'' +
                ", username='" + username + '\'' +
                ", vote=" + vote +
                '}';
    }
}
