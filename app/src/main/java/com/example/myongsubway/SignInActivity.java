package com.example.myongsubway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * SigninActivity
 * Firebase firestore Database 이용
 * 회원가입(SignUpFragment)을 통해 문서 UID가 생성됩니다.
 * UID를 통해 컬렉션 내부 문서를 생성합니다.
 * 컬렉션 이름은 "subwayData" (언제든지 수정 가능)
 * 문서 내부 데이터는 해시 맵으로 이루어져 있으며, key는 String, value는 ArrayList로 이루어져 있음.

 * 별도로 firebase 데이터를 확인해보고 싶으시면 wndtjq0510@naver.com으로 메일 주시면 권한 부여해드리겠습니다.
 * MyongSubway 데이터베이스는 테스트 모드로 만들어졌으며, 2021년 12월 31일까지 데이터에 접근 할 수 있습니다.
 */

public class SignInActivity extends AppCompatActivity {

    private Button signIn;                          // 로그인을 진행하는 버튼
    private Button withoutSignIn;                   // 로그인 없이 진행하는 버튼
    public EditText email;
    public EditText password;
    public TextView signUp;                         // 회원가입 프래그먼트로 이동
    public ArrayList<String> bookmarkedStation;     // "즐겨찾는 역" 데이터를 저장
    public ArrayList<String> bookmarkedRoute;       // "즐겨찾는 경로" 데이터를 저장
    public Map<String, Object> bookmarkedMap;       // 유저 즐겨찾기 정보 저장

    private FragmentManager fragmentManager;        // 프래그먼트를 다루는 매니저
    private SignUpFragment SignUpFragment;
    private FragmentTransaction transaction;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.Theme_MyongSubway);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // 이하윤 ... 어플 강제종료 시 알람 등록해제를 위함
        startService(new Intent(this, ForcedTerminationService.class));

        fragmentManager = getSupportFragmentManager();
        SignUpFragment = new SignUpFragment();

        signIn = findViewById(R.id.signin_button_signin);
        withoutSignIn = findViewById(R.id.signin_button_withoutsignin);
        email = findViewById(R.id.signin_edittext_emailinput);
        password = findViewById(R.id.signin_edittext_passwordinput);
        signUp = findViewById(R.id.signin_textview_signup);

        // 파이어스토어에 접근하기 위한 객체
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        loadSearchData();

        // 로그인 버튼 리스너
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getEmail().isEmpty()){
                    email.setError("이메일을 입력해주세요");
                    email.requestFocus();
                    return;
                }

                if(getPassword().isEmpty()){
                    password.setError("비밀번호를 입력해주세요");
                    password.requestFocus();
                    return;
                }
                mAuth.signInWithEmailAndPassword(getEmail(), getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // users 컬렉션을 참조
                            DocumentReference docRef = db.collection("subwayData").document(mAuth.getUid());

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
                                            ((CustomAppGraph) getApplicationContext()).setAccount(getEmail(), getPassword(), bookmarkedStation, bookmarkedRoute);
                                            saveSearchData();
                                        } else {
                                            // 없으면 빈 배열 채로 이동
                                            System.out.println("No such document");
                                        }
                                        // 메인 액티비티로 이동
                                        Toast.makeText(SignInActivity.this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        //그렇지 않을때
                                        System.out.println("get failed with " + task.getException());
                                    }
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignInActivity.this, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                            password.setText(null);
                        }
                    }
                });
            }
        });

        // 로그인 없이 앱을 이용할 경우의 해당 버튼 리스너
        withoutSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(SignInActivity.this);
                dlg.setMessage("로그인 없이 시작하면 특정 기능들을 사용할 수 없습니다. 진행하시겠습니까?");
                dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //토스트 메시지
                        Toast.makeText(SignInActivity.this, "메인화면으로 넘어갑니다.", Toast.LENGTH_SHORT).show();
                        ((CustomAppGraph) getApplicationContext()).clearAccount();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
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

        // 회원가입 프래그먼트로 넘어가는 리스너
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transaction = fragmentManager.beginTransaction();
                getSupportFragmentManager().beginTransaction().replace(R.id.signin_constraintlayout_signin, SignUpFragment).commit();
            }
        });
    }

    public void saveSearchData(){

        SharedPreferences sp = getSharedPreferences("savedid", MODE_PRIVATE);
        SharedPreferences.Editor mEdit= sp.edit();

        mEdit.remove("savedid");
        mEdit.putString("savedid",getEmail());

        mEdit.commit();
    }

    public void loadSearchData(){

        SharedPreferences prefs = getSharedPreferences("savedid", MODE_PRIVATE);

         setEmail(prefs.getString("savedid", null));

    }
    void setEmail(String s){ email.setText(s); }
    String getEmail(){ return email.getText().toString(); }
    String getPassword(){ return password.getText().toString(); }
}