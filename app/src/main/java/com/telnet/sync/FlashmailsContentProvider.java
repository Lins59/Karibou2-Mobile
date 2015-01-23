package com.telnet.sync;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Created by Pierre Quételart on 12/01/2015.
 */
public class FlashmailsContentProvider extends ContentProvider {

    public static final UriMatcher URI_MATCHER = buildUriMatcher();
    public static final String PATH_LIST = "flashmails";
    public static final int ITEM_LIST = 1;
    public static final String PATH_ID = "flashmails/#";
    public static final int ITEM_ID = 2;
    private FlashmailsDbHelper dbHelper;

    // Uri Matcher for the content provider
    static final UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FlashmailsContract.AUTHORITY;
        matcher.addURI(authority, PATH_LIST, ITEM_LIST);
        matcher.addURI(authority, PATH_ID, ITEM_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new FlashmailsDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case ITEM_LIST:
                return FlashmailsContract.CONTENT_TYPE_DIR;
            case ITEM_ID:
                return FlashmailsContract.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("URI " + uri + " is not supported.");
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        final int match = URI_MATCHER.match(uri);
        switch (match) {
            case ITEM_LIST: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(FlashmailsDbHelper.FLASHMAILS_TABLE_NAME);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }
            case ITEM_ID: {
                int flashmailId = (int) ContentUris.parseId(uri);
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(FlashmailsDbHelper.FLASHMAILS_TABLE_NAME);
                builder.appendWhere(FlashmailsDbHelper.FLASHMAILS_COL_ID + "=" + flashmailId);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }
            default:
                return null;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int token = URI_MATCHER.match(uri);
        switch (token) {
            case ITEM_LIST: {
                long id = db.insert(FlashmailsDbHelper.FLASHMAILS_TABLE_NAME, null, values);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return FlashmailsContract.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
            }
            default: {
                throw new UnsupportedOperationException("URI: " + uri + " not supported.");
            }
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int token = URI_MATCHER.match(uri);
        int rowsDeleted = -1;
        switch (token) {
            case (ITEM_LIST):
                rowsDeleted = db.delete(FlashmailsDbHelper.FLASHMAILS_TABLE_NAME, selection, selectionArgs);
                break;
            case (ITEM_ID):
                String flashmailIdWhereClause = FlashmailsDbHelper.FLASHMAILS_COL_ID + "=" + uri.getLastPathSegment();
                if (!TextUtils.isEmpty(selection))
                    flashmailIdWhereClause += " AND " + selection;
                rowsDeleted = db.delete(FlashmailsDbHelper.FLASHMAILS_TABLE_NAME, flashmailIdWhereClause, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        // Notifying the changes, if there are any
        if (rowsDeleted != -1)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    /**
     * Man..I'm tired..
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        return 0;
    }
}