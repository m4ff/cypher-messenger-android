package com.cyphermessenger.sqlite;

/**
 * Created by Pier DAgostino on 24/03/14.
 */
public class User {

    private String userName;
    private String password;
    private UserKey userKey;
    private int avatarId;

    public User(String userName, String password, UserKey userKey, int avatarId) {
        this.userName = userName;
        this.password = password;
        this.userKey = userKey;
        this.avatarId = avatarId;
    }

    // GETTERS
    public String getUserName() { return userName; }

    public String getPassword() {
        return password;
    }

    public UserKey getUserKey() {
        return userKey;
    }

    public int getAvatar() {
        return avatarId;
    }
}
