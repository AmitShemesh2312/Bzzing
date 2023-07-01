package com.example.bzzing_last;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class Rate extends AppCompatActivity implements RateHandler {

    DB database = new DB();
    String name;

    private FragmentWaitingOthersGuess fragmentWaitingOthersGuess;
    private FragmentWaitingOthersScoring fragmentWaitingOthersScoring;
    private FragmentManager fragmentManager;
    private MediaPlayer mediaPlayer;
    private boolean next = false;

    private boolean set = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        fragmentManager = getSupportFragmentManager();

        database.setRate(this);

        database.listenToRateChanges(this);

        name = getIntent().getStringExtra("name");

        fragmentWaitingOthersScoring = new FragmentWaitingOthersScoring();

        fragmentWaitingOthersGuess = new FragmentWaitingOthersGuess();
        fragmentWaitingOthersGuess.setName(name);
        fragmentManager.beginTransaction().replace(R.id.fragmentContainerView, fragmentWaitingOthersGuess, null).commit();
    }

    public void updateDocumentChanges(GameRoom g) {
        AppUtilities.gameRoom = g;
        Player player = g.getPlayers().get(g.getPlayerIndex(name));

        nextRound();

        if (player.getDoneScoring())
            everybodyDoneScoring();
        else
            everybodyDoneGuessingSong();
    }


    public void everybodyDoneGuessingSong() {
        if (!next) {
            if (!AppUtilities.gameRoom.getEverybodyDone()) {
                fragmentWaitingOthersGuess.writeNames();
                if (name.equals(AppUtilities.gameRoom.getActivePlayer()))
                    checkIfEverybodyDone();
            } else
                fragmentSongReveal();
        }
    }

    public void everybodyDoneScoring() {
        GameRoom gameRoom = AppUtilities.gameRoom;
        String activePlayer = gameRoom.getActivePlayer();
        boolean everybody_done_scoring = checkIfEverybodyDoneScoring();
        int rounds = gameRoom.getRounds();
        int players_size = gameRoom.getPlayers().size();


        if(everybody_done_scoring)
        {
            gameRoom.getPlayers().get(rounds).setReality(calculateReality());

            if (rounds + 1 < players_size) {
                if (activePlayer.equals(name) && set) {
                    setRules();
                    defaultSettings();
                    database.updateAll();
                    nextRound();
                    set = false;
                }
            } else if (rounds + 1 >= players_size)
                end();
        }


        else fragmentWaitingOthersScoring.writeNames();
    }


    public void setRules() {
        GameRoom gameRoom = AppUtilities.gameRoom;
        String activePlayer = gameRoom.getPlayers().get(gameRoom.getRounds() + 1).getName();
        gameRoom.setActivePlayer(activePlayer);


        gameRoom.setGuessers(new ArrayList<>());

        for (int i = 0; i < gameRoom.getPlayers().size(); i++) {
            String player_name = gameRoom.getPlayers().get(i).getName();
            if (!player_name.equals(activePlayer))
                gameRoom.addGuesser(new Guesser(player_name));
        }
    }


    public void defaultSettings() {
        GameRoom gameRoom = AppUtilities.gameRoom;

        gameRoom.setRounds(gameRoom.getRounds() + 1);
        gameRoom.setCurrentSong("");
        gameRoom.setEverybodyDone(false);
        gameRoom.setUploadFinished(false);
        for (int i = 0; i < gameRoom.getPlayers().size(); i++) {
            gameRoom.getPlayers().get(i).setDoneScoring(false);
        }

        gameRoom.setUpdated(true);
    }

    public void nextRound() {
        if (AppUtilities.gameRoom.getUpdated()) {
            database.stopListeningDocumentChanges();

            Intent intent = new Intent(this, nextPlayer.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("name", name);
            startActivity(intent);
        }
    }

    public void end() {
        database.stopListeningDocumentChanges();

        Intent intent = new Intent(this, ScoreTable.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("name", name);
        startActivity(intent);
    }

    public int calculateReality() {
        GameRoom gameRoom = AppUtilities.gameRoom;
        int reality = 0;

        for (int i = 0; i < gameRoom.getGuessers().size(); i++) {
            reality += gameRoom.getGuessers().get(i).getRate();
        }
        if (gameRoom.getGuessers().size() != 0)
            reality /= gameRoom.getGuessers().size();

        return reality;
    }

    public boolean checkIfEverybodyDoneScoring() {
        GameRoom gameRoom = AppUtilities.gameRoom;
        Boolean done = true;
        for (int i = 0; i < gameRoom.getPlayers().size(); i++) {
            if (!gameRoom.getPlayers().get(i).getDoneScoring()) {
                done = false;
            }
        }
        return done;
    }

    public void checkIfEverybodyDone() {
        GameRoom gameRoom = AppUtilities.gameRoom;
        boolean everybodyDone = true;

        for (int i = 0; i < gameRoom.getGuessers().size(); i++) {
            if (gameRoom.getGuessers().get(i).getSongGuess().equals(""))
                everybodyDone = false;
        }
        if (everybodyDone) {
            gameRoom.setEverybodyDone(true);
            database.updateField("everybodyDone");
            fragmentSongReveal();
        }
    }

    public void fragmentSongReveal() {
        if (!name.equals(AppUtilities.gameRoom.getActivePlayer())) {
            FragmentSongReveal fragmentSongReveal = new FragmentSongReveal();
            fragmentSongReveal.setName(name);
            fragmentManager.beginTransaction().replace(R.id.fragmentContainerView, fragmentSongReveal).commit();
        } else {
            fragmentManager.beginTransaction().replace(R.id.fragmentContainerView, FragmentExpectations.class, null).commit();
        }
    }

    public void ratePlayer(View view) {
        TextView textView = findViewById(R.id.points);
        int tag = Integer.parseInt(view.getTag().toString());
        updateRate(textView, tag);
    }

    public void rateMyself(View view) {
        TextView textView = findViewById(R.id.pointsHum);
        int tag = Integer.parseInt(view.getTag().toString());
        updateRate(textView, tag);
    }

    public void updateRate(TextView textView, int tag)
    {
        int rate = Integer.parseInt(textView.getText().toString());

        if (rate < 10 && rate > 0) {
            if (tag == 1)
                rate += 1;
            else
                rate -= 1;
        } else if (rate == 0) {
            if (tag == 1)
                rate += 1;
        } else {
            if (tag == 0)
                rate -= 1;
        }

        textView.setText("" + rate);
    }


    public void submitRate(View view) {
        GameRoom gameRoom = AppUtilities.gameRoom;
        fragmentManager.beginTransaction().replace(R.id.fragmentContainerView, fragmentWaitingOthersScoring, null).commit();
        AppUtilities.gameRoom.getPlayers().get(AppUtilities.gameRoom.getPlayerIndex(name)).setDoneScoring(true);

        TextView textView = findViewById(R.id.points);
        Guesser p = gameRoom.getGuessers().get(gameRoom.getGuesserIndex(name));
        p.setRate(Integer.parseInt(textView.getText().toString()));

        database.updateField("guessers");
        database.updateField("players");

        next = true;
    }

    public void submitScore(View view) {
        GameRoom gameRoom = AppUtilities.gameRoom;
        fragmentManager.beginTransaction().replace(R.id.fragmentContainerView, fragmentWaitingOthersScoring, null).commit();
        AppUtilities.gameRoom.getPlayers().get(AppUtilities.gameRoom.getPlayerIndex(name)).setDoneScoring(true);

        TextView textView = findViewById(R.id.pointsHum);
        Player p = gameRoom.getPlayers().get(gameRoom.getRounds());
        p.setExpectations(Integer.parseInt(textView.getText().toString()));

        database.updateField("players");

        next = true;
    }

    public void playRecording(View view) {
        if (mediaPlayer == null)
            database.getHumming(AppUtilities.gameRoom.getRounds());
        else {
            mediaPlayer.stop();
            mediaPlayer = null;
        }
    }

    @Override
    public void onBackPressed() {//הפעולה חוסמת את אפשרות הלחיצה על כפתור החזור
        return;
    }
}