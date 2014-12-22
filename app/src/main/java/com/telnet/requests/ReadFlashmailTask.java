package com.telnet.requests;

import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import com.telnet.karibou.FlashmailFragment;
import com.telnet.karibou.ReadFlashmailActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReadFlashmailTask extends KaribouTask<String, Void, String> {

    private FlashmailFragment flashmailFragment;
    private ReadFlashmailActivity readFlashmailActivity;

    public ReadFlashmailTask(ReadFlashmailActivity a) {
        this.readFlashmailActivity = a;
    }

    public ReadFlashmailTask(FlashmailFragment a) {
        this.flashmailFragment = a;
    }

    @Override
    protected String doInBackground(String... params) {
        String result = "";
        String url = params[0];
        String id = params[1];
        try {
            Log.d("ReadFlashmailTask", url);

            // Remove notification
            NotificationManager notificationManager = null;
            if (flashmailFragment != null) {
                notificationManager = (NotificationManager) flashmailFragment.getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            }
            if (readFlashmailActivity != null) {
                notificationManager = (NotificationManager) readFlashmailActivity.getSystemService(Context.NOTIFICATION_SERVICE);
            }
            notificationManager.cancel(Integer.parseInt(id));

            // Send request to Karibou
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("flashmailid", id));

            doPost(url, nameValuePairs);
        } catch (IOException e) {
            Log.e("ReadFlashmailTask", e.getMessage());
        }
        return result;
    }

    protected void onPostExecute() {
        // Relaunch GET Flashmail activity
        if (flashmailFragment != null) {
            flashmailFragment.getFlashmailTask().run();
        }
    }
}

