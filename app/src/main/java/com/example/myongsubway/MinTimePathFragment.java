package com.example.myongsubway;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MinTimePathFragment extends Fragment {
    public TextView textView;
    public String departure, arrival;

    public MinTimePathFragment(String d, String a) {
        departure = d;
        arrival = a;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_min_time_path, container, false);

        textView = v.findViewById(R.id.textView);
        textView.setText("departure : " + departure + "\narrival : " + arrival);

        return v;
    }
}
