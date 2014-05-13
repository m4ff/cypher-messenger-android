/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cyphermessenger.client;

import java.security.Timestamp;

/**
 *
 * @author halfblood
 */
public class CypherMessage {
    private int messageID;
    private byte[] payload;
    private Timestamp timestamp;

    public int getMessageID() {
        return messageID;
    }

    public void setCaptchaImage(int messageID) {
        this.messageID = messageID;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
