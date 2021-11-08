package com.example.myongsubway;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;

public class MinCostPathFragment extends Fragment {
    ArrayList<Integer> minTimePath;                 // 최소시간을 기준으로 탐색한 경로
    ArrayList<Integer> costs;                       // 순서대로 소요시간, 소요거리, 소요비용을 저장하는 리스트
    CustomAppGraph graph;                           // 액티비티 간에 공유되는 데이터를 담는 클래스
    ArrayList<CustomAppGraph.Vertex> vertices;      // 역들의 정보를 담는 클래스인 Vertex 객체들을 저장하는 리스트
    ArrayList<Integer> btnBackgrounds;              // 역을 나타내는 버튼들의 background xml 파일의 id를 저장하는 리스트

    Button departureButton, arrivalButton;          // 출발역과 도착역을 나타내는 버튼
    TextView departureLine, arrivalLine;    // 출발역과 도착역의 호선을 나타내는 텍스트뷰

    public MinCostPathFragment(ArrayList<Integer>path, ArrayList<Integer> _costs, CustomAppGraph _graph) {
        minTimePath = path;
        costs = _costs;
        graph = _graph;

        vertices = graph.getVertices();
        initializeBtnBackgrounds();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_min_cost_path, container, false);

        departureButton = v.findViewById(R.id.departureButton);
        arrivalButton = v.findViewById(R.id.arrivalButton);
        CustomAppGraph.Vertex departure = graph.getVertices().get(minTimePath.get(0));
        CustomAppGraph.Vertex arrival = graph.getVertices().get(minTimePath.get(minTimePath.size() - 1));

        departureButton.setText(departure.getVertex());
        arrivalButton.setText((arrival.getVertex()));

        departureButton.setBackgroundResource(btnBackgrounds.get(departure.getLine()));
        arrivalButton.setBackgroundResource(btnBackgrounds.get(arrival.getLine()));

        departureLine = v.findViewById(R.id.departureLine);
        arrivalLine = v.findViewById(R.id.arrivalLine);

        departureLine.setText(departure.getLine() + "호선");
        arrivalLine.setText(arrival.getLine() + "호선");

        // 각각 departureButton , arrivalButton 의 중간에 오도록
        // departureLine , arrivalLine 의 marginLeft, marginRight 를 설정한다.
        setLineTextViewMargin();

        return v;
    }

    private void setLineTextViewMargin() {
        float density = this.getResources().getDisplayMetrics().density;

        ConstraintLayout.LayoutParams dLayoutParams = (ConstraintLayout.LayoutParams) departureButton.getLayoutParams();
        int halfDepartureWidth = (int)(dLayoutParams.width / density + 0.5) / 2;    // dp

        ConstraintLayout.LayoutParams dLineLayoutParams = (ConstraintLayout.LayoutParams) departureLine.getLayoutParams();
        ConstraintLayout.LayoutParams aLineLayoutParams = (ConstraintLayout.LayoutParams) arrivalLine.getLayoutParams();
        int halfDepartureLineWidth = (int)(dLineLayoutParams.width / density + 0.5) / 2;    // dp
        int margin = (25 + halfDepartureWidth) - halfDepartureLineWidth;    // dp

        dLineLayoutParams.setMarginStart((int)(margin * density + 0.5));
        aLineLayoutParams.setMarginEnd((int)(margin * density + 0.5));
    }

    private void initializeBtnBackgrounds() {
        btnBackgrounds = new ArrayList<Integer>(10);
        btnBackgrounds.add(-1);
        btnBackgrounds.add(R.drawable.round_button_1);
        btnBackgrounds.add(R.drawable.round_button_2);
        btnBackgrounds.add(R.drawable.round_button_3);
        btnBackgrounds.add(R.drawable.round_button_4);
        btnBackgrounds.add(R.drawable.round_button_5);
        btnBackgrounds.add(R.drawable.round_button_6);
        btnBackgrounds.add(R.drawable.round_button_7);
        btnBackgrounds.add(R.drawable.round_button_8);
        btnBackgrounds.add(R.drawable.round_button_9);
    }
}
