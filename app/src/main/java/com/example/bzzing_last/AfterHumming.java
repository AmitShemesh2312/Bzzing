package com.example.bzzing_last;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class AfterHumming extends AppCompatActivity implements AfterHummingHandler {

    DB database = new DB();
    String name;
    int num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_humming);

        database.setAfterUploadHumming(this);

        name = getIntent().getStringExtra("name");


        if(AppUtilities.gameRoom.getActivePlayer().equals(name))
            writeNamesIfPlayer();
        else
            writeNamesIfNotPlayer();
    }

    public void writeNamesIfPlayer() {
        for (int i = 0; i < AppUtilities.gameRoom.getPlayers().size(); i++) {
            if (!name.equals(AppUtilities.gameRoom.getPlayers().get(i).getName())) {
                int id = getResources().getIdentifier("player_name" + num, "id", getPackageName());
                TextView textView = findViewById(id);
                textView.setText(AppUtilities.gameRoom.getPlayers().get(i).getName());
                textView.setTextColor(Color.parseColor("#CF2500"));
                num++;
            }
        }
    }

    public void writeNamesIfNotPlayer()
    {
        for (int i = 0; i < AppUtilities.gameRoom.getPlayers().size(); i++) {
            if(AppUtilities.gameRoom.getPlayers().get(i).getName().equals(name))
            {
                int id = getResources().getIdentifier("player_name" + num, "id", getPackageName());
                TextView textView = findViewById(id);
                textView.setText(AppUtilities.gameRoom.getPlayers().get(i).getName());
                textView.setTextColor(Color.parseColor("#126C08"));
                num++;
                Toast.makeText(this, "hey", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public int get_player_position() {
        GameRoom gameRoom = AppUtilities.gameRoom;
        int position = -1;
        for (int i = 0; i < gameRoom.getPlayers().size(); i++)
            if (gameRoom.getPlayers().get(i).getName().equals(name))
                position = i;
        return position;
    }
}