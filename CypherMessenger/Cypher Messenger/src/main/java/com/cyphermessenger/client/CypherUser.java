/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cyphermessenger.client;

import com.cyphermessenger.crypto.ECKey;

/**
 *
 * @author halfblood
 */
public class CypherUser {
    private final String username;
    private final String password;
    private final long userID;
    private final ECKey key;

    
    public CypherUser(String username, String password, long userID, ECKey key) {
        this.username = username;
        this.password = password;
        this.userID = userID;
        this.key = key;
    }

    public ECKey getKey() {
        return key;
    }
    
    public String getPassword() {
        return password;
    }
    
    public String getUsername() {
        return username;
    }

    public long getUserID() {
        return userID;
    }

}
