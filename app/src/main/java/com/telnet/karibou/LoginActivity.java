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

import com.telnet.requests.LoginTask;

import org.json.JSONArray;
import org.json.JSONException;

public class LoginActivity extends ActionBarActivity {
    private static ProgressBar progressBar;
    public String loginText = "", passwordText = "";
    SharedPreferences prefs;
    private Button button;
    private EditText login, pwd;
    private LoginActivity l = this;

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
                new LoginTask(l, loginText, passwordText).execute();
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
                new LoginTask(l, loginText, passwordText).execute();
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
            alertBuilder.setPositiveButton("OK",
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
}
