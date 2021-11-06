package com.example.myongsubway;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MotionEventCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.OnViewTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class    MainActivity extends AppCompatActivity implements View.OnClickListener {


    public StationReportFragment fragment;
    private Button findButton;
    private Button changeButton;
    private Button gotoShort;
    private Button gotoBookmark;
    private Button gotoSearch;
    private Button gotoSetting;
    private TextView departText;
    private TextView destiText;
    private Intent intent;
    private CustomAppGraph graph;
    public ArrayList<Button>stationButtonList = new ArrayList();
    public ArrayList<CustomAppGraph.Vertex> mainVertices;

    private BackPressHandler backPressHandler = new BackPressHandler(this); //백버튼 핸들러
    private boolean isFragment = false;                 //프래그먼트 켜져있으면 true 아니면 false


    @Override        //초기화 메소드
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //액션바 가리기   + 액션바 없앤 테마로 바꿔서 주석처리함 지워도 됨 by 이하윤
        //ActionBar actionBar = getSupportActionBar();
        //actionBar.hide();

        graph = (CustomAppGraph) getApplicationContext();        //공유되는 데이터 담는 객체
        mainVertices = graph.getVertices();

        //지하철버튼들을 동적으로 연결, 클릭리스너 설정
        for(int i=0;i<graph.getStationCount();i++) {
            String tempId =  "main_Button_Station" + (i+1) ;
            int resID = getResources().getIdentifier(tempId, "id", getPackageName());
            Button button = findViewById(resID);
            stationButtonList.add(button);
            stationButtonList.get(i).setOnClickListener(this);
        }

        //버튼 ID등록
        findButton = findViewById(R.id.Main_Button_Find);
        changeButton = findViewById(R.id.Main_Button_Change);
        departText = findViewById(R.id.Main_textView_Depstation);
        destiText = findViewById(R.id.Main_textView_Desstation);
        gotoBookmark = findViewById(R.id.Main_Button_GotoBookmark);
        gotoSearch = findViewById(R.id.Main_Button_GotoSearch);
        gotoSetting = findViewById(R.id.Main_Button_GotoSetting);
        gotoShort = findViewById(R.id.Main_Button_GotoShort);

        //클릭리스너등록
        findButton.setOnClickListener(this);
        changeButton.setOnClickListener(this);
        gotoBookmark.setOnClickListener(this);
        gotoSearch.setOnClickListener(this);
        gotoSetting.setOnClickListener(this);
        gotoShort.setOnClickListener(this);
    }


    //Report 프래그먼트 종료
    void destroyFragment(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
        isFragment=false;
    }

    //터치할시에
    @Override
    public void onClick(View view) {

        //역 버튼을 클릭했는지 확인하는 반복문
        for(int i=0;i<graph.getStationCount();i++) {
            if(view.getId() ==stationButtonList.get(i).getId()) {
                fragment = new StationReportFragment(mainVertices.get(i),mainVertices,graph,mainVertices.get(i).getLine());
                FragmentTransaction transaction  = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.Main_ConstraintLayout_Main, fragment);
                transaction.commit();
                isFragment = true;
            }
        }

        //상단과 하단의 메뉴버튼들 클릭확인하는 스위치문
        switch (view.getId()) {
            case R.id.Main_Button_Find:                                             //출발역과 도착역 넘기기
                if(destiText.length() ==0 || departText.length() ==0){
                    Toast.makeText(this, "출발역과 도착역을 모두 입력하지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
                else{
                    String depart = departText.getText().toString();
                    String desti  = destiText.getText().toString();
                    Intent intent = new Intent(this,ShortestPathActivity.class);
                    intent.putExtra("departureStation",depart);
                    intent.putExtra("destinationStation",desti);
                    startActivity(intent);
                }
                break;
            case R.id.Main_Button_Change:                                       //출발역과도착역 바꾸기
                    String temp = departText.getText().toString();
                    departText.setText(destiText.getText());
                    destiText.setText(temp);
                break;
            case R.id.Main_Button_GotoBookmark:
                intent = new Intent(this,BookmarkActivity.class);
                startActivity(intent);
                break;
            case R.id.Main_Button_GotoSearch:
                intent = new Intent(this,SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.Main_Button_GotoSetting:
                intent = new Intent(this,SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.Main_Button_GotoShort:
                intent = new Intent(this,ShortestPathActivity.class);
                startActivity(intent);
                break;
        }
    }

    String getDepart(){
        return departText.getText().toString();
    }
    String getDesti(){
        return destiText.getText().toString();
    }
    void setDepart(String s){
        departText.setText(s);
    }
    void setDesti(String s){
        destiText.setText(s);
    }
    void setIsFragmentFalse(){
        isFragment=false;
    }
    void setIsFragmentTrue(){
        isFragment=true;
    }

    @Override
    public void onBackPressed() {
        if(isFragment == false) {
            backPressHandler.onBackPressed();
        }
        if(isFragment == true) {
            destroyFragment();
        }
    }


}
