package com.cyphermessenger.client;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by halfblood on 06/05/14.
 */
public class PullResults {
    private ArrayList<Message> messages;
    private ArrayList<CypherUser> contacts;
    private Date notifiedUntil;

    PullResults(ArrayList<Message> messages, ArrayList<CypherUser> contacts, Date notifiedUntil) {
        this.messages = messages;
        this.contacts = contacts;
        this.notifiedUntil = notifiedUntil;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public ArrayList<CypherUser> getContacts() {
        return contacts;
    }

    public Date getNotifiedUntil() {
        return notifiedUntil;
    }
}
