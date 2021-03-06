package com.cyphermessenger.client;

import com.cyphermessenger.crypto.ECKey;

import java.util.List;

/**
 * Created by Pier DAgostino on 08/04/14.
 */
public interface DBManager {
    public void setSession(CypherSession session);
    public void insertContact(CypherContact contact);
    public void insertMessage(CypherMessage msg);
    public void insertKey(CypherUser user, ECKey key);

    public void setLastUpdateTime(long time);
    public long getLastUpdateTime();
    public CypherContact getContactByID(long id);

    public void setMessageSent(CypherMessage msg);
    
    public CypherSession getSession();
    public ECKey getKeyForTime(long time);
    public List<CypherMessage> getMessages(CypherUser contact, int offset, int limit);
    public CypherMessage getLastMessage(CypherUser contact);
    public List<CypherContact> getContacts();
    public boolean messageExists(CypherMessage msg);
    
    public void logout();
    public void deleteContact(CypherUser contact);
}
