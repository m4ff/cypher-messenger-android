package com.cyphermessenger.provider;

import com.cyphermessenger.client.CypherSession;
import com.cyphermessenger.client.CypherUser;
import com.cyphermessenger.client.Message;
import com.cyphermessenger.sqlite.DBManager;

import java.util.List;

/**
 * Created by Paolo on 04/05/2014.
 */
public class ContentManager {
    private static ContentManager contentManager;
    private final DBManager dbManager;

    private ContentManager(DBManager dbManager) {
        this.dbManager = dbManager;
    }

    public static synchronized ContentManager getInstance(DBManager dbManager) {
        if(contentManager == null) {
            contentManager = new ContentManager(dbManager);
        }
        return contentManager;
    }

    public CypherSession register(String username, String password, String captchaValue) {
        return null;
    }

    public CypherSession login(String username, String password) {
        return null;
    }

    public CypherSession getStoredSession() {
        return null;
    }

    public void invalidateSession(CypherSession session) {

    }

    public List<Message> getMessageList(CypherSession session, CypherUser contact) {
        return null;
    }

    public void sendMessage(CypherSession session, CypherUser contact) {

    }

    public List<String> matchUsername(CypherSession session, String query) {
        return null;
    }

    public void inviteContact(CypherSession session, String username) {

    }

    public void blockContact(CypherSession session, CypherUser contact) {

    }

    public void unblockContact(CypherSession session, CypherUser contact) {

    }

    public List<CypherUser> getContactList(CypherSession session) {
        return null;
    }
}
