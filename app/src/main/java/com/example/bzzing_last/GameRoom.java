        package com.example.bzzing_last;

import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class GameRoom{
    private final int maxPlayers = 4;
    private ArrayList<Player> players = new ArrayList<>();
    private int rounds = 0;
    private String roomCode;
    private ArrayList<Song> songs = new ArrayList<>();

    private String currentSong = "";

    private boolean uploadFinished = false;

    private String activePlayer = "";

    private Boolean everybodyDone = false;

    private ArrayList<Guesser> guessers = new ArrayList<>();

    private ArrayList<String> complimentsSentences = new ArrayList<>();

    private MediaPlayer mediaPlayer;

    private boolean updated = false;



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

        //Bruno Mars
        songs.add(new Song("Uptown Funk", "Bruno Mars", "uptown_special"));
        songs.add(new Song("The Lazy Song", "Bruno Mars", "doo_wops_and_hooligans"));
        songs.add(new Song("24K Magic", "Bruno Mars", "_24k_magic"));
        songs.add(new Song("Treasure", "Bruno Mars", "unorthodox_jukebox"));
        songs.add(new Song("Grenade", "Bruno Mars", "doo_wops_and_hooligans"));
        songs.add(new Song("Locked Out Of Heaven", "Bruno Mars", "unorthodox_jukebox"));

        Collections.shuffle(songs);






        complimentsSentences.add("You did great!");
        complimentsSentences.add("You were okay");
        complimentsSentences.add("Not that bad!");
        complimentsSentences.add("Not bad at all!");
        complimentsSentences.add("Next time you will do better");
        complimentsSentences.add("Good job!");
        Collections.shuffle(complimentsSentences);
    }


    public int getMaxPlayers() {
        return maxPlayers;
    }



    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }
    public ArrayList<Player> getPlayers() { return players; }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }
    public int getRounds() {
        return rounds;
    }

    public void setRoomCode(String roomCode){ this.roomCode = roomCode; }
    public String getRoomCode(){ return roomCode; }


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

    public Boolean getEverybodyDone(){ return everybodyDone;}
    public void setEverybodyDone(boolean everybodyDone){ this.everybodyDone = everybodyDone;}

    public ArrayList<Guesser> getGuessers(){return guessers;}
    public void setGuessers(ArrayList<Guesser> guessers){ this.guessers = guessers; }

    public ArrayList<String> getComplimentsSentences(){return complimentsSentences;}

    public boolean getUpdated()
    {
        return updated;
    }
    public void setUpdated(boolean updated)
    {
        this.updated = updated;
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

            case "everybodyDone":
                map.put(field, everybodyDone);
                break;

            case "guessers":
                map.put(field, guessers);
                break;

            case "rounds":
                map.put(field, rounds);


            case "updated":
                map.put(field, updated);
        }
        return map;
    }






    public HashMap<String,Object> GameRoomToHashMap()
    {
        HashMap<String, Object> map = new HashMap<>();
        map.put("roomCode", roomCode);
        map.put("rounds", rounds);
        map.put("players", players);
        map.put("songs", songs);
        map.put("currentSong", currentSong);
        map.put("uploadFinished", uploadFinished);
        map.put("activePlayer", activePlayer);
        map.put("everybodyDone", everybodyDone);
        map.put("guessers", guessers);
        map.put("complimentsSentences", complimentsSentences);
        map.put("updated", updated);

        return map;
    }


    public GameRoom(HashMap<String,Object> map)
    {
        // hashmap to game room object
        this.rounds = Integer.parseInt(map.get("rounds").toString());
        this.roomCode = map.get("roomCode").toString();

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

        this.everybodyDone = Boolean.parseBoolean(map.get("everybodyDone").toString());


        this.guessers = new ArrayList<>();
        ArrayList<HashMap<Integer,Object>> np = (ArrayList<HashMap<Integer, Object>>) map.get("guessers");
        for (int i = 0; i < np.size(); i++) {
            this.guessers.add(new Guesser(np.get(i)));
        }

        this.complimentsSentences = (ArrayList<String>) map.get("complimentsSentences");

        this.updated = Boolean.parseBoolean(map.get("updated").toString());

    }


    public int getPlayerIndex(String name)
    {
        int index = -1;
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getName().equals(name)) {
                index = i;
            }
        }
        return index;
    }

    public int getGuesserIndex(String name)
    {
        int index = -1;
        for (int i = 0; i < guessers.size(); i++) {
            if (guessers.get(i).getName().equals(name)) {
                index = i;
            }
        }
        return index;
    }

    public int getCurrentSongIndex()
    {
        int index = -1;
        for (int i = 0; i < songs.size(); i++) {
            if (songs.get(i).getName().equals(currentSong))
            {
                index = i;
            }
        }
        return index;
    }

    public void playHumming(Uri downloadUrl) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(downloadUrl.toString());
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer = null;
                }
            });
        } catch (IOException e) {
            Log.e("TAG", "Failed to play audio file", e);
        }
    }

    public boolean everybodyReady()
    {
        GameRoom gameRoom = AppUtilities.gameRoom;
        boolean allReady = true;

        for (int i = 0; i < gameRoom.getPlayers().size(); i++) {
            if (!gameRoom.getPlayers().get(i).getReady())
                allReady = false;
        }
        if (allReady)
            return true;
        return false;
    }

    public void addPlayer(Player p)
    {
        this.players.add(p);
    }

    public void addGuesser(Guesser p){ this.guessers.add(p); }
}
