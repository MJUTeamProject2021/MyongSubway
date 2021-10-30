package com.example.myongsubway;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;


public class SearchActivity extends AppCompatActivity {

    private ListView Search_ListView_StationListView; // 역 리스트를 보여주는 ListView
    private EditText Search_EditText_EditStation; // 역 검색창
    private SearchAdapter Search_ListView_SearchAdapter; // ListView 와 연결해주는 Adapter
    private ArrayList<String> Search_StationList; // 모든 역 리스트
    private List<String> Search_SearchedStationList; // 이전에 검색된 역 리스트
    private List<String> Search_ViewedStationList; // ListView 에서 보여질 역 리스트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    
        // 액션바 숨기기
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        // 검색창, 리스트뷰 연결
        Search_EditText_EditStation = findViewById(R.id.EditStation);
        Search_ListView_StationListView = findViewById(R.id.StationListView);

        // 모든 역 리스트 생성 및 설정
        Search_StationList = new ArrayList<>();
        settingList();

        // 리스트뷰에 보여질 역 리스트, 이전에 검색된 역 리스트 생성
        Search_ViewedStationList = new ArrayList<>();
        Search_SearchedStationList = new ArrayList<>();

        // SearchAdapter 생성 및 설정
        Search_ListView_SearchAdapter = new SearchAdapter(Search_ViewedStationList, this);
        Search_ListView_StationListView.setAdapter(Search_ListView_SearchAdapter);

        // 검색창에 텍스트를 입력했을 때 리스너 설정
        Search_EditText_EditStation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // 텍스트가 입력될 때마다 입력된 텍스트를 search
                String text = Search_EditText_EditStation.getText().toString();
                search(text);
            }
        });
        // 리스트뷰의 목록을 선택했을 때 리스너 설정
        Search_ListView_StationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                // 선택된 역이 검색할 역이 맞는지 확인하는 알림창
                AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
                dialog.setMessage("선택한 역이 " + Search_ViewedStationList.get(pos) + "역이 맞습니까?");
                dialog.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    // 맞다면 검색된 역에 해당 역을 추가하고, 메인으로 넘어가 메소드 호출!!
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 앱을 껐다 키면 초기화
                        addSearchedStation(Search_ViewedStationList.get(pos));
                    }
                });
                dialog.setNegativeButton("아니오", null);
                dialog.show();
            }
        });
    }
    public void search(String text){
        // 문자가 입력될 때마다 ListView를 새로 지정해야 함
        Search_ViewedStationList.clear();
        if (text.length() == 0) {
            // 문자 입력이 없을 때 이전 검색된 역 리스트 보여줌
            Search_ViewedStationList.addAll(Search_SearchedStationList);
        } else {
            // 문자 입력이 있을 때
            for(int i = 0; i < Search_StationList.size(); i++){
                // 검색창에 입력된 text로 시작하는 역을 ListView에 띄운다.
                if(Search_StationList.get(i).startsWith(text)){
                    Search_ViewedStationList.add(Search_StationList.get(i));
                }
            }
        }
        // 리스트 데이터 변경으로 Adapter 갱신
        Search_ListView_SearchAdapter.notifyDataSetChanged();
    }
    public void settingList() {
        // 1xx
        for(int i = 101; i <= 123; i++) Search_StationList.add(Integer.toString(i));
        // 2xx
        for(int i = 201; i <= 217; i++) Search_StationList.add(Integer.toString(i));
        // 3xx
        for(int i = 301; i <= 308; i++) Search_StationList.add(Integer.toString(i));
        // 4xx
        for(int i = 401; i <= 417; i++) Search_StationList.add(Integer.toString(i));
        // 5xx
        for(int i = 501; i <= 507; i++) Search_StationList.add(Integer.toString(i));
        // 6xx
        for(int i = 601; i <= 622; i++) Search_StationList.add(Integer.toString(i));
        // 7xx
        for(int i = 701; i <= 707; i++) Search_StationList.add(Integer.toString(i));
        // 8xx
        for(int i = 801; i <= 806; i++) Search_StationList.add(Integer.toString(i));
        // 9xx
        for(int i = 901; i <= 904; i++) Search_StationList.add(Integer.toString(i));
        //System.out.println("총 역 수는 " + Search_StationList.size());
    }
    public void addSearchedStation(String str){
        // 이전에 검색된 역 리스트에 추가
        if(!Search_SearchedStationList.contains(str)) {
            Search_SearchedStationList.add(0, str);
        }
    }
}