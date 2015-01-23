package com.telnet.karibou;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by a551894 on 09/01/2015.
 */
public class PrioritizedStringRequest extends StringRequest {
    private Priority mPriority = Priority.LOW;

    public PrioritizedStringRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public PrioritizedStringRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
    }

    @Override
    public Priority getPriority() {
        return mPriority;
    }

    public void setPriority(Priority priority) {
        mPriority = priority;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<String, String>(super.getHeaders());
        headers.put("User-agent", "Lin's Droid");
        if (Method.POST == super.getMethod()) {
            headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        }

        return headers;
    }
}
