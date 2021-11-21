package com.example.myongsubway;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class MinTransferPathFragment extends Fragment {
    ArrayList<Integer> minTransferPath;             // 최소시간을 기준으로 탐색한 경로
    ArrayList<Integer> lines;                       // 각 역의 호선을 담고있는 리스트
    ArrayList<Integer> costs;                       // 순서대로 소요시간, 소요거리, 소요비용을 저장하는 리스트
    CustomAppGraph graph;                           // 액티비티 간에 공유되는 데이터를 담는 클래스
    ArrayList<CustomAppGraph.Vertex> vertices;      // 역들의 정보를 담는 클래스인 Vertex 객체들을 저장하는 리스트
    ArrayList<Integer> btnBackgrounds;              // 역을 나타내는 버튼들의 background xml 파일의 id를 저장하는 리스트
    ArrayList<Integer> lineColors;                  // 호선의 색들을 담고있는 리스트

    Button departureButton, arrivalButton;          // 출발역과 도착역을 나타내는 버튼
    ImageButton zoomButton;                         // 확대 버튼
    Button reportButton;                            // 잘못된 정보 신고 버튼
    TextView departureLine, arrivalLine;            // 출발역과 도착역의 호선을 나타내는 텍스트뷰
    TextView costTime, costDistance, costCost,
            costTransfer;                           // 각각 소요시간, 소요거리, 소요비용, 환승횟수를 나타내는  텍스트뷰
    ImageView midLine;                              // 역버튼 사이의 선을 나타내는 뷰

    public MinTransferPathFragment(ArrayList<Integer> path, ArrayList<Integer> _lines, ArrayList<Integer> _costs, CustomAppGraph _graph, ArrayList<Integer> _btnBackgrounds, ArrayList<Integer> _lineColors) {
        minTransferPath = path;
        lines = _lines;
        costs = _costs;
        graph = _graph;
        btnBackgrounds = _btnBackgrounds;
        lineColors = _lineColors;

        vertices = graph.getVertices();
    }

    @Override
    public void onResume() {
        ((ShortestPathActivity) getActivity()).setPageType(CustomAppGraph.SearchType.MIN_TRANSFER);
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_min_transfer_path, container, false);

        // 초기화한다.
        init(v);

        // 각각 departureButton , arrivalButton 의 중간에 오도록
        // departureLine , arrivalLine 의 marginLeft, marginRight 를 설정한다.
        setLineTextViewMargin();

        // midLine 에 그라데이션 넣기
        setMidLineGradient();

        // 소요시간, 소요거리, 소요비용을 텍스트뷰에 적용시킨다.
        setCostTextView();

        // 버튼들의 이벤트를 등록한다.
        registerListener();

        return v;
    }

    // 초기화하는 메소드
    private void init(View v) {
        // 역버튼을 나타내는 버튼 참조
        departureButton = v.findViewById(R.id.departureButton);
        arrivalButton = v.findViewById(R.id.arrivalButton);

        // 출발역, 도착역의 Vertex 객체
        CustomAppGraph.Vertex departure = graph.getVertices().get(minTransferPath.get(0));
        CustomAppGraph.Vertex arrival = graph.getVertices().get(minTransferPath.get(minTransferPath.size() - 1));

        // 역버튼의 이름을 설정한다.
        departureButton.setText(departure.getVertex());
        arrivalButton.setText((arrival.getVertex()));

        // 호선의 색에 맞춰 역버튼의 색을 설정한다.
        departureButton.setBackgroundResource(btnBackgrounds.get(lines.get(0)));
        arrivalButton.setBackgroundResource(btnBackgrounds.get(lines.get(lines.size() - 1)));

        // 호선을 나타내는 텍스트뷰 참조
        departureLine = v.findViewById(R.id.departureLine);
        arrivalLine = v.findViewById(R.id.arrivalLine);

        // 호선에 맞춰 텍스트뷰를 설정한다.
        departureLine.setText(lines.get(0) + "호선");
        arrivalLine.setText(lines.get(lines.size() - 1) + "호선");

        // 소요시간, 소요거리, 소요비용, 환승횟수를 나타내는 텍스트뷰 참조
        costTime = v.findViewById(R.id.costTime);
        costDistance = v.findViewById(R.id.costDistance);
        costCost = v.findViewById(R.id.costCost);
        costTransfer = v.findViewById(R.id.costTransfer);

        // 확대, 잘못된 정보 신고, 즐겨찾기 등록 버튼 참조
        zoomButton = v.findViewById(R.id.zoomButton);
        reportButton = v.findViewById(R.id.reportButton);

        // 역버튼 사이의 선을 나타내는 뷰 참조
        midLine = v.findViewById(R.id.midLine);
    }

    // 버튼에 클릭리스너를 등록하는 메소드
    private void registerListener() {

        View.OnClickListener onClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.departureButton:
                        ((ShortestPathActivity) getActivity()).generateStationInformationFragment(graph.getVertices().get(minTransferPath.get(0)));
                        break;

                    case R.id.arrivalButton:
                        ((ShortestPathActivity) getActivity()).generateStationInformationFragment(graph.getVertices().get(minTransferPath.get(minTransferPath.size() - 1)));
                        break;

                    case R.id.zoomButton:
                        ((ShortestPathActivity) getActivity()).generateStationInformationFragment(minTransferPath, btnBackgrounds, CustomAppGraph.SearchType.MIN_TRANSFER);
                        break;

                    case R.id.reportButton:
                        Intent email = new Intent(Intent.ACTION_SEND);
                        email.setType("plain/text");
                        String[] address = {"wndtjq0510@gmail.com"};
                        email.putExtra(Intent.EXTRA_EMAIL, address);
                        email.putExtra(Intent.EXTRA_SUBJECT, "");
                        email.putExtra(Intent.EXTRA_TEXT, "잘못된 정보를 입력해주세요.");
                        startActivity(email);
                        break;
                }
            }
        };

        departureButton.setOnClickListener(onClickListener);
        arrivalButton.setOnClickListener(onClickListener);
        zoomButton.setOnClickListener(onClickListener);
        reportButton.setOnClickListener(onClickListener);
    }

    // 각각 departureButton , arrivalButton 의 중간에 오도록
    // departureLine , arrivalLine 의 marginLeft, marginRight 를 설정한다.
    private void setLineTextViewMargin() {
        float density = this.getResources().getDisplayMetrics().density;        // dp 와 px 사이를 변환할 때 필요한 변수

        ConstraintLayout.LayoutParams dLayoutParams = (ConstraintLayout.LayoutParams) departureButton.getLayoutParams();
        int halfDepartureWidth = (int)(dLayoutParams.width / density + 0.5) / 2;            // px to dp

        ConstraintLayout.LayoutParams dLineLayoutParams = (ConstraintLayout.LayoutParams) departureLine.getLayoutParams();
        ConstraintLayout.LayoutParams aLineLayoutParams = (ConstraintLayout.LayoutParams) arrivalLine.getLayoutParams();
        int halfDepartureLineWidth = (int)(dLineLayoutParams.width / density + 0.5) / 2;    // px to dp
        int margin = (25 + halfDepartureWidth) - halfDepartureLineWidth;                    // px to dp

        dLineLayoutParams.setMarginStart((int)(margin * density + 0.5));
        aLineLayoutParams.setMarginEnd((int)(margin * density + 0.5));

    }

    // midLine 에 그라데이션을 넣는 메소드. 출발역의 호선색 ~ 도착역의 호선색
    private void setMidLineGradient() {
        int departureLine = lines.get(0);
        int arrivalLine = lines.get(lines.size() - 1);

        GradientDrawable bgShape = (GradientDrawable) midLine.getBackground();
        bgShape.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        int[] colors = { lineColors.get(departureLine), lineColors.get(arrivalLine) };
        bgShape.setColors(colors);
    }

    // 소요시간, 소요거리, 소요비용, 환승횟수를 텍스트뷰에 적용시킨다.
    private void setCostTextView() {
        String time = convertTime(costs.get(CustomAppGraph.SearchType.MIN_TIME.ordinal()));
        costTime.setText(time);

        String distance = convertDistance(costs.get(CustomAppGraph.SearchType.MIN_DISTANCE.ordinal()));
        costDistance.setText(distance);

        String cost = convertCost(costs.get(CustomAppGraph.SearchType.MIN_COST.ordinal()));
        costCost.setText(cost);

        String transfer = costs.get(CustomAppGraph.SearchType.MIN_TRANSFER.ordinal()) + "회";
        costTransfer.setText(transfer);
    }

    // 초를 시간, 분, 초로 변환하는 메소드
    private String convertTime(int time) {
        String output = "";

        if (time >= 3600000) {
            // 단순 방어용 코드
            int temp = time / 3600;
            time %= 3600;
            StringBuffer sb = new StringBuffer();
            sb.append(Integer.toString(temp));
            int lastIndex = sb.length() - 1;

            while (lastIndex > 2) {
                lastIndex -= 2;
                sb.insert(lastIndex, ",");
                lastIndex--;
            }

            sb.append("시간 ");
            output += sb.toString();

        } else if (time >= 3600) {
            output += time / 3600 + "시간 ";
            time %= 3600;
        }

        if (time >= 60) {
            output += time / 60 + "분 ";
            time %= 60;
        }
        if (time > 0) {
            output += time + "초";
        }

        return output;
    }

    // , 로 1000 단위를 구분하게 하는 메소드
    private String convertDistance(int distance) {
        StringBuffer sb = new StringBuffer();
        sb.append(Integer.toString(distance));
        int lastIndex = sb.length() - 1;

        while (lastIndex > 2) {
            lastIndex -= 2;
            sb.insert(lastIndex, ",");
            lastIndex--;
        }

        sb.append("m");
        return sb.toString();
    }

    // , 로 1000 단위를 구분하게 하는 메소드
    private String convertCost(int cost) {
        StringBuffer sb = new StringBuffer();
        sb.append(Integer.toString(cost));
        int lastIndex = sb.length() - 1;

        while (lastIndex > 2) {
            lastIndex -= 2;
            sb.insert(lastIndex, ",");
            lastIndex--;
        }

        sb.append("원");
        return sb.toString();
    }
}
