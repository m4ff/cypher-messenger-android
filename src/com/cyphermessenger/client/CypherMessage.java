/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cyphermessenger.client;

import android.util.Log;
import com.cyphermessenger.crypto.Decrypt;
import com.cyphermessenger.crypto.ECKey;
import com.cyphermessenger.crypto.Encrypt;
import com.cyphermessenger.utils.Utils;
import org.spongycastle.crypto.InvalidCipherTextException;

/**
 *
 * @author halfblood
 */
public class CypherMessage implements Comparable {
    int messageID;
    String text;
    long timestamp;
    boolean isSender;
    long contactID;
    boolean isSent;
    boolean isEncrypted;
    byte[] ciphertext;

    public CypherMessage(int messageID, String payload, long timestamp, boolean isSender, long contactID) {
        this.messageID = messageID;
        this.text = payload;
        this.timestamp = timestamp;
        this.isSender = isSender;
        this.contactID = contactID;
        this.isSent = true;
        this.isEncrypted = false;
    }

    public CypherMessage(int messageID, byte[] payload, long timestamp, boolean isSender, long contactID) {
        this.messageID = messageID;
        this.ciphertext = payload;
        this.timestamp = timestamp;
        this.isSender = isSender;
        this.contactID = contactID;
        this.isSent = true;
        this.isEncrypted = true;
    }

    public CypherMessage(int messageID, String text, long timestamp, boolean isSender, long contactID, boolean isSent) {
        this(messageID, text, timestamp, isSender, contactID);
        this.isSent = isSent;
    }

    public static CypherMessage create(CypherUser contactUser, String message) {
        long timestamp = System.currentTimeMillis();
        byte[] messageID = Utils.randomBytes(4);
        int messageIDLong = (int) Utils.bytesToLong(messageID);
        return new CypherMessage(messageIDLong, message, timestamp, true, contactUser.getUserID(), false);
    }

    public void decrypt(ECKey key, ECKey peerKey) throws InvalidCipherTextException {
        if(!isEncrypted) {
            return;
        }
        byte[] sharedSecret = key.getSharedSecret(peerKey);
        text = new String(Decrypt.process(sharedSecret, ciphertext, Utils.longToBytes(messageID), Utils.longToBytes(timestamp)));
        isEncrypted = false;
        ciphertext = null;
    }

    public boolean isEncrypted() {
        return isEncrypted;
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

    public boolean isSent() { return isSent; }

    @Override
    public String toString() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CypherMessage that = (CypherMessage) o;

        if (contactID != that.contactID) return false;
        if (messageID != that.messageID) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = messageID;
        result = 31 * result + (int) (contactID ^ (contactID >>> 32));
        return result;
    }

    @Override
    public int compareTo(Object o) {
        CypherMessage m = (CypherMessage) o;
        if(m.equals(this)) {
            return 0;
        }
        return timestamp <= m.timestamp ? -1 : 1;
    }
}
