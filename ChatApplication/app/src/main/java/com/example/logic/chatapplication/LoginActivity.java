package com.example.logic.chatapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.logic.chatclient.Client;
import com.example.logic.chatclient.Message;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

public class LoginActivity extends Activity {
    Button login, signup;
    private EditText user,pwd;
    TextView txt;

    private Client chatClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = (Button) findViewById(R.id.login);
        signup = (Button) findViewById(R.id.signup);
        user = (EditText) findViewById(R.id.user_email);
        pwd = (EditText) findViewById(R.id.password);
        txt= findViewById(R.id.textView);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
        try {
            chatClient = Client.getClient(new URI("ws://10.0.0.179:8080/socket"));
        } catch (URISyntaxException e) {
            Log.e("WS", e.getMessage());
        } catch (Exception e) {
            Log.e("WS", e.getMessage());
        }


        chatClient.OnPath("/auth_response", (Message m) -> runOnUiThread(()-> {
            HandleAuthChanged(m);
                }
        ));
    }

    public void HandleAuthChanged(Message m) {

        Toast.makeText(LoginActivity.this, m.GetString("message"),Toast.LENGTH_SHORT).show();
        if (m.GetBoolean("success")) {
            Intent intent1 = new Intent(LoginActivity.this, GeneralChat.class);
            startActivity(intent1);
        }

    }

    public void Login(View view) {
        if (user.getText().toString().matches("") || pwd.getText().toString().matches("")) {
            Toast.makeText(LoginActivity.this, "Enter both Email and Password",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            Message m = new Message();
            m.path = "/auth";
            m.data = new HashMap<>();
            m.data.put("username", user.getText().toString());
            m.data.put("password", pwd.getText().toString());

            chatClient.Send(m);
        }

    }


}
