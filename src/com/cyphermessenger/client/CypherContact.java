package com.cyphermessenger.client;

import com.cyphermessenger.crypto.ECKey;

/**
 * Created by halfblood on 15/05/14.
 */
public class CypherContact extends CypherUser {

    String status;
    long contactTimestamp;

    public CypherContact(String username, long userID, ECKey key, long keyTime, String status, long timestamp) {
        super(username, userID, key, keyTime);
        this.status = status;
        this.contactTimestamp = timestamp;
    }



}
