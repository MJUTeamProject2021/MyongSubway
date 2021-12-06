package com.example.myongsubway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PriorityQueue;

import static android.content.ContentValues.TAG;

public class ShortestPathActivity extends AppCompatActivity {
    private ViewPager2 viewPager;                           // 뷰페이저
    private FragmentStateAdapter pagerAdapter;              // 뷰페이저 어댑터
    private TabLayout tabLayout;                            // 탭들을 담는 탭 레이아웃
    private final List<String> tabElement =
            Arrays.asList("최소시간", "최단거리", "최소비용");  // 탭을 채울 텍스트

    private String departure, arrival;                      // 출발역과 도착역
    private ArrayList<ArrayList<Integer>> paths;            // 출발역 ~ 도착역의 경로를 저장하는 리스트, 순서대로 최소시간, 최단거리, 최소비용의 경로가 저장됨
    private ArrayList<ArrayList<Integer>> allCosts;         // 소요시간, 소요거리, 소요비용, 환승횟수를 저장하는 리스트, 순서대로 최소시간, 최단거리, 최소비용의 경우가 저장됨
    private ArrayList<ArrayList<Integer>> allLines;         // 경로의 각 역의 호선을 저장하는 리스트
    private final int TYPE_COUNT = 3;                       // SearchType 의 경우의 수 (최소시간, 최단거리, 최소비용)
    private final int TRANSFER_WEIGHT = 500;                // 환승 시에 추가되는 가중치
    final int LAST_INDEX = CustomAppGraph.SearchType.TRANSFER.ordinal();

    private ImageButton setAlarmButton;                     // 도착알람 설정 버튼

    private CustomAppGraph graph;                           // 액티비티 간에 공유되는 데이터를 담는 클래스
    private FirebaseAuth mAuth;                             // 파이어베이스의 uid 를 참조하기 위해 필요한 변수

    private ArrayList<Integer> btnBackgrounds;              // 역을 나타내는 버튼들의 background xml 파일의 id를 저장하는 리스트
    private ArrayList<Integer> lineColors;                  // 호선의 색들을 담고있는 리스트

    private ImageButton bookmarkButton;                     // 즐겨찾기 등록 버튼

    private boolean isButtonClicked = false;                // 알람버튼이 눌렸는지를 확인하는 상태변수
    private CustomAppGraph.SearchType pageType;             // 현재 켜져있는 페이지의 타입
    private CustomAppGraph.SearchType buttonType;           // 알람을 등록했을 때 페이지의 타입

    private AlarmManager alarmManager;                      // 알람 등록을 위한 알람매니저
    private int registeredAlarmCount = 0;                   // 등록된 알람의 개수

    public static Context ShortestPathContext;              // AlarmReceiver 에서 해당 액티비티의 메소드를 호출하기 위한 스태틱 변수

    final int IC_NORMAL_ALARM_BUTTON =                      // 알람이 등록되지 않은 평범한 상태의 알람 버튼 아이콘 
            R.mipmap.ic_alarm_foreground;
    final int IC_ANOTHER_SELECTED_ALARM_BUTTON =            // 이미 다른 페이지에서 알람이 등록된 상태의 알람 버튼 아이콘
            R.mipmap.ic_alarm_another_selected_foreground;

    private StationInformationFragment infoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shortest_path);

        // 초기화
        init();

        // 버튼 리스너 설정
        registerListener();

        // 프래그먼트에서 사용할 데이터를 초기화
        initializeBtnBackgrounds();
        initializeLineColors();

        // 툴바 설정
        setToolbar();

        // 다익스트라 알고리즘을 통해 경로탐색, 3가지 SearchType 을 모두 수행한다.
        dijkstra(graph.getMap().get(departure), CustomAppGraph.SearchType.MIN_TIME);
        dijkstra(graph.getMap().get(departure), CustomAppGraph.SearchType.MIN_DISTANCE);
        dijkstra(graph.getMap().get(departure), CustomAppGraph.SearchType.MIN_COST);

        // 뷰페이저2, 탭레이아웃 설정
        setPagerAndTabLayout();

        int time1 = graph.getVertices().get(graph.getMap().get("122")).getTransferDistance();
        int time2 = graph.getVertices().get(graph.getMap().get("503")).getTransferDistance();
        Log.d("test", "time1 : " + time1 + " time2 : " + time2);
    }

    // 초기설정을 한다.
    private void init() {
        // static 변수 초기화
        ShortestPathContext = this;

        // 변수  초기화

        // 액티비티 간에 공유되는 데이터를 담는 클래스의 객체.
        graph = (CustomAppGraph) getApplicationContext();
        if (graph == null) return;

        // 경로를 담는 리스트 초기화
        paths = new ArrayList<ArrayList<Integer>>(TYPE_COUNT);
        for (int i = 0; i < TYPE_COUNT; i++) {
            paths.add(new ArrayList<Integer>());
        }

        // 비용을 담는 리스트 초기화
        allCosts = new ArrayList<ArrayList<Integer>>(TYPE_COUNT);
        for (int i = 0; i < TYPE_COUNT; i++) {
            ArrayList<Integer> temp = new ArrayList<Integer>(4);
            temp.add(-1);
            temp.add(-1);
            temp.add(-1);
            temp.add(-1);
            allCosts.add(temp);
        }

        // 호선을 담는 리스트 초기화
        allLines = new ArrayList<ArrayList<Integer>>(TYPE_COUNT);
        for (int i = 0; i < TYPE_COUNT; i++) {
            allLines.add(new ArrayList<Integer>());
        }

        // MainActivity 가 전송한 데이터 받기
        Intent intent = getIntent();
        departure = intent.getStringExtra("departureStation");
        arrival = intent.getStringExtra("destinationStation");

        // departure 와 arrival 이 올바른 값이 들어있는지 체크한다.
        checkDepartureArrival();

        // 파이어베이스 관련 변수 초기화
        mAuth = FirebaseAuth.getInstance();

        // 즐겨찾기 버튼 참조 및 초기화
        bookmarkButton = findViewById(R.id.bookmarkButton);
        bookmarkButton.setColorFilter(Color.parseColor("#BEBEBE"));
        initBookmarkButton();

        // 도착알람 버튼 참조 및 초기화
        setAlarmButton = findViewById(R.id.setAlarmButton);
        setAlarmButton.setColorFilter(getResources().getColor(R.color.moreGray, null));
        initAlarmButton();
    }

    // 출발역과 도착역을 올바르게 설정한다.
    private void checkDepartureArrival() {
        // Notification 을 터치해 넘어온 경우
        if (departure == null) {
            // SharedPreference 에 저장한 기존의 출발역 도착역을 저장한다.
            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String departure_arrival = sharedPref.getString(getString(R.string.departure_arrival), null);

            if (departure_arrival != null) {
                departure = departure_arrival.split("_")[0];
                arrival = departure_arrival.split("_")[1];
            }
        }
        // 메인액티비티에서 넘어온 경우
        else {
            // departure 와 arrival 을 SharedPreference 에 저장한다.
            String departure_arrival = departure + "_" + arrival;
            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.departure_arrival), departure_arrival);
            editor.apply();
        }
    }

    // 즐겨찾기 버튼을 초기화한다.
    private void initBookmarkButton() {
        if (isContained()) {
            bookmarkButton.setBackgroundResource(R.mipmap.ic_star_selected_foreground);
        } else {
            bookmarkButton.setBackgroundResource(R.mipmap.ic_star_unselected_foreground);
        }
    }

    // 알람 버튼을 초기화한다.
    private void initAlarmButton() {
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        String alarmKey = graph.getAlarmKey();
        String key = departure + "-" + arrival;

        // 등록한 알람의 키와 현재 경로의 키가 다를때
        if (!key.equals(alarmKey)) {
            // 다른 경로에 등록된 알람이 있을 때
            if (alarmKey != null) {
                isButtonClicked = true;
                buttonType = CustomAppGraph.SearchType.NONE;
            }
        }
        else
        // 등록한 알람의 키와 현재 경로의 키가 같을 때 => 알람을 등록했던 경로일 때
        {
            isButtonClicked = true;
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            // 현재 경로와 같은 페이지에 등록된 알람이 있다면
            int index = sharedPref.getInt(alarmKey, -1);
            if (index != -1) {
                CustomAppGraph.SearchType sharedPrefButtonType = CustomAppGraph.SearchType.values()[index];
                buttonType = sharedPrefButtonType;
            }
        }
    }

    // 알람이 하나씩 종료될 때 마다 호출된다.
    // 등록된 모든 알람이 종료되면 등록된 모든 알람을 제거한다.
    public void finishAlarm() {
        // 알람이 모두 종료되면
        if (graph.decreaseAlarmNum() == 0) {
            isButtonClicked = false;

            // 버튼의 모양과 색을 원래대로 돌린다.
            setAlarmButton.setBackgroundResource(R.drawable.bg_white_ripple_stroke);
            setAlarmButton.setImageResource(R.mipmap.ic_alarm_foreground);
            setAlarmButton.setColorFilter(getResources().getColor(R.color.moreGray, null));

            // 현재 등록된 알람을 제거한다.
            graph.destroyAlarm();
        }
    }

    // 알람을 등록한다.
    private void registerAlarm() {
        // 초를 밀리세컨드로 변환하기 위한 상수 , 현재는 디버깅을 위해 10으로 설정.
        // TODO : 실제 시간으로 설정하기 위해선 1000으로 바꿔야함
        final int CONSTANT_FOR_CONVERT = 100;

        // 환승을 위해 하차할 때 마다 알람이 울리도록 설정한다.
        ArrayList<Integer> lines = allLines.get(pageType.ordinal());
        ArrayList<Integer> path = paths.get(pageType.ordinal());

        int prevLine = lines.get(0);            // 이전 호선을 나타낸다. 초기값은 첫번째 역의 호선
        int cumulative = 0;                     // 다음역으로 갈 때마다 누적되는 시간을 나타낸다.
        int alarmCount = 0;                     // 알람을 등록할 때마다 증가하는 변수

        // 호선을 비교하여 알람을 등록할 때를 찾는다.
        for (int i = 0; i < lines.size(); i++) {
            // 현재역까지 걸리는 시간을 갱신한다.
            if (i != 0) {
                cumulative += graph.getAdjacent().get(path.get(i - 1)).get(path.get(i)).getCost(CustomAppGraph.SearchType.MIN_TIME);
            }

            // 현재 역의 호선이 이전 역의 호선과 다른 호선이면 => 환승을 위해 하차해야하는 역이면
            if (lines.get(i) != prevLine) {
                CustomAppGraph.Vertex getOffStation = graph.getVertices().get(path.get(i));
                int halfTimeBeforeGetOff = cumulative - (graph.getAdjacent().get(path.get(i - 1)).get(path.get(i)).getCost(CustomAppGraph.SearchType.MIN_TIME) / 2);
                int oneMinuteAgoBeforeGetOff = cumulative - 60;

                startAlarm(halfTimeBeforeGetOff * CONSTANT_FOR_CONVERT, alarmCount++, getOffStation);
                startAlarm(oneMinuteAgoBeforeGetOff * CONSTANT_FOR_CONVERT, alarmCount++, getOffStation);
            }
            // 가장 마지막의 하차역이면
            else if (i == lines.size() - 1) {
                CustomAppGraph.Vertex getOffStation = graph.getVertices().get(path.get(i));
                int halfTimeBeforeGetOff = cumulative - (graph.getAdjacent().get(path.get(i - 1)).get(path.get(i)).getCost(CustomAppGraph.SearchType.MIN_TIME) / 2);
                int oneMinuteAgoBeforeGetOff = cumulative - 60;

                startAlarm(halfTimeBeforeGetOff * CONSTANT_FOR_CONVERT, alarmCount++, getOffStation);
                startAlarm(oneMinuteAgoBeforeGetOff * CONSTANT_FOR_CONVERT, alarmCount++, getOffStation);
            }

            // 이전역을 갱신한다.
            prevLine = lines.get(i);
        }

        // 등록될 알람의 개수를 저장한다.
        registeredAlarmCount = alarmCount;
        graph.setAlarmCount(registeredAlarmCount);

        // SharedPreference 에 현재 알람 내용을 저장한다.
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String key = departure + "-" + arrival;
        editor.putInt(key, buttonType.ordinal());
        editor.apply();

        // 공유클래스에 현재 등록하는 알람의 키를 저장한다.
        graph.setAlarmKey(key);
    }

    // 새로운 알람을 시작한다.
    private void startAlarm(int elapsedMilliSec, int requestId, CustomAppGraph.Vertex vertex) {
        // 캘린더 객체를 받아와 알람을 울릴 시간을 설정한다.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis() + elapsedMilliSec);

        /** date 포맷을 이용해 알람이 등록되는 시간을 확인한다. */
        /*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.KOREA);
        Log.d("test", (sdf.format(calendar.getTime()).toString()));*/

        // 얻어온 캘린더 객체가 현재의 캘린더 객체보다 이전의 것이면 갱신한다.
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        // 캘린더 객체에 설정된 시간에 맞춰 알람이 울리도록 한다.
        if (alarmManager != null) {
            Intent intent = new Intent(ShortestPathContext, AlarmReceiver.class);

            Bundle bundle = new Bundle();
            bundle.putString("station", vertex.getVertex());
            bundle.putString("doorDirection", vertex.getDoorDirection());
            intent.putExtras(bundle);

            PendingIntent alarmIntent = PendingIntent.getBroadcast(ShortestPathContext, requestId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
        }
    }

    // 등록된 모든 알람을 해제한다. (버튼을 등록한 경로에서 해제할 때)
    public void cancelAlarm() {
        for (int i = 0; i < registeredAlarmCount; i++) {
            Intent intent = new Intent(ShortestPathContext, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(ShortestPathContext, i, intent, PendingIntent.FLAG_NO_CREATE);
            alarmManager.cancel(pendingIntent);
        }

        // 등록된 알람의 개수를 초기화하고 공유클래스의 알람의 개수도 초기화한다.
        registeredAlarmCount = 0;
        graph.setAlarmCount(0);

        // SharedPreference 에 저장된 키-값을 제거한다.
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String key = departure + "-" + arrival;
        editor.remove(key);
        editor.commit();

        // 알람이 모두 해제되었으므로 공유클래스의 키값을 초기화한다.
        graph.setAlarmKey(null);
    }

    // 버튼에 클릭리스너를 등록하는 메소드
    private void registerListener() {
        View.OnClickListener onClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.bookmarkButton:

                        // 로그인된 상태일 때만 즐겨찾기 가능
                        if (graph.isLogined()) {
                            if (isContained()) {
                                Toast.makeText(ShortestPathContext, "즐겨찾기 해제되었습니다.", Toast.LENGTH_SHORT).show();
                                // 이미 켜져있을 때, 버튼의 이미지를 빈 별의 이미지로 바꾼다.
                                bookmarkButton.setBackgroundResource(R.mipmap.ic_star_unselected_foreground);
                                // 해당 경로의 즐겨찾기를 제거한다.
                                removeBookmarkedRoute();
                            } else {
                                Toast.makeText(ShortestPathContext, "즐겨찾기 설정되었습니다.", Toast.LENGTH_SHORT).show();
                                // 이미 꺼져있을 때, 버튼의 이미지를 노란 별의 이미지로 바꾼다.
                                bookmarkButton.setBackgroundResource(R.mipmap.ic_star_selected_foreground);
                                // 해당 경로의 즐겨찾기를 추가한다.
                                addBookmarkedRoute();
                            }
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ShortestPathContext);
                            builder.setTitle("로그인이 필요합니다.");
                            builder.setMessage("로그인 창으로 이동하시겠습니까?");
                            builder.setPositiveButton("확인",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(ShortestPathContext, SignInActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                            builder.setNegativeButton("취소",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });

                            AlertDialog alert = builder.create();
                            alert.setOnShowListener(new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(DialogInterface dialog) {
                                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);
                                    alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLUE);
                                }
                            });

                            alert.show();
                        }
                        break;

                    case R.id.setAlarmButton:
                        if (!isButtonClicked) {
                            buttonType = pageType;
                            isButtonClicked = true;

                            // 버튼의 모양과 색을 눌려져 있는 상태의 경우로 바꾼다.
                            setAlarmButton.setBackgroundResource(R.drawable.bg_white_ripple_stroke_red);
                            setAlarmButton.setColorFilter(Color.WHITE);

                            // 알람을 등록한다.
                            registerAlarm();

                        } else {
                            if (pageType == buttonType) {
                                // 버튼을 눌렀던 페이지
                                // 알람 해제 ... 해제할건지 물어봄
                                AlertDialog.Builder builder = new AlertDialog.Builder(ShortestPathContext);
                                builder.setMessage("설정된 알람을 해제하시겠습니까?");
                                builder.setPositiveButton("확인",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                isButtonClicked = false;

                                                // 버튼의 모양과 색을 원래대로 돌린다.
                                                setAlarmButton.setBackgroundResource(R.drawable.bg_white_ripple_stroke);
                                                setAlarmButton.setImageResource(R.mipmap.ic_alarm_foreground);
                                                setAlarmButton.setColorFilter(getResources().getColor(R.color.moreGray, null));

                                                // 등록된 알람을 취소한다.
                                                cancelAlarm();
                                            }
                                        });
                                builder.setNegativeButton("취소",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });

                                AlertDialog alert = builder.create();
                                alert.setOnShowListener(new DialogInterface.OnShowListener() {
                                    @Override
                                    public void onShow(DialogInterface dialog) {
                                        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);
                                        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLUE);
                                    }
                                });
                                alert.show();

                            } else {
                                // 버튼을 눌렀던 페이지와 다른 페이지
                                // 다른 페이지에서 이미 알람을 등록한 상태이므로 새로 등록할 것인지 물어봄
                                AlertDialog.Builder builder = new AlertDialog.Builder(ShortestPathContext);
                                builder.setMessage("기존의 알람을 해제하고 새 알람을 등록하시겠습니까?");
                                builder.setPositiveButton("확인",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                buttonType = pageType;

                                                // 버튼의 모양과 색을 눌려졌을 때로 바꾼다.
                                                setAlarmButton.setBackgroundResource(R.drawable.bg_white_ripple_stroke_red);
                                                setAlarmButton.setImageResource(IC_NORMAL_ALARM_BUTTON);
                                                setAlarmButton.setColorFilter(Color.WHITE);

                                                // 이미 등록된 알람을 해제하고 새로운 알람을 등록한다.
                                                cancelAlarm();
                                                registerAlarm();
                                            }
                                        });
                                builder.setNegativeButton("취소",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });

                                AlertDialog alert = builder.create();
                                alert.setOnShowListener(new DialogInterface.OnShowListener() {
                                    @Override
                                    public void onShow(DialogInterface dialog) {
                                        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);
                                        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLUE);
                                    }
                                });
                                alert.show();

                            }
                        }
                        break;
                }
            }
        };

        // 버튼 클릭 이벤트를 등록한다.
        bookmarkButton.setOnClickListener(onClickListener);
        setAlarmButton.setOnClickListener(onClickListener);
    }

    // 현재 경로를 즐겨찾기에 추가한다.
    private void addBookmarkedRoute() {
        String value = departure + "역" + " - " + arrival + "역";
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("subwayData").document(mAuth.getUid());

        ArrayList<String> list = new ArrayList<String>();
        Map map = new HashMap<String, Object>();

        for(int i = 0; i < graph.getBookmarkedRoute().size(); i++){
            list.add(graph.getBookmarkedRoute().get(i));
        }

        list.add(value);
        graph.setBookmarkedRoute(list);

        map = graph.getBookmarkedMap();

        map.put("즐겨찾는 역", graph.getBookmarkedStation());
        map.put("즐겨찾는 경로", graph.getBookmarkedRoute());

        docRef.set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

    // 현재 경로를 즐겨찾기에서 제거한다.
    private void removeBookmarkedRoute() {
        String value = departure + "역" + " - " + arrival + "역";
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        graph.getBookmarkedRoute().remove(value);
        DocumentReference docRef = db.collection("subwayData").document(mAuth.getUid());
        docRef.update("즐겨찾는 경로", FieldValue.arrayRemove(value));
    }

    // 현재 경로가 즐겨찾기에 등록되어있는지 확인한다.
    private boolean isContained() {
        String value = departure + "역" + " - " + arrival + "역";
        return graph.getBookmarkedRoute().contains(value);
    }

    // 현재 켜져있는 페이지의 타입을 설정한다.
    public void setPageType(CustomAppGraph.SearchType type) {
        pageType = type;

        if (isButtonClicked)
        {
            if (buttonType != pageType) {
                // 버튼의 모양과 색을 다른 페이지에서 눌려져 있는 상태의 경우로 바꾼다.
                setAlarmButton.setBackgroundResource(R.drawable.bg_white_ripple_stroke);
                setAlarmButton.setImageResource(IC_ANOTHER_SELECTED_ALARM_BUTTON);
                setAlarmButton.setColorFilter(Color.RED);
            } else {
                // 버튼의 모양과 색을 눌려져 있는 상태의 경우로 바꾼다.
                setAlarmButton.setBackgroundResource(R.drawable.bg_white_ripple_stroke_red);
                setAlarmButton.setImageResource(IC_NORMAL_ALARM_BUTTON);
                setAlarmButton.setColorFilter(Color.WHITE);
            }
        }
    }

    // 뷰페이저2, 탭레이아웃 설정
    private void setPagerAndTabLayout() {
        // 뷰페이저2와 어댑터를 연결 (반드시 TabLayoutMediator 선언 전에 선행되어야 함)
        viewPager = findViewById(R.id.viewPager);
        pagerAdapter = new VPAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        tabLayout = findViewById(R.id.tabLayout);


        // 뷰페이저2와 탭레이아웃을 연동
        // 탭과 뷰페이저를 연결, 여기서 새로운 탭을 다시 만드므로 레이아웃에서 꾸미지말고 여기서 꾸며야함
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull @NotNull TabLayout.Tab tab, int position) {
                // 탭의 텍스트를 나타낼 텍스트뷰를 만든다.
                // 텍스트뷰의 정렬, 색을 정하고 탭에 적용시킨다.
                TextView textView = new TextView(ShortestPathActivity.this);
                textView.setText(tabElement.get(position));
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(getColor(R.color.tabUnSelectedColor));
                if (position == 0) textView.setTextColor(getColor(R.color.tabSelectedColor));
                tab.setCustomView(textView);
            }
        }).attach();

        // 탭이 선택됐을 때의 액션을 설정하는 부분
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) { // 선택 X -> 선택 O
                TextView textView = (TextView) tab.getCustomView();
                textView.setTextColor(getColor(R.color.tabSelectedColor));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { // 선택 O -> 선택 X
                TextView textView = (TextView) tab.getCustomView();
                textView.setTextColor(getColor(R.color.tabUnSelectedColor));
            }

            public void onTabReselected(TabLayout.Tab tab) { // 선택 O -> 선택 O

            }
        });
    }

    // 툴바를 설정하는 메소드
    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // 기본 텍스트를 숨긴다.
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // 뒤로가기 버튼을 추가
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // 툴바의 액션버튼이 선택됐을때의 기능을 설정하는 메소드
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                if (infoFragment != null) {
                    onBackPressed();
                    infoFragment = null;
                }
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 경로를 계산하는 다익스트라 알고리즘
    private void dijkstra(int here, CustomAppGraph.SearchType TYPE) {
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
                if (this.cost >= vc.cost)
                    return 1;
                else
                    return -1;
            }
        }

        // 각 역에서 갈 수 있는 역을 저장하는 리스트
        // 해당 리스트의 역들은 발견만 한 역일뿐 아직 방문하지 않은 상태이다.
        PriorityQueue<VertexCost> discovered = new PriorityQueue<VertexCost>();

        ArrayList<Integer> best = new ArrayList<Integer>(graph.getStationCount());     // 각 역으로 가는 최단거리를 저장하는 리스트
        ArrayList<Integer> parent = new ArrayList<Integer>(graph.getStationCount());   // 각 역의 이전 역을 저장하는 리스트

        // 리스트 초기화
        for (int i = 0; i < graph.getStationCount(); i++) {
            best.add(Integer.MAX_VALUE);
        }
        for (int i = 0; i < graph.getStationCount(); i++) {
            parent.add(-1);
        }

        // 처음 위치의 정보를 저장
        discovered.add(new VertexCost(here, 0));
        best.set(here, 0);
        parent.set(here, here);

        // 출발역부터 갈 수 있는 모든 정점을 탐색한다.
        while (!discovered.isEmpty()) {
            // 발견한 후보 중 cost 가 가장 작은, 방문할 후보를 찾는다.
            VertexCost bestVC = discovered.remove();

            int cost = bestVC.cost;     // 현재 방문할 역까지의 비용을 저장
            here = bestVC.vertex;       // 현재 방문할 역을 찾은 후보로 변경

            // 더 짧은 경로가 존재한다면 스킵 (새로 찾은 현재 역까지의 비용이 기존의 현재 역까지의 비용보다 크면 스킵)
            if (best.get(here) < cost)
                continue;

            // 방문했을 때 환승여부를 판단하기 위한 이전 역의 호선들과 현재 역의 호선들을 담는 리스트
            ArrayList<Integer> prevLines = graph.getVertices().get(parent.get(here)).getLines();
            ArrayList<Integer> hereLines = graph.getVertices().get(here).getLines();

            // 방문
            // here 에 해당하는 Vertex 객체에서 연결되어 있는 역 정보를 받아와서 방문할 수 있는 모든 역을 발견
            for (int there : graph.getVertices().get(here).getAdjacent()) {
                // 다음 역까지의 비용 (아래에서 환승여부에 따른 가중치 추가)
                int nextCost = best.get(here) + graph.getAdjacent().get(here).get(there).getCost(TYPE);



                // 현재 역이 출발 역이 아니라면
                if (parent.get(here) != here) {
                    // 다음 역의 호선들을 담는 리스트
                    ArrayList<Integer> thereLines = graph.getVertices().get(there).getLines();

                    // 이전 역부터 현재 역까지 이용한 호선
                    int hereLine = 0;

                    // 이전 역부터 현재 역까지 이용한 호선을 구한다.
                    Loop1 :
                    for (int pLine : prevLines) {
                        for (int hLine : hereLines) {
                            if (pLine == hLine) {
                                hereLine = pLine;
                                break Loop1;
                            }
                        }
                    }

                    // 현재 역부터 다음 역까지 이용할 호선
                    int thereLine = 0;

                    // 현재 역부터 다음 역까지 이용할 호선을 구한다.
                    Loop2 :
                    for (int hLine : hereLines) {
                        for (int tLine : thereLines) {
                            if (hLine == tLine) {
                                thereLine = hLine;
                                break Loop2;
                            }
                        }
                    }

                    // 이전 역부터 이용한 호선과 다음 역까지 이용할 호선이 다른 경우 => 환승
                    // 환승이면 가중치를 추가한다.
                    if (hereLine != thereLine) {
                        nextCost += TRANSFER_WEIGHT;

                        // 판정기준에 환승하는데 걸리는 시간을 추가함
                        if (TYPE == CustomAppGraph.SearchType.MIN_TIME) {
                            // here 에서 환승거리
                            int transferDistance = graph.getVertices().get(here).getTransferDistance();
                            // 환승이 불가능한 역이면 -1
                            if (transferDistance != -1) {
                                // 환승하는데 걸리는 시간(분) = 환승 거리(m) / (도보 속도(km/h) * 1000 / 60)
                                int transferTime = (int)(transferDistance / (graph.getWalkSpeed() * 1000 / 60));
                                // 환승하는데 걸리는 시간을 가중치로 추가
                                nextCost += transferTime;
                            }
                        }
                        // 판정기준에 환승 거리를 추가함
                        else if (TYPE == CustomAppGraph.SearchType.MIN_DISTANCE) {
                            // here 에서 환승거리
                            int transferDistance = graph.getVertices().get(here).getTransferDistance();
                            // 환승이 불가능한 역이면 -1
                            if (transferDistance != -1) {
                                // 환승 거리를 가중치로 추가
                                nextCost += transferDistance;
                            }
                        }
                    }
                }

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

        // 각 경로의 역의 호선을 저장한다.
        getPathLines(TYPE);

        // 소요시간, 소요거리, 소요비용을 저장한다.
        calculateAllCosts(paths.get(TYPE.ordinal()), TYPE, best.get(graph.getMap().get(arrival)));
    }

    // 경로의 각 역의 호선을 저장하는 메소드
    private void getPathLines(CustomAppGraph.SearchType TYPE) {
        ArrayList<Integer> path = paths.get(TYPE.ordinal());        // TYPE 에 맞는 경로
        ArrayList<Integer> lines = allLines.get(TYPE.ordinal());    // TYPE 에 맞는 호선 리스트

        for (int index = 0; index < path.size() - 1; index++) {
            int here = path.get(index);
            int there = path.get(index + 1);

            // 현재 역의 호선과 다음 역의 호선 중 같은 호선이 있다면 그 호선이 현재 역의 호선이 됨.
            Loop1 :
            for (int hereLine : graph.getVertices().get(here).getLines()) {
                for (int thereLine : graph.getVertices().get(there).getLines()) {
                    if (hereLine == thereLine) {
                        lines.add(hereLine);
                        break Loop1;
                    }
                }
            }
        }
        
        // 마지막 역의 경우를 처리, 마지막 역 이전 역의 호선과 같은 호선으로 저장
        lines.add(lines.get(lines.size() - 1));
    }

    // 소요시간, 소요거리, 소요비용, 환승횟수를 계산하는 메소드
    private void calculateAllCosts(ArrayList<Integer> path, CustomAppGraph.SearchType TYPE, int best) {
        // 각 경우의 경로탐색에서 소요되는 여러 비용들을 계산한다.
        // 각 경우는 dijkstra 메소드에서 구한 최고(최소)의 값을 각 경우에 대응되는 비용에 추가한다.
        // 이때 환승횟수에 따른 가중치를 제거한다.

        // 환승횟수에 따른 가중치 제거를 위해 환승횟수를 먼저 구하여 저장한다.
        allCosts.get(TYPE.ordinal()).set(LAST_INDEX, calculateElapsed(TYPE));

        allCosts.get(TYPE.ordinal()).set(0, calculateElapsed(path, TYPE, CustomAppGraph.SearchType.MIN_TIME));
        allCosts.get(TYPE.ordinal()).set(1, calculateElapsed(path, TYPE, CustomAppGraph.SearchType.MIN_DISTANCE));
        allCosts.get(TYPE.ordinal()).set(2, calculateElapsed(path, TYPE, CustomAppGraph.SearchType.MIN_COST));
    }

    // 소요시간, 소요거리, 소요비용을 계산한다.
    private int calculateElapsed(ArrayList<Integer> path, CustomAppGraph.SearchType ALL_COST_TYPE, CustomAppGraph.SearchType COST_TYPE) {
        int output = 0;

        for (int pathIndex = 0; pathIndex < path.size() - 1; pathIndex++) {
            output += graph.getAdjacent().get(path.get(pathIndex)).get(path.get(pathIndex + 1)).getCost(COST_TYPE);

            // 소요비용을 계산하는 경우가 아닐 때
            if (COST_TYPE != CustomAppGraph.SearchType.MIN_COST) {
                // ALL_COST_TYPE 인 경로탐색일 때의 경로의 호선을 담는 리스트
                ArrayList<Integer> lines = allLines.get(ALL_COST_TYPE.ordinal());
                
                // 다음역에서 환승일 때
                if (lines.get(pathIndex) != lines.get(pathIndex + 1)) {
                    // 환승하는 역
                    CustomAppGraph.Vertex transferStation = graph.getVertices().get(path.get(pathIndex + 1));
                    // 환승하는 역에서 환승거리
                    int transferDistance = transferStation.getTransferDistance();

                    switch (COST_TYPE) {
                        // 환승 시간 추가 계산
                        case MIN_TIME:
                            // 환승이 불가능한 역이면 -1
                            if (transferDistance != -1) {
                                // 환승하는데 걸리는 시간(분) = 환승 거리(m) / (도보 속도(km/h) * 1000 / 60)
                                int transferTime = (int) (transferDistance / (graph.getWalkSpeed() * 1000 / 60));
                                //Log.d("test", "transferTime : " + transferTime +" station : " + transferStation.getVertex());
                                // 환승하는데 걸리는 시간을 소요시간에 추가
                                output += transferTime * 60;
                            }

                            break;
                            
                        // 환승 거리 추가 계산
                        case MIN_DISTANCE:
                            // 환승이 불가능한 역이면 -1
                            if (transferDistance != -1) {
                                // 환승 거리를 가중치로 추가
                                //Log.d("test", "transferDistance : " + transferDistance);
                                output += transferDistance;
                            }

                            break;
                    }
                }
            }
        }

        return output;
    }

    // 환승횟수를 계산한다.
    private int calculateElapsed(CustomAppGraph.SearchType ALL_COST_TYPE) {
        int output = 0;

        ArrayList<Integer> lines = allLines.get(ALL_COST_TYPE.ordinal());

        for (int pathIndex = 0; pathIndex < lines.size() - 1; pathIndex++) {
            if (lines.get(pathIndex) != lines.get(pathIndex + 1)) {
                output += 1;
            }
        }

        return output;
    }

    // StationInformationFragment 에서 뒤로가기 버튼을 누르면 실행되는 메소드
    // FragmentManager 에서 프래그먼트를 제거하고 툴바를 보이게 만든다.
    public void removeInfoFragment(StationInformationFragment infoFrag) {
        getSupportFragmentManager().beginTransaction().remove(infoFrag).commit();

        showToolbar();
        hideInfoFragmentContainer();
    }

    // 툴바를 보이게 만든다.
    public void showToolbar() {
        getSupportActionBar().show();
    }

    // 역정보 프래그먼트가 나타나는 레이아웃을 가린다.
    public void hideInfoFragmentContainer() {
        FrameLayout infoLayoutContainer = findViewById(R.id.station_info_fragment_container);
        infoLayoutContainer.setVisibility(View.GONE);
    }

    // 역정보 프래그먼트를 띄우는 메소드
    public void generateStationInformationFragment(CustomAppGraph.Vertex vertex) {
        // 툴바를 가린다.
        getSupportActionBar().hide();

        // 역정보 프래그먼트가 나타나는 레이아웃을 보이게 만든다.
        FrameLayout infoLayoutContainer = findViewById(R.id.station_info_fragment_container);
        infoLayoutContainer.setVisibility(View.VISIBLE);

        // 역정보 프래그먼트를 띄운다.
        infoFragment = new StationInformationFragment(vertex, graph, true);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.add(R.id.station_info_fragment_container, infoFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    
    // 확대경로 프래그먼트를 띄우는 메소드
    public void generateZoomPathFragment(ArrayList<Integer> path, ArrayList<Integer> btnBackgrounds, CustomAppGraph.SearchType TYPE) {
        // 확대경로 프래그먼트를 띄운다.
        ZoomPathFragment frag = new ZoomPathFragment(path, allLines.get(TYPE.ordinal()), btnBackgrounds, lineColors);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.add(R.id.zoom_path_fragment_container, frag);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // 호선에 따른 역버튼 배경 xml 을 담는 메소드
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

    // 호선에 따른 색을 담는 메소드
    private void initializeLineColors() {
        lineColors = new ArrayList<Integer>(10);
        lineColors.add(-1);
        lineColors.add(getResources().getColor(R.color.line1Color, null));
        lineColors.add(getResources().getColor(R.color.line2Color, null));
        lineColors.add(getResources().getColor(R.color.line3Color, null));
        lineColors.add(getResources().getColor(R.color.line4Color, null));
        lineColors.add(getResources().getColor(R.color.line5Color, null));
        lineColors.add(getResources().getColor(R.color.line6Color, null));
        lineColors.add(getResources().getColor(R.color.line7Color, null));
        lineColors.add(getResources().getColor(R.color.line8Color, null));
        lineColors.add(getResources().getColor(R.color.line9Color, null));

    }

    // 뷰페이저 어댑터 클래스
    private class VPAdapter extends FragmentStateAdapter {
        private final ArrayList<Fragment> items;

        public VPAdapter(FragmentActivity fa) {
            super(fa);
            items = new ArrayList<Fragment>();
            
            // 최소시간 경로탐색을 나타내는 프래그먼트
            items.add(new MinTimePathFragment(paths.get(CustomAppGraph.SearchType.MIN_TIME.ordinal()),
                    allLines.get(CustomAppGraph.SearchType.MIN_TIME.ordinal()),
                    allCosts.get(CustomAppGraph.SearchType.MIN_TIME.ordinal()), graph, btnBackgrounds, lineColors));

            // 최단거리 경로탐색을 나타내는 프래그먼트
            items.add(new MinDistancePathFragment(paths.get(CustomAppGraph.SearchType.MIN_DISTANCE.ordinal()),
                    allLines.get(CustomAppGraph.SearchType.MIN_DISTANCE.ordinal()),
                    allCosts.get(CustomAppGraph.SearchType.MIN_DISTANCE.ordinal()), graph, btnBackgrounds, lineColors));

            // 최소비용 경로탐색을 나타내는 프래그먼트
            items.add(new MinCostPathFragment(paths.get(CustomAppGraph.SearchType.MIN_COST.ordinal()),
                    allLines.get(CustomAppGraph.SearchType.MIN_COST.ordinal()),
                    allCosts.get(CustomAppGraph.SearchType.MIN_COST.ordinal()), graph, btnBackgrounds, lineColors));
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