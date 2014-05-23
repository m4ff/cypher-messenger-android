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
    byte[] localPassword;
    byte[] serverPassword;
    Long userID;
    Long keyTime;
    ECKey key;

    
    public CypherUser(String username, byte[] localPassword, byte[] serverPassword, Long userID, ECKey key, Long keyTime) {
        this.username = username;
        this.localPassword = localPassword;
        this.serverPassword = serverPassword;
        this.userID = userID;
        this.key = key;
        this.keyTime = keyTime;
    }

    public CypherUser(String username, Long userID, ECKey key, Long keyTime) {
        this.username = username;
        this.userID = userID;
        this.key = key;
        this.keyTime = keyTime;
    }

    public ECKey getKey() {
        return key;
    }
    
    public String getUsername() {
        return username;
    }

    public byte[] getLocalPassword() {
        return localPassword;
    }

    public byte[] getServerPassword() {
        return serverPassword;
    }

    public Long getUserID() {
        return userID;
    }

	public Long getKeyTime() {
		return keyTime;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CypherUser)) return false;

        CypherUser that = (CypherUser) o;

        if (!username.equals(that.username)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    @Override
    public String toString() {
        return "{ name: " + username + ", id: " + userID + " }";
    }
}
