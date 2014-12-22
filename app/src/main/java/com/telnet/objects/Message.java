package com.telnet.objects;

import java.util.Date;

public class Message {
    private String pseudo;
    private String message;
    private String color;
    private Date date;

    public Message(String pseudo, String message, Date date, String color) {
        this.pseudo = pseudo;
        this.message = message;
        this.date = date;
        this.color = color;
    }

    public String getMessage() {
        return message;
    }

    public String getColor() {
        return color;
    }

    public String getPseudo() {
        return pseudo;
    }

    public Date getDate() {
        return date;
    }

}
