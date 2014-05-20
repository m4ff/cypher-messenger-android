package com.cyphermessenger.client;

import org.spongycastle.crypto.InvalidCipherTextException;

import java.io.IOException;
import java.util.List;

public class ContentManager {
    private static ContentManager contentManager;
    private final DBManager dbManager;
    
    private CypherSession session;

    private ContentManager(DBManager dbManager) {
        this.dbManager = dbManager;
        this.session = dbManager.getSession();
    }

    public static synchronized ContentManager getInstance(DBManager dbManager) {
        if(contentManager == null) {
            contentManager = new ContentManager(dbManager);
        }
        return contentManager;
    }
    
    public boolean isLogged() {
    	return session != null;
    }
    
    public CypherUser getUser() {
    	return dbManager.getUser();
    }
    
    public Captcha requestCaptcha() throws IOException, APIErrorException {
    	return SyncRequest.requestCaptcha();
    }

    public void register(String username, String password, String captchaValue, Captcha captcha) throws IOException, APIErrorException, InvalidCipherTextException {
    	CypherUser user = SyncRequest.registerUser(username, password, captchaValue, captcha);
    	dbManager.setUser(user);
        login(username, password);
    }

    public void login(String username, String password) throws IOException, APIErrorException, InvalidCipherTextException {
    	CypherSession session = SyncRequest.userLogin(username, password);
    	dbManager.setSession(session);
        this.session = session;
    }

    public CypherSession getSession() {
    	return session;
    }

    public void logout() {
    	dbManager.logout();
    }

    public List<CypherMessage> getMessageList(CypherSession session, CypherUser contact) {
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
