package com.cyphermessenger.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Pier DAgostino on 24/03/14.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    private static MySQLiteHelper instance;

    private static final String DATABASE_NAME = "chypermessenger.db";
    private static final int DATABASE_VERSION = 1;

    // TABLES
    public static final String TABLE_USER = "USER";
    public static final String TABLE_CONTACT = "CONTACTS";
    public static final String TABLE_MESSAGES = "MESSAGES";
    public static final String TABLE_KEY = "KEY";

    // USER COLUMNS
    public static final String COLUMN_USER_NAME = "USER_NAME";
    public static final String COLUMN_USER_PASSWORD = "PASSWORD";
    public static final String COLUMN_USER_AVATAR = "AVATAR";

    // CONTACTS COLUMNS
    public static final String COLUMN_CONTACT_NAME = "NAME";
    public static final String COLUMN_CONTACT_AVATAR = "AVATAR";

    // MESSAGES COLUMNS
    public static final String COLUMN_MESSAGES_ID = "MEX_ID";
    public static final String COLUMN_MESSAGES_SENDER = "SENDER";
    public static final String COLUMN_MESSAGES_RECEIVER = "RECEIVER";
    public static final String COLUMN_MESSAGES_TEXT = "TEXT";
    public static final String COLUMN_MESSAGES_SENT = "SENT";
    public static final String COLUMN_MESSAGES_DATE_TIME = "DATE_TIME";

    // KEY COLUMNS
    public static final String COLUMN_KEY_USER = "USER";
    public static final String COLUMN_KEY_PUBLIC = "PUBLIC_KEY";
    public static final String COLUMN_KEY_PRIVATE = "PRIVATE_KEY";

    // CREATION QUERIES
    private static final String DATABASE_CREATE_USER = "CREATE TABLE " + TABLE_USER + "("
            + COLUMN_USER_NAME + " TEXT PRIMARY KEY NOT NULL, "
            + COLUMN_USER_PASSWORD + " TEXT NOT NULL, "
            + COLUMN_USER_AVATAR + " INT);";

    private static final String DATABASE_CREATE_CONTACT = "CREATE TABLE " + TABLE_CONTACT +"("
            + COLUMN_CONTACT_NAME + " TEXT PRIMARY KEY NOT NULL, "
            + COLUMN_CONTACT_AVATAR + " INT);";

    private static final String DATABASE_CREATE_MESSAGES = "CREATE TABLE " + TABLE_MESSAGES + "("
            + COLUMN_MESSAGES_ID + " INT PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_MESSAGES_SENDER + " TEXT NOT NULL, "
            + COLUMN_MESSAGES_RECEIVER + " TEXT NOT NULL, "
            + COLUMN_MESSAGES_TEXT + " TEXT NOT NULL, "
            + COLUMN_MESSAGES_SENT + " BOOLEAN, "
            + COLUMN_MESSAGES_DATE_TIME + " TIMESTAMP);";

    private static final String DATABASE_CREATE_KEY = "CREATE TABLE " + TABLE_KEY + "("
            + COLUMN_KEY_USER + " TEXT PRIMARY KEY, "
            + COLUMN_KEY_PUBLIC + " TEXT NOT NULL, "
            + COLUMN_KEY_PRIVATE + " TEXT NOT NULL);";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized MySQLiteHelper getInstance(Context ctx) {
        if(instance == null) {
            instance = new MySQLiteHelper(ctx);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_USER);
        db.execSQL(DATABASE_CREATE_CONTACT);
        db.execSQL(DATABASE_CREATE_MESSAGES);
        db.execSQL(DATABASE_CREATE_KEY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(), "Updating from version " + oldVersion + " to "
                + newVersion + "which will destroy all data");
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_CONTACT);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_KEY);
        onCreate(db);
    }
}
