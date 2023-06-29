package com.example.bzzing_last;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class End extends AppCompatActivity {

    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

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

            int expectation_id = getResources().getIdentifier("end_expectations" + i, "id", getPackageName());
            TextView player_expectations = findViewById(expectation_id);
            player_expectations.setText("" + gameRoom.getPlayers().get(i).getExpectations());
            player_expectations.setVisibility(View.VISIBLE);

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

        AppUtilities.gameRoom = null;
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("name", name);
        startActivity(intent);
    }
}