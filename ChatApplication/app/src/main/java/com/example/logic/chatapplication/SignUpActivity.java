package com.example.logic.chatapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.logic.chatclient.Client;
import com.example.logic.chatclient.Message;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

public class SignUpActivity extends Activity {
    Button back;
    private EditText name_text, pass_text;
    private Client chatClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        name_text = (EditText) findViewById(R.id.uname_edittext);
        pass_text = (EditText) findViewById(R.id.password_edittext);

        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name_text.getText().toString().matches("") || pass_text.getText().toString().matches("")){
                    Toast.makeText(SignUpActivity.this, "Must enter both username and password:  Back to Login",Toast.LENGTH_SHORT).show();
                    return;
                }

                Message m = new Message();
                m.path = "/signup";
                m.data = new HashMap<>();
                m.data.put("username", name_text.getText().toString());
                m.data.put("password", pass_text.getText().toString());

                chatClient.Send(m);
            }
        });


        try {
            chatClient = Client.getClient(new URI("ws://10.0.0.179:8080/socket"));
        } catch (URISyntaxException e) {
            Log.e("WS", e.getMessage());
        } catch (Exception e) {
            Log.e("WS", e.getMessage());
        }

        chatClient.OnPath("/signup_response", (Message m) -> runOnUiThread(()-> {
                HandleSigninResponse(m);
            }
        ));
    }


    public void HandleSigninResponse(Message m) {
        Toast.makeText(SignUpActivity.this, m.GetString("message"),Toast.LENGTH_SHORT).show();
        if (m.GetBoolean("success")) {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }
}