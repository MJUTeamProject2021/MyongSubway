package com.example.myongsubway;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class ZoomPathFragment extends Fragment {
    private ArrayList<Integer> path;                // 탐색한 경로를 담고있는 리스트
    ArrayList<Integer> btnBackgrounds;              // 역을 나타내는 버튼들의 background xml 파일의 id를 저장하는 리스트

    public ZoomPathFragment(ArrayList<Integer> _path, ArrayList<Integer> _btnBackgrounds) {
        path = _path;
        btnBackgrounds = _btnBackgrounds;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_zoom_path, container, false);

        createStationButton(v);

        return v;
    }

    private void createStationButton(View v) {
        CustomAppGraph graph = (CustomAppGraph) getActivity().getApplicationContext();
        float density = this.getResources().getDisplayMetrics().density;        // dp 와 px 사이를 변환할 때 필요한 변수

        LinearLayout btnContainer = (LinearLayout) v.findViewById(R.id.btnContainer);

        // LinearLayout Params 정의
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)(50 * density + 0.5), (int)(50 * density + 0.5));

        for (int i = 0; i < path.size(); i++) {
            final Button btn = new AppCompatButton(getActivity());

            CustomAppGraph.Vertex vertex = graph.getVertices().get(path.get(i));

            btn.setId(i + 1);
            //btn.setWidth((int)(50 * density + 0.5));
            //btn.setHeight((int)(50 * density + 0.5));
            btn.setLayoutParams(params);
            btn.setText(vertex.getVertex());
            btn.setBackgroundResource(btnBackgrounds.get(vertex.getLine()));

            final int position = i;

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("test", "position :" + position);
                }
            });

            btnContainer.addView(btn);
        }
    }
}
