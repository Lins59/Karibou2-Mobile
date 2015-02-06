package com.telnet.authentication;

import android.accounts.AccountManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.telnet.karibou.Constants;
import com.telnet.karibou.HttpToolbox;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by Pierre Quételart on 06/02/2015.
 */
public class TELnetAuthentication implements IAuthentication {
    private String TAG = "TELnetAuthentication";
    private AuthenticationState state = AuthenticationState.NOT_STARTED;

    @Override
    public Bundle signIn(final Context context, final String login, final String password) {
        // Generate pantie id and save it for later use
        HttpToolbox.setPantieId(generateId());

        // Get RequestQueue
        final HttpToolbox httpToolbox = HttpToolbox.getInstance(context);

        // Generate error listener
        final Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        };

        try {
            // Cookies
            RequestFuture<String> future = RequestFuture.newFuture();
            StringRequest cookiesRequest = new StringRequest(Request.Method.GET, Constants.HOME_URL, future, future);
            httpToolbox.addToRequestQueue(cookiesRequest, "LOGIN");
            String cookiesResponse = future.get();
            setAuthenticationState(AuthenticationState.COOKIES);
            Log.d(TAG, getAuthenticationState() + " > " + cookiesResponse);

            // Login
            StringRequest loginRequest = new StringRequest(Request.Method.POST, Constants.LOGIN_URL, future, future) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("_user", login);
                    params.put("_pass", password);
                    return params;
                }
            };
            httpToolbox.addToRequestQueue(loginRequest, "LOGIN");
            String loginResponse = future.get();
            setAuthenticationState(AuthenticationState.LOGIN);
            Log.d(TAG, getAuthenticationState() + " > " + loginResponse);

            // Minichat
            StringRequest minichatRequest = new StringRequest(Request.Method.GET, Constants.MC_URL, future, future);
            httpToolbox.addToRequestQueue(minichatRequest, "LOGIN");
            String minichatResponse = future.get();
            setAuthenticationState(AuthenticationState.MINICHAT);
            Log.d(TAG, getAuthenticationState() + " > " + minichatResponse);

            // Register to Pantie
            StringRequest pantieRequest = new StringRequest(Request.Method.GET, Constants.PANTIE_URL + "/?session=" + HttpToolbox.getPantieId() + "&event=mc2-*-message", future, future);
            httpToolbox.addToRequestQueue(pantieRequest, "LOGIN");
            String pantieResponse = future.get();
            setAuthenticationState(AuthenticationState.PANTIE);
            Log.d(TAG, getAuthenticationState() + " > " + pantieResponse);

            // Trigger authentication valid
            Date targetTime = new Date(new Date().getTime() + (Constants.AUTHTOKEN_VALIDITY_DURATION * 60000));
            String authTokenValidity = String.valueOf(targetTime.getTime());
            Bundle data = new Bundle();
            data.putString(AccountManager.KEY_ACCOUNT_TYPE, Constants.ACCOUNT_TYPE);
            data.putString(Constants.AUTHTOKEN_VALIDITY, authTokenValidity);
            data.putString(Constants.AUTHTOKEN_PANTIE, HttpToolbox.getPantieId());

            // Get PHP Session Id from Cookie Store
            String authToken = "";
            CookieStore cookieStore = HttpToolbox.getInstance(context).getCookieStore();
            List<Cookie> cookiesList = cookieStore.getCookies();
            for (Cookie cookie : cookiesList) {
                if (Constants.COOKIE_KEY.equals(cookie.getName())) {
                    Log.d(TAG, "Cookie > " + cookie.getName() + "=" + cookie.getValue());
                    authToken = cookie.getValue();
                }
            }
            data.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            data.putString(AccountManager.KEY_ACCOUNT_NAME, login);
            data.putString(AccountManager.KEY_PASSWORD, password);
            return data;
        } catch (InterruptedException e) {
            Log.e(TAG, "Error in authentication. Current state: " + getAuthenticationState());
            Log.e(TAG, e.getClass().getCanonicalName());
            if (e != null && e.getMessage() != null) {
                Log.e(TAG, e.getMessage());
            }
            // exception handling
        } catch (ExecutionException e) {
            Log.e(TAG, "Error in authentication. Current state: " + getAuthenticationState());
            Log.e(TAG, e.getClass().getCanonicalName());
            if (e != null && e.getMessage() != null) {
                Log.e(TAG, e.getMessage());
            }
        }
        return new Bundle();
    }

    public AuthenticationState getAuthenticationState() {
        return state;
    }

    public void setAuthenticationState(AuthenticationState state) {
        Log.i(TAG, "> setAuthenticationState > Changing authentication state from " + this.state + " to " + state);
        this.state = state;
    }

    public String generateId() {
        Date d = new Date();
        int id1 = (int) ((d.getTime() + Math.floor(Math.random() * 10000000)) % 100000000);
        int id2 = (int) ((d.getTime() + Math.floor(Math.random() * 10000000)) % 100000000);
        int id3 = (int) ((d.getTime() + Math.floor(Math.random() * 10000000)) % 100000000);
        int id4 = (int) ((d.getTime() + Math.floor(Math.random() * 10000000)) % 100000000);
        return Integer.toString(id1) + Integer.toString(id2) +
                Integer.toString(id3) + Integer.toString(id4);
    }

    private enum AuthenticationState {
        NOT_STARTED, COOKIES, LOGIN, MINICHAT, PANTIE;
    }
}
