package com.telnet.karibou;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.telnet.requests.SendFlashmailTask;

public class SendFlashmailActivity extends ActionBarActivity {
    public static String SEND_FM_URL = "http://karibou2.telecom-lille.fr/flashmail/send/";
    private TextView info, pseudoView;
    private EditText msg;
    private Button send, cancel;
    private String flashmailId;
    private String userId;
    private SendFlashmailActivity sfma = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_flashmail);

        this.info = (TextView) findViewById(R.id.info);
        this.pseudoView = (TextView) findViewById(R.id.pseudo);
        this.msg = (EditText) findViewById(R.id.messageField);
        this.send = (Button) findViewById(R.id.send);
        this.cancel = (Button) findViewById(R.id.cancel);

        // Get params from UserList (if exists)
        Intent i = getIntent();
        flashmailId = i.getStringExtra("id");
        pseudoView.setText(i.getStringExtra("pseudo"));
        userId = i.getStringExtra("userId");
        boolean answer = i.getBooleanExtra("answer", false);

        if (answer) {
            info.setText("Répondre à");
        } else {
            info.setText("Envoyer à");
        }

        // Send a message listener
        this.send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new SendFlashmailTask(sfma).execute(SEND_FM_URL, msg.getText().toString(), userId, flashmailId);
            }
        });

        this.cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sfma.finish();
            }
        });
    }

    public void flashmailSent(boolean result) {
        if (result) {
            Toast.makeText(sfma, "Flashmail envoyé", Toast.LENGTH_LONG).show();
            Intent returnIntent = new Intent();
            returnIntent.putExtra("id", flashmailId);
            setResult(RESULT_OK, returnIntent);
            finish();
        }
    }
}
