package com.cyphermessenger.sqlite;


/**
 * Created by Pier DAgostino on 08/04/14.
 */
public class Contact {

    private String contactName;
    private int avatarId;

    public Contact(String contactName, int avatarId) {
        this.contactName = contactName;
        this.avatarId = avatarId;
    }

    // GETTERS
    public String getName() {
        return contactName;
    }

    public int getAvatar() {
        return avatarId;
    }


}
