package com.example.bzzing_last;

import java.io.Serializable;
import java.util.HashMap;

public class Player implements Serializable {
    private String name;
    private int score;
    private int accuracy;
    private boolean ready;

    private String songGuess;



    public Player(String name) {
        this.name = name;
        this.score = 0;
        this.accuracy = 0;
        this.ready = false;
        this.songGuess = "";
    }


    public Player(HashMap<Integer,Object> map)
    {
        this.name = map.get("name").toString();
        this.score = Integer.valueOf(map.get("score").toString());
        this.accuracy = Integer.valueOf(map.get("accuracy").toString());
        this.ready = Boolean.valueOf(map.get("ready").toString());
        this.songGuess = map.get("songGuess").toString();
    }

    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getAccuracy() {return accuracy;}
    public void setAccuracy(int accuracy) {this.accuracy = accuracy;}

    public boolean getReady(){return ready;}
    public void setReady(boolean ready){this.ready = ready;}

    public String getSongGuess()
    {
        return songGuess;
    }
    public void setSongGuess(String songGuess)
    {
        this.songGuess = songGuess;
    }
}
