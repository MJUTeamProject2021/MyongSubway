package com.example.myongsubway;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
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
    private ArrayList<Integer> btnBackgrounds;      // 역을 나타내는 버튼들의 background xml 파일의 id를 저장하는 리스트
    private ArrayList<Integer> lineColors;          // 호선의 색들을 담고있는 리스트

    final int stationButtonWidthDpBig = 100;        // 큰 역 버튼의 가로 dp 값
    final int stationButtonWidthDpSmall = 60;       // 작은 역 버튼의 가로 dp 값
    final int stationButtonHeightDp = 60;           // 역 버튼의 세로 dp 값

    final int dottedLineMarginDp = 5;               // 점선의 마진 dp 값 (top + bottom)

    final int dottedLineHeightDp = 85;              // 점선의 세로 dp 값

    final int simpleLineWidthDp = 10;               // 역버튼 사이의 선의 가로 dp 값
    final int simpleLineHeightDp = 40;              // 역버튼 사이의 선의 세로 dp 값
    
    public ZoomPathFragment(ArrayList<Integer> _path, ArrayList<Integer> _btnBackgrounds, ArrayList<Integer> _lineColors) {
        path = _path;
        btnBackgrounds = _btnBackgrounds;
        lineColors = _lineColors;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_zoom_path, container, false);

        // 역 버튼을 생성한다.
        createStationButton(v);

        // 잘못된 정보 신고 버튼의 클릭 이벤트를 등록한다.
        registerListener(v);

        return v;
    }

    private void createStationButton(View v) {
        CustomAppGraph graph = (CustomAppGraph) getActivity().getApplicationContext();
        float density = this.getResources().getDisplayMetrics().density;                // dp 와 px 사이를 변환할 때 필요한 변수

        int prevLine = -1;      // 이전의 역 버튼이 가르키는 호선

        LinearLayout btnContainer = (LinearLayout) v.findViewById(R.id.btnContainer);   // 버튼들을 담는 리니어레이아웃

        for (int i = 0; i < path.size(); i++) {

            // 현재 역 버튼이 나타내는 Vertex 객체
            CustomAppGraph.Vertex vertex = graph.getVertices().get(path.get(i));

            // 역 버튼 만들기
            Button btn = new AppCompatButton(getActivity());
            btn.setText(vertex.getVertex());
            btn.setBackgroundResource(btnBackgrounds.get(vertex.getLine()));

            // 역 버튼을 누르면 해당 역의 정보를 나타내는 프래그먼트를 호출한다.
            btn.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ShortestPathActivity) getActivity()).generateStationInformationFragment(vertex);
                }
            });

            // 역 버튼을 연결하는 Line
            ImageView line = new AppCompatImageView(getActivity());

            // 역 버튼과 그 사이를 연결하는 선의 상세사항을 설정하기 위한 LayoutParams
            LinearLayout.LayoutParams btnParams = null;

            // 환승을 나타낼 때만 필요한 뷰
            RelativeLayout midLinear = null;
            TextView textView = null;
            ImageView walkIcon = null;

            // 환승 처리
            if (vertex.getLine() != prevLine) {
                // 큰 역 버튼의 LayoutParams 설정
                btnParams = new LinearLayout.LayoutParams((int)(stationButtonWidthDpBig * density + 0.5), (int)(stationButtonHeightDp * density + 0.5));

                midLinear = new RelativeLayout(getActivity());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                midLinear.setLayoutParams(layoutParams);

                // 이전역에서 환승하고 새로운 호선이므로 점선을 그려줌
                line.setBackgroundResource(R.drawable.dotted_line_vertical);
                line.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                RelativeLayout.LayoutParams lineParams = new RelativeLayout.LayoutParams((int)(dottedLineHeightDp * density + 0.5), (int)(dottedLineHeightDp * density + 0.5));
                lineParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                lineParams.topMargin = (int)(dottedLineMarginDp * density + 0.5);
                lineParams.bottomMargin = (int)(dottedLineMarginDp * density + 0.5);
                line.setLayoutParams(lineParams);
                line.setId(i);

                textView = new AppCompatTextView(getActivity());
                textView.setText("내리는문 " + vertex.getDoorDirection());
                RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, (int)(dottedLineHeightDp * density + 0.5));
                textView.setGravity(Gravity.CENTER);
                textParams.addRule(RelativeLayout.RIGHT_OF, line.getId());
                textView.setLayoutParams(textParams);

                walkIcon = new AppCompatImageView(getActivity());
                walkIcon.setBackgroundResource(R.mipmap.ic_walk_foreground);
                RelativeLayout.LayoutParams iconParams = new RelativeLayout.LayoutParams((int) (dottedLineHeightDp * density + 0.5), (int) (dottedLineHeightDp * density + 0.5));
                iconParams.addRule(RelativeLayout.LEFT_OF, line.getId());
                walkIcon.setLayoutParams(iconParams);
            } else {
                // 작은 역 버튼의 LayoutParams 설정, 마지막 역 버튼이면 큰 역 버튼으로 설정
                // 현재 역버튼이 마지막 역버튼이면 || 뒤의 조건은 검사하지 않으므로 IndexOutOfBoundsException 예외가 발생하지 않는다.
                if (i == path.size() - 1 || vertex.getLine() != graph.getVertices().get(path.get(i + 1)).getLine())
                    btnParams = new LinearLayout.LayoutParams((int)(stationButtonWidthDpBig * density + 0.5), (int)(stationButtonHeightDp * density + 0.5));
                else
                    btnParams = new LinearLayout.LayoutParams((int)(stationButtonWidthDpSmall * density + 0.5), (int)(stationButtonHeightDp * density + 0.5));

                // 현재 역의 호선과 이전 역의 호선은 동일하므로 같은색의 선 그림
                line.setBackgroundResource(R.drawable.simple_line);
                ((GradientDrawable) line.getBackground()).setColor(lineColors.get(vertex.getLine()));
                LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams((int)(simpleLineWidthDp * density + 0.5), (int)(simpleLineHeightDp * density + 0.5));
                line.setLayoutParams(lineParams);
            }

            // 뷰의 LayoutParams 를 설정해준다.
            btn.setLayoutParams(btnParams);

            
            // 이전 역의 호선을 업데이트
            prevLine = vertex.getLine();

            // 역 버튼의 위에 선을 올린다, 첫번째 역버튼은 선을 제거
            if (midLinear == null) {
                if (i == 0) line = null;
                else btnContainer.addView(line);
            } else {
                if (i == 0) {
                    line = null;
                    textView = null;
                } else {
                    midLinear.addView(line);
                    midLinear.addView(textView);
                    midLinear.addView(walkIcon);
                    btnContainer.addView(midLinear);
                }
            }

            btnContainer.addView(btn);
        }
    }

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
