package com.example.logic.chatclient;

import com.google.gson.Gson;

import java.util.HashMap;

public class Message {

    public String path;
    public HashMap<String,String> data;

    public Message() {
        data = new HashMap<String,String>();
    }


    public static Message FromJson(String json) {
        Gson g = new Gson();
        return g.fromJson(json, Message.class);
    }

    public String ToJson() {
        Gson g = new Gson();
        return g.toJson(this);
    }

    public String GetString(String key) {
        return data.get(key);
    }

    public String GetString(String key, String defaultValue) {
        return data.getOrDefault(key, defaultValue);
    }

    public Boolean GetBoolean(String key) {
        return Boolean.parseBoolean(data.get(key));
    }

    public Boolean GetBoolean(String key, Boolean defaultValue) {
        return Boolean.parseBoolean(data.getOrDefault(key, defaultValue.toString()));
    }
}