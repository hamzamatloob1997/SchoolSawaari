package com.example.schoolsawari.helper;

import java.io.Serializable;

public class ModelLocation implements Serializable {
    Double Longitude,Latitude;

    public ModelLocation() {
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }
    public android.location.Location toGoogleLocation(){
        android.location.Location location=new android.location.Location("Dummy");
        location.setLongitude(Longitude);
        location.setLatitude(Latitude);
        return location;
    }
}
