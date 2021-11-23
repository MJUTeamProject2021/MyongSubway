package com.example.myongsubway;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.CustomViewHolder> {

    private ArrayList<CardItem> mDataList;
    private Context context;


    public interface OnItemClickEventListener{
        void onItemClick(View v,int position);
    }
    private OnItemClickEventListener mItemClickListener;


    public void setOnItemClickListener(OnItemClickEventListener listener) {
        mItemClickListener = listener;
    }


    public BoardAdapter(ArrayList<CardItem> dataList, Context _context){
        mDataList = dataList;
        context= _context;
    }


    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row,parent,false);
        return new CustomViewHolder(view,mItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @org.jetbrains.annotations.NotNull CustomViewHolder holder, int position) {
        CardItem item = mDataList.get(position);

        holder.title.setText(item.getTitle());
        holder.content.setText(item.getContent());
        holder.writer.setText("  |   "+item.getWriter());
        holder.commentnumber.setText(item.getCommentnumber());
        //holder.time.setText(item.getTime());


        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yy/MM/dd HH:mm");
            Date tempDate = null;
            tempDate = dateFormat.parse(item.getTime());
            Long tempLong = tempDate.getTime();
            System.out.println(tempDate);
            holder.time.setText(formatTimeString(tempLong));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView content;
        TextView time;
        TextView writer;
        Button commentnumber;
        public CustomViewHolder(View itemView,final OnItemClickEventListener clicklistener){
            super(itemView);
            title = itemView.findViewById(R.id.board_title);
            content = itemView.findViewById(R.id.board_content);
            time = itemView.findViewById(R.id.board_time);
            writer = itemView.findViewById(R.id.board_writer);
            commentnumber = itemView.findViewById(R.id.board_commentnumber);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        clicklistener.onItemClick(v,position);
                    }
                }
            });
        }
    }

    //시간 변환 코드 출처 : https://milkissboy.tistory.com/31
    private static class TIME_MAXIMUM{
        public static final int SEC = 60;
        public static final int MIN = 60;
        public static final int HOUR = 24;
        public static final int DAY = 30;
        public static final int MONTH = 12;
    }
    public static String formatTimeString(long regTime) {
        long curTime = System.currentTimeMillis();
        long diffTime = (curTime - regTime) / 1000;
        String msg = null;
        if (diffTime < TIME_MAXIMUM.SEC) {
            msg = "방금 전";
        } else if ((diffTime /= TIME_MAXIMUM.SEC) < TIME_MAXIMUM.MIN) {
            msg = diffTime + "분 전";
        } else if ((diffTime /= TIME_MAXIMUM.MIN) < TIME_MAXIMUM.HOUR) {
            msg = (diffTime) + "시간 전";
        } else if ((diffTime /= TIME_MAXIMUM.HOUR) < TIME_MAXIMUM.DAY) {
            msg = (diffTime) + "일 전";
        } else if ((diffTime /= TIME_MAXIMUM.DAY) < TIME_MAXIMUM.MONTH) {
            msg = (diffTime) + "달 전";
        } else {
            msg = (diffTime) + "년 전";
        }
        return msg;
    }
}
