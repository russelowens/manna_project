package com.example.mannaprototype.models;

import com.google.type.DateTime;

public class Scan {
    String value;
    DateTime dateTime;

    public Scan(){}

    public Scan(String value, DateTime dateTime) {
        this.value = value;
        this.dateTime = dateTime;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }
}
