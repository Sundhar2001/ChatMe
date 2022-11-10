package com.example.chatme.Models;

public class Message {

    String uId, message, messageId;
    Long timestamp;


    //1 constructor
    public Message(String uId, String message, Long timestamp) {
        this.uId = uId;
        this.message = message;
        this.timestamp = timestamp;
    }
    //2 constructor for uid and message

    public Message(String uId, String message) {
        this.uId = uId;
        this.message = message;
    }

    public Message() {

    }

    //generating getter and setter


    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}