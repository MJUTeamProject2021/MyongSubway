package com.example.myongsubway;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class StationReportFragment extends Fragment implements View.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button departureButton;
    private Button destinationButton;
    private Button closeButton;
    private Button adjoinButton;
    private Button informationButton;
    private Button touristButton;
    private Button bookmarkButton;

    public StationReportFragment() {
        // Required empty public constructor
    }

    public static StationReportFragment newInstance(String param1, String param2) {
        StationReportFragment fragment = new StationReportFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        departureButton = v.findViewById(R.id.fragment_report_depart);
        destinationButton= v.findViewById(R.id.fragment_report_desti);
        closeButton= v.findViewById(R.id.fragment_report_close);
        adjoinButton = v.findViewById(R.id.fragment_report_adjoin);
        informationButton = v.findViewById(R.id.fragment_report_information);
        touristButton= v.findViewById(R.id.fragment_report_tourist);
        bookmarkButton= v.findViewById(R.id.fragment_report_bookmark);

        departureButton.setOnClickListener(this);
        destinationButton.setOnClickListener(this);
        closeButton.setOnClickListener(this);
        adjoinButton.setOnClickListener(this);
        informationButton.setOnClickListener(this);
        touristButton.setOnClickListener(this);
        bookmarkButton.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //출근버튼
            case R.id.fragment_report_close:
                ((MainActivity) getActivity()).destroyFragment();
                break;

        }
    }
}