package com.telnet.parsers;

import android.util.Log;

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
                Object oldMessage = obj.get("oldMessage");
                Date date = formatter.parse(obj.getString("date"));

                // Parse the pseudo
                User sender = UserParser.parseSingleUser(obj.getString("author"));

                // Add flashmail
                Flashmail fm = new Flashmail(id, sender, date, message);
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
