package com.example.myongsubway;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MotionEventCompat;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class    MainActivity extends AppCompatActivity implements View.OnClickListener {

    //public StationInformationFragment fragment;
    public StationReportFragment fragment;
    public Button findButton;
    public Button changeButton;
    public Button gotoShort;
    public Button gotoBookmark;
    public Button gotoSearch;
    public Button gotoSetting;
    public TextView departText;
    public TextView destiText;
    public Intent intent;
    public ArrayList<Button>StationButtonList = new ArrayList();
    public Button station444;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for(int i=0;i<111;i++) {
            //ID 동적으로 받기
            String tempId =  "main_Button_Station" + (i+1) ;
            int resID = getResources().getIdentifier(tempId, "id", getPackageName());
            Button button = findViewById(resID);
            StationButtonList.add(button);
            StationButtonList.get(i).setOnClickListener(this);
        }

        //액션바 가리기
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
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

    //역 클릭
  /*  void onPhotoTap (ImageView view ,float  x ,float  y )
    {
        ArrayList<String> adjacent_ = new ArrayList();
        ArrayList<String> facilities_ = new ArrayList();
        if(x>0.03275892&& y>0.020183377&&x<0.065530114&& y<0.0830131 ) {
            String a,b;  a = "204";  b = "206";
            adjacent_.add(a); adjacent_.add(b);
            String c;  c = "세븐일레븐 205역점 ";
            facilities_.add(c);
           // fragment = new StationInformationFragment("205역",adjacent_,facilities_,2,false);
            fragment = new StationReportFragment();
        }
        else if(x>0.027996428&& y>0.10434187&&x<0.06481509&& y<0.15522596 ){
            String a,b;  a = "203";  b = "205";
            adjacent_.add(a); adjacent_.add(b);
            fragment = new StationInformationFragment("204역",adjacent_,facilities_,2,false);
        }
        else if(x>0.035435267&& y>0.18798444&&x<0.06665038&& y<0.23491357 ){
            String a,b;  a = "202";  b = "204";
            adjacent_.add(a); adjacent_.add(b);
            String c,d;  c = "교보문고 203역점 "; d = "맛나분식 203역점";
            facilities_.add(c); facilities_.add(d);
            fragment = new StationInformationFragment("203역",adjacent_,facilities_,2,false);
        }
        else if(x>0.036504436&& y>0.26615265&&x<0.061516732&& y<0.30894282 ) {
            String a,b,f;  a = "201";  b = "203"; f = "303";
            adjacent_.add(a); adjacent_.add(b); adjacent_.add(f);
            String c,d,e;  c = "GS25 202역점 ";  d = "202 빵가게"; e = "휴대폰성지 202역점";
            facilities_.add(c); facilities_.add(d); facilities_.add(e);
            fragment = new StationInformationFragment("202역",adjacent_,facilities_,2,true);
        }
        else {
            return;
        }
        FragmentTransaction transaction  = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.Main_ConstraintLayout_Main, fragment);
        transaction.commit();
    }*/

    //프래그먼트 파괴
    void destroyFragment(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
    }

    //터치
    @Override
    public void onClick(View view) {

        for(int i=0;i<111;i++) {
            if(view.getId() ==StationButtonList.get(i).getId()) {
                System.out.println(StationButtonList.get(i).getId());
                fragment = new StationReportFragment();
                FragmentTransaction transaction  = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.Main_ConstraintLayout_Main, fragment);
                transaction.commit();
            }
            }
        switch (view.getId()) {
            case R.id.Main_Button_Find:
                if(destiText.length() ==0 || departText.length() ==0){
                    Toast.makeText(this, "출발역과 도착역을 모두 입력하지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
                else{
                    String depart = departText.getText().toString();
                    String desti  = destiText.getText().toString();
                    Intent intent = new Intent(this,ShortestPathActivity.class);
                    intent.putExtra("departureStation",depart);
                    intent.putExtra("DestinationStation",desti);
                    startActivity(intent);
                }
                break;
            case R.id.Main_Button_Change:
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
            case R.id.main_Button_Station1:
                System.out.println(StationButtonList.get(0).getId());
                break;

        }

    }
}
