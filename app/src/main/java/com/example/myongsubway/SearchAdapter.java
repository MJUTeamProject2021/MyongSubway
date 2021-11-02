package com.example.myongsubway;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

class ViewHolder{
    public TextView Search_TextView_Station;
}

public class SearchAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflate;
    private List<String> Search_StationList; // 모든 역 리스트
    private ViewHolder viewHolder; // ListView 를 구성하는 TextView

    public SearchAdapter(List<String> list, Context context){
        this.Search_StationList = list;
        this.context = context;
        this.inflate = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return this.Search_StationList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup viewGroup) {
        if(convertView == null){
            convertView = inflate.inflate(R.layout.row_listview, null);

            viewHolder = new ViewHolder();
            viewHolder.Search_TextView_Station = (TextView) convertView.findViewById(R.id.Station);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // TextView 설정
        viewHolder.Search_TextView_Station.setText(Search_StationList.get(pos));

        return convertView;
    }


}
