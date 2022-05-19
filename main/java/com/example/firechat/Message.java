package com.example.firechat;

import java.io.Serializable;

public class Message implements Serializable {
    public String message;
    public String senderId;
    public long time;

    public Message() {
    }

    public Message(String message, String senderId, long time) {
        this.message = message;
        this.senderId = senderId;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
