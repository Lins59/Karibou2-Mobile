package com.telnet.parsers;

import android.util.Log;

import com.telnet.karibou.ColorFactory;
import com.telnet.karibou.Constants;
import com.telnet.objects.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserListParser {
    public static List<User> parse(String json) {
        // Parse users
        List<User> users = new ArrayList<User>();
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String message = obj.getString("message");

                Integer userId = obj.getInt("user_id");

                // Parse the pseudo
                String pseudo;
                if (!obj.isNull("surname")) {
                    pseudo = obj.getString("surname");
                } else {
                    pseudo = (String) obj.get("login");
                }

                // Get color
                String color = ColorFactory.getColor(userId);

                users.add(new User(userId, pseudo, color, message, (obj.getInt("away") == 1), Constants.BASE_URL + obj.getString("picture_path")));
            }
        } catch (JSONException e) {
            Log.e("UserListParser", e.getMessage());
        }
        return users;
    }
}
