package com.example.myongsubway;



import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceFragmentCompat;
public class SettingsActivity extends AppCompatActivity {

    private Button accountInformation;
    private Button transferWalkSpeed;
    private CustomAppGraph graph;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기본 타이틀 보여줄지 말지 설정
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김

        accountInformation = findViewById(R.id.setiings_button_accountinfo);
        transferWalkSpeed = findViewById(R.id.setiings_button_walking);
        graph = (CustomAppGraph) getApplicationContext();        //공유되는 데이터 담는 객체
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

    public void onClickAccount(View view) {
        if(graph.isLogined()){
            Intent intent = new Intent(getApplicationContext(), AccountInformationActivity.class);
            startActivity(intent);
        } else {
            AlertDialog.Builder dlg = new AlertDialog.Builder(SettingsActivity.this);
            dlg.setTitle("로그인이 필요합니다.");
            dlg.setMessage("로그인 창으로 이동하시겠습니까?");
            dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                    startActivity(intent);
                }
            });
            dlg.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            dlg.show();
        }
    }

    public void onclickQuestion(View view) {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.setType("plain/text");
        String[] address = {"wndtjq0510@gmail.com"};
        email.putExtra(Intent.EXTRA_EMAIL, address);
        email.putExtra(Intent.EXTRA_SUBJECT, "");
        email.putExtra(Intent.EXTRA_TEXT, "잘못된 정보를 입력해주세요.");
        startActivity(email);
    }

    public void onClickTransferWalkSpeed(View view) {
        Intent intent = new Intent(getApplicationContext(), TransferWalkTimeActivity.class);
        startActivity(intent);
    }

    public void onnClickOpenSourceInfo(View view) {
        Intent intent = new Intent(getApplicationContext(), OpenSourceInfoActivity.class);
        startActivity(intent);
    }
}