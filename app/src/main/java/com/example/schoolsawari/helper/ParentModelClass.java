package com.example.schoolsawari.helper;

import java.io.Serializable;

public class ParentModelClass implements Serializable {
    String id,name,address,Number,noOfKids,profileImageUrl;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public String getNoOfKids() {
        return noOfKids;
    }

    public void setNoOfKids(String noOfKids) {
        this.noOfKids = noOfKids;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
