package com.example.myongsubway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class BookmarkActivity extends AppCompatActivity  {

    private FirebaseAuth mAuth;

    private ListView stationList;
    private ListView routeList;

    private CustomAppGraph graph;                   // 액티비티 간에 공유되는 데이터를 담는 클래스

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        DocumentReference docRef = db.collection("subwayData").document(mAuth.getUid());

        Toolbar toolbarStation = findViewById(R.id.bookmark_toolbar_station);
        toolbarStation.setTitle("즐겨찾는 역");
        toolbarStation.setTitleTextColor(Color.BLACK);
        setSupportActionBar(toolbarStation);

        stationList = (ListView)findViewById(R.id.bookmark_listview_station);

        Toolbar toolbarRoute = findViewById(R.id.bookmark_toolbar_route);
        toolbarRoute.setTitle("즐겨찾는 경로");
        toolbarRoute.setTitleTextColor(Color.BLACK);
        setSupportActionBar(toolbarRoute);

        routeList = (ListView)findViewById(R.id.bookmark_listview_route);

        graph = (CustomAppGraph) getApplicationContext();       // 액티비티 간에 공유되는 데이터를 담는 클래스의 객체.

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,graph.getBookmarkedStation());
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,graph.getBookmarkedRoute());

        stationList.setAdapter(adapter);
        routeList.setAdapter(adapter2);

        //adapter.notifyDataSetChanged();
        //adapter2.notifyDataSetChanged();
    }


    // 즐겨찾기 역이 추가되는 메소드
    public void addBookmarkedStation(String name){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        DocumentReference docRef = db.collection("subwayData").document(mAuth.getUid());

        graph = (CustomAppGraph) getApplicationContext();

        db.collection("subwayData").document(mAuth.getUid()).set(graph.getBookmarkedStation().add(name));
    }
}

/*docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
        });*/