package com.example.myongsubway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class LoginActivity extends AppCompatActivity {

    private Button saveChange;
    public EditText ID;
    public EditText Password;

    public static Context context_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        saveChange = findViewById(R.id.button5);
        ID = findViewById(R.id.login_edittext_id);
        Password = findViewById(R.id.login_edittext_password);

        context_login = this;

        saveChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });
    }

    String getID(){ return ID.getText().toString(); }
    String getPassword(){ return Password.getText().toString(); }
}