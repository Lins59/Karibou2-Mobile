package com.telnet.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.telnet.karibou.Constants;
import com.telnet.objects.Flashmail;

import java.util.List;

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
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(TAG, " > onPerformSync");
        try {
            // Get the auth token for the current account
            String authToken = mAccountManager.blockingGetAuthToken(account, Constants.AUTHTOKEN_TYPE_FULL_ACCESS, true);
            Log.d(TAG, "We have retrieved this authToken: " + authToken);

            // TODO GET REMOTE FM
            List<Flashmail> remoteFlashmails;


            // Get shows from local
            /*ArrayList<Flashmail> localFlashmails = new ArrayList<Flashmail>();
            Cursor curTvShows = provider.query(TvShowsContract.CONTENT_URI, null, null, null, null);
            if (curTvShows != null) {
                while (curTvShows.moveToNext()) {
                    localTvShows.add(TvShow.fromCursor(curTvShows));
                }
                curTvShows.close();
            }
// See what Local shows are missing on Remote
            ArrayList<TvShow> showsToRemote = new ArrayList<TvShow>();
            for (TvShow localTvShow : localTvShows) {
                if (!remoteTvShows.contains(localTvShow))
                    showsToRemote.add(localTvShow);
            }
// See what Remote shows are missing on Local
            ArrayList<TvShow> showsToLocal = new ArrayList<TvShow>();
            for (TvShow remoteTvShow : remoteTvShows) {
                if (!localTvShows.contains(remoteTvShow) && remoteTvShow.year != 1) // TODO REMOVE THIS
                    showsToLocal.add(remoteTvShow);
            }

            provider.bulkInsert(TvShowsContract.CONTENT_URI, showsToLocalValues);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}