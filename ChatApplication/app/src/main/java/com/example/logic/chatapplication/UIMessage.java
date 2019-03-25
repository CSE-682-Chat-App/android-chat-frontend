package com.example.logic.chatapplication;

import android.util.Log;

import com.google.gson.Gson;

public class UIMessage {
    public String message;
    public UIUser sender;
    public String timestamp;

    public boolean belongsToCurrentUser; // is this message sent by us?

    public UIMessage() {

    }

    public UIMessage(String messageData) {
        parseData(messageData);
    }

    public UIMessage(String messageData, boolean belongsToCurrentUser) {
        parseData(messageData);
        this.belongsToCurrentUser = belongsToCurrentUser;
    }

    public void parseData(String messageData) {
        Gson g = new Gson();
        UIMessage m = g.fromJson(messageData, UIMessage.class);

        this.message = m.message;
        this.sender = m.sender;
        this.timestamp = m.timestamp;
    }

    public String getText() {
        return message;
    }

    public UIUser getSender() {
        return sender;
    }

    public boolean isBelongsToCurrentUser() {
        return belongsToCurrentUser;
    }
}
