package com.telnet.authentication;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
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
import com.telnet.karibou.Constants;
import com.telnet.karibou.HttpToolbox;
import com.telnet.karibou.PrioritizedStringRequest;
import com.telnet.karibou.R;
import com.telnet.sync.FlashmailsContract;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Pierre Quételart on 12/01/2015.
 */
public class AuthenticatorActivity extends AccountAuthenticatorActivity {
    // View
    private static ProgressBar progressBar;
    private String TAG = "AuthenticatorActivity";
    private AuthenticationState state = AuthenticationState.NOT_STARTED;
    private AccountManager accountManager;
    private Button button;
    private EditText login, pwd;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        // Create account manager
        accountManager = AccountManager.get(getBaseContext());

        // Create view
        setContentView(R.layout.activity_login);
        this.button = (Button) findViewById(R.id.button);
        this.login = (EditText) findViewById(R.id.login);
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        this.progressBar.setVisibility(View.INVISIBLE);
        this.pwd = (EditText) findViewById(R.id.pwd);

        // Listeners
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    public void submit() {
        Log.d(TAG, " > submit");
        final String login = ((EditText) findViewById(R.id.login)).getText().toString();
        final String password = ((EditText) findViewById(R.id.pwd)).getText().toString();

        toggleView(true);

        // Generate pantie id and save it for later use
        HttpToolbox.setPantieId(generateId());

        // Get RequestQueue
        final HttpToolbox httpToolbox = HttpToolbox.getInstance(getApplicationContext());

        // Generate error listener
        final Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "Error in authentication. Current state: " + getAuthenticationState());
                Log.e(TAG, volleyError.getClass().getCanonicalName());
                if (volleyError != null && volleyError.getMessage() != null) {
                    Log.e(TAG, volleyError.getMessage());
                }
            }
        };

        PrioritizedStringRequest cookiesRequest = new PrioritizedStringRequest(Request.Method.GET, Constants.HOME_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                setAuthenticationState(AuthenticationState.COOKIES);
                Log.d(TAG, getAuthenticationState() + " > " + response);
                // If the cookie request is successful, issue a POST to login
                PrioritizedStringRequest loginRequest = new PrioritizedStringRequest(Request.Method.POST, Constants.LOGIN_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        setAuthenticationState(AuthenticationState.LOGIN);
                        Log.d(TAG, getAuthenticationState() + " > " + response);
                        // Get MC page to see if connection is ready
                        PrioritizedStringRequest minichatRequest = new PrioritizedStringRequest(Request.Method.GET, Constants.MC_URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(final String minichatResponse) {
                                setAuthenticationState(AuthenticationState.MINICHAT);
                                Log.d(TAG, getAuthenticationState() + " > " + minichatResponse);
                                // When the login is successful, register to Pantie
                                PrioritizedStringRequest pantieRequest = new PrioritizedStringRequest(Request.Method.GET, Constants.PANTIE_URL + "/?session=" + HttpToolbox.getPantieId() + "&event=mc2-*-message", new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        setAuthenticationState(AuthenticationState.PANTIE);
                                        Log.d(TAG, getAuthenticationState() + " > " + response);

                                        // Trigger authentication valid
                                        Date targetTime = new Date(new Date().getTime() + (Constants.AUTHTOKEN_VALIDITY_DURATION * 60000));
                                        String authTokenValidity = String.valueOf(targetTime.getTime());
                                        Bundle data = new Bundle();
                                        data.putString(Constants.AUTHTOKEN_VALIDITY, authTokenValidity);
                                        data.putString(Constants.AUTHTOKEN_PANTIE, HttpToolbox.getPantieId());

                                        // Get PHP Session Id from Cookie Store
                                        String authToken = "";
                                        CookieStore cookieStore = HttpToolbox.getInstance(getApplicationContext()).getCookieStore();
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
                                        Intent res = new Intent();
                                        res.putExtras(data);
                                        finishLogin(res);
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

    public void finishLogin(Intent intent) {
        Log.d(TAG, " > finishLogin");
        String login = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String password = intent.getStringExtra(AccountManager.KEY_PASSWORD);
        String authToken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
        String authTokenValidity = intent.getStringExtra(Constants.AUTHTOKEN_VALIDITY);
        String authTokenPantie = intent.getStringExtra(Constants.AUTHTOKEN_PANTIE);

        Bundle userData = new Bundle();
        userData.putString(Constants.AUTHTOKEN_VALIDITY, authTokenValidity);
        userData.putString(Constants.AUTHTOKEN_PANTIE, authTokenPantie);

        // Creating the account on the device and setting the auth token we got
        // (Not setting the auth token will cause another call to the server to authenticate the user)
        final Account account = new Account(login, Constants.ACCOUNT_TYPE);
        accountManager.addAccountExplicitly(account, password, null);
        accountManager.setAuthToken(account, Constants.AUTHTOKEN_TYPE_FULL_ACCESS, authToken);

        // Set Syncable
        ContentResolver.setIsSyncable(account, FlashmailsContract.AUTHORITY, 1);
        ContentResolver.setSyncAutomatically(account, FlashmailsContract.AUTHORITY, true);

        this.setResult(RESULT_OK, intent);
        this.finish();
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