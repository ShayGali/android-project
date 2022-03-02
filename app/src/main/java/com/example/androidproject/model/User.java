package com.example.androidproject.model;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    String ID;
    String userName;
    String country;
    List<String> categories;
    List<String> friends;
    List<String> friendsRequests;
    List<String> rooms;

    public User(String userName, String country, List<String> categories, List<String> friends, List<String> friendsRequests, List<String> rooms) {
        this.userName = userName;
        this.country = country;
        this.categories = categories;
        this.friends = friends;
        this.friendsRequests = friendsRequests;
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

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public List<String> getFriendsRequests() {
        return friendsRequests;
    }

    public void setFriendsRequests(List<String> friendsRequests) {
        this.friendsRequests = friendsRequests;
    }

    public void setRooms(List<String> rooms) {
        this.rooms = rooms;
    }

    public List<String> getRooms() {
        return rooms;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
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
