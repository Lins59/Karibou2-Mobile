package com.telnet.karibou;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class ReadFlashmailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get read flashmail
        Intent i = getIntent();
        final String flashmailId = i.getStringExtra("id");

        PrioritizedStringRequest readFlashmailRequest = new PrioritizedStringRequest(Request.Method.POST, Constants.FLASHMAIL_READ_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("ReadFlashmailRequest", "Message marked as read");

                // TODO Rerun get flashmail
                finish();
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
                Log.e("ReadFlashmailRequest", "Error when marking message as read.");
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                // Send request to Karibou
                params.put("flashmailid", flashmailId);
                return params;
            }
        };
    }
}
