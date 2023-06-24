package com.example.bzzing_last;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class End extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        //score();
    }

    public void score()
    {
        GameRoom gameRoom = AppUtilities.gameRoom;
        for (int i = 0; i < gameRoom.getPlayers().size(); i++) {
            int name_id = getResources().getIdentifier("end_name" + i, "id", null);
            TextView player_name = findViewById(name_id);
            player_name.setText(gameRoom.getPlayers().get(i).getName());

            int expectation_id = getResources().getIdentifier("end_expectations" + i, "id", null);
            TextView player_expectations = findViewById(expectation_id);
            player_expectations.setText(gameRoom.getPlayers().get(i).getExpectations());

            int reality_id = getResources().getIdentifier("end_reality" + i, "id", null);
            TextView player_reality = findViewById(reality_id);
            player_reality.setText(gameRoom.getPlayers().get(i).getReality());
        }
    }
}