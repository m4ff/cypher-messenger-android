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
public class CypherMessage {
    private int messageID;
    private String text;
    private long timestamp;
    private boolean isSender;
    private long contactID;

    public CypherMessage(int messageID, String payload, long timestamp, boolean isSender, long contactID) {
        this.messageID = messageID;
        this.text = payload;
        this.timestamp = timestamp;
        this.isSender = isSender;
        this.contactID = contactID;
    }

    public int getMessageID() {
        return messageID;
    }

    public String getText() {
        return text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isSender() {
        return isSender;
    }

    public long getContactID() {
        return contactID;
    }

}
