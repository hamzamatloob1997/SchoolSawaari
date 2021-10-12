package com.example.schoolsawari.helper;

import java.io.Serializable;

public class BookingModelClass implements Serializable {
    String ParentId,ParentsName,DriverId,Days,ParentImageUrl,RequestId,Date,DriverName,DriverImageUrl;
    boolean isAccepted;
    ModelLocation PickupLocation,DropOffLocation;

    public String getDriverName() {
        return DriverName;
    }

    public void setDriverName(String driverName) {
        DriverName = driverName;
    }

    public String getDriverImageUrl() {
        return DriverImageUrl;
    }

    public void setDriverImageUrl(String driverImageUrl) {
        DriverImageUrl = driverImageUrl;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getDays() {
        return Days;
    }

    public void setDays(String days) {
        Days = days;
    }

    public String getRequestId() {
        return RequestId;
    }

    public void setRequestId(String requestId) {
        RequestId = requestId;
    }

    public String getParentImageUrl() {
        return ParentImageUrl;
    }

    public void setParentImageUrl(String parentImageUrl) {
        ParentImageUrl = parentImageUrl;
    }

    public ModelLocation getPickupLocation() {
        return PickupLocation;
    }

    public void setPickupLocation(ModelLocation pickupLocation) {
        PickupLocation = pickupLocation;
    }

    public ModelLocation getDropOffLocation() {
        return DropOffLocation;
    }

    public void setDropOffLocation(ModelLocation dropOffLocation) {
        DropOffLocation = dropOffLocation;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }

    public String getParentsName() {
        return ParentsName;
    }

    public void setParentsName(String parentsName) {
        ParentsName = parentsName;
    }

    public String getParentId() {
        return ParentId;
    }

    public void setParentId(String parentId) {
        ParentId = parentId;
    }

    public String getDriverId() {
        return DriverId;
    }

    public void setDriverId(String driverId) {
        DriverId = driverId;
    }


}
