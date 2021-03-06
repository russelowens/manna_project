package com.example.mannaprototype.models;

import com.google.firebase.firestore.ServerTimestamp;
import com.google.type.Date;

public class InOutModel {
    String idNumber;
    String fullName;
    String blockAndLot;
    Object dateTime;
    String age;
    String contact;
    String userType;
    String inout;

    public InOutModel(){}

    public InOutModel(String idNumber, String fullName, String blockAndLot, Object dateTime, String age, String contact, String userType, String inout) {
        this.idNumber = idNumber;
        this.fullName = fullName;
        this.blockAndLot = blockAndLot;
        this.dateTime = dateTime;
        this.age = age;
        this.contact = contact;
        this.userType = userType;
        this.inout = inout;
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

    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getBlockAndLot() {
        return blockAndLot;
    }

    public void setBlockAndLot(String blockAndLot) {
        this.blockAndLot = blockAndLot;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
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

    public String getInout() {
        return inout;
    }

    public void setInout(String inout) {
        this.inout = inout;
    }
}
