package com.cyphermessenger.sqlite;

/**
 * Created by Pier DAgostino on 24/03/14.
 */
public class User {

    private String userId;
    private String userName;
    private String password;
    private String publicKey;
    private String privateKey;
    private boolean registered;

    public String getId() {
        return userId;
    }

    public String getUserName() { return userName; }

    public String getPassword() {
        return password;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public boolean checkIfRegistered() { return registered; }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) { this.userName = userName; }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public void hadRegistered() {
        registered = true;
    }

}
