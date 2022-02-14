package com.example.androidproject.model;

import java.util.Map;

public class User {
    String userName;
    String country;
    Map<String,String> categories;
    Map<String,String> friends;
    Map<String,String> rooms;


    public User(String userName, String country, Map<String, String> categories, Map<String, String> friends, Map<String, String> rooms) {
        this.userName = userName;
        this.country = country;
        this.categories = categories;
        this.friends = friends;
        this.rooms = rooms;
    }

    public User() {

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Map<String, String> getCategories() {
        return categories;
    }

    public void setCategories(Map<String, String> categories) {
        this.categories = categories;
    }

    public Map<String, String> getFriends() {
        return friends;
    }

    public void setFriends(Map<String, String> friends) {
        this.friends = friends;
    }

    public Map<String, String> getRooms() {
        return rooms;
    }

    public void setRooms(Map<String, String> rooms) {
        this.rooms = rooms;
    }
}
