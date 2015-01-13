package com.telnet.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Pierre Quételart on 12/01/2015.
 */
public class FlashmailSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static FlashmailSyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null)
                sSyncAdapter = new FlashmailSyncAdapter(getApplicationContext(), true);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}