/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cyphermessenger.client;

/**
 *
 * @author halfblood
 */
public class StatusCode {
    public static final int OK = 1;
    
    public static final int SERVER_ERROR= 2;
    
    // REGISTRATION
    public static final int CAPTCHA_INVALID = 100;
    public static final int USERNAME_TAKEN = 101;
    public static final int USERNAME_NOT_FOUND = 102;
    
    // LOGIN
    public static final int LOGIN_INVALID = 200;
    
    // AUTHENTICATION
    public static final int SESSION_INVALID = 300;
    public static final int SESSION_EXPIRED = 301;
    
    // CONTACT REQUESTS
    public static final int CONTACT_WAITING = 401;
    public static final int CONTACT_BLOCKED = 402;
    public static final int CONTACT_DENIED = 403;
    public static final int CONTACT_NOT_FOUND = 404;

    // MESSAGE
    public static int USER_KEY_OUTDATED = 501;
    public static int CONTACT_KEY_OUTDATED = 502;
    public static int TIMESTAMP_SYNC_ERROR = 503;
}

