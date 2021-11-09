package com.example.myongsubway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class BookmarkActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        Toolbar toolbarStation = findViewById(R.id.bookmark_toolbar_station);
        toolbarStation.setTitle("즐겨찾는 역");
        toolbarStation.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbarStation);

        Toolbar toolbarRoute = findViewById(R.id.bookmark_toolbar_route);
        toolbarRoute.setTitle("즐겨찾는 경로");
        toolbarRoute.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbarRoute);
    }

/*
        DocumentReference docRef = db.collection("users").document(User);


        // Create a new user
        list = new ArrayList<String>();
        map = new HashMap<String, ArrayList<String>>();

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Toast.makeText(getApplicationContext(), "DocumentSnapshot data: " + document.getData(), Toast.LENGTH_SHORT).show();
                        System.out.println(document.getData().get("즐겨찾는역"));
                    } else {
                        Toast.makeText(getApplicationContext(), "No such document" + document.getData(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        bookmarkOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.add(getStation());
                map.put("즐겨찾는역", list);

                // Add a new document with a generated ID
                db.collection("users").document(User)
                        .set(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "DocumentSnapshot successfully written!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Error writing document", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    String getDepart(){ return departStation.getText().toString(); }
    void setDepart(String s){ departStation.setText(s); }

    String getDestination(){ return destination.getText().toString(); }
    void setDestination(String s){ destination.setText(s); }

    String getStation(){ return station.getText().toString(); }*/
}