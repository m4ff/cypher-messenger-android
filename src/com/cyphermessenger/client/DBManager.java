package com.cyphermessenger.client;

import com.cyphermessenger.crypto.ECKey;

import java.util.List;

/**
 * Created by Pier DAgostino on 08/04/14.
 */
public interface DBManager {
    public void addUser(CypherUser user);
    public void setSession(CypherSession session);
    public void insertContact(CypherUser user, CypherContact contact);
    public void insertMessage(CypherUser user, CypherMessage msg);
    public void insertKey(CypherUser user, ECKey key);

    public void setNotifiedUntil(CypherUser user, long timestamp);
    
    public CypherSession getSession();
    public CypherUser getUser();
    public ECKey getKeyForTime(long time);
    public List<CypherMessage> getMessages(CypherUser user, CypherUser contact);
    public List<CypherContact> getContacts(CypherUser user);
    
    public void logout(CypherUser user, boolean deleteData);
    public void deleteContact(CypherUser user, CypherUser contact);
}
