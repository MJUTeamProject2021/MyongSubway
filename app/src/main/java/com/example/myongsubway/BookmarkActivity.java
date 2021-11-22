package com.example.myongsubway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import com.google.firebase.firestore.FieldValue;
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
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        DocumentReference docRef = db.collection("subwayData").document(mAuth.getUid());

        Toolbar toolbarRoute = findViewById(R.id.bookmark_toolbar_route);
        setSupportActionBar(toolbarRoute);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기본 타이틀 보여줄지 말지 설정

        Toolbar toolbarStation = findViewById(R.id.bookmark_toolbar_station);
        setSupportActionBar(toolbarStation);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기본 타이틀 보여줄지 말지 설정
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김

        stationList = (ListView)findViewById(R.id.bookmark_listview_station);
        routeList = (ListView)findViewById(R.id.bookmark_listview_route);

        graph = (CustomAppGraph) getApplicationContext();       // 액티비티 간에 공유되는 데이터를 담는 클래스의 객체.

        mContext = this;

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,graph.getBookmarkedStation());
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,graph.getBookmarkedRoute());

        stationList.setAdapter(adapter);
        routeList.setAdapter(adapter2);

        stationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                // 선택된 역이 검색할 역이 맞는지 확인하는 알림창
                AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
                dialog.setMessage("선택한 역이 " + graph.getBookmarkedStation().get(pos) + "이 맞습니까?");
                dialog.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    // 맞다면 검색된 역에 해당 역을 추가하고, 메인으로 넘어가 메소드 호출!!
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String intStr = graph.getBookmarkedStation().get(pos).replaceAll("[^0-9]", "");
                        ((MainActivity)MainActivity.mcontext).launchReport(intStr);
                        finish();
                    }
                });
                dialog.setNegativeButton("아니오", null);
                dialog.show();
            }
        });

        routeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                // 선택된 역이 검색할 역이 맞는지 확인하는 알림창
                AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
                dialog.setMessage("선택한 역이 " + graph.getBookmarkedRoute().get(pos) + "이 맞습니까?");
                dialog.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    // 맞다면 검색된 역에 해당 역을 추가하고, 메인으로 넘어가 메소드 호출!!
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String[] results = graph.getBookmarkedRoute().get(pos).split(" - ");
                        Log.e("출발역", results[0].replaceAll("[^0-9]", ""));
                        Log.e("도착역", results[1].replaceAll("[^0-9]", ""));

                        Intent intent = new Intent(mContext, ShortestPathActivity.class);
                        intent.putExtra("departureStation", results[0].replaceAll("[^0-9]", ""));
                        intent.putExtra("destinationStation", results[1].replaceAll("[^0-9]", ""));
                        startActivity(intent);
                    }
                });
                dialog.setNegativeButton("아니오", null);
                dialog.show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onResume() {
        super.onResume();
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,graph.getBookmarkedRoute());
        routeList.setAdapter(adapter2);
    }
}