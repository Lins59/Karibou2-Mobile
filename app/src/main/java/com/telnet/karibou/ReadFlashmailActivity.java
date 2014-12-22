package com.telnet.karibou;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.telnet.requests.ReadFlashmailTask;

public class ReadFlashmailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get read flashmail
        Intent i = getIntent();
        String flashmailId = i.getStringExtra("id");
        ReadFlashmailTask readFlashmailTask = new ReadFlashmailTask(this);
        readFlashmailTask.execute(Constants.FLASHMAIL_READ_URL, flashmailId);
        finish();
    }
}
