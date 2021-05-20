package com.example.mannaprototype.models;

public class Notification {

    private Object timestamp;
    private String resident_id, visitor_name, inout_id;
    private boolean isDone;

    public Notification() {}

    public Notification(Object timestamp, String resident_id, String visitor_name, String inout_id, boolean isDone) {
        this.timestamp = timestamp;
        this.resident_id = resident_id;
        this.visitor_name = visitor_name;
        this.inout_id = inout_id;
        this.isDone = isDone;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }

    public String getResident_id() {
        return resident_id;
    }

    public void setResident_id(String resident_id) {
        this.resident_id = resident_id;
    }

    public String getVisitor_name() {
        return visitor_name;
    }

    public void setVisitor_name(String visitor_name) {
        this.visitor_name = visitor_name;
    }

    public String getInout_id() {
        return inout_id;
    }

    public void setInout_id(String inout_id) {
        this.inout_id = inout_id;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
