package com.example.logic.chatclient;

import java.util.LinkedList;
import java.util.List;

public class MessageQueue {

    private List queue = new LinkedList<Message>();


    public synchronized void enqueue(Message item)
    {
        if (this.queue.size() == 0) {
            notifyAll();
        }
        this.queue.add(item);
    }

    public synchronized Message dequeue()
            throws InterruptedException
    {
        while (this.queue.size() == 0) {
            wait();
        }

        return (Message)this.queue.remove(0);
    }
}