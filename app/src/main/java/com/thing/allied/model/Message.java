package com.thing.allied.model;


import java.util.Date;

public class Message {
    
    private String message;
    
    private Date sentTime;
    
    private String senderUid;

    private int messageType;

    public Message(String message, String senderUid, Date sentTime, int messageType) {
        this.message = message;
        this.senderUid = senderUid;
        this.sentTime = sentTime;
        this.messageType = messageType;
    }


    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getMessage() {
        return this.message;
    }
    
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    
    
    public Date getSentTime() {
        return this.sentTime;
    }
    
    
    public void setSentTime(Date sentTime) {
        this.sentTime = sentTime;
    }
    
    
    
    public String getSenderUid() {
        return this.senderUid;
    }
    
    
    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }
    
    
    
    
}
