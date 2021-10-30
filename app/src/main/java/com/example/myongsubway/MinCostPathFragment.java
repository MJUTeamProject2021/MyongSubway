package com.example.myongsubway;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;

public class MinCostPathFragment extends Fragment {
    public TextView textView;
    ArrayList<Integer> minCostPath;
    HashMap<Integer, String> reverseMap;

    public MinCostPathFragment(ArrayList<Integer>path, HashMap<Integer, String> _reverseMap) {
        minCostPath = path;
        reverseMap = _reverseMap;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_min_cost_path, container, false);

        textView = v.findViewById(R.id.textView);
        String output = "";

        int i;
        for (i = 0; i < minCostPath.size() - 1; i++) {
            output += reverseMap.get(minCostPath.get(i)) + " ";
        }
        output += "총 비용 : " + minCostPath.get(i);

        textView.setText(output);


        return v;
    }
}
