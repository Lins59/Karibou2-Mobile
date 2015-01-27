package com.telnet.objects;

/**
 * Created by Pierre on 13/12/2014.
 */
public class User {
    private String login;
    private String firstname;
    private String lastname;
    private String surname;
    private String color;
    private String message;
    private boolean away = false;
    private Integer id;
    private String picturePath;

    public User(Integer id, String login, String firstname, String lastname, String surname) {
        this.id = id;
        this.login = login;
        this.firstname = firstname;
        this.lastname = lastname;
        this.surname = surname;
    }

    public User(Integer id, String login, String firstname, String lastname, String surname, String message, boolean away, String picturePath) {
        this.id = id;
        this.login = login;
        this.firstname = firstname;
        this.lastname = lastname;
        this.surname = surname;
        this.message = message;
        this.away = away;
        this.picturePath = picturePath;
    }

    public String getPseudo() {
        String pseudo;
        if (surname != null) {
            pseudo = surname;
        } else {
            pseudo = login;
        }
        return pseudo;
    }


    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getSurname() {
        return surname;
    }

    public String getLogin() {
        return login;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (color != null ? !color.equals(user.color) : user.color != null) return false;
        if (firstname != null ? !firstname.equals(user.firstname) : user.firstname != null)
            return false;
        if (!id.equals(user.id)) return false;
        if (lastname != null ? !lastname.equals(user.lastname) : user.lastname != null)
            return false;
        if (!login.equals(user.login)) return false;
        if (message != null ? !message.equals(user.message) : user.message != null) return false;
        if (picturePath != null ? !picturePath.equals(user.picturePath) : user.picturePath != null)
            return false;
        if (surname != null ? !surname.equals(user.surname) : user.surname != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = login.hashCode();
        result = 31 * result + (firstname != null ? firstname.hashCode() : 0);
        result = 31 * result + (lastname != null ? lastname.hashCode() : 0);
        result = 31 * result + (surname != null ? surname.hashCode() : 0);
        result = 31 * result + (color != null ? color.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + id.hashCode();
        result = 31 * result + (picturePath != null ? picturePath.hashCode() : 0);
        return result;
    }
}
