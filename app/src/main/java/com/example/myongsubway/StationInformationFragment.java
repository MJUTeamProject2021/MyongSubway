package com.example.myongsubway;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.URL;

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
    private TextView update;
    private TextView airstate;
    private TextView pmq;
    private Button lineName;
    private Button closeButton;
    private CustomAppGraph.Vertex vertex;
    CustomAppGraph graph;
    
    private boolean isBlockCloseBtn;        // 나가기 버튼을 막을지를 나타내는 변수. true면 막는다.
    
    public StationInformationFragment(CustomAppGraph.Vertex _vertex,CustomAppGraph _graph) {
        vertex=_vertex;
        graph = _graph;
    }

    // ShortestPathActivity 에서 프래그먼트를 띄울 때 사용하는 생성자 오버로딩 함수
    public StationInformationFragment(CustomAppGraph.Vertex _vertex,CustomAppGraph _graph, boolean _isBlockCloseBtn) {
        vertex=_vertex;
        graph = _graph;
        isBlockCloseBtn = _isBlockCloseBtn;
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
        pmq = v.findViewById(R.id.fragment_information_pmq);
        airstate = v.findViewById(R.id.fragment_information_airstate);
        update = v.findViewById(R.id.fragment_information_update);

        number.setOnClickListener(this);
        closeButton.setOnClickListener(this);

        // ShortestPathActivity 에서 생성한 프래그먼트 객체일 때 나가기버튼을 막는다.
        if (isBlockCloseBtn)
            closeButton.setVisibility(View.INVISIBLE);

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

        AirThread at = new AirThread();
        at.start();
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

    void getAir(){

        StrictMode.enableDefaults();
        boolean isCheckDate = false, isPMq = false;
        String checkDate =null, pMq = null;

        try{
            URL url = new URL("http://openapi.seoul.go.kr:8088/734a457a6f6a68393536686a70446c/xml/airPolutionInfo/" +(graph.getMap().get(vertex.getVertex())+1)+"/"+(graph.getMap().get(vertex.getVertex())+1)+"/"); //검색 URL부분
            System.out.println(url);
            XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserCreator.newPullParser();

            parser.setInput(url.openStream(), null);
            int parserEvent = parser.getEventType();

            while (parserEvent != XmlPullParser.END_DOCUMENT){
                switch(parserEvent){
                    case XmlPullParser.START_TAG:
                        if(parser.getName().equals("CHECKDATE")){       //업데이트 날짜면 true
                            isCheckDate = true;
                        }
                        if(parser.getName().equals("PMq")){             //PMq면 true
                            isPMq = true;
                        }
                        if(parser.getName().equals("message")){
                            airstate.setText("정보를 불러오지 못했습니다.");
                        }
                        break;

                    case XmlPullParser.TEXT:
                        if(isCheckDate){ //업데이트가 맞았다면 설정
                            checkDate = parser.getText();
                            isCheckDate = false;
                        }
                        if(isPMq){      //PMq가 맞았다면 설정
                            pMq = parser.getText();
                            isPMq = false;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if(parser.getName().equals("CHECKDATE")){
                            String finalCheckDate = checkDate;
                            update.post(new Runnable() {
                                @Override
                                public void run() {
                                    update.setText("확인 날짜 및 시간 : " + finalCheckDate);
                                }
                            });
                            }
                        if(parser.getName().equals("PMq")){
                            String finalPMq = pMq;
                            pmq.post(new Runnable() {
                                @Override
                                public void run() {
                                    pmq.setText("초미세먼지량 pMq : " + finalPMq + "\n");
                                }
                            });
                            }
                        break;
                }
                parserEvent = parser.next();
            }
        } catch(Exception e){
            airstate.setText("정보를 불러오지 못했습니다    .");
        }
        double tempPmq = Double.parseDouble(pMq);
        airstate.post(new Runnable() {
            @Override
            public void run() {
                if(tempPmq<15){airstate.setText("공기가 아주 좋습니다");airstate.setTextColor(Color.rgb(0,0,255));}
                else if(tempPmq>=15&&tempPmq<35){airstate.setText("공기가 괜찮습니다");airstate.setTextColor(Color.rgb(0,255,0));}
                else if(tempPmq>=35&&tempPmq<75){airstate.setText("공기가 좋지 않습니다");airstate.setTextColor(Color.rgb(255,128,0));}
                else if(tempPmq>=76){airstate.setText("공기가 매우 나쁩니다");airstate.setTextColor(Color.rgb(255,0,0));}
            }
        });
    }

    class AirThread extends Thread{
        public void run(){
            getAir();
        }
    }
}