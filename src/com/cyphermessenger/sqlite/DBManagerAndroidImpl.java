package com.cyphermessenger.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cyphermessenger.client.*;
import com.cyphermessenger.crypto.ECKey;

import java.util.List;

/**
 * Created by Pier DAgostino on 08/04/14.
 */
public class DBManagerAndroidImpl implements DBManager {

    private static DBManager impl;
    private SQLiteOpenHelper openHelper;

    private DBManagerAndroidImpl(Context ctx){
        openHelper = DBHelper.getInstance(ctx);
    }

    public static DBManager getInstance(Context ctx) {
        if(impl == null) {
            impl = new DBManagerAndroidImpl(ctx);
        }
        return impl;
    }

    @Override
    public void setUser(CypherUser user) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues val = new ContentValues();
        val.put(DBHelper.COLUMN_USER_ID, user.getUserID());
        val.put(DBHelper.COLUMN_USER_NAME, user.getUsername());
        val.put(DBHelper.COLUMN_LOCAL_PASSWORD, user.getLocalPassword());
        val.put(DBHelper.COLUMN_SERVER_PASSWORD, user.getServerPassword());
        db.replace(DBHelper.TABLE_USERS, null, val);
    }

    @Override
    public void setSession(CypherSession session) {
    }

    @Override
    public void insertContact(CypherContact contact) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues val = new ContentValues();
        val.put(DBHelper.COLUMN_CONTACT_ID , contact.getUserID());
        val.put(DBHelper.COLUMN_CONTACT_NAME, contact.getUsername());
        val.put(DBHelper.COLUMN_CONTACT_STATUS, contact.getStatus());
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
        db.replace(DBHelper.TABLE_MESSAGES, null, val);
    }

    @Override
    public void insertKey(ECKey key) {

    }

    @Override
    public void setNotifiedUntil(long timestamp) {

    }

    @Override
    public void setMessageSent(CypherMessage msg) {

    }

    @Override
    public CypherSession getSession() {
        return null;
    }

    @Override
    public CypherUser getUser() {
        return null;
    }

    @Override
    public ECKey getKeyForTime(long time) {
        return null;
    }

    @Override
    public List<CypherMessage> getMessages(CypherUser contact) {
        return null;
    }

    @Override
    public List<CypherContact> getContacts() {
        return null;
    }

    @Override
    public void logout() {
        //TRUNCATE TABLE ...
    }

    @Override
    public void deleteContact(CypherUser contact) {

    }
}
