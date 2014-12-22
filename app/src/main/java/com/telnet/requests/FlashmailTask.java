package com.telnet.requests;

import android.util.Log;

import com.telnet.karibou.FlashmailFragment;

import java.io.IOException;

public class FlashmailTask extends KaribouTask<String, Void, String> {

    private FlashmailFragment flashmailFragment;

    public FlashmailTask(FlashmailFragment a) {
        this.flashmailFragment = a;
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
        flashmailFragment.setFlashmails(content);
    }
}
