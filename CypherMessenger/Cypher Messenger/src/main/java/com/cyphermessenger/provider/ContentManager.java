package com.cyphermessenger.provider;

import com.cyphermessenger.sqlite.DBManager;

/**
 * Created by Paolo on 04/05/2014.
 */
public class ContentManager {
    private static ContentManager contentManager;
    private final DBManager manager;

    private ContentManager(DBManager manager) {
        this.manager = manager;
    }

    public static  synchronized ContentManager getInstance(DBManager manager) {
        if(contentManager == null) {
            contentManager = new ContentManager(manager);
        }
        return contentManager;
    }

    public void register(String username, String password, String captchaValue) {

    }

    public void login(String username, String password) {

    }

    public void logout() {

    }
}
