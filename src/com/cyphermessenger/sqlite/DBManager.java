package com.cyphermessenger.sqlite;

import com.cyphermessenger.crypto.ECKey;
/**
 * Created by Pier DAgostino on 08/04/14.
 */
public interface DBManager {
    public void insertUser(User user);
    public void insertContact(User user, Contact contact);
    public void deleteContact(User user, Contact contact);
    public void insertKey(User user, ECKey key);
    public void insertMessage(User user, Contact contact, Messages msg);
    public void deleteMessage(Messages msg);
}
