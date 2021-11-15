package com.example.myongsubway;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


//본 fragment는 댓글 그 자체를 나타내는 fragment이다.

public class CommentFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    FirebaseDatabase db;
    DatabaseReference dr;

    private String content;
    private String writer;
    private String time;
    private String boardid;
    private String id;


    private TextView commentText;
    private TextView writerText;
    private TextView timeText;
    private Button confirmButton;

    private CustomAppGraph graph;

    public CommentFragment(String _writer,String _content,String _time,String _boardid,String _id) {
        writer = _writer; content = _content; time=_time; boardid=_boardid; id = _id;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_comment, container, false);

        graph = (CustomAppGraph)getActivity().getApplicationContext();
        commentText = v.findViewById(R.id.fragment_comment_content);
        writerText = v.findViewById(R.id.fragment_comment_writer);
        timeText = v.findViewById(R.id.fragment_comment_time);
        confirmButton = v.findViewById(R.id.fragment_comment_button);

        confirmButton.setOnClickListener(this);

        commentText.setText(content);
        writerText.setText(writer);
        timeText.setText(time);
        return v;
    }

    public String getContent() {
        return content;
    }

    public String getWriter() {
        return writer;
    }

    public String getTime() {
        return time;
    }

    public String getBoardid() {
        return boardid;
    }


    public String getCommenttId() {
        return id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setBoardid(String boardid) {
        this.boardid = boardid;
    }

    public void setId(String id) {
        this.id = id;
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.fragment_comment_button:
                AlertDialog.Builder dlg = new AlertDialog.Builder(getContext());
                dlg.setTitle("정말로 댓글을 지우시겠습니까?");
                dlg.setMessage("지우시려면 예를 눌러주세요");
                dlg.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if(!getWriter().substring(5).equals(graph.getEmail())){
                                    Toast.makeText(getContext(), "본인의 댓글만 지울 수 있습니다.", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    db = FirebaseDatabase.getInstance();                                //db에서 댓글을 지운다.
                                    dr = db.getReference("Comments").child(getCommenttId());
                                    dr.removeValue();
                                }
                            }
                        });
                dlg.setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                dlg.show();
                break;
        }
    }



}