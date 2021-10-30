package com.example.myongsubway;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import jxl.Sheet;
import jxl.Workbook;

// TODO : 그래프, 역 등의 데이터 사용하는 방법 by 이하윤
/*
이 클래스는 모든 액티비티에서 접근할 수 있는 데이터를 모아두는 클래스입니다. 
모든 코드는 추후 변경 가능합니다. 필요한 기능, 데이터, getter 메소드 등은 다같이 얘기해보고 정하면 좋을 것 같습니다.
해당 클래스의 데이터를 사용하기 위해선 액티비티에서 해당 클래스의 객체가 필요합니다.
해당 클래스의 객체는 (CustomAppGraph) getApplicationContext(); 을 통해 얻을 수 있습니다. (따로 변수에 할당하여 사용하는 것을 추천합니다.)
(좀 더 상세한 사용방법은 ShortestPathActivity.java 의 onCreate() 함수의 초기화 부분을 참고)
데이터는 해당 클래스의 객체로 아래의 getter 메소드를 호출해 사용할 수 있습니다.
내부의 클래스를 참조하기 위해선 CustomAppGraph.~ 로 가능합니다. (CustomAppGraph.SearchType, CustomAppGraph.Edge, CustomAppGraph.Vertex)
아래는 데이터들의 설명입니다. 각 설명의 사용예시는 간단한 예이므로 실제 액티비티에서 사용할 때는 해당 클래스의 객체의 getter 메소드를 통해 접근해야 합니다.
        (map 의 경우 ((CustomAppGraph) getApplicationContext()).getMap().get("101") 처럼 사용)

TODO) enum class SearchType : 경로 탐색의 타입을 나타내는 enum 클래스, ordinal() 메소드로 인덱스(순서)를 얻을 수 있습니다.
TODO) class Edge : 그래프의 간선 (역과 역 사이의 정보를 나타내는 클래스)
TODO) class Vertex : 정점 (역의 정보를 담고 있는 클래스, getter 메소드로 내부의 데이터에 접근할 수 있습니다.)
TODO) HashMap<String, Integer> map : 역의 이름(문자열)을 리스트의 인덱스로 변환시켜주는 map, 101번 역부터 순서대로 0, 1, ...
        (map.get("101") => 0)
TODO) HashMap<Integer, String> reverseMap : 리스트의 index 를 역의 이름(문자열)로 변환시켜주는 reverseMap, 0번 인덱스 부터 순서대로 "101", "102", ...
        (reverseMap.get(0) => "101")
TODO) ArrayList<Vertex> vertices : 역의 정보들을 저장하는 리스트, 역의 이름(문자열)로 원소 Vertex 객체에 접근하려면 vertices.get(map.get("101")) 처럼 map 을 활용하여 접근해야 합니다.
        (vertices.get(map.get("101")).getVertex() => "101")
TODO) ArrayList<ArrayList<Edge>> adjacent : 역 사이의 정보들을 저장하는 리스트, 2차원 배열처럼 사용합니다.
        adjacent.get(map.get("101")).get(map.get("102")).getCost(SearchType TYPE) => 101번역부터 102번역까지 TYPE 에 맞는 필요한 비용을 반환합니다.
TODO) final int STATION_COUNT : 역의 개수를 나타내는 심볼릭 상수, 변경할 수 없게 getter 메소드의 반환값 또한 final 입니다.
TODO) final int EDGE_COUNT : 간선의 개수를 나타내는 심볼릭 상수, 변경할 수 없게 getter 메소드의 반환값 또한 final 입니다.
        산출 기준은 엑셀파일 stations.xls 의 행의 개수 * 2 입니다. (틀릴 수 있음)
 */

// 액티비티 간에 공유되는 데이터를 담는 클래스
// 그래프 자료구조, 다익스트라 알고리즘에 필요한 데이터를 모아두는 클래스
public class CustomAppGraph extends Application {
    public enum SearchType {
        MIN_TIME,       // 최소 시간
        MIN_DISTANCE,   // 최소 거리
        MIN_COST,       // 최소 비용
    }

    public class Edge {
        private int[] edgeData;                 // 역 사이의 정보(걸리는시간, 거리, 비용)를 저장하는 배열

        public Edge(int _elapsedTime, int _distance, int _cost) {
            edgeData = new int[3];
            edgeData[0] = _elapsedTime;         // 걸리는 시간
            edgeData[1] = _distance;            // 거리
            edgeData[2] = _cost;                // 비용
        }

        // Dijkstra() 의 인자로 넘겨진 TYPE 에 해당하는 비용을 반환하는 메소드
        public int getCost(SearchType TYPE) {
            return edgeData[TYPE.ordinal()];
        }
    }

    public class Vertex {
        private String vertex;                          // 역의 이름 (ex. "101")
        private ArrayList<Integer> adjacent;            // 역과 연결된 역을 저장하는 리스트
        private int line;                               // 호선
        private boolean toilet;                         // 역 내 화장실 유무
        private String number;                          // 역 전화번호
        private String doorDirection;                   // 내리는 문 방향
        private String[] stationFacilities;             // 역 내 편의시설을 저장하는 리스트
        private String[] nearbyRestaurants;             // 역 주변 식당을 저장하는 리스트
        private String[] nearbyFacilities;              // 역 주변 시설을 저장하는 리스트

        public Vertex(String _vertex, int _line) {
            adjacent = new ArrayList<Integer>();
            vertex = _vertex;
            line = _line;
        }

        // 역과 연결된 역을 등록하는 메소드
        public void addAdjacent(int _adjacent) {
            adjacent.add(_adjacent);
        }

        // 역의 정보를 등록하는 메소드
        public void addInformation(String _toilet, String _number, String _doorDirection, String _stationFacilities, String _nearbyRestaurants, String _nearbyFacilities) {
            toilet = (Integer.parseInt(_toilet) == 1);
            number = _number;
            doorDirection = _doorDirection;
            stationFacilities = _stationFacilities.split(",");
            nearbyRestaurants = _nearbyRestaurants.split(",");
            nearbyFacilities = _nearbyFacilities.split(",");
        }

        // 디버깅 용 메소드
        public String getInformation() {
            String output = vertex + " : " + String.valueOf(toilet) + " " + number + " " + doorDirection + " ";
            for (String item : stationFacilities) {
                output += item + " ";
            }
            for (String item : nearbyRestaurants) {
                output += item + " ";
            }
            for (String item : nearbyFacilities) {
                output += item + " ";
            }

            return output;
        }

        // getter (안쓰는 메소드는 삭제할 예정)
        public String getVertex() { return vertex; }
        public ArrayList<Integer> getAdjacent() { return adjacent; }
        public int getLine() { return line; }
        public boolean getToilet() { return toilet; }
        public String getNumber() { return number; }
        public String getDoorDirection() { return doorDirection; }
        public String[] getStationFacilities() { return stationFacilities; }
        public String[] getNearbyRestaurants() { return nearbyRestaurants; }
        public String[] getNearbyFacilities() { return nearbyFacilities; }
    }

    private HashMap<String, Integer> map = new HashMap<String, Integer>();          // 역의 이름을 배열의 index 로 변환시키기 위한 map
    private HashMap<Integer, String> reverseMap = new HashMap<Integer, String>();   // 리스트의 index 를 역의 이름으로 변환시키기 위한 map
    private ArrayList<Vertex> vertices;                               // 역의 정보를 저장하는 리스트
    private ArrayList<ArrayList<Edge>> adjacent;                      // 역 사이의 정보를 저장하는 리스트

    private final int STATION_COUNT = 111;       // 역의 개수
    private final int EDGE_COUNT = 278;          // edge 의 개수 (엑셀의 row * 2)


    @Override
    public void onCreate() {
        // 그래프 생성
        createGraph();

        super.onCreate();
    }

    // 초기화, 그래프를 생성하는 함수
    public void createGraph() {
        // 그래프에 필요한 리스트들의 초기화
        adjacent = new ArrayList<ArrayList<Edge>>(EDGE_COUNT);
        vertices = new ArrayList<Vertex>(STATION_COUNT);

        for (int i = 0; i < EDGE_COUNT; i++) {
            ArrayList<Edge> temp = new ArrayList<Edge>(EDGE_COUNT);
            for (int j = 0; j < EDGE_COUNT; j++) {
                temp.add(null);
            }
            adjacent.add(temp);
        }
        for (int i = 0; i < STATION_COUNT; i++) {
            vertices.add(null);
        }

        String from, to;    // 엑셀 파일의 0번째와 1번째 셀을 저장하는 변수 (역 이름)

        // 엑셀 읽기
        try {
            // stations.xls 노선도 읽기
            InputStream stationsIs = getBaseContext().getResources().getAssets().open("stations.xls");
            Workbook stationWb = Workbook.getWorkbook(stationsIs);

            // data.xls 역 정보 읽기
            InputStream dataIs = getBaseContext().getResources().getAssets().open("data.xls");
            Workbook dataWb = Workbook.getWorkbook(dataIs);

            if (stationWb != null && dataWb != null) {
                Sheet stationsSheet = stationWb.getSheet(0);
                Sheet dataSheet = dataWb.getSheet(0);

                if (stationsSheet != null && dataSheet != null) {
                    // stations.xls 를 읽어서 초기화
                    {
                        int colTotal = stationsSheet.getColumns();
                        int rowIndexStart = 1;
                        int rowTotal = stationsSheet.getColumn(colTotal - 1).length;

                        int count = 0;          // 배열의 index 를 세기위한 변수

                        for (int row = rowIndexStart; row < rowTotal; row++) {
                            from = stationsSheet.getCell(0, row).getContents(); // 첫번째 셀
                            to = stationsSheet.getCell(1, row).getContents();   // 두번째 셀

                            // 역이름을 배열의 index 로 변환하기 위한 map 사용
                            if (!map.containsKey(from))
                                map.put(from, count++);
                            if (!map.containsKey(to))
                                map.put(to, count++);

                            // 배열의 index 를 역이름으로 변환하기 위한 reverseMap
                            if (!reverseMap.containsKey(map.get(from)))
                                reverseMap.put(map.get(from), from);
                            if (!reverseMap.containsKey(map.get(to)))
                                reverseMap.put(map.get(to), to);

                            // 역의 정보를 등록
                            Vertex vFrom = new Vertex(from, Integer.parseInt(from) / 100);
                            Vertex vTo = new Vertex(to, Integer.parseInt(to) / 100);

                            // 현재 from 에 해당하는 역이 등록되지 않았다면 추가하고
                            // from 에 연결된 역에 to 를 추가
                            if (vertices.get(map.get(from)) == null) {
                                Log.d("test", from + " is null");
                                vertices.set(map.get(from), vFrom);
                            } else {
                                Log.d("test", from + " is not null");
                            }

                            vertices.get(map.get(from)).addAdjacent(map.get(to));

                            // 현재 to 에 해당하는 역이 등록되지 않았다면 추가하고
                            // to 에 연결된 역에 from 을 추가
                            if (vertices.get(map.get(to)) == null) {
                                Log.d("test", to + " is null");
                                vertices.set(map.get(to), vTo);
                            } else {
                                Log.d("test", to + " is not null");
                            }

                            vertices.get(map.get(to)).addAdjacent(map.get(from));

                            // adjacent 2차원 배열에서 관리하기 위해 from 과 to 사이를 잇는 Edge 객체를 만들어 등록 (3, 4, 5 번째 셀)
                            adjacent.get(map.get(from)).set(map.get(to), (new Edge(Integer.parseInt(stationsSheet.getCell(2, row).getContents()),
                                    Integer.parseInt(stationsSheet.getCell(3, row).getContents()),
                                    Integer.parseInt(stationsSheet.getCell(4, row).getContents()))));
                            adjacent.get(map.get(to)).set(map.get(from), (new Edge(Integer.parseInt(stationsSheet.getCell(2, row).getContents()),
                                    Integer.parseInt(stationsSheet.getCell(3, row).getContents()),
                                    Integer.parseInt(stationsSheet.getCell(4, row).getContents()))));
                        }
                    }

                    // 추가로 data.xls 를 읽어서 vertices 업데이트
                    {
                        int colTotal = dataSheet.getColumns();
                        int rowIndexStart = 1;
                        int rowTotal = dataSheet.getColumn(colTotal - 1).length;

                        int count = 0;          // 배열의 index 를 세기위한 변수

                        // stations.xls 를 읽어서 초기화
                        for (int row = rowIndexStart; row < rowTotal; row++) {
                            String index = dataSheet.getCell(0, row).getContents();
                            String toilet = dataSheet.getCell(1, row).getContents();
                            String number = dataSheet.getCell(2, row).getContents();
                            String doorDirection = dataSheet.getCell(3, row).getContents();
                            String stationFacilities = dataSheet.getCell(4, row).getContents();
                            String nearbyRestaurants = dataSheet.getCell(5, row).getContents();
                            String nearbyFacilities = dataSheet.getCell(6, row).getContents();
                            vertices.get(map.get(index)).addInformation(toilet, number, doorDirection, stationFacilities, nearbyRestaurants, nearbyFacilities);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // getter
    public ArrayList<Vertex> getVertices() { return vertices; }
    public ArrayList<ArrayList<Edge>> getAdjacent() { return adjacent; }
    public HashMap<String, Integer> getMap() { return map; }
    public HashMap<Integer, String> getReverseMap() { return reverseMap; }
    public final int getEdgeCount() { return EDGE_COUNT; }
    public final int getStationCount() { return STATION_COUNT; }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
