package com.example.bzzing_last;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AfterHumming extends AppCompatActivity implements AfterHummingHandler {

    DB database = new DB();
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_humming);

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
        if(!AppUtilities.gameRoom.getEverybodyDone())
        {
            AppUtilities.gameRoom = g;

            writeNames();

            if(g.getPlayers().get(0).getName().equals(name))
                checkIfEverybodyDone();
        }
        else
            songReveal();

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
            songReveal();
        }
    }

    public void songReveal()
    {
        ArrayList<NotPlayer> not_players = AppUtilities.gameRoom.getNotPlayers();
        for (int i = 0; i < not_players.size(); i++) {
            int id = getResources().getIdentifier("player_name" + i, "id", getPackageName());
            TextView textView = findViewById(id);
            textView.setText(not_players.get(i).getName());
            if(!not_players.get(i).getSongGuess().equals(""))
            {
                textView.setTextColor(Color.parseColor("#161616"));
            }
            else {
                textView.setTextColor(Color.parseColor("#161616"));
            }
        }
    }
}