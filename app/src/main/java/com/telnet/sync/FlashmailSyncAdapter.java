package com.telnet.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.telnet.karibou.Constants;
import com.telnet.karibou.HttpToolbox;
import com.telnet.karibou.PrioritizedStringRequest;
import com.telnet.objects.Flashmail;
import com.telnet.parsers.FlashmailParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Pierre Quételart on 12/01/2015.
 */
public class FlashmailSyncAdapter extends AbstractThreadedSyncAdapter {
    private final AccountManager mAccountManager;
    private String TAG = "FlashmailSyncAdapter";

    public FlashmailSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mAccountManager = AccountManager.get(context);
    }

    @Override
    public void onPerformSync(final Account account, final Bundle extras, final String authority, final ContentProviderClient provider, final SyncResult syncResult) {
        Log.d(TAG, " > onPerformSync");
        try {
            // Get the auth token for the current account
            final String authToken = mAccountManager.blockingGetAuthToken(account, Constants.AUTHTOKEN_TYPE_FULL_ACCESS, true);
            Log.d(TAG, "We have retrieved this authToken: " + authToken);

            StringRequest flashmailsRequest = new PrioritizedStringRequest(Request.Method.GET, Constants.FLASHMAIL_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String flashmails) {
                    onRequestComplete(account, extras, authority, provider, syncResult, flashmails);
                    Log.i("FlashmailFragment", "End of polling flashmails");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.e("FlashmailFragment", "Error in polling");
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>(super.getHeaders());
                    headers.put("User-agent", "Lin's Droid Flashmail synchronizer");
                    headers.put(Constants.COOKIE_KEY, authToken);
                    return headers;
                }
            };
            HttpToolbox.getInstance(getContext()).addToRequestQueue(flashmailsRequest, "FM");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onRequestComplete(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult, String json) {
        // Remote flashmails
        List<Flashmail> remoteFlashmails = FlashmailParser.parse(json);

        // Get shows from local
        List<Flashmail> localFlashmails = new ArrayList<Flashmail>();
        try {
            Cursor curFlashmails = provider.query(FlashmailsContract.CONTENT_URI, null, null, null, null);

            if (curFlashmails != null) {
                while (curFlashmails.moveToNext()) {
                    localFlashmails.add(Flashmail.fromCursor(curFlashmails));
                }
                curFlashmails.close();
            }

            // See what Remote flashmails are missing on Local
            List<Flashmail> flashmailsToLocal = new ArrayList<Flashmail>();
            for (Flashmail remoteFlashmail : remoteFlashmails) {
                if (!localFlashmails.contains(remoteFlashmail)) {
                    flashmailsToLocal.add(remoteFlashmail);
                }
            }

            if (flashmailsToLocal.size() > 0) {
                // Updating local flashmails
                int i = 0;
                ContentValues flashmailsToLocalValues[] = new ContentValues[flashmailsToLocal.size()];
                for (Flashmail localFlashmail : flashmailsToLocal) {
                    flashmailsToLocalValues[i++] = localFlashmail.getContentValues();
                }
                provider.bulkInsert(FlashmailsContract.CONTENT_URI, flashmailsToLocalValues);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}