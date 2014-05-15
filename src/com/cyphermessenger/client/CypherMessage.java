/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cyphermessenger.client;

import java.util.Date;

/**
 *
 * @author halfblood
 */
public class CypherMessage {
    private int messageID;
    private byte[] payload;
    private Date timestamp;
    private boolean isSender;
    private long contactID;

    public CypherMessage(int messageID, byte[] payload, Date timestamp, boolean isSender, long contactID) {
        this.messageID = messageID;
        this.payload = payload;
        this.timestamp = timestamp;
        this.isSender = isSender;
        this.contactID = contactID;
    }

    public int getMessageID() {
        return messageID;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSender() {
        return isSender;
    }

    public long getContactID() {
        return contactID;
    }

}
