package com.poma.restaurant.model;

import java.util.ArrayList;
import java.util.List;

public class Restaurant implements Comparable<Restaurant>{
    private String id;
    private String name, description, email, address, city, phone, admin_id,
            imageUrl, category, category_id;
    private int n_reviews;
    private Float vote;

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
    private List<String> tags = new ArrayList<String>();

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
