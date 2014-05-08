package com.cyphermessenger.sqlite;

import com.cyphermessenger.crypto.ECKey;

/**
 * Created by Pier DAgostino on 08/04/14.
 */
public class UserKey {

    private User user;
    private ECKey userKey;

    // GETTERS
    public User getUser() {
        return user;
    }

    public ECKey getUserKey() {
        return userKey;
    }
}
