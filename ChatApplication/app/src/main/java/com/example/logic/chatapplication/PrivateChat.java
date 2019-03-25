package com.example.logic.chatapplication;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.logic.chatclient.Client;
import com.example.logic.chatclient.Message;

public class PrivateChat extends AppCompatActivity {
    private EditText editText;
    private Client chatClient;
    private MessageAdapter messageAdapter;
    private UIUser me;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_chat);

        messageAdapter = new MessageAdapter(this);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

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
            Toast.makeText(PrivateChat.this, m.GetString("message"),Toast.LENGTH_SHORT).show();
            if (m.GetBoolean("success")) {
                Intent intent1 = new Intent(PrivateChat.this, GeneralChat.class);
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
