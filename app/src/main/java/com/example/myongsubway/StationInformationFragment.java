package com.example.myongsubway;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Icon;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.style.TypefaceSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class StationInformationFragment extends Fragment implements View.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView lineName;
    private TextView stationName;
    private TextView transferExist;
    private Button departureStation;
    private Button destinationStation;
    private Button closeButton;
    private LinearLayout toiletExist;
    private LinearLayout linkStationLayout;
    private LinearLayout facilitiesLayout;
    private LinearLayout transferlayout;
    private Icon YesToilet;
    private Icon noToilet;

    private String name;                  // 역의 이름 (ex. "101")
    private ArrayList<String> adjacent;    // 역과 연결된 역을 저장하는 리스트
    private ArrayList<String> facilities;    // 시설알려주는 리스트
    private ArrayList<String> transferstation; //환승역
    private int line;                       // 호선
    private boolean toilet;                 // 화장실 유무

    public StationInformationFragment(String name_,ArrayList adjacent_,ArrayList facilities_,int line_,boolean toilet_) {
        name = name_; adjacent = adjacent_; facilities = facilities_; line = line_; toilet = toilet_;transferstation = new ArrayList();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //뷰 ID등록
        View v = inflater.inflate(R.layout.fragment_station_information, container, false);
        lineName = (TextView) v.findViewById(R.id.fragment_station_linename);
        stationName = (TextView) v.findViewById(R.id.fragment_station_name);
        transferExist = (TextView) v.findViewById(R.id.fragment_station_transferOX);
        toiletExist = (LinearLayout) v.findViewById(R.id.fragment_station_toiletOX);
        departureStation = (Button) v.findViewById(R.id.fragment_station_departbutton);
        destinationStation = (Button) v.findViewById(R.id.fragment_station_destibutton);
        closeButton = (Button) v.findViewById(R.id.fragment_station_close);
        linkStationLayout = (LinearLayout) v.findViewById(R.id.fragment_station_linkstalayout);
        facilitiesLayout = (LinearLayout) v.findViewById(R.id.fragment_station_facilitieslayout);
        transferlayout = (LinearLayout) v.findViewById(R.id.fragment_station_transferlayout);

        //onClickListener 등록
        closeButton.setOnClickListener(this);
        departureStation.setOnClickListener(this);
        destinationStation.setOnClickListener(this);

        //역명과 호선이름 설정
        stationName.setText(name);
        lineName.setText(line+"호선");

        //인접 역
        for(int i=0;i<adjacent.size();i++){
            TextView linktextview = new TextView(getContext());
            linktextview.setText(adjacent.get(i).toString()+"역");
            linktextview.setGravity(Gravity.CENTER);
            linkStationLayout.addView(linktextview);
            if(adjacent.get(i).charAt(0) != name.charAt(0)){
                transferstation.add(adjacent.get(i));
            }
        }
        //환승역
        if(transferstation.size()!=0){
            transferlayout.removeView(transferExist);
        }
        for(int i=0;i<transferstation.size();i++){
            TextView transfertextview = new TextView(getContext());
            transfertextview.setText(transferstation.get(i).charAt(0)+"호선 : "+ transferstation.get(i).toString()+"역");
            transfertextview.setGravity(Gravity.CENTER);
            transfertextview.setTypeface(null, Typeface.BOLD);
            transferlayout.addView(transfertextview);
        }
        //편의시설
        for(int i=0;i<facilities.size();i++){
            TextView facilitiestextview = new TextView(getContext());
            facilitiestextview.setText(facilities.get(i).toString()+"역");
            facilitiestextview.setGravity(Gravity.CENTER);
            facilitiesLayout.addView(facilitiestextview);
        }
        //화장실
        if(toilet==false){
            toiletExist.setVisibility(View.INVISIBLE);
        }
        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //출근버튼
            case R.id.fragment_station_close:
                ((MainActivity) getActivity()).destroyFragment();
                break;
            case R.id.fragment_station_departbutton:
                if(((MainActivity)getActivity()).destiText.getText() == name){
                    Toast.makeText(getContext(), "도착역에 이미 등록되어있습니다.", Toast.LENGTH_SHORT).show();
                }
                else {
                    ((MainActivity) getActivity()).departText.setText(name);
                    ((MainActivity) getActivity()).destroyFragment();
                }
                break;
            case R.id.fragment_station_destibutton:
                if(((MainActivity)getActivity()).departText.getText() == name){
                    Toast.makeText(getContext(), "출발역에 이미 등록되어있습니다.", Toast.LENGTH_SHORT).show();
                }
                else{
                    ((MainActivity)getActivity()).destiText.setText(name);
                    ((MainActivity) getActivity()).destroyFragment();
                }
                break;
        }
    }
}