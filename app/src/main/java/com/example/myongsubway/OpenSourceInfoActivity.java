package com.example.myongsubway;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.TextView;

public class OpenSourceInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_source_info);

        Toolbar toolbar = findViewById(R.id.opensource_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기본 타이틀 보여줄지 말지 설정
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김

        TextView textView = findViewById(R.id.opensource_textview_info);
        textView.setText("Layout 확대 축소 \n" +
                "https://natario1.github.io/ZoomLayout/docs/zoom-layout\n\n" +
                "실시간 지하철 공기\n" +
                "https://data.seoul.go.kr/dataList/OA-15495/A/1/datasetView.do\n\n" +
                "지하철 아이콘\n" +
                "https://www.flaticon.com/kr/free-icon/underground_491050?term=%EC%A7%80%ED%95%98%EC%B2%A0&page=2&position=1&page=2&position=1&related_id=491050&origin=tag\n\n" +
                "Android 프로젝트에 Firebase 추가\n" +
                "https://firebase.google.com/docs/android/setup\n\n" +
                "인증 상태 지속성\n" +
                "https://firebase.google.com/docs/auth/web/auth-state-persistence?hl=ko");
    }
}