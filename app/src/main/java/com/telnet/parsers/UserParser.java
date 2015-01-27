package com.telnet.parsers;

import android.util.Log;

import com.telnet.karibou.Constants;
import com.telnet.objects.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserParser {
    public static List<User> parse(String json) {
        // Parse users
        List<User> users = new ArrayList<User>();
        JSONArray jsonArray;

        try {
            jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject obj = jsonArray.getJSONObject(i);

                    Integer userId = obj.getInt("user_id");
                    String login = obj.getString("login");
                    String firstname = obj.getString("firstname");
                    String lastname = obj.getString("lastname");

                    // Optional fields
                    // Optional fields
                    String surname = null, message = null, picturePath = null;
                    boolean away = true;
                    if (obj.has("surname")) {
                        surname = obj.getString("surname");
                    }
                    if (obj.has("message")) {
                        message = obj.getString("message");
                    }
                    if (obj.has("away")) {
                        away = (obj.getInt("away") == 1);
                    }
                    if (obj.has("picture_path")) {
                        picturePath = Constants.BASE_URL + obj.getString("picture_path");
                    }
                    users.add(new User(userId, login, firstname, lastname, surname, message, away, picturePath));
                } catch (JSONException e) {
                    Log.e("UserListParser", e.getMessage());
                }
            }
        } catch (JSONException e) {
            Log.e("UserListParser", e.getMessage());
        }
        return users;
    }

    public static User parseSingleUser(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            Integer userId = obj.getInt("id");
            String login = obj.getString("login");
            String firstname = obj.getString("firstname");
            String lastname = obj.getString("lastname");

            // Optional fields
            String surname = null, message = null, picturePath = null;
            boolean away = true;
            if (obj.has("surname")) {
                surname = obj.getString("surname");
            }
            if (obj.has("message")) {
                message = obj.getString("message");
            }
            if (obj.has("away")) {
                away = (obj.getInt("away") == 1);
            }
            if (obj.has("picture_path")) {
                picturePath = Constants.BASE_URL + obj.getString("picture_path");
            }
            return new User(userId, login, firstname, lastname, surname, message, away, picturePath);
        } catch (JSONException e) {
            Log.e("UserListParser > parseSingleUser", e.getMessage());
        }
        return null;
    }
}
