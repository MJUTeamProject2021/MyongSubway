package com.example.myongsubway;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import java.util.ArrayList;


public class SearchActivity extends AppCompatActivity {

    private ListView stationListView; // 역 리스트를 보여주는 ListView
    private EditText editStation; // 역 검색창
    private SearchAdapter searchAdapter; // ListView 와 연결해주는 Adapter
    private ArrayList<String> searchedStationList; // 이전에 검색된 역 리스트
    private ArrayList<String> viewedStationList; // ListView 에서 보여질 역 리스트
    private Button backButton; // 뒤로가기 버튼
    private CustomAppGraph graph; // CustomAppGraph
    private ArrayList<CustomAppGraph.Vertex> vertices; // 모든 정점(역) 리스트
    private Button removeSearchHistoryButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // 뒤로가기 버튼
        backButton = findViewById(R.id.Search_Button_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        // CustomAppGraph 설정 및 Vertex 설정
        graph = (CustomAppGraph) getApplicationContext();
        vertices = graph.getVertices();

        // 검색창, 리스트뷰 연결
        editStation = findViewById(R.id.Search_EditText_editStation);
        stationListView = findViewById(R.id.Search_ListView_stationListView);


        // 리스트뷰에 보여질 역 리스트, 이전에 검색된 역 리스트 생성
        viewedStationList = new ArrayList<>();
        searchedStationList = new ArrayList<>();



        // SearchAdapter 생성 및 설정
        searchAdapter = new SearchAdapter(viewedStationList, this);
        stationListView.setAdapter(searchAdapter);

        //검색했던 데이터 로드
        loadSearchData();
        viewedStationList.addAll(searchedStationList);

        // 검색창에 텍스트를 입력했을 때 리스너 설정
        editStation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // 텍스트가 입력될 때마다 입력된 텍스트를 search
                String text = editStation.getText().toString();
                search(text);
            }
        });
        // 리스트뷰의 목록을 선택했을 때 리스너 설정
        stationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                // 선택된 역이 검색할 역이 맞는지 확인하는 알림창
                AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
                dialog.setMessage("선택한 역이 " + viewedStationList.get(pos) + "역이 맞습니까?");
                dialog.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    // 맞다면 검색된 역에 해당 역을 추가하고, 메인으로 넘어가 메소드 호출!!
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addSearchedStation(viewedStationList.get(pos));
                        saveSearchData();
                        ((MainActivity)MainActivity.mcontext).launchReport(viewedStationList.get(pos));
                        finish();
                    }
                });
                dialog.setNegativeButton("아니오", null);
                dialog.show();
            }
        });
        // 전체삭제 버튼을 눌렀을 때
        removeSearchHistoryButton = (Button) findViewById(R.id.Search_Button_removeSearchHistory);
        removeSearchHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               removeSearchHistory();
           }
        });
    }
    public void removeSearchHistory(){
        for(int i = searchedStationList.size() - 1; i >= 0; i--){
            searchedStationList.remove(i);
        }
        viewedStationList.clear();
        saveSearchData();
        // 리스트 데이터 변경으로 Adapter 갱신
        searchAdapter.notifyDataSetChanged();
    }
    public void search(String text){
        // 문자가 입력될 때마다 ListView를 새로 지정해야 함
         viewedStationList.clear();

        if (text.length() == 0) {
            // 문자 입력이 없을 때 이전 검색된 역 리스트 보여줌
            viewedStationList.addAll(searchedStationList);
        } else {
            // text로 시작되는 정점이 있다면
            for(int i = 0; i < graph.getStationCount(); i++){
                if(vertices.get(i).getVertex().startsWith(text)){
                    // viewedStationList 에 추가
                    viewedStationList.add(vertices.get(i).getVertex());
                }
            }
        }
        // 리스트 데이터 변경으로 Adapter 갱신
        searchAdapter.notifyDataSetChanged();
    }

    public void addSearchedStation(String str){
        // 이전에 검색된 역 리스트에 추가
        if(!searchedStationList.contains(str)) {
            searchedStationList.add(0, str);
        }
    }
    public void saveSearchData(){

        SharedPreferences sp = getSharedPreferences("search", MODE_PRIVATE);
        SharedPreferences.Editor mEdit= sp.edit();

        mEdit.putInt("search_size",searchedStationList.size());

        for(int i=0;i<searchedStationList.size();i++)
        {
            mEdit.remove("search" + i);
            mEdit.putString("search" + i, searchedStationList.get(i));
        }
        mEdit.commit();
    }

    public void loadSearchData(){

        SharedPreferences prefs = getSharedPreferences("search", MODE_PRIVATE);
        int size = prefs.getInt("search_size", 0);
        for(int i=0;i<size;i++)
        {
            searchedStationList.add(prefs.getString("search" + i, null));
        }
    }

}