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
    String username;
    String password;
    long userID;
    long keyTime;
    ECKey key;

    
    CypherUser(String username, String password, long userID, ECKey key, long keyTime) {
        this.username = username;
        this.password = password;
        this.userID = userID;
        this.key = key;
        this.keyTime = keyTime;
    }

    CypherUser(String username, long userID, ECKey key, long keyTime) {
        this.username = username;
        this.userID = userID;
        this.password = null;
        this.key = key;
        this.keyTime = keyTime;
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

	public long getKeyTime() {
		return keyTime;
	}

}
