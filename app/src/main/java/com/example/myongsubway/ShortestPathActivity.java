package com.example.myongsubway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShortestPathActivity extends AppCompatActivity {
    private ViewPager2 pager;
    private FragmentStateAdapter pagerAdapter;
    private TabLayout tabLayout;
    private final List<String> tabElement = Arrays.asList("최소시간", "최단거리", "최소비용");

    // 출발역과 도착역
    private String departure, arrival;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shortest_path);

        // MainActivity가 전송한 데이터 받기
        //Intent intent = getIntent();
        //departure = intent.getStringExtra("departureStation");
        //arrival = intent.getStringExtra("DestinationStation");
        departure = "101";
        arrival = "204";

        //액션바 가리기
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        // 뷰페이저2와 어댑터를 연결 (반드시 TabLayoutMediator 선언 전에 선행되어야 함)
        pager = findViewById(R.id.viewpager);
        pagerAdapter = new VPAdapter(this);
        pager.setAdapter(pagerAdapter);

        // 뷰페이저와 탭레이아웃을 연동
        tabLayout = findViewById(R.id.tab);
        new TabLayoutMediator(tabLayout, pager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull @NotNull TabLayout.Tab tab, int position) {
                TextView textView = new TextView(ShortestPathActivity.this);
                textView.setText(tabElement.get(position));
                tab.setCustomView(textView);
            }
        }).attach();
    }


    private class VPAdapter extends FragmentStateAdapter {
        private ArrayList<Fragment> items;

        public VPAdapter(FragmentActivity fa) {
            super(fa);
            items = new ArrayList<Fragment>();
            items.add(new MinTimePathFragment(departure, arrival));
            items.add(new MinDistancePathFragment());
            items.add(new MinCostPathFragment());
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {  // 포지션마다 있을 fragment 설정
            return items.get(position);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

}