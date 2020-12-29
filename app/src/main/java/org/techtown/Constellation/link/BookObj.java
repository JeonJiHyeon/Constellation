package org.techtown.Constellation.link;

import java.io.Serializable;

public class BookObj implements Serializable {
    private String bookcode;
    private String book;
    private String topic;

    public String getBookcode(){
        return bookcode;
    }

    public String getBook(){
        return book;
    }
    public String getTopic(){
        return topic;
    }

    public void setBookcode(String data){
        this.bookcode = data;
    }

    public void setBook(String data){
        this.book = data;
    }
    public void setTopic(String data){
        this.topic = data;
    }

}
