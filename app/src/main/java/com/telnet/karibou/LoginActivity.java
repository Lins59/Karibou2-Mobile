package com.telnet.karibou;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends ActionBarActivity {
    private static ProgressBar progressBar;
    public String loginText = "", passwordText = "";
    SharedPreferences prefs;
    private Button button;
    private EditText login, pwd;
    private LoginActivity l = this;
    private AuthenticationState state = AuthenticationState.NOT_STARTED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.button = (Button) findViewById(R.id.button);
        this.login = (EditText) findViewById(R.id.login);
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        this.progressBar.setVisibility(View.INVISIBLE);
        this.pwd = (EditText) findViewById(R.id.pwd);

        this.button.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("deprecation")
            public void onClick(View v) {
                loginText = login.getText().toString();
                passwordText = pwd.getText().toString();

                toggleView(true);
                triggerAuthentication(loginText, passwordText);
            }
        });
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getString("login", null) != null) {
            this.login.setText(prefs.getString("login", null));
        }

        // Check connectivity
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            // Get login and password from prefs
            if (prefs.getString("login", null) != null && prefs.getString("password", null) != null) {
                toggleView(true);
                loginText = prefs.getString("login", null);
                passwordText = prefs.getString("password", null);
                triggerAuthentication(loginText, passwordText);
            }
        } else {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setMessage(R.string.no_connection);
            alertBuilder.setCancelable(false);
            alertBuilder.setPositiveButton(R.string.taking_risk,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert = alertBuilder.create();
            alert.show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("LoginActivity", "onResume called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("LoginActivity", "onPause called");
    }

    public void toggleView(Boolean duringLogin) {
        if (duringLogin) {
            // Make progress bar visible
            progressBar.setVisibility(View.VISIBLE);

            // Clear all unused elements
            button.setVisibility(View.GONE);
            login.setVisibility(View.INVISIBLE);
            pwd.setVisibility(View.INVISIBLE);
        } else {
            // Make progress bar invisible
            progressBar.setVisibility(View.INVISIBLE);

            // Make all other elements visible
            button.setVisibility(View.VISIBLE);
            login.setVisibility(View.VISIBLE);
            pwd.setVisibility(View.VISIBLE);
        }
    }

    public AuthenticationState getAuthenticationState() {
        return state;
    }

    public void setAuthenticationState(AuthenticationState state) {
        Log.i("LoginTask", "Changing authentication state from " + this.state + " to " + state);
        this.state = state;
    }

    public void triggerAuthentication(final String login, final String password) {
        // Generate pantie id and save it for later use
        HttpToolbox.setPantieId(generateId());

        // Get RequestQueue
        final HttpToolbox httpToolbox = HttpToolbox.getInstance(getApplicationContext());

        // Generate error listener
        final Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("LoginTask", "Error in authentication. Current state: " + getAuthenticationState());
                Log.e("LoginTask", volleyError.getMessage());
            }
        };

        PrioritizedStringRequest cookiesRequest = new PrioritizedStringRequest(Request.Method.GET, Constants.HOME_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                setAuthenticationState(AuthenticationState.COOKIES);
                Log.d("LoginTask", response);
                // If the cookie request is successful, issue a POST to login
                PrioritizedStringRequest loginRequest = new PrioritizedStringRequest(Request.Method.POST, Constants.LOGIN_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        setAuthenticationState(AuthenticationState.LOGIN);
                        Log.d("LoginTask", response);
                        // Get MC page to see if connection is ready
                        PrioritizedStringRequest minichatRequest = new PrioritizedStringRequest(Request.Method.GET, Constants.MC_URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(final String minichatResponse) {
                                setAuthenticationState(AuthenticationState.MINICHAT);
                                Log.d("LoginTask", minichatResponse);
                                // When the login is successful, register to Pantie
                                Log.d("Size", "Size:" + httpToolbox.getCookieManager().getCookieStore().getCookies().size());
                                PrioritizedStringRequest pantieRequest = new PrioritizedStringRequest(Request.Method.GET, Constants.PANTIE_URL + "/?session=" + HttpToolbox.getPantieId() + "&event=mc2-*-message", new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        setAuthenticationState(AuthenticationState.PANTIE);
                                        Log.d("LoginTask", response);

                                        // Trigger authentication valid
                                        authenticationValid(minichatResponse);
                                        Log.d("ConnectTask", response);
                                        Log.i("LoginTask", "End of LoginTask");
                                    }
                                }, errorListener);
                                pantieRequest.setPriority(Request.Priority.IMMEDIATE);
                                httpToolbox.addToRequestQueue(pantieRequest, "LOGIN");


                            }
                        }, errorListener);
                        minichatRequest.setPriority(Request.Priority.IMMEDIATE);
                        httpToolbox.addToRequestQueue(minichatRequest, "LOGIN");
                    }
                }, errorListener) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("_user", login);
                        params.put("_pass", password);
                        return params;
                    }
                };
                loginRequest.setRetryPolicy(new DefaultRetryPolicy(30000, 0, 0));
                loginRequest.setPriority(Request.Priority.IMMEDIATE);
                httpToolbox.addToRequestQueue(loginRequest, "LOGIN");
            }
        }, errorListener);
        cookiesRequest.setPriority(Request.Priority.IMMEDIATE);
        httpToolbox.addToRequestQueue(cookiesRequest, "LOGIN");
    }

    public void authenticationValid(String mcResult) {
        // Login is successful if result is JSONArray
        try {
            new JSONArray(mcResult);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            prefs.edit().putString("login", loginText).apply();
            prefs.edit().putString("password", passwordText).apply();

            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
            finish();
        } catch (JSONException e) {
            login.setText(prefs.getString("login", ""));
            pwd.setText("");
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setMessage(R.string.wrong_password);
            alertBuilder.setCancelable(false);
            alertBuilder.setPositiveButton(R.string.ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert = alertBuilder.create();
            alert.show();
            toggleView(false);
        }
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
