package com.example.myongsubway;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;


//작성한 게시글을 수정하는 Activity이다.


public class BoardModifyActivity extends AppCompatActivity implements View.OnClickListener{

    Button closeButton;
    Button confirmButton;
    TextView titleText;
    TextView contentText;
    CardItem item;
    private CustomAppGraph graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_modify);

        graph = (CustomAppGraph)getApplicationContext();
        Intent intent = getIntent();
        item = (CardItem)intent.getSerializableExtra("item");               //객체를 전달받는다.

        closeButton = findViewById(R.id.modify_Button_close);
        confirmButton = findViewById(R.id.modify_Button_confirm);
        titleText= findViewById(R.id.modify_textview_title);
        contentText= findViewById(R.id.modify_textview_content);

        closeButton.setOnClickListener(this);
        confirmButton.setOnClickListener(this);


        titleText.setText(item.getTitle());
        contentText.setText(item.getContent());


    }
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            //다음 과정은 동일 key의 데이터에 덮어씌우는 것이다. = 수정

            case R.id.modify_Button_confirm:
                if(titleText.getText().toString().equals("")){
                    Toast.makeText(this ,"제목을 입력하세요.", Toast.LENGTH_SHORT).show();
                    break;
                }
                if(contentText.getText().toString().equals("")){
                    Toast.makeText(this ,"내용을 입력하세요.", Toast.LENGTH_SHORT).show();
                    break;
                }
                AlertDialog.Builder dlg = new AlertDialog.Builder(this);
                dlg.setTitle("글을 수정하시겠습니까?");
                dlg.setMessage("수정하시려면 예를 눌러주세요");
                dlg.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseDatabase db = FirebaseDatabase.getInstance();
                                DatabaseReference dr = db.getReference("Boards");
                                dr.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull @NotNull DataSnapshot datasnapshot) {

                                        DatabaseReference databaseReference = dr.child(item.getId());                    //데이터들 삽입
                                        databaseReference.child("id").setValue(item.getId());
                                        databaseReference.child("title").setValue(titleText.getText().toString());
                                        databaseReference.child("content").setValue(contentText.getText().toString());
                                        databaseReference.child("writer").setValue(graph.getEmail());
                                        databaseReference.child("time").setValue(item.getTime());

                                    }
                                    @Override
                                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                    }
                                });
                                Toast.makeText(getApplicationContext(), "글을 수정하였습니다.", Toast.LENGTH_SHORT).show();
                                finish();

                            }
                        });
                dlg.setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                dlg.show();
                break;
            case R.id.modify_Button_close:
                finish();
                break;
        }
    }


}