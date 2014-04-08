package com.cyphermessenger.sqlite;

import java.sql.Date;
import java.util.LinkedList;

/**
 * Created by Pier DAgostino on 06/04/14.
 */
public class Messages {

    private int messageId;
    private String messageText;
    private LinkedList<Integer> messageFiles;
    private Date messageDateTime;
    private boolean messageSent;

    public Messages(int messageId, String messageText, LinkedList<Integer> messageFiles, Date messageDateTime, boolean messageSent) {
        this.messageId = messageId;
        this.messageText = messageText;
        this.messageFiles = messageFiles;
        this.messageDateTime = messageDateTime;
        this.messageSent = messageSent;
    }

    // GETTERS
    public int getId() {
        return messageId;
    }

    public String getText() {
        return messageText;
    }

    public Integer getFile(int fileId) {
        return messageFiles.get(fileId);
    }

    public LinkedList<Integer> getFiles() {
        return messageFiles;
    }

    public Date getTime() {
        return messageDateTime;
    }

    public boolean isSent() {
        return messageSent;
    }
}
