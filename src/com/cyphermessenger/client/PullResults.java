package com.cyphermessenger.client;

import com.cyphermessenger.crypto.ECKey;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by halfblood on 06/05/14.
 */
public class PullResults {
    private ArrayList<CypherMessage> messages;
    private ArrayList<CypherContact> contacts;
    private ArrayList<ECKey> keys;
    private long notifiedUntil;

    PullResults(ArrayList<CypherMessage> messages, ArrayList<CypherContact> contacts, ArrayList<ECKey> keys, long notifiedUntil) {
        this.messages = messages;
        this.contacts = contacts;
        this.keys = keys;
        this.notifiedUntil = notifiedUntil;
    }

    public ArrayList<CypherMessage> getMessages() {
        return messages;
    }

    public ArrayList<CypherContact> getContacts() {
        return contacts;
    }

    public ArrayList<ECKey> getKeys() {
        return keys;
    }

    public long getNotifiedUntil() {
        return notifiedUntil;
    }
}
