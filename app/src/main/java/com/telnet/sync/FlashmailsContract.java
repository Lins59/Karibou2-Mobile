package com.telnet.sync;

import android.content.ContentResolver;
import android.net.Uri;

/**
 * Created by Pierre Quételart on 12/01/2015.
 */
public class FlashmailsContract {
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.karibou.flashmail";
    public static final String CONTENT_TYPE_DIR = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.karibou.flashmail";

    public static final String AUTHORITY = "com.telnet.karibou.provider";
    // content://<authority>/<path to type>
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/flashmails");
}
