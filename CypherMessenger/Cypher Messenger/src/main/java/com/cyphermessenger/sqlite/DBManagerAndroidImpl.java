package com.cyphermessenger.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cyphermessenger.crypto.Key;
/**
 * Created by Pier DAgostino on 08/04/14.
 */
public class DBManagerAndroidImpl implements DBManager {

    private static DBManager impl;
    private SQLiteOpenHelper openHelper;

    private DBManagerAndroidImpl(Context ctx){
        openHelper = MySQLiteHelper.getInstance(ctx);
    }

    public static DBManager getInstance(Context ctx) {
        if(impl == null) {
            impl = new DBManagerAndroidImpl(ctx);
        }
        return impl;
    }

    @Override
    public void insertUser(User user) throws SQLException {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_USER_NAME, user.getUserName());
        values.put(MySQLiteHelper.COLUMN_USER_PASSWORD, user.getPassword());
        values.put(MySQLiteHelper.COLUMN_USER_AVATAR, user.getAvatar());
        db.insert(MySQLiteHelper.TABLE_USER, null, values);
        db.close();
    }

    @Override
    public void deleteUser(User user) throws SQLException {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.delete(MySQLiteHelper.TABLE_USER, MySQLiteHelper.COLUMN_USER_NAME + "=?",new String[]{"" + user.getUserName()});
        db.close();
    }

    @Override
    public void insertContact(Contact contact) throws SQLException {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_CONTACT_NAME, contact.getName());
        values.put(MySQLiteHelper.COLUMN_CONTACT_AVATAR, contact.getAvatar());
        db.insert(MySQLiteHelper.TABLE_CONTACT, null, values);
        db.close();
    }

    @Override
    public void deleteContact(Contact contact) throws SQLException {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.delete(MySQLiteHelper.TABLE_CONTACT, MySQLiteHelper.COLUMN_CONTACT_NAME + "=?", new String[]{"" + contact.getName()});
        db.close();
    }

    @Override
    public void insertKey(User user, Key key) throws SQLException {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_KEY_USER, user.getUserName());
        values.put(MySQLiteHelper.COLUMN_KEY_PUBLIC, key.getPublicKey());
        values.put(MySQLiteHelper.COLUMN_KEY_PRIVATE, key.getPrivateKey());
        db.insert(MySQLiteHelper.TABLE_KEY, null, values);
        db.close();
    }

    @Override
    public void insertMessage(User user, Contact contact, Messages msg) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_MESSAGES_ID, msg.getId());
        values.put(MySQLiteHelper.COLUMN_MESSAGES_SENDER, user.getUserName());
        values.put(MySQLiteHelper.COLUMN_MESSAGES_RECEIVER, contact.getName());
        values.put(MySQLiteHelper.COLUMN_MESSAGES_TEXT, msg.getText());
        values.put(MySQLiteHelper.COLUMN_MESSAGES_SENT, msg.isSent());
        db.insert(MySQLiteHelper.TABLE_MESSAGES, null, values);
        db.close();
    }

    @Override
    public void deleteMessage(Messages msg) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.delete(MySQLiteHelper.TABLE_MESSAGES, MySQLiteHelper.COLUMN_MESSAGES_ID + "=?", new String[]{"" + msg.getId()});
        db.close();
    }
}
