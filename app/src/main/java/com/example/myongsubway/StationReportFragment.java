package com.example.myongsubway;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class StationReportFragment extends Fragment implements View.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    private LinearLayout menuLinearLayout;
    private LinearLayout addLinearLayout;
    private Button opaqueButton;
    private Button departureButton;
    private Button destinationButton;
    private Button closeButton;
    private Button informationButton;
    private Button touristButton;
    private Button bookmarkButton;
    private Button nameButton;
    private Button leftButton;
    private Button rightButton;
    private Button toiletButton;
    private Button doorButton;
    private Button nameButton2;
    private TextView lineTextView;

    private ArrayList<Button> addButtonList = new ArrayList();
    private CustomAppGraph.Vertex vertex;
    private ArrayList<CustomAppGraph.Vertex> vertices;
    private CustomAppGraph graph;
    private int line;


    public StationReportFragment(CustomAppGraph.Vertex _vertex,ArrayList<CustomAppGraph.Vertex> _vertices,CustomAppGraph _graph,int _line) {
        vertex = _vertex;
        vertices = _vertices;
        graph = _graph;
        line = _line;
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
        View v = inflater.inflate(R.layout.fragment_station_report, container, false);

        //버튼 ID등록 및 클릭리스너 설정
        opaqueButton= v.findViewById(R.id.fragment_report_opaque);
        departureButton = v.findViewById(R.id.fragment_report_depart);
        destinationButton= v.findViewById(R.id.fragment_report_desti);
        closeButton= v.findViewById(R.id.fragment_report_close);
        informationButton = v.findViewById(R.id.fragment_report_information);
        touristButton= v.findViewById(R.id.fragment_report_tourist);
        bookmarkButton= v.findViewById(R.id.fragment_report_bookmark);
        leftButton= v.findViewById(R.id.fragment_report_left);
        rightButton= v.findViewById(R.id.fragment_report_right);
        toiletButton = v.findViewById(R.id.fragment_report_toilet);
        doorButton = v.findViewById(R.id.fragment_report_door);
        nameButton= v.findViewById(R.id.fragment_report_name);
        nameButton2 =  v.findViewById(R.id.fragment_report_name2);
        lineTextView= v.findViewById(R.id.fragment_report_line);
        addLinearLayout = v.findViewById(R.id.fragment_report_addlayout);
        menuLinearLayout = v.findViewById(R.id.fragment_report_menulayout);

        opaqueButton.setOnClickListener(this);
        departureButton.setOnClickListener(this);
        destinationButton.setOnClickListener(this);
        closeButton.setOnClickListener(this);
        informationButton.setOnClickListener(this);
        touristButton.setOnClickListener(this);
        bookmarkButton.setOnClickListener(this);
        nameButton.setOnClickListener(this);
        nameButton2.setOnClickListener(this);
        leftButton.setOnClickListener(this);
        rightButton.setOnClickListener(this);
        toiletButton.setOnClickListener(this);
        doorButton.setOnClickListener(this);
        lineTextView.setOnClickListener(this);


        //하단 메뉴 설정
        nameButton.setText(vertex.getVertex()+"역");
        lineTextView.setText(vertex.getLine()+"호선");
        nameButton2.setText(vertex.getVertex()+"역");
        if(vertex.getLine()==1){menuLinearLayout.setBackgroundColor(getResources().getColor(R.color.frag1Color, null));}
        else if(vertex.getLine()==2){menuLinearLayout.setBackgroundColor(getResources().getColor(R.color.frag2Color, null));}
        else if(vertex.getLine()==3){menuLinearLayout.setBackgroundColor(getResources().getColor(R.color.frag3Color, null));}
        else if(vertex.getLine()==4){menuLinearLayout.setBackgroundColor(getResources().getColor(R.color.frag4Color, null));}
        else if(vertex.getLine()==5){menuLinearLayout.setBackgroundColor(getResources().getColor(R.color.frag5Color, null));}
        else if(vertex.getLine()==6){menuLinearLayout.setBackgroundColor(getResources().getColor(R.color.frag6Color, null));}
        else if(vertex.getLine()==7){menuLinearLayout.setBackgroundColor(getResources().getColor(R.color.frag7Color, null));}
        else if(vertex.getLine()==8){menuLinearLayout.setBackgroundColor(getResources().getColor(R.color.frag8Color, null));}
        else if(vertex.getLine()==9){menuLinearLayout.setBackgroundColor(getResources().getColor(R.color.frag9Color, null));}

        //주변과 환승역들 보여주기
        for(int i=0;i<vertex.getAdjacent().size();i++) {
            int adjName = Integer.parseInt(vertices.get(vertex.getAdjacent().get(i)).getVertex());
            int verName = Integer.parseInt(vertex.getVertex());
            int lineName = vertices.get(vertex.getAdjacent().get(i)).getLine();

            if (vertex.getAdjacent().size() == 1 && adjName + 1 == verName) {
                leftButton.setText(Integer.toString(adjName) + "역");
                break;
            } else if (vertex.getAdjacent().size() == 1 && adjName - 1 == verName) {
                rightButton.setText(Integer.toString(adjName) + "역");
                break;
            } else if (i == 0) {
                leftButton.setText(Integer.toString(adjName) + "역");
            } else if (i == 1) {
                rightButton.setText(Integer.toString(adjName) + "역");
            }else {
                final Button btn = new Button(getContext());
                btn.setId(i*10);
                btn.setText(Integer.toString(adjName) + "역");
                btn.setBackgroundColor(Color.rgb(250,200,200));
                btn.setOnClickListener(this);
                addLinearLayout.addView(btn);
                addButtonList.add(btn);
            }
         }


        //거꾸로 나오는 현상 거르기
        if(Integer.parseInt(leftButton.getText().subSequence(0,leftButton.getText().length()-1).toString())-1 == Integer.parseInt(vertex.getVertex())){
            String s = leftButton.getText().toString();
            leftButton.setText(rightButton.getText());
            rightButton.setText(s);
        }

        //화장실 여부
        if(vertex.getToilet()==false){
            toiletButton.setText("내부\n화장실: X");
        }else {
            toiletButton.setText("내부\n화장실: O");
        }
        //내리는 문
        if(vertex.getDoorDirection().equals("오른쪽")){
           doorButton.setText("내리는문:\t\t오른쪽");
        }else if(vertex.getDoorDirection().equals("왼쪽")){
            doorButton.setText("내리는문:\t\t왼쪽");
        }else {
            doorButton.setText("내리는문:\t\t양쪽");
        }

        return v;
    }

    //information 프래그먼트 닫기
    public void removeInformationFragment(StationInformationFragment _fragment){
        FragmentTransaction mFragmentTransaction = getChildFragmentManager().beginTransaction();
        mFragmentTransaction.remove(_fragment);
        mFragmentTransaction.commit();
    }
    public void removeSurroundingFragment(SurroundingFragment _fragment){
        FragmentTransaction mFragmentTransaction = getChildFragmentManager().beginTransaction();
        mFragmentTransaction.remove(_fragment);
        mFragmentTransaction.commit();
    }

    @Override
    public void onClick(View view) {
        int index;
        FragmentTransaction transaction;                    //환승역 클릭시 교체
        FragmentTransaction mFragmentTransaction = getChildFragmentManager().beginTransaction();
        for(int i=0;i<vertex.getAdjacent().size()-2;i++) {
            if(view.getId() == 20+i*10){
                Button btn = addButtonList.get(i);
                if(btn.getText()==""){break;}
                ((MainActivity) getActivity()).destroyFragment();
                index = graph.getMap().get(btn.getText().subSequence(0,btn.getText().length()-1));
                ((MainActivity) getActivity()).fragment = new StationReportFragment(vertices.get(index),vertices,graph,line);
                transaction = ((MainActivity)getActivity()).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.Main_ConstraintLayout_Main, ((MainActivity) getActivity()).fragment);
                transaction.commit();
                ((MainActivity)getActivity()).setIsFragmentTrue();
            }
        }

        switch (view.getId()) {
            case R.id.fragment_report_close:
                ((MainActivity) getActivity()).destroyFragment();
                break;
            case R.id.fragment_report_depart:
                if(((MainActivity)getActivity()).getDesti() == vertex.getVertex()){
                    Toast.makeText(getContext(), "도착역에 이미 등록되어있습니다.", Toast.LENGTH_SHORT).show();
                }
                else {
                    ((MainActivity) getActivity()).setDepart(vertex.getVertex());
                    ((MainActivity) getActivity()).destroyFragment();
                }
                break;
            case R.id.fragment_report_desti:
                if(((MainActivity)getActivity()).getDepart() == vertex.getVertex()){
                    Toast.makeText(getContext(), "출발역에 이미 등록되어있습니다.", Toast.LENGTH_SHORT).show();
                }
                else{
                    ((MainActivity)getActivity()).setDesti(vertex.getVertex());
                    ((MainActivity) getActivity()).destroyFragment();
                }
                break;
            case R.id.fragment_report_left:         //왼쪽 역 터치
                if(leftButton.getText()==""){break;}
                ((MainActivity) getActivity()).destroyFragment();
                index = graph.getMap().get(leftButton.getText().subSequence(0,leftButton.getText().length()-1));
                ((MainActivity) getActivity()).fragment = new StationReportFragment(vertices.get(index),vertices,graph,line);
                transaction  = ((MainActivity)getActivity()).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.Main_ConstraintLayout_Main, ((MainActivity) getActivity()).fragment);
                transaction.commit();
                ((MainActivity)getActivity()).setIsFragmentTrue();
                break;

            case R.id.fragment_report_right:        //오른쪽 역 터치
                if(rightButton.getText()==""){break;}
                ((MainActivity) getActivity()).destroyFragment();
                index = graph.getMap().get(rightButton.getText().subSequence(0,rightButton.getText().length()-1));
                ((MainActivity) getActivity()).fragment = new StationReportFragment(vertices.get(index),vertices,graph,line);
                transaction = ((MainActivity)getActivity()).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.Main_ConstraintLayout_Main, ((MainActivity) getActivity()).fragment);
                transaction.commit();
                ((MainActivity)getActivity()).setIsFragmentTrue();
                break;
            case R.id.fragment_report_information:      //역정보 터치
                StationInformationFragment informationfragment = new StationInformationFragment(vertex,graph);
                mFragmentTransaction.add(R.id.fragment_report_framelayout,informationfragment);
                mFragmentTransaction.commit();
                break;
            case R.id.fragment_report_tourist:      //역정보 터치
                SurroundingFragment surroundingfragment = new SurroundingFragment(vertex,graph);
                mFragmentTransaction.add(R.id.fragment_report_framelayout,surroundingfragment);
                mFragmentTransaction.commit();
                break;
            case R.id.fragment_report_opaque:
                ((MainActivity) getActivity()).destroyFragment();
                break;
        }
    }

}
