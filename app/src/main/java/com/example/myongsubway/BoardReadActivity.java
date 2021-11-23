package com.example.myongsubway;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

    Toolbar myToolbar;
    LinearLayout commentLayout;
    ArrayList<CommentFragment> commentList = new ArrayList();
    CardItem item;
    private CustomAppGraph graph;
    private InputMethodManager imm;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_read);

        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        graph = (CustomAppGraph)getApplicationContext();

        Intent intent = getIntent();
        item = (CardItem)intent.getSerializableExtra("item");

        myToolbar = findViewById(R.id.board_read_toolbar);

        titleText = findViewById(R.id.board_read_title);
        contentText = findViewById(R.id.board_read_content);
        writerandtimeText = findViewById(R.id.board_read_writerandtime);

        commentText = findViewById(R.id.board_read_commenttext);
        commentLayout = findViewById(R.id.board_read_commentlayout);
        commentbutton = findViewById(R.id.board_read_commentbutton);

        commentbutton.setOnClickListener(this);

        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable arrow = getResources().getDrawable(R.drawable.ic_left,null);
        arrow.setTint(Color.BLACK);
        getSupportActionBar().setTitle("");
        myToolbar.setNavigationIcon(arrow);

    }

    //업데이트된 게시글들을 갱신한다.
    void setRefresh() {
        titleText.setText(item.getTitle());
        contentText.setText(item.getContent());
        writerandtimeText.setText(item.getWriter()+"  |   "+item.getTime());
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.board_read_commentbutton:

                //게시글에 댓글 개수를 늘려준다.

                    if (commentText.getText().toString().equals("")) {
                        Toast.makeText(this, "내용을 입력하세요.", Toast.LENGTH_SHORT).show();
                        break;
                    } //내용을 채워야 댓글을 달 수 있다.
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    dr = db.getReference("Boards").child(item.getId()).child("commentnumber");
                    dr.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            try{
                            int num = Integer.parseInt(snapshot.getValue().toString());
                            num += 1;
                            dr.setValue(Integer.toString(num));}
                            catch(Exception e){
                                e.printStackTrace();
                                Toast.makeText(BoardReadActivity.this, "이미 삭제된 글입니다.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        }
                    });

                    //댓글을 생성한다
                    DatabaseReference dr = db.getReference("Comments");
                    dr.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot datasnapshot) {

                            long num = datasnapshot.getChildrenCount();         //현재 댓글 갯수

                            for (DataSnapshot snapshot : datasnapshot.getChildren()) {        //key가 같은지 거르기
                                if (num <= Integer.parseInt(snapshot.getKey())) {
                                    num = Integer.parseInt(snapshot.getKey()) + 1;
                                }         //이미 같은 key존재시 id를증가
                            }//같은 key가 아니거나 없을 시 반복문 종료

                            DatabaseReference databaseReference = dr.child(Long.toString(num));                    //데이터들 삽입
                            databaseReference.child("id").setValue(Long.toString(num));
                            databaseReference.child("boardid").setValue(item.getId());
                            databaseReference.child("content").setValue(commentText.getText().toString());
                            databaseReference.child("writer").setValue(graph.getEmail());
                            databaseReference.child("time").setValue(new SimpleDateFormat("yy/MM/dd hh:mm").format(new Date(System.currentTimeMillis())));
                            setCommentRefresh();
                            commentText.setText("");
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        }
                    });
                    InputMethodManager keyboard = imm;
                    if (keyboard != null) {
                        keyboard.hideSoftInputFromWindow(this.commentbutton.getWindowToken(), 0);
                    }
                      break;
                }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.read_menu, menu);
       MenuItem deleteItem = (MenuItem)menu.findItem(R.id.board_read_delete);
       MenuItem modifyItem = (MenuItem)menu.findItem(R.id.board_read_modify);
        if(!item.getWriter().equals(graph.getEmail())){deleteItem.setVisible(false); modifyItem.setVisible(false);}
        return true ;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem _item) {
        switch (_item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

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

                                dr = db.getReference();
                                Query filterQuery = dr.child("Comments").orderByChild("boardid").equalTo(item.getId());
                                filterQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            dataSnapshot.getRef().removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
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
                return true;

            case R.id.board_read_modify:
                finish();
                Intent intent = new Intent(this, BoardModifyActivity.class);
                intent.putExtra("item", item);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(_item);
        }

    }


}
