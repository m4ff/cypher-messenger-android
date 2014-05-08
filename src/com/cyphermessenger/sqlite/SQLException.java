package com.cyphermessenger.sqlite;

/**
 * Created by Pier DAgostino on 08/04/14.
 */
public class SQLException extends Exception {

    public SQLException(String msg) {
        super(msg);
    }

    public SQLException(String msg, Throwable th) {
        super(msg,th);
    }

    public SQLException(Throwable th) {
        super(th);
    }
}
