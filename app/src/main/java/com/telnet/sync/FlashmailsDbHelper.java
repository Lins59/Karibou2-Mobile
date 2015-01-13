package com.telnet.sync;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Pierre Quételart on 12/01/2015.
 */
public class FlashmailsDbHelper extends SQLiteOpenHelper {
    // DB Table consts
    public static final String FLASHMAILS_TABLE_NAME = "flashmails";
    public static final String FLASHMAILS_COL_ID = "id";
    public static final String FLASHMAILS_COL_MESSAGE = "message";
    // Database creation sql statement
    public static final String DATABASE_CREATE = "create table "
            + FLASHMAILS_TABLE_NAME + "(" +
            FLASHMAILS_COL_ID + " integer primary key autoincrement, " +
            FLASHMAILS_COL_MESSAGE + " text not null" +
            ");";
    private static final String DATABASE_NAME = "karibou.db";
    private static final int DATABASE_VERSION = 1;

    public FlashmailsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(FlashmailsDbHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        onCreate(db);
    }

}