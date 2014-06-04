package com.cyphermessenger.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.cyphermessenger.client.*;
import com.cyphermessenger.crypto.ECKey;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Pier DAgostino on 08/04/14.
 */
public class DBManagerAndroidImpl implements DBManager {

    private static DBManager impl;
    private SQLiteOpenHelper openHelper;

    private DBManagerAndroidImpl(Context ctx) {
        openHelper = DBHelper.getInstance(ctx);
    }

    public static DBManager getInstance(Context ctx) {
        if (impl == null) {
            impl = new DBManagerAndroidImpl(ctx);
        }
        return impl;
    }

    @Override
    public void setSession(CypherSession session) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        CypherUser user = session.getUser();
        ContentValues val = new ContentValues();
        val.put(DBHelper.COLUMN_USER_SESSION_ID, session.getSessionID());
        val.put(DBHelper.COLUMN_USER_ID, user.getUserID());
        val.put(DBHelper.COLUMN_USER_NAME, user.getUsername());
        val.put(DBHelper.COLUMN_USER_LOCAL_PASSWORD, user.getLocalPassword());
        val.put(DBHelper.COLUMN_USER_SERVER_PASSWORD, user.getServerPassword());
        db.replace(DBHelper.TABLE_USERS, null, val);
    }

    @Override
    public void insertContact(CypherContact contact) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues val = new ContentValues();
        val.put(DBHelper.COLUMN_CONTACT_ID, contact.getUserID());
        val.put(DBHelper.COLUMN_CONTACT_NAME, contact.getUsername());
        val.put(DBHelper.COLUMN_CONTACT_STATUS, contact.getStatus());
        val.put(DBHelper.COLUMN_CONTACT_DATE_TIME, contact.getContactTimestamp());
        db.replace(DBHelper.TABLE_CONTACTS, null, val);
    }

    @Override
    public void insertMessage(CypherMessage msg) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues val = new ContentValues();
        val.put(DBHelper.COLUMN_MESSAGE_ID, msg.getMessageID());
        val.put(DBHelper.COLUMN_MESSAGE_TEXT, msg.getText());
        val.put(DBHelper.COLUMN_MESSAGE_DATE_TIME, msg.getTimestamp());
        val.put(DBHelper.COLUMN_MESSAGE_IS_SENDER, msg.isSender());
        val.put(DBHelper.COLUMN_MESSAGE_CONTACT_ID, msg.getContactID());
        val.put(DBHelper.COLUMN_MESSAGE_SENT, msg.isSent());
        db.replace(DBHelper.TABLE_MESSAGES, null, val);
    }

    @Override
    public void insertKey(CypherUser user, ECKey key) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues val = new ContentValues();
        val.put(DBHelper.COLUMN_KEY_PUBLIC, key.getPublicKey());
        byte[] privateKey = key.getPrivateKey();
        if(privateKey == null) {
            val.putNull(DBHelper.COLUMN_KEY_PRIVATE);
        } else {
            val.put(DBHelper.COLUMN_KEY_PRIVATE, privateKey);
        }
        val.put(DBHelper.COLUMN_KEY_ID, key.getTime());
        val.put(DBHelper.COLUMN_KEY_USER, user.getUserID());
        db.replace(DBHelper.TABLE_KEYS, null, val);
    }

    @Override
    public void setLastUpdateTime(long time) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues val = new ContentValues();
        val.put(DBHelper.COLUMN_USER_NOTIFIED_UNTIL, time);
        db.update(DBHelper.TABLE_USERS, val, null, null);
    }

    @Override
    public long getLastUpdateTime() {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        Cursor c = db.query(DBHelper.TABLE_USERS, new String[] {DBHelper.COLUMN_USER_NOTIFIED_UNTIL}, null, null, null, null, null);
        if (c.moveToNext()) {
            return c.getLong(0);
        } else {
            throw new RuntimeException("No user in db");
        }
    }

    @Override
    public CypherContact getContactByID(long id) {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        Cursor d = db.query(DBHelper.TABLE_CONTACTS, new String[]{DBHelper.COLUMN_CONTACT_NAME, DBHelper.COLUMN_CONTACT_STATUS, DBHelper.COLUMN_CONTACT_DATE_TIME}, DBHelper.COLUMN_CONTACT_ID + " = " + id, null, null, null, null);
        if (d.moveToNext()) {
            String username = d.getString(0);
            String status = d.getString(1);
            long contactTime = d.getLong(2);
            Cursor c = db.query(DBHelper.TABLE_KEYS, new String[]{DBHelper.COLUMN_KEY_ID, DBHelper.COLUMN_KEY_PUBLIC}, DBHelper.COLUMN_KEY_USER + " = " + id, null, null, null, null);
            ECKey key = null;
            Long keyTime = null;
            if (c.moveToNext()) {
                keyTime = c.getLong(0);
                byte[] publicKey = c.getBlob(1);
                key = new ECKey(publicKey, null, keyTime);
            }
            CypherContact contact = new CypherContact(username, id, key, keyTime, status, contactTime);
            return contact;
        } else {
            return null;
        }
    }

    @Override
    public void setMessageSent(CypherMessage msg) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues val = new ContentValues();
        val.put(DBHelper.COLUMN_MESSAGE_SENT, true);
        db.update(DBHelper.TABLE_MESSAGES, val, DBHelper.COLUMN_MESSAGE_ID + " = ? AND " + DBHelper.COLUMN_MESSAGE_CONTACT_ID + " = ?", new String[]{msg.getMessageID() + "", msg.getContactID() + ""});
    }

    @Override
    public CypherSession getSession() {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        String[] columns = new String[]{DBHelper.COLUMN_USER_SESSION_ID, DBHelper.COLUMN_USER_ID, DBHelper.COLUMN_USER_NAME, DBHelper.COLUMN_USER_LOCAL_PASSWORD, DBHelper.COLUMN_USER_SERVER_PASSWORD};
        Cursor c = db.query(DBHelper.TABLE_USERS, columns, null, null, null, null, null);
        if (c.moveToNext()) {
            String sessionID = c.getString(0);
            long userID = c.getLong(1);
            String username = c.getString(2);
            byte[] localPassword = c.getBlob(3);
            byte[] serverPassword = c.getBlob(4);
            Cursor d = db.query(DBHelper.TABLE_KEYS, new String[]{DBHelper.COLUMN_KEY_PUBLIC, DBHelper.COLUMN_KEY_PRIVATE, DBHelper.COLUMN_KEY_ID}, DBHelper.COLUMN_KEY_USER + " = ?", new String[]{userID + ""}, null, null, null);
            if (d.moveToNext()) {
                byte[] publicKey = d.getBlob(0);
                byte[] privateKey = d.getBlob(1);
                ECKey keys = new ECKey(publicKey, privateKey);
                long keyTime = d.getLong(2);
                keys.setTime(keyTime);
                CypherUser newUser = new CypherUser(username, localPassword, serverPassword, userID, keys, keyTime);
                CypherSession newSession = new CypherSession(newUser, sessionID);
                return newSession;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public ECKey getKeyForTime(long time) {
        return null;
    }

    @Override
    public List<CypherMessage> getMessages(CypherUser contact, int offset, int limit) {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        String[] columns = new String[]{DBHelper.COLUMN_MESSAGE_ID, DBHelper.COLUMN_MESSAGE_IS_SENDER, DBHelper.COLUMN_MESSAGE_TEXT, DBHelper.COLUMN_MESSAGE_SENT, DBHelper.COLUMN_MESSAGE_DATE_TIME};
        Cursor c = db.query(DBHelper.TABLE_MESSAGES, columns, DBHelper.COLUMN_MESSAGE_CONTACT_ID + " = ?", new String[]{contact.getUserID() + ""}, "", "", "", offset + ", " + limit);
        List<CypherMessage> list = new LinkedList<>();
        while(c.moveToNext()) {
            list.add(new CypherMessage(c.getInt(0), c.getString(2), c.getLong(4), c.getInt(1) != 0, contact.getUserID(), c.getInt(3) != 0));
        }
        return list;
    }

    @Override
    public List<CypherContact> getContacts() {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        String[] columns = new String[]{DBHelper.COLUMN_CONTACT_NAME, DBHelper.COLUMN_CONTACT_ID, DBHelper.COLUMN_CONTACT_STATUS, DBHelper.COLUMN_CONTACT_DATE_TIME};
        Cursor c = db.query(DBHelper.TABLE_CONTACTS, columns, null, null, null, null, null, null);
        List<CypherContact> list = new LinkedList<>();
        while(c.moveToNext()) {
            long contactID = c.getLong(1);
            Cursor d = db.query(DBHelper.TABLE_KEYS, new String[] {DBHelper.COLUMN_KEY_ID, DBHelper.COLUMN_KEY_PUBLIC}, DBHelper.COLUMN_KEY_USER + " = " + contactID, null, null, null, "1");
            d.moveToNext();
            ECKey key = null;
            Long keyTime = null;
            if(d.moveToNext()) {
                key = new ECKey(d.getBlob(1), null);
                keyTime = d.getLong(0);
                key.setTime(keyTime);
            }
            list.add(new CypherContact(c.getString(0), contactID, key, keyTime, c.getString(2), c.getLong(3)));
        }
        return list;
    }

    @Override
    public void logout() {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + DBHelper.TABLE_CONTACTS);
        db.execSQL("DELETE FROM " + DBHelper.TABLE_USERS);
        db.execSQL("DELETE FROM " + DBHelper.TABLE_KEYS);
        db.execSQL("DELETE FROM " + DBHelper.TABLE_MESSAGES);
    }

    @Override
    public void deleteContact(CypherUser contact) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.delete(DBHelper.TABLE_CONTACTS, DBHelper.COLUMN_CONTACT_ID + " = " + contact.getUserID(), null);
        db.delete(DBHelper.TABLE_KEYS, DBHelper.COLUMN_KEY_USER + " = " + contact.getUserID(), null);
        db.delete(DBHelper.TABLE_MESSAGES, DBHelper.COLUMN_MESSAGE_CONTACT_ID + " = " + contact.getUserID(), null);
    }
}
