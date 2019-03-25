package com.example.logic.chatapplication;

import android.icu.text.SimpleDateFormat;
import android.util.Log;

import com.google.gson.Gson;

import java.sql.Timestamp;

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

    public String getMessageTime() {
        Timestamp time = Timestamp.valueOf(timestamp);
        return new SimpleDateFormat("MM-dd-yyyy hh:mm:ss").format(time);
    }

    public boolean isBelongsToCurrentUser() {
        return belongsToCurrentUser;
    }
}
