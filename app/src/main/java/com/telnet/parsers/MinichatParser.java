package com.telnet.parsers;

import com.telnet.karibou.ColorFactory;
import com.telnet.objects.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class MinichatParser {
    public static ArrayList<Message> parse(String result) throws JSONException {
        ArrayList<Message> results = new ArrayList<Message>();

        JSONArray jsonArray;
        jsonArray = new JSONArray(result);
        int len = jsonArray.length();
        for (int i = 0; i < len; i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            // Don't process messages like score
            if (obj.isNull("type") || !"msg".equals(obj.getString("type"))) {
                continue;
            }
            String message = (String) obj.get("post");
            Integer userId = obj.getInt("user_id");

            Date date = new Date(obj.getLong("time"));

            // Parse the pseudo
            String pseudo;
            if (!obj.isNull("surname")) {
                pseudo = obj.getString("surname");
            } else if (!obj.isNull("login")) {
                pseudo = (String) obj.get("login");
            } else {
                pseudo = "Inconnu";
            }

            // Get color
            String color = ColorFactory.getColor(userId);

            // Mix up
            results.add(new Message(pseudo, message, date, color));
        }
        return results;
    }
}
