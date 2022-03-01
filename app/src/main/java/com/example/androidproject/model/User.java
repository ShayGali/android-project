package com.example.androidproject.model;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    String userName;
    String country;
    ArrayList<String> categories;
    ArrayList<String> friends;
    ArrayList<String> rooms;

    public User(String userName, String country, ArrayList<String> categories, ArrayList<String> friends, ArrayList<String> rooms) {
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

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    public ArrayList<String> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<String> friends) {
        this.friends = friends;
    }

    public ArrayList<String> getRooms() {
        return rooms;
    }

    public void setRooms(ArrayList<String> rooms) {
        this.rooms = rooms;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", country='" + country + '\'' +
                ", categories=" + categories +
                ", friends=" + friends +
                ", rooms=" + rooms +
                '}';
    }
}
