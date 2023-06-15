package com.example.bzzing_last;

import java.util.HashMap;

public class NotPlayer {
    private String name = "";
    private String songGuess = "";
    private int rate = 5;

    public NotPlayer(String name)
    {
        this.name = name;
    }

    public NotPlayer(HashMap<Integer,Object> map)
    {
        this.name = map.get("name").toString();
        this.songGuess = map.get("songGuess").toString();
        this.rate = Integer.parseInt(map.get("rate").toString());
    }

    public void setRate(int rate)
    {
        this.rate = rate;
    }
    public int getRate() {return rate;}

    public String getName() {
        return name;
    }

    public String getSongGuess()
    {
        return songGuess;
    }
    public void setSongGuess(String songGuess) { this.songGuess = songGuess;}
}
