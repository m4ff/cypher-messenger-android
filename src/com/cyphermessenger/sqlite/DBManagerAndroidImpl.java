package com.cyphermessenger.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cyphermessenger.crypto.ECKey;
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
    public void insertUser(User user) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_USER_NAME, user.getUserName());
        values.put(DBHelper.COLUMN_USER_PASSWORD, user.getPassword());
        values.put(DBHelper.COLUMN_USER_AVATAR, user.getAvatar());
        db.insert(DBHelper.TABLE_USERS, null, values);
        db.close();
    }

    @Override
    public void insertContact(User user, Contact contact) {

    }

    @Override
    public void deleteContact(User contact, Contact contact2) {

    }


    public void deleteUser(User user) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.delete(DBHelper.TABLE_USERS, DBHelper.COLUMN_USER_NAME + "=?",new String[]{"" + user.getUserName()});
        db.close();
    }

    public void insertContact(Contact contact) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_CONTACT_NAME, contact.getName());
        values.put(DBHelper.COLUMN_CONTACT_AVATAR, contact.getAvatar());
        db.insert(DBHelper.TABLE_CONTACTS, null, values);
        db.close();
    }

    public void deleteContact(Contact contact) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.delete(DBHelper.TABLE_CONTACTS, DBHelper.COLUMN_CONTACT_NAME + "=?", new String[]{"" + contact.getName()});
        db.close();
    }

    public void insertKey(User user, ECKey key) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_KEY_USER, user.getUserName());
        values.put(DBHelper.COLUMN_KEY_PUBLIC, key.getPublicKey());
        values.put(DBHelper.COLUMN_KEY_PRIVATE, key.getPrivateKey());
        db.insert(DBHelper.TABLE_KEYS, null, values);
        db.close();
    }

    @Override
    public void insertMessage(User user, Contact contact, Messages msg) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_MESSAGES_ID, msg.getId());
        values.put(DBHelper.COLUMN_MESSAGES_SENDER, user.getUserName());
        values.put(DBHelper.COLUMN_MESSAGES_RECEIVER, contact.getName());
        values.put(DBHelper.COLUMN_MESSAGES_TEXT, msg.getText());
        values.put(DBHelper.COLUMN_MESSAGES_SENT, msg.isSent());
        db.insert(DBHelper.TABLE_MESSAGES, null, values);
        db.close();
    }

    @Override
    public void deleteMessage(Messages msg) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.delete(DBHelper.TABLE_MESSAGES, DBHelper.COLUMN_MESSAGES_ID + "=?", new String[]{"" + msg.getId()});
        db.close();
    }
}
