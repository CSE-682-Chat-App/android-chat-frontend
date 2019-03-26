package com.example.logic.chatapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.logic.chatclient.Client;
import com.example.logic.chatclient.Message;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

public class PrivateChat extends Activity  implements NavigationView.OnNavigationItemSelectedListener{
    private EditText editText;

    private Client chatClient;
    private MessageAdapter messageAdapter;
    private UIUser me;
    private UIUser other;
    private Menu userList;

    private HashMap<String,MenuItem> menuItems = new HashMap<String,MenuItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_chat);

        me = new UIUser(getIntent().getStringExtra("Me"));
        other = new UIUser(getIntent().getStringExtra("User"));

        messageAdapter = new MessageAdapter(this);

        ListView listView = (ListView) findViewById(R.id.messages_view);
        listView.setAdapter(messageAdapter);

        editText = (EditText) findViewById(R.id.editText);

        chatClient = Client.getClient();

        chatClient.OnPath("/user_response", (Message m) -> runOnUiThread(()->{
            HandleUserReply(m);
        }));

        chatClient.OnPath("/joined/"+getChannel(), (Message m) -> runOnUiThread(()->{
            HandleChannelJoined(m);
        }));


        chatClient.OnPath("/message/"+getChannel(), (Message m) -> runOnUiThread(()-> {
            HandleMessage(m);
        }));

        chatClient.OnPath("/error", (Message m) -> runOnUiThread(()->{
            HandleError(m);
        }));

        chatClient.OnPath("/users", (Message m) -> runOnUiThread(()->{
            HandleUsers(m);
        }));

        initNavigation();

        getUsers();
    }

    @Override
    public boolean onNavigationItemSelected( MenuItem item) {
        // Handle navigation view item clicks here.

        switch (item.getItemId()) {
            case R.id.general_chat:
                finish();
        }
        return true;
    }

    protected String getChannel() {
        return me.uuid + "-" + other.uuid;
    }

    protected void getUsers() {
        Message m = new Message();
        m.path = "/users";
        chatClient.Send(m);
    }

    protected void initNavigation() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();
        userList = menu.addSubMenu("Users");
    }

    public void HandleUsers(Message m) {
        Gson g = new Gson();
        ArrayList<UIUser> users = g.fromJson(m.GetString("users"),new TypeToken<ArrayList<UIUser>>() {}.getType());


        users.forEach((user)->{
            if (!menuItems.containsKey(user.uuid)) {
                MenuItem item = userList.add(user.name);
                item.setOnMenuItemClickListener((MenuItem i) -> HandleItemClicked(i, user));
                menuItems.put(user.uuid, item);
            }
        });

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.invalidate();

    }

    protected boolean HandleItemClicked(MenuItem i, UIUser u) {
        Intent intent = new Intent(this, PrivateChat.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        intent.putExtra("Me", me.toString());
        intent.putExtra("User", u.toString());
        startActivity(intent);
        return true;
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
        m.data.put("channel", ""+getChannel());
        chatClient.Send(m);
    }

    protected void leaveChannel() {
        Message m = new Message();
        m.path = "/leave";
        m.data.put("channel", ""+getChannel());
        chatClient.Send(m);
    }

    public void HandleUserReply(Message m) {
        me = new UIUser(m.GetString("user"));
        messageAdapter.setUser(me);

        Log.i("WS", "HERE");
        joinChannel();
    }

    public void HandleChannelJoined(Message m) {
        Gson g = new Gson();
        ArrayList<UIMessage> messages = g.fromJson(m.GetString("messages"),new TypeToken<ArrayList<UIMessage>>() {}.getType());

        messages.forEach((message)->messageAdapter.add(message));
    }

    public void HandleError(Message m) {
        if (!m.GetBoolean("authorized", true)) {
            Toast.makeText(PrivateChat.this, m.GetString("message"),Toast.LENGTH_SHORT).show();
            if (m.GetBoolean("success")) {
                Intent intent1 = new Intent(PrivateChat.this, LoginActivity.class);
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
        m.data.put("channel", ""+getChannel());

        editText.setText("");

        UIMessage message = new UIMessage();
        message.sender = me;
        message.message = m.GetString("message");
        message.timestamp = new Timestamp(System.currentTimeMillis()).toString();

        messageAdapter.add(message);

        chatClient.Send(m);
    }
}
