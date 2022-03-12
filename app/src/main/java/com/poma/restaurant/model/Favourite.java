package com.poma.restaurant.model;

public class Favourite {
    private String id;
    private String user_id, restaurant_id, restaurant_name, restaurant_category, restaurant_adrress;

    public Favourite(){

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

    public String getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(String restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public String getRestaurant_name() {
        return restaurant_name;
    }

    public void setRestaurant_name(String restaurant_name) {
        this.restaurant_name = restaurant_name;
    }

    public String getRestaurant_category() {
        return restaurant_category;
    }

    public void setRestaurant_category(String restaurant_category) {
        this.restaurant_category = restaurant_category;
    }

    public String getRestaurant_adrress() {
        return restaurant_adrress;
    }

    public void setRestaurant_adrress(String restaurant_adrress) {
        this.restaurant_adrress = restaurant_adrress;
    }
}
