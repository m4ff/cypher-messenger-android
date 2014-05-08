package com.cyphermessenger.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Paolo on 03/05/2014.
 */
public class CypherDictionary {
    public static final String AUTHORITY = "com.cyphermessenger.provider";

    public static class Users implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/users");
        public static final Uri CURRENT_USER_URI = Uri.parse("content://" + AUTHORITY + "/currentUser");
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
    }

    public static class Messages implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/messages");
        public static final String SENDER = "sender";
        public static final String RECEIVER = "receiver";
        public static final String PAYLOAD = "payload";
        public static final String TIMESTAMP = "timestamp";
    }

    public static class Keys {
        public static final Uri CONTENT_URI = Uri.parse("contact://" + AUTHORITY + "/keys");
        public static final Uri LATEST_KEY_URI = Uri.parse("contact://" + AUTHORITY + "/latestKey");
        public static final String PUBLIC_KEY = "publicKey";
        public static final String PRIVATE_KEY = "privateKey";
        public static final String TIMESTAMP = "timestamp";
    }
}
