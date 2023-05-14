package com.example.bzzing_last;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class AfterHumming extends AppCompatActivity implements AfterHummingHandler {

    DB database = new DB();
    String name;

    private FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_humming);

        fragmentManager = getSupportFragmentManager();

        database.setAfterUploadHumming(this);

        database.listenToEndChanges(this);

        name = getIntent().getStringExtra("name");

        writeNames();
    }


    public void writeNames()
    {
        ArrayList<NotPlayer> not_players = AppUtilities.gameRoom.getNotPlayers();
        for (int i = 0; i < not_players.size(); i++) {
            int id = getResources().getIdentifier("player_name" + i, "id", getPackageName());
            TextView textView = findViewById(id);
            textView.setText(not_players.get(i).getName());
            if(!not_players.get(i).getSongGuess().equals(""))
            {
                textView.setTextColor(Color.parseColor("#126C08"));
            }
            else {
                textView.setTextColor(Color.parseColor("#CF2500"));
            }
        }
    }

    public void updateDocumentChanges(GameRoom g)
    {
        AppUtilities.gameRoom = g;
        if(!AppUtilities.gameRoom.getEverybodyDone())
        {
            if(name.equals(g.getActivePlayer()))
                checkIfEverybodyDone();
            else writeNames();
        }
        else
            fragmentSongReveal();

    }

    public void checkIfEverybodyDone()
    {
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

    public void fragmentSongReveal()
    {
        ArrayList<NotPlayer> not_players = AppUtilities.gameRoom.getNotPlayers();
        for (int i = 0; i < not_players.size(); i++) {
            int id = getResources().getIdentifier("player_name" + i, "id", getPackageName());
            TextView textView = findViewById(id);
            textView.setText(not_players.get(i).getName());
            if(!not_players.get(i).getSongGuess().equals(""))
            {
                textView.setTextColor(Color.parseColor("#000000"));
            }
            else {
                textView.setTextColor(Color.parseColor("#000000"));
            }
        }
    }
}