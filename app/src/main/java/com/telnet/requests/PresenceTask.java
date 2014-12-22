package com.telnet.requests;

import android.util.Log;

import java.io.IOException;

public class PresenceTask extends KaribouTask<String, Void, Void> {
    public PresenceTask() {

    }

    @Override
    protected Void doInBackground(String... params) {
        String url = params[0];
        try {
            Log.d("PresenceTask", url);

            doPost(url, null);
        } catch (IOException e) {
            Log.e("PresenceTask", e.getMessage());
        }
        return null;
    }
}
