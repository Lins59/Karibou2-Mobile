package com.telnet.karibou;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.io.IOException;

public class LoginActivity extends ActionBarActivity {
    private static int ACCOUNT_CHOOSER_ACTIVITY = 1;
    private static ProgressBar progressBar;
    private String TAG = "LoginActivity";
    private AccountManager accountManager;
    private Account account;
    private String authToken;
    private Button button;
    private EditText login, pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.button = (Button) findViewById(R.id.button);
        this.login = (EditText) findViewById(R.id.login);
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        this.progressBar.setVisibility(View.INVISIBLE);
        this.pwd = (EditText) findViewById(R.id.pwd);

        toggleView(true);

        // Call authenticator
        accountManager = AccountManager.get(getApplicationContext());

        // Select good account or add one
        Account[] acc = accountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
        if (acc.length == 0) {
            Log.e(TAG, "No accounts of type " + Constants.ACCOUNT_TYPE + " found");
            Log.d(TAG, "Adding account of type " + Constants.ACCOUNT_TYPE + " found");
            accountManager.addAccount(
                    Constants.ACCOUNT_TYPE,
                    Constants.AUTHTOKEN_TYPE_FULL_ACCESS,
                    null,
                    new Bundle(),
                    this,
                    new OnAccountAddComplete(),
                    null);
        } else {
            Log.i(TAG, "Found " + acc.length + " accounts of type " + Constants.ACCOUNT_TYPE);

            Intent intent = AccountManager.newChooseAccountIntent(
                    null,
                    null,
                    new String[]{Constants.ACCOUNT_TYPE},
                    false,
                    null,
                    Constants.AUTHTOKEN_TYPE_FULL_ACCESS,
                    null,
                    null);

            startActivityForResult(intent, ACCOUNT_CHOOSER_ACTIVITY);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, " > onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, " > onPause");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, " > onActivityResult");
        if (resultCode == RESULT_CANCELED)
            return;
        if (requestCode == ACCOUNT_CHOOSER_ACTIVITY) {
            Bundle bundle = data.getExtras();
            this.account = new Account(
                    bundle.getString(AccountManager.KEY_ACCOUNT_NAME),
                    bundle.getString(AccountManager.KEY_ACCOUNT_TYPE)
            );
            Log.d(TAG, "Selected account " + account.name + ", fetching");
            startAuthTokenFetch();
        }
    }

    private void startAuthTokenFetch() {
        Log.d(TAG, " > startAuthTokenFetch");
        Bundle options = new Bundle();
        accountManager.getAuthToken(
                account,
                Constants.ACCOUNT_TYPE,
                options,
                true,
                new OnAccountManagerComplete(),
                new Handler()
        );
    }


    private class OnAccountManagerComplete implements AccountManagerCallback<Bundle> {
        @Override
        public void run(AccountManagerFuture<Bundle> result) {
            Log.d(TAG, " > onAccountManagerComplete");
            Bundle bundle;
            try {
                bundle = result.getResult();
            } catch (OperationCanceledException e) {
                e.printStackTrace();
                return;
            } catch (AuthenticatorException e) {
                e.printStackTrace();
                return;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            authToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
            Log.d(TAG, "Received authentication token " + authToken);

            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private class OnAccountAddComplete implements AccountManagerCallback<Bundle> {
        @Override
        public void run(AccountManagerFuture<Bundle> result) {
            Log.d(TAG, " > onAccountAddComplete");
            Bundle bundle;
            try {
                bundle = result.getResult();
            } catch (OperationCanceledException e) {
                e.printStackTrace();
                return;
            } catch (AuthenticatorException e) {
                e.printStackTrace();
                return;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            account = new Account(
                    bundle.getString(AccountManager.KEY_ACCOUNT_NAME),
                    bundle.getString(AccountManager.KEY_ACCOUNT_TYPE)
            );
            Log.d(TAG, "Added account " + account.name + ", fetching");
            startAuthTokenFetch();
        }
    }
}
