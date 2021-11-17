package com.example.myongsubway;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

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
        holder.writer.setText(item.getWriter());
        holder.time.setText(item.getTime());
        holder.commentnumber.setText(item.getCommentnumber());
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
}
