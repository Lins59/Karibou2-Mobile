package com.telnet.karibou;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;

public class SendFlashmailActivity extends ActionBarActivity {
    private HttpToolbox httpToolbox;
    private TextView info, pseudoView;
    private EditText msg;
    private Button send, cancel;
    private String flashmailId;
    private String userId;
    private SendFlashmailActivity sfma = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create http toolbox
        httpToolbox = HttpToolbox.getInstance(getApplicationContext());

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
                sendFlashmail(Constants.FLASHMAIL_SEND, msg.getText().toString(), userId, flashmailId);
            }
        });

        this.cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sfma.finish();
            }
        });
    }

    public void sendFlashmail(final String url, final String message, final String userId, final String oldFlashmailId) {
        PrioritizedStringRequest sendFlashmailRequest = new PrioritizedStringRequest(Request.Method.POST, Constants.FLASHMAIL_SEND, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("SendFlashmailRequest", "Flashmail sent");
                endActivity();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError instanceof TimeoutError || volleyError instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), "Timeout",
                            Toast.LENGTH_LONG).show();
                } else if (volleyError instanceof AuthFailureError) {
                    //TODO
                } else if (volleyError instanceof ServerError) {
                    //TODO
                } else if (volleyError instanceof NetworkError) {
                    //TODO
                } else if (volleyError instanceof ParseError) {
                    //TODO
                }
                Log.e("SendFlashmailRequest", "Error when sending flashmail.");
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("message", message);
                params.put("to_user_id", userId);
                if (oldFlashmailId != null) {
                    params.put("omsgid", oldFlashmailId);
                }
                return params;
            }
        };
        sendFlashmailRequest.setPriority(Request.Priority.IMMEDIATE);
        httpToolbox.addToRequestQueue(sendFlashmailRequest, "FM");
    }

    public void endActivity() {
        Toast.makeText(sfma, "Flashmail envoyé", Toast.LENGTH_LONG).show();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("id", flashmailId);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
