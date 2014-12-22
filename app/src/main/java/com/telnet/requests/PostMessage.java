package com.telnet.requests;

import android.util.Log;

import com.telnet.karibou.MinichatFragment;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PostMessage extends KaribouTask<String, Void, Boolean> {
    private MinichatFragment mA;

    public PostMessage(MinichatFragment a) {
        this.mA = a;
    }

    protected void onPreExecute() {
        // TODO NOTIF
    }

    protected Boolean doInBackground(String... params) {
        Boolean result = false;
        try {
            String url = params[0];
            String msg = params[1];
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("msg", msg));

            Log.d("PostTask", url);
            Log.d("PostTask", msg);
            doPost(url, nameValuePairs);
            result = true;
        } catch (IOException e) {
            Log.e("PostTask", e.getMessage());
        }
        return result;
    }

    protected void onPostExecute(Boolean result) {
        if (result) {
            mA.clearForm();
        }
    }
}