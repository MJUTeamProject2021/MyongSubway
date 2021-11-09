package com.example.myongsubway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
LoginActivity
Firebase firestore Database 이용
로그인을 통해 해당 컬렉션(users) 내부의 문서를 탐색해 탐색된 내용을 배열에 담아 사용됩니다.
회원가입(SignUpFragment)을 통해 문서 ID가 생성됩니다.
데이터 추가는 데이터 추가 버튼이 있는 다른 액티비티에서 진행될 예정이며,
ToDO: 데이터 전시 및 삭제는 BookmarkActivity에서 진행될 예정입니다.
컬렉션 이름은 "users" (언제든지 수정 가능)
문서 ID 형식은 email_password 형식 (형식 처음부터 끝까지 일치해야 데이터 접근 가능)
문서 내부 데이터는 해시 맵으로 이루어져 있으며, key는 String, value는 ArrayList로 이루어져 있음.

별도로 firebase 데이터를 확인해보고 싶으시면 wndtjq0510@naver.com으로 메일 주시면 권한 부여해드리겠습니다.
MyongSubway 데이터베이스 테스트 모드로 만들어졌으며, 2021년 12월 31일까지 데이터에 접근 할 수 있습니다.
 */

public class LoginActivity extends AppCompatActivity {

    private Button signIn;                          // 로그인을 진행하는 버튼
    private Button withoutSignIn;                   // 로그인 없이 진행하는 버튼
    public EditText email;
    public EditText password;
    public TextView signUp;                         // 회원가입 프래그먼트로 이동
    public ArrayList<String> bookmarkedStation;     // "즐겨찾는 역" 데이터를 저장
    public ArrayList<String> bookmarkedRoute;       // "즐겨찾는 경로" 데이터를 저장

    private FragmentManager fragmentManager;        // 프래그먼트를 다루는 매니저
    private SignUpFragment SignUpFragment;
    private FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.Theme_MyongSubway);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fragmentManager = getSupportFragmentManager();
        SignUpFragment = new SignUpFragment();

        signIn = findViewById(R.id.login_button_login);
        withoutSignIn = findViewById(R.id.login_button_withoutlogin);
        email = findViewById(R.id.login_edittext_emailinput);
        password = findViewById(R.id.login_edittext_passwordinput);
        signUp = findViewById(R.id.login_textview_signup);

        // 파이어스토어에 접근하기 위한 객체
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        // 로그인 버튼 리스너
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // docRef는 파이어스토어의 컬렉션을 참조하는 객체
                // users 컬렉션을 참조
                DocumentReference docRef = db.collection("users").document(getUserData());

                //get()을 통해서 해당 문서의 정보를 가져온다.
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        //작업이 성공적으로 마쳤을때 document에 결과를 담고, 배열 생성
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            bookmarkedStation = new ArrayList<String>();
                            bookmarkedRoute = new ArrayList<String>();
                            // 내부에 문서가 있다면 배열에 데이터 담기
                            if (document.exists()) {
                                System.out.println("DocumentSnapshot data: " + document.getData());
                                bookmarkedStation = (ArrayList) document.get("즐겨찾는 역");
                                bookmarkedRoute = (ArrayList) document.get("즐겨찾는 경로");
                                System.out.println(bookmarkedStation);
                                System.out.println(bookmarkedRoute);
                            } else {
                                // 없으면 빈 배열 채로 이동
                                System.out.println("No such document");
                            }
                        } else {
                            //그렇지 않을때
                            System.out.println("get failed with " + task.getException());
                        }
                    }
                });
                // 메인 액티비티로 이동
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        // 로그인 없이 앱을 이용할 경우의 해당 버튼 리스너
        withoutSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 대화 상자 경고문, 개발 중에는 주석처리로 닫아 놓겠음.
                /*
                AlertDialog.Builder dlg = new AlertDialog.Builder(LoginActivity.this);
                dlg.setMessage("로그인 없이 시작하면 특정 기능들을 사용할 수 없습니다. 진행하시겠습니까?");
                dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //토스트 메시지
                        Toast.makeText(LoginActivity.this, "메인화면으로 넘어갑니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                });
                dlg.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"아니오를 선택했습니다.",Toast.LENGTH_SHORT).show();
                    }
                });
                dlg.show();*/
            }
        });

        // 회원가입 프래그먼트로 넘어가는 리스너
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transaction = fragmentManager.beginTransaction();
                getSupportFragmentManager().beginTransaction().replace(R.id.login_constraintlayout_login, SignUpFragment).commit();
            }
        });
    }

    String getEmail(){ return email.getText().toString(); }
    String getPassword(){ return password.getText().toString(); }
    String getUserData(){return getEmail() +"_" + getPassword();}
}