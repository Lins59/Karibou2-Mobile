package com.telnet.objects;

/**
 * Created by Pierre on 13/12/2014.
 */
public class User {
    private String pseudo;
    private String color;
    private String message;
    private boolean away;
    private Integer id;
    private String picturePath;

    public User(Integer id, String pseudo, String color) {
        this.id = id;
        this.pseudo = pseudo;
        this.color = color;
    }

    public User(Integer id, String pseudo, String color, String message, boolean away, String picturePath) {
        this.id = id;
        this.pseudo = pseudo;
        this.color = color;
        this.message = message;
        this.away = away;
        this.picturePath = picturePath;
    }

    public String getPseudo() {
        return pseudo;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isAway() {
        return away;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPicturePath() {
        return picturePath;
    }
}
