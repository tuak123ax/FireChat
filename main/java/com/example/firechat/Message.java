package com.example.firechat;

import java.io.Serializable;

public class Message implements Serializable {
    public String message;
    public String senderId;
    public long time;
    public String type;

    public Message() {
    }

    public Message(String message, String senderId, long time,String type) {
        this.message = message;
        this.senderId = senderId;
        this.time = time;
        this.type=type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
