package com.example.myongsubway;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;
import jxl.Cell;

// TODO : 그래프, 역 등의 데이터 사용하는 방법 by 이하윤
/**
이 클래스는 모든 액티비티에서 접근할 수 있는 데이터를 모아두는 클래스입니다.
모든 코드는 추후 변경 가능합니다. 필요한 기능, 데이터, getter 메소드 등은 다같이 얘기해보고 정하면 좋을 것 같습니다.
해당 객체는 앱이 실행 시 딱 한번만 생성되기 때문에 모든 액티비티에서 같은 객체를 사용합니다.
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

// TODO : 로그인 관련 데이터 사용하는 법
/**
초기에 해당 객체가 생성되면 아이디와 비밀번호를 나타내는 email, password 변수가 생성되고 null 을 초기값으로 가집니다.

TODO) 초기에 로그인 시 계정 정보를 설정하기
    로그인이 성공적으로 된다면 setAccount() 메소드를 통해 현재 앱에 로그인한 계정 정보를 설정할 수 있습니다.
    또한 파이어베이스 에서 받아온 즐찾 역, 즐찾 경로 리스트들을 인자로 전달받아 setter 를 사용해 초기화합니다.

 TODO) 내부의 즐겨찾는 역, 즐겨찾는 경로 리스트를 변경하기
    setBookmarkedStation() , setBookmarkedRoute() 의 인자로 바꾸고자 하는 리스트를 전달하여 변경합니다.
    이때 전달한 리스트로 통째로 변경됩니다.
 
 TODO) 내부의 즐겨찾는 역, 즐겨찾는 경로 리스트를 참조하기
    getBookmarkedStation() , getBookmarkedRoute() 를 이용해 ArrayList<String> 변수를 얻을 수 있습니다.
    이때 복사값이 아닌 리스트 자체가 넘어가기 때문에 변경할 수 있습니다. 이왕이면 setter 를 이용해 주세요
    참고로 반환되는 리스트는 final 이기 때문에 getBookmarkedStation() = new ArrayList<String>() ... 와 같이 직접 대입은 불가능합니다.
    (여담으로 다른 변수로 getter 의 반환값을 참조하면 대입으로 리스트를 변경할 수 있습니다. 하지만 역시 setter 를 이용해주세요.)

TODO) 현재 로그인 상태인지 확인하기
    또한 현재 앱이 로그인 상태인지를 확인하기 위해선 isLogined() 메소드를 통해 boolean 으로 확인할 수 있습니다.
 
 TODO) 해당 객체에 저장되어 있는 계정정보 데이터를 지우기
    clearAccount() 메소드를 통해 아이디, 비밀번호, 두개의 리스트를 초기화할 수 있다.
*/

// 액티비티 간에 공유되는 데이터를 담는 클래스
// 그래프 자료구조, 다익스트라 알고리즘에 필요한 데이터를 모아두는 클래스
// 로그인과 관련된 데이터를 담는 클래스
public class CustomAppGraph extends Application {
    public enum SearchType {
        MIN_TIME,       // 최소 시간
        MIN_DISTANCE,   // 최소 거리
        MIN_COST,       // 최소 비용
        MIN_TRANSFER    // 최소 환승
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
        private ArrayList<Integer> lines;               // 호선들
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
            lines = new ArrayList<Integer>();
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

        // getter (안쓰는 메소드는 삭제할 예정)
        public String getVertex() { return vertex; }
        public ArrayList<Integer> getAdjacent() { return adjacent; }
        public int getLine() { return line; }
        public ArrayList<Integer> getLines() { return lines; }
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

    private final int STATION_COUNT = 111;              // 역의 개수
    private final int EDGE_COUNT = 278;                 // edge 의 개수 (엑셀의 row * 2)

    private String email = null;                        // 로그인에 필요한 아이디
    private String password = null;                     // 로그인에 필요한 비밀번호
    private ArrayList<String> bookmarkedStation
            = new ArrayList<String>();                       // 즐겨찾기에 저장된 역
    private ArrayList<String> bookmarkedRoute
            = new ArrayList<String>();                       // 즐겨찾기에 저장된 경로
    private Map<String, Object> bookmarkedMap
            = new HashMap<>();
    private final int LINE_COUNT = 9;                   // 호선의 개수

    @Override
    public void onCreate() {
        // 그래프 생성
        createGraph();

        super.onCreate();
    }

    // 초기화, 그래프를 생성하는 함수
    private void createGraph() {
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

        // 엑셀 읽기
        try {
            String from, to;    // 엑셀 파일의 0번째와 1번째 셀을 저장하는 변수 (역 이름)

            // stations.xls 노선도 읽기
            InputStream stationsIs = getBaseContext().getResources().getAssets().open("stations.xls");
            Workbook stationWb = Workbook.getWorkbook(stationsIs);

            // data.xls 역 정보 읽기
            InputStream dataIs = getBaseContext().getResources().getAssets().open("data.xls");
            Workbook dataWb = Workbook.getWorkbook(dataIs);

            // data.xls 역 정보 읽기
            InputStream linesIs = getBaseContext().getResources().getAssets().open("lines.xls");
            Workbook linesWb = Workbook.getWorkbook(linesIs);

            if (stationWb != null && dataWb != null && linesWb != null) {
                Sheet stationsSheet = stationWb.getSheet(0);
                Sheet dataSheet = dataWb.getSheet(0);
                Sheet linesSheet = linesWb.getSheet(0);

                if (stationsSheet != null && dataSheet != null && linesSheet != null) {
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
                            if (vertices.get(map.get(from)) == null)
                                vertices.set(map.get(from), vFrom);

                            vertices.get(map.get(from)).addAdjacent(map.get(to));

                            // 현재 to 에 해당하는 역이 등록되지 않았다면 추가하고
                            // to 에 연결된 역에 from 을 추가
                            if (vertices.get(map.get(to)) == null)
                                vertices.set(map.get(to), vTo);

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

                        // data.xls 를 읽어서 초기화
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

                    // 추가로 lines.xls 를 읽어서 각 역의 호선 업데이트
                    {
                        for (int row = 1; row <= LINE_COUNT; row++) {
                            int col = 1;
                            int line = row;
                            while (true) {
                                try {
                                    Cell c = linesSheet.getCell(col++, row);
                                    vertices.get(map.get(c.getContents())).getLines().add(line);
                                } catch (Exception e) {
                                    break;
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* TODO : 디버깅용 코드
    void printLines() {
        for (Vertex vertex : vertices) {
            String output = vertex.getVertex() + " 인접역 : ";
            for (int index : vertex.getAdjacent()) {
                output += reverseMap.get(index) + " ";
            }
            output += "호선 : ";
            for (int line : vertex.getLines()) {
                output += line + " ";
            }

            Log.d("test", output);
        }
    }*/

    // 로그인 관련 setter
    // email 과 password 는 초기에 null 이지만 해당 setter 가 수행되면 email 과 password 는 값이 생기게 된다.
    // 또한 저장된 역 리스트와 저장된 경로 리스트를 담는다.
    public boolean setAccount(String _email, String _password, ArrayList<String> _bookmarkedStation, ArrayList<String> _bookmarkedRoute) {
        if (_email == null || _password == null || _email == "" || _password == "") {
            return false;
        }

        email = _email;
        password = _password;
        setBookmarkedStation(_bookmarkedStation);
        setBookmarkedRoute(_bookmarkedRoute);

        return true;
    }

    // 전달된 리스트로 완전히 대체된다.
    public void setBookmarkedStation(ArrayList<String> _bookmarkedStation) {
        if (_bookmarkedStation == null) return;

        // Deep Copy
        bookmarkedStation.clear();

        for (int i = 0; i < _bookmarkedStation.size(); i++) {
            bookmarkedStation.add(_bookmarkedStation.get(i));
        }
    }

    // 전달된 리스트로 완전히 대체된다.
    public void setBookmarkedRoute(ArrayList<String> _bookmarkedRoute) {
        if (_bookmarkedRoute == null) return;

        // Deep Copy
        bookmarkedRoute.clear();

        for (int i = 0; i < _bookmarkedRoute.size(); i++) {
            bookmarkedRoute.add(_bookmarkedRoute.get(i));
        }
    }

    // 전달받은 두 개의 리스트로 해시 맵을 생성한다.
    public void setBookmarkedMap(HashMap<String, Object> _bookmarkedMap) {
        _bookmarkedMap.put("즐겨찾는 역", getBookmarkedStation());
        _bookmarkedMap.put("즐겨찾는 경로", getBookmarkedRoute());
    }

    // email 과 password 가 하나라도 null 이면 로그인되지 않은 상태
    public boolean isLogined() {
        if (email == null || password == null) {
            return false;
        }

        return true;
    }

    // 어플에 저장되는 계정 정보를 비운다.
    public void clearAccount() {
        email = null;
        password = null;
        bookmarkedStation.clear();
        bookmarkedRoute.clear();
    }

    // 로그인 관련 getter
    public String getEmail(){return email;}

    // getter() 로 얻은 리스트에 직접 대입은 불가능하다.
    public final ArrayList<String> getBookmarkedStation() {
        return bookmarkedStation;
    }
    public final ArrayList<String> getBookmarkedRoute() { return bookmarkedRoute; }
    public final Map<String, Object> getBookmarkedMap() {   return bookmarkedMap;   }

    // 그래프, 알고리즘 관련 getter
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
