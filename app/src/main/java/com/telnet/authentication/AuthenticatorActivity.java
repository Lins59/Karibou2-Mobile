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

import com.telnet.karibou.Constants;
import com.telnet.karibou.R;
import com.telnet.sync.FlashmailsContract;

/**
 * Created by Pierre Quételart on 12/01/2015.
 */
public class AuthenticatorActivity extends AccountAuthenticatorActivity {
    private static ProgressBar progressBar;
    private static TELnetAuthentication telnetAuthentication = new TELnetAuthentication();
    private String TAG = "AuthenticatorActivity";
    private AccountManager accountManager;
    private Button button;
    private EditText login, pwd;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        // Create account manager
        accountManager = AccountManager.get(getApplicationContext());

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
        toggleView(true);

        final String login = ((EditText) findViewById(R.id.login)).getText().toString();
        final String password = ((EditText) findViewById(R.id.pwd)).getText().toString();

        Bundle authBundle = telnetAuthentication.signIn(getApplicationContext(), login, password);
        Intent authIntent = new Intent();
        authIntent.putExtras(authBundle);
        finishLogin(authIntent);
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
        accountManager.addAccountExplicitly(account, password, userData);

        accountManager.setAuthToken(account, Constants.AUTHTOKEN_TYPE_FULL_ACCESS, authToken);

        // Set Syncable
        ContentResolver.setIsSyncable(account, FlashmailsContract.AUTHORITY, 1);
        ContentResolver.setSyncAutomatically(account, FlashmailsContract.AUTHORITY, true);

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
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
}