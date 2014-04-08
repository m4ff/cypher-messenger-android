package com.cyphermessenger.sqlite;

import com.cyphermessenger.crypto.Key;
/**
 * Created by Pier DAgostino on 08/04/14.
 */
public interface DBManager {

    public void insertUser(User user) throws SQLException;
    public void deleteUser(User user) throws SQLException;
    public void insertContact(Contact contact) throws SQLException;
    public void deleteContact(Contact contact) throws SQLException;
    public void insertKey(User user, Key key) throws SQLException;
    public void insertMessage(User user, Contact contact, Messages msg);
    public void deleteMessage(Messages msg);

}
