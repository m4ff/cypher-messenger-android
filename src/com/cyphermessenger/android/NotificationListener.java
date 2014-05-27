package com.cyphermessenger.android;

import com.cyphermessenger.client.CypherContact;
import com.cyphermessenger.client.CypherMessage;

import java.util.HashMap;
import java.util.List;

/**
 * Created by paolo on 24/05/14.
 */
public interface NotificationListener {

    public void onNewMessages(HashMap<Long, List<CypherMessage>> message);
    public void onNewContacts(List<CypherContact> contacts);
    public void onNewKeys();

}
