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
    }




    public void updateDocumentChanges(GameRoom g)
    {
        AppUtilities.gameRoom = g;
        if(!AppUtilities.gameRoom.getEverybodyDone())
        {
            if(name.equals(g.getActivePlayer()))
                checkIfEverybodyDone();
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
        if(!name.equals(AppUtilities.gameRoom.getActivePlayer()))
            fragmentManager.beginTransaction().replace(R.id.fragmentContainerView, FragmentSongReveal.class, null).commit();
    }
}