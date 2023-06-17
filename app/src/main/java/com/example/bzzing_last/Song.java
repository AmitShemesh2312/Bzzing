package com.example.bzzing_last;

import java.util.HashMap;

public class Song {
    private String name;
    private String singer;
    private String image;


    public Song(String name, String singer, String image) {
        this.name = name;
        this.singer = singer;
        this.image = image;
    }

    public Song(HashMap<Integer,Object> map)
    {
        this.name = map.get("name").toString();
        this.singer = map.get("singer").toString();
        this.image = map.get("image").toString();
    }
    public String getSinger() {return singer;}
    public void setSinger(String singer) {this.singer = singer;}

    public String getImage() {return image;}
    public void setImage(String image) {this.image = image;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
}
