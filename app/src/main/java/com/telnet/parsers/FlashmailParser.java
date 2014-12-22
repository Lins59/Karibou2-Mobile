package com.telnet.parsers;

import android.util.Log;

import com.telnet.karibou.ColorsFactory;
import com.telnet.objects.Flashmail;
import com.telnet.objects.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FlashmailParser {
    public static List<Flashmail> parse(String json) {
        List<Flashmail> flashmails = new ArrayList<Flashmail>();
        // Parse users
        JSONArray jsonArray;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            jsonArray = new JSONArray(json);
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                String id = obj.getString("id");
                String message = obj.getString("message");

                // Parse the pseudo
                JSONObject author = obj.getJSONObject("author");

                Date date = formatter.parse(obj.getString("date"));
                Integer userId = author.getInt("id");
                String pseudo;
                if (!author.isNull("surname")) {
                    pseudo = author.getString("surname");
                } else {
                    pseudo = (String) author.get("login");
                }

                // Get color
                String color = ColorsFactory.getColor(userId);

                // Get old message if exists
                Object oldMessage = obj.get("oldMessage");

                // Add flashmail
                Flashmail fm = new Flashmail(id, new User(userId, pseudo, color), date, message);
                if (oldMessage instanceof String) {
                    fm.setOldMessage((String) oldMessage);
                }
                flashmails.add(fm);
            }
        } catch (JSONException e) {
            Log.e("FlashmailParser", e.getMessage());
        } catch (ParseException e) {
            Log.e("FlashmailParser", e.getMessage());
        }
        return flashmails;
    }
}
