package com.example.beauty;

public class Cover {
    private String coverName;
    private String coverUrl;
    private String Link;
    public String getCoverName(){
        return coverName;
    }
    public String getCoverUrl(){
        return coverUrl;
    }
    public String getLink(){
        return Link;
    }
    public void setCoverName(String coverName){
        this.coverName=coverName;
    }
    public void setCoverUrl(String coverUrl){
        this.coverUrl=coverUrl;
    }
    public void setLink(String Link){
        this.Link=Link;
    }
}