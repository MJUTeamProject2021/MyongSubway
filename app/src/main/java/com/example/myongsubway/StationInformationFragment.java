package com.example.myongsubway;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class StationInformationFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private LinearLayout adjacentLayout;
    private LinearLayout facilitiesLayout;
    private TextView vertexName;
    private TextView toilet;
    private TextView door;
    private TextView number;
    private Button lineName;
    private Button closeButton;
    private CustomAppGraph.Vertex vertex;
    CustomAppGraph graph;
    public StationInformationFragment(CustomAppGraph.Vertex _vertex,CustomAppGraph _graph) {
        vertex=_vertex;
        graph = _graph;
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
        View v = inflater.inflate(R.layout.fragment_information, container, false);

        //버튼 ID등록 및 클릭리스너 등록
        adjacentLayout = v.findViewById(R.id.fragment_information_adjacent);
        facilitiesLayout = v.findViewById(R.id.fragment_information_facilities);
        vertexName= v.findViewById(R.id.fragment_information_name);
        toilet = v.findViewById(R.id.fragment_information_toilet);
        door = v.findViewById(R.id.fragment_information_door);
        number = v.findViewById(R.id.fragment_information_number);
        lineName= v.findViewById(R.id.fragment_information_line);
        closeButton = v.findViewById(R.id.fragment_information_close);

        number.setOnClickListener(this);
        closeButton.setOnClickListener(this);

        //역 이름 및 호선이름
        vertexName.setText(vertex.getVertex()+"역");
        lineName.setText(vertex.getLine()+"호선");

        //전화번호
        number.setText(vertex.getNumber());

        //동적으로 인접역 보여주기
        for(int i=0;i<vertex.getAdjacent().size();i++){
            TextView tv = new TextView(getContext());
            tv.setText(graph.getReverseMap().get(vertex.getAdjacent().get(i))+"역");
            adjacentLayout.addView(tv);
        }
        //동적으로 역 내 편의시설보여주기
        String facilities[] = vertex.getStationFacilities();
        for(int i=0;i<facilities.length;i++){
            TextView tv = new TextView(getContext());
            tv.setText(facilities[i]);
            facilitiesLayout.addView(tv);
        }
        //화장실 및 내리는 문 위치
        if(vertex.getToilet()==false) {
            toilet.setText("없음");
        }else{
            toilet.setText("있음");
        }
        door.setText(vertex.getDoorDirection());

        return v;
    }

    @Override
    public void onClick(View v) {
        StationReportFragment af =  (StationReportFragment)getParentFragment();
        switch (v.getId()) {
            case R.id.fragment_information_close:
                 af.removeInformationFragment(this);
                break;
            case R.id.fragment_information_number:
                Intent tt = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+vertex.getNumber().replace("-","")));
                startActivity(tt);
                break;
        }
    }
}