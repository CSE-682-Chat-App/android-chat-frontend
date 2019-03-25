package com.example.logic.chatapplication;

import android.util.Log;

import com.google.gson.Gson;

public class UIUser {
    public String name;
    public String uuid;

    public String getName() {
        return name;
    }
    public UIUser() {}

    public UIUser(String userData) {
        parseData(userData);
    }

    public void parseData(String messageData) {
        Gson g = new Gson();
        UIUser u = g.fromJson(messageData, UIUser.class);
        this.name = u.name;
        this.uuid = u.uuid;
    }

    public boolean isSystem() {
        return this.uuid.equals("0");
    }
}
