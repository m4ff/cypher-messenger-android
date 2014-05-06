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
public class APIErrorException extends Exception {
    
    private final int statusCode;
    
    public APIErrorException(int statusCode) {
        super("API call returned status " + statusCode);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
