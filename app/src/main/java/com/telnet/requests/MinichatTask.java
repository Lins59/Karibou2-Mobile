package com.telnet.requests;

import android.util.Log;

import com.telnet.karibou.MinichatFragment;

import java.io.IOException;

public class MinichatTask extends KaribouTask<String, Void, String> {

    private MinichatFragment minichatFragment;

    public MinichatTask(MinichatFragment a) {
        this.minichatFragment = a;
    }

    @Override
    protected String doInBackground(String... params) {
        String result = "";
        String url = params[0];
        try {
            Log.d("FlashmailTask", url);

            result = doGet(url);
        } catch (IOException e) {
            Log.e("FlashmailTask", e.getMessage());
        }
        return result;
    }

    protected void onPostExecute(String content) {
        minichatFragment.setMessages(content);
    }
}