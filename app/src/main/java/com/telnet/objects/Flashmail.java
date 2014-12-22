package com.telnet.objects;

import java.util.Date;

public class Flashmail {
    private String id;
    private User sender;
    private Date date;
    private String message, oldMessage;

    public Flashmail(String id) {
        this.id = id;
    }

    public Flashmail(String id, User sender, Date date, String message) {
        this.id = id;
        this.sender = sender;
        this.date = date;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOldMessage() {
        return oldMessage;
    }

    public void setOldMessage(String oldMessage) {
        this.oldMessage = oldMessage;
    }

}
