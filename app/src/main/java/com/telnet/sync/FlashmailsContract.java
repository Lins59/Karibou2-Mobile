package com.telnet.sync;

import android.net.Uri;

/**
 * Created by Pierre Quételart on 12/01/2015.
 */
public class FlashmailsContract {
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.karibou.flashmail";
    public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/vnd.karibou.flashmail";

    public static final String AUTHORITY = "com.telnet.karibou.flashmails";
    // content://<authority>/<path to type>
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String FLASHMAILS_ID = "id";
    public static final String FLASHMAILS_MESSAGE = "message";
}
