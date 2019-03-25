package com.example.logic.chatapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.logic.chatclient.Client;
import com.example.logic.chatclient.Message;

import java.util.HashMap;

public class GeneralChat extends Activity {
    private EditText editText;

    private Client chatClient;
    private MessageAdapter messageAdapter;
    private UIUser me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_chat);

        messageAdapter = new MessageAdapter(this);

        ListView listView = (ListView) findViewById(R.id.messages_view);
        listView.setAdapter(messageAdapter);

        editText = (EditText) findViewById(R.id.editText);

        chatClient = Client.getClient();

        chatClient.OnPath("/user_reply", (Message m) -> runOnUiThread(()->{
            HandleUserReply(m);
        }));

        chatClient.OnPath("/message/general", (Message m) -> runOnUiThread(()-> {
            HandleMessage(m);
        }));

        chatClient.OnPath("/error", (Message m) -> runOnUiThread(()->{
            HandleError(m);
        }));

        getUser();
        joinChannel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        leaveChannel();
    }

    protected void getUser() {
        Message m = new Message();
        m.path = "/user";
        chatClient.Send(m);
    }

    protected void joinChannel() {
        Message m = new Message();
        m.path = "/join";
        m.data.put("channel", "general");
        chatClient.Send(m);
    }

    protected void leaveChannel() {
        Message m = new Message();
        m.path = "/leave";
        m.data.put("channel", "general");
        chatClient.Send(m);
    }

    public void HandleUserReply(Message m) {
        me = new UIUser(m.GetString("user"));
    }

    public void HandleError(Message m) {
        if (!m.GetBoolean("authorized", true)) {
            Toast.makeText(GeneralChat.this, m.GetString("message"),Toast.LENGTH_SHORT).show();
            if (m.GetBoolean("success")) {
                Intent intent1 = new Intent(GeneralChat.this, LoginActivity.class);
                startActivity(intent1);
            }
        }
    }

    public void HandleMessage(Message m) {
        Log.i("Message", m.ToJson());

        UIMessage m2 = new UIMessage(m.GetString("message"));

        messageAdapter.add(m2);
    }

    public void sendMessage(View view) {
        Message m = new Message();
        m.path = "/message";
        m.data.put("message", editText.getText().toString());
        m.data.put("channel", "general");

        editText.setText("");

        UIMessage message = new UIMessage("{}");
        message.sender = me;
        message.message = m.GetString("message");
        message.belongsToCurrentUser = true;

        messageAdapter.add(message);

        chatClient.Send(m);
    }
}
