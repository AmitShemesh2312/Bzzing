package com.example.bzzing_last;

public class NotPlayer {
    private String name = "";
    private String songGuess = "";

    public NotPlayer(String name, String songGuess)
    {
        this.name = name;
        this.songGuess = songGuess;
    }

    public String getName() {
        return name;
    }

    public String getSongGuess()
    {
        return songGuess;
    }

}
