package com.cyphermessenger.client;

/**
 * Created by paolo on 22/05/14.
 */
public interface APICallListener {
    public void onSuccess();
    public void onError(int statusCode);
}
