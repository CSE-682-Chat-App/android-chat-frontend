package com.example.logic.chatclient;

import android.util.Log;

import android.util.Log;

import android.util.Log;

class MessageProcessor extends Thread {
    public boolean ShouldRun = false;
    public MessageQueue queue;
    protected Client client;

    public MessageProcessor(Client c) {
        client = c;
    }

    public void run() {
        try {
            while (true) {
                if (ShouldRun) {
                    Message m = queue.dequeue();
                    client.DoSend(m);
                } else {
                    Thread.sleep(100);
                }
            }
        } catch (InterruptedException e) {
            Log.e("Processing Message", e.getMessage());
        }
    }
}