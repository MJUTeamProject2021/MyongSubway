package com.example.myongsubway;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class AccountInformationActivity extends AppCompatActivity {

    private TextView account;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_information);

        Toolbar toolbar = findViewById(R.id.accountinfo_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기본 타이틀 보여줄지 말지 설정
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김

        account = findViewById(R.id.accountinfo_textview_email);
        account.setText(((CustomAppGraph) getApplicationContext()).getEmail());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickSignOut(View view) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(AccountInformationActivity.this);
        dlg.setMessage("로그아웃하시겠습니까?");
        dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //토스트 메시지
                Toast.makeText(AccountInformationActivity.this, "로그아웃되었습니다.", Toast.LENGTH_SHORT).show();

                FirebaseAuth.getInstance().signOut();

                PackageManager packageManager = getPackageManager();
                Intent intent = packageManager.getLaunchIntentForPackage(getPackageName());
                ComponentName componentName = intent.getComponent();
                Intent mainIntent = Intent.makeRestartActivityTask(componentName);
                startActivity(mainIntent);
                System.exit(0);
            }
        });
        dlg.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(),"아니오를 선택했습니다.",Toast.LENGTH_SHORT).show();
            }
        });
        dlg.show();
    }

    public void onCLickRevoke(View view) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(AccountInformationActivity.this);
        dlg.setMessage("회원탈퇴 하시겠습니까? 모든 데이터가 완전히 삭제됩니다.");
        dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //토스트 메시지
                Toast.makeText(AccountInformationActivity.this, "탈퇴되었습니다.", Toast.LENGTH_SHORT).show();

                mAuth.getCurrentUser().delete();
                //TODO. firestore 내부 데이터 삭제
                PackageManager packageManager = getPackageManager();
                Intent intent = packageManager.getLaunchIntentForPackage(getPackageName());
                ComponentName componentName = intent.getComponent();
                Intent mainIntent = Intent.makeRestartActivityTask(componentName);
                startActivity(mainIntent);
                System.exit(0);
            }
        });
        dlg.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(),"아니오를 선택했습니다.",Toast.LENGTH_SHORT).show();
            }
        });
        dlg.show();
    }
}