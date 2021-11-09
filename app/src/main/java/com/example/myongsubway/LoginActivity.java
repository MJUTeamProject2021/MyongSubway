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

public class LoginActivity extends AppCompatActivity {

    private Button signIn;
    private Button withoutSignIn;
    public EditText email;
    public EditText password;
    public TextView signUp;
    public ArrayList<String> bookmarkedStation;

    private FragmentManager fragmentManager;
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

        //파이어스토어에 접근하기 위한 객체
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CollectionReference 는 파이어스토어의 컬렉션을 참조하는 객체다.
                DocumentReference docRef = db.collection("users").document(getUserData());

                //get()을 통해서 해당 문서의 정보를 가져온다.
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                System.out.println("DocumentSnapshot data: " + document.getData());
                                bookmarkedStation = new ArrayList<String>();
                                bookmarkedStation = (ArrayList) document.get("즐겨찾는 역");
                                System.out.println(bookmarkedStation);
                            } else {
                                System.out.println("No such document");
                            }
                        } else {
                            System.out.println("get failed with " + task.getException());
                        }
                    }
                });
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        withoutSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                dlg.show();
            }
        });

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