package com.example.myongsubway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

// Login Activity
// 로그인 기능 보유 (회원 가입은 나중에)
// ToDo 1. 로그인 창 UI ----> 틀만 완성
// TODo 2. ID와 PW를 통한 문서명 생성  ----> 회원가입때 생성
// TODo 3. 로그인 이후 해당 데이터를 불러오기  --> 완료
// TODo 4. 리스트와 ID PW String은 모두 북마크를 포함한 여러 액티비티로 넘겨야함.

public class LoginActivity extends AppCompatActivity {

    private Button signIn;
    public EditText email;
    public EditText password;
    public ArrayList<String> bookmarkedStation;

    public static Context context_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signIn = findViewById(R.id.login_button_login);
        email = findViewById(R.id.login_edittext_emailinput);
        password = findViewById(R.id.login_edittext_passwordinput);

        context_login = this;

        //파이어스토어에 접근하기 위한 객체
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CollectionReference 는 파이어스토어의 컬렉션을 참조하는 객체다.
                DocumentReference docRef = db.collection("users").document(getUserData());

                System.out.println(getUserData());
                //get()을 통해서 해당 문서의 정보를 가져온다.
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                System.out.println("DocumentSnapshot data: " + document.getData());
                                bookmarkedStation = new ArrayList<String>();
                                bookmarkedStation = (ArrayList)document.get("즐겨찾는 역");
                            } else {
                                System.out.println("No such document");
                            }
                        } else {
                            System.out.println("get failed with " + task.getException());
                        }
                    }
                });
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });
    }

    String getEmail(){ return email.getText().toString(); }
    String getPassword(){ return password.getText().toString(); }
    String getUserData(){return getEmail() +"_" + getPassword();}
}