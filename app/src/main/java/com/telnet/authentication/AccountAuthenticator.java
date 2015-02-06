package com.telnet.authentication;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.telnet.karibou.Constants;

import java.util.Date;

/**
 * Created by Pierre Quételart on 12/01/2015.
 */
/*
 * Implement AbstractAccountAuthenticator and stub out all
 * of its methods
 */
public class AccountAuthenticator extends AbstractAccountAuthenticator {
    private static TELnetAuthentication telnetAuthentication = new TELnetAuthentication();
    private String TAG = "AccountAuthenticator";
    private Context context;

    public AccountAuthenticator(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options)
            throws NetworkErrorException {
        Log.d(TAG, " > addAccount");
        final Intent intent = new Intent(this.context, AuthenticatorActivity.class);
        intent.putExtra(Constants.ACCOUNT_TYPE, authTokenType);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        Log.d(TAG, " > getAuthToken");
        final AccountManager accountManager = AccountManager.get(context);

        // See if there is already an authentication token stored
        String authToken = accountManager.peekAuthToken(account, Constants.AUTHTOKEN_TYPE_FULL_ACCESS);

        // Look for validity
        boolean hasError = false;
        Date validityTime = null, currentTime = null;
        try {
            String timestamp = accountManager.getUserData(account, Constants.AUTHTOKEN_VALIDITY);
            validityTime = new Date(Long.parseLong(timestamp));
            currentTime = new Date();
        } catch (NumberFormatException e) {
            hasError = true;
        }
        // If we have no token, use the account credentials to fetch
        // a new one, effectively another logon
        // Cookie has expired
        if (hasError || validityTime == null || currentTime == null || validityTime.before(currentTime)) {
            Log.d(TAG, " > getAuthToken > Cookie has expired");
            accountManager.invalidateAuthToken(Constants.ACCOUNT_TYPE, authToken);

            Bundle authBundle = telnetAuthentication.signIn(context, account.name, accountManager.getUserData(account, AccountManager.KEY_PASSWORD));
            authToken = authBundle.getString(AccountManager.KEY_PASSWORD);
            accountManager.setUserData(account, Constants.AUTHTOKEN_VALIDITY, authBundle.getString(Constants.AUTHTOKEN_VALIDITY));
            accountManager.setUserData(account, Constants.AUTHTOKEN_PANTIE, authBundle.getString(Constants.AUTHTOKEN_PANTIE));

            accountManager.setAuthToken(account, Constants.AUTHTOKEN_TYPE_FULL_ACCESS, authToken);
        }


        // If we either got a cached token, or fetched a new one, hand
        // it back to the client that called us.
        if (!TextUtils.isEmpty(authToken)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            result.putString(Constants.AUTHTOKEN_PANTIE, accountManager.getUserData(account, Constants.AUTHTOKEN_PANTIE));
            return result;
        }

        // If we get here, then we don't have a token, and we don't have
        // a password that will let us get a new one (or we weren't able
        // to use the password we do have).  We need to fetch
        // information from the user, we do that by creating an Intent
        // to an Activity child class.
        final Intent intent = new Intent(context, AuthenticatorActivity.class);

        // We want to give the Activity the information we want it to
        // return to the AccountManager.  We'll cover that with the
        // KEY_ACCOUNT_AUTHENTICATOR_RESPONSE parameter.
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE,
                response);
        // We'll also give it the parameters we've already looked up, or
        // were given.
        /*intent.putExtra(LoginActivity.ARG_IS_ADDING_NEW_ACCOUNT, false);
        intent.putExtra(LoginActivity.ARG_ACCOUNT_NAME, account.name);
        intent.putExtra(LoginActivity.ARG_ACCOUNT_TYPE, account.type);
        intent.putExtra(LoginActivity.ARG_AUTH_TYPE, authTokenType);*/

        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return Constants.AUTHTOKEN_TYPE_FULL_ACCESS_LABEL;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        final Bundle result = new Bundle();
        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
        return result;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        return null;
    }
}
