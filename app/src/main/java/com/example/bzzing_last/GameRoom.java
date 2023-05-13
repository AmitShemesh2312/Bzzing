        package com.example.bzzing_last;

import android.util.Log;

import com.google.android.material.tabs.TabLayout;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class GameRoom{
    private final int maxPlayers = 4;
    private ArrayList<Player> players;
    private int playersNum;
    private int rounds = 0;
    private String roomCode;
    private boolean everybodyReady = false;
    private ArrayList<Song> songs = new ArrayList<>();

    private String currentSong = "";

    private boolean uploadFinished = false;



    private String activePlayer = "";



    public GameRoom() {
        //Shawn Mendes
        songs.add(new Song("There's Nothing Holdin' Me Back", "Shawn Mendes", "illuminate"));
        songs.add(new Song("Stitches", "Shawn Mendes", "handwritten"));

        //The Weeknd
        songs.add(new Song("Save Your Tears", "The Weeknd", "after_hours"));
        songs.add(new Song("Blinding Lights", "The Weeknd", "after_hours"));

        //Harry Styles
        songs.add(new Song("As It Was", "Harry Styles", "harrys_house"));
        songs.add(new Song("Late Night Talking", "Harry Styles", "harrys_house"));
        songs.add(new Song("Adore You", "Harry Styles", "fine_line"));
        songs.add(new Song("Watermelon Sugar", "Harry Styles", "fine_line"));

        //Maroon 5
        songs.add(new Song("Animals", "Maroon 5", "v"));
        songs.add(new Song("Sugar", "Maroon 5", "v"));
        songs.add(new Song("Maps", "Maroon 5", "v"));
        songs.add(new Song("Payphone", "Maroon 5", "overexposed"));

        //Twenty One Pilots
        songs.add(new Song("Stressed Out", "Twenty One Pilots", "blurryface"));



        Collections.shuffle(songs);
    }


    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setPlayersNum(int num) {
        this.playersNum += num;
    }
    public int getPlayersNum() {
        return playersNum;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }
    public ArrayList<Player> getPlayers() { return players; }

    public void setRounds() {
        this.rounds++;
    }
    public int getRounds() {
        return rounds;
    }

    public void setRoomCode(String roomCode){ this.roomCode = roomCode; }
    public String getRoomCode(){ return "" + roomCode; }

    public void setEverybodyReady(boolean everybodyReady){ this.everybodyReady = everybodyReady; }
    public boolean getEverybodyReady(){ return everybodyReady;}


    public void setSongs(ArrayList<Song> songs){this.songs = songs;}
    public ArrayList<Song> getSongs(){return songs;}


    public void setCurrentSong(String currentSong)
    {
        this.currentSong = currentSong;
    }
    public String getCurrentSong()
    {
        return currentSong;
    }

    public void setUploadFinished(boolean uploadFinished){this.uploadFinished = uploadFinished;}
    public boolean getUploadFinished(){return uploadFinished;}

    public String getActivePlayer() {
        return activePlayer;
    }
    public void setActivePlayer(String activePlayer) {
        this.activePlayer = activePlayer;
    }






    public HashMap<String, Object> getField(String field)
    {
        HashMap<String, Object> map = new HashMap<>();

        switch (field){
            case "currentSong":
                map.put(field, currentSong);
                break;

            case "activePlayer":
                map.put(field, activePlayer);
                break;



            case "uploadFinished":
                map.put(field, uploadFinished);
                break;

            case "players":
                map.put(field, players);
                break;

            case "everybodyReady":
                map.put(field, everybodyReady);
        }

        return map;
    }


    public ArrayList<String> getNotPlayers()
    {
        ArrayList<String> arr = new ArrayList<>();
        for (int i = 0; i < players.size(); i++) {
            if(i != getRounds())
                arr.add(players.get(i).getName());
        }
        return arr;
    }



    public HashMap<String,Object> GameRoomToHashMap()
    {
        HashMap<String, Object> map = new HashMap<>();
        map.put("roomCode", roomCode);
        map.put("playersNum", playersNum);
        map.put("rounds", rounds);
        map.put("players", players);
        map.put("everybodyReady", everybodyReady);
        map.put("songs", songs);
        map.put("currentSong", currentSong);
        map.put("uploadFinished", uploadFinished);
        map.put("activePlayer", activePlayer);

        return map;
    }


    public GameRoom(HashMap<String,Object> map)
    {
        // hashmap to game room object
        this.playersNum = Integer.parseInt(map.get("playersNum").toString());
        this.rounds = Integer.parseInt(map.get("rounds").toString());
        this.roomCode = map.get("roomCode").toString();
        this.everybodyReady =  Boolean.parseBoolean(map.get("everybodyReady").toString());

        this.players = new ArrayList<>();
        ArrayList<HashMap<Integer,Object>> hm = (ArrayList<HashMap<Integer,Object>>)map.get("players");
        for (int i = 0; i < hm.size(); i++) {
            this.players.add(new Player(hm.get(i)));
        }


        this.songs = new ArrayList<>();
        ArrayList<HashMap<Integer,Object>> s = (ArrayList<HashMap<Integer, Object>>) map.get("songs");
        for (int i = 0; i < s.size(); i++) {
            this.songs.add(new Song(s.get(i)));
        }


        this.currentSong = map.get("currentSong").toString();

        this.uploadFinished = Boolean.parseBoolean(map.get("uploadFinished").toString());

        this.activePlayer = map.get("activePlayer").toString();


    }

    public void addPlayer(Player p)
    {
        this.players.add(p);
    }//מוסיף שחקן לרשימת השחקנים


}
