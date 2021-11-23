package com.example.myongsubway;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

public class BoardWriteActivity extends AppCompatActivity implements View.OnClickListener{

    Button closeButton;
    Button confirmButton;
    TextView titleText;
    TextView contentText;
    CustomAppGraph graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_write);

        graph = (CustomAppGraph)getApplicationContext();
        closeButton = findViewById(R.id.write_Button_close);
        confirmButton = findViewById(R.id.write_Button_confirm);
        titleText= findViewById(R.id.write_textview_title);
        contentText= findViewById(R.id.write_textview_content);

        closeButton.setOnClickListener(this);
        confirmButton.setOnClickListener(this);


    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //게시글을 쓰는 과정이다
            case R.id.write_Button_confirm:
                if(titleText.getText().toString().equals("")){
                    Toast.makeText(this ,"제목을 입력하세요.", Toast.LENGTH_SHORT).show();
                    break;
                }
                if(contentText.getText().toString().equals("")){
                    Toast.makeText(this ,"내용을 입력하세요.", Toast.LENGTH_SHORT).show();
                    break;
                }
                AlertDialog.Builder dlg = new AlertDialog.Builder(this);
                dlg.setTitle("글을 작성하시겠습니까?");
                dlg.setMessage("작성하시려면 예를 눌러주세요");
                dlg.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseDatabase db = FirebaseDatabase.getInstance();
                                DatabaseReference dr = db.getReference("Boards");
                                dr.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull @NotNull DataSnapshot datasnapshot) {

                                        long num = datasnapshot.getChildrenCount();         //현재 게시글 갯수

                                        for(DataSnapshot snapshot : datasnapshot.getChildren()){        //key가 같은지 거르기
                                            if(num <= Integer.parseInt(snapshot.getKey())){num = Integer.parseInt(snapshot.getKey())+1 ;}         //이미 같은 key존재시 1증가
                                        }//같은 key가 아니거나 없을 시 반복문 종료

                                        DatabaseReference databaseReference = dr.child(Long.toString(num));                    //데이터들 삽입
                                        databaseReference.child("id").setValue(Long.toString(num));
                                        databaseReference.child("title").setValue(titleText.getText().toString());
                                        databaseReference.child("content").setValue(contentText.getText().toString());
                                        databaseReference.child("writer").setValue(graph.getEmail());
                                        databaseReference.child("time").setValue(new SimpleDateFormat("yy/MM/dd HH:mm").format(new Date(System.currentTimeMillis())));
                                        databaseReference.child("commentnumber").setValue("0");

                                    }
                                    @Override
                                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                    }
                                });
                                Toast.makeText(getApplicationContext(), "글을 작성하였습니다.", Toast.LENGTH_SHORT).show();
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
            case R.id.write_Button_close:
                finish();
                break;
        }
    }


}