package com.cyphermessenger.sqlite;

import com.cyphermessenger.crypto.Key;
/**
 * Created by Pier DAgostino on 08/04/14.
 */
public interface DBManager {
    public void insertUser(User user);
    public void deleteUser(User user);
    public void insertContact(Contact contact);
    public void deleteContact(Contact contact);
    public void insertKey(User user, Key key);
    public void insertMessage(User user, Contact contact, Messages msg);
    public void deleteMessage(Messages msg);
}
