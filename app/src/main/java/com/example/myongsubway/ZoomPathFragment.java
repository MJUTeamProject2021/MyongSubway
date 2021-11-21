package com.example.myongsubway;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ZoomPathFragment extends Fragment {
    private ArrayList<Integer> path;                // 탐색한 경로를 담고있는 리스트
    private ArrayList<Integer> lines;               // 경로의 각 역의 호선을 담고있는 리스트
    private ArrayList<Integer> btnBackgrounds;      // 역을 나타내는 버튼들의 background xml 파일의 id를 저장하는 리스트
    private ArrayList<Integer> lineColors;          // 호선의 색들을 담고있는 리스트

    private CustomAppGraph graph;

    final int stationButtonWidthDpBig = 100;        // 큰 역 버튼의 가로 dp 값
    final int stationButtonWidthDpSmall = 60;       // 작은 역 버튼의 가로 dp 값
    final int stationButtonHeightDp = 60;           // 역 버튼의 세로 dp 값

    final int dottedLineMarginDp = 5;               // 점선의 마진 dp 값 (top + bottom)

    final int dottedLineHeightDp = 85;              // 점선의 세로 dp 값

    final int simpleLineWidthDp = 10;               // 역버튼 사이의 선의 가로 dp 값
    final int simpleLineHeightDp = 40;              // 역버튼 사이의 선의 세로 dp 값
    
    public ZoomPathFragment(ArrayList<Integer> _path, ArrayList<Integer> _lines, ArrayList<Integer> _btnBackgrounds, ArrayList<Integer> _lineColors) {
        path = _path;
        lines = _lines;
        btnBackgrounds = _btnBackgrounds;
        lineColors = _lineColors;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_zoom_path, container, false);

        // 역 버튼을 생성한다.
        createStation(v);

        // 잘못된 정보 신고 버튼의 클릭 이벤트를 등록한다.
        registerListener(v);

        return v;
    }

    // 확대 프래그먼트에 들어갈 역 버튼과 선을 그려주는 메소드
    private void createStation(View v) {
        graph = (CustomAppGraph) getActivity().getApplicationContext();                 // 그래프 데이터에 접근하기 위한 graph 변수

        float density = this.getResources().getDisplayMetrics().density;                // dp 와 px 사이를 변환할 때 필요한 변수

        for (int i = 0; i < path.size(); i++) {
            if (i == path.size() - 1) {
                // 마지막 역버튼
                createButton(v, i, density, stationButtonWidthDpBig, stationButtonHeightDp);
            } else if (i == 0) {
                createLineAndButton(v, i, density, stationButtonWidthDpBig, stationButtonHeightDp);
                //createButton(v, i, density, stationButtonWidthDpBig, stationButtonHeightDp);
                createSimpleLine(v, i, density);
            } else {
                // 환승
                if (lines.get(i) != lines.get(i - 1)) {
                    createButton(v, i, density, stationButtonWidthDpBig, stationButtonHeightDp, lines.get(i - 1));
                    createTransferButton(v, i, density);
                } else {
                    createButton(v, i, density, stationButtonWidthDpSmall, stationButtonHeightDp);
                    createSimpleLine(v, i, density);
                }
            }
        }
    }




    // 역버튼을 생성하는 메소드.
    private void createButton(View v, int index, float density, int width, int height) {
        LinearLayout btnContainer = (LinearLayout) v.findViewById(R.id.btnContainer);   // 버튼들을 담는 리니어레이아웃
        CustomAppGraph.Vertex vertex = graph.getVertices().get(path.get(index));        // 현재 역 버튼이 나타내는 Vertex 객체

        // 역 버튼 만들기
        Button btn = new AppCompatButton(getActivity());
        btn.setText(vertex.getVertex());
        btn.setBackgroundResource(btnBackgrounds.get(lines.get(index)));
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams((int)(width * density + 0.5), (int)(height * density + 0.5));
        btn.setLayoutParams(btnParams);

        // 역 버튼을 누르면 해당 역의 정보를 나타내는 프래그먼트를 호출한다.
        btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ShortestPathActivity) getActivity()).generateStationInformationFragment(vertex);
            }
        });

        // 뷰를 레이아웃에 추가한다.
        btnContainer.addView(btn);
    }

    // 역버튼을 다른 호선의 색으로 생성하는 메소드
    private void createButton(View v, int index, float density, int width, int height, int _line) {
        LinearLayout btnContainer = (LinearLayout) v.findViewById(R.id.btnContainer);   // 버튼들을 담는 리니어레이아웃
        CustomAppGraph.Vertex vertex = graph.getVertices().get(path.get(index));        // 현재 역 버튼이 나타내는 Vertex 객체

        // 역 버튼 만들기
        Button btn = new AppCompatButton(getActivity());
        btn.setText(vertex.getVertex());
        btn.setBackgroundResource(btnBackgrounds.get(_line));
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams((int)(width * density + 0.5), (int)(height * density + 0.5));
        btn.setLayoutParams(btnParams);

        // 역 버튼을 누르면 해당 역의 정보를 나타내는 프래그먼트를 호출한다.
        btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ShortestPathActivity) getActivity()).generateStationInformationFragment(vertex);
            }
        });

        // 뷰를 레이아웃에 추가한다.
        btnContainer.addView(btn);
    }

    // 같은 호선내의 역버튼을 잇는 선을 생성하는 메소드
    private void createSimpleLine(View v, int index, float density) {
        LinearLayout btnContainer = (LinearLayout) v.findViewById(R.id.btnContainer);   // 버튼들을 담는 리니어레이아웃
        CustomAppGraph.Vertex vertex = graph.getVertices().get(path.get(index));        // 현재 역 버튼이 나타내는 Vertex 객체

        // 역 버튼을 잇는 선
        ImageView line = new AppCompatImageView(getActivity());
        line.setBackgroundResource(R.drawable.simple_line);
        ((GradientDrawable) line.getBackground()).setColor(lineColors.get(lines.get(index)));
        LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams((int)(simpleLineWidthDp * density + 0.5), (int)(simpleLineHeightDp * density + 0.5));
        line.setLayoutParams(lineParams);

        // 뷰를 레이아웃에 추가한다.
        btnContainer.addView(line);
    }

    // 역버튼을 잇는 선을 다른 호선의 색으로 생성하는 메소드
    private void createSimpleLine(View v, int index, float density, int _line) {
        LinearLayout btnContainer = (LinearLayout) v.findViewById(R.id.btnContainer);   // 버튼들을 담는 리니어레이아웃
        CustomAppGraph.Vertex vertex = graph.getVertices().get(path.get(index));        // 현재 역 버튼이 나타내는 Vertex 객체

        // 역 버튼을 잇는 선
        ImageView line = new AppCompatImageView(getActivity());
        line.setBackgroundResource(R.drawable.simple_line);
        ((GradientDrawable) line.getBackground()).setColor(lineColors.get(_line));
        LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams((int)(simpleLineWidthDp * density + 0.5), (int)(simpleLineHeightDp * density + 0.5));
        line.setLayoutParams(lineParams);

        // 뷰를 레이아웃에 추가한다.
        btnContainer.addView(line);
    }

    // 환승하는 역버튼을 생성하는 메소드
    private void createTransferButton(View v, int index, float density) {
        createMidLayout(v, index, density);
        createLineAndButton(v, index, density, stationButtonWidthDpBig, stationButtonHeightDp);
        //createButton(v, index, density, stationButtonWidthDpBig, stationButtonHeightDp);
        createSimpleLine(v, index, density);
    }

    private void createLineAndButton(View v, int index, float density, int width, int height) {
        LinearLayout btnContainer = (LinearLayout) v.findViewById(R.id.btnContainer);   // 버튼들을 담는 리니어레이아웃
        CustomAppGraph.Vertex vertex = graph.getVertices().get(path.get(index));        // 현재 역 버튼이 나타내는 Vertex 객체
        RelativeLayout relativeLayout = new RelativeLayout(getActivity());

        // 역 버튼 만들기
        Button btn = new AppCompatButton(getActivity());
        btn.setId(index + 1);
        btn.setText(vertex.getVertex());
        btn.setBackgroundResource(btnBackgrounds.get(lines.get(index)));
        RelativeLayout.LayoutParams btnParams = new RelativeLayout.LayoutParams((int)(width * density + 0.5), (int)(height * density + 0.5));
        btnParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        btn.setLayoutParams(btnParams);

        // 역 버튼을 누르면 해당 역의 정보를 나타내는 프래그먼트를 호출한다.
        btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ShortestPathActivity) getActivity()).generateStationInformationFragment(vertex);
            }
        });

        // 내리는문을 나타내는 텍스트뷰
        TextView textView = new AppCompatTextView(getActivity());
        textView.setText(lines.get(index) + "호선");
        RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, (int)(height * density + 0.5));
        textParams.rightMargin = (int)(20 * density + 0.5);
        textView.setGravity(Gravity.CENTER);
        textParams.addRule(RelativeLayout.LEFT_OF, btn.getId());
        textView.setLayoutParams(textParams);

        // 뷰를 레이아웃에 추가한다.
        relativeLayout.addView(btn);
        relativeLayout.addView(textView);
        btnContainer.addView(relativeLayout);
    }

    // 환승을 나타내는 레이아웃을 생성하는 메소드
    private void createMidLayout(View v, int index, float density) {
        CustomAppGraph.Vertex vertex = graph.getVertices().get(path.get(index));        // 환승하는 역을 나타내는 vertex

        LinearLayout btnContainer = (LinearLayout) v.findViewById(R.id.btnContainer);   // 버튼들을 담는 리니어레이아웃
        RelativeLayout midLinear = new RelativeLayout(getActivity());                   // 아이콘, 점선, 텍스트를 담는 레이아웃

        // 점선을 그리는 이미지뷰를 생성
        ImageView dottedLine = new AppCompatImageView(getActivity());
        dottedLine.setId(index + 1);
        dottedLine.setBackgroundResource(R.drawable.dotted_line_vertical);
        dottedLine.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        RelativeLayout.LayoutParams lineParams = new RelativeLayout.LayoutParams((int)(dottedLineHeightDp * density + 0.5), (int)(dottedLineHeightDp * density + 0.5));
        lineParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        lineParams.topMargin = (int)(dottedLineMarginDp * density + 0.5);
        lineParams.bottomMargin = (int)(dottedLineMarginDp * density + 0.5);
        dottedLine.setLayoutParams(lineParams);

        // 내리는문을 나타내는 텍스트뷰
        TextView textView = new AppCompatTextView(getActivity());
        textView.setText("내리는문 " + vertex.getDoorDirection());
        RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, (int)(dottedLineHeightDp * density + 0.5));
        textView.setGravity(Gravity.CENTER);
        textParams.addRule(RelativeLayout.RIGHT_OF, dottedLine.getId());
        textView.setLayoutParams(textParams);

        // 걷는 아이콘을 나타내는 이미지뷰
        ImageView walkIcon = new AppCompatImageView(getActivity());
        walkIcon.setBackgroundResource(R.mipmap.ic_walk_foreground);
        RelativeLayout.LayoutParams iconParams = new RelativeLayout.LayoutParams((int) (dottedLineHeightDp * density + 0.5), (int) (dottedLineHeightDp * density + 0.5));
        iconParams.addRule(RelativeLayout.LEFT_OF, dottedLine.getId());
        walkIcon.setLayoutParams(iconParams);

        // 뷰와 레이아웃을 레이아웃에 추가한다.
        midLinear.addView(walkIcon);
        midLinear.addView(dottedLine);
        midLinear.addView(textView);
        btnContainer.addView(midLinear);
    }

    // 버튼의 클릭이벤트를 등록하는 메소드
    private void registerListener(View v) {
        Button reportButton = v.findViewById(R.id.reportButton);
        reportButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.setType("plain/text");
                String[] address = {"email@address.com"};
                email.putExtra(Intent.EXTRA_EMAIL, address);
                email.putExtra(Intent.EXTRA_SUBJECT, "");
                email.putExtra(Intent.EXTRA_TEXT, "잘못된 정보를 입력해주세요.");
                startActivity(email);
            }
        });
    }
}
