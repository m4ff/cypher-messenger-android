package com.cyphermessenger.client;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by halfblood on 06/05/14.
 */
public class PullResults {
    private ArrayList<CypherMessage> messages;
    private ArrayList<CypherUser> contacts;
    private Date notifiedUntil;

    PullResults(ArrayList<CypherMessage> messages, ArrayList<CypherUser> contacts, Date notifiedUntil) {
        this.messages = messages;
        this.contacts = contacts;
        this.notifiedUntil = notifiedUntil;
    }

    public ArrayList<CypherMessage> getMessages() {
        return messages;
    }

    public ArrayList<CypherUser> getContacts() {
        return contacts;
    }

    public Date getNotifiedUntil() {
        return notifiedUntil;
    }
}
