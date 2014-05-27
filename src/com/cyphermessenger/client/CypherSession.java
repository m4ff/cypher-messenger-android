/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cyphermessenger.client;

/**
 *
 * @author halfblood
 */
public class CypherSession {
    CypherUser user;
    String sessionID;

    public CypherSession(CypherUser user, String sessionID) {
        this.user = user;
        this.sessionID = sessionID;
    }

    public CypherUser getUser() {
        return user;
    }

    public String getSessionID() {
        return sessionID;
    }
    
}
