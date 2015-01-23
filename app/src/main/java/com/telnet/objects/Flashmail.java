package com.telnet.objects;

import android.content.ContentValues;
import android.database.Cursor;

import com.telnet.sync.FlashmailsDbHelper;

import java.util.Date;

public class Flashmail {
    private String id;
    private User sender;
    private Date date;
    private String message, oldMessage;

    public Flashmail(String id) {
        this.id = id;
    }

    public Flashmail(String id, User sender, Date date, String message) {
        this.id = id;
        this.sender = sender;
        this.date = date;
        this.message = message;
    }

    // Create a TvShow object from a cursor
    public static Flashmail fromCursor(Cursor curFlashmail) {
        String id = curFlashmail.getString(curFlashmail.getColumnIndex(FlashmailsDbHelper.FLASHMAILS_COL_ID));
        String message = curFlashmail.getString(curFlashmail.getColumnIndex(FlashmailsDbHelper.FLASHMAILS_COL_MESSAGE));
        String oldMessage = curFlashmail.getString(curFlashmail.getColumnIndex(FlashmailsDbHelper.FLASHMAILS_COL_OLD_MESSAGE));
        Flashmail fm = new Flashmail(id);
        fm.setMessage(message);
        fm.setOldMessage(oldMessage);
        return fm;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(FlashmailsDbHelper.FLASHMAILS_COL_ID, id);
        values.put(FlashmailsDbHelper.FLASHMAILS_COL_MESSAGE, message);
        values.put(FlashmailsDbHelper.FLASHMAILS_COL_OLD_MESSAGE, oldMessage);
        return values;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOldMessage() {
        return oldMessage;
    }

    public void setOldMessage(String oldMessage) {
        this.oldMessage = oldMessage;
    }

}
