package com.example.bzzing_last;

import java.io.Serializable;
import java.util.HashMap;

public class Player implements Serializable {
    private String name;
    private int expectations;
    private int reality;
    private boolean ready;
    private boolean doneScoring;




    public Player(String name) {
        this.name = name;
        this.ready = false;
        this.doneScoring = false;
    }


    public Player(HashMap<Integer,Object> map)
    {
        this.name = map.get("name").toString();
        this.expectations = Integer.valueOf(map.get("expectations").toString());
        this.reality = Integer.valueOf(map.get("reality").toString());
        this.ready = Boolean.valueOf(map.get("ready").toString());
        this.doneScoring = Boolean.valueOf(map.get("doneScoring").toString());
    }


    public Boolean getDoneScoring()
    {
        return doneScoring;
    }

    public void setDoneScoring(Boolean doneScoring)
    {
        this.doneScoring = doneScoring;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getExpectations() {
        return expectations;
    }
    public void setExpectations(int expectations) {
        this.expectations = expectations;
    }

    public int getReality() {return reality;}
    public void setReality(int reality) {this.reality = reality;}

    public boolean getReady(){return ready;}
    public void setReady(boolean ready){this.ready = ready;}
}
