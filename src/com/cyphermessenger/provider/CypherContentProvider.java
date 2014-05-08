package com.cyphermessenger.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.cyphermessenger.sqlite.DBHelper;

/**
 * Created by Paolo on 02/05/2014.
 */
public class CypherContentProvider extends ContentProvider {

    private SQLiteOpenHelper sqLiteOpenHelper;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int USERS = 1;
    private static final int MESSAGES = 2;
    private static final int KEYS = 3;

    static {
        uriMatcher.addURI(CypherDictionary.AUTHORITY, CypherDictionary.Users.CONTENT_URI.getPath(), USERS);
        uriMatcher.addURI(CypherDictionary.AUTHORITY, CypherDictionary.Messages.CONTENT_URI.getPath(), MESSAGES);
        uriMatcher.addURI(CypherDictionary.AUTHORITY, CypherDictionary.Keys.CONTENT_URI.getPath(), KEYS);
    }

    @Override
    public boolean onCreate() {
        sqLiteOpenHelper = DBHelper.getInstance(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selectionClause, String[] selectionArgs, String orderClause) {
        SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
        switch(uriMatcher.match(uri)) {
            case USERS:
                return db.query("users", projection, selectionClause, selectionArgs, null, null, orderClause);
            case MESSAGES:
                return db.query("messages", projection, selectionClause, selectionArgs, null, null, orderClause);
            case KEYS:
                return db.query("keys", projection, selectionClause, selectionArgs, null, null, orderClause);
            default:
                throw new IllegalArgumentException(uri.toString());
        }
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
