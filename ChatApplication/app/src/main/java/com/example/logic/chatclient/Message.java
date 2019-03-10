package com.example.logic.chatclient;

import com.google.gson.Gson;

import java.util.HashMap;

public class Message {

    public String path;
    public HashMap<String,String> data;

    public static Message FromJson(String json) {
        Gson g = new Gson();
        return g.fromJson(json, Message.class);
    }

    public String ToJson() {
        Gson g = new Gson();
        return g.toJson(this);
    }
}
