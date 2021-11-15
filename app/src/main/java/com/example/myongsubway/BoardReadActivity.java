package com.example.myongsubway;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//본 Activity는 게시글을 읽는 Activity이다
public class BoardReadActivity extends AppCompatActivity implements View.OnClickListener{


    FirebaseDatabase db;
    DatabaseReference dr;

    TextView titleText;
    TextView contentText;
    TextView writerandtimeText;
    TextView commentText;
    Button commentbutton;
    Button closeButton;
    Button deleteButton;
    Button modifyButton;
    LinearLayout commentLayout;
    ArrayList<CommentFragment> commentList = new ArrayList();
    CardItem item;
    private CustomAppGraph graph;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_read);

        graph = (CustomAppGraph)getApplicationContext();
        Intent intent = getIntent();
        item = (CardItem)intent.getSerializableExtra("item");

        titleText = findViewById(R.id.board_read_title);
        contentText = findViewById(R.id.board_read_content);
        writerandtimeText = findViewById(R.id.board_read_writerandtime);
        commentText = findViewById(R.id.board_read_commenttext);
        commentLayout = findViewById(R.id.board_read_commentlayout);

        closeButton = findViewById(R.id.board_read_close);
        modifyButton = findViewById(R.id.board_read_modify);
        deleteButton = findViewById(R.id.board_read_delete);
        commentbutton = findViewById(R.id.board_read_commentbutton);
        modifyButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
        closeButton.setOnClickListener(this);
        commentbutton.setOnClickListener(this);


    }

    //업데이트된 게시글들을 갱신한다.
    void setRefresh() {
        titleText.setText(item.getTitle());
        contentText.setText(item.getContent());
        writerandtimeText.setText(item.getWriter()+"     "+item.getTime());
        if(!item.getWriter().substring(5).equals(graph.getEmail())){deleteButton.setVisibility(View.INVISIBLE); modifyButton.setVisibility(View.INVISIBLE);}
    }

    //업데이트된 댓글들을 갱신한다.
    void setCommentRefresh(){
        db = FirebaseDatabase.getInstance();
        dr = db.getReference();

        Query filterQuery = dr.child("Comments").orderByChild("boardid").equalTo(item.getId());
        filterQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot datasnapshot) {
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();

                for(int i=0;i<commentList.size();i++){                      //트랜직션에 현재 담겨있는 프래그먼트들을 다 지우고
                    fragmentTransaction.remove(commentList.get(i));
                }
                commentList.clear();                                        //프래그먼트 리스트의 프래그먼트들도 다지운다.

                for (DataSnapshot snapshot : datasnapshot.getChildren()) {    //그 후 Firebase에서 데이터들을 불러와 프래그먼트를 생성하고 리스트에 넣는다.

                    String _writer = (String) snapshot.child("writer").getValue();
                    String _content = (String) snapshot.child("content").getValue();
                    String _time = (String) snapshot.child("time").getValue();
                    String _boardid = (String) snapshot.child("boardid").getValue();
                    String _id = (String) snapshot.child("id").getValue();

                    CommentFragment commentFragment = new CommentFragment(_writer,_content,_time,_boardid,_id);
                    commentList.add(commentFragment);
                }

                for(int i=0;i<commentList.size();i++){                       //리스트 안의 데이터들을 트랜직션에 추가한다.
                    fragmentTransaction.add(R.id.board_read_commentlayout,commentList.get(i));
                }

                fragmentTransaction.commitAllowingStateLoss();              //commit으로 하려했으나 오류로인해 commitAllowingStateLoss사용 (구글검색해서 찾음 원인모름)
            }


            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setRefresh();
        setCommentRefresh();
        System.out.println("재실행, Refresh");
    }

    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager;
        switch (v.getId()) {

            case R.id.board_read_close:
                finish();
                break;

            case R.id.board_read_delete:
                AlertDialog.Builder dlg = new AlertDialog.Builder(this);
                dlg.setTitle("글을 삭제하시겠습니까?");
                dlg.setMessage("삭제하시려면 예를 눌러주세요");
                dlg.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {                //글을 삭제한다.

                                db = FirebaseDatabase.getInstance();
                                dr = db.getReference("Boards").child(item.getId());
                                dr.removeValue();
                                finish();
                                Toast.makeText(getApplicationContext(), "글을 삭제하였습니다.", Toast.LENGTH_SHORT).show();

                                dr = db.getReference();
                                Query filterQuery = dr.child("Comments").orderByChild("boardid").equalTo(item.getId());
                                filterQuery.addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                        dataSnapshot.getRef().setValue(null);
                                    }

                                    @Override
                                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                    }

                                    @Override
                                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                                    }

                                    @Override
                                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                finish();
                                Toast.makeText(getApplicationContext(), "글을 삭제하였습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                dlg.setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                dlg.show();
                break;

            case R.id.board_read_modify:                            //글을 수정한다.
                finish();
                Intent intent = new Intent(this, BoardModifyActivity.class);
                intent.putExtra("item", item);
                startActivity(intent);
                break;


                //댓글을 다는 과정

            case R.id.board_read_commentbutton:
                if(commentText.getText().toString().equals("")){
                    Toast.makeText(this, "내용을 입력하세요.", Toast.LENGTH_SHORT).show();break;} //내용을 채워야 댓글을 달 수 있다.
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference dr = db.getReference("Comments");
                dr.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot datasnapshot) {

                        long num = datasnapshot.getChildrenCount();         //현재 댓글 갯수

                        for(DataSnapshot snapshot : datasnapshot.getChildren()){        //key가 같은지 거르기
                            if(num <= Integer.parseInt(snapshot.getKey())){num = Integer.parseInt(snapshot.getKey())+1;}         //이미 같은 key존재시 id를증가
                        }//같은 key가 아니거나 없을 시 반복문 종료

                        DatabaseReference databaseReference = dr.child(Long.toString(num));                    //데이터들 삽입
                        databaseReference.child("id").setValue(Long.toString(num));
                        databaseReference.child("boardid").setValue(item.getId());
                        databaseReference.child("content").setValue(commentText.getText().toString());
                        databaseReference.child("writer").setValue("작성자: "+graph.getEmail());
                        databaseReference.child("time").setValue("작성시간: "+new SimpleDateFormat("yyyy-MM-dd hh:mm").format(new Date(System.currentTimeMillis())));
                        setCommentRefresh();
                    }
                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
                break;
        }
    }
}
