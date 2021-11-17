package com.example.myongsubway;

import java.io.Serializable;

public class CardItem implements Serializable {

    private String id;
    private String title;
    private String content;
    private String time;
    private String writer;
    private String commentnumber;

 public CardItem(){
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setId(String id) {this.id = id; }

    public void setCommentnumber(String commentnumber) {this.commentnumber = commentnumber; }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getWriter() {
        return writer;
    }

    public String getTime() {
        return time;
    }

    public String getId() { return id; }

    public String getCommentnumber(){return commentnumber;}
}
