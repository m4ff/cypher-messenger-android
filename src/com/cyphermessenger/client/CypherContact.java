package com.cyphermessenger.client;

import com.cyphermessenger.crypto.ECKey;

import java.util.Date;

/**
 * Created by halfblood on 15/05/14.
 */
public class CypherContact extends CypherUser {

    String status;
    Date timestamp;

    CypherContact(String username, long userID, ECKey key, Date keyTime, String status, Date timestamp) {
        super(username, userID, key, keyTime);
        this.status = status;
        this.timestamp = timestamp;
    }



}
