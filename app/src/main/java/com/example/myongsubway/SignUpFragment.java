package com.example.myongsubway;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executor;

import static android.content.ContentValues.TAG;

/**
 * 회원가입 프래그먼트
 * 이메일과 비밀번호를 통해 문서 ID를 만들고 생성합니다.
 */
public class SignUpFragment extends Fragment {

    private FirebaseAuth mAuth;
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public EditText email;
    public EditText password;
    public Button complete;

    public SignUpFragment() {
        // Required empty public constructor
    }

    public static SignUpFragment newInstance(String param1, String param2) {
        SignUpFragment fragment = new SignUpFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sign_up, container, false);

        email = v.findViewById(R.id.signup_edittext_emailsignup);
        password = v.findViewById(R.id.signup_edittext_passwordsignup);
        complete = v.findViewById(R.id.signup_button_compltetsignup);

        //파이어스토어에 접근하기 위한 객체
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        DocumentReference docRef = db.collection("users").document(getUserData());

        complete.setOnClickListener(new View.OnClickListener() {
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
                mAuth.createUserWithEmailAndPassword(getEmail(), getPassword())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            ArrayList<String> list = new ArrayList<String>();
                            HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
                            // Add a new document with a generated ID
                            db.collection("subwayData").document(mAuth.getUid())
                                    .set(map)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // 버튼 클릭 후 성공하면 마지막으로 프래그먼트 종료
                                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                            fragmentManager.beginTransaction().remove(SignUpFragment.this).commit();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error writing document:failure", task.getException());
                                        }
                                    });
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        }
                    }
                });
            }
        });
        return v;
    }

    String getEmail(){ return email.getText().toString(); }
    String getPassword(){ return password.getText().toString(); }
    String getUserData(){return getEmail() +"_" + getPassword();}

}