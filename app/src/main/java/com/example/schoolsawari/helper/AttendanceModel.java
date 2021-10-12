package com.example.schoolsawari.helper;

public class AttendanceModel {
    String DriverId,ParentId,RequestId,Date,PickHomeTime,PickSchoolTime,DropSchoolTime,DropHomeTime;

    public String getDriverId() {
        return DriverId;
    }

    public void setDriverId(String driverId) {
        DriverId = driverId;
    }

    public String getParentId() {
        return ParentId;
    }

    public void setParentId(String parentId) {
        ParentId = parentId;
    }

    public String getRequestId() {
        return RequestId;
    }

    public void setRequestId(String requestId) {
        RequestId = requestId;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getPickHomeTime() {
        return PickHomeTime;
    }

    public void setPickHomeTime(String pickHomeTime) {
        PickHomeTime = pickHomeTime;
    }

    public String getPickSchoolTime() {
        return PickSchoolTime;
    }

    public void setPickSchoolTime(String pickSchoolTime) {
        PickSchoolTime = pickSchoolTime;
    }

    public String getDropSchoolTime() {
        return DropSchoolTime;
    }

    public void setDropSchoolTime(String dropSchoolTime) {
        DropSchoolTime = dropSchoolTime;
    }

    public String getDropHomeTime() {
        return DropHomeTime;
    }

    public void setDropHomeTime(String dropHomeTime) {
        DropHomeTime = dropHomeTime;
    }
}
