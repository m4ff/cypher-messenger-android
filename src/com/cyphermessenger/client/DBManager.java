package com.cyphermessenger.client;

import com.cyphermessenger.crypto.ECKey;

import java.util.List;

/**
 * Created by Pier DAgostino on 08/04/14.
 */
public interface DBManager {
    public void setUser(CypherUser user);
    public void setSession(CypherSession session);
    public void insertContact(CypherContact contact);
    public void insertMessage(CypherMessage msg);
    public void insertKey(ECKey key);

    public void setNotifiedUntil(long timestamp);
    public void setMessageSent(CypherMessage msg);
    
    public CypherSession getSession();
    public CypherUser getUser();
    public ECKey getKeyForTime(long time);
    public List<CypherMessage> getMessages(CypherUser contact);
    public List<CypherContact> getContacts();
    
    public void logout();
    public void deleteContact(CypherUser contact);
}
