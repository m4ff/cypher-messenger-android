package com.cyphermessenger.client;

import com.cyphermessenger.crypto.ECKey;
/**
 * Created by Pier DAgostino on 08/04/14.
 */
public interface DBManager {
    public void setUser(CypherUser user);
    public void setSession(CypherSession session);
    public void insertContact(CypherSession user, CypherUser contact);
    public void insertMessage(CypherSession user, CypherUser contact, CypherMessage msg);
    
    public CypherSession getSession();
    public CypherUser getUser();
    public ECKey getKeyForTime(long time);
    
    public void logout();
    public void deleteContact(CypherSession user, CypherUser contact);
}
