package com.cyphermessenger.sqlite;

import com.cyphermessenger.crypto.Key;

/**
 * Created by Pier DAgostino on 08/04/14.
 */
public class UserKey {

    private User user;
    private Key userKey;

    // GETTERS
    public User getUser() {
        return user;
    }

    public Key getUserKey() {
        return userKey;
    }
}
