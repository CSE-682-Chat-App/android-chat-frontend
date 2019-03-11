package com.example.logic.chatapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity {
    Button back;
    private EditText name_text, pass_text;


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
                }
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }
}
