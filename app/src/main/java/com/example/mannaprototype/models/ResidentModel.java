package com.example.mannaprototype.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ResidentModel implements Serializable {
    String idNumber;
    String fullName;
    String blockAndLot;
    Object dateTime;
    String age;
    String contact;
    String userName;
    String password;
    String userType;
    String status;
    Double latitude, longitude;

    public ResidentModel(){}

    public ResidentModel(String idNumber, String fullName, String blockAndLot, Object dateTime, String age, String contact, String userName, String password, String userType, String status, Double latitude, Double longitude) {
        this.idNumber = idNumber;
        this.fullName = fullName;
        this.blockAndLot = blockAndLot;
        this.dateTime = dateTime;
        this.age = age;
        this.contact = contact;
        this.userName = userName;
        this.password = password;
        this.userType = userType;
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Object getDateTime() {
        return dateTime;
    }

    public void setDateTime(Object dateTime) {
        this.dateTime = dateTime;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getBlockAndLot() {
        return blockAndLot;
    }

    public void setBlockAndLot(String blockAndLot) {
        this.blockAndLot = blockAndLot;
    }


    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
