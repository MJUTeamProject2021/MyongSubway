package com.example.myongsubway;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class TransferWalkTimeActivity extends AppCompatActivity {
    private CustomAppGraph graph;                                   // 액티비티 간에 공유되는 데이터를 담는 클래스
    
    private Button fastWalkButton, normalWalkButton,                // 환승 도보 속도를 나타내는 버튼
            slowWalkButton;

    final int START_INDEX = 6;                                      // 버튼의 부분 색을 변경할 텍스트가 시작되는 인덱스

    final String SPAN_COLOR = "#808080";                            // 버튼 텍스트의 부분 색                               
    final String CLICK_COLOR = "#3EB489";                           // 버튼이 선택됐을 때의 색

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_walk_speed);

        init();

        setToolbar();

    }

    // 초기화하는 메소드
    private void init() {
        graph = (CustomAppGraph) getApplicationContext();

        fastWalkButton = findViewById(R.id.fastWalk);
        normalWalkButton = findViewById(R.id.normalWalk);
        slowWalkButton = findViewById(R.id.slowWalk);

        initButton();

        registerListener();
    }

    // 버튼들을 초기화 한다.
    private void initButton() {
        // fastWalkButton 의 텍스트 색을 부분 조정
        changeButtonColorSpan(fastWalkButton, SPAN_COLOR);
        // normalWalkSpeed 의 텍스트 색을 부분 조정
        changeButtonColorSpan(normalWalkButton, SPAN_COLOR);
        // slowWalkSpeed 의 텍스트 색을 부분 조정
        changeButtonColorSpan(slowWalkButton, SPAN_COLOR);

        // 현재 저장되어있는 환승 도보 속도
        float walkSpeed = graph.getWalkSpeed();
        
        // 현재 선택된 버튼의 색을 갱신한다.
        if (walkSpeed == graph.FAST_WALK) {
            fastWalkButton.setTextColor(Color.parseColor(CLICK_COLOR));
            changeButtonColorSpan(fastWalkButton, CLICK_COLOR);
        }
        else if (walkSpeed == graph.NORMAL_WALK) {
            normalWalkButton.setTextColor(Color.parseColor(CLICK_COLOR));
            changeButtonColorSpan(normalWalkButton, CLICK_COLOR);
        }
        else if (walkSpeed == graph.SLOW_WALK) {
            slowWalkButton.setTextColor(Color.parseColor(CLICK_COLOR));
            changeButtonColorSpan(slowWalkButton, CLICK_COLOR);
        }
    }

    // 툴바를 설정하는 메소드
    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.transferWalkSpeedToolbar);
        setSupportActionBar(toolbar);
        //기본 타이틀을 가린다.
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // 뒤로가기 버튼을 생성한다.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // 버튼의 클릭 이벤트를 등록하는 메소드
    private void registerListener() {
        View.OnClickListener onClickListener = new Button.OnClickListener() {
            // 클릭한 버튼만 색을 변경시키고 나머지는 원래의 색으로 설정한다.
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.fastWalk:
                        // walkSpeed 를 갱신하고 선택된 버튼의 색을 변경한다.
                        graph.setWalkSpeed(graph.FAST_WALK);
                        fastWalkButton.setTextColor(Color.parseColor(CLICK_COLOR));
                        changeButtonColorSpan(fastWalkButton, CLICK_COLOR);

                        // 나머지 버튼의 색을 원래대로 돌린다.
                        normalWalkButton.setTextColor(Color.BLACK);
                        slowWalkButton.setTextColor(Color.BLACK);
                        changeButtonColorSpan(normalWalkButton, SPAN_COLOR);
                        changeButtonColorSpan(slowWalkButton, SPAN_COLOR);
                        break;

                    case R.id.normalWalk:
                        // walkSpeed 를 갱신하고 선택된 버튼의 색을 변경한다.
                        graph.setWalkSpeed(graph.NORMAL_WALK);
                        normalWalkButton.setTextColor(Color.parseColor(CLICK_COLOR));
                        changeButtonColorSpan(normalWalkButton, CLICK_COLOR);

                        // 나머지 버튼의 색을 원래대로 돌린다.
                        fastWalkButton.setTextColor(Color.BLACK);
                        slowWalkButton.setTextColor(Color.BLACK);
                        changeButtonColorSpan(fastWalkButton, SPAN_COLOR);
                        changeButtonColorSpan(slowWalkButton, SPAN_COLOR);
                        break;

                    case R.id.slowWalk:
                        // walkSpeed 를 갱신하고 선택된 버튼의 색을 변경한다.
                        graph.setWalkSpeed(graph.SLOW_WALK);
                        slowWalkButton.setTextColor(Color.parseColor(CLICK_COLOR));
                        changeButtonColorSpan(slowWalkButton, CLICK_COLOR);

                        // 나머지 버튼의 색을 원래대로 돌린다.
                        fastWalkButton.setTextColor(Color.BLACK);
                        normalWalkButton.setTextColor(Color.BLACK);
                        changeButtonColorSpan(fastWalkButton, SPAN_COLOR);
                        changeButtonColorSpan(normalWalkButton, SPAN_COLOR);
                        break;
                }
            }
        };

        // 버튼 클릭 이벤트를 등록한다.
        fastWalkButton.setOnClickListener(onClickListener);
        normalWalkButton.setOnClickListener(onClickListener);
        slowWalkButton.setOnClickListener(onClickListener);
    }

    // 버튼 텍스트의 부분 색을 변경시키는 메소드
    private void changeButtonColorSpan(Button button, String color) {
        String content = button.getText().toString();
        SpannableString spannableString = new SpannableString(content);
        int end = content.length();
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor(color)), START_INDEX, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new RelativeSizeSpan(0.8f), START_INDEX, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        button.setText(spannableString);
    }

    // 툴바의 액션버튼이 선택됐을때의 기능을 설정하는 메소드
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}
