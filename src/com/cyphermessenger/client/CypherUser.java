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
    long userID;
    long keyTime;
    ECKey key;

    
    public CypherUser(String username, byte[] localPassword, byte[] serverPassword, long userID, ECKey key, long keyTime) {
        this.username = username;
        this.localPassword = localPassword;
        this.serverPassword = serverPassword;
        this.userID = userID;
        this.key = key;
        this.keyTime = keyTime;
    }

    public CypherUser(String username, long userID, ECKey key, long keyTime) {
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

    public long getUserID() {
        return userID;
    }

	public long getKeyTime() {
		return keyTime;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CypherUser that = (CypherUser) o;

        if (userID != that.userID) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (userID ^ (userID >>> 32));
    }

    @Override
    public String toString() {
        return "{ name: " + username + ", id: " + userID + " }";
    }
}
