package com.example.bzzing_last;

import java.util.HashMap;

public class NotPlayer {
    private String name = "";
    private String songGuess = "";

    public NotPlayer(String name)
    {
        this.name = name;
    }

    public NotPlayer(HashMap<Integer,Object> map)
    {
        this.name = map.get("name").toString();
        this.songGuess = map.get("songGuess").toString();
    }

    public String getName() {
        return name;
    }

    public String getSongGuess()
    {
        return songGuess;
    }
    public void setSongGuess(String songGuess) { this.songGuess = songGuess;}
}
