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
    int num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_humming);

        database.setAfterUploadHumming(this);

        name = getIntent().getStringExtra("name");


        writeNames();
    }


    public void writeNames()
    {
        ArrayList<String> not_players = AppUtilities.gameRoom.getNotPlayers();
        for (int i = 0; i < not_players.size(); i++) {
            int id = getResources().getIdentifier("player_name" + i, "id", getPackageName());
            TextView textView = findViewById(id);
            textView.setText(not_players.get(i));
//            if(!not_players.get(i).getSongGuess().equals(""))
//            {
//                textView.setTextColor(Color.parseColor("#126C08"));
//            }
//            else {
//                textView.setTextColor(Color.parseColor("#CF2500"));
//            }
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