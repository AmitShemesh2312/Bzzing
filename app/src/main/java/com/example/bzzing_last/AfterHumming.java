package com.example.bzzing_last;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class AfterHumming extends AppCompatActivity implements AfterHummingHandler {

    DB database = new DB();
    String name;

    private FragmentOthersChoose fragmentOthersChoose;
    private FragmentFinishScoring fragmentFinishScoring;
    private FragmentManager fragmentManager;
    private MediaPlayer mediaPlayer;
    private boolean next = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_humming);

        fragmentManager = getSupportFragmentManager();


        database.setAfterUploadHumming(this);

        database.listenToEndChanges(this);

        name = getIntent().getStringExtra("name");

        fragmentFinishScoring = new FragmentFinishScoring();

        fragmentOthersChoose = new FragmentOthersChoose();
        fragmentOthersChoose.setName(name);
        fragmentManager.beginTransaction().replace(R.id.fragmentContainerView, fragmentOthersChoose, null).commit();
    }


    public void updateDocumentChanges(GameRoom g) {
        AppUtilities.gameRoom = g;
        if (g.getPlayers().get(g.getPlayerIndex(name)).getDoneScoring()){
            if(checkIfEverybodyDoneScoring())
            {
               fragmentManager.beginTransaction().replace(R.id.fragmentContainerView, FragmentNextPlayer.class, null).commit();
            }
            fragmentFinishScoring.writeNames();
        }
        else {
            if (!AppUtilities.gameRoom.getEverybodyDone()) {
                fragmentOthersChoose.writeNames();
                if (name.equals(g.getActivePlayer()))
                    checkIfEverybodyDone();
            } else
                fragmentSongReveal();
        }
    }


    public boolean checkIfEverybodyDoneScoring() {
        GameRoom gameRoom = AppUtilities.gameRoom;
        for (int i = 0; i < gameRoom.getPlayers().size(); i++) {
            if (!gameRoom.getPlayers().get(i).getDoneScoring()) {
                return false;
            }
        }
        return true;
    }


    public void checkIfEverybodyDone() {
        GameRoom gameRoom = AppUtilities.gameRoom;
        boolean everybodyDone = true;

        for (int i = 0; i < gameRoom.getNotPlayers().size(); i++) {
            if (gameRoom.getNotPlayers().get(i).getSongGuess().equals(""))
                everybodyDone = false;
        }
        if (everybodyDone) {
            gameRoom.setEverybodyDone(true);
            database.updateField("everybodyDone");
            fragmentSongReveal();
        }
    }

    public void fragmentSongReveal() {
        if (!next) {
            if (!name.equals(AppUtilities.gameRoom.getActivePlayer())) {
                FragmentSongReveal fragmentSongReveal = new FragmentSongReveal();
                fragmentSongReveal.setName(name);
                fragmentManager.beginTransaction().replace(R.id.fragmentContainerView, fragmentSongReveal).commit();
            } else {
                fragmentManager.beginTransaction().replace(R.id.fragmentContainerView, FragmentExpectations.class, null).commit();
            }
        }
    }

    public void ratePlayer(View view) {
        GameRoom gameRoom = AppUtilities.gameRoom;
        NotPlayer p = gameRoom.getNotPlayers().get(gameRoom.getNotPlayerIndex(name));
        TextView textView = findViewById(R.id.points);
        int rate = Integer.parseInt(textView.getText().toString());

        int tag = Integer.parseInt(view.getTag().toString());
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
        p.setRate(rate);
        textView.setText("" + rate);
    }

    public void rateHum(View view) {
        GameRoom gameRoom = AppUtilities.gameRoom;
        Player p = gameRoom.getPlayers().get(gameRoom.getRounds());
        TextView textView = findViewById(R.id.pointsHum);
        int rate = Integer.parseInt(textView.getText().toString());

        int tag = Integer.parseInt(view.getTag().toString());
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
        p.setExpectations(rate);
        textView.setText("" + rate);
    }

    public void submitReality(View view) {
        fragmentManager.beginTransaction().replace(R.id.fragmentContainerView, fragmentFinishScoring, null).commit();
        AppUtilities.gameRoom.getPlayers().get(AppUtilities.gameRoom.getPlayerIndex(name)).setDoneScoring(true);
        database.updateField("notPlayers");
        database.updateField("players");
        next = true;
    }

    public void submitExpectations(View view) {
        fragmentManager.beginTransaction().replace(R.id.fragmentContainerView, fragmentFinishScoring, null).commit();
        AppUtilities.gameRoom.getPlayers().get(AppUtilities.gameRoom.getPlayerIndex(name)).setDoneScoring(true);
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