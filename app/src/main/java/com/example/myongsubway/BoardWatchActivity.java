package com.example.myongsubway;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class BoardWatchActivity extends AppCompatActivity implements BoardAdapter.OnItemClickEventListener,View.OnClickListener {

    FirebaseDatabase db;
    DatabaseReference dr;
    public static BoardAdapter adapter;
    ArrayList<CardItem> dataList = new ArrayList<>();
    RecyclerView recyclerView;
    private FloatingActionButton writeButton;
    private Button watchButton;
    private Button resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_watch);
        resetButton = findViewById(R.id.fragment_boardwatch_reset);
        writeButton = findViewById(R.id.fragment_boardwatch_write);
        watchButton = findViewById(R.id.fragment_boardwatch_close);


        resetButton.setOnClickListener(this);
        writeButton.setOnClickListener(this);
        watchButton.setOnClickListener(this);


        recyclerView = findViewById(R.id.Recycler_border);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        resetBoard();
    }

    //게시글들을 갱신한다.
    public void resetBoard(){
        dataList.clear();
        db = FirebaseDatabase.getInstance();
        dr = db.getReference("Boards");

        dr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot datasnapshot) {
                dataList.clear();
                for(DataSnapshot snapshot : datasnapshot.getChildren()){
                    CardItem item = snapshot.getValue(CardItem.class);
                    dataList.add(item);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        adapter = new BoardAdapter(dataList,this);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onItemClick(View v, int position) {

        final CardItem item = dataList.get(position);
        Intent intent = new Intent(getApplicationContext(), BoardReadActivity.class);
        intent.putExtra("item",item);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch(v.getId()){
            case R.id.fragment_boardwatch_write:
                intent = new Intent(getApplicationContext(), BoardWriteActivity.class);
                startActivity(intent);
                break;
            case R.id.fragment_boardwatch_close:
               finish();
                break;
            case R.id.fragment_boardwatch_reset:
                resetBoard();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        resetBoard();
    }
}
