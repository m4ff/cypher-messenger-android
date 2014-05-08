package com.cyphermessenger.sqlite;

import java.util.Date;
import java.util.LinkedList;

/**
 * Created by Pier DAgostino on 06/04/14.
 */
public class Messages {

    private int messageId;
    private String messageText;
    //private LinkedList<Integer> messageFiles;
    private int messageDateTime;
    private boolean messageSent;
    private boolean isUserSender;

    public Messages(int messageId, String messageText, int messageDateTime, boolean messageSent, boolean isUserSender) {
        this.messageId = messageId;
        this.messageText = messageText;
        this.messageDateTime = messageDateTime;
        this.messageSent = messageSent;
        this.isUserSender = isUserSender;

    }



    // GETTERS
    public int getId() {
        return messageId;
    }

    public String getText() {
        return messageText;
    }

   /* public Integer getFile(int fileId) {
        return messageFiles.get(fileId);
    }

    public LinkedList<Integer> getFiles() {
        return messageFiles;
    }*/

    public int getTime() {
        return messageDateTime;
    }

    public boolean isSent() {
        return messageSent;
    }

    public boolean isUserSender(){ return isUserSender; };
}
