package com.telnet.objects;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.telnet.sync.FlashmailsDbHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Flashmail {
    public static final String TAG = "Flashmail";
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
        String dateTime = curFlashmail.getString(curFlashmail.getColumnIndex(FlashmailsDbHelper.FLASHMAILS_COL_DATE));
        Date date = null;

        try {
            date = sdf.parse(dateTime);
        } catch (ParseException e) {
            Log.e(TAG, "Parsing datetime failed", e);
        }

        // Get user
        // TODO JOIN
        int userId = curFlashmail.getInt(curFlashmail.getColumnIndex(FlashmailsDbHelper.FLASHMAILS_COL_USER_ID));
        String login = curFlashmail.getString(curFlashmail.getColumnIndex(FlashmailsDbHelper.FLASHMAILS_COL_USER_LOGIN));
        String firstname = curFlashmail.getString(curFlashmail.getColumnIndex(FlashmailsDbHelper.FLASHMAILS_COL_USER_FIRSTNAME));
        String lastname = curFlashmail.getString(curFlashmail.getColumnIndex(FlashmailsDbHelper.FLASHMAILS_COL_USER_LASTNAME));
        String surname = curFlashmail.getString(curFlashmail.getColumnIndex(FlashmailsDbHelper.FLASHMAILS_COL_USER_SURNAME));
        User sender = new User(userId, login, firstname, lastname, surname);

        // Forge flashmail
        Flashmail fm = new Flashmail(id, sender, date, message);
        fm.setOldMessage(oldMessage);
        return fm;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(FlashmailsDbHelper.FLASHMAILS_COL_ID, id);
        values.put(FlashmailsDbHelper.FLASHMAILS_COL_MESSAGE, message);
        values.put(FlashmailsDbHelper.FLASHMAILS_COL_OLD_MESSAGE, oldMessage);
        values.put(FlashmailsDbHelper.FLASHMAILS_COL_DATE, sdf.format(date));

        // User
        values.put(FlashmailsDbHelper.FLASHMAILS_COL_USER_ID, sender.getId());
        values.put(FlashmailsDbHelper.FLASHMAILS_COL_USER_LOGIN, sender.getLogin());
        values.put(FlashmailsDbHelper.FLASHMAILS_COL_USER_FIRSTNAME, sender.getFirstname());
        values.put(FlashmailsDbHelper.FLASHMAILS_COL_USER_LASTNAME, sender.getLastname());
        values.put(FlashmailsDbHelper.FLASHMAILS_COL_USER_SURNAME, sender.getSurname());
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Flashmail flashmail = (Flashmail) o;

        if (!date.equals(flashmail.date)) return false;
        if (!id.equals(flashmail.id)) return false;
        if (!message.equals(flashmail.message)) return false;
        if (oldMessage != null ? !oldMessage.equals(flashmail.oldMessage) : flashmail.oldMessage != null)
            return false;
        if (!sender.equals(flashmail.sender)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + sender.hashCode();
        result = 31 * result + date.hashCode();
        result = 31 * result + message.hashCode();
        result = 31 * result + (oldMessage != null ? oldMessage.hashCode() : 0);
        return result;
    }
}
