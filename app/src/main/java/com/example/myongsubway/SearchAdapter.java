package com.example.myongsubway;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

class ViewHolder{
    public TextView stationTextView;
}

public class SearchAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflate;
    private List<String> stationList; // 모든 역 리스트
    private ViewHolder viewHolder; // ListView 를 구성하는 TextView

    public SearchAdapter(List<String> list, Context context){
        this.stationList = list;
        this.context = context;
        this.inflate = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return this.stationList.size();
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
            convertView = inflate.inflate(R.layout.listview_station, null);

            viewHolder = new ViewHolder();
            viewHolder.stationTextView = (TextView) convertView.findViewById(R.id.Search_TextView_station);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // TextView 설정
        viewHolder.stationTextView.setText(stationList.get(pos));

        return convertView;
    }


}
