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
import android.util.Log;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class ShortestPathActivity extends AppCompatActivity {
    private ViewPager2 pager;                   // 뷰페이저
    private FragmentStateAdapter pagerAdapter;  // 뷰페이저 어댑터
    private TabLayout tabLayout;                // 탭들을 담는 탭 레이아웃
    private final List<String> tabElement = Arrays.asList("최소시간", "최단거리", "최소비용");  // 탭을 채울 텍스트


    private String departure, arrival;              // 출발역과 도착역
    private ArrayList<ArrayList<Integer>> paths;    // 출발역 ~ 도착역의 경로를 저장하는 리스트, 순서대로 최소시간, 최단거리, 최소비용의 경로가 저장됨
    private final int TYPE_COUNT = 3;               // SearchType 의 경우의 수 (최소시간, 최단거리, 최소비용)

    private CustomAppGraph graph;                   // 액티비티 간에 공유되는 데이터를 담는 클래스

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shortest_path);

        // 초기화
        graph = (CustomAppGraph) getApplicationContext();       // 액티비티 간에 공유되는 데이터를 담는 클래스의 객체.

        paths = new ArrayList<ArrayList<Integer>>(TYPE_COUNT);
        for (int i = 0; i < TYPE_COUNT; i++) {
            paths.add(new ArrayList<Integer>());
        }

        // MainActivity 가 전송한 데이터 받기
        /*Intent intent = getIntent();
        departure = intent.getStringExtra("departureStation");
        arrival = intent.getStringExtra("DestinationStation");*/
        departure = "101";
        arrival = "204";

        // 다익스트라 알고리즘을 통해 경로탐색, 3가지 SearchType 을 모두 수행한다.
        dijkstra(graph.getMap().get(departure), CustomAppGraph.SearchType.MIN_TIME);
        dijkstra(graph.getMap().get(departure), CustomAppGraph.SearchType.MIN_DISTANCE);
        dijkstra(graph.getMap().get(departure), CustomAppGraph.SearchType.MIN_COST);

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

        //액션바 가리기
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }

    public void dijkstra(int here, CustomAppGraph.SearchType TYPE) {
        // 역과 비용을 관리하는 VertexCost 클래스
        class VertexCost implements Comparable<VertexCost> {
            int vertex;     // 역
            int cost;       // 비용

            public VertexCost(int _vertex, int _cost) {
                vertex = _vertex;
                cost = _cost;
            }

            @Override
            public int compareTo(VertexCost vc) {
                if (this.cost > vc.cost)
                    return 1;
                else if (this.cost < vc.cost)
                    return -1;
                else
                    return 0;
            }
        }

        // 각 역에서 갈 수 있는 역을 저장하는 리스트
        // 해당 리스트의 역들은 발견만 한 역일뿐 아직 방문하지 않은 상태이다.
        PriorityQueue<VertexCost> discovered = new PriorityQueue<VertexCost>();

        ArrayList<Integer> best = new ArrayList<Integer>(graph.getEdgeCount());     // 각 역으로 가는 최단거리를 저장하는 리스트
        ArrayList<Integer> parent = new ArrayList<Integer>(graph.getEdgeCount());   // 각 역의 이전 역을 저장하는 리스트

        // 리스트 초기화
        for (int i = 0; i < graph.getEdgeCount(); i++) {
            best.add(Integer.MAX_VALUE);
        }
        for (int i = 0; i < graph.getEdgeCount(); i++) {
            parent.add(-1);
        }

        // 처음 위치의 정보를 저장
        discovered.add(new VertexCost(here, 0));
        best.set(here, 0);
        parent.set(here, here);

        // 출발역부터 갈 수 있는 모든 정점을 탐색한다.
        while (discovered.isEmpty() == false) {
            // 발견한 후보 중 cost 가 가장 작은, 방문할 후보를 찾는다.
            VertexCost bestVC = discovered.remove();

            int cost = bestVC.cost;     // 현재 방문할 역까지의 비용을 저장
            here = bestVC.vertex;       // 현재 방문할 역을 찾은 후보로 변경

            // 더 짧은 경로가 존재한다면 스킵 (새로 찾은 현재 역까지의 비용이 기존의 현재 역까지의 비용보다 크면 스킵)
            if (best.get(here) < cost)
                continue;
            
            // 방문
            // here 에 해당하는 Vertex 객체에서 연결되어 있는 역 정보를 받아와서 방문할 수 있는 모든 역을 발견
            for (int there : graph.getVertices().get(here).getAdjacent()) {
                int nextCost = best.get(here) + graph.getAdjacent().get(here).get(there).getCost(TYPE);

                // 더 좋은 경로를 과거에 찾았으면 스킵
                if (nextCost >= best.get(there))
                    continue;

                // 현재 역에서 발견한 역을 등록
                discovered.add(new VertexCost(there, nextCost));
                best.set(there, nextCost);
                parent.set(there, here);
            }
        }

        // 경로탐색이 끝남

        // paths 중 인자로 전달된 SearchType 에 맞는 리스트에 저장함
        // 도착역부터 각 역에 등록된 parent 를 찾아 출발역까지 거슬러올라감
        int pos = graph.getMap().get(arrival);
        while (true) {
            paths.get(TYPE.ordinal()).add(pos);
            
            // 출발지에 다다름
            if (pos == parent.get(pos))
                break;

            pos = parent.get(pos);
        }

        // 경로가 저장된 리스트를 뒤집는다.
        Collections.reverse(paths.get(TYPE.ordinal()));

        // path 리스트의 마지막에 총 비용을 추가한다.
        paths.get(TYPE.ordinal()).add(best.get(graph.getMap().get(arrival)));
    }


    private class VPAdapter extends FragmentStateAdapter {
        private ArrayList<Fragment> items;

        public VPAdapter(FragmentActivity fa) {
            super(fa);
            items = new ArrayList<Fragment>();
            items.add(new MinTimePathFragment(paths.get(CustomAppGraph.SearchType.MIN_TIME.ordinal()), graph.getReverseMap()));
            items.add(new MinDistancePathFragment(paths.get(CustomAppGraph.SearchType.MIN_DISTANCE.ordinal()), graph.getReverseMap()));
            items.add(new MinCostPathFragment(paths.get(CustomAppGraph.SearchType.MIN_COST.ordinal()), graph.getReverseMap()));
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