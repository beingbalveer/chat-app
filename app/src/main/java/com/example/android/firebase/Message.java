package com.example.android.firebase;


import java.sql.Struct;
import java.util.Date;

public class Message {
    private String fromTo;
    private String messageText;
    private long messageTime;

    public Message() {
    }

    public Message(String fromTo,String from,String messageText) {
        this.fromTo = fromTo;
        this.messageText = from + " " + messageText;
        this.messageTime = new Date().getTime();
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public String getFromTo() {
        return fromTo;
    }

    public void setFromTo(String fromTo) {
        this.fromTo = fromTo;
    }
}

