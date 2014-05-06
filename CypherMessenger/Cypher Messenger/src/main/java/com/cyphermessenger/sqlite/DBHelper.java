package com.cyphermessenger.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Pier DAgostino on 24/03/14.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper instance;

    private static final String DATABASE_NAME = "cypherMessenger.db";
    private static final int DATABASE_VERSION = 1;

    // TABLES
    public static final String TABLE_USERS = "users";
    public static final String TABLE_CONTACTS = "contacts";
    public static final String TABLE_MESSAGES = "messages";
    public static final String TABLE_KEYS = "keys";

    // USER COLUMNS
    public static final String COLUMN_USER_ID = "userID";
    public static final String COLUMN_USER_NAME = "username";
    public static final String COLUMN_USER_PASSWORD = "password";
    public static final String COLUMN_USER_AVATAR = "avatar";

    // CONTACTS COLUMNS
    public static final String COLUMN_CONTACT_ID = "_id";
    public static final String COLUMN_CONTACT_NAME = "contactName";
    public static final String COLUMN_CONTACT_AVATAR = "avatar";
    public static final String COLUMN_CONTACT_KEY_PUBLIC = "contactName";

    // MESSAGES COLUMNS
    public static final String COLUMN_MESSAGES_ID = "_id";
    public static final String COLUMN_MESSAGES_SENDER = "sender";
    public static final String COLUMN_MESSAGES_RECEIVER = "receiver";
    public static final String COLUMN_MESSAGES_TEXT = "text";
    public static final String COLUMN_MESSAGES_SENT = "sent";
    public static final String COLUMN_MESSAGES_DATE_TIME = "timestamp";

    // KEY COLUMNS
    public static final String COLUMN_KEY_ID = "_id";
    public static final String COLUMN_KEY_USER = "userID";
    public static final String COLUMN_KEY_PUBLIC = "publicKey";
    public static final String COLUMN_KEY_PRIVATE = "privateKey";

    // CREATION QUERIES
    private static final String DATABASE_CREATE_USER = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_USER_ID + " BIGINT PRIMARY KEY NOT NULL, "
            + COLUMN_USER_NAME + " TEXT UNIQUE KEY NOT NULL, "
            + COLUMN_USER_PASSWORD + " BLOB NOT NULL, "
            + COLUMN_USER_AVATAR + " BLOB);";

    private static final String DATABASE_CREATE_CONTACT = "CREATE TABLE " + TABLE_CONTACTS +"("
            + COLUMN_CONTACT_ID + " BIGINT PRIMARY KEY NOT NULL, "
            + COLUMN_CONTACT_NAME + " TEXT UNIQUE KEY NOT NULL, "
            + COLUMN_CONTACT_KEY_PUBLIC + " BLOB NOT NULL, "
            + COLUMN_CONTACT_AVATAR + " BLOB);";

    private static final String DATABASE_CREATE_MESSAGES = "CREATE TABLE " + TABLE_MESSAGES + "("
            + COLUMN_MESSAGES_ID + " INT PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_MESSAGES_SENDER + " BIGINT NOT NULL, "
            + COLUMN_MESSAGES_RECEIVER + " BIGINT NOT NULL, "
            + COLUMN_MESSAGES_TEXT + " TEXT NOT NULL, "
            + COLUMN_MESSAGES_SENT + " BOOLEAN, "
            + COLUMN_MESSAGES_DATE_TIME + " DATETIME);";

    private static final String DATABASE_CREATE_KEY = "CREATE TABLE " + TABLE_KEYS + "("
            + COLUMN_KEY_ID + " INT PRIMARY KEY, "
            + COLUMN_KEY_USER + " BIGINT NOT NULL, "
            + COLUMN_KEY_PUBLIC + " BLOB NOT NULL, "
            + COLUMN_KEY_PRIVATE + " BLOB NOT NULL);";

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DBHelper getInstance(Context ctx) {
        if(instance == null) {
            instance = new DBHelper(ctx);
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
        Log.w(DBHelper.class.getName(), "Updating from version " + oldVersion + " to "
                + newVersion + "which will destroy all data");
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_KEYS);
        onCreate(db);
    }
}
