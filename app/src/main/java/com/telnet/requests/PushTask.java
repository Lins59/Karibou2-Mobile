package com.telnet.requests;

import android.util.Log;

import com.telnet.karibou.MinichatFragment;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PushTask extends KaribouTask<String, Void, String> {
    private MinichatFragment mA;
    private String url = "";

    public PushTask(MinichatFragment a) {
        this.mA = a;
    }

    @Override
    protected String doInBackground(String... params) {
        if (!isCancelled()) {
            String result = "";
            this.url = params[0];
            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("session", super.getPantieId()));

                Log.d("PushTask", url);
                result = doPost(url, nameValuePairs);
            } catch (IOException e) {
                Log.e("PushTask", e.getMessage());
            }
            return result;
        }
        return null;
    }

    protected void onPostExecute(String json) {
        if (!isCancelled()) {
            // Append messages
            mA.appendMessagesFromPantie(json);
            // Relaunch a new task
            PushTask newPushTask = new PushTask(mA);
            mA.setPushTask(newPushTask);
            newPushTask.execute(url);
        }
    }
}
