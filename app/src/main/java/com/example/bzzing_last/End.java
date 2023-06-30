package com.example.bzzing_last;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class End extends AppCompatActivity implements EndHandler{

    private DB database = new DB();
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        database.setEnd(this);
        database.listen();

        name = getIntent().getStringExtra("name");

        score();

        if(name.equals(AppUtilities.gameRoom.getActivePlayer()))
            resetData();
    }

    public void score()
    {
        GameRoom gameRoom = AppUtilities.gameRoom;
        for (int i = 0; i < gameRoom.getPlayers().size(); i++) {
            int num_id = getResources().getIdentifier("num" + i, "id", getPackageName());
            TextView num = findViewById(num_id);
            num.setVisibility(View.VISIBLE);

            int name_id = getResources().getIdentifier("end_name" + i, "id", getPackageName());
            TextView player_name = findViewById(name_id);
            player_name.setText(gameRoom.getPlayers().get(i).getName());
            player_name.setVisibility(View.VISIBLE);

            int textview_expectations_id = getResources().getIdentifier("textview_expectations" + i, "id", getPackageName());
            TextView textview_expectations = findViewById(textview_expectations_id);
            textview_expectations.setVisibility(View.VISIBLE);

            int expectation_id = getResources().getIdentifier("end_expectations" + i, "id", getPackageName());
            TextView player_expectations = findViewById(expectation_id);
            player_expectations.setText("" + gameRoom.getPlayers().get(i).getExpectations());
            player_expectations.setVisibility(View.VISIBLE);

            int textview_reality_id = getResources().getIdentifier("textview_reality" + i, "id", getPackageName());
            TextView textview_reality = findViewById(textview_reality_id);
            textview_reality.setVisibility(View.VISIBLE);

            int reality_id = getResources().getIdentifier("end_reality" + i, "id", getPackageName());
            TextView player_reality = findViewById(reality_id);
            player_reality.setText("" + gameRoom.getPlayers().get(i).getReality());
            player_reality.setVisibility(View.VISIBLE);
        }
    }

    public void resetData()//לעשות
    {
        AppUtilities.gameRoom.setPlayers(new ArrayList<>());
        AppUtilities.gameRoom.setRounds(0);
    }

    public void homePage(View view)//להוציא את השחקן
    {
        removePlayerFromPlayers();


        AppUtilities.gameRoom = null;
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("name", name);
        startActivity(intent);
    }

    public void removePlayerFromPlayers()
    {
        GameRoom gameRoom = AppUtilities.gameRoom;
        if(gameRoom.getPlayers().size() == 1)
        {
            database.deleteHum(gameRoom.getPlayerIndex(name));
            database.deleteStorage();
            database.deleteGameRoom();
        }
        else
        {
            gameRoom.getPlayers().remove(gameRoom.getPlayerIndex(name));
            database.updateField("players");
            database.deleteHum(gameRoom.getPlayerIndex(name));
        }
    }

    public void update(GameRoom g)
    {
        AppUtilities.gameRoom = g;
    }
}