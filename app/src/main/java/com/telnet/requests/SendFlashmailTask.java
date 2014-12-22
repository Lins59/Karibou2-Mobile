package com.telnet.requests;

import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import com.telnet.karibou.SendFlashmailActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SendFlashmailTask extends KaribouTask<String, Void, Boolean> {
    private SendFlashmailActivity sfa;

    public SendFlashmailTask(SendFlashmailActivity a) {
        this.sfa = a;
    }

    protected Boolean doInBackground(String... params) {
        Boolean result = false;
        try {
            String url = params[0];
            String msg = params[1];
            String userId = params[2];
            String oldFlashmailId = params[3];
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("message", msg));
            nameValuePairs.add(new BasicNameValuePair("to_user_id", userId));
            if (oldFlashmailId != null) {
                nameValuePairs.add(new BasicNameValuePair("omsgid", oldFlashmailId));

                // Clear notification
                NotificationManager notificationManager = (NotificationManager) sfa.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(Integer.parseInt(oldFlashmailId));
            }

            Log.d("SendFlashmail", url);
            Log.d("SendFlashmail", msg);
            Log.d("SendFlashmail", userId);
            doPost(url, nameValuePairs);
            result = true;
        } catch (IOException e) {
            Log.e("PostTask", e.getMessage());
        }
        return result;
    }

    protected void onPostExecute(Boolean result) {
        if (result) {
            sfa.flashmailSent(result);
        }
    }
}