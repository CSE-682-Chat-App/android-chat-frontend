package com.example.logic.chatclient;

import android.util.Log;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

public final class Client extends WebSocketClient {

    private static Client _instance;
    private Boolean connected = false;

    public static Client getClient(URI serverURI) {
        if (_instance == null) {
            _instance = new Client(serverURI);
            _instance.connect();
        }
        return _instance;
    }

    private MessageProcessor messageProcessor;
    private MessageQueue messageQueue;

    private Client(URI serverUri , Draft draft ) {
        super( serverUri, draft );
        Init();
    }

    private Client(URI serverURI) {
        super( serverURI );
        Init();
    }

    private Client(URI serverUri, Map<String, String> httpHeaders ) {
        super(serverUri, httpHeaders);
        Init();
    }

    private void Init() {
        messageProcessor = new MessageProcessor(this);
        messageProcessor.queue = new MessageQueue();
        messageProcessor.start();
    }


    @Override
    public void onOpen( ServerHandshake handshakedata ) {
        System.out.println( "opened connection" );

        messageProcessor.ShouldRun = true;
        connected = true;
        // if you plan to refuse connection based on ip or httpfields overload: onWebsocketHandshakeReceivedAsClient
    }

    private Map<String,ArrayList<Consumer<Message>>>  messagePaths = new HashMap<>();

    public void OnPath(String path, Consumer<Message> cb) {
        if (!messagePaths.containsKey(path)) {
            messagePaths.put(path, new ArrayList<Consumer<Message>>());
        }

        ArrayList<Consumer<Message>> list = messagePaths.get(path);
        list.add(cb);

        messagePaths.put(path, list);
    }

    private void Handle(Message message) {
        if (messagePaths.containsKey(message.path)) {
            ArrayList<Consumer<Message>> list = messagePaths.get(message.path);

            list.forEach((cb) -> cb.accept(message));
        }
    }

    public void Send(Message message) {
        messageProcessor.queue.enqueue(message);
    }

    public void DoSend(Message message) {
        send(message.ToJson());
    }

    @Override
    public void onMessage( String message ) {
        Message m = Message.FromJson(message);
        Handle(m);
    }

    @Override
    public void onClose( int code, String reason, boolean remote ) {
        // The codecodes are documented in class org.java_websocket.framing.CloseFrame
        System.out.println( "Connection closed by " + ( remote ? "remote peer" : "us" ) + " Code: " + code + " Reason: " + reason );
        messageProcessor.ShouldRun = false;
        connected = false;;
    }

    @Override
    public void onError( Exception ex ) {
        ex.printStackTrace();
        // if the error is fatal then onClose will be called additionally
    }
}