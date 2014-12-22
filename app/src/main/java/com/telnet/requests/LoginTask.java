package com.telnet.requests;

import android.util.Log;

import com.telnet.karibou.Constants;
import com.telnet.karibou.LoginActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LoginTask extends KaribouTask<String, Void, String> {
    public static LoginActivity loginActivity;

    public LoginTask(LoginActivity a, String login, String password) {
        super();
        super.setPantieId(generateId());
        super.setLogin(login);
        super.setPassword(password);

        loginActivity = a;
    }

    public static String generateId() {
        Date d = new Date();
        int id1 = (int) ((d.getTime() + Math.floor(Math.random() * 10000000)) % 100000000);
        int id2 = (int) ((d.getTime() + Math.floor(Math.random() * 10000000)) % 100000000);
        int id3 = (int) ((d.getTime() + Math.floor(Math.random() * 10000000)) % 100000000);
        int id4 = (int) ((d.getTime() + Math.floor(Math.random() * 10000000)) % 100000000);
        return Integer.toString(id1) + Integer.toString(id2) +
                Integer.toString(id3) + Integer.toString(id4);
    }

    @Override
    protected String doInBackground(String... params) {
        String result = "";

        Log.d("ConnectTask", super.getPantieId());

        try {
            // Get cookies
            LoginTask.doGet(Constants.HOME_URL);

            // Post login and password
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("_user", super.getLogin()));
            nameValuePairs.add(new BasicNameValuePair("_pass", super.getPassword()));
            doPost(Constants.LOGIN_URL, nameValuePairs);

            // Register to Pantie
            LoginTask.doGet(Constants.PANTIE_URL + "/?session=" + super.getPantieId() + "&event=mc2-*-message");

            // Get MC page to see if connection is ready
            result = LoginTask.doGet(Constants.MC_URL);
            Log.d("ConnectTask", result);
            Log.d("ConnectTask", super.getLogin());
            Log.d("ConnectTask", super.getPassword());
        } catch (IOException e) {
            Log.e("ConnectTask", e.getMessage());
        }
        return result;
    }

    protected void onPostExecute(String mcResult) {
        loginActivity.authenticationValid(mcResult);
    }
}
