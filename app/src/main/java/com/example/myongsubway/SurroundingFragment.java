package com.example.myongsubway;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class SurroundingFragment extends Fragment implements View.OnClickListener{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private LinearLayout buildingLayout;
    private LinearLayout restuarentLayout;
    private TextView vertexName;
    private Button lineName;
    private Button closeButton;

    private CustomAppGraph.Vertex vertex;
    CustomAppGraph graph;


    public SurroundingFragment(CustomAppGraph.Vertex _vertex,CustomAppGraph _graph) {
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
        View v = inflater.inflate(R.layout.fragment_surrounding, container, false);

        closeButton = v.findViewById(R.id.fragment_surrounding_close);
        buildingLayout = v.findViewById(R.id.fragment_surrounding_building);
        restuarentLayout = v.findViewById(R.id.fragment_surrounding_restuarent);
        lineName = v.findViewById(R.id.fragment_surrounding_line);
        vertexName = v.findViewById(R.id.fragment_surrounding_name);

        closeButton.setOnClickListener(this);

        //역 이름 및 호선이름
        vertexName.setText(vertex.getVertex()+"역");
        lineName.setText(vertex.getLine()+"호선");

        String facilities[] = vertex.getNearbyFacilities();
        for(int i=0;i<facilities.length;i++){
            TextView tv = new TextView(getContext());
            tv.setTextColor(Color.rgb(0,0,0));
            tv.setGravity(Gravity.CENTER);
            tv.setText(facilities[i]);
            buildingLayout.addView(tv);
        }

       facilities = vertex.getNearbyRestaurants();
        for(int i=0;i<facilities.length;i++){
            TextView tv = new TextView(getContext());
            tv.setTextColor(Color.rgb(0,0,0));
            tv.setGravity(Gravity.CENTER);
            tv.setText(facilities[i]);
            restuarentLayout.addView(tv);
        }

        return v;
    }

    @Override
    public void onClick(View v) {
        StationReportFragment af =  (StationReportFragment)getParentFragment();
        switch(v.getId()){
            case R.id.fragment_surrounding_close:
                af.removeSurroundingFragment(this);
        }
    }
}