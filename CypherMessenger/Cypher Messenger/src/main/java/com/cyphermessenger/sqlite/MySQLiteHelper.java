package com.cyphermessenger.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Pier DAgostino on 24/03/14.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "chypermessenger.db";
    private static final int DATABASE_VERSION = 1;

    // TABLES
    public static final String TABLE_USER = "USER";
    public static final String TABLE_CONTACTS = "CONTACTS";
    public static final String TABLE_MESSAGES = "MESSAGES";

    // USER COLUMNS
    public static final String COLUMN_USER_ID = "USER ID";
    public static final String COLUMN_USER_PASSWORD = "PASSWORD";
    public static final String COLUMN_USER_PUBLIC_KEY = "PUBLIC KEY";
    public static final String COLUMN_USER_PRIVATE_KEY = "PRIVATE KEY";
    public static final String COLUMN_USER_REGISTERED = "REGISTERED";

    // CONTACTS COLUMNS
    public static final String COLUMN_CONTACTS_NAME = "NAME";
    public static final String COLUMN_CONTACTS_HAS_SHARED_CONVERSATION = "SHARE CONVERSATIONS";
    public static final String COLUMN_CONTACTS_LAST_TIME_LOGGED = "LAST TIME LOGGED";
    /* TODO think about all possible columns */

    // MESSAGES COLUMNS
    /* TODO think about all possible columns */

    // CREATION QUERIES
    private static final String DATABASE_CREATE_USER = "CREATE TABLE " + TABLE_USER + "("
            + COLUMN_USER_ID + " TEXT PRIMARY KEY NOT NULL, "
            + COLUMN_USER_PASSWORD + " TEXT NOT NULL, "
            + COLUMN_USER_PUBLIC_KEY + " TEXT NOT NULL, "
            + COLUMN_USER_PRIVATE_KEY + " TEXT NOT NULL"
            + COLUMN_USER_REGISTERED + " NUMERIC);";
    private static final String DATABASE_CREATE_CONTACTS = "CREATE TABLE " + TABLE_CONTACTS +"("
            + COLUMN_CONTACTS_NAME + " TEXT PRIMARY KEY NOT NULL, "
            + COLUMN_CONTACTS_HAS_SHARED_CONVERSATION + " NUMERIC, "
            + COLUMN_CONTACTS_LAST_TIME_LOGGED + "TEXT DEFAULT CURRENT_TIMESTAMP";
    private static final String DATABASE_CREATE_MESSAGES = "";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_USER);
        db.execSQL(DATABASE_CREATE_CONTACTS);
        db.execSQL(DATABASE_CREATE_MESSAGES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(), "Updating from version " + oldVersion + " to "
                + newVersion + "which will destroy all data");
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_MESSAGES);
        onCreate(db);
    }
}
