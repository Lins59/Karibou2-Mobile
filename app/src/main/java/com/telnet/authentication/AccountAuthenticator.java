package com.telnet.authentication;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

        // Ask AccountManager for the auth-token
        String authToken = accountManager.peekAuthToken(account, authTokenType);

        // Look for validity
        Date validityTime = new Date(accountManager.getUserData(account, Constants.AUTHTOKEN_VALIDITY));
        Date currentTime = new Date();

        // Cookie has expired
        if (validityTime.compareTo(currentTime) > 0) {
            Log.d(TAG, " > getAuthToken > Cookie has expired");
            accountManager.invalidateAuthToken(Constants.ACCOUNT_TYPE, authToken);
        } else {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            result.putString(Constants.AUTHTOKEN_PANTIE, accountManager.getUserData(account, Constants.AUTHTOKEN_PANTIE));
            return result;
        }

        final Bundle bundle = new Bundle();
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
