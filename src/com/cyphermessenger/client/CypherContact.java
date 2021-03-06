package com.cyphermessenger.client;

import com.cyphermessenger.crypto.ECKey;

/**
 * Created by halfblood on 15/05/14.
 */
public class CypherContact extends CypherUser implements Comparable {

    public static final String ACCEPTED = "accepted";
    public static final String WAITING = "waiting";
    public static final String BLOCKED = "blocked";
    public static final String DENIED = "denied";


    String status;
    Long contactTimestamp;
    Boolean isFirst;

    public CypherContact(String username, Long userID, ECKey key, Long keyTime, String status, Long timestamp, Boolean isFirst) {
        super(username, userID, key, keyTime);
        this.status = status;
        this.contactTimestamp = timestamp;
        this.isFirst = isFirst;
    }

    public CypherContact(String username, Long userID, ECKey key, Long keyTime, String status, Long timestamp) {
        super(username, userID, key, keyTime);
        this.status = status;
        this.contactTimestamp = timestamp;
    }

    public CypherContact(String username, String status) {
        super(username, null, null, null);
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public Boolean isFirst() {
        return isFirst;
    }

    public boolean isAccepted() {
        return status.equals(ACCEPTED);
    }

    public boolean isBlocked() {
        return status.equals(BLOCKED);
    }

    public boolean isDenied() {
        return status.equals(DENIED);
    }

    public boolean isWaiting() {
        return status.equals(WAITING);
    }

    @Override
    public int compareTo(Object o) {
        CypherContact m = (CypherContact) o;
        if(m.equals(this)) {
            return 0;
        }
        if(m.contactTimestamp == null || contactTimestamp == null) {
            return -1;
        }
        return contactTimestamp <= m.contactTimestamp ? -1 : 1;
    }

    public Long getContactTimestamp() {
        return contactTimestamp;
    }
}
